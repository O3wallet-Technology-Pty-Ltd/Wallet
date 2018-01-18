/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlQRCodeScreen;
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
 * @author
 */

/**
 * <p>Class that implements ui dialog to show QRCode of address</p>
*/
public class DlgQRCode extends BasicDialog {
    
    private static final Logger logger = LoggerFactory.getLogger(DlgCreateNewAccount.class);
    private PnlQRCodeScreen pnlQRCodeScreen;
    private List<JButton> controls = new ArrayList<>();
    private String address = null;
    private String amount = null;
    private String description = null;
    private String btcAmount = null;
    /**
     * Creates new form DlgQRCode
     */
    public DlgQRCode(String address) {
        super(false);
        this.address = address;
        setupUI();
    }
    
    public DlgQRCode(String address, String description, String amount, String btcAmount) {
        super(false);
        this.address = address;
        this.amount = amount;
        this.description = description;
        this.btcAmount = btcAmount;
        setupUI();
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlQRCodeScreen == null) {
            pnlQRCodeScreen = new PnlQRCodeScreen(address,description, amount, btcAmount);
        }
        return pnlQRCodeScreen;
    }


    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.add(0, getCopyQRCodeButton());
        controls.add(0, getMailQRCodeButton());
        controls.add(0, getPrintQRCodeButton());
        controls.add(0, getUriQRCodeButton());
        return controls;
    }

    /**
     * function to get Copy button for dialog and attach event handler
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
    
    
    //Mail Button
    
     protected JButton getMailQRCodeButton() {
        JButton accountButton = new JButton("Mail");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop dt = Desktop.getDesktop();
                    try {
                        dt.mail();
                    } catch (IOException ex) {
                        //java.util.logging.Logger.getLogger(DlgScanQRCode.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    }
            }
        });
        return accountButton;
    }
     
     
     //Print Button
     
        protected JButton getPrintQRCodeButton() {
        JButton accountButton = new JButton("Print");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                 try {
                    Desktop desktop = null;
                    if (Desktop.isDesktopSupported()) {
                        desktop = Desktop.getDesktop();
                    }
                    desktop.print(new File(pnlQRCodeScreen.qrcodeFilePath));

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    System.out.println("________" + ioe + "___________");
                }
            }
        });
        return accountButton;
    }
    
    //Uri Button
        protected JButton getUriQRCodeButton() {
        JButton accountButton = new JButton("Uri");
        XButtonFactory.themedButton(accountButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT)
                .size(XButtonFactory.NORMAL_BUTTON_DIMENSION);

        accountButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = PnlWalletScreen.LabelAddress;
                StringSelection selection = new StringSelection(value);

                    Clipboard clipboard = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
            }
        });
        return accountButton;
    }
    
    
    /**
     * callback function for Copy button event 
    */
    protected void handleCopyQRCodetButtonClickEvent(ActionEvent e) {
        ImageSelection imgSel = new ImageSelection(new ImageIcon(pnlQRCodeScreen.getQrcodeFilePath()).getImage());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }
    
    private void deleteQRCodeFile() {
        File file = new File(pnlQRCodeScreen.getQrcodeFilePath());
        if( file.exists() )
            file.delete();
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

