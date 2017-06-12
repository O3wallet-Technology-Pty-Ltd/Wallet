/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs.screens;

import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.dialogs.DlgCreateNewAccount;
import com.o3.bitcoin.util.ResourcesProvider;
import javax.swing.JOptionPane;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p>Class that creates UI form for New Account dialog</p>
 */
public class PnlNewAccountScreen extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlLoginScreen.class);
    
    private final DlgCreateNewAccount dlgNewAccount;
    private final WalletService service;

    /**
     * Creates new form PnlNewPaymentScreen
     *
     * @param DlgCreateNewAccount
     * @param service
     */
    public PnlNewAccountScreen(DlgCreateNewAccount dlgNewAccount, WalletService service) {
        this.dlgNewAccount = dlgNewAccount;
        this.service = service;
        initComponents();
    }

    /**
     * function that validates ui form data
     */
    public void validateData() {
        String password = txtAccountName.getText() != null ? new String(txtAccountName.getText()) : "";
        if (password.isEmpty()) {
            txtAccountName.requestFocusInWindow();
            throw new IllegalArgumentException("Account Name is required.");
        }
    }

    /**
     * function that creates new account
     * @return whether account created or not
     */
    public boolean createAccount() {
        try {
            validateData();
            String accountName = new String(txtAccountName.getText());
            Coin balance = service.getWallet().getBalance(Wallet.BalanceType.ESTIMATED);
            if(balance.value > 0) {
                if( !service.isAccountExists(txtAccountName.getText()) )
                {
                    if( service.getAllAccounts().size() >= 2 ) {
                       JOptionPane.showMessageDialog(null, "ERROR: You can't create more than 2 accounts", "ERROR", JOptionPane.ERROR_MESSAGE); 
                       return false;
                    }

                    service.addAccount(accountName);
                    return true;
                }
                else {
                     JOptionPane.showMessageDialog(null, "ERROR: " + txtAccountName.getText() + " already exists", "ERROR", JOptionPane.ERROR_MESSAGE);
                     return false;
                }
            }
            else {
                    JOptionPane.showMessageDialog(null, "ERROR: You can't create new account with 0 wallet balance", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
            }    
                
        } catch (Exception e) {
            logger.error("Account Creation failed: {}", e.getMessage());
            ApplicationUI.get().showError(e);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
                         
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblAccountName = new javax.swing.JLabel();
        txtAccountName = new javax.swing.JTextField();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblAccountName.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAccountName.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAccountName.setText("Account Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblAccountName, gridBagConstraints);

        txtAccountName.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtAccountName.setPreferredSize(new java.awt.Dimension(275, 33));
        //txtAccountName.setText("Account "+service.getHDAccountsCount());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtAccountName, gridBagConstraints);
    }                        
                  
    private javax.swing.JLabel lblAccountName;
    private javax.swing.JTextField txtAccountName;

}

