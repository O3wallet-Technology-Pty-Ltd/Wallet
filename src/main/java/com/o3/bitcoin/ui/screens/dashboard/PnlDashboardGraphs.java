/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.dashboard;

import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.WalletComboBoxUI;
import com.o3.bitcoin.util.ChartBuilder;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.RingPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * Class that shows graphs on Dashboard page 
 */
public class PnlDashboardGraphs extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlDashboardGraphs.class);
    private final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    private boolean loading = true;
    private Date priceGraphLastRenderedAt = null;
    private Date balancesGraphLastRenderedAt = null;

    private final int PRICE_GRAPH_REFRESH_INTERVAL = 60 * 1000; // 1 minute
    private final int BALANCES_GRAPH_REFRESH_INTERVAL = 5 * 1000; //5 seconds
    
    private List<JLabel> walletLabels = new ArrayList<>();
    private List<JPanel> walletPanels = new ArrayList<>();

    /**
     * Creates new form PnlDashboardGraphs
     */
    public PnlDashboardGraphs() {
        initComponents();
        customizeUI();
        loadData();
        startTimer();
    }

    /**
     * function that loads graphs after a specific time interval
     */
    private void startTimer() {
        Timer timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (priceGraphLastRenderedAt == null) {
                    loadPriceGraph();
                    priceGraphLastRenderedAt = new Date();
                } else {
                    if (new Date().getTime() - priceGraphLastRenderedAt.getTime() >= PRICE_GRAPH_REFRESH_INTERVAL) {
                        loadPriceGraph();
                        priceGraphLastRenderedAt = new Date();
                    }
                }
                if (balancesGraphLastRenderedAt == null) {
                    loadBalancesGraph();
                    balancesGraphLastRenderedAt = new Date();
                } else {
                    if (new Date().getTime() - balancesGraphLastRenderedAt.getTime() >= BALANCES_GRAPH_REFRESH_INTERVAL) {
                        loadBalancesGraph();
                        balancesGraphLastRenderedAt = new Date();
                    }
                }
            }
        });
        timer.start();
    }

    /**
     * function that loads initial data to show for graphs
     */
    private void loadData() {
        walletLabels = new ArrayList<>(Arrays.asList(new JLabel[]{
            lblWallet1,
            lblWallet2,
            lblWallet3,
            lblWallet4,
            lblWallet5,
        }));
        walletPanels = new ArrayList<>(Arrays.asList(new JPanel[]{
            pnlWallet1,
            pnlWallet2,
            pnlWallet3,
            pnlWallet4,
            pnlWallet5
        }));
        for(JPanel panel: walletPanels) {
            panel.setVisible(false);
        }
        for (String currency : ConfigManager.config().getCurrencies()) {
            model.addElement(currency.toUpperCase());
        }
        String currency = ConfigManager.config().getSelectedCurrency();
        if (currency != null) {
            model.setSelectedItem(currency.toUpperCase());
        }
        loading = false;
    }

    private void customizeUI() {
        cmbCurrencies.setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                list.setSelectionBackground(ResourcesProvider.Colors.APP_BG_COLOR);
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

        Object child = cmbCurrencies.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup) child;
        popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
    }

    /**
     * function that shows price history graph
     */
    synchronized private void loadPriceGraph() {
        String error = null;
        try {
            JFreeChart chart = ChartBuilder.get().getCurrencyConversionRateChart();
            ChartPanel panel = new ChartPanel(chart);
            panel.setPopupMenu(null);
            panel.setMouseZoomable(false);
            panel.setDomainZoomable(false);
            panel.setRangeZoomable(false);
            panel.setPreferredSize(new Dimension(450, 200));
            pnlPriceGraphView.removeAll();
            pnlPriceGraphView.add(panel, BorderLayout.CENTER);
        } catch (Exception e) {
            error = e.toString();
            logger.error("Unable to load Price Graph: ", e);
        } finally {
            if (error != null) {
                showError(pnlPriceGraphView, error);
            }
        }
        pnlPriceGraphContainer.validate();
        pnlPriceGraphContainer.repaint();
    }

    /**
     * function that shows balance graph
     */
    synchronized private void loadBalancesGraph() {
        String error = null;
        try {
            
            JFreeChart chart = ChartBuilder.get().getWalletBalancesChart();
            if( chart == null )
                return;
            ChartPanel panel = new ChartPanel(chart);
            //int count = ((RingPlot)panel.getChart().getPlot()).getDataset().getItemCount();
            /*List<WalletConfig> configs = ConfigManager.config().getWallets(ConfigManager.getActiveNetworkParamsString());
            for(int i=0; i<count; i++) {
                walletPanels.get(i).setVisible(true);
                walletLabels.get(i).setText(configs.get(i).getId());
            }*/
            WalletConfig config = ConfigManager.get().getFirstWallet(); 
            WalletService service = WalletManager.get().getWalletService(config.getId());
            List<HDAccount> acctList = service.getAllAccounts();
            int i = 0;
            for( HDAccount acct : acctList ){
                walletPanels.get(i).setVisible(true);
                walletLabels.get(i).setText(acct.toString());
                i++;
            }
            panel.setPopupMenu(null);
            panel.setMouseZoomable(false);
            panel.setDomainZoomable(false);
            panel.setRangeZoomable(false);
            panel.setMinimumDrawWidth(0);
            panel.setMaximumDrawWidth(Integer.MAX_VALUE);
            panel.setMinimumDrawHeight(0);
            panel.setMaximumDrawHeight(Integer.MAX_VALUE);
            panel.setPreferredSize(new Dimension(125, 125));
            pnlBalanceGraphContainer.removeAll();
            pnlBalanceGraphContainer.add(panel, BorderLayout.CENTER);
        } catch (Exception e) {
            error = e.toString();
            logger.error("Unable to load Balances Graph: ", e);
        } finally {
            if (error != null) {
                showError(pnlBalanceGraphContainer, error);
            }
        }
        pnlBalanceGraphContainer.validate();
        pnlBalanceGraphContainer.repaint();
    }

    private void showError(JPanel container, String error) {
        container.removeAll();
        container.add(getErrorLabel(error), BorderLayout.CENTER);
        container.validate();
        container.repaint();
    }

    private JLabel getErrorLabel(String error) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setText("<html>" + error + "</html>");
        return label;
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

        pnlOtherGraphs = new javax.swing.JPanel();
        pnlBalanceGraphContainer = new javax.swing.JPanel();
        pnlWalletNames = new javax.swing.JPanel();
        pnlWallet1 = new javax.swing.JPanel();
        pnlBullet1 = new javax.swing.JPanel();
        lblWallet1 = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlWallet2 = new javax.swing.JPanel();
        pnlBullet2 = new javax.swing.JPanel();
        lblWallet2 = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlWallet3 = new javax.swing.JPanel();
        pnlBullet3 = new javax.swing.JPanel();
        lblWallet3 = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlWallet4 = new javax.swing.JPanel();
        pnlBullet4 = new javax.swing.JPanel();
        lblWallet4 = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlWallet5 = new javax.swing.JPanel();
        pnlBullet5 = new javax.swing.JPanel();
        lblWallet5 = new com.o3.bitcoin.ui.component.XScalableLabel();
        pnlSpacer = new javax.swing.JPanel();
        pnlPriceGraphContainer = new javax.swing.JPanel();
        pnlPriceGraphView = new javax.swing.JPanel();
        pnlGraphControlsContainer = new javax.swing.JPanel();
        pnlGraphControls = new javax.swing.JPanel();
        lblGraphTitle = new javax.swing.JLabel();
        cmbCurrencies = new javax.swing.JComboBox();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlOtherGraphs.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        pnlOtherGraphs.setOpaque(false);
        pnlOtherGraphs.setLayout(new java.awt.GridBagLayout());

        pnlBalanceGraphContainer.setOpaque(false);
        pnlBalanceGraphContainer.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlOtherGraphs.add(pnlBalanceGraphContainer, gridBagConstraints);

        pnlWalletNames.setOpaque(false);
        pnlWalletNames.setLayout(new java.awt.GridBagLayout());

        pnlWallet1.setOpaque(false);
        pnlWallet1.setLayout(new java.awt.BorderLayout(10, 0));

        pnlBullet1.setBackground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        pnlBullet1.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout pnlBullet1Layout = new javax.swing.GroupLayout(pnlBullet1);
        pnlBullet1.setLayout(pnlBullet1Layout);
        pnlBullet1Layout.setHorizontalGroup(
            pnlBullet1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlBullet1Layout.setVerticalGroup(
            pnlBullet1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlWallet1.add(pnlBullet1, java.awt.BorderLayout.WEST);

        lblWallet1.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblWallet1.setText("Wallet 1");
        lblWallet1.setFont(Fonts.BOLD_SMALL_FONT);
        pnlWallet1.add(lblWallet1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlWalletNames.add(pnlWallet1, gridBagConstraints);

        pnlWallet2.setOpaque(false);
        pnlWallet2.setLayout(new java.awt.BorderLayout(10, 0));

        pnlBullet2.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        pnlBullet2.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout pnlBullet2Layout = new javax.swing.GroupLayout(pnlBullet2);
        pnlBullet2.setLayout(pnlBullet2Layout);
        pnlBullet2Layout.setHorizontalGroup(
            pnlBullet2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlBullet2Layout.setVerticalGroup(
            pnlBullet2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlWallet2.add(pnlBullet2, java.awt.BorderLayout.WEST);

        lblWallet2.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblWallet2.setText("Wallet 2");
        lblWallet2.setFont(Fonts.BOLD_SMALL_FONT);
        pnlWallet2.add(lblWallet2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlWalletNames.add(pnlWallet2, gridBagConstraints);

        pnlWallet3.setOpaque(false);
        pnlWallet3.setLayout(new java.awt.BorderLayout(10, 0));

        pnlBullet3.setBackground(ResourcesProvider.Colors.NAV_MENU_CONTACTS_COLOR);
        pnlBullet3.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout pnlBullet3Layout = new javax.swing.GroupLayout(pnlBullet3);
        pnlBullet3.setLayout(pnlBullet3Layout);
        pnlBullet3Layout.setHorizontalGroup(
            pnlBullet3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlBullet3Layout.setVerticalGroup(
            pnlBullet3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        pnlWallet3.add(pnlBullet3, java.awt.BorderLayout.WEST);

        lblWallet3.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblWallet3.setText("Wallet 3");
        lblWallet3.setFont(Fonts.BOLD_SMALL_FONT);
        pnlWallet3.add(lblWallet3, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlWalletNames.add(pnlWallet3, gridBagConstraints);

        pnlWallet4.setOpaque(false);
        pnlWallet4.setLayout(new java.awt.BorderLayout(10, 0));

        pnlBullet4.setBackground(ResourcesProvider.Colors.NAV_MENU_EXCHANGE_COLOR);
        pnlBullet4.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout pnlBullet4Layout = new javax.swing.GroupLayout(pnlBullet4);
        pnlBullet4.setLayout(pnlBullet4Layout);
        pnlBullet4Layout.setHorizontalGroup(
            pnlBullet4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlBullet4Layout.setVerticalGroup(
            pnlBullet4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        pnlWallet4.add(pnlBullet4, java.awt.BorderLayout.WEST);

        lblWallet4.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblWallet4.setText("Wallet 4");
        lblWallet4.setFont(Fonts.BOLD_SMALL_FONT);
        pnlWallet4.add(lblWallet4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlWalletNames.add(pnlWallet4, gridBagConstraints);

        pnlWallet5.setOpaque(false);
        pnlWallet5.setLayout(new java.awt.BorderLayout(10, 0));

        pnlBullet5.setBackground(ResourcesProvider.Colors.NAV_MENU_SETTINGS_COLOR);
        pnlBullet5.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout pnlBullet5Layout = new javax.swing.GroupLayout(pnlBullet5);
        pnlBullet5.setLayout(pnlBullet5Layout);
        pnlBullet5Layout.setHorizontalGroup(
            pnlBullet5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlBullet5Layout.setVerticalGroup(
            pnlBullet5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        pnlWallet5.add(pnlBullet5, java.awt.BorderLayout.WEST);

        lblWallet5.setForeground(Colors.DEFAULT_HEADING_COLOR);
        lblWallet5.setText("Wallet 5");
        lblWallet5.setFont(Fonts.BOLD_SMALL_FONT);
        pnlWallet5.add(lblWallet5, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlWalletNames.add(pnlWallet5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlOtherGraphs.add(pnlWalletNames, gridBagConstraints);

        javax.swing.GroupLayout pnlSpacerLayout = new javax.swing.GroupLayout(pnlSpacer);
        pnlSpacer.setLayout(pnlSpacerLayout);
        pnlSpacerLayout.setHorizontalGroup(
            pnlSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlSpacerLayout.setVerticalGroup(
            pnlSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlOtherGraphs.add(pnlSpacer, gridBagConstraints);

        add(pnlOtherGraphs, java.awt.BorderLayout.EAST);

        pnlPriceGraphContainer.setOpaque(false);
        pnlPriceGraphContainer.setLayout(new java.awt.BorderLayout());

        pnlPriceGraphView.setOpaque(false);
        pnlPriceGraphView.setLayout(new java.awt.BorderLayout());
        pnlPriceGraphContainer.add(pnlPriceGraphView, java.awt.BorderLayout.CENTER);

        pnlGraphControlsContainer.setOpaque(false);

        pnlGraphControls.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);

        lblGraphTitle.setFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        lblGraphTitle.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblGraphTitle.setText("Weekly Bitcoin currency conversion rates in : ");
        pnlGraphControls.add(lblGraphTitle);

        cmbCurrencies.setBackground(ResourcesProvider.Colors.APP_BG_COLOR);
        cmbCurrencies.setFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        cmbCurrencies.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbCurrencies.setModel(model);
        cmbCurrencies.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        cmbCurrencies.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCurrencies.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCurrenciesItemStateChanged(evt);
            }
        });
        pnlGraphControls.add(cmbCurrencies);
        cmbCurrencies.setUI((ComboBoxUI) WalletComboBoxUI.createUI(cmbCurrencies, ResourcesProvider.Colors.APP_BG_COLOR));

        pnlGraphControlsContainer.add(pnlGraphControls);

        pnlPriceGraphContainer.add(pnlGraphControlsContainer, java.awt.BorderLayout.SOUTH);

        add(pnlPriceGraphContainer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCurrenciesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCurrenciesItemStateChanged
        if (loading) {
            return;
        }
        if (evt.getStateChange() == ItemEvent.SELECTED && cmbCurrencies.getSelectedItem() != null) {
            String currency = (String) model.getSelectedItem();
            try {
                ConfigManager.config().setSelectedCurrency(currency);
                ConfigManager.get().save();
                loadPriceGraph();
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationUI.get().showError(e);
            }
        }
    }//GEN-LAST:event_cmbCurrenciesItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbCurrencies;
    private javax.swing.JLabel lblGraphTitle;
    private com.o3.bitcoin.ui.component.XScalableLabel lblWallet1;
    private com.o3.bitcoin.ui.component.XScalableLabel lblWallet2;
    private com.o3.bitcoin.ui.component.XScalableLabel lblWallet3;
    private com.o3.bitcoin.ui.component.XScalableLabel lblWallet4;
    private com.o3.bitcoin.ui.component.XScalableLabel lblWallet5;
    private javax.swing.JPanel pnlBalanceGraphContainer;
    private javax.swing.JPanel pnlBullet1;
    private javax.swing.JPanel pnlBullet2;
    private javax.swing.JPanel pnlBullet3;
    private javax.swing.JPanel pnlBullet4;
    private javax.swing.JPanel pnlBullet5;
    private javax.swing.JPanel pnlGraphControls;
    private javax.swing.JPanel pnlGraphControlsContainer;
    private javax.swing.JPanel pnlOtherGraphs;
    private javax.swing.JPanel pnlPriceGraphContainer;
    private javax.swing.JPanel pnlPriceGraphView;
    private javax.swing.JPanel pnlSpacer;
    private javax.swing.JPanel pnlWallet1;
    private javax.swing.JPanel pnlWallet2;
    private javax.swing.JPanel pnlWallet3;
    private javax.swing.JPanel pnlWallet4;
    private javax.swing.JPanel pnlWallet5;
    private javax.swing.JPanel pnlWalletNames;
    // End of variables declaration//GEN-END:variables
}
