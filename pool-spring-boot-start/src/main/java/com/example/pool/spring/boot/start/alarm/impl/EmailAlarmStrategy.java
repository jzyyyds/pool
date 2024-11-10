package com.example.pool.spring.boot.start.alarm.impl;

import com.example.pool.spring.boot.start.alarm.AlarmStrategy;
import com.example.pool.spring.boot.start.config.DynamicThreadPoolNotifyAutoProperties;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import com.example.pool.spring.boot.start.service.abstracts.AlarmAbstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Component
public class EmailAlarmStrategy extends AlarmAbstract {
    private final DynamicThreadPoolNotifyAutoProperties dynamicThreadPoolNotifyAutoProperties;

    public EmailAlarmStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.dynamicThreadPoolNotifyAutoProperties = notifyProperties;
    }
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendNotify(AlarmMessageVo alarmMessageVo) {
        DynamicThreadPoolNotifyAutoProperties.Email email = dynamicThreadPoolNotifyAutoProperties.getEmail();
        String from = email.getFrom();
        String to = email.getTo();
        // 创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        // 设置收件人
        message.setTo(to);
        // 设置邮件主题
        message.setSubject("动态线程池告警");
        // 设置邮件内容
        message.setText(buildMsg(alarmMessageVo));
        // 发送邮件
        mailSender.send(message);
    }



    @Override
    public String getStrategyName() {
        return "email";
    }
}
