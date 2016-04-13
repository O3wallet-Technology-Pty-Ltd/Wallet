/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin;

//import chrriis.dj.nativeswing.NativeSwing;
//import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.dialogs.DlgCreateWallet;
import com.o3.bitcoin.ui.dialogs.DlgWalletLoadingProgress;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.ui.dialogs.DlgEula;
import com.o3.bitcoin.ui.dialogs.DlgLogin;
import java.util.ArrayList;
import com.o3.bitcoin.util.seed.SeedGeneratorUtils;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Address;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
/**
 * <p> Class that contains main function, entry point for the application</p>
 * <ul>
 * <li>Show Create Wallet Dialog or Login Dialog</li>
 * <li>Load wallet configuration file</li>
 * </ul>
 *
*/

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static final String SUN_JAVA_COMMAND = "sun.java.command";
    private static boolean loggedIn = false;
    public static boolean appLoaded = false;
    private static String args[];
    private static WalletService  mWalletService = null;
    
    public static void main(String args[]) {
        //NativeSwing.initialize();
        //NativeInterface.open();
        Application.args = args;
        final ConfigManager manager = ConfigManager.get();
        try {
            manager.init();
        } catch (IOException e) {
            logger.error("FATAL ERROR: ", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "FATAL ERROR: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
                ApplicationUI ui = ApplicationUI.get();
                List<Image> icons = new ArrayList<Image>();
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_24x24.png").getImage());
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_32x32.png").getImage());
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_48x48.png").getImage());
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_64x64.png").getImage());
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_96x96.png").getImage());
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_128x128.png").getImage());
                icons.add(com.o3.bitcoin.util.Utils.loadClassPathIcon("icons/o3_256x256.png").getImage());
                ui.setIconImages(icons);
                ui.setTitle("o3Wallet");
                try {
                    eula();
                    login();
                    WalletConfig wallet = manager.getFirstWallet();
                    if (wallet == null) {
                        try {
                            DlgCreateWallet dlgCreateWallet = new DlgCreateWallet();
                            dlgCreateWallet.centerOnScreen();
                            dlgCreateWallet.setVisible(true);
                            mWalletService = dlgCreateWallet.getWalletService();
                            if (mWalletService == null) {
                                logger.debug("No wallet created. Application will exit.");
                                System.exit(1);
                            }
                            ConfigManager.config().setDefaultNetwork(mWalletService.getNetworkParameters() instanceof MainNetParams ? "MAINNET" : "TESTNET");
                            ConfigManager.get().save();
                            DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(mWalletService);
                            progress.start();
                        } catch (Exception e) {
                            ui.showError("Error creating Wallet: {}", e.getMessage());
                            logger.error("Error creating wallet: {}", e.getMessage(), e);
                            System.exit(1);
                        }
                    } else {
                        for (WalletConfig _wallet : manager.getAllWallets()) {
                            try {
                                mWalletService = WalletManager.get().createOrLoadWalletService(ConfigManager.getActiveNetworkParams(), _wallet);
                            } catch (Exception e) {
                                ui.showError("Error loading wallet (" + _wallet.getId() + ") : " + e.getMessage());
                                logger.error("Error loading wallet: {}", e.getMessage(), e);
                                System.exit(1);;
                            }
                        }
                        if (mWalletService != null) {
                            DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(mWalletService);
                            progress.start();
                            mWalletService.applyAllTransactionsToHDWallet();
                            mWalletService.ensureLookAhead();
                            mWalletService.saveWallet();
                        }
                    }
                } catch (Exception e) {
                    logger.error("ERROR: {}", e.getMessage(), e);
                    ui.showError("Error loading wallet : " + e.getMessage());
                    System.exit(1);
                }
                appLoaded = true;
                ApplicationUI.get().restore();
                ApplicationUI.get().setLocationRelativeTo(null);
                ApplicationUI.get().setVisible(true);
            }
        });
        /*NativeInterface.runEventPump();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        @Override
	        public void run() {
	            NativeInterface.close();
	        }
	    }));*/
    }

    /**
     * Sets user login status 
     *
     * @param loggedIn current user login status
     */
    public static void setLoggedIn(boolean loggedIn) {
        Application.loggedIn = loggedIn;
    }
    
    /**
     * function to show eula dialog to user
     */
    private static void eula() {
        if(!ConfigManager.config().isEulaAccepted()) {
            DlgEula dlgEula = new DlgEula();
            dlgEula.centerOnScreen();
            dlgEula.setVisible(true);
            if( !DlgEula.eulaAccepted )
                System.exit(1);
            else {
                ConfigManager.config().setEulaAccepted(true);
                try {
                    ConfigManager.get().save();
                }catch(Exception e) {
                    logger.debug("Exception="+e.getMessage());
                }
            }
        }
    }
    
    /**
     * Returns current user login status 
     */
    public static boolean isLoggedIn() {
        return Application.loggedIn;
    }

    /**
     * Shows login dialog to user 
     * @param loggedIn current user login status
     */
    private static void login() {
        try {
            DlgLogin dialog = new DlgLogin((ConfigManager.get().getWalletsOnAllNetworks().size() > 0) ? false : true);
            dialog.centerOnScreen(450, 250);
            dialog.setVisible(true);
            if (!isLoggedIn()) {
                if (!dialog.isClosed()) {
                    ApplicationUI.get().showError("Incorrect Password.");
                    login();
                } else {
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            ApplicationUI.get().showError(e.getMessage() + "\nApplication will exit.");
            System.exit(1);
        }
    }
}
