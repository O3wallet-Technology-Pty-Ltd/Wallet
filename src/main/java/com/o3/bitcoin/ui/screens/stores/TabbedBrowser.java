/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.stores;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.dialogs.DlgNewPayment;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
//Class that implements the JTabPane with close functionality
public class TabbedBrowser extends JWebBrowser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TabbedBrowser.class);
    
    private JTabbedPane tabbedPane;
    private TabbedBrowserAdapter actionListener;
    private WalletService currentService = null;
    boolean listenerActivated;
    private TabbedBrowser browser = null;
    private String tabTemporaryTitle="Opening New Tab...";
    private boolean paymentInProgress = false;
    
    TabbedBrowser(String url, JTabbedPane tPane,String title,  WalletService service) {
        currentService = service;
        tabbedPane = tPane;
        listenerActivated = false;
        actionListener = new TabbedBrowserAdapter();
        this.addWebBrowserListener(actionListener);
        this.setBarsVisible(false);
        this.setStatusBarVisible(false);
        this.navigate(url);
        tabbedPane.addTab(title, this);
        this.setDefaultPopupMenuRegistered(false);
    }
    
    public class TabbedBrowserAdapter extends WebBrowserAdapter {

        @Override
        public void locationChanged(WebBrowserNavigationEvent e) {
              listenerActivated = true;
        }

        @Override
        public void locationChanging(WebBrowserNavigationEvent e) {
            if( paymentInProgress ) {
                System.out.println("Payment In Progress");
                e.consume();
                return;
            }
            if (listenerActivated) {
                final String newResourceLocation = e.getNewResourceLocation();

                if (newResourceLocation.startsWith("bitcoin:")) {
                    paymentInProgress = true;
                    e.consume();
                    try {
                        BitcoinURI bcuri = new BitcoinURI(newResourceLocation);
                        if( bcuri.getAddress() != null && bcuri.getAmount() != null ) {
                            DlgNewPayment dlgNewPayment = new DlgNewPayment(WalletManager.get().getCurentWalletService());
                            dlgNewPayment.centerOnScreen();
                            dlgNewPayment.setReceiveAddress(bcuri.getAddress().toString());
                            dlgNewPayment.setAmount(bcuri.getAmount().toString());
                            dlgNewPayment.setVisible(true);
                        }
                        else
                        {
                            YesNoDialog dialog = new YesNoDialog("Error","Bad Bitcoin URI ("+newResourceLocation+")", false);
                            dialog.start();
                        }
                    } catch (BitcoinURIParseException ex) {
                        logger.error("Error occured while Parsing Bitcoin URI: {}", ex.getMessage(), ex);
                        YesNoDialog dialog = new YesNoDialog("Error","BitcoinURIParseException ("+ex.getMessage()+")", false);
                        dialog.start();
                    }
                    paymentInProgress = false;

                } else if (newResourceLocation.startsWith("http")) {
                    e.consume();
                    String title;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {

                            final String title = newResourceLocation;
                            OneTabbBrowser webBrowser = new OneTabbBrowser(newResourceLocation, tabbedPane,  currentService);
                        }
                    });
                }
            }
        }

        @Override
        public void windowWillOpen(WebBrowserWindowWillOpenEvent e) {
            // We let the window to be created, but we will check the first location that is set on it.
            e.getNewWebBrowser().addWebBrowserListener(new WebBrowserAdapter() {

                @Override
                public void locationChanging(WebBrowserNavigationEvent e) {
                    final String newResourceLocation = e.getNewResourceLocation();
                    if (newResourceLocation.startsWith("http")) {
                        e.consume();
                        final JWebBrowser webB =e.getWebBrowser();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                final String title = newResourceLocation;
                                OneTabbBrowser webBrowser = new OneTabbBrowser(newResourceLocation, tabbedPane,  currentService);
                                webB.getWebBrowserWindow().dispose();
                            }
                        });
                    }
                }
            });
        }
    }
}
