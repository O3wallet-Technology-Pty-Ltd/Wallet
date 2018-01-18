/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs.screens;

import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.hdwallet.util.WalletUtil;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.dialogs.DlgNewPaymentForOfflineWallet;
import com.o3.bitcoin.ui.dialogs.DlgScanQRCodeForOfflineWallet;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.util.BitcoinCurrencyRateApi;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.http.HttpGetClient;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.utils.ExchangeRate;
import org.bitcoinj.utils.Fiat;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * Class that make transaction for Cold wallet
 */
public class PnlNewPaymentForOfflineWallet extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlNewPaymentScreen.class);
    private DlgNewPaymentForOfflineWallet dlgNewPaymnetForOfflineWallet;
    private final WalletService service;
    private HDAccount mSelectedAccount = null;
    private boolean paymentPending = false;
    private Coin balance = Coin.ZERO;
    private KeyParameter key;
    private boolean btcFocus = false;
    private boolean fiatFocus = false;
    private boolean loading = true;

    private Coin maxFee = Coin.parseCoin("0.01");
    private Coin minFee = Coin.parseCoin("0.00015");
    private Coin selectedFeePerKB = minFee;
    private Coin fastestFee = minFee;
    private Coin mediumFee = minFee;
    private Coin lowFee = minFee;

    private final DefaultComboBoxModel<HDAccount> comboModel = new DefaultComboBoxModel<>();

    /**
     * Creates new form PnlNewPaymentForOfflineWallet
     */
    public PnlNewPaymentForOfflineWallet(DlgNewPaymentForOfflineWallet dlgNewPayment, WalletService service) {

        this.dlgNewPaymnetForOfflineWallet = dlgNewPayment;
        this.service = service;
        initComponents();
        loadAccounts();

        lblProgress.setText("Getting fee information, please wait");
        pnlProgress.setVisible(true);
        dlgNewPayment.disablePaymentButton();
        getFeeInfo();
        if (BitcoinCurrencyRateApi.currentRate != null) {
            txtBTC.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent de) {
                    if (txtBTC.getText().isEmpty()) {
                        if (btcFocus) {
                            txtFiat.setText("");
                        }
                        return;
                    }
                    if (txtBTC.getText().equals(".")) {
                        if (btcFocus) {
                            txtBTC.setCaretPosition(1);
                        }
                        return;
                    }
                    if (btcFocus) {
                        Double fiatValue = (Double.parseDouble(txtBTC.getText()) * BitcoinCurrencyRateApi.currentRate.getValue());
                        txtFiat.setText(String.format("%.2f", fiatValue));
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    if (txtBTC.getText().isEmpty()) {
                        if (btcFocus) {
                            txtFiat.setText("");
                        }
                        return;
                    }
                    if (txtBTC.getText().equals(".")) {
                        if (btcFocus) {
                            return;
                        }
                    }
                    if (btcFocus) {
                        Double fiatValue = (Double.parseDouble(txtBTC.getText()) * BitcoinCurrencyRateApi.currentRate.getValue());
                        txtFiat.setText(String.format("%.2f", fiatValue));
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    if (txtBTC.getText().isEmpty()) {
                        if (btcFocus) {
                            txtFiat.setText("");
                        }
                        return;
                    }
                    if (txtBTC.getText().equals(".")) {
                        if (btcFocus) {
                            return;
                        }
                    }
                    if (btcFocus) {
                        Double fiatValue = (Double.parseDouble(txtBTC.getText()) * BitcoinCurrencyRateApi.currentRate.getValue());
                        txtFiat.setText(String.format("%.2f", fiatValue));
                    }
                }
            });

            txtFiat.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent de) {
                    if (txtFiat.getText().isEmpty()) {
                        if (fiatFocus) {
                            txtBTC.setText("");
                        }
                        return;
                    }
                    if (txtFiat.getText().equals(".")) {
                        return;
                    }

                    if (fiatFocus) {
                        Double btcValue = (Double.parseDouble(txtFiat.getText()) / BitcoinCurrencyRateApi.currentRate.getValue());
                        txtBTC.setText(String.format("%.6f", btcValue));
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    if (txtFiat.getText().isEmpty()) {
                        if (fiatFocus) {
                            txtBTC.setText("");
                        }
                        return;
                    }
                    if (txtFiat.getText().equals(".")) {
                        return;
                    }
                    if (fiatFocus) {
                        Double btcValue = (Double.parseDouble(txtFiat.getText()) / BitcoinCurrencyRateApi.currentRate.getValue());
                        txtBTC.setText(String.format("%.6f", btcValue));
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    if (txtFiat.getText().isEmpty()) {
                        if (fiatFocus) {
                            txtBTC.setText("");
                        }
                        return;
                    }
                    if (txtFiat.getText().equals(".")) {
                        return;
                    }
                    if (fiatFocus) {
                        Double btcValue = (Double.parseDouble(txtFiat.getText()) / BitcoinCurrencyRateApi.currentRate.getValue());
                        txtBTC.setText(String.format("%.6f", btcValue));
                    }
                }
            });
        }
        String selectedCurrency = ConfigManager.config().getSelectedCurrency();
        if (selectedCurrency != null && !selectedCurrency.isEmpty()) {
            lblFiat.setText("Fiat (" + selectedCurrency + ")");
        }
        lblSpeed.setVisible(false);
        cmbSpeed.setVisible(false);
        lblfeeLabel.setVisible(false);
        lblFeeValue.setVisible(false);
    }

    /**
     * function that loads and show accounts in Accounts combo
     */
    private void loadAccounts() {
        List<HDAccount> accounts = service.getAllAccounts();
        for (HDAccount account : accounts) {
            comboModel.addElement(account);
        }

        WalletService wService = WalletManager.get().getCurentWalletService();
        if (wService != null) {
            int index = comboModel.getIndexOf(wService.getCurrentAccount());
            if (index >= 0) {
                cmbAccounts.setSelectedIndex(1);
            }
        }
    }
    public void setReceiveAddress(String address) {

        txtAddress.setText(address);
    }

    public void setAmount(String amount) {
        float btcAmount = (float) (Long.parseLong(amount) / 100000000.0f);
        txtBTC.setText(String.format("%.5f", btcAmount));
        try {
            Double fiatValue = (Double.parseDouble(txtBTC.getText()) * BitcoinCurrencyRateApi.currentRate.getValue());
            txtFiat.setText(String.format("%.2f", fiatValue));
        } catch (Exception e) {
            txtFiat.setText("");
        }
    }

    /**
     * function that validates ui form data
     */
    public void validateData() {
        if (txtAddress.getText() == null || txtAddress.getText().isEmpty()) {
            if(DlgScanQRCodeForOfflineWallet.isScanned == true)
            {
                DlgScanQRCodeForOfflineWallet.isScanned = false;
                txtAddress.requestFocusInWindow();
                throw new IllegalArgumentException("Generate QRCode, Sign Transaction From Android Wallet, Scan QRCode To Proceed");
            }
            else
            {
                txtAddress.requestFocusInWindow();
                throw new IllegalArgumentException("Receiver's Address is required.");
            }
        }
         else {
            try {
                new Address(service.getNetworkParameters(), txtAddress.getText().trim());
            } catch (Exception e) {
                txtAddress.requestFocusInWindow();
                throw new IllegalArgumentException("Not a valid Receiver's address.");
            }
        }
        if (lblEditDescription.getText() == null || lblEditDescription.getText().isEmpty()) {
            lblEditDescription.setText("");
        }
        Coin amount;
        if (txtBTC.getText() == null || txtBTC.getText().isEmpty()) {
            txtBTC.requestFocusInWindow();
            throw new IllegalArgumentException("Amount is required.");
        } else {
            try {
                amount = Coin.parseCoin(txtBTC.getText());
            } catch (Exception e) {
                txtBTC.requestFocusInWindow();
                throw new IllegalArgumentException("Payment amount is not valid.");
            }
        }

        balance = WalletUtil.satoshiToBTC(mSelectedAccount.balance());
        if (balance.isLessThan(amount)) {
            txtBTC.requestFocusInWindow();
            throw new IllegalArgumentException("Payment amount is greater than available balance.");
        }

        Coin feePerKB;
        if (lblFeeValue.getText() == null || lblFeeValue.getText().isEmpty()) {
            lblFeeValue.requestFocusInWindow();
            throw new IllegalArgumentException("Fee Per KB is required.");
        } else {
            try {
                System.out.println("lblFeeValue=" + lblFeeValue.getText());
                feePerKB = Coin.parseCoin(lblFeeValue.getText().trim());
            } catch (Exception e) {
                lblFeeValue.requestFocusInWindow();
                throw new IllegalArgumentException("Fee Per KB amount is not valid.");
            }
        }

        if (feePerKB.isGreaterThan(maxFee) || feePerKB.isLessThan(minFee)) {
            throw new IllegalArgumentException("Maximum Fee Per KB - 0.01, Minimum Fee Per KB - 0.00015");
        }
        if (service.getWallet().isEncrypted()) {
            try {
                KeyCrypter crypter = service.getWallet().getKeyCrypter();
                key = crypter.deriveKey(new String(WalletManager.walletPassword));
                if (!service.getWallet().checkAESKey(key)) {
                    throw new ClientRuntimeException();
                }
            } catch (Exception e) {
                logger.error("Encrypter Error: {}", e.toString(), e);
                throw new IllegalArgumentException("Incorrect Passphrase.");
            }
        }
    }

    /**
     * function that make payment
     */
    public void payCoins() {
        validateData();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createPayment();
            }
        });
    }

    /**
     * function that creates payment request and monitor for payment
     */
    private void createPayment() {
        int numConnectedPeer = service.getNumberOfConnectedPeers();
        if (numConnectedPeer == 0) {
            ApplicationUI.get().showError("No connected peer found, please try later");
            return;
        }
        try {
            Address address = new Address(service.getNetworkParameters(), txtAddress.getText().trim());
            Coin amount = Coin.parseCoin(txtBTC.getText());

            ExchangeRate exchangeRate = new ExchangeRate(Fiat.parseFiat(ConfigManager.config().getSelectedCurrency(), String.format("%.2f", BitcoinCurrencyRateApi.get().getCurrentRateValue())));
            System.out.println("_____________" + exchangeRate.fiat + "_____________");

            final Wallet.SendRequest req;
            final Wallet.SendResult res;
            req = Wallet.SendRequest.to(address, amount);
            req.changeAddress = mSelectedAccount.nextChangeAddress();
            req.coinSelector = mSelectedAccount.coinSelector(false);
            req.feePerKb = Coin.parseCoin(lblFeeValue.getText());
            req.memo = lblEditDescription.getText();
            req.exchangeRate = exchangeRate;

            if (service.getWallet().isEncrypted()) {
                req.aesKey = key;
            }
            res = service.getWallet().sendCoins(req);
            String transHash = res.tx.getHashAsString();
            if (transHash != null && !transHash.isEmpty()) {
                logger.info("Payment Info:: Transaction Hash = " + transHash + ", Amount Sent = " + txtBTC.getText() + ", To Address=" + txtAddress.getText());
                YesNoDialog dialog = new YesNoDialog("Payment", "Payment sent", false);
                dialog.start();
            } else {
                JOptionPane.showMessageDialog(null, "FATAL ERROR: " + "Unable to send payment", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            dlgNewPaymnetForOfflineWallet.dispose();
        } catch (ECKey.KeyIsEncryptedException e) {
            setPaymentPending(false);
            logger.error("Error occured while making payment: {}", e.getMessage(), e);
        } catch (ECKey.MissingPrivateKeyException e) {
            setPaymentPending(false);
            logger.error("Error occured whlie broadcasting payment: {}", e.toString(), e);
            ApplicationUI.get().showError("Payment are not allowed for Watch-Only wallets.");
            dlgNewPaymnetForOfflineWallet.markWatchOnly();
        } catch (Exception e) {
            setPaymentPending(false);
            logger.error("Error occured while broadcasting payment: {}", e, e);
            ApplicationUI.get().showError("Error occured while making payment: " + e.getMessage());
        }
    }

    private void setPaymentPending(boolean paymentPending) {
        this.paymentPending = paymentPending;
        txtAddress.setEditable(!paymentPending);
        txtFiat.setEditable(!paymentPending);
        txtBTC.setEditable(!paymentPending);
        pnlProgress.setVisible(paymentPending);
        dlgNewPaymnetForOfflineWallet.markPaymentPending(paymentPending);
    }

//    private void setPaymentSuccess(Wallet.SendResult res) {
//        this.paymentPending = false;
//        progressBar.setIndeterminate(false);
//        progressBar.setMinimum(0);
//        progressBar.setMaximum(100);
//        progressBar.setValue(100);
//        lblProgress.setText("Payment made successfully.");
//        dlgNewPaymnet.complete(res);
//    }
    /**
     * function that shows payment progress
     *
     * @param seenByPeersCount
     */
//   
    public boolean isPaymentPending() {
        return paymentPending;
    }

    private void getFeeInfo() {
        new Thread(new Runnable() {
            int fFee;
            int mFee;
            int lFee;

            @Override
            public void run() {
                try {
                    String feeDetails = HttpGetClient.getFee(ResourcesProvider.FEE_PREF_URLs.get(ConfigManager.config().getSelectedFeePref()));
                    if (feeDetails != null && !feeDetails.isEmpty()) {
                        JSONObject json = new JSONObject(feeDetails);
                        if (json.has("fastestFee") && json.has("halfHourFee") && json.has("hourFee")) {
                            fFee = json.getInt("fastestFee");// per byte satoshi
                            mFee = json.getInt("halfHourFee");
                            lFee = json.getInt("hourFee");
                            // convert to satoshi per kb
                            fFee *= 1000;
                            mFee *= 1000;
                            lFee *= 1000;
                        } else if (json.has("high_fee_per_kb") && json.has("medium_fee_per_kb") && json.has("low_fee_per_kb")) {
                            fFee = json.getInt("high_fee_per_kb");
                            mFee = json.getInt("medium_fee_per_kb");
                            lFee = json.getInt("low_fee_per_kb");
                        } else {
                            pnlProgress.setVisible(false);
                            YesNoDialog dialog = new YesNoDialog("Fee Error", "Unable to get Fee information, setting default fee", false);
                            dialog.start();
                            dlgNewPaymnetForOfflineWallet.enablePaymentButton();
                            lblFeeValue.setText(String.format("%.5f", (float) minFee.longValue() / 100000000.0));

                            lblSpeed.setVisible(false);
                            cmbSpeed.setVisible(false);

                            lblfeeLabel.setVisible(true);
                            lblFeeValue.setVisible(true);

                            dlgNewPaymnetForOfflineWallet.pack();
                            return;

                        }
                        fastestFee = Coin.valueOf(fFee);
                        mediumFee = Coin.valueOf(mFee);
                        lowFee = Coin.valueOf(lFee);
                        //apply limits
                        if (fastestFee.isGreaterThan(maxFee)) {
                            fastestFee = maxFee;
                        }
                        if (mediumFee.isGreaterThan(maxFee)) {
                            mediumFee = maxFee;
                        }
                        if (lowFee.isGreaterThan(maxFee)) {
                            lowFee = maxFee;
                        }
                        if (fastestFee.isLessThan(minFee)) {
                            fastestFee = minFee;
                        }
                        if (mediumFee.isLessThan(minFee)) {
                            mediumFee = minFee;
                        }
                        if (lowFee.isLessThan(minFee)) {
                            lowFee = minFee;
                        }
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                lblSpeed.setVisible(true);
                                cmbSpeed.setVisible(true);
                                lblfeeLabel.setVisible(true);
                                lblFeeValue.setVisible(true);

                                dlgNewPaymnetForOfflineWallet.enablePaymentButton();
                                lblFeeValue.setText(String.format("%.5f", (float) lowFee.longValue() / 100000000.0));
                                selectedFeePerKB = mediumFee;
                                lblProgress.setVisible(false);
                                progressBar.setVisible(false);
                                pnlProgress.setVisible(false);
                                dlgNewPaymnetForOfflineWallet.pack();
                                loading = false;
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("Fee Exception=" + e.getMessage());
                    pnlProgress.setVisible(false);
                    YesNoDialog dialog = new YesNoDialog("Fee Error", e.getMessage() + "<br>setting default fee", false);
                    dialog.start();
                    dlgNewPaymnetForOfflineWallet.enablePaymentButton();
                    lblFeeValue.setText(String.format("%.5f", (float) minFee.longValue() / 100000000.0));
                    lblSpeed.setVisible(false);
                    cmbSpeed.setVisible(false);
                    lblfeeLabel.setVisible(true);
                    lblFeeValue.setVisible(true);
                    dlgNewPaymnetForOfflineWallet.pack();
                    return;
                }
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlProgress = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        pnlProgress1 = new javax.swing.JPanel();
        lblAmount = new javax.swing.JLabel();
        lblAccount = new javax.swing.JLabel();
        cmbAccounts = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        lblBTC = new javax.swing.JLabel();
        lblFiat = new javax.swing.JLabel();
        txtBTC = new javax.swing.JTextField();
        txtFiat = new javax.swing.JTextField();
        lblfeeLabel = new javax.swing.JLabel();
        lblSpeed = new javax.swing.JLabel();
        cmbSpeed = new javax.swing.JComboBox();
        lblFeeValue = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        lblProgress = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblEditDescription = new javax.swing.JTextField();
        transactionStatus = new javax.swing.JLabel();
        transactionStatusSigned = new javax.swing.JLabel();

        pnlProgress.setLayout(new java.awt.BorderLayout());

        jPanel2.setOpaque(false);
        jPanel2.setRequestFocusEnabled(false);
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAddress.setText("Receiver's Address:");
        jPanel2.add(lblAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, 31));

        txtAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtAddress.setPreferredSize(new java.awt.Dimension(275, 31));
        jPanel2.add(txtAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 280, -1));

        pnlProgress1.setLayout(new java.awt.BorderLayout());
        jPanel2.add(pnlProgress1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 257, 418, -1));

        lblAmount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAmount.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAmount.setText("Amount:");
        jPanel2.add(lblAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 95, 62));

        lblAccount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAccount.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAccount.setText("From Account:");
        lblAccount.setMaximumSize(new java.awt.Dimension(59, 14));
        lblAccount.setMinimumSize(new java.awt.Dimension(59, 14));
        jPanel2.add(lblAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 95, 31));

        cmbAccounts.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbAccounts.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbAccounts.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbAccounts.setModel(comboModel);
        cmbAccounts.setPreferredSize(new java.awt.Dimension(275, 31));
        cmbAccounts.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAccountsItemStateChanged(evt);
            }
        });
        cmbAccounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAccountsActionPerformed(evt);
            }
        });
        jPanel2.add(cmbAccounts, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 280, -1));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblBTC.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblBTC.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblBTC.setText("BTC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblBTC, gridBagConstraints);

        lblFiat.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblFiat.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblFiat.setText("Fiat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblFiat, gridBagConstraints);

        txtBTC.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtBTC.setPreferredSize(new java.awt.Dimension(59, 33));
        txtBTC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBTCFocusGained(evt);
            }
        });
        txtBTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBTCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel1.add(txtBTC, gridBagConstraints);

        txtFiat.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtFiat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFiatFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(txtFiat, gridBagConstraints);

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 160, 280, -1));

        lblfeeLabel.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblfeeLabel.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblfeeLabel.setText("Fee Per KB (BTC):");
        jPanel2.add(lblfeeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 130, 31));

        lblSpeed.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblSpeed.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblSpeed.setText("Transaction Speed:");
        jPanel2.add(lblSpeed, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 130, 31));

        cmbSpeed.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbSpeed.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbSpeed.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbSpeed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Medium", "Fast" }));
        cmbSpeed.setPreferredSize(new java.awt.Dimension(275, 31));
        cmbSpeed.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSpeedItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbSpeed, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 230, 280, -1));

        lblFeeValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblFeeValue.setPreferredSize(new java.awt.Dimension(275, 31));
        jPanel2.add(lblFeeValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 270, 280, -1));

        progressBar.setForeground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new java.awt.Dimension(146, 25));
        jPanel2.add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 418, -1));

        lblProgress.setText("Payment sent, you may close the dialog or wait for verification from bitcoin network");
        lblProgress.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        jPanel2.add(lblProgress, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 418, -1));

        lblDescription.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblDescription.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblDescription.setText("Description:");
        jPanel2.add(lblDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 90, 40));

        lblEditDescription.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblEditDescription.setPreferredSize(new java.awt.Dimension(275, 31));
        jPanel2.add(lblEditDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, 280, 30));

        transactionStatus.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        transactionStatus.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        transactionStatus.setText("Status: ");
        transactionStatus.setPreferredSize(new java.awt.Dimension(99, 16));
        jPanel2.add(transactionStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 130, 20));

        transactionStatusSigned.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        transactionStatusSigned.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        jPanel2.add(transactionStatusSigned, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 280, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(pnlProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(362, 362, 362)
                .addComponent(pnlProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbAccountsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAccountsItemStateChanged
        // TODO add your handling code here:
        mSelectedAccount = (HDAccount) cmbAccounts.getSelectedItem();
    }//GEN-LAST:event_cmbAccountsItemStateChanged

    private void cmbAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAccountsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAccountsActionPerformed


    private void txtBTCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBTCFocusGained
        // TODO add your handling code here:
        btcFocus = true;
        fiatFocus = false;
    }//GEN-LAST:event_txtBTCFocusGained

    private void txtBTCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBTCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBTCActionPerformed

    private void txtFiatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFiatFocusGained
        // TODO add your handling code here:
        btcFocus = false;
        fiatFocus = true;
    }//GEN-LAST:event_txtFiatFocusGained

    private void cmbSpeedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSpeedItemStateChanged
        // TODO add your handling code here:
        if (loading) {
            return;
        }
        String speed = (String) cmbSpeed.getSelectedItem();
        if (speed.equalsIgnoreCase("Fast")) {
            lblFeeValue.setText(String.format("%.5f", (float) fastestFee.longValue() / 100000000.0));
            selectedFeePerKB = fastestFee;
        } else if (speed.equalsIgnoreCase("Medium")) {
            lblFeeValue.setText(String.format("%.5f", (float) mediumFee.longValue() / 100000000.0));
            selectedFeePerKB = mediumFee;
        } else {
            lblFeeValue.setText(String.format("%.5f", (float) lowFee.longValue() / 100000000.0));
            selectedFeePerKB = lowFee;
        }
    }//GEN-LAST:event_cmbSpeedItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox cmbAccounts;
    public static javax.swing.JComboBox cmbSpeed;
    private javax.swing.JPanel jPanel1;
    public static javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAccount;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblAmount;
    private javax.swing.JLabel lblBTC;
    private javax.swing.JLabel lblDescription;
    public static javax.swing.JTextField lblEditDescription;
    public static javax.swing.JTextField lblFeeValue;
    private javax.swing.JLabel lblFiat;
    private javax.swing.JLabel lblProgress;
    public static javax.swing.JLabel lblSpeed;
    private javax.swing.JLabel lblfeeLabel;
    private javax.swing.JPanel pnlProgress;
    private javax.swing.JPanel pnlProgress1;
    public static javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel transactionStatus;
    public static javax.swing.JLabel transactionStatusSigned;
    public static javax.swing.JTextField txtAddress;
    public static javax.swing.JTextField txtBTC;
    public static javax.swing.JTextField txtFiat;
    // End of variables declaration//GEN-END:variables
}
