package com.xandrev.jdorg.organizers.impl;

import com.google.gson.JsonObject;
import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.data.ShowsData;
import com.xandrev.jdorg.configuration.Configuration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xandrev.jdorg.organizers.Organizer;
import com.xandrev.jdorg.organizers.tvshows.impl.TVShowsOrganizerConfiguration;
import com.xandrev.jdorg.organizers.tvshows.impl.TVShowsOrganizerConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * TV Show organizer for JDownloadOrganizer (JDORG)
 *
 * @author xandrev.com
 *
 */
public class TVShowsOrganizer implements Organizer {

    private String name;
    private String type;
    
    private String rootFolder = "";
    private String pattern = "";
    private final Configuration config = Configuration.getInstance();
    private static final Logger logger = LogManager.getLogger(TVShowsOrganizer.class);
    private int priority;
    private final boolean folderSeason;
    private final Collection<String> extensionList;

    public TVShowsOrganizer() {
        rootFolder = config.getProperty(TVShowsOrganizerConfiguration.ROOT_FOLDER_CONFIGURATION);
        if (rootFolder == null) {
            rootFolder = TVShowsOrganizerConstants.ROOT_FOLDER_DEFAULT_VALUE;
        }

        pattern = config.getProperty(TVShowsOrganizerConfiguration.PATTERN_CONFIGURATION);
        if (pattern == null || pattern.isEmpty()) {
            pattern = TVShowsOrganizerConstants.PATTERN_DEFAULT_VALUE;
        }

        String priorityStr = config.getProperty(TVShowsOrganizerConfiguration.PRIORITY_CONFIGURATION);
        if (priorityStr == null) {
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException ex) {
                priority = TVShowsOrganizerConstants.PRIORITY_DEFAULT_VALUE;
            }
        }

        String folderSeasonString = config.getProperty(TVShowsOrganizerConfiguration.FOLDER_SEASON_CONFIGURATION);
        if (folderSeasonString == null) {
            folderSeasonString = TVShowsOrganizerConstants.FOLDER_SEASON_CONFIGURATION_DEFAULT_VALUE;
        }
        folderSeason = Boolean.parseBoolean(folderSeasonString);

        logger.debug("Pattern: "+pattern);
        
        extensionList = new ArrayList<String>();
        
         String extensionsStr = config.getProperty(TVShowsOrganizerConfiguration.EXTENSIONS_CONFIGURATION);
        if (extensionsStr == null || extensionsStr.isEmpty()) {
            extensionsStr = TVShowsOrganizerConstants.EXTENSION_DEFAULT_VALUE;
        }

        name = TVShowsOrganizerConstants.NAME_ORGANIZER;
        type = TVShowsOrganizerConstants.TYPE_ORGANIZER;

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
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(fileName);
        boolean prueba = m.matches();
        logger.debug("Filename : " + fileName + " matches: "+prueba);
        if (prueba) {
            
            String serie = m.group(1);
            serie = serie.replaceAll("\\.", " ");
            logger.debug("TV Show extracted: " + serie);
            if (serie != null) {
                serie = serie.trim();
            }

            if (!folderSeason) {
                return rootFolder + File.separator + serie;
            } else {
                String season = m.group(3);
                if (season == null || season.isEmpty()) {
                    season = m.group(5);
                }

                int seasonInt = -1;
                try {
                    seasonInt = Integer.parseInt(season);
                } catch (NumberFormatException ex) {
                    logger.warn(ex);
                }
                if (seasonInt > 0) {
                    return rootFolder + File.separator + serie + File.separator + "Season " + seasonInt;
                } else {
                    return rootFolder + File.separator + serie + File.separator + "Unknown Season ";
                }

            }
        }
        logger.debug("No TV Show extracted");
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
        logger.debug("Original file path: " + origPath);
        ShowsData sw = extractFileName(origPath);
        sw.setRenamed(renamed);
        sw.setCopied(copied);
        sw.setDeleted(deleted);
        logger.debug("Starting audit action");
        auditService.auditAction(sw);
        logger.debug("Completed audit action");
    }

    private ShowsData extractFileName(String origPath) {
        ShowsData out = new ShowsData();
        Pattern p = Pattern.compile(pattern);

        File fTmp = new File(origPath);
        String itemName = fTmp.getName();
        Matcher m = p.matcher(itemName);
        boolean prueba = m.matches();
        if (prueba) {
            String serie = m.group(1);
            logger.debug("Serie extracted: " + serie);
            serie = serie.replaceAll("\\.", " ");
            if (serie != null) {
                serie = serie.trim();
            }
            out.setShowsName(serie);

            String season = m.group(3);
            if (season == null || season.isEmpty()) {
                season = m.group(5);
            }

            int seasonInt = -1;
            try {
                seasonInt = Integer.parseInt(season);
            } catch (NumberFormatException ex) {
                logger.warn(ex);
            }

            out.setSeason(seasonInt);

            String episode = m.group(4);
            if (episode == null || episode.isEmpty()) {
                episode = m.group(6);
            }

            int episodeInt = -1;
            try {
                episodeInt = Integer.parseInt(episode);
            } catch (NumberFormatException ex) {
                logger.warn(ex);
            }

            out.setEpisode(episodeInt);

        }

        out.setFileName(origPath);

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
