/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import java.util.HashMap;

/**
 *
 * @author 
 */
public class ExchangeServiceFactory {
    
    public static String currentExchange = "";
    public static String currentCurrency = "";
    public static HashMap <String, ExchangeService> exchangeMap = new HashMap<String, ExchangeService>();
   
   
    public static ExchangeService getExchange(String name){
        ExchangeService exchangeService;
        exchangeService = exchangeMap.get(name);
        return exchangeService;
    }
    
    public static void clearExchangeMap(){
        exchangeMap.clear();
    }
    
    public static Boolean addExchange(String name, String apiKey, String apiSecret, String customerID){
        ExchangeService exchangeService;
        Boolean returnValue = false;
        exchangeService = exchangeMap.get(name);
        if(exchangeService == null) {
            //exchangeService = ExchangeService.getExchange(name.toLowerCase(),apiKey,apiSecret);
            if( name.toLowerCase().equals("btcmarkets"))
                exchangeService = new BTCMarketExchangeService(name.toLowerCase(),apiKey,apiSecret);
            else if( name.toLowerCase().equals("independentreserve"))
                exchangeService = new IndependentReserveExchangeService(name.toLowerCase(),apiKey,apiSecret);
            else if( name.toLowerCase().equals("bitstamp"))
                exchangeService = new BitstampExchangeService(name.toLowerCase(),apiKey,apiSecret,customerID);
            exchangeMap.put(name, exchangeService);
            returnValue = true;
        }
        return returnValue;
    }
  /*  public static ExchangeService getExchange(String name, String apiKey, String apiSecret){
         ExchangeService exchangeService;
        exchangeService = exchangeMap.get(name);
        if(exchangeService == null){
           exchangeService = ExchangeService.getExchange(name,"6a70fc07-b292-47e9-8ed8-056bb25e47c3","bGPvMYhBsE7vG5fQpBxv+aeeQ/DNthAX8Tz05urLYtHxaopx7+jQ4DDhpyDVi4S37bw2TufUaoxfnlqWljsQvw==");
           exchangeMap.put(name, exchangeService);
        }
        
        return exchangeService;
    }*/
}
