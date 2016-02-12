/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model.manager;

import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.model.Config;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.util.WalletCleanupThread;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 *
 * @author
 */

/**
 * <p>Class that serialize/deserialize data as xml in file</p>
*/
public class ConfigManager {

    public static final String CONFIG_ROOT = System.getProperty("user.home") + File.separator + ".o3bcl";
    public static final String CONFIG_FILE_NAME = "o3bcl-config.xml";
    private static final String CONFIG_FILE_PATH = CONFIG_ROOT + File.separator + CONFIG_FILE_NAME;

    public static final String ALIAS_WALLET = "wallet";
    public static final String ALIAS_CONFIG = "config";

    private static ConfigManager manager;
    private Config config = new Config();
    private static boolean initialized = false;

    /**
     * class constructor  
    */
    private ConfigManager() {
    }

    /**
     * function to get class object  
    */
    public static ConfigManager get() {
        if (manager == null) {
            manager = new ConfigManager();
        }
        return manager;
    }

    /**
     * function to initialize data  
     */
    public void init() throws IOException {
        load();
        initialized = true;
        WalletCleanupThread.delete();
    }

    /**
     * function to save data to file as xml 
     */
    public void save() throws IOException {
        XStream xstream = getXStreamInstance();
        xstream.processAnnotations(WalletConfig.class);
        String xml = xstream.toXML(config);
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(xml);
        }
    }

    public void addWallet(WalletConfig wallet) {
        config.addWallet(wallet);
    }

    public WalletConfig getWallet(String id, String network) {
        return config.getWallet(id, network);
    }

    public boolean removeWallet(String id, String network) {
        return config.removeWallet(id, network);
    }

    public boolean removeWallet(WalletConfig wallet) {
        return config.removeWallet(wallet);
    }

    public WalletConfig getFirstWallet() {
        List<WalletConfig> wallets = config.getWallets(getActiveNetworkParamsString());
        if (wallets != null && !wallets.isEmpty()) {
            return wallets.get(0);
        }
        return null;
    }
    
    public List<WalletConfig> getWalletsOnAllNetworks() {
        return config.getWallets();
    }

    public List<WalletConfig> getAllWallets() {
        return config.getWallets(getActiveNetworkParamsString());
    }

    /**
     * function to load data from file  
    */
    public void load() throws IOException {
        XStream xstream = getXStreamInstance();
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            save();
        }
        config = (Config) xstream.fromXML(configFile);
    }

    public static Config config() {
        return get().config;
    }
    
    private XStream getXStreamInstance() {
        XStream xstream = new XStream();
        xstream.alias(ALIAS_CONFIG, Config.class);
        xstream.alias(ALIAS_WALLET, WalletConfig.class);
        return xstream;
    }
    
    public static NetworkParameters getActiveNetworkParams() {
        return config().getDefaultNetwork().toUpperCase().contains("MAIN") ? MainNetParams.get() : TestNet3Params.get();
    }
    
    public static String getActiveNetworkParamsString() {
        NetworkParameters params = getActiveNetworkParams();
        int network = params instanceof MainNetParams ? Config.NETOWRK_MAINNET : Config.NETWORK_TESTNET;
        return Config.networks.get(network);
    }
    
    public static String getUnActiveNetworkParamsString() {
        NetworkParameters params = getActiveNetworkParams();
        int network = params instanceof MainNetParams ? Config.NETWORK_TESTNET : Config.NETOWRK_MAINNET;
        return Config.networks.get(network);
    }
    
    public static WalletConfig getWalletConfigOnActiveNetwork(){
        List<WalletConfig> wallets = ConfigManager.config().getWallets(getActiveNetworkParamsString());
        if( wallets != null && wallets.size() > 0 )
            return wallets.get(0);
        return null;
    }
    
    public static WalletConfig getWalletConfigOnUnactiveNetwork(){
        List<WalletConfig> wallets = ConfigManager.config().getWallets(getUnActiveNetworkParamsString());
        if( wallets != null && wallets.size() > 0 )
            return wallets.get(0);
        return null;
    }
}
