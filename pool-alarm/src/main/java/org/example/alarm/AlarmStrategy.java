package org.example.alarm;


import org.example.domain.vo.AlarmMessageVo;

/**
 * 告警的接口
 */
public interface AlarmStrategy {

    void sendNotify(AlarmMessageVo alarmMessageVo);
    String getStrategyName();
}
