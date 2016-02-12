/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.exception;

/**
 * <p> Exception to provide Encrypted File Read/Write failure information</p>
*/
public class EncryptedFileReaderWriterException extends RuntimeException {

    public EncryptedFileReaderWriterException(String s) {
        super(s);
    }

    public EncryptedFileReaderWriterException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
