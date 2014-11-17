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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.codec.digest.DigestUtils;
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
    private final String finalDirectory;
    private static final Logger logger = Logger.getLogger(ExecutorThread.class);
    private final TextLocalizerManager i18n;
    private final String initialDirectory;
    private final Audit auditService;
    private boolean isRunning = false;

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
        if (!isRunning) {
            isRunning = true;
            File file = new File(initialDirectory);
            logger.debug("Starting to apply the organization to the folder: " + file.getAbsolutePath());
            applyOrganizers(file);
            auditService.log("INFO", "Executed organization");
            logger.debug("Finished the organization to the selected folder");
            isRunning = false;
        }
    }

    private void applyOrganizers(File initialFolder) {
        ArrayList<String> list = new ArrayList<String>();
        for (Organizer org : organizerList) {
            logger.info("Organizer: " + org.getClass().toString());
            Collection<File> fileList = org.getFiles(initialFolder);
            if (fileList != null) {
                for (File fd : fileList) {
                    String folder = org.generateFolder(fd.getName());
                    if (folder != null && !list.contains(fd.getAbsolutePath())) {
                        list.add(fd.getAbsolutePath());
                        logger.info("Folder: " + folder);
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
                                String origHash = calculateHash(fd);
                                logger.debug("Original hash: "+origHash);
                                boolean renamed = fd.renameTo(finalFile);
                                boolean deleted = false;
                                boolean copied = false;
                                logger.info("Renamed attempt result successfully: " + renamed);
                                if (!renamed) {
                                    logger.info("Trying to copy the file to: " + finalFile.getAbsolutePath());
                                    FileUtils.copyFile(fd, finalFile);
                                    copied = finalFile.exists();
                                    if (copied) {
                                        String finalHash = calculateHash(finalFile);
                                        logger.debug("Final hash: "+finalHash);
                                        if (origHash != null && origHash.equals(finalHash)) {
                                            deleted = fd.delete();
                                            logger.info("Copy finished: " + finalFile.getAbsolutePath());
                                        } else {
                                            logger.info("Copy finisehd with error because hash are not equals");
                                            finalFile.delete();
                                        }
                                    }

                                }

                                logger.info(i18n.getLocalizerText("file.rename.1") + fd.getAbsolutePath() + i18n.getLocalizerText("file.rename.2") + finalFile.getAbsolutePath());
                                logger.info("Starting to audit the file organized");
                                org.audit(auditService, origPath, finalPath, renamed, copied, deleted);
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
                }
            }
        }

    }

    public String getTime() {
        return executionTime.toString();
    }

    public Collection<Organizer> getOrganizers() {
        return organizerList;
    }

    private String calculateHash(File fd) {
        String out = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fd);
            out = DigestUtils.md5Hex(fis);
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            try {
                if(fis != null){
                    fis.close();
                }
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return out;
    }

}
