package org.example.manager;


import lombok.extern.slf4j.Slf4j;
import org.example.plugin.ExecuteAwarePlugin;
import org.example.plugin.RejectedAwarePlugin;
import org.example.plugin.ShutdownAwarePlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程池插件的管理器
 */
@Slf4j
public class DefultThreadPoolPluginManager implements ThreadPoolPluginManager{
    //读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    //存放线程池插件对象的集合
    private final Map<String, ThreadPoolPlugin> registPluginMap = new ConcurrentHashMap<>(16);
    //存放runnableTask.run()方法功能扩展插件对象的集合
    private final List<ExecuteAwarePlugin> executeAwarePluginList = new CopyOnWriteArrayList<>();
    //存放拒绝策略插件对象的集合
    private final List<RejectedAwarePlugin> rejectedAwarePluginList = new CopyOnWriteArrayList<>();
    //采访shutdown和shutdownNow插件对象的集合
    private final List<ShutdownAwarePlugin> shutdownAwarePluginList = new CopyOnWriteArrayList<>();

    /**
     * 将插件注入管理器
     * @param plugin
     */
    @Override
    public void register(ThreadPoolPlugin plugin) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            String pluginId = plugin.getId();
            //判断是否已经注册过了
            boolean flag = isRegist(pluginId);
            if (!flag) {
                return;
            }
            registPluginMap.put(pluginId, plugin);
            //判断插件的类型
            if (plugin instanceof ExecuteAwarePlugin) {
                executeAwarePluginList.add((ExecuteAwarePlugin) plugin);
            } else if (plugin instanceof RejectedAwarePlugin) {
                rejectedAwarePluginList.add((RejectedAwarePlugin) plugin);
            } else if (plugin instanceof ShutdownAwarePlugin) {
                shutdownAwarePluginList.add((ShutdownAwarePlugin) plugin);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private boolean isRegist(String pluginId) {
        //有人可能会有疑问，此时已经获取到写锁了，此时读锁不是一定会失败吗？但是对于同一线程来说，可以先获取写锁，然后再获取读锁，此时参考读写锁的锁降级
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            ThreadPoolPlugin threadPoolPlugins = registPluginMap.get(pluginId);
            if (threadPoolPlugins != null) {
                //TODO 先打印日志
                log.info("repeat register plugin！！！");
                return false;
            }
        }finally {
            readLock.unlock();
        }
        return true;
    }

    /**
     * 获得扩展execute方法的线程池的插件
     * @return
     */
    @Override
    public Collection<ExecuteAwarePlugin> getExecuteAwarePluginList() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
             return executeAwarePluginList;
        }finally {
            readLock.unlock();
        }

    }

     /**
     * 获得扩展reject方法的线程池的插件
     * @return
     */
    @Override
    public Collection<RejectedAwarePlugin> getRejectedAwarePluginList() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
             return rejectedAwarePluginList;
        }finally {
            readLock.unlock();
        }
    }

     /**
     * 获得扩展shutdown方法的线程池的插件
     * @return
     */
    @Override
    public Collection<ShutdownAwarePlugin> getShutdownAwarePluginList() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
             return shutdownAwarePluginList;
        }finally {
            readLock.unlock();
        }
    }


    @Override
    public void clear() {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            registPluginMap.clear();
            executeAwarePluginList.clear();
            rejectedAwarePluginList.clear();
            shutdownAwarePluginList.clear();
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public ThreadPoolPlugin getPlugin(String pluginId) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return registPluginMap.get(pluginId);
        }finally {
            readLock.unlock();
        }
    }
}
