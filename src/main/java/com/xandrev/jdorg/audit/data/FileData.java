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
public class FileData extends AuditData{
    
    public FileData(String item,String file){
            setTimestamp(new Date());
            setType(AuditConstants.FILE_TYPE);
            setFileName(file);
    }
    
    @Override
    public void setCustomData(String customData) {
     }

    @Override
    public String getCustomData() {
        return "";
    }
}
