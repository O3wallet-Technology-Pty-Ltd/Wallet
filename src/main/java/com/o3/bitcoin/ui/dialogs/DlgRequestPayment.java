/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlNewRequestPaymentScreen;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.bitcoinj.core.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author LPS-PC
 */
public class DlgRequestPayment extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgRequestPayment.class);
    private final WalletService service;
    private PnlNewRequestPaymentScreen pnlNewRequestPaymentScreen;
    private JButton paymentButton;
    private List<JButton> controls = new ArrayList<>();
    public static boolean isClicked = false;
    private String amount = "";
    private String descrisption = "";
    private String btcAmount = "";
    private String address = "";

    /**
     * Creates new form DlgRequestPayment
     *
     * @param service
     */
    public DlgRequestPayment(WalletService service) {
        super(false);
        this.service = service;
        setupUI();
    }
    
    

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlNewRequestPaymentScreen == null) {
            pnlNewRequestPaymentScreen = new PnlNewRequestPaymentScreen(this, service);
        }
        return pnlNewRequestPaymentScreen;
    }

//    public void setReceiveAddress(String address) {
//        pnlNewRequestPaymentScreen.setReceiveAddress(address);
//    }
    public void setAmount(String amount) {
        pnlNewRequestPaymentScreen.setAmount(amount);
    }

    public void complete(Wallet.SendResult res) {

    }

    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getPaymentButton());
        return controls;
    }

    /**
     * function to get Pay button for dialog and attach event handler
     *
     * @return Pay button
     */
    protected JButton getPaymentButton() {
        paymentButton = new JButton("Create");
        XButtonFactory.themedButton(paymentButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        paymentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               amount = pnlNewRequestPaymentScreen.txtFiat.getText();
               descrisption = pnlNewRequestPaymentScreen.description.getText();
               btcAmount =  pnlNewRequestPaymentScreen.txtBTC.getText();
               //PnlQRCodeScreen pnlQRCodeScreen = new PnlQRCodeScreen(address, descrisption, amount, btcAmount);
                //New Functionality
                DlgQRCode dlgQrcode = new DlgQRCode(PnlWalletScreen.LabelAddress, descrisption, amount, btcAmount);
                dlgQrcode.centerOnScreen();
                dlgQrcode.setVisible(true);
            }
        });
        return paymentButton;
    }

    /**
     * callback function for Pay button event
     */
//    protected void handlePayCoinsButtonClickEvent(ActionEvent e) {
//        try {
//            pnlNewRequestPaymentScreen.payCoins();
//        } catch (IllegalArgumentException ex) {
//            logger.error("Payment failed: {}", ex.toString(), ex);
//            ApplicationUI.get().showError(ex);
//        } catch (Exception ex) {
//            logger.error("Payment failed: {}", ex.getMessage(), ex);
//            ApplicationUI.get().showError(ex);
//        }
//    }
    @Override
    protected String getHeadingText() {
        return "Payment Request";
    }

    @Override
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        super.handleCloseDialogControlEvent(e);
    }

    @Override
    protected void handleDefaultCloseEvent(ActionEvent e) {
        handleCloseDialogControlEvent(e);
    }

    public void markPaymentPending(boolean paymentPending) {
        controls.get(0).setEnabled(!paymentPending);
    }

    public void markWatchOnly() {
        try {
            service.getWalletConfig().setWatchOnly(true);
            ConfigManager.get().save();
        } catch (Exception ex2) {
            logger.error("Save Watch-Only config [{}]: Unable to save config: ", service.getWalletConfig().getId(), ex2);
        }
        this.dispose();
    }

    public void disablePaymentButton() {
        paymentButton.setEnabled(false);
    }

    public void enablePaymentButton() {
        paymentButton.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    // End of variables declaration            
}
