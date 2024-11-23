package org.example.manager;

import com.sun.istack.internal.NotNull;
import org.example.plugin.ExecuteAwarePlugin;
import org.example.plugin.RejectedAwarePlugin;
import org.example.plugin.ShutdownAwarePlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 线程池插件的管理器
 */
public class ThreadPoolPluginManager {

    //存放线程池插件对象的集合
    private final Map<String, List<ThreadPoolPlugin>> registPluginMap = new ConcurrentHashMap<>(16);
    //存放runnableTask.run()方法功能扩展插件对象的集合
    private final List<ExecuteAwarePlugin> executeAwarePluginList = new CopyOnWriteArrayList<>();

    //存放拒绝策略插件对象的集合
    private final List<RejectedAwarePlugin> rejectedAwarePluginList = new CopyOnWriteArrayList<>();

    //采访shutdown和shutdownNow插件对象的集合
    private final List<ShutdownAwarePlugin> shutdownAwarePluginList = new CopyOnWriteArrayList<>();

    /**
     * 将插件注入管理器
     * @param poolPlugin
     */
    public void regist(@NotNull ThreadPoolPlugin poolPlugin) {
        //判断插件的类型
        if (poolPlugin instanceof ExecuteAwarePlugin) {
            executeAwarePluginList.add((ExecuteAwarePlugin) poolPlugin);
        } else if (poolPlugin instanceof RejectedAwarePlugin) {
            rejectedAwarePluginList.add((RejectedAwarePlugin) poolPlugin);
        } else if (poolPlugin instanceof ShutdownAwarePlugin) {
            shutdownAwarePluginList.add((ShutdownAwarePlugin) poolPlugin);
        }
    }

    /**
     * 获得扩展execute方法的线程池的插件
     * @return
     */
    public Collection<ExecuteAwarePlugin> getExecutePlugin(){
        return executeAwarePluginList;
    }

    /**
     * 获得扩展execute方法的线程池的插件
     * @return
     */
    public Collection<RejectedAwarePlugin> getRejectPlugin(){
        return rejectedAwarePluginList;
    }

    /**
     * 获得扩展execute方法的线程池的插件
     * @return
     */
    public Collection<ShutdownAwarePlugin> getShutDownPlugin(){
        return shutdownAwarePluginList;
    }
}
