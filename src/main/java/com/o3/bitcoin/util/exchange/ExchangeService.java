/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import com.o3.bitcoin.ui.ApplicationUI;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.btcmarkets.BTCMarketsExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.btcmarkets.dto.marketdata.BTCMarketsOrderBook;
import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsMarketDataServiceRaw;
import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsTradeService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.independentreserve.IndependentReserveExchange;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;
import org.knowm.xchange.service.polling.trade.PollingTradeService;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.service.polling.account.PollingAccountService;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.bitstamp.service.polling.BitstampMarketDataServiceRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsMarketDataServiceRaw;

/**
 *
 * @author
 */
public class ExchangeService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeService.class);
    protected Exchange exchange = null;
    private String exchangeName = "";
    protected String apiKey = "";
    protected String apiSecret = "";
    private String customerID = "";
    protected String currencyPair = "";
    
    public ExchangeService() {
        
    }
    public ExchangeService(String exchangeName, String apiKey, String apiSecret) {
        this.exchangeName = exchangeName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        createExchange(exchangeName,apiKey,apiSecret,"");
    }
    
    public ExchangeService(String exchangeName, String apiKey, String apiSecret, String customerID) {
        this.exchangeName = exchangeName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.customerID = customerID;
        createExchange(exchangeName,apiKey,apiSecret, customerID);
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
    
    public String getExchangeName() {
        return exchangeName;
    }
    
    public void getAccountInfo() throws IOException {
        PollingAccountService accountService = exchange.getPollingAccountService();
        // Get the account information
        AccountInfo accountInfo = accountService.getAccountInfo();
    }
    
    private void createExchange(String name, String apiKey, String apiSecret, String customerID) {
        ExchangeSpecification spec = null;
        switch (name) {
            case "btcmarkets":
                exchange = ExchangeFactory.INSTANCE.createExchange(BTCMarketsExchange.class.getName());
                currencyPair = "BTC/AUD";    
                spec =  exchange.getExchangeSpecification();
                spec.getExchangeSpecificParameters().put(BTCMarketsExchange.CURRENCY_PAIR, CurrencyPair.BTC_AUD);

                spec.setApiKey(apiKey);
                spec.setSecretKey(apiSecret);
                exchange.applySpecification(spec);
                break;
            case "btc-e":
                try {
                    ExchangeSpecification exSpec = new ExchangeSpecification(BTCEExchange.class);
                    exSpec.setSecretKey(apiSecret);
                    exSpec.setApiKey(apiKey);
                    currencyPair = "BTC/USD";
                    exSpec.setSslUri("https://btc-e.com");
                    exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
                    exchange.remoteInit();
                }catch(Exception e) {
                    System.out.println("BTC-E creation exception ="+e.getMessage());
                }
                break;
            case "independentreserve":
                ExchangeSpecification exSpec = new IndependentReserveExchange().getDefaultExchangeSpecification();
                exSpec.setApiKey(apiKey);
                exSpec.setSecretKey(apiSecret);
                currencyPair = "XBT/AUD";
                exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
                break;
            case "bitstamp":
                ExchangeSpecification btSpec = new BitstampExchange().getDefaultExchangeSpecification();
                btSpec.setUserName(customerID);
                btSpec.setApiKey(apiKey);
                btSpec.setSecretKey(apiSecret);
                currencyPair = "BTC/USD";
                exchange = ExchangeFactory.INSTANCE.createExchange(btSpec);
                break;
            default:
                break;
        }
    }
    
    public Exchange getExchange() {
        return exchange;
    }
   
    public void setCurrencyPair(String exchangeName, String currencyPair) {
     
        switch (exchangeName) {
            case "btcmarkets":
                ExchangeSpecification spec =  this.exchange.getExchangeSpecification();
                this.currencyPair = currencyPair;
                spec.getExchangeSpecificParameters().put(BTCMarketsExchange.CURRENCY_PAIR,new CurrencyPair(currencyPair) );
                this.exchange.applySpecification(spec);
                //set current currency pair selected
                ExchangeServiceFactory.currentCurrency = currencyPair;
                break;
            case "btc-e":
                ExchangeSpecification spec1 =  this.exchange.getExchangeSpecification();
                this.currencyPair = currencyPair;
                spec1.getExchangeSpecificParameters().put(BTCMarketsExchange.CURRENCY_PAIR,new CurrencyPair(currencyPair) );
                this.exchange.applySpecification(spec1);
                //set current currency pair selected
                ExchangeServiceFactory.currentCurrency = currencyPair;
                 break;
            case "independentreserve":
                ExchangeSpecification spec2 =  exchange.getExchangeSpecification();
                this.currencyPair = currencyPair;
                spec2.getExchangeSpecificParameters().put(BTCMarketsExchange.CURRENCY_PAIR,new CurrencyPair(currencyPair) );
                this.exchange.applySpecification(spec2);
                //set current currency pair selected
                ExchangeServiceFactory.currentCurrency = currencyPair;
             break;
             case "bitstamp":
                ExchangeSpecification spec3 =  exchange.getExchangeSpecification();
                this.currencyPair = currencyPair;
                spec3.getExchangeSpecificParameters().put(BTCMarketsExchange.CURRENCY_PAIR,new CurrencyPair(currencyPair) );
                this.exchange.applySpecification(spec3);
                //set current currency pair selected
                ExchangeServiceFactory.currentCurrency = currencyPair;
             break;       
            default:
                break;
        }
        
    }
    
    public String getFiatCurrency() {
        String[] separatePair = currencyPair.split("/");
        return separatePair[1];
    }
    
    public String getAltcoinCurrency() {
        String[] separatePair = currencyPair.split("/");
        return separatePair[0];
    }

    public String getSelectedCurrencyPair() {
        return currencyPair;
    }
    
    public List<String> getExchangeCurrencyPairs(String exchangeName) {
        List<String> currencyPairs = new ArrayList<String>();
        switch (exchangeName.toLowerCase()) {
            case "btcmarkets":
                currencyPairs.add("BTC/AUD");
                currencyPairs.add("LTC/AUD");
                break;
            case "btc-e":
                currencyPairs.add("BTC/USD");
                break;
            case "independentreserve":
                currencyPairs.add("BTC/AUD");
                currencyPairs.add("BTC/USD");
                currencyPairs.add("BTC/NZD");
                break;
            case "bitstamp":
                currencyPairs.add("BTC/USD");
                break;    
            default:
                break;
        }
        return  currencyPairs;
    }
   
    
    
    public TickerDTO getTicker(String currencyPair) throws IOException {

        if (exchange == null) {
            return null;
        }

        PollingMarketDataService marketDataService = exchange.getPollingMarketDataService();
        Ticker ticker = marketDataService.getTicker(new CurrencyPair(currencyPair));
        TickerDTO tickerDTO = new TickerDTO();
        tickerDTO.setAsk(ticker.getAsk());
        tickerDTO.setBid(ticker.getBid());
        tickerDTO.setLast(ticker.getLast());
        tickerDTO.setVolume(ticker.getVolume());
        return tickerDTO;
    }

    public UserTrades getTradeHistory() throws IOException {
        return null;
    }
    
    /*public Trades getTradeHistory() throws IOException {
        return null;
    }*/
    
    
    public String getOpenOrders(String currency, String altcoin, int limit, int since) throws Exception {
        return "";
    }
    
    public String getCurrencyBalance(Currency currency, int scale) throws Exception {
        Balance balance = null;
        PollingAccountService accountService = exchange.getPollingAccountService();
        balance = accountService.getAccountInfo().getWallet().getBalance(currency);
        return BigDecimal.valueOf(balance.getTotal().doubleValue()).toPlainString();
    }
    
    public String getDepositeAddress(Currency currency) throws Exception {
        PollingAccountService accountService = exchange.getPollingAccountService();
        String depositAddress = accountService.requestDepositAddress(currency);
        return depositAddress;
    }
    
    public String withdrawBitcoins(String currency, String coinAmount, String withdrawAddress) throws Exception {
        PollingAccountService accountService = exchange.getPollingAccountService();
        return accountService.withdrawFunds(new Currency(currency), new BigDecimal (coinAmount), withdrawAddress);
    }
    
    public String getDepositAddressForExchange(String currency) throws Exception {
        PollingAccountService accountService = exchange.getPollingAccountService();
        return accountService.requestDepositAddress(new Currency(currency.toUpperCase()));
    }
    
    public List<BigDecimal[]> getOrdersRaw()throws IOException{
       return null;
   }
   
   public List<BigDecimal[]> getBidsRaw()throws IOException{
       return null;
   }
   
   public Trades getTradeData(String currencyPair) throws IOException {
        Trades trades = null;
        PollingMarketDataService marketDataService = exchange.getPollingMarketDataService();
        try {
            trades = marketDataService.getTrades(new CurrencyPair(currencyPair));
        }catch(Exception e) {
            System.out.println("Graph Data Exception="+e.getMessage());
            logger.error("Unable to load Graph Data: ", e);
            return new Trades(new ArrayList<Trade>(),Trades.TradeSortType.SortByTimestamp);
        }
        return trades;
    }
}
