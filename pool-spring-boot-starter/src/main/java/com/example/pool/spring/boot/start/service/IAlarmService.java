package com.example.pool.spring.boot.start.service;

import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;

public interface IAlarmService {
    void send(AlarmMessageVo message);
}
