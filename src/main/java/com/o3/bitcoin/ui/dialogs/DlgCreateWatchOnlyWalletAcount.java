/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlNewAccountScreen;
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
 * @author tygac
 */
public class DlgCreateWatchOnlyWalletAcount extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgCreateWatchOnlyWalletAcount.class);
    private final WalletService service;
    private PnlNewAccountScreen pnlNewAccountScreen;
    private List<JButton> controls = new ArrayList<>();

    /**
     * Creates new form DlgCreateNewAccount
     *
     * @param service WalletService
     */
    
    public DlgCreateWatchOnlyWalletAcount(WalletService service) {
        super(false);
        this.service = service;
        setupUI();
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlNewAccountScreen == null) {
           pnlNewAccountScreen = new PnlNewAccountScreen(this, service);
        }
        return pnlNewAccountScreen;
    }

    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getNewAccountButton());
        return controls;
    }

    /**
     * function to get new account button for dialog and attach event handler
     * @return new account button
    */
    protected JButton getNewAccountButton() {
        JButton accountButton = new JButton("Create Account");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateAccountButtonClickEvent(e);
            }
        });
        return accountButton;
    }

    /**
     * callback function for new account button event 
    */
    protected void handleCreateAccountButtonClickEvent(ActionEvent e) {
        try {
            if(pnlNewAccountScreen.createWatchOnlyWalletAccount())
                handleCloseDialogControlEvent(e);
        } catch (IllegalArgumentException ex) {
            logger.error("Account creation failed: {}", ex.toString(), ex);
            ApplicationUI.get().showError(ex);
        } catch (Exception ex) {
            logger.error("Account creation failed: {}", ex.getMessage(), ex);
            ApplicationUI.get().showError(ex);
        }
    }

    @Override
    protected String getHeadingText() {
        return "Account For Watch Only Wallet";
    }

    @Override
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        super.handleCloseDialogControlEvent(e);
    }

    @Override
    protected void handleDefaultCloseEvent(ActionEvent e) {
        handleCloseDialogControlEvent(e);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
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
    }// </editor-fold>                        

}