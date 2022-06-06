package com.ddkj.buffalo.controller;

import com.ddkj.buffalo.interfaces.IFunctionHandle;
import com.ddkj.buffalo.util.text.ResourceText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class StartController extends JFrame {
    @Resource
    List<IFunctionHandle> functionHandle;
    private JFrame w;
    //选项卡
    JTabbedPane tabbedPane; //总选项卡
    //顺序
    private static final String[] tablePanels = {
            "活动Json生成", "活动翻译转Java", "Json工具", "多语翻译", "工具说明"
    };

    private final JTable table = new JTable();   //表格

    public void initFrame() {
        System.out.println("大水牛启动服务开始");
        w = new JFrame("大水牛工具箱1.0");

        try {
            String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream csv = this.getClass().getResourceAsStream(ResourceText.icoUrl);
        ImageIcon imageIcon = new ImageIcon(toByteArray(csv));
        w.setIconImage(imageIcon.getImage());

        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置可关闭
        //w.setAlwaysOnTop(true);             //总在最前面
        w.setSize(400, 400);//设置大小
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.getContentPane().setLayout(new BorderLayout(0, 0));//流式自适应布局

        //处理内容
        Map<String, JPanel> map = new HashMap<>();
        for (String str : tablePanels) {
            map.put(str, new JPanel());
        }
        buildJTabbedPane(map);
        w.add(tabbedPane);//w.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        w.pack();
        w.setLocationRelativeTo(null);//设置居中
        w.setVisible(true);
        System.out.println("大水牛启动服务完成");
    }

    /**
     * 选项卡面板
     */
    private void buildJTabbedPane(Map<String, JPanel> map) {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 通过BorderFactory来设置边框的特性
        for (String panelName : tablePanels) {
            IFunctionHandle iFunctionHandle = functionHandle.stream().filter(e -> e.id().equals(panelName)).findFirst().orElse(null);
            if(iFunctionHandle == null){
                log.error("没有功能serviceName：{}", panelName);
                continue;
            }
            JPanel jPanel = map.get(panelName);
            iFunctionHandle.initPanel(jPanel, w, table, tabbedPane);
            tabbedPane.add(panelName, jPanel);
        }
    }
    //4 加解密操作框架


    public byte[] toByteArray(InputStream input) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (Exception e) {
            System.out.println();
        }
        return null;
    }
}
