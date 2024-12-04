package org.example.plugin.impl;

import org.example.enums.PluginNameEnums;
import org.example.plugin.ShutdownAwarePlugin;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池内置的shutdown插件
 */
public class ShutdownAwarePluginImpl implements ShutdownAwarePlugin {
    @Override
    public String getId() {
        return PluginNameEnums.SHUTDOWN.getKey();
    }

    @Override
    public void beforeShutdown(ThreadPoolExecutor executor) {
       //TODO 还没有实现
    }

    @Override
    public void afterShutdown(ThreadPoolExecutor executor, List<Runnable> remainingTasks) {
        //TODO 还没有实现
    }
}
