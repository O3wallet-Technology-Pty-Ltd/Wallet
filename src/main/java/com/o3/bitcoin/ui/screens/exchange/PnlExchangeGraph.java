/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.util.ChartBuilder;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import com.o3.bitcoin.util.exchange.ExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import com.o3.bitcoin.util.exchange.GraphDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;

/**
 *
 * @author
 */
public class PnlExchangeGraph extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlExchangeGraph.class);
    
    private int days;
    private ExchangeService exchange = null;
    private List<JButton> graphButtons;
    
    /**
     * Creates new form PnlExchangeGraph
     */
    public PnlExchangeGraph() {
        initComponents();
        graphButtons = new ArrayList<>();
        graphButtons.add(btnGraph1);
        graphButtons.add(btnGraph2);
        graphButtons.add(btnGraph3);
        graphButtons.add(btnGraph4);
        graphButtons.add(btnGraph5);
        graphButtons.add(btnGraph6);
        setSelectedButton(btnGraph6);
    }
    
    private void disableGraphButtons() {
        btnGraph1.setEnabled(false);
        btnGraph2.setEnabled(false);
        btnGraph3.setEnabled(false);
        btnGraph4.setEnabled(false);
        btnGraph5.setEnabled(false);
        btnGraph6.setEnabled(false);
    }
    
    private void enableGraphButtons() {
        btnGraph1.setEnabled(true);
        btnGraph2.setEnabled(true);
        btnGraph3.setEnabled(true);
        btnGraph4.setEnabled(true);
        btnGraph5.setEnabled(true);
        btnGraph6.setEnabled(true);
    }
    
    public void loadData() {
        setSelectedButton(btnGraph6);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(24,10,dateFormater);
    }
    /**
     * function that loads exchange chart
     * @param days number of past days 
     */
    synchronized public void loadExchangeGraph(final int hours, final int intervalMin, final SimpleDateFormat dateFormater) {
        this.days = days;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    long startTimeInMillis = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours);
                    long intervalInMillis = TimeUnit.MINUTES.toMillis(intervalMin);
                    final List<GraphDTO> lstTrades = getGraphData(startTimeInMillis, intervalInMillis);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String error = null;
                            try {
                                JFreeChart chart = ChartBuilder.get().getExchangeChart(lstTrades,dateFormater);
                                ChartPanel panel = new ChartPanel(chart);
                                panel.setPopupMenu(null);
                                panel.setMouseZoomable(false);
                                panel.setDomainZoomable(false);
                                panel.setRangeZoomable(false);
                                panel.setPreferredSize(new Dimension(250, 100));
                                pnlExGraph.removeAll();
                                if( lstTrades.size() > 0 ) {
                                    enableGraphButtons();
                                    pnlExGraph.add(panel, BorderLayout.CENTER);
                                }
                                else {
                                    disableGraphButtons();
                                    JLabel errorMsgLbl = new JLabel("Graph data not supported for selected exchange");
                                    errorMsgLbl.setForeground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
                                    pnlExGraph.add(errorMsgLbl, BorderLayout.CENTER);
                                }
                            } catch (Exception e) {
                                error = e.toString();
                                logger.error("Unable to load Exchange Graph: ", e);
                                System.out.println("Unable to load Graph Exception ="+e.getMessage());
                            } /*finally {
                                if (error != null) {
                                    showError(pnlExGraph, error);
                                }
                            }*/
                            pnlExGraph.validate();
                            pnlExGraph.repaint();
                            validate();
                            repaint();
                        }
                    });
                }catch(Exception e) {
                    logger.error("Unable to load Graph Data: ", e);
                }
            }
        }).start();
    }
    
    /*
     Gets the price, time and volume data
     startTime is in epoc
     intervalTime is in epoc
     */
    
    public List<GraphDTO> getGraphData(long startTime, long intervalTime) throws Exception {
        List<GraphDTO> graphData = new ArrayList<GraphDTO>();
        GraphDTO  graphDTO = null;
        boolean startTimeFound = false;
        int index = -1,entryGap = 0;
        exchange = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
        if(!exchange.getExchangeName().equalsIgnoreCase("btcmarkets")) {
            Trades trades = exchange.getTradeData(exchange.getSelectedCurrencyPair());
            long totalTrades = trades.getTrades().size();
            for (Trade trade : trades.getTrades()) {
                index++;
                if(trade.getTimestamp().getTime() >= startTime && !startTimeFound){
                    startTimeFound=true;
                    entryGap = (int)(totalTrades - index ) / 18;
                    graphDTO = new GraphDTO();
                    graphDTO.setPrice(trade.getPrice());
                    graphDTO.setTime(trade.getTimestamp());
                    graphDTO.setVolume(trade.getTradableAmount());
                    graphData.add(graphDTO);
                    index = 0;
                }else if(trade.getTimestamp().getTime() >= startTime && startTimeFound && index == entryGap){
                    graphDTO = new GraphDTO();
                    graphDTO.setPrice(trade.getPrice());
                    graphDTO.setTime(trade.getTimestamp());
                    graphDTO.setVolume(trade.getTradableAmount());
                    graphData.add(graphDTO);
                    index = 0;
                }
            }
        }
        else {
            JSONObject trade;
            List<JSONObject> arrJO = exchange.getTradeData(exchange.getAltcoinCurrency().toUpperCase(),exchange.getFiatCurrency().toUpperCase());
            long totalTrades = arrJO.size();
            for (int i = (arrJO.size() - 1); i >= 0; i--) {
                trade = arrJO.get(i);
                index++;
                if(trade.getLong("date") >= (long)(startTime / 1000) && !startTimeFound){
                    startTimeFound=true;
                    entryGap = (int)(totalTrades - index ) / 18;
                    graphDTO = new GraphDTO();
                    graphDTO.setPrice(BigDecimal.valueOf(trade.getDouble("price")));
                    Date tradeDate = new Date();
                    tradeDate.setTime(trade.getLong("date")*1000);
                    graphDTO.setTime(tradeDate);
                    graphDTO.setVolume(BigDecimal.valueOf(trade.getDouble("amount")));
                    graphData.add(graphDTO);
                    index = 0;
                }else if(trade.getLong("date") >= (long)(startTime / 1000) && startTimeFound && index == entryGap){
                    graphDTO = new GraphDTO();
                    graphDTO.setPrice(BigDecimal.valueOf(trade.getDouble("price")));
                    Date tradeDate = new Date();
                    tradeDate.setTime(trade.getLong("date")*1000);
                    graphDTO.setTime(tradeDate);
                    graphDTO.setVolume(BigDecimal.valueOf(trade.getDouble("amount")));
                    graphData.add(graphDTO);
                    index = 0;
                }
            }
        }
        return graphData;
    }

    /*public List<GraphDTO> getGraphData(long startTime, long intervalTime) throws Exception {
        List<GraphDTO> graphData = new ArrayList<GraphDTO>();
        GraphDTO  graphDTO= null;
        boolean startTimeFound=false;
        long nextIntervalTime=0;
        exchange = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
        Trades trades = exchange.getTradeData(exchange.getSelectedCurrencyPair());
        for (Trade trade : trades.getTrades()) {
            if(trade.getTimestamp().getTime() >= startTime && startTimeFound==false){
                startTimeFound=true;
                nextIntervalTime = trade.getTimestamp().getTime()+intervalTime;
            }else if(trade.getTimestamp().getTime() >= nextIntervalTime && startTimeFound==true){
                graphDTO = new GraphDTO();
                graphDTO.setPrice(trade.getPrice());
                graphDTO.setTime(trade.getTimestamp());
                graphDTO.setVolume(trade.getTradableAmount());
                graphData.add(graphDTO);
                nextIntervalTime = trade.getTimestamp().getTime()+intervalTime;
            }
        }
        
        if( graphData.size() < 10 ) {
            graphData.clear();
            int index = 0;
            boolean firstEntry = true;
            int entryGap = trades.getTrades().size() / 15;
            for (Trade trade : trades.getTrades()) {
                index++;
                if(firstEntry) {
                    graphDTO = new GraphDTO();
                    graphDTO.setPrice(trade.getPrice());
                    graphDTO.setTime(trade.getTimestamp());
                    graphDTO.setVolume(trade.getTradableAmount());
                    graphData.add(graphDTO);
                    firstEntry = false;
                }
                else if(index == entryGap){
                    graphDTO = new GraphDTO();
                    graphDTO.setPrice(trade.getPrice());
                    graphDTO.setTime(trade.getTimestamp());
                    graphDTO.setVolume(trade.getTradableAmount());
                    graphData.add(graphDTO);
                    index = 0;
                }
            }   
        }
        return graphData;
    }*/

    /*private Trades getTradeData() {
        Trades trades = null;
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        PollingMarketDataService marketDataService = bitstamp.getPollingMarketDataService();
        try {
            trades = marketDataService.getTrades(CurrencyPair.BTC_USD, BitstampMarketDataServiceRaw.BitstampTime.DAY);

        } catch (Exception e) {
            logger.error("Unable to load  Graph Data: ", e.getMessage());
        }
        return trades;
    }*/
    
    
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

        pnlGraphInterval = new javax.swing.JPanel();
        btnGraph1 = new javax.swing.JButton();
        btnGraph2 = new javax.swing.JButton();
        btnGraph3 = new javax.swing.JButton();
        btnGraph4 = new javax.swing.JButton();
        btnGraph5 = new javax.swing.JButton();
        btnGraph6 = new javax.swing.JButton();
        pnlExGraph = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        pnlGraphInterval.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnGraph1.setText("3h");
        btnGraph1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGraph1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraph1ActionPerformed(evt);
            }
        });
        pnlGraphInterval.add(btnGraph1);

        btnGraph2.setText("6h");
        btnGraph2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGraph2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraph2ActionPerformed(evt);
            }
        });
        pnlGraphInterval.add(btnGraph2);

        btnGraph3.setText("12h");
        btnGraph3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGraph3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraph3ActionPerformed(evt);
            }
        });
        pnlGraphInterval.add(btnGraph3);

        btnGraph4.setText("15h");
        btnGraph4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGraph4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraph4ActionPerformed(evt);
            }
        });
        pnlGraphInterval.add(btnGraph4);

        btnGraph5.setText("18h");
        btnGraph5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGraph5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraph5ActionPerformed(evt);
            }
        });
        pnlGraphInterval.add(btnGraph5);

        btnGraph6.setText("24h");
        btnGraph6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGraph6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraph6ActionPerformed(evt);
            }
        });
        pnlGraphInterval.add(btnGraph6);

        add(pnlGraphInterval, java.awt.BorderLayout.NORTH);

        pnlExGraph.setLayout(new java.awt.BorderLayout());
        add(pnlExGraph, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void setSelectedButton(JButton selectedButton) {
        for(int i = 0; i < graphButtons.size(); i++) {
            if( selectedButton == graphButtons.get(i))
                Utils.themeSelectButton(graphButtons.get(i), ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
            else
               Utils.themeUnSelectButton(graphButtons.get(i)); 
        }
    }
    
    private void btnGraph1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraph1ActionPerformed
        // TODO add your handling code here:
        setSelectedButton(btnGraph1);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(3,10,dateFormater);
    }//GEN-LAST:event_btnGraph1ActionPerformed

    private void btnGraph2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraph2ActionPerformed
        // TODO add your handling code here:
        setSelectedButton(btnGraph2);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(6,10*2,dateFormater);
    }//GEN-LAST:event_btnGraph2ActionPerformed

    private void btnGraph3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraph3ActionPerformed
        // TODO add your handling code here:
        setSelectedButton(btnGraph3);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(12,10*4,dateFormater);
    }//GEN-LAST:event_btnGraph3ActionPerformed

    private void btnGraph4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraph4ActionPerformed
        // TODO add your handling code here:
        setSelectedButton(btnGraph4);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(15,10*5,dateFormater);
    }//GEN-LAST:event_btnGraph4ActionPerformed

    private void btnGraph5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraph5ActionPerformed
        // TODO add your handling code here:
        setSelectedButton(btnGraph5);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(18,10*6,dateFormater);
    }//GEN-LAST:event_btnGraph5ActionPerformed

    private void btnGraph6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraph6ActionPerformed
        // TODO add your handling code here:
        setSelectedButton(btnGraph6);
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        loadExchangeGraph(24,10*8,dateFormater);
        
    }//GEN-LAST:event_btnGraph6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGraph1;
    private javax.swing.JButton btnGraph2;
    private javax.swing.JButton btnGraph3;
    private javax.swing.JButton btnGraph4;
    private javax.swing.JButton btnGraph5;
    private javax.swing.JButton btnGraph6;
    private javax.swing.JPanel pnlExGraph;
    private javax.swing.JPanel pnlGraphInterval;
    // End of variables declaration//GEN-END:variables
}
