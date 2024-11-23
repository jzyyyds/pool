package org.example.plugin;

import org.example.manager.ThreadPoolPlugin;

/**
 * 线程池自行任务的插件接口
 */

public interface ExecuteAwarePlugin extends ThreadPoolPlugin {

    default void beforeExecute(Thread thread, Runnable runnable) {
    }

    default void afterExecute(Runnable runnable, Throwable throwable) {
    }
}
