package com.example.pool.spring.boot.start.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class UniQueIdUtils {
    private static String UNIQUE_ID;

    public UniQueIdUtils(ApplicationContext applicationContext) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("get host error");
        }
        UNIQUE_ID = applicationName + ":" + hostAddress;
    }

    public static String getId() {
        return UNIQUE_ID;
    }
}
