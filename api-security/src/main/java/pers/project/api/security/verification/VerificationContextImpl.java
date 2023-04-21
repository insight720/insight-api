package pers.project.api.security.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.VerificationContextException;

import java.util.Map;

/**
 * 验证上下文实现
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Component
@RequiredArgsConstructor
public class VerificationContextImpl implements VerificationContext {

    private final Map<String, VerificationStrategy> beanNameStrategyMap;

    @Override
    public void sendCredential(String contextInfo, VerificationStrategyEnum strategyEnum)
            throws VerificationContextException {
        String beanName = strategyEnum.beanName();
        VerificationStrategy strategy = beanNameStrategyMap.get(beanName);
        strategy.sendVerificationCode(contextInfo);
    }

    @Override
    public boolean verifyCredential(String contextInfo, String credential,
                                    VerificationStrategyEnum strategyEnum)
            throws VerificationContextException {
        String beanName = strategyEnum.beanName();
        VerificationStrategy strategy = beanNameStrategyMap.get(beanName);
        return strategy.checkVerificationCode(contextInfo, credential);
    }

}
