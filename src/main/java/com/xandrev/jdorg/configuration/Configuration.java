package com.xandrev.jdorg.configuration;

import com.xandrev.jdorg.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

                String videoStr = (String) prop.get(Constants.VIDEO_EXTENSIONS);
                if (videoStr != null && !videoStr.equals("")) {
                    videoExtensions = new ArrayList<String>(Arrays.asList(videoStr.split(",")));
                }

                String subtitleStr = (String) prop.get(Constants.SUBTITLE_EXTENSIONS);
                if (subtitleStr != null && !subtitleStr.equals("")) {
                    subtitleExtensions = new ArrayList<String>(Arrays.asList(subtitleStr.split(",")));
                }
                logger.debug("Configuration loaded...");

            } catch (IOException e) {
                logger.error(e);
            }
        }
        return prop;
    }

    public void saveProperties(String fileName) {
        logger.debug("Saving properties...");
        URL urlConfigFile = Configuration.class.getClassLoader().getResource(fileName);
        OutputStreamWriter fw = null;
        try {

            fw = new OutputStreamWriter(new FileOutputStream(new File(urlConfigFile.toURI())), "UTF-8");

            String videoExtension = Utils.toString(videoExtensions);
            String subtitleExtension = Utils.toString(subtitleExtensions);

            prop.setProperty(Constants.VIDEO_EXTENSIONS, videoExtension);
            prop.setProperty(Constants.SUBTITLE_EXTENSIONS, subtitleExtension);

            prop.store(fw, "");

            logger.debug("Properties saved...");
        } catch (IOException e) {
            logger.error(e);
        } catch (URISyntaxException e) {
            logger.error(e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }

    public Properties getProperties() {
        return prop;
    }

    public boolean addVideoExtension(String result) {
        if (!videoExtensions.contains(result)) {
            videoExtensions.add(result);
            return true;
        }
        return false;
    }

    public boolean removeVideoExtension(String extension) {
        if (videoExtensions.contains(extension)) {
            videoExtensions.remove(extension);
            return true;
        }
        return false;

    }

    public boolean removeSubtitleExtension(String extension) {
        if (subtitleExtensions.contains(extension)) {
            subtitleExtensions.remove(extension);
            return true;
        }
        return false;

    }

    public boolean addSubtitleExtension(String result) {
        if (!subtitleExtensions.contains(result)) {
            subtitleExtensions.add(result);
            return true;
        }
        return false;
    }

    public List getAllowedExtensions() {
        String extensionsArray = prop.getProperty(Constants.VIDEO_EXTENSIONS);
        String subtitleArray = prop.getProperty(Constants.SUBTITLE_EXTENSIONS);
        String extensionCompleteString = extensionsArray + "," + subtitleArray;
        String[] extensions = extensionCompleteString.split(",");
        return new ArrayList(Arrays.asList(extensions));
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
