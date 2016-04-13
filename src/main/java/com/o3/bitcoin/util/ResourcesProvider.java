/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Utility class that provides different resources values used in application 
 */
public class ResourcesProvider {

    public static final String APP_TITLE = "o3Wallet";
    public static final String APP_VERSION = "1.0.4 Beta";
    public static final int APP_MAJOR = 1;
    public static final int APP_MINOR = 0;
    public static final int APP_MINOR_MINOR = 4;
    public static final String APP_TITLE_VERSION = APP_TITLE + " - " + APP_VERSION;

    public static final List<String> DEFAULT_CURRENCIES = Arrays.asList(new String[]{"AUD", "BRL", "CAD", "CNY", "EUR","GBP","HKD","IDR","ILS","INR","JPY","MXN","NOK","NZD","PLN","RON","RUB","SEK","SGD","USD","ZAR"});
    public static final List<String> FEE_PREF = Arrays.asList(new String[]{"bitcoinfees.21.com", "api.blockcypher.com"});
    public static HashMap<String,String> FEE_PREF_URLs = new HashMap<String, String>() 
    {
        {
            put("bitcoinfees.21.com","https://bitcoinfees.21.co/api/v1/fees/recommended");
            put("api.blockcypher.com","http://api.blockcypher.com/v1/btc/main");
        }
    };

    public static final String DEFAULT_CURRENCY = "USD";
    public static final String DEFAULT_FEE_PREF = "bitcoinfees.21.com";
    private static Map<String, Image> images = new HashMap();
    private static Map<String, ImageIcon> icons = new HashMap();
    private static Map<String, Font> fonts = new HashMap();

    /**
     * interface that provides different Dimensions values used in application
     */
    public static interface Dimensions {

        public static final Dimension BASE_UI_DIMENSION = new Dimension(1024, 700);
        public static final int BASE_NAV_MENU_ITEM_WIDTH = 175;
        public static final int BASE_NAV_MENU_ITEM_HEIGHT = 50;
        public static final int BASE_NAV_MENU_WIDTH = 175;
        public static final int BASE_NAV_MENU_HEIGHT = BASE_UI_DIMENSION.height;
        public static final int TITLE_BAR_HEIGHT = 25;
        public static final int TITLE_BAR_NAV_HEIGHT = 50;
    }

    /**
     * interface that provides different Colors values used in application
     */
    public static interface Colors {

        public static final Color APP_BG_COLOR = new Color(242, 242, 240);

        public static final Color NAV_MENU_BG_COLOR = new Color(243, 242, 233);
        public static final Color NAV_MENU_RIGHT_BORDER_COLOR = new Color(212, 213, 206);
        public static final Color NAV_MENU_ITEM_BORDER_COLOR = new Color(232, 232, 223);
        public static final Color DEFAULT_HEADING_COLOR = new Color(120, 122, 121);
        public static final Color DEFAULT_HEADING_COLOR1 = new Color(115, 195, 129);
        public static final Color LIGHT_HEADING_COLOR = new Color(212, 211, 202);
        public static final Color NAV_MENU_DASHBOARD_COLOR = new Color(225, 95, 104);
        public static final Color NAV_MENU_WALLET_COLOR = new Color(102, 170, 223);
        public static final Color NAV_MENU_CONTACTS_COLOR = new Color(115, 195, 129);
        public static final Color NAV_MENU_EXCHANGE_COLOR = new Color(149, 107, 207);
        public static final Color NAV_MENU_SETTINGS_COLOR = new Color(99, 112, 225);
        public static final Color NAV_MENU_ABOUT_COLOR = new Color(217, 173, 104);

        public static final Color TITLE_BAR_BG_COLOR = new Color(204, 204, 204);
        public static final Color TITLE_BAR_NAV_BG_COLOR = new Color(55, 55, 55);
        public static final Color TITLE_BAR_NAV_BUTTON_BIG_BG_COLOR = new Color(39, 39, 39);
        public static final Color TITLE_BAR_NAV_BUTTON_COLOR = new Color(203, 203, 203);

        public static final Color SCREEN_TOP_PANEL_BG_COLOR = new Color(248, 246, 242);

        public static final Color DASHBOARD_WALLET_BALANCE_COLOR = new Color(218, 99, 98);

        public static final Color TABLE_HEADER_BG_COLOR = new Color(241, 243, 247);

        public static final Color TABLE_EVEN_ROW_BG_COLOR = new Color(237, 239, 243);
        public static final Color TABLE_EVEN_ODD_BG_COLOR = new Color(235, 238, 244);
        public static final Color TABLE_CELL_BORDER_COLOR = new Color(227, 228, 232);
        public static final Color TABLE_CELL_TEXT_COLOR = new Color(165, 170, 185);
        public static final Color TABLE_CELL_TEXT_COLOR1 = new Color(102, 170, 223);
        
        public static final Color RATE_GRAPH_BG_COLOR = new Color(241, 233, 230);
    }

    /**
     * interface that provides different Font values used in application
     */
    public static interface Fonts {

        public static final Font MYRIAD_PRO_REGULAR_FONT = new Font("Arial", Font.PLAIN, 11); //getFont("myriadpro-regular").deriveFont(Font.PLAIN, 12f);
        public static final Font MYRIAD_PRO_BOLD_FONT = new Font("Arial", Font.BOLD, 11);//getFont("myriadpro-bold").deriveFont(Font.BOLD, 12f);

        public static final Font DEFAULT_FONT = MYRIAD_PRO_REGULAR_FONT;

        public static final Font REGULAR_SMALL_FONT = MYRIAD_PRO_REGULAR_FONT;
        public static final Font BOLD_SMALL_FONT = MYRIAD_PRO_BOLD_FONT;

        public static final Font REGULAR_MEDIUM_FONT = MYRIAD_PRO_REGULAR_FONT.deriveFont(13f);
        public static final Font BOLD_MEDIUM_FONT = MYRIAD_PRO_BOLD_FONT.deriveFont(13f);

        public static final Font REGULAR_LARGE_FONT = MYRIAD_PRO_REGULAR_FONT.deriveFont(15f);
        public static final Font BOLD_LARGE_FONT = MYRIAD_PRO_BOLD_FONT.deriveFont(15f);

        public static final Font DEFAULT_HEADING_FONT = BOLD_MEDIUM_FONT;
        public static final Font NAV_MENU_ITEM_FONT = BOLD_SMALL_FONT;

        public static final Font DASHBOARD_WALLENT_BALANCE_FONT = BOLD_LARGE_FONT.deriveFont(26f);

        public static final Font WALLETS_STATISTICS_FONT = BOLD_LARGE_FONT.deriveFont(20f);
        
        public static final Font WALLETS_TOTAL_CR_DR_FONT = BOLD_LARGE_FONT.deriveFont(22f);
    }
    
    /**
     * interface that provides License text used in about page
     */
    public static interface License {
        public static final String licenseText = "<html><center> <blockquote style=\"text-align:center;padding:20px;padding-top:0px;margin:0 auto;\"><b style=\"font-size:1.2em\">O3 Wallet Version: 1.0.4 Beta</b><br> <p>Except logo and name, Software is Distributed under following licence:</p><br><p><b style=\"font-size:1.2em\">The MIT Licence (MIT)</b><br>Copyright (c) 2016 O3 wallet technology Pty Ltd<br></p><p><br>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:<br><br>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.<br><br>THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.</p></blockquote></center></html>";
    }
    
    

    public static Image getImage(String key) {
        Image image = images.get(key);
        if (image == null) {
            String imageName = key;
            if (imageName != null && !imageName.isEmpty()) {
                imageName += key.contains(".") ? "" : ".png";
            }
            image = Utils.loadClassPathImage("images/" + imageName);
            images.put(key, image);
        }
        return image;
    }

    public static ImageIcon getIcon(String key) {
        ImageIcon icon = icons.get(key);
        if (icon == null) {
            String iconName = key;
            if (iconName != null && !iconName.isEmpty()) {
                iconName += key.contains(".") ? "" : ".png";
            }
            icon = Utils.loadClassPathIcon("icons/" + iconName);
            icons.put(key, icon);
        }
        return icon;
    }

    public static ImageIcon getIcon(String key, int size) {
        ImageIcon icon = icons.get(key);
        if (icon == null) {
            String iconName = key;
            if (iconName != null && !iconName.isEmpty()) {
                if (size > 0) {
                    iconName += "_" + size + "x" + size;
                }
                iconName += key.contains(".") ? "" : ".png";
            }
            icon = Utils.loadClassPathIcon("icons/" + iconName);
            icons.put(key + "_" + size, icon);
        }
        return icon;
    }

    public static Font getFont(String key) {
        Font font = fonts.get(key);
        if (font == null) {
            String fontName = key;
            if (fontName != null && !fontName.isEmpty()) {
                fontName += key.contains(".") ? "" : ".ttf";
            }
            font = Utils.loadClassPathFont("fonts/" + fontName);
            fonts.put(key, font);
        }
        return font;
    }
}
