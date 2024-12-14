package com.example.pool.spring.boot.start.job;

import com.example.pool.spring.boot.start.service.IHeartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

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
