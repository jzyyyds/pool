package org.example.manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.plugin.impl.ExecuteAwarePluginImpl;
import org.example.plugin.impl.RejectedAwarePluginImpl;
import org.example.plugin.impl.ShutdownAwarePluginImpl;

@AllArgsConstructor
@NoArgsConstructor
public class DefaultThreadPoolPluginRegist implements ThreadPoolPluginRegist{

    private long executeTimeOut;

    private long awaitTerminationMillis;
    @Override
    public void doRegister(ThreadPoolPluginSupport support) {
        support.register(new ExecuteAwarePluginImpl());
        support.register(new RejectedAwarePluginImpl());
        support.register(new ShutdownAwarePluginImpl());
    }
}
