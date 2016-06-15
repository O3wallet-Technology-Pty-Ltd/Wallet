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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
//Class that implements the JTabPane with close functionality
public class OneTabbBrowser extends JWebBrowser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TabbedBrowser.class);
    private static Integer nameCounter=0;
    private JTabbedPane tabbedPane;
    private TabbedBrowserAdapter actionListener;
    private WalletService currentService = null;
    boolean listenerActivated;
    private OneTabbBrowser browser = null;
    private String tabTemporaryTitle="Opening New Tab...";
    private int mainIndex=-1;
    private String titleText="";
    private boolean paymentInProgress = false;
    private JPanel pnlTab=null;
    private JLabel lblClose=null;
    private JLabel lblLoading=null;
    private JLabel lblTitle = null;
    private GridBagConstraints gbc=null;
    private int timerDelay = 4000;//miliseconds
    private ActionListener taskPerformer=null;
    private Timer timer=null;
    private boolean inChanged=false;
    
    OneTabbBrowser(String url, JTabbedPane tPane,  WalletService service) {
        currentService = service;
        tabbedPane = tPane;
        listenerActivated = false;
        actionListener = new TabbedBrowserAdapter();
        this.addWebBrowserListener(actionListener);
        this.setBarsVisible(false);
        this.setStatusBarVisible(false);
        this.navigate(url);
        browser = this;
    
        nameCounter++;
        if(nameCounter==100){
            nameCounter=1;
        }
        addTimerListener();
        
        titleText = tabTemporaryTitle+nameCounter.toString();
        tabbedPane.addTab(titleText, this);
        mainIndex = tabbedPane.indexOfTab(titleText);
        addTabTitle(titleText);
        this.setDefaultPopupMenuRegistered(false);
    }

 public void addTimerListener() {
     taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(inChanged==true){
                  lblLoading.setIcon(null);
                }
            }
        };
 }
  

    
    public void addTabTitle(final String title) {

        pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        lblTitle = new JLabel(title);
        lblTitle.setPreferredSize(new Dimension(80, 20));
        lblClose = new JLabel();
        lblClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png")));
        lblClose.setBorder(BorderFactory.createEmptyBorder());
        
        lblLoading = new JLabel();
        lblLoading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/loading.gif")));
        lblLoading.setBorder(BorderFactory.createEmptyBorder());
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlTab.add(lblLoading, gbc);
        
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        pnlTab.add(lblClose, gbc);

        tabbedPane.setTabComponentAt(mainIndex, pnlTab);
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                int index = tabbedPane.indexOfTab(title);
                if (index >= 0) {
                    tabbedPane.removeTabAt(index);

                }
            }
        });

    }
    public class TabbedBrowserAdapter extends WebBrowserAdapter {

        @Override
        public void locationChanged(WebBrowserNavigationEvent e) {
            
            inChanged=true;
            if(timer==null){
                  timer = new Timer(timerDelay, taskPerformer);
                  timer.start();
            }else{
                timer.stop();
                timer.start();
            }
            
            
            if (listenerActivated == false) { 
              mainIndex = tabbedPane.indexOfTab(titleText);
              if(mainIndex== -1){
                  return;
              }
                if (mainIndex > 0) {
                    
                    final String title = browser.getPageTitle();
                    lblTitle.setText(browser.getPageTitle());
           
                    
                }
                 
                listenerActivated = true;
            }

        }

        @Override
        public void locationChanging(WebBrowserNavigationEvent e) {
            inChanged=false;
            if( paymentInProgress ) {
                System.out.println("Payment In Progress");
                e.consume();
                return;
            }
            if (listenerActivated) {
                lblLoading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/loading.gif")));
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
