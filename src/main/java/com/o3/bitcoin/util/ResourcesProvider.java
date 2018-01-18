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
    public static final String APP_VERSION = "1.1.1 Beta";
    public static final String VERSION_INFO_URL = "https://version.o3wallet.com/version.htm";
    public static final int APP_MAJOR = 1;
    public static final int APP_MINOR = 1;
    public static final int APP_MINOR_MINOR = 1;
    public static final String APP_TITLE_VERSION = APP_TITLE + " - " + APP_VERSION;

    public static final List<String> DEFAULT_CURRENCIES = Arrays.asList(new String[]{"AUD", "BRL", "CAD", "CNY", "EUR","GBP","HKD","IDR","ILS","INR","JPY","MXN","NOK","NZD","PLN","RON","RUB","SEK","SGD","USD","ZAR"});
    public static final List<String> FEE_PREF = Arrays.asList(new String[]{"bitcoinfees.21.com", "api.blockcypher.com"});
    public static final List<String> EXCHANGES = Arrays.asList(new String[]{"BTCMarkets","Bitstamp"});
    public static HashMap<String, String> CURRENCY_SHORT_CODES = new HashMap<String, String>() {
        {
            put("Bitcoin", "BTC");
            put("Bitshares", "BTS");
            put("Blackcoin", "BLK");
            put("BitCrystals", "BCY");
            put("Clams", "CLAM");
            put("Dash", "DASH");
            put("Dogecoin", "DOGE");
            put("Digibyte", "DGB");
            put("Emercoin", "EMC");
            put("Ether", "ETH");
            put("Factoids", "FCT");
            put("GEMZ", "GEMZ");
            put("Litecoin", "LTC");
            put("Monacoin", "MONA");
            put("Nubits", "NBT");
            put("Novacoin", "NVC");
            put("Nxt", "NXT");
            put("Peercoin", "PPC");
            put("Reddcoin", "RDD");
            put("Shadowcash", "SDC");
            put("StorjX", "SJCX");
            put("Startcoin", "START");
            put("Vertcoin", "VTC");
            put("Counterparty", "XCP");
            put("Monero", "XMR");
            put("Ripple", "XRP");
        }
    };
    public static HashMap<String, String> CURRENCY_ICONS = new HashMap<String, String>() {
        {
            put("Ripple", "/icons/ripple_16x16.png");
            put("BitCrystals", "/icons/bitcrystals_16x16.png");
            put("Blackcoin", "/icons/blackcoin_16x16.png");
            put("Bitshares", "/icons/bitshares_16x16.png");
            put("Clams", "/icons/clams_16x16.png");
            put("Dash", "/icons/dash_16x16.png");
            put("Dogecoin", "/icons/dogecoin_16x16.png");
            put("Digibyte", "/icons/digibyte_16x16.png");
            put("Emercoin", "/icons/emercoin_16x16.png");
            put("Ether", "/icons/ether_16x16.png");
            put("Factoids", "/icons/factoids_16x16.png");
            put("GEMZ", "/icons/gemz_16x16.png");
            put("Litecoin", "/icons/litecoin_16x16.png");
            put("Monacoin", "/icons/monacoin_16x16.png");
            put("Nubits", "/icons/nubits_16x16.png");
            put("Novacoin", "/icons/novacoin_16x16.png");
            put("Nxt", "/icons/nxt_16x16.png");
            put("Peercoin", "/icons/peercoin_16x16.png");
            put("Reddcoin", "/icons/reddcoin_16x16.png");
            put("Shadowcash", "/icons/shadowcash_16x16.png");
            put("StorjX", "/icons/storjcoinx_16x16.png");
            put("Startcoin", "/icons/startcoin_16x16.png");
            put("Vertcoin", "/icons/vertcoin_16x16.png");
            put("Counterparty", "/icons/counterparty_16x16.png");
            put("Monero", "/icons/monero_16x16.png");
               
        }
    };
    public static HashMap<String, String> COUNTRY_DIALING_CODES = new HashMap<String, String>() {
        {
            put("Afghanistan", "93");
            put("Albania", "355");
            put("Algeria", "213");
            put("American Samoa", "684");
            put("Andorra", "376");
            put("Angola", "244");
            put("Anguilla", "809");
            put("Antigua and Barbuda", "268");
            put("Argentina", "54");
            put("Armenia", "374");
            put("Aruba", "297");
            put("Ascension Island", "247");
            put("Australia", "61");
            put("Australian External Territories", "672");
            put("Austria", "43");
            put("Azerbaijan", "994");
            put("Bahamas", "242");
            put("Barbados", "246");
            put("Bahrain", "973");
            put("Bangladesh", "880");
            put("Belarus", "375");
            put("Belgium", "32");
            put("Belize", "501");
            put("Benin", "229");
            put("Bermuda", "809");
            put("Bhutan", "975");
            put("Virgin Islands", "284");
            put("Bolivia", "591");
            put("Bosnia and Hercegovina", "387");
            put("Botswana", "267");
            put("Brazil", "55");
            put("Brunei Darussalm", "673");
            put("Bulgaria", "359");
            put("Burkina Faso", "226");
            put("Burundi", "257");
            put("Cambodia", "855");
            put("Cameroon", "237");
            put("Canada", "1");
            put("CapeVerde Islands", "238");
            put("Caribbean Nations", "1");
            put("Cayman Islands", "345");
            put("Cape Verdi", "238");
            put("Central African Republic", "236");
            put("Chad", "235");
            put("Chile", "56");
            put("China", "86");
            put("Colombia", "57");
            put("Comoros and Mayotte", "269");
            put("Congo", "242");
            put("Cook Islands", "682");
            put("Costa Rica", "506");
            put("Croatia", "385");
            put("Cuba", "53");
            put("Curacao", "599");
            put("Cyprus", "357");
            put("Czech Republic", "420");
            put("Denmark", "45");
            put("Democratic Republic of the Congo", "243");
            put("Diego Garcia", "246");
            put("Dominca", "767");
            put("Dominican Republic", "809");
            put("Dominica", "1");
            put("Djibouti", "253");
            put("Ecuador", "593");
            put("Egypt", "20");
            put("El Salvador", "503");
            put("Equatorial Guinea", "240");
            put("Eritrea", "291");
            put("Estonia", "372");
            put("Ethiopia", "251");
            put("Falkland Islands", "500");
            put("Faroe Islands", "298");
            put("Fiji", "679");
            put("Finland", "358");
            put("France", "33");
            put("French Antilles", "596");
            put("French Guiana", "594");
            put("Gabon", "241");
            put("Gambia", "220");
            put("Georgia", "995");
            put("Germany", "49");
            put("Ghana", "233");
            put("Gibraltar", "350");
            put("Greece", "30");
            put("Greenland", "299");
            put("Grenada", "473");
            put("Guam", "671");
            put("Guatemala", "502");
            put("Guinea", "224");
            put("Guinea Bissau", "245");
            put("Guyana", "592");
            put("Haiti", "509");
            put("Honduras", "504");
            put("Hong Kong", "852");
            put("Hungary", "36");
            put("Iceland", "354");
            put("India", "91");
            put("Indonesia", "62");
            put("Iran", "98");
            put("Iraq", "964");
            put("Ireland", "353");
            put("Israel", "972");
            put("Italy", "39");
            put("Ivory Coast", "225");
            put("Jamaica", "876");
            put("Japan", "81");
            put("Jordan", "962");
            put("Kazakhstan", "7");
            put("Kenya", "254");
            put("Khmer Republic", "855");
            put("Kiribati Republic", "686");
            put("Republic of Korea", "82");
            put("People's Republic of Korea", "850");
            put("Kuwait", "965");
            put("Kyrgyz Republic", "996");
            put("Kosovo", "383");
            
            put("Latvia", "371");
            put("Laos", "856");
            put("Lebanon", "961");
            put("Lesotho", "266");
            put("Liberia", "231");
            put("Lithuania", "370");
            put("Libya", "218");
            put("Liechtenstein", "423");
            put("Luxembourg", "352");
            put("Macao", "853");
            put("Macedonia", "389");
            put("Madagascar", "261");
            put("Malawi", "265");
            put("Malaysia", "60");
            put("Maldives", "960");
            put("Mali", "223");
            put("Malta", "356");
            put("Marshall Islands", "692");
            put("Martinique", "596");
            put("Mauritania", "222");
            put("Mauritius", "230");
            put("Mayolte", "269");
            put("Mexico", "52");
            put("Micronesia", "691");
            put("Moldova", "373");
            put("Monaco", "33");
            put("Mongolia", "976");
            put("Montserrat", "473");
            put("Morocco", "212");
            put("Mozambique", "258");
            put("Myanmar", "95");
            put("Namibia", "264");
            put("Nauru", "674");
            put("Nepal", "977");
            put("Netherlands", "31");
            put("Netherlands Antilles", "599");
            put("Nevis", "869");
            put("New Caledonia", "687");
            put("New Zealand", "64");
            put("Nicaragua", "505");
            put("Niger", "227");
            put("Nigeria", "234");
            put("Niue", "683");
            put("North Korea", "850");
            put("North Mariana Islands", "1670");
            put("Norway", "47");
            put("Oman", "968");
            put("Pakistan", "92");
            put("Palau", "680");
            put("Panama", "507");
            put("Papua New Guinea", "675");
            put("Paraguay", "595");
            put("Peru", "51");
            put("Philippines", "63");
            put("Poland", "48");
            put("Portugal", "351");
            put("Puerto Rico", "1787");
            put("Qatar", "974");
            put("Reunion", "262");
            put("Romania", "40");
            put("Russiaput", "7");
            put("Rwanda", "250");
            put("Saipan", "670");
            put("San Marino", "378");
            put("Sao Tome and Principe", "239");
            put("Saudi Arabia", "966");
            put("Senegal", "221");
            put("Serbia and Montenegro", "381");
            put("Seychelles", "248");
            put("Sierra Leone", "232");
            put("Singapore", "65");
            put("Slovakia", "421");
            put("Slovenia", "386");
            put("Solomon Islands", "677");
            put("Somalia", "252");
            put("South Africa", "27");
            put("Spain", "34");
            put("Sri Lanka", "94");
            put("St. Helena", "290");
            put("St. Kitts", "869");
            put("St. Pierre &(et) Miquelon", "508");
            put("Sudan", "249");
            put("Suriname", "597");
            put("Swaziland", "268");
            put("Sweden", "46");
            put("Switzerland", "41");
            put("Syrian Arab Republic", "963");
            put("Tahiti", "689");
            put("Taiwan", "886");
            put("Tajikistan", "7");
            put("Tanzania", "255");
            put("Thailand", "66");
            put("Togo", "228");
            put("Tokelau", "690");
            put("Tonga", "676");
            put("Trinidad and Tobago", "1868");
            put("Tunisia", "216");
            put("Turkey", "90");
            put("Turkmenistan", "993");
            put("Tuvalu", "688");
            put("Uganda", "256");
            put("Ukraine", "380");
            put("United Arab Emirates", "971");
            put("United Kingdom", "44");
            put("Uruguay", "598");
            put("USA", "1");
            put("Uzbekistan", "7");
            put("Vanuatu", "678");
            put("Vatican City", "39");
            put("Venezuela", "58");
            put("VietNam", "84");
            put("Virgin Islands", "1340");
            put("Wallis and Futuna", "681");
            put("Western Samoa", "685");
            put("People's Democratic Republic of Yemen", "381");
            put("Yemen Arab Republic", "967");
            put("Zaire", "243");
            put("Zambia", "260");
            put("Zimbabwe", "263");
            put("Guadeloupe", "590");
            
            put("Kyrgyzstan", "996");
            put("Palestine", "970");
            put("Russia", "7");
            put("Samoa", "685");
            put("Serbia", "381");
            put("St Kitts and Nevis", "1");
            put("St Lucia", "1");
            put("St Vincent and Grenadines", "1");
            put("Syria", "963");
            put("Turks and Caicos", "1");
            put("Vietnam", "84");
            put("Yemen", "967");
            
            
        }
    };
    public static HashMap<String,String> FEE_PREF_URLs = new HashMap<String, String>() 
    {
        {
            put("bitcoinfees.21.com","https://bitcoinfees.21.co/api/v1/fees/recommended");
            put("api.blockcypher.com","http://api.blockcypher.com/v1/btc/main");
        }
    };
    
    public static HashMap<String,String> LTC_BTC_URLs = new HashMap<String, String>() 
    {
        {
            put("GetCoins","https://shapeshift.io/getcoins");
            put("MarketInfo","https://shapeshift.io/marketinfo/");
            put("QuickTrans","https://shapeshift.io/shift");
            put("PreciseTrans","https://shapeshift.io/sendamount");
        }
    };
    public static String bitrefillUserName = "";
    public static String bitRefillPassWord = "";
    public static String shapShiftPubKey = "";
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
        public static final Color NAV_MENU_CONTACTS_COLOR = new Color(115, 195, 129);// used at different locations, for CONTACT menu item use different color with variable
        public static final Color NAV_MENU_EXCHANGES_COLOR = new Color(115, 195, 129);
        public static final Color NAV_MENU_APPLICATIONS_COLOR = new Color(149, 107, 207);
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
        
        public static final Font EXCHANGE_STATS_LARGE_FONT = BOLD_LARGE_FONT.deriveFont(22f);
        
        public static final Font EXCHANGE_STATS_MEDIUM_FONT = BOLD_LARGE_FONT.deriveFont(18f);
    }
    
    /**
     * interface that provides License text used in about page
     */
    public static interface License {
        public static final String licenseText = "<html><center> <blockquote style=\"text-align:center;padding:20px;padding-top:0px;margin:0 auto;\"><b style=\"font-size:1.2em\">O3 Wallet Version: 1.1.1 Beta</b><br> <p>Except logo and name, Software is Distributed under following licence:</p><br><p><b style=\"font-size:1.2em\">The MIT Licence (MIT)</b><br>Copyright (c) 2017 O3 wallet technology Pty Ltd<br></p><p><br>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:<br><br>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.<br><br>THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.</p></blockquote></center></html>";
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
