/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.DlgNewPayment;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.Utils;
import com.o3.bitcoin.util.exchange.BTCMarketExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.polling.account.PollingAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class PnlExchangeWithdrawal extends javax.swing.JPanel  implements BasicExchangeScreen {

    /**
     * Creates new form PnlExchangeWithdrawal
     */
    
    private static final Logger logger = LoggerFactory.getLogger(PnlExchangeWithdrawal.class);
    
    private ExchangeService exchangeService = null;
    private String availableCoins = "";
    private String password = "";
    private String withdrawalAddress = "";
    private String coinAmount = "";
    private boolean isFirstTime = true;
    private boolean isOperationInProgress = false;
    
    private final DefaultComboBoxModel<HDAccount> comboModel = new DefaultComboBoxModel<>();
    
    public PnlExchangeWithdrawal() {
        initComponents();
        customizeUI();
        lblMinimumWithdrawal.setVisible(false);
        lblMinimumWithdrawalValue.setVisible(false);
    }
    
    private void customizeUI() {
        themeButton(btnWithdraw,ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        themeButton(btnDeposit, Colors.NAV_MENU_WALLET_COLOR);
    }
    
    private void themeButton(JButton button, Color background) {
        XButtonFactory
                .themedButton(button)
                .color(Color.WHITE)
                .background(background)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
    }
    
    private void prepareAccountCombo() {
        
        exchangeService =  ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
        String altCoin = exchangeService.getAltcoinCurrency();
        if(altCoin.equalsIgnoreCase("BTC") || altCoin.equalsIgnoreCase("XBT")) {
            lblAccounts.setVisible(true);
            cmbAccounts.setVisible(true);
            cmbAccounts.setRenderer(new BasicComboBoxRenderer() {

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    list.setSelectionBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
                    list.setSelectionForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
                    JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
                    if (isSelected) {
                        component.setForeground(Color.WHITE);
                        component.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
                    }
                    component.setPreferredSize(new Dimension(component.getPreferredSize().height, 30));
                    return component;
                }
            });

            Object child = cmbAccounts.getAccessibleContext().getAccessibleChild(0);
            BasicComboPopup popup = (BasicComboPopup) child;
            popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
            int index = 0;
            List<HDAccount> accounts = WalletManager.get().getCurentWalletService().getAllAccounts();
            comboModel.removeAllElements();// reload application
            for (HDAccount account : accounts) {
                if( index == 0 )
                    txtWithdrawalAddress.setText(account.nextReceiveAddress().toString());
                comboModel.addElement(account);
                index = 1;
            }
        }
        else {
            lblAccounts.setVisible(false);
            cmbAccounts.setVisible(false);
            txtWithdrawalAddress.setText("");
        }
    }
    
    public void loadExchangeData() {
        try {
            
            prepareAccountCombo();
            txtAmount.setText("");
            exchangeService =  ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
            if(exchangeService.getExchangeName().equalsIgnoreCase("btcmarkets"))
                btnDeposit.setVisible(false);
            else
                btnDeposit.setVisible(true);
            btnDeposit.setText("Deposit "+exchangeService.getAltcoinCurrency().toUpperCase());
            lblAvailableBtc.setText("Available "+exchangeService.getAltcoinCurrency().toUpperCase());
            lblAmount.setText("Amount "+exchangeService.getAltcoinCurrency().toUpperCase());
            lblBitcoinAddress.setText(exchangeService.getAltcoinCurrency().toUpperCase() +" Address");
            if( isFirstTime ) {
                availableCoins = exchangeService.getCurrencyBalance(new Currency(exchangeService.getAltcoinCurrency().toUpperCase()),8);
                if(availableCoins.equalsIgnoreCase("0.00000000"))
                    availableCoins = "0.0";
                lblAvailableBtcValue.setText(availableCoins);
            }
            else {
                updateBalance();
            }
            isFirstTime = false;
        }catch(Exception e) {
            logger.error("Exception: {}", e.getMessage());
            ApplicationUI.get().showError(e);
        }
        
    }
    
    private void updateBalance() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    exchangeService =  ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
                    availableCoins = exchangeService.getCurrencyBalance(new Currency(exchangeService.getAltcoinCurrency().toUpperCase()),8);
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if(availableCoins.equalsIgnoreCase("0.00000000"))
                                availableCoins = "0.0";
                            lblAvailableBtcValue.setText(availableCoins);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Exception while getting Buy Orders: {}", e.getMessage());
                }
            }
        }).start();
    }

     private void validateSellBitcoinData() {
        availableCoins = lblAvailableBtcValue.getText();
        if (txtAmount == null || txtAmount.getText().isEmpty()) {
            txtAmount.requestFocusInWindow();
            throw new IllegalArgumentException("Coin's amount is required.");
        }
        if (txtWithdrawalAddress == null || txtWithdrawalAddress.getText().isEmpty()) {
            txtWithdrawalAddress.requestFocusInWindow();
            throw new IllegalArgumentException("Withdrawal Adress is required.");
        }

        /*if (Double.parseDouble(txtAmount.getText()) > Double.parseDouble(availableCoins)) {
            txtAmount.requestFocusInWindow();
            throw new IllegalArgumentException("You cannot withdraw more than your availabe coins.");
        }*/

        populateControlValues();
    }
     
    private void populateControlValues() {
        withdrawalAddress = txtWithdrawalAddress.getText();
        coinAmount = txtAmount.getText();
    }
       
       
       /*private String withdrawBitcoins(){
        String withdrawResult="";
           try {
            PollingAccountService accountService = exchangeService.getExchange().getPollingAccountService();
            withdrawResult = accountService.withdrawFunds(Currency.BTC, new BigDecimal (bitcoinAmount), "1PxYUsgKdw75sdLmM7HYP2p74LEq3mxM6L");
            return withdrawResult;
        } catch (Exception e) {
            logger.error("Withdraw Coins Exception: {}", e.getMessage());
            ApplicationUI.get().showError(e);
        } 
           return withdrawResult;
       }*/
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        btnDeposit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblMinimumWithdrawal = new javax.swing.JLabel();
        lblMinimumWithdrawalValue = new javax.swing.JLabel();
        lblAvailableBtc = new javax.swing.JLabel();
        lblAvailableBtcValue = new javax.swing.JLabel();
        lblBitcoinAddress = new javax.swing.JLabel();
        txtWithdrawalAddress = new javax.swing.JTextField();
        lblAmount = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        btnWithdraw = new javax.swing.JButton();
        lblHorizontalSpacer = new javax.swing.JLabel();
        lblVerticalSpacer = new javax.swing.JLabel();
        lblAccounts = new javax.swing.JLabel();
        cmbAccounts = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnDeposit.setText("Deposit");
        btnDeposit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDepositMouseClicked(evt);
            }
        });
        jPanel1.add(btnDeposit);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Withdraw", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, ResourcesProvider.Fonts.BOLD_LARGE_FONT, ResourcesProvider.Colors.DEFAULT_HEADING_COLOR));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblMinimumWithdrawal.setFont(Fonts.BOLD_SMALL_FONT);
        lblMinimumWithdrawal.setText("Minimum Withdrawal : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblMinimumWithdrawal, gridBagConstraints);

        lblMinimumWithdrawalValue.setFont(Fonts.BOLD_SMALL_FONT);
        lblMinimumWithdrawalValue.setText("0.001 BTC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblMinimumWithdrawalValue, gridBagConstraints);

        lblAvailableBtc.setFont(Fonts.BOLD_SMALL_FONT
        );
        lblAvailableBtc.setText("Available BTC : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblAvailableBtc, gridBagConstraints);

        lblAvailableBtcValue.setFont(Fonts.BOLD_SMALL_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblAvailableBtcValue, gridBagConstraints);

        lblBitcoinAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblBitcoinAddress.setText("BTC Address : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblBitcoinAddress, gridBagConstraints);

        txtWithdrawalAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtWithdrawalAddress.setPreferredSize(new java.awt.Dimension(275, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(txtWithdrawalAddress, gridBagConstraints);

        lblAmount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAmount.setText("Amount BTC : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblAmount, gridBagConstraints);

        txtAmount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtAmount.setPreferredSize(new java.awt.Dimension(175, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(txtAmount, gridBagConstraints);

        btnWithdraw.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnWithdraw.setText("Withdraw");
        btnWithdraw.setPreferredSize(new java.awt.Dimension(175, 35));
        btnWithdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWithdrawActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(btnWithdraw, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(lblHorizontalSpacer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(lblVerticalSpacer, gridBagConstraints);

        lblAccounts.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAccounts.setText("Account");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblAccounts, gridBagConstraints);

        cmbAccounts.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbAccounts.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbAccounts.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbAccounts.setModel(comboModel);
        cmbAccounts.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbAccounts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbAccounts.setPreferredSize(new java.awt.Dimension(175, 35));
        cmbAccounts.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAccountsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(cmbAccounts, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnWithdrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWithdrawActionPerformed
        try {
            if( isOperationInProgress )
                return;
            isOperationInProgress = true;
            validateSellBitcoinData();
            btnWithdraw.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            exchangeService.withdrawBitcoins(exchangeService.getAltcoinCurrency().toUpperCase(), coinAmount, withdrawalAddress);
            btnWithdraw.setCursor(new Cursor(Cursor.HAND_CURSOR));
            updateBalance();
            YesNoDialog dialog = new YesNoDialog("Withdraw Coins","Coin's withdrawal order placed successfully", false);
            dialog.start();
        }catch(Exception e) {
            logger.error("Withdraw Coins Exception: {}", e.getMessage());
            ApplicationUI.get().showError(e);
        }
        btnWithdraw.setCursor(new Cursor(Cursor.HAND_CURSOR));
        isOperationInProgress = false;
    }//GEN-LAST:event_btnWithdrawActionPerformed

    private void cmbAccountsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAccountsItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (!(evt.getItem() instanceof HDAccount)) {
                return;
            }
            HDAccount hdacct = (HDAccount) evt.getItem();
            if (hdacct != null) {
                txtWithdrawalAddress.setText(hdacct.nextReceiveAddress().toString());
            }
        }
    }//GEN-LAST:event_cmbAccountsItemStateChanged

    private void btnDepositMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDepositMouseClicked
        // TODO add your handling code here:
        try {
            btnDeposit.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            exchangeService =  ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
            String depositAddress = exchangeService.getDepositAddressForExchange(exchangeService.getAltcoinCurrency());
            DlgNewPayment dlgNewPayment = new DlgNewPayment(WalletManager.get().getCurentWalletService());
            dlgNewPayment.centerOnScreen();
            dlgNewPayment.setReceiveAddress(depositAddress);
            dlgNewPayment.setVisible(true);
            btnDeposit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }catch(Exception e) {
            btnDeposit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            ApplicationUI.get().showError(e);
        }
    }//GEN-LAST:event_btnDepositMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeposit;
    private javax.swing.JButton btnWithdraw;
    private javax.swing.JComboBox cmbAccounts;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAccounts;
    private javax.swing.JLabel lblAmount;
    private javax.swing.JLabel lblAvailableBtc;
    private javax.swing.JLabel lblAvailableBtcValue;
    private javax.swing.JLabel lblBitcoinAddress;
    private javax.swing.JLabel lblHorizontalSpacer;
    private javax.swing.JLabel lblMinimumWithdrawal;
    private javax.swing.JLabel lblMinimumWithdrawalValue;
    private javax.swing.JLabel lblVerticalSpacer;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtWithdrawalAddress;
    // End of variables declaration//GEN-END:variables
}
