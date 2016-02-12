/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.exception;

/**
 * <p> Exception to provide Currency Rate failure information</p>
*/

public class CurrencyRateNotAvailableException extends ClientRuntimeException {

    private int code;

    public CurrencyRateNotAvailableException(int code) {
        super("Currency Rates are not available");
        this.code = code;
    }

    public CurrencyRateNotAvailableException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CurrencyRateNotAvailableException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public CurrencyRateNotAvailableException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return super.toString() + " : Err: " + code;
    }
}
