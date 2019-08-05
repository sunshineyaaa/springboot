package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.FileDTO;
import com.sunshine.sunshine.provider.UCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {
    @Autowired
    private UCloudProvider ucloudProvider;


    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request)
    {
        MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest) request;
        MultipartFile file= multipartHttpServletRequest.getFile("editormd-image-file");
        //这里的editormd 是我们需要在本地上传的时候 给定一个名字 我们去前端页面里面找到这个样式的地址所在地 点击找到本地上传即可
        try {
           String fileName= ucloudProvider.upload(file.getInputStream(),file.getContentType(),file.getOriginalFilename());
            FileDTO fileDTO=new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileName);
            return fileDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileDTO fileDTO=new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/dong.jpg");
        return fileDTO;
    }
}
