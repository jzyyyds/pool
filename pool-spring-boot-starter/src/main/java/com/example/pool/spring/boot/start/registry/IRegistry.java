package com.example.pool.spring.boot.start.registry;

import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心的上报
 */

public interface IRegistry {

    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);

    void updateThreadPoolEntity(ThreadPoolConfigEntity threadPoolConfigEntity);
}
