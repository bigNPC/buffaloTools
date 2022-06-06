package com.ddkj.buffalo.util.other;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class excelTool {
    //文件目录递归调用
    public static void changeFileName(String path,List<String> list){
        File file = new File(path);
        if(file.exists()){
            File[] files = file.listFiles();//当前目录的list文件
            if (files==null || files.length < 1) {
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        changeFileName(file2.getAbsolutePath(),list);
                    } else {
                        list.add(file2.getAbsolutePath());
                    }
                }
            }
        }else{
            System.out.println("该路径不存在");
        }
    }


    public static void loadExcel(String content,String url) throws Exception{
        FileInputStream in = new FileInputStream(url);
        Workbook wk = StreamingReader.builder()
                .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                .bufferSize(4096)   //读取资源时，缓存到内存的字节大小，默认是1024
                .open(in);          //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

        int num= wk.getNumberOfSheets();int recent=0;
        while (recent <num){
            Sheet sheet = wk.getSheetAt(recent);
            //遍历所有的行 row.getRowNum()
            int i=1;
            for (Row row : sheet) {
                int j=1;
                for (Cell cell : row) {//遍历所有的列
                    if (j==1 && !"1".equalsIgnoreCase(cell.getStringCellValue()))//true 变成1。。。
                        break;
                    if (cell.getStringCellValue().contains(content))
                        System.out.println("Sheet名："+wk.getSheetName(recent)+","+i+"行"+j+"列"+",内容："+cell.getStringCellValue());
                    j++;
                }
                i++;
            }
            recent++;
        }
    }

    public static void main(String[] args) throws Exception {
        long start= System.currentTimeMillis();
        String content= "一百万";
        List<String> fileList = new ArrayList<>();
        changeFileName("C:\\Users\\Administrator\\Desktop\\突击",fileList);
        //过滤非xlsx文件\
        List<String> files = new ArrayList<>();
        for(String s:fileList){
            if (s.endsWith(".xlsx"))
                files.add(s);
        }
        if(files.size()<1){
            //弹出消息框
        }
        for(String path:files){
            loadExcel(content,path);
        }
        System.out.println("耗时"+(System.currentTimeMillis()-start)/1000+"秒");

    }



    public static void _a(){
//        File f = new File("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt");
        long start= System.currentTimeMillis();
        String jsonString = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt")), "UTF-8"));// 读取文件
            String thisLine = null;
            while ((thisLine = in.readLine()) != null) {
                jsonString += thisLine;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {   in.close();
                } catch (IOException el) {
                }
        }
        System.out.println(jsonString.contains("使用可激活2元充值奖"));
//        Pattern p = Pattern.compile("使用可激活2元充值奖");
//        Matcher m = p.matcher(jsonString);
//        while (m.find()) {
//            System.out.println("包含这个字bai符串！");
//        }
        System.out.println("耗时"+(System.currentTimeMillis()-start)+"秒");
        //效率
        //while>for>fore>iterator>fore
        //contain > Pattern > indexof
    }
}
