package org.example.domain;

public enum AlarmEnum {
    EMAIL("email"),
    FEISHU("feishu"),
    DINGDING("dingding");
    private final String key;

    AlarmEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
