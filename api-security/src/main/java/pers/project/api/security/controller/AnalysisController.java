package pers.project.api.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.entity.ApiInfo;
import pers.project.api.security.annotation.AuthCheck;
import pers.project.api.security.common.BaseResponse;
import pers.project.api.security.common.ErrorCode;
import pers.project.api.security.common.ResultUtils;
import pers.project.api.security.exception.BusinessException;
import pers.project.api.security.mapper.ApiInfoMapper;
import pers.project.api.security.mapper.UserApiInfoMapper;
import pers.project.api.security.model.entity.UserApiInfo;
import pers.project.api.security.model.vo.ApiInfoVO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserApiInfoMapper userApiInfoMapper;

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @GetMapping("/top/api/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<ApiInfoVO>> listTopInvokeApiInfo() {
        List<UserApiInfo> userApiInfoList = userApiInfoMapper.listTopInvokeApiInfo(3);
        Map<Long, List<UserApiInfo>> apiInfoIdObjMap = userApiInfoList.stream()
                .collect(Collectors.groupingBy(UserApiInfo::getApiInfoId));
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", apiInfoIdObjMap.keySet());
        List<ApiInfo> list = apiInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<ApiInfoVO> apiInfoVOList = list.stream().map(apiInfo -> {
            int totalNum = apiInfoIdObjMap.get(apiInfo.getId()).get(0).getTotalNum();
            ApiInfoVO apiInfoVO = new ApiInfoVO();
            BeanUtils.copyProperties(apiInfo, apiInfoVO);
            apiInfoVO.setTotalNum(totalNum);
            return apiInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(apiInfoVOList);
    }

}
