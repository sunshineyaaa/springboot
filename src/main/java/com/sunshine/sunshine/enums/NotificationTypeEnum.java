package com.sunshine.sunshine.enums;

import lombok.Data;

public enum NotificationTypeEnum {
//    这里的作用是我们定义了一个枚举类型 我们在数据库里面尽量存放比较少的信息 在枚举里面拿到这些信息的展示
    REPLY_QUESTION(1,"回复了问题"),
    REPLY_COMMENT(2,"回复了评论");
    private int type;
    private String name;
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    NotificationTypeEnum(int type, String name){
        this.name=name;
        this.type=type;
    }
    public static String nameOfType(int type){
       for(NotificationTypeEnum notificationTypeEnum: NotificationTypeEnum.values()){
           if(notificationTypeEnum.getType()==type){
               return notificationTypeEnum.getName();
           }
       }
       return "";
    }
}
