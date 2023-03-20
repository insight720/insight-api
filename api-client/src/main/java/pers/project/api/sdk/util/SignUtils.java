package pers.project.api.sdk.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * 签名工具类
 *
 * @author Luo Fei
 * @date 2023/03/16
 */
public abstract class SignUtils {

    public static String sign(String body, String secretKey) {
        return DigestUtils.sha256Hex(body + "." + secretKey);
    }

}
