/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlImportPubKey;
import com.o3.bitcoin.ui.screens.settings.PnlSettingsScreen;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tygac
 */
public class DlgImportPublicKey extends BasicDialog {

    private static Cipher ecipher;
    private static Cipher dcipher;
    private static SecretKey key;
    private final String filePath = ConfigManager.CONFIG_ROOT + File.separator + "tPub.txt";
    private final String filePathX = ConfigManager.CONFIG_ROOT + File.separator + "xPub.txt";
    private PnlImportPubKey pnlImportKey;
    public static String pubKey = null;
    private int count = 0;

    @Override
    protected String getHeadingText() {
        return "Confirm Public Key";
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlImportKey == null) {
            pnlImportKey = new PnlImportPubKey(this);
        }
        return pnlImportKey;
    }

    @Override
    protected List<JButton> getControls() {
        List<JButton> controls = super.getControls();
        controls.add(0, getScanButton());
        controls.add(1, getcreateButton());
        return controls;
    }

    @Override
    protected JButton getCloseDialogControl() {
        JButton close = getCloseButton();
        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleCloseDialogControlEvent(e);
                getCloseDialogControl();
            }
        });
        return close;
    }

    /*
    * Scan Button
     */
    protected JButton getScanButton() {
        JButton scanButton = new JButton("Scan");
        XButtonFactory.themedButton(scanButton)
                .background(Colors.NAV_MENU_ABOUT_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);
        scanButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DlgScanQRCode dlgScanQRCode = new DlgScanQRCode();
                    dlgScanQRCode.centerOnScreen();
                    dlgScanQRCode.setVisible(true);
                    //dlgScanQRCode.handleScanQRCodetButtonClickEvent(e);
                    String qrCode = dlgScanQRCode.getQRCode();

                    if (qrCode != null) {
                        if (qrCode.startsWith("bitcoin:")) {
                            qrCode = qrCode.substring(8);
                        }
                        if (qrCode.contains("?")) {
                            qrCode = qrCode.substring(0, qrCode.indexOf("?"));
                        }

                        String[] elements = qrCode.split("\\:", -1);
                        PnlImportPubKey.getPubKey.setText(elements[0]);
                        dlgScanQRCode.handleCloseDialogControlEvent(e);
                        dlgScanQRCode.handleDefaultCloseEvent(e);
                    }
                } catch (NullPointerException a) {
                    System.out.println("Handled");
                }
            }
        });
        return scanButton;
    }

    /**
     * function to get Next button for dialog and attach event handler
     *
     * @return Next button
     */
    protected JButton getcreateButton() {
        JButton createButton = new JButton("Create");
        XButtonFactory.themedButton(createButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handlecreateButtonClickEvent(e);
            }

            private void handlecreateButtonClickEvent(ActionEvent e) {
                pubKey = PnlImportPubKey.getPubKey.getText();
                try {
                    if (!pubKey.isEmpty()) {
                        if (pubKey.charAt(0) == 't' && PnlSettingsScreen.rdoTestNet.isSelected()) {
                            if ((PnlSettingsScreen.xPublicKey == null ? pubKey != null : !PnlSettingsScreen.xPublicKey.equals(pubKey)) || "".equals(pubKey)) {
                                createColdWalletAccount(filePath, e);
                            }
                            if (PnlSettingsScreen.xPublicKey == null ? pubKey == null : PnlSettingsScreen.xPublicKey.equals(pubKey)) {
                                DlgCreateWallet.setSuccessMsg("Key imported succfully");
                                dispose();
                            }
                        }
                        else if (pubKey.charAt(0) == 'x' && PnlSettingsScreen.rdoMainNet.isSelected()) {
                            if ((PnlSettingsScreen.xPublicKey == null ? pubKey != null : !PnlSettingsScreen.xPublicKey.equals(pubKey)) || "".equals(pubKey)) {
                                createColdWalletAccount(filePathX, e);
                            }
                            if (PnlSettingsScreen.xPublicKey == null ? pubKey == null : PnlSettingsScreen.xPublicKey.equals(pubKey)) {
                                DlgCreateWallet.setSuccessMsg("Key imported succfully");
                                dispose();
                            }
                        }
                        else {
                            DlgCreateWallet.setWarningMsg("Not a valid key");
                            dispose();
                        }
                    } else {
                        DlgCreateWallet.setWarningMsg("Key not matched Or empty field");
                        dispose();
                    }

                } catch (Exception ex) {
                    System.out.println("--------------------" + ex + "-----------------");
                }
            }
            private void createColdWalletAccount(String txtFilePath, ActionEvent e)
            {
                writeFile(txtFilePath, pubKey);
                count++;
                DefaultComboBoxModel<HDAccount> comboModel = new DefaultComboBoxModel<>();
                Logger logger = LoggerFactory.getLogger(PnlWalletScreen.class);
                WalletService currentService = null;
                currentService = WalletManager.get().getCurentWalletService();
                try {
                    HDAccount lastAccount = currentService.getLastAccount();
                    DlgCreateWatchOnlyWalletAcount dlgCreateAccount = new DlgCreateWatchOnlyWalletAcount(currentService);
                    dlgCreateAccount.centerOnScreen();
                    dlgCreateAccount.setVisible(true);
                    HDAccount accountAdded = currentService.getLastAccount();
                    if (!lastAccount.toString().equals(accountAdded.toString())) {
                        logger.debug("Account added=" + accountAdded.toString());
                        comboModel.addElement(accountAdded);
                        comboModel.setSelectedItem(accountAdded);
                        DlgCreateWallet.setSuccessMsg("Account Created Successfully");
                    }
                } catch (Exception ex) {
                    logger.error("Error creating account: {}", ex.getMessage(), e);
                    ApplicationUI.get().showError("Error creating Account: ", ex.getMessage());
                }
                dispose();
            }
        });
        return createButton;
    }

    private void writeFile(String filePath, String contentToWrite) {
        if(count > 1)
            return;
        File file = new File(filePath);
        try (BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(filePath))) {
            String content = contentToWrite;
            bufferWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String txtFileName) throws FileNotFoundException, IOException
    {
        String fileName = ConfigManager.CONFIG_ROOT + File.separator + txtFileName;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
           return sb.toString();
        } finally {
            br.close();
        }
    }
}
