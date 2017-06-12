/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.exchange;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author 
 */
public class GraphDTO {
    private BigDecimal price;
    private BigDecimal volume;
    private Date time;

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public Date getTime() {
        return time;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "time="+time+" price="+price+" volume="+volume;
    }
    
}
