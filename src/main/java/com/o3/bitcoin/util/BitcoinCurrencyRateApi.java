/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.exception.CurrencyRateNotAvailableException;
import com.o3.bitcoin.model.currency.BitcoinCurrencyRateHistory;
import com.o3.bitcoin.model.currency.Rate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that gets bitcoin to currency rates 
 */
public class BitcoinCurrencyRateApi {

    private static final Logger logger = LoggerFactory.getLogger(BitcoinCurrencyRateApi.class);
    private static BitcoinCurrencyRateHistory historyCache = null;
    private static BitcoinCurrencyRateApi api;
    public static Rate currentRate = null;

    private BitcoinCurrencyRateApi() {

    }

    /**
     * function that returns class object
     * @return current class object
     */
    public static BitcoinCurrencyRateApi get() {
        if (api == null) {
            api = new BitcoinCurrencyRateApi();
        }
        return api;
    }

    public BitcoinCurrencyRateHistory getBitcoinCurrentRateHistory() throws IOException {
        ////return getCurrentRateHistoryData("USD");
        return getCurrentRateHistoryData("BTCUSD");
    }

    public BitcoinCurrencyRateHistory getCurrentRateHistoryData(String currency) throws IOException {
        Date endDate = new Date();
        Date startDate = new Date();
        startDate.setDate(startDate.getDate() - 6);
        ////return getCurrentRateHistoryData(currency, startDate, endDate);
        return getCurrentRateHistoryData("BTC"+currency, startDate, endDate);
    }

    /**
     * function that gets bitcoint to currency rates for a specific days
     * @param currency bitcoing to currency 
     * @param startDate start date of currency rate
     * @param endDate end date of currency rate
     * @return bitcoin to currency rates
     * @throws IOException 
     */
    public BitcoinCurrencyRateHistory getCurrentRateHistoryData(String currency, Date startDate, Date endDate) throws IOException {
        if (currency == null || currency.isEmpty()) {
            throw new ClientRuntimeException("currency is required.");
        }
        if (startDate == null) {
            throw new ClientRuntimeException("startDate is required.");
        }
        if (endDate == null) {
            throw new ClientRuntimeException("endDate is required.");
        }
        BitcoinCurrencyRateHistory history = new BitcoinCurrencyRateHistory(startDate, endDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<Date, Double> maps = new TreeMap<Date, Double>(new Comparator<Date>() {
            public int compare(Date date1, Date date2) {
                return date2.compareTo(date1);
            }
        });
        List<String> lines = new ArrayList<String>();
        try {
          URL url = new URL("https://apiv2.bitcoinaverage.com/indices/global/history/"+currency+"?period=monthly&format=csv");  
        
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(60000);
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        int count = 0;
        while ((inputLine = in.readLine()) != null) {
            if( count == 0 ) {
                count = 1;
                continue;
            }
            String[] values = inputLine.split(",");
            maps.put(new Date(dateFormat.parse(values[0].split(" ")[0]).getTime()), Double.parseDouble(values[3]));        
        }
        in.close();
        int day = 1;
        long numberOfDays = (endDate.getTime() - startDate.getTime())/(1000 * 60 * 60 * 24);
        for (Map.Entry<Date, Double> entry : maps.entrySet()) {
            if( day > numberOfDays )
                break;
            history.addRate(new Rate(entry.getValue(), getPreviousDate(day++)));
        }
        history.addRate(getCurrentRate(currency));
        historyCache = history;
        }catch(Exception e ) {
            throw new ClientRuntimeException(e.getMessage());
        }
        return history;
    }
 
    /**
     * function that get current bitcoin to currency rate
     * @param currency currency in conversion
     * @return current rate
     * @throws IOException 
     */
    public Rate getCurrentRate(String currency) throws IOException {
        Rate rate = null;
        try {
          URL url = new URL("https://apiv2.bitcoinaverage.com/indices/global/history/"+currency+"?period=daily&format=json");  
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(60000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            String str = "";
            String strRate = "";
            while ((inputLine = in.readLine()) != null) {
                str = inputLine;
                if(str.contains("average")) {
                    String[] values = inputLine.split(":");
                    String[] rates = values[1].split(",");
                    strRate = rates[0];
                    rate = new Rate(Double.parseDouble(strRate), new Date());
                    currentRate = rate;
                    break;
                }
            }
            in.close();
        }catch(Exception e) {
            throw new ClientRuntimeException(e.getMessage());
        }
        return rate;
    }
        
    public double getCurrentRateValue() {
        if(currentRate == null) {
            return 0d;
        }
        return currentRate.getValue();
    }

    private Date getPreviousDate(int days) {
        Date date = new Date();
        date.setDate(date.getDate() - days);
        return date;
    }
}
