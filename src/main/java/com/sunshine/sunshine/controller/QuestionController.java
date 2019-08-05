package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.CommentDTO;
import com.sunshine.sunshine.dto.QuestionDTO;
import com.sunshine.sunshine.enums.CommentTypeEnum;
import com.sunshine.sunshine.provider.service.CommentService;
import com.sunshine.sunshine.provider.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    /*
    我们想要实现的是 在我们点击我们的自己的信息以后 会跳转到我们的编辑页面 实现修改的功能
    然后第一步就是要编辑一个controller，来实现接受的作用 我们的大概布局和之前的index大概相同
    先添加一个controller 注解，然后 写一个方法 返回 “question” 然后 请求 getMapping 表示接受请求 拿到参数
     */
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name="id") Long id,Model model)
    {
      QuestionDTO questionDTO=questionService.getById(id);
//      查找标签
List<QuestionDTO> relatedQuestions =questionService.selectRelated(questionDTO);
      //累加阅读数
        List<CommentDTO> comments=commentService.listBytargetId(id, CommentTypeEnum.QUESTION);
      questionService.incView(id);
      model.addAttribute("question",questionDTO);
      model.addAttribute("comments",comments);
      model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
