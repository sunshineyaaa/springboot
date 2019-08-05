package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.PaginationDTO;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.provider.service.NotificationService;
import com.sunshine.sunshine.provider.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    /*
    我们想要实现的是 在自己的页面点击以后会跳转出一个自己发布的问题 就像是我们之前的publishcontroller一样
    我们可以在controller里面加载一些数据出来 这里的就是如此
     */
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name="action") String action,
                          Model model,
                          @RequestParam(name="page",defaultValue = "1") Integer page,
                          @RequestParam(name="size",defaultValue = "5")Integer size)
    {

        User user=(User)request.getSession().getAttribute("user");
            //如果没有登录期望跳转到登录页面
        if(user==null)
        {
            return "redirect:/";
        }
        if("questions".equals(action)) {
            model.addAttribute("section","questions");//希望
            model.addAttribute("sectionName","我的提问");
            //避免发现空指针
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination",paginationDTO);
        }else if("replies".equals(action))
        {
            PaginationDTO paginationDTO=notificationService.list(user.getId(),page,size);
         //   Long unreadCount=notificationService.unreadCount(user.getId());
            model.addAttribute("section","replies");//希望
            model.addAttribute("pagination",paginationDTO);
//            model.addAttribute("unreadCount",unreadCount);
            model.addAttribute("sectionName","最新回复");
            //避免发现空指针
        }

        return "profile";
    }
}
