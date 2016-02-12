/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.component;

import com.o3.bitcoin.ui.DirectionRatio;
import com.o3.bitcoin.ui.ScaleDescriptor;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author
 */
public class XScalableImage extends JPanel {

    private Image image;
    private String imageKey;

    public XScalableImage() {

    }

    public XScalableImage(String imageKey) {
        this.imageKey = imageKey;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            ScaleDescriptor sd = Utils.createScaleDescriptor(this);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            double directionRatio = sd != null ? sd.getValueByRatio(DirectionRatio.MIN_DIRECTION_RATIO) : 1.0;
            int preferredWidth = (int) ((double) width * directionRatio);
            int preferredHeight = (int) ((double) height * directionRatio);
            image = image.getScaledInstance(preferredWidth, preferredHeight, 4);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(image, 0, 0, null);
        }
        super.paintComponent(g);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
        setImage(ResourcesProvider.getImage(imageKey));
    }
}
