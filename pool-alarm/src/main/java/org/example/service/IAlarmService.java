package org.example.service;


import org.example.domain.vo.AlarmMessageVo;

public interface IAlarmService {
    void send(AlarmMessageVo message);
}
