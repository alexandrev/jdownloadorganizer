package com.xandrev.jdorg.organizers.service;

import com.xandrev.jdorg.configuration.Configuration;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.xandrev.jdorg.i18n.TextLocalizerManager;
import com.xandrev.jdorg.organizers.Organizer;
import java.util.Collections;

public class OrganizerManager {

    private static OrganizerManager instance;
    private List<Organizer> organizerList;
    private Logger logger;

    private static final String ORGANIZER_SEPARATOR = ",";
    private static final String PACKAGE_NAME = "com.xandrev.jdorg.organizers.impl.";
    private static final Object LOCK = new Object();

    public static OrganizerManager getInstance(String configurationList) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new OrganizerManager(configurationList);
            }
        }
        return instance;
    }

    protected OrganizerManager(String configurationList) {
        TextLocalizerManager i18n = TextLocalizerManager.getInstance(null);
        logger = LogManager.getLogger(OrganizerManager.class);
        organizerList = new ArrayList<Organizer>();
        logger.debug(i18n.getLocalizerText("init.organizermanager"));
        if (configurationList != null) {
            logger.debug(i18n.getLocalizerText("organizermanager.read.list") + configurationList);
            String[] listString = configurationList.split(ORGANIZER_SEPARATOR);
            for (String str : listString) {
                logger.debug(i18n.getLocalizerText("organizermanager.process.item") + str);
                Organizer organizerTarget = generateOrganizer(str);
                if (organizerTarget != null) {
                    logger.debug(i18n.getLocalizerText("organizermanager.item.added") + str);
                    organizerList.add(organizerTarget);
                }
            }
        }
        
        Collections.sort(organizerList);
        for(Organizer org: organizerList){
            logger.debug("Organizer: "+org.getRootFolder() + " Priority: "+ org.getPriority());
        }
        
        logger.debug(i18n.getLocalizerText("organizermanager.finish"));

    }

    private Organizer generateOrganizer(String str) {
        if (str != null) {
            String normalClass = PACKAGE_NAME + str;
            try {
                Object classObject = Class.forName(normalClass).newInstance();
                if (classObject instanceof Organizer) {
                    return (Organizer) classObject;
                }

            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        return null;
    }

    public List<Organizer> getOrganizerList() {
        return organizerList;
    }
}
