package org.example.manager;

import lombok.NonNull;
import org.example.plugin.ExecuteAwarePlugin;
import org.example.plugin.RejectedAwarePlugin;
import org.example.plugin.ShutdownAwarePlugin;

import java.util.Collection;

public interface ThreadPoolPluginSupport extends ThreadPoolPluginManager{
    @NonNull
    ThreadPoolPluginManager getThreadPoolPluginManager();
    @Override
    default void clear() {
        getThreadPoolPluginManager().clear();
    }

    @Override
    default void register(ThreadPoolPlugin plugin){
        getThreadPoolPluginManager().register(plugin);
    }

    @Override
    default Collection<ExecuteAwarePlugin> getExecuteAwarePluginList(){
        return getThreadPoolPluginManager().getExecuteAwarePluginList();
    }

    @Override
    default Collection<RejectedAwarePlugin> getRejectedAwarePluginList(){
        return getThreadPoolPluginManager().getRejectedAwarePluginList();
    }

    @Override
    default Collection<ShutdownAwarePlugin> getShutdownAwarePluginList(){
        return getThreadPoolPluginManager().getShutdownAwarePluginList();
    }

    @Override
    default ThreadPoolPlugin getPlugin(String pluginId){
        return getThreadPoolPluginManager().getPlugin(pluginId);
    }
}
