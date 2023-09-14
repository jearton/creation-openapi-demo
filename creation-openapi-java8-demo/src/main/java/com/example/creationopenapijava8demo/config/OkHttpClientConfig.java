package com.example.creationopenapijava8demo.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 建立连接超时：默认10s
                .connectTimeout(Duration.ofSeconds(5))
                // 等待服务器响应超时：默认10s
                .readTimeout(Duration.ofSeconds(5))
                // 请求服务器超时：默认10s
                .writeTimeout(Duration.ofSeconds(5))
                // 总体调用耗时（包含DNS解析+请求+响应）：默认不超时
                .callTimeout(Duration.ofSeconds(15))
                .build();
    }
}
