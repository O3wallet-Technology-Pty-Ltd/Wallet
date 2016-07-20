/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlExchangeTransationScreen;
import com.o3.bitcoin.ui.dialogs.screens.PnlNewPaymentScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 
 */
public class DlgExchangeTransaction extends BasicDialog {

     private static final Logger logger = LoggerFactory.getLogger(DlgExchangeTransaction.class);
   
    private PnlExchangeTransationScreen pnlExchangeTransationScreen;
    private String depositAddress = "";
    private String title;
    private String orderId;
    private String depositeAmount;
    private String expiryTime;
    private String statusAddress;
    private JButton paymentButton;
    private List<JButton> controls = new ArrayList<>();
    private boolean isQuick=false;
    private boolean isPercise=false;
    private boolean bitrefill=false;
    private String satoshiPrice="";
   
    
    /**
     * Creates new form DlgExchangeTransaction
     */
    public DlgExchangeTransaction(String title, String orderId, String depositAddress) {
        super(false);
        isQuick = true;
        this.title = title;
        this.orderId = orderId;
        this.depositAddress = depositAddress; 
        setupUI();
    }
    
    /**
     * Creates new form DlgExchangeTransaction
     */
    public DlgExchangeTransaction(String title, String orderId, String depositAddress, String depositeAmount, String expiryTime) {
        super(false);
        isPercise=true;
        this.title = title;
        this.orderId = orderId;
        this.depositAddress = depositAddress;
        this.depositeAmount = depositeAmount;
        this.expiryTime = expiryTime;
        setupUI();
    }
     public DlgExchangeTransaction(String title, String orderId, String depositAddress, String depositeAmount, String expiryTime,String statusAddress,Long satoshiPrice) {
        super(false);
        bitrefill=true;
        this.title = title;
        this.orderId = orderId;
        this.depositAddress = depositAddress;
        this.depositeAmount = depositeAmount;
        this.expiryTime = expiryTime;
        this.statusAddress = statusAddress;
        this.satoshiPrice = satoshiPrice.toString();
        setupUI();
    }

     @Override
    protected JPanel getMainContentPanel() {
        if (pnlExchangeTransationScreen == null) {
            if( isQuick ){
                controls.get(0).setVisible(false);
                pnlExchangeTransationScreen = new PnlExchangeTransationScreen(this, title, orderId, depositAddress);
            }
            else if(isPercise){
                controls.get(0).setVisible(false);
                pnlExchangeTransationScreen = new PnlExchangeTransationScreen(this, title, orderId, depositAddress, depositeAmount, expiryTime);
            }
            else if(bitrefill){
                pnlExchangeTransationScreen = new PnlExchangeTransationScreen(this, title, orderId, depositAddress, depositeAmount, expiryTime,statusAddress,satoshiPrice);
            }
        }
        return pnlExchangeTransationScreen;
    }
    
    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getPaymentButton());
        return controls;
    }

    /**
     * function to get Pay button for dialog and attach event handler
     * @return Pay button
    */
    protected JButton getPaymentButton() {
        paymentButton = new JButton("Pay");
        XButtonFactory.themedButton(paymentButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        paymentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handlePayCoinsButtonClickEvent(e);
            }
        });
        return paymentButton;
    }

    /**
     * callback function for Pay button event 
    */
    protected void handlePayCoinsButtonClickEvent(ActionEvent e) {
        try {
           pnlExchangeTransationScreen.payCoins();
        } catch (IllegalArgumentException ex) {
            logger.error("Payment failed: {}", ex.toString(), ex);
            ApplicationUI.get().showError(ex);
        } catch (Exception ex) {
            logger.error("Payment failed: {}", ex.getMessage(), ex);
            ApplicationUI.get().showError(ex);
        }
    }
    
    public void setDepositAddress(String address) {
        pnlExchangeTransationScreen.setDepositAddress(address);
    }
    
     @Override
    protected String getHeadingText() {
        return title;
    }

     
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
