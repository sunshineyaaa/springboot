package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.AccessTokenDTO;
import com.sunshine.sunshine.dto.GithubUser;
import com.sunshine.sunshine.mapper.UserMapper;
import com.sunshine.sunshine.model.User;
import com.sunshine.sunshine.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client_id}")
    private  String clientID;
    @Value("${github.client_secret}")
    private  String clientSecret;
    @Value("${github.redirect.uri}")
    private  String redirectUri;
@Autowired
private UserMapper userMapper;
  @GetMapping("/callback")
    //HttpServletRequest 我们的 session是在这个函数里面拿到的 当我们完成 这个 以后 就可以把上下文中的request来给我们使用
    public  String callback(@RequestParam(name ="code") String code,
                            @RequestParam(name ="state") String state,
                           HttpServletRequest request) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        //这里的id和secret都是我们在完成   https://github.com/settings/applications/1095088
        accessTokenDTO.setClient_id("clientID");
        accessTokenDTO.setClient_secret("clientSecret");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("redirectUri");
        accessTokenDTO.setState(state);
        String accessTolen = githubProvider.getAccessTolen(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessTolen);
        if(githubUser!=null)
        {
            //要是user不是空 我们就做 cookie和session
            User user =new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            request.getSession().setAttribute("githubUser",githubUser);
            return "redirect:/";
//这里相当于我们在后端已经为我们创建了一个用户 但是 前端里面 并还没有相关联的信息
        }
        else
        {
            return "redirect:/";
        }
    }
}
