/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui.dialogs;

import com.o3.bitcoin.ui.component.XButtonFactory;
import com.o3.bitcoin.util.ResourcesProvider;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.util.StringUtils;

/**
 * <p>Class that implements utility ui dialog</p>
*/
public class YesNoDialog extends BasicDialog {

    public static final int OPTION_YES = 0;
    public static final int OPTION_NO = 1;

    private int option = OPTION_NO;

    private String message;
    private String heading = "Confirmation Required";
    private JLabel lblMessage = null;
    private boolean showCancelButton = true;
    private boolean copyText = false;

    public YesNoDialog(String message) {
        this(null, message);
    }

    public YesNoDialog(String heading, String message) {
        super(true);
        this.message = message;
        this.heading = heading;
    }
    
    public YesNoDialog(String heading, String message, boolean showCancelButton) {
        super(true);
        this.message = message;
        this.heading = heading;
        this.showCancelButton = showCancelButton;
    }
    
    public YesNoDialog(String heading, String message, boolean showCancelButton, boolean copyText) {
        super(true);
        this.message = message;
        this.heading = heading;
        this.showCancelButton = showCancelButton;
        this.copyText = copyText;
    }

    public void start() {
        setupUI();
        setCopyText();
        setHeading(getHeadingText());
        setSize(425, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void setCopyText() {
        if( copyText ) {
            lblMessage.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblMessage.setToolTipText("Click to copy text on clipboard");
            lblMessage.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    String address = lblMessage.getText();
                    address = address.replaceAll("<html>", "");
                    address = address.replaceAll("</html>", "");
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(new StringSelection(address), null);
                }
            });
        }
    }

    @Override
    protected JPanel getMainContentPanel() {
        JPanel pnlContents = new JPanel(new BorderLayout());
        if (message != null && !message.isEmpty()) {
            if (!message.startsWith("<html>")) {
                message = "<html>" + message + "</html>";
            }
        }
        lblMessage = new JLabel(message);
        lblMessage.setHorizontalAlignment(JLabel.CENTER);
        lblMessage.setHorizontalTextPosition(JLabel.CENTER);
        lblMessage.setFont(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT);
        lblMessage.setForeground(ResourcesProvider.Colors.DEFAULT_HEADING_COLOR);
        lblMessage.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlContents.add(lblMessage, BorderLayout.CENTER);
        pnlContents.setOpaque(false);
        return pnlContents;
    }

    @Override
    protected String getHeadingText() {
        return heading;
    }

    @Override
    protected List<JButton> getControls() {
        List<JButton> controls = super.getControls();
        if( !showCancelButton)
            controls.get(0).setVisible(false);
        controls.add(0, getOkButton());
        return controls;
    }

    protected JButton getOkButton() {
        JButton nextButton = new JButton("Ok");
        XButtonFactory.themedButton(nextButton)
                .background(ResourcesProvider.Colors.NAV_MENU_WALLET_COLOR)
                .color(Color.WHITE)
                .font(ResourcesProvider.Fonts.BOLD_MEDIUM_FONT).
                size(XButtonFactory.NORMAL_BUTTON_DIMENSION);
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                handleYesButonActionEvent(e);
            }
        });
        return nextButton;
    }

    @Override
    protected String getCloseButtonText() {
        return "Cancel";
    }

    private void handleYesButonActionEvent(ActionEvent e) {
        this.option = OPTION_YES;
        super.handleDefaultCloseEvent(e);
    }

    public int getSelectedOption() {
        return this.option;
    }
}
