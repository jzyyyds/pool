package com.example.pool.spring.boot.start.config.service.registry;

import com.example.pool.spring.boot.start.config.service.domain.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心的上报
 */

public interface IRegistry {

    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
