package pers.project.api.security.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.enumeration.UploadFileEnum;
import pers.project.api.security.execption.UploadContextException;

import java.util.Map;

/**
 * 上传上下文实现
 *
 * @author Luo Fei
 * @date 2023/04/14
 */
@Component
@RequiredArgsConstructor
public class UploadContextImpl implements UploadContext {

    /**
     * 上传策略实现类 Bean 名称和上传策略实现类 Bean 之间的映射
     */
    private final Map<String, UploadStrategy> beanNameUploadStrategyMap;

    @Override
    public String upload(String userProfileId, MultipartFile file, UploadFileEnum uploadFileEnum) throws UploadContextException {
        String strategyBeanName = uploadFileEnum.uploadStrategyEnum().beanName();
        UploadStrategy uploadStrategy = beanNameUploadStrategyMap.get(strategyBeanName);
        return uploadStrategy.uploadByUserProfileId(userProfileId, file, uploadFileEnum.directoryUri());
    }

    @Override
    public void delete(String fileUrl, UploadFileEnum uploadFileEnum) throws UploadContextException {
        String strategyBeanName = uploadFileEnum.uploadStrategyEnum().beanName();
        UploadStrategy uploadStrategy = beanNameUploadStrategyMap.get(strategyBeanName);
        uploadStrategy.deleteByUrl(fileUrl);
    }

}
