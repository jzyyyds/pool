package org.example.plugin.impl;

import org.example.enums.PluginNameEnums;
import org.example.plugin.ExecuteAwarePlugin;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 线程池框架内置的插件
 */
public class ExecuteAwarePluginImpl implements ExecuteAwarePlugin {

    private final ThreadLocal<Long> startTimes = new ThreadLocal<>();

    //代表任务超时时间,默认值是10s
    @Value("${dynamic.run.out.time}:10")
    private Long executeTimeOut;


    @Override
    public void beforeExecute(Thread thread, Runnable runnable) {
        //获取执行前的耗时
        startTimes.set(currentTime());
    }

    @Override
    public void afterExecute(Runnable runnable, Throwable throwable) {
        try {
            //从线程本地map中得到任务的开始时间
            Optional.ofNullable(startTimes.get())
                    //计算出耗时时间
                    .map(startTime -> currentTime() - startTime)
                    //交给processTaskTime方法处理
                    .ifPresent(this::processTaskTime);
        } finally {
            //清除线程本地map
            startTimes.remove();
        }

    }

    private void processTaskTime(Long time) {
        if (time<executeTimeOut){
            return;
        }
    }

    @Override
    public String getId() {
        return PluginNameEnums.EXECTE.getKey();
    }

    private long currentTime(){
        return System.currentTimeMillis();
    }
}
