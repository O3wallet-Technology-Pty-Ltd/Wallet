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
public class PlainTabelCellRenderer extends JLabel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
        } else {
            if (row % 2 == 0) {
                setForeground(Colors.TABLE_CELL_TEXT_COLOR);
                setBackground(Colors.TABLE_EVEN_ROW_BG_COLOR);
            } else {
                setForeground(Colors.TABLE_CELL_TEXT_COLOR);
                setBackground(Colors.TABLE_EVEN_ODD_BG_COLOR);
            }
        }
        setOpaque(true);
        setFont(Fonts.BOLD_SMALL_FONT);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.TABLE_CELL_BORDER_COLOR));
        setText(value == null ? null : value.toString());
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }

}
