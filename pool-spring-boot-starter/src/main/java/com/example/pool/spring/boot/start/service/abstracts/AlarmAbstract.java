package com.example.pool.spring.boot.start.service.abstracts;

import com.example.pool.spring.boot.start.alarm.AlarmStrategy;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
@Component
public abstract class AlarmAbstract implements AlarmStrategy {
    public abstract String getStrategyName();

    public abstract void sendNotify(AlarmMessageVo alarmMessageVo);

    protected String buildMsg(AlarmMessageVo alarmMessageVo) {
        StringBuilder content = new StringBuilder();
        Map<String, String> parameters = alarmMessageVo.getParameters();

        content.append("【动态线程池告警】").append("\n").append(alarmMessageVo.getMessage()).append("\n");
        parameters.forEach(
                (k, v) -> content
                        .append(" ")
                        .append(k)
                        .append(v)
                        .append("\n")
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        content.append("⏰通知时间: ").append(LocalDateTime.now().format(formatter)).append("\n");
        return content.toString();
    }
}
