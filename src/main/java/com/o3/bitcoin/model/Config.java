/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.model;

import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author
 */

/**
 * <p>Class to store wallets configuration</p>
*/
public class Config {

    public static final List<String> networks = new ArrayList<>(Arrays.asList("MAINNET", "TESTNET"));

    public static final int NETOWRK_MAINNET = 0;
    public static final int NETWORK_TESTNET = 1;

    private String defaultNetwork = "MAINNET";
    private boolean useTor = false;
    private boolean eulaAccepted = false;
    private List<WalletConfig> wallets = new ArrayList<>();
    private List<String> deleted = new ArrayList<>();
    private List<String> currencies = ResourcesProvider.DEFAULT_CURRENCIES;
    private String selectedCurrency = ResourcesProvider.DEFAULT_CURRENCY;
    private String selectedFeePref = ResourcesProvider.DEFAULT_FEE_PREF;
    private List<ExchangeConfig> exchanges = new ArrayList<ExchangeConfig>();

    private String encp = null;

    public Config() {
    }

    /**
     * class constructor  
     * @param wallets list of wallets configuration
     */
    public Config(List<WalletConfig> wallets) {
        this.wallets = wallets;
        if (this.wallets == null) {
            this.wallets = new ArrayList<>();
        }
    }

    /**
     * function to get configuration of all wallets  
     * @return list of wallets configuration on both networks 
     */
    public List<WalletConfig> getWallets() {
        return wallets;
    }

     /**
     * function to get wallet configuration on a specific network
     * @param network name of network
     * @return list of wallets configuration on specific networks 
     */
    public List<WalletConfig> getWallets(String network) {
        if (network == null) {
            throw new IllegalArgumentException("Network is required.");
        }
        if (networks.contains(network.toUpperCase())) {
            List<WalletConfig> filtered = new ArrayList<>();
            for (WalletConfig wallet : wallets) {
                if (wallet.getNetwork().equalsIgnoreCase(network)) {
                    filtered.add(wallet);
                }
            }
            return filtered;
        }
        throw new IllegalArgumentException("Not a valid network [" + network + "]");
    }

    /**
     * function to set wallet configuration list
     * @param network name of network
     * @return list of wallets configuration on specific networks 
     */
    public void setWallets(List<WalletConfig> wallets) {
        this.wallets = wallets;
    }

    /**
     * function to add wallet in wallets configuration list
     * @param wallet wallet configuration
     */
    public void addWallet(WalletConfig wallet) {
        if (!wallets.contains(wallet)) {
            wallets.add(wallet);
        }
    }

    /**
     * function to get a specific wallet configuration on a specific network
     * @param id wallet name
     * @param network network name
     * @return wallet configuration
    */
    public WalletConfig getWallet(String id, String network) {
        for (WalletConfig wallet : wallets) {
            if (wallet.getId().equals(id) && wallet.getNetwork().equalsIgnoreCase(network)) {
                return wallet;
            }
        }
        return null;
    }

    public boolean removeWallet(String id, String network) {
        return removeWallet(getWallet(id, network));
    }

    public boolean removeWallet(WalletConfig wallet) {
        return wallets.remove(wallet);
    }
    
    /**
     * function to add/update ExchangeConfig 
     * @param wallet wallet configuration
     */
    public void addUpdateExchange(ExchangeConfig exchange) {
        if( exchanges == null ) {
            exchanges = new ArrayList<>();
            exchanges.add(exchange);
            return;
        }
        if(exchanges.size() > 0 ) {
            if (!exchanges.contains(exchange)) {
                exchanges.add(exchange);
            }
            else {
                ExchangeConfig exchangeConfig = getExchange(exchange.getExchangeName());
                exchangeConfig.setApiKey(exchange.getApiKey());
                exchangeConfig.setApiSecret(exchange.getApiSecret());
                exchangeConfig.setCustomerID(exchange.getCustomerID());
            }
        }
        else {
            exchanges.add(exchange);
        }
    }

    /**
     * function to get a specific exchange configuration 
     * @param name exchange name
     * @return exchange configuration
    */
    public ExchangeConfig getExchange(String name) {
        if(exchanges == null ) {
            return null;
        }
        for (ExchangeConfig exchange : exchanges) {
            if (exchange.getExchangeName().equals(name)) {
                return exchange;
            }
        }
        return null;
    }

    public List<ExchangeConfig> getExchanges() {
        if( exchanges == null )
            return null;
        else
            return exchanges;
    }
    
    public List<String> getDeleted() {
        if (deleted == null) {
            deleted = new ArrayList<String>();
        }
        return deleted;
    }

    public void setDeleted(List<String> paths) {
        if (paths == null) {
            getDeleted().clear();
        }
        this.deleted = paths;
    }

    public void addDeleted(String path) {
        if (!getDeleted().contains(path)) {
            getDeleted().add(path);
        }
    }

    public boolean removeDeleted(String path) {
        return getDeleted().remove(path);
    }

    public void removeAllDeleted() {
        getDeleted().clear();
    }

    /**
     * function to get default network of the wallet
     * @param default network name
     */
    public String getDefaultNetwork() {
        return defaultNetwork;
    }

    /**
     * function to set default network of the wallet
     * @param defaultNetwork network name
     */
    public void setDefaultNetwork(String defaultNetwork) {
        if (defaultNetwork == null || defaultNetwork.isEmpty() || !networks.contains(defaultNetwork)) {
            this.defaultNetwork = "MAINNET";
        } else {
            this.defaultNetwork = defaultNetwork;
        }
    }

    public String getEncp() {
        if (encp == null || encp.isEmpty() || encp.equals(Utils.DEFAULT_APP_PASSWORD)) {
            encp = Utils.getDefaultApplicationPassword();
        }
        return encp;
    }

    public void setEncp(String encp) {
        if (encp == null || encp.isEmpty() || encp.equals(Utils.DEFAULT_APP_PASSWORD)) {
            encp = Utils.getDefaultApplicationPassword();
        }
        this.encp = encp;
    }

    /**
     * function to check whether tor is enabled or not
     * @return whether tor is enabled or not
     */
    public boolean isUseTor() {
        return useTor;
    }

    /**
     * function to set wallet use tor or not
     * @param useTor whether use tor or not
     */
    public void setUseTor(boolean useTor) {
        this.useTor = useTor;
    }
    
    /**
     * function to get if eula is accepted by client
     * @return whether eula accepted or not  
     */ 
    public boolean isEulaAccepted() {
        return this.eulaAccepted;
    }
    
    /**
     * function to set eula acceptance
     * @param eulaAccepted 
     */
    public void setEulaAccepted( boolean eulaAccepted) {
        this.eulaAccepted = eulaAccepted;
    }

    /**
     * function to get currency list
     * @return currency name list
     */
    public List<String> getCurrencies() {
        if (currencies == null || currencies.isEmpty()) {
            currencies = ResourcesProvider.DEFAULT_CURRENCIES;
        }
        return currencies;
    }
    
    /**
     * function to set currency list
     * @param currencies list of currency
     */
    public void setCurrencies(List<String> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            currencies = ResourcesProvider.DEFAULT_CURRENCIES;
        }
        this.currencies = currencies;
    }

    /**
     * function to get selected currency
     * @return selected currency
     */
    public String getSelectedCurrency() {
        if (selectedCurrency == null || selectedCurrency.isEmpty()) {
            selectedCurrency = ResourcesProvider.DEFAULT_CURRENCY;
        }
        return selectedCurrency;
    }

    /**
     * function to set selected currency
     * @param selectedCurrency currency to set as selected
     */
    public void setSelectedCurrency(String selectedCurrency) {
        if (selectedCurrency == null || selectedCurrency.isEmpty()) {
            selectedCurrency = ResourcesProvider.DEFAULT_CURRENCY;
        }
        this.selectedCurrency = selectedCurrency;
    }
    
    /**
     * function to set selected fee pref site
     * @return selected fee pref
     */
    public String getSelectedFeePref() {
        if (selectedFeePref == null || selectedFeePref.isEmpty()) {
            selectedFeePref = ResourcesProvider.DEFAULT_FEE_PREF;
        }
        return selectedFeePref;
    }
    
     /**
     * function to set selected fee pref
     * @param selectedFeePref fee pref to set as selected
     */
    public void setSelectedFeePref(String selectedFeePref) {
        if (selectedFeePref == null || selectedFeePref.isEmpty()) {
            selectedFeePref = ResourcesProvider.DEFAULT_FEE_PREF;
        }
        this.selectedFeePref = selectedFeePref;
    }

    public boolean verifyApplicationPassword(String password) {
        try {
            return Utils.verifyApplicationPassword(password);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * function to check wallet existence
     * @return whether wallet exists or not
     */
    public boolean walletsExist() {
        return wallets != null && !wallets.isEmpty();
    }

}
