/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.component;

/**
 *
 * @author
 */
import com.o3.bitcoin.ui.DirectionRatio;
import com.o3.bitcoin.ui.ScaleDescriptor;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import java.awt.*;
import javax.swing.*;

public class XScalableLabel extends JLabel {

    protected Font primaryFont;
    protected ImageIcon primaryIcon;
    protected DirectionRatio directionratio;
    protected int vGap;

    public XScalableLabel() {
        this(null);
    }

    public XScalableLabel(String text) {
        this(text, null, 0);
    }

    public XScalableLabel(String text, String iconKey, int iconSize) {
        this(text, iconKey, iconSize, JLabel.LEFT);
    }

    public XScalableLabel(String text, int horizontalAlignment) {
        this(text, null, 0, horizontalAlignment);
    }

    public XScalableLabel(String text, String iconKey, int iconSize, int horizontalAlignment) {
        directionratio = DirectionRatio.MIN_DIRECTION_RATIO;
        vGap = -1;
        if (text != null) {
            setText(text);
        }
        setHorizontalAlignment(horizontalAlignment);
        if (iconKey != null) {
            primaryIcon = ResourcesProvider.getIcon(iconKey, iconSize);
            setIcon(primaryIcon);
        }
    }

    public void setvGap(int vGap) {
        this.vGap = vGap;
    }

    public int getvGap() {
        return vGap;
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        if (primaryIcon == null) {
            primaryIcon = (ImageIcon) icon;
        }
    }

    public Font getPrimaryFont() {
        return primaryFont;
    }

    @Override
    public void setFont(Font font) {
        primaryFont = font;
        super.setFont(font);
    }

    public void setDirectionratio(DirectionRatio directionratio) {
        this.directionratio = directionratio;
    }

    @Override
    public void paintComponent(Graphics g) {
        ScaleDescriptor sd = Utils.createScaleDescriptor(this);
        Font newFont = Utils.deliverFontForComponent(primaryFont, sd, directionratio);
        super.setFont(newFont);
        if (primaryIcon != null) {
            int iconWigth = primaryIcon.getIconWidth();
            int iconHeight = primaryIcon.getIconHeight();
            double directionRatio = sd != null ? sd.getValueByRatio(directionratio) : 1.0;
            int newIconWidth = (int) ((double) iconWigth * directionRatio);
            int newIconHeight = (int) ((double) iconHeight * directionRatio);
            super.setIcon(new ImageIcon(primaryIcon.getImage().getScaledInstance(newIconWidth, newIconHeight, 4)));
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }
}
