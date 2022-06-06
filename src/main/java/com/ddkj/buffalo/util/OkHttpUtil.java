package com.ddkj.buffalo.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpUtil {
    private static volatile OkHttpClient okHttpClient = null;
    private static volatile OkHttpUtil okHttp = null;

    private OkHttpUtil() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(2000);
        dispatcher.setMaxRequestsPerHost(2000);
        ConnectionPool connectionPool = new ConnectionPool(300, 5, TimeUnit.MINUTES);
        okHttpClient = new OkHttpClient.Builder().dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpUtil build() {
        if (okHttp == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttp == null) {
                    okHttp = new OkHttpUtil();
                }
            }
        }
        return okHttp;
    }

    public void enqueue(Request request, Callback callback) {
        okHttpClient.newCall(request).enqueue(callback);
    }

    public String execute(Request request) throws IOException {
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody body = response.body();
        assert body != null;
        return new String(body.bytes(), StandardCharsets.UTF_8);
    }

    public String getResult(String urlString) throws IOException {
        Request request = new Request.Builder().url(urlString).build();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody body = response.body();
        assert body != null;
        return new String(body.bytes(), StandardCharsets.UTF_8);
    }

    public ResponseObject getResponseObject(Request request) throws IOException {
        Response response = okHttpClient.newCall(request).execute();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCookie(response.header("cookie"));
        responseObject.setUserAgent(response.header("user-agent"));
        ResponseBody body = response.body();
        assert body != null;
        String bodyContent = new String(body.bytes(), StandardCharsets.UTF_8);
        responseObject.setBody(bodyContent);
        return responseObject;
    }

    @Data
    public class ResponseObject {
        private String cookie;
        private String userAgent;
        private String body;
    }
}
