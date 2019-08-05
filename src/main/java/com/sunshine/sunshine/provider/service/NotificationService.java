package com.sunshine.sunshine.provider.service;

import com.sunshine.sunshine.dto.NotificationDTO;
import com.sunshine.sunshine.dto.PaginationDTO;
import com.sunshine.sunshine.dto.QuestionDTO;
import com.sunshine.sunshine.enums.NotificationStatusEnum;
import com.sunshine.sunshine.enums.NotificationTypeEnum;
import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.exception.CustomizeException;
import com.sunshine.sunshine.mapper.NotificationMapper;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO =new PaginationDTO<>();
        Integer totalPage;
      NotificationExample notificationExample=new NotificationExample();
        notificationExample
                .createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount=(int)notificationMapper.countByExample(notificationExample);
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
        NotificationExample example=new NotificationExample();
        example
                .createCriteria()
                .andReceiverEqualTo(userId);
//        这句话的意思是我们来把我们的未读通知的信息倒序排放
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offest, size));
//        拿到所有的notified 找到要通知的人
        if(notifications.size()==0){
            return paginationDTO;
        }
//        Set<Long> disUserIds = notifications.stream().map(notify -> notify.getNotifier()).collect(Collectors.toSet());
//        List<Long> userIds = new ArrayList<>(disUserIds);
//        UserExample userExample = new UserExample();
//        userExample.createCriteria().andIdIn(userIds);
//        List<User> users = userMapper.selectByExample(userExample);
//        //业务代码看不懂系列
//        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));
        List<NotificationDTO> notificationDTOS=new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO=new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);

        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long Userid) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample
                .createCriteria()
                .andReceiverEqualTo(Userid)
        .andStatusEqualTo(NotificationStatusEnum.NUREAD.getStatus());
        return  notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
if(notification==null){
    throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
}
if(!Objects.equals(notification.getReceiver(), user.getId())){
    throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION);
}
notification.setStatus(NotificationStatusEnum.READ.getStatus());
notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO=new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
