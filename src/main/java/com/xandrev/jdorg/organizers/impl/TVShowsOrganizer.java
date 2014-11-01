package com.xandrev.jdorg.organizers.impl;

import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.data.ShowsData;
import com.xandrev.jdorg.configuration.Configuration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xandrev.jdorg.organizers.Organizer;
import com.xandrev.jdorg.organizers.tvshows.impl.TVShowsOrganizerConfiguration;
import com.xandrev.jdorg.organizers.tvshows.impl.TVShowsOrganizerConstants;
import java.io.File;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * TV Show organizer for JDownloadOrganizer (JDORG)
 *
 * @author xandrev.com
 *
 */
public class TVShowsOrganizer implements Organizer {

    private String rootFolder = "";
    private String pattern = "";
    private final Configuration config = Configuration.getInstance();
    private static final Logger logger = LogManager.getLogger(TVShowsOrganizer.class);
    private int priority;
    private final boolean folderSeason;

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
}
