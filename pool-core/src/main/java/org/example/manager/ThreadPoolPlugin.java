package org.example.manager;

/**
 * 线程池的插件接口，用来给用户自定义扩展的插件接口
 */
public interface ThreadPoolPlugin {

    String getId();

    default void beforeExecute(Thread thread, Runnable runnable) {

    }

    default void afterExecute(Runnable runnable, Throwable throwable) {

    }
}
