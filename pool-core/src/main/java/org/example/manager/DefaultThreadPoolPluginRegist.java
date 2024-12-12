package org.example.manager;

import javafx.application.Application;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.executor.DynamicThreadPoolExecutor;
import org.example.plugin.impl.ExecuteAwarePluginImpl;
import org.example.plugin.impl.RejectedAwarePluginImpl;
import org.example.plugin.impl.ShutdownAwarePluginImpl;
import org.example.service.IAlarmService;
import org.example.service.impl.AlarmServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Configuration
@Component
public class DefaultThreadPoolPluginRegist implements ThreadPoolPluginRegist {

    private long executeTimeOut;
    private long awaitTerminationMillis;

    @Override
    public void doRegister(ThreadPoolPluginSupport support) {
        support.register(new ExecuteAwarePluginImpl(support.getThreadPoolId(),executeTimeOut));
        support.register(new RejectedAwarePluginImpl());
        support.register(new ShutdownAwarePluginImpl());
    }
}
