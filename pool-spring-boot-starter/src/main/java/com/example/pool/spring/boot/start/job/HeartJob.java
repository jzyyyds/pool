package com.example.pool.spring.boot.start.job;

import com.alibaba.fastjson.JSON;
import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.service.IDynamicThreadPoolService;
import com.example.pool.spring.boot.start.service.IHeartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

public class HeartJob {
    private final Logger logger = LoggerFactory.getLogger(HeartJob.class);

    private final IHeartService heartService;

    public HeartJob(IHeartService heartService) {
        this.heartService = heartService;
    }

    @Scheduled(cron = "0/15 * * * * ?")
    public void execReportThreadPoolList() {
        heartService.registToRedis();
    }
}
