/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.Application;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.component.progress.ProgressEvent;
import com.o3.bitcoin.ui.dialogs.screens.PnlProgressScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.bitcoinj.uri.BitcoinURI;

/**
 * <p>Class that implements ui dialog to show block chain download progress</p>
*/
public class DlgProgress extends BasicDialog implements ActionListener {

    protected PnlProgressScreen progressScreen;
    protected int minimum = 0;
    protected int maximum = 100;
    protected String status = "Loading ...";
    protected int progress = minimum;
    protected boolean indeterminate = true;
    protected boolean closeOnComplete = true;
    protected boolean hideBeforeComplete = false;
    protected boolean completed = false;
    public static boolean isAlreadyShown = false;

    public DlgProgress() {
        super(false);
    }

    public void start() {
        setupUI();
        if (progressScreen != null) {
            if (indeterminate) {
                progressScreen.setProgress(status);
            } else {
                progressScreen.setProgress(status, progress);
            }
        }
        setSize(425, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void setupUI() {
        progressScreen = PnlProgressScreen.create();
        progressScreen.setIndeterminant(indeterminate)
                .setMinimum(minimum)
                .setMaximum(maximum)
                .setStatus(status);
        super.setupUI();
    }

    @Override
    public String getHeadingText() {
        return "Loading ...";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (progressScreen != null) {
            if (e instanceof ProgressEvent) {
                ProgressEvent progressEvent = (ProgressEvent) e;
                progressScreen.setProgress(progressEvent.getActionCommand(), (int) progressEvent.getProgress());
            } else {
                progressScreen.setProgress(e.getActionCommand());
            }
            if (closeOnComplete) {
                if (isCompleted()) {
                    this.dispose();
                    /*if(!isAlreadyShown) {
                        if(Application.args.length == 1) {
                            try {
                                BitcoinURI bcuri = new BitcoinURI(Application.args[0]);
                                if( bcuri.getAddress() != null && bcuri.getAmount() != null ) {
                                    DlgNewPayment dlgNewPayment = new DlgNewPayment(WalletManager.get().getCurentWalletService());
                                    dlgNewPayment.centerOnScreen();
                                    dlgNewPayment.setReceiveAddress(bcuri.getAddress().toString());
                                    dlgNewPayment.setAmount(bcuri.getAmount().toString());
                                    dlgNewPayment.setVisible(true);
                                    isAlreadyShown = true;
                                }
                            }catch(Exception ex) {
                            }
                        }
                    }*/
                }
            }
        }
    }

    @Override
    protected JPanel getMainContentPanel() {
        return this.progressScreen;
    }

    public double getMinimum() {
        return minimum;
    }

    public DlgProgress setMinimum(int minimum) {
        this.minimum = minimum;
        progressScreen.setMinimum(minimum);
        return this;
    }

    public double getMaximum() {
        return maximum;
    }

    public DlgProgress setMaximum(int maximum) {
        this.maximum = maximum;
        progressScreen.setMaximum(maximum);
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DlgProgress setStatus(String status) {
        this.status = status;
        progressScreen.setStatus(status);
        return this;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public DlgProgress setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        progressScreen.setIndeterminant(indeterminate);
        return this;
    }

    public boolean isCloseOnComplete() {
        return closeOnComplete;
    }

    public void setCloseOnComplete(boolean closeOnComplete) {
        this.closeOnComplete = closeOnComplete;
    }

    public boolean isHideBeforeComplete() {
        return hideBeforeComplete;
    }

    public DlgProgress setHideBeforeComplete(boolean hideBeforeComplete) {
        this.hideBeforeComplete = hideBeforeComplete;
        return this;
    }

    public boolean isCompleted() {
        return progressScreen != null && progressScreen.isCompleted();
    }

    public void markCompleted() {
        this.completed = true;
        progressScreen.markCompleted();
    }

    public void markCompleted(String status) {
        this.completed = true;
        progressScreen.markCompleted(status);
    }

    @Override
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        if (hideBeforeComplete) {
            super.handleCloseDialogControlEvent(e); 
        } else {
            if (isCompleted()) {
                super.handleCloseDialogControlEvent(e);
            }
        }
    }

    @Override
    protected void handleDefaultCloseEvent(ActionEvent e) {
        handleCloseDialogControlEvent(e);
    }

}
