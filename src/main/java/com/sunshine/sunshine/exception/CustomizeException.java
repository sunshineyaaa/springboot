package com.sunshine.sunshine.exception;

public class CustomizeException extends RuntimeException{
    //继承这个异常 目的在于要是我们不继承这个异常 之前我们发现
    private String message;
    private Integer code;
    public CustomizeException(ICustomizeErrorCode errorCode)
    {
        this.code=errorCode.getCode();
        this.message=errorCode.getMessage();
    }
    @Override
    public String getMessage(){
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
