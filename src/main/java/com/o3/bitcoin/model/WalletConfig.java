/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import org.bitcoinj.crypto.DeterministicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p>Class to store configuration of a wallet</p>
*/
public class WalletConfig {

    private static final Logger logger = LoggerFactory.getLogger(WalletConfig.class);
    private String id;
    private String location;
    private String network = "TESTNET";
    @XStreamOmitField
    private String firstAccountName;
    @XStreamOmitField
    private int numberOfAccounts = 1; 
    @XStreamOmitField
    private List<String> mnemonicCodes;
    @XStreamOmitField
    private String password;
    @XStreamOmitField
    private Date creationDate;
    @XStreamOmitField
    private File walletFile;
    @XStreamOmitField
    private DeterministicKey watchingKey;
    @XStreamOmitField
    private boolean watchOnly = false;

    /**
     * class constructor  
     * @param id wallet name
     * @param location wallet location on hard disk
     * @param network network name 
     */
    public WalletConfig(String id, String location, String network) {
        this.id = id;
        this.location = location;
        this.network = network;
    }

    /**
     * class constructor  
     * @param id wallet name
     * @param location wallet location on hard disk
     * @param network network name 
     * @param mnemonicCodes wallet creation mnemonic codes
     * @param password wallet password
     * @param creationDate wallet creation date
     */
    public WalletConfig(String id, String location, String network, List<String> mnemonicCodes, String password, Date creationDate) {
        this.id = id;
        this.location = location;
        this.mnemonicCodes = mnemonicCodes;
        this.password = password;
        this.creationDate = creationDate;
        this.network = network;
    }
    
    /**
     * class constructor  
     * @param id wallet name
     * @param location wallet location on hard disk
     * @param network network name 
     * @param mnemonicCodes wallet creation mnemonic codes
     * @param creationDate wallet creation date
     * @param firstAccountName HD wallet first account name
     * @param numberOfAccounts number of accounts in HD Wallet
     */
    public WalletConfig(String id, String location, String network, List<String> mnemonicCodes, Date creationDate,String firstAccountName, int numberOfAccounts) {
        this.id = id;
        this.location = location;
        this.mnemonicCodes = mnemonicCodes;
        this.creationDate = creationDate;
        this.network = network;
        this.firstAccountName = firstAccountName;
        this.numberOfAccounts = numberOfAccounts;
    }
    
    /**
     * class constructor  
     * @param id wallet name
     * @param location wallet location on hard disk
     * @param network network name 
     * @param walletFile wallet file on disk
     * @param password wallet password
     */
    public WalletConfig(String id, String location, String network, File walletFile, String password) {
        this.id = id;
        this.location = location;
        this.walletFile = walletFile;
        this.password = password;
        this.network = network;
    }

    public WalletConfig(String id, String location, String network, DeterministicKey watchingKey) {
        this.id = id;
        this.location = location;
        this.watchingKey = watchingKey;
        this.watchOnly = true;
        this.network = network;
    }

    /**
     * function to get wallet name  
     * @return name of wallet 
     */
    public String getId() {
        return id;
    }

    /**
     * function to set wallet name  
     * @param id wallet name 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * function to get wallet location on disk  
     * @return wallet location on disk 
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * function to set wallet location on disk  
     * @param location wallet location 
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public String getFirstAccountName() {
        return firstAccountName;
    }
     
    public void setFirstAccountName(String acctName) {
        firstAccountName = acctName;
    } 
    
    /**
     * function to get network on which wallet is created  
     * @return wallet network 
     */
    public String getNetwork() {
        return network;
    }

    /**
     * function to set network on which wallet is created  
     * @return network wallet network 
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    public List<String> getMnemonicCodes() {
        return mnemonicCodes;
    }

    public void setMnemonicCodes(List<String> mnemonicCodes) {
        this.mnemonicCodes = mnemonicCodes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

     /**
     * function to get wallet file on disk  
     * @return wallet file
     */
    public File getWalletFile() {
        return walletFile;
    }

    /**
     * function to set wallet   
     * @param walletFile wallet file on disk
     */
    public void setWalletFile(File walletFile) {
        this.walletFile = walletFile;
    }

    public DeterministicKey getWatchingKey() {
        return watchingKey;
    }

    public void setWatchingKey(DeterministicKey watchingKey) {
        this.watchingKey = watchingKey;
    }

    public boolean isWatchOnly() {
        return watchOnly;
    }

    public void setWatchOnly(boolean watchOnly) {
        this.watchOnly = watchOnly;
    }
    
    /**
     * function to set number of accounts in the wallet  
     * @param acctNums number of accounts
     */
    public void setNumberOfAccounts( int acctNums) {
        this.numberOfAccounts = acctNums;
    }
    
    public int getNumberOfAccounts() {
        return this.numberOfAccounts;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.network);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WalletConfig other = (WalletConfig) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.network, other.network)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id;
    }
}
