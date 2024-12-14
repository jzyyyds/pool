package org.example.service.impl;


import org.example.alarm.AlarmStrategy;
import org.example.config.DynamicThreadPoolNotifyAutoProperties;
import org.example.domain.vo.AlarmMessageVo;
import org.example.service.IAlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class AlarmServiceImpl implements IAlarmService {
    private final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);

    private Map<String, AlarmStrategy> strategies = new HashMap<>();

    private final DynamicThreadPoolNotifyAutoProperties dynamicThreadPoolNotifyAutoProperties;

    public AlarmServiceImpl(DynamicThreadPoolNotifyAutoProperties dynamicThreadPoolNotifyAutoProperties, List<AlarmStrategy> alarmStrategies){
        this.dynamicThreadPoolNotifyAutoProperties = dynamicThreadPoolNotifyAutoProperties;
        this.strategies = alarmStrategies.stream().collect(Collectors.toMap(AlarmStrategy::getStrategyName,strategies->strategies));
    }

    @Override
    public void send(AlarmMessageVo message) {
        List<String> usePlatform = dynamicThreadPoolNotifyAutoProperties.getUsePlatform();
        for (int i = 0; i < usePlatform.size(); i++) {
            AlarmStrategy alarmStrategy = strategies.get(usePlatform.get(i));
            alarmStrategy.sendNotify(message);
        }
    }
}
