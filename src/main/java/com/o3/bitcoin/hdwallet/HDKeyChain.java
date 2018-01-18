/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.hdwallet;

import com.o3.bitcoin.model.manager.WalletManager;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.KeyCrypter;
import org.json.JSONArray;
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
 * <p> Class that implement key chain in HD Account of HD Wallet</p>
 * <ul>
 * <li>maintain keys in key chain</li>
 * <li>maintain look ahead zone</li>
 * <li>provide next unused address</li>
 * </ul>
 */
public class HDKeyChain {
    
    private static final Logger logger = LoggerFactory.getLogger(HDKeyChain.class);
    
    private NetworkParameters mParams;
    private DeterministicKey mChainKey;
    private boolean mIsReceive;
    private String mChainName;

    private ArrayList<HDKey> mAddrs;

    static private final int DESIRED_MARGIN = 100;
    static private final int MAX_UNUSED_GAP = 8;
    
     /**
     * class constructor 
     * @param params NetworkParameter testnet or main
     * @param accountKey key to create key chain
     * @param isReceive  receive or change key chain
     * @param chainName key chain name
     * @param numAddrs number of keys in key chain
     */
    public HDKeyChain(NetworkParameters params,
                   DeterministicKey accountKey,
                   boolean isReceive,
                   String chainName,
                   int numAddrs) {

        mParams = params;
        mIsReceive = isReceive;
        int chainnum = mIsReceive ? 0 : 1;
        mChainKey = HDKeyDerivation.deriveChildKey(accountKey, chainnum);
        mChainName = chainName;

        logger.info("created HDKeyChain " + mChainName + ": " + mChainKey.getPath());
        
        mAddrs = new ArrayList<HDKey>();
        for (int ii = 0; ii < numAddrs; ++ii)
            mAddrs.add(new HDKey(mParams, mChainKey, ii));
    }
    
    /**
     * class constructor 
     * @param params NetworkParameter testnet or main
     * @param accountKey key to create key chain
     * @param chainNode  JSON node containing key chain information
     */
    public HDKeyChain(NetworkParameters params,
                   DeterministicKey accountKey,
                   JSONObject chainNode)
        throws RuntimeException, JSONException {

        mParams = params;
        mChainName = chainNode.getString("name");
        mIsReceive = chainNode.getBoolean("isReceive");
        int chainnum = mIsReceive ? 0 : 1;
        mChainKey = HDKeyDerivation.deriveChildKey(accountKey, chainnum);
        logger.info("created HDChain " + mChainName + ": " + mChainKey.getPath());
        mAddrs = new ArrayList<HDKey>();
        JSONArray addrobjs = chainNode.getJSONArray("addrs");
        for (int ii = 0; ii < addrobjs.length(); ++ii) {
            JSONObject addrNode = addrobjs.getJSONObject(ii);
            mAddrs.add(new HDKey(mParams, mChainKey, addrNode));
        }
    }
    
    /**
     * function to dump the key chain information to a JASON node
     */
    public JSONObject dumps() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", mChainName);
            obj.put("isReceive", mIsReceive);
            JSONArray addrs = new JSONArray();
            for (HDKey addr : mAddrs)
                addrs.put(addr.dumps());
            obj.put("addrs", addrs);
            return obj;
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);	// Shouldn't happen.
        }
    }
    
    /**
     * function to gather all keys in key chain
     * @param keyCrypter KeyCrypter used to encrypt the wallet
     * @param aesKey AES key used to encrypt data
     * @param creationTime key creation time
     * @param keys  list of keys that are collected
     */
    public void gatherAllKeys(KeyCrypter keyCrypter,
                              KeyParameter aesKey,
                              long creationTime,
                              List<ECKey> keys) {
        for (HDKey hda : mAddrs)
            hda.gatherKey(keyCrypter, aesKey, creationTime, keys);
    }
    
    /**
     * function to return number of free keys in key chain
     * @return number of free keys in the key chain
     */
    private int marginSize() {
        int count = 0;
        ListIterator li = mAddrs.listIterator(mAddrs.size());
        while (li.hasPrevious()) {
            HDKey hda = (HDKey) li.previous();
            if (!hda.isUnused())
                return count;
            ++count;
        }
        return count;
    }
    
    /**
     * function to maintain look ahead zone of the key chain
     * @return number of keys added in the key chain
     */
    public int ensureMargins(Wallet wallet,
                             KeyCrypter keyCrypter,
                             KeyParameter aesKey) {
        // How many unused addresses do we have at the end of the chain?
        int numUnused = marginSize();
        // Do we have an right margin
        if (numUnused >= DESIRED_MARGIN) {
            return 0;
        }
        else {
            // how many addresses to add
            int numAdd = DESIRED_MARGIN - numUnused;
            // Set the new keys creation time to now.
            long now = Utils.now().getTime() / 1000;
            // Add the addresses
            int newSize = mAddrs.size() + numAdd;
            ArrayList<ECKey> keys = new ArrayList<ECKey>();
            for (int ii = mAddrs.size(); ii < newSize; ++ii) {
                HDKey hda = new HDKey(mParams, mChainKey, ii);
                mAddrs.add(hda);
                hda.gatherKey(keyCrypter, aesKey, now, keys);
            }
            wallet.importKeysAndEncrypt(keys,WalletManager.walletPassword);
            return numAdd;
        }
    }
    
    /**
     * function to clear balance of all keys in key chain
     */
    public void clearBalance() {
        for (HDKey hda : mAddrs)
            hda.clearBalance();
    }
    
    /**
     * function to credit key balance in key chain
     */
    public void applyOutput(byte[] pubkey,
                            byte[] pubkeyhash,
                            long value,
                            boolean avail) {
        for (HDKey hda : mAddrs)
            hda.applyOutput(pubkey, pubkeyhash, value, avail);
    }
    
    /**
     * function to debit key balance in key chain
     */
    public void applyInput(byte[] pubkey, long value) {
        for (HDKey hda : mAddrs)
            hda.applyInput(pubkey, value);
    }
    
    /**
     * function to get balance of all keys in key chain
     * @return key chain balance
     */
    public long balance() {
        long balance = 0;
        for (HDKey hda : mAddrs)
            balance += hda.getBalance();
        return balance;
    }
    
    /**
     * function to check existence of a key in key chain
     * @return whether key exists in key chain or not
     */
    public boolean hasPubKey(byte[] pubkey, byte[] pubkeyhash) {
        for (HDKey hda : mAddrs) {
            if (hda.isMatch(pubkey, pubkeyhash))
                return true;
        }
        return false;
    }
    
    /**
     * function to check existence of a key public address in key chain
     * @return whether key public address exists in key chain or not
     */
    public boolean hasAddress(Address address) {
        for (HDKey hda : mAddrs) {
            if(hda.isAddressMatch(address))
                return true;
        }
        return false;
    }
    
    /**
     * function to get next unused address in key chain
     * @return next unused address
     */
    public Address nextUnusedAddress() {
        for (HDKey hda : mAddrs) {
            if (hda.isUnused())
                return hda.getAddress();
        }
        throw new RuntimeException("no unused address available");
    }
    
    /**
     * function to get first key on keychain
     * @return next unused address
     */
    public ECKey getFirstReceiveKey() {
        return mAddrs.get(0).getKey();
    }
    
    /**
     * function to get first Address on keychain
     * @return Address
     */
    public Address getFirstReceiveAddress() {
        //System.out.println("_______________________________________" + mAddrs.get(0).getAddress());
        return mAddrs.get(0).getAddress();
        
    }
    
}
