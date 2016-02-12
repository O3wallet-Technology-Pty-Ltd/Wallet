/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.hdwallet;

import java.util.Arrays;
import java.util.List;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.KeyCrypter;
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
 * <p> Class that implement a Key in Account's key chain of HD Wallet</p>
 * <ul>
 * <li>maintain key</li>
 * <li>maintain key balance</li>
 * <li>apply transaction to key</li>
 * </ul>
 *
 */
public class HDKey {
    
    private static final Logger logger = LoggerFactory.getLogger(HDKey.class);
    
    public static long EPOCH = (Utils.now().getTime() / 1000) - (6 * 60 * 60);

    private NetworkParameters mParams;
    private int mAddrNum;
    private String mPath;
    private byte[] mPrvBytes;
    private byte[] mPubBytes;
    private ECKey mECKey;
    private byte[] mPubKey;
    private byte[] mPubKeyHash;
    private Address mAddress;

    private int mNumTrans;
    private long mBalance;
    private long mAvailable;

    /**
     * class constructor 
     * @param params NetworkParameter testnet or main
     * @param chainKey chain key to create keys
     * @param addrnum  key number in key chain
     */
    public HDKey(NetworkParameters params, DeterministicKey chainKey, int addrnum) {

        mParams = params;
        mAddrNum = addrnum;

        DeterministicKey dk = HDKeyDerivation.deriveChildKey(chainKey, addrnum);
        mPath = dk.getPath().toString();// marker

        // Derive ECKey.
        mPrvBytes = dk.getPrivKeyBytes();
        mPubBytes = dk.getPubKey(); // Expensive, save.
        mECKey = new ECKey(mPrvBytes, mPubBytes);

        // Set creation time to now.
        long now = Utils.now().getTime() / 1000;
        mECKey.setCreationTimeSeconds(now);

        // Derive public key, public hash and address.
        mPubKey = mECKey.getPubKey();
        mPubKeyHash = mECKey.getPubKeyHash();
        mAddress = mECKey.toAddress(mParams);

        // Initialize transaction count and balance.
        mNumTrans = 0;
        mBalance = 0;
        mAvailable = 0;

        //logger.info("created address " + mPath + ": " + mAddress.toString());
    }
    
    /**
     * class constructor 
     * @param params NetworkParameter testnet or main
     * @param chainKey chain key to create keys
     * @param addrNode JSON node containing key information
     * throws RuntimeException, JSONException
     */
    public HDKey(NetworkParameters params,
                     DeterministicKey chainKey,
                     JSONObject addrNode)
        throws RuntimeException, JSONException {

        mParams = params;

        mAddrNum = addrNode.getInt("addrNum");

        // If our persisted state doesn't have the path or prvBytes
        // we'll need to use the expensive operation to derive them.
        // We'll persist them going forward so we can do the faster
        // deserialization.
        //
        if (!addrNode.has("path") || !addrNode.has("prvBytes")) {

            DeterministicKey dk = HDKeyDerivation.deriveChildKey(chainKey, mAddrNum);

            // Derive ECKey.
            mPrvBytes = dk.getPrivKeyBytes();
            mPath = dk.getPath().toString();// marker
        }
        else {
            try {
                mPrvBytes = Base58.decode(addrNode.getString("prvBytes"));
            } catch (AddressFormatException ex) {
                throw new RuntimeException("failed to decode prvBytes");
            }
            mPath = addrNode.getString("path");
        }

        try {
            mPubBytes = Base58.decode(addrNode.getString("pubBytes"));
        } catch (AddressFormatException ex) {
            throw new RuntimeException("failed to decode pubBytes");
        }
        
        mECKey = new ECKey(mPrvBytes, mPubBytes);

        // Set creation time to o3wallet epoch.
        mECKey.setCreationTimeSeconds(EPOCH);

        // Derive public key, public hash and address.
        mPubKey = mECKey.getPubKey();
        mPubKeyHash = mECKey.getPubKeyHash();
        mAddress = mECKey.toAddress(mParams);

        // Initialize transaction count and balance.  If we don't have
        // a persisted available amount, presume it is all available.
        mNumTrans = addrNode.getInt("numTrans");
        mBalance = addrNode.getLong("balance");
        mAvailable = addrNode.has("available") ? addrNode.getLong("available") : mBalance;
        //logger.info("read address " + mPath + ": " + mAddress.toString() + " mNumTrans="+mNumTrans+" mBalance="+mBalance+" mAvailable="+mAvailable);
    }
    
    /**
     * function to dump the key information to a Jason node
     */
    public JSONObject dumps() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("addrNum", mAddrNum);
            obj.put("path", mPath);
            obj.put("prvBytes", Base58.encode(mPrvBytes));
            obj.put("pubBytes", Base58.encode(mPubBytes));
            obj.put("numTrans", mNumTrans);
            obj.put("balance", mBalance);
            obj.put("available", mAvailable);
            return obj;
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * function to add key in keys list
     */
    public void gatherKey(KeyCrypter keyCrypter,
                          KeyParameter aesKey,
                          long creationTime,
                          List<ECKey> keys) {
        mECKey.setCreationTimeSeconds(creationTime);
        keys.add(mECKey);
    }
    
    /**
     * function to clear key balance
     */
    public void clearBalance() {
        mNumTrans = 0;
        mBalance = 0;
        mAvailable = 0;
    }
    
    /**
     * function to add balance to key 
     * @param pubkey public key to apply output
     * @param pubkeyhash public key hash to apply output
     * @parma value balance to add to key
     */
    public void applyOutput(byte[] pubkey,
                            byte[] pubkeyhash,
                            long value,
                            boolean avail) {

        // Does this output apply to this address?
        if (!isMatch(pubkey, pubkeyhash))
            return;
        ++mNumTrans;
        mBalance += value;
        if (avail)
            mAvailable += value;
        //logger.info("apply output address " + mPath + ": " + mAddress.toString() + " value="+value+" balance="+mBalance);
    }
    
    /**
     * function to match key
     * @param pubkey public key to apply output
     * @param pubkeyhash public key hash to apply output
     * @return whether key matched or not
     */
    public boolean isMatch(byte[] pubkey, byte[] pubkeyhash) {
        if (pubkey != null)
            return Arrays.equals(pubkey, mPubKey);
        else if (pubkeyhash != null)
            return Arrays.equals(pubkeyhash, mPubKeyHash);
        else
            return false;
    }
    
     /**
     * function to match address
     * @param address address to match
     * @return whether address matched or not
     */
    public boolean isAddressMatch(Address address) {
        if (address != null)
        {
            if(mAddress.equals(address) )
                return true;
        }
            return false;
    }
    
    /**
     * function subtract balance from key
     * @param pubkey public key to apply output
     * @param value balance to subtract from key
     */
    public void applyInput(byte[] pubkey, long value) {
        // Does this input apply to this address?
        if (!Arrays.equals(pubkey, mPubKey))
            return;
        ++mNumTrans;
        mBalance -= value;
        mAvailable -= value;
    }
    
    /**
     * function to check availability of key
     * @return whether key has transactions or not
     */
    public boolean isUnused() {
        return mNumTrans == 0;
    }
    
    /**
     * function to get key balance
     * @return key balance
     */
    public long getBalance() {
        return mBalance;
    }
    
    /**
     * function to get public address associated with key
     * @return key address
     */
    public Address getAddress() {
        return mAddress;
    }
}
