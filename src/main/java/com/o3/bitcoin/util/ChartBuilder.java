/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.hdwallet.util.WalletUtil;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.model.currency.BitcoinCurrencyRateHistory;
import com.o3.bitcoin.model.currency.Rate;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.util.exchange.ExchangeServiceFactory;
import com.o3.bitcoin.util.exchange.GraphDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.script.Script;
import org.bitcoinj.utils.MonetaryFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that creates different charts shown in application 
 */
public class ChartBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ChartBuilder.class);
    private static ChartBuilder builder;

    private static final List<Color> walletColors
            = new ArrayList<>(Arrays.asList(new Color[]{
                ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR,
                ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR,
                ResourcesProvider.Colors.NAV_MENU_CONTACTS_COLOR,
                ResourcesProvider.Colors.NAV_MENU_SETTINGS_COLOR,
                ResourcesProvider.Colors.NAV_MENU_APPLICATIONS_COLOR,
                ResourcesProvider.Colors.NAV_MENU_ABOUT_COLOR
            }));

    private ChartBuilder() {

    }

    public static ChartBuilder get() {
        if (builder == null) {
            builder = new ChartBuilder();
        }
        return builder;
    }

    /**
     * function that creates bitcoin to currency conversion chart on Dashboard page
     * @return chart
     * @throws IOException 
     */
    public JFreeChart getCurrencyConversionRateChart() throws IOException {

        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        String currency = ConfigManager.config().getSelectedCurrency();
        String heading = currency;

        BitcoinCurrencyRateHistory history = BitcoinCurrencyRateApi.get().getCurrentRateHistoryData(currency);
        history.sort();

        JFreeChart chart;

        for (Rate rate : history.getRateData()) {
            dataSet.addValue(rate.getValue(), heading, rate.getDay().toUpperCase());
        }

        chart = ChartFactory.createLineChart(
                null,
                null,
                null,
                dataSet,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        chart.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        chart.setBorderPaint(ResourcesProvider.Colors.APP_BG_COLOR);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.setOutlineVisible(false);
        plot.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        plot.setDomainGridlinePaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        double percent = 0.15;
        double diff = (history.getMaximumValue()-history.getMinimumValue());
        double margin = diff * percent;
        rangeAxis.setRange(history.getMinimumValue() - margin, history.getMaximumValue() + margin);
        rangeAxis.setTickUnit(new NumberTickUnit((history.getMaximumValue()-history.getMinimumValue())/5));
        rangeAxis.setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        rangeAxis.setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        plot.getDomainAxis().setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        plot.getDomainAxis().setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesShapesVisible(0, Boolean.TRUE);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesPaint(0, ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        renderer.setSeriesFillPaint(0, Color.WHITE);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setSeriesStroke(0, new BasicStroke(3));
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{2} " + currency, new DecimalFormat("##.####")));

        return chart;
    }

    /**
     * function that creates Account balance ring graph on Accounts page
     * @return chart
     */
    public JFreeChart getWalletBalancesChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        /*for (WalletConfig config : ConfigManager.get().getAllWallets()) {
            Double balance = getWalletBalance(config);
            dataset.setValue(config.getId(), balance);
        }*/
        WalletConfig config = ConfigManager.get().getFirstWallet();
        if( config == null )
            return null;
        WalletService service = WalletManager.get().getWalletService(config.getId());
        if( service == null )
            return null;
        List<HDAccount> acctList = service.getAllAccounts();
        if( acctList == null )
            return null;
        for( HDAccount acct : acctList ){
            String strBalance = WalletUtil.satoshiToBTC(acct.balance()).toPlainString();
            Double balance = Double.parseDouble(strBalance);
            dataset.setValue(strBalance, balance);
        }
        
       
        JFreeChart chart;
        chart = ChartFactory.createRingChart(
                null,
                dataset,
                false,
                false,
                false);
        chart.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);

        RingPlot plot = (RingPlot) chart.getPlot();

        plot.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        plot.setOutlineVisible(false);
        plot.setCircular(true);
        plot.setLabelLinksVisible(false);
        plot.setLabelGenerator(null);
        plot.setSeparatorsVisible(false);
        if( service.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).isGreaterThan(Coin.ZERO))
            plot.setToolTipGenerator(new StandardPieToolTipGenerator("{1} BTC"));
        plot.setShadowPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        plot.setSectionOutlinesVisible(false);
        /*List<WalletConfig> walletConfigList = ConfigManager.get().getAllWallets();
        for (int i = 0; i < dataset.getItemCount(); i++) {
            plot.setSectionPaint(walletConfigList.get(i).getId(), walletColors.get(i));
            plot.setSectionDepth(0.4);
        }*/
        
        if( service.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).isGreaterThan(Coin.ZERO))
        {
            int i = 0;
            for( HDAccount acct : acctList ){
                String strBalance = WalletUtil.satoshiToBTC(acct.balance()).toPlainString();
                Double balance = Double.parseDouble(strBalance);
                dataset.setValue(strBalance, balance);
                plot.setSectionPaint(strBalance, walletColors.get(i));
                plot.setSectionDepth(0.4);
                i++;
            }
        }
        else {
            String strBalance = "0.1";
            Double balance = 0.1;
            dataset.setValue(strBalance, balance);
            plot.setSectionPaint(strBalance, walletColors.get(0));
            plot.setSectionDepth(0.4);
        }
            

        return chart;
    }

    private Double getWalletBalance(WalletConfig config) {
        try {
            WalletService service = WalletManager.get().getWalletService(config.getId());
            if (service != null) {
                Coin balance = service.getWallet().getBalance();
                return Double.valueOf(MonetaryFormat.BTC.noCode().format(balance).toString());
            } else {
                logger.debug("No WalletService found for ID: " + config.getId());
            }
        } catch (Exception e) {
            logger.error("Unable to get wallet balance : " + e);
        }
        return 0d;
    }

    /**
     * function that creates Account debit/create bar chart on Accounts page
     * @param config wallet
     * @param days number of days for which to create graph
     * @param type debit or credit or debit and credit
     * @return chart
     */
    public TransactionsChart getWalletTransactionsChart(WalletConfig config, int days, TransactionsChart.Type type) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        double totalDebit = 0d;
        double totalCredit = 0d;
        
        if(type == TransactionsChart.Type.BOTH) {
            totalDebit = prepareTransactionsDataSet(config, days, true, dataset);
            totalCredit = prepareTransactionsDataSet(config, days, false, dataset);
        } else {
            if(type == TransactionsChart.Type.DEBIT) {
                totalDebit = prepareTransactionsDataSet(config, days, true, dataset);
            } else {
                totalCredit = prepareTransactionsDataSet(config, days, false, dataset);
            }
        }

        JFreeChart chart;
        chart = ChartFactory.createBarChart(
                null,
                null,
                null,
                dataset,
                PlotOrientation.VERTICAL,
                false, true,
                false);

        chart.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        chart.setBorderVisible(false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.setBackgroundPaint(ResourcesProvider.Colors.TABLE_EVEN_ODD_BG_COLOR);
        plot.getDomainAxis().setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        plot.getDomainAxis().setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        rangeAxis.setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        if(type == TransactionsChart.Type.BOTH) {
            renderer.setSeriesPaint(0, ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
            renderer.setSeriesPaint(1, ResourcesProvider.Colors.NAV_MENU_CONTACTS_COLOR);
        } else {
            if(type == TransactionsChart.Type.DEBIT) {
                renderer.setSeriesPaint(0, ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
            } else {
                renderer.setSeriesPaint(0, ResourcesProvider.Colors.NAV_MENU_CONTACTS_COLOR);
            }
        }
        plot.setOutlineVisible(false);
        plot.getRenderer().setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{2} BTC", new DecimalFormat("#.####")));

        return new TransactionsChart(chart, totalDebit, totalCredit, type);
    }
    
    
    /**
     * function that creates Account debit/create bar chart on Accounts page
     * @param config wallet
     * @param days number of days for which to create graph
     * @param type debit or credit or debit and credit
     * @return chart
     */
    
    public JFreeChart getExchangeChart( List<GraphDTO> dataList, SimpleDateFormat dateFormatter ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        prepareExchangeDataset(dataList, dataset,dateFormatter);
        JFreeChart chart = ChartFactory.createLineChart(
                null,
                null,
                null,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        chart.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        chart.setBorderPaint(ResourcesProvider.Colors.APP_BG_COLOR);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.setOutlineVisible(false);
        plot.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        plot.setDomainGridlinePaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        if(dataList.size() > 0) {
            MinMax minMaxPrice = getMinMaxExchangePrice(dataList); 
            double minValue = minMaxPrice.getMinValue();
            double maxValue = minMaxPrice.getMaxValue();
            double percent = 0.15;
            double diff = maxValue - minValue;
            double margin = diff * percent;
            if(minValue == maxValue) {
                rangeAxis.setRange(minValue-2,minValue+3);
                rangeAxis.setTickUnit(new NumberTickUnit(1));
            }
            else {
                rangeAxis.setRange(minValue - margin, maxValue + margin);
                rangeAxis.setTickUnit(new NumberTickUnit((maxValue-minValue)/5));
            }
        }
        else {
            //rangeAxis.setRange(0,5);
            //rangeAxis.setTickUnit(new NumberTickUnit(5/5));
        }
        
        
        rangeAxis.setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        rangeAxis.setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        plot.getDomainAxis().setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        plot.getDomainAxis().setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesShapesVisible(0, Boolean.TRUE);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesPaint(0, ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        renderer.setSeriesFillPaint(0, Color.WHITE);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setSeriesStroke(0, new BasicStroke(3));
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{2} " + ExchangeServiceFactory.getExchange(ApplicationUI.get().getExchangeScreen().getSelectedExchange()).getFiatCurrency().toUpperCase(), new DecimalFormat("##.####")));

        return chart;
    }
    
    private MinMax getMinMaxExchangePrice(List<GraphDTO> dataList) {
        BigDecimal min = new BigDecimal(9999999.00);
        BigDecimal max = BigDecimal.ZERO;
        for( int i = 0; i < dataList.size(); i++) {
            GraphDTO data = dataList.get(i);
            if( data.getPrice().compareTo(max) == 1 )// greater
                max = data.getPrice();
            if( data.getPrice().compareTo(min) == -1 ) // less
                min = data.getPrice();
        }
        MinMax minMaxValues = new MinMax();
        minMaxValues.setMaxValue(max.doubleValue());
        minMaxValues.setMinValue(min.doubleValue());
        return minMaxValues;
    }
    
    /*public JFreeChart getExchangeChart( List<GraphDTO> dataList, SimpleDateFormat dateFormatter ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        prepareExchangeDataset(dataList, dataset,dateFormatter);
        JFreeChart chart;
        chart = ChartFactory.createBarChart(
                null,
                null,
                null,
                dataset,
                PlotOrientation.VERTICAL,
                false, true,
                false);
        chart.setBackgroundPaint(ResourcesProvider.Colors.APP_BG_COLOR);
        chart.setBorderVisible(false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(ResourcesProvider.Colors.TABLE_EVEN_ODD_BG_COLOR);
        plot.getDomainAxis().setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        plot.getDomainAxis().setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickLabelFont(ResourcesProvider.Fonts.BOLD_SMALL_FONT);
        rangeAxis.setTickLabelPaint(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        plot.setOutlineVisible(false);
        plot.getRenderer().setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{2}", new DecimalFormat("#.####")));
        return chart;
    }*/
    
    
    public void prepareExchangeDataset(List<GraphDTO> dataList, DefaultCategoryDataset dataset,SimpleDateFormat dateFormatter) {
        for( int i = 0; i < dataList.size(); i++) {
            GraphDTO data = dataList.get(i);
            dataset.addValue(data.getPrice(), "Ex",dateFormatter.format(data.getTime().getTime()));
        }
    }
    
    
    /**
     * function that provides data set for transaction chart
     * @param config wallet
     * @param days number days for which to provide dataset
     * @param isDebit debit/credit
     * @param dataset dataset for chart
     * @return total of all transactions
     */
    private Double prepareTransactionsDataSet(WalletConfig config, int days, Boolean isDebit, DefaultCategoryDataset dataset) {
        Map<Date, Double> totals = getWalletTransactions(config, days, isDebit);
        Double totalValue = 0.0d;
        Iterator<Date> iterator = totals.keySet().iterator();
        while (iterator.hasNext()) {
            Date date = iterator.next();
            String dateString = new SimpleDateFormat("dd/MM").format(date);
            Double value = totals.get(date);
            totalValue += value;
            dataset.addValue(value, isDebit ? "Dr" : "Cr", dateString);
        }
        return totalValue;
    }

    /**
     * function that provides map of transaction value with date
     * @param config wallet
     * @param days number days for which to provide transactions
     * @param isDebit debit/credit
     * @return map of transaction values
     */
    private Map<Date, Double> getWalletTransactions(WalletConfig config, int days, boolean isDebit) {
        Map<Date, Double> data = new TreeMap<>();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -days);

        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        for (int i = 1; i <= 15; i++) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(start.getTimeInMillis());
            date.add(Calendar.DAY_OF_MONTH, i);
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            data.put(date.getTime(), 0.0d);
        }
        try {
            if (config == null) {
                return data;
            }
            WalletService service = WalletManager.get().getWalletService(config.getId());
            if (service != null) {
                Wallet wallet = service.getWallet();
                HDAccount currentAccount = service.getCurrentAccount();
                List<Transaction> transactionList = WalletUtil.getAccountTransactions(wallet, currentAccount);
                for (Transaction trx : transactionList) {
                    Date trxDate = trx.getUpdateTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(trxDate);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    trxDate = cal.getTime();
                    if (trxDate.equals(start.getTime()) || trxDate.after(start.getTime())) {
                        Coin amount = trx.getValue(wallet);
                        Coin fee = trx.getFee();

                        if( fee != null ) {
                            if( (amount.getValue()*-1) == fee.getValue() ) { // transaction is from one account to another account in same wallet
                                long outputValue = 0;
                                long connectedOutputValue = 0;
                                boolean connectedFound = false;
                                List<TransactionOutput> lto = trx.getOutputs();
                                for (TransactionOutput txo : lto) {
                                    long value = txo.getValue().longValue();// coin value
                                    try {
                                        byte[] pubkey = null;
                                        byte[] pubkeyhash = null;
                                        Script script = txo.getScriptPubKey();
                                        if (script.isSentToRawPubKey())
                                            pubkey = script.getPubKey();
                                        else
                                            pubkeyhash = script.getPubKeyHash();
                                        if( currentAccount.hasPubKey(pubkey, pubkeyhash) )
                                        {
                                            outputValue += value;
                                         }
                                    } catch (ScriptException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                                // Traverse the HDAccounts with all inputs.
                                List<TransactionInput> lti = trx.getInputs();
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
                                        if( currentAccount.hasPubKey(pubkey, null) )
                                        {
                                            connectedFound = true;
                                            connectedOutputValue += value;
                                        }
                                        //for (HDAccount hda : mAccounts)
                                          //  hda.applyInput(pubkey, value);
                                    } catch (ScriptException e) {
                                        // This happens if the input doesn't have a
                                        // public key (eg P2SH).  No worries in this
                                        // case, it isn't one of ours ...
                                    }
                                }

                                amount = Coin.valueOf(outputValue-connectedOutputValue);       
                            }
                        }   
                        Double doubleAmount = Double.valueOf(MonetaryFormat.BTC.noCode().format(amount).toString());
                        if ((amount.isNegative() && isDebit) || (amount.isPositive() && !isDebit)) {
                            data.put(trxDate, data.get(trxDate) + Math.abs(doubleAmount));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve wallet transactions" + e);
        }
        return data;
    }
}
