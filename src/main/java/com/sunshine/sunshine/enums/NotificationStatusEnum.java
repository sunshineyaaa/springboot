package com.sunshine.sunshine.enums;

public enum NotificationStatusEnum {
    NUREAD(0),READ(1)
    ;
    private int status;

    public int getStatus() {
        return status;
    }
    NotificationStatusEnum(int status){
        this.status=status;
    }
}
