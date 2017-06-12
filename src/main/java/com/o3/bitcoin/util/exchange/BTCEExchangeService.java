/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.knowm.xchange.dto.marketdata.Trades;

/**
 *
 * @author
 */
public class BTCEExchangeService extends ExchangeService {
    
    public BTCEExchangeService(String exchangeName, String apiKey, String apiSecret) {
        super(exchangeName,apiKey,apiSecret);
    }

    public List<BigDecimal[]> getOrdersRaw()throws IOException {
        List<BigDecimal[]> asks=null;
       BTCEMarketDataServiceRaw marketDataService = (BTCEMarketDataServiceRaw) exchange.getPollingMarketDataService();

    // Get the latest full order book data for LTC/USD
        Map<String, BTCEDepth> depth = marketDataService.getBTCEDepth("btc_usd", 50).getDepthMap();
        for (Map.Entry<String, BTCEDepth> entry : depth.entrySet()) {
            asks = entry.getValue().getAsks();
        }
        return asks;
      //Map.Entry<String, BTCEDepth> entry = depth.entrySet();
      //return entry.getValue().getBids();
       
       //return null;
   }
   
   public List<BigDecimal[]> getBidsRaw()throws IOException{
       List<BigDecimal[]> bids=null;
       BTCEMarketDataServiceRaw marketDataService = (BTCEMarketDataServiceRaw) exchange.getPollingMarketDataService();

        // Get the latest full order book data for LTC/USD
        Map<String, BTCEDepth> depth = marketDataService.getBTCEDepth("btc_usd", 50).getDepthMap();
        for (Map.Entry<String, BTCEDepth> entry : depth.entrySet()) {
            bids = entry.getValue().getBids();
        }
        return bids;
   }
   
   public UserTrades getTradeHistory() throws IOException {
        PollingTradeService tradeService = exchange.getPollingTradeService();
        BTCETradeHistoryParams params = (BTCETradeHistoryParams) tradeService.createTradeHistoryParams();
        params.setPageLength(50);
        params.setCurrencyPair(new CurrencyPair(currencyPair));
        UserTrades trades = tradeService.getTradeHistory(params);
        return trades;
    }
   
   /*public Trades getTradeHistory() throws IOException {
        PollingTradeService tradeService = exchange.getPollingTradeService();
        BTCETradeHistoryParams params = (BTCETradeHistoryParams) tradeService.createTradeHistoryParams();
        params.setPageLength(50);
        params.setCurrencyPair(new CurrencyPair(currencyPair));
        UserTrades trades = tradeService.getTradeHistory(params);
        return trades;
    }*/
   
}
