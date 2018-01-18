/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.dashboard;

import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.component.PlainTabelCellRenderer;
import com.o3.bitcoin.ui.component.PlainTableHeaderRenderer;
import com.o3.bitcoin.ui.component.XScrollbarUI;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.applications.PnlShapshiftIOExchangeDividerScreen;
import com.o3.bitcoin.ui.dialogs.screens.PnlNewPaymentScreen;
import com.o3.bitcoin.ui.screens.exchange.PnlExchangeScreen;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.BitcoinCurrencyRateApi;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.Utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.utils.MonetaryFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * Class that implements dashboard screen of ui
 */
public class PnlDashboardScreen extends javax.swing.JPanel implements BasicScreen 
{
    
    private final String statusCompleted = "COMPLETE";
    private final String statusPending = "PENDING";
    private final String statusConfirmed = "CONFIRMED";
    private String status = "";
    private final int columnStatusNumber = 8; 

    public static String test = "";
    
    private static final Logger logger = LoggerFactory.getLogger(PnlDashboardStats.class);

    
    
 
    /**
     * Creates new form PnlDashboardScreen
     */
    public PnlDashboardScreen() {
        initComponents();
        prepareUI();
        startTimer();
    }

    @Override
    public void loadData() {
        PnlShapshiftIOExchangeDividerScreen.stopMarketInfoTimer();
        PnlExchangeScreen.stopTimers();
    }

    /**
     * function that creates History table
     */
    private void prepareUI() {
        for (int i = 0; i < 9; i++) {
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
        tblTransactions.getTableHeader().setBackground(Colors.TABLE_HEADER_BG_COLOR);
        tblTransactions.getTableHeader().setOpaque(true);
        

        ////////////////////////////////////////////////////////////////////////
        // To set the font size and style of the contents of table
        tblTransactions.setFont(new Font("Tahoma", Font.PLAIN, 11));
            

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
        tblTransactions.setRowHeight(25);
        tblTransactions.setDefaultRenderer(Object.class, new PlainTabelCellRenderer());
    }

    /**
     * start a timer that update dashboard
     */
    private void startTimer() {
        Timer timer = new Timer(5000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<WalletService> services = WalletManager.get().getAllWalletServices();
                if (services == null || services.isEmpty()) {
                    reset();
                } else {
                    updateWalletsSummary(services);
                }
            }
        });
        timer.start();
    }

    /**
     * function to reset stats data
     */
    private void reset() {
        pnlDashboardStats.reset();
    }

    /**
     * function that updates debit/credit/balance/price in fiat and Transaction History table 
     * @param services 
     */
    private void updateWalletsSummary(List<WalletService> services) {
        Coin totalDebit = Coin.ZERO;
        Coin totalCredit = Coin.ZERO;
        Coin totalBalance = Coin.ZERO;
        double priceInFiat = 0.00d;
        String confidence = "";
        // update debit/credit/balance and price in fiat 
        List<TransactionWrapper> transactions = new ArrayList<>();
        for (WalletService service : services) {
            try {
                Wallet wallet = service.getWallet();
                totalBalance = totalBalance.add(wallet.getBalance());
                for (Transaction trx : wallet.getTransactionsByTime()) {
                    if( trx.getConfidence().equals(TransactionConfidence.ConfidenceType.DEAD) )
                        continue;
                    Coin amount = trx.getValue(wallet);
                    if (amount.isPositive()) {
                        totalCredit = totalCredit.add(amount);
                    } else {
                        totalDebit = totalDebit.add(amount);
                    }
                    transactions.add(new TransactionWrapper(trx, wallet, amount));
                }
            } catch (Exception e) {
                logger.error("Unable to update wallet details");
            }
        }
        pnlDashboardStats.setTotalBalance(MonetaryFormat.BTC.noCode().format(totalBalance).toString());
        
        /*******************FARIHA SHAIKH********************************/
        test = "" + MonetaryFormat.BTC.noCode().format(totalBalance).toString();
        

        //pnlDashboardStats.setTotalDebit(MonetaryFormat.BTC.noCode().format(totalDebit).toString());
        //pnlDashboardStats.setTotalCredit(MonetaryFormat.BTC.noCode().format(totalCredit).toString());
        priceInFiat = Double.valueOf(MonetaryFormat.BTC.noCode().format(totalBalance).toString());
        priceInFiat *= BitcoinCurrencyRateApi.get().getCurrentRateValue();
        pnlDashboardStats.setPriceInFiat(String.format("%.2f", priceInFiat), "", ConfigManager.config().getSelectedCurrency());
        pnlDashboardStats.setExchangeRate(ConfigManager.config().getSelectedCurrency(), String.format("%.2f",BitcoinCurrencyRateApi.get().getCurrentRateValue()));
        Collections.sort(transactions, new Comparator<TransactionWrapper>() {
            @Override
            public int compare(TransactionWrapper o1, TransactionWrapper o2) {
                return o2.getTransaction().getUpdateTime().compareTo(o1.getTransaction().getUpdateTime());
            }
        });
        // update Transaction History table
        DefaultTableModel model = (DefaultTableModel) tblTransactions.getModel();
        model.setRowCount(0);
        for (TransactionWrapper wrapper : transactions) {
            Transaction transaction = wrapper.getTransaction();
            if( transaction.getConfidence().getDepthInBlocks() > 6 )
                confidence = "<html>6<sup>+</sup></html>";
            else
                confidence = transaction.getConfidence().getDepthInBlocks() + "";
            Coin amount = wrapper.getAmount();
            Coin fee = transaction.getFee();
            String amountString = MonetaryFormat.BTC.noCode().format(amount).toString();
            String feeString = fee != null ? MonetaryFormat.BTC.noCode().format(fee).toString() : "0.00";
            Address from = transaction.getInput(0).getFromAddress();
            Address to = transaction.getOutput(0).getAddressFromP2PKHScript(wrapper.getWallet().getNetworkParameters());
            boolean credit = amount.isPositive();
            
            if (transaction.getConfidence().getDepthInBlocks() < 1)
            {
                status = statusPending;
            }
            else if (transaction.getConfidence().getDepthInBlocks() >= 1 && transaction.getConfidence().getDepthInBlocks() <= 2)
            {
                status = statusConfirmed;
            }
            else
            {
                status = statusCompleted;
            }
            
                        ///////////////////////////////////////////////////////////////////
            // To place the text of table at the center
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);//(JLabel.CENTER);
            for(int x=0;x<model.getColumnCount();x++){
            tblTransactions.getColumnModel().getColumn(x).setCellRenderer( rightRenderer );
        }
            
            model.addRow(new Object[]{
                Utils.formatTransactionDate(transaction.getUpdateTime()),
                from,
                to,
                credit ? "Credit" : "Debit",
                amountString,
                feeString,
                "",
                confidence,
                status,
            });
        }
        
        /////////////////////////////////////////////////////////////////////// 
        // to set the color of column 8 (Status) as green
        Color color = null;
        TableColumn tm = tblTransactions.getColumnModel().getColumn(columnStatusNumber);
        tm.setCellRenderer((TableCellRenderer) columnCellRenderer(color));
            
        Coin balanceAfter = Coin.ZERO;
        for (int index = transactions.size() - 1; index >= 0; index--) {
            balanceAfter = balanceAfter.add(Coin.parseCoin((String) model.getValueAt(index, 4)));
            model.setValueAt(MonetaryFormat.BTC.noCode().format(balanceAfter).toString(), index, 6);
        }
    }
    
    public Component columnCellRenderer(final Color c)
    {
        tblTransactions.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
            {
                //@Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column)
                {
                    Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                    if (column == columnStatusNumber)
                    {
                        Object columnValue=table.getValueAt(row,columnStatusNumber);
                        if (columnValue.equals(statusCompleted))
                        {
                            setBackground(java.awt.Color.decode("#21C86D"));//"#FFA500"));                        
                            setForeground(java.awt.Color.WHITE);
                            setHorizontalAlignment(SwingConstants.CENTER);                        
                        }
                        else if (columnValue.equals(statusConfirmed))
                        {
                            setBackground(java.awt.Color.decode("#8DF158"));                        
                            setForeground(java.awt.Color.WHITE);
                            setHorizontalAlignment(SwingConstants.CENTER);                        
                        }
                        else if (columnValue.equals(statusPending))
                        {
                            setBackground(java.awt.Color.decode("#FFA500"));
                            setForeground(java.awt.Color.WHITE);
                            setHorizontalAlignment(SwingConstants.CENTER);
                        }
                        
                        return cell;
                    }
                    return cell;
                }                
            });
        return null;
    }
    
    /**
     * function to get reference to dashboard graph
     * @return PnlDashboardGraphs
     */

        public PnlDashboardGraphs getDashboardGraph() {
        return pnlDashboardGraphs1;
    }

    /**
     * Wrapper class for wallet transaction
     */
    private class TransactionWrapper {

        private Transaction transaction;
        private Wallet wallet;
        private Coin amount;

        public TransactionWrapper(Transaction transaction, Wallet wallet, Coin amount) {
            this.transaction = transaction;
            this.wallet = wallet;
            this.amount = amount;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public Wallet getWallet() {
            return wallet;
        }

        public Coin getAmount() {
            return amount;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.transaction);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TransactionWrapper other = (TransactionWrapper) obj;
            if (!Objects.equals(this.transaction, other.transaction)) {
                return false;
            }
            return true;
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

        pnlMain = new javax.swing.JPanel();
        pnlGraphs = new javax.swing.JPanel();
        pnlDashboardStats = new com.o3.bitcoin.ui.screens.dashboard.PnlDashboardStats();
        pnlDashboardGraphs1 = new com.o3.bitcoin.ui.screens.dashboard.PnlDashboardGraphs();
        pnlTransactions = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tblTransactions = new javax.swing.JTable();
        lblTransactionsHistory = new javax.swing.JLabel();
        pnlTop = new javax.swing.JPanel();
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlTopEdge = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlMain.setOpaque(false);
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlGraphs.setOpaque(false);
        pnlGraphs.setLayout(new java.awt.BorderLayout());
        pnlGraphs.add(pnlDashboardStats, java.awt.BorderLayout.PAGE_END);
        pnlGraphs.add(pnlDashboardGraphs1, java.awt.BorderLayout.CENTER);

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
                "Date", "From", "To", "Credit/Debit", "Amount", "Fee", "Total Balance", "Confidence", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(Fonts.BOLD_SMALL_FONT);
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/o3_16x16.png"))); // NOI18N
        lblTitle.setText("DASHBOARD");
        lblTitle.setToolTipText("");
        lblTitle.setIconTextGap(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(lblTitle, gridBagConstraints);

        pnlTop.add(pnlTitle, java.awt.BorderLayout.EAST);

        pnlTopEdge.setBackground(Colors.NAV_MENU_DASHBOARD_COLOR);
        pnlTopEdge.setPreferredSize(new java.awt.Dimension(1024, 5));
        pnlTop.add(pnlTopEdge, java.awt.BorderLayout.NORTH);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTransactionsHistory;
    private com.o3.bitcoin.ui.screens.dashboard.PnlDashboardGraphs pnlDashboardGraphs1;
    private com.o3.bitcoin.ui.screens.dashboard.PnlDashboardStats pnlDashboardStats;
    private javax.swing.JPanel pnlGraphs;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopEdge;
    private javax.swing.JPanel pnlTransactions;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblTransactions;
    // End of variables declaration//GEN-END:variables
}
