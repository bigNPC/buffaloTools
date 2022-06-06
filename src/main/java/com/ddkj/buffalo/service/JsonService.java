package com.ddkj.buffalo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ddkj.buffalo.interfaces.IFunctionHandle;
import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.service.func.base.JsonGenFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Json相关(校验、格式化、压缩、生成)
 */
@Configuration
public class JsonService extends CommonPanelService implements IFunctionHandle {
    @Override
    public String id() {
        return "Json工具";
    }
    JPanel jsonPanel;
    JPanel northPanel;
    JPanel southPanel;
    JTextArea descTrans;
    JTextArea sourceString;
    JPanel butPanel;
    JPanel butCenterPanel;
    JButton checkBut;
    JButton formatBut;
    JButton shortBut;

    JPanel butGenPanel;
    JButton genJavaBut;
    JTextField genNameEnter;
    JTextField genPackageEnter;
    JButton copyBut;
    JTextArea printString;
    JScrollPane scrollPane;

    private static final String defaultGenNameTip = "请输入java文件名";
    private static final String defaultPackageTip = "请输入java包名";
    @Override
    public void initPanel(JPanel transPanel, JFrame w, JTable table, JTabbedPane tabbedPane) {
        transPanel.setLayout(new BorderLayout());
        creatTools();
        northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(descTrans, BorderLayout.NORTH);
        northPanel.add(scrollPane, BorderLayout.CENTER);
        butPanel = new JPanel();
        butPanel.add(checkBut, BorderLayout.WEST);
        butCenterPanel = new JPanel();
        butCenterPanel.add(formatBut, BorderLayout.WEST);
        butCenterPanel.add(shortBut, BorderLayout.CENTER);
        //构建Java
        butGenPanel = new JPanel();
        butGenPanel.setLayout(new BorderLayout());
        butGenPanel.add(genNameEnter, BorderLayout.NORTH);
        butGenPanel.add(genPackageEnter, BorderLayout.CENTER);
        butGenPanel.add(genJavaBut, BorderLayout.SOUTH);
        butCenterPanel.add(butGenPanel, BorderLayout.EAST);
        butPanel.add(butCenterPanel, BorderLayout.CENTER);
        butPanel.add(copyBut, BorderLayout.EAST);

        southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(butPanel, BorderLayout.NORTH);
        southPanel.add(printString, BorderLayout.CENTER);
        jsonPanel = new JPanel();
        jsonPanel.setLayout(new BorderLayout());
        jsonPanel.add(northPanel, BorderLayout.NORTH);
        jsonPanel.add(southPanel, BorderLayout.CENTER);
        transPanel.add(jsonPanel, BorderLayout.CENTER);

    }

    /**
     * 操作框架
     */
    public void creatTools() {
        descTrans = new JTextArea();
        descTrans.setBackground(Color.orange);
        descTrans.setText("请输入JSON····");
        descTrans.setEditable(false);
        descTrans.setFont(new Font("黑体", Font.PLAIN, 15));

        sourceString = new JTextArea("", 10, 5);
        sourceString.setForeground(new Color(0x665F5F));
        sourceString.setText("");
        sourceString.setFont(new Font("宋体", Font.PLAIN, 15));
        sourceString.setPreferredSize(new Dimension(500, 100));
        //TODO 添加滚动条
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(sourceString);

        printString = new JTextArea("", 10, 5);
        printString.setForeground(new Color(0x000000));
        printString.setText("");
        printString.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        printString.setPreferredSize(new Dimension(500, 100));


        checkBut = new JButton("\uD83C\uDF0FJSON检验\uD83C\uDF0F");
        checkBut.setForeground(new Color(0xFFFFFF));
        checkBut.setBackground(new Color(0xE02433));
        checkBut.addActionListener(e -> {
            try {
                printString.setText("");
                if (StringUtils.isBlank(sourceString.getText())) {
                    printString.setText("请输入字符");
                    return;
                }

                Object obj = JSON.parse(sourceString.getText());
                System.err.println(obj);
                printString.setText("正确");
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                printString.setText("非Json格式");
            }
        });
        formatBut = new JButton("\uD83C\uDFC2JSON格式化\uD83D\uDD4A");
        formatBut.setForeground(new Color(0xFFFFFF));
        formatBut.setBackground(new Color(0x5BB4B0));
        formatBut.addActionListener(e -> {
            try {
                printString.setText("");
                if (StringUtils.isBlank(sourceString.getText())) {
                    printString.setText("请输入字符");
                    return;
                }
                JSONObject object = JSONObject.parseObject(sourceString.getText());
                String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat);
                printString.setText(pretty);
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                printString.setText("非Json格式");
            }
        });
        shortBut = new JButton("\uD83E\uDD3DJSON压缩\uD83D\uDC01");
        shortBut.setForeground(new Color(0xFFFFFF));
        shortBut.setBackground(new Color(0xD79654));
        shortBut.addActionListener(e -> {
            try {
                printString.setText("");
                if (StringUtils.isBlank(sourceString.getText())) {
                    printString.setText("请输入字符");
                    return;
                }
                JSON.parse(sourceString.getText());
                printString.setText(replaceAllBlank(sourceString.getText()));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                printString.setText("非Json格式");
            }
        });

        genNameEnter = new JTextField();
        genNameEnter.setBackground(Color.orange);
        genNameEnter.setText("JavaEntity");
        genNameEnter.setEditable(true);
        genNameEnter.setFont(new Font("黑体", Font.PLAIN, 13));
        genPackageEnter = new JTextField();
        genPackageEnter.setBackground(Color.orange);
        genPackageEnter.setText("me.ddkj.dto.domain");
        genPackageEnter.setEditable(true);
        genPackageEnter.setFont(new Font("黑体", Font.PLAIN, 13));

        genJavaBut = new JButton("\uD83C\uDF20JSON转Java\uD83D\uDC7B");
        genJavaBut.setForeground(new Color(0xFFFFFF));
        genJavaBut.setBackground(new Color(0xD324E0));
        genJavaBut.addActionListener(e -> {
            try {
                printString.setText("");
                if (StringUtils.isBlank(sourceString.getText())) {
                    printString.setText("请输入字符");
                    return;
                }
                if (StringUtils.isBlank(genNameEnter.getText())) {
                    printString.setText("请输入java包名");
                    return;
                }
                String chinese = CommonServiceUtil.getChinese(genNameEnter.getText());
                String chinese2 = CommonServiceUtil.getChinese(genPackageEnter.getText());
                if(StringUtils.isNotBlank(chinese) || StringUtils.isNotBlank(chinese2)){
                    printString.setText("输入不支持中文");
                    return;
                }
                JSON.parse(sourceString.getText());
                JSONObject jsonObject = JSON.parseObject(sourceString.getText(), Feature.OrderedField);
                genJava(jsonObject, genNameEnter.getText(), genPackageEnter.getText());
                printString.setText("成功");
                Desktop.getDesktop().open(new File(JsonGen));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                printString.setText("非Json格式");
            } finally {
                files.clear();
            }
        });

        copyBut = new JButton("复制结果");
        copyBut.setForeground(new Color(0xFFFFFF));
        copyBut.setBackground(new Color(0xE06C24));
        copyBut.addActionListener(e -> {
            try {
                // 获取系统剪贴板
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                // 封装文本内容
                Transferable trans = new StringSelection(printString.getText());
                // 把文本内容设置到系统剪贴板
                clipboard.setContents(trans, null);
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                printString.setText("复制异常");
            }
        });
    }


    public static final String JsonGen = CommonServiceUtil.getProjectUrl() + File.separator + "output" + File.separator + "JsonGen" + File.separator;
    private static final List<JsonGenFile> files = new LinkedList<>();
    public void genJava(JSONObject jsonObject, String javaName, String packageName){
        parseJson(null, jsonObject, javaName);
        for(JsonGenFile file:files){
            String javaContent = getJavaContent(file.getName(), file.getNameTypes(), packageName, file.isHasList());
            CommonServiceUtil.genJava2(JsonGen + CommonServiceUtil.getStartStringUp(javaName), file.getName(), javaContent);
        }
        files.clear();
    }
    public static void parseJson(String parentName, JSONObject jsonObject, String javaName){
        Map<String, String> nameTypeMap = new LinkedHashMap<>();

        boolean hasList = false;
        for(Map.Entry<String, Object> entry : jsonObject.entrySet()){
            String key = entry.getKey();
            Object object = entry.getValue();
            if(object == null){
                //有字段默认String
                nameTypeMap.put(key, "String");
                continue;
            }
            if(object instanceof JSONObject){
                nameTypeMap.put(key, CommonServiceUtil.getStartStringUp(key));
                parseJson(javaName, (JSONObject)object, key);
            }else if(object instanceof JSONArray){
                nameTypeMap.put(key, "List<" + CommonServiceUtil.getStartStringUp(key) + ">");
                hasList = true;
                //获取json列表的最长的对象
                JSONObject maxFieldObject = getMaxFieldObject((JSONArray) object);
                if (maxFieldObject == null) return;
                parseJson(javaName, maxFieldObject, key);
            }else if (object instanceof String) {
                int index = object.getClass().getName().lastIndexOf(".");
                nameTypeMap.put(key, object.getClass().getName().substring(index + 1));
            }else if (object instanceof Integer) {
                int index = object.getClass().getName().lastIndexOf(".");
                nameTypeMap.put(key, object.getClass().getName().substring(index + 1));
            }else if (object instanceof Long) {
                int index = object.getClass().getName().lastIndexOf(".");
                nameTypeMap.put(key, object.getClass().getName().substring(index + 1));
            }else if (object instanceof BigDecimal) {
                nameTypeMap.put(key, "Double");
            }else if (object instanceof Boolean) {
                int index = object.getClass().getName().lastIndexOf(".");
                nameTypeMap.put(key, object.getClass().getName().substring(index + 1));
            }else{
               continue;
            }
        }
        if(CollectionUtils.isEmpty(nameTypeMap)){
            return;
        }
        files.add(new JsonGenFile(javaName, parentName,  nameTypeMap, hasList));
    }
    private static JSONObject getMaxFieldObject(JSONArray array) {
        int maxFieldLength = 0;
        Object maxFieldObject = null;
        for(Object object : array.toArray()){
            if(object == null){
                continue;
            }
            if(object.getClass().getDeclaredFields().length > maxFieldLength){
                maxFieldLength = object.getClass().getDeclaredFields().length;
                maxFieldObject = object;
            }
        }
        return (JSONObject)maxFieldObject;
    }
    public static String getJavaContent(String className, Map<String, String> nameTypeMap, String packageName, boolean hasList){
        List<String> list = new ArrayList<>();
        for(Map.Entry<String,String> entry : nameTypeMap.entrySet()){
            String typeAndString = "\t" + "private " + entry.getValue() + " " + entry.getKey() + ";" + "\n\n";
            list.add(typeAndString);
        }

        StringBuilder sb = new StringBuilder();
        if(StringUtils.isEmpty("")){
            sb.append("package "+ packageName +";\n\n");
        }
        sb.append(
                "import lombok.Data;\n" +
                        "import lombok.EqualsAndHashCode;\n" +
                        "import java.io.Serializable;\n"
        );
        if(hasList){
            sb.append("import java.util.*;\n");
        }
        sb.append("\n" +
                "@Data\n" +
                "@EqualsAndHashCode(callSuper = false)\n" +
                "public class " + CommonServiceUtil.getStartStringUp(className) + " implements Serializable {\n\n");
        for(String str:list){
            sb.append(str);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 使用正则表达式删除字符串中的空格、回车、换行符、制表符
     */
    public static String replaceAllBlank(String str) {
        String dest = "";
        if (StringUtils.isNotBlank(str)) {
            Matcher m = Pattern.compile("\\s{2,}|\t|\r|\n").matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
