/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.service;

import com.google.common.io.Files;
import com.o3.bitcoin.Application;
import com.o3.bitcoin.util.seed.SeedGeneratorUtils;
import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.hdwallet.HDKey;
import com.o3.bitcoin.hdwallet.HDWallet;
import com.o3.bitcoin.hdwallet.util.WalletUtil;
import static com.o3.bitcoin.hdwallet.util.WalletUtil.getWalletFilePath;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.progress.ProgressEvent;
import com.o3.bitcoin.ui.component.progress.DownloadProgressTracker;
import com.o3.bitcoin.ui.dialogs.DlgCreateWallet;
import com.o3.bitcoin.ui.dialogs.DlgWalletLoadingProgress;
import com.o3.bitcoin.util.ResourcesProvider;
import com.subgraph.orchid.TorClient;
import com.subgraph.orchid.TorInitializationListener;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.swing.SwingUtilities;
import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.WrongNetworkException;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *
 * @author
 */

/**
 * <p>Class that wraps WalletAppKit for the wallet implementation</p>
*/

public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    public static final int TOR_SYNC_MODE = 0;
    public static final int BITCOIN_NETWROK_SYNC_MODE = 1;

    private NetworkParameters params = ConfigManager.getActiveNetworkParams();

    private WalletAppKit appKit = null;

    private Coin balance = Coin.ZERO;
    private Address receiveAddress = null;

    private int syncMode = TOR_SYNC_MODE;
    private double networkSyncPct = -1;
    private boolean setupCompleted = false;

    private AbstractWalletEventListener walletEventListener = null;
    private final List<ActionListener> progressListeners = new ArrayList<>();

    private WalletConfig o3Wallet;

    private String loadingStatus = "Loading ...";

    private static final Object initLock = new Object();

    private boolean terminated = false;
    private boolean terminating = false;
    
    private Thread tRun = null;
    
    private HDWallet mHDWallet = null;

    /**
     * function to add block chain download progress listener  
     * @param progressListener block chain download progress listener
    */
    public void addProgressListener(ActionListener progressListener) {
        synchronized (progressListener) {
            if (!progressListeners.contains(progressListener)) {
                progressListeners.add(progressListener);
            }
        }
    }

    /**
     * function to remove block chain download progress listener  
     * @param progressListener block chain download progress listener
    */
    public void removeProgressListener(ActionListener progressListener) {
        synchronized (progressListener) {
            progressListeners.remove(progressListener);
        }
    }

    /**
     * function to remove all block chain download progress listener  
    */
    public void removeAllProgressListeners() {
        synchronized (progressListeners) {
            progressListeners.clear();
        }
    }

    /**
     * function to create wallet on WalletAppKit  
     * @param params testnet or main network
     * @param walletConfig wallet configuration data to create Wallet 
    */
    public void createOrLoadWallet(final NetworkParameters params, final WalletConfig walletConfig) {
        try {
            this.params = params;
            this.terminated = false;
            this.o3Wallet = walletConfig;
            tRun = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (initLock) {
                        String path = walletConfig.getLocation();
                        path += walletConfig.getId();
                        if (params instanceof MainNetParams) {
                            path += File.separator + "main" + File.separator;
                        } else {
                            path += File.separator + "test" + File.separator;
                        }
                        logger.info("Init app kit for wallet [{} : {} : {} : {}] ...", walletConfig.getId(), path, "None", walletConfig.getPassword() != null && !walletConfig.getPassword().isEmpty() ? "*****" : "None");
                        appKit = new WalletAppKit(params, new File(path), walletConfig.getId()) {

                            @Override
                            protected Wallet createWallet() {
                                if (walletConfig.getWatchingKey() != null) {
                                    DeterministicKey watchingKey = walletConfig.getWatchingKey();
                                    DeterministicKey key = DeterministicKey.deserializeB58(null, watchingKey.serializePubB58());
                                    long creationTime = watchingKey.getCreationTimeSeconds();
                                    return Wallet.fromWatchingKey(params, key, creationTime);
                                } else if (walletConfig.getMnemonicCodes() == null) {
                                    if (walletConfig.getWalletFile() != null) {
                                        try {
                                            Wallet wallet = Wallet.loadFromFile(walletConfig.getWalletFile());
                                            if (walletConfig.getPassword() != null && !walletConfig.getPassword().isEmpty()) {
                                                wallet.encrypt(walletConfig.getPassword());
                                            }
                                            WalletUtil.restoreHDWallet(walletConfig);
                                            return wallet;
                                        } catch (Exception e) {
                                            throw new ClientRuntimeException(e);
                                        }
                                    } else {
                                        return super.createWallet();
                                    }
                                } else {
                                    
                                    List <String> mnemonicCodes = new SeedGeneratorUtils().newSeedPhrase();
                                    DeterministicSeed seed = new DeterministicSeed(mnemonicCodes, null, "", walletConfig.getCreationDate().getTime()/1000);
                                    //DeterministicSeed seed = new DeterministicSeed(walletConfig.getMnemonicCodes(), null, "", walletConfig.getCreationDate().getTime()/1000);
                                    Wallet wallet = Wallet.fromSeed(params, seed);
                                    
                                    ////Wallet wallet = new Wallet(params);
                                    wallet.encrypt(WalletManager.walletPassword);
                                    HDKey.EPOCH = walletConfig.getCreationDate().getTime()/1000;
                                    WalletUtil.createHDWallet(walletConfig, wallet.getKeyCrypter(), wallet.getKeyCrypter().deriveKey(WalletManager.walletPassword), walletConfig.getNumberOfAccounts());
                                    return wallet;
                                }
                            }

                            @Override
                            protected void onSetupCompleted() {
                                super.onSetupCompleted();
                                setupCompleted = true;
                                long now = Utils.now().getTime() / 1000;
                                mHDWallet = WalletUtil.restoreHDWallet(walletConfig, getWallet().getKeyCrypter(), getWallet().getKeyCrypter().deriveKey(WalletManager.walletPassword));
                                ArrayList<ECKey> keys = new ArrayList<ECKey>();
                                mHDWallet.gatherAllKeys(now, keys);
                                wallet().importKeysAndEncrypt(keys,WalletManager.walletPassword);
                                //peerGroup().setFastCatchupTimeSecs(now);// marker
                                peerGroup().setFastCatchupTimeSecs(HDKey.EPOCH);
                                appKit.wallet().allowSpendingUnconfirmedTransactions();// marker
                                initBitcoinNetwork();
                            }
                        };
                        if (ConfigManager.config().isUseTor()) {
                            appKit.useTor();
                        }
                        appKit.setDownloadListener(new DownloadProgressTracker(WalletService.this));
                        appKit.setBlockingStartup(false);
                        appKit.setUserAgent(ResourcesProvider.APP_TITLE, ResourcesProvider.APP_VERSION);
                        try {
                            logger.debug("{} Waiting for appkit to start ....", walletConfig.getId());
                            Thread.sleep(2000); // just to ensure app kit starts async successfully.
                            appKit.startAsync();
                         } catch (Exception e) {
                        }
                    }
                }
            });
            tRun.start();
        } catch (Exception e) {
            logger.error("{} Error creating wallet:", walletConfig, e);
            throw e;
        }
    }
    
    /**
     * function to apply all transactions in bitconinj wallet to HD wallet with multiple accounts  
    */
    public void applyAllTransactionsToHDWallet()
    {
        Iterable<WalletTransaction> iwt = appKit.wallet().getWalletTransactions();
        mHDWallet.applyAllTransactions(iwt);
    }

    /**
     * function to set wallet events listener  
     * @param listener wallet event listener
    */
    public void setWalletEventListener(AbstractWalletEventListener listener) {
        this.walletEventListener = listener;
    }

    /**
     * function to remove wallet event listener  
    */
    public void unsetWalletEventListener() {
        this.walletEventListener = null;
    }

    /**
     * function to attach wallet events listener to wallet and start receiving onWalletChange events  
    */
    private void startWallet() {
        appKit.wallet().addEventListener(new AbstractWalletEventListener() {

            @Override
            public void onWalletChanged(Wallet wallet) {
                super.onWalletChanged(wallet);
                updateWallet(wallet);
            }
        });
        updateWallet(appKit.wallet());
    }

    /**
     * function to update wallet when wallet has some wallet event   
     * @param wallet wallet to be updated
    */
    private void updateWallet(final Wallet wallet) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                balance = appKit.wallet().getBalance();
                receiveAddress = appKit.wallet().currentReceiveAddress();
                if (walletEventListener != null && isNetworkSync()) {
                    walletEventListener.onWalletChanged(wallet);
                }
                if (isNetworkSync()) {
                    logger.info("Update Wallet :: {} > Current Address: {} :: Balance: {} BTC", o3Wallet, receiveAddress, MonetaryFormat.BTC.noCode().format(balance).toString());
                }
            }
        });
    }

    /**
     * function to get wallet configuration data   
     * @return WalletConfig object that contains wallet configuration data
    */
    public WalletConfig getWalletConfig() {
        return o3Wallet;
    }

    /**
     * function to get wallet for WalletAppKit   
     * @return wallet from WalletAppKit
    */
    public Wallet getWallet() {
        return appKit != null ? appKit.wallet() : null;
    }

    /**
     * function to initialize bitcoin network   
    */
    private void initBitcoinNetwork() {
        startWallet();
        TorClient torClient = appKit.peerGroup().getTorClient();
        if (torClient != null) {
            logger.debug("using tor client");
            final String torMsg = "Initialising Tor";
            torClient.addInitializationListener(new TorInitializationListener() {
                @Override
                public void initializationProgress(final String message, final int percent) {
                    fireProgressEvent(percent / 100.0, torMsg + " : " + message);
                }

                @Override
                public void initializationCompleted() {
                    fireProgressEvent(1.0, torMsg);
                    networkSyncPct = -1;
                    syncMode = BITCOIN_NETWROK_SYNC_MODE;
                    updateNetworkSyncPct(networkSyncPct);
                }
            });
        } else {
            networkSyncPct = -1;
            syncMode = BITCOIN_NETWROK_SYNC_MODE;
            fireProgressEvent(networkSyncPct, "Synchorinzing with bitcoin network");
        }

    }

    /**
     * function to fire block chain download progress event   
     * @param progress download progress
     * @param message download message
    */
    private void fireProgressEvent(final double progress, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadingStatus = message;
                networkSyncPct = progress;
                synchronized (progressListeners) {
                    for (ActionListener listener : progressListeners) {
                        ProgressEvent event = new ProgressEvent(appKit, syncMode, networkSyncPct, message);
                        listener.actionPerformed(event);
                    }
                }
            }
        });
    }

    public synchronized void updateNetworkSyncPct(double pct) {
        this.networkSyncPct = pct;
        fireProgressEvent(pct, "Synchronizing with bitcoin network " + (isNetworkSync() ? "(Done)" : "(this may take several minutes) ..."));
    }

    public NetworkParameters getNetworkParameters() {
        return params;
    }

    public double getNetworkSyncPct() {
        return this.networkSyncPct;
    }

    public String getLoadingStatus() {
        return this.loadingStatus;
    }

    public boolean isNetworkSync() {
        return this.syncMode == BITCOIN_NETWROK_SYNC_MODE && this.networkSyncPct >= 1.0;
    }

    public boolean isSetupcompleted() {
        return this.setupCompleted;
    }

    public void networkSyncCompleted() {
        updateNetworkSyncPct(1.0);
    }

    //public void restoreWallet(WalletConfig wallet) {
    //}

    public void changeWalletPassword(String passphrase) {
        if (passphrase == null || passphrase.isEmpty()) {
            throw new ClientRuntimeException("Passphrase is required.");
        }
        if (passphrase.length() < 5) {
            throw new ClientRuntimeException("Passphrase must be at least 5 characters long.");
        }
        if (getWallet().isEncrypted()) {
            getWallet().decrypt(passphrase);
        }
        getWallet().encrypt(passphrase);
    }
    
    /**
     * function to change password of wallet on both networks   
     * @param oldPassword wallet old password
     * @param message passphrase wallet new password
    */
    public void changeAllWalletsPassword(String oldPassword, String passphrase) throws Exception{
        if (passphrase == null || passphrase.isEmpty()) {
            throw new ClientRuntimeException("Passphrase is required.");
        }
        if (passphrase.length() < 5) {
            throw new ClientRuntimeException("Passphrase must be at least 5 characters long.");
        }
        try {
            KeyCrypter oldKeyCrypter = null, newKeyCrypter = null;
            if (getWallet().isEncrypted()) {
                oldKeyCrypter = getWallet().getKeyCrypter();
                getWallet().decrypt(oldPassword);
            }
            getWallet().encrypt(passphrase);
            newKeyCrypter = getWallet().getKeyCrypter();
            mHDWallet.changeO3WalletPassword(getWalletConfig(), oldKeyCrypter, newKeyCrypter, oldPassword, passphrase, true);
            WalletConfig walletConfig = ConfigManager.getWalletConfigOnUnactiveNetwork();
            if( walletConfig != null )
            {
                String walletFile = getWalletFilePath(walletConfig);
                Wallet wallet = Wallet.loadFromFile(new File(walletFile));
                if( wallet != null )
                {
                    if (wallet.isEncrypted()) {
                        oldKeyCrypter = wallet.getKeyCrypter();
                        wallet.decrypt(oldPassword);
                    }
                    wallet.encrypt(passphrase);
                    wallet.saveToFile(new File(walletFile));
                    newKeyCrypter = wallet.getKeyCrypter();
                    mHDWallet.changeO3WalletPassword(ConfigManager.getWalletConfigOnUnactiveNetwork(), oldKeyCrypter, newKeyCrypter,oldPassword, passphrase, false);
                }
            }
            WalletManager.walletPassword = passphrase;
        } catch (Exception ex) {
            logger.debug("Wallet password set error="+ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * function to close wallet and stop WalletAppKit   
    */
    public void closeWallet() {
        try {
            logger.info("Closing wallet [{}] ...", getWalletConfig().getId());
            unsetWalletEventListener();
            removeAllProgressListeners();
            appKit.stopAsync();
            this.terminating = true;
            this.loadingStatus = "Terminating ...";
        } catch (Exception e) {
            logger.error("Error stopping wallet ({}) service : {}", o3Wallet, e.getMessage(), e);
            throw new ClientRuntimeException(e);
        }
    }

    public void closeWallet(boolean wait) {
        try {
            logger.info("Closing wallet [{}] > [waiting]...", getWalletConfig().getId());
            unsetWalletEventListener();
            removeAllProgressListeners();
            this.terminating = true;
            this.loadingStatus = "Terminating ...";
            appKit.stopAsync().awaitTerminated();
            tRun.stop();
        } catch (Exception e) {
            logger.error("Error stopping wallet ({}) service : {}", o3Wallet, e.getMessage(), e);
            throw new ClientRuntimeException(e);
        }
    }
    
    /**
     * function to replay block chain, this will re-download the block chain   
    */
    public void replayBlockChain() {
        try {
            logger.info("Closing wallet [{}] > [waiting]...", getWalletConfig().getId());
            unsetWalletEventListener();
            removeAllProgressListeners();
            appKit.wallet().reset();
            this.terminating = true;
            this.loadingStatus = "Terminating ...";
            appKit.stopAsync().awaitTerminated();
            tRun.stop();
            try {
                WalletConfig walletConfig = ConfigManager.get().getFirstWallet();
                String spvchainFile = WalletUtil.getSPVChainFilePath(walletConfig);
                File f = new File(spvchainFile);
                if( f.exists() )
                    f.delete();
                WalletService service = WalletManager.get().reLoadWalletService(ConfigManager.getActiveNetworkParams(),walletConfig);
                if (service != null) {
                    DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(service);
                    progress.start();
                    service.applyAllTransactionsToHDWallet();
                    service.ensureLookAhead();
                    service.saveWallet();
                }
            } catch (Exception e) {
                logger.error("Error re-loading wallet: {}", e.getMessage(), e);
                System.exit(1);
            }
        } catch (Exception e) {
            logger.error("Error stopping wallet ({}) service : {}", o3Wallet, e.getMessage(), e);
            throw new ClientRuntimeException(e);
        }
    }
    
    /**
     * function to reconnect wallet with or without tor network   
    */
    public void switchTorNetwork() {
        try {
            logger.info("Closing wallet [{}] > [waiting]...", getWalletConfig().getId());
            unsetWalletEventListener();
            removeAllProgressListeners();
            this.terminating = true;
            this.loadingStatus = "Terminating ...";
            appKit.stopAsync().awaitTerminated();
            tRun.stop();
            try {
                WalletConfig walletConfig = ConfigManager.get().getFirstWallet();
                WalletService service = WalletManager.get().reLoadWalletService(ConfigManager.getActiveNetworkParams(),walletConfig);
                if (service != null) {
                    DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(service);
                    progress.start();
                    service.applyAllTransactionsToHDWallet();
                    service.ensureLookAhead();
                    service.saveWallet();
                }
            } catch (Exception e) {
                logger.error("Error re-loading wallet: {}", e.getMessage(), e);
                System.exit(1);
            }
        } catch (Exception e) {
            logger.error("Error stopping wallet ({}) service : {}", o3Wallet, e.getMessage(), e);
            throw new ClientRuntimeException(e);
        }
    }
    
    /**
     * function to reload wallet, this will close and WalletAppKit and then open it again   
    */
    public void reloadApplication() {
        try {
            logger.info("Closing wallet [{}] > [waiting]...", getWalletConfig().getId());
            unsetWalletEventListener();
            removeAllProgressListeners();
            this.terminating = true;
            this.loadingStatus = "Terminating ...";
            appKit.stopAsync().awaitTerminated();
            tRun.stop();
            try {
                WalletService service = null;
                WalletConfig wallet = ConfigManager.get().getFirstWallet();
                service = WalletManager.get().reLoadWalletService1(ConfigManager.getActiveNetworkParams(),wallet);
                if (service != null) {
                    DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(service);
                    progress.start();
                    service.applyAllTransactionsToHDWallet();
                    service.ensureLookAhead();
                    service.saveWallet();
                }
            } catch (Exception e) {
                logger.error("Error re-loading wallet: {}", e.getMessage(), e);
                System.exit(1);
            }
        } catch (Exception e) {
            logger.error("Error stopping wallet ({}) service : {}", o3Wallet, e.getMessage(), e);
            throw new ClientRuntimeException(e);
        }
    }
    
    public boolean isTerminated() {
        return this.terminated;
    }
    
    public int getNumberOfConnectedPeers() {
        return appKit.peerGroup().numConnectedPeers();
    }

    public boolean isTerminating() {
        return this.terminating;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.o3Wallet);
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
        final WalletService other = (WalletService) obj;
        if (!Objects.equals(this.o3Wallet, other.o3Wallet)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return o3Wallet != null ? o3Wallet.toString() : "n/a";
    }
    
    /**
     * function to balance of an account   
     * @param acctnum account number for which to get balance
     * @return account balance
    */
    public long balanceForAccount(int acctnum) {
        return mHDWallet.balanceForAccount(acctnum);
    }
    
    /**
     * function to get all accounts in HD Wallet   
     * @return accounts list
    */
    public List getAllAccounts()
    {
        if( mHDWallet == null )
            return null;
        return mHDWallet.getAllAccounts();
    }
    
    /**
     * function to rename an account   
     * @param oldAcct old account name
     * @param newAcct new account name
     * @return whether account renamed or not
    */
    public boolean renameAccount(String oldAcct, String newAcct) {
        if( mHDWallet != null ) {
            mHDWallet.renameAccount(oldAcct, newAcct);
            mHDWallet.persist();
            return true;
        }
        return false;
    }
    
    /**
     * function to save HD Wallet on disk   
    */
    public void saveWallet(){
        mHDWallet.persist();
    }
    
    /**
     * function to ensure look ahead zone of keys of all accounts   
    */
    public void ensureLookAhead(){
        mHDWallet.ensureMargins(appKit.wallet());
    }
    
    /**
     * function to add an account in HD Wallet  
    */
    public void addAccount() {
        mHDWallet.addAccount();
        mHDWallet.ensureMargins(appKit.wallet());
        long now = Utils.now().getTime() / 1000;
        ArrayList<ECKey> keys = new ArrayList<ECKey>();
        mHDWallet.gatherAllKeys(now, keys);// marker
        appKit.wallet().importKeysAndEncrypt(keys, WalletManager.walletPassword);
        mHDWallet.persist();
    }
    
     /**
     * function to add an account in HD Wallet by name  
    */
    public void addAccount(String accountName) {
        mHDWallet.addAccount(accountName);
        mHDWallet.ensureMargins(appKit.wallet());

        // Set the new keys creation time to now.
        long now = Utils.now().getTime() / 1000;

        ArrayList<ECKey> keys = new ArrayList<ECKey>();
        mHDWallet.gatherAllKeys(now, keys);// marker
        appKit.wallet().importKeysAndEncrypt(keys, WalletManager.walletPassword);
        mHDWallet.persist();
    }
    
    /**
     * function to get number of accounts in HD Wallet  
     * @return number of accounts in wallet
    */
    public int getHDAccountsCount(){
        return mHDWallet.getAccountsCount();
    }
    
    /**
     * function to get last account in HD Wallet  
     * @return last HDAccount object
    */
    public HDAccount getLastAccount(){
        return mHDWallet.getLastAccount();
    }
    
    /**
     * function to get max account id
     * @return  max account id
    */
    public int getHeighestAccountId(){
        return mHDWallet.getHeighestAccountId();
    }
    
    /**
     * function to set current account of wallet
     * @param account current account to set
    */
    public void setCurrentAccount(HDAccount currentAccount)
    {
        mHDWallet.setCurrentAccount(currentAccount);
    }
    
    /**
     * function to get current account
     * @return current account
    */
    public HDAccount getCurrentAccount()
    {
        return mHDWallet.getCurrentAccount();
    }
    
    /**
     * function to get account by account id
     * @param acctId account id
     * @return account
    */
    public HDAccount getAccount(int acctID){
        if (mHDWallet == null)
            return null;
        return mHDWallet.getAccount(acctID);
    }
    
    /**
     * function to get account by account name
     * @param acctName account name
     * @return account
    */
    public HDAccount getAccount(String acctName){
        if (mHDWallet == null)
            return null;
        return mHDWallet.getAccount(acctName);
    }
    
    /**
     * function to check the existence of an account by name
     * @param accountName account name 
     * @return whether account exists or not
    */
    public boolean isAccountExists( String accountName ){
        return mHDWallet.isAccountExists(accountName);
    }
    
    /**
     * function to get Mnemonic codes of wallet
     * @return whether wallet mnemonic codes
    */
    public String getMnemonicCodes(){
        return mHDWallet.getMnemonicCodes();
    }
    
    /**
     * function that returns account name of the address
     * @param address 
     * @return account name
     */
    public String getAddressAcountName(Address address) {
        return mHDWallet.getAddressAccountName(address);
    }
}
