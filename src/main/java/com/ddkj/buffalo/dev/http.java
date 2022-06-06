package com.ddkj.buffalo.dev;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class http {
    private static String url="http://localhost:8080/bridge_qqgame/jwqk.html";
    public static void _okGet() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        String param="buy-588000]钻石|60";
        param= URLEncoder.encode(param,"utf-8");
        System.out.println("appid=1111044909&param="+param);
        HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("appid", "1111044909")
                .addQueryParameter("param", param)
                .build();

        Request request = new Request.Builder().url(httpUrl.toString()).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                System.out.println(body.string());
            } else {
                System.out.println(body.string());
            }
        }
    }

    public static void _okPost() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

    // request body
        Map<String, String> foo = new HashMap<>();
        foo.put("name", "HTTP");
        foo.put("age", "18");

        Request request = new Request.Builder().url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSONString(foo))).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                System.out.println(body.string());
            } else {
                System.out.println(body.string());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        _okGet();
    }
}
