/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui;

import com.o3.bitcoin.ui.dialogs.YesNoDialog;
import com.o3.bitcoin.ui.dialogs.screens.BasicScreen;
import com.o3.bitcoin.ui.screens.settings.PnlSettingsScreen;
import com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen;
import com.o3.bitcoin.util.ResourcesProvider;
import static com.o3.bitcoin.util.ResourcesProvider.Dimensions.BASE_UI_DIMENSION;
import com.o3.bitcoin.util.ResourcesProvider.Fonts;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */

/**
 * <p> Class that implements main application UI form</p>
 * <ul>
 * <li>shows ui of left side navigation</li>
 * <li>shows title bar of ui</li>
 * <li>creates screens for different navigations</li>
 * </ul>
*/
public class ApplicationUI extends javax.swing.JFrame {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUI.class);
    public static final String SCREEN_DASHBOARD = "SCREEN_DASHBOARD";
    public static final String SCREEN_WALLET = "SCREEN_WALLET";
    public static final String SCREEN_SETTINGS = "SCREEN_SETTINGS";

    private static ApplicationUI instance = null;
    private Dimension minimumDimension = new Dimension(BASE_UI_DIMENSION.width, BASE_UI_DIMENSION.height);

    private Map<String, BasicScreen> screens = new HashMap<>();

    /**
     * Creates new form ApplicationUI
    */
    private ApplicationUI() {
        resize();
        prepareUI();
        initComponents();
        prepareCache();
        revalidate();
        repaint();

    }

    private void prepareCache() {
        screens.put(SCREEN_DASHBOARD, pnlDashboardScreen);
        screens.put(SCREEN_WALLET, pnlWalletScreen);
        screens.put(SCREEN_SETTINGS, pnlSettingsScreen);
    }

    public static ApplicationUI get() {
        if (instance == null) {
            instance = new ApplicationUI();
        }
        return instance;
    }

    /**
     * function to show a specific screen on ApplictionUI form
    */
    public void showScreen(String screenName) {
        if (screens.containsKey(screenName)) {
            try {
                screens.get(screenName).loadData();
                CardLayout c = (CardLayout) pnlContents.getLayout();
                c.show(pnlContents, screenName);
            } catch (Exception e) {
                logger.error("Error loading data for screen ({}) : ", screenName, e.getMessage(), e);
                ApplicationUI.this.showError(e);
            }
        }
    }

    /**
     * function to resize ApplicationUI form
    */
    private void resize() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setMaximizedBounds(env.getMaximumWindowBounds());
        int screenWidth = (int) env.getMaximumWindowBounds().getWidth();
        int screenHeight = (int) env.getMaximumWindowBounds().getHeight();
        int minScreenWidth = Math.max(screenWidth / 2, BASE_UI_DIMENSION.width);
        int minScreenHeight = Math.max(screenHeight / 2, BASE_UI_DIMENSION.height);
        minimumDimension = new Dimension(minScreenWidth, minScreenHeight);
        setMinimumSize(minimumDimension);
    }

    private void prepareUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UIManager.getLookAndFeelDefaults().put("defaultFont", Fonts.DEFAULT_FONT);
    }

    /**
     * function to maximize ApplicationUI form
    */
    public void maximize() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            restore();
        } else {
            setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
    }

    /**
     * function to restore ApplicationUI form from minimize
    */
    public void restore() {
        setExtendedState(JFrame.NORMAL);
        setSize(minimumDimension);
    }

    /**
     * function to minimize ApplicationUI form
    */
    public void minimize() {
        setExtendedState(getExtendedState() | JFrame.ICONIFIED);
    }

    public void showError(String error) {
        showError("Error", error);
    }

    public void showError(Exception e) {
        showError("Error", e.getMessage());
    }

    public void showError(String title, Exception e) {
        showError("Error", e.getMessage());
    }

    public void showError(String title, String error) {
        YesNoDialog dialog = new YesNoDialog(title, error);
        dialog.start();
    }

    public void showSuccess(String success) {
        showMessage("Done", success);
    }

    public void showInfo(String info) {
        showMessage("Information", info);
    }

    public void showMessage(String title, String message) {
        YesNoDialog dialog = new YesNoDialog(title, message);
        dialog.start();
    }
    
    /**
     * function to get Accounts screen
     * @return PnlWalletScreen object that represent Account screen
    */
    public PnlWalletScreen getWalletScreen() {
        return pnlWalletScreen;
    }
    
    /**
     * function to get Settings screen
     * @return PnlSettingsScreen object that represent Settings screen
    */
    public PnlSettingsScreen getSettingsScreen() {
        return pnlSettingsScreen;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContents = new javax.swing.JPanel();
        pnlDashboardScreen = new com.o3.bitcoin.ui.screens.dashboard.PnlDashboardScreen();
        pnlWalletScreen = new com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen();
        pnlSettingsScreen = new com.o3.bitcoin.ui.screens.settings.PnlSettingsScreen();
        pnlLeftNavigationMenu1 = new com.o3.bitcoin.ui.component.nav.PnlLeftNavigationMenu();
        pnlTitleBar1 = new com.o3.bitcoin.ui.PnlTitleBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        pnlContents.setBackground(ResourcesProvider.Colors.APP_BG_COLOR);
        pnlContents.setLayout(new java.awt.CardLayout());
        pnlContents.add(pnlDashboardScreen, "SCREEN_DASHBOARD");
        pnlContents.add(pnlWalletScreen, "SCREEN_WALLET");
        pnlContents.add(pnlSettingsScreen, "SCREEN_SETTINGS");

        getContentPane().add(pnlContents, java.awt.BorderLayout.CENTER);
        getContentPane().add(pnlLeftNavigationMenu1, java.awt.BorderLayout.WEST);
        getContentPane().add(pnlTitleBar1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        resize();
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlContents;
    private com.o3.bitcoin.ui.screens.dashboard.PnlDashboardScreen pnlDashboardScreen;
    private com.o3.bitcoin.ui.component.nav.PnlLeftNavigationMenu pnlLeftNavigationMenu1;
    private com.o3.bitcoin.ui.screens.settings.PnlSettingsScreen pnlSettingsScreen;
    private com.o3.bitcoin.ui.PnlTitleBar pnlTitleBar1;
    private com.o3.bitcoin.ui.screens.wallet.PnlWalletScreen pnlWalletScreen;
    // End of variables declaration//GEN-END:variables
}
