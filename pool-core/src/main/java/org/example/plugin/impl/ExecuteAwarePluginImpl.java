package org.example.plugin.impl;

import org.example.enums.PluginNameEnums;
import org.example.plugin.ExecuteAwarePlugin;

/**
 * 线程池框架内置的插件
 */
public class ExecuteAwarePluginImpl implements ExecuteAwarePlugin {

    @Override
    public void beforeExecute(Thread thread, Runnable runnable) {
        //TODO 还没有实现
    }

    @Override
    public void afterExecute(Runnable runnable, Throwable throwable) {
        //TODO 还没有实现
    }

    @Override
    public String getId() {
        return PluginNameEnums.EXECTE.getKey();
    }
}
