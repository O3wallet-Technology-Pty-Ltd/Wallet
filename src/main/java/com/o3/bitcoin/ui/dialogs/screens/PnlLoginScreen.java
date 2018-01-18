/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs.screens;

import com.o3.bitcoin.hdwallet.util.WalletUtil;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p>Class that creates UI form for Login dialog</p>
*/
public class PnlLoginScreen extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlLoginScreen.class);

    /**
     * Creates new form PnlNewPaymentScreen
     *
     * @param service
     */
    public PnlLoginScreen() {
        initComponents();
        controls();
    }

    private void controls(){
        ConfigManager configManager = ConfigManager.get();
        if(configManager.getWalletsOnAllNetworks().size() > 0)
        {
            lblStrengthPassword.setVisible(false);
            lblConfirmPassword.setVisible(false);
            txtConfirmPassword.setVisible(false);
        }
        else
        {
            lblStrengthPassword.setVisible(true);
            lblConfirmPassword.setVisible(true);
            txtConfirmPassword.setVisible(true);
        }
    }
    
    
    
    /**
     * function that validates ui form data
     */
    public void validateData() {
        ConfigManager configManager = ConfigManager.get();
        String password = txtApplicationPassword.getPassword() != null ? new String(txtApplicationPassword.getPassword()) : "";
        if (password.isEmpty()) {
            txtApplicationPassword.requestFocusInWindow();
            throw new IllegalArgumentException("Wallet Password is required.");
        }
        if( configManager.getWalletsOnAllNetworks().size() == 0 ){
            if ((password.length() < 8)) {
                txtApplicationPassword.requestFocusInWindow();
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            
            String confirmPassword = txtConfirmPassword.getPassword() != null ? new String(txtConfirmPassword.getPassword()) : "";
            if (confirmPassword.isEmpty()) {
                txtConfirmPassword.requestFocusInWindow();
                throw new IllegalArgumentException("Confirm Password is required.");
            }
            if( !confirmPassword.equals(password) )
            {
                throw new IllegalArgumentException("Wallet Password and Confirm Password not matched.");
            }
        }
    }

    /**
     * function to login user
     * @return whether user authenticated or not
     */
    public boolean login() {
        try {
            boolean isLoggedIn = false;
            validateData();
            String password = new String(txtApplicationPassword.getPassword());
            if( ConfigManager.get().getWalletsOnAllNetworks().size() == 0 ){// no wallet yet
                WalletManager.walletPassword = new String(txtApplicationPassword.getPassword());
                return true;
            }
            isLoggedIn = WalletUtil.verifyPassword(password);
            if( isLoggedIn )
                WalletManager.walletPassword = new String(txtApplicationPassword.getPassword());
            return isLoggedIn;
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    
    /** 
     * Function that check strength of Password
     * @param password
     * @return 
     */
        
    private int checkPasswordStrength(String password) 
    {
                int strengthPercentage=0;
                String[] partialRegexChecks = { ".*[a-z]+.*", // lower
                                                ".*[A-Z]+.*", // upper
                                                ".*[\\d]+.*", // digits
                                                ".*[@#$!%&*^% ]+.*" // symbols
        };
                    if (password.matches(partialRegexChecks[0]) || password.matches(partialRegexChecks[1])) {
                    strengthPercentage+=25;
            }
                    
                    if (password.matches(partialRegexChecks[2])) {
                    strengthPercentage+=25;
            }
                    if (password.matches(partialRegexChecks[3])) {
                    strengthPercentage+=25;
            }
        return strengthPercentage;
    }

    
    private void checkStrength (int strength)
    {
        if (strength == 0)
        {
            lblStrengthPassword.setText("");
        }
        
        else if (strength == 25)
        {
            lblStrengthPassword.setForeground(Color.RED);
            lblStrengthPassword.setText("Password is Weak");
        }
        
        else if (strength == 50)
        {
            lblStrengthPassword.setForeground(Color.ORANGE);
            lblStrengthPassword.setText("Password is Normal");
        }
        
        else if (strength == 75)
        {
            lblStrengthPassword.setForeground(Color.GREEN);
            lblStrengthPassword.setText("Password is Strong");
        }
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblApplicationPassword = new javax.swing.JLabel();
        txtApplicationPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        lblStrengthPassword = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblApplicationPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblApplicationPassword.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblApplicationPassword.setText("Wallet Password:");
        lblApplicationPassword.setPreferredSize(new java.awt.Dimension(120, 14));
        add(lblApplicationPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, 33));

        txtApplicationPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtApplicationPassword.setPreferredSize(new java.awt.Dimension(275, 33));
        txtApplicationPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtApplicationPasswordKeyReleased(evt);
            }
        });
        add(txtApplicationPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, -1, -1));

        lblConfirmPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblConfirmPassword.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblConfirmPassword.setText("Confirm Password:");
        lblConfirmPassword.setMaximumSize(new java.awt.Dimension(120, 14));
        lblConfirmPassword.setMinimumSize(new java.awt.Dimension(120, 14));
        lblConfirmPassword.setPreferredSize(new java.awt.Dimension(120, 14));
        add(lblConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, 33));

        txtConfirmPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtConfirmPassword.setPreferredSize(new java.awt.Dimension(275, 33));
        add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, -1, -1));
        add(lblStrengthPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 190, -1));
    }// </editor-fold>//GEN-END:initComponents

    public void aa()
    {
        String Pass = txtApplicationPassword.getText();
        if (Pass.length() == 0)
            lblStrengthPassword.setText("");
        
        else{
            int Strength;
            Strength = checkPasswordStrength(Pass);
            checkStrength(Strength);
        }
    }
    
    private void txtApplicationPasswordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApplicationPasswordKeyReleased
        
        ConfigManager configManager = ConfigManager.get();
        if (configManager.getWalletsOnAllNetworks().size() > 0)
        {
            
        }
        else
        {
            aa();
        }
    }//GEN-LAST:event_txtApplicationPasswordKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblApplicationPassword;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblStrengthPassword;
    private javax.swing.JPasswordField txtApplicationPassword;
    private javax.swing.JPasswordField txtConfirmPassword;
    // End of variables declaration//GEN-END:variables
}
