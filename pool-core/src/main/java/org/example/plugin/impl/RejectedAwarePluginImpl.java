package org.example.plugin.impl;

import org.example.enums.PluginNameEnums;
import org.example.plugin.RejectedAwarePlugin;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池内置的reject插件
 */
public class RejectedAwarePluginImpl implements RejectedAwarePlugin {
    @Override
    public void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        //TODO 还没有实现
    }

    @Override
    public String getId() {
        return PluginNameEnums.REJECTED.getKey();
    }
}
