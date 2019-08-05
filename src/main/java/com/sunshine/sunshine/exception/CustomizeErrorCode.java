package com.sunshine.sunshine.exception;

public enum CustomizeErrorCode implements  ICustomizeErrorCode {
    //可能后面的就是会出现业务爆炸 我们后面还会有很多的业务 也会出现很多的异常 我们在这里使用接口的形式
    QUESTION_NOT_FOUND(2001,"你找的页面好像丢失了，请缓一缓再来试一试"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登陆，请登陆以后重试"),
    SYS_ERROR(2004,"服务冒烟了，建议您待会重试55555"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOTE_FOUND(2006,"你回复的评论不存在，要不换一个试一试"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION(2008,"现在是读信息"),
    NOTIFICATION_NOT_FOUND(2008,"你要找的消息不翼而飞了"),
    FILE_UPLOAD_FAIL(2009,"图片上传失败");
    private Integer code;
    private  String message;
    @Override
    public String getMessage(){
        return message;
    }
    @Override
    public Integer getCode()
    {
        return code;
    }
    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
