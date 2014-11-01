package com.xandrev.jdorg.audit.data;

import com.google.gson.JsonObject;
import java.util.Date;

/**
 * Created by alexa_000 on 25/01/14.
 */
public abstract class AuditData {

    private String type;
    private String showsName;
    private String itemName;
    private String fileName;
    private Date timestamp;
    private String status;
    private boolean renamed;
    private boolean copied;
    private boolean deleted;
    
    


    
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShowsName() {
        return showsName;
    }

    public void setShowsName(String showsName) {
        this.showsName = showsName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public abstract JsonObject toJSON();

    public abstract void setCustomData(String customData);
    
    public abstract String getCustomData();

    /**
     * @return the status
     */
    public String getStatus() {
        if(status == null || status.isEmpty()){
            return status;
        }
        return "Renamed: "+renamed+" Copied: "+copied+" Deleted: "+deleted;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the renamed
     */
    public boolean isRenamed() {
        return renamed;
    }

    /**
     * @param renamed the renamed to set
     */
    public void setRenamed(boolean renamed) {
        this.renamed = renamed;
    }

    /**
     * @return the copied
     */
    public boolean isCopied() {
        return copied;
    }

    /**
     * @param copied the copied to set
     */
    public void setCopied(boolean copied) {
        this.copied = copied;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
        
}
