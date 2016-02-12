/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.o3.bitcoin.hdwallet;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.AllowUnconfirmedCoinSelector;
import org.bitcoinj.wallet.CoinSelection;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.DefaultCoinSelector;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * @author
 */

/**
    * <p> Class that implements HD Account of HD Wallet</p>
    * <ul>
    * <li>maintain HD account information</li>
    * <li>maintain receive key chain</li>
    * <li>maintain change key chain</li>
    * </ul>
    *
*/
public class HDAccount {
    
    private static final Logger logger = LoggerFactory.getLogger(HDAccount.class);
    
    private NetworkParameters mParams;
    private DeterministicKey mAccountKey;
    private String mAccountName;
    private int mAccountId;

    private HDKeyChain mReceiveChain;
    private HDKeyChain mChangeChain;
    
     /**
     * class constructor 
     * @param params NetworkParameter testnet or main
     * @param masterkey Master Key of the account
     * @param accountName  Name of the account
     * @param acctnum   Account number of HD Wallet
     */
    public HDAccount(NetworkParameters params,
                     DeterministicKey masterKey,
                     String accountName,
                     int acctnum) {

        mParams = params;
        int childnum = acctnum;
        mAccountKey = HDKeyDerivation.deriveChildKey(masterKey, childnum);
        mAccountName = accountName;
        mAccountId = acctnum;
        mReceiveChain = new HDKeyChain(mParams, mAccountKey, true, "Receive", 100);
        mChangeChain = new HDKeyChain(mParams, mAccountKey, false, "Change", 100);
    }
    
    /**
     * class constructor 
     * @param params NetworkParameter testnet or main
     * @param masterkey Master Key of the account
     * @param acctNode  Jason node that contains account information
     */
    public HDAccount(NetworkParameters params,
                     DeterministicKey masterKey,
                     JSONObject acctNode)
        throws RuntimeException, JSONException {
        mParams = params;
        mAccountName = acctNode.getString("name");
        mAccountId = acctNode.getInt("id");
        int childnum = mAccountId;
        mAccountKey = HDKeyDerivation.deriveChildKey(masterKey, childnum);
        logger.info("created HDAccount " + mAccountName + ": " +
        mAccountKey.getPath());
        mReceiveChain =  new HDKeyChain(mParams, mAccountKey, acctNode.getJSONObject("receive"));
        mChangeChain = new HDKeyChain(mParams, mAccountKey,acctNode.getJSONObject("change"));
    }
    
    /**
     * function to dump the account information to a Jason node
    */
    public JSONObject dumps() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", mAccountName);
            obj.put("id", mAccountId);
            obj.put("receive", mReceiveChain.dumps());
            obj.put("change", mChangeChain.dumps());
            return obj;
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * function to gather all keys of the account 
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     * @param creationTime  to set creation time of keys
     * @param keys gathered account keys
     */
    public void gatherAllKeys(KeyCrypter keyCrypter,
                              KeyParameter aesKey,
                              long creationTime,
                              List<ECKey> keys) {
        mReceiveChain.gatherAllKeys(keyCrypter, aesKey, creationTime, keys);
        mChangeChain.gatherAllKeys(keyCrypter, aesKey, creationTime, keys);
    }
    
     /**
     * function to ensure look ahead zone
     * @param wallet  HD Wallet
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     */
    public int ensureMargins(Wallet wallet,
                              KeyCrypter keyCrypter,
                              KeyParameter aesKey) {
        int receiveAdded = mReceiveChain.ensureMargins(wallet, keyCrypter, aesKey);
        int changeAdded = mChangeChain.ensureMargins(wallet, keyCrypter, aesKey);
        return (receiveAdded > changeAdded) ? receiveAdded : changeAdded;
    }
    
    /**
     * function to clear account balance
    */
    public void clearBalance() {
        mReceiveChain.clearBalance();
        mChangeChain.clearBalance();
    }
    
    /**
     * function to get account id
     * @return account id
    */
    public int getAccountId() {
        return mAccountId;
    }
    
    /**
     * function to apply output to account key chains
     * @param pubkey public key to apply output
     * @param pubkeyhash public key hash to apply output
     * @parma value output value
     * @param avail whether pending or not
     */
    public void applyOutput(byte[] pubkey,
                            byte[] pubkeyhash,
                            long value,
                            boolean avail) {
        mReceiveChain.applyOutput(pubkey, pubkeyhash, value, avail);
        mChangeChain.applyOutput(pubkey, pubkeyhash, value, avail);
    }
    
    /**
     * function to apply input to account key chains
     * @param pubkey public key to apply input
     * @parma value output value
     */
    public void applyInput(byte[] pubkey, long value) {
        mReceiveChain.applyInput(pubkey, value);
        mChangeChain.applyInput(pubkey, value);
    }
    
    /**
     * function to get account balance
     * @return account balance
    */
    public long balance() {
        long balance = 0;
        balance += mReceiveChain.balance();
        balance += mChangeChain.balance();
        return balance;
    }
    
    @Override
    public String toString(){
        return mAccountName;
    }
    
    /**
     * function to check an address exists in account or not
     * @return address existence
    */
    public boolean hasAddress(Address address) {
        if (mReceiveChain.hasAddress(address))
            return true;
        return mChangeChain.hasAddress(address);
    }
    
    /**
     * function to get account path in hd wallet hierarchy
     * @return account path
    */
    public String getAccountPath() {
        return mAccountKey.getPathAsString();
    }
    
    /**
     * function to check whether a public key exists in account or not
     * @param pubkey  public key bytes
     * @param pubkeyhas  public key hash
     * @return public key existence in account
    */
    public boolean hasPubKey(byte[] pubkey, byte[] pubkeyhash) {
        if (mReceiveChain.hasPubKey(pubkey, pubkeyhash))
            return true;
        return mChangeChain.hasPubKey(pubkey, pubkeyhash);
    }
    
    /**
     * function to get next receive address of the account
     * @return next receive address
    */
    public Address nextReceiveAddress() {
        return mReceiveChain.nextUnusedAddress();
    }

    /**
     * function to get next change address of the account
     * @return next change address
    */
    public Address nextChangeAddress() {
        return mChangeChain.nextUnusedAddress();
    }
    
    /**
     * function to set account name
     * @return acctName name of account
    */
    public void setAccountName(String acctName) {
        this.mAccountName = acctName;
    }
    
    /**
     * function to get coins from account
     * @param spendUnconfirmed whether spend unconfirmed coins or not
     * @return CointSelector selected coins
    */
    public CoinSelector coinSelector(boolean spendUnconfirmed) {
        return new AccountCoinSelector(spendUnconfirmed);
    }

    /**
    * <p> Class that implements a CoinSelector for the account</p>
    */
    public class AccountCoinSelector implements CoinSelector {

        private DefaultCoinSelector mDefaultCoinSelector;

        public AccountCoinSelector(boolean spendUnconfirmed) {
            mDefaultCoinSelector = spendUnconfirmed ?
                new AllowUnconfirmedCoinSelector() :
                new DefaultCoinSelector();
        }

        public CoinSelection select(Coin biTarget, List<TransactionOutput> candidates) {
            // Filter the candidates so only coins from this account
            // are considered.  Let the Wallet.DefaultCoinSelector do
            // all the remaining work.
            LinkedList<TransactionOutput> filtered = new LinkedList<TransactionOutput>();
            for (TransactionOutput to : candidates) {
                try {
                    byte[] pubkey = null;
                    byte[] pubkeyhash = null;
                    Script script = to.getScriptPubKey();
                    if (script.isSentToRawPubKey())
                        pubkey = script.getPubKey();
                    else
                        pubkeyhash = script.getPubKeyHash();

                    if (mReceiveChain.hasPubKey(pubkey, pubkeyhash))
                    {
                        filtered.add(to);
                    }
                    else if (mChangeChain.hasPubKey(pubkey, pubkeyhash))
                    {
                        filtered.add(to);
                    }
                    else
                        // Not this account
                        continue;

				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            // Does all the real work ...
            return mDefaultCoinSelector.select(biTarget, filtered);
        }
    }
    
}
