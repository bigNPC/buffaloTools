package com.ddkj.buffalo.service;

import com.ddkj.buffalo.interfaces.IFunctionHandle;
import com.ddkj.buffalo.util.text.DefineText;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 工具说明面板
 */
@Configuration
public class RemarkService extends CommonPanelService implements IFunctionHandle {
    @Override
    public String id() {
        return "工具说明";
    }

    public JScrollPane describeJsp;
    public JButton describeBut;
    public JTextArea describe;


    @Override
    public void initPanel(JPanel descPanel, JFrame w, JTable table, JTabbedPane tabbedPane) {

        descPanel.setLayout(new BorderLayout());
        describeBut = new JButton("create by xzb version1.0");
        describeBut.setForeground(new Color(0xFFFFFF));
        describeBut.setBackground(new Color(0x78653C));

        describe = new JTextArea();
        describe.setForeground(new Color(0xFFFFFF));
        describe.setBackground(new Color(0x78653C));
        describe.setText(DefineText.RegexRemark);
        describe.setEditable(false);
        describe.setPreferredSize(new Dimension(500, 100));
        describe.setFont(new Font("黑体",Font.PLAIN, 15));

        describeJsp = new JScrollPane(describe);
        describeJsp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeTable(w,table,true);
            }
        });
        descPanel.add(describeJsp, BorderLayout.CENTER);

    }
}
