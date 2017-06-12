/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.json.JSONObject;
import org.knowm.xchange.btcmarkets.dto.marketdata.BTCMarketsOrderBook;
import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsMarketDataServiceRaw;
import org.knowm.xchange.btcmarkets.service.polling.BTCMarketsTradeService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;
import org.knowm.xchange.service.polling.trade.PollingTradeService;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.service.polling.account.PollingAccountService;


/**
 *
 * @author 
 */
public class BTCMarketExchangeService extends ExchangeService{

    public BTCMarketExchangeService(String exchangeName, String apiKey, String apiSecret) {
        super(exchangeName,apiKey,apiSecret);
    }
     
    public TickerDTO getTicker(String currencyPair) throws IOException {

        if (exchange == null) {
            return null;
        }
        BigDecimal santoshiValue;
        santoshiValue = java.math.BigDecimal.valueOf(100000000);
        PollingMarketDataService btcMarketsMarketDataService = exchange.getPollingMarketDataService();
        Ticker ticker = btcMarketsMarketDataService.getTicker(new CurrencyPair(currencyPair));
        TickerDTO tickerDTO = new TickerDTO();
        tickerDTO.setAsk(ticker.getAsk().multiply(santoshiValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        tickerDTO.setBid(ticker.getBid().multiply(santoshiValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        tickerDTO.setLast(ticker.getLast().multiply(santoshiValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        tickerDTO.setVolume(ticker.getVolume());
        return tickerDTO;
    }
    
    
    public List<BigDecimal[]> getOrdersRaw()throws IOException{
        List<BigDecimal[]> orders=null;
        BTCMarketsMarketDataServiceRaw btcMarketsMarketDataService = (BTCMarketsMarketDataServiceRaw) exchange.getPollingMarketDataService();
        BTCMarketsOrderBook depth = btcMarketsMarketDataService.getBTCMarketsOrderBook(new CurrencyPair(currencyPair));
        orders = depth.getAsks();
       return orders;
   }
   
   public List<BigDecimal[]> getBidsRaw()throws IOException{
        List<BigDecimal[]> bids = null;
        BTCMarketsMarketDataServiceRaw btcMarketsMarketDataService = (BTCMarketsMarketDataServiceRaw) exchange.getPollingMarketDataService();
        BTCMarketsOrderBook depth = btcMarketsMarketDataService.getBTCMarketsOrderBook(new CurrencyPair(currencyPair));
        bids = depth.getBids();
       return bids;
   }
   
   public UserTrades getTradeHistory() throws IOException {
        UserTrades tradeHistory = null;
        PollingTradeService tradeService = exchange.getPollingTradeService();
        BTCMarketsTradeService.HistoryParams params = (BTCMarketsTradeService.HistoryParams) tradeService.createTradeHistoryParams();
        params.setPageLength(50);
        tradeHistory = tradeService.getTradeHistory(params);
        return tradeHistory;
    }
   
   public String withdrawBitcoins(String currency, String coinAmount, String withdrawAddress) throws Exception {
        BTCMarketApiClient client = new BTCMarketApiClient(apiKey,apiSecret);
        String result = client.createNewWithdraw((int)(Double.parseDouble(coinAmount)*100000000),withdrawAddress,currency);
        JSONObject json = new JSONObject(result);
        boolean success = json.getBoolean("success");
        if( !success ) {
            throw new Exception(json.getString("errorMessage"));
        }
        return "";// currently this call does not return the tx id of the withdrawal as this is just the submission of the request. The actual withdraw process is executed in a separate job.
    }
   
   public String getOpenOrders(String currency, String altcoin, int limit, int since) throws Exception {
        BTCMarketApiClient client = new BTCMarketApiClient(apiKey,apiSecret);
        String result = client.orderOpen(currency, altcoin, limit, since);
        //String result = "{\"success\":true,\"errorCode\":null,\"errorMessage\":null,\"orders\":[{\"id\":1003245675,\"currency\":\"AUD\",\"instrument\":\"BTC\",\"orderSide\":\"Bid\",\"ordertype\":\"Limit\",\"creationTime\":1378862733366,\"status\":\"Placed\",\"errorMessage\":null,\"price\":13000000000,\"volume\":10000000,\"openVolume\":10000000,\"clientRequestId\":null},{\"id\":4345675,\"currency\":\"AUD\",\"instrument\":\"BTC\",\"orderSide\":\"Ask\",\"ordertype\":\"Limit\",\"creationTime\":1378636912705,\"status\":\"Fully Matched\",\"errorMessage\":null,\"price\":13000000000,\"volume\":10000000,\"openVolume\":0,\"clientRequestId\":null}]}";
        JSONObject json = new JSONObject(result);
        boolean success = json.getBoolean("success");
        if( !success ) {
            throw new Exception(json.getString("errorMessage"));
        }
        return result;
    }
   
   /*public Trades getTradeHistory() throws IOException {
        UserTrades tradeHistory = null;
        PollingTradeService tradeService = exchange.getPollingTradeService();
        BTCMarketsTradeService.HistoryParams params = (BTCMarketsTradeService.HistoryParams) tradeService.createTradeHistoryParams();
        params.setPageLength(50);
        tradeHistory = tradeService.getTradeHistory(params);
        return tradeHistory;
    }*/
}
