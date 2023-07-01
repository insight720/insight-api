package pers.project.api.security.verification;

import com.alibaba.fastjson2.JSONObject;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.Template;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pers.project.api.security.execption.VerificationContextException;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static pers.project.api.security.config.property.VerificationContextProperties.EmailProperties;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.EMAIL;

/**
 * 邮件验证策略
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Component
public class EmailVerificationStrategy extends AbstractVerificationStrategy {

    private final SesClient sesClient;

    private final EmailProperties properties;

    protected EmailVerificationStrategy(RedisTemplate<String, Object> redisTemplate,
                                        SesClient sesClient,
                                        EmailProperties properties) {
        super(redisTemplate);
        this.sesClient = sesClient;
        this.properties = properties;
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 用户邮箱
     * @throws VerificationContextException 如果发送出现异常
     * @see <a href="https://cloud.tencent.com/document/api/1288/51034">
     * 发送邮件</a>
     */
    @Override
    public void sendVerificationCode(String email) throws VerificationContextException {
        // 构造邮件模板
        Template template = new Template();
        template.setTemplateID(properties.getTemplateId());
        String verificationCode = generateVerificationCode();
        int validityPeriod = properties.getValidityPeriod();
        String[] variableNames = properties.getVariableNames();
        JSONObject jsonObject = new JSONObject(variableNames.length);
        jsonObject.put(variableNames[INTEGER_ZERO], verificationCode);
        jsonObject.put(variableNames[INTEGER_ONE], validityPeriod);
        template.setTemplateData(jsonObject.toString());
        // 构造邮件发送请求
        SendEmailRequest emailRequest = new SendEmailRequest();
        emailRequest.setFromEmailAddress(properties.getFrom());
        emailRequest.setDestination(new String[]{email});
        emailRequest.setSubject(properties.getSubject());
        emailRequest.setTemplate(template);
        // 保存邮件验证码到 Redis 并发送给用户
        try {
            saveVerificationCodeInRedis
                    (EMAIL.keyPrefix(), email, verificationCode, validityPeriod);
            sesClient.SendEmail(emailRequest);
        } catch (Exception e) {
            throw new VerificationContextException(e);
        }
    }

    @Override
    public boolean checkVerificationCode(String email, String userVerificationCode)
            throws VerificationContextException {
        try {
            return checkRedisVerificationCode(EMAIL.keyPrefix(), email, userVerificationCode);
        } catch (Exception e) {
            throw new VerificationContextException(e);
        }
    }

}
