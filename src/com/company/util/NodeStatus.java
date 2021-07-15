package com.company.util;

public enum NodeStatus {
    SUSPECT(0),
    INFECTED(1),
    RECOVERED(2);
    private int value;
    NodeStatus(int val) {
        this.value=val;
    }
}
