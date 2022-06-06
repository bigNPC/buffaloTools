package com.ddkj.buffalo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ddkj.buffalo.interfaces.IFunctionHandle;
import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.service.func.TranslateToJava;
import com.ddkj.buffalo.util.DebugUtil;
import com.ddkj.buffalo.util.FileBootUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 活动其它工具
 */
@Configuration
public class TimeToolsService extends CommonPanelService implements IFunctionHandle {
    @Override
    public String id() {
        return "活动翻译转Java";
    }
    String date = "";
    String timap = "";
    Boolean isStart = true;

    JPanel TransPanel;
    JPanel centerTransPanel;
    JPanel southTransPanel;
    //JTextArea beTrans;
    JTextArea hadTrans;

    //时间戳工具
    JPanel timePanel;
    JTextArea timapArea;
    JTextArea dateArea;
    JButton startOrstop;
    //活动翻译工具
    JPanel buttonPanel;
    JLabel activityArea;
    JTextField activityEnter;
    JButton startJavaButton;
    JButton openConfigButton;
    //json转excel
    JPanel jsonPanel;
    JLabel jsonLabel;
    JTextField jsonEnter;
    JPanel jsonButtonPanel;
    JButton startJsonButton;
    JButton openJsonButton;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    @Override
    public void initPanel(JPanel toolsPanel, JFrame w, JTable table, JTabbedPane tabbedPane) {
        toolsPanel.setLayout(new BorderLayout());
        creatTools();
        //时间框
        timePanel = new JPanel();
        timePanel.setLayout(new BorderLayout());
        timePanel.add(timapArea, BorderLayout.WEST);
        timePanel.add(dateArea, BorderLayout.CENTER);
        timePanel.add(startOrstop, BorderLayout.EAST);


        //活动Id
        createConfigTools();
        centerTransPanel = new JPanel();
        centerTransPanel.setLayout(new BorderLayout());
        centerTransPanel.add(activityArea, BorderLayout.WEST);
        centerTransPanel.add(activityEnter, BorderLayout.CENTER);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(openConfigButton, BorderLayout.NORTH);
        buttonPanel.add(startJavaButton, BorderLayout.CENTER);
        centerTransPanel.add(buttonPanel, BorderLayout.EAST);


        //活动Json转化
        createJsonTools();
        southTransPanel = new JPanel();
        southTransPanel.setLayout(new BorderLayout());
        jsonPanel = new JPanel();
        jsonPanel.setLayout(new BorderLayout());
        jsonPanel.add(jsonLabel, BorderLayout.WEST);
        jsonPanel.add(jsonEnter, BorderLayout.CENTER);
        jsonButtonPanel = new JPanel();
        jsonButtonPanel.setLayout(new BorderLayout());
        jsonButtonPanel.add(openJsonButton, BorderLayout.NORTH);
        jsonButtonPanel.add(startJsonButton, BorderLayout.CENTER);
        jsonPanel.add(jsonButtonPanel, BorderLayout.EAST);

        southTransPanel.add(jsonPanel, BorderLayout.NORTH);
        southTransPanel.add(hadTrans, BorderLayout.SOUTH);


        //整体
        TransPanel = new JPanel();
        TransPanel.setLayout(new BorderLayout());
        TransPanel.add(timePanel, BorderLayout.NORTH);
        TransPanel.add(centerTransPanel, BorderLayout.CENTER);
        TransPanel.add(southTransPanel, BorderLayout.SOUTH);
        toolsPanel.add(TransPanel, BorderLayout.CENTER);

    }

    private void createJsonTools() {
        jsonLabel = new JLabel();
        jsonLabel.setBackground(Color.orange);
        jsonLabel.setText("排名json转Excel,输入json文件名可指定,空则全部");
        jsonLabel.setFont(new Font("黑体", Font.PLAIN, 13));
        jsonEnter = new JTextField();
        jsonEnter.setBackground(Color.orange);
        jsonEnter.setText("");
        jsonEnter.setEditable(true);
        jsonEnter.setBounds(0, 0, 50, 200);
        jsonEnter.setFont(new Font("黑体", Font.PLAIN, 13));


        openJsonButton = new JButton("✿(打开源文件)✿");
        openJsonButton.setForeground(new Color(0xFFFFFF));
        openJsonButton.setBackground(new Color(0xDCD49F2C, true));
        openJsonButton.addActionListener(e -> {
            try {
                String url = System.getProperty("user.dir");
                url = url.substring(0, url.lastIndexOf(File.separator));
                Desktop.getDesktop().open(new File(url + "/config/rank/"));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                hadTrans.setText(e1.getMessage());
            }
        });

        startJsonButton = new JButton("✿(ﾟ◕开始转化◕ﾟ)✿");
        startJsonButton.setForeground(new Color(0xFFFFFF));
        startJsonButton.setBackground(new Color(0xDC852CD4, true));
        startJsonButton.addActionListener(e -> {
            try {
                hadTrans.setText("");
                String url = System.getProperty("user.dir");
                url = url.substring(0, url.lastIndexOf(File.separator));
                //获取所有文件
                if(StringUtils.isBlank(jsonEnter.getText())){
                    List<String> fileName = CommonServiceUtil.getFileUrls(url + "/config/rank");
                    fileName.removeIf(f->!f.endsWith(".json"));

                    Map<String, List<Map<String, Object>>> sheetMap = new LinkedHashMap<>();
                    StringBuilder sb = new StringBuilder();
                    for (String jsonUrl : fileName) {
                        String fileStringLine = FileBootUtil.getFileStringLine(jsonUrl);
                        JSONObject jsonObject = JSON.parseObject(fileStringLine);
                        String jsonArray = getJsonArray(jsonObject);
                        if(!"".equals(jsonArray)) {
                            JSONArray objects = JSONArray.parseArray(jsonArray);
                            List<Map<String, Object>> list = new LinkedList<>();
                            for (Object object : objects) {
                                Map map = JSONObject.parseObject(object.toString(), LinkedHashMap.class);
                                list.add(map);
                            }
                            jsonUrl = jsonUrl.replace(url + File.separator + "config"+ File.separator +"rank"+ File.separator, "");
                            jsonUrl = jsonUrl.split("\\.")[0];
                            sheetMap.put(jsonUrl, list);
                        }else{
                            sb.append(jsonUrl).append("不存在列表信息 \n");
                        }
                    }
                    hadTrans.setText(sb.toString());
                    CommonServiceUtil.writeListToExcelList(url + "/output/rank/rank.xlsx", sheetMap);
                    Desktop.getDesktop().open(new File(url + "/output/rank" ));
                }else{
                    String jsonUrl = url + "/config/rank/" + jsonEnter.getText() + ".json";
                    String fileStringLine = FileBootUtil.getFileStringLine(jsonUrl);
                    JSONObject jsonObject = JSON.parseObject(fileStringLine);
                    String jsonArray = getJsonArray(jsonObject);
                    if(!"".equals(jsonArray)){
                        JSONArray objects = JSONArray.parseArray(jsonArray);
                        List<Map<String, Object>> list = new LinkedList<>();
                        for(Object object:objects){
                            Map map = JSONObject.parseObject(object.toString(), LinkedHashMap.class);
                            list.add(map);
                        }
                        Map<String, List<Map<String, Object>>> sheetMap = new LinkedHashMap<>();
                        sheetMap.put(jsonEnter.getText(), list);
                        CommonServiceUtil.writeListToExcelList(url + "/output/rank/" + jsonEnter.getText() + ".xlsx", sheetMap);
                        Desktop.getDesktop().open(new File(url + "/output/rank" ));
                        hadTrans.setText(df.format(new Date()) + " 成功");
                    }else{
                        hadTrans.setText(jsonUrl + "不存在列表信息");
                    }
                }
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                hadTrans.setText(DebugUtil.printStack(e1));
            }
        });
    }
    /**
     * 获取第一个JsonArray
     */
    public String getJsonArray(JSONObject jsonObject){
        for(Object obj:jsonObject.values()){
            if(obj instanceof JSONArray){
                return obj.toString();
            }else if(obj instanceof JSONObject){
                return getJsonArray((JSONObject)obj);
            }
        }
        return "";
    }

    /**
     * 时间戳
     */
    public void creatTools() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        timapArea = new JTextArea();
        timapArea.setBackground(Color.orange);
        timapArea.setText("时间戳");
        timapArea.setEditable(true);
        timapArea.setFont(new Font("黑体", Font.PLAIN, 13));
        dateArea = new JTextArea();
        dateArea.setBackground(Color.orange);
        dateArea.setText("当前时间");
        dateArea.setEditable(true);
        dateArea.setFont(new Font("黑体", Font.PLAIN, 13));
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        if (isStart) {
                            timapArea.setText("时间戳：" + System.currentTimeMillis() / 1000);
                            dateArea.setText("当前日期：" + df.format(new Date()));
                            date = df.format(new Date());
                            timap = df.format(new Date());
                            Thread.sleep(1000);//暂停一秒
                        } else {
                            Thread.sleep(1000);//暂停一秒
                        }
                    }
                } catch (Exception e) {
                }
            }
        }.start();


        hadTrans = new JTextArea("", 10, 5);
        hadTrans.setForeground(new Color(0x000000));
        hadTrans.setText("");
        hadTrans.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        hadTrans.setPreferredSize(new Dimension(500, 100));


        startOrstop = new JButton("✿(ﾟ◕暂停◕ﾟ)✿");
        startOrstop.setForeground(new Color(0xFFFFFF));
        startOrstop.setBackground(new Color(0xDC852CD4, true));
        startOrstop.addActionListener(e -> {
            try {
                if (isStart) {
                    isStart = false;
                    startOrstop.setText("✿(ﾟ◕开始◕ﾟ)✿");
                } else {
                    isStart = true;
                    startOrstop.setText("✿(ﾟ◕暂停◕ﾟ)✿");
                }
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });
    }

    /**
     * 活动Java翻译
     */
    public void createConfigTools() {
        activityArea = new JLabel();
        activityArea.setBackground(Color.orange);
        activityArea.setText("活动翻译转java文件, 请输入活动Id");
        activityArea.setFont(new Font("黑体", Font.PLAIN, 13));
        activityEnter = new JTextField();
        activityEnter.setBackground(Color.orange);
        activityEnter.setText("");
        activityEnter.setEditable(true);
        activityEnter.setBounds(0, 0, 50, 200);
        activityEnter.setFont(new Font("黑体", Font.PLAIN, 13));

        openConfigButton = new JButton("✿(打开配置和翻译)✿");
        openConfigButton.setForeground(new Color(0xFFFFFF));
        openConfigButton.setBackground(new Color(0xDC2CD4B2, true));
        openConfigButton.addActionListener(e -> {
            try {
                String url = System.getProperty("user.dir");
                url = url.substring(0, url.lastIndexOf(File.separator));
                Desktop.getDesktop().open(new File(url + "/config/message"));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                hadTrans.setText(e1.getMessage());
            }
        });

        startJavaButton = new JButton("✿(ﾟ◕开始转化◕ﾟ)✿");
        startJavaButton.setForeground(new Color(0xFFFFFF));
        startJavaButton.setBackground(new Color(0xDC852CD4, true));
        startJavaButton.addActionListener(e -> {
            try {
                if("".equals(activityEnter.getText())){
                    hadTrans.setText("空，请输入字符");
                    return;
                }
                String url = System.getProperty("user.dir");
                url = url.substring(0, url.lastIndexOf(File.separator));
                String fileStringLine = FileBootUtil.getFileStringLine(url + "/config/message/ActivityMessageDefines.txt");
                String javaString = TranslateToJava.getJavaString(activityEnter.getText(), fileStringLine
                        , url + "/config/message/ActivityMessageDefines.properties");
                System.err.println(fileStringLine);
                System.err.println(javaString);
                //hadTrans.setText(javaString);
                TranslateToJava.genJavaString(url + File.separator + "output/message", TranslateToJava.getStartStringUp(activityEnter.getText()), javaString);
                Desktop.getDesktop().open(new File(url + "/output/message/" + TranslateToJava.getStartStringUp(activityEnter.getText()) + ".java"));
                hadTrans.setText(df.format(new Date()) + " 成功");
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                hadTrans.setText(e1.getMessage());
            }
        });


    }

    class listen implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            System.out.println("insertUpdate");
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            System.out.println("removeUpdate");
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            System.out.println("changedUpdate");
        }
    }
}
