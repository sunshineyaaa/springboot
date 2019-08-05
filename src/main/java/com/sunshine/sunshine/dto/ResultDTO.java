package com.sunshine.sunshine.dto;

import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.exception.CustomizeException;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private  Integer code;//告诉前端用什么来显示
    private String message;//用来提示
    private T data;
    public static ResultDTO errorOf(Integer code,String message)
    {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return  resultDTO;

    }
    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        return errorOf(errorCode.getCode(),errorCode.getMessage());
    }
    public static ResultDTO okOf(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return  resultDTO;
    }
    public static ResultDTO errorOf(CustomizeException ex) {
        return errorOf(ex.getCode(),ex.getMessage());
    }
    public static <T>ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return  resultDTO;
    }

}
