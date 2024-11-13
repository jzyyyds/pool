package com.example.pool.spring.boot.start.alarm.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.example.pool.spring.boot.start.config.DynamicThreadPoolNotifyAutoProperties;
import com.example.pool.spring.boot.start.domain.vo.AlarmMessageVo;
import com.example.pool.spring.boot.start.service.abstracts.AlarmAbstract;
import com.taobao.api.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class DingDingStrategy extends AlarmAbstract {
    private final Logger logger = LoggerFactory.getLogger(DingDingStrategy.class);

    private final DynamicThreadPoolNotifyAutoProperties notifyProperties;

    public DingDingStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Override
    public String getStrategyName() {
        return "dingding";
    }

    @Override
    public void sendNotify(AlarmMessageVo alarmMessageVo) {
        try {
            String token = notifyProperties.getWebhook().getDingding();
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send");
            OapiRobotSendRequest req = new OapiRobotSendRequest();

            //定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(buildMsg(alarmMessageVo));

            //定义 @ 对象
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(true);

            //设置消息类型
            req.setMsgtype("text");
            req.setText(text);
            req.setAt(at);
            OapiRobotSendResponse rsp = null;
            rsp = client.execute(req, token);
            if (rsp.isSuccess()) {
                return;
            }
            throw new ApiException(rsp.getErrcode().toString(), rsp.getErrmsg());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
