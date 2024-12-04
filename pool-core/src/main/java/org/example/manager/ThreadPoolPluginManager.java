package org.example.manager;

import org.example.plugin.ExecuteAwarePlugin;
import org.example.plugin.RejectedAwarePlugin;
import org.example.plugin.ShutdownAwarePlugin;

import java.util.Collection;

public interface ThreadPoolPluginManager {
    void clear();

    void register(ThreadPoolPlugin plugin);

    Collection<ExecuteAwarePlugin> getExecuteAwarePluginList();

    Collection<RejectedAwarePlugin> getRejectedAwarePluginList();

    Collection<ShutdownAwarePlugin> getShutdownAwarePluginList();

    ThreadPoolPlugin getPlugin(String pluginId);
}
