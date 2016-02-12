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
 * <p>Exception to provide Seed Phrase failure information</p>
*/
public class SeedPhraseException extends RuntimeException {

    /**
     * class constructor 
     */
    public SeedPhraseException() {
    }

    /**
     * class constructor 
     * @param message exception message
     */
    public SeedPhraseException(String message) {
        super(message);
    }

    public SeedPhraseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeedPhraseException(Throwable cause) {
        super(cause);
    }

    public SeedPhraseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
