package com.example.pool.spring.boot.start.runner;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.build.DynamicPool;
import org.example.executor.DynamicThreadPoolExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@AllArgsConstructor
@Slf4j
public class PrometheusConfigRunner implements InitializingBean {
    private final ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Registering ThreadPoolExecutor beans...");
        String[] beanNamesForAnnotation = applicationContext.getBeanNamesForAnnotation(DynamicPool.class);
        for (String bean : beanNamesForAnnotation) {
            Object object = applicationContext.getBean(bean);
            if (object instanceof ThreadPoolExecutor || object instanceof DynamicThreadPoolExecutor) {
                //直接上报
                registerParam(object, bean);
            }
        }
    }

    private void registerParam(Object object, String beanName) {
        List<Tag> tags = Arrays.asList(
                new ImmutableTag("applicationName", getApplicationName()),
                new ImmutableTag("poolName", beanName)
        );
        if (object instanceof DynamicThreadPoolExecutor) {
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) object;
            Metrics.gauge("thread_pool_core_size", tags, dynamicThreadPoolExecutor, DynamicThreadPoolExecutor::getCorePoolSize);
            Metrics.gauge("thread_pool_max_size", tags, dynamicThreadPoolExecutor, DynamicThreadPoolExecutor::getMaximumPoolSize);
            Metrics.gauge("thread_pool_active_thread_count", tags, dynamicThreadPoolExecutor, DynamicThreadPoolExecutor::getActiveCount);
            Metrics.gauge("thread_pool_size", tags, dynamicThreadPoolExecutor, DynamicThreadPoolExecutor::getPoolSize);
            Metrics.gauge("thread_pool_queue_size", tags, dynamicThreadPoolExecutor,
                    (threadPoolExecutor) -> threadPoolExecutor.getQueue().size()
            );
            Metrics.gauge("thread_pool_queue_remaining_capacity", tags, dynamicThreadPoolExecutor,
                    (threadPoolExecutor) -> threadPoolExecutor.getQueue().remainingCapacity()
            );
        } else {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) object;
            Metrics.gauge("thread_pool_core_size", tags, executor, ThreadPoolExecutor::getCorePoolSize);
            Metrics.gauge("thread_pool_max_size", tags, executor, ThreadPoolExecutor::getMaximumPoolSize);
            Metrics.gauge("thread_pool_active_thread_count", tags, executor, ThreadPoolExecutor::getActiveCount);
            Metrics.gauge("thread_pool_size", tags, executor, ThreadPoolExecutor::getPoolSize);
            Metrics.gauge("thread_pool_queue_size", tags, executor,
                    (threadPoolExecutor) -> threadPoolExecutor.getQueue().size()
            );
            Metrics.gauge("thread_pool_queue_remaining_capacity", tags, executor,
                    (threadPoolExecutor) -> threadPoolExecutor.getQueue().remainingCapacity()
            );
        }
    }

    private String getApplicationName(){
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("get host error");
        }
        return applicationName+":"+hostAddress;
    }
}
