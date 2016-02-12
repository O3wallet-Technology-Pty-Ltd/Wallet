/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.component;

import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author
 */
public class PlainTableHeaderRenderer extends JLabel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean hasFocus,
            boolean isSelected,
            int row,
            int col) {
        setText(value == null ? null : value.toString());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.TABLE_CELL_BORDER_COLOR));
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }
}
