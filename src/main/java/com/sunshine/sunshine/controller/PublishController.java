package com.sunshine.sunshine.controller;


import com.sunshine.sunshine.cache.TagCache;
import com.sunshine.sunshine.dto.QuestionDTO;
import com.sunshine.sunshine.mapper.QuestionMapper;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.Question;
//import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.provider.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    /*
    gest 渲染
    post 执行请求
     */
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;
@GetMapping("/publish/{id}")
    public String edit(@PathVariable(name="id")Long id,
                       Model model)
    {//在这里我们想要实现的是编辑的功能 作用是 我们点击页面可以传递过来一个id 获取到当期的question 然后写到页面上面

        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());//我们在publish 无法断定什么是惟一的 若是我是通过编辑路径进入到这个访问我们的publish
        //希望页面能够告诉我 这个是不同于发布时候的编辑 我们发一个id


        return  "publish";
    }
    @GetMapping("/publish")
    public String publish( Model model)
    {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }
    @PostMapping("/publish")
    public String  doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value ="description",required = false) String description,
            @RequestParam(value ="tag",required = false) String tag,
            @RequestParam(value ="id",required = false) Long id,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());

        if(title==null ||title=="") {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if(description==null ||description=="") {
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }
        if(tag==null ||tag=="") {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }
        String invalid=TagCache.filterInvalid(tag);
        if(StringUtils.isNoneBlank(invalid)){
            model.addAttribute("error", "输入非法标签"+invalid);
            return "publish";
        }
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        User user=(User)request.getSession().getAttribute("user");
        if(null == user)
                    {
        model.addAttribute("error","用户未登陆");
            return  "publish";
        }
        Question question= new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createUpdate(question);
        return "redirect:/";
    }
}
