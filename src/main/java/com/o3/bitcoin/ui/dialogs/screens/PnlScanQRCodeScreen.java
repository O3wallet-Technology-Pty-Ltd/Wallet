/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs.screens;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.o3.bitcoin.ui.dialogs.BasicDialog;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author
 */

/**
 * <p>Class that creates UI form for Scan QRCode dialog</p> 
 */
public class PnlScanQRCodeScreen extends javax.swing.JPanel implements Runnable, ThreadFactory {

    private static final long serialVersionUID = 6441489157408381878L;
    private Executor executor = Executors.newSingleThreadExecutor(this);
    private WebcamPanel panel = null;
    private Webcam webcam = null;
    private String qrcodeString = null;
    private BasicDialog parent = null;
    private static boolean stopThread = false;
    /**
     * Creates new form PnlScanQRCodeScreen
     */
    public PnlScanQRCodeScreen(BasicDialog parent) {
        this.parent = parent;
        initComponents();
        if( getWebcam() )
            initCamera();
        else {
            JOptionPane.showMessageDialog(null,"No Webcam found","ERROR",JOptionPane.ERROR_MESSAGE);
            parent.dispose();
        }
        stopThread = false;
    }
    
    /**
     * function to get webcam
     * @return webcam found or not
     */
    private boolean getWebcam() {
        webcam = Webcam.getDefault();
        if( webcam != null )
            return true;
        else
            return false;
    }
    
    /**
     * function that initialize webcam control where output of webcam is shown
     */
    private void initCamera() {
        java.awt.GridBagConstraints gridBagConstraints;
        Dimension size = WebcamResolution.QVGA.getSize();
        panel = new WebcamPanel(webcam);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panel, gridBagConstraints);
        panel.setPreferredSize(size);
        scanQRCode();
    }
    
    /**
     * function that get image from webcam and show it on ui and get address from it
     */
    @Override
    public void run() {
        do {
            if( stopThread )
                break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Result result = null;
            BufferedImage image = null;
            if (webcam.isOpen()) {
                if ((image = webcam.getImage()) == null) {
                        continue;
                }
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // no qrcode image
                }
            }
            if (result != null) {
                qrcodeString = result.getText();
                panel.stop();
                PnlNewPaymentScreen.setDefaultCursor();
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        parent.dispose();
                    }
                });
                break;
            }
        } while (true);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "qrcode-scanner");
        t.setDaemon(true);
        return t;
    }
    
    /**
     * function that starts scanning of QRCode
     */
    public void scanQRCode() {
        if( panel != null ) {
            panel.start();
            executor.execute(this);
        }
    }
    
    /**
     * function that returns string scanned from QRCode
     * @return string 
     */
    public String getQRCode() {
        return qrcodeString;
    }
    
    /**
     * function that close camera
     */
    public void closeCamera() {
        if( panel != null ) {
            panel.stop();
            stopThread = true;
            PnlNewPaymentScreen.setDefaultCursor();
        }
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
