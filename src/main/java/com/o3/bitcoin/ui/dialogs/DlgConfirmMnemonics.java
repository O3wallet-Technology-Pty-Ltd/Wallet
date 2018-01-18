/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.screens.PnlConfirmMnemonicCode;
import com.o3.bitcoin.ui.dialogs.screens.PnlCreateWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author tygac
 */
public class DlgConfirmMnemonics extends BasicDialog {
    
    public static final int MODE_APP_START = 0;
    public static final int MODE_APP_RUNNING = 1;
    private int mode = MODE_APP_START;
    private PnlConfirmMnemonicCode pnlConfirmMnemonic;
    String mnemonic;
    public static boolean isConfirm = false;

    public DlgConfirmMnemonics() {
        this(MODE_APP_START);
    }

    public DlgConfirmMnemonics(int mode) {
        super();
        this.mode = mode;
    }

    @Override
    protected String getHeadingText() {
        return "Confirm Mnemonic Code";
    }

    @Override
    protected JPanel getMainContentPanel() {
        if (pnlConfirmMnemonic == null) {
            pnlConfirmMnemonic = new PnlConfirmMnemonicCode(this);
        }
        return pnlConfirmMnemonic;
    }

    @Override
    protected List<JButton> getControls() {
        List<JButton> controls = super.getControls();
        controls.add(0, getNextButton());
        return controls;
    }

    public int getMode() {
        return mode;
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

            private void handleNextButtonClickEvent(ActionEvent e) {
                try {
                    mnemonic = pnlConfirmMnemonic.txtpanelMnemonic.getText();
                    getMnemonicCodeMethod();
                } catch (Exception excepion) {
                    System.out.println("-----------" + excepion + "-----------");
                }

            }
        });
        return nextButton;
    }

    public void getMnemonicCodeMethod() {
        if ("".equals(mnemonic) || !mnemonic.equals(DlgCreateWallet.confirmMnemonicCode)) {
            DlgCreateWallet.setWarningMsg("Empty Field or Mnemonic Code does not match");
            isConfirm = false;
        }
        if (mnemonic.equals(DlgCreateWallet.confirmMnemonicCode)) {
            isConfirm = true;
            dispose();
        }

    }
}
