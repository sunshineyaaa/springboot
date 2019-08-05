package com.sunshine.sunshine.exception;

public interface ICustomizeErrorCode {
    //这里表示的是 我们的异常是有很多的 我们不能在每一次出现要判断的时候都写上相同的代码 需要进行相关的封装
    String getMessage();
    //这里的代码的含义是 这个是我们的父类 里面有一个方法 下面有一个子类 来实现这些方法
    Integer getCode();

}
