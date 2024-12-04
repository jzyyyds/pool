package org.example.enums;

public enum PluginNameEnums {
    EXECTE("thread-pool-executor-execute-plugin"),
    SHUTDOWN("thread-pool-executor-shutdown-plugin"),
    REJECTED("thread-pool-executor-rejected-plugin");
    private String key;

    PluginNameEnums(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
