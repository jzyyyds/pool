package org.example.support;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.example.build.ThreadPoolBuilder;
import org.example.executor.DynamicThreadPoolExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.*;

@Slf4j
public class AbstractBuildThreadPoolTemplate {

    public static DynamicThreadPoolExecutor buildDynamicPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
        try {
            dynamicThreadPoolExecutor = new DynamicThreadPoolExecutor(
                    initParam.getCorePoolNum(),
                    initParam.getMaxPoolNum(),
                    initParam.getKeepAliveTime(),
                    initParam.getTimeUnit(),
                    initParam.getExecuteTimeOut(),
                    initParam.getWaitForTasksToCompleteOnShutdown(),
                    initParam.getAwaitTerminationMillis(),
                    initParam.getWorkQueue(),
                    initParam.getThreadPoolId(),
                    initParam.getThreadFactory(),
                    initParam.getRejectedExecutionHandler());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(String.format("Error creating thread pool parameter. threadPool id: %s", initParam.getThreadPoolId()), ex);
        }//在这里设置了任务装饰器
        dynamicThreadPoolExecutor.allowCoreThreadTimeOut(initParam.allowCoreThreadTimeOut);
        return dynamicThreadPoolExecutor;
    }



    @Data
    @Accessors(chain = true)
    public static class ThreadPoolInitParam {

        private Integer corePoolNum;

        private Integer maxPoolNum;

        private Long keepAliveTime;

        private TimeUnit timeUnit;

        private Long executeTimeOut;

        private Integer capacity;

        private BlockingQueue<Runnable> workQueue;

        private RejectedExecutionHandler rejectedExecutionHandler;

        private ThreadFactory threadFactory;

        private String threadPoolId;

        private Long awaitTerminationMillis;

        private Boolean waitForTasksToCompleteOnShutdown;

        private Boolean allowCoreThreadTimeOut = false;

        public ThreadPoolInitParam(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
        }
    }
}
