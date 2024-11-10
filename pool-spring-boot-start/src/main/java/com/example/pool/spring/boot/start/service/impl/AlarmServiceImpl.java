package com.example.pool.spring.boot.start.service.impl;

import com.example.pool.spring.boot.start.alarm.AlarmStrategy;
import com.example.pool.spring.boot.start.config.DynamicThreadPoolNotifyAutoProperties;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import com.example.pool.spring.boot.start.service.IAlarmService;
import com.example.pool.spring.boot.start.service.abstracts.AlarmAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
        AlarmStrategy email = strategies.get("email");
        email.sendNotify(message);
    }
}
