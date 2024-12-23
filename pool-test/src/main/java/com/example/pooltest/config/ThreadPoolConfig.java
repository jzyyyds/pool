package com.example.pooltest.config;

import lombok.extern.slf4j.Slf4j;
import org.example.build.DynamicPool;
import org.example.build.ThreadPoolBuilder;
import org.example.queue.ResizableCapacityLinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {
    @Bean("threadPoolExecutor01")
    @DynamicPool
    public ThreadPoolExecutor threadPoolExecutor01(ThreadPoolConfigProperties properties) {
        System.out.println("开始初始化");
        // 实例化策略
        RejectedExecutionHandler handler;
        switch (properties.getPolicy()){
            case "AbortPolicy":
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        // 创建线程池
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }

    @Bean("threadPoolExecutor02")
    @DynamicPool
    public ThreadPoolExecutor threadPoolExecutor02(ThreadPoolConfigProperties properties) {
        // 实例化策略
        RejectedExecutionHandler handler;
        switch (properties.getPolicy()){
            case "AbortPolicy":
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "DiscardPolicy":
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            default:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }

        // 创建线程池
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }


    @Bean("dynamicThreadPoolExecutor")
    @DynamicPool
    public ThreadPoolExecutor dynamicThreadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.builder().threadPoolId("jzy")
                .corePoolSize(25)
                .maxPoolNum(50)
                .awaitTerminationMillis(10L)
                .threadFactory("jzy")
                .allowCoreThreadTimeOut(false)
                .waitForTasksToCompleteOnShutdown(true)
                .keepAliveTime(10, TimeUnit.SECONDS)
                .workQueue(new ResizableCapacityLinkedBlockingQueue<>(200))
                .rejected(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory(Executors.defaultThreadFactory())
                .executeTimeOut(100000000)
                .enable(false)
                .build();
        return threadPoolExecutor;
    }
}
