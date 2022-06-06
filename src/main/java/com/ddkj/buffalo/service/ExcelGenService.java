package com.ddkj.buffalo.service;

import com.alibaba.fastjson.JSON;
import com.ddkj.buffalo.interfaces.IFunctionHandle;
import com.ddkj.buffalo.service.func.ExcelConfigToJson;
import com.ddkj.buffalo.service.func.ExcelRewardToJson;
import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.util.DebugUtil;
import com.ddkj.buffalo.util.FileBootUtil;
import com.ddkj.buffalo.util.encryp.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动Json生成
 */
@Configuration
public class ExcelGenService extends CommonPanelService implements IFunctionHandle {
    @Override
    public String id() {
        return "活动Json生成";
    }

    JPanel northPanel;
    JPanel centerPanel;
    JPanel southPanel;
    JTextArea resultArea;

    //MD5
    JPanel timePanel;
    JPanel md5Panel;
    JTextArea MD5TipsArea;
    JTextArea MD5Area;
    JCheckBox checkBox;
    JButton startMD5Button;
    //活动配置
    JPanel selectEnterPanel;
    JLabel activityArea;
    JPanel boxPanel;
    JComboBox selectConfigBox;
    JCheckBox checkConfigBox;
    JPanel configButtonPanel;
    JButton startJavaButton;
    JButton openConfigButton;
    //json转excel
    JPanel jsonPanel;
    JLabel jsonLabel;
    JPanel boxRewardPanel;
    JComboBox selectRewardBox;
    JCheckBox checkRewardBox;
    JTextField jsonRewardEnter;
    JPanel rewardButtonPanel;
    JButton startRewardButton;
    JButton openRewardButton;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private final String defaultTimeTip = "请输入活动时间";

    @Override
    public void initPanel(JPanel toolsPanel, JFrame w, JTable table, JTabbedPane tabbedPane) {
        toolsPanel.setLayout(new BorderLayout());
        creatMD5Tools();
        //时间框
        timePanel = new JPanel();
        timePanel.setLayout(new BorderLayout());
        md5Panel = new JPanel();
        md5Panel.setLayout(new BorderLayout());
        md5Panel.add(checkBox, BorderLayout.WEST);
        md5Panel.add(MD5Area, BorderLayout.CENTER);
        timePanel.add(MD5TipsArea, BorderLayout.WEST);
        timePanel.add(md5Panel, BorderLayout.CENTER);
        timePanel.add(startMD5Button, BorderLayout.EAST);


        //活动Id
        createConfigTools();
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(activityArea, BorderLayout.WEST);


        boxPanel = new JPanel();
        boxPanel.setLayout(new BorderLayout());
        boxPanel.add(checkConfigBox, BorderLayout.NORTH);
        boxPanel.add(selectConfigBox, BorderLayout.CENTER);
        centerPanel.add(boxPanel, BorderLayout.CENTER);

        selectEnterPanel = new JPanel();
        selectEnterPanel.setLayout(new BorderLayout());
        selectEnterPanel.add(openConfigButton, BorderLayout.NORTH);
        selectEnterPanel.add(startJavaButton, BorderLayout.CENTER);
        centerPanel.add(selectEnterPanel, BorderLayout.EAST);


        //活动Json转化
        createRewardTools();
        southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        jsonPanel = new JPanel();
        jsonPanel.setLayout(new BorderLayout());
        jsonPanel.add(jsonLabel, BorderLayout.WEST);

        boxPanel = new JPanel();
        boxPanel.setLayout(new BorderLayout());
        boxPanel.add(checkRewardBox, BorderLayout.NORTH);
        boxPanel.add(jsonRewardEnter, BorderLayout.CENTER);
        boxPanel.add(selectRewardBox, BorderLayout.SOUTH);
        jsonPanel.add(boxPanel, BorderLayout.CENTER);

        rewardButtonPanel = new JPanel();
        rewardButtonPanel.setLayout(new BorderLayout());
        rewardButtonPanel.add(openRewardButton, BorderLayout.NORTH);
        rewardButtonPanel.add(startRewardButton, BorderLayout.CENTER);
        jsonPanel.add(rewardButtonPanel, BorderLayout.EAST);

        southPanel.add(jsonPanel, BorderLayout.NORTH);
        southPanel.add(resultArea, BorderLayout.SOUTH);


        //整体
        northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(timePanel, BorderLayout.NORTH);
        northPanel.add(centerPanel, BorderLayout.CENTER);
        northPanel.add(southPanel, BorderLayout.SOUTH);
        toolsPanel.add(northPanel, BorderLayout.CENTER);

    }

    /**
     * MD5
     */
    public void creatMD5Tools() {
        MD5TipsArea = new JTextArea();
        MD5TipsArea.setBackground(Color.orange);
        MD5TipsArea.setText("MD5转换器：");
        MD5TipsArea.setEditable(false);
        MD5TipsArea.setFont(new Font("黑体", Font.PLAIN, 13));

        MD5Area = new JTextArea();
        MD5Area.setBackground(Color.orange);
        MD5Area.setText("");
        MD5Area.setEditable(true);
        MD5Area.setFont(new Font("黑体", Font.PLAIN, 13));
        //
        checkBox = new JCheckBox();
        checkBox.setBackground(new Color(0xFF1782CE, true));
        checkBox.setText("大写");
        checkBox.setFont(new Font("黑体", Font.PLAIN, 13));


        resultArea = new JTextArea("", 10, 5);
        resultArea.setForeground(new Color(0x000000));
        resultArea.setText("");
        resultArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        resultArea.setPreferredSize(new Dimension(500, 100));


        startMD5Button = new JButton("✿(ﾟ◕生成◕ﾟ)✿");
        startMD5Button.setForeground(new Color(0xFFFFFF));
        startMD5Button.setBackground(new Color(0xDC852CD4, true));
        startMD5Button.addActionListener(e -> {
            try {
                if("".equals(MD5Area.getText())){
                    resultArea.setText("空，请输入字符");
                    return;
                }
                if(checkBox.isSelected()){
                    resultArea.setText(MD5Utils.MD5Upper(MD5Area.getText()));
                }else{
                    resultArea.setText(MD5Utils.MD5Lower(MD5Area.getText()));
                }

            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                resultArea.setText(e1.getMessage());
            }
        });
    }


    /**
     * 活动配置转化
     */
    public void createConfigTools() {
        activityArea = new JLabel();
        activityArea.setBackground(Color.orange);
        activityArea.setText("活动Excel转json文件, 请选择活动Id");
        activityArea.setFont(new Font("黑体", Font.PLAIN, 13));

        checkConfigBox = new JCheckBox();
        checkConfigBox.setBackground(new Color(0xFF1782CE, true));
        checkConfigBox.setText("继承通用");
        checkConfigBox.setFont(new Font("黑体", Font.PLAIN, 13));

        try{
            List<String> fileName = CommonServiceUtil.getFileUrls(ExcelConfigToJson.excelConfigSrc);
            fileName.removeIf(e->!e.endsWith(".xlsx"));
            fileName = fileName.stream().map(e -> CommonServiceUtil.getFileName(e).split("\\.")[0]).collect(Collectors.toList());
            selectConfigBox = new JComboBox();
            selectConfigBox.setBackground(Color.orange);
            for(String str:fileName){
                selectConfigBox.addItem(str);
            }
            if(fileName.size() > 0){
                selectConfigBox.setSelectedItem(fileName.get(0));
            }
            selectConfigBox.setFont(new Font("黑体", Font.PLAIN, 13));
        }catch(Exception e){
            resultArea.setText(DebugUtil.printStack(e));
        }

        openConfigButton = new JButton("✿(打开活动配置)✿");
        openConfigButton.setForeground(new Color(0xFFFFFF));
        openConfigButton.setBackground(new Color(0xDC2CD4B2, true));
        openConfigButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(ExcelConfigToJson.excelConfigSrc));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                resultArea.setText(e1.getMessage());
            }
        });

        startJavaButton = new JButton("✿(ﾟ◕开始转化◕ﾟ)✿");
        startJavaButton.setForeground(new Color(0xFFFFFF));
        startJavaButton.setBackground(new Color(0xDC852CD4, true));
        startJavaButton.addActionListener(e -> {
            try {
                String activityId = selectConfigBox.getSelectedItem().toString();
                boolean extend = checkConfigBox.isSelected();          //是否继承通用组件
                ExcelConfigToJson.clear();
                List<Map<String, Object>> main = ExcelConfigToJson.excelToJson(activityId, ExcelConfigToJson.MainSheetName, null);
                //生成Java文件
                ExcelConfigToJson.gen(activityId, extend);
                //生成json文件
                FileBootUtil.writeFile(ExcelConfigToJson.excelGenConfigJson + activityId + ".json", JSON.toJSONString(main.get(0)));
                resultArea.setText(df.format(new Date()) + " 成功");
                Desktop.getDesktop().open(new File(ExcelConfigToJson.excelGenConfigJson));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                resultArea.setText(e1.getMessage());
            }
        });


    }

    /**
     * 活动奖励配置
     */
    private void createRewardTools() {
        jsonLabel = new JLabel();
        jsonLabel.setBackground(Color.orange);
        jsonLabel.setText("奖励Excel转json，请选择活动id：");
        jsonLabel.setFont(new Font("黑体", Font.PLAIN, 13));
        jsonRewardEnter = new JTextField();
        jsonRewardEnter.setBackground(Color.orange);
        jsonRewardEnter.setText(defaultTimeTip);
        jsonRewardEnter.setEditable(true);
        jsonRewardEnter.setBounds(0, 0, 50, 200);
        jsonRewardEnter.setFont(new Font("黑体", Font.PLAIN, 13));
        jsonRewardEnter.addFocusListener(new myFocusListener());
        //jsonRewardEnter.getDocument().addDocumentListener(new listen());

        checkRewardBox = new JCheckBox();
        checkRewardBox.setBackground(new Color(0xFF1782CE, true));
        checkRewardBox.setText("礼物多排行");
        checkRewardBox.setFont(new Font("黑体", Font.PLAIN, 13));

        try{
            List<String> fileName = CommonServiceUtil.getFileUrls(ExcelConfigToJson.excelRewardSrc);
            fileName.removeIf(e->!e.endsWith(".xlsx"));
            fileName = fileName.stream().map(e -> CommonServiceUtil.getFileName(e).split("\\.")[0]).collect(Collectors.toList());
            selectRewardBox = new JComboBox();
            selectRewardBox.setBackground(Color.orange);
            for(String str:fileName){
                selectRewardBox.addItem(str);
            }
            if(fileName.size() > 0){
                selectRewardBox.setSelectedItem(fileName.get(0));
            }
            selectRewardBox.setFont(new Font("黑体", Font.PLAIN, 13));
        }catch(Exception e){
            resultArea.setText(DebugUtil.printStack(e));
        }


        openRewardButton = new JButton("✿(打开源文件)✿");
        openRewardButton.setForeground(new Color(0xFFFFFF));
        openRewardButton.setBackground(new Color(0xDCD49F2C, true));
        openRewardButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(ExcelConfigToJson.excelRewardSrc));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                resultArea.setText(e1.getMessage());
            }
        });

        startRewardButton = new JButton("✿(ﾟ◕开始转化◕ﾟ)✿");
        startRewardButton.setForeground(new Color(0xFFFFFF));
        startRewardButton.setBackground(new Color(0xDC852CD4, true));
        startRewardButton.addActionListener(e -> {
            try {
                resultArea.setText("");
                String activityId = selectRewardBox.getSelectedItem().toString();
                String time = jsonRewardEnter.getText();
                boolean isGiftIdsRank = checkRewardBox.isSelected();          //是否是多礼物排行

                ExcelRewardToJson.genRewardJson(activityId, isGiftIdsRank, StringUtils.isBlank(time) ? null : time);
                resultArea.setText(df.format(new Date()) + " 成功");
                Desktop.getDesktop().open(new File(ExcelConfigToJson.excelGenRewardJson));
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                resultArea.setText(DebugUtil.printStack(e1));
            }
        });
    }



    class listen implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if(defaultTimeTip.equals(jsonRewardEnter.getText())){
                jsonRewardEnter.setText("");
            }
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
    class myFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent arg0) {
            if(defaultTimeTip.equals(jsonRewardEnter.getText())){
                jsonRewardEnter.setText("");
            }
        }
        @Override
        public void focusLost(FocusEvent arg0) {
        }
    }

}
