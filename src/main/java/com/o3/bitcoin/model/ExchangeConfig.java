/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class ExchangeConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ExchangeConfig.class);
    private String exchangeName;
    private String apiKey;
    private String apiSecret;
    private String customerID;
    
    public ExchangeConfig(String exchangeName, String apiKey, String apiSecret, String customerID) {
        this.exchangeName = exchangeName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.customerID = customerID;
    }
    
    public String getExchangeName() {
        return exchangeName;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = apiSecret;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(exchangeName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if( obj == this) 
            return true;
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExchangeConfig other = (ExchangeConfig) obj;
        if (!Objects.equals(this.exchangeName, other.exchangeName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return exchangeName + "," + apiKey + ", " + apiSecret + "," + customerID;
    }
}
