package com.xandrev.jdorg.organizers.impl;

import com.google.gson.JsonObject;
import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.data.FileData;
import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.organizers.Organizer;
import com.xandrev.jdorg.organizers.files.impl.FileOrganizerConfiguration;
import com.xandrev.jdorg.organizers.files.impl.FileOrganizerConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Movie organizer for JDownloadOrganizer (JDORG)
 *
 * @author xandrev.com
 *
 */
public class FileOrganizer implements Organizer {

    private String name;
    private String type;

    private String rootFolder;
    private int priority;
    private static final Logger LOG = LogManager.getLogger(TVShowsOrganizer.class);
    private final HashMap<String, String> extensionsFolder;
    private final Collection<String> extensionList;

    public FileOrganizer() {

        Configuration config = Configuration.getInstance();
        rootFolder = config.getProperty(FileOrganizerConfiguration.ROOT_FOLDER_CONFIGURATION);
        if (rootFolder == null) {
            rootFolder = FileOrganizerConstants.ROOT_FOLDER_DEFAULT_VALUE;
        }

        String priorityStr = config.getProperty(FileOrganizerConfiguration.PRIORITY_CONFIGURATION);
        if (priorityStr == null) {
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException ex) {
                priority = FileOrganizerConstants.PRIORITY_DEFAULT_VALUE;
            }
        }

        extensionList = new ArrayList<String>();
        extensionsFolder = new HashMap();
        String extensionsStr = config.getProperty(FileOrganizerConfiguration.EXTENSION_CONFIGURATION);
        if (extensionsStr == null || extensionsStr.isEmpty()) {
            extensionsStr = FileOrganizerConstants.EXTENSION_DEFAULT_VALUE;
        }

        name = FileOrganizerConstants.NAME_ORGANIZER;
        type = FileOrganizerConstants.TYPE_ORGANIZER;

        parseExtension(extensionsStr);

        LOG.info("Priority: " + priority);

    }

    /**
     * Method that return the root folder for this organizer
     *
     * @return root folder name
     */
    public String getRootFolder() {
        return rootFolder;
    }

    /**
     * Method that indicate the folder name which the item has to be located
     *
     * @param fileName item to relocate
     * @return folder name to relocate the item.
     */
    public String generateFolder(String fileName) {
        String finalFolder = null;
        LOG.debug("Starting to generate folder");
        if (fileName != null && !fileName.isEmpty()) {
            LOG.debug("Starting to work with file:" + fileName);
            String extension = FilenameUtils.getExtension(fileName);
            LOG.debug("Extracted extension from file: " + extension);
            if (extension != null && !extension.isEmpty()) {
                String folder = extensionsFolder.get(extension);
                LOG.debug("Recovered folder from extension: " + folder);
                if (folder != null && !folder.isEmpty()) {
                    finalFolder = rootFolder + File.separator + folder;
                }
            }
        }
        LOG.debug("Final path:" + finalFolder);
        return finalFolder;
    }

    public int getPriority() {
        return priority;
    }

    public int compareTo(Object t) {
        if (t instanceof Organizer) {
            return getPriority() - ((Organizer) t).getPriority();
        }
        return -1;
    }

    public void audit(Audit auditService, String origPath, String finalPath, boolean renamed, boolean copied, boolean deleted) {
        FileData mv = new FileData(origPath, finalPath);
        mv.setRenamed(renamed);
        mv.setCopied(copied);
        mv.setDeleted(deleted);
        auditService.auditAction(mv);
    }

    private void parseExtension(String extensionsStr) {
        if (extensionsStr != null && !extensionsStr.isEmpty()) {
            String[] extensionArray = extensionsStr.split(";");
            if (extensionArray != null) {
                LOG.debug("Extension array length: " + extensionArray.length);
                for (String extension : extensionArray) {
                    String[] extParsed = extension.split("=");
                    if (extParsed != null && extParsed.length == 2) {
                        LOG.debug("Extension parsed length: " + extParsed.length);
                        String folder = extParsed[0];
                        String values = extParsed[1];
                        LOG.debug("Folder: " + folder);
                        LOG.debug("Value: " + values);
                        String[] valueArray = values.split(",");
                        if (valueArray != null && valueArray.length > 0) {
                            for (String val : valueArray) {
                                if (!extensionsFolder.containsKey(val)) {
                                    LOG.debug("Adding a new extension folder for extension: " + val);
                                    extensionsFolder.put(val, folder);
                                    extensionList.add(val);
                                    LOG.debug("Added a new extension folder for extension");
                                }
                            }
                        }
                    }
                }
            }
            LOG.debug("Final parsed size: " + extensionsFolder.size());
        }

    }

    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", getName());
        obj.addProperty("type", getType());
        return obj;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public Collection<File> getFiles(File initialFolder) {
        Collection<File> out = new ArrayList<File>();
        if (initialFolder != null && !initialFolder.exists()) {
            if (extensionList.size() > 0) {
                String[] tmpArray = new String[extensionList.size()];
                out = FileUtils.listFiles(initialFolder, extensionList.toArray(tmpArray), true);
            }
        }
        return out;
    }
}
