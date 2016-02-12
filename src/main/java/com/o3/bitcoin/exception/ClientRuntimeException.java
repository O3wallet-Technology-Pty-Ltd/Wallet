/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.exception;

/**
 *
 * @author
 */
/**
 * <p> Base Exception to provide general run time failure information</p>
*/

public class ClientRuntimeException extends RuntimeException {

    /**
     * class constructor 
     */
    public ClientRuntimeException() {
    }
    
    /**
     * class constructor 
     * @param message exception message
     */
    public ClientRuntimeException(String message) {
        super(message);
    }
    
    /**
     * class constructor 
     * @param message exception message
     * @param cause exception cause
     */
    public ClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientRuntimeException(Throwable cause) {
        super(cause);
    }

    public ClientRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
