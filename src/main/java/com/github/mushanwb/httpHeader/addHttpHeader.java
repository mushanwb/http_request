package com.github.mushanwb.httpHeader;

import com.alibaba.fastjson.JSONArray;
import com.github.mushanwb.html.HtmlAnalyze;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addHttpHeader {
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
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // 登录接口限制了请求来源，必须是浏览器，否则请求不了，因此需要设置 USER_AGENT
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";


    public static void main(String[] args) throws IOException {
        String username = "xdml";
        String password = "xdml";
        String getHttpUrl = "http://47.91.156.35:8000/auth";
        String postHttpUrl = "http://47.91.156.35:8000/auth/login";
        Map<String,String> userInfo = getUserInfo(username,password,postHttpUrl,getHttpUrl);
        System.out.println(userInfo);
    }

    public static Map<String,String> getUserInfo(String username, String password, String postHttpUrl, String getHttpUrl) throws IOException {

        Map<String,String> userInfo = new HashMap<>();

        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("username",username);
        dataMap.put("password",password);

        // 将 map 序列化为 json 字符串
        String dataJson = JSONArray.toJSONString(dataMap);

        // 创建一个 body（发送数据） 并且规定 body 数据的格式
        RequestBody body = RequestBody.create(dataJson, JSON);

        // 创建一个 post 的 header
        Map<String,String> postHeader = new HashMap<>();
        postHeader.put("User-Agent",USER_AGENT);

        // http post 请求
        Response responsePost = HttpPost(postHttpUrl, postHeader, body);

        // 获取请求返回头中的 Set-Cookie
        String[] cookies = responsePost.header("Set-Cookie").split(";");
        String cookie = cookies[0];

        // 创建一个 get 的 header
        Map<String,String> getHeader = new HashMap<>();
        getHeader.put("cookie",cookie);

        // 发送 get 请求
        Response responseGet = httpGet(getHttpUrl,getHeader);

        //获取请求返回的 json 数据
        String responseJson = responseGet.body().string();

        // 获取 json 数据中的 data
//        userInfo = JSONArray.p(responseJson);
        return userInfo;
    }

    public static Response HttpPost(String postHttpUrl, Map<String,String> header, RequestBody body) throws IOException {
        Headers headers = Headers.of(header);

        OkHttpClient client = new OkHttpClient();

        // 创建 post 请求
        Request requestPost = new Request.Builder()
                .url(postHttpUrl)
                .headers(headers)
                .post(body)
                .build();
        try (Response response = client.newCall(requestPost).execute()) {
            return response;
        }

    }

    public static Response httpGet(String getHttpUrl, Map<String,String> header) throws IOException {
        Headers headers = Headers.of(header);

        OkHttpClient client = new OkHttpClient();
        // 创建 get 请求
        Request requestGet = new Request.Builder()
                .url(getHttpUrl)
                .headers(headers)
                .build();
        try (Response response = client.newCall(requestGet).execute()) {
            return response;
        }
    }


}
