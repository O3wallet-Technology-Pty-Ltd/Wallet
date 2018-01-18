/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlQRCodeScreen;
import com.o3.bitcoin.ui.screens.settings.PnlSettingsScreen;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.ImageSelection;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tygac
 */
public class DlgXpubKey extends BasicDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgCreateNewAccount.class);
    private PnlQRCodeScreen pnlQRCodeScreen;
    private List<JButton> controls = new ArrayList<>();
    private String address = null;

    public DlgXpubKey(String address) {
        super(false);
        this.address = address;
        setupUI();
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlQRCodeScreen == null) {
            pnlQRCodeScreen = new PnlQRCodeScreen(address);
        }
        return pnlQRCodeScreen;
    }

    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getCopyQRCodeButton());
        controls.add(0, tPubKey());
        controls.add(0, xPubKey());
        return controls;
    }

    /**
     * function to get Copy button for dialog and attach event handler
     *
     * @return Copy button
     */
    protected JButton getCopyQRCodeButton() {
        JButton accountButton = new JButton("Copy");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               handleCopyQRCodetButtonClickEvent(e);
            }
        });
        return accountButton;
    }

   
    protected JButton xPubKey() {
        JButton accountButton = new JButton("XPubKey");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = PnlSettingsScreen.xPublicKey;
                try
                {
                    if(!value.equals(""))
                    {
                        StringSelection selection = new StringSelection(value);
                        Clipboard clipboard = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                    else
                    {
                        DlgCreateWallet.setWarningMsg("Switch to main net");
                        PnlSettingsScreen.xPublicKey = "";
                    }
                }
                catch(NullPointerException v)
                {System.out.println("aaaa");}
            }
        });
        return accountButton;
    }
    
    protected JButton tPubKey() {
        JButton accountButton = new JButton("TPubKey");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = PnlSettingsScreen.tPublicKey;
                try
                {
                    if(!value.equals(""))
                    {
                        StringSelection selection = new StringSelection(value);
                        Clipboard clipboard = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                    else
                    {
                        DlgCreateWallet.setWarningMsg("Switch to test net");
                        PnlSettingsScreen.tPublicKey = "";
                    }
                }
                catch(NullPointerException b)
                {System.out.println("aaa");}
            }
        });
        return accountButton;
    }

    private void deleteQRCodeFile() {
        File file = new File(pnlQRCodeScreen.getQrcodeFilePath());
        if (file.exists()) {
            file.delete();
        }
    }
    
     @Override
    protected String getHeadingText() {
        return "QRCode";
    }

    @Override
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        super.handleCloseDialogControlEvent(e);
        deleteQRCodeFile();
    }

    @Override
    protected void handleDefaultCloseEvent(ActionEvent e) {
        handleCloseDialogControlEvent(e);
    }
    protected void handleCopyQRCodetButtonClickEvent(ActionEvent e) {
        ImageSelection imgSel = new ImageSelection(new ImageIcon(pnlQRCodeScreen.getQrcodeFilePath()).getImage());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    /**
     * Creates new form DlgXpubKey
     */
    public DlgXpubKey() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
