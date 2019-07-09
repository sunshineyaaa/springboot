package com.sunshine.sunshine.controller;

import com.sunshine.sunshine.dto.AccessTokenDTO;
import com.sunshine.sunshine.dto.GithubUser;
import com.sunshine.sunshine.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/callback")
    public  String callback(@RequestParam(name ="code") String code,
                             @RequestParam(name ="state") String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        //这里的id和secret都是我们在完成   https://github.com/settings/applications/1095088
        accessTokenDTO.setClient_id("clientID");
        accessTokenDTO.setClient_secret("clientSecret");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("redirectUri");
        accessTokenDTO.setState(state);
        String accessTolen = githubProvider.getAccessTolen(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessTolen);
        System.out.println(user.getName());
        return "index";
    }
}
