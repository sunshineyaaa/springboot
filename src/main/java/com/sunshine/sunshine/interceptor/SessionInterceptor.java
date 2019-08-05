package com.sunshine.sunshine.interceptor;

import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.model.UserExample;
import com.sunshine.sunshine.provider.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Service
public class SessionInterceptor implements HandlerInterceptor {
/*
https://docs.spring.io/spring/docs/5.0.3.RELEASE/spring-framework-reference/web.html#mvc-handlermapping-interceptor(文档地)
作用对地址的请求进行拦截
 */
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies=request.getCookies();
        //System.out.println(cookies.length);
        if(cookies!=null && cookies.length!=0)
            for(Cookie cookie:cookies)
            {
                if(cookie.getName().equals("token"))
                {
                    String token=cookie.getValue();
                    UserExample userExample=new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if(users.size()!=0)
                    {
                        request.getSession().setAttribute("user",users.get(0));
                        Long unreadCount=notificationService.unreadCount(users.get(0).getId());
                        request.getSession().setAttribute("unreadCount",unreadCount);
                    }
                    break;
                }
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

