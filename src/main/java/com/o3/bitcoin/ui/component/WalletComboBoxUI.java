/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.component;

import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class WalletComboBoxUI extends BasicComboBoxUI {
    private Color buttonBgColor = ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR;
    
    public WalletComboBoxUI() {
    }
    
    public WalletComboBoxUI(Color buttonBGColor) {
        this.buttonBgColor = buttonBGColor;
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new WalletComboBoxUI();
    }
    
    public static ComponentUI createUI(JComponent c, Color buttonBGColor) {
        return new WalletComboBoxUI(buttonBGColor);
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setRequestFocusEnabled(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setIcon(ResourcesProvider.getIcon("arrow"));
        button.setBackground(buttonBgColor);
        return button;
    }
}
