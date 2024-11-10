package com.example.pool.spring.boot.start.alarm;

import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;

/**
 * 告警的接口
 */
public interface AlarmStrategy {

    void sendNotify(AlarmMessageVo alarmMessageVo);
    String getStrategyName();
}
