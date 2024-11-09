package com.example.pool.spring.boot.start.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.service.IDynamicThreadPoolService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class DynamicThreadPoolService implements IDynamicThreadPoolService {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);
    private final String applicationName;
    private final ConcurrentHashMap<String, ThreadPoolExecutor> threadPoolExecutorMap;
    public DynamicThreadPoolService(String applicationName, ConcurrentHashMap<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolBeanNames.size());
        for (String beanName : threadPoolBeanNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(beanName);
            ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, beanName);
            threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
            threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
            threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
            threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
            threadPoolVOS.add(threadPoolConfigVO);
        }
        return threadPoolVOS;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        if (StringUtils.isBlank(threadPoolName)){
            logger.info("查询的名称为空");
        }
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (null == threadPoolExecutor) return new ThreadPoolConfigEntity(applicationName, threadPoolName);

        // 线程池配置数据
        ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
        threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());

        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, threadPoolName, JSON.toJSONString(threadPoolConfigVO));
        }

        return threadPoolConfigVO;
    }

    @Override
    public boolean updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        //1.先判断应用的名称是否相同以及线程池是否为空
        if (threadPoolConfigEntity==null){
            return false;
        }
        if (!threadPoolConfigEntity.getAppName().equals(applicationName)){
            return false;
        }
        String threadPoolName = threadPoolConfigEntity.getThreadPoolName();
        if (threadPoolName==null&&threadPoolName.length()==0){
            return false;
        }
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (threadPoolExecutor==null){
            return false;
        }
        int corePoolSize = threadPoolConfigEntity.getCorePoolSize();
        int maximumPoolSize = threadPoolConfigEntity.getMaximumPoolSize();
        if (corePoolSize>maximumPoolSize){
            //TODO 增加告警的信息
            logger.error("动态线程池, 变更配置时出错(最大线程数小于核心线程数): {}", threadPoolConfigEntity);
            return false;
        }
        //变更的时候需要注意，要满足核心线程数小于最大线程数
        if (corePoolSize < threadPoolExecutor.getMaximumPoolSize()) {
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        } else {
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        }
        //TODO 变更阻塞队列的大小
        //TODO 推送变更成功的信息
        return true;
    }
}
