package com.example.pool.spring.boot.start.config.service.config;

import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


public class PoolConfig {
    private final Logger logger = LoggerFactory.getLogger(PoolConfig.class);
    private String applicationName;


    @Bean("dynamicThreadPollService")
    public String dynamicThreadPollService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap){
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }
        for (Map.Entry<String, ThreadPoolExecutor> stringThreadPoolExecutorEntry : threadPoolExecutorMap.entrySet()) {
            String key = stringThreadPoolExecutorEntry.getKey();
            ThreadPoolExecutor value = stringThreadPoolExecutorEntry.getValue();
            System.out.println(value.getCorePoolSize());
        }
        return new String("");
    }

}
