package pers.project.api.gateway.util;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 网关 HTTP 工具类
 *
 * @author Luo Fei
 * @date 2023/07/16
 */
public abstract class GatewayHttpUtils {

    public static String getCachedRequestBody(ServerWebExchange exchange) {
        Object attribute = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
        if (!(attribute instanceof DataBuffer dataBuffer)) {
            return null;
        }
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        if (contentType != null && contentType.includes(MediaType.MULTIPART_FORM_DATA)) {
            return null;
        }
        return readDataBufferToString(dataBuffer);
    }

    public static String readDataBufferToString(DataBuffer dataBuffer) {
        // 安全起见，使用堆内存而不是直接内存
        ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
        dataBuffer.toByteBuffer(byteBuffer);
        // 使用 asReadOnlyBuffer()，否则可能丢失数据
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        return charBuffer.toString();
    }

}
