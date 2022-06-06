package com.ddkj.buffalo.dev;


import java.io.IOException;

public class test {

    public static void main(String[] args) throws IOException {
        //支持 汉字 转 UTF-8 ， 也可以 UTF-8编码 转 汉字
        //ip(输入域名查IP，输入IP查地址)  请求(getPost)、json、text
        //Base64加解密、url转码、大小写、时间戳互转

        System.err.println(TranslatorGoogleUtil.translate("안녕하세요", "en"));
    }




}
