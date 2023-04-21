package pers.project.api.security.verification;

import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pers.project.api.security.execption.VerificationContextException;

import static pers.project.api.security.enumeration.VerificationStrategyEnum.PHONE;
import static pers.project.api.security.properties.VerificationContextProperties.PhoneProperties;

/**
 * 手机验证策略
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Component
public class PhoneVerificationStrategy extends AbstractVerificationStrategy {

    private final PhoneProperties properties;

    private final SmsClient smsClient;

    public PhoneVerificationStrategy(RedisTemplate<String, Object> redisTemplate,
                                     PhoneProperties properties,
                                     SmsClient smsClient) {
        super(redisTemplate);
        this.properties = properties;
        this.smsClient = smsClient;
    }

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @see <a href="https://cloud.tencent.com/document/product/382/43194">
     * Tencent Cloud Java SDK</a>
     */
    @Override
    public void sendVerificationCode(String phoneNumber) throws VerificationContextException {
        // 构造短信发送请求
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setSmsSdkAppId(properties.getSmsSdkAppId());
        smsRequest.setSignName(properties.getSignName());
        smsRequest.setTemplateId(properties.getTemplateId());
        String verificationCode = generateVerificationCode();
        int validityPeriod = properties.getValidityPeriod();
        String[] templateParamSet = {
                verificationCode,
                String.valueOf(validityPeriod)
        };
        smsRequest.setTemplateParamSet(templateParamSet);
        String[] phoneNumberSet = {phoneNumber};
        smsRequest.setPhoneNumberSet(phoneNumberSet);
        // 保存短信验证码到 Redis 并发送给用户
        try {
            saveVerificationCodeInRedis
                    (PHONE.keyPrefix(), phoneNumber, verificationCode, validityPeriod);
            smsClient.SendSms(smsRequest);
        } catch (Exception e) {
            throw new VerificationContextException(e);
        }
    }

    @Override
    public boolean checkVerificationCode(String phoneNumber, String userVerificationCode)
            throws VerificationContextException {
        try {
            return checkRedisVerificationCode(PHONE.keyPrefix(), phoneNumber, userVerificationCode);
        } catch (Exception e) {
            throw new VerificationContextException(e);
        }
    }

}
