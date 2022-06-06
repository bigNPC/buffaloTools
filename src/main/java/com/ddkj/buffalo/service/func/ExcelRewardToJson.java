package com.ddkj.buffalo.service.func;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ddkj.buffalo.service.func.base.CommonService;
import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.service.func.domain.GiftDtoData;
import com.ddkj.buffalo.service.func.domain.CommonDTOData;
import com.ddkj.buffalo.service.func.domain.ActResourceDTO;
import com.ddkj.buffalo.service.func.domain.ResDtoEnums;
import com.ddkj.buffalo.service.func.domain.RewardData;
import com.ddkj.buffalo.util.FileBootUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelRewardToJson extends CommonService {
    public static void main(String[] args) throws Exception {
        String activityId = "familyReward";
        boolean isGiftIdsRank = false;
        genRewardJson(activityId, isGiftIdsRank, null);
        System.err.println(getNumber("vip1"));
    }
    //TODO  注意一定要有8个元素
    /**
     * 礼物表转json  DemoDataListener这个会以最多第二行单元格作为行数，同一个id不同天的会丢弃   因此不适合easyExcel  适合poi
     */
    public static void genRewardJson(String activityId, boolean isGiftIdsRank, String activityTime) throws Exception{
        //constellationReward--harirayaReward--familyReward
        String fileName = excelRewardSrc + activityId + ".xlsx";
        String targetJson = excelGenRewardJson + activityId + ".json";

        boolean isSingleRank = false;
        RewardData rewardData = new RewardData();
        activityTime = StringUtils.isBlank(activityTime) ? "1|2022-01-01 05:00:00|2022-01-01 05:00:00" : activityTime;
        rewardData.setActivityTime(activityTime);
        //poi读取
        List<Map<Integer, String>> cellInfo = getCellInfo(fileName);
        int index = -1;

        //礼物榜
        Map<String, List<GiftDtoData>> groupMap = new HashMap<>();
        String rankName = "";
        String timeUnit = "";
        for(Map<Integer, String> en:cellInfo){
            List<String> values = new LinkedList<>(en.values());
            GiftDtoData dtoData = new GiftDtoData();
            for (int i = 0; i < values.size(); i++) {
                if(values.get(i).contains("榜")){
                    rankName = getRankName(values.get(i), isSingleRank, false).get(0);
                    timeUnit = "";
                    continue;
                }
                if(values.get(0).contains("奖励类型")){
                    timeUnit = values.get(6);
                    break;
                }
                try{
                    //0奖励类型	1序号	2名称	3资源ID	4排名	5用户ID	6时长（小时）7备注
                    switch (i) {
                        case 0: dtoData.setTypeName(values.get(i));break;
                        case 1: break;
                        case 2: break;
                        case 3:
                            if(StringUtils.isEmpty(values.get(i)) || values.get(i).equals("/") || values.get(i).equals("新")){
                                index--;
                                dtoData.setResourceId(index);
                            }else{
                                dtoData.setResourceId(Integer.parseInt(values.get(i)));
                            }
                            break;
                        case 4: dtoData.setRank(values.get(i).replace("-",","));break;
                        case 5:dtoData.setUid(null);break;
                        //设置单位
                        case 6: dtoData.setDays(values.get(i) + timeUnit);break;
                        case 7: dtoData.setRemark(values.get(i));break;
                    }
                }catch(Exception e){
                    System.err.println(e.getMessage());
                }

            }
            groupMap.computeIfAbsent(rankName, k -> new LinkedList<>());
            List<GiftDtoData> giftDtoData = groupMap.get(rankName);
            giftDtoData.add(dtoData);
        }
        for(String str:groupMap.keySet()){
            List<GiftDtoData> giftDtoData = groupMap.get(str);
            giftDtoData = giftDtoData.stream()
                    .filter(e->StringUtils.isNotEmpty(e.getTypeName())).collect(Collectors.toList());
            giftDtoData.removeIf(e->{
                if(e.getRemark() == null){
                    return false;
                }
                return e.getRemark().contains("运营") || e.getRemark().contains("手动") || StringUtils.isBlank(e.getRank());
            });
            groupMap.put(str, giftDtoData);
        }

        //json
        Map<String, Map<String, ActResourceDTO>> map = new LinkedHashMap<>();//避免重复 第二个String需要Id连接天数
        groupMap.keySet().forEach(rankNames->{
            //根据排名排序
            //List<GiftDtoData> collect = groupMap.get(rankName).stream().sorted(Comparator.comparing(GiftDtoData::getSource)).collect(Collectors.toCollection(LinkedList::new));
            //顺序排序
            List<GiftDtoData> collect = groupMap.get(rankNames);
            //判断是否有“的”，有则嵌入
            Map<String,ActResourceDTO> totalMap = new LinkedHashMap<>();
            for(GiftDtoData data:collect){
                System.err.println(JSONObject.toJSONString(data));
                ActResourceDTO dto = new ActResourceDTO();
                dto.setResourceId(data.getResourceId());
                dto.setId("0");
                int numByStrDays = 0;

                    numByStrDays = getNumByStrDays(data.getDays());
                    dto.setNum(numByStrDays);
                    dto.setType(getType(data.getTypeName()));


                //设置排名范围(宝箱、兑换没有排名范围，有“的”也分割)
                dto.setRank(String.valueOf(data.getRank()));
                if(rankNames.contains("的")){
                    dto.setName(getRankRange(data.getRemark(), rankNames, true));
                    totalMap.put(dto.getResourceId() + "-" + numByStrDays + "-" + dto.getRank(), dto);
                }else{
                    totalMap.put(dto.getResourceId() + "-" + numByStrDays + "-" + dto.getRank(), dto);
                }
                //VIP处理
                if(dto.getType() == 11){
                    dto.setActGift(getNumber(data.getTypeName()));
                }
                //道具个数处理
                Integer number = getNumber(data.getRemark());
                if(number != null){
                    dto.setGiftNumber(number);
                }
            }
            map.put(rankNames, totalMap);
        });
        for(Map.Entry<String, Map<String, ActResourceDTO>> entry : map.entrySet()){
            System.err.println(entry.getKey());
            for(Map.Entry<String, ActResourceDTO> entry2 : entry.getValue().entrySet()){
                System.err.println(JSONObject.toJSONString(entry2.getValue()));
            }
        }

        rewardData.setReward(map);
        
        String json = JSON.toJSONString(rewardData);
        FileBootUtil.writeFile(targetJson, json);
        System.err.println("写入完成,路径(若如完整默认为根目录)：" + excelConfigSrc + "json\\" + targetJson);
        
        //写入excel对比校验是否遗漏：
        Map<String, Map<String, ActResourceDTO>> reward = rewardData.getReward();
        _rankInfo(reward, activityId);
    }
    /**
     * 严格按行读取
     */
    public static List<Map<Integer, String>> getCellInfo(String fileName) throws Exception{
        FileInputStream input = new FileInputStream(fileName);
        Workbook workbook;
        if (fileName.endsWith(".xls")){
            workbook = new HSSFWorkbook(input);
        } else {
            workbook = new XSSFWorkbook(input);
        }
        Sheet sheet = workbook.getSheet("main");
        if(sheet == null){
            sheet = workbook.getSheet("榜单奖励");
        }
        List<Map<Integer, String>> list = new LinkedList<>();
        String typeName = "";
        String resourceId = "";
        String days = "";
        for (Row row : sheet) {
            Map<Integer, String> map = new LinkedHashMap<>();
            for (int j = 0; j < 8; j++) {
                Cell cell = row.getCell(j);
                if(cell == null){
                    map.put(j, "");
                    continue;
                }
                cell.setCellType(CellType.STRING);
                String value = cell.getStringCellValue();
                if(value.contains("榜")){
                    map.put(0, value);
                    break;
                }
                if(j == 0){//奖励类型
                    if(StringUtils.isBlank(cell.getStringCellValue())){
                        value = typeName;
                    }else{
                        typeName = cell.getStringCellValue().trim();
                    }
                }
                if(j == 3){//资源Id
                    if(StringUtils.isBlank(cell.getStringCellValue())){
                        value = resourceId;
                    }else{
                        resourceId = cell.getStringCellValue().trim();
                    }
                }
                if(j == 6){//时长
                    if(StringUtils.isBlank(cell.getStringCellValue())){
                        value = days;
                    }else{
                        days = cell.getStringCellValue().trim();
                    }
                }
                //去除非可发道具
//                if(getType(typeName) == null){
//                    break;
//                }
                map.put(j, value);
            }
            //去除重复的字段
//            if(map.size() > 3  && StringUtils.isEmpty(map.get(5))){
//                continue;
//            }
            //去除空字段
//            if(StringUtils.isBlank(map.get(4))){
//                continue;
//            }

            list.add(map);
        }
        //去除非可发道具
        list.removeIf(e->e.keySet().size() == 0);
        return list;
    }



    /**
     * 一个单元格内可能多个榜对应一个id
     */
    public static List<String> getRankName(String sourceString, boolean isSingleRank, boolean giftIds) throws RuntimeException {
        //剩下中文范围或换行
        String reg = "[^\u4e00-\u9fa5|\n]";
        String sourceChina = sourceString.replaceAll(reg, "");

        //多礼物排行
        String giftId = "";
        if(giftIds){
            giftId += "-" + getNumber(sourceString);
        }

        List<String> list = new ArrayList<>();
        for(String source:sourceChina.split("\n")) {
            if(isSingleRank){
                //单排行处理
                if(sourceChina.contains("的")){
                    //list.addAll(Arrays.asList(source.split("的")));
                    //list.add(source.split("的")[0]);
                    list.add("的" + source.split("的")[1] + giftId);
                }else if(sourceChina.contains("总")){
                    list.add("sendTotal" + giftId);
                }else if(sourceChina.contains("日")){
                    list.add("sendDaily" + giftId);
                }else {
                    Collections.addAll(list, source);
                }

            }else {
                //多排行处理
                if((source.contains("财富") || source.contains("富豪") ||  source.contains("送") ||  source.contains("椰枣"))){
                    if(source.contains("总")){
                        list.add("sendTotal" + giftId);
                    }else if(source.contains("日")){
                        list.add("sendDaily" + giftId);
                    }else {
                        list.add("send" + giftId);
                    }
                }else if(source.contains("魅力") ||  source.contains("收")){
                    if(source.contains("总")){
                        list.add("receiveTotal" + giftId);
                    }else if(source.contains("日")){
                        list.add("receiveDaily" + giftId);
                    }else {
                        list.add("receive" + giftId);
                    }
                }else if(source.contains("房间")){
                    //month表有问题
                    list.add("room" + giftId);
                }else if(source.contains("宝箱")){
                    //抽奖宝箱处理
                    Collections.addAll(list, source.split("\n"));
                }else{
                    //兑换或者其它处理
                    Collections.addAll(list, source.split("\n"));
                }
            }

        }

        if(CollectionUtils.isEmpty(list)){
            throw new RuntimeException();
        }
        return list;
    }
    public static String getRankRange(String source, String rankName, boolean getBig){
        String topStr = source.split("\n")[0];
        String rank = "";
        String[] 的 = topStr.split("的");
        if(的.length == 1){
            rank = getRank(rank, topStr);
        }else{
            if(getBig){//上司
                if(!rankName.contains("的")){
                    rank = getRank(rank, topStr.split("的")[1]);
                }else{
                    rank = getRank(rank, topStr.split("的")[0]);
                }
            }else{
                if(rankName.contains("的")){
                    rank = getRank(rank, topStr.split("的")[1]);
                }else{
                    rank = getRank(rank, topStr.split("的")[0]);
                }
            }

        }

        return rank.trim().replaceAll("-", ",");
    }

    public static String getRank(String rank, String topStr){
        if(topStr.contains("TOP")){
            rank = topStr.split("TOP")[1];
        }else if (topStr.contains("Top")){
            rank = topStr.split("Top")[1];
        }else {
            rank = topStr.split("top")[1];
        }
        return rank.replaceAll("[\u4e00-\u9fa5]", "");
    }
    /**
     * 读取本地文件测试
     */
    public static Map<String, List<ActResourceDTO>> getRankRewardMap(String activityName, String rankKey) throws IOException {
        ClassPathResource classPathResource = null;//new ClassPathResource("reward/" + activityName + ".json", excel.class.getClassLoader());
        InputStream inputStream = classPathResource.getInputStream();
        String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        RewardData rewardData = JSON.parseObject(text, RewardData.class);
        Map<String, Map<String, ActResourceDTO>> reward = rewardData.getReward();
        Map<String, ActResourceDTO> allDTOMap = reward.get(rankKey);

        return allDTOMap.values().stream().collect(Collectors.groupingBy(ActResourceDTO::getRank));
    }


    public static void _rankInfo(Map<String, Map<String, ActResourceDTO>> reward, String activityId) throws Exception {
        List<CommonDTOData> data = data(reward);

        String fileName = CommonServiceUtil.getDeskTopPath().getPath() + File.separator + activityId + ".xlsx";
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(fileName, CommonDTOData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "main").build();
            excelWriter.write(data, writeSheet);
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    private static List<CommonDTOData> data(Map<String, Map<String, ActResourceDTO>> reward) throws Exception {
        List<CommonDTOData> list = new ArrayList<>();
        for(Map.Entry<String, Map<String, ActResourceDTO>> entry : reward.entrySet()){
            Map<String, ActResourceDTO> value = entry.getValue();
            for(Map.Entry<String, ActResourceDTO> entry2 : value.entrySet()){
                CommonDTOData data = new CommonDTOData();
                data.setKey(entry2.getKey());
                data.setTypeName(ResDtoEnums.getByType(entry2.getValue().getType()).Cn() + "|" + entry2.getValue().getType());
                data.setResourceId(String.valueOf(entry2.getValue().getResourceId()));
                data.setRank(entry2.getValue().getRank());
                data.setHours(entry2.getValue().getNum());
                data.setId(entry2.getValue().getId());
                data.setActGift(entry2.getValue().getActGift());
                data.setRemark(entry2.getValue().getGiftNumber());
                list.add(data);
            }
        }
        return list;
    }
}
