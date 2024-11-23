package org.example.manager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 线程池插件的管理器
 */
public class ThreadPoolPluginManager {
     //存放线程池插件对象的集合
    private final List<ThreadPoolPlugin> threadPoolPluginList = new CopyOnWriteArrayList<>();

    /**
     * 将插件注入管理器
     * @param poolPlugin
     */
    public void regist(ThreadPoolPlugin poolPlugin){
        threadPoolPluginList.add(poolPlugin);
    }

    /**
     * 获得线程池的插件
     * @return
     */
    public Collection<ThreadPoolPlugin> getAllPlugin(){
        return threadPoolPluginList;
    }
}
