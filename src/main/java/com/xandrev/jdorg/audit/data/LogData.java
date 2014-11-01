/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.audit.data;

import com.google.gson.JsonObject;
import java.util.Date;

/**
 *
 * @author alexa_000
 */
public class LogData {
    private Date date;
    private String level;
    private String message;

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public JsonObject toJSON() {
        JsonObject tmpJson = new JsonObject();
        tmpJson.addProperty("level", getLevel());
        tmpJson.addProperty("message", getMessage());
        tmpJson.addProperty("timestamp", getDate() == null? "":getDate().toString());
        return tmpJson;
    }
    
    
}
