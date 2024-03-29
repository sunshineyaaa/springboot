package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.AccessTokenDTO;
import com.sunshine.sunshine.dto.GithubUser;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.provider.GithubProvider;
import com.sunshine.sunshine.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Controller
public class AuthorizeController {
/*
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor());
        registry.addInterceptor(new ThemeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");
    }
}
 */
    @Autowired
    private GithubProvider githubProvider;
//    @Value("${github.client.id}")
//    private  String clientID;
//    @Value("${github.client.secret}")
//    private  String clientSecret;
//    @Value("${github.redirect.uri}")
//    private  String redirectUri;

@Autowired
private UserService userService;
  @GetMapping("/callback")
    //HttpServletRequest 我们的 session是在这个函数里面拿到的 当我们完成 这个 以后 就可以把上下文中的request来给我们使用
    public  String callback(@RequestParam(name ="code") String code,
                            @RequestParam(name ="state") String state,
                            HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        //这里的id和secret都是我们在完成   https://github.com/settings/applications/1095088
        accessTokenDTO.setClient_id("b7900b01e0890476acbf");
        accessTokenDTO.setClient_secret("a9b60468f185e730064539e2c9d6a185669c3355");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8886/callback");
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser!=null && githubUser.getId() !=null)
        {
            /*要是user不是空 我们就做 cookie和session
            我们重新来梳理一下 我们在完成登陆以后，会做登录  然后获取一个用户信息 生成一个token 在得到token以后
            把他存放到user对象里面 再存取到数据库 并且把token放到cookie
             */
            User user =new User();
           String token= UUID.randomUUID().toString();
           user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            System.out.println(user.getName());
            userService.createUpdate(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
//这里相当于我们在后端已经为我们创建了一个用户 但是 前端里面 并还没有相关联的信息
        }
        else
        {
            return "redirect:/";
        }
    }
    @GetMapping("/logout")//这里表示返回的地址
  public String logout(HttpServletRequest request,
                       HttpServletResponse response){
      request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        //这里的退出登录 就是删除cookie
      return "redirect:/";

  }

    
}
