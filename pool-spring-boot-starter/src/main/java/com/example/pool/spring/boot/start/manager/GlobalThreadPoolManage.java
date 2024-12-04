package com.example.pool.spring.boot.start.manager;

import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.registry.IRegistry;
import org.example.executor.DynamicThreadPoolExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class GlobalThreadPoolManage {

    //存放动态线程池包装对象的map，收集线程运行时信息的时候会用到
    private static final Map<String, ThreadPoolConfigEntity> EXECUTOR_MAP = new ConcurrentHashMap();

    private static final Map<String, ThreadPoolExecutor> SIMPLE_MAP = new ConcurrentHashMap();

    private static final Map<String, DynamicThreadPoolExecutor> DYNAMIC_MAP = new ConcurrentHashMap();

    public static void registPool(ThreadPoolConfigEntity entity){
        if (Objects.nonNull(entity)){
            EXECUTOR_MAP.put(entity.getThreadPoolId(),entity);
        }
    }

    public static  Map<String, ThreadPoolConfigEntity> getEntity(){
        return EXECUTOR_MAP;
    }

    public static ThreadPoolConfigEntity getEntityByName(String name){
        return EXECUTOR_MAP.get(name);
    }
    public static void registSimplePool(String name,ThreadPoolExecutor threadPoolExecutor){
        if (Objects.nonNull(threadPoolExecutor)){
            SIMPLE_MAP.put(name,threadPoolExecutor);
        }
    }

    public static void registDynamicPool(String name,DynamicThreadPoolExecutor threadPoolExecutor){
        if (Objects.nonNull(threadPoolExecutor)){
            DYNAMIC_MAP.put(name,threadPoolExecutor);
        }
    }

    public static ThreadPoolExecutor getSimplePool(String name){
        return SIMPLE_MAP.get(name);
    }


    public static DynamicThreadPoolExecutor getDynamicPool(String name){
        return DYNAMIC_MAP.get(name);
    }

    public static <T extends ThreadPoolExecutor> void updateAndRegist(ThreadPoolConfigEntity threadPoolConfigEntity, T threadPoolExecutor) {
        String threadPoolId = threadPoolConfigEntity.getThreadPoolId();
        String threadPoolName = threadPoolConfigEntity.getThreadPoolName();
        if (threadPoolId==null || DYNAMIC_MAP.get(threadPoolId) == null) {
            //说明是普通的线程池
            EXECUTOR_MAP.put(threadPoolName,threadPoolConfigEntity);
            SIMPLE_MAP.put(threadPoolName,threadPoolExecutor);
        }else{
            EXECUTOR_MAP.put(threadPoolId,threadPoolConfigEntity);
            DYNAMIC_MAP.put(threadPoolId,(DynamicThreadPoolExecutor) threadPoolExecutor);
        }

    }

    public static void updateEntity(String name, ThreadPoolConfigEntity threadPoolConfigEntity) {
        ThreadPoolConfigEntity current = EXECUTOR_MAP.get(name);
        if (Objects.nonNull(current)) {
            EXECUTOR_MAP.put(name,threadPoolConfigEntity);
        }
    }
}
