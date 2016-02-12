/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.ui.component.progress.ProgressEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p>Class that shows block chain download progress dialog</p>
*/
public class DlgWalletLoadingProgress extends DlgProgress {

    private static final Logger logger = LoggerFactory.getLogger(DlgWalletLoadingProgress.class);
    private WalletService service;
    private List<JButton> controls = new ArrayList<>();

    public DlgWalletLoadingProgress(WalletService service) {
        super();
        this.service = service;
    }

    @Override
    public void start() {
        if (service.isSetupcompleted() && service.isNetworkSync()) {
            handleCloseDialogControlEvent(null);
            return;
        }
        setupUI();
        setIndeterminate(false);
        setCloseOnComplete(true);
        setHideBeforeComplete(true);
        progressScreen.setProgress(service.getLoadingStatus(), (int) service.getNetworkSyncPct());
        service.addProgressListener(this);
        setSize(425, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public String getHeadingText() {
        return "Loading Wallet (" + service.getWalletConfig().getId() + ")";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e instanceof ProgressEvent) {
            ProgressEvent bitcoinEvent = (ProgressEvent) e;
            setIndeterminate(bitcoinEvent.getProgress() <= 0);
            super.actionPerformed(new ProgressEvent(e.getSource(), e.getID(), bitcoinEvent.getProgress() * 100, e.getActionCommand()));
        }
    }
    
    @Override
    protected List<JButton> getControls() {
        controls = super.getControls();
        controls.get(0).setVisible(false);
        return controls;
    }

    @Override
    protected String getCloseButtonText() {
        return "OK";
    }

    @Override
    protected void handleCloseDialogControlEvent(ActionEvent e) {
        if (service != null) {
            service.removeProgressListener(this);
        }
        System.exit(1);
        ////super.handleCloseDialogControlEvent(e); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void handleDefaultCloseEvent(ActionEvent e) {
        handleCloseDialogControlEvent(e);
    }
}
