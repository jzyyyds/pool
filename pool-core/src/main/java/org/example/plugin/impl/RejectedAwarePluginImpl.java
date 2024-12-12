package org.example.plugin.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ApplicationContextHolder;
import org.example.domain.vo.AlarmMessageVo;
import org.example.enums.PluginNameEnums;
import org.example.plugin.RejectedAwarePlugin;
import org.example.service.IAlarmService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池内置的reject插件
 */
@Slf4j
public class RejectedAwarePluginImpl implements RejectedAwarePlugin {


    private String threadPoolId;

    public RejectedAwarePluginImpl(String threadPoolId) {
        this.threadPoolId = threadPoolId;
    }

    @Override
    public void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        //发送告警信息
        log.info("进入拒绝策略啦");
        AlarmMessageVo alarmMessageVo = buildAlarmMessage(runnable, executor);
        IAlarmService alarmService = ApplicationContextHolder.getBean(IAlarmService.class);
        alarmService.send(alarmMessageVo);
    }

    private AlarmMessageVo buildAlarmMessage(Runnable runnable, ThreadPoolExecutor executor) {
        AlarmMessageVo alarmMessageVo = new AlarmMessageVo();
        alarmMessageVo.setMessage("线程池执行了拒绝策略，请注意！！！");
        Map<String,String> map = new HashMap<>();
        map.put("threadPoolId:",threadPoolId);
        map.put("reject_handle:",executor.getRejectedExecutionHandler().toString());
        map.put("runnable:",runnable.toString());
        alarmMessageVo.setParameters(map);
        return alarmMessageVo;
    }

    @Override
    public String getId() {
        return PluginNameEnums.REJECTED.getKey();
    }
}
