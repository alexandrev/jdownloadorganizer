package com.xandrev.jdorg.gui;

import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.impl.AuditImpl;
import com.xandrev.jdorg.features.Feature;
import com.xandrev.jdorg.i18n.TextLocalizerManager;
import com.xandrev.jdorg.utils.Utils;

import java.awt.*;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Main class to generate and control the system tray icon for JDOrg
 *
 * @author xandrev.com
 *
 */
public final class JDOrgTrayIcon {

    /**
     * Static instance of the tray icon
     */
    private static TrayIcon trayIcon;

    /**
     * Instance of the logger
     */
    private static Logger logger = LogManager.getLogger(JDOrgTrayIcon.class);

    private JDOrgTrayIcon() {

    }

    /**
     * Method that initialize and load the trayIcon
     */
    public static boolean loadTrayIcon(Properties confProperties) {

        TextLocalizerManager i18n = TextLocalizerManager.getInstance(null);
        if (trayIcon == null) {
            SystemTray tray = SystemTray.getSystemTray();
            ImageIcon image = new ImageIcon(JDOrgTrayIcon.class.getResource("/com/xandrev/jdorg/main/icon.gif"));
            PopupMenu popup = new PopupMenu();

            MenuItem preferencesItem = new MenuItem(i18n.getLocalizerText("gui.preferences.title"));
            MenuItem defaultItem = new MenuItem(i18n.getLocalizerText("gui.exit.title"));

            Menu featuresMenu = new Menu(i18n.getLocalizerText("gui.features.title"));

            loadFeatures(featuresMenu, confProperties);

            MenuItem auditItem = new MenuItem(i18n.getLocalizerText("gui.exit.audit"));
            popup.add(featuresMenu);
            popup.add(preferencesItem);
            popup.add(auditItem);
            popup.add(defaultItem);

            trayIcon = new TrayIcon(image.getImage(), i18n.getLocalizerText("gui.title"), popup);
            trayIcon.setImageAutoSize(true);

            try {
                tray.add(trayIcon);
                return true;
            } catch (AWTException e) {
                logger.error(e);
            }

        }
        return false;
    }

    private static void loadFeatures(Menu featuresMenu,
            Properties confProperties) {
        TextLocalizerManager i18n = TextLocalizerManager.getInstance(null);

        String features = (String) confProperties.get("features");
        if (features == null || features.equals("")) {
            return;
        }

        String[] featureList = features.split(",");
        for (String feature : featureList) {
            try {
                final Feature f1 = (Feature) Class.forName(Utils.createFeatureClassName(feature)).newInstance();
                MenuItem featureItem = new MenuItem(i18n.getLocalizerText("gui." + feature.toLowerCase() + ".title"));
                featuresMenu.add(featureItem);
                featureItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        f1.activateFeature();
                    }
                });
            } catch (InstantiationException ex) {
                logger.error(ex);
            } catch (IllegalAccessException ex) {
                logger.error(ex);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }

        }
    }

    /**
     * Method that display a message linked to the system icon tray
     *
     * @param string Title of the message
     * @param string2 Message text
     * @param info Icon to be visualized
     */
    public static void displayMessage(String string, String string2,
            MessageType info) {
        trayIcon.displayMessage(string, string2, info);

    }
}
