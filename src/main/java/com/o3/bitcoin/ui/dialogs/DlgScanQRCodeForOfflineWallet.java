/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.github.sarxos.webcam.Webcam;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlNewPaymentForOfflineWallet;
import com.o3.bitcoin.ui.dialogs.screens.PnlQRCodeScreen;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.BitcoinCurrencyRateApi;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
/**
 * <p>
 * Class that implements ui dialog to show QRCode of address</p>
 */
public class DlgScanQRCodeForOfflineWallet extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgCreateNewAccount.class);
    private PnlQRCodeScreen pnlQRCodeScreen;
    private List<JButton> controls = new ArrayList<>();
    private String address = null;
    private String amount = null;
    private String description = null;
    private String btcAmount = null;
    public static boolean isScanned = false;

    /**
     * Creates new form DlgQRCode
     */
    public DlgScanQRCodeForOfflineWallet(String address) {
        super(false);
        this.address = address;
        setupUI();
    }

    public DlgScanQRCodeForOfflineWallet(String address, String description, String amount, String btcAmount) {
        super(false);
        this.address = address;
        this.amount = amount;
        this.description = description;
        this.btcAmount = btcAmount;
        setupUI();
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlQRCodeScreen == null) {
            pnlQRCodeScreen = new PnlQRCodeScreen(address, description, amount, btcAmount);
        }
        return pnlQRCodeScreen;
    }

    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getCopyQRCodeButton());
        return controls;
    }

    /**
     * function to get Copy button for dialog and attach event handler
     *
     * @return Copy button
     */
    protected JButton getCopyQRCodeButton() {
        JButton accountButton = new JButton("Next");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleNextButtonClickEvent(e);
            }
        });
        return accountButton;
    }

    /**
     * callback function for Copy button event
     */
    protected void handleNextButtonClickEvent(ActionEvent e) {

        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(null, "FATAL ERROR: No Webcam found", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        webcam.close();
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DlgScanQRCode dlgSC = new DlgScanQRCode();
                dlgSC.centerOnScreen();
                dlgSC.setVisible(true);
                String qrCode = dlgSC.getQRCode();
                if (qrCode != null) {
                    //do not change this...
                    if (PnlWalletScreen.isWatchOnly) {
                        isScanned = true;
                        if (isScanned) {
                            makeFieldsUneditable();
                        } else {
                            makeFieldsEditable();
                        }
                    } else {
                        isScanned = false;
                    }
                    //End here
                    try {
                        settingPaymentLayout(qrCode);
                        dispose();
                    } catch (NumberFormatException ex) {
                        java.util.logging.Logger.getLogger(PnlNewPaymentForOfflineWallet.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(DlgScanQRCodeForOfflineWallet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            private void settingPaymentLayout(String qrCode) throws IOException {
                signedTransactionValidation(qrCode);
            }

            private void signedTransactionValidation(String qrCode) throws IOException {
                dataValidation(qrCode);
            }
        });

    }

    private void dataValidation(String qrCode) throws IOException {
        try {
            String splitQRCode[] = qrCode.split(",");
            String address = splitQRCode[0];
            String amount = splitQRCode[1];
            String message = splitQRCode[2];
            String xPub = splitQRCode[3];
            String key[] = xPub.split("=");
            String setAddress[] = address.split("=");
            String amountBtc[] = amount.split("=");
            System.out.println(key[1]);
            if (key[1].equals(DlgImportPublicKey.readFile("tPub.txt"))) {
                String getFileData[] = DlgNewPaymentForOfflineWallet.readFile();
                if (getFileData.length != 0) {
                    if (getFileData[0].equals(setAddress[1]) && getFileData[1].equals(amountBtc[1]) && !getFileData.toString().equals("")) {
                        settingLayoutForSignedTransaciton(setAddress, amountBtc, message);
                    } else {
                        ApplicationUI.get().showError("Transaction can not be signed, Sign transactin to proceed");
                        settingLayoutForUnsignedTransaciton();
                    }
                } else {
                    PnlNewPaymentForOfflineWallet.transactionStatusSigned.setText("Empty QrCode");
                }
            } else if (key[1].equals(DlgImportPublicKey.readFile("xPub.txt"))) {
                String getFileData[] = DlgNewPaymentForOfflineWallet.readFile();
                if (getFileData.length != 0) {
                    if (getFileData[0].equals(setAddress[1]) && getFileData[1].equals(amountBtc[1]) && !getFileData.toString().equals("")) {
                        settingLayoutForSignedTransaciton(setAddress, amountBtc, message);
                    } else {
                        ApplicationUI.get().showError("Transaction can not be signed, Sign transactin to proceed");
                        settingLayoutForUnsignedTransaciton();
                    }
                } else {
                    PnlNewPaymentForOfflineWallet.transactionStatusSigned.setText("Empty QrCode");
                }
            } else {
                ApplicationUI.get().showError("Transaction can not be proceed");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ApplicationUI.get().showError("Transaction can not be proceed " + e);
            dispose();
        }
    }

    private void settingLayoutForSignedTransaciton(String[] setAddress, String[] amountBtc, String message) throws NumberFormatException {
        PnlNewPaymentForOfflineWallet.txtAddress.setText(setAddress[1]);
        PnlNewPaymentForOfflineWallet.txtBTC.setText(amountBtc[1]);
        Double fiatValue = (Double.parseDouble(amountBtc[1]) * BitcoinCurrencyRateApi.currentRate.getValue());
        PnlNewPaymentForOfflineWallet.txtFiat.setText(String.format("%.2f", fiatValue));
        String description[] = message.split("=");
        PnlNewPaymentForOfflineWallet.lblEditDescription.setText(description[1]);
        PnlNewPaymentForOfflineWallet.transactionStatusSigned.setText("Transaction Signed");
    }

    private void settingLayoutForUnsignedTransaciton() {
        PnlNewPaymentForOfflineWallet.txtAddress.setText("");
        PnlNewPaymentForOfflineWallet.txtBTC.setText("");
        PnlNewPaymentForOfflineWallet.txtFiat.setText("");
        PnlNewPaymentForOfflineWallet.lblEditDescription.setText("");
        PnlNewPaymentForOfflineWallet.transactionStatusSigned.setText("Unsigned Transaction");
        PnlNewPaymentForOfflineWallet.lblFeeValue.setText("");
    }

    private void makeFieldsEditable() {
        PnlNewPaymentForOfflineWallet.txtAddress.setEditable(true);
        PnlNewPaymentForOfflineWallet.lblEditDescription.setEditable(true);
        PnlNewPaymentForOfflineWallet.txtFiat.setEditable(true);
        PnlNewPaymentForOfflineWallet.txtBTC.setEditable(true);
        PnlNewPaymentForOfflineWallet.lblFeeValue.setEditable(true);
        PnlNewPaymentForOfflineWallet.cmbAccounts.setEditable(true);
        PnlNewPaymentForOfflineWallet.cmbSpeed.setVisible(true);
        PnlNewPaymentForOfflineWallet.lblSpeed.setVisible(true);
    }

    private void makeFieldsUneditable() {
        PnlNewPaymentForOfflineWallet.txtAddress.setEditable(false);
        PnlNewPaymentForOfflineWallet.lblEditDescription.setEditable(false);
        PnlNewPaymentForOfflineWallet.txtFiat.setEditable(false);
        PnlNewPaymentForOfflineWallet.txtBTC.setEditable(false);
        PnlNewPaymentForOfflineWallet.lblFeeValue.setEditable(false);
        PnlNewPaymentForOfflineWallet.cmbSpeed.setVisible(false);
        PnlNewPaymentForOfflineWallet.lblSpeed.setVisible(false);
        PnlNewPaymentForOfflineWallet.cmbAccounts.setEditable(false);
    }

    private void deleteQRCodeFile() {
        File file = new File(pnlQRCodeScreen.getQrcodeFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected String getHeadingText() {
        return "QRCode For Signing Transaction";
    }

    @Override
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        super.handleCloseDialogControlEvent(e);
        deleteQRCodeFile();
    }

    @Override
    protected void handleDefaultCloseEvent(ActionEvent e) {
        handleCloseDialogControlEvent(e);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    // End of variables declaration                   
}
