package com.example.pool.spring.boot.start.support;

import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.domain.enums.RegistryEnumVO;
import com.example.pool.spring.boot.start.manager.GlobalThreadPoolManage;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.build.DynamicPool;
import org.example.executor.DynamicThreadPoolExecutor;
import org.example.queue.ResizableCapacityLinkedBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池初始化后的注册
 */
@AllArgsConstructor
@Slf4j
public class DynamicThreadPoolPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;
    private final RedissonClient redissonClient;
    private static String applicationName;

    /**
     * bean初始化完成之后会调用的方法，是一个扩展点
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ThreadPoolExecutor || bean instanceof DynamicThreadPoolExecutor) {
            //原生的线程池和框架内置的线程池都需要进行管理
            //判断是否有注解标注
            DynamicPool dynamicPool;
            try {
                dynamicPool = applicationContext.findAnnotationOnBean(beanName, DynamicPool.class);
                if (Objects.isNull(dynamicPool)) {
                    //不包含直接返回
                    return bean;
                }
            } catch (Exception ex) {
                log.error("Failed to create dynamic thread pool in annotation mode.", ex);
                return bean;
            }
            //此时走到这里的话，就说明此时都存在，此时需要进行注册到redis中，但是在注册之前，如果redis已经有这个数据的话，
            //此时要已redis的数据为准，所以要先去查询redis中的数据
            fillPoolAndRegist(bean,beanName);
        }
        return bean;
    }

    private void fillPoolAndRegist(Object bean, String beanName) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            log.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }
        //此时如果是框架的线程池的话，根据id来进行区分
        if (bean instanceof DynamicThreadPoolExecutor) {
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) bean;
            String threadPoolId = dynamicThreadPoolExecutor.getThreadPoolId();
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolId).get();
            if (Objects.nonNull(threadPoolConfigEntity)) {
                dynamicThreadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
                dynamicThreadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
                if (dynamicThreadPoolExecutor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                    //此时判断是否是可以更改的队列
                    ResizableCapacityLinkedBlockingQueue<Runnable> queue = (ResizableCapacityLinkedBlockingQueue)dynamicThreadPoolExecutor.getQueue();
                    queue.setCapacity(threadPoolConfigEntity.getQueueSize());
                }
                buildThreadEntityAndRegist(dynamicThreadPoolExecutor);
            }else {
                //第一次注册，直接注册到redis中即可
                buildThreadEntityAndRegist(dynamicThreadPoolExecutor);
            }
        }else {
            //普通的线程池
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) bean;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + beanName).get();
             if (Objects.nonNull(threadPoolConfigEntity)) {
                threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
                threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
                if (threadPoolExecutor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
                    //此时判断是否是可以更改的队列
                    ResizableCapacityLinkedBlockingQueue<Runnable> queue = (ResizableCapacityLinkedBlockingQueue)threadPoolExecutor.getQueue();
                    queue.setCapacity(threadPoolConfigEntity.getQueueSize());
                }
                buildSimpleThreadEntityAndRegist(beanName,threadPoolExecutor);
            }else {
                //第一次注册，直接注册到redis中即可
                buildSimpleThreadEntityAndRegist(beanName,threadPoolExecutor);
            }
        }
    }

    private void buildSimpleThreadEntityAndRegist(String name,ThreadPoolExecutor threadPoolExecutor) {
         ThreadPoolConfigEntity entity = ThreadPoolConfigEntity.builder().poolSize(threadPoolExecutor.getPoolSize())
                .activeCount(threadPoolExecutor.getActiveCount())
                .appName(applicationName)
                .threadPoolName(name)
                .maximumPoolSize(threadPoolExecutor.getMaximumPoolSize())
                .corePoolSize(threadPoolExecutor.getCorePoolSize())
                .queueType(threadPoolExecutor.getQueue().getClass().getSimpleName())
                .queueSize(threadPoolExecutor.getQueue().size())
                 .dynamic(false)
                .threadPoolId(name).build();
        //注册到全局的线程池管理器
        GlobalThreadPoolManage.registPool(entity);
        GlobalThreadPoolManage.registSimplePool(name,threadPoolExecutor);
    }

    private void buildThreadEntityAndRegist(DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
        ThreadPoolConfigEntity entity = ThreadPoolConfigEntity.builder()
                .poolSize(dynamicThreadPoolExecutor.getPoolSize())
                .activeCount(dynamicThreadPoolExecutor.getActiveCount())
                .appName(applicationName)
                .threadPoolName(dynamicThreadPoolExecutor.getThreadPoolId())
                .maximumPoolSize(dynamicThreadPoolExecutor.getMaximumPoolSize())
                .corePoolSize(dynamicThreadPoolExecutor.getCorePoolSize())
                .queueType(dynamicThreadPoolExecutor.getQueue().getClass().getSimpleName())
                .queueSize(dynamicThreadPoolExecutor.getQueue().size())
                .dynamic(true)
                .threadPoolId(dynamicThreadPoolExecutor.getThreadPoolId()).build();
        //通过全局线程池管理器注册到redis中
        GlobalThreadPoolManage.registPool(entity);
        GlobalThreadPoolManage.registDynamicPool(dynamicThreadPoolExecutor.getThreadPoolId(), dynamicThreadPoolExecutor);
    }
}