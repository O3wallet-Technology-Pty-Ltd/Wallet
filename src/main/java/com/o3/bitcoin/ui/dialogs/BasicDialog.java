/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author
 */

/**
 * <p>Base Class for different dialogs shown in application</p>
 */
public class BasicDialog extends javax.swing.JDialog {
    
    private boolean closed = false;
    private JPanel mainContentPanel = null;
    private JPanel additionalControlsPanel = null;

    /**
     * Creates new form AbstractDialog
     */
    public BasicDialog() {
        super(ApplicationUI.get(), true);
        initComponents();
        setup();
    }
    
    public BasicDialog(boolean defer) {
        super(ApplicationUI.get(), true);
        initComponents();
    }
    
    public void centerOnScreen(boolean pack) {
        if (pack) {
            pack();
        }
        setLocationRelativeTo(null);
    }
    
    public void centerOnScreen() {
        centerOnScreen(true);
    }
    
    public void centerOnScreen(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
    }
    
    /**
     * function to set up template dialog  
    */
    private void setup() {
        pnlTitleBar.setParent(this);
        pnlTitleBar.setTitle(getTitleText());
        pnlTitleBar.setHeading(getHeadingText());
        pnlTitleBar.setDefaultCloseActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDefaultCloseEvent(e);
            }
        });
        List<JButton> controls = getControls();
        if (controls != null) {
            for (JButton button : controls) {
                pnlControls.add(button);
            }
        }
        mainContentPanel = getMainContentPanel();
        if (mainContentPanel != null) {
            pnlContents.add(mainContentPanel, BorderLayout.CENTER);
        }
        additionalControlsPanel = getAdditionalControlsPanel();
        if (additionalControlsPanel != null) {
            pnlControlsContainer.add(additionalControlsPanel, BorderLayout.WEST);
        }
        revalidate();
        repaint();
    }
    
    public void setupUI() {
        setup();
    }
    
    protected void handleDefaultCloseEvent(ActionEvent e) {
        closed = true;
        this.dispose();
    }
    
    /**
     * function to get dialog title text
     * @return title text
    */
    protected String getTitleText() {
        return null;
    }
    
    /**
     * function to get dialog Heading text
     * @return heading text
    */
    protected String getHeadingText() {
        return null;
    }
    
    /**
     * function to set dialog Heading text
     * @param heading heading text
    */
    public void setHeading(String heading) {
        pnlTitleBar.setHeading(heading);
    }
    
    @Override
    public void setTitle(String title) {
        pnlTitleBar.setTitle(title);
    }
    
    /**
     * function to get button controls for dialog
     * @return buttons control list
    */
    protected List<JButton> getControls() {
        List<JButton> controls = new ArrayList<>();
        controls.add(getCloseDialogControl());
        return controls;
    }
    
    /**
     * function to get close button for dialog and attach event handler
     * @return close button
    */
    protected JButton getCloseDialogControl() {
        JButton close = getCloseButton();
        close.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCloseDialogControlEvent(e);
            }
        });
        return close;
    }
    
    /**
     * function to get themed close button for dialog
     * @return close button
    */
    protected JButton getCloseButton() {
        JButton close = new JButton(getCloseButtonText());
        XButtonFactory.themedButton(close)
                .color(Color.WHITE)
                .background(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);
        return close;
    }
    
    /**
     * function to get close button label text
     * @return close button label text
    */
    protected String getCloseButtonText() {
        return "Close";
    }
    
    /**
     * callback function to handle close event for dialog
    */
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        this.closed = true;
        this.dispose();
    }
    
    protected JPanel getAdditionalControlsPanel() {
        return additionalControlsPanel;
    }
    
    /**
     * function to get main content panel where dialog form controls will be shown
     * @return main content panel
    */
    protected JPanel getMainContentPanel() {
        return mainContentPanel;
    }
    
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTitleBar = new com.o3.bitcoin.ui.PnlDialogTitleBar();
        pnlContents = new javax.swing.JPanel();
        pnlControlsContainer = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        pnlTitleBar.setPreferredSize(new java.awt.Dimension(400, 60));
        getContentPane().add(pnlTitleBar, java.awt.BorderLayout.PAGE_START);

        pnlContents.setBackground(ResourcesProvider.Colors.NAV_MENU_BG_COLOR);
        pnlContents.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 1, ResourcesProvider.Colors.TITLE_BAR_BG_COLOR));
        pnlContents.setLayout(new java.awt.BorderLayout());
        getContentPane().add(pnlContents, java.awt.BorderLayout.CENTER);

        pnlControlsContainer.setBackground(ResourcesProvider.Colors.NAV_MENU_BG_COLOR);
        pnlControlsContainer.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 1, 1, ResourcesProvider.Colors.TITLE_BAR_BG_COLOR), javax.swing.BorderFactory.createEmptyBorder(1, 1, 5, 1)));
        pnlControlsContainer.setLayout(new java.awt.BorderLayout());

        pnlControls.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pnlControls.setOpaque(false);
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 1);
        flowLayout1.setAlignOnBaseline(true);
        pnlControls.setLayout(flowLayout1);
        pnlControlsContainer.add(pnlControls, java.awt.BorderLayout.EAST);

        getContentPane().add(pnlControlsContainer, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlContents;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlControlsContainer;
    private com.o3.bitcoin.ui.PnlDialogTitleBar pnlTitleBar;
    // End of variables declaration//GEN-END:variables
}
