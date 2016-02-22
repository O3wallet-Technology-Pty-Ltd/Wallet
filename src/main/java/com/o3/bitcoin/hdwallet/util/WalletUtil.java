/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.hdwallet.util;

import com.google.common.util.concurrent.ServiceManager;
import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.hdwallet.HDWallet;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.util.seed.SeedGeneratorUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.utils.MonetaryFormat;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * @author
 */

/**
 * <p> Utility class of HD Wallet</p>
*/

public class WalletUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(WalletUtil.class);

    /**
     * function to create new HD Wallet 
     * @param walletConfig wallet configuration data
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     * @param acctNums number of accounts in HD Wallet
     */
    public static void createHDWallet(WalletConfig walletConfig,
                                      KeyCrypter keyCrypter,
                                      KeyParameter aesKey,
                                      int acctNums){
        SeedGeneratorUtils utils = new SeedGeneratorUtils();
        byte[] seedBytes = utils.convertToSeed(walletConfig.getMnemonicCodes());
        HDWallet hdwallet = new HDWallet(walletConfig,keyCrypter,aesKey,seedBytes,acctNums);
        hdwallet.persist();
    }
    /**
     * function to restore HD Wallet 
     * @param walletConfig wallet configuration data
     */
    public static HDWallet restoreHDWallet(WalletConfig walletConfig){
        NetworkParameters params = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? MainNetParams.get() : TestNet3Params.get(); 
        try
        {
            HDWallet hdwallet = HDWallet.restore(walletConfig,params,null,null);
            return hdwallet;
        }catch(Exception ex){
            logger.debug("wallet restore exception="+ex.getMessage());
           }
        return null;
    }
    
    /**
     * function to restore HD Wallet 
     * @param walletConfig wallet configuration data
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     */
    public static HDWallet restoreHDWallet(WalletConfig walletConfig,
                                           KeyCrypter keyCrypter,
                                           KeyParameter aesKey){
        NetworkParameters params = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? MainNetParams.get() : TestNet3Params.get(); 
        try
        {
            HDWallet hdwallet = HDWallet.restore(walletConfig,params,keyCrypter,aesKey);
            return hdwallet;
        }catch(Exception ex){
            logger.debug("wallet restore exception="+ex.getMessage());
        }
        return null;
    }
    
    public static String getWalletName(){
        return "o3wallet.hdwallet";
    }
    
    public static String getTempWalletFilePath(WalletConfig walletConfig){
        String networkFolder = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? "main" : "test";
        return walletConfig.getLocation() + File.separator + walletConfig.getId() + File.separator + networkFolder  + File.separator + getWalletName()+".tmp";
    }
    
    public static String getO3WalletFilePath(WalletConfig walletConfig){
        String networkFolder = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? "main" : "test";
        return walletConfig.getLocation() + File.separator + walletConfig.getId() + File.separator + networkFolder + File.separator + getWalletName();
    }
    
    public static String getActiveNetworkWalletDirPath(WalletConfig walletConfig){
        String networkFolder = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? "main" : "test";
        return walletConfig.getLocation() + File.separator + walletConfig.getId() + File.separator + networkFolder + File.separator;
    }
    
    public static String getWalletFilePath(WalletConfig walletConfig){
        String networkFolder = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? "main" : "test";
        return walletConfig.getLocation() + File.separator + walletConfig.getId() + File.separator + networkFolder + File.separator + walletConfig.getId()+".wallet";
    }
    
    public static String getSPVChainFilePath(WalletConfig walletConfig){
        String networkFolder = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? "main" : "test";
        return walletConfig.getLocation() + File.separator + walletConfig.getId() + File.separator + networkFolder + File.separator + walletConfig.getId()+".spvchain";
    }        
    
    public static Coin satoshiToBTC(long satoshi){
        return Coin.parseCoin(MonetaryFormat.BTC.noCode().format(Coin.valueOf(satoshi)).toString());
    }
    
    /**
     * function to get list of transactions in an Account  
     * @param wallet Wallet object
     * @param account hd account
     * @return account transactions list
     */
    public static List<Transaction> getAccountTransactions(Wallet wallet, HDAccount account){
        List<Transaction> accTrans = new ArrayList<>();
        boolean found = false;
        List<Transaction> transactionList = wallet.getTransactionsByTime();
        for (Transaction trx : transactionList) {
            if( trx.getConfidence().equals(TransactionConfidence.ConfidenceType.DEAD) )
                continue;
            found = false;
            List<TransactionOutput> lto = trx.getOutputs();
            for (TransactionOutput txo : lto) {
                try {
                    byte[] pubkey = null;
                    byte[] pubkeyhash = null;
                    Script script = txo.getScriptPubKey();
                    if (script.isSentToRawPubKey())
                        pubkey = script.getPubKey();
                    else
                        pubkeyhash = script.getPubKeyHash();
                    if( account.hasPubKey(pubkey, pubkeyhash) )
                        found = true;
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
            if( !found )
            {
                continue;
            }
             accTrans.add(trx);
        }
        return accTrans;
    }
    
    /**
     * function to authenticate wallet password  
     * @param password password to verify
     * @return whether wallet password verified or not
     */
    public static boolean verifyPassword(String password){
        
        WalletConfig walletConfig = ConfigManager.getWalletConfigOnActiveNetwork();
        if( walletConfig == null )
            walletConfig = ConfigManager.getWalletConfigOnUnactiveNetwork();
        String walletFile = getWalletFilePath(walletConfig);
        try {
            boolean isCorrectPassword = false;
            Wallet wallet = Wallet.loadFromFile(new File(walletFile));
            if( wallet != null )
            {
                isCorrectPassword = wallet.checkPassword(password);
            }
            return isCorrectPassword;
        } catch (UnreadableWalletException ex) {
            logger.debug("Wallet error="+ex.getMessage());
        } 
        return false;
    }
    
    public static String getMnemonicCodeAsString(List<String> mnemonicCodes) {
        int count = 0;
        String strMnemonicCode = "";
        for (String word : mnemonicCodes) {
            if( count == 0 ) 
               strMnemonicCode = word;
            else
                strMnemonicCode += " " + word;
            count = 1;
        }
        return strMnemonicCode;
    }
}
