package com.example.pool.spring.boot.start.registry.redis;

import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.domain.enums.RegistryEnumVO;
import com.example.pool.spring.boot.start.registry.IRegistry;
import com.example.pool.spring.boot.start.utils.UniQueIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities) {
        if (Objects.isNull(threadPoolEntities) && threadPoolEntities.size()==0){
            return;
        }
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        //此时需要先进行去重的操作
        if (list.isEmpty()){
            list.addAll(threadPoolEntities);
            return;
        }
        RLock lock = redissonClient.getLock(RegistryEnumVO.REPORT_THREAD_POOL_CONFIG_LIST_REDIS_LOCK_KEY.getKey());
        try {
            boolean hasLock = lock.tryLock(3000, 3000, TimeUnit.MILLISECONDS);
            if(hasLock){
                reportPoolInfomation(threadPoolEntities,list);
            }
        } catch (InterruptedException e) {
            //TODO 添加告警机制
            log.error("动态线程池, 上报线程池列表时出现错误: {}", e.toString());
        }finally {
            lock.unlock();
        }
    }

    /**
     * 有三种情况，
     * 1. 缓存中没有，但是List里面有
     * 2. 缓存中有，List里面也有，但是数据不一样
     * 3. 缓存中有，但是List里面没有
     * @param threadPoolEntities
     * @param list
     */

    private void reportPoolInfomation(List<ThreadPoolConfigEntity> threadPoolEntities, RList<ThreadPoolConfigEntity> list) {
        //获取当前应用的名称
        String applicationName = UniQueIdUtils.getId();
        List<ThreadPoolConfigEntity> resultList = list.stream().filter(x -> x.getAppName().equals(applicationName)).collect(Collectors.toList());
        //求出交集，就是核心线程和最大核心线程数都是相等的
        List<ThreadPoolConfigEntity> sameList = threadPoolEntities.stream().filter(pool -> {
            Optional<ThreadPoolConfigEntity> threadPoolConfigEntity = resultList.stream().filter(x -> x.getThreadPoolName().equals(pool.getThreadPoolName())).findFirst();
            if (threadPoolConfigEntity.isPresent()) {
                ThreadPoolConfigEntity entity = threadPoolConfigEntity.get();
                //如果存在的话，此时要判断是否相等
                if (Objects.equals(pool,entity)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        //移除交集里面的数据
        threadPoolEntities.removeAll(sameList);
        //redis里面的数据也是一样，移除不在交集的数据
        resultList.stream().forEach(x->{
            Optional<ThreadPoolConfigEntity> first = sameList.stream().filter(y -> {
                if (Objects.equals(x,y)) {
                    return true;
                }
                return false;
            }).findFirst();
            if(!first.isPresent()){
                //说明存在一个不是交集的，此时需要删除
                list.remove(x);
            }
        });
        list.addAll(threadPoolEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getAppName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity);
        bucket.expire(Duration.ofDays(30));
    }

    @Override
    public void updateThreadPoolEntity(ThreadPoolConfigEntity threadPoolConfigEntity) {
        RLock lock = redissonClient.getLock(RegistryEnumVO.REPORT_THREAD_POOL_CONFIG_LIST_REDIS_LOCK_KEY.getKey());
        try {
            boolean hasLock = lock.tryLock(3000, 3000, TimeUnit.MILLISECONDS);
            RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
            //此时需要先进行去重的操作
            if (list.isEmpty()) {
                list.add(threadPoolConfigEntity);
                return;
            } else {
                Optional<ThreadPoolConfigEntity> result = list.stream().
                        filter(x -> x.isDynamic() == threadPoolConfigEntity.isDynamic() && x.getThreadPoolName().equals(threadPoolConfigEntity.getThreadPoolName())).findFirst();
                if (Objects.isNull(result)) {
                    list.add(threadPoolConfigEntity);
                } else {
                    list.remove(result.get());
                    list.add(threadPoolConfigEntity);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }


    @Override
    public void reportPool(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (Objects.isNull(threadPoolConfigEntity)) {
            return;
        }
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        //此时需要先进行去重的操作
        if (list.isEmpty()){
            list.add(threadPoolConfigEntity);
            return;
        }
        RLock lock = redissonClient.getLock(RegistryEnumVO.REPORT_THREAD_POOL_CONFIG_LIST_REDIS_LOCK_KEY.getKey());
        try {
            boolean hasLock = lock.tryLock(3000, 3000, TimeUnit.MILLISECONDS);
            if(hasLock){
                reportPoolMessage(threadPoolConfigEntity,list);
            }
        } catch (InterruptedException e) {
            //TODO 添加告警机制
            log.error("动态线程池, 上报出现错误: {}", e.toString());
        }finally {
            lock.unlock();
        }
    }

    private void reportPoolMessage(ThreadPoolConfigEntity threadPoolConfigEntity, RList<ThreadPoolConfigEntity> list) {

    }
}
