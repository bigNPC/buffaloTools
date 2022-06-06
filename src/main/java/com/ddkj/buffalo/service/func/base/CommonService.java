package com.ddkj.buffalo.service.func.base;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonService extends CommonServiceUtil {
    public static final String excelConfigSrc = getProjectUrl() + File.separator + "config" + File.separator + "configExcel" + File.separator;
    public static final String excelGenConfigJson = getProjectUrl() + File.separator + "output" + File.separator + "configJson" + File.separator;
    public static final String excelRewardSrc = getProjectUrl() + File.separator + "config" + File.separator + "rewardExcel" + File.separator;
    public static final String excelGenRewardJson = getProjectUrl() + File.separator + "output" + File.separator + "rewardJson" + File.separator;
    public static final String TRUE = "true";
    public static final String END = "end";

    public static final String int8 = "int8";
    public static final String int16 = "int16";
    public static final String int32 = "int32";
    public static final String INT = "int";
    public static final String int64 = "int64";
    public static final String DOUBLE = "double";
    public static final String LONG = "long";
    public static final String string = "string";
    public static final String json = "json";
    public static final String jsonArray = "array";
    public static final String SHEET = "sheet";    //表
    public static final String Key = "key";        //唯一键

    //genObject
    public static final String genFileURL = getProjectUrl() + File.separator + "output" + File.separator + "configJava" + File.separator;
    public static final String genFileExtend = "me.ddkj.dto.activity.domain.";
    public static void genJava(String className, Map<String, String> keyMap, Map<String, String> nameMap, String activityId, boolean extend) throws IOException {
        String javaContent = getJavaContent(className.split("\\.")[0], keyMap, nameMap, activityId, extend);
        System.err.println(genFileURL  + activityId + File.separator + className + ".java");
        File file = new File(genFileURL  + activityId + File.separator + className + ".java");
        if(file.exists()){
            boolean delete = file.delete();
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        boolean newFile = file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(javaContent);
        out.flush();
        out.close();
    }
    public static String getJavaContent(String className, Map<String, String> keyMap, Map<String, String> nameMap, String activityId, boolean extend){
        List<String> list = new ArrayList<>();
        for(Map.Entry<String,String> entry : keyMap.entrySet()){
            String remark = "\t/**\n" +
                    "\t *  " + nameMap.get(entry.getKey()) + "\n" +
                    "\t */\n";
            String typeAndString = getTypeAndString(entry.getValue(), entry.getKey(), activityId);
            typeAndString = "\t" + "private " + typeAndString + ";" + "\n\n";
            list.add(remark + typeAndString);
        }

        StringBuilder sb = new StringBuilder();
        if(StringUtils.isEmpty("")){
            sb.append("package tools.genFile."+ activityId +";\n\n");
        }

        if(extend){
            sb.append(
                    "import lombok.Data;\n" +
                            "import lombok.EqualsAndHashCode;\n" +
                            "import java.util.*;\n" +
                            "import java.io.Serializable;\n"
            );

            if(className.contains("Draw")){
                sb.append( "import " + genFileExtend  + "CommonActDraw;\n" +
                        "\n" +
                        "@Data\n" +
                        "@EqualsAndHashCode(callSuper = false)\n");
                sb.append("public class " + className + " extends CommonActDraw" + " implements Serializable {\n\n");
            }else if(className.contains("Daily") || className.contains("Function")){
                sb.append( "import " + genFileExtend  + "CommonActFunction;\n" +
                        "\n" +
                        "@Data\n" +
                        "@EqualsAndHashCode(callSuper = false)\n");
                sb.append("public class " + className + " extends CommonActFunction" + " implements Serializable {\n\n");
            }else if(className.contains("Main")){
                sb.append( "import " + genFileExtend  + "ActivityMain;\n" +
                        "\n" +
                        "@Data\n" +
                        "@EqualsAndHashCode(callSuper = false)\n");
                sb.append("public class " + className + " extends ActivityMain" + " implements Serializable {\n\n");
            }else{
                sb.append(
                        "\n" +
                                "@Data\n" +
                                "@EqualsAndHashCode(callSuper = false)\n");
                sb.append("public class " + className + " implements Serializable {\n\n");
            }
        }else{
            sb.append(
                    "import lombok.Data;\n" +
                            "import lombok.EqualsAndHashCode;\n" +
                            "import java.util.*;\n" +
                            "import java.io.Serializable;\n" +
                            "\n" +
                            "@Data\n" +
                            "@EqualsAndHashCode(callSuper = false)\n"
            );
            sb.append("public class " + className + " implements Serializable {\n\n");
        }
        for(String str:list){
            sb.append(str);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String getTypeAndString(String type, String key, String activityId){
        String typeAndField = "String";
        if(SHEET.equalsIgnoreCase(type)){
            typeAndField = "List<" + getStartStringUp(activityId) + getStartStringUp(key) + ">";
        }else if(int8.equalsIgnoreCase(type)){
            typeAndField = "Byte";
        }else if(int16.equalsIgnoreCase(type)){
            typeAndField = "Short";
        }else if(int32.equalsIgnoreCase(type) || INT.equalsIgnoreCase(type)){
            typeAndField = "Integer";
        }else if(int64.equalsIgnoreCase(type) || LONG.equalsIgnoreCase(type)){
            typeAndField = "Long";
        }else if(DOUBLE.equalsIgnoreCase(type)){
            typeAndField = "Double";
        }else if(json.equalsIgnoreCase(type)){
            typeAndField = "String";
        }
//        if(SHEET.equalsIgnoreCase(type)){
//            typeAndField += " " + getStartStringLow(key) + "List";
//        }else{
//            typeAndField += " " + getStartStringLow(key);
//        }
        typeAndField += " " + key;
        return typeAndField;
    }

    //Reward
    /**
     * 字符串获取数字
     */
    public static int getNumByStrDay(String str) {
        if (str.contains("天") || str.contains("day")) {
            return Integer.parseInt(Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim()) * 24;
        } else {
            return Integer.parseInt(Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim());
        }
    }
    /**
     * 字符串获取数字 小时数
     */
    public static int getNumByStrDays(String str) {
        if (str.contains("h") || str.contains("小时")) {
            return Integer.parseInt(Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim());
        }
        if (str.contains("天") || str.contains("day")) {

        }
        //默认全数字为天数  //Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim()
        if(str.contains(".")){
            Matcher matcher = Pattern.compile("(\\d+(\\.\\d+))").matcher(str);
            if(matcher.find()){
                return (int)(Double.parseDouble(matcher.group()) * 24);
            }else{
                throw new IllegalArgumentException("");
            }
        }
        return Integer.parseInt(Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim()) * 24;
    }
    /**
     * 排除不存在此列表的奖励
     */
    public static Integer getType(String typeName) {
        if (StringUtils.isEmpty(typeName)) {
            return null;
        }
        if (typeName.contains("徽章") || typeName.contains("勋章")) {
            return 1;
        }
        if (typeName.contains("头饰")) {
            return 2;
        }
        if (typeName.contains("背景")) {
            return 3;
        }
        if (typeName.contains("座驾")) {
            return 4;
        }
        if (typeName.contains("气泡")) {
            return 8;
        }
        if (typeName.contains("主页飘") || typeName.contains("飘屏") ) {
            return 9;
        }
        if (typeName.contains("抽奖券")) {
            return 10;
        }
        if (typeName.contains("vip") || typeName.contains("VIP") ) {
            return 11;
        }
        //RoomRoom  FrameFrame
        if (typeName.contains("Room") && typeName.contains("Frame")  || typeName.contains("框") ) {
            return 12;
        }
        return null;
    }
    public String getTypeNameByResource(int type) {
        switch (type) {
            case 1:
                return "لقب";       //徽章Symbol
            case 2:
                return "إطار";      //头饰Frame
            case 3:
                return "خلفية";     //房间背景Theme
            case 4:
                return "سيارة";     //座驾Car
            case 5:
                return "عملة ذهبية";//金币Coins1
            case 6:
                return "أيدي مميز"; //靓号CuteNumber
            case 7:
                return null;//背包免费礼物GiftBack
            // 还有一些礼物 属于 福袋礼物 不走送礼流程 但是 开出来的 礼物 走送礼流程。类似送礼的时候加了随机礼物送出
            // 普通礼物和抽奖礼物 都属于金币礼物
            // 靓号和 金币 不算商城里的
        }
        return null;
    }
}
