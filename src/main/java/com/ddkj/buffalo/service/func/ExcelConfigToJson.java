package com.ddkj.buffalo.service.func;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ddkj.buffalo.service.func.base.ActivityConfigFile;
import com.ddkj.buffalo.service.func.base.CommonService;
import com.ddkj.buffalo.util.FileBootUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 配置转换工具
 */
public class ExcelConfigToJson extends CommonService {
    private static final List<ActivityConfigFile> files = new LinkedList<>();
    public static final String MainSheetName = "main";
    //constellation--hariraya--family--recharge
    public static void main(String[] args) throws Exception {
        String activityId = "recharge";
        boolean extend = true;          //是否继承通用组件
        files.clear();
        List<Map<String, Object>> main = excelToJson(activityId, MainSheetName, null);
        //生成Java文件
        gen(activityId, extend);

        //生成json文件
        FileBootUtil.writeFile(activityId + ".json", JSON.toJSONString(main.get(0)));
        FileBootUtil.writeFile(excelGenConfigJson + activityId + ".json", JSON.toJSONString(main.get(0)));
    }

    public static void gen(String activityId, boolean extend) throws IOException {
        for(ActivityConfigFile file:files){
            Map<String, String> keys = file.getTypes();
            Map<String, String> names = file.getNames();
            String className = getStartStringUp(activityId) + getStartStringUp(file.getSheetName());
            genJava(className, keys, names, activityId, extend);
        }
    }
    public static void clear(){
        files.clear();
    }


    //先转Java文件  再转json
    public static List<Map<String, Object>> excelToJson(String activityId, String sheetName, String parentSheetName) throws Exception {
        String fileName = excelConfigSrc + activityId +  ".xlsx";
        List<Map<Integer, String>> cellInfo = getCellInfo(fileName, sheetName);
        //检测是否重复，否则throws
        List<String> keyList = new LinkedList<>();
        List<String> typeList = new LinkedList<>();
        List<String> nameList = new LinkedList<>();
        List<String> remarkList  = new LinkedList<>();
        List<Map<String, Object>> dataMap = new LinkedList<>();
        for (int i = 0; i < cellInfo.size(); i++) {
            switch(i){
                case 0: //主键Key
                    keyList = getKeyList(cellInfo, i, sheetName);
                    break;
                case 1: //类型Type string sheet int8 int16 int32(int) int64(long) 等
                    typeList = getList(cellInfo, i);
                    break;
                case 2: //名字Name
                    nameList = getList(cellInfo, i);
                    break;
                case 3: //备注Remark
                    remarkList = getList(cellInfo, i);
                    //生成Java文件 activityId + sheetName
                    Map<String, String> typeMap = new LinkedHashMap<>();
                    for (int j = 1; j < keyList.size(); j++) {
                        typeMap.put(keyList.get(j), typeList.get(j));
                    }
                    Map<String, String> nameMap = new LinkedHashMap<>();
                    for (int j = 1; j < keyList.size(); j++) {
                        String nameAndRemark = null;
                        if(nameList.get(j).equals(remarkList.get(j))){
                            nameAndRemark = nameList.get(j);
                        }else{
                            nameAndRemark = nameList.get(j) + "(" + remarkList.get(j) + ")";
                        }
                        nameMap.put(keyList.get(j), nameAndRemark);
                    }
                    files.add(new ActivityConfigFile(sheetName, parentSheetName, typeMap, nameMap));
                    break;
                default://True、False And End 数据
                    Map<String, Object> data = getDataMap(cellInfo, i, keyList, typeList, activityId, sheetName);
                    if(data == null){
                        //END结束循环
                        Map<Integer, String> integerStringMap = cellInfo.get(i);
                        String key = integerStringMap.get(0);
                        if(key.equalsIgnoreCase("end") ){
                            break;
                        }
                        //False跳过
                        continue;
                    }
                    Map<Integer, String> integerStringMap = cellInfo.get(i);
                    String key = integerStringMap.get(1);//refId为key值
                    dataMap.add(data);
                    break;
            }
        }
        System.err.println(dataMap);
        return dataMap;
    }
    /**
     * 主键Key列表
     */
    public static List<String> getKeyList(List<Map<Integer, String>> cellInfo, int n, String sheetName){
        Map<Integer, String> cellMap = cellInfo.get(n);
        List<String> values = new LinkedList<>(cellMap.values());
        List<String> list = new LinkedList<>();
        //检测重复和结束
        for (int i = 0; i < values.size(); i++) {
            String str = values.get(i);
            if(StringUtils.isBlank(str)){
                break;
            }
            if(list.contains(str)){
                System.err.println("excel:" + sheetName + "行列[" + n + "," + i + "]" + "重复异常, 数值：" + str);
                throw new NullPointerException("excel:" + sheetName + "行列[" + n + "," + i + "]" + "重复异常, 数值：" + str);
            }else{
                list.add(str);
            }
        }
        return list;
    }
    public static List<String> getList(List<Map<Integer, String>> cellInfo, int n){
        Map<Integer, String> cellMap = cellInfo.get(n);
        return new LinkedList<>(cellMap.values());
    }

    /**
     * 校验数据和存入数据对象
     */
    public static Map<String, Object> getDataMap(List<Map<Integer, String>> cellInfo, int n, List<String> keyList, List<String> typeList, String activityId, String sheetName) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Map<Integer, String> cellMap = cellInfo.get(n);
        if(!cellMap.get(0).equalsIgnoreCase(TRUE) || cellMap.get(0).equalsIgnoreCase(END)){
            return null;
        }


        List<String> refIdKeys = new LinkedList<>();
        int i = 1;
        try{
            for (; i < keyList.size(); i++) {
                String type = typeList.get(i);
                String value = cellMap.get(i);
                //判断是否是sheet 转成Object
                if(SHEET.equalsIgnoreCase(type)){
                    Object sheetJson = getSheetJson(value, activityId, sheetName);
                    map.put(keyList.get(i), sheetJson);
                    continue;
                }else if(int8.equalsIgnoreCase(type)){
                    byte i1 = Byte.parseByte(value);
                }else if(int16.equalsIgnoreCase(type)){
                    short i1 = Short.parseShort(value);
                }else if(int32.equalsIgnoreCase(type)){
                    //只有int32允许空
                    if(StringUtils.isNotEmpty(value)){
                        int i1 = Integer.parseInt(value);
                    }
                }else if(INT.equalsIgnoreCase(type)){
                    int i1 = Integer.parseInt(value);
                }else if(int64.equalsIgnoreCase(type) || LONG.equalsIgnoreCase(type)){
                    long i1 = Long.parseLong(value);
                }else if(DOUBLE.equalsIgnoreCase(type)){
                    double i1 = Double.parseDouble(value);
                }else if(json.equalsIgnoreCase(type)){
                    //json也允许为空
                    if(StringUtils.isNotEmpty(value)){
                        JSONObject jsonObject = JSON.parseObject(value);
                    }
                }else if(jsonArray.equalsIgnoreCase(type)){
                    //array也允许为空
                    if(StringUtils.isNotEmpty(value)){
                        JSONArray objects = JSON.parseArray(value);
                    }
                }else if(string.equalsIgnoreCase(type)){
                    if(value != null){
                        value = value.trim();
                    }
                }else if(Key.equalsIgnoreCase(type)){
                    //唯一键值校验
                    if(refIdKeys.contains(value)){
                        throw new NullPointerException();
                    }
                    refIdKeys.add(value);
                }

                map.put(keyList.get(i), value);
            }
        }catch(Exception e){
            System.err.println("excel:" + sheetName + "行列[" + n + "," + i + "]" + "数值异常, 数值：" + typeList.get(i));
            System.err.println(e.getMessage());
            throw new NullPointerException("excel:" + sheetName + "行列[" + n + "," + i + "]" + "数值异常, 数值：" + typeList.get(i));
        }

        return map;
    }

    /**
     * 表连表处理
     */
    public static Object getSheetJson(String sheetName, String activityId, String parentSheetName) throws Exception {
        List<Map<String, Object>> maps = excelToJson(activityId, sheetName, parentSheetName);
        return maps;
    }

    /**
     * 获取表格信息
     */
    public static List<Map<Integer, String>> getCellInfo(String fileName, String sheetName) throws Exception{
        FileInputStream input = new FileInputStream(fileName);
        Workbook workbook;
        if (fileName.endsWith(".xls")){
            workbook = new HSSFWorkbook(input);
        } else {
            workbook = new XSSFWorkbook(input);
        }
        //4.0版本  Workbook workbook = WorkbookFactory.create(input);
        Sheet sheet = workbook.getSheet(sheetName);

        List<Map<Integer, String>> list = new LinkedList<>();
        for (Row row : sheet) {
            Map<Integer, String> map = new LinkedHashMap<>();
            int i = 0;
            for(Cell cell:row){
                cell.setCellType(CellType.STRING);
                map.put(i, cell.getStringCellValue());
                i++;
            }

            list.add(map);
        }
        return list;
    }
}
