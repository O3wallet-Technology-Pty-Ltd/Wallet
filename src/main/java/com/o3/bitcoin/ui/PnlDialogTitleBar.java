/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui;

import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Dimensions;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author
 */

/**
 * <p> Class that creates title bar of different dialogs shown in application</p>
*/
public class PnlDialogTitleBar extends javax.swing.JPanel {

    private int x;
    private int y;
    private JDialog parent;
    private ActionListener listener = null;

    /**
     * Creates new form PnlDialogTitleBar
     */
    public PnlDialogTitleBar() {
        this(null);
    }

    /**
     * Creates new form PnlDialogTitleBar
     *
     * @param parent 
     */
    public PnlDialogTitleBar(JDialog parent) {
        initComponents();
        this.parent = parent;
    }

    public void setParent(JDialog parent) {
        this.parent = parent;
    }

    public void setHeading(String heading) {
        if (heading == null) {
            lblHeading.setText("");
        } else {
            lblHeading.setText(heading);
        }
    }

    public void setTitle(String title) {
        if (title == null) {
            lblTitle.setText("");
        } else {
            lblTitle.setText(title);
        }
    }

    public void setHeadingFont(Font font) {
        lblHeading.setFont(font);
    }

    public void setAdditionalHeaderPanel(JPanel additioanlHeaderPanel) {
        pnlHeader.add(additioanlHeaderPanel, BorderLayout.EAST);
    }

    public void setHeaderPanelPreferredSize(Dimension dimension) {
        pnlHeader.setPreferredSize(dimension);
    }

    private void handleCloseDialogEvent() {
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, 0, "CLOSED_EVENT"));
        }
    }

    public void setDefaultCloseActionListener(ActionListener listener) {
        this.listener = listener;
    }

    public void unsetDefaultCloseActionListener() {
        this.listener = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();
        lblClose = new javax.swing.JLabel();
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlHeader = new javax.swing.JPanel();
        lblHeading = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        pnlTop.setBackground(Colors.TITLE_BAR_BG_COLOR);
        pnlTop.setPreferredSize(new Dimension(1024, Dimensions.TITLE_BAR_HEIGHT));
        pnlTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlTopMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlTopMousePressed(evt);
            }
        });
        pnlTop.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlTopMouseDragged(evt);
            }
        });
        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlControls.setOpaque(false);
        pnlControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        lblClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png"))); // NOI18N
        lblClose.setToolTipText("Close");
        lblClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblCloseMousePressed(evt);
            }
        });
        pnlControls.add(lblClose);

        pnlTop.add(pnlControls, java.awt.BorderLayout.CENTER);

        pnlTitle.setForeground(Color.WHITE);
        pnlTitle.setOpaque(false);

        lblTitle.setFont(Fonts.BOLD_SMALL_FONT);
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setText("<Title>");
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        pnlTitle.add(lblTitle);

        pnlTop.add(pnlTitle, java.awt.BorderLayout.WEST);

        add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlHeader.setBackground(Colors.TITLE_BAR_NAV_BG_COLOR);
        pnlHeader.setPreferredSize(new java.awt.Dimension(55, 35));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblHeading.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        lblHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading.setText("<Heading>");
        lblHeading.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        pnlHeader.add(lblHeading, java.awt.BorderLayout.CENTER);

        add(pnlHeader, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void lblCloseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMousePressed
        handleCloseDialogEvent();
    }//GEN-LAST:event_lblCloseMousePressed

    private void pnlTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopMouseClicked
        //do nothing ...
    }//GEN-LAST:event_pnlTopMouseClicked

    private void pnlTopMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopMousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_pnlTopMousePressed

    private void pnlTopMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopMouseDragged
        parent.setLocation(evt.getXOnScreen() - x, evt.getYOnScreen() - y);
    }//GEN-LAST:event_pnlTopMouseDragged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblClose;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlTop;
    // End of variables declaration//GEN-END:variables
}
