package com.example.pool.spring.boot.start.alarm.impl;

import com.example.pool.spring.boot.start.config.DynamicThreadPoolNotifyAutoProperties;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import com.example.pool.spring.boot.start.service.abstracts.AlarmAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeishuStrategy extends AlarmAbstract {
    private final Logger logger = LoggerFactory.getLogger(FeishuStrategy.class);

    private final DynamicThreadPoolNotifyAutoProperties notifyProperties;

    public FeishuStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Override
    public void sendNotify(AlarmMessageVo notifyMsg) {
        logger.debug("飞书通知暂未实现。");
    }

    @Override
    public String getStrategyName() {
        return "feishu";
    }
}
