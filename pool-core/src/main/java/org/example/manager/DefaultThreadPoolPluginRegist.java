package org.example.manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.plugin.impl.ExecuteAwarePluginImpl;
import org.example.plugin.impl.RejectedAwarePluginImpl;
import org.example.plugin.impl.ShutdownAwarePluginImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Configuration
@Component
public class DefaultThreadPoolPluginRegist implements ThreadPoolPluginRegist {

    private long executeTimeOut;
    private long awaitTerminationMillis;
    private boolean enable;

    @Override
    public void doRegister(ThreadPoolPluginSupport support) {
        support.register(new ExecuteAwarePluginImpl(support.getThreadPoolId(),executeTimeOut,enable));
        support.register(new RejectedAwarePluginImpl(support.getThreadPoolId(),enable));
        support.register(new ShutdownAwarePluginImpl());
    }
}
