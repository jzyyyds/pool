package org.example.executor;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.manager.DefaultThreadPoolPluginRegist;
import org.example.manager.DefultThreadPoolPluginManager;
import org.example.support.ExtensibleThreadPoolExecutor;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 动态线程池类，给应用程序使用的
 */
@Slf4j
public class DynamicThreadPoolExecutor extends ExtensibleThreadPoolExecutor implements DisposableBean {

    @Getter
    @Setter
    public boolean waitForTasksToCompleteOnShutdown;

    public DynamicThreadPoolExecutor(
                                     int corePoolSize, int maximumPoolSize,
                                     long keepAliveTime, TimeUnit unit,
                                     long executeTimeOut, boolean waitForTasksToCompleteOnShutdown, long awaitTerminationMillis,
                                     @NonNull BlockingQueue<Runnable> blockingQueue,
                                     @NonNull String threadPoolId,
                                     @NonNull ThreadFactory threadFactory,
                                     @NonNull RejectedExecutionHandler rejectedExecutionHandler) {
        super(
                threadPoolId, new DefultThreadPoolPluginManager(),
                corePoolSize, maximumPoolSize, keepAliveTime, unit,
                blockingQueue, threadFactory, rejectedExecutionHandler);
        log.info("Initializing ExecutorService {}", threadPoolId);
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        //内置的插件需要进行注册，此时在这里注入
        new DefaultThreadPoolPluginRegist().doRegister(this);
    }

    @Override
    public void destroy() throws Exception {
        if (isWaitForTasksToCompleteOnShutdown()) {
            super.shutdown();
        } else {
            super.shutdownNow();
        }//在这里清空插件管理器中的插件
        getThreadPoolPluginManager().clear();
    }


}
