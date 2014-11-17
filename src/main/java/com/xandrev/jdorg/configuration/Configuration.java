package com.xandrev.jdorg.configuration;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public final class Configuration {

    private Properties prop;
    private static Configuration instance;
    private static final Object LOCK = new Object();
    private Logger logger = LogManager.getLogger(Configuration.class);
    private List<String> videoExtensions;
    private List<String> subtitleExtensions;

    public static Configuration getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new Configuration();
            }
        }
        return instance;
    }

    private Configuration() {
        videoExtensions = new ArrayList<String>();
        subtitleExtensions = new ArrayList<String>();
    }

    public Properties loadProperties(String fileName, boolean inner) {

        prop = new Properties();
        if (fileName != null) {
            try {
                if (inner) {
                    InputStream inStream = Configuration.class.getClassLoader().getResourceAsStream(fileName);
                    if (inStream != null) {
                        prop.load(inStream);
                    }
                }
                else{
                    prop.load(new FileReader(fileName));
                }
                logger.debug("Configuration loaded...");

            } catch (IOException e) {
                logger.error(e);
            }
        }
        return prop;
    }

   

    public Properties getProperties() {
        return prop;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
