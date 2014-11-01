/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.main;

import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.configuration.Constants;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexandre
 */
public class ExecutorThread implements Runnable {

    private final Configuration cfg;
    private static final Logger logger = Logger.getLogger(ExecutorThread.class);
    private final int sleepTime;
    private final boolean exit;
    private final ExecutorService service;

    public ExecutorThread() {
        
        service = ExecutorService.getInstance();
        
        cfg = Configuration.getInstance();
        sleepTime = Integer.parseInt(cfg.getProperty(Constants.SLEEP_TIME));
        exit = false;
    }

    public void run() {
        
            while(!exit){
                try {
                    logger.info("Starting lookup for multimedia files at: "+new Date());
                    service.applyExistentFiles();
                    logger.info("Finished lookup for multimedia files at: "+new Date());
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(ExecutorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
}
