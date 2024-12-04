package org.example.enums;

public enum QueueEnums {
    LINK_BLOCK_QUEUE("LinkedBlockingQueue"),
    RESIZABLE_CAPACITY_LINK_BLOCK_QUEUE("ResizableCapacityLinkedBlockingQueue");



    private String key;

    QueueEnums(String key) {
        this.key = key;
    }



}
