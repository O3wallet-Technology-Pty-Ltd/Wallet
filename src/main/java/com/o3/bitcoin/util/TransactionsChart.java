/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

import org.jfree.chart.JFreeChart;

/**
 * Class that wraps transaction chart
 */
public class TransactionsChart {
    public enum Type {
        DEBIT("Debit"), CREDIT("Credit"), BOTH("Debit/Credit");
        private String value;
        
        private Type(String value) {
            this.value = value;
        }        
        
        @Override
        public String toString() {
            return this.value;
        }
    };
    private JFreeChart chart;
    private double totalDebit;
    private double totalCredit;
    private Type type = Type.DEBIT;

    public TransactionsChart(JFreeChart chart, double totalDebit, double totalCredit, Type type) {
        this.chart = chart;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
        this.type = type;
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    public double getTotalDebit() {
        return this.totalDebit;
    }

    public double getTotalCredit() {
        return totalCredit;
    }

    public boolean isDebitAndCredit() {
        return type == Type.BOTH;
    }
    
    public boolean isDebit() {
        return type == Type.DEBIT;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public String getTypeString() {
        return type.toString();
    }
}
