package com.example.creationopenapijava8demo;

import com.example.creationopenapijava8demo.util.FormatUtil;
import com.example.creationopenapijava8demo.util.SignatureUtil;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Objects;

@SpringBootTest
class OpenApiTests {

    private static final String BASE_URL = "https://api.creatlyai.cn";
    private static final String APP_KEY = "your AppKey";
    private static final String APP_SECRET = "your AppSecret";

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private OkHttpClient okHttpClient;

    @Test
    public void testRequest() {
        // 构造请求
        String body = this.readContent("classpath:post.ai-talk.videos.json");
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/openapi/ai-talk/videos")
                .post(requestBody)
                .build();

        // 计算鉴权头
        long timestamp = System.currentTimeMillis();
        String signature = SignatureUtil.sign(request, timestamp, APP_SECRET);
        String token = FormatUtil.format("XR1-HMAC-SHA256 Credential={}/{}/{},Signature={}", APP_KEY, timestamp, "creation", signature);

        // 加入鉴权头
        request = request.newBuilder().header("Authorization", token).build();

        // 发起请求
        String responseBody;
        try (Response response = okHttpClient.newCall(request).execute()) {
            responseBody = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.out.println(responseBody);
    }

    private String readContent(String location) {
        Resource resource = resourceLoader.getResource(location);
        try (InputStream is = resource.getInputStream()) {
            return StreamUtils.copyToString(is, Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
