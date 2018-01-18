/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.wallet;

import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.PlainTabelCellRenderer;
import com.o3.bitcoin.ui.component.PlainTableHeaderRenderer;
import com.o3.bitcoin.ui.component.WalletComboBoxUI;
import com.o3.bitcoin.ui.component.XScrollbarUI;
import com.o3.bitcoin.ui.dialogs.DlgNewPayment;
import com.o3.bitcoin.ui.dialogs.DlgWalletLoadingProgress;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.util.Utils;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.ui.component.progress.ProgressEvent;
import com.o3.bitcoin.ui.dialogs.DlgCreateNewAccount;
import com.o3.bitcoin.applications.PnlShapshiftIOExchangeDividerScreen;
import com.o3.bitcoin.ui.dialogs.DlgNewPaymentForOfflineWallet;
import com.o3.bitcoin.ui.dialogs.DlgRequestPayment;
import com.o3.bitcoin.ui.screens.exchange.PnlExchangeScreen;
//import static com.o3.bitcoin.ui.screens.settings.PnlSettingsScreen.AccountCondition;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.script.Script;
import org.bitcoinj.utils.ExchangeRate;
import org.bitcoinj.utils.MonetaryFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Class that implements Accounts Page
 */
public class PnlWalletScreen extends javax.swing.JPanel implements ActionListener, BasicScreen {

    private static final Logger logger = LoggerFactory.getLogger(PnlWalletScreen.class);
    private static final int MAX_DAYS = 15;
    private final DefaultComboBoxModel<HDAccount> comboModel = new DefaultComboBoxModel<>();
    private boolean firstLoad = true;
    private WalletService currentService = null;
    private HDAccount currentAccount = null;
    public static String LabelAddress = "";
    private static String description = null;
    private String currentExchangeRate = null;
    private final String statusCompleted = "COMPLETE";
    private final String statusPending = "PENDING";
    private final String statusConfirmed = "CONFIRMED";
    private final int columnStatusNumber = 5;
    private String status = "";
    public static boolean isWatchOnly = false;

    /**
     * Creates new form PnlWalletScreen
     */
    public PnlWalletScreen() {
        initComponents();
        prepareUI();
    }

    /**
     * function that loads accounts in combo model
     */
    public void loadData() {
        List<HDAccount> accounts = WalletManager.get().getCurentWalletService().getAllAccounts();
        comboModel.removeAllElements();// reload application
        for (HDAccount account : accounts) {
            comboModel.addElement(account);
        }
        firstLoad = false;
        currentService = WalletManager.get().getCurentWalletService();
        PnlShapshiftIOExchangeDividerScreen.stopMarketInfoTimer();
        PnlExchangeScreen.stopTimers();
    }

    /**
     * function that customize look and feel of accounts combo and Transaction
     * History table
     */
    private void prepareUI() {
        btnNewPayment.setVisible(false);
        cmbWallets.setRenderer(new BasicComboBoxRenderer() {

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

        Object child = cmbWallets.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup) child;
        popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));

        for (int i = 0; i < 7; i++) {
            TableColumn column = tblTransactions.getColumnModel().getColumn(i);
            column.setHeaderRenderer(new PlainTableHeaderRenderer());
            if (i == 1 || i == 2) {
                column.setWidth(250);
                column.setMinWidth(250);
                column.setPreferredWidth(250);
            }
        }
        tblTransactions.getTableHeader().setFont(Fonts.DEFAULT_HEADING_FONT);
        tblTransactions.getTableHeader().setForeground(Color.BLACK);
        //tblTransactions.getTableHeader().setBackground(Color.WHITE);
        tblTransactions.getTableHeader().setBackground(Colors.TABLE_HEADER_BG_COLOR);
        tblTransactions.getTableHeader().setOpaque(true);

        scrollPane.setColumnHeader(new JViewport() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = 25;
                return d;
            }
        });
        scrollPane.getHorizontalScrollBar().setUI(new XScrollbarUI());
        scrollPane.getVerticalScrollBar().setUI(new XScrollbarUI());
        //tblTransactions.setBackground(Color.red);
        tblTransactions.setRowHeight(25);
        tblTransactions.setDefaultRenderer(Object.class, new PlainTabelCellRenderer());
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

        pnlMain = new javax.swing.JPanel();
        pnlGraphs = new javax.swing.JPanel();
        pnlWalletStats = new com.o3.bitcoin.ui.screens.wallet.PnlWalletStats();
        pnlWalletGraphs = new com.o3.bitcoin.ui.screens.wallet.PnlWalletGraphs();
        pnlTransactions = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblTransactions = new javax.swing.JTable();
        lblTransactionsHistory = new javax.swing.JLabel();
        pnlTop = new javax.swing.JPanel();
        pnlTopEdge = new javax.swing.JPanel();
        pnlWallets = new javax.swing.JPanel();
        cmbWallets = new javax.swing.JComboBox();
        pnlWalletSummary = new javax.swing.JPanel();
        pnlWalletLoading = new javax.swing.JPanel();
        lblWalletLoading = new javax.swing.JLabel();
        pnlWalletControls = new javax.swing.JPanel();
        lblAddress = new javax.swing.JLabel();
        pnlNewPayment = new javax.swing.JPanel();
        btnNewPayment = new javax.swing.JLabel();
        lblQrcode = new javax.swing.JLabel();
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblAddWallet = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlMain.setOpaque(false);
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlGraphs.setOpaque(false);
        pnlGraphs.setLayout(new java.awt.BorderLayout());
        pnlGraphs.add(pnlWalletStats, java.awt.BorderLayout.NORTH);

        pnlWalletGraphs.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 20, 1));
        pnlGraphs.add(pnlWalletGraphs, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlGraphs, java.awt.BorderLayout.NORTH);

        pnlTransactions.setOpaque(false);
        pnlTransactions.setLayout(new java.awt.BorderLayout());

        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setOpaque(false);

        tblTransactions.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tblTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "To", "Decription", "Amount", "Fee", "Status", "Exchange Rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransactions.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblTransactions.setGridColor(Colors.NAV_MENU_ITEM_BORDER_COLOR);
        tblTransactions.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tblTransactions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTransactions.setShowVerticalLines(false);
        tblTransactions.getTableHeader().setReorderingAllowed(false);
        scrollPane.setViewportView(tblTransactions);

        pnlTransactions.add(scrollPane, java.awt.BorderLayout.CENTER);
        scrollPane.getViewport().setOpaque(false);

        lblTransactionsHistory.setBackground(Colors.SCREEN_TOP_PANEL_BG_COLOR);
        lblTransactionsHistory.setFont(Fonts.BOLD_LARGE_FONT);
        lblTransactionsHistory.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblTransactionsHistory.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTransactionsHistory.setText("Transactions History");
        lblTransactionsHistory.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 1));
        lblTransactionsHistory.setOpaque(true);
        pnlTransactions.add(lblTransactionsHistory, java.awt.BorderLayout.PAGE_START);

        pnlMain.add(pnlTransactions, java.awt.BorderLayout.CENTER);

        add(pnlMain, java.awt.BorderLayout.CENTER);

        pnlTop.setBackground(Colors.SCREEN_TOP_PANEL_BG_COLOR);
        pnlTop.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.NAV_MENU_ITEM_BORDER_COLOR));
        pnlTop.setPreferredSize(new java.awt.Dimension(1024, 50));
        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlTopEdge.setBackground(Colors.NAV_MENU_WALLET_COLOR);
        pnlTopEdge.setPreferredSize(new java.awt.Dimension(1024, 5));
        pnlTop.add(pnlTopEdge, java.awt.BorderLayout.NORTH);

        pnlWallets.setOpaque(false);
        pnlWallets.setLayout(new java.awt.BorderLayout());

        cmbWallets.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbWallets.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbWallets.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbWallets.setModel(comboModel);
        cmbWallets.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        cmbWallets.setUI((ComboBoxUI) WalletComboBoxUI.createUI(cmbWallets));
        cmbWallets.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbWallets.setPreferredSize(new java.awt.Dimension(175, 20));
        cmbWallets.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbWalletsItemStateChanged(evt);
            }
        });
        cmbWallets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbWalletsActionPerformed(evt);
            }
        });
        pnlWallets.add(cmbWallets, java.awt.BorderLayout.CENTER);

        pnlWalletSummary.setOpaque(false);
        pnlWalletSummary.setLayout(new java.awt.CardLayout());

        pnlWalletLoading.setOpaque(false);
        pnlWalletLoading.setLayout(new java.awt.BorderLayout());

        lblWalletLoading.setText("Loading, Please wait ...");
        lblWalletLoading.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        lblWalletLoading.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblWalletLoading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblWalletLoadingMousePressed(evt);
            }
        });
        pnlWalletLoading.add(lblWalletLoading, java.awt.BorderLayout.CENTER);

        pnlWalletSummary.add(pnlWalletLoading, "WALLET_LOADING");

        pnlWalletControls.setOpaque(false);
        pnlWalletControls.setLayout(new java.awt.BorderLayout());

        lblAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblAddress.setForeground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        lblAddress.setText("<Address>");
        lblAddress.setToolTipText("Click to copy address to clipboard");
        lblAddress.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        lblAddress.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAddress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblAddressMousePressed(evt);
            }
        });
        pnlWalletControls.add(lblAddress, java.awt.BorderLayout.CENTER);

        pnlWalletSummary.add(pnlWalletControls, "WALLET_CONTROLS");

        pnlWallets.add(pnlWalletSummary, java.awt.BorderLayout.EAST);

        pnlTop.add(pnlWallets, java.awt.BorderLayout.WEST);

        pnlNewPayment.setOpaque(false);
        pnlNewPayment.setLayout(new java.awt.GridBagLayout());

        btnNewPayment.setBackground(Colors.NAV_MENU_WALLET_COLOR);
        btnNewPayment.setFont(Fonts.BOLD_LARGE_FONT);
        btnNewPayment.setForeground(Color.WHITE);
        btnNewPayment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNewPayment.setText("+ New Payment");
        btnNewPayment.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.TABLE_CELL_BORDER_COLOR));
        btnNewPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewPayment.setOpaque(true);
        btnNewPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnNewPaymentMousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        pnlNewPayment.add(btnNewPayment, gridBagConstraints);

        lblQrcode.setBackground(Colors.SCREEN_TOP_PANEL_BG_COLOR);
        lblQrcode.setFont(Fonts.BOLD_LARGE_FONT);
        lblQrcode.setForeground(Color.WHITE);
        lblQrcode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQrcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/qrcode.png"))); // NOI18N
        lblQrcode.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.TABLE_CELL_BORDER_COLOR));
        lblQrcode.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblQrcode.setOpaque(true);
        lblQrcode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblQrcodeMousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        pnlNewPayment.add(lblQrcode, gridBagConstraints);

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(Fonts.BOLD_SMALL_FONT);
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/wallet_16x16.png"))); // NOI18N
        lblTitle.setText("New Account");
        lblTitle.setToolTipText("");
        lblTitle.setIconTextGap(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(lblTitle, gridBagConstraints);

        lblAddWallet.setBackground(Colors.APP_BG_COLOR);
        lblAddWallet.setFont(Fonts.DASHBOARD_WALLENT_BALANCE_FONT);
        lblAddWallet.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblAddWallet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAddWallet.setText("+");
        lblAddWallet.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.NAV_MENU_RIGHT_BORDER_COLOR));
        lblAddWallet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAddWallet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblAddWallet.setOpaque(true);
        lblAddWallet.setPreferredSize(new java.awt.Dimension(24, 24));
        lblAddWallet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblAddWalletMousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        pnlTitle.add(lblAddWallet, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        pnlNewPayment.add(pnlTitle, gridBagConstraints);

        pnlTop.add(pnlNewPayment, java.awt.BorderLayout.CENTER);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbWalletsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbWalletsItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (!(evt.getItem() instanceof HDAccount)) {
                return;
            }
            HDAccount hdacct = (HDAccount) evt.getItem();
            String watchOnlyAccountFlag = hdacct.toString();
            if (watchOnlyAccountFlag.endsWith(".")) {
                isWatchOnly = true;
            } else {
                isWatchOnly = false;
            }
            if (hdacct != null) {
                currentAccount = hdacct;
                showWalletControls();
                currentService.setCurrentAccount(currentAccount);
            }
        }
    }//GEN-LAST:event_cmbWalletsItemStateChanged

    private void lblAddressMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddressMousePressed
        String address = lblAddress.getText();
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(new StringSelection(address), null);
    }//GEN-LAST:event_lblAddressMousePressed

    private void lblAddWalletMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddWalletMousePressed
        try {
            HDAccount lastAccount = currentService.getLastAccount();
            DlgCreateNewAccount dlgCreateAccount = new DlgCreateNewAccount(currentService);
            dlgCreateAccount.centerOnScreen();
            dlgCreateAccount.setVisible(true);
            HDAccount accountAdded = currentService.getLastAccount();
            if (!lastAccount.toString().equals(accountAdded.toString())) {
                logger.debug("Account added=" + accountAdded.toString());
                comboModel.addElement(accountAdded);
                comboModel.setSelectedItem(accountAdded);
            }
        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage(), e);
            ApplicationUI.get().showError("Error creating Account: ", e.getMessage());
        }
    }//GEN-LAST:event_lblAddWalletMousePressed

    private void lblWalletLoadingMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWalletLoadingMousePressed
        if (currentService == null) {
            return;
        }
        DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(currentService);
        progress.start();
    }//GEN-LAST:event_lblWalletLoadingMousePressed

    private void btnNewPaymentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewPaymentMousePressed
        try {
            if (currentService.getWalletConfig().isWatchOnly()) {
                ApplicationUI.get().showError("Payments are not allowed via Watch-Only wallets.");
                btnNewPayment.setVisible(!currentService.getWalletConfig().isWatchOnly());
                return;
            }
            if (!currentService.getWallet().getBalance().isZero() && !isWatchOnly) {
                DlgNewPayment dlgNewPayment = new DlgNewPayment(currentService);
                dlgNewPayment.centerOnScreen();
                dlgNewPayment.setVisible(true);
            } 
            else if (!currentService.getWallet().getBalance().isZero() && isWatchOnly) {
                DlgNewPaymentForOfflineWallet dlgNewPaymentForOfflineWallet = new DlgNewPaymentForOfflineWallet(currentService);
                dlgNewPaymentForOfflineWallet.centerOnScreen();
                dlgNewPaymentForOfflineWallet.setVisible(true);
            } else {
                ApplicationUI.get().showError("You do not have sufficient funds to make a payment.");
            }
        } catch (Exception e) {
            logger.error("Error occured while making payment: {}", e, e);
            ApplicationUI.get().showError("Error occured while making payment: ", e.toString());
        }
    }//GEN-LAST:event_btnNewPaymentMousePressed

    private void cmbWalletsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbWalletsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbWalletsActionPerformed

    private void lblQrcodeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQrcodeMousePressed
        // TODO add your handling code here:
        //New Functionality Calling DlgRequestPayment Class Here
        DlgRequestPayment dlgRequestPayment = new DlgRequestPayment(currentService);
        dlgRequestPayment.centerOnScreen();
        dlgRequestPayment.setVisible(true);
    }//GEN-LAST:event_lblQrcodeMousePressed

    public void showWalletControls() {
        currentService = WalletManager.get().getCurentWalletService();
        CardLayout layout = (CardLayout) pnlWalletSummary.getLayout();
        layout.show(pnlWalletSummary, "WALLET_CONTROLS");
        currentService.setWalletEventListener(new AbstractWalletEventListener() {
            @Override
            public void onWalletChanged(Wallet wallet) {
                currentService.applyAllTransactionsToHDWallet();
                currentService.ensureLookAhead();
                currentService.saveWallet();
                String nextAddress = "N/A";
                try {
                    nextAddress = currentAccount.nextReceiveAddress().toString();
                } catch (RuntimeException e) {
                    ApplicationUI.get().showMessage("Error fetching next address", e.getMessage());
                }
                updateWalletSummary(Coin.parseCoin(MonetaryFormat.BTC.noCode().format(Coin.valueOf(currentAccount.balance())).toString()), nextAddress, wallet.getTransactionsByTime());
            }
        });
        Wallet wallet = currentService.getWallet();
        String nextAddress = "N/A";
        try {
            nextAddress = currentAccount.nextReceiveAddress().toString();
        } catch (RuntimeException e) {
            ApplicationUI.get().showMessage("Error fetching next address", e.getMessage());
        }
        updateWalletSummary(Coin.parseCoin(MonetaryFormat.BTC.noCode().format(Coin.valueOf(currentAccount.balance())).toString()), nextAddress, wallet.getTransactionsByTime());
    }

    /**
     * function that update debit/credit/balance stats, update Transaction
     * History table and loads graph
     *
     * @param balance account balance
     * @param receiveAddress next receive address for account
     * @param transactions transactions list in wallet
     */
    private void updateWalletSummary(Coin balance, String receiveAddress, List<Transaction> transactions) {
        boolean found = false;
        boolean connectedFound = false;
        int skipped = 0;
        String sameWalletTransMarker = "";
        boolean sameWalletTrans = false;
        boolean isDead = false;
        long outputValue = 0;
        long connectedOutputValue = 0;
        String fromAccount = "";
        String toAccount = "";

        btnNewPayment.setVisible(!currentService.getWalletConfig().isWatchOnly());
        String walletBalanceString = MonetaryFormat.BTC.noCode().format(balance).toString();
        lblAddress.setText(receiveAddress);
        LabelAddress = receiveAddress;
        DefaultTableModel model = (DefaultTableModel) tblTransactions.getModel();
        model.setRowCount(0);
        Coin creditAmount = Coin.ZERO;
        Coin debitAmount = Coin.ZERO;
        pnlWalletStats.setBalanceAmount("0.00");
        pnlWalletStats.setCreditAmount("0.00");
        pnlWalletStats.setDebitAmount("0.00");
        if (transactions != null && !transactions.isEmpty()) {
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction o1, Transaction o2) {
                    return o2.getUpdateTime().compareTo(o1.getUpdateTime());
                }
            });
            skipped = 0;
            for (Transaction transaction : transactions) {
                isDead = false;
                if (transaction.getConfidence().equals(TransactionConfidence.ConfidenceType.DEAD)) {
                    isDead = true;
                }
                fromAccount = "";
                toAccount = "";
                sameWalletTrans = false;
                connectedFound = false;
                sameWalletTransMarker = "";
                outputValue = 0;
                connectedOutputValue = 0;
                Coin amount = transaction.getValue(currentService.getWallet());
                Coin fee = transaction.getFee();
                if (fee != null) {
                    if ((amount.getValue() * -1) == fee.getValue()) { // transaction is from one account to another account in same wallet
                        sameWalletTrans = true;
                        sameWalletTransMarker = " *";
                    }
                }
                ////////////////////////////////////////////////////////////////////////
                // To set the font size and style of the contents of table
                tblTransactions.setFont(new Font("Tahoma", Font.PLAIN, 11));
                ///////////////////////////////////////////////////////////////////
                // To place the text of table at the center
                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);//(JLabel.CENTER);
                for (int x = 0; x < model.getColumnCount(); x++) {
                    tblTransactions.getColumnModel().getColumn(x).setCellRenderer(rightRenderer);
                }
                Color color = null;
                /////////////////////////////////////////////////////////////////////// 
                // to set the color of column 5 (Status) as green
                TableColumn tm = tblTransactions.getColumnModel().getColumn(columnStatusNumber);
                tm.setCellRenderer((TableCellRenderer) columnCellRenderer(color));
                List<TransactionOutput> lto = transaction.getOutputs();
                String amountString = MonetaryFormat.BTC.noCode().format(amount).toString();
                String feeString = fee != null ? MonetaryFormat.BTC.noCode().format(fee).toString() : "0.00";
                Address to = transaction.getOutput(0).getAddressFromP2PKHScript(currentService.getWallet().getNetworkParameters());
                description = transaction.getMemo();
                getExchangeRate(transaction);
                getStatus(transaction);
                found = false;
                for (TransactionOutput txo : lto) {
                    long value = txo.getValue().longValue();// coin value
                    try {
                        byte[] pubkey = null;
                        byte[] pubkeyhash = null;
                        Script script = txo.getScriptPubKey();

                        if (script.isSentToRawPubKey()) {
                            pubkey = script.getPubKey();
                        } else {
                            pubkeyhash = script.getPubKeyHash();
                        }
                        if (currentAccount.hasPubKey(pubkey, pubkeyhash)) {
                            found = true;
                            outputValue += value;
                        }
                    } catch (ScriptException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // Traverse the HDAccounts with all inputs.
                List<TransactionInput> lti = transaction.getInputs();
                for (TransactionInput ti : lti) {
                    // Get the connected TransactionOutput to see value.
                    TransactionOutput cto = ti.getConnectedOutput();
                    if (cto == null) {
                        // It appears we land here when processing transactions
                        // where we handled the output above.
                        //
                        // mLogger.warn("couldn't find connected output for input");
                        continue;
                    }
                    long value = cto.getValue().longValue();
                    try {
                        byte[] pubkey = ti.getScriptSig().getPubKey();
                        if (currentAccount.hasPubKey(pubkey, null)) {
                            found = true;
                            connectedFound = true;
                            connectedOutputValue += value;
                        }
                    } catch (ScriptException e) {
                        // This happens if the input doesn't have a
                        // public key (eg P2SH).  No worries in this
                        // case, it isn't one of ours ...
                    }
                }
                if (sameWalletTrans) {
                    amount = Coin.valueOf(outputValue - connectedOutputValue);
                    amountString = MonetaryFormat.BTC.noCode().format(amount).toString();
                    if (!connectedFound) {
                        feeString = "0.00";
                    }
                    //  fromAccount = currentService.getAddressAcountName(from);
                    for (TransactionOutput txo : lto) {
                        toAccount = currentService.getAddressAcountName(txo.getAddressFromP2PKHScript(currentService.getWallet().getNetworkParameters()));
                        if (!fromAccount.equalsIgnoreCase(toAccount)) {
                            break;
                        }
                    }
                }
                if (!found) {
                    skipped++;// skip transaction
                    continue;
                } else {
                    if (isDead) { // dead transaction
                        skipped++;
                        continue;
                    }
                }

                boolean credit = amount.isPositive();
                if (credit) {
                    creditAmount = creditAmount.add(Coin.parseCoin(amountString));
                } else {
                    debitAmount = debitAmount.add(Coin.parseCoin(amountString));
                }
                if (!sameWalletTrans) {
                    model.addRow(new Object[]{
                        Utils.formatTransactionDate(transaction.getUpdateTime()),
                        to,
                        description,
                        amountString,
                        feeString,
                        status,
                        currentExchangeRate
                    });
                } else {
                    model.addRow(new Object[]{
                        Utils.formatTransactionDate(transaction.getUpdateTime()),
                        toAccount,
                        description,
                        amountString,
                        feeString,
                        status,
                        currentExchangeRate
                    });
                }

            }
            Coin balanceAfter = Coin.ZERO;
            for (int index = transactions.size() - 1 - skipped; index >= 0; index--) {
                balanceAfter = balanceAfter.add(Coin.parseCoin((String) model.getValueAt(index, 4)));
                //model.setValueAt(MonetaryFormat.BTC.noCode().format(balanceAfter).toString(), index, 6);
            }

            pnlWalletStats.setBalanceAmount(walletBalanceString);
            pnlWalletStats.setCreditAmount(MonetaryFormat.BTC.noCode().format(creditAmount).toString());
            pnlWalletStats.setDebitAmount(MonetaryFormat.BTC.noCode().format(debitAmount).toString());
        }
        pnlWalletGraphs.loadGraph(currentService.getWalletConfig(), MAX_DAYS);
    }

    private void getStatus(Transaction transaction) {
        if (transaction.getConfidence().getDepthInBlocks() < 1) {
            status = statusPending;
        } else if (transaction.getConfidence().getDepthInBlocks() >= 1 && transaction.getConfidence().getDepthInBlocks() <= 2) {
            status = statusConfirmed;
        } else {
            status = statusCompleted;
        }
    }

    private void getExchangeRate(Transaction transaction) {
        try {
            if (!StringUtils.isEmpty(description)) {
                currentExchangeRate = transaction.getExchangeRate().fiat.toString();
                currentExchangeRate = currentExchangeRate.substring(0, currentExchangeRate.length() - 2);
                currentExchangeRate = currentExchangeRate.substring(0, 5) + "." + currentExchangeRate.substring(5, currentExchangeRate.length());
            } else {
                currentExchangeRate = "";
            }

        } catch (Exception e) {
            System.out.println("Exception Catached..." + e);
        }
    }

    public void removeWallet(WalletService service) {
        if (service != null) {
            service.unsetWalletEventListener();
            service.removeAllProgressListeners();
            if (service.equals(currentService)) {
                resetWalletScreen();
            }
            comboModel.removeElement(service);
        }
    }

    /**
     * function that reset Transaction History table
     */
    private void resetWalletScreen() {
        ((DefaultTableModel) tblTransactions.getModel()).setRowCount(0);
    }

    public WalletService getCurrentService() {
        return this.currentService;
    }

    @Override
    public void actionPerformed(ActionEvent e
    ) {
        currentService = WalletManager.get().getCurentWalletService();// reload Application
        if (e instanceof ProgressEvent) {
            ProgressEvent evt = (ProgressEvent) e;
            if (currentService != null && currentService.isNetworkSync()) {
                showWalletControls();
            }
        }
    }

    /**
     * *****************************************************
     *
     *******************************************************
     */
    public Component columnCellRenderer(final Color c) {
        tblTransactions.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == columnStatusNumber) {
                    Object columnValue = table.getValueAt(row, columnStatusNumber);
                    if (columnValue.equals(statusCompleted)) {
                        setBackground(java.awt.Color.decode("#21C86D"));
                        setForeground(java.awt.Color.WHITE);
                        
                    } else if (columnValue.equals(statusConfirmed)) {
                        setBackground(java.awt.Color.decode("#8DF158"));
                        setForeground(java.awt.Color.WHITE);
                    } else if (columnValue.equals(statusPending)) {
                        setBackground(java.awt.Color.decode("#FFA500"));
                        setForeground(java.awt.Color.WHITE);
                    }
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return cell;
                }
                return cell;
            }
        });
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnNewPayment;
    private javax.swing.JComboBox cmbWallets;
    private javax.swing.JLabel lblAddWallet;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblQrcode;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTransactionsHistory;
    private javax.swing.JLabel lblWalletLoading;
    private javax.swing.JPanel pnlGraphs;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlNewPayment;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopEdge;
    private javax.swing.JPanel pnlTransactions;
    private javax.swing.JPanel pnlWalletControls;
    private com.o3.bitcoin.ui.screens.wallet.PnlWalletGraphs pnlWalletGraphs;
    private javax.swing.JPanel pnlWalletLoading;
    private com.o3.bitcoin.ui.screens.wallet.PnlWalletStats pnlWalletStats;
    private javax.swing.JPanel pnlWalletSummary;
    private javax.swing.JPanel pnlWallets;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblTransactions;
    // End of variables declaration//GEN-END:variables
}
