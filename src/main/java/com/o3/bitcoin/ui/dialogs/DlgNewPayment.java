/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlNewPaymentScreen;
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
 * @author
 */
/**
 * <p>
 * Class that implements ui dialog to make payment</p>
 */
public class DlgNewPayment extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgNewPayment.class);
    private final WalletService service;
    private PnlNewPaymentScreen pnlNewPaymentScreen;
    public static JButton paymentButton;
    private List<JButton> controls = new ArrayList<>();

    /**
     * Creates new form DlgNewPayment
     *
     * @param service
     */
    public DlgNewPayment(WalletService service) {
        super(false);
        this.service = service;
        setupUI();
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlNewPaymentScreen == null) {
            pnlNewPaymentScreen = new PnlNewPaymentScreen(this, service);
        }
        return pnlNewPaymentScreen;
    }

    public void setReceiveAddress(String address) {
        pnlNewPaymentScreen.setReceiveAddress(address);
    }

    public void setAmount(String amount) {
        pnlNewPaymentScreen.setAmount(amount);
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
        paymentButton = new JButton("Pay");
        XButtonFactory.themedButton(paymentButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        paymentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handlePayCoinsButtonClickEvent(e);
            }
        });
        return paymentButton;
    }

    /**
     * callback function for Pay button event
     */
    protected void handlePayCoinsButtonClickEvent(ActionEvent e) {
        String comboBoxSelectedItem = String.valueOf(PnlNewPaymentScreen.cmbAccounts.getSelectedItem());
        if(comboBoxSelectedItem.endsWith("."))
        {
             ApplicationUI.get().showError("This is watch only acount, You can not proceed with this acount Switch to watch only acount to proceed");
        }
        else
        {
            try {
            pnlNewPaymentScreen.payCoins();
            } catch (IllegalArgumentException ex) {
                logger.error("Payment failed: {}", ex.toString(), ex);
                ApplicationUI.get().showError(ex);
            } catch (Exception ex) {
                logger.error("Payment failed: {}", ex.getMessage(), ex);
                ApplicationUI.get().showError(ex);
            }
        }
    }

    protected void handleQRCodeButtonClickEvent(ActionEvent e) {
        String address = PnlNewPaymentScreen.txtAddress.getText();
        String amount = PnlNewPaymentScreen.txtFiat.getText();
        String btcAmount = PnlNewPaymentScreen.txtBTC.getText();
        String description = PnlNewPaymentScreen.lblEditDescription.getText();
        DlgQRCode dlgQrcode = new DlgQRCode(address, description, amount, btcAmount);
        dlgQrcode.centerOnScreen();
        dlgQrcode.setVisible(true);
    }

    @Override
    protected String getHeadingText() {
        return "Send Payment";
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
