package com.sunshine.sunshine.provider;

import com.alibaba.fastjson.JSON;
import com.sunshine.sunshine.dto.AccessTokenDTO;
import com.sunshine.sunshine.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
        //若是参数超过两个我们可以分装成为一个对象 这里我们采用封装的方式 调用参数很多的一个自己写的一个类 然后再回到我们的OKhttp文档 调用之前的里面的代码
        public String getAccessTolen(AccessTokenDTO accessTokenDTO)
        {
         MediaType mediaType = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
             String string = response.body().string();
             String token =string.split("&")[0].split("=")[1];
            } catch (Exception e) {
               e.printStackTrace();
            }
            return null;
        }
        //代码来源https://square.github.io/okhttp/ URL
        public GithubUser getUser(String accessToken)
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/user?access_token"+accessToken)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String string = response.body().string();
                GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
                return githubUser;
                //string 的 jSion转换为java的类对象 不用自己再去一点点解析string
            } catch (IOException e) {
                return null;

            }

        }

        }


