/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.audit.impl;

import com.xandrev.jdorg.audit.Audit;
import com.xandrev.jdorg.audit.data.AuditData;
import com.xandrev.jdorg.db.DBImpl;
import java.util.Collection;
import java.util.Date;

/**
 *
 *
 */
public class AuditImpl implements Audit {

    private static AuditImpl instance;
    private static final Object LOCK = new Object();    
    private final DBImpl db = DBImpl.getInstance();


    public static AuditImpl getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new AuditImpl();
            }
        }
        return instance;
    }
    private Date resetTime;

    protected AuditImpl() {
        resetTime = new Date();
        db.connect();
    }


    public void log(String level, String message){
        db.log(level,message);
    }
    
    public void auditAction(AuditData mv) {
       db.auditAction(mv);
    }

    public boolean removeElements() {
        db.removeElements();
        resetTime = new Date();
        return true;
    }

    public Collection getElements() {
        return db.getElements();
    }
    
     public Collection getElements(Date date) {
       return db.getElements(date);
    }
     
      public Collection getElements(String type,Date date) {
        return db.getElements(type, date);
    }

    public String getResetTime() {
        return resetTime.toString();
    }

    public Collection getElements(String type,int index, int count) {
       return db.getElements(type,index,count); 
    }

    public Collection getLogs(int index, int count) {
        return db.getLogs(index, count);
    }

}
