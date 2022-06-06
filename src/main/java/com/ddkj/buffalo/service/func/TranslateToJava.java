package com.ddkj.buffalo.service.func;

import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.util.FileBootUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TranslateToJava extends CommonServiceUtil {
    //【年度争霸】xxx开出稀有奖励bbb*ah>>我也去开
    public static final String DrawReward = "[أباطرة العام] حصل xxx على المكافآت النادرة bbb لمدة a ساعة >> شارك الآن";

    public static void main(String[] args) {
        String activityId = "Recharge";
        String content =
                "【太空之旅】你已经进入榜单前20，将有丰厚奖励哦~>>前往查看\t[Christmas Event] You have entered the top 10 list, and there will be generous rewards~>>View\t[السفر إلى الفضاء] لقد دخلت أفضل 20 في القائمة، وستكون هناك مكافآت سخية ~ >> استعراض\n" +
                        "【太空之旅】你已经掉出榜单前20，将失去丰厚奖励>>前往查看\t[Christmas Event] You have fallen out of the top 10 list, and you will lose great rewards >> Go to view\t[السفر إلى الفضاء] لقد خرجت من أفضل 20 في القائمة، وستفقد مكافآت كبيرة >> شارك في النشاط"
                        +"";
        String url = System.getProperty("user.dir");
        url = url.substring(0, url.lastIndexOf(File.separator));
        String javaString = getJavaString(activityId, content, url + "/config/ActivityMessageDefines.properties");

    }

    public static String getJavaString(String activityId, String content, String propertiesUrl) {
        String fileStringLine = FileBootUtil.getFileStringLine(propertiesUrl);
        //System.err.println(fileStringLine);
        assert fileStringLine != null;
        String[] split = fileStringLine.trim().split("\n");
        Map<String, String> configMap = new HashMap<>();
        for(String str:split){
            str = str.trim();
            if(StringUtils.isBlank(str) || str.startsWith("#")){
                continue;
            }
            configMap.put(str.split("=")[0].trim(), str.split("=")[1].trim());
        }
        System.err.println(configMap);

        String replace = configMap.get("replace");
        String defaultValue = configMap.get("default");
        configMap.keySet().removeIf(e->e.equals("default") || e.equals("replace"));

        StringBuilder sb = new StringBuilder();

        content = content.replaceAll("\"\n", "");
        content = content.replaceAll("\t\t\n", "");
        for(String str:content.split("\n")){
            str = str.replaceAll("\r", "");
            String cn = str.split("\\t")[0];
            String ar = str.split("\\t")[2];
            System.err.println("//" + cn);
            if("false".equals(replace)){
                ar=ar.replaceAll(">> اذهب للعرض","");
                ar=ar.replaceAll(">> اذهب للتحقق","");
                ar=ar.replaceAll(">> عرض","");
            }
            for (int i = 0; i < 11; i++) {
                if(i == 10){
                    cn = cn.replace("零一二三四五六七八九十".charAt(i), '0');
                    break;
                }
                cn = cn.replace("零一二三四五六七八九十".charAt(i), (char) ('0' + i));
            }
            String field = getResult(cn, defaultValue, configMap);
            String result = "\tpublic static final String " + getStartStringUp(activityId) + field + " = \"" + ar + "\";";
            System.err.println(result);
            sb.append(result).append("\n");
        }
        return sb.toString();
    }
    private static String getResult(String chinese, String defaultValue, Map<String, String> configMap){
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String,String> entry : configMap.entrySet()){
            result.append(getField(chinese, entry.getKey(), entry.getValue()));
        }
        return "".equals(result.toString()) ? defaultValue : result.toString();
    }


    private static String getField(String chinese, String key, String value){
        if(key.contains("!") || key.contains("&&") || key.contains("||")){
            //!我也去开&&出=Kick

            boolean flag = true;
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                if(i == 0){
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < key.length(); j++) {
                        if(key.charAt(j) == '|' || key.charAt(j) == '&'){
                            i = j - 1;
                            break;
                        }
                        sb.append(key.charAt(j));
                        if(j == key.length() - 1){
                            i = j;
                            break;
                        }
                    }
                    String str = sb.toString();
                    if(str.contains("!")){
                        flag = flag && !chinese.contains(str.replace("!", ""));
                    }else{
                        flag = flag && chinese.contains(str);
                    }
                }
                if(c == '&'){
                    StringBuilder sb = new StringBuilder();
                    for (int j = i + 2; j < key.length(); j++) {
                        if(key.charAt(j) == '|' || key.charAt(j) == '&'){
                            i = j - 1;
                            break;
                        }
                        sb.append(key.charAt(j));
                        if(j == key.length() - 1){
                            i = j;
                            break;
                        }
                    }
                    String str = sb.toString();
                    if(str.contains("!")){
                        flag = flag && !chinese.contains(str.replace("!", ""));
                    }else{
                        flag = flag && chinese.contains(str);
                    }
                }
                if(c == '|'){
                    StringBuilder sb = new StringBuilder();
                    for (int j = i + 2; j < key.length(); j++) {
                        if(key.charAt(j) == '|' || key.charAt(j) == '&'){
                            i = j - 1;
                            break;
                        }
                        sb.append(key.charAt(j));
                        if(j == key.length() - 1){
                            i = j;
                            break;
                        }
                    }
                    String str = sb.toString();
                    if(str.contains("!")){
                        flag = flag || !chinese.contains(str.replace("!", ""));
                    }else{
                        flag = flag || chinese.contains(str);
                    }
                }
            }
            if(flag){
                if(value.contains("+number")){//进入=Enter+number
                    return value.replace("+number", "") + getNumber(chinese);
                }else{
                    return value;
                }
            }else{
                return "";
            }
        }else{
            if(!chinese.contains(key)){//结束=Last
                return "";
            }
            if(value.contains("+number")){//进入=Enter+number
                return value.replace("+number", "") + getNumber(chinese);
            }else{
                return value;
            }
        }
    }

    public static void genJavaString(String url, String className, String content){
        StringBuilder sb = new StringBuilder();
        sb.append("public class " + className + " implements Serializable {\n\n");
        sb.append(content);
        sb.append("}");
        String javaContent = sb.toString();
        genJava2(url, className, javaContent);
    }
}
