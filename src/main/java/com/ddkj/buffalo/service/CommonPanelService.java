package com.ddkj.buffalo.service;


import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public abstract class CommonPanelService {

    /**
     * 界面拉伸
     */
    public void resizeTable(JFrame w, JTable table, boolean bool) {
        Dimension containerwidth;
        if (!bool) {
            // 初始化时，父容器大小为首选大小，实际大小为0
            containerwidth = w.getPreferredSize();
        } else {
            // 界面显示后，如果父容器大小改变，使用实际大小而不是首选大小
            containerwidth = w.getSize();
        }
        // 计算表格总体宽度 getTable().
        if(table == null){
            return;
        } else {
            table.getIntercellSpacing();
        }
        int allwidth = table.getIntercellSpacing().width;
        for (int j = 0; j < table.getColumnCount(); j++) {
            // 计算该列中最长的宽度
            int max = 0;
            for (int i = 0; i < table.getRowCount(); i++) {
                int width = table
                        .getCellRenderer(i, j)
                        .getTableCellRendererComponent(table,
                                table.getValueAt(i, j), false, false, i, j)
                        .getPreferredSize().width;
                if (width > max) {
                    max = width;
                }
            }
            // 计算表头的宽度
            int headerwidth = table
                    .getTableHeader()
                    .getDefaultRenderer()
                    .getTableCellRendererComponent(
                            table,
                            table.getColumnModel().getColumn(j)
                                    .getIdentifier(), false, false, -1, j)
                    .getPreferredSize().width;
            // 列宽至少应为列头宽度
            max += headerwidth;
            // 设置列宽
            table.getColumnModel().getColumn(j).setPreferredWidth(max);
            // 给表格的整体宽度赋值，记得要加上单元格之间的线条宽度1个像素
            allwidth += max + table.getIntercellSpacing().width;
        }
        allwidth += table.getIntercellSpacing().width;
        // 如果表格实际宽度大小父容器的宽度，则需要我们手动适应；否则让表格自适应
        if (allwidth > containerwidth.width) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        }
    }


}
