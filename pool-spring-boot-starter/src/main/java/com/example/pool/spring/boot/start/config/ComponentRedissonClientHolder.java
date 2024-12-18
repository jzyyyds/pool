package com.example.pool.spring.boot.start.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组件的redissionClient单例模式
 */
@Slf4j
public class ComponentRedissonClientHolder {
    private final Logger logger = LoggerFactory.getLogger(ComponentRedissonClientHolder.class);
    private static volatile RedissonClient redissonClient;

    // 私有构造函数，禁止外部实例化
    private ComponentRedissonClientHolder() {}

    public static RedissonClient getInstance(DynamicThreadPoolAutoProperties properties) {
        if (redissonClient == null) {
            synchronized (ComponentRedissonClientHolder.class) {
                if (redissonClient == null) {
                    Config config = new Config();
                    config.setCodec(JsonJacksonCodec.INSTANCE);
                    config.useSingleServer()
                            .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                            .setPassword(properties.getPassword())
                            .setConnectionPoolSize(properties.getPoolSize())
                            .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                            .setIdleConnectionTimeout(properties.getIdleTimeout())
                            .setConnectTimeout(properties.getConnectTimeout())
                            .setRetryAttempts(properties.getRetryAttempts())
                            .setRetryInterval(properties.getRetryInterval())
                            .setPingConnectionInterval(properties.getPingInterval())
                            .setKeepAlive(properties.isKeepAlive());

                    redissonClient = Redisson.create(config);
                }
            }
        }
        log.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
        return redissonClient;
    }
}
