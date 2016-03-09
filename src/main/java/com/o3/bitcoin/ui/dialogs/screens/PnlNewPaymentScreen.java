/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs.screens;

import com.github.sarxos.webcam.Webcam;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.hdwallet.util.WalletUtil;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.dialogs.DlgNewPayment;
import com.o3.bitcoin.ui.dialogs.DlgScanQRCode;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.util.BitcoinCurrencyRateApi;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Cursor;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionConfidence.Listener;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.utils.MonetaryFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * @author
 */

/**
 * <p>Class that creates UI form for New Payment dialog</p> 
 */
public class PnlNewPaymentScreen extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlNewPaymentScreen.class);
    private final DlgNewPayment dlgNewPaymnet;
    private final WalletService service;
    private HDAccount mSelectedAccount = null;
    private boolean paymentPending = false;
    private Coin balance = Coin.ZERO;
    private KeyParameter key;
    private boolean btcFocus = false; 
    private boolean fiatFocus = false;
    
    private final DefaultComboBoxModel<HDAccount> comboModel = new DefaultComboBoxModel<>();

    /**
     * Creates new form PnlNewPaymentScreen
     *
     * @param dlgNewPayment
     * @param service
     */
    public PnlNewPaymentScreen(DlgNewPayment dlgNewPayment, WalletService service) {
        this.dlgNewPaymnet = dlgNewPayment;
        this.service = service;
        initComponents();
        loadAccounts();
        pnlProgress.setVisible(false);
        
        txtBTC.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                if( txtBTC.getText().isEmpty() ) {
                    if( btcFocus )
                        txtFiat.setText("");
                    return;
                }
                if( txtBTC.getText().equals(".") ) {
                    if( btcFocus )
                        txtBTC.setCaretPosition(1);
                        return;
                }
                if( btcFocus ) {
                    Double fiatValue = (Double.parseDouble(txtBTC.getText())*BitcoinCurrencyRateApi.currentRate.getValue());
                    //Double truncatedValue = new BigDecimal(fiatValue ).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    //txtFiat.setText(truncatedValue+"");
                    txtFiat.setText(String.format("%.2f", fiatValue));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                if( txtBTC.getText().isEmpty() ) {
                    if( btcFocus )
                        txtFiat.setText("");
                    return;
                }
                if( txtBTC.getText().equals(".") ) {
                    if( btcFocus )
                        //txtBTC.setCaretPosition(1);
                        return;
                }
                if( btcFocus ) {
                    Double fiatValue = (Double.parseDouble(txtBTC.getText())*BitcoinCurrencyRateApi.currentRate.getValue());
                    //Double truncatedValue = new BigDecimal(fiatValue ).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    //txtFiat.setText(truncatedValue+"");
                    txtFiat.setText(String.format("%.2f", fiatValue));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                if( txtBTC.getText().isEmpty() ) {
                    if( btcFocus )
                        txtFiat.setText("");
                    return;
                }
                if( txtBTC.getText().equals(".") ) {
                    if( btcFocus )
                        //txtBTC.setCaretPosition(1);
                        return;
                }
                if( btcFocus ) {
                    Double fiatValue = (Double.parseDouble(txtBTC.getText())*BitcoinCurrencyRateApi.currentRate.getValue());
                    //Double truncatedValue = new BigDecimal(fiatValue ).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    //txtFiat.setText(truncatedValue+"");
                    txtFiat.setText(String.format("%.2f", fiatValue));
                }
            }
        });
        
        txtFiat.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                if( txtFiat.getText().isEmpty() ) {
                    if( fiatFocus ) 
                        txtBTC.setText("");
                    return;
                }
                if( txtFiat.getText().equals(".") ) {
                    return;
                }
                
                
                if( fiatFocus ) {
                    Double btcValue = (Double.parseDouble(txtFiat.getText())/BitcoinCurrencyRateApi.currentRate.getValue());
                    txtBTC.setText(String.format("%.6f", btcValue));        
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                if( txtFiat.getText().isEmpty() ) {
                    if( fiatFocus ) 
                        txtBTC.setText("");
                    return;
                }
                if( txtFiat.getText().equals(".") ) {
                    return;
                }
                if( fiatFocus ) {
                    Double btcValue = (Double.parseDouble(txtFiat.getText())/BitcoinCurrencyRateApi.currentRate.getValue());
                    txtBTC.setText(String.format("%.6f", btcValue));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                if( txtFiat.getText().isEmpty() ) {
                    if( fiatFocus ) 
                        txtBTC.setText("");
                    return;
                }
                if( txtFiat.getText().equals(".") ) {
                    return;
                }
                if( fiatFocus ) {
                    Double btcValue = (Double.parseDouble(txtFiat.getText())/BitcoinCurrencyRateApi.currentRate.getValue());
                    //Double truncatedValue = new BigDecimal(btcValue ).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                    txtBTC.setText(String.format("%.6f", btcValue));
                }
            }
        });
        String selectedCurrency = ConfigManager.config().getSelectedCurrency();
        if( selectedCurrency != null && !selectedCurrency.isEmpty() )
            lblFiat.setText("Fiat ("+selectedCurrency+")");
        float feePerKb = (float)(Wallet.SendRequest.DEFAULT_FEE_PER_KB.getValue() / 100000000.0);
        lblFeeValue.setText(String.format("%.5f", feePerKb)+" BTC");
    }
    
    /**
     * function that loads and show accounts in Accounts combo
     */
    private void loadAccounts(){
        List<HDAccount> accounts = service.getAllAccounts();
        for (HDAccount account : accounts) {
                comboModel.addElement(account);
        }
        WalletService wService = WalletManager.get().getCurentWalletService();
        if (wService != null) {
            int index = comboModel.getIndexOf(wService.getCurrentAccount());
            if (index >= 0) {
                cmbAccounts.setSelectedIndex(index);
            }
        }
    }

    /**
     * function that validates ui form data
     */
    public void validateData() {
        if (txtAddress.getText() == null || txtAddress.getText().isEmpty()) {
            txtAddress.requestFocusInWindow();
            throw new IllegalArgumentException("Receiver's Address is required.");
        } else {
            try {
                new Address(service.getNetworkParameters(), txtAddress.getText().trim());
            } catch (Exception e) {
                txtAddress.requestFocusInWindow();
                throw new IllegalArgumentException("Not a valid Receiver's address.");
            }
        }
        /*Coin amount;
        try {
            txtAmount.commitEdit();
        }catch( ParseException pex) {
            throw new IllegalArgumentException("Not a valid coin value");
        }
        if (txtAmount.getValue() == null) {
            txtAmount.requestFocusInWindow();
            throw new IllegalArgumentException("Amount is required.");
        } else {
            try {
                amount = Coin.parseCoin(txtAmount.getText());
            } catch (Exception e) {
                txtAmount.requestFocusInWindow();
                throw new IllegalArgumentException("Payment amount is not valid.");
            }
        }*/
        
        Coin amount;

        if (txtBTC.getText() == null || txtAddress.getText().isEmpty()) {
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
        if (service.getWallet().isEncrypted()) {
            try {
                KeyCrypter crypter = service.getWallet().getKeyCrypter();
                ////key = crypter.deriveKey(new String(txtPassphrase.getPassword()));
                key = crypter.deriveKey(new String(WalletManager.walletPassword));
                if (!service.getWallet().checkAESKey(key)) {
                    throw new ClientRuntimeException();
                }
            } catch (Exception e) {
                logger.error("Encrypter Error: {}", e.toString(), e);
                //txtPassphrase.requestFocusInWindow();
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
        if( numConnectedPeer == 0 )
        {
            ApplicationUI.get().showError("No connected peer found, please try later");
            return;
        }
        try {
            
            Address address = new Address(service.getNetworkParameters(), txtAddress.getText().trim());
            //Coin amount = Coin.parseCoin(txtAmount.getText());
            Coin amount = Coin.parseCoin(txtBTC.getText());
            final Wallet.SendRequest req;
            final Wallet.SendResult res;
            req = Wallet.SendRequest.to(address, amount);
            req.changeAddress = mSelectedAccount.nextChangeAddress();
            req.coinSelector = mSelectedAccount.coinSelector(false);
            if (service.getWallet().isEncrypted()) {
                req.aesKey = key;
            }
            res = service.getWallet().sendCoins(req);
            String transHash = res.tx.getHashAsString();
            if( transHash != null && !transHash.isEmpty() )
            {
                logger.info("Payment Info:: Transaction Hash = "+transHash+", Amount Sent = " + txtBTC.getText() + ", To Address="+txtAddress.getText() );
                YesNoDialog dialog = new YesNoDialog("Payment","Payment sent", false);
                dialog.start();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "FATAL ERROR: " + "Unable to send payment", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            
            dlgNewPaymnet.dispose();
            /*Futures.addCallback(res.broadcastComplete, new FutureCallback<Transaction>() {
                @Override
                public void onSuccess(Transaction result) {
                    System.out.println("Success");
                    setPaymentSuccess(res);
                }

                @Override
                public void onFailure(Throwable t) {
                    setPaymentPending(false);
                    logger.error("Error occured during making payment: {}", t.getMessage(), t);
                    ApplicationUI.get().showError("Error occured during making payment: " + t.getMessage());
                }
            });*/
            /*res.tx.getConfidence().addEventListener(new TransactionConfidence.Listener() {

//                @Override
//                public void onConfidenceChanged(TransactionConfidence confidence, Listener.ChangeReason reason) {
//                    if (reason == TransactionConfidence.Listener.ChangeReason.SEEN_PEERS) {
//                        showProgress(res.tx.getConfidence().numBroadcastPeers());
//                    }
//                }
                @Override
                public void onConfidenceChanged(Transaction tx, Listener.ChangeReason reason) {
                    if (reason == TransactionConfidence.Listener.ChangeReason.SEEN_PEERS) {
                        showProgress(res.tx.getConfidence().numBroadcastPeers());
                    }
                }

            });*/
            ////setPaymentPending(true);
            //showProgress(0);
            ////dlgNewPaymnet.pack();
        } catch (ECKey.KeyIsEncryptedException e) {
            setPaymentPending(false);
            logger.error("Error occured while making payment: {}", e.getMessage(), e);
        } catch (ECKey.MissingPrivateKeyException e) {
            setPaymentPending(false);
            logger.error("Error occured whlie broadcasting payment: {}", e.toString(), e);
            ApplicationUI.get().showError("Payment are not allowed for Watch-Only wallets.");
            dlgNewPaymnet.markWatchOnly();
        } catch (Exception e) {
            setPaymentPending(false);
            logger.error("Error occured whlie broadcasting payment: {}", e, e);
            ApplicationUI.get().showError("Error occured while making payment: " + e.getMessage());
        }
    }

    private void setPaymentPending(boolean paymentPending) {
        this.paymentPending = paymentPending;
        txtAddress.setEditable(!paymentPending);
        txtFiat.setEditable(!paymentPending);
        txtBTC.setEditable(!paymentPending);
        //txtAmount.setEditable(!paymentPending);
        //txtPassphrase.setEditable(!paymentPending);
        pnlProgress.setVisible(paymentPending);
        dlgNewPaymnet.markPaymentPending(paymentPending);
    }

    private void setPaymentSuccess(Wallet.SendResult res) {
        this.paymentPending = false;
        progressBar.setIndeterminate(false);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(100);
        lblProgress.setText("Payment made successfully.");
        dlgNewPaymnet.complete(res);
    }

    /**
     * function that shows payment progress
     * @param seenByPeersCount 
     */
    public void showProgress(final int seenByPeersCount) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (isPaymentPending()) {
                    lblProgress.setText(String.format("Transaction broadcasted. Seen by %d Peers so far ...", seenByPeersCount));
                }
            }
        });
    }

    public boolean isPaymentPending() {
        return paymentPending;
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

        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        pnlProgress = new javax.swing.JPanel();
        lblProgress = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        lblAmount = new javax.swing.JLabel();
        lblAccount = new javax.swing.JLabel();
        cmbAccounts = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblBTC = new javax.swing.JLabel();
        lblFiat = new javax.swing.JLabel();
        txtBTC = new javax.swing.JTextField();
        txtFiat = new javax.swing.JTextField();
        lblfeeLabel = new javax.swing.JLabel();
        lblFeeValue = new javax.swing.JLabel();

        setOpaque(false);
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        lblAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAddress.setText("Receiver's Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblAddress, gridBagConstraints);

        txtAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtAddress.setPreferredSize(new java.awt.Dimension(275, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtAddress, gridBagConstraints);

        pnlProgress.setLayout(new java.awt.BorderLayout());

        lblProgress.setText("Payment sent, you may close the dialog or wait for verification from bitcoin network");
        lblProgress.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        pnlProgress.add(lblProgress, java.awt.BorderLayout.CENTER);

        progressBar.setForeground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new java.awt.Dimension(146, 25));
        pnlProgress.add(progressBar, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        add(pnlProgress, gridBagConstraints);

        lblAmount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAmount.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAmount.setText("Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblAmount, gridBagConstraints);

        lblAccount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAccount.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblAccount.setText("From Account:");
        lblAccount.setMaximumSize(new java.awt.Dimension(59, 14));
        lblAccount.setMinimumSize(new java.awt.Dimension(59, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblAccount, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmbAccounts, gridBagConstraints);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/qrcode.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);

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
        txtBTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBTCActionPerformed(evt);
            }
        });
        txtBTC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBTCFocusGained(evt);
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);

        lblfeeLabel.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblfeeLabel.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblfeeLabel.setText("Network Fee:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblfeeLabel, gridBagConstraints);

        lblFeeValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblFeeValue.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblFeeValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbAccountsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAccountsItemStateChanged
        // TODO add your handling code here:
        mSelectedAccount = (HDAccount)cmbAccounts.getSelectedItem();
    }//GEN-LAST:event_cmbAccountsItemStateChanged

    private void cmbAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAccountsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAccountsActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        Webcam webcam = Webcam.getDefault();
        if( webcam == null ) {
            JOptionPane.showMessageDialog(null, "FATAL ERROR: No Webcam found", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        webcam.close();
        jLabel1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DlgScanQRCode dlgSC = new DlgScanQRCode();
                dlgSC.centerOnScreen();
                dlgSC.setVisible(true);
                String qrCode = dlgSC.getQRCode();
                if( qrCode != null ) {
                    if(qrCode.startsWith("bitcoin:"))
                        qrCode = qrCode.substring(8);
                    if( qrCode.contains("?"))
                        qrCode = qrCode.substring(0,qrCode.indexOf("?"));
                    txtAddress.setText(qrCode);
                }
            }
        });
    }//GEN-LAST:event_jLabel1MouseClicked

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

    public static void setDefaultCursor() {
        jLabel1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbAccounts;
    public static javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAccount;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblAmount;
    private javax.swing.JLabel lblBTC;
    private javax.swing.JLabel lblFeeValue;
    private javax.swing.JLabel lblFiat;
    private javax.swing.JLabel lblProgress;
    private javax.swing.JLabel lblfeeLabel;
    private javax.swing.JPanel pnlProgress;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtBTC;
    private javax.swing.JTextField txtFiat;
    // End of variables declaration//GEN-END:variables
}
