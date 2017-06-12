/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import java.math.BigDecimal;
import java.util.Date;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

/**
 *
 * @author
 */
public class TickerDTO {
    private CurrencyPair currencyPair;
        private BigDecimal last;
        private BigDecimal bid;
        private BigDecimal ask;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal vwap;
        private BigDecimal volume;
        private Date timestamp;

    /*public TickerDTO(Ticker ticker) {
     
       BigDecimal santoshiValue;
       santoshiValue = java.math.BigDecimal.valueOf(100000000);
    
      setAsk(ticker.getAsk().multiply(santoshiValue).setScale(2, BigDecimal.ROUND_HALF_UP));
      setBid(ticker.getBid().multiply(santoshiValue).setScale(2, BigDecimal.ROUND_HALF_UP));
      setLast(ticker.getLast().multiply(santoshiValue).setScale(2, BigDecimal.ROUND_HALF_UP));
    }*/

    public TickerDTO() {
    }    
        
    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public BigDecimal getLast() {
        return last;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getVwap() {
        return vwap;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public void setVwap(BigDecimal vwap) {
        this.vwap = vwap;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
       
}
