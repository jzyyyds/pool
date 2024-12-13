package com.example.pool.spring.boot.start.config;

import com.example.pool.spring.boot.start.job.HeartJob;
import com.example.pool.spring.boot.start.runner.HeartRunner;
import com.example.pool.spring.boot.start.service.IHeartService;
import com.example.pool.spring.boot.start.service.impl.HeartServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeartConfig {

    @Bean(name = "heartService")
    public HeartServiceImpl getHeartServiceImpl(ApplicationContext applicationContext, RedissonClient redissonClient){
        return new HeartServiceImpl(applicationContext,redissonClient);
    }
    @Bean
    public HeartRunner getHeartRunner(IHeartService heartService){
        return new HeartRunner(heartService);
    }

    @Bean
    public HeartJob getHeartJob(IHeartService heartService) {
        return new HeartJob(heartService);
    }

}
