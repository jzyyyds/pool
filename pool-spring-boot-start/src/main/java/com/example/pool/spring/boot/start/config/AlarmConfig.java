package com.example.pool.spring.boot.start.config;


import com.example.pool.spring.boot.start.alarm.AlarmStrategy;
import com.example.pool.spring.boot.start.service.IAlarmService;
import com.example.pool.spring.boot.start.service.abstracts.AlarmAbstract;
import com.example.pool.spring.boot.start.service.impl.AlarmServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "dynamic-thread-pool.alarm",name = "enable",havingValue = "true")
@EnableConfigurationProperties(DynamicThreadPoolNotifyAutoProperties.class)
@ComponentScan(basePackages = "com.example.pool.spring.boot.start.alarm")
public class AlarmConfig {
    private final Logger logger = LoggerFactory.getLogger(AlarmConfig.class);
    @Value("${dynamic-thread-pool.alarm.enable}")
    private boolean enable;

    @Bean(name = "alarmService")
    public AlarmServiceImpl getAlarmStrategy(DynamicThreadPoolNotifyAutoProperties dynamicThreadPoolNotifyAutoProperties, List<AlarmStrategy> alarmStrategyList){
        if (enable){
            logger.info("开始初始化告警配置");
            System.out.println(1111);
            return new AlarmServiceImpl(dynamicThreadPoolNotifyAutoProperties,alarmStrategyList);
        }
        return null;
    }
}
