/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.PlainTabelCellRenderer;
import com.o3.bitcoin.ui.component.XScrollbarUI;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.Utils;
import com.o3.bitcoin.util.exchange.BTCMarketExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeService;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.service.polling.trade.PollingTradeService;
import org.knowm.xchange.dto.trade.UserTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class PnlOrderBook extends javax.swing.JPanel implements BasicExchangeScreen{

    private static final Logger logger = LoggerFactory.getLogger(PnlOrderBook.class);
    private ExchangeService exchangeService = null;
    private String currentExchange="";
    private static Timer orderbookTimer = null;
    private boolean isFirstTime = true;
    
    /**
     * Creates new form PnlOrderBook
     */
    public PnlOrderBook() {
        initComponents();
        lblOrderbookHead.setVisible(false);
        prepareUI();
    }
    
    private void prepareUI() {
        customizeTable(tblBuyOrders, scrollBuyOrders);
        customizeTable(tblSellOrders, scrollSellOrders);
        customizeTable(tblOpenOrders, scrollOpenOrders);
        tblOpenOrders.getColumn("Action").setCellRenderer(new ButtonRenderer());
        tblOpenOrders.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        customizeTable(tblTradingHistory, scrollTradingHistory);
    }
    
    private void customizeTable(JTable table, JScrollPane scrollPane) {
        table.getTableHeader().setFont(ResourcesProvider.Fonts.DEFAULT_HEADING_FONT);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBackground(ResourcesProvider.Colors.TABLE_HEADER_BG_COLOR);
        table.getTableHeader().setOpaque(true);

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
        table.setRowHeight(25);
        table.setDefaultRenderer(Object.class, new PlainTabelCellRenderer());
    }
    
    private void startOrderbookTimer() {
        if (orderbookTimer == null) {
            orderbookTimer = new Timer(7000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateData(); 
                }
            });
            orderbookTimer.start();
        }
        else{
            if( !orderbookTimer.isRunning() )
                orderbookTimer.start();
        }
    }
    
    public static void stopOrderbookTimer(){
        if(orderbookTimer != null){
            if(orderbookTimer.isRunning())
                orderbookTimer.stop();
        }
    }

    
    public void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                try {
                    final List<UserTrade> tradeHistory = getTradeHistory();
                    //final List<Trade> tradeHistory = getTradeHistory();
                    if( tradeHistory != null ) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                showTradeData(tradeHistory);
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("Exception while getting trade history: {}", e.getMessage());
                    /*java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            showTradeData(null);
                        }
                    });*/
                }
                
                try {
                    if(exchangeService.getExchangeName().equalsIgnoreCase("btcmarkets")) {
                        final String result = getBTCMarketsOpenOrders();
                        if( result != null && result != "") {
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    showBTCMarketsOpenOrders(result);
                                }
                            });
                        }
                    }
                    else {
                        final OpenOrders result = getOpenOrders();
                        if( result != null) {
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    showOpenOrders(result);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception while getting open orders: {}", e.getMessage());
                    /*java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            showTradeData(null);
                        }
                    });*/
                }
                
                try {
                    final List<BigDecimal[]> bids = getBuyOrders();
                    if( bids != null ) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                showBuyOrders(bids);
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("Exception while getting Buy Orders: {}", e.getMessage());
                    /*java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            showBuyOrders(null);
                        }
                    });*/
                }   

                try {
                    final List<BigDecimal[]> asks = getSellOrders();
                    if( asks != null ) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                showSellOrders(asks);
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("Exception while getting Sell Orders: {}", e.getMessage());
                    /*java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            showSellOrders(null);
                        }
                    });*/
                }
            }
        }).start();
    }
    
    public void setCurrentExchange(String exchangeName){
        currentExchange = exchangeName;
        exchangeService = ExchangeServiceFactory.getExchange(currentExchange);
        resetTables();
        updateData();
    }
    
    private void resetTables() {
        DefaultTableModel model = (DefaultTableModel) tblTradingHistory.getModel();
        model.setRowCount(0);
        DefaultTableModel model1 = (DefaultTableModel) tblBuyOrders.getModel();
        model1.setRowCount(0);
        DefaultTableModel model2 = (DefaultTableModel) tblSellOrders.getModel();
        model2.setRowCount(0);
        DefaultTableModel model3 = (DefaultTableModel) tblOpenOrders.getModel();
        model3.setRowCount(0);
    }
    
    public void loadExchangeData() {
          try {
              exchangeService = ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange());
              adjustTableColumnHeadings();
              if( isFirstTime )
              {
                loadBuyOrders();
                loadSellOrders();
                loadTradeHistory();
                loadOpenOrders();
                isFirstTime = false;
              }
              else
                  updateData();
          } catch (Exception ex) {
              logger.error("Exception while loading orderbook screen data: {}", ex.getMessage());
              System.out.println("Exception while loading orderbook screen data:"+ex.getMessage());
              //ApplicationUI.get().showError(ex);
          }
          startOrderbookTimer();
      }

    private void adjustTableColumnHeadings() {
       tblBuyOrders.getColumnModel().getColumn(0).setHeaderValue("Price("+exchangeService.getFiatCurrency().toUpperCase()+")");
       tblBuyOrders.getColumnModel().getColumn(1).setHeaderValue("Volume("+exchangeService.getAltcoinCurrency().toUpperCase()+")");
       tblBuyOrders.getColumnModel().getColumn(2).setHeaderValue("Value("+exchangeService.getFiatCurrency().toUpperCase()+")");
       tblBuyOrders.getTableHeader().repaint();
       
       tblSellOrders.getColumnModel().getColumn(0).setHeaderValue("Price("+exchangeService.getFiatCurrency().toUpperCase()+")");
       tblSellOrders.getColumnModel().getColumn(1).setHeaderValue("Volume("+exchangeService.getAltcoinCurrency().toUpperCase()+")");
       tblSellOrders.getColumnModel().getColumn(2).setHeaderValue("Value("+exchangeService.getFiatCurrency().toUpperCase()+")");
       tblSellOrders.getTableHeader().repaint();
       
       tblOpenOrders.getColumnModel().getColumn(2).setHeaderValue("Volume("+exchangeService.getAltcoinCurrency().toUpperCase()+")");
       tblOpenOrders.getColumnModel().getColumn(3).setHeaderValue("Price("+exchangeService.getFiatCurrency().toUpperCase()+")");
       
       tblOpenOrders.getTableHeader().repaint();
       
       tblTradingHistory.getColumnModel().getColumn(2).setHeaderValue("Volume("+exchangeService.getAltcoinCurrency().toUpperCase()+")");
       tblTradingHistory.getColumnModel().getColumn(3).setHeaderValue("Price("+exchangeService.getFiatCurrency().toUpperCase()+")");
       tblTradingHistory.getTableHeader().repaint();
       
       
       
    }
    
    private List<UserTrade> getTradeHistory() throws IOException {
        List<UserTrade> tradeHistory = exchangeService.getTradeHistory().getUserTrades();
        return tradeHistory;
    }
    
    /*private List<Trade> getTradeHistory() throws IOException {
        List<Trade> tradeHistory = exchangeService.getTradeHistory().getTrades();
        return tradeHistory;
    }*/
    
    private void showTradeData(List<UserTrade> tradeHistory) {
         int counter = 0;
        Date time;
        Order.OrderType orderType;
        BigDecimal tradePrice, tradeValue;
        DefaultTableModel model = (DefaultTableModel) tblTradingHistory.getModel();
        model.setRowCount(0);
        if(tradeHistory != null) {
            for (UserTrade trade : tradeHistory) {
                time = trade.getTimestamp();
                orderType = trade.getType();
                tradePrice = trade.getPrice();
                tradeValue = trade.getTradableAmount();
                model.addRow(new Object[]{
                    time,
                    orderType,
                    tradeValue,
                    tradePrice
                });
                counter++;
                if (counter == 50) {
                    break;
                }
            }
        }
    }
    
    /*private void showTradeData(List<Trade> tradeHistory) {
         int counter = 0;
        Date time;
        Order.OrderType orderType;
        BigDecimal tradePrice, tradeValue;
        DefaultTableModel model = (DefaultTableModel) tblTradingHistory.getModel();
        model.setRowCount(0);
        if(tradeHistory != null) {
            for (Trade trade : tradeHistory) {
                time = trade.getTimestamp();
                orderType = trade.getType();
                tradePrice = trade.getPrice();
                tradeValue = trade.getTradableAmount();
                model.addRow(new Object[]{
                    time,
                    orderType,
                    tradeValue,
                    tradePrice
                });
                counter++;
                if (counter == 50) {
                    break;
                }
            }
        }
    }*/
    
    private void loadTradeHistory() {
        try { 
            List<UserTrade> tradeHistory = getTradeHistory();
            //List<Trade> tradeHistory = getTradeHistory();
            if( tradeHistory != null )
                showTradeData(tradeHistory);
        }catch(Exception e) {
            logger.error("Exception while getting trade history: {}", e.getMessage());
            System.out.println("Exception while loadTradeHistory:"+e.getMessage());
            //ApplicationUI.get().showError(e);
        }
    }
    
    public synchronized String getBTCMarketsOpenOrders() throws Exception {
        PollingTradeService tradeService = exchangeService.getExchange().getPollingTradeService();
        String result = exchangeService.getOpenOrders(exchangeService.getFiatCurrency().toUpperCase() ,exchangeService.getAltcoinCurrency().toUpperCase(),50, 1);
        return result;
    }
    
    private void showBTCMarketsOpenOrders(String result) {
        int counter = 0;
        String orderId="";
        Date time = new Date();
        String status="";
        DefaultTableModel model = (DefaultTableModel) tblOpenOrders.getModel();
        model.setRowCount(0);
        String orderTypeBTC = "ordertype";
        String tradePriceBTC = "price";
        String volumeBTC = "volume";
        JSONObject json = new JSONObject(result);
        JSONArray array = json.getJSONArray("orders");
        for(int i = 0; i < array.length(); i++)
        {
            JSONObject jO = array.getJSONObject(i);
            long id = jO.getLong("id");
            time = new Date(jO.getLong("creationTime"));
            orderTypeBTC = jO.getString("ordertype");
            tradePriceBTC = ((Double)(jO.getLong("price")/100000000.0)).toString();
            status = jO.getString("status");
            orderId = ((Long)(jO.getLong("id"))).toString();
            volumeBTC = ((Double)(jO.getLong("volume")/100000000.0)).toString();           

            model.addRow(new Object[]{
                time,
                orderTypeBTC,
                volumeBTC,
                tradePriceBTC,
                status,
                orderId,
                "X"
            });
        }
       /* tblOpenOrders.getColumn("Action").setCellRenderer(new ButtonRenderer());
        tblOpenOrders.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));*/
    }
    
    private OpenOrders getOpenOrders() throws Exception {
        PollingTradeService tradeService = exchangeService.getExchange().getPollingTradeService();
        OpenOrders openOrders = tradeService.getOpenOrders();
        return openOrders;
    }
    
    private void showOpenOrders(OpenOrders openOrders) {
        int counter = 0;
        String orderId="";
        Date time = new Date();
        Order.OrderType orderType;
        String status="";
        List<LimitOrder> openOrdersList=null;
        BigDecimal tradePrice, tradeValue,volume;
        DefaultTableModel model = (DefaultTableModel) tblOpenOrders.getModel();
        model.setRowCount(0);
        if(!exchangeService.getExchangeName().equalsIgnoreCase("btcmarkets")) {
            //OpenOrders openOrders = tradeService.getOpenOrders();
            if(openOrders !=null  ){
                openOrdersList = openOrders.getOpenOrders();
            }

            for (LimitOrder openOrder : openOrdersList) {
                time = openOrder.getTimestamp();
                orderType = openOrder.getType();
                tradePrice = openOrder.getAveragePrice();
                status = openOrder.getStatus().toString();
                orderId = openOrder.getId();
                tradeValue = openOrder.getTradableAmount();
                volume = tradeValue.divide(tradePrice);           

                model.addRow(new Object[]{
                    time,
                    orderType,
                    volume,
                    tradePrice,
                    status,
                    orderId,
                    "X"
                });
                counter++;
                if (counter == 50) {
                    break;
                }
            }
        }
    }
    
    private void loadOpenOrders() throws Exception{
        int counter = 0;
        String orderId="";
        Date time = new Date();
        Order.OrderType orderType;
        String status="";
        List<LimitOrder> openOrdersList=null;
        BigDecimal tradePrice, tradeValue,volume;
        PollingTradeService tradeService = exchangeService.getExchange().getPollingTradeService();
        
        DefaultTableModel model = (DefaultTableModel) tblOpenOrders.getModel();
        model.setRowCount(0);
        if(!exchangeService.getExchangeName().equalsIgnoreCase("btcmarkets")) {
            OpenOrders openOrders = tradeService.getOpenOrders();
            if(openOrders !=null  ){
                openOrdersList = openOrders.getOpenOrders();
            }

            for (LimitOrder openOrder : openOrdersList) {
                time = openOrder.getTimestamp();
                orderType = openOrder.getType();
                tradePrice = openOrder.getAveragePrice();
                status = openOrder.getStatus().toString();
                orderId = openOrder.getId();
                tradeValue = openOrder.getTradableAmount();
                volume = tradeValue.divide(tradePrice);           

                model.addRow(new Object[]{
                    time,
                    orderType,
                    volume,
                    tradePrice,
                    status,
                    orderId,
                    "X"
                });
                counter++;
                if (counter == 50) {
                    break;
                }
            }
        }
        else {
            String orderTypeBTC = "ordertype";
            String tradePriceBTC = "price";
            String volumeBTC = "volume";
            String result = exchangeService.getOpenOrders(exchangeService.getFiatCurrency().toUpperCase() ,exchangeService.getAltcoinCurrency().toUpperCase(),50, 1);
            JSONObject json = new JSONObject(result);
            JSONArray array = json.getJSONArray("orders");
            for(int i = 0; i < array.length(); i++)
            {
                JSONObject jO = array.getJSONObject(i);
                long id = jO.getLong("id");
                time = new Date(jO.getLong("creationTime"));
                orderTypeBTC = jO.getString("ordertype");
                tradePriceBTC = ((Double)(jO.getLong("price")/100000000.0)).toString();
                status = jO.getString("status");
                orderId = ((Long)(jO.getLong("id"))).toString();
                volumeBTC = ((Double)(jO.getLong("volume")/100000000.0)).toString();           

                model.addRow(new Object[]{
                    time,
                    orderTypeBTC,
                    volumeBTC,
                    tradePriceBTC,
                    status,
                    orderId,
                    "X"
                });
            }
        }
         /*tblOpenOrders.getColumn("Action").setCellRenderer(new ButtonRenderer());
         tblOpenOrders.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));*/
    }

    private List<BigDecimal[]> getBuyOrders() throws Exception {
        List<BigDecimal[]> bids = exchangeService.getBidsRaw();
        return bids;
    } 
    
    private void showBuyOrders(List<BigDecimal[]> bids) {
        DefaultTableModel model = (DefaultTableModel) tblBuyOrders.getModel();
        model.setRowCount(0);
        int counter = 0;
        BigDecimal bidAmount, bidQuantity, bidValue;
        if( bids != null ) { 
            for (BigDecimal[] bid : bids) {
                bidAmount = bid[0];
                bidQuantity = bid[1];
                bidValue = bidQuantity.multiply(bidAmount);
                bidValue = bidValue.setScale(2, BigDecimal.ROUND_HALF_UP);
                model.addRow(new Object[]{
                    bidAmount,
                    bidQuantity,
                    bidValue
                });
                counter++;
                if (counter == 50) {
                    break;
                }
            }
        }
    }
    
    private void loadBuyOrders() throws IOException {
        try {
            List<BigDecimal[]> bids = getBuyOrders();
            showBuyOrders(bids);
        }catch(Exception e) {
            logger.error("Exception while getting Buy Orders: {}", e.getMessage());
            System.out.println("Exception while loading loadBuyOrders:"+e.getMessage());
            //ApplicationUI.get().showError(e);
        }
    }

    private List<BigDecimal[]> getSellOrders() throws Exception {
        List<BigDecimal[]> asks = exchangeService.getOrdersRaw();
        return asks;
    } 
    
    private void showSellOrders(List<BigDecimal[]> asks) {
        DefaultTableModel model = (DefaultTableModel) tblSellOrders.getModel();
        model.setRowCount(0);
        int counter = 0;
        BigDecimal sellAmount, sellQuantity, sellValue;
        if( asks != null ) {
            for (BigDecimal[] ask : asks) {
                sellAmount = ask[0];
                sellQuantity = ask[1];
                sellValue = sellQuantity.multiply(sellAmount);
                sellValue = sellValue.setScale(2, BigDecimal.ROUND_HALF_UP);
                model.addRow(new Object[]{
                    sellAmount,
                    sellQuantity,
                    sellValue
                });
                counter++;
                if (counter == 50) {
                    break;
                }
            }
        }
    }
    
    private void loadSellOrders() throws IOException {
        try {
            List<BigDecimal[]> asks = getSellOrders();
            showSellOrders(asks);
        }catch(Exception e) {
            logger.error("Exception while getting Sell Orders: {}", e.getMessage());
            System.out.println("Exception while loading loadSellOrders:"+e.getMessage());
            //ApplicationUI.get().showError(e);
        }
    }
    //method to cancel the order
    private boolean cancelOpenOrder(String orderId){
        boolean cancelResult = false;
        try {
            YesNoDialog dialog = new YesNoDialog("<html>This will cancel this Order.<br/>Do you want to Proceed?</html>");
            dialog.start();
            if (dialog.getSelectedOption() == YesNoDialog.OPTION_YES) {
                PollingTradeService tradeService = exchangeService.getExchange().getPollingTradeService();
                cancelResult = tradeService.cancelOrder(orderId);
                ApplicationUI.get().showMessage("Success", "Order canceled successfully");
                loadOpenOrders();
            }
        } catch (Exception ex) {
            logger.error("Exception while canceling order: {}", ex.getMessage());
            ApplicationUI.get().showError(ex);
        } 
        
        return cancelResult;
    }
  //Class for using button in JTable of OpenOrders  
    
      class ButtonRenderer extends JButton implements TableCellRenderer {

  public ButtonRenderer() {
    setOpaque(true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
    setText("X");
    Utils.themeSelectButton(this,Colors.NAV_MENU_DASHBOARD_COLOR);
    
    return this;
  }
}

/**
 * Renderer class for openorders
 */

class ButtonEditor extends DefaultCellEditor {
  protected JButton button;

  private String label;
  private String orderId;

  private boolean isPushed;
  private int row, col;
    private JTable table;

  public ButtonEditor(JCheckBox checkBox) {
    super(checkBox);
    button = new JButton();
    button.setOpaque(true);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
      }
    });
  }

  public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        this.col = column;
        orderId = (value == null) ? "" : table.getModel().getValueAt(row, column).toString();
        button.setText("X");
        isPushed = true;
        return button;
  }

  @Override
  public Object getCellEditorValue() {
      if (isPushed) {
       cancelOpenOrder((String)table.getValueAt(row, 5));
    }
    isPushed = false;
    return new String("");
  }

  public boolean stopCellEditing() {
    isPushed = false;
    return super.stopCellEditing();
  }

  protected void fireEditingStopped() {
      super.fireEditingStopped();
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

        lblOrderbookHead = new com.o3.bitcoin.ui.component.XScalableLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        scrollBuyOrders = new javax.swing.JScrollPane();
        tblBuyOrders = new javax.swing.JTable();
        xScalableLabel2 = new com.o3.bitcoin.ui.component.XScalableLabel();
        jPanel5 = new javax.swing.JPanel();
        scrollSellOrders = new javax.swing.JScrollPane();
        tblSellOrders = new javax.swing.JTable();
        xScalableLabel3 = new com.o3.bitcoin.ui.component.XScalableLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        scrollOpenOrders = new javax.swing.JScrollPane();
        tblOpenOrders = new javax.swing.JTable();
        xScalableLabel6 = new com.o3.bitcoin.ui.component.XScalableLabel();
        jPanel10 = new javax.swing.JPanel();
        scrollTradingHistory = new javax.swing.JScrollPane();
        tblTradingHistory = new javax.swing.JTable();
        xScalableLabel7 = new com.o3.bitcoin.ui.component.XScalableLabel();

        setLayout(new java.awt.BorderLayout());

        lblOrderbookHead.setText("Order Book");
        add(lblOrderbookHead, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        tblBuyOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Price (AUD)", "Volume (BTC)", "Value (AUD)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollBuyOrders.setViewportView(tblBuyOrders);

        jPanel4.add(scrollBuyOrders, java.awt.BorderLayout.CENTER);

        xScalableLabel2.setText("Buy Orders (Top 50)");
        xScalableLabel2.setFont(Fonts.EXCHANGE_STATS_MEDIUM_FONT);
        jPanel4.add(xScalableLabel2, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel2.add(jPanel4, gridBagConstraints);

        jPanel5.setLayout(new java.awt.BorderLayout());

        tblSellOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Price (AUD)", "Volume (BTC)", "Value (AUD)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollSellOrders.setViewportView(tblSellOrders);

        jPanel5.add(scrollSellOrders, java.awt.BorderLayout.CENTER);

        xScalableLabel3.setText("Sell Orders (Top 50)");
        xScalableLabel3.setFont(Fonts.EXCHANGE_STATS_MEDIUM_FONT);
        jPanel5.add(xScalableLabel3, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.BorderLayout());

        tblOpenOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Type", "Volume (BTC)", "Price (AUD)", "Status", "Order Id", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollOpenOrders.setViewportView(tblOpenOrders);

        jPanel9.add(scrollOpenOrders, java.awt.BorderLayout.CENTER);

        xScalableLabel6.setText("My Open Orders");
        xScalableLabel6.setFont(Fonts.EXCHANGE_STATS_MEDIUM_FONT);
        jPanel9.add(xScalableLabel6, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel6.add(jPanel9, gridBagConstraints);

        jPanel10.setLayout(new java.awt.BorderLayout());

        tblTradingHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Type", "Volume (BTC)", "Price (AUD)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollTradingHistory.setViewportView(tblTradingHistory);

        jPanel10.add(scrollTradingHistory, java.awt.BorderLayout.CENTER);

        xScalableLabel7.setText("My Trading History");
        xScalableLabel7.setFont(Fonts.EXCHANGE_STATS_MEDIUM_FONT);
        jPanel10.add(xScalableLabel7, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        jPanel1.add(jPanel6, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private com.o3.bitcoin.ui.component.XScalableLabel lblOrderbookHead;
    private javax.swing.JScrollPane scrollBuyOrders;
    private javax.swing.JScrollPane scrollOpenOrders;
    private javax.swing.JScrollPane scrollSellOrders;
    private javax.swing.JScrollPane scrollTradingHistory;
    private javax.swing.JTable tblBuyOrders;
    private javax.swing.JTable tblOpenOrders;
    private javax.swing.JTable tblSellOrders;
    private javax.swing.JTable tblTradingHistory;
    private com.o3.bitcoin.ui.component.XScalableLabel xScalableLabel2;
    private com.o3.bitcoin.ui.component.XScalableLabel xScalableLabel3;
    private com.o3.bitcoin.ui.component.XScalableLabel xScalableLabel6;
    private com.o3.bitcoin.ui.component.XScalableLabel xScalableLabel7;
    // End of variables declaration//GEN-END:variables
}
