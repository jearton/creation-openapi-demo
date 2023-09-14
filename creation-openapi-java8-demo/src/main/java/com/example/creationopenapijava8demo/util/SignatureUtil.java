package com.example.creationopenapijava8demo.util;

import com.google.common.base.Joiner;
import lombok.experimental.UtilityClass;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

@UtilityClass
public class SignatureUtil {

    /**
     * 计算签名
     *
     * @param request   OKHTTP的请求对象
     * @param timestamp 加签时间
     * @param appSecret 密钥
     * @return 签名
     */
    public String sign(Request request, long timestamp, String appSecret) {
        HttpUrl httpUrl = request.url();
        String algorithmVersion = "XR1-HMAC-SHA256";
        String appName = "creation";
        String httpMethod = StringUtils.upperCase(request.method());
        String path = httpUrl.encodedPath();

        // 对用户数据进行加签验证
        StringBuilder signatureBody = new StringBuilder();
        signatureBody.append(algorithmVersion).append("\n")
                .append(timestamp).append("\n")
                .append(appName).append("\n")
                .append(httpMethod).append("\n")
                .append(path).append("\n");

        // 查询参数
        Set<String> parameterNames = httpUrl.queryParameterNames();
        if (!CollectionUtils.isEmpty(parameterNames)) {
            LinkedHashMap<String, String> sortedParams = new LinkedHashMap<>();
            parameterNames.forEach(name -> {

            });
            parameterNames.stream().sorted().forEach(name -> {
                String value = String.join(",", httpUrl.queryParameterValues(name));
                sortedParams.put(name, value);
            });
            signatureBody.append(Joiner.on("&").withKeyValueSeparator("=").join(sortedParams)).append("\n");
        }

        // 请求体（只对json格式做加签处理）
        boolean isJsonRequestBody = Optional.ofNullable(request.body())
                .map(RequestBody::contentType)
                .map(contentType -> contentType.type() + "/" + contentType.subtype())
                .filter("application/json"::equalsIgnoreCase)
                .isPresent();
        if (isJsonRequestBody) {
            String requestBody;
            try (Buffer buffer = new Buffer()) {
                request.body().writeTo(buffer);
                requestBody = buffer.readUtf8();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            signatureBody.append(DigestUtils.sha256Hex(requestBody)).append("\n");
        }

        // 计算签名
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, appSecret.getBytes(StandardCharsets.UTF_8));
        return StringUtils.lowerCase(hmacUtils.hmacHex(signatureBody.toString()));
    }
}
