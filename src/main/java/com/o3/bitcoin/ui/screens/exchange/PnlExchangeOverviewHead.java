/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.model.ExchangeConfig;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.exchange.BTCMarketExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import com.o3.bitcoin.util.exchange.TickerDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.marketdata.Ticker;

/**
 *
 * @author
 */
public class PnlExchangeOverviewHead extends javax.swing.JPanel {

    private ExchangeService exchangeService = null;
    private DefaultComboBoxModel<String> currencyPairModel = new DefaultComboBoxModel<>();
    private String currentCurrencyPair = "";
    private String currentExchange = "";

    /**
     * Creates new form PnlExchangeOverviewHead
     */
    public PnlExchangeOverviewHead() {
        initComponents();
        lblDailyChange.setVisible(false);
        pnlDailyChanges.setVisible(false);
    }
    
    private void resetValues() {
        lblLastPriceValue.setText("...");
        lblBidValue.setText("...");
        lblAskValue.setText("...");
        lblVolume24hValue.setText("...");
                
    }

    private void prepareUI() {
        cmbCurrencyPairs.setRenderer(new BasicComboBoxRenderer() {
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

        Object child = cmbCurrencyPairs.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup) child;
        popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
    }
    
    private TickerDTO getTickerValues(String exchangeName) throws IOException{
        TickerDTO ticker = null;
        exchangeService = ExchangeServiceFactory.getExchange(exchangeName);
        if (exchangeService == null) {
            return null;
        }
        exchangeService.setCurrencyPair(exchangeName, exchangeService.getSelectedCurrencyPair());
        ticker = exchangeService.getTicker(exchangeService.getSelectedCurrencyPair());
        return ticker;
    }
    
    private void updateValues(TickerDTO ticker ) {
        if (ticker.getVolume() != null) {
            this.lblVolume24hValue.setText(ticker.getVolume().toString());
        }
        lblAskValue.setText(ticker.getAsk().toEngineeringString());
        lblBidValue.setText(ticker.getBid().toEngineeringString());
        lblLastPriceValue.setText(ticker.getLast().toEngineeringString());
    }
    
    public void updateTickerValues(String exchangeName) throws IOException {
        TickerDTO ticker = getTickerValues(exchangeName);
        if(ticker != null)
            updateValues(ticker);
    }

    public void updateTickerValues() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TickerDTO ticker = getTickerValues(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
                    if(ticker != null) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                updateValues(ticker);
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Exception in thread while getting ticker =" + e.getMessage());
                }
            }
        }).start();

    }

    public void populateCurrencyModel(String exchangeName) {
        currentExchange = exchangeName;
        exchangeService = ExchangeServiceFactory.getExchange(exchangeName);
        if (exchangeService == null) {
            return;
        }
        List<String> currencyPairs = exchangeService.getExchangeCurrencyPairs(exchangeName);
        currencyPairModel.removeAllElements();
        if (currencyPairs != null) {
            if (currencyPairs.size() > 0) {

                for (String pair : currencyPairs) {
                    currencyPairModel.addElement(pair);
                }
                currentCurrencyPair = currencyPairModel.getElementAt(0);

            }
        }
    }

    public String getSelectedCurrencyPair() {
        return cmbCurrencyPairs.getSelectedItem().toString();
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

        pnlExchangeCurrencyPairs = new javax.swing.JPanel();
        xScalableLabel1 = new com.o3.bitcoin.ui.component.XScalableLabel();
        cmbCurrencyPairs = new javax.swing.JComboBox();
        pnlLineSeparator1 = new javax.swing.JPanel();
        pnlSummary = new javax.swing.JPanel();
        pnlSummaryControls = new javax.swing.JPanel();
        lblLastPrice = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblDailyChange = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblBid = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblAsk = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblVolume24h = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblLastPriceValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlDailyChanges = new javax.swing.JPanel();
        lblDailyChangeValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblDailyChangePercent = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblBidValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblAskValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        lblVolume24hValue = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlSummarySpacer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pnlExchangeCurrencyPairs.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        xScalableLabel1.setText("Bitcoin Exchange - ");
        xScalableLabel1.setFont(Fonts.EXCHANGE_STATS_LARGE_FONT);
        pnlExchangeCurrencyPairs.add(xScalableLabel1);

        cmbCurrencyPairs.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbCurrencyPairs.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbCurrencyPairs.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbCurrencyPairs.setModel(currencyPairModel);
        cmbCurrencyPairs.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbCurrencyPairs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCurrencyPairs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCurrencyPairsItemStateChanged(evt);
            }
        });
        pnlExchangeCurrencyPairs.add(cmbCurrencyPairs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(pnlExchangeCurrencyPairs, gridBagConstraints);

        pnlLineSeparator1.setBackground(new java.awt.Color(153, 153, 153));
        pnlLineSeparator1.setMinimumSize(new java.awt.Dimension(100, 2));
        pnlLineSeparator1.setPreferredSize(new java.awt.Dimension(558, 1));

        javax.swing.GroupLayout pnlLineSeparator1Layout = new javax.swing.GroupLayout(pnlLineSeparator1);
        pnlLineSeparator1.setLayout(pnlLineSeparator1Layout);
        pnlLineSeparator1Layout.setHorizontalGroup(
            pnlLineSeparator1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 558, Short.MAX_VALUE)
        );
        pnlLineSeparator1Layout.setVerticalGroup(
            pnlLineSeparator1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(pnlLineSeparator1, gridBagConstraints);

        pnlSummary.setLayout(new java.awt.GridBagLayout());

        pnlSummaryControls.setLayout(new java.awt.GridBagLayout());

        lblLastPrice.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblLastPrice.setText("LAST PRICE");
        lblLastPrice.setFont(Fonts.DEFAULT_HEADING_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblLastPrice, gridBagConstraints);

        lblDailyChange.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblDailyChange.setText("DAILY CHANGE");
        lblDailyChange.setFont(Fonts.DEFAULT_HEADING_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblDailyChange, gridBagConstraints);

        lblBid.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblBid.setText("BID");
        lblBid.setFont(Fonts.DEFAULT_HEADING_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblBid, gridBagConstraints);

        lblAsk.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblAsk.setText("ASK");
        lblAsk.setFont(Fonts.DEFAULT_HEADING_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblAsk, gridBagConstraints);

        lblVolume24h.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblVolume24h.setText("VOLUME 24h");
        lblVolume24h.setFont(Fonts.DEFAULT_HEADING_FONT);
        pnlSummaryControls.add(lblVolume24h, new java.awt.GridBagConstraints());

        lblLastPriceValue.setText("NA");
        lblLastPriceValue.setFont(Fonts.EXCHANGE_STATS_LARGE_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblLastPriceValue, gridBagConstraints);

        pnlDailyChanges.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));

        lblDailyChangeValue.setText("NA");
        pnlDailyChanges.add(lblDailyChangeValue);

        lblDailyChangePercent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/minimize.png"))); // NOI18N
        lblDailyChangePercent.setText("NA");
        lblDailyChangePercent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblDailyChangePercent.setIconTextGap(1);
        lblDailyChangePercent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlDailyChanges.add(lblDailyChangePercent);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(pnlDailyChanges, gridBagConstraints);

        lblBidValue.setText("NA");
        lblBidValue.setFont(Fonts.EXCHANGE_STATS_LARGE_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblBidValue, gridBagConstraints);

        lblAskValue.setText("NA");
        lblAskValue.setFont(Fonts.EXCHANGE_STATS_LARGE_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 40);
        pnlSummaryControls.add(lblAskValue, gridBagConstraints);

        lblVolume24hValue.setText("NA");
        lblVolume24hValue.setFont(Fonts.EXCHANGE_STATS_LARGE_FONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlSummaryControls.add(lblVolume24hValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlSummary.add(pnlSummaryControls, gridBagConstraints);

        javax.swing.GroupLayout pnlSummarySpacerLayout = new javax.swing.GroupLayout(pnlSummarySpacer);
        pnlSummarySpacer.setLayout(pnlSummarySpacerLayout);
        pnlSummarySpacerLayout.setHorizontalGroup(
            pnlSummarySpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 151, Short.MAX_VALUE)
        );
        pnlSummarySpacerLayout.setVerticalGroup(
            pnlSummarySpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlSummary.add(pnlSummarySpacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(pnlSummary, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCurrencyPairsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCurrencyPairsItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            currentCurrencyPair = evt.getItem().toString().toLowerCase();
            exchangeService = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
            exchangeService.setCurrencyPair(ApplicationUI.get().getExchangeScreen().getSelectedExchange(), currentCurrencyPair);
            try {
                resetValues();
                ApplicationUI.get().getExchangeScreen().getExchangeStatsScreen().getBuySellPanel().resetValues();
                updateTickerValues();
                ApplicationUI.get().getExchangeScreen().getExchangeStatsScreen().getBuySellPanel().adjustLabels();
                ApplicationUI.get().getExchangeScreen().getExchangeStatsScreen().getBuySellPanel().updateAccountBalance();
                ApplicationUI.get().getExchangeScreen().getExchangeStatsScreen().loadGraph();
            } catch (Exception ex) {
                Logger.getLogger(PnlExchangeOverviewHead.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_cmbCurrencyPairsItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbCurrencyPairs;
    private com.o3.bitcoin.ui.component.XScalableLabel lblAsk;
    private com.o3.bitcoin.ui.component.XScalableLabel lblAskValue;
    private com.o3.bitcoin.ui.component.XScalableLabel lblBid;
    private com.o3.bitcoin.ui.component.XScalableLabel lblBidValue;
    private com.o3.bitcoin.ui.component.XScalableLabel lblDailyChange;
    private com.o3.bitcoin.ui.component.XScalableLabel lblDailyChangePercent;
    private com.o3.bitcoin.ui.component.XScalableLabel lblDailyChangeValue;
    private com.o3.bitcoin.ui.component.XScalableLabel lblLastPrice;
    private com.o3.bitcoin.ui.component.XScalableLabel lblLastPriceValue;
    private com.o3.bitcoin.ui.component.XScalableLabel lblVolume24h;
    private com.o3.bitcoin.ui.component.XScalableLabel lblVolume24hValue;
    private javax.swing.JPanel pnlDailyChanges;
    private javax.swing.JPanel pnlExchangeCurrencyPairs;
    private javax.swing.JPanel pnlLineSeparator1;
    private javax.swing.JPanel pnlSummary;
    private javax.swing.JPanel pnlSummaryControls;
    private javax.swing.JPanel pnlSummarySpacer;
    private com.o3.bitcoin.ui.component.XScalableLabel xScalableLabel1;
    // End of variables declaration//GEN-END:variables
}
