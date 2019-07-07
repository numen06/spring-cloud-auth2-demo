package com.ladyishenlong.oauth2clientsqr.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;
import javax.xml.ws.Response;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author ruanchenhao
 * @Date 2019-07-03 17:19
 */
@Slf4j
@RestController
public class HelloController {


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/hello")
    public String hello() {
        return "hello oath2 client";
    }


    /**
     * 获取返回的code
     *
     * @param code
     * @return
     */
    @GetMapping("/getCode")
    public String getCode(@RequestParam("code") String code) {
        log.info("验证码：{}", code);
        return code;
    }


    /**
     * todo 这里已经获得了code，但是token有没有用还不知道
     *
     * @return
     */
    @GetMapping("/login")
    public String login() throws Exception {

        String oauthUrl = "http://localhost:5002/oauth/authorize?client_id=123&response_type=code&redirect_uri=http://localhost:5003/getCode";
        String loginUrl = "http://localhost:5002/login";

        String username = "123";
        String password = "123";

//        HashMap<String, String> param = new HashMap<>();
//        param.put("client_id", "123");
//        param.put("response_type", "code");
//
//        //response cookie是一样的 -> login的request cookie
//
//        ResponseEntity<String> result =
//
//                restTemplate.getForEntity(url, String.class);
//
//        log.info("请求结果：" + result.getStatusCode());
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("client_id", "123");
        params.put("response_type", "code");
        params.put("redirect_uri", "http://localhost:5003/getCode");

        StringBuilder postStr = new StringBuilder();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postStr.length() != 0) {
                postStr.append('&');
            }
            postStr.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postStr.append('=');
            postStr.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        byte[] postStrBytes = postStr.toString().getBytes("UTF-8");

        URL url = new URL(oauthUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(postStrBytes.length));
        connection.getOutputStream().write(postStrBytes);

        connection.setInstanceFollowRedirects(false);// 必须设置该属性

        String location = connection.getHeaderField("Location");
        String cookie = connection.getHeaderField("Set-Cookie");


        log.info("重定向Location:{}", location);
        log.info("重定向信息：{}", cookie);
        log.info("截取cookie：{}", cookie.substring(0, cookie.indexOf(";")));
        log.info("状态码：{}", connection.getResponseCode());

        if (connection.getResponseCode() == 302) {

            MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
            params2.add("username", "123");
            params2.add("password", "123");

            HttpHeaders header = new HttpHeaders();
            header.set("Cookie", cookie.substring(0, cookie.indexOf(";")));

            HttpEntity<MultiValueMap<String, String>> requestEntity =
                    new HttpEntity<>(params2, header);

            ResponseEntity<String> request = restTemplate.
                    postForEntity(loginUrl, requestEntity, String.class);

            log.info("请求结果：{}", request);
        }


        return location.substring(location.indexOf("=") + 1);
    }


    /**
     * 接口访问资源服务器，没有登录认证返回401异常
     *
     * @return
     */
    @GetMapping("/getResource")
    public String getResource() {
        return restTemplate.getForObject("http://localhost:5004/getResource", String.class);
    }

}
