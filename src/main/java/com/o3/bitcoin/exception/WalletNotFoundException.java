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
 * <p> Exception to provide Wallet found failure information</p>
*/
public class WalletNotFoundException extends Exception {

    public WalletNotFoundException(String id, String location) {
        super("Wallet {" + id + "} not found at location {" + location + "}" + id);
    }
}
