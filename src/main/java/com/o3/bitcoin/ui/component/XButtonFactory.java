/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;

/**
 *
 * @author
 */
public class XButtonFactory {

    public static final Dimension NORMAL_BUTTON_DIMENSION = new Dimension(90, 26);

    private final JButton button;

    public static XButtonFactory themedButton(JButton button) {
        return new XButtonFactory(button);
    }

    private XButtonFactory(JButton button) {
        this.button = button;
        init();
    }

    private XButtonFactory init() {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return this;
    }

    public XButtonFactory font(Font font) {
        button.setFont(font);
        return this;
    }

    public XButtonFactory color(Color color) {
        button.setForeground(color);
        return this;
    }

    public XButtonFactory background(Color color) {
        button.setBackground(color);
        return this;
    }

    public XButtonFactory size(Dimension dimension) {
        button.setPreferredSize(dimension);
        return this;
    }
}
