/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.applications.PnlShapshiftIOExchangeDividerScreen;
import com.o3.bitcoin.model.Config;
import com.o3.bitcoin.model.ExchangeConfig;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.DlgExchangeConfig;
import com.o3.bitcoin.ui.dialogs.DlgExchangeWithdraw;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.Utils;
import com.o3.bitcoin.util.exchange.ExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import org.bitcoinj.crypto.KeyCrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class PnlExchangeScreen extends javax.swing.JPanel implements BasicScreen {

    private static final Logger logger = LoggerFactory.getLogger(PnlExchangeScreen.class);

    private final String EXCHANGE_OVERVIEW_SCREEN = "EXCHANGE_OVERVIEW_SCREEN";
    private final String EXCHANGE_ORDERBOOK_SCREEN = "EXCHANGE_ORDERBOOK_SCREEN";
    private final String EXCHANGE_WITHDRAWAL_SCREEN = "EXCHANGE_WITHDRAWAL_SCREEN";

    private Map<String, BasicExchangeScreen> exchangeScreens = new HashMap<>();
    private String currentExchage = "";
    private String currentCurrencyPair = "";
    private ExchangeService exchangeService = null;

    private DefaultComboBoxModel<String> exchangesModel = new DefaultComboBoxModel<>();
    
    private boolean firstTime = true;

    /**
     * Creates new form PnlExchangeScreen
     */
    public PnlExchangeScreen() {
        initComponents();
        prepareScreensCache();
        themeSelectButton(btnAddExchange, Colors.NAV_MENU_WALLET_COLOR);
        initSwitchButtons();
    }
    
    public void initSwitchButtons() {
        themeSelectButton(btnOverview, Colors.NAV_MENU_DASHBOARD_COLOR);
        themeUnSelectButton(btnOrderbook);
        themeUnSelectButton(btnWithdraw);
    }

    public static void stopTimers() {
        PnlExchangeStats.stopMarketInfoTimer();
        PnlOrderBook.stopOrderbookTimer();
    }
    public void populateExchangeCombo() {
        String apiKey = "";
        String apiSecret = "";
        String customerID = "";
        KeyCrypter keyCrypter = null;
        List<ExchangeConfig> exchanges = ConfigManager.config().getExchanges();
        if (exchanges != null) {
            if (exchanges.size() > 0) {
                exchangesModel.removeAllElements();
                ExchangeServiceFactory.clearExchangeMap();
                keyCrypter = WalletManager.get().getCurentWalletService().getWallet().getKeyCrypter();
                for (ExchangeConfig exchange : exchanges) {
                    if(exchange.getExchangeName().equalsIgnoreCase("BTC-e"))
                        continue;
                    customerID = "";
                    apiKey = Utils.decryptData(keyCrypter, WalletManager.walletPassword, exchange.getApiKey());
                    apiSecret = Utils.decryptData(keyCrypter, WalletManager.walletPassword, exchange.getApiSecret());
                    if(!exchange.getCustomerID().isEmpty())
                        customerID = Utils.decryptData(keyCrypter, WalletManager.walletPassword, exchange.getCustomerID());
                    ExchangeServiceFactory.addExchange(exchange.getExchangeName().toLowerCase(), apiKey, apiSecret, customerID);
                    exchangesModel.addElement(exchange.getExchangeName());
                }
                showPageButtons();
                pnlContents.setVisible(true);
                return;
            }
        }
        hidePageButtons();
        pnlContents.setVisible(false);
    }

    private void hidePageButtons() {
        btnOverview.setVisible(false);
        btnOrderbook.setVisible(false);
        btnWithdraw.setVisible(false);
    }

    private void showPageButtons() {
        btnOverview.setVisible(true);
        btnOrderbook.setVisible(true);
        btnWithdraw.setVisible(true);
    }

    private void prepareUI() {
        cmbExchanges.setRenderer(new BasicComboBoxRenderer() {
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

        Object child = cmbExchanges.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup) child;
        popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
    }

    public void loadData() {
        PnlShapshiftIOExchangeDividerScreen.stopMarketInfoTimer();
        enableExchangeSelection();
        initSwitchButtons();
        showExchangeScreen(EXCHANGE_OVERVIEW_SCREEN);
        if(firstTime) {
            populateExchangeCombo();
            firstTime = false;
        }
        if(cmbExchanges.getItemCount() > 0) {
            pnlExchangeStats.setIsFirstTime(false);
            pnlExchangeStats.loadGraph();
        }
        /*if( cmbExchanges.getSelectedIndex() != -1 ) {
            currentExchage = cmbExchanges.getSelectedItem().toString().toLowerCase();
            loadDefaultTicker(currentExchage);
        }*/
    }

    private void prepareScreensCache() {
        exchangeScreens.put(EXCHANGE_OVERVIEW_SCREEN, pnlExchangeStats);
        exchangeScreens.put(EXCHANGE_ORDERBOOK_SCREEN, pnlOrderBook);
        exchangeScreens.put(EXCHANGE_WITHDRAWAL_SCREEN, pnlExchangeWithdrawal1);
    }

    /**
     * function to show a specific exchange screen
     */
    public void showExchangeScreen(String screenName) {
        if (exchangeScreens.containsKey(screenName)) {
            try {
                exchangeScreens.get(screenName).loadExchangeData();
                CardLayout c = (CardLayout) pnlContents.getLayout();
                c.show(pnlContents, screenName);
            } catch (Exception e) {
                logger.error("Error loading data for screen ({}) : ", screenName, e.getMessage(), e);
                ApplicationUI.get().showError(e);
            }
        }
    }

    public void disableExchangeSelection() {
        cmbExchanges.setEnabled(false);
        btnAddExchange.setEnabled(false);
    }
    
    public void enableExchangeSelection() {
        cmbExchanges.setEnabled(true);
        btnAddExchange.setEnabled(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        pnlTopEdge = new javax.swing.JPanel();
        pnlTitle = new javax.swing.JPanel();
        lblTop = new javax.swing.JLabel();
        pnlExchangeOptions = new javax.swing.JPanel();
        btnOverview = new javax.swing.JButton();
        btnOrderbook = new javax.swing.JButton();
        btnWithdraw = new javax.swing.JButton();
        pnlExchanges = new javax.swing.JPanel();
        lblSelectExchange = new com.o3.bitcoin.ui.component.XScalableLabel();
        cmbExchanges = new javax.swing.JComboBox();
        btnAddExchange = new javax.swing.JButton();
        pnlContents = new javax.swing.JPanel();
        pnlOrderBook = new com.o3.bitcoin.ui.screens.exchange.PnlOrderBook();
        pnlExchangeStats = new com.o3.bitcoin.ui.screens.exchange.PnlExchangeStats();
        pnlExchangeWithdrawal1 = new com.o3.bitcoin.ui.screens.exchange.PnlExchangeWithdrawal();

        setLayout(new java.awt.BorderLayout());

        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlTopEdge.setBackground(Colors.NAV_MENU_EXCHANGES_COLOR);
        pnlTopEdge.setPreferredSize(new java.awt.Dimension(1024, 5));
        pnlTop.add(pnlTopEdge, java.awt.BorderLayout.NORTH);

        pnlTitle.setPreferredSize(new java.awt.Dimension(84, 26));
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTop.setFont(Fonts.BOLD_SMALL_FONT);
        lblTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exchange_16x16.png"))); // NOI18N
        lblTop.setText("EXCHANGES");
        pnlTitle.add(lblTop, new java.awt.GridBagConstraints());

        pnlTop.add(pnlTitle, java.awt.BorderLayout.EAST);

        pnlExchangeOptions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnOverview.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnOverview.setText("Overview");
        btnOverview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOverviewActionPerformed(evt);
            }
        });
        pnlExchangeOptions.add(btnOverview);

        btnOrderbook.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnOrderbook.setText("Orderbook");
        btnOrderbook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrderbookActionPerformed(evt);
            }
        });
        pnlExchangeOptions.add(btnOrderbook);

        btnWithdraw.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnWithdraw.setText("Accounts");
        btnWithdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWithdrawActionPerformed(evt);
            }
        });
        pnlExchangeOptions.add(btnWithdraw);

        pnlTop.add(pnlExchangeOptions, java.awt.BorderLayout.WEST);

        lblSelectExchange.setText("Select Exchange : ");
        lblSelectExchange.setFont(Fonts.EXCHANGE_STATS_LARGE_FONT);
        pnlExchanges.add(lblSelectExchange);

        cmbExchanges.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbExchanges.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbExchanges.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbExchanges.setModel(exchangesModel);
        cmbExchanges.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbExchanges.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbExchanges.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbExchangesItemStateChanged(evt);
            }
        });
        pnlExchanges.add(cmbExchanges);

        btnAddExchange.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnAddExchange.setText(" Add");
        btnAddExchange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddExchangeActionPerformed(evt);
            }
        });
        pnlExchanges.add(btnAddExchange);

        pnlTop.add(pnlExchanges, java.awt.BorderLayout.CENTER);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);

        pnlContents.setLayout(new java.awt.CardLayout());
        pnlContents.add(pnlOrderBook, "EXCHANGE_ORDERBOOK_SCREEN");
        pnlContents.add(pnlExchangeStats, "EXCHANGE_OVERVIEW_SCREEN");
        pnlContents.add(pnlExchangeWithdrawal1, "EXCHANGE_WITHDRAWAL_SCREEN");

        add(pnlContents, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnOverviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOverviewActionPerformed
        // TODO add your handling code here:
        PnlOrderBook.stopOrderbookTimer();
        enableExchangeSelection();
        themeSelectButton(btnOverview, Colors.NAV_MENU_DASHBOARD_COLOR);
        themeUnSelectButton(btnOrderbook);
        themeUnSelectButton(btnWithdraw);
        btnOverview.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        showExchangeScreen(EXCHANGE_OVERVIEW_SCREEN);
        btnOverview.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btnOverviewActionPerformed

    private void btnOrderbookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderbookActionPerformed
        // TODO add your handling code here:
        PnlExchangeStats.stopMarketInfoTimer();
        disableExchangeSelection();
        ////pnlOrderBook.setCurrentExchange(currentExchage);
        themeSelectButton(btnOrderbook, Colors.NAV_MENU_DASHBOARD_COLOR);
        themeUnSelectButton(btnOverview);
        themeUnSelectButton(btnWithdraw);
        btnOrderbook.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        showExchangeScreen(EXCHANGE_ORDERBOOK_SCREEN);
        btnOrderbook.setCursor(new Cursor(Cursor.HAND_CURSOR));

    }//GEN-LAST:event_btnOrderbookActionPerformed

    private void btnWithdrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWithdrawActionPerformed
        // TODO add your handling code here:
        pnlExchangeStats.stopMarketInfoTimer();
        PnlOrderBook.stopOrderbookTimer();
        
        disableExchangeSelection();
        themeSelectButton(btnWithdraw, Colors.NAV_MENU_DASHBOARD_COLOR);
        themeUnSelectButton(btnOverview);
        themeUnSelectButton(btnOrderbook);
        btnWithdraw.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        showExchangeScreen(EXCHANGE_WITHDRAWAL_SCREEN);
        btnWithdraw.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btnWithdrawActionPerformed

    private void themeSelectButton(JButton button, Color background) {
        XButtonFactory
                .themedButton(button)
                .color(Color.WHITE)
                .background(background)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
    }

    private void themeUnSelectButton(JButton button) {
        XButtonFactory
                .themedButton(button)
                .color(Color.BLACK)
                .background(Color.LIGHT_GRAY)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
    }

    public String getSelectedExchange() {
        if( cmbExchanges.getItemCount() > 0 ) // new line line
            return cmbExchanges.getSelectedItem().toString().toLowerCase();
        else
            return "";
    }

    private void cmbExchangesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbExchangesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            currentExchage = evt.getItem().toString().toLowerCase();
            if (ExchangeServiceFactory.getExchange(currentExchage) != null) {
                pnlExchangeStats.updateTickerData(currentExchage);
                pnlOrderBook.setCurrentExchange(currentExchage);
            }
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbExchangesItemStateChanged

    private void btnAddExchangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddExchangeActionPerformed
        // TODO add your handling code here:
        Config config = ConfigManager.config();
        String currentNetwork = config.getDefaultNetwork();
        if( currentNetwork.equals("MAINNET")) {
            DlgExchangeConfig dlgEc = new DlgExchangeConfig(this);
            dlgEc.centerOnScreen();
            dlgEc.setVisible(true);
        }
        else {
            ApplicationUI.get().showError("Network Error", "You can add exchange on MAINNET only");
        }
    }//GEN-LAST:event_btnAddExchangeActionPerformed

    public PnlExchangeStats getExchangeStatsScreen() {
        return pnlExchangeStats;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddExchange;
    private javax.swing.JButton btnOrderbook;
    private javax.swing.JButton btnOverview;
    private javax.swing.JButton btnWithdraw;
    private javax.swing.JComboBox cmbExchanges;
    private com.o3.bitcoin.ui.component.XScalableLabel lblSelectExchange;
    private javax.swing.JLabel lblTop;
    private javax.swing.JPanel pnlContents;
    private javax.swing.JPanel pnlExchangeOptions;
    private com.o3.bitcoin.ui.screens.exchange.PnlExchangeStats pnlExchangeStats;
    private com.o3.bitcoin.ui.screens.exchange.PnlExchangeWithdrawal pnlExchangeWithdrawal1;
    private javax.swing.JPanel pnlExchanges;
    private com.o3.bitcoin.ui.screens.exchange.PnlOrderBook pnlOrderBook;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopEdge;
    // End of variables declaration//GEN-END:variables
}
