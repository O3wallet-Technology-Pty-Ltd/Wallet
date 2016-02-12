/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlRestoreWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class DlgRestoreWallet extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgRestoreWallet.class);

    public static final int MODE_APP_START = 0;
    public static final int MODE_APP_RUNNING = 1;

    private int mode = MODE_APP_START;
    private PnlRestoreWalletScreen pnlRestoreWalletScreen;
    private WalletService service;

    /**
     * Creates new form DlgCreateWallet
     */
    public DlgRestoreWallet() {
        super();
    }

    @Override
    protected String getHeadingText() {
        return "Restore Wallet";
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlRestoreWalletScreen == null) {
            pnlRestoreWalletScreen = new PnlRestoreWalletScreen(this);
        }
        return pnlRestoreWalletScreen;
    }

    @Override
    protected List<JButton> getControls() {
        List<JButton> controls = super.getControls();
        controls.add(0, getNextButton());
        return controls;
    }

    protected JButton getNextButton() {
        JButton nextButton = new JButton("Next");
        XButtonFactory.themedButton(nextButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleNextButtonClickEvent(e);
            }
        });
        return nextButton;
    }

    protected void handleNextButtonClickEvent(ActionEvent e) {
        try {
            pnlRestoreWalletScreen.validateData();
            String id = pnlRestoreWalletScreen.getWalletName().trim();
            String location = pnlRestoreWalletScreen.getLocationPath();
            String passphrase = pnlRestoreWalletScreen.getPassphrase();
            WalletConfig wallet;
            if (pnlRestoreWalletScreen.isRestoreFromSeed()) {
                List<String> mnemonicCodes = pnlRestoreWalletScreen.getMnemonicCodes();
                wallet = new WalletConfig(id, location, Utils.getNetworkName(pnlRestoreWalletScreen.getSelectedNetwork()), mnemonicCodes, passphrase, pnlRestoreWalletScreen.getCreationDate());
            } else {
                File walletFile = new File(pnlRestoreWalletScreen.getWalletFile());
                wallet = new WalletConfig(id, location, Utils.getNetworkName(pnlRestoreWalletScreen.getSelectedNetwork()), walletFile, passphrase);
            }
            service = WalletManager.get().createOrLoadWalletService(pnlRestoreWalletScreen.getSelectedNetwork(), wallet);
            if (service != null) {
                DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(service);
                progress.start();
            }
            dispose();
        } catch (IllegalArgumentException ex) {
            logger.error("Restore Wallet Vaidation Error: {}", ex.toString());
            ApplicationUI.get().showError(ex);
        } catch (Exception ex) {
            logger.error("Restore Wallet Vaidation Error: {}", ex, ex);
            ApplicationUI.get().showError(ex);
        }
    }

    public int getMode() {
        return mode;
    }

    public WalletService getWalletService() {
        return service;
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
