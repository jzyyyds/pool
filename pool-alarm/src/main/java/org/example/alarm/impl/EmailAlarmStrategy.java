package org.example.alarm.impl;


import org.example.config.ApplicationContextHolder;
import org.example.config.DynamicThreadPoolNotifyAutoProperties;
import org.example.domain.AlarmEnum;
import org.example.domain.vo.AlarmMessageVo;
import org.example.service.abstracts.AlarmAbstract;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailAlarmStrategy extends AlarmAbstract {
    private final DynamicThreadPoolNotifyAutoProperties dynamicThreadPoolNotifyAutoProperties;

    public EmailAlarmStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.dynamicThreadPoolNotifyAutoProperties = notifyProperties;
    }

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
        JavaMailSender mailSender = ApplicationContextHolder.getBean(JavaMailSender.class);
        // 发送邮件
        mailSender.send(message);
    }



    @Override
    public String getStrategyName() {
        return AlarmEnum.EMAIL.getKey();
    }
}
