/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

import com.lambdaworks.crypto.SCryptUtil;
import com.o3.bitcoin.Application;
import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.exception.CurrencyRateNotAvailableException;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.ui.DirectionRatio;
import com.o3.bitcoin.ui.ScaleDescriptor;
import com.o3.bitcoin.util.ResourcesProvider.Dimensions;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that provides different utility functions for application
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    public static final String DEFAULT_APP_PASSWORD = "123456";

    public static ScaleDescriptor createScaleDescriptor(JComponent comp) {
        JRootPane rootPane = comp != null ? comp.getRootPane() : null;
        if (rootPane == null) {
            return null;
        } else {
            return createScaleDescriptor(rootPane);
        }
    }
    
    /**
     * function that provides scale factor used to scale controls on ui according to form size
     * @param w
     * @return scale factor
     */
    public static ScaleDescriptor createScaleDescriptor(JRootPane w) {
        Dimension d = w.getContentPane().getSize();
        int realWidth = d.width;
        int realHeight = d.height;
        double scaleCoffX = (double) realWidth / (double) Dimensions.BASE_UI_DIMENSION.width;
        double scaleCoffY = (double) realHeight / (double) Dimensions.BASE_UI_DIMENSION.height;
        return new ScaleDescriptor(scaleCoffX, scaleCoffY);
    }

    public static Font deliverFontForComponent(Font originalFont, JComponent comp) {
        return deliverFontForComponent(originalFont, createScaleDescriptor(comp));
    }

    public static Font deliverFontForComponent(Font originalFont, ScaleDescriptor sd) {
        return deliverFontForComponent(originalFont, sd, DirectionRatio.MIN_DIRECTION_RATIO);
    }

    /**
     * function that scales font according to form size
     * @param originalFont original font of component
     * @param sd scale factor
     * @param directionRatio
     * @return scaled font
     */
    public static Font deliverFontForComponent(Font originalFont, ScaleDescriptor sd, DirectionRatio directionRatio) {
        if (sd == null) {
            return originalFont;
        }
        double val = sd.getValueByRatio(directionRatio);
        int fontOrigVal = originalFont.getSize();
        float res = (float) ((double) fontOrigVal * val);
        Font newFont = originalFont.deriveFont(res);
        return newFont;
    }

    /**
     * function that load icons 
     * @param key icon to liad
     * @return loaded icon 
     */
    public static ImageIcon loadClassPathIcon(String key) {
        URL path = getClassPathResource(key);
        return new ImageIcon(path);
    }

    /**
     * function that loads images
     * @param key image to load
     * @return loaded image
     */
    public static Image loadClassPathImage(String key) {
        URL path = getClassPathResource(key);
        try {
            return ImageIO.read(path);
        } catch (IOException e) {
            throw new ClientRuntimeException((new StringBuilder()).append("Can't load class path image by key ").append(key).toString(), e);
        } catch (RuntimeException e) {
            throw new ClientRuntimeException((new StringBuilder()).append("Can't load class path image by key ").append(key).toString(), e);
        }
    }

    public static ImageIcon loadImageFromURL(String url) {
        try {
            return new ImageIcon(ImageIO.read(new URL(url)));
        } catch (IOException e) {
            throw new ClientRuntimeException((new StringBuilder()).append("Can't load image by url ").append(url).toString(), e);
        } catch (RuntimeException e) {
            throw new ClientRuntimeException((new StringBuilder()).append("Can't load image by url ").append(url).toString(), e);
        }
    }

    /**
     * function that loads font 
     * @param fontKey font to laod
     * @return loaded font
     */
    public static Font loadClassPathFont(String fontKey) {
        URL url = getClassPathResource(fontKey);
        try {
            Font res = Font.createFont(0, url.openStream());
            return res;
        } catch (FontFormatException e) {
            throw new ClientRuntimeException((new StringBuilder()).append("Invalid font format for key: ").append(fontKey).toString(), e);
        } catch (IOException e) {
            throw new ClientRuntimeException((new StringBuilder()).append("Can't load font by key: ").append(fontKey).toString(), e);
        }
    }

    public static URL getClassPathResource(String key) {
        URL url = Utils.class.getClassLoader().getResource(key);
        if (url == null) {
            throw new ClientRuntimeException((new StringBuilder()).append("Class path resource not found: ").append(key).toString());
        } else {
            return url;
        }
    }

    public static String formatTransactionDate(Date date) {
        if (date == null) {
            return "";
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            return "-";
        }
    }

    public static String formatSimpleDate(Date date) {
        if (date == null) {
            return "";
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception e) {
            return "-";
        }
    }

    public static Date parseSimpleDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty or this is a file so delete it 
        return file.delete();
    }

    public static String getDefaultApplicationPassword() {
        try {
            return encryptApplicationPassword(DEFAULT_APP_PASSWORD);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Default Application Password Error: ", ex, ex);
            return "";
        }
    }

    public static String encryptSha26(String data) throws NoSuchAlgorithmException {
        return encryptSha26(data.getBytes());
    }

    public static String encryptSha26(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger bi = new BigInteger(1, md.digest(data));
        return bi.toString(16);
    }

    public static String encryptMD5(String data) throws NoSuchAlgorithmException {
        return encryptMD5(data.getBytes());
    }

    public static String encryptMD5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        BigInteger bi = new BigInteger(1, md.digest(data));
        return bi.toString(16);
    }

    public static String encryptApplicationPassword(String password) throws NoSuchAlgorithmException {
        String encryptedPassword = encryptSha26(password);
        String scrypt = SCryptUtil.scrypt(encryptedPassword, 16, 16, 16);
        return scrypt;
    }
    
    public static boolean verifyApplicationPassword(String password) throws NoSuchAlgorithmException {
        String enceryptedPassword = encryptSha26(password);
        return SCryptUtil.check(enceryptedPassword, ConfigManager.config().getEncp());
    }

    /**
     * function that calls restful api and return response
     * @param apiCall api url to call
     * @return response string
     * @throws IOException 
     */
    public static String getHttpResponseAsString(String apiCall) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            //logger.debug("Calling API: " + apiCall);
            HttpGet getRequest = new HttpGet(apiCall);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new CurrencyRateNotAvailableException(response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);
            if (data == null || data.isEmpty()) {
                throw new CurrencyRateNotAvailableException(101);
            }
            return data;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
    
    public static String getNetworkName(NetworkParameters params) {
        return params instanceof MainNetParams ? "MAINNET" : "TESTNET";
    }
}
