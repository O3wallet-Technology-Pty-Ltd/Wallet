/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs.screens;

import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import java.awt.Color;
import java.awt.Font;
import org.bitcoinj.crypto.KeyCrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * @author
 */
public class PnlManageApplicationPasswordScreen extends javax.swing.JPanel 
{

    private static final Logger logger = LoggerFactory.getLogger(PnlManageApplicationPasswordScreen.class);
    private KeyParameter key;

    /**
     * Creates new form PnlNewPaymentScreen
     *
     * @param service
     */
    public PnlManageApplicationPasswordScreen() 
    {
        initComponents();
    }

    public void validateData() throws IllegalArgumentException,ClientRuntimeException{
        String oldPass = txtOldPassword.getPassword() != null ? new String(txtOldPassword.getPassword()) : "";
        String newPass = txtNewPassword.getPassword() != null ? new String(txtNewPassword.getPassword()) : "";
        String confirmPass = txtConfirmPassword.getPassword() != null ? new String(txtConfirmPassword.getPassword()) : "";
        if (oldPass.isEmpty()) {
            txtOldPassword.requestFocusInWindow();
            throw new IllegalArgumentException("Old Password is required.");
        }
        if (newPass.isEmpty()) {
            txtNewPassword.requestFocusInWindow();
            throw new IllegalArgumentException("New Password is required.");
        }
        if (newPass.length() < 8 ) {
            txtNewPassword.requestFocusInWindow();
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (confirmPass.isEmpty() || !confirmPass.equalsIgnoreCase(newPass)) {
            txtConfirmPassword.requestFocusInWindow();
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if ((confirmPass.length() < 8)) {
            txtConfirmPassword.requestFocusInWindow();
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if( newPass.equals(oldPass)) {
            throw new ClientRuntimeException("Old Passowrd and New Password are same.");
        }
        if( !WalletManager.walletPassword.equals(oldPass)) {
            throw new ClientRuntimeException("Incorrect Old Application Password.");
        }
    }

    public boolean changePassword() {
        try {
            validateData();
            _changePassphrase();
            return true;
        } catch (Exception e) {
            logger.error("Change Password Error: {}", e.getMessage());
            ApplicationUI.get().showError(e.getMessage());
        }
        return false;
    }
    
    
    private void _changePassphrase() throws Exception {
        try{
            String oldPassword = new String(txtOldPassword.getPassword());
            String password = new String(txtNewPassword.getPassword());
            WalletService service = WalletManager.get().getCurentWalletService();
            service.changeAllWalletsPassword(oldPassword,password);
        }catch( Exception e){
            logger.error("Change Password Error: {}", e.getMessage());
            throw e;
        }
    }
    
    
    private int checkPasswordStrength(String password) 
    {
                int strengthPercentage=0;
                String[] partialRegexChecks = { ".*[a-z]+.*", // lower
                                                ".*[A-Z]+.*", // upper
                                                ".*[\\d]+.*", // digits
                                                ".*[@#$!%&*^%]+.*" // symbols
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

        lblOldPassword = new javax.swing.JLabel();
        txtOldPassword = new javax.swing.JPasswordField();
        lblNewPassword = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        lblStrengthPassword = new javax.swing.JLabel();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(380, 160));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblOldPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblOldPassword.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblOldPassword.setText("Old Password:");
        add(lblOldPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 110, 31));

        txtOldPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtOldPassword.setPreferredSize(new java.awt.Dimension(275, 31));
        add(txtOldPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 230, -1));

        lblNewPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblNewPassword.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblNewPassword.setText("New Password:");
        add(lblNewPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 110, 33));

        txtNewPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtNewPassword.setPreferredSize(new java.awt.Dimension(275, 33));
        txtNewPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNewPasswordKeyReleased(evt);
            }
        });
        add(txtNewPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 230, -1));

        lblConfirmPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblConfirmPassword.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblConfirmPassword.setText("Confirm Password:");
        add(lblConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 110, 33));

        txtConfirmPassword.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtConfirmPassword.setPreferredSize(new java.awt.Dimension(275, 33));
        add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, 230, -1));
        add(lblStrengthPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 140, 200, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void txtNewPasswordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNewPasswordKeyReleased
        String Pass = txtNewPassword.getText();
        if (Pass.length() == 0)
        {
            lblStrengthPassword.setText("");
        }
        else
        {
            int Strength;
            Strength = checkPasswordStrength(Pass);
            checkStrength(Strength);
        }
    }//GEN-LAST:event_txtNewPasswordKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblNewPassword;
    private javax.swing.JLabel lblOldPassword;
    private javax.swing.JLabel lblStrengthPassword;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JPasswordField txtNewPassword;
    private javax.swing.JPasswordField txtOldPassword;
    // End of variables declaration//GEN-END:variables
}
