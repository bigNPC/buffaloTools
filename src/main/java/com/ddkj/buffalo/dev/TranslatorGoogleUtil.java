package com.ddkj.buffalo.dev;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.util.OkHttpUtil;
import com.ddkj.buffalo.util.text.HttpHeader;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Google翻译
 */
public class TranslatorGoogleUtil {
    public static String cookie;
    public static String userAgent;
    public static void main(String[] args) throws IOException {
        //System.err.println(TranslatorGoogleUtil.translate2("你好"));
        System.err.println(TranslatorGoogleUtil.translate("안녕하세요", "zh-cn"));
    }

    public static String translate(String trans, String toLanguage) throws IOException {
        String sourceLanguage = CommonServiceUtil.getLanguage(trans);
        System.err.println(sourceLanguage);
        Map<String, String> map = new ConcurrentHashMap<>();
        //client：客户端，通常为 at；
        //sl：source language，源语言；
        //tl：target language，目标语言；
        //dt：返回数据，这里指定为 t 表示对源的翻译结果；
        //q：查询字符串。
        String url = "https://translate.google.cn/translate_a/single?" +
                "client=gtx" +
                "&sl=" + sourceLanguage +
                "&tl=" + toLanguage +
                "&dt=t" +
                "&q=" + trans;
        //String result = OkHttpUtil.build().getResult(url);
        String result;
        if(cookie == null){
            int i = ThreadLocalRandom.current().nextInt(0, HttpHeader.Header.length);
            Request request = new Request.Builder().url(url)
                    .addHeader("user-agent", HttpHeader.Header[i])
                    .build();
            OkHttpUtil.ResponseObject responseObject = OkHttpUtil.build().getResponseObject(request);
            String cookieString = responseObject.getCookie();
            if(StringUtils.isNotBlank(cookieString)){
                cookie = cookieString;
                userAgent = HttpHeader.Header[i];
                System.err.println(cookie);
                System.err.println(userAgent);
            }
            result = responseObject.getBody();
        }else{
            Request request = new Request.Builder().url(url)
                    .addHeader("user-agent", userAgent)
                    .addHeader("cookie", cookie)
                    .build();
            result = OkHttpUtil.build().execute(request);
        }

        System.err.println(result);
        JSONArray array = JSONArray.parseArray(result);
        array = array.getJSONArray(0).getJSONArray(0);
        map.put("source", array.getString(1));
        map.put("target", array.getString(0));
        System.out.println("原文：" + array.getString(1));
        System.out.println("译文：" + array.getString(0));
        return array.getString(0);
    }

    public static Map<String, String> translate2(String trans) throws IOException {
        Map<String, String> map = new ConcurrentHashMap<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://translate.google.cn/translate_a/single?client=gtx&sl=zh-CN&tl=en&dt=t&q=" + trans);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            //请求体内容
            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONArray array = JSONArray.parseArray(content);
            array = array.getJSONArray(0).getJSONArray(0);
            map.put("source", array.getString(1));
            map.put("target", array.getString(0));
            System.out.println("原文：" + array.getString(1));
            System.out.println("译文：" + array.getString(0));
        }
        response.close();
        httpclient.close();
        return map;
    }

    public static String translate(String word){
        try {
            String url = "https://translate.googleapis.com/translate_a/single?" +
                    "client=gtx&" +
                    "sl=en" +
                    "&tl=zh-CN" +
                    "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return parseResult(response.toString());
        }catch (Exception e){
            return  word;
        }
    }

    private static String parseResult(String inputJson){
        System.err.println(inputJson);
        JSONArray jsonArray2 = JSON.parseArray(inputJson);
        StringBuilder result = new StringBuilder();
        for (Object o : jsonArray2) {
            result.append(((JSONArray) o).get(0).toString());
        }
        return result.toString();
    }
}
