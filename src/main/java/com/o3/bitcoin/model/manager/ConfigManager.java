/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model.manager;

import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.model.Config;
import com.o3.bitcoin.model.ExchangeConfig;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.util.Utils;
import com.o3.bitcoin.util.WalletCleanupThread;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p>Class that serialize/deserialize data as xml in file</p>
*/
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    public static final String CONFIG_ROOT = System.getProperty("user.home") + File.separator + ".o3bcl";
    public static final String CONFIG_FILE_NAME = "o3bcl-config.xml";
    private static final String CONFIG_FILE_PATH = CONFIG_ROOT + File.separator + CONFIG_FILE_NAME;

    public static final String ALIAS_WALLET = "wallet";
    public static final String ALIAS_CONFIG = "config";
    public static final String ALIAS_EXCHANGE = "exchange";

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
        xstream.alias(ALIAS_EXCHANGE, ExchangeConfig.class);
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
    
    public boolean reEncryptExchangesConfig(KeyCrypter oldKeyCrypter, String oldPasspharse, KeyCrypter newKeyCrypter, String newPasspharse) {
        try {
            List<ExchangeConfig> exchanges = config.getExchanges();
            if( exchanges != null ) {
                for(ExchangeConfig exchange : exchanges) {
                    exchange.setApiKey(Utils.encryptData(newKeyCrypter, newPasspharse, Utils.decryptData(oldKeyCrypter, oldPasspharse, exchange.getApiKey())));
                    exchange.setApiSecret(Utils.encryptData(newKeyCrypter, newPasspharse, Utils.decryptData(oldKeyCrypter, oldPasspharse, exchange.getApiSecret())));
                }
                save();
            }
            return true;
        }catch(Exception ex) {
            logger.error("Exchange config Re-Encrypt Exchanges Config error: {}", ex.getMessage());
            System.out.println("Exchange config Re-Encrypt Exchanges Config error: "+ ex.getMessage());
        }
        return false;
    }
}
