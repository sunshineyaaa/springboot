package com.sunshine.sunshine.mapper;

import com.sunshine.sunshine.dto.QuestionQueryDTO;
import com.sunshine.sunshine.model.Question;

import java.util.List;

public interface QuestionExtMapper {
     int incView(Question record);
     int incCommentCount(Question record);
     //实现的是 我们

//     这里就是我们在questionExtMapper.xml 的引用
     List<Question> selectRelated (Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);

//     然后进去quesitoncoller里面加以使用
}
