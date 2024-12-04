package com.example.pool.spring.boot.start.config;

import com.example.pool.spring.boot.start.alarm.AlarmStrategy;
import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.domain.enums.RegistryEnumVO;
import com.example.pool.spring.boot.start.job.ThreadPoolDataReportJob;
import com.example.pool.spring.boot.start.listener.ThreadPoolConfigAdjustListener;
import com.example.pool.spring.boot.start.manager.GlobalThreadPoolManage;
import com.example.pool.spring.boot.start.registry.IRegistry;
import com.example.pool.spring.boot.start.registry.redis.RedisRegistry;
import com.example.pool.spring.boot.start.service.IDynamicThreadPoolService;
import com.example.pool.spring.boot.start.service.impl.DynamicThreadPoolService;
import com.example.pool.spring.boot.start.support.DynamicThreadPoolPostProcessor;
import io.micrometer.core.instrument.util.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class PoolConfig {
    private final Logger logger = LoggerFactory.getLogger(PoolConfig.class);
    private String applicationName;

    @Bean
    //@ConditionalOnBean(name = "dynamicThreadRedissonClient")
    public IRegistry redisRegistry(RedissonClient dynamicThreadRedissonClient) {
        return new RedisRegistry(dynamicThreadRedissonClient);
    }

    @Bean("dynamicThreadPollService")
    public DynamicThreadPoolService dynamicThreadPollService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient,IRegistry registry){
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }
//        //此时如果线程池的参数被修改了，此时需要从redis中进行读取参数
//        Set<String> threadPools = threadPoolExecutorMap.keySet();
//        for (String threadPool : threadPools) {
//            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPool).get();
//            if(threadPoolConfigEntity==null){
//                continue;
//            }
//            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPool);
//            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
//            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
//        }
//        ConcurrentHashMap<String,ThreadPoolExecutor> map = new ConcurrentHashMap<>(threadPoolExecutorMap);
        return new DynamicThreadPoolService(applicationName,registry);
    }


    @Bean("dynamicThreadRedissonClient")
    //@ConditionalOnProperty(prefix = "dynamic.thread.pool.config",name = "used",value = "redis")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
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
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }



    @Bean
    //@ConditionalOnBean(name = "dynamicThreadRedissonClient")
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, registry);
    }

    @Bean(name = "threadPoolListener")
    //@ConditionalOnBean(name = "dynamicThreadRedissonClient")
    public ThreadPoolConfigAdjustListener threadPoolListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolConfigAdjustListener(dynamicThreadPoolService, registry);
    }

    /**
     * 方便测试，真正的实现是不需要的
     * @param redissonClient
     * @param threadPoolConfigAdjustListener
     * @return
     */
    @Bean(name = "dynamicThreadPoolRedisTopic")
    public RTopic threadPoolConfigAdjustListener(RedissonClient redissonClient, ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener) {
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigAdjustListener);
        return topic;
    }


    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContext context,RedissonClient redissonClient){
        return new DynamicThreadPoolPostProcessor(context,redissonClient);
    }

    @Bean
    public GlobalThreadPoolManage globalThreadPoolManage(IRegistry registry){
        return new GlobalThreadPoolManage();
    }


}
