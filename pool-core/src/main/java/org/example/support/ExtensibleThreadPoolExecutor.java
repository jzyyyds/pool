package org.example.support;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.manager.ThreadPoolPlugin;
import org.example.manager.ThreadPoolPluginManager;
import org.example.plugin.ExecuteAwarePlugin;
import org.example.plugin.RejectedAwarePlugin;
import org.example.plugin.ShutdownAwarePlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * 这个类是扩展的线程类，此时为原生的线程池提供记录任务耗时的操作
 */
public class ExtensibleThreadPoolExecutor extends ThreadPoolExecutor {

    private final ThreadPoolPluginManager threadPoolPluginManager;

    /**
     *
     * @param threadPoolPluginManager
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
        Collection<ExecuteAwarePlugin> threadPoolPlugins = threadPoolPluginManager.getExecutePlugin();
        threadPoolPlugins.forEach(poolPlugin -> poolPlugin.beforeExecute(thread,runnable));
    }



    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        //从管理器中获取到需要执行的插件，然后执行
        Collection<ExecuteAwarePlugin> threadPoolPlugins = threadPoolPluginManager.getExecutePlugin();
        threadPoolPlugins.forEach(poolPlugin -> poolPlugin.afterExecute(r,t));
    }

    @Override
    public void shutdown() {
        Collection<ShutdownAwarePlugin> shutDownPlugin = threadPoolPluginManager.getShutDownPlugin();
        shutDownPlugin.forEach(plugin->plugin.beforeShutdown(this));
        super.shutdown();
        shutDownPlugin.forEach(plugin->plugin.afterShutdown(this, Collections.emptyList()));
    }

    @Override
    public List<Runnable> shutdownNow() {
        Collection<ShutdownAwarePlugin> shutDownPlugin = threadPoolPluginManager.getShutDownPlugin();
        shutDownPlugin.forEach(plugin->plugin.beforeShutdown(this));
        List<Runnable> runnables = super.shutdownNow();
        shutDownPlugin.forEach(plugin->plugin.afterShutdown(this, runnables));
        return runnables;
    }

    @AllArgsConstructor
    private static class RejectedAwareHandlerWrapper implements RejectedExecutionHandler {

        //插件管理器
        private final ThreadPoolPluginManager registry;

        @Setter
        @Getter
        //真正的拒绝策略处理器
        private RejectedExecutionHandler handler;

        //在执行拒绝策略之前，会先执行拒绝策略插件对象中的方法，就是执行通知告警功能
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Collection<RejectedAwarePlugin> rejectPlugin = registry.getRejectPlugin();
            rejectPlugin.forEach(plugin->plugin.beforeRejectedExecution(r,executor));
            handler.rejectedExecution(r,executor);
        }
    }

}
