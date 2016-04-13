/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.screens.settings;

import com.o3.bitcoin.Application;
import com.o3.bitcoin.exception.ClientRuntimeException;
import com.o3.bitcoin.hdwallet.HDAccount;
import com.o3.bitcoin.hdwallet.HDKey;
import com.o3.bitcoin.model.Config;
import com.o3.bitcoin.model.WalletConfig;
import com.o3.bitcoin.service.WalletService;
import com.o3.bitcoin.model.manager.WalletManager;
import com.o3.bitcoin.ui.ApplicationUI;
import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.ui.component.WalletComboBoxUI;
import com.o3.bitcoin.ui.dialogs.DlgCreateWallet;
import com.o3.bitcoin.ui.dialogs.DlgCreateWatchOnlyWallet;
import com.o3.bitcoin.ui.dialogs.DlgManageApplicationPassword;
import com.o3.bitcoin.ui.dialogs.DlgManageWalletPassphrase;
import com.o3.bitcoin.ui.dialogs.DlgRenameAccount;
import com.o3.bitcoin.ui.dialogs.DlgRestoreWallet;
import com.o3.bitcoin.ui.dialogs.DlgWalletLoadingProgress;
import com.o3.bitcoin.ui.dialogs.DlgWalletSeedQRCode;
import com.o3.bitcoin.util.ResourcesProvider;
import com.o3.bitcoin.util.ResourcesProvider.Colors;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that implements Settings screen of UI 
 */
public class PnlSettingsScreen extends javax.swing.JPanel implements BasicScreen {

    private static final Logger logger = LoggerFactory.getLogger(PnlSettingsScreen.class);
    private final DefaultComboBoxModel<HDAccount> model = new DefaultComboBoxModel<>();
    
    private boolean loading = true;
    private final DefaultComboBoxModel<String> currencyModel = new DefaultComboBoxModel<>();
    private List<String> currencies = ResourcesProvider.DEFAULT_CURRENCIES;
    
    private final DefaultComboBoxModel<String> feeModel = new DefaultComboBoxModel<>();
    private List<String> feePref = ResourcesProvider.FEE_PREF;

    /**
     * Creates new form PnlSettingsScreen
     */
    public PnlSettingsScreen() {
        initComponents();
        customizeUI();
        btnDeleteWallet.setEnabled(true);
        btnManagePassphrase.setEnabled(true);
        lblWalletName.setVisible(true);
        lblWalletNameValue.setVisible(true);
        lblWalletStatus.setVisible(true);
        lblWalletStatusValue.setVisible(true);
        lblWalletLocation.setVisible(true);
        lblWalletLocationValue.setVisible(true);
        lblWalletPassphrase.setVisible(false);
        lblWalletPassphraseValue.setVisible(false);
        lblCreationDate.setVisible(true);
        lblWalletCreationDateValue.setVisible(true);
        btnDeleteWallet.setVisible(true);
        btnManagePassphrase.setVisible(true);
    }

    /**
     * function that customize look and feel of controls on settings screen
     */
    private void customizeUI() {
        themeWalletActionButton(btnDeleteWallet, Colors.NAV_MENU_DASHBOARD_COLOR);
        themeWalletActionButton(btnManagePassphrase, Colors.NAV_MENU_WALLET_COLOR);
        themeWalletActionButton(btnWalletSeed, Colors.NAV_MENU_ABOUT_COLOR);
        themeWalletActionButton(btnRestoreWallet, Colors.NAV_MENU_WALLET_COLOR);
        cmbWallets.setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                list.setSelectionBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
                list.setSelectionForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
                JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
                if (isSelected) {
                    component.setForeground(Color.WHITE);
                    component.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
                }
                component.setPreferredSize(new Dimension(component.getPreferredSize().height, 30));
                return component;
            }
        });

        Object child = cmbWallets.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup) child;
        popup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
        
        
        cmbCurrencies.setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                list.setSelectionBackground(ResourcesProvider.Colors.APP_BG_COLOR);
                list.setSelectionForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
                JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
                if (isSelected) {
                    component.setForeground(Color.WHITE);
                    component.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
                }
                component.setPreferredSize(new Dimension(component.getPreferredSize().height, 30));
                return component;
            }
        });

        Object child1 = cmbCurrencies.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup1 = (BasicComboPopup) child1;
        popup1.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
        
        
        // fee pref combo box
        cmbFeePref.setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                list.setSelectionBackground(ResourcesProvider.Colors.APP_BG_COLOR);
                list.setSelectionForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
                JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
                if (isSelected) {
                    component.setForeground(Color.WHITE);
                    component.setBackground(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR);
                }
                component.setPreferredSize(new Dimension(component.getPreferredSize().height, 30));
                return component;
            }
        });

        Object feePrefChild = cmbFeePref.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup feePrefPopup = (BasicComboPopup) feePrefChild;
        feePrefPopup.setBorder(BorderFactory.createLineBorder(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR));
        

        themeWalletPropertyHeader(lblAccountName);
        themeWalletPropertyHeader(lblWalletName);
        themeWalletPropertyValue(lblWalletNameValue);
        themeWalletPropertyHeader(lblWalletLocation);
        themeWalletPropertyValue(lblWalletLocationValue);
        themeWalletPropertyHeader(lblCreationDate);
        themeWalletPropertyValue(lblWalletCreationDateValue);
        themeWalletPropertyHeader(lblWalletPassphrase);
        themeWalletPropertyValue(lblWalletPassphraseValue);
        themeWalletPropertyHeader(lblWalletStatus);
        themeWalletPropertyValue(lblWalletStatusValue);
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
     * function that apply theme to header lablel
     * @param label 
     */
    private void themeWalletPropertyHeader(JLabel label) {
        label.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        label.setForeground(Colors.DEFAULT_HEADING_COLOR);
    }

    /**
     * function that apply theme to value label
     * @param label 
     */
    private void themeWalletPropertyValue(JLabel label) {
        label.setFont(ResourcesProvider.Fonts.REGULAR_MEDIUM_FONT);
        label.setForeground(Colors.DEFAULT_HEADING_COLOR);
    }

    /**
     * function to apply initial settings
     */
    @Override
    public void loadData() {
        Config config = ConfigManager.config();
        rdoMainNet.setSelected("MAINNET".equals(config.getDefaultNetwork()));
        rdoTestNet.setSelected("TESTNET".equals(config.getDefaultNetwork()));
        chkUseTor.setSelected(config.isUseTor());
        model.removeAllElements();
        List<HDAccount> accounts = WalletManager.get().getCurentWalletService().getAllAccounts();
        for (HDAccount account : accounts) {
            model.addElement(account);
        }
        WalletService wService = WalletManager.get().getCurentWalletService();
        if (wService != null) {
            int index = model.getIndexOf(wService.getCurrentAccount());
            if (index >= 0) {
                cmbWallets.setSelectedIndex(index);
            }
            renderServiceProperties(wService);
        }
        
        
        for (String currency : currencies) {
            currencyModel.addElement(currency.toUpperCase());
        }
        String currency = ConfigManager.config().getSelectedCurrency();
        if (currency != null) {
            currencyModel.setSelectedItem(currency.toUpperCase());
        }
        ConfigManager.config().setCurrencies(currencies);
        
        // fee pref combobox
        if(feeModel.getSize() == 0 )
        {
            for (String fee : feePref) {
                feeModel.addElement(fee);
            }
        }
        String feePref = ConfigManager.config().getSelectedFeePref();
        if (feePref != null) {
            feeModel.setSelectedItem(feePref);
        }
        loading = false;
    }

    public void renderServiceProperties(WalletService service) {
        if (service != null) {
            ////lblWalletNameValue.setText(service.getWalletConfig().getId());
            if (!service.isTerminated()) {
                if (service.isSetupcompleted() && service.isNetworkSync()) {
                    lblWalletStatusValue.setText("Running ...");
                } else {
                    lblWalletStatusValue.setText("Loading ...");
                }
            } else {
                lblWalletStatusValue.setText("Stopped ...");
            }
            lblWalletLocationValue.setText(service.getWalletConfig().getLocation() + service.getWalletConfig().getId());
            try {
                Wallet wallet = service.getWallet();
                if (wallet != null) {
                    lblWalletPassphraseValue.setText(wallet.isEncrypted() ? "*****" : "Not Protected");
                    //long seconds = wallet.getKeyChainSeed().getCreationTimeSeconds();
                    long seconds = HDKey.EPOCH;
                    Date creationDate = new Date(seconds * 1000);
                    lblWalletCreationDateValue.setText(new SimpleDateFormat("dd/MM/yyyy").format(creationDate));
                } else {
                    lblWalletPassphraseValue.setText("--");
                    lblWalletCreationDateValue.setText("--");
                }
            } catch (Exception e) {
                logger.error("Can't render wallet [{}] fully. Loading ...", service.getWalletConfig().getId());
            }
        } else {
            clearServiceProperties();
        }
    }

    private void clearServiceProperties() {
        lblWalletNameValue.setText("--");
        lblWalletCreationDateValue.setText("--");
    }
    
    /**
     * function to apply settings upon network change 
     */
    private void changeNetwork() {
        Config config = ConfigManager.config();
        String currentNetwork = config.getDefaultNetwork();
        boolean currentUseTor = config.isUseTor();
        String selectedNetwork = rdoMainNet.isSelected() ? "MAINNET" : "TESTNET";
        boolean useTor = chkUseTor.isSelected();
        if (currentNetwork.equals(selectedNetwork) && useTor == currentUseTor) {
            return;
        }
        YesNoDialog dialog = new YesNoDialog("<html>Wallet will be reloaded to apply network changes.<br/>Do you want to Proceed?</html>");
        dialog.start();
        if (dialog.getSelectedOption() == YesNoDialog.OPTION_YES) {
            if(!currentNetwork.equals(selectedNetwork)) {
                try {
                    ConfigManager.config().setDefaultNetwork(rdoMainNet.isSelected() ? "MAINNET" : "TESTNET");
                    ConfigManager.get().save();
                    WalletConfig wallet = ConfigManager.get().getFirstWallet();
                    if( wallet == null ) {
                        WalletManager.get().getCurentWalletService().closeWallet(true);
                        DlgCreateWallet dlgCreateWallet = new DlgCreateWallet();
                        dlgCreateWallet.centerOnScreen();
                        dlgCreateWallet.setVisible(true);
                        WalletService service = dlgCreateWallet.getWalletService();
                        if (service == null) {
                            logger.debug("No wallet created. Application will exit.");
                            ApplicationUI.get().showError("Unable to create wallet. Application will exit.");
                            System.exit(1);
                        }
                        DlgWalletLoadingProgress progress = new DlgWalletLoadingProgress(service);
                        progress.start();
                        service.applyAllTransactionsToHDWallet();
                        service.ensureLookAhead();
                        service.saveWallet();
                        ApplicationUI.get().getWalletScreen().loadData();
                        ApplicationUI.get().getWalletScreen().showWalletControls();
                        ApplicationUI.get().getSettingsScreen().loadData();
                    }
                    else {
                        WalletManager.get().getCurentWalletService().reloadApplication();
                        ApplicationUI.get().getWalletScreen().loadData();
                        ApplicationUI.get().getWalletScreen().showWalletControls();
                        ApplicationUI.get().getSettingsScreen().loadData();
                    }
                } catch (Exception e) {
                    ApplicationUI.get().showError("Error loading/creating Wallet: {}", e.getMessage());
                    logger.error("Error creating wallet: {}", e.getMessage(), e);
                    System.exit(1);
                }
            }
            else {
                    try {
                        ConfigManager.config().setUseTor(chkUseTor.isSelected());
                        ConfigManager.get().save();
                        WalletManager.get().getCurentWalletService().switchTorNetwork();
                        ApplicationUI.get().getWalletScreen().loadData();
                        ApplicationUI.get().getWalletScreen().showWalletControls();
                        ApplicationUI.get().getSettingsScreen().loadData();
                    } catch(Exception e) {
                        ApplicationUI.get().showError("Error switching Tor network: {}", e.getMessage());
                        logger.error("Error switching Tor network: {}", e.getMessage(), e);
                        System.exit(1);
                    }
            }
 
        } else {
            rdoMainNet.setSelected("MAINNET".equals(currentNetwork));
            rdoTestNet.setSelected("TESTNET".equals(currentNetwork));
            chkUseTor.setSelected(currentUseTor);
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
        java.awt.GridBagConstraints gridBagConstraints;

        btnGroupNetwork = new javax.swing.ButtonGroup();
        pnlMain = new javax.swing.JPanel();
        pnlNetwork = new javax.swing.JPanel();
        pnlGeneralSettings = new javax.swing.JPanel();
        pnlNetworkSelection = new javax.swing.JPanel();
        rdoMainNet = new javax.swing.JRadioButton();
        rdoTestNet = new javax.swing.JRadioButton();
        lblNetworkWarning = new javax.swing.JLabel();
        lblNetworkSettingsDescription = new javax.swing.JLabel();
        chkUseTor = new javax.swing.JCheckBox();
        pnlPasswordSettings = new javax.swing.JPanel();
        pnlWalletSettings = new javax.swing.JPanel();
        pnlWalletDetails = new javax.swing.JPanel();
        pnlServiceProperties = new javax.swing.JPanel();
        lblWalletName = new javax.swing.JLabel();
        lblWalletNameValue = new javax.swing.JLabel();
        lblWalletLocation = new javax.swing.JLabel();
        lblWalletLocationValue = new javax.swing.JLabel();
        lblWalletStatus = new javax.swing.JLabel();
        lblWalletStatusValue = new javax.swing.JLabel();
        lblWalletPassphrase = new javax.swing.JLabel();
        lblWalletPassphraseValue = new javax.swing.JLabel();
        lblCreationDate = new javax.swing.JLabel();
        lblWalletCreationDateValue = new javax.swing.JLabel();
        pnlSpace = new javax.swing.JPanel();
        pnlWalletControls = new javax.swing.JPanel();
        btnDeleteWallet = new javax.swing.JButton();
        btnManagePassphrase = new javax.swing.JButton();
        btnWalletSeed = new javax.swing.JButton();
        pnlWalletChooser = new javax.swing.JPanel();
        lblAccountName = new javax.swing.JLabel();
        cmbWallets = new javax.swing.JComboBox();
        btnRestoreWallet = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        pnlCurrency = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbCurrencies = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbFeePref = new javax.swing.JComboBox();
        pnlTop = new javax.swing.JPanel();
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlTopEdge = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlMain.setOpaque(false);
        pnlMain.setLayout(new java.awt.GridBagLayout());

        pnlNetwork.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "General Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, ResourcesProvider.Fonts.BOLD_LARGE_FONT, ResourcesProvider.Colors.DEFAULT_HEADING_COLOR));
        pnlNetwork.setOpaque(false);
        pnlNetwork.setLayout(new java.awt.BorderLayout());

        pnlGeneralSettings.setOpaque(false);
        pnlGeneralSettings.setLayout(new java.awt.BorderLayout());

        pnlNetworkSelection.setOpaque(false);
        pnlNetworkSelection.setLayout(new java.awt.GridBagLayout());

        btnGroupNetwork.add(rdoMainNet);
        rdoMainNet.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        rdoMainNet.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        rdoMainNet.setText("Main Net");
        rdoMainNet.setOpaque(false);
        rdoMainNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoMainNetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        pnlNetworkSelection.add(rdoMainNet, gridBagConstraints);

        btnGroupNetwork.add(rdoTestNet);
        rdoTestNet.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        rdoTestNet.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        rdoTestNet.setText("Test Net");
        rdoTestNet.setOpaque(false);
        rdoTestNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoTestNetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlNetworkSelection.add(rdoTestNet, gridBagConstraints);

        lblNetworkWarning.setFont(ResourcesProvider.Fonts.REGULAR_SMALL_FONT);
        lblNetworkWarning.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblNetworkWarning.setText("(Change in Bitcoin Network settings requires wallet reload to take effect)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlNetworkSelection.add(lblNetworkWarning, gridBagConstraints);

        lblNetworkSettingsDescription.setFont(ResourcesProvider.Fonts.REGULAR_MEDIUM_FONT);
        lblNetworkSettingsDescription.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblNetworkSettingsDescription.setText("Change Bitcoin network");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlNetworkSelection.add(lblNetworkSettingsDescription, gridBagConstraints);

        chkUseTor.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        chkUseTor.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        chkUseTor.setText("Use Tor to establish connection with Bitcoin Network");
        chkUseTor.setOpaque(false);
        chkUseTor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseTorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlNetworkSelection.add(chkUseTor, gridBagConstraints);

        pnlGeneralSettings.add(pnlNetworkSelection, java.awt.BorderLayout.CENTER);

        pnlPasswordSettings.setOpaque(false);
        pnlPasswordSettings.setLayout(new java.awt.GridBagLayout());
        pnlGeneralSettings.add(pnlPasswordSettings, java.awt.BorderLayout.EAST);

        pnlNetwork.add(pnlGeneralSettings, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(pnlNetwork, gridBagConstraints);

        pnlWalletSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Wallet Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, ResourcesProvider.Fonts.BOLD_LARGE_FONT, ResourcesProvider.Colors.DEFAULT_HEADING_COLOR));
        pnlWalletSettings.setOpaque(false);
        pnlWalletSettings.setLayout(new java.awt.BorderLayout());

        pnlWalletDetails.setOpaque(false);
        pnlWalletDetails.setLayout(new java.awt.GridBagLayout());

        pnlServiceProperties.setOpaque(false);
        pnlServiceProperties.setLayout(new java.awt.GridBagLayout());

        lblWalletName.setText("Account Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletName, gridBagConstraints);

        lblWalletNameValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblWalletNameValue.setText("...");
        lblWalletNameValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletNameValue, gridBagConstraints);

        lblWalletLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblWalletLocation.setText("Wallet Location:");
        lblWalletLocation.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletLocation, gridBagConstraints);

        lblWalletLocationValue.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletLocationValue, gridBagConstraints);

        lblWalletStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblWalletStatus.setText("Wallet Status:");
        lblWalletStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletStatus, gridBagConstraints);

        lblWalletStatusValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblWalletStatusValue.setText("...");
        lblWalletStatusValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletStatusValue, gridBagConstraints);

        lblWalletPassphrase.setText("Passphrase:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletPassphrase, gridBagConstraints);

        lblWalletPassphraseValue.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletPassphraseValue, gridBagConstraints);

        lblCreationDate.setText("Wallet Creation Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblCreationDate, gridBagConstraints);

        lblWalletCreationDateValue.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(lblWalletCreationDateValue, gridBagConstraints);

        pnlSpace.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlServiceProperties.add(pnlSpace, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlWalletDetails.add(pnlServiceProperties, gridBagConstraints);

        pnlWalletControls.setOpaque(false);

        btnDeleteWallet.setText("Change Wallet Password");
        btnDeleteWallet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetApplicationPasswordActionPerformed(evt);
            }
        });
        pnlWalletControls.add(btnDeleteWallet);

        btnManagePassphrase.setText("Re-synchronize Wallet");
        btnManagePassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplayChainActionPerformed(evt);
            }
        });
        pnlWalletControls.add(btnManagePassphrase);

        btnWalletSeed.setText("Wallet Seed");
        btnWalletSeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWalletSeedActionPerformed(evt);
            }
        });
        pnlWalletControls.add(btnWalletSeed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        pnlWalletDetails.add(pnlWalletControls, gridBagConstraints);

        pnlWalletSettings.add(pnlWalletDetails, java.awt.BorderLayout.CENTER);

        pnlWalletChooser.setBackground(Colors.SCREEN_TOP_PANEL_BG_COLOR);
        pnlWalletChooser.setOpaque(false);
        pnlWalletChooser.setLayout(new java.awt.GridBagLayout());

        lblAccountName.setText("Account Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlWalletChooser.add(lblAccountName, gridBagConstraints);

        cmbWallets.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbWallets.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbWallets.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbWallets.setModel(model);
        cmbWallets.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        cmbWallets.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbWallets.setPreferredSize(new java.awt.Dimension(175, 20));
        cmbWallets.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbWalletsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlWalletChooser.add(cmbWallets, gridBagConstraints);
        cmbWallets.setUI((ComboBoxUI) WalletComboBoxUI.createUI(cmbWallets));

        btnRestoreWallet.setText("Rename Account");
        btnRestoreWallet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestoreWalletActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlWalletChooser.add(btnRestoreWallet, gridBagConstraints);

        pnlWalletSettings.add(pnlWalletChooser, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(pnlWalletSettings, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(jPanel1, gridBagConstraints);

        pnlCurrency.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preferences ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, ResourcesProvider.Fonts.BOLD_LARGE_FONT, ResourcesProvider.Colors.DEFAULT_HEADING_COLOR));
        pnlCurrency.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        jLabel1.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        jLabel1.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        jLabel1.setText("Currency:");
        pnlCurrency.add(jLabel1);

        cmbCurrencies.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbCurrencies.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbCurrencies.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbCurrencies.setModel(currencyModel);
        cmbCurrencies.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbCurrencies.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCurrencies.setPreferredSize(new java.awt.Dimension(100, 20));
        cmbCurrencies.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCurrenciesItemStateChanged(evt);
            }
        });
        pnlCurrency.add(cmbCurrencies);
        cmbCurrencies.setUI((ComboBoxUI) WalletComboBoxUI.createUI(cmbCurrencies, ResourcesProvider.Colors.APP_BG_COLOR));

        jLabel2.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        jLabel2.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        jLabel2.setText("Fee:");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        pnlCurrency.add(jLabel2);

        cmbFeePref.setBackground(ResourcesProvider.Colors.SCREEN_TOP_PANEL_BG_COLOR);
        cmbFeePref.setFont(ResourcesProvider.Fonts.BOLD_LARGE_FONT);
        cmbFeePref.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        cmbFeePref.setModel(feeModel);
        cmbFeePref.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbFeePref.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbFeePref.setPreferredSize(new java.awt.Dimension(175, 20));
        cmbFeePref.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbFeePrefItemStateChanged(evt);
            }
        });
        pnlCurrency.add(cmbFeePref);
        cmbFeePref.setUI((ComboBoxUI) WalletComboBoxUI.createUI(cmbFeePref, ResourcesProvider.Colors.APP_BG_COLOR));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMain.add(pnlCurrency, gridBagConstraints);

        add(pnlMain, java.awt.BorderLayout.CENTER);

        pnlTop.setBackground(Colors.SCREEN_TOP_PANEL_BG_COLOR);
        pnlTop.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.NAV_MENU_ITEM_BORDER_COLOR));
        pnlTop.setPreferredSize(new java.awt.Dimension(1024, 50));
        pnlTop.setLayout(new java.awt.BorderLayout());

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(Fonts.BOLD_SMALL_FONT);
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings_16x16.png"))); // NOI18N
        lblTitle.setText("SETTINGS");
        lblTitle.setToolTipText("");
        lblTitle.setIconTextGap(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(lblTitle, gridBagConstraints);

        pnlTop.add(pnlTitle, java.awt.BorderLayout.EAST);

        pnlTopEdge.setBackground(Colors.NAV_MENU_SETTINGS_COLOR);
        pnlTopEdge.setPreferredSize(new java.awt.Dimension(1024, 5));
        pnlTop.add(pnlTopEdge, java.awt.BorderLayout.NORTH);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void rdoMainNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoMainNetActionPerformed
        changeNetwork();
    }//GEN-LAST:event_rdoMainNetActionPerformed

    private void rdoTestNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoTestNetActionPerformed
        changeNetwork();
    }//GEN-LAST:event_rdoTestNetActionPerformed

    private void cmbWalletsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbWalletsItemStateChanged
        /*if (cmbWallets.getSelectedItem() != null) {
            WalletService service = (WalletService) cmbWallets.getSelectedItem();
            renderServiceProperties(service);
        }*/
        //if (evt.getStateChange() == ItemEvent.SELECTED) {
            //if (!(evt.getItem() instanceof HDAccount)) {
              //  return;
           // }
            HDAccount hdacct = (HDAccount) evt.getItem();
            if (hdacct != null) {
                lblWalletNameValue.setText(hdacct.getAccountPath());
            }
       // }
    }//GEN-LAST:event_cmbWalletsItemStateChanged

    private void btnResetApplicationPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetApplicationPasswordActionPerformed
        DlgManageApplicationPassword dialog = new DlgManageApplicationPassword();
        dialog.centerOnScreen();
        dialog.setVisible(true);
    }//GEN-LAST:event_btnResetApplicationPasswordActionPerformed

    private void btnRestoreWalletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestoreWalletActionPerformed
        try {
            WalletService wService = WalletManager.get().getCurentWalletService();
            DlgRenameAccount dialog = new DlgRenameAccount(wService, cmbWallets.getSelectedItem().toString());
            dialog.centerOnScreen();
            dialog.setVisible(true);
            model.removeAllElements();
            
            List<HDAccount> accounts = WalletManager.get().getCurentWalletService().getAllAccounts();
            for (HDAccount account : accounts) {
                model.addElement(account);
            }
                                
            if (wService != null) {
                int index = model.getIndexOf(wService.getAccount(dialog.getNewAccountName()));
                if (index >= 0) {
                    cmbWallets.setSelectedIndex(index);
                }
            }
        } catch (Exception e) {
            logger.error("Restore Waller Error: {}", e, e);
        }
        
    }//GEN-LAST:event_btnRestoreWalletActionPerformed

    private void chkUseTorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseTorActionPerformed
        changeNetwork();
    }//GEN-LAST:event_chkUseTorActionPerformed

    private void btnWalletSeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWalletSeedActionPerformed
        DlgWalletSeedQRCode dlgWsQrCode = new DlgWalletSeedQRCode();
        dlgWsQrCode.centerOnScreen();
        dlgWsQrCode.setVisible(true);
    }//GEN-LAST:event_btnWalletSeedActionPerformed

    private void btnReplayChainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplayChainActionPerformed
        // TODO add your handling code here:
        YesNoDialog dialog = new YesNoDialog("<html>This will reset all your trasactions in the Wallet and will resynchronize it. This may take several minutes.<br/>Do you want to Proceed?</html>");
        dialog.start();
        if (dialog.getSelectedOption() == YesNoDialog.OPTION_YES) {
            WalletManager.get().getCurentWalletService().replayBlockChain();
        }
    }//GEN-LAST:event_btnReplayChainActionPerformed

    private void cmbCurrenciesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCurrenciesItemStateChanged
        // TODO add your handling code here:
        if (loading) {
            return;
        }
        if (evt.getStateChange() == ItemEvent.SELECTED && cmbCurrencies.getSelectedItem() != null) {
            String currency = (String) currencyModel.getSelectedItem();
            try {
                ConfigManager.config().setSelectedCurrency(currency);
                ConfigManager.get().save();
                ApplicationUI.get().getDashboardScreen().getDashboardGraph().loadPriceGraph();
                //loadPriceGraph();
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationUI.get().showError(e);
            }
        }
    }//GEN-LAST:event_cmbCurrenciesItemStateChanged

    private void cmbFeePrefItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbFeePrefItemStateChanged
        // TODO add your handling code here:
        if (loading) {
            return;
        }
        if (evt.getStateChange() == ItemEvent.SELECTED && cmbFeePref.getSelectedItem() != null) {
            String feePref = (String) feeModel.getSelectedItem();
            try {
                ConfigManager.config().setSelectedFeePref(feePref);
                ConfigManager.get().save();
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationUI.get().showError(e);
            }
        }
    }//GEN-LAST:event_cmbFeePrefItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteWallet;
    private javax.swing.ButtonGroup btnGroupNetwork;
    private javax.swing.JButton btnManagePassphrase;
    private javax.swing.JButton btnRestoreWallet;
    private javax.swing.JButton btnWalletSeed;
    private javax.swing.JCheckBox chkUseTor;
    private javax.swing.JComboBox cmbCurrencies;
    private javax.swing.JComboBox cmbFeePref;
    private javax.swing.JComboBox cmbWallets;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAccountName;
    private javax.swing.JLabel lblCreationDate;
    private javax.swing.JLabel lblNetworkSettingsDescription;
    private javax.swing.JLabel lblNetworkWarning;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWalletCreationDateValue;
    private javax.swing.JLabel lblWalletLocation;
    private javax.swing.JLabel lblWalletLocationValue;
    private javax.swing.JLabel lblWalletName;
    private javax.swing.JLabel lblWalletNameValue;
    private javax.swing.JLabel lblWalletPassphrase;
    private javax.swing.JLabel lblWalletPassphraseValue;
    private javax.swing.JLabel lblWalletStatus;
    private javax.swing.JLabel lblWalletStatusValue;
    private javax.swing.JPanel pnlCurrency;
    private javax.swing.JPanel pnlGeneralSettings;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlNetwork;
    private javax.swing.JPanel pnlNetworkSelection;
    private javax.swing.JPanel pnlPasswordSettings;
    private javax.swing.JPanel pnlServiceProperties;
    private javax.swing.JPanel pnlSpace;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopEdge;
    private javax.swing.JPanel pnlWalletChooser;
    private javax.swing.JPanel pnlWalletControls;
    private javax.swing.JPanel pnlWalletDetails;
    private javax.swing.JPanel pnlWalletSettings;
    private javax.swing.JRadioButton rdoMainNet;
    private javax.swing.JRadioButton rdoTestNet;
    // End of variables declaration//GEN-END:variables
}
