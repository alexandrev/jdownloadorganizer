/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xandrev.jdorg.audit.data;

import com.google.gson.JsonObject;
import com.xandrev.jdorg.audit.AuditConstants;
import java.util.Date;

/**
 *
 * 
 */
public class MovieData extends AuditData{
    
    public MovieData(String movie,String file){
            setTimestamp(new Date());
            setType(AuditConstants.MOVIE_TYPE);
            setShowsName(movie);
            setFileName(file);
    }
    
     public JsonObject toJSON() {
        JsonObject tmpJson = new JsonObject();
        tmpJson.addProperty("filename", getFileName());
        tmpJson.addProperty("show", getShowsName());
        tmpJson.addProperty("type", getType());
        tmpJson.addProperty("timestamp", getTimestamp() == null? "":getTimestamp().toString());
        tmpJson.addProperty("status", getStatus());
        tmpJson.addProperty("renamed", isRenamed());
        tmpJson.addProperty("copied", isCopied());
        tmpJson.addProperty("deleted", isDeleted());
        return tmpJson;
    }

    @Override
    public void setCustomData(String customData) {
     }

    @Override
    public String getCustomData() {
        return "";
    }
}
