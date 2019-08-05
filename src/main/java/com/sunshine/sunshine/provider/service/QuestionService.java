package com.sunshine.sunshine.provider.service;

import com.sunshine.sunshine.dto.PaginationDTO;
import com.sunshine.sunshine.dto.QuestionDTO;
import com.sunshine.sunshine.dto.QuestionQueryDTO;
import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.exception.CustomizeException;
import com.sunshine.sunshine.mapper.QuestionExtMapper;
import com.sunshine.sunshine.mapper.QuestionMapper;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.Question;
import com.sunshine.sunshine.model.QuestionExample;
import com.sunshine.sunshine.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

@Autowired
private QuestionMapper questionMapper;

@Autowired
private UserMapper userMapper;

@Autowired
private QuestionExtMapper questionExtMapper;
    public PaginationDTO list(String search,Integer page, Integer size)
    {
//        search 来实现我们的搜索功能
        if(StringUtils.isNotBlank(search)){
//            我们还是和之前的一个标签的带入一样 采用正则表达式的方式 来搜索
            String [] tags= StringUtils.split(search," ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }


        PaginationDTO paginationDTO =new PaginationDTO();
        Integer totalPage;

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount= questionExtMapper.countBySearch(questionQueryDTO);

        if(totalCount%size==0)
        {
            totalPage=totalCount/size;
        }else {
            totalPage=totalCount/size+1;
        }
        if(page<1){page=1;}
        if(page>totalPage){page=totalPage;}
        paginationDTO.setPagination(totalPage,page);
        Integer offest=size*(page-1);
        //这里是我们的评论来倒序排列+
        QuestionExample questionExample=new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setPage(offest);
        questionQueryDTO.setSize(size);
       List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for(Question question:questions)
        {
            User user=userMapper.selectByPrimaryKey(question.getCreator());
           QuestionDTO questionDTO= new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            //这个类的作用就是 快速把前一个对象上面的属性拷贝到后面一个对象上面--
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
            return paginationDTO;
    }
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO =new PaginationDTO();
        Integer totalPage;
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount=(int)questionMapper.countByExample(questionExample);
        if(totalCount%size==0)
        {
            totalPage=totalCount/size;
        }else {
            totalPage=totalCount/size+1;
        }
        if(page<1){page=1;}
        if(page>totalPage)
        {page=totalPage;}
        paginationDTO.setPagination(totalPage,page);
        Integer offest=size*(page-1);
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offest, size));
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for(Question question:questions)
        {
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO= new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            //这个类的作用就是 快速把前一个对象上面的属性拷贝到后面一个对象上面--
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }
    public QuestionDTO getById(Long id) {
         Question question=questionMapper.selectByPrimaryKey(id);
         if(question==null)
         {
             throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
         }
         QuestionDTO questionDTO =new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user=userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }
    public void createUpdate(Question question) {
        if(question.getId()==null){
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else{
            //更新
            question.setGmtModified(question.getGmtCreate());
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            questionMapper.updateByExampleSelective(updateQuestion,example);
            int update=questionMapper.updateByExampleSelective(updateQuestion,example);
            if(update!=1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }
    public void incView(Long id) {
        /*用来表示我们的阅读数的  我们的服务层页面做的主要功能就是我们有一个函数 在传入一个id以后确认是相同的一个问题
        然后 调用我们的questionExtm 进入mapper层 里面也是只有一个函数调用 回到我们的相同名称的xml项目进行解析
         */
        Question question = new Question();
        questionExtMapper.incView(question);
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        String [] tags= StringUtils.split(queryDTO.getTag(),",");
        String regepTag= Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question=new Question();
        question.setId(queryDTO.getId());
        question.setTag(queryDTO.getTag());

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOs=questions.stream().map(q ->{
            QuestionDTO questionDTO=new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOs;

    }
}

