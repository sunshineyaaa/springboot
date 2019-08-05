package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.PaginationDTO;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.provider.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import  org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller

public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                              @RequestParam(name="page",defaultValue = "1") Integer page,
                              @RequestParam(name="size",defaultValue = "2")Integer size,
//        这里来实现搜索功能
                              @RequestParam(name="search",required = false)String search)
    {
        PaginationDTO pagination= questionService.list(search,page,size);
        model.addAttribute("pagination",pagination);
        return "index";
    }

}
