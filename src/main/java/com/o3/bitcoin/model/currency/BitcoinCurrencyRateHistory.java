/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model.currency;

import com.o3.bitcoin.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * <p>Class to contain Bitcoin to currency rate for specific time period</p>
*/
public class BitcoinCurrencyRateHistory {

    private Date startDate;
    private Date endDate;
    private List<Rate> rateData = new ArrayList<>();

    /**
     * class constructor  
     */
    public BitcoinCurrencyRateHistory() {
    }

    /**
     * class constructor  
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     */
    public BitcoinCurrencyRateHistory(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    /**
     * class constructor  
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @param dateData Bitcoin to currency conversion data list
     */
    public BitcoinCurrencyRateHistory(Date startDate, Date endDate, List<Rate> rateData) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rateData = rateData;
    }

    /**
     * function to get startDate  
     * @return startDate 
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * function to set startDate  
     * @param startDate start date 
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * function to get endDate  
     * @return endDate 
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * function to set endDate  
     * @return endDate 
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * function to get Bitcoin to currency rate data  
     * @return currency rate data 
     */
    public List<Rate> getRateData() {
        return rateData;
    }

    /**
     * function to set Bitcoin to currency rate data  
     * @param rateData currency rate data 
    */
    public void setRateData(List<Rate> rateData) {
        this.rateData = rateData;
    }

    /**
     * function to add Bitcoin to currency rate data in list  
     * @param rate currency rate data 
     */
    public void addRate(Rate rate) {
        if (this.rateData == null) {
            this.rateData = new ArrayList<>();
        }
        this.rateData.add(rate);
    }

    /**
     * function to reset bitcoin to currency rate data list  
     */
    public void resetRates() {
        this.rateData.clear();
    }
    
    /**
     * function to get minimum rate value
     * @return minimum rate value
     */
    public Double getMinimumValue() {
        Double value = rateData.get(0).getValue();
        for( int i = 0; i < rateData.size(); i++ )
        {
            if( value > rateData.get(i).getValue() )
                value = rateData.get(i).getValue();
        }
        return value;        
    }
    
    /**
     * function to get maximum rate value
     * @return maximum rate value
     */
    public Double getMaximumValue() {
        Double value = rateData.get(0).getValue();
        for( int i = 0; i < rateData.size(); i++ )
        {
            if( value < rateData.get(i).getValue() )
                value = rateData.get(i).getValue();
        }
        return value;        
    }

    public void sort() {
        Collections.sort(rateData, new Comparator<Rate>() {

            @Override
            public int compare(Rate o1, Rate o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
    }

    @Override
    public String toString() {
        return "History: [" + Utils.formatSimpleDate(startDate) + " : " + Utils.formatSimpleDate(endDate) + "] > Rates: " + (rateData != null ? rateData.toString() : "N/A");
    }
}
