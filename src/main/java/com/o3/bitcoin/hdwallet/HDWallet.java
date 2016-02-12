/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.hdwallet;


import com.o3.bitcoin.hdwallet.util.WalletUtil;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.model.manager.WalletManager;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionConfidence.ConfidenceType;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.Wallet.SendRequest;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.WalletTransaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * @author
 */

/**
    * <p> Class that implements HD Wallet</p>
    * <ul>
    * <li>maintain HD account information</li>
    * <li>serialize/deserialize HD Wallet</li>
    * </ul>
    *
*/
public class HDWallet {
    
    private static final Logger logger = LoggerFactory.getLogger(HDWallet.class);
    
    private static transient SecureRandom secureRandom = new SecureRandom();

    private NetworkParameters mParams = null;
    private KeyCrypter mKeyCrypter;
    private KeyParameter mAesKey;

    private DeterministicKey mMasterKey = null;
    private DeterministicKey mWalletRoot = null;

    private String mMnemonicCode = "";
    private byte[] mWalletSeed = {0};
    private String mPassphrase = "";
    
    private ArrayList<HDAccount> mAccounts;
    
    private HDAccount mCurrentAccount = null;
    
    public static WalletConfig mWalletConfig;
    
    /**
     * class constructor 
     * @param walletConfig wallet configuration data
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     * @param walletSeed seed to create HD Wallet
     * @param acctnum number of accounts in HD Wallet
     */
    public HDWallet(WalletConfig walletConfig,
                    KeyCrypter keyCrypter,
                    KeyParameter aesKey,
                    byte[] walletSeed,
                    int acctNums) {
        mWalletConfig = walletConfig;
        mParams = walletConfig.getNetwork().equalsIgnoreCase("MAINNET") ? MainNetParams.get() : TestNet3Params.get();
        mKeyCrypter = keyCrypter;
        mAesKey = aesKey;
        mMnemonicCode = WalletUtil.getMnemonicCodeAsString(walletConfig.getMnemonicCodes());
        mWalletSeed = walletSeed;
        mPassphrase = WalletManager.walletPassword;
        mMasterKey = HDKeyDerivation.createMasterPrivateKey(walletSeed);
        mWalletRoot = mMasterKey;
        // Add account.
        mAccounts = new ArrayList<HDAccount>();
        if( acctNums == -1 )// create case
            mAccounts.add(new HDAccount(mParams, mWalletRoot, walletConfig.getFirstAccountName(), 10));// first 10 accounts are reserved
        else { // restore case 
            for (int ii = 10; ii < acctNums + 10; ++ii) {// first 10 accounts are reserved
                String acctName = String.format("Account %d", ii);
                mAccounts.add(new HDAccount(mParams, mWalletRoot, acctName, ii));
            }
        }
    }
    
    /**
     * class constructor 
     * @param walletConfig wallet configuration data
     * @param params NetworkParameter testnet or main network
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     * @param walletNode JASON containing wallet data
     */
    public HDWallet(WalletConfig walletConfig,
                    NetworkParameters params,
                    KeyCrypter keyCrypter,
                    KeyParameter aesKey,
                    JSONObject walletNode) throws JSONException {
        
        mWalletConfig = walletConfig;
        mParams = params;
        mKeyCrypter = keyCrypter;
        mAesKey = aesKey;

        try {
            if( walletNode.has("wmnc") )
                mMnemonicCode = walletNode.getString("wmnc");
            mWalletSeed = Base58.decode(walletNode.getString("seed"));
            HDKey.EPOCH = walletNode.getLong("wepoch");
            mPassphrase = walletNode.has("passphrase") ? walletNode.getString("passphrase") : "";
        } catch (AddressFormatException e) {
            throw new RuntimeException("trouble decoding wallet");
        }
        mMasterKey = HDKeyDerivation.createMasterPrivateKey(mWalletSeed);
        mWalletRoot = mMasterKey;
        mAccounts = new ArrayList<HDAccount>();
        JSONArray accounts = walletNode.getJSONArray("accounts");
        for (int ii = 0; ii < accounts.length(); ++ii) {
            JSONObject acctNode = accounts.getJSONObject(ii);
            mAccounts.add(new HDAccount(mParams, mWalletRoot, acctNode));
        }
    }
    
    /**
     * function to save wallet data in a file
    */
    public void persist() {
        File tmpFile = new File(WalletUtil.getTempWalletFilePath(mWalletConfig));
        File newFile = new File(WalletUtil.getO3WalletFilePath(mWalletConfig));
        try {
            // Serialize wallet into a byte array.
            JSONObject jsonobj = dumps();
            String jsonstr = jsonobj.toString(4); // indentation
            byte[] plainBytes = jsonstr.getBytes(Charset.forName("UTF-8"));
            EncryptedData encryptedData = mKeyCrypter.encrypt(plainBytes, mAesKey);
            // Ready a tmp file.
            if (tmpFile.exists())
                tmpFile.delete();
            FileOutputStream ostrm = new FileOutputStream(tmpFile);
            ostrm.write(encryptedData.initialisationVector);
            ostrm.write(encryptedData.encryptedBytes);
            ostrm.close();
             if (newFile.exists())
                newFile.delete();                   
            // Swap the tmp file into place.
            if (!tmpFile.renameTo(newFile))
                logger.warn("failed to rename to " + newFile.getPath());

        } catch (JSONException ex) {
            logger.warn("failed generating JSON: " + ex.toString());
        } catch (IOException ex) {
            logger.warn("failed to write to " + tmpFile.getPath() + ": " +  ex.toString());
        } catch (DataLengthException ex) {
            logger.warn("encryption failed: " + ex.toString());
        } catch (IllegalStateException ex) {
            logger.warn("encryption failed: " + ex.toString());
        }
    }
        
    /**
     * function to dump wallet data in JASON format
     * @return JASON object representing wallet data
    */
    public JSONObject dumps() {
        try {
            JSONObject obj = new JSONObject();
            if( mMnemonicCode.length() > 0 ) 
                obj.put("wmnc", mMnemonicCode);
            obj.put("seed", Base58.encode(mWalletSeed));
            obj.put("wepoch", HDKey.EPOCH);
            obj.put("passphrase", mPassphrase);
            JSONArray accts = new JSONArray();
            for (HDAccount acct : mAccounts)
                accts.put(acct.dumps());
            obj.put("accounts", accts);
            return obj;
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * function to gather all keys in the wallet
     * @param creationTime time to set for key creation
     * @param keys list of keys gathered
    */
    public void gatherAllKeys(long creationTime, List<ECKey> keys) {
        for (HDAccount acct : mAccounts)
        {
            acct.gatherAllKeys(mKeyCrypter, mAesKey, creationTime, keys);
        }
    }
    
    /**
     * function to create an HDWallet from persisted file data.
     * @param walletConfig wallet configuration data
     * @param params NetworkParameter testnet or main network
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
    */
    public static HDWallet restore(WalletConfig walletConfig,
                                   NetworkParameters params,
                                   KeyCrypter keyCrypter,
                                   KeyParameter aesKey)
        throws InvalidCipherTextException, IOException {

        try {
            JSONObject node = deserialize(walletConfig, keyCrypter, aesKey);
            return new HDWallet(walletConfig, params, keyCrypter,aesKey, node);
        }
        catch (JSONException ex) { // marker
            String msg = "trouble deserializing wallet: " + ex.toString();
            // Have to break the message into chunks for big messages ...
            while (msg.length() > 1024) {
                String chunk = msg.substring(0, 1024);
                msg = msg.substring(1024);
            }
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * function to deserialize wallet data
     * @param walletConfig wallet configuration data
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
    */
    public static JSONObject deserialize(WalletConfig walletConfig,
                                         KeyCrypter keyCrypter,
                                         KeyParameter aesKey)
        throws IOException, InvalidCipherTextException, JSONException {

        File file = new File(WalletUtil.getO3WalletFilePath(walletConfig));
        String path = file.getPath();

        try {
            int len = (int) file.length();
            // Open persisted file.
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            byte[] iv = new byte[KeyCrypterScrypt.BLOCK_LENGTH];
            // read initilization vector
            dis.readFully(iv);
            byte[] encryptedBytes = new byte[len - iv.length];
            // read encrypted text
            dis.readFully(encryptedBytes);
            dis.close();
            EncryptedData encryptedData = new EncryptedData(iv, encryptedBytes);
            byte[] decryptedData = keyCrypter.decrypt(encryptedData, aesKey);
            JSONObject node = new JSONObject(new String(decryptedData));
            return node;

        } catch (IOException ex) {
            logger.warn("trouble reading " + path + ": " + ex.toString());
            throw ex;
        } catch (RuntimeException ex) {
            logger.warn("trouble restoring wallet: " + ex.toString());
            throw ex;
        }
    }
    
    /**
     * function to change wallet password
     * @param walletConfig wallet configuration data
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     * @param oldPassword old wallet password
     * @param newPassword new wallet password
     * @param setKeyCrypter whether set new KeyCrypter
    */
    public boolean changeO3WalletPassword(WalletConfig walletConfig,
                                         KeyCrypter oldKeyCrypter,
                                         KeyCrypter newKeyCrypter,
                                         String oldPassword,String newPassword, boolean setKeyCrypter)
        throws IOException, InvalidCipherTextException {

        File file = new File(WalletUtil.getO3WalletFilePath(walletConfig));
        String path = file.getPath();

        try {
            int len = (int) file.length();
            // Open persisted file.
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            byte[] iv = new byte[KeyCrypterScrypt.BLOCK_LENGTH];
            // read initilization vector
            dis.readFully(iv);
            byte[] encryptedBytes = new byte[len - iv.length];
            // read encrypted text
            dis.readFully(encryptedBytes);
            dis.close();
            EncryptedData encryptedData = new EncryptedData(iv, encryptedBytes);
            byte[] decryptedData = oldKeyCrypter.decrypt(encryptedData, oldKeyCrypter.deriveKey(oldPassword));
            File tmpFile = new File(WalletUtil.getTempWalletFilePath(walletConfig));
            File newFile = new File(WalletUtil.getO3WalletFilePath(walletConfig));
            EncryptedData newEncryptedData = newKeyCrypter.encrypt(decryptedData, newKeyCrypter.deriveKey(newPassword));
             if (tmpFile.exists())
                tmpFile.delete();
            FileOutputStream ostrm = new FileOutputStream(tmpFile);
            // Write the IV 
            ostrm.write(newEncryptedData.initialisationVector);
            // Write the encrypted data 
            ostrm.write(newEncryptedData.encryptedBytes);
            ostrm.close();
            if (newFile.exists())
               newFile.delete();                   
            // Swap the tmp file into place.
            if (!tmpFile.renameTo(newFile))
                logger.warn("failed to rename to " + newFile.getPath());
            if( setKeyCrypter )
            {
                mKeyCrypter = newKeyCrypter;
                mAesKey = mKeyCrypter.deriveKey(newPassword);
            }
            return true;
        } catch (Exception ex) {
            logger.warn("execption happened " + path + ": " + ex.toString());
            return false;
        }
        
    }
    
    /**
     * function to set wallet KeyCrypter
     * @param keyCrypter KeyCrypter used to encrypt the wallet
    */
    private void setKeyCrypter(KeyCrypter keyCrypter){
        this.mKeyCrypter = keyCrypter;
    }
    
    /**
     * function to ensure look ahead zone on all chains
     * @param wallet wallet for which to ensure look ahead zone
    */
    public int ensureMargins(Wallet wallet) {
        int maxAdded = 0;
        for (HDAccount acct : mAccounts) {
            int numAdded = acct.ensureMargins(wallet, mKeyCrypter, mAesKey);
            if (maxAdded < numAdded)
                maxAdded = numAdded;
        }
        return maxAdded;
    }
    
    /**
     * function to clear balance of all accounts in the HD Wallet
    */
    public void clearBalances() {
        // Clears the balance and tx counters.
        for (HDAccount acct : mAccounts)
            acct.clearBalance();
    }
    
    /**
     * function to apply all transactions in wallet to its keys
     * @param iwt wallet transactions
    */
    public void applyAllTransactions(Iterable<WalletTransaction> iwt) {
        // Clear the balance and tx counters.
        clearBalances();
        for (WalletTransaction wtx : iwt) {
            Transaction tx = wtx.getTransaction();
            boolean avail = !tx.isPending();// true if this transaction hasn't been seen in any block yet
            TransactionConfidence conf = tx.getConfidence();
            ConfidenceType ct = conf.getConfidenceType();
            // Skip dead transactions.
            if (ct != ConfidenceType.DEAD) {
                // Traverse the HDAccounts with all outputs.
                List<TransactionOutput> lto = tx.getOutputs();
                for (TransactionOutput to : lto) {
                    long value = to.getValue().longValue();// coin value
                    try {
                        byte[] pubkey = null;
                        byte[] pubkeyhash = null;
                        Script script = to.getScriptPubKey();
                        if (script.isSentToRawPubKey())
                            pubkey = script.getPubKey();
                        else
                            pubkeyhash = script.getPubKeyHash();
                        for (HDAccount hda : mAccounts)
                            hda.applyOutput(pubkey, pubkeyhash, value, avail);
                    } catch (ScriptException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Traverse the HDAccounts with all inputs.
                List<TransactionInput> lti = tx.getInputs();
                for (TransactionInput ti : lti) {
                    // Get the connected TransactionOutput to see value.
                    TransactionOutput cto = ti.getConnectedOutput();
                    if (cto == null) {
                        // It appears we land here when processing transactions
                        // where we handled the output above.
                        //
                        // mLogger.warn("couldn't find connected output for input");
                        continue;
                    }
                    long value = cto.getValue().longValue();
                    try {
                        byte[] pubkey = ti.getScriptSig().getPubKey();
                        for (HDAccount hda : mAccounts)
                            hda.applyInput(pubkey, value);
                    } catch (ScriptException e) {
                        // This happens if the input doesn't have a
                        // public key (eg P2SH).  No worries in this
                        // case, it isn't one of ours ...
                    }
                }
            }
        }

    }
    
    /**
     * function to get balance of an account in HD Wallet
     * @param acctnum account number
     * @return balance of the account
    */
    public long balanceForAccount(int acctnum) {
        // Which accounts are we considering?  (-1 means all)
        if (acctnum != -1) {
            return mAccounts.get(acctnum).balance();
        } else {
            long sum = 0;
            for (HDAccount hda : mAccounts)
                sum += hda.balance();
            return sum;
        }
    }
    
    /**
     * function to get list of all accounts in the HD Wallet
     * @return accounts list in the wallet
    */
    public List getAllAccounts() {
        return mAccounts;
    }
    
    /**
     * function to add account in HD Wallet
    */
    public void addAccount() {
        int ndx = mAccounts.size();
        String acctName = String.format("Account %d", ndx);
        mAccounts.add(new HDAccount(mParams, mWalletRoot, acctName, ndx));
    }
    
    /**
     * function to add account with name in HD Wallet 
     * @param accountName name of account
    */
    public void addAccount(String accountName) {
        int nextAccountId = getHeighestAccountId() + 1;
        mAccounts.add(new HDAccount(mParams, mWalletRoot, accountName, nextAccountId));
    }
    
    /**
     * function to get number of accounts in the wallet
     * @return number of accounts in the wallet
    */
    public int getAccountsCount(){
        return mAccounts.size();
    }
    
    /**
     * function to get last account in the wallet
     * @return last account in the wallet
    */
    public HDAccount getLastAccount(){
        return mAccounts.get(mAccounts.size()-1);
    }
    
    /**
     * function to set current account of wallet
     * @param account current account to set
    */
    public void setCurrentAccount(HDAccount account){
        mCurrentAccount = account;
    }
    
    /**
     * function to rename an account
     * @param oldAcct old account name
     * @param newAcct new account name
     * @retun whether account renamed or not
    */
    public boolean renameAccount(String oldAcct, String newAcct) {
        for (HDAccount hda : mAccounts){
            if( hda.toString().equalsIgnoreCase(oldAcct) ) {
                hda.setAccountName(newAcct);
                return true;
            }
        }
        return false;
    }
    
    /**
     * function to get max account id
     * @return  max account id
    */
    public int getHeighestAccountId() {
        int maxAccountId = 9;
        for (HDAccount hda : mAccounts){
            if( hda.getAccountId() > maxAccountId ) {
                maxAccountId = hda.getAccountId();
            }
        }
        return maxAccountId;
    }
    
    /**
     * function to get current account
     * @return current account
    */
    public HDAccount getCurrentAccount(){
        return mCurrentAccount;
    }
    
    /**
     * function to get account by account id
     * @param acctId account id
     * @return account
    */
    public HDAccount getAccount(int acctId) {
        return mAccounts.get(acctId);
    }
    
    /**
     * function to get account by account name
     * @param acctName account name
     * @return account
    */
    public HDAccount getAccount(String acctName){
        for (HDAccount hda : mAccounts){
            if( hda.toString().equalsIgnoreCase(acctName) )
                return hda;
        }
        return null;
    }
    
    /**
     * function to check the existence of an account by name
     * @param accountName account name 
     * @return whether account exists or not
    */
    public boolean isAccountExists(String accountName){
        for (HDAccount hda : mAccounts){
            if( hda.toString().equalsIgnoreCase(accountName) )
                return true;
        }
        return false;
    }
    
    /**
     * function to get Mnemonic codes of wallet
     * @return whether wallet mnemonic codes
    */
    public String getMnemonicCodes() {
        return mMnemonicCode;
    }
    
    /**
     * function that returns account name of the address
     * @param address 
     * @return account name
     */
    public String getAddressAccountName(Address address) {
        for (HDAccount hda : mAccounts){
            if( hda.hasAddress(address) )
                return hda.toString();
        }
        return "";
    }
}
