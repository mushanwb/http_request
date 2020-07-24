package com.github.mushanwb.httpHeader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mushanwb.OkHttp;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddHttpHeader {
    /*
     * 需求简介:
     *
     * 1、随便找一个登录 api 例如：http://47.91.156.35:8000/auth/login
     * 2、使用账号密码登录 api （一般登录为post），提供账号密码：{"username": "xdml", "password": "xdml"}
     * 3、获取登录后的 cookie
     * 4、使用登录后获取的 cookie 访问需要登录的网页 例如：http://47.91.156.35:8000/auth
     * 5、获取带正确 cookie 后访问 http://47.91.156.35:8000/auth 的用户信息
     *
     * http://47.91.156.35:8000/auth 没带 cookie 的话会输出用户未登录
     */

    // 请求 post 请求的数据发送格式
    public static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    // 登录接口限制了请求来源，必须是浏览器，否则请求不了，因此需要设置 USER_AGENT
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";


    public static void main(String[] args) throws IOException {
        String username = "xdml";
        String password = "xdml";
        String getHttpUrl = "http://47.91.156.35:8000/auth";
        String postHttpUrl = "http://47.91.156.35:8000/auth/login";
        JSONObject userInfo = getUserInfo(username,password,postHttpUrl,getHttpUrl);
        System.out.println(userInfo);
    }

    public static JSONObject getUserInfo(String username, String password, String postHttpUrl, String getHttpUrl) throws IOException {

        Map<String,String> userInfo = new HashMap<>();

        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("username",username);
        dataMap.put("password",password);

        // 将 map 序列化为 json 字符串
        String dataJson = JSONArray.toJSONString(dataMap);

        // 创建一个 body（发送数据） 并且规定 body 数据的格式
        RequestBody body = RequestBody.create(dataJson, JSON_TYPE);

        // 创建一个 post 的 header
        Map<String,String> postHeader = new HashMap<>();
        postHeader.put("User-Agent",USER_AGENT);

        OkHttp okHttpPost = new OkHttp(postHttpUrl,postHeader);
        // http post 请求
        Response responsePost = okHttpPost.HttpPost(body).getResponse();

        // 获取请求返回头中的 Set-Cookie
        String[] cookies = responsePost.header("Set-Cookie").split(";");
        String cookie = cookies[0];

        // 创建一个 get 的 header
        Map<String,String> getHeader = new HashMap<>();
        getHeader.put("cookie",cookie);

        OkHttp okHttpGet = new OkHttp(getHttpUrl,getHeader);

        // 发送 get 请求 获取请求返回的 json 数据
        String responseJson = okHttpGet.httpGet().getBody();

        // 获取 json 数据中的 data
        return ((JSONObject) JSON.parse(responseJson)).getJSONObject("data");
    }




}
