package com.example.pool.spring.boot.start.config;

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
import com.example.pool.spring.boot.start.utils.UniQueIdUtils;
import io.micrometer.core.instrument.util.StringUtils;
import org.example.config.ApplicationContextHolder;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class PoolConfig {
    private final Logger logger = LoggerFactory.getLogger(PoolConfig.class);
    //private String applicationName;

    @Bean
    public UniQueIdUtils getUniqueIdUtils(ApplicationContext applicationContext){
        return new UniQueIdUtils(applicationContext);
    }

    @Bean
    public IRegistry redisRegistry(DynamicThreadPoolAutoProperties dynamicThreadPoolAutoProperties) {
        RedissonClient dynamicThreadRedissonClient = ComponentRedissonClientHolder.getInstance(dynamicThreadPoolAutoProperties);
        return new RedisRegistry(dynamicThreadRedissonClient);
    }



    @Bean("dynamicThreadPollService")
    public DynamicThreadPoolService dynamicThreadPollService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap,IRegistry registry){
        String applicationName = UniQueIdUtils.getId();
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }
        return new DynamicThreadPoolService(applicationName,registry);
    }


//    @Bean(name = "dynamicThreadRedissonClient")
//    //@ConditionalOnProperty(prefix = "dynamic.thread.pool.config.enabled",havingValue = "true")
//    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
//        Config config = new Config();
//        config.setCodec(JsonJacksonCodec.INSTANCE);
//
//        config.useSingleServer()
//                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
//                .setPassword(properties.getPassword())
//                .setConnectionPoolSize(properties.getPoolSize())
//                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
//                .setIdleConnectionTimeout(properties.getIdleTimeout())
//                .setConnectTimeout(properties.getConnectTimeout())
//                .setRetryAttempts(properties.getRetryAttempts())
//                .setRetryInterval(properties.getRetryInterval())
//                .setPingConnectionInterval(properties.getPingInterval())
//                .setKeepAlive(properties.isKeepAlive())
//        ;
//
//        RedissonClient redissonClient = Redisson.create(config);
//
//        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
//
//        return redissonClient;
//    }



    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, registry);
    }

    @Bean(name = "threadPoolListener")
    public ThreadPoolConfigAdjustListener threadPoolListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolConfigAdjustListener(dynamicThreadPoolService, registry);
    }

    /**
     * @param
     * @param threadPoolConfigAdjustListener
     * @return
     */
    @Bean(name = "dynamicThreadPoolRedisTopic")
    public RTopic threadPoolConfigAdjustListener(DynamicThreadPoolAutoProperties dynamicThreadPoolAutoProperties, ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener) {
        RedissonClient redissonClient = ComponentRedissonClientHolder.getInstance(dynamicThreadPoolAutoProperties);
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + UniQueIdUtils.getId());
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigAdjustListener);
        return topic;
    }


    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContext context,DynamicThreadPoolAutoProperties dynamicThreadPoolAutoProperties){
        RedissonClient redissonClient = ComponentRedissonClientHolder.getInstance(dynamicThreadPoolAutoProperties);
        return new DynamicThreadPoolPostProcessor(context,redissonClient);
    }

    @Bean
    public GlobalThreadPoolManage globalThreadPoolManage(IRegistry registry){
        return new GlobalThreadPoolManage();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

}
