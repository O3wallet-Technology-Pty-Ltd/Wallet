/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.hdwallet.util.WalletUtil;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.DlgConfirmMnemonics;
import com.o3.bitcoin.ui.dialogs.screens.PnlConfirmMnemonicCode;
import com.o3.bitcoin.ui.dialogs.screens.PnlCreateWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.Utils;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.sound.sampled.LineEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
/**
 * <p>
 * Class that implements ui dialog to create new wallet</p>
 */
public class DlgCreateWallet extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgCreateWallet.class);
    public static String confirmMnemonicCode = "";
    public static final int MODE_APP_START = 0;
    public static final int MODE_APP_RUNNING = 1;

    private int mode = MODE_APP_START;
    private PnlCreateWalletScreen pnlCreateWalletScreen;
    private WalletService service;

    /**
     * Creates new form DlgCreateWallet
     */
    public DlgCreateWallet() {
        this(MODE_APP_START);
    }

    public DlgCreateWallet(int mode) {
        super();
        this.mode = mode;
    }

    @Override
    protected String getHeadingText() {
        return "Create/Restore Wallet";
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlCreateWalletScreen == null) {
            pnlCreateWalletScreen = new PnlCreateWalletScreen(this);
        }
        return pnlCreateWalletScreen;
    }

    @Override
    protected List<JButton> getControls() {
        List<JButton> controls = super.getControls();
        controls.add(0, getNextButton());
        return controls;
    }

    /**
     * function to get Next button for dialog and attach event handler
     *
     * @return Next button
     */
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

    /**
     * callback function for Next button event
     *
     * @param e
     */
    protected void handleNextButtonClickEvent(ActionEvent e) {

        try {
            confirmMnemonicCode = pnlCreateWalletScreen.txtSeed.getText();
            mnemonicScreen();
            if (DlgConfirmMnemonics.isConfirm) {
                DlgConfirmMnemonics.isConfirm = false;
                pnlCreateWalletScreen.validateData();
                String id = pnlCreateWalletScreen.getWalletName().trim();
                String location = pnlCreateWalletScreen.getLocationPath();
                List<String> mnemonicCodes = pnlCreateWalletScreen.getMnemonicCodes();
                WalletConfig wallet = null;
                if (pnlCreateWalletScreen.isRestoreFromSeed()) {
                    wallet = new WalletConfig(id, location, Utils.getNetworkName(pnlCreateWalletScreen.getSelectedNetwork()), mnemonicCodes, pnlCreateWalletScreen.getCreationDate(), "", pnlCreateWalletScreen.getNumberOfAccounts());
                } else {
                    wallet = new WalletConfig(id, location, Utils.getNetworkName(pnlCreateWalletScreen.getSelectedNetwork()), mnemonicCodes, new Date(), pnlCreateWalletScreen.getAccountName(), -1);
                }
                service = WalletManager.get().createOrLoadWalletService(pnlCreateWalletScreen.getSelectedNetwork(), wallet);
                dispose();
            }
        } catch (IllegalArgumentException ex) {
            logger.error("Create Wallet Vaidation Error: {}", ex.toString());
            ApplicationUI.get().showError(ex);
        } catch (Exception ex) {
            logger.error("Create Wallet Vaidation Error: {}", ex, ex);
            ApplicationUI.get().showError(ex);
        }

    }

    private static void mnemonicScreen() {
        DlgConfirmMnemonics dlgConfirmMnemonics = new DlgConfirmMnemonics();
        dlgConfirmMnemonics.centerOnScreen();
        dlgConfirmMnemonics.setVisible(true);
    }

    public int getMode() {
        return mode;
    }

    public WalletService getWalletService() {
        return service;
    }

    public static void setWarningMsg(String text) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane optionPane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog("Warning!");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    
    public static void setSuccessMsg(String text) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane optionPane = new JOptionPane(text, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog("Success");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
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
