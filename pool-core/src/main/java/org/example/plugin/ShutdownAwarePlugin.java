package org.example.plugin;

import org.example.manager.ThreadPoolPlugin;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public interface ShutdownAwarePlugin extends ThreadPoolPlugin {
    default void beforeShutdown(ThreadPoolExecutor executor) {
    }

    default void afterShutdown(ThreadPoolExecutor executor, List<Runnable> remainingTasks) {
    }
}
