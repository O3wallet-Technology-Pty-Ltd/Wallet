/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.github.sarxos.webcam.Webcam;
import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.DlgExchangeTransaction;
import com.o3.bitcoin.ui.dialogs.DlgScanQRCode;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.http.HttpGetClient;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class PnlShapshiftIOExchangeDividerScreen extends javax.swing.JPanel implements BasicScreen{

    private static final Logger logger = LoggerFactory.getLogger(pnlExchangeScreen.class);
    private WalletService currentService = null;
    private HDAccount currentAccount = null;
    private final DefaultComboBoxModel<HDAccount> model = new DefaultComboBoxModel<>();
    
    private static Timer marketInfoTimer=null;
    private double maxLimit;
    private double minLimit;
    private double minerFee;
    private double instantRate;
    private String  selectedCurrecy = "Ripple";
    private String depositCurrencyShortCode="";
    
    /**
     * Creates new form PnlShapshiftIOExchangeDividerScreen
     */
    public PnlShapshiftIOExchangeDividerScreen() {
        initComponents();
        customizeUI();
    }

     private void startMarketInfoTimer() {
        if (marketInfoTimer == null) {
            marketInfoTimer = new Timer(30000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    lblProcessing.setVisible(true);
                    depositCurrencyShortCode = ResourcesProvider.CURRENCY_SHORT_CODES.get(selectedCurrecy);
                    getMarketInfo();
                }
            });
            marketInfoTimer.start();
        }
        else{
            marketInfoTimer.start();
        }
    }
    
     
    public void loadData() {
        lblProcessing.setVisible(true);
        btnStartTransaction.setEnabled(false);
        rdoBtnQuick.setSelected(true);
        lblBitcoinAmount.setEnabled(false);
        txtBitcoinAmountValue.setEnabled(false);
        txtBitcoinAmountValue.setBackground(new Color(238,238,238));
        currentService = WalletManager.get().getCurentWalletService();
        model.removeAllElements();
        List<HDAccount> accounts = WalletManager.get().getCurentWalletService().getAllAccounts();
        for (HDAccount account : accounts) {
            model.addElement(account);
        }
       depositCurrencyShortCode = ResourcesProvider.CURRENCY_SHORT_CODES.get(selectedCurrecy);
       getMarketInfo();
       startMarketInfoTimer();
    }
    
    /**
     * function that customize look and feel of controls on settings screen
     */
    private void customizeUI() {
        themeWalletActionButton(btnStartTransaction, ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        cmbDeposit.setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                list.setSelectionBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
                list.setSelectionForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
                JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
                java.net.URL imgURL = getClass().getResource(ResourcesProvider.CURRENCY_ICONS.get(value));
                component.setIcon(new ImageIcon(imgURL));
                if (isSelected) {
                    component.setForeground(Color.WHITE);
                    component.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
                }
               // component.setPreferredSize(new Dimension(component.getPreferredSize().height, 30));
                return component;
            }
        });
    }
    
    
     /**
     * function that apply theme to button 
     * @param button
     * @param background 
     */
    private void themeWalletActionButton(JButton button, Color background) {
        XButtonFactory
                .themedButton(button)
                .color(Color.WHITE)
                .background(background)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
    }
    public static void stopMarketInfoTimer(){
        if(marketInfoTimer != null){
            if(marketInfoTimer.isRunning())
                marketInfoTimer.stop();
            System.out.println("Timer Stoped..");
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
        java.awt.GridBagConstraints gridBagConstraints;

        btnGroupTransaction = new javax.swing.ButtonGroup();
        pnlMain = new javax.swing.JPanel();
        pnlDeposit = new javax.swing.JPanel();
        lblReceive = new javax.swing.JLabel();
        lblDeposit = new javax.swing.JLabel();
        lblBitcoin = new javax.swing.JLabel();
        cmbDeposit = new javax.swing.JComboBox();
        lblTransactionType = new javax.swing.JLabel();
        cmbWallets = new javax.swing.JComboBox();
        lblSelectAccount = new javax.swing.JLabel();
        lblLitcoinAddress = new javax.swing.JLabel();
        txtWithdrawalAddress = new javax.swing.JTextField();
        lblRefundAddress = new javax.swing.JLabel();
        lblBitcoinAmount = new javax.swing.JLabel();
        txtBitcoinAmountValue = new javax.swing.JTextField();
        lblInstantValue1 = new javax.swing.JLabel();
        lblDepositMin = new javax.swing.JLabel();
        lblDepositMax = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblMinerFeeValue = new javax.swing.JLabel();
        lblMaxDepositValue = new javax.swing.JLabel();
        lblMinDepositValue = new javax.swing.JLabel();
        lblInstantRateValue = new javax.swing.JLabel();
        pnlTransactionType = new javax.swing.JPanel();
        rdoBtnQuick = new javax.swing.JRadioButton();
        rdoBtnPrecise = new javax.swing.JRadioButton();
        lblProcessing = new javax.swing.JLabel();
        pnlStartTransaction = new javax.swing.JPanel();
        btnStartTransaction = new javax.swing.JButton();
        pnlSpacer = new javax.swing.JPanel();
        lblInstantValue = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtRefundAddress = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        pnlMain.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ShapeShift", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, ResourcesProvider.Fonts.BOLD_LARGE_FONT, ResourcesProvider.Colors.DEFAULT_HEADING_COLOR));
        pnlMain.setMinimumSize(new java.awt.Dimension(800, 340));
        pnlMain.setPreferredSize(new java.awt.Dimension(800, 340));
        pnlMain.setLayout(new java.awt.GridBagLayout());

        pnlDeposit.setMinimumSize(new java.awt.Dimension(275, 60));
        pnlDeposit.setPreferredSize(new java.awt.Dimension(275, 60));
        pnlDeposit.setLayout(new java.awt.GridBagLayout());

        lblReceive.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblReceive.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblReceive.setText("Receive");
        lblReceive.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lblReceive.setMinimumSize(new java.awt.Dimension(57, 49));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.5;
        pnlDeposit.add(lblReceive, gridBagConstraints);

        lblDeposit.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblDeposit.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblDeposit.setText("Deposit");
        lblDeposit.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lblDeposit.setMinimumSize(new java.awt.Dimension(57, 49));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.5;
        pnlDeposit.add(lblDeposit, gridBagConstraints);

        lblBitcoin.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblBitcoin.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblBitcoin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBitcoin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bitcoin-24x24.png"))); // NOI18N
        lblBitcoin.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblBitcoin.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lblBitcoin.setMinimumSize(new java.awt.Dimension(57, 49));
        lblBitcoin.setPreferredSize(new java.awt.Dimension(57, 55));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5;
        pnlDeposit.add(lblBitcoin, gridBagConstraints);

        cmbDeposit.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbDeposit.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbDeposit.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbDeposit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ripple", "BitCrystals", "Blackcoin", "Bitshares", "Clams", "Dash", "Dogecoin", "Digibyte", "Emercoin", "Ether", "Factoids", "GEMZ", "Litecoin", "Monacoin", "Nubits", "Novacoin", "Nxt", "Peercoin", "Reddcoin", "Shadowcash", "StorjX", "Startcoin", "Vertcoin", "Counterparty", "Monero" }));
        cmbDeposit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbDeposit.setPreferredSize(new java.awt.Dimension(80, 10));
        cmbDeposit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbDepositItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        pnlDeposit.add(cmbDeposit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(pnlDeposit, gridBagConstraints);

        lblTransactionType.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblTransactionType.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblTransactionType.setText("Select Transaction Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblTransactionType, gridBagConstraints);

        cmbWallets.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbWallets.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbWallets.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbWallets.setModel(model);
        cmbWallets.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbWallets.setPreferredSize(new java.awt.Dimension(310, 31));
        cmbWallets.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbWalletsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(cmbWallets, gridBagConstraints);

        lblSelectAccount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblSelectAccount.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblSelectAccount.setText("Select Account:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblSelectAccount, gridBagConstraints);

        lblLitcoinAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblLitcoinAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblLitcoinAddress.setText("Bitcoin Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblLitcoinAddress, gridBagConstraints);

        txtWithdrawalAddress.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        txtWithdrawalAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtWithdrawalAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        txtWithdrawalAddress.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txtWithdrawalAddress.setPreferredSize(new java.awt.Dimension(310, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(txtWithdrawalAddress, gridBagConstraints);

        lblRefundAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblRefundAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblRefundAddress.setText("Refund Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblRefundAddress, gridBagConstraints);

        lblBitcoinAmount.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblBitcoinAmount.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblBitcoinAmount.setText("Bitcoin Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        pnlMain.add(lblBitcoinAmount, gridBagConstraints);

        txtBitcoinAmountValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtBitcoinAmountValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txtBitcoinAmountValue.setPreferredSize(new java.awt.Dimension(310, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(txtBitcoinAmountValue, gridBagConstraints);

        lblInstantValue1.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblInstantValue1.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblInstantValue1.setText("Instant Rate: ");
        lblInstantValue1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 8, 5);
        pnlMain.add(lblInstantValue1, gridBagConstraints);

        lblDepositMin.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblDepositMin.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblDepositMin.setText("Deposit Min:");
        lblDepositMin.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lblDepositMin.setPreferredSize(new java.awt.Dimension(68, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblDepositMin, gridBagConstraints);

        lblDepositMax.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblDepositMax.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblDepositMax.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDepositMax.setText("Deposit Max:");
        lblDepositMax.setPreferredSize(new java.awt.Dimension(68, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblDepositMax, gridBagConstraints);

        jLabel3.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        jLabel3.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Miner Fee:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(jLabel3, gridBagConstraints);

        lblMinerFeeValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblMinerFeeValue.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblMinerFeeValue.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblMinerFeeValue, gridBagConstraints);

        lblMaxDepositValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblMaxDepositValue.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblMaxDepositValue.setText("...");
        lblMaxDepositValue.setPreferredSize(new java.awt.Dimension(12, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblMaxDepositValue, gridBagConstraints);

        lblMinDepositValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblMinDepositValue.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblMinDepositValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMinDepositValue.setText("...");
        lblMinDepositValue.setPreferredSize(new java.awt.Dimension(12, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblMinDepositValue, gridBagConstraints);

        lblInstantRateValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblInstantRateValue.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblInstantRateValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblInstantRateValue.setText("...");
        lblInstantRateValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblInstantRateValue.setPreferredSize(new java.awt.Dimension(12, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblInstantRateValue, gridBagConstraints);

        pnlTransactionType.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        btnGroupTransaction.add(rdoBtnQuick);
        rdoBtnQuick.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        rdoBtnQuick.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        rdoBtnQuick.setSelected(true);
        rdoBtnQuick.setText("Quick");
        rdoBtnQuick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBtnQuickActionPerformed(evt);
            }
        });
        pnlTransactionType.add(rdoBtnQuick);

        btnGroupTransaction.add(rdoBtnPrecise);
        rdoBtnPrecise.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        rdoBtnPrecise.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        rdoBtnPrecise.setText("Precise");
        rdoBtnPrecise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBtnPreciseActionPerformed(evt);
            }
        });
        pnlTransactionType.add(rdoBtnPrecise);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlMain.add(pnlTransactionType, gridBagConstraints);

        lblProcessing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/loading.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblProcessing, gridBagConstraints);

        pnlStartTransaction.setLayout(new java.awt.GridBagLayout());

        btnStartTransaction.setText("Start Transaction");
        btnStartTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartTransactionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 25);
        pnlStartTransaction.add(btnStartTransaction, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlMain.add(pnlStartTransaction, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlMain.add(pnlSpacer, gridBagConstraints);

        lblInstantValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblInstantValue.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblInstantValue.setText("Choose Deposit Coin: ");
        lblInstantValue.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(lblInstantValue, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel2.setForeground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        jLabel2.setText("Instant Rates are updated every 30 seconds");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(jLabel2, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setPreferredSize(new java.awt.Dimension(2, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        pnlMain.add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(199, 25));
        jPanel2.setPreferredSize(new java.awt.Dimension(320, 37));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        txtRefundAddress.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        txtRefundAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtRefundAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        txtRefundAddress.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRefundAddress.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRefundAddress.setMargin(new java.awt.Insets(0, 0, 0, 0));
        txtRefundAddress.setPreferredSize(new java.awt.Dimension(275, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(txtRefundAddress, gridBagConstraints);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/qrcode.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.setMaximumSize(new java.awt.Dimension(32, 32));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        jPanel2.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlMain.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlMain, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbDepositItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbDepositItemStateChanged
        // TODO add your handling code here:
        lblProcessing.setVisible(true);
        selectedCurrecy = (String) cmbDeposit.getSelectedItem();
        depositCurrencyShortCode = ResourcesProvider.CURRENCY_SHORT_CODES.get(selectedCurrecy);

        getMarketInfo();
    }//GEN-LAST:event_cmbDepositItemStateChanged

    private void cmbWalletsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbWalletsItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (!(evt.getItem() instanceof HDAccount)) {
                return;
            }
            HDAccount hdacct = (HDAccount) evt.getItem();
            if (hdacct != null) {
                currentAccount = hdacct;
                txtWithdrawalAddress.setText(currentAccount.nextReceiveAddress().toString());
                currentService.setCurrentAccount(currentAccount);
            }
        }
    }//GEN-LAST:event_cmbWalletsItemStateChanged

    private void rdoBtnQuickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBtnQuickActionPerformed
        // TODO add your handling code here:
        txtBitcoinAmountValue.setText("");
        lblBitcoinAmount.setEnabled(false);
        txtBitcoinAmountValue.setEnabled(false);
        txtBitcoinAmountValue.setBackground(new Color(238,238,238));
    }//GEN-LAST:event_rdoBtnQuickActionPerformed

    private void rdoBtnPreciseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBtnPreciseActionPerformed
        // TODO add your handling code here:
        lblBitcoinAmount.setEnabled(true);
        txtBitcoinAmountValue.setEnabled(true);
        txtBitcoinAmountValue.setBackground(new Color(248,246,242));
    }//GEN-LAST:event_rdoBtnPreciseActionPerformed

    private void btnStartTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartTransactionActionPerformed
        // TODO add your handling code here:
        try {
            validateData();
            /*cmbDeposit.setEnabled(false);
            lblProcessing.setVisible(true);
            btnStartTransaction.setEnabled(false);*/
            if( rdoBtnQuick.isSelected())
                makeQuickTransaction();
            else
                makePreciseTransaction();
        } catch (Exception e) {
            logger.error("Shapeshift Transaction Failed: {}", e.getMessage());
            ApplicationUI.get().showError(e);
        }
        /*cmbDeposit.setEnabled(true);
        lblProcessing.setVisible(false);
        btnStartTransaction.setEnabled(true);*/
    }//GEN-LAST:event_btnStartTransactionActionPerformed

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
            private Object txtAddress;

            @Override
            public void run() {
                DlgScanQRCode dlgSC = new DlgScanQRCode(false);
                dlgSC.centerOnScreen();
                dlgSC.setVisible(true);
                String qrCode = dlgSC.getQRCode();
                if( qrCode != null ) {
                    if(qrCode.startsWith("bitcoin:"))
                    qrCode = qrCode.substring(8);
                    if( qrCode.contains("?"))
                    qrCode = qrCode.substring(0,qrCode.indexOf("?"));
                    txtRefundAddress.setText(qrCode);
                }
            }
        });
    }//GEN-LAST:event_jLabel1MouseClicked

     private void getMarketInfo() {
        final String urlAddition = depositCurrencyShortCode.toLowerCase()+"_btc";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cmbDeposit.setEnabled(false);
                    lblProcessing.setVisible(true);
                    btnStartTransaction.setEnabled(false);
                    String url = ResourcesProvider.LTC_BTC_URLs.get("MarketInfo");
                    url = url+urlAddition;
                    System.out.println("URL..."+url);
                    String limitDetails = HttpGetClient.getValuesFromUrl(url);
                    if (limitDetails != null && !limitDetails.isEmpty()) {
                        JSONObject json = new JSONObject(limitDetails);
                        if (json.has("rate") ) {
                            maxLimit = json.getDouble("limit");
                            minLimit = json.getDouble("minimum");
                            minerFee = json.getDouble("minerFee");
                            instantRate = json.getDouble("rate");
                        } else {
                            System.out.println("result is empty");
                            lblProcessing.setVisible(false);
                            cmbDeposit.setEnabled(true);
                            YesNoDialog dialog = new YesNoDialog("Rate Error","No instant rates found", false);
                            dialog.start();
                            return;

                        }

                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                lblMinDepositValue.setText(String.format("%1.8f %s", minLimit,depositCurrencyShortCode));
                                lblMaxDepositValue.setText(String.format("%1.8f %s", maxLimit,depositCurrencyShortCode));
                                lblMinerFeeValue.setText(String.format("%1.4f BTC", minerFee));
                                lblInstantRateValue.setText(String.format("1%s = %1.8f BTC", depositCurrencyShortCode,instantRate));
                                cmbDeposit.setEnabled(true);
                                lblProcessing.setVisible(false);
                                btnStartTransaction.setEnabled(true);
                            }
                        });

                    }
                } catch (Exception e) {
                    logger.error("Min/Max Rate Exception=" + e.getMessage());
                    lblProcessing.setVisible(false);
                    cmbDeposit.setEnabled(true);
                    YesNoDialog dialog = new YesNoDialog("Rate Error",e.getMessage(), false);
                    dialog.start();   
                    return;
                }
            }
        }).start();
    }
     
      /**
     * function that validates ui form data
     */
    public void validateData() {
        String withdrawalAddress = txtWithdrawalAddress.getText() != null ? new String(txtWithdrawalAddress.getText()) : "";
        if (withdrawalAddress.isEmpty()) {
            txtWithdrawalAddress.requestFocusInWindow();
            throw new IllegalArgumentException("Withdrawal address is required.");
        }
        
         if( rdoBtnPrecise.isSelected() ) {
            String bitcoinValue = txtBitcoinAmountValue.getText() != null ? new String(txtBitcoinAmountValue.getText()) : "";
            if (bitcoinValue.isEmpty()) {
                txtBitcoinAmountValue.requestFocusInWindow();
                throw new IllegalArgumentException("Bitcoin Amount is required.");
            }
            else {
                try { 
                    Float.parseFloat(bitcoinValue);
                }catch(Exception e) {
                    txtBitcoinAmountValue.requestFocusInWindow();
                    throw new IllegalArgumentException("Invalid Bitcoin Amount.");
                }
            }
        }
    }

    public static void setDefaultCursor() {
        jLabel1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
 /**
     * function to create JSON object for ltc_btc transfer
     *
     * @param amount
     * @param returnAddress
     * @param withdrawalAddress
     * @returm JSONObject
     */
    private String makeQuickTransactionParameters(String refundAddress, String withdrawalAddress) {
        String  urlParameters;
        if( refundAddress.isEmpty())
            urlParameters ="{\"apiKey\":\""+ResourcesProvider.shapShiftPubKey+"\",\"withdrawal\":\""+withdrawalAddress+"\",\"pair\":\""+depositCurrencyShortCode.toLowerCase()+"_btc\"}";
        else
            urlParameters ="{\"apiKey\":\""+ResourcesProvider.shapShiftPubKey+"\",\"withdrawal\":\""+withdrawalAddress+"\",\"pair\":\""+depositCurrencyShortCode.toLowerCase()+"_btc\",\"returnAddress\":\""+refundAddress+"\"}";
        return urlParameters;
    }
    
    private String makePreciseTransactionParameters(String refundAddress, String withdrawalAddress, String amount) {
        String  urlParameters;
        if( refundAddress.isEmpty())
            urlParameters ="{\"apiKey\":\""+ResourcesProvider.shapShiftPubKey+"\",\"withdrawal\":\""+withdrawalAddress+"\",\"pair\":\""+depositCurrencyShortCode+"_btc\",\"amount\":\""+amount+"\"}";
        else
            urlParameters ="{\"apiKey\":\""+ResourcesProvider.shapShiftPubKey+"\",\"withdrawal\":\""+withdrawalAddress+"\",\"pair\":\""+depositCurrencyShortCode+"_btc\",\"returnAddress\":\""+refundAddress+"\",\"amount\":\""+amount+"\"}";
        return urlParameters;
    }
      private void makeQuickTransaction() {
        new Thread(new Runnable() {
            String withDrawalAddress = txtWithdrawalAddress.getText();
            String ltcRefundAddress = txtRefundAddress.getText();

            @Override
            public void run() {
                try {
                    
                    cmbDeposit.setEnabled(false);
                    lblProcessing.setVisible(true);
                    btnStartTransaction.setEnabled(false);
                    
                    String transactionParameters = makeQuickTransactionParameters(ltcRefundAddress, withDrawalAddress);
                    System.out.println("transactionParameters="+transactionParameters);
                    String transactionDetails = HttpGetClient.makeLtcToBtcTransaction(ResourcesProvider.LTC_BTC_URLs.get("QuickTrans"),transactionParameters);
                    System.out.println("transactionDetails="+transactionDetails);
                    if (transactionDetails != null && !transactionDetails.isEmpty()) {
                        final JSONObject json = new JSONObject(transactionDetails);
                        if (json.has("deposit")  && json.has("orderId") ) {
                            
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    DlgExchangeTransaction dlgExchangeTransaction = new DlgExchangeTransaction("Quick Transaction",json.getString("orderId"),json.getString("deposit"));
                                    dlgExchangeTransaction.centerOnScreen();
                                    dlgExchangeTransaction.setVisible(true);
                                }
                            });
                        } else if (json.has("error")){
                            YesNoDialog dialog = new YesNoDialog("Transaction Error", json.getString("error"), false);
                            dialog.start();
                        }else {
                            System.out.println("deposit not found");
                            YesNoDialog dialog = new YesNoDialog("Transaction Error", "Deposite Address not returned by server", false);
                            dialog.start();
                        }
                    }
                } catch (Exception e) {
                    logger.error("Transaction Exception=" + e.getMessage());
                    YesNoDialog dialog = new YesNoDialog("Transaction Error", e.getMessage(), false);
                    dialog.start();        
                }
                cmbDeposit.setEnabled(true);
                lblProcessing.setVisible(false);
                btnStartTransaction.setEnabled(true);
            }
        }).start();
    }
    
    private void makePreciseTransaction() {
        
        new Thread(new Runnable() {
            String bitcoinAmount = txtBitcoinAmountValue.getText();
            String withDrawalAddress = txtWithdrawalAddress.getText();
            String ltcRefundAddress = txtRefundAddress.getText();

            @Override
            public void run() {
                try {
                    
                    cmbDeposit.setEnabled(false);
                    lblProcessing.setVisible(true);
                    btnStartTransaction.setEnabled(false);
                    
                    String transactionParameters = makePreciseTransactionParameters(ltcRefundAddress, withDrawalAddress, bitcoinAmount);
                    System.out.println("transactionParameters="+transactionParameters);
                    String transactionDetails = HttpGetClient.makeLtcToBtcTransaction(ResourcesProvider.LTC_BTC_URLs.get("PreciseTrans"),transactionParameters);
                    System.out.println("transactionDetails="+transactionDetails);
                    if (transactionDetails != null && !transactionDetails.isEmpty()) {
                        JSONObject json = new JSONObject(transactionDetails);
                        if (json.has("success") ) {
                            final JSONObject jsonValues = json.getJSONObject("success");
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Date date = new Date(jsonValues.getLong("expiration"));
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    String expiryTime = df.format(date);
                                    DlgExchangeTransaction dlgExchangeTransaction = new DlgExchangeTransaction("Precise Transaction",jsonValues.getString("orderId"),jsonValues.getString("deposit"), jsonValues.getString("depositAmount") + " "+depositCurrencyShortCode, expiryTime);
                                    dlgExchangeTransaction.centerOnScreen();
                                    dlgExchangeTransaction.setVisible(true);
                                }
                            });
                        } else if (json.has("error")){
                            YesNoDialog dialog = new YesNoDialog("Transaction Error", json.getString("error"), false);
                            dialog.start();
                        }else {
                            System.out.println("deposit not found");
                            YesNoDialog dialog = new YesNoDialog("Transaction Error", "Deposite Address not returned by server", false);
                            dialog.start();
                        }
                    }
                } catch (Exception e) {
                    logger.error("Transaction Exception=" + e.getMessage());
                    YesNoDialog dialog = new YesNoDialog("Transaction Error", e.getMessage(), false);
                    dialog.start();        
                }
                cmbDeposit.setEnabled(true);
                lblProcessing.setVisible(false);
                btnStartTransaction.setEnabled(true);
            }
        }).start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGroupTransaction;
    private javax.swing.JButton btnStartTransaction;
    private javax.swing.JComboBox cmbDeposit;
    private javax.swing.JComboBox cmbWallets;
    public static javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBitcoin;
    private javax.swing.JLabel lblBitcoinAmount;
    private javax.swing.JLabel lblDeposit;
    private javax.swing.JLabel lblDepositMax;
    private javax.swing.JLabel lblDepositMin;
    private javax.swing.JLabel lblInstantRateValue;
    private javax.swing.JLabel lblInstantValue;
    private javax.swing.JLabel lblInstantValue1;
    private javax.swing.JLabel lblLitcoinAddress;
    private javax.swing.JLabel lblMaxDepositValue;
    private javax.swing.JLabel lblMinDepositValue;
    private javax.swing.JLabel lblMinerFeeValue;
    private javax.swing.JLabel lblProcessing;
    private javax.swing.JLabel lblReceive;
    private javax.swing.JLabel lblRefundAddress;
    private javax.swing.JLabel lblSelectAccount;
    private javax.swing.JLabel lblTransactionType;
    private javax.swing.JPanel pnlDeposit;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSpacer;
    private javax.swing.JPanel pnlStartTransaction;
    private javax.swing.JPanel pnlTransactionType;
    private javax.swing.JRadioButton rdoBtnPrecise;
    private javax.swing.JRadioButton rdoBtnQuick;
    private javax.swing.JTextField txtBitcoinAmountValue;
    private javax.swing.JTextField txtRefundAddress;
    private javax.swing.JTextField txtWithdrawalAddress;
    // End of variables declaration//GEN-END:variables
}
