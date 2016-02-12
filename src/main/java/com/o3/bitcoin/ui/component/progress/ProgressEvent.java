/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.component.progress;

import com.o3.bitcoin.service.WalletService;
import java.awt.event.ActionEvent;

/**
 *
 * @author
 */

/**
 * <p>Class that represent ActionEvent for the progress of block chain download</p>
 */
public class ProgressEvent extends ActionEvent {

    private final double progress;

    public ProgressEvent(Object source, int id, double progress, String command) {
        super(source, id, command);
        this.progress = progress;
    }

    public double getProgress() {
        return this.progress;
    }

    @Override
    public String toString() {
        return (id == WalletService.TOR_SYNC_MODE ? " (Tor) > " : "(Bitcoin Netowrk) > ") + getActionCommand() + " :: " + progress;
    }
}
