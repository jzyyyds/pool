package com.example.pool.spring.boot.start.runner;

import com.example.pool.spring.boot.start.service.IHeartService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

/**
 * 心跳启动和服务注册
 */
public class HeartRunner implements InitializingBean {

    private final IHeartService heartService;

    public HeartRunner(IHeartService heartService) {
        this.heartService = heartService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        heartService.registToRedis();
    }
}
