package pers.project.api.security.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pers.project.api.client.InsightApiClient;
import pers.project.api.client.InsightApiRequest;
import pers.project.api.client.InsightApiResponse;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.ClientUserInfoDTO;
import pers.project.api.common.model.query.*;
import pers.project.api.common.model.vo.*;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.VerificationContextException;
import pers.project.api.security.feign.FacadeFeignClient;
import pers.project.api.security.mapper.SecurityMapper;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.dto.UserApiTestDTO;
import pers.project.api.security.model.dto.VerificationCodeCheckDTO;
import pers.project.api.security.model.dto.VerificationCodeSendingDTO;
import pers.project.api.security.model.po.UserAccountPO;
import pers.project.api.security.model.po.UserProfilePO;
import pers.project.api.security.model.vo.ApiCreatorVO;
import pers.project.api.security.model.vo.UserApiTestVO;
import pers.project.api.security.service.SecurityService;
import pers.project.api.security.verification.VerificationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.util.StringUtils.commaDelimitedListToSet;
import static pers.project.api.common.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.common.enumeration.ErrorEnum.VERIFICATION_CODE_ERROR;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.EMAIL;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.PHONE;

/**
 * Security 项目的 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private final VerificationContext verificationContext;

    private final FacadeFeignClient facadeFeignClient;

    private final SecurityMapper securityMapper;

    private final UserAccountMapper userAccountMapper;

    private final UserProfileMapper userProfileMapper;

    @Override
    public void loadDeferredCsrfToken(HttpServletRequest request) {
        Object attribute = request.getAttribute(DEFAULT_CSRF_PARAMETER_NAME);
        // instanceof 可以判空
        if (attribute instanceof CsrfToken csrfToken) {
            // 必须调用，否则 CsrfToken 因 Lambda 表达式延迟加载，不会生成 Cookie
            csrfToken.getToken();
            return;
        }
        throw new IllegalStateException("Should never get here");
    }

    @Override
    public void sendVerificationCode(VerificationCodeSendingDTO codeSendingDTO) {
        Supplier<BusinessException> supplierForIllegalState
                = () -> new BusinessException(SERVER_ERROR, "服务器错误，验证码发送失败");
        String identifier = determineVerificationIdentifier(supplierForIllegalState,
                codeSendingDTO.getPhoneNumber(), codeSendingDTO.getEmailAddress());
        VerificationStrategyEnum strategyEnum = VerificationStrategyEnum.valueOf(codeSendingDTO.getStrategy());
        try {
            switch (strategyEnum) {
                case PHONE -> verificationContext.sendCredential(identifier, PHONE);
                case EMAIL -> verificationContext.sendCredential(identifier, EMAIL);
            }
        } catch (VerificationContextException e) {
            warnVerificationContextException(identifier, strategyEnum, e);
            throw supplierForIllegalState.get();
        }
    }

    @Override
    public boolean checkLoginVerificationCode(String loginIdentifier, String verificationCode, String strategy) {
        VerificationStrategyEnum strategyEnum
                = EnumUtils.getEnum(VerificationStrategyEnum.class, strategy);
        Assert.state(strategy != null, "The strategyEnum must be found");
        boolean isValid;
        try {
            isValid = isValidVerificationCode(loginIdentifier, verificationCode, strategyEnum);
        } catch (VerificationContextException e) {
            warnVerificationContextException(loginIdentifier, strategyEnum, e);
            throw new IllegalStateException(e);
        }
        return isValid;
    }

    @Override
    public VerificationStrategyEnum checkVerificationCode(VerificationCodeCheckDTO codeCheckDTO,
                                                          Supplier<BusinessException> supplierForInvalidCode) {
        Supplier<BusinessException> supplierForIllegalState
                = () -> new BusinessException(SERVER_ERROR, "服务器错误，验证失败");
        if (supplierForInvalidCode == null) {
            supplierForInvalidCode = () -> new BusinessException(VERIFICATION_CODE_ERROR, "验证码错误");
        }
        String identifier = determineVerificationIdentifier(supplierForIllegalState,
                codeCheckDTO.getPhoneNumber(), codeCheckDTO.getEmailAddress());
        VerificationStrategyEnum strategyEnum = VerificationStrategyEnum.valueOf(codeCheckDTO.getStrategy());
        boolean isValid;
        try {
            isValid = isValidVerificationCode(identifier, codeCheckDTO.getVerificationCode(), strategyEnum);
        } catch (VerificationContextException e) {
            warnVerificationContextException(identifier, strategyEnum, e);
            throw supplierForIllegalState.get();
        }
        if (!isValid) {
            throw supplierForInvalidCode.get();
        }
        return strategyEnum;
    }

    @Override
    public UserApiDigestPageVO getUserApiDigestPageVO(UserApiDigestPageQuery pageQuery) {
        Result<UserApiDigestPageVO> pageResult
                = facadeFeignClient.getUserApiDigestPageResult(pageQuery);
        if (ResultUtils.isFailure(pageResult)) {
            throw new BusinessException(SERVER_ERROR, "Feign 调用失败");
        }
        return pageResult.getData();
    }

    @Override
    public UserApiFormatAndQuantityUsageVO getUserApiFormatAndQuantityUsageVO(@Valid UserApiFormatAndQuantityUsageQuery query) {
        Result<UserApiFormatAndQuantityUsageVO> formatAndQuantityUsageResult
                = facadeFeignClient.getUserApiFormatAndQuantityUsageResult(query);
        if (ResultUtils.isFailure(formatAndQuantityUsageResult)) {
            throw new BusinessException(SERVER_ERROR, "Feign 调用失败");
        }
        return formatAndQuantityUsageResult.getData();
    }

    @Override
    public UserAdminPageVO getUserAdminPageVO(UserAdminPageQuery pageQuery) {
        Long total = securityMapper.countUserAdminVOs(pageQuery);
        if (total == 0L) {
            return new UserAdminPageVO();
        }
        List<UserAdminVO> userAdminVOList = securityMapper.listUserAdminVOs(pageQuery);
        UserAdminPageVO userAdminPageVO = new UserAdminPageVO();
        userAdminPageVO.setTotal(total);
        userAdminPageVO.setUserAdminVOList(userAdminVOList);
        return userAdminPageVO;
    }

    @Override
    public ApiAdminPageVO getApiAdminPageVO(ApiAdminPageQuery pageQuery) {
        Result<ApiAdminPageVO> userAdminPageResult
                = facadeFeignClient.getApiAdminPageResult(pageQuery);
        if (ResultUtils.isFailure(userAdminPageResult)) {
            throw new BusinessException(SERVER_ERROR, "Feign 调用失败");
        }
        return userAdminPageResult.getData();
    }

    @Override
    public ApiCreatorVO getApiCreatorVO(String accountId) {
        LambdaQueryWrapper<UserAccountPO> accountQueryWrapper = new LambdaQueryWrapper<>();
        accountQueryWrapper.select(UserAccountPO::getUsername, UserAccountPO::getEmailAddress,
                UserAccountPO::getAccountStatus, UserAccountPO::getAuthority, UserAccountPO::getUpdateTime);
        accountQueryWrapper.eq(UserAccountPO::getId, accountId);
        UserAccountPO userAccountPO = userAccountMapper.selectOne(accountQueryWrapper);
        LambdaQueryWrapper<UserProfilePO> profileQueryWrapper = new LambdaQueryWrapper<>();
        profileQueryWrapper.select(UserProfilePO::getAvatar, UserProfilePO::getNickname,
                UserProfilePO::getWebsite, UserProfilePO::getGithub, UserProfilePO::getGitee,
                UserProfilePO::getBiography, UserProfilePO::getIpLocation, UserProfilePO::getLastLoginTime);
        profileQueryWrapper.eq(UserProfilePO::getAccountId, accountId);
        UserProfilePO userProfilePO = userProfileMapper.selectOne(profileQueryWrapper);
        ApiCreatorVO apiCreatorVO = new ApiCreatorVO();
        BeanUtils.copyProperties(userAccountPO, apiCreatorVO);
        BeanUtils.copyProperties(userProfilePO, apiCreatorVO);
        apiCreatorVO.setAuthoritySet(commaDelimitedListToSet(userAccountPO.getAuthority()));
        return apiCreatorVO;
    }

    @Override
    public UserApiTestVO getUserApiTestVO(UserApiTestDTO userApiTestDTO) {
        // 根据 accountId 查询 secretKey
        LambdaQueryWrapper<UserAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserAccountPO::getSecretKey);
        queryWrapper.eq(UserAccountPO::getId, userApiTestDTO.getAccountId());
        UserAccountPO userAccountPO = userAccountMapper.selectOne(queryWrapper);
        // 创建请求对象和客户端
        InsightApiRequest insightApiRequest = InsightApiRequest.newBuilder()
                .method(userApiTestDTO.getMethod())
                .url(userApiTestDTO.getUrl())
                .pathVariable(convertJsonStringToStringMap(userApiTestDTO.getPathVariable()))
                .requestParam(convertJsonStringToStringMap(userApiTestDTO.getRequestParam()))
                .requestHeader(convertJsonStringToStringMap(userApiTestDTO.getRequestHeader()))
                .requestBody(userApiTestDTO.getRequestBody())
                .build();
        InsightApiClient insightApiClient = InsightApiClient.newBuilder()
                .secretId(userApiTestDTO.getSecretId())
                .secretKey(userAccountPO.getSecretKey())
                .build();
        // 发送请求并转换响应
        UserApiTestVO userApiTestVO = new UserApiTestVO();
        try {
            InsightApiResponse<String> insightApiResponse = insightApiClient.send(insightApiRequest, String.class);
            userApiTestVO.setStatusCode(insightApiResponse.statusCode());
            JSONObject jsonObject = new JSONObject();
            // HttpHeaders 的 map 方法返回的是不可变视图
            insightApiResponse.responseHeader().map().forEach((headerName, headerValue) ->
                    jsonObject.put(headerName, headerValue.get(0)));
            userApiTestVO.setResponseHeader(jsonObject.toString());
            userApiTestVO.setResponseBody(insightApiResponse.body());
        } catch (Exception e) {
            throw new BusinessException(SERVER_ERROR, "测试调用失败");
        }
        return userApiTestVO;
    }

    @Override
    public ClientUserInfoDTO getClientUserInfoDTO(ClientUserInfoQuery userInfoQuery) {
        LambdaQueryWrapper<UserAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserAccountPO::getId, UserAccountPO::getSecretKey);
        queryWrapper.eq(UserAccountPO::getSecretId, userInfoQuery.getSecretId());
        UserAccountPO userAccountPO = userAccountMapper.selectOne(queryWrapper);
        ClientUserInfoDTO clientUserInfoDTO = new ClientUserInfoDTO();
        clientUserInfoDTO.setAccountId(userAccountPO.getId());
        clientUserInfoDTO.setSecretKey(userAccountPO.getSecretKey());
        return clientUserInfoDTO;
    }

    /**
     * 将 JSON 字符串转换为字符串 Map
     *
     * @param jsonString JSON 字符串
     * @return 字符串 Map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<String, String> convertJsonStringToStringMap(String jsonString) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        if (jsonObject == null) {
            return null;
        }
        jsonObject.entrySet().forEach(entry -> entry.setValue(entry.getValue().toString()));
        return (Map) jsonObject;
    }

    /**
     * 确定验证标识符（手机号或邮箱地址）。
     *
     * @param supplierForIllegalState 当验证标识符无效时，使用此供应程序生成异常。
     * @param identifiers             验证标识符的可变参数列表。
     * @return 如果验证标识符有效，则返回验证标识符；否则抛出异常。
     * @throws E 如果验证标识符无效，则抛出 {@code supplierForIllegalState} 生成的异常。
     */
    private <E extends RuntimeException> String determineVerificationIdentifier(Supplier<E> supplierForIllegalState,
                                                                                String... identifiers) {

        List<String> nonNullIdentifiers = new ArrayList<>(2);
        for (String identifier : identifiers) {
            if (identifier != null) {
                nonNullIdentifiers.add(identifier);
            }
        }
        if (nonNullIdentifiers.size() != 1) {
            log.warn("There is not only one identifier that is not null");
            throw supplierForIllegalState.get();
        }
        return nonNullIdentifiers.get(0);
    }

    /**
     * 验证验证码是否有效。
     *
     * @param identifier       验证标识符，可以是电话号码或电子邮件地址。
     * @param verificationCode 验证码。
     * @param strategyEnum     发送验证码的策略。
     * @return 如果验证码是有效的，则返回 {@code true}；否则返回 {@code false}。
     * @throws VerificationContextException 当验证过程出现错误时，会抛出此异常。
     */
    private boolean isValidVerificationCode(String identifier,
                                            String verificationCode,
                                            VerificationStrategyEnum strategyEnum)
            throws VerificationContextException {
        boolean isValid;
        isValid = switch (strategyEnum) {
            case PHONE -> verificationContext.verifyCredential
                    (identifier, verificationCode, PHONE);
            case EMAIL -> verificationContext.verifyCredential
                    (identifier, verificationCode, EMAIL);
        };
        return isValid;
    }

    /**
     * 记录验证上下文异常的日志。
     *
     * @param identifier   验证标识符，可以是电话号码或电子邮件地址。
     * @param strategyEnum 发送验证码的策略。
     * @param e            验证上下文异常。
     */
    private void warnVerificationContextException(String identifier,
                                                  VerificationStrategyEnum strategyEnum,
                                                  Exception e) {
        if (log.isWarnEnabled()) {
            String argument = strategyEnum.equals(PHONE) ?
                    "phone number" : "email address";
            log.warn("""
                    A verification context failure occurred, %s: %s
                    """.formatted(argument, identifier), e);
        }
    }

}
