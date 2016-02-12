/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model.currency;

import com.o3.bitcoin.util.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Class that represents Rate data in bitcoint to currency rate data list in class BitcoinCurrencyRateHistory </p>
*/
public class Rate {
    private Double value;
    private Date timestamp;

    public Rate() {
    }

    public Rate(Double value, Date timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getDay() {
        return new SimpleDateFormat("EEE").format(timestamp);
    }

    @Override
    public String toString() {
        return "[R: " + value + " : D: " + Utils.formatSimpleDate(timestamp) + "]";
    }
}
