package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.CommentCreateDTO;
import com.sunshine.sunshine.dto.CommentDTO;
import com.sunshine.sunshine.dto.ResultDTO;
import com.sunshine.sunshine.enums.CommentTypeEnum;
import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.mapper.CommentMapper;
import com.sunshine.sunshine.model.Comment;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.provider.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {


    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentService commentService;
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {
        User attribute = (User) request.getSession().getAttribute("user");
        if(attribute==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);//我们在想要进行评论之前也是要先要进行
            //判断 下面才是相关的服务层需要完成的
        }
        if(commentCreateDTO ==null || StringUtils.isBlank(commentCreateDTO.getContent())){
         return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(attribute.getId());
        comment.setLikeCount((long) 0);//这里我们的是long 要转换一下
        commentService.insert(comment,attribute);
        //以json为传输的服务端的api的接口 并且把他插入到数据库里面去
        return ResultDTO.okOf();
    }
    //comment层实现的就是我们的评论相关联的功能 下面 开始写二级评论相关的部分
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO <List<CommentDTO>>comments(@PathVariable(name="id")Long id){
        List<CommentDTO> commentDTOS = commentService.listBytargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
