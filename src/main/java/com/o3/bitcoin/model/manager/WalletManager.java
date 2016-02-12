/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model.manager;

import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.util.WalletCleanupThread;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p>Class to manager Wallets in application on both networks</p>
 * <ul>
 * <li>Create/Load Wallet Services</li>
 * </ul>
*/
public class WalletManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WalletManager.class);
    public static final String DEFAULT_WALLET_ROOT = ConfigManager.CONFIG_ROOT + File.separator + "WALLETS" + File.separator;
    private static final int MAX_WALLETS = 5;
    
    public static String walletPassword = null;
    
    private WalletService currnetWalletService = null;
    private final Map<String, WalletService> walletServices = new HashMap<>();
    private static WalletManager manager = new WalletManager();
    
    /**
     * class constructor  
    */
    private WalletManager() {
    }
    
    /**
     * function to get class object  
    */
    public static WalletManager get() {
        if (manager == null) {
            manager = new WalletManager();
        }
        return manager;
    }
    
    /**
     * function to create or load Wallet Service
     * @param NetworkParameter testnet or main
     * @param walletConfig wallet configuration data to create Wallet 
    */
    public WalletService createOrLoadWalletService(final NetworkParameters params, final WalletConfig walletConfig) {
        synchronized (walletServices) {
            if (!walletServices.isEmpty() && walletServices.size() >= MAX_WALLETS) {
                throw new ClientRuntimeException("You've reached maximum wallets limit [" + MAX_WALLETS + "].");
            }
        }
        currnetWalletService = new WalletService();
        if (walletConfig.getLocation() == null) {
            walletConfig.setLocation(DEFAULT_WALLET_ROOT);
        }
        if (walletConfig.getLocation() != null && !walletConfig.getLocation().endsWith(File.separator)) {
            walletConfig.setLocation(walletConfig.getLocation() + File.separator);
        }
        currnetWalletService.createOrLoadWallet(params, walletConfig);
        synchronized (walletServices) {
            walletServices.clear();
            walletServices.put(walletConfig.getId(), currnetWalletService);
        }
        ConfigManager.get().addWallet(walletConfig);
        try {
            ConfigManager.get().save();
        } catch (Exception e) {
            throw new ClientRuntimeException(e);
        }
        return currnetWalletService;
    }
    
    /**
     * function to reload Wallet Service
     * @param NetworkParameter testnet or main
     * @param walletConfig wallet configuration data to reload wallet 
    */
    public WalletService reLoadWalletService(final NetworkParameters params, WalletConfig walletConfig) {
        currnetWalletService = new WalletService();
        currnetWalletService.createOrLoadWallet(params, walletConfig);
        synchronized (walletServices) {
            walletServices.remove(walletConfig.getId());
            walletServices.put(walletConfig.getId(), currnetWalletService);
        }
        return currnetWalletService;
    }
    
    
    public WalletService reLoadWalletService1(final NetworkParameters params, WalletConfig walletConfig) {
        currnetWalletService = new WalletService();
        currnetWalletService.createOrLoadWallet(params, walletConfig);
        synchronized (walletServices) {
            walletServices.clear();
            walletServices.put(walletConfig.getId(), currnetWalletService);
        }
        return currnetWalletService;
    }
    
    public boolean isWalletExists(WalletConfig wallet) {
        return new File(wallet.getLocation() + File.separator + wallet.getId()).exists();
    }
    
    public WalletService getAnyWalletService() {
        if (walletServices != null && !walletServices.isEmpty()) {
            return new ArrayList<>(walletServices.values()).get(0);
        }
        return null;
    }
    
    public List<WalletService> getAllWalletServices() {
        return new ArrayList<>(walletServices.values());
    }
    
    public WalletService getWalletService(String id) {
        return walletServices.get(id);
    }
    
    public boolean isUniqueWalletId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Wallet Id is required.");
        }
        if (walletServices != null) {
            for (WalletService service : walletServices.values()) {
                WalletConfig config = service.getWalletConfig();
                if (config.getId().equalsIgnoreCase(id) && config.getNetwork().equals(ConfigManager.getActiveNetworkParamsString())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isWalletLocationMarkedForDeletion(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Wallet location is required.");
        }
        for (String path : ConfigManager.config().getDeleted()) {
            if (path.contains(location)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isWalletLocationAvailable(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Wallet location is required.");
        }
        if (walletServices != null) {
            for (WalletService service : walletServices.values()) {
                String path = service.getWalletConfig().getLocation();
                if (!path.endsWith(File.separator)) {
                    path += File.separator;
                }
                path += service.getWalletConfig().getId();
                if (path.contains(location)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void removeWalletService(String id) {
        WalletService service;
        synchronized (walletServices) {
            service = getWalletService(id);
            if (service != null) {
                try {
                    String path = service.getWalletConfig().getLocation() + File.separator + service.getWalletConfig().getId();
                    ConfigManager.config().removeWallet(service.getWalletConfig());
                    ConfigManager.config().addDeleted(path);
                    ConfigManager.get().save();
                    walletServices.remove(id);
                    new WalletCleanupThread().deleteWallet(service);
                } catch (Exception e) {
                    throw new ClientRuntimeException(e);
                }
            }
        }
    }
    
    /**
     * function to get active Wallet Service
     * @return current active Wallet Service
    */
    public WalletService getCurentWalletService() {
        return currnetWalletService;
    }
    
    public void setCurrentWalletService(WalletService walletService) {
        this.currnetWalletService = walletService;
    }
    
    public boolean isEmpty() {
        return walletServices == null || walletServices.isEmpty();
    }
 
    public void clearWalletServices() {
        walletServices.clear();
    }
}
