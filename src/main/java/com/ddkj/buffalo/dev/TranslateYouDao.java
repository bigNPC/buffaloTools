package com.ddkj.buffalo.dev; /**
 * @author: admins
 * @date：2021/1/12 10:01
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.dongliu.requests.Requests;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

public class TranslateYouDao {
    private String msg;
    private String url="http://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule";
    private String D="n%A-rKaT5fb[Gy?;N5@Tj";
    private String bv;
    private String salt;
    private String sign;
    private String ts;
    private Map<String,Object> header;
    private Map<String,Object> params;
    public TranslateYouDao() {
        params=new HashMap<String,Object>();
        header=new HashMap<String,Object>();
    }
    private Map<String,Object> getParams(String msg) {
        params.put("i", setMsg(msg));//设置msg同时更新ts,salt,sign;
        params.put("from","AUTO");
        params.put("to","AUTO");
        params.put("smartresult","dict");
        params.put("client","fanyideskweb");
        params.put("sign",this.sign);
        params.put("bv",this.bv);
        params.put("ts",this.ts);
        params.put("salt",this.salt);
        params.put("doctype","json");
        params.put("version", "2.1");
        params.put("keyfrom", "fanyi.web");
        params.put("action", "FY_BY_REALTlME");
        return params;
    }
    public String getResult(String msg) {
        return Requests.post(url).headers(getHeaders()).body(getParams(msg)).send().readToText();
        //得到json格式的文本
    }
    public  void setHeaders(Map<String,Object> header) {
        this.header=header;
    }
    public Map<String,Object> getHeaders() {
        if(this.header.get("Referer")==null) {
            this.header.put("Referer", "http://fanyi.youdao.com/");
        }
        return this.header;
    }
    public void setUserAgent(String UA) {
        this.header.put("User-Agent", UA);
        //设置UserAgent
        String cookie=Requests.get("http://fanyi.youdao.com").headers(this.header).send().getHeader("Set-Cookie").split(";")[0]+";";
        //得到Cookie

        //设置Cookie
        this.header.put("Cookie", cookie);
        this.bv=getBv(UA);//设置UA的同时要更新bv，因为bv是通过加密UA得到的。
    }
    private String getSalt() {
        this.salt=String.valueOf(this.ts)+String.valueOf(((int)Math.random()*10));
        return this.salt;

    }
    private String getSign() {
        return getMd5("fanyideskweb"+this.msg+this.salt+this.D);
    }
    private String getTs() {
        return String.valueOf(System.currentTimeMillis());
    }
    private String getBv(String UserAgent) {
        return getMd5(UserAgent);
    }
    private String getMd5(String val) {
        return DigestUtils.md5Hex(val);
    }
    public String setMsg(String msg) {

        this.msg=msg;
        //设置需要翻译的内容
        this.ts=getTs();//更新ts
        this.salt=getSalt();//更新salt
        //设置翻译内容的同时更新ts,salt
        this.sign=getSign();
        //有了翻译内容,salt才能得到sign,网易主要靠此判断
        return this.msg;
    }

    public static String tranlate(String content){
        TranslateYouDao fanyi=new TranslateYouDao();
        fanyi.setUserAgent("Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
        String st=fanyi.getResult(content);
        JSONObject json=JSONObject.parseObject(st);
        JSONArray ja=(JSONArray) ((JSONArray) json.get("translateResult")).get(0);
        JSONObject js=(JSONObject) ja.get(0);
        System.out.println(st);
        System.out.print(js.get("tgt"));
        return  js.get("tgt").toString();
    }
    public static void main( String[] args ) {
        tranlate("아사드");
    }

    //type
    //zh-CHS2en  中文》英语
    //en2zh-CHS  英语》中文
    //zh-CHS2ja  中文》日语
    //ja2zh-CHS  日语》中文
    //zh-CHS2ko  中文》韩语
    //ko2zh-CHS  韩语》中文
    //zh-CHS2fr  中文》法语
    //fr2zh-CHS  法语》中文
    //zh-CHS2de  中文》德语
    //de2zh-CHS  德语》中文
    //zh-CHS2ru  中文》俄语
    //ru2zh-CHS  俄语》中文
    //zh-CHS2es  中文》西班牙语
    //es2zh-CHS  西班牙语》中文
    //zh-CHS2pt  中文》葡萄牙语
    //pt2zh-CHS  葡萄牙语》中文
    //zh-CHS2it  中文》意大利语
    //it2zh-CHS  意大利语》中文
    //zh-CHS2vi  中文》越南语
    //vi2zh-CHS  越南语》中文
    //zh-CHS2id  中文》印尼语
    //id2zh-CHS  印尼语》中文
    //zh-CHS2ar  中文》阿拉伯语
    //ar2zh-CHS  阿拉伯语》中文
    //zh-CHS2n1  中文》荷兰语
    //n12zh-CHS  荷兰语》中文
}
