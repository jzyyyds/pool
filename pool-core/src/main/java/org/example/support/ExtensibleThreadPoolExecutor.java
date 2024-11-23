package org.example.support;


import org.example.manager.ThreadPoolPlugin;
import org.example.manager.ThreadPoolPluginManager;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * 这个类是扩展的线程类，此时为原生的线程池提供记录任务耗时的操作
 */
public class ExtensibleThreadPoolExecutor extends ThreadPoolExecutor {

    private final ThreadPoolPluginManager threadPoolPluginManager;

    /**
     * 构造函数，参数参考原生的线程池
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     */
    public ExtensibleThreadPoolExecutor(ThreadPoolPluginManager threadPoolPluginManager,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadPoolPluginManager = threadPoolPluginManager;
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable runnable) {
        //从管理器中获取到需要执行的插件，然后执行
        Collection<ThreadPoolPlugin> threadPoolPlugins = threadPoolPluginManager.getAllPlugin();
        threadPoolPlugins.forEach(poolPlugin -> poolPlugin.beforeExecute(thread,runnable));
    }



    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        //从管理器中获取到需要执行的插件，然后执行
        Collection<ThreadPoolPlugin> threadPoolPlugins = threadPoolPluginManager.getAllPlugin();
        threadPoolPlugins.forEach(poolPlugin -> poolPlugin.afterExecute(r,t));
    }

}
