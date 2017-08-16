/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui;

import com.o3.bitcoin.Application;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.dialogs.DlgNewPayment;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Dimensions;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import com.o3.bitcoin.util.http.HttpGetClient;
import com.o3.bitcoin.util.http.HttpPostClient;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.bitcoinj.uri.BitcoinURI;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p> Class that creates title bar of ApplicationUI form</p>
*/
public class PnlTitleBar extends javax.swing.JPanel {

    private static final Logger logger = LoggerFactory.getLogger(PnlTitleBar.class);
    private int x;
    private int y;
    private String paymentURI = "";
    private ServerSocket svrSocket;

    /**
     * Creates new form PnlTitleBar
     */
    public PnlTitleBar() {
        initComponents();
        lblSupport.setText(" ");
        lblSupport.setVisible(true);
        lblUser.setVisible(false);
        pnlNotification.setVisible(false);
//        getVersionNotification();
        startTCPServer();
        checkForBitcoinURIPayment();
    }
    
    private void checkForBitcoinURIPayment() {
        if(Application.args.length == 1) {
            showPaymentNotification(Application.args[0]);
        }
    }
    
    private void showPaymentNotification(String bitcoinURI) {
        try {
            BitcoinURI bcuri = new BitcoinURI(bitcoinURI);
            paymentURI = bitcoinURI;
            if( bcuri.getAddress() != null && bcuri.getAmount() != null ) {
                float btcAmount = (float)(Long.parseLong(bcuri.getAmount().toString()) / 100000000.0f);
                lblNotification.setText("Payment to: "+bcuri.getAddress().toString()+" , Amount: "+String.format("%.5f", btcAmount)+" BTC");
                pnlNotification.setVisible(true);
            }
        }catch(Exception ex) {
            System.out.println("showPaymentNotification Invalid Bitcoin URI");
        }
    }
    
    private void startTCPServer() {
         new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                     svrSocket = new ServerSocket(29753,0,InetAddress.getByName(null));
                     while (true) {
                         System.out.println("accepting connection");
                         Socket connectionSocket = svrSocket.accept();
                         BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                         paymentURI = inFromClient.readLine();
                         System.out.println("Received: " + paymentURI);
                         showPaymentNotification(paymentURI);
                     }
                  }catch(Exception e) {
                        System.out.println("TCP Server Exception="+e.getMessage());
                 }
                }
        }).start();
    }
    
    private void getVersionNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    URL url = new URL(ResourcesProvider.VERSION_INFO_URL);
                    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
                    {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }
                        public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }
                    } };
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                   String version = null;
                   String line = null; 			
                   while ((line = br.readLine()) != null){
                       version = line;
                   }
                   br.close();
                   version = version.substring(version.indexOf('{'),version.indexOf('}')+1);// get rid of any garbge character
                    if( version != null && !version.isEmpty()) {
                        JSONObject json = new JSONObject(version);
                        if( json.has("major") && json.has("minor") && json.has("minor_minor") && !pnlNotification.isVisible()) {
                            int major = json.getInt("major");
                            int minor = json.getInt("minor");
                            int minorMinor = json.getInt("minor_minor");
                            String notification = "New application version "+major+"."+minor+"."+minorMinor+" is available for download";
                            if( major > ResourcesProvider.APP_MAJOR ) {
                                lblNotification.setText(notification);
                                pnlNotification.setVisible(true);
                            }
                            else if( minor > ResourcesProvider.APP_MINOR ) {
                                lblNotification.setText(notification);
                                pnlNotification.setVisible(true);
                            }
                            else if( minorMinor > ResourcesProvider.APP_MINOR_MINOR ) {
                                lblNotification.setText(notification);
                                pnlNotification.setVisible(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Version Exception="+e.getMessage());
                }
            }
        }).start();
    }
    
    public Component getUserLbl() {
        return lblUser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();
        lblMinimize = new javax.swing.JLabel();
        lblMaximize = new javax.swing.JLabel();
        lblClose = new javax.swing.JLabel();
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlTopNav = new javax.swing.JPanel();
        pnlNotification = new javax.swing.JPanel();
        lblNotificationClose = new javax.swing.JLabel();
        lblNotification = new javax.swing.JLabel();
        lblSupport = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();

        setBackground(Colors.TITLE_BAR_BG_COLOR);
        setLayout(new java.awt.BorderLayout());

        pnlTop.setBackground(Colors.TITLE_BAR_BG_COLOR);
        pnlTop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlTop.setPreferredSize(new Dimension(1024, Dimensions.TITLE_BAR_HEIGHT));
        pnlTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlTopMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlTopMousePressed(evt);
            }
        });
        pnlTop.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlTopMouseDragged(evt);
            }
        });
        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlControls.setOpaque(false);
        pnlControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        lblMinimize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/minimize.png"))); // NOI18N
        lblMinimize.setToolTipText("Minimize");
        lblMinimize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMinimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblMinimizeMousePressed(evt);
            }
        });
        pnlControls.add(lblMinimize);

        lblMaximize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/maximize.png"))); // NOI18N
        lblMaximize.setToolTipText("Restore/Maximize");
        lblMaximize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMaximize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblMaximizeMousePressed(evt);
            }
        });
        pnlControls.add(lblMaximize);

        lblClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png"))); // NOI18N
        lblClose.setToolTipText("Exit");
        lblClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblCloseMousePressed(evt);
            }
        });
        pnlControls.add(lblClose);

        pnlTop.add(pnlControls, java.awt.BorderLayout.CENTER);

        pnlTitle.setForeground(Color.WHITE);
        pnlTitle.setOpaque(false);

        lblTitle.setFont(Fonts.BOLD_SMALL_FONT);
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setText(ResourcesProvider.APP_TITLE_VERSION);
        pnlTitle.add(lblTitle);

        pnlTop.add(pnlTitle, java.awt.BorderLayout.WEST);

        add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlTopNav.setBackground(Colors.TITLE_BAR_NAV_BG_COLOR);
        pnlTopNav.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        pnlNotification.setPreferredSize(new java.awt.Dimension(500, 40));
        pnlNotification.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlNotificationMouseClicked(evt);
            }
        });
        pnlNotification.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblNotificationClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png"))); // NOI18N
        lblNotificationClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblNotificationClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNotificationCloseMouseClicked(evt);
            }
        });
        pnlNotification.add(lblNotificationClose);

        lblNotification.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblNotification.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNotificationMouseClicked(evt);
            }
        });
        pnlNotification.add(lblNotification);

        pnlTopNav.add(pnlNotification);

        lblSupport.setFont(Fonts.BOLD_MEDIUM_FONT);
        lblSupport.setForeground(Colors.TITLE_BAR_NAV_BUTTON_COLOR);
        lblSupport.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSupport.setText("Support/Help");
        lblSupport.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblSupport.setPreferredSize(new java.awt.Dimension(110, 45));
        pnlTopNav.add(lblSupport);

        lblUser.setBackground(Colors.TITLE_BAR_NAV_BUTTON_BIG_BG_COLOR);
        lblUser.setFont(Fonts.BOLD_MEDIUM_FONT);
        lblUser.setForeground(Colors.TITLE_BAR_NAV_BUTTON_COLOR);
        lblUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/default_user_dp.png"))); // NOI18N
        lblUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblUser.setIconTextGap(10);
        lblUser.setOpaque(true);
        lblUser.setPreferredSize(new java.awt.Dimension(115, 45));
        pnlTopNav.add(lblUser);

        add(pnlTopNav, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void lblMinimizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeMousePressed
        ApplicationUI.get().minimize();
    }//GEN-LAST:event_lblMinimizeMousePressed

    private void lblMaximizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeMousePressed
        ApplicationUI.get().maximize();
    }//GEN-LAST:event_lblMaximizeMousePressed

    private void lblCloseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMousePressed
        System.exit(0);
    }//GEN-LAST:event_lblCloseMousePressed

    private void pnlTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopMouseClicked
        if (evt.getClickCount() >= 2) {
            ApplicationUI.get().maximize();
        }
    }//GEN-LAST:event_pnlTopMouseClicked

    private void pnlTopMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopMousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_pnlTopMousePressed

    private void pnlTopMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopMouseDragged
        ApplicationUI.get().setLocation(evt.getXOnScreen() - x, evt.getYOnScreen() - y);
    }//GEN-LAST:event_pnlTopMouseDragged

    private void lblNotificationCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNotificationCloseMouseClicked
        // TODO add your handling code here:
        pnlNotification.setVisible(false);
    }//GEN-LAST:event_lblNotificationCloseMouseClicked

    private void pnlNotificationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlNotificationMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlNotificationMouseClicked

    private void lblNotificationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNotificationMouseClicked
        // TODO add your handling code here:
        try {
            lblNotification.setText("");
            pnlNotification.setVisible(false);
            BitcoinURI bcuri = new BitcoinURI(paymentURI);
            if( bcuri.getAddress() != null && bcuri.getAmount() != null ) {
                DlgNewPayment dlgNewPayment = new DlgNewPayment(WalletManager.get().getCurentWalletService());
                dlgNewPayment.centerOnScreen();
                dlgNewPayment.setReceiveAddress(bcuri.getAddress().toString());
                dlgNewPayment.setAmount(bcuri.getAmount().toString());
                dlgNewPayment.setVisible(true);
            }
        } catch(Exception e) {
            System.out.println("Invalid Bitcoin URI "+e.getMessage());
        }
    }//GEN-LAST:event_lblNotificationMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblClose;
    private javax.swing.JLabel lblMaximize;
    private javax.swing.JLabel lblMinimize;
    private javax.swing.JLabel lblNotification;
    private javax.swing.JLabel lblNotificationClose;
    private javax.swing.JLabel lblSupport;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlNotification;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopNav;
    // End of variables declaration//GEN-END:variables
}
