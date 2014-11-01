/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.main;

import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.impl.AuditImpl;
import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.configuration.Constants;
import com.xandrev.jdorg.i18n.TextLocalizerManager;
import com.xandrev.jdorg.organizers.Organizer;
import com.xandrev.jdorg.organizers.service.OrganizerManager;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

/**
 *
 * 
 */
public class ExecutorService {

    private static ExecutorService instance;
    private static final Object LOCK = new Object();

    private final Configuration cfg;
    private final OrganizerManager manager;
    private final List<Organizer> organizerList;
    private final List extensionsAllowed;
    private final String finalDirectory;
    private static final Logger logger =  Logger.getLogger(ExecutorThread.class);
    private final TextLocalizerManager i18n;
    private final String initialDirectory;
    private final Audit auditService;

    public static ExecutorService getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ExecutorService();
            }
        }
        return instance;
    }
    private Date executionTime;

    private ExecutorService() {
        executionTime = new Date();
        auditService = AuditImpl.getInstance();
        cfg = Configuration.getInstance();
        String organizerListString = cfg.getProperty(Constants.ORGANIZER_NAMES);
        manager = OrganizerManager.getInstance(organizerListString);
        organizerList = manager.getOrganizerList();
        extensionsAllowed = cfg.getAllowedExtensions();
        finalDirectory = cfg.getProperty(Constants.FINAL_FOLDER);
        String language = cfg.getProperty(Constants.LANGUAGE);
        initialDirectory = cfg.getProperty(Constants.INITIAL_FOLDER);
        Locale locale = Locale.getDefault();
        if (language != null && !language.equals("")) {
            locale = new Locale(language);
        }
        i18n = TextLocalizerManager.getInstance(locale);

    }

    public void applyExistentFiles() {
        applyExistentFiles(initialDirectory);
        executionTime = new Date();
    }

    public void applyExistentFiles(String initialDirectory) {
        File file = new File(initialDirectory);
        logger.debug("Starting to apply the organization to the folder: " + file.getAbsolutePath());
        Configuration cfg = Configuration.getInstance();
        String videoExt = (String) cfg.getProperty("general.extensions.video");
        if (videoExt != null) {
            logger.debug("Recovering the video extensions: " + videoExt);
            String[] videoExtArray = videoExt.split(",");
            if (videoExtArray != null) {
                String[] fileList = FileUtils.getFilesFromExtension(file.getAbsolutePath(), videoExtArray);
                if (fileList != null) {
                    logger.debug("Recovering the files with useful extensions: " + Arrays.deepToString(fileList));
                    for (String fileTmp : fileList) {
                        logger.debug("File to organize: " + fileTmp + " . Start");
                        applyOrganizers(FileUtils.extension(fileTmp), new File(fileTmp));
                        logger.debug("File to organize: " + fileTmp + " . End");
                    }
                    logger.debug("Finished the recovered files organization");
                }
            }
        }
        auditService.log("INFO","Executed organization");
        logger.debug("Finished the organization to the selected folder");
    }

    private void applyOrganizers(String extension, File fd) {
        if (extensionsAllowed.contains(extension)) {
            for (Organizer org : organizerList) {
                logger.info("Organizer: " + org.getClass().toString());
                boolean out = false;
                String folder = org.generateFolder(fd.getName());
                if (folder != null) {
                    logger.info("Folder: " + folder);
                    out = true;
                    logger.info("Root Folder: " + folder);
                    String finalPath = finalDirectory + File.separator + folder;
                    File dirPath = new File(finalPath);
                    logger.info("Final Path: " + finalPath);
                    if (!dirPath.exists()) {
                        boolean result = dirPath.mkdirs();
                        logger.debug("Resultado de la creacion de directorios: " + result);
                    }
                    File finalFile = new File(finalPath + File.separator + fd.getName());
                    if (!finalFile.exists()) {
                        try {
                            logger.info("Final path not exist. Copying the file at: " + finalFile.getAbsolutePath());
                            String origPath = fd.getAbsolutePath();
                            boolean renamed = fd.renameTo(finalFile);
                            boolean deleted = false;
                            boolean copied = false;
                            logger.info("Renamed attempt result successfully: "+renamed);
                            if(!renamed){
                                logger.info("Trying to copy the file to: "+finalFile.getAbsolutePath());
                                FileUtils.copyFile(fd, finalFile);
                                copied = finalFile.exists();
                                deleted = fd.delete();
                                logger.info("Copy finished: "+finalFile.getAbsolutePath());
                            }
                            
                            logger.info(i18n.getLocalizerText("file.rename.1") + fd.getAbsolutePath() + i18n.getLocalizerText("file.rename.2") + finalFile.getAbsolutePath());
                            logger.info("Starting to audit the file organized");
                            org.audit(auditService, origPath, finalPath,renamed,copied,deleted);
                            logger.info("Audit the file organized completed");
                        } catch (IOException ex) {
                            logger.error(ex);
                        }
                    }

                    
                    if (!fd.delete()) {
                        try {
                            FileDeleteStrategy.FORCE.delete(fd);
                        } catch (IOException e) {
                            logger.error(e);
                        }
                    }

                }
                if (out) {
                    break;
                }
            }
        }
    }

    public String getTime() {
        return executionTime.toString();
    }

}
