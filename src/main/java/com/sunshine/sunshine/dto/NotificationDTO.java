package com.sunshine.sunshine.dto;

import com.sunshine.sunshine.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerid;
//    页面需要的是地址和名称  就是外部的名字 页面 还有一个文案 是回复了问题 还是回复了评论
    private String typeName;
    private Integer type;
}
