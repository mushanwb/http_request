package com.github.mushanwb.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mushanwb.html.HtmlAnalyze;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonAnalyze {

    /*
     * 需求简介:
     *
     * 1、随便找一个 github 仓库项目的 issues api，例如：https://api.github.com/repos/mushanwb/casual_write/issues
     * github 仓库提供了专门的 api，可以在 google 上搜到 https://api.github.com/repos/ + 项目名 + 列表
     * 2、获取 api 的 json 数据进行分析
     * 3、需要的数据有 issues 中的 title ，number，author，url
     *
     * 用 HtmlAnalyze 的静态内部类 GitHubIssues 作为数据的输出（一次一次的学习笔记，就不去封装代码了）
     */

    public static void main(String[] args) throws IOException {

        String githubIssuesApi = "https://api.github.com/repos/mushanwb/casual_write/issues";

        List<HtmlAnalyze.GitHubIssues> lists = getFirstPageOfIssues(githubIssuesApi);

        for (HtmlAnalyze.GitHubIssues list : lists) {
            System.out.println(list.getId());
            System.out.println(list.getAuthor());
            System.out.println(list.getTitle());
            System.out.println(list.getUrl());
        }
    }

    public static List<HtmlAnalyze.GitHubIssues> getFirstPageOfIssues(String url) throws IOException {

        List<HtmlAnalyze.GitHubIssues> list = new ArrayList<>();

        // 创建 httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 发送 get 请求
        HttpGet httpGet = new HttpGet(url);
        // 执行 get 请求
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            // getStatusLine 获取 http 请求状态码
            System.out.println(response.getStatusLine().getStatusCode());   //  打印结果    200

            // 获取 http 返回的字节流
            HttpEntity entity = response.getEntity();

            // 获取字节流中的 body 数据流
            InputStream inputStream = entity.getContent();

            //将 body 数据流转化为 字符串，编码为 UTF-8
            String jsons = IOUtils.toString(inputStream, "UTF-8");

            // 将 json 数据 反序列化
            JSONArray jsonArray = JSON.parseArray(jsons);

            for (Object json: jsonArray) {

                // 需要将每一个 json 数据转型为 JSONObject 类型，获取里面的值
                String title = ((JSONObject) json).getString("title");
                String issuesUrl = ((JSONObject) json).getString("html_url");

                // getString 获取的是一个字符串类型，需要转为 int 类型
                int id = Integer.valueOf(((JSONObject) json).getString("number"));

                // user 也是一个 Object ，所以需要 getJSONObject 方法，然后再使用 getString 获取里面的 login（author）
                String author = ((JSONObject) json).getJSONObject("user").getString("login");

                // 创建 GitHubIssues 的对象放入 list 中
                list.add(new HtmlAnalyze.GitHubIssues(id, title, author, issuesUrl));
            }

        } finally {
            response.close();
        }

        return list;
    }


}
