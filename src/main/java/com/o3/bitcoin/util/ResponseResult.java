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
public class ResponseResult {
    private int statusCode;
    private String responseText;
    
    public void setStatusCode(int stCode) {
        statusCode = stCode;
    }
    public void setResponseText(String resText) {
        responseText = resText;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    public String getResponseText() {
        return responseText;
    }
}
