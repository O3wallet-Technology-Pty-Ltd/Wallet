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
import com.o3.bitcoin.ui.dialogs.screens.PnlNewPaymentForOfflineWallet;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class DlgNewPaymentForOfflineWallet extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgNewPayment.class);
    private final WalletService service;
    private PnlNewPaymentForOfflineWallet pnlNewPaymentScreenForOfflineWallet;
    public static JButton paymentButton;
    public static JButton qrCodeButton;
    private List<JButton> controls = new ArrayList<>();

    /**
     * Creates new form DlgNewPayment
     *
     * @param service
     */
    public DlgNewPaymentForOfflineWallet(WalletService service) {
        super(false);
        this.service = service;
        setupUI();
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlNewPaymentScreenForOfflineWallet == null) {
            pnlNewPaymentScreenForOfflineWallet = new PnlNewPaymentForOfflineWallet(this, service);
        }
        return pnlNewPaymentScreenForOfflineWallet;
    }

    public void setReceiveAddress(String address) {
        pnlNewPaymentScreenForOfflineWallet.setReceiveAddress(address);
    }

    public void setAmount(String amount) {
        pnlNewPaymentScreenForOfflineWallet.setAmount(amount);
    }

    public void complete(Wallet.SendResult res) {

    }

    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getPaymentButton());
        controls.add(0, getQRCodeButton());
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
            if(PnlWalletScreen.isWatchOnly && DlgScanQRCodeForOfflineWallet.isScanned)
            {
                if(!PnlNewPaymentForOfflineWallet.transactionStatusSigned.getText().equals(""))
                {
                    try {
                    pnlNewPaymentScreenForOfflineWallet.payCoins();
                    DlgScanQRCodeForOfflineWallet.isScanned = false;
                    } catch (IllegalArgumentException ex) {
                        logger.error("Payment failed: {}", ex.toString(), ex);
                        ApplicationUI.get().showError(ex);
                    } catch (Exception ex) {
                        logger.error("Payment failed: {}", ex.getMessage(), ex);
                        ApplicationUI.get().showError(ex);
                    }
                }
                else
                {
                    ApplicationUI.get().showError("Unsigned Transaction");
                }
            }
            else
            {
               ApplicationUI.get().showError("Generate QRCode, Sign Transaction From Android Wallet, Scan QRCode To Proceed");
            }
    }

    protected JButton getQRCodeButton() {
        qrCodeButton = new JButton("QRCode");
        XButtonFactory.themedButton(qrCodeButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        qrCodeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleQRCodeButtonClickEvent(e);
            }
        });
        return qrCodeButton;
    }

    protected void handleQRCodeButtonClickEvent(ActionEvent e) {
            String address = PnlNewPaymentForOfflineWallet.txtAddress.getText();
            String amount = PnlNewPaymentForOfflineWallet.txtFiat.getText();
            String btcAmount = PnlNewPaymentForOfflineWallet.txtBTC.getText();
            String description = PnlNewPaymentForOfflineWallet.lblEditDescription.getText();
            saveContentToFile(address, btcAmount);
            if(PnlNewPaymentForOfflineWallet.transactionStatusSigned.getText().equals("Transaction Signed") || PnlNewPaymentForOfflineWallet.transactionStatusSigned.getText().equals("Unsigned Transaction") || PnlNewPaymentForOfflineWallet.transactionStatusSigned.getText().equals("Empty QrCode"))
            {
                ApplicationUI.get().showError("Transaction already signed Or Empty field");
            }
            else
            {
                DlgScanQRCodeForOfflineWallet dlgQrcode = new DlgScanQRCodeForOfflineWallet(address, description, amount, btcAmount);
                dlgQrcode.centerOnScreen();
                dlgQrcode.setVisible(true);
            }
    }

    private void saveContentToFile(String address, String btcAmount) {
        String filePath = ConfigManager.CONFIG_ROOT + File.separator + "tH.txt";
        createFile(filePath);
        String contentToWrite = address + "," + btcAmount;
        writeFile(filePath,contentToWrite);
    }
    
    
    private void createFile(String filePath) {
        File file = new File(filePath);
    }

    private void writeFile(String filePath, String contentToWrite) {
        try (BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(filePath))) {
            String content = contentToWrite;
            bufferWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] readFile() throws FileNotFoundException, IOException {
        String fileName = ConfigManager.CONFIG_ROOT + File.separator + "tH.txt";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            String split[] = sb.toString().split(",");
           return split;
        } finally {
            br.close();
        }
    }

    @Override
    protected String getHeadingText() {
        return "Send Payment(Watch Only Wallet)";
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
