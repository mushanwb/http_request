package com.github.mushanwb;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class OkHttp {

    private String httpUrl;
    private Headers headers;
    private String body;
    private Response response;
    static OkHttpClient client = new OkHttpClient();

    public OkHttp(String httpUrl, Map<String,String> header) {
        this.httpUrl = httpUrl;
        this.headers = Headers.of(header);
    }

    public String getBody() {
        return body;
    }

    public Response getResponse() {
        return response;
    }

    public OkHttp HttpPost(RequestBody body) throws IOException {

        // 创建 post 请求
        Request requestPost = new Request.Builder()
                .url(this.httpUrl)
                .headers(this.headers)
                .post(body)
                .build();
        try (Response response = client.newCall(requestPost).execute()) {
            this.response = response;
            this.body = Objects.requireNonNull(response.body()).string();
            return this;
        }

    }

    public OkHttp httpGet() throws IOException {
        // 创建 get 请求
        Request requestGet = new Request.Builder()
                .url(this.httpUrl)
                .headers(this.headers)
                .build();
        try (Response response = client.newCall(requestGet).execute()) {
            this.response = response;
            this.body = Objects.requireNonNull(response.body()).string();
            return this;
        }
    }



}
