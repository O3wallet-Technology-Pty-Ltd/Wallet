/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.DlgNewPayment;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.exchange.BTCMarketExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.math.BigDecimal;
import javax.swing.JButton;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.polling.account.PollingAccountService;
import org.knowm.xchange.service.polling.trade.PollingTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class PnlBuySellBitcoins extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlBuySellBitcoins.class);
    
    private ExchangeService exchangeService = null;
    private String availableAmount = "";
    private String availableBitcoin = "";
    private String price = "0.0";
    private String volume = "0.0";
    private double spendTotal = 0.0;
    private double sellTotal = 0.0;
    private boolean orderTypeMarket = true;
    private String fiatBalance = "0.0";
    private String coinBalance = "0.0";
    private boolean isBuyOrderInProgress = false;
    private boolean isSellOrderInProgress = false;

    /**
     * Creates new form PnlBuySellBitcoins
    */
    public PnlBuySellBitcoins() {
        initComponents();
        customizeUI();
        lblBuySpendAll.setText("     ");
        lblSellAll.setText("     ");
        lblDepositAUD.setVisible(false);
        lblSpendTotal.setVisible(false);
        lblSpendTotalValue.setVisible(false);
        lblReceiveTotal.setVisible(false);
        lblReceiveTotalValue.setVisible(false);
    }
    
    public void resetValues() {
        lblAvailableCurrencyValue.setText("0.0");
        txtBuyPrice.setText("");
        txtBuyVolume.setText("");
        rdoBtnBuyLimit.setSelected(true);
        
        lblAvailableBTCValue.setText("0.0");
        txtSellPrice.setText("");
        txtSellVolume.setText("");
        rdoBtnSellLimit.setSelected(true);
        
    }
    
    private void customizeUI() {
        themeButton(btnBuyBitcoin,ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        themeButton(btnSellBitcoin,ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
    }
    
    private void themeButton(JButton button, Color background) {
        XButtonFactory
                .themedButton(button)
                .color(Color.WHITE)
                .background(background)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
    }
    
    public void adjustLabels() {
        exchangeService = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
        lblBuyBitcoin.setText("Buy "+exchangeService.getAltcoinCurrency().toUpperCase());
        lblSellBitcoin.setText("Sell "+exchangeService.getAltcoinCurrency().toUpperCase());
        lblAvailableCurrency.setText("Available "+exchangeService.getFiatCurrency().toUpperCase());
        lblAvailableBTC.setText("Available "+exchangeService.getAltcoinCurrency().toUpperCase());
        btnBuyBitcoin.setText("Buy "+exchangeService.getAltcoinCurrency().toUpperCase());
        btnSellBitcoin.setText("Sell "+exchangeService.getAltcoinCurrency().toUpperCase());
        lblDepositAUD.setText("Deposit "+exchangeService.getFiatCurrency().toUpperCase());
        
        rdoBtnBuyLimit.setSelected(true);
        rdoBtnSellLimit.setSelected(true);
        txtBuyVolume.setText("");
        txtSellVolume.setText("");
        txtSellPrice.setText("");
        txtSellPrice.setEnabled(true);
        txtBuyPrice.setText("");
        txtBuyPrice.setEnabled(true);
    }
    
    public void loadData() {
        adjustLabels();
        String fialBalance = "n/a";
        String altCoinBalace = "n/a";
        
        try {
            fiatBalance = exchangeService.getCurrencyBalance(new Currency(exchangeService.getFiatCurrency().toUpperCase()),2);
            altCoinBalace = exchangeService.getCurrencyBalance(new Currency(exchangeService.getAltcoinCurrency().toUpperCase()),8);
            if(fiatBalance.equalsIgnoreCase("0.00"))
                fiatBalance = "0.0";
            if(altCoinBalace.equalsIgnoreCase("0.00000000"))
                altCoinBalace = "0.0";
            lblAvailableCurrencyValue.setText(fiatBalance);
            lblAvailableBTCValue.setText(altCoinBalace);
        }catch(Exception e) {
            logger.error("Exception while getting account balance: {}", e.getMessage());
            //ApplicationUI.get().showError(e);
        }
    }
    
    public void updateAccountBalance() {
        //adjustLabels();
        exchangeService = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    exchangeService = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
                    fiatBalance = exchangeService.getCurrencyBalance(new Currency(exchangeService.getFiatCurrency().toUpperCase()),2);
                    coinBalance = exchangeService.getCurrencyBalance(new Currency(exchangeService.getAltcoinCurrency().toUpperCase()),8);
                    if(fiatBalance.equalsIgnoreCase("0.00"))
                        fiatBalance = "0.0";
                    if(coinBalance.equalsIgnoreCase("0.00000000"))
                        coinBalance = "0.0";
                    
                    java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    lblAvailableCurrencyValue.setText(fiatBalance);
                    lblAvailableBTCValue.setText(coinBalance);
                }
            });
                    
                }catch(Exception e) {
                    System.out.println("Exception while getting acccount balance in thread ="+e.getMessage());
                    logger.error("Update Account Balance Exception: {}", e.getMessage());
                }
            }
        }).start();
    }

    private void populateBuyControlValues() {
        //availableAmount = getCurrencyBalance(Currency.BTC);
        availableAmount = lblAvailableCurrencyValue.getText();
        price = txtBuyPrice.getText();
        volume = txtBuyVolume.getText();
        //rdoBtnBuyMarket.setSelected(true);
        //if(rdoBtnSellLimit.isSelected())
            //spendTotal = Double.parseDouble(txtBuyVolume.getText()) * Double.parseDouble(txtBuyPrice.getText());
    }

    private void populateSellControlValues() {
        availableBitcoin = lblAvailableBTCValue.getText();
        price = txtSellPrice.getText();
        volume = txtSellVolume.getText();
        //rdoBtnSellMarket.setSelected(true);
        //sellTotal = Double.parseDouble(txtBuyVolume.getText()) * Double.parseDouble(txtBuyPrice.getText());
    }

    public void validateBuyBitcoinData() {
        double totalPrice = 0.0;
        availableAmount = lblAvailableCurrencyValue.getText();
        if(rdoBtnBuyLimit.isSelected()) {
            if (txtBuyPrice == null || txtBuyPrice.getText().isEmpty()) {
                txtBuyPrice.requestFocusInWindow();
                throw new IllegalArgumentException("Price is required.");
            }
        }
        if (txtBuyVolume == null || txtBuyVolume.getText().isEmpty()) {
            txtBuyVolume.requestFocusInWindow();
            throw new IllegalArgumentException("Volume is required.");
        }
        if(rdoBtnBuyLimit.isSelected()) {
            if (txtBuyVolume != null && !txtBuyVolume.getText().isEmpty()
                    && txtBuyPrice != null && !txtBuyPrice.getText().isEmpty()) {
                totalPrice = Double.parseDouble(txtBuyVolume.getText()) * Double.parseDouble(txtBuyPrice.getText());
                if (totalPrice >= Double.parseDouble(availableAmount)) {
                    txtBuyVolume.requestFocusInWindow();
                    throw new IllegalArgumentException("Insufficent funds.");
                }
            }
        }
        populateBuyControlValues();
    }

    public void validateSellBitcoinData() {
        double totalBitcoins = 0.0;
        availableBitcoin = lblAvailableBTCValue.getText();
        if( rdoBtnSellLimit.isSelected()) {
            if (txtSellPrice == null || txtSellPrice.getText().isEmpty()) {
                txtSellPrice.requestFocusInWindow();
                throw new IllegalArgumentException("Price is required.");
            }
        }
        if (txtSellVolume == null || txtSellVolume.getText().isEmpty()) {
            txtSellVolume.requestFocusInWindow();
            throw new IllegalArgumentException("Volume is required.");
        }

        if (Double.parseDouble(txtSellVolume.getText()) > Double.parseDouble(availableBitcoin)) {
            txtSellVolume.requestFocusInWindow();
            throw new IllegalArgumentException("You cannot sell more then your available coins.");
        }

        populateSellControlValues();
    }

    public double getAccountCurrencyBalance() {
        double balance = 0.0;
        return balance;
    }

    public double getAccountBitcoinBalance() {
        double balance = 0.0;
        return balance;
    }

    public String sendBuyOrder(Boolean market) throws Exception {
        String orderId = "";
        exchangeService = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
        String currencyPair = exchangeService.getSelectedCurrencyPair();
        PollingTradeService tradeService = exchangeService.getExchange().getPollingTradeService();
        if (market) {
            MarketOrder buyOrder = new MarketOrder(OrderType.BID, new BigDecimal(volume), new CurrencyPair(currencyPair), null, null);
            orderId = tradeService.placeMarketOrder(buyOrder);
        } else {
            LimitOrder buyOrder = new LimitOrder(OrderType.BID, new BigDecimal(volume), new CurrencyPair(currencyPair), null, null, new BigDecimal(price));
            orderId = tradeService.placeLimitOrder(buyOrder);
        }
        return orderId;
    }

    public String sendSellOrder(Boolean market) throws Exception {
        String orderId = "";
        String selectedExchange = ApplicationUI.get().getExchangeScreen().getSelectedExchange();
        exchangeService = ExchangeServiceFactory.getExchange(selectedExchange);
        String currencyPair = exchangeService.getSelectedCurrencyPair();
        PollingTradeService tradeService = exchangeService.getExchange().getPollingTradeService();

        if (market) {
            MarketOrder buyOrder = new MarketOrder(OrderType.ASK, new BigDecimal(volume), new CurrencyPair(currencyPair), null, null);
            orderId = tradeService.placeMarketOrder(buyOrder);
        } else {
            LimitOrder buyOrder = new LimitOrder(OrderType.ASK, new BigDecimal(volume), new CurrencyPair(currencyPair), null, null, new BigDecimal(price));
            orderId = tradeService.placeLimitOrder(buyOrder);
        }

        return orderId;
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

        btnGrpBuyOrderType = new javax.swing.ButtonGroup();
        btnGrpSellOrderType = new javax.swing.ButtonGroup();
        pnlBuyBitcoin = new javax.swing.JPanel();
        pnlBuyBitcoinHeader = new javax.swing.JPanel();
        lblBuyBitcoin = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblDepositAUD = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlBuyBitcoinControls = new javax.swing.JPanel();
        lblAvailableCurrency = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblAvailableCurrencyValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblOrderType = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlBuyOrderType = new javax.swing.JPanel();
        rdoBtnBuyMarket = new javax.swing.JRadioButton();
        rdoBtnBuyLimit = new javax.swing.JRadioButton();
        lblBuyVolume = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlBuyVolume = new javax.swing.JPanel();
        txtBuyVolume = new javax.swing.JTextField();
        lblBuySpendAll = new javax.swing.JLabel();
        lblBuyPrice = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlBuyPrice = new javax.swing.JPanel();
        txtBuyPrice = new javax.swing.JTextField();
        pnlBuyPriceSpacer = new javax.swing.JPanel();
        lblSpendTotal = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblSpendTotalValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        btnBuyBitcoin = new javax.swing.JButton();
        pnlBuyBitcoinSpacer = new javax.swing.JPanel();
        pnlSellBitcoins = new javax.swing.JPanel();
        pnlBuyBitcoinHeader1 = new javax.swing.JPanel();
        lblSellBitcoin = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlSellBitcoinControls = new javax.swing.JPanel();
        lblAvailableBTC = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblAvailableBTCValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblBuyOrderType = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlSellOrderType = new javax.swing.JPanel();
        rdoBtnSellMarket = new javax.swing.JRadioButton();
        rdoBtnSellLimit = new javax.swing.JRadioButton();
        lblSellVolume = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlBuyVolume1 = new javax.swing.JPanel();
        txtSellVolume = new javax.swing.JTextField();
        lblSellAll = new javax.swing.JLabel();
        lblSellPrice = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlBuyPrice1 = new javax.swing.JPanel();
        txtSellPrice = new javax.swing.JTextField();
        pnlBuyPriceSpacer1 = new javax.swing.JPanel();
        lblReceiveTotal = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblReceiveTotalValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        btnSellBitcoin = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pnlBuyBitcoin.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        pnlBuyBitcoin.setLayout(new java.awt.GridBagLayout());

        pnlBuyBitcoinHeader.setLayout(new java.awt.GridBagLayout());

        lblBuyBitcoin.setText("Buy Bitcoin");
        lblBuyBitcoin.setFont(Fonts.EXCHANGE_STATS_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBuyBitcoinHeader.add(lblBuyBitcoin, gridBagConstraints);

        lblDepositAUD.setText("Deposit AUD");
        lblDepositAUD.setFont(Fonts.BOLD_SMALL_FONT);
        pnlBuyBitcoinHeader.add(lblDepositAUD, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyBitcoin.add(pnlBuyBitcoinHeader, gridBagConstraints);

        pnlBuyBitcoinControls.setLayout(new java.awt.GridBagLayout());

        lblAvailableCurrency.setText("Available AUD : ");
        lblAvailableCurrency.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlBuyBitcoinControls.add(lblAvailableCurrency, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyBitcoinControls.add(lblAvailableCurrencyValue, gridBagConstraints);

        lblOrderType.setText("Order Type : ");
        lblOrderType.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlBuyBitcoinControls.add(lblOrderType, gridBagConstraints);

        pnlBuyOrderType.setLayout(new java.awt.GridBagLayout());

        btnGrpBuyOrderType.add(rdoBtnBuyMarket);
        rdoBtnBuyMarket.setText("Market");
        rdoBtnBuyMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBtnBuyMarketActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlBuyOrderType.add(rdoBtnBuyMarket, gridBagConstraints);

        btnGrpBuyOrderType.add(rdoBtnBuyLimit);
        rdoBtnBuyLimit.setText("Limit");
        rdoBtnBuyLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBtnBuyLimitActionPerformed(evt);
            }
        });
        pnlBuyOrderType.add(rdoBtnBuyLimit, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyBitcoinControls.add(pnlBuyOrderType, gridBagConstraints);

        lblBuyVolume.setText("Volume : ");
        lblBuyVolume.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlBuyBitcoinControls.add(lblBuyVolume, gridBagConstraints);

        pnlBuyVolume.setLayout(new java.awt.GridBagLayout());

        txtBuyVolume.setMinimumSize(new java.awt.Dimension(100, 30));
        txtBuyVolume.setPreferredSize(new java.awt.Dimension(100, 30));
        txtBuyVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuyVolumeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlBuyVolume.add(txtBuyVolume, gridBagConstraints);

        lblBuySpendAll.setFont(Fonts.BOLD_SMALL_FONT);
        lblBuySpendAll.setText("Spend All");
        lblBuySpendAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyVolume.add(lblBuySpendAll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyBitcoinControls.add(pnlBuyVolume, gridBagConstraints);

        lblBuyPrice.setText("Price : ");
        lblBuyPrice.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlBuyBitcoinControls.add(lblBuyPrice, gridBagConstraints);

        pnlBuyPrice.setLayout(new java.awt.GridBagLayout());

        txtBuyPrice.setMinimumSize(new java.awt.Dimension(100, 30));
        txtBuyPrice.setPreferredSize(new java.awt.Dimension(100, 30));
        txtBuyPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuyPriceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlBuyPrice.add(txtBuyPrice, gridBagConstraints);

        javax.swing.GroupLayout pnlBuyPriceSpacerLayout = new javax.swing.GroupLayout(pnlBuyPriceSpacer);
        pnlBuyPriceSpacer.setLayout(pnlBuyPriceSpacerLayout);
        pnlBuyPriceSpacerLayout.setHorizontalGroup(
            pnlBuyPriceSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlBuyPriceSpacerLayout.setVerticalGroup(
            pnlBuyPriceSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBuyPrice.add(pnlBuyPriceSpacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyBitcoinControls.add(pnlBuyPrice, gridBagConstraints);

        lblSpendTotal.setText("Spend Total : ");
        lblSpendTotal.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlBuyBitcoinControls.add(lblSpendTotal, gridBagConstraints);

        lblSpendTotalValue.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyBitcoinControls.add(lblSpendTotalValue, gridBagConstraints);

        btnBuyBitcoin.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnBuyBitcoin.setText("Buy BTC");
        btnBuyBitcoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuyBitcoinActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        pnlBuyBitcoinControls.add(btnBuyBitcoin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlBuyBitcoin.add(pnlBuyBitcoinControls, gridBagConstraints);

        javax.swing.GroupLayout pnlBuyBitcoinSpacerLayout = new javax.swing.GroupLayout(pnlBuyBitcoinSpacer);
        pnlBuyBitcoinSpacer.setLayout(pnlBuyBitcoinSpacerLayout);
        pnlBuyBitcoinSpacerLayout.setHorizontalGroup(
            pnlBuyBitcoinSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlBuyBitcoinSpacerLayout.setVerticalGroup(
            pnlBuyBitcoinSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBuyBitcoin.add(pnlBuyBitcoinSpacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 25);
        add(pnlBuyBitcoin, gridBagConstraints);

        pnlSellBitcoins.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        pnlSellBitcoins.setLayout(new java.awt.GridBagLayout());

        pnlBuyBitcoinHeader1.setLayout(new java.awt.GridBagLayout());

        lblSellBitcoin.setText("Sell Bitcoin");
        lblSellBitcoin.setFont(Fonts.EXCHANGE_STATS_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBuyBitcoinHeader1.add(lblSellBitcoin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSellBitcoins.add(pnlBuyBitcoinHeader1, gridBagConstraints);

        pnlSellBitcoinControls.setLayout(new java.awt.GridBagLayout());

        lblAvailableBTC.setText("Available BTC");
        lblAvailableBTC.setFont(Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlSellBitcoinControls.add(lblAvailableBTC, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSellBitcoinControls.add(lblAvailableBTCValue, gridBagConstraints);

        lblBuyOrderType.setText("Order Type : ");
        lblBuyOrderType.setFont(Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlSellBitcoinControls.add(lblBuyOrderType, gridBagConstraints);

        pnlSellOrderType.setLayout(new java.awt.GridBagLayout());

        btnGrpSellOrderType.add(rdoBtnSellMarket);
        rdoBtnSellMarket.setText("Market");
        rdoBtnSellMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBtnSellMarketActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlSellOrderType.add(rdoBtnSellMarket, gridBagConstraints);

        btnGrpSellOrderType.add(rdoBtnSellLimit);
        rdoBtnSellLimit.setText("Limit");
        rdoBtnSellLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoBtnSellLimitActionPerformed(evt);
            }
        });
        pnlSellOrderType.add(rdoBtnSellLimit, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSellBitcoinControls.add(pnlSellOrderType, gridBagConstraints);

        lblSellVolume.setText("Volume : ");
        lblSellVolume.setFont(Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlSellBitcoinControls.add(lblSellVolume, gridBagConstraints);

        pnlBuyVolume1.setLayout(new java.awt.GridBagLayout());

        txtSellVolume.setMinimumSize(new java.awt.Dimension(100, 30));
        txtSellVolume.setPreferredSize(new java.awt.Dimension(100, 30));
        txtSellVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSellVolumeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlBuyVolume1.add(txtSellVolume, gridBagConstraints);

        lblSellAll.setFont(Fonts.BOLD_SMALL_FONT);
        lblSellAll.setText("Sell All");
        lblSellAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBuyVolume1.add(lblSellAll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSellBitcoinControls.add(pnlBuyVolume1, gridBagConstraints);

        lblSellPrice.setText("Price : ");
        lblSellPrice.setFont(Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlSellBitcoinControls.add(lblSellPrice, gridBagConstraints);

        pnlBuyPrice1.setLayout(new java.awt.GridBagLayout());

        txtSellPrice.setMinimumSize(new java.awt.Dimension(100, 30));
        txtSellPrice.setPreferredSize(new java.awt.Dimension(100, 30));
        txtSellPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSellPriceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlBuyPrice1.add(txtSellPrice, gridBagConstraints);

        javax.swing.GroupLayout pnlBuyPriceSpacer1Layout = new javax.swing.GroupLayout(pnlBuyPriceSpacer1);
        pnlBuyPriceSpacer1.setLayout(pnlBuyPriceSpacer1Layout);
        pnlBuyPriceSpacer1Layout.setHorizontalGroup(
            pnlBuyPriceSpacer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlBuyPriceSpacer1Layout.setVerticalGroup(
            pnlBuyPriceSpacer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBuyPrice1.add(pnlBuyPriceSpacer1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSellBitcoinControls.add(pnlBuyPrice1, gridBagConstraints);

        lblReceiveTotal.setText("Receive Total : ");
        lblReceiveTotal.setFont(Fonts.BOLD_MEDIUM_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 30);
        pnlSellBitcoinControls.add(lblReceiveTotal, gridBagConstraints);

        lblReceiveTotalValue.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSellBitcoinControls.add(lblReceiveTotalValue, gridBagConstraints);

        btnSellBitcoin.setFont(Fonts.DEFAULT_HEADING_FONT);
        btnSellBitcoin.setText("Sell BTC");
        btnSellBitcoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSellBitcoinActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        pnlSellBitcoinControls.add(btnSellBitcoin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlSellBitcoins.add(pnlSellBitcoinControls, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlSellBitcoins.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 25);
        add(pnlSellBitcoins, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuyVolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuyVolumeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuyVolumeActionPerformed

    private void txtBuyPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuyPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuyPriceActionPerformed

    private void btnBuyBitcoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuyBitcoinActionPerformed
        if( isBuyOrderInProgress )
            return;
        isBuyOrderInProgress = true;
        String orderId = "";
        try {
            validateBuyBitcoinData();
            if (rdoBtnBuyMarket.isSelected()) {
                try {
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    orderId = sendBuyOrder(true);
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    updateAccountBalance();
                    YesNoDialog dialog = new YesNoDialog("Buy Market Order","Buy Market Order placed successfully", false);
                    dialog.start();
                } catch (Exception ex) {
                    logger.error("Buy Order Failed: {}", ex.getMessage());
                    ApplicationUI.get().showError(ex);
                }
            } else {
                try {
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    orderId = sendBuyOrder(false);
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    updateAccountBalance();
                    YesNoDialog dialog = new YesNoDialog("Buy Limit Order","Buy Limit Order placed successfully", false);
                    dialog.start();
                } catch (Exception ex) {
                    logger.error("Buy Order Failed: {}", ex.getMessage());
                    ApplicationUI.get().showError(ex);
                }
            }
            
        }catch(Exception e) {
            ApplicationUI.get().showError(e);
        }
        btnBuyBitcoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        isBuyOrderInProgress = false;
    }//GEN-LAST:event_btnBuyBitcoinActionPerformed

    private void txtSellVolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSellVolumeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSellVolumeActionPerformed

    private void txtSellPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSellPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSellPriceActionPerformed

    private void btnSellBitcoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSellBitcoinActionPerformed
        if( isSellOrderInProgress )
            return;
        isSellOrderInProgress = true;
        String orderId = "";
        try {
            validateSellBitcoinData();
            if (rdoBtnSellMarket.isSelected()) {
                try {
                    btnSellBitcoin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    orderId = sendSellOrder(true);
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    updateAccountBalance();
                    YesNoDialog dialog = new YesNoDialog("Sell Market Order","Sell Market Order placed successfully", false);
                    dialog.start();
                } catch (Exception ex) {
                    logger.error("Sell Order Failed: {}", ex.getMessage());
                    ApplicationUI.get().showError(ex);
                }
            } else {
                try {
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    orderId = sendSellOrder(false);
                    btnBuyBitcoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    updateAccountBalance();
                    YesNoDialog dialog = new YesNoDialog("Sell Limit Order","Sell Limit Order placed successfully", false);
                    dialog.start();
                } catch (Exception ex) {
                    logger.error("Sell Order Failed: {}", ex.getMessage());
                    ApplicationUI.get().showError(ex);
                }
            }
        }catch(Exception e){
            ApplicationUI.get().showError(e);
        }
        btnBuyBitcoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        isSellOrderInProgress = false;
    }//GEN-LAST:event_btnSellBitcoinActionPerformed

    private void rdoBtnBuyMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBtnBuyMarketActionPerformed
        //orderTypeMarket = rdoBtnBuyMarket.isSelected() ? true : false;
        txtBuyPrice.setText("");
        txtBuyPrice.setEnabled(false);
    }//GEN-LAST:event_rdoBtnBuyMarketActionPerformed

    private void rdoBtnBuyLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBtnBuyLimitActionPerformed
        // TODO add your handling code here:
        txtBuyPrice.setText("");
        txtBuyPrice.setEnabled(true);
    }//GEN-LAST:event_rdoBtnBuyLimitActionPerformed

    private void rdoBtnSellMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBtnSellMarketActionPerformed
        // TODO add your handling code here:
        txtSellPrice.setText("");
        txtSellPrice.setEnabled(false);
    }//GEN-LAST:event_rdoBtnSellMarketActionPerformed

    private void rdoBtnSellLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoBtnSellLimitActionPerformed
        // TODO add your handling code here:
        txtSellPrice.setText("");
        txtSellPrice.setEnabled(true);
    }//GEN-LAST:event_rdoBtnSellLimitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuyBitcoin;
    private javax.swing.ButtonGroup btnGrpBuyOrderType;
    private javax.swing.ButtonGroup btnGrpSellOrderType;
    private javax.swing.JButton btnSellBitcoin;
    private javax.swing.JPanel jPanel2;
    private com.o3.bitcoin.ui.component.XScalableLabel lblAvailableBTC;
    private com.o3.bitcoin.ui.component.XScalableLabel lblAvailableBTCValue;
    private com.o3.bitcoin.ui.component.XScalableLabel lblAvailableCurrency;
    private com.o3.bitcoin.ui.component.XScalableLabel lblAvailableCurrencyValue;
    private com.o3.bitcoin.ui.component.XScalableLabel lblBuyBitcoin;
    private com.o3.bitcoin.ui.component.XScalableLabel lblBuyOrderType;
    private com.o3.bitcoin.ui.component.XScalableLabel lblBuyPrice;
    private javax.swing.JLabel lblBuySpendAll;
    private com.o3.bitcoin.ui.component.XScalableLabel lblBuyVolume;
    private com.o3.bitcoin.ui.component.XScalableLabel lblDepositAUD;
    private com.o3.bitcoin.ui.component.XScalableLabel lblOrderType;
    private com.o3.bitcoin.ui.component.XScalableLabel lblReceiveTotal;
    private com.o3.bitcoin.ui.component.XScalableLabel lblReceiveTotalValue;
    private javax.swing.JLabel lblSellAll;
    private com.o3.bitcoin.ui.component.XScalableLabel lblSellBitcoin;
    private com.o3.bitcoin.ui.component.XScalableLabel lblSellPrice;
    private com.o3.bitcoin.ui.component.XScalableLabel lblSellVolume;
    private com.o3.bitcoin.ui.component.XScalableLabel lblSpendTotal;
    private com.o3.bitcoin.ui.component.XScalableLabel lblSpendTotalValue;
    private javax.swing.JPanel pnlBuyBitcoin;
    private javax.swing.JPanel pnlBuyBitcoinControls;
    private javax.swing.JPanel pnlBuyBitcoinHeader;
    private javax.swing.JPanel pnlBuyBitcoinHeader1;
    private javax.swing.JPanel pnlBuyBitcoinSpacer;
    private javax.swing.JPanel pnlBuyOrderType;
    private javax.swing.JPanel pnlBuyPrice;
    private javax.swing.JPanel pnlBuyPrice1;
    private javax.swing.JPanel pnlBuyPriceSpacer;
    private javax.swing.JPanel pnlBuyPriceSpacer1;
    private javax.swing.JPanel pnlBuyVolume;
    private javax.swing.JPanel pnlBuyVolume1;
    private javax.swing.JPanel pnlSellBitcoinControls;
    private javax.swing.JPanel pnlSellBitcoins;
    private javax.swing.JPanel pnlSellOrderType;
    private javax.swing.JRadioButton rdoBtnBuyLimit;
    private javax.swing.JRadioButton rdoBtnBuyMarket;
    private javax.swing.JRadioButton rdoBtnSellLimit;
    private javax.swing.JRadioButton rdoBtnSellMarket;
    private javax.swing.JTextField txtBuyPrice;
    private javax.swing.JTextField txtBuyVolume;
    private javax.swing.JTextField txtSellPrice;
    private javax.swing.JTextField txtSellVolume;
    // End of variables declaration//GEN-END:variables
}
