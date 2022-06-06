package com.ddkj.buffalo.service;

import com.ddkj.buffalo.dev.TranslateYouDao;
import com.ddkj.buffalo.dev.TranslatorGoogleUtil;
import com.ddkj.buffalo.interfaces.IFunctionHandle;
import com.ddkj.buffalo.service.func.base.CommonServiceUtil;
import com.ddkj.buffalo.util.DebugUtil;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * 翻译面板
 */
@Configuration
public class TranslateService extends CommonPanelService implements IFunctionHandle {
    @Override
    public String id() {
        return "多语翻译";
    }
    JPanel TransPanel;
    JPanel northTransPanel;
    JPanel southTransPanel;
    JTextArea descTrans;
    JTextArea beTrans;
    JTextArea hadTrans;
    JPanel northLabelPanel;
    JComboBox selectRewardBox;
    JCheckBox checkRewardBox;
    JButton searchTransBut;
    private static final String defaultString = "请输入你要翻译的文字";
    @Override
    public void initPanel(JPanel transPanel, JFrame w, JTable table, JTabbedPane tabbedPane) {
        transPanel.setLayout(new BorderLayout());
        creatTranslate();
        northTransPanel = new JPanel();
        northTransPanel.setLayout(new BorderLayout());
        northTransPanel.add(descTrans, BorderLayout.WEST);
        northLabelPanel = new JPanel();
        northLabelPanel.add(checkRewardBox, BorderLayout.NORTH);
        northLabelPanel.add(selectRewardBox, BorderLayout.CENTER);
        northLabelPanel.add(searchTransBut, BorderLayout.EAST);

        northTransPanel.add(northLabelPanel, BorderLayout.CENTER);
        southTransPanel = new JPanel();
        southTransPanel.setLayout(new BorderLayout());
        southTransPanel.add(beTrans, BorderLayout.NORTH);
        southTransPanel.add(hadTrans, BorderLayout.CENTER);
        TransPanel = new JPanel();
        TransPanel.setLayout(new BorderLayout());
        TransPanel.add(northTransPanel, BorderLayout.NORTH);
        TransPanel.add(southTransPanel, BorderLayout.CENTER);
        transPanel.add(TransPanel, BorderLayout.CENTER);

    }

    /**
     * 翻译操作框架
     */
    public void creatTranslate() {
        descTrans = new JTextArea();
        descTrans.setBackground(Color.orange);
        descTrans.setText("自动检测语言····");
        descTrans.setEditable(false);
        descTrans.setFont(new Font("黑体", Font.PLAIN, 15));

        beTrans = new JTextArea("", 10, 5);
        beTrans.setForeground(new Color(0x665F5F));
        beTrans.setText(defaultString);//如果焦点切内容不重复发送  1.5秒自动翻译
        beTrans.setFont(new Font("宋体", Font.PLAIN, 15));
        beTrans.setPreferredSize(new Dimension(500, 100));
        beTrans.addFocusListener(new myFocusListener());

        hadTrans = new JTextArea("", 10, 5);
        hadTrans.setForeground(new Color(0x000000));
        hadTrans.setText("");
        hadTrans.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        hadTrans.setPreferredSize(new Dimension(500, 100));

        checkRewardBox = new JCheckBox();
        checkRewardBox.setBackground(new Color(0xFF1782CE, true));
        checkRewardBox.setText("有道翻译");
        checkRewardBox.setFont(new Font("黑体", Font.PLAIN, 13));
        try{
            selectRewardBox = new JComboBox();
            selectRewardBox.setBackground(Color.orange);
            for(String str:CommonServiceUtil.googleTrans){
                selectRewardBox.addItem(str);
            }
            selectRewardBox.setSelectedItem(CommonServiceUtil.googleTrans[3]);
            selectRewardBox.setFont(new Font("黑体", Font.PLAIN, 13));
        }catch(Exception e){
            beTrans.setText(DebugUtil.printStack(e));
        }

        searchTransBut = new JButton("♡((o(*ﾟ翻译ﾟ*)o))♡");
        searchTransBut.setForeground(new Color(0xFFFFFF));
        searchTransBut.setBackground(new Color(0xE02433));
        searchTransBut.addActionListener(e -> {
            try {
                hadTrans.setText("");
                if("".equals(beTrans.getText())){
                    hadTrans.setText("空，请输入字符");
                    return;
                }
                if(checkRewardBox.isSelected()){
                    String reStr = TranslateYouDao.tranlate(beTrans.getText());
                    hadTrans.setText(reStr);
                }else{
                    String reStr = TranslatorGoogleUtil.translate(beTrans.getText(), selectRewardBox.getSelectedItem().toString());
                    hadTrans.setText(reStr);
                }
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
                hadTrans.setText(DebugUtil.printStack(e1));
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
            beTrans.setForeground(new Color(0x534C3D));
            beTrans.setFont(new Font("黑体", Font.BOLD, 15));

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            System.out.println("changedUpdate");
        }
    }
    class myFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent arg0) {
            if(defaultString.equals(beTrans.getText())){
                beTrans.setText("");
            }
        }
        @Override
        public void focusLost(FocusEvent arg0) {
        }
    }
}
