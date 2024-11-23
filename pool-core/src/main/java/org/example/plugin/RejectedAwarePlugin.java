package org.example.plugin;

import org.example.manager.ThreadPoolPlugin;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略的扩展接口
 */
public interface RejectedAwarePlugin extends ThreadPoolPlugin {
    //该方法会在拒绝策略方法执行之前被回调
    default void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
    }
}
