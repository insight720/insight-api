package pers.project.api.facade.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.facade.mapper.ApiInfoMapper;
import pers.project.api.facade.mapper.UserApiInfoMapper;

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

/*    @GetMapping("/top/api/invoke")
    public BaseResponse<List<ApiInfoVO>> listTopInvokeApiInfo() {
        List<UserApiInfo> userApiInfoList = userApiInfoMapper.listTopInvokeApiInfo(3);
        Map<Long, List<UserApiInfo>> apiInfoIdObjMap = userApiInfoList.stream()
                .collect(Collectors.groupingBy(UserApiInfo::getApiInfoId));
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", apiInfoIdObjMap.keySet());
        List<ApiInfo> list = apiInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException(ErrorCodeEnum.SYSTEM_ERROR);
        }
        List<ApiInfoVO> apiInfoVOList = list.stream().map(apiInfo -> {
            int totalNum = apiInfoIdObjMap.get(apiInfo.getId()).get(0).getTotalNum();
            ApiInfoVO apiInfoVO = new ApiInfoVO();
            BeanUtils.copyProperties(apiInfo, apiInfoVO);
            apiInfoVO.setTotalNum(totalNum);
            return apiInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(apiInfoVOList);
    }*/

}
