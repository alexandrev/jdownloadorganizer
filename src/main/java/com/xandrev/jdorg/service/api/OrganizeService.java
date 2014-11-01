/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.service.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.xandrev.jdorg.audit.data.AuditData;
import com.xandrev.jdorg.audit.data.LogData;
import com.xandrev.jdorg.audit.impl.AuditImpl;
import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.configuration.Constants;
import com.xandrev.jdorg.main.ExecutorService;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import javax.servlet.DispatcherType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;


/**
 *
 * 

*/
@Path("/organizer")
public class OrganizeService {

    private final Configuration cfg;
    private final ExecutorService service;
    private final AuditImpl auditService;
    private final int httpPort;

    public OrganizeService() {
        cfg = Configuration.getInstance();
        service = ExecutorService.getInstance();
        auditService = AuditImpl.getInstance();
        
        String httpPortString = cfg.getProperty(Constants.REST_API_HTTP_PORT);
        if(httpPortString == null){
            httpPortString = Constants.REST_API_HTTP_PORT_DEFAULT_VALUE;
        }
        httpPort = Integer.parseInt(httpPortString);
    }

    public void init() throws Exception{
        Server server = new Server(httpPort);
        ServletContextHandler context = new ServletContextHandler(server, "/api", ServletContextHandler.SESSIONS);
        FilterHolder filterHolder = new FilterHolder();
        CrossOriginFilter filter = new CrossOriginFilter();
        filterHolder.setFilter(filter);
        filterHolder.setInitParameter("allowedOrigins", "*");
        context.addFilter(filterHolder, "/*", EnumSet.allOf(DispatcherType.class));
        context.addServlet(new ServletHolder(new ServletContainer(new PackagesResourceConfig("com.xandrev.jdorg.service.api"))), "/");        
        server.setHandler(context);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/gui");
        webapp.setResourceBase("src/main/resources/webapp");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { context, webapp });
        server.setHandler(handlers);
        server.start();
        server.join();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/execute")
    public String executeOrganizer() {
        JsonObject result = new JsonObject();
        try {
            service.applyExistentFiles();
            result.addProperty("result", Boolean.TRUE);
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/status")
    public String getStatus() {
        JsonObject result = new JsonObject();
        try {
            result.addProperty("status", Boolean.TRUE);
        } catch (Exception ex) {
            result.addProperty("status", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/audit/list")
    public String getAudit() {
        JsonObject result = new JsonObject();
        try {
            Collection item = auditService.getElements();
            Iterator it  = item.iterator();
            JsonArray items = new JsonArray();
            while(it.hasNext()){
                AuditData tmp = (AuditData)it.next();
                JsonObject tmpJson = tmp.toJSON();
                items.add(tmpJson);
                
            }
            result.addProperty("result", Boolean.TRUE);
            result.add("items", items);
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/audit/list/{type}/{index}/{count}")
    public String getAudit(@PathParam("type")String type,@PathParam("index")int index, @PathParam("count")int count) {
        JsonObject result = new JsonObject();
        try {
            Collection item = auditService.getElements(type,index,count);
            Iterator it  = item.iterator();
            JsonArray items = new JsonArray();
            while(it.hasNext()){
                AuditData tmp = (AuditData)it.next();
                JsonObject tmpJson = tmp.toJSON();
                items.add(tmpJson);
            }
            result.addProperty("result", Boolean.TRUE);
            result.add("items", items);
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/logs/{index}/{count}")
    public String getLogs(@PathParam("index")int index, @PathParam("count")int count) {
        JsonObject result = new JsonObject();
        try {
            Collection item = auditService.getLogs(index,count);
            Iterator it  = item.iterator();
            JsonArray items = new JsonArray();
            while(it.hasNext()){
                LogData tmp = (LogData)it.next();
                JsonObject tmpJson = tmp.toJSON();
                items.add(tmpJson);
            }
            result.addProperty("result", Boolean.TRUE);
            result.add("items", items);
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/audit/reset")
    public String resetAudit() {
        JsonObject result = new JsonObject();
        try {
            result.addProperty("result", auditService.removeElements());
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/audit/from/{date}/{hour}")
    public String auditfrom(@PathParam("date")String date,@PathParam("hour")String hour) {
        JsonObject result = new JsonObject();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dDate = sdf.parse(date);
            int hHour = Integer.parseInt(hour);
            dDate.setTime(dDate.getTime()+hHour*3600000);
            
            Collection item = auditService.getElements(dDate);
            Iterator it  = item.iterator();
            JsonArray items = new JsonArray();
            while(it.hasNext()){
                AuditData tmp = (AuditData)it.next();
                JsonObject tmpJson = tmp.toJSON();
                items.add(tmpJson);
            }
            result.addProperty("result", Boolean.TRUE);
            result.add("items", items);
            result.addProperty("result", auditService.removeElements());
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/audit/{type}/from/{date}/{hour}")
    public String auditfrom(@PathParam("type")String type,@PathParam("date")String date,@PathParam("hour")String hour) {
        JsonObject result = new JsonObject();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dDate = sdf.parse(date);
            int hHour = Integer.parseInt(hour);
            dDate.setTime(dDate.getTime()+hHour*3600000);
            
            Collection item = auditService.getElements(type,dDate);
            Iterator it  = item.iterator();
            JsonArray items = new JsonArray();
            while(it.hasNext()){
                AuditData tmp = (AuditData)it.next();
                JsonObject tmpJson = tmp.toJSON();
                items.add(tmpJson);
            }
            result.addProperty("result", Boolean.TRUE);
            result.add("items", items);
            result.addProperty("result", auditService.removeElements());
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/audit/timereset")
    public String timereset() {
        JsonObject result = new JsonObject();
        try {
            result.addProperty("result", Boolean.TRUE);
            result.addProperty("time", auditService.getResetTime());
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/time")
    public String getTime() {
        JsonObject result = new JsonObject();
        try {
            result.addProperty("result", Boolean.TRUE);
            result.addProperty("time", service.getTime());
        } catch (Exception ex) {
            result.addProperty("result", Boolean.FALSE);
            result.addProperty("error", ex.getMessage());
        }
        return result.toString();
    }
}
