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
import org.knowm.xchange.btce.v3.service.polling.BTCEMarketDataServiceRaw;
import org.knowm.xchange.btcmarkets.dto.marketdata.BTCMarketsOrderBook;
import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.btce.v3.dto.marketdata.BTCEDepth;
import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsTradeService;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.polling.trade.PollingTradeService;
import org.knowm.xchange.btce.v3.service.polling.trade.params.BTCETradeHistoryParams;


import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.bitstamp.dto.marketdata.BitstampOrderBook;
import org.knowm.xchange.bitstamp.dto.marketdata.BitstampTicker;
import org.knowm.xchange.bitstamp.service.polling.BitstampMarketDataServiceRaw;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;

import org.knowm.xchange.bitstamp.service.polling.BitstampTradeService;
import org.knowm.xchange.bitstamp.service.polling.BitstampTradeServiceRaw;






/**
 *
 * @author
 */
public class BitstampExchangeService extends ExchangeService {
    
    public BitstampExchangeService(String exchangeName, String apiKey, String apiSecret, String customerID) {
        super(exchangeName,apiKey,apiSecret, customerID);
    }

    public List<BigDecimal[]> getOrdersRaw()throws IOException {
        int records = 0;
        List<BigDecimal[]> bids = new ArrayList<BigDecimal[]>();
        // Interested in the public market data feed (no authentication)
        PollingMarketDataService marketDataService = exchange.getPollingMarketDataService();
        BitstampMarketDataServiceRaw raw = (BitstampMarketDataServiceRaw)marketDataService;
        BitstampOrderBook orderBook = raw.getBitstampOrderBook();    
        List<List<BigDecimal>> obBids = orderBook.getAsks();
        // Get the current orderbook
        for(int i = 0; i < obBids.size(); i++) {
            records++;
            List<BigDecimal> values = obBids.get(i);
            BigDecimal[] entry = new BigDecimal[2];
            entry[0] = values.get(0);
            entry[1] = values.get(1);
            bids.add(entry);
            if(records == 50 )
                break;
        }
        return bids;
   }
   
   public List<BigDecimal[]> getBidsRaw()throws IOException{
      int records = 0;
        List<BigDecimal[]> bids = new ArrayList<BigDecimal[]>();
        // Interested in the public market data feed (no authentication)
        PollingMarketDataService marketDataService = exchange.getPollingMarketDataService();
        BitstampMarketDataServiceRaw raw = (BitstampMarketDataServiceRaw)marketDataService;
        BitstampOrderBook orderBook = raw.getBitstampOrderBook();    
        List<List<BigDecimal>> obBids = orderBook.getBids();
        // Get the current orderbook
        for(int i = 0; i < obBids.size(); i++) {
            records++;
            List<BigDecimal> values = obBids.get(i);
            BigDecimal[] entry = new BigDecimal[2];
            entry[0] = values.get(0);
            entry[1] = values.get(1);
            bids.add(entry);
            if(records == 50 )
                break;
        }
        return bids;
   }
   
   public UserTrades getTradeHistory() throws IOException {
       return null;
    }
   
   /*public Trades getTradeHistory() throws IOException {
        PollingTradeService tradeService = exchange.getPollingTradeService();
        Trades trades = tradeService.getTradeHistory(tradeService.createTradeHistoryParams());
        
        return trades;
    }*/
   
   public Trades getTradeData(String currencyPair) throws IOException {
        Trades trades = null;
        PollingMarketDataService marketDataService = exchange.getPollingMarketDataService();
        try {
            trades = marketDataService.getTrades(new CurrencyPair(currencyPair),BitstampMarketDataServiceRaw.BitstampTime.DAY);
        }catch(Exception e) {
            return new Trades(new ArrayList<Trade>(),Trades.TradeSortType.SortByTimestamp);
        }
        return trades;
    }
}
