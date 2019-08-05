package com.sunshine.sunshine.provider.service;

import com.sunshine.sunshine.dto.CommentDTO;
import com.sunshine.sunshine.enums.CommentTypeEnum;
import com.sunshine.sunshine.enums.NotificationStatusEnum;
import com.sunshine.sunshine.enums.NotificationTypeEnum;
import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.exception.CustomizeException;
import com.sunshine.sunshine.mapper.*;
import com.sunshine.sunshine.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private QuestionMapper questionMapper ;
    @Autowired
    private  CommentMapper commentMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private NotificationMapper notificationMapper;
//    这个函数的作用是用于来为我们的通知使用
    @Transactional
    //这里是事物 要么不做 要么全做
    public  void insert(Comment comment, User commentator) {
        if(comment.getParentId()==null||comment.getParentId()==0)
        {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if(comment.getType()==null || !CommentTypeEnum.isExit(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if(comment.getType()==CommentTypeEnum.COMMENT.getType())
        {//回复评论
            Comment dbComment=commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment==null)
            { throw new CustomizeException(CustomizeErrorCode.COMMENT_NOTE_FOUND); }
//            回复问题 先验证我们的问题是否存在
            Question question= questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null)
            { throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND); }
            commentMapper.insert(comment);
            //获取我们的子评论的个数
            Comment parentCount=new Comment();
          parentCount.setId(comment.getParentId());
            parentCount.setCommentCount(1);
            commentExtMapper.incCommentCount(parentCount);

//            相关评论的通知信息 看来还是转换为默认的idea快捷键好用 创建通知
            createNotified(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT,question.getId());
        }
        else
            {
//                回复问题
                Question question= questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null)
            { throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND); }
                comment.setCommentCount(0);
                commentMapper.insert(comment);
                question.setCommentCount(1);
                questionExtMapper.incCommentCount(question);
            //创建通知
            createNotified(comment,question.getCreator(),commentator.getName(),question.getTitle(), NotificationTypeEnum.REPLY_QUESTION,question.getId());
            //服务层里面就是我们的 业务代码 为前端的信息作出计算
           }
    }

    private void createNotified(Comment comment, Long receiver, String notifierName, String altTitle, NotificationTypeEnum notificationType,Long outerId) {
        if(receiver==comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());//创建时间
        notification.setType(notificationType.getType());//我们这里是关于评论的部分
        notification.setOuterid(outerId);//要获取到我们评论的是谁的信息
        notification.setNotifier(comment.getCommentator());//评论人的相关信息这里好像不是很明白notifier是干什么用的
        notification.setStatus(NotificationStatusEnum.NUREAD.getStatus());
        notification.setReceiver(receiver);//是这条评论的对象的信息
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(altTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listBytargetId(long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
//ctrl+alt+ v 是抽取一个变量出来 用来先写上一个new 然后抽取一个变量出来 m是抽取一个方法出来 p是参数
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> commentList = commentMapper.selectByExample(commentExample);
        if(commentList.size()==0){
            return new ArrayList<>();
        }
        //p42 完全不懂这段代码的含义
        //获取去重的评论人
        Set<Long> commentators = commentList.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList();
        userIds.addAll(commentators);
        //获取评论人 并转化为map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //转换comment为commentdto
        List<CommentDTO> commentDTOS = commentList.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
