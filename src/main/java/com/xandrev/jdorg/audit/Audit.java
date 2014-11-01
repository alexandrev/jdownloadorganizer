/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xandrev.jdorg.audit;

import com.xandrev.jdorg.audit.data.AuditData;
import java.util.Collection;

/**
 *
 * 
 */
public interface Audit {
    
     void auditAction(AuditData mv);
     
     Collection getElements();

     void log(String info, String executed_organization);
}
