/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.btce.v3.dto.marketdata.BTCEDepth;
import org.knowm.xchange.btce.v3.service.polling.BTCEMarketDataServiceRaw;
import org.knowm.xchange.btce.v3.service.polling.trade.params.BTCETradeHistoryParams;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.polling.trade.PollingTradeService;

import org.knowm.xchange.independentreserve.service.polling.IndependentReserveMarketDataServiceRaw;
import org.knowm.xchange.independentreserve.dto.marketdata.IndependentReserveOrderBook;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.independentreserve.dto.marketdata.OrderBookOrder;

/**
 *
 * @author
 */
public class IndependentReserveExchangeService extends ExchangeService {
    
    public IndependentReserveExchangeService(String exchangeName, String apiKey, String apiSecret) {
        super(exchangeName,apiKey,apiSecret);
    }
    
    public List<BigDecimal[]> getOrdersRaw()throws IOException {
        int records = 0;
        List<BigDecimal[]> asks = new ArrayList<BigDecimal[]>();
        IndependentReserveMarketDataServiceRaw marketDataService = (IndependentReserveMarketDataServiceRaw) exchange.getPollingMarketDataService();
        IndependentReserveOrderBook orderBook = marketDataService.getIndependentReserveOrderBook(new Currency(getFiatCurrency().toUpperCase()).getCurrencyCode(), new Currency(getFiatCurrency().toUpperCase()).getCurrencyCode());
        List<OrderBookOrder> lstOrderBook = orderBook.getSellOrders();
        for(OrderBookOrder order : lstOrderBook ) {
            records++;
            BigDecimal[] entry = new BigDecimal[2];
            entry[0] = order.getPrice();
            entry[1] = order.getVolume();
            asks.add(entry);
            if(records == 50 )
                break;
        }
        return asks;
   }
   
   public List<BigDecimal[]> getBidsRaw()throws IOException{
        int records = 0;
        List<BigDecimal[]> bids = new ArrayList<BigDecimal[]>();
        IndependentReserveMarketDataServiceRaw marketDataService = (IndependentReserveMarketDataServiceRaw) exchange.getPollingMarketDataService();
        IndependentReserveOrderBook orderBook = marketDataService.getIndependentReserveOrderBook(new Currency(getFiatCurrency().toUpperCase()).getCurrencyCode(), new Currency(getFiatCurrency().toUpperCase()).getCurrencyCode());
        List<OrderBookOrder> lstOrderBook = orderBook.getBuyOrders();
        for(OrderBookOrder order : lstOrderBook ) {
            records++;
            BigDecimal[] entry = new BigDecimal[2];
            entry[0] = order.getPrice();
            entry[1] = order.getVolume();
            bids.add(entry);
            if(records == 50 )
                break;
        }
        return bids;
   }
   
   public UserTrades getTradeHistory() throws IOException {
        PollingTradeService tradeService = exchange.getPollingTradeService();
        UserTrades trades = tradeService.getTradeHistory(tradeService.createTradeHistoryParams());
        return trades;
    }
}
