package com.ddkj.buffalo.util;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FileBootUtil {

    public static void writeFile(String urlName, String content) throws IOException {
        File file =new File(urlName);
        if(!file.exists()){
            file.createNewFile();
        }else{
            file.delete();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(urlName));
        out.write(content); // \r\n即为换行
        out.flush(); // 把缓存区内容压入文件
        out.close(); // 最后记得关闭文件
    }

    public static void createTempFile(String url, String fileName){
        ClassPathResource classPathResource = new ClassPathResource(url);//url == mybatis/xxx.xxx
        File somethingFile = null;
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
            try {
                somethingFile = File.createTempFile(fileName.split("\\.")[0],fileName.split("\\.").length == 1 ? "" : fileName.split("\\.")[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert somethingFile != null;
            FileUtils.copyInputStreamToFile(inputStream, somethingFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String getStringByInputStream(InputStream inputStream){
        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public static String getFileString(String url){
        try {
            BufferedReader in = new BufferedReader(new FileReader(url));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static String getFileStringLine(String url){
        try {
            FileInputStream fis = new FileInputStream(url);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
