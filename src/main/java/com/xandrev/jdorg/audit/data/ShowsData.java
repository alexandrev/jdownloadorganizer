package com.xandrev.jdorg.audit.data;

import com.google.gson.JsonObject;
import com.xandrev.jdorg.audit.AuditConstants;

import java.util.Date;

/**
 * Created by alexa_000 on 25/01/14.
 */
public class ShowsData extends AuditData {

    private int season;

    private int episode;

    public ShowsData() {
        setType(AuditConstants.TV_SHOW_TYPE);
    }

    public ShowsData(String show, String item, String file) {
        setTimestamp(new Date());
        setType(AuditConstants.TV_SHOW_TYPE);
        setItemName(item);
        setShowsName(show);
        setFileName(file);
    }

    public JsonObject toJSON() {
        JsonObject tmpJson = new JsonObject();
        tmpJson.addProperty("filename", getFileName());
        tmpJson.addProperty("show", getShowsName());
        tmpJson.addProperty("type", getType());
        tmpJson.addProperty("timestamp", getTimestamp() == null? "":getTimestamp().toString());
        tmpJson.addProperty("season", getSeason());
        tmpJson.addProperty("episode", getEpisode());
        tmpJson.addProperty("status", getStatus());
        tmpJson.addProperty("renamed", isRenamed());
        tmpJson.addProperty("copied", isCopied());
        tmpJson.addProperty("deleted", isDeleted());
        return tmpJson;
    }

    /**
     * @return the season
     */
    public int getSeason() {
        return season;
    }

    /**
     * @param season the season to set
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * @return the episode
     */
    public int getEpisode() {
        return episode;
    }

    /**
     * @param episode the episode to set
     */
    public void setEpisode(int episode) {
        this.episode = episode;
    }

    @Override
    public void setCustomData(String customData) {
        if(customData != null){
            String[] data = customData.split(",");
            if(data != null && data.length >= 2){
                setSeason(Integer.parseInt(data[0]));
                setEpisode(Integer.parseInt(data[1]));
            }
        }
    }

    @Override
    public String getCustomData() {
        return season+","+episode;
    }
}
