package com.example.pool.spring.boot.start.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import com.example.pool.spring.boot.start.manager.GlobalThreadPoolManage;
import com.example.pool.spring.boot.start.registry.IRegistry;
import com.example.pool.spring.boot.start.service.IAlarmService;
import com.example.pool.spring.boot.start.service.IDynamicThreadPoolService;
import org.apache.commons.lang3.StringUtils;
import org.example.executor.DynamicThreadPoolExecutor;
import org.example.queue.ResizableCapacityLinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class DynamicThreadPoolService implements IDynamicThreadPoolService {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);
    private final String applicationName;
    @Autowired
    private IAlarmService alarmService;

    private final IRegistry registry;

    public DynamicThreadPoolService(String applicationName,IRegistry registry) {
        this.applicationName = applicationName;
        this.registry = registry;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        Map<String, ThreadPoolConfigEntity> entity = GlobalThreadPoolManage.getEntity();
        List<ThreadPoolConfigEntity> result = entity.values().stream().collect(Collectors.toList());
        for (ThreadPoolConfigEntity threadPoolConfigEntity : result) {
            boolean dynamic = threadPoolConfigEntity.isDynamic();
            if (dynamic) {
                DynamicThreadPoolExecutor dynamicPool = GlobalThreadPoolManage.getDynamicPool(threadPoolConfigEntity.getThreadPoolId());
                threadPoolConfigEntity.setPoolSize(dynamicPool.getPoolSize());
                threadPoolConfigEntity.setQueueSize(dynamicPool.getQueue().size());
                threadPoolConfigEntity.setRemainingCapacity(dynamicPool.getQueue().remainingCapacity());
                threadPoolConfigEntity.setActiveCount(dynamicPool.getActiveCount());
                //重新放回全局线程池的管理器
                GlobalThreadPoolManage.updateEntity(threadPoolConfigEntity.getThreadPoolId(),threadPoolConfigEntity);
            }else {
                ThreadPoolExecutor simplePool = GlobalThreadPoolManage.getSimplePool(threadPoolConfigEntity.getThreadPoolName());
                threadPoolConfigEntity.setPoolSize(simplePool.getPoolSize());
                threadPoolConfigEntity.setQueueSize(simplePool.getQueue().size());
                threadPoolConfigEntity.setRemainingCapacity(simplePool.getQueue().remainingCapacity());
                threadPoolConfigEntity.setActiveCount(simplePool.getActiveCount());
                 //重新放回全局线程池的管理器
                GlobalThreadPoolManage.updateEntity(threadPoolConfigEntity.getThreadPoolName(),threadPoolConfigEntity);
            }
        }
        return result;
//        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
//        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolBeanNames.size());
//        for (String beanName : threadPoolBeanNames) {
//            ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, beanName);
//            threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
//            threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
//            threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
//            threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
//            threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
//            threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
//            threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
//            threadPoolVOS.add(threadPoolConfigVO);
//        }
//        return threadPoolVOS;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        if (StringUtils.isBlank(threadPoolName)){
            logger.info("查询的名称为空");
        }
        ThreadPoolConfigEntity entityByName = GlobalThreadPoolManage.getEntityByName(threadPoolName);
        if (null == entityByName) {
            return new ThreadPoolConfigEntity(applicationName,threadPoolName);
        }
        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, threadPoolName, JSON.toJSONString(entityByName));
        }

        return entityByName;
//        if (null == threadPoolExecutor) return new ThreadPoolConfigEntity(applicationName, threadPoolName);
//
//        // 线程池配置数据
//        ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
//        threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
//        threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
//        threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
//        threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
//        threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
//        threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
//        threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());


        //return threadPoolConfigVO;
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
        if (threadPoolConfigEntity.isDynamic()){
            DynamicThreadPoolExecutor threadPoolExecutor = GlobalThreadPoolManage.getDynamicPool(threadPoolConfigEntity.getThreadPoolName());
            updateMessage(threadPoolExecutor,threadPoolConfigEntity);
        }else {
            ThreadPoolExecutor threadPoolExecutor = GlobalThreadPoolManage.getSimplePool(threadPoolConfigEntity.getThreadPoolName());
            updateMessage(threadPoolExecutor,threadPoolConfigEntity);
        }
        //ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);

        return true;
    }

    private <T extends ThreadPoolExecutor> void updateMessage(T threadPoolExecutor,ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (threadPoolExecutor==null){
            logger.info("threadPoolExecutor is null");
        }
        int corePoolSize = threadPoolConfigEntity.getCorePoolSize();
        int maximumPoolSize = threadPoolConfigEntity.getMaximumPoolSize();
        if (corePoolSize>maximumPoolSize){
            //TODO 增加告警的信息
            AlarmMessageVo alarmMessageVo = new AlarmMessageVo();
            alarmMessageVo.setApplicationName(applicationName);
            alarmMessageVo.setMessage("出现问题");
            Map<String,String> map = new HashMap<>();
            map.put("core.pool.size",String.valueOf(corePoolSize));
            map.put("max.pool.size",String.valueOf(maximumPoolSize));
            alarmMessageVo.setParameters(map);
            alarmService.send(alarmMessageVo);
            logger.error("动态线程池, 变更配置时出错(最大线程数小于核心线程数): {}", threadPoolConfigEntity);
        }
        //变更的时候需要注意，要满足核心线程数小于最大线程数
        if (corePoolSize < threadPoolExecutor.getMaximumPoolSize()) {
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        } else {
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        }
        if (threadPoolExecutor.getQueue() instanceof ResizableCapacityLinkedBlockingQueue) {
            int queueSize = threadPoolConfigEntity.getQueueSize();
            if (threadPoolExecutor.getQueue().size() != queueSize) {
                ((ResizableCapacityLinkedBlockingQueue<Runnable>) threadPoolExecutor.getQueue()).setCapacity(queueSize);
            }
        }
        //重新放入全局管理器，不然会更新失败
        //更新threadPoolConfigEntity
        threadPoolConfigEntity = buildThreadPoolConfigEntity(threadPoolConfigEntity,threadPoolExecutor);
        GlobalThreadPoolManage.updateAndRegist(threadPoolConfigEntity,threadPoolExecutor);
        //TODO 数据上报redis
        registry.updateThreadPoolEntity(threadPoolConfigEntity);
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
        logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
         //TODO 推送变更成功的信息

    }

    private <T extends ThreadPoolExecutor> ThreadPoolConfigEntity buildThreadPoolConfigEntity(ThreadPoolConfigEntity threadPoolConfigEntity,T threadPoolExecutor) {
        String threadPoolName = threadPoolConfigEntity.getThreadPoolName();
        ThreadPoolConfigEntity entity = GlobalThreadPoolManage.getEntityByName(threadPoolName);
        entity.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        entity.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        entity.setActiveCount(threadPoolExecutor.getActiveCount());
        entity.setPoolSize(threadPoolExecutor.getPoolSize());
        entity.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        entity.setQueueSize(threadPoolExecutor.getQueue().size());
        //TODO 后续需要补充，现在先写死
        entity.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        //TODO 队列的长度
        return entity;
    }
}
