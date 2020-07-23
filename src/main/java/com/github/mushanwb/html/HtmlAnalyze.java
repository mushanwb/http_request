package com.github.mushanwb.html;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HtmlAnalyze {
    /*
     * 需求简介:
     *
     * 1、随便找一个 github 仓库项目的 issues url，例如：https://github.com/mushanwb/casual_write/issues
     * 2、抓取 issues 网页中的 Open 列表
     * 3、需要的数据有 issues 中的 title ，number，author，url
     *
     */

    // 创建 issues 的内部静态类
    public static class GitHubIssues {
        // Issues 的标号
        int id;
        // Issues 的标题
        String title;
        // Issues 的作者的 GitHub 用户名
        String author;
        // Issues 的 url
        String url;

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public GitHubIssues(int id, String title, String author, String url) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.url = url;
        }
    }


    public static void main(String[] args) throws IOException {

        String githubIssuesHtmlUrl = "https://github.com/mushanwb/casual_write/issues";

        List<GitHubIssues> lists = getFirstPageOfIssues(githubIssuesHtmlUrl);

        for (GitHubIssues list : lists) {
            System.out.println(list.getId());
            System.out.println(list.getAuthor());
            System.out.println(list.getTitle());
            System.out.println(list.getUrl());
        }
    }



    public static List<GitHubIssues> getFirstPageOfIssues(String url) throws IOException {

        List<GitHubIssues> list = new ArrayList<>();

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
            String html = IOUtils.toString(inputStream, "UTF-8");

            // 使用 jsoup 包解析 html
            Document document = Jsoup.parse(html);

            // 找到 js-issue-row 的 class 标签（这应该是 issues 的列表 div 相同标签）
            ArrayList<Element> issues = document.select(".js-issue-row");

            for (Element element:issues) {
                // 根据 issues 页面的 html 分析，title 在 class 为 js-navigation-open 的 a 标签中，是 a 便签的内容，用 text 方法取内容
                String title = element.select(".js-navigation-open").text();

                // issues 的 url 也在 class 为 js-navigation-open 的 a 标签中，同时是一个 href 标签，用 arrt 方法取标签数据
                String githubIssuesUrl = element.select(".js-navigation-open").attr("href");

                // issues 的编号和名字都在 class 为 opened-by 的 span 标签中
                String issuesInfo = element.select(".opened-by").text();        // 结果：#11 opened Jul 20, 2020 by mushanwb

                // 将这个信息用空格分成 String[];
                String[] infos = issuesInfo.split(" ");

                // name 是数组中最后一个元素
                String name = infos[infos.length - 1];

                // id 在数组中第一个元素中，同时需要去掉 # 符号，
                // 这里使用 substring 方法将字符串第一个数去掉，同时使用 Integer 的 valueOf 方法，将字符串数字变为 int
                int id = Integer.valueOf(infos[0].substring(1));

                // 创建 GitHubIssues 的对象放入 list 中
                list.add(new GitHubIssues(id, title, name, githubIssuesUrl));
            }
        } finally {
            response.close();
        }

        return list;
    }


}
