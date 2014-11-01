package com.xandrev.jdorg.main;

import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.configuration.Constants;
import com.xandrev.jdorg.gui.JDOrgTrayIcon;
import com.xandrev.jdorg.i18n.TextLocalizerManager;
import com.xandrev.jdorg.service.api.OrganizeService;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Main class of JDOrg
 *
 * @author xandrev.com
 *
 */
public final class JDorgStarter {

    private static final Logger logger = LogManager.getLogger(JDorgStarter.class);
    private static OrganizeService api;

    private JDorgStarter() {
    }

    /**
     * Main method of JDOrg
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Configuration cfg = Configuration.getInstance();

            String configFilePath = Constants.CONFIG_FILE_PATH;
            boolean inner = true;

            if (args.length > 0) {
                configFilePath = args[0];
                inner = false;
            }
            
            logger.debug("Loading configuration form internal file: "+inner + " / "+ configFilePath);
            Properties prop = cfg.loadProperties(configFilePath, inner);
            if(prop.isEmpty()){
                logger.warn("Empty configuration. We use the default file to configure the application");
                prop = cfg.loadProperties(Constants.CONFIG_FILE_PATH, true);
            }

            JDOrgTrayIcon.loadTrayIcon(prop);

            ExecutorThread listener1 = new ExecutorThread();
            Thread fm = new Thread(listener1);
            fm.start();
            api = new OrganizeService();
            api.init();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(JDorgStarter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
