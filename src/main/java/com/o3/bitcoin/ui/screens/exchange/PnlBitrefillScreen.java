/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.exchange;

import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.DlgExchangeTransaction;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.http.HttpGetClient;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.codec.binary.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author
 */
public class PnlBitrefillScreen extends javax.swing.JPanel implements BasicScreen {

    private final DefaultComboBoxModel<String> countryModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<String> packageModel = new DefaultComboBoxModel<>();
    private String selectedCountry = "";
    private String selectedCountryDialingCode = "";
    private String selectedOperatorSlug = "";
    private String phoneNumber = "";
    private String uNamePass = "";
    private String basicAuth = "";
    private  String operatorName="";

    /**
     * Creates new form PnlBitrefillScreen
     */
    public PnlBitrefillScreen() {
        initComponents();
        customizeUI();
        makeBase64String();

        countryModel.removeAllElements();
        List<String> countryNames = getCountryNames();
        Collections.sort(countryNames, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        for (String cn : countryNames) {
            countryModel.addElement(cn);
        }

    }

    public void loadData() {

        initializeData();
    }

    private void makeBase64String() {
        String uNamePass = ResourcesProvider.bitrefillUserName + ":" + ResourcesProvider.bitRefillPassWord;
        byte[] authEncBytes = Base64.encodeBase64(uNamePass.getBytes());
        String base64String = new String(authEncBytes);
        basicAuth = "Basic " + base64String;
    }

    private void initializeData() {
        lblSelectCountry.setVisible(true);
        cmbCountry.setVisible(true);
        lblOperatorName.setVisible(true);
        txtPhoneNumber.setVisible(true);
        btnSeePrices.setVisible(true);
        lblPackageText.setVisible(false);
        cmbPackage.setVisible(false);
        lblEnterEmail.setVisible(false);
        txtEmailAddress.setVisible(false);
        btnBitcoinPayment.setVisible(false);
        txtPhoneNumber.setText("");
        lblProcessing.setVisible(false);
        lblOperatorNameValue.setVisible(false);
        lblOperatorName.setVisible(false);
        packageModel.removeAllElements();
    }

    private void customizeUI() {
        themeWalletActionButton(btnSeePrices, ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        themeWalletActionButton(btnBitcoinPayment, ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
        cmbCountry.setRenderer(new BasicComboBoxRenderer() {
            String countryName = "";

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                list.setSelectionBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
                list.setSelectionForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
                JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
                if (value != null) {
                    countryName = value.toString();
                    countryName = countryName.replace(" ", "-");
                    java.net.URL imgURL = getClass().getResource("/icons/" + countryName + ".png");
                    if (imgURL != null) {
                        component.setIcon(new ImageIcon(imgURL));
                    } else {
                        System.out.println("Image Missing..    " + countryName);
                    }
                }
                if (isSelected) {
                    component.setForeground(Color.WHITE);
                    component.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
                }
                return component;
            }
        });
    }

    public List getCountryNames() {
        String url = "https://api.bitrefill.com/v1/inventory/";
        List<String> countryNames = new ArrayList<String>();
        try {
            String limitDetails = HttpGetClient.getValuesFromUrlWithBase64Auth(url, basicAuth);
            JSONObject jObject = new JSONObject(limitDetails.trim());
            Iterator<?> keys = jObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (jObject.get(key) instanceof JSONObject) {
                    JSONObject jO = (JSONObject) jObject.get(key);
                    countryNames.add(jO.getString("name"));
                }
            }
        } catch (Exception e) {

        }

        return countryNames;
    }

    private String makePlaceOrderParameters(String number, String valuePackage, String email) {
        String urlParameters;
        urlParameters = "{\"operatorSlug\":\"" + selectedOperatorSlug + "\",\"valuePackage\":\"" + valuePackage + "\",\"number\":\"" + number + "\",\"email\":\"" + email + "\"}";
        return urlParameters;
    }

    public List lookUpNumber(String number) {
        String url = "https://api.bitrefill.com/v1/lookup_number?number=" + number;
        List<String> packageNames = new ArrayList<String>();
        String currency = "";
        String packageString = "";
       
        try {
            String limitDetails = HttpGetClient.getValuesFromUrlWithBase64Auth(url, basicAuth);
            System.out.println(limitDetails);
            JSONObject jObject = new JSONObject(limitDetails.trim());

            Iterator<?> keys = jObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (jObject.get(key) instanceof JSONObject) {
                    JSONObject jO = (JSONObject) jObject.get(key);
                    if (key.equals("country")) {
                        JSONArray jsonArray = jO.getJSONArray("currencies");
                        currency = jsonArray.get(0).toString();
                    }
                    if (key.equals("operator")) {
                        selectedOperatorSlug = jO.getString("slug");
                        operatorName  = jO.getString("name");
                        if(currency.isEmpty()){
                            currency = jO.getString("currency");
                        }
                        JSONArray jsonA = jO.getJSONArray("packages");
                        for (int i = 0; i < jsonA.length(); i++) {
                            JSONObject packageObject = (JSONObject) jsonA.get(i);
                            double packgeBitcoinPrice = packageObject.getDouble("satoshiPrice") * 0.00000001;
                            String packgeBitcoinPriceText = String.format("%.6f", packgeBitcoinPrice);
                            packageString = packageObject.getString("value") + " " + currency.toUpperCase() + " you pay " + packgeBitcoinPriceText + " BTC";
                            packageNames.add(packageString);
                        }
                    }
                    if (key.equals("altOperators")) {

                    }
                }
            }
        } catch (Exception e) {

        }

        return packageNames;
    }

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
    
    /**
     * function that apply theme to button 
     * @param button
     * @param background 
     */
    private void themeWalletActionButton(JButton button, Color background) {
        XButtonFactory
                .themedButton(button)
                .color(Color.WHITE)
                .background(background)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblPackageText = new javax.swing.JLabel();
        cmbPackage = new javax.swing.JComboBox();
        lblSelectCountry = new javax.swing.JLabel();
        txtEmailAddress = new javax.swing.JTextField();
        lblEnterEmail = new javax.swing.JLabel();
        cmbCountry = new javax.swing.JComboBox();
        lblOperatorName = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JTextField();
        pnlSpace = new javax.swing.JPanel();
        pnlWalletControls = new javax.swing.JPanel();
        btnSeePrices = new javax.swing.JButton();
        btnBitcoinPayment = new javax.swing.JButton();
        lblProcessing = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblEnterNumber3 = new javax.swing.JLabel();
        lblOperatorNameValue = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bitrefill", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, ResourcesProvider.Fonts.BOLD_LARGE_FONT, ResourcesProvider.Colors.DEFAULT_HEADING_COLOR));
        setLayout(new java.awt.GridBagLayout());

        lblPackageText.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblPackageText.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblPackageText.setText("Select Package: ");
        lblPackageText.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblPackageText, gridBagConstraints);

        cmbPackage.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbPackage.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbPackage.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbPackage.setModel(packageModel);
        cmbPackage.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbPackage.setMinimumSize(new java.awt.Dimension(275, 31));
        cmbPackage.setPreferredSize(new java.awt.Dimension(275, 31));
        cmbPackage.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPackageItemStateChanged(evt);
            }
        });
        cmbPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPackageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(cmbPackage, gridBagConstraints);

        lblSelectCountry.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblSelectCountry.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblSelectCountry.setText("Select Country: ");
        lblSelectCountry.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblSelectCountry, gridBagConstraints);

        txtEmailAddress.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        txtEmailAddress.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtEmailAddress.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        txtEmailAddress.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txtEmailAddress.setPreferredSize(new java.awt.Dimension(275, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtEmailAddress, gridBagConstraints);

        lblEnterEmail.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblEnterEmail.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblEnterEmail.setText("Enter Your Email: ");
        lblEnterEmail.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblEnterEmail, gridBagConstraints);

        cmbCountry.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbCountry.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbCountry.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbCountry.setModel(countryModel);
        cmbCountry.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbCountry.setMinimumSize(new java.awt.Dimension(275, 31));
        cmbCountry.setPreferredSize(new java.awt.Dimension(275, 31));
        cmbCountry.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCountryItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(cmbCountry, gridBagConstraints);

        lblOperatorName.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblOperatorName.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblOperatorName.setText("Operator Name: ");
        lblOperatorName.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblOperatorName, gridBagConstraints);

        txtPhoneNumber.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        txtPhoneNumber.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        txtPhoneNumber.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        txtPhoneNumber.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txtPhoneNumber.setPreferredSize(new java.awt.Dimension(275, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtPhoneNumber, gridBagConstraints);

        pnlSpace.setMinimumSize(new java.awt.Dimension(1, 1));
        pnlSpace.setOpaque(false);
        pnlSpace.setPreferredSize(new java.awt.Dimension(1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlSpace, gridBagConstraints);

        pnlWalletControls.setOpaque(false);
        pnlWalletControls.setPreferredSize(new java.awt.Dimension(165, 33));
        pnlWalletControls.setLayout(new java.awt.GridBagLayout());

        btnSeePrices.setText("See Prices");
        btnSeePrices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeePricesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 25);
        pnlWalletControls.add(btnSeePrices, gridBagConstraints);

        btnBitcoinPayment.setText("Bitcoin To Pay");
        btnBitcoinPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBitcoinPaymentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 25);
        pnlWalletControls.add(btnBitcoinPayment, gridBagConstraints);

        lblProcessing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/loading.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlWalletControls.add(lblProcessing, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(pnlWalletControls, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel2.setForeground(ResourcesProvider.Colors.NAV_MENU_DASHBOARD_COLOR);
        jLabel2.setText("Number Should Not Contain Country Code");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel2, gridBagConstraints);

        lblEnterNumber3.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblEnterNumber3.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblEnterNumber3.setText("Enter Your Number: ");
        lblEnterNumber3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblEnterNumber3, gridBagConstraints);

        lblOperatorNameValue.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblOperatorNameValue.setText("...");
        lblOperatorNameValue.setToolTipText("Click to copy to clipboard");
        lblOperatorNameValue.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblOperatorNameValue.setMaximumSize(new java.awt.Dimension(12, 31));
        lblOperatorNameValue.setMinimumSize(new java.awt.Dimension(12, 31));
        lblOperatorNameValue.setPreferredSize(new java.awt.Dimension(12, 31));
        lblOperatorNameValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblOperatorNameValueMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblOperatorNameValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCountryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCountryItemStateChanged
        initializeData();
        btnSeePrices.setVisible(true);
        selectedCountry = (String) cmbCountry.getSelectedItem();
        selectedCountryDialingCode = ResourcesProvider.COUNTRY_DIALING_CODES.get(selectedCountry);
 
    }//GEN-LAST:event_cmbCountryItemStateChanged

    private void makeControlsVisible() {

        lblPackageText.setVisible(true);
        cmbPackage.setVisible(true);
        lblEnterEmail.setVisible(true);
        txtEmailAddress.setVisible(true);
        btnBitcoinPayment.setVisible(true);
        lblOperatorNameValue.setVisible(true);
        lblOperatorName.setVisible(true);
    }
    private void cmbPackageItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPackageItemStateChanged

    }//GEN-LAST:event_cmbPackageItemStateChanged

    private void cmbPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPackageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbPackageActionPerformed

    private void btnSeePricesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeePricesActionPerformed
       
        // TODO add your handling code here:
        lblProcessing.setVisible(true);
        if (selectedCountryDialingCode==null) {
            
            ApplicationUI.get().showError("Country Not Supported.");
            lblProcessing.setVisible(false);
            return;
        }
        if (txtPhoneNumber.getText().isEmpty()) {
            txtPhoneNumber.requestFocusInWindow();
            ApplicationUI.get().showError("Phone Number is required.");
            lblProcessing.setVisible(false);
            return;
        } else {
            try {
                Long.parseLong(txtPhoneNumber.getText());
            } catch (Exception e) {
                txtPhoneNumber.requestFocusInWindow();
                ApplicationUI.get().showError("Invalid Phone Number.");
                lblProcessing.setVisible(false);
                return;
            }
            phoneNumber = selectedCountryDialingCode + txtPhoneNumber.getText();
            List<String> packageNames = lookUpNumber(phoneNumber);
            for (String cn : packageNames) {
                packageModel.addElement(cn);
            }
             lblOperatorNameValue.setText(operatorName);
            if (packageNames.isEmpty()) {
                ApplicationUI.get().showError("No Package Available.");
                lblProcessing.setVisible(false);
                return;
            } else {
                makeControlsVisible();
                btnSeePrices.setVisible(false);
                lblProcessing.setVisible(false);
            }
        }

    }//GEN-LAST:event_btnSeePricesActionPerformed

    private void btnBitcoinPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBitcoinPaymentActionPerformed
       lblProcessing.setVisible(true);
        if (txtEmailAddress.getText().isEmpty()) {
            txtEmailAddress.requestFocusInWindow();
            ApplicationUI.get().showError("Email is required.");
            lblProcessing.setVisible(false);
            return;
        } else if (!isValidEmailAddress(txtEmailAddress.getText())) {
            txtEmailAddress.requestFocusInWindow();
            ApplicationUI.get().showError("Email is not valid.");
            lblProcessing.setVisible(false);
            return;
        }

        String url = "https://api.bitrefill.com/v1/order/";
        String packageValue = "";

        String selectedPackage = (String) cmbPackage.getSelectedItem();
        StringTokenizer defaultTokenizer = new StringTokenizer(selectedPackage);
        packageValue = defaultTokenizer.nextToken();
        String urlParameters = makePlaceOrderParameters(phoneNumber, packageValue, txtEmailAddress.getText());
        try {
            String transactionDetails = HttpGetClient.placeOrderTransaction(url, urlParameters, basicAuth);
            System.out.println(transactionDetails);
            if (transactionDetails.isEmpty()) {
                lblProcessing.setVisible(false);
                ApplicationUI.get().showError("There is no package available.");
                return;
            }
            
            JSONObject jsonValues = new JSONObject(transactionDetails);
            JSONObject jsonObjectPayment = jsonValues.getJSONObject("payment");
            Date date = new Date(jsonValues.getLong("expirationTime"));
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String expiryTime = df.format(date);
            DlgExchangeTransaction dlgExchangeTransaction = new DlgExchangeTransaction("Your Order " + jsonValues.getString("itemDesc"), jsonValues.getString("orderId"), jsonObjectPayment.getString("address"), jsonValues.getString("btcPrice") + " BTC", expiryTime, jsonObjectPayment.getString("human"),jsonValues.getLong("satoshiPrice"));
            dlgExchangeTransaction.centerOnScreen();
            lblProcessing.setVisible(false);
            dlgExchangeTransaction.setVisible(true);

        } catch (Exception e) {
            lblProcessing.setVisible(false);
            ApplicationUI.get().showError(e.getMessage());
            return;
        }

    }//GEN-LAST:event_btnBitcoinPaymentActionPerformed

    private void lblOperatorNameValueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOperatorNameValueMouseClicked
        // TODO add your handling code here:
       
    }//GEN-LAST:event_lblOperatorNameValueMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBitcoinPayment;
    private javax.swing.JButton btnSeePrices;
    private javax.swing.JComboBox cmbCountry;
    private javax.swing.JComboBox cmbPackage;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblEnterEmail;
    private javax.swing.JLabel lblEnterNumber3;
    private javax.swing.JLabel lblOperatorName;
    private javax.swing.JLabel lblOperatorNameValue;
    private javax.swing.JLabel lblPackageText;
    private javax.swing.JLabel lblProcessing;
    private javax.swing.JLabel lblSelectCountry;
    private javax.swing.JPanel pnlSpace;
    private javax.swing.JPanel pnlWalletControls;
    private javax.swing.JTextField txtEmailAddress;
    private javax.swing.JTextField txtPhoneNumber;
    // End of variables declaration//GEN-END:variables
}
