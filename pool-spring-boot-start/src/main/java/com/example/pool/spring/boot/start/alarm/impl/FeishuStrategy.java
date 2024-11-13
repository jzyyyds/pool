package com.example.pool.spring.boot.start.alarm.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.example.pool.spring.boot.start.config.DynamicThreadPoolNotifyAutoProperties;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import com.example.pool.spring.boot.start.service.abstracts.AlarmAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FeishuStrategy extends AlarmAbstract {
    private final Logger logger = LoggerFactory.getLogger(FeishuStrategy.class);

    private final DynamicThreadPoolNotifyAutoProperties notifyProperties;

    public FeishuStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Override
    public void sendNotify(AlarmMessageVo notifyMsg) {
        //获取webhook的地址
        String webhook = notifyProperties.getWebhook().getFeishu();
        String context = buildMsg(notifyMsg);
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> text = new HashMap<>();
        params.put("msg_type", "text");
        text.put("text", "动态线程池告警通知"+context);
        params.put("content", text);
        String result = HttpRequest.post(webhook).body(JSON.toJSONString(params), "application/json;charset=UTF-8").execute().body();
        logger.info("飞书的返回值"+result);
    }

    @Override
    public String getStrategyName() {
        return "feishu";
    }
}
