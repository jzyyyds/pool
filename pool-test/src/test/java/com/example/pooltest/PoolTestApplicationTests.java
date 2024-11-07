package com.example.pooltest;

import com.example.pool.spring.boot.start.config.service.domain.entity.ThreadPoolConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class PoolTestApplicationTests {

    @Autowired
    private RTopic dynamicThreadPoolRedisTopic;

    @Test
    public void test_dynamicThreadPoolRedisTopic() throws InterruptedException {
        log.info("执行了");
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity("pool-test", "threadPoolExecutor01");
        threadPoolConfigEntity.setMaximumPoolSize(100);
        dynamicThreadPoolRedisTopic.publish(threadPoolConfigEntity);
        new CountDownLatch(1).await();
    }

}
