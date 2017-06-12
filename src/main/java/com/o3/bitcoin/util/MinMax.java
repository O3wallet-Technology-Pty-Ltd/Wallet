/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

/**
 *
 * @author
 */
public class MinMax {
    private double minValue = 0.0;
    private double maxValue = 0.0;
    
    public double getMinValue() {
        return minValue;
    }
    public double getMaxValue() {
        return maxValue;
    }
    
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
