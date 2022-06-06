package com.ddkj.buffalo.service.func.base;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class CommonServiceUtil {

    /**
     * 字符串获取数字
     */
    public static Integer getNumber(String sourceString){
        if(StringUtils.isEmpty(sourceString)){
            return null;
        }
        String reg = "[^0-9]";
        String sourceChina = sourceString.replaceAll(reg, "");
        if(sourceChina.equals("")){
            return null;
        }
        return Integer.parseInt(sourceChina);
    }

    /**
     * 字符串获取中文
     */
    public static String getChinese(String sourceString) {
        //剩下中文范围
        String reg = "[^\u4e00-\u9fa5|\n]";
        return sourceString.replaceAll(reg, "");
    }

    /**
     * 字符串获取中文
     */
    public static boolean hasChinese(String sourceString) {
        return sourceString.matches("[\\u4e00-\\u9fbf]+");
    }
    public static boolean hasJapan(String sourceString) {
        return sourceString.matches("[\\u0800-\\u4e00]+");
    }
    public static boolean hasKorea(String sourceString) {
        return sourceString.matches("[\\uac00-\\ud7ff]+");
    }
    public static boolean hasAr(String sourceString) {
        return sourceString.matches("[\\u0600-\\u06ff|\\u0750-\\u077f].*");
    }
    public static final String[] googleTrans = {"zh-cn", "ja", "ko", "en", "ar"};
    public static String getLanguage(String sourceString){
        if(hasChinese(sourceString)){
            return googleTrans[0];
        }else if(hasJapan(sourceString)){
            return googleTrans[1];
        }else if(hasKorea(sourceString)){
            return googleTrans[2];
        }else if(hasAr(sourceString)){
            return googleTrans[4];
        }else{
            return googleTrans[3];
        }
    }



    public static void genJava(String url, String className, String javaContent){
        try{
            File file = new File(url + File.separator + className + ".java");
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
        }catch(Exception e){
            System.err.println(e);
        }
    }

    public static void genJava2(String url, String className, String javaContent){
        try{
            File file = new File(url + File.separator + className + ".java");
            if(file.exists()){
                boolean delete = file.delete();
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }else{
                file.getParentFile().delete();
                file.getParentFile().mkdir();
            }
            boolean newFile = file.createNewFile();
            FileOutputStream fos = new FileOutputStream(url + File.separator + className + ".java");
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            osw.write(javaContent);
            osw.flush();
        }catch(Exception e){
            System.err.println(e);
        }
    }

    public static String getStartStringUp(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public static String getStartStringLow(String str){
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 写入JsonArrayExcel
     */
    public static void writeListToExcelList(String filePath, Map<String, List<Map<String, Object>>> sheetMap){
        File file = new File(filePath);
        if(file.exists()){
            boolean delete = file.delete();
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        //
        System.out.println("开始写入文件>>>>>>>>>>>>");
        Workbook workbook;
        if (filePath.toLowerCase().endsWith("xls")) {//2003
            workbook = new XSSFWorkbook();
        }else if(filePath.toLowerCase().endsWith("xlsx")){//2007
            workbook = new HSSFWorkbook();
        }else{
            return;
        }

        try{
            for(Map.Entry<String, List<Map<String, Object>>> sheetEntry : sheetMap.entrySet()){
                String sheetName = sheetEntry.getKey();
                List<Map<String, Object>> list = sheetEntry.getValue();
                Sheet sheet = workbook.createSheet(sheetName);
                //写表头数据
                LinkedList<String> strings = new LinkedList<>(list.get(0).keySet());
                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("index");
                for (int i = 0; i < strings.size(); i++) {
                    //创建表头单元格,填值
                    titleRow.createCell(i + 1).setCellValue(strings.get(i));
                }
                System.out.println("表头写入完成>>>>>>>>");

                //循环写入主表数据
                for(int i = 0; i < list.size(); i++){
                    Map<String, Object> map = list.get(i);
                    int index = 0;
                    Row row = sheet.createRow(i + 1);//标识位，用于标识sheet的行号
                    //增加序号
                    Cell cellIndex = row.createCell(index);
                    cellIndex.setCellValue(i + 1);
                    index++;
                    //列表值
                    for(Map.Entry<String, Object> entry : map.entrySet()){
                        Cell cell = row.createCell(index);
                        cell.setCellValue(String.valueOf(entry.getValue()));
                        index++;
                    }
                }
            }

            System.out.println("主表数据写入完成>>>>>>>>");
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
            System.out.println(filePath + "写入文件成功>>>>>>>>>>>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getProjectUrl(){
        String url = System.getProperty("user.dir");
        return url.substring(0, url.lastIndexOf(File.separator));
    }

    /**
     * 获取所有文件和子文件夹
     */
    public static List<String> getFileUrls(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return new ArrayList<>();
        }

        File[] fa = f.listFiles();
        if(fa == null){
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (File fs : fa) {
            if (!fs.isDirectory()) {
                list.add(fs.getAbsolutePath());
            }else{
                list.addAll(getFileUrls(fs.getAbsolutePath()));
            }
        }
        return list;
    }

    /**
     * 截取文件名
     */
    public static String getFileName(String path){
        int index = path.lastIndexOf(File.separator);
        return path.substring(index + 1);
    }

    /**
     * 获取桌面路劲
     */
    public static File getDeskTopPath(){
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getHomeDirectory();
    }
}
