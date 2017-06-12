/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.wallet;

import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.WalletComboBoxUI;
import com.o3.bitcoin.util.ChartBuilder;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.TransactionsChart;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * Class that implements graphs of Accounts page 
 */
public class PnlWalletGraphs extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlWalletGraphs.class);
    private int days;
    private WalletConfig config;
    private TransactionsChart.Type type = TransactionsChart.Type.BOTH;
    private DefaultComboBoxModel model = new DefaultComboBoxModel(TransactionsChart.Type.values());

    /**
     * Creates new form PnlDashboardGraphs
     */
    public PnlWalletGraphs() {
        initComponents();
        customizeUI();
    }

    /**
     * function that customize look and feel of graph combos
     */
    private void customizeUI() {
        cmbGraphType.setRenderer(new BasicComboBoxRenderer() {

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

        Object child = cmbGraphType.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup) child;
        popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
        cmbGraphType.setSelectedIndex(2);
    }

    /**
     * function that loads debit/credit chart
     * @param config wallet configuration
     * @param days number of past days 
     */
    synchronized public void loadGraph(final WalletConfig config, final int days) {
        this.config = config;
        this.days = days;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String error = null;
                try {
                    TransactionsChart chartData = ChartBuilder.get().getWalletTransactionsChart(config, days, type);
                    if (chartData.isDebitAndCredit() || chartData.isDebit()) {
                        lblTotalDebit.setText(String.format("%.4f", chartData.getTotalDebit()));
                    }
                    if (chartData.isDebitAndCredit() || !chartData.isDebit()) {
                        lblTotalCredit.setText(" " + String.format("%.4f", chartData.getTotalCredit()));
                    }
                    lblTotalDebit.setVisible(chartData.isDebitAndCredit() || chartData.isDebit());
                    lblTotalCredit.setVisible(chartData.isDebitAndCredit() || !chartData.isDebit());
                    JFreeChart chart = chartData.getChart();
                    ChartPanel panel = new ChartPanel(chart);
                    panel.setPopupMenu(null);
                    panel.setMouseZoomable(false);
                    panel.setDomainZoomable(false);
                    panel.setRangeZoomable(false);
                    panel.setPreferredSize(new Dimension(450, 200));
                    pnlGraphView.removeAll();
                    pnlGraphView.add(panel, BorderLayout.CENTER);
                } catch (Exception e) {
                    error = e.toString();
                    logger.error("Unable to load Wallet Graph: ", e);
                } finally {
                    if (error != null) {
                        showError(pnlGraphView, error);
                    }
                }
                pnlGraphView.validate();
                pnlGraphView.repaint();
                validate();
                repaint();
            }
        });
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

        pnlGraphView = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();
        lblTotalDebit = new javax.swing.JLabel();
        pnlSpacerControls = new javax.swing.JPanel();
        cmbGraphType = new javax.swing.JComboBox();
        lblTotalCredit = new javax.swing.JLabel();
        lblLeftSpacer = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlGraphView.setOpaque(false);
        pnlGraphView.setLayout(new java.awt.BorderLayout());
        add(pnlGraphView, java.awt.BorderLayout.CENTER);

        pnlControls.setOpaque(false);
        pnlControls.setPreferredSize(new java.awt.Dimension(125, 10));
        pnlControls.setLayout(new java.awt.GridBagLayout());

        lblTotalDebit.setFont(ResourcesProvider.Fonts.WALLETS_TOTAL_CR_DR_FONT);
        lblTotalDebit.setForeground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        lblTotalDebit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalDebit.setText("-0.0000");
        lblTotalDebit.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, ResourcesProvider.Colors.LIGHT_HEADING_COLOR));
        lblTotalDebit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlControls.add(lblTotalDebit, gridBagConstraints);

        pnlSpacerControls.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlControls.add(pnlSpacerControls, gridBagConstraints);

        cmbGraphType.setBackground(ResourcesProvider.Colors.APP_BG_COLOR);
        cmbGraphType.setFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        cmbGraphType.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbGraphType.setModel(model);
        cmbGraphType.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        cmbGraphType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbGraphType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbGraphTypeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlControls.add(cmbGraphType, gridBagConstraints);
        cmbGraphType.setUI((ComboBoxUI) WalletComboBoxUI.createUI(cmbGraphType, ResourcesProvider.Colors.APP_BG_COLOR));

        lblTotalCredit.setFont(ResourcesProvider.Fonts.WALLETS_TOTAL_CR_DR_FONT);
        lblTotalCredit.setForeground(ResourcesProvider.Colors.NAV_MENU_CONTACTS_COLOR);
        lblTotalCredit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalCredit.setText(" 0.0000");
        lblTotalCredit.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, ResourcesProvider.Colors.LIGHT_HEADING_COLOR));
        lblTotalCredit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlControls.add(lblTotalCredit, gridBagConstraints);

        add(pnlControls, java.awt.BorderLayout.EAST);

        lblLeftSpacer.setOpaque(false);
        lblLeftSpacer.setPreferredSize(new java.awt.Dimension(80, 10));
        add(lblLeftSpacer, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbGraphTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbGraphTypeItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            try {
                type = (TransactionsChart.Type) cmbGraphType.getSelectedItem();
                loadGraph(config, days);
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationUI.get().showError(e);
            }
        }
    }//GEN-LAST:event_cmbGraphTypeItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbGraphType;
    private javax.swing.JPanel lblLeftSpacer;
    private javax.swing.JLabel lblTotalCredit;
    private javax.swing.JLabel lblTotalDebit;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlGraphView;
    private javax.swing.JPanel pnlSpacerControls;
    // End of variables declaration//GEN-END:variables
}
