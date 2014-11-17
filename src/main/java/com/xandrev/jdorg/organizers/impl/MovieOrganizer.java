package com.xandrev.jdorg.organizers.impl;

import com.google.gson.JsonObject;
import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.data.MovieData;
import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.organizers.Organizer;
import com.xandrev.jdorg.organizers.movies.impl.MovieOrganizerConfiguration;
import com.xandrev.jdorg.organizers.movies.impl.MovieOrganizerConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Movie organizer for JDownloadOrganizer (JDORG)
 *
 * @author xandrev.com
 *
 */
public class MovieOrganizer implements Organizer {

    private String name;
      private String type;
    
    private String rootFolder;
    private int priority;
    private boolean ownFolder;
    private static final Logger LOG = LogManager.getLogger(MovieOrganizer.class);
    private final Collection<String> extensionList;

    public MovieOrganizer() {

        Configuration config = Configuration.getInstance();
        rootFolder = config.getProperty(MovieOrganizerConfiguration.ROOT_FOLDER_CONFIGURATION);
        if (rootFolder == null) {
            rootFolder = MovieOrganizerConstants.ROOT_FOLDER_DEFAULT_VALUE;
        }

        String priorityStr = config.getProperty(MovieOrganizerConfiguration.PRIORITY_CONFIGURATION);
        if (priorityStr == null) {
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException ex) {
                priority = MovieOrganizerConstants.PRIORITY_DEFAULT_VALUE;
            }
        }

        LOG.info("Priority: " + priority);

        String ownFolderString = config.getProperty(MovieOrganizerConfiguration.OWN_FOLDER_MOVIE);
        if (ownFolderString == null) {
            ownFolderString = MovieOrganizerConstants.OWN_FOLDER_MOVIE_DEFAULT_VALUE;
        }
        ownFolder = Boolean.parseBoolean(ownFolderString);
        
        extensionList = new ArrayList<String>();

        LOG.info("Own Folder: " + ownFolder);

        String extensionsStr = config.getProperty(MovieOrganizerConfiguration.EXTENSIONS_CONFIGURATION);
        if (extensionsStr == null || extensionsStr.isEmpty()) {
            extensionsStr = MovieOrganizerConstants.EXTENSION_DEFAULT_VALUE;
        }

        name = MovieOrganizerConstants.NAME_ORGANIZER;
        type = MovieOrganizerConstants.TYPE_ORGANIZER;

        parseExtension(extensionsStr);

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

        if (!ownFolder) {
            return rootFolder;
        } else {
            int index = fileName.lastIndexOf('.');
            if (index != -1) {
                String folder = fileName.substring(0, index);
                return rootFolder + File.separator + folder.trim();
            }
        }
        return null;
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
        String moviName = extractMovieName(origPath);
        MovieData mv = new MovieData(moviName, finalPath);
        mv.setRenamed(renamed);
        mv.setCopied(copied);
        mv.setDeleted(deleted);
        auditService.auditAction(mv);
    }

    public String extractMovieName(String origPath) {
        String out = "";
        if (origPath != null) {
            int idxOf = origPath.lastIndexOf(File.separatorChar);
            int lstIndexOf = origPath.lastIndexOf('.');
            if (lstIndexOf < 0) {
                lstIndexOf = origPath.length();
            }

            String tmp = origPath.substring(idxOf + 1, lstIndexOf);
            tmp = tmp.replace('.', ' ');
            tmp = tmp.replaceAll("\\([^\\)]*\\)", "");
            out = tmp.trim();
        }
        return out;
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
    
    private void parseExtension(String extensionsStr) {
        if(extensionsStr != null && !extensionsStr.isEmpty()){
            String[] extList = extensionsStr.split(",");
            if(extList != null){
                extensionList.addAll(Arrays.asList(extList));
            }
        }
    }
}
