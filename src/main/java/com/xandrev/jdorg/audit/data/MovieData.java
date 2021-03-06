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
    
    @Override
     public JsonObject toJSON() {
        JsonObject tmpJson = super.toJSON();
        tmpJson.addProperty("show", getShowsName());
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
