package com.example.pool.spring.boot.start.service.impl;

import com.example.pool.spring.boot.start.service.IHeartService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartServiceImpl implements IHeartService {

    private final RedissonClient redissonClient;

    private final String applicationName;


    private static final String redis_key = "dynamic_heart:";

    public HeartServiceImpl(ApplicationContext applicationContext,RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
    }
    @Override
    public void registToRedis() {
        //注册到redis中去
        String key = redis_key + applicationName;
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(applicationName);
        bucket.expire(30,TimeUnit.SECONDS);
        log.info("applicationName"+applicationName+"应用程序注册成功!");
    }

    @Override
    @PreDestroy
    public void unregistToRedis() {
        //从redis中移除掉
        String key = redis_key + applicationName;
        redissonClient.getBucket(key).delete();
        log.info("applicationName"+applicationName+"应用程序移除成功!");
    }
}
