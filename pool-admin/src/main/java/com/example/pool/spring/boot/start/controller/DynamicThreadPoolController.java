package com.example.pool.spring.boot.start.controller;


import com.alibaba.fastjson.JSON;
import com.example.pool.spring.boot.start.domain.entity.ThreadPoolConfigEntity;
import com.example.pool.spring.boot.start.enums.Code;
import com.example.pool.spring.boot.start.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/dynamic/thread/pool/")
public class DynamicThreadPoolController {

    @Resource
    public RedissonClient redissonClient;


    @RequestMapping(value = "query_thread_pool_list", method = RequestMethod.GET)
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            Stream<String> keysStreamByPattern = redissonClient.getKeys().getKeysStreamByPattern("dynamic_heart:*");
            RList<ThreadPoolConfigEntity> cacheList = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
            List<String> applicationList = keysStreamByPattern.collect(Collectors.toList());
            List<ThreadPoolConfigEntity> result = cacheList.stream().filter(x -> applicationList.contains("dynamic_heart:"+x.getAppName())).collect(Collectors.toList());
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Code.SUCCESS.getCode())
                    .info(Code.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (Exception e) {
            log.error("查询线程池数据异常", e);
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Code.UN_ERROR.getCode())
                    .info(Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_thread_pool_config", method = RequestMethod.GET)
    public Response<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        try {
            String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Code.SUCCESS.getCode())
                    .info(Code.SUCCESS.getInfo())
                    .data(threadPoolConfigEntity)
                    .build();
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Code.UN_ERROR.getCode())
                    .info(Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update_thread_pool_config", method = RequestMethod.POST)
    public Response<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity request) {
        try {
            log.info("修改线程池配置开始 {} {} {}", request.getAppName(), request.getThreadPoolName(), JSON.toJSONString(request));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + request.getAppName());
            topic.publish(request);
            log.info("发送变更消息完成", request.getAppName(), request.getThreadPoolName());
            return Response.<Boolean>builder()
                    .code(Code.SUCCESS.getCode())
                    .info(Code.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("修改线程池配置异常 {}", JSON.toJSONString(request), e);
            return Response.<Boolean>builder()
                    .code(Code.UN_ERROR.getCode())
                    .info(Code.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

}
