package pers.project.api.common.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * 签名工具类
 *
 * @author Luo Fei
 * @version 2023/3/16
 */
public abstract class SignUtils {

    public static String sign(String body, String secretKey) {
        return DigestUtils.sha256Hex(body + "." + secretKey);
    }

}
