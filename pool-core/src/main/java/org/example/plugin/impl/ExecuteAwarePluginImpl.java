package org.example.plugin.impl;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.example.config.ApplicationContextHolder;
import org.example.domain.vo.AlarmMessageVo;
import org.example.enums.PluginNameEnums;
import org.example.plugin.ExecuteAwarePlugin;
import org.example.service.IAlarmService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 线程池框架内置的插件
 */
@Slf4j
public class ExecuteAwarePluginImpl implements ExecuteAwarePlugin {

    private final ThreadLocal<Pair<Long,Thread>> startTimes = new ThreadLocal<>();

    //代表任务超时时间,默认值是10s
    private long executeTimeOut;

    private String threadPoolId;

    public ExecuteAwarePluginImpl(String threadPoolId,Long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
        this.threadPoolId = threadPoolId;
    }

    @Override
    public void beforeExecute(Thread thread, Runnable runnable) {
        //获取执行前的耗时
        startTimes.set(new Pair<>(currentTime(),thread));
    }

    @Override
    public void afterExecute(Runnable runnable, Throwable throwable) {
        try {
            //从线程本地map中得到任务的开始时间
            Optional.ofNullable(startTimes.get().getKey())
                    // 计算出耗时时间
                    .map(startTime -> currentTime() - startTime)
                    // 交给 processTaskTime 方法处理
                    .ifPresent(duration -> processTaskTime(duration,runnable,startTimes.get().getValue()));
        } finally {
            //清除线程本地map
            startTimes.remove();
        }
    }

    private void processTaskTime(Long time,Runnable runnable,Thread thread) {
        if (time<executeTimeOut){
            return;
        }
        //此时超过了限定的时间，此时需要进行告警
        AlarmMessageVo alarmMessage = buildAlarmMessage(runnable,time,thread);
        IAlarmService alarmService = ApplicationContextHolder.getBean(IAlarmService.class);
        alarmService.send(alarmMessage);
    }

    private AlarmMessageVo buildAlarmMessage(Runnable runnable,Long time,Thread thread) {
        AlarmMessageVo alarmMessageVo = new AlarmMessageVo();
        alarmMessageVo.setMessage("任务执行超时！！！");
        Map<String,String> map = new HashMap<>();
        map.put("threadPoolId:",threadPoolId);
        map.put("thread:",thread.getName());
        map.put("execute_time:",time.toString());
        alarmMessageVo.setParameters(map);
        return alarmMessageVo;
    }

    @Override
    public String getId() {
        return PluginNameEnums.EXECTE.getKey();
    }

    private long currentTime(){
        return System.currentTimeMillis();
    }
}
