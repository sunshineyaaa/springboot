package com.sunshine.sunshine.advice;

import com.alibaba.fastjson.JSON;
import com.sunshine.sunshine.dto.ResultDTO;
import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
//注解还是要有的
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    ModelAndView handle(HttpServletRequest request, Throwable ex, Model model, HttpServletResponse response) {
        String contentType = request.getContentType();
        if("application/json".equals(contentType))
        {
            ResultDTO resultDTO;
            //返回json
            if(ex instanceof CustomizeException)
            {
                resultDTO =ResultDTO.errorOf((CustomizeException) ex);
            }else{
                resultDTO= ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
        }
        return null;
        }
        else{//返回错误页面跳转
            if(ex instanceof CustomizeException)
            {
                model.addAttribute("message",ex.getMessage());
            }else{
                model.addAttribute("message",CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }

    }
}
