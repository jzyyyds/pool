package com.example.pool.spring.boot.start.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "dynamic-thread-pool.alarm", ignoreUnknownFields = true)
public class DynamicThreadPoolNotifyAutoProperties {
    private Boolean enable;
    private List<String> usePlatform = new ArrayList<>();
    private Email email;
    private Webhook webhook;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Email {
        private String from;
        private String to;
        private String password;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Webhook {
        private String dingding;
        private String feishu;
    }
}
