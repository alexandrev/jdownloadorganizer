/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.db;

import com.xandrev.jdorg.audit.data.AuditData;
import com.xandrev.jdorg.audit.data.LogData;
import com.xandrev.jdorg.audit.data.MovieData;
import com.xandrev.jdorg.audit.data.ShowsData;
import com.xandrev.jdorg.configuration.Configuration;
import com.xandrev.jdorg.configuration.Constants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author alexa_000
 */
public class DBImpl {

    private final Logger logger = LogManager.getLogger(DBImpl.class);

    private static DBImpl instance;
    private static final Object LOCK = new Object();

    private String driver;

    private String fullPath;

    private String user;

    private String password;

    private Configuration config = Configuration.getInstance();

    public static DBImpl getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new DBImpl();
            }
        }
        return instance;
    }

    private DBImpl() {
        driver = config.getProperty(Constants.DB_DRIVER);
        if (driver == null || driver.isEmpty()) {
            driver = Constants.DB_DRIVER_DEFAULT_VALUE;
        }

        fullPath = config.getProperty(Constants.DB_URL_PATH);
        if (fullPath == null || fullPath.isEmpty()) {
            fullPath = Constants.DB_URL_PATH_DEFAULT_VALUE;
        }

        user = config.getProperty(Constants.DB_USER);
        if (user == null || user.isEmpty()) {
            user = Constants.DB_USER_DEFAULT_VALUE;
        }

        password = config.getProperty(Constants.DB_PASSWORD);
        if (password == null || password.isEmpty()) {
            password = Constants.DB_PASSWORD_DEFAULT_VALUE;
        }
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the fullPath
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * @param fullPath the fullPath to set
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Configuration config) {
        this.config = config;
    }

    private Connection connection;

    public void connect() {
        try {
            logger.debug("Loading the database driver..");
            Class.forName(driver);
            logger.debug("Driver loaded!");
            logger.debug("Open the database connection...");
            logger.debug("Database Url Path: " + fullPath);
            logger.debug("Database Username: " + user);
            connection = DriverManager.getConnection(fullPath, user, password);
            logger.debug("Database connection sucessfully opened.");
            if (connection != null) {
                logger.debug("Initialiation phase started");
                init();
                logger.debug("Initialiation phase completed!");
            }
        } catch (ClassNotFoundException ex) {
            logger.error("Class not found error loading the driver: ", ex);
        } catch (SQLException ex) {
            logger.error("SQL Exception open the connection: ", ex);
        }
    }

    public void disconnect() {
        logger.debug("Disconnecting the database connection");
        if (connection != null) {
            try {
                logger.debug("Closing DB connection");
                connection.close();
                logger.debug("DB connection closed");
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
    }

    private void init() {
        try {
            Statement statement = connection.createStatement();
            logger.debug("Statement created successfully");
            boolean result = statement.execute("CREATE TABLE IF NOT EXISTS AUDIT_DATA(ID VARCHAR(36) PRIMARY KEY, TYPE VARCHAR(255),SHOWNAME VARCHAR(255),ITEMNAME VARCHAR(255),FILENAME VARCHAR(255),AUDIT_DATE TIMESTAMP,CUSTOM_DATA VARCHAR(255),STATUS VARCHAR(255), RENAMED BOOLEAN, COPIED BOOLEAN,DELETED BOOLEAN);");
            result = statement.execute("CREATE TABLE IF NOT EXISTS LOG_DATA(LEVEL VARCHAR(255),MESSAGE VARCHAR(255),AUDIT_DATE TIMESTAMP);");
            logger.debug("Statement executed and completed");
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    public void auditAction(AuditData mv) {
        logger.debug("Starting to audit the data: " + mv.toJSON());
        try {

            PreparedStatement statement = connection.prepareStatement("INSERT INTO AUDIT_DATA(ID,TYPE,SHOWNAME,ITEMNAME,FILENAME,AUDIT_DATE,CUSTOM_DATA,STATUS,RENAMED,COPIED,DELETED) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            logger.debug("Statement created");
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, mv.getType());
            statement.setString(3, mv.getShowsName());
            statement.setString(4, mv.getItemName());
            statement.setString(5, mv.getFileName());
            if (mv.getTimestamp() != null) {
                statement.setTimestamp(6, new Timestamp(mv.getTimestamp().getTime()));
            } else {
                statement.setTimestamp(6, new Timestamp(new Date().getTime()));
            }
            statement.setString(7, mv.getCustomData());
            statement.setString(8, mv.getStatus());
            statement.setBoolean(9, mv.isRenamed());
            statement.setBoolean(10, mv.isCopied());
            statement.setBoolean(11, mv.isDeleted());
            logger.debug("Statement parameters binded successfully");
            boolean result = statement.execute();
            logger.debug("Statement result: " + result);
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    public boolean removeElements() {
        try {
            Statement statement = connection.createStatement();
            boolean result = statement.execute("DELETE FROM AUDIT_DATA;");
            logger.debug("Statement result: " + result);
        } catch (SQLException ex) {
            logger.error("Error executing the sql query", ex);
        }
        return true;
    }

    public Collection getElements() {
        ArrayList tmp = new ArrayList();
        try {
            Statement statement = connection.createStatement();
            logger.debug("Statement created");
            ResultSet rs = statement.executeQuery("SELECT ID,TYPE,SHOWNAME,ITEMNAME,FILENAME,AUDIT_DATE,CUSTOM_DATA,STATUS,RENAMED,COPIED,DELETED FROM AUDIT_DATA;");
            if (rs != null) {
                logger.debug("Starting the result parsing process");
                parseResultSet(rs, tmp);
                logger.debug("Result parsing process completed");
            }
        } catch (SQLException ex) {
            logger.error("Error executing the sql query", ex);
        }
        return tmp;
    }

    public Collection getElements(Date date) {
        ArrayList tmp = new ArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT ID,TYPE,SHOWNAME,ITEMNAME,FILENAME,AUDIT_DATE,CUSTOM_DATA,STATUS,RENAMED,COPIED,DELETED FROM AUDIT_DATA WHERE AUDIT_DATE = ?;");
            logger.debug("Statement created");
            statement.setTimestamp(1, new Timestamp(date.getTime()));
            logger.debug("Statement parameters binded successfully");
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                logger.debug("Starting the result parsing process");
                parseResultSet(rs, tmp);
                logger.debug("Result parsing process completed");
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        return tmp;
    }

    public Collection getElements(String type, Date date) {
        ArrayList tmp = new ArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT ID,TYPE,SHOWNAME,ITEMNAME,FILENAME,AUDIT_DATE,CUSTOM_DATA,STATUS,RENAMED,COPIED,DELETED FROM AUDIT_DATA WHERE TYPE = ? AND AUDIT_DATE = ?;");
            logger.debug("Statement created");
            statement.setString(1, type);
            statement.setTimestamp(2, new Timestamp(date.getTime()));
            logger.debug("Statement parameters binded successfully");
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                logger.debug("Starting the result parsing process");
                parseResultSet(rs, tmp);
                logger.debug("Result parsing process completed");
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        return tmp;
    }

    private void parseResultSet(ResultSet rs, ArrayList tmp) throws SQLException {
        while (rs.next()) {
            String uuid = rs.getString(1);
            String type = rs.getString(2);
            String showName = rs.getString(3);
            String itemName = rs.getString(4);
            String fileName = rs.getString(5);
            Timestamp timeStamp = rs.getTimestamp(6);
            AuditData tmpAud;
            Date dt = new Date();
            dt.setTime(timeStamp.getTime());
            String customData = rs.getString(7);
            String status = rs.getString(8);
            boolean isRenamed = rs.getBoolean(9);
            boolean isCopied = rs.getBoolean(10);
            boolean isDeleted = rs.getBoolean(11);
            if (type != null && "MOVIE".equals(type)) {
                tmpAud = new MovieData(itemName, fileName);

            } else {
                tmpAud = new ShowsData(showName, itemName, fileName);
            }
            tmpAud.setCustomData(customData);
            tmpAud.setStatus(status);
            tmpAud.setTimestamp(dt);
            tmpAud.setRenamed(isRenamed);
            tmpAud.setCopied(isCopied);
            tmpAud.setDeleted(isDeleted);
            tmp.add(tmpAud);
        }
    }

    public Collection getElements(String type, int index, int count) {
        ArrayList tmp = new ArrayList();
        try {
            if (index >= 0) {
                PreparedStatement statement = connection.prepareStatement("SELECT ID,TYPE,SHOWNAME,ITEMNAME,FILENAME,AUDIT_DATE,CUSTOM_DATA,STATUS,RENAMED,COPIED,DELETED FROM AUDIT_DATA WHERE TYPE = ? ORDER BY AUDIT_DATE DESC limit ? offset ?;");
                logger.debug("Statement created");
                statement.setString(1, type);
                statement.setInt(2, count);
                statement.setInt(3, index);
                logger.debug("Statement parameters binded successfully");
                ResultSet rs = statement.executeQuery();
                if (rs != null) {
                    logger.debug("Starting the result parsing process");
                    parseResultSet(rs, tmp);
                    logger.debug("Result parsing process completed");
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        return tmp;
    }

    public void log(String level, String message) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO LOG_DATA(LEVEL,MESSAGE,AUDIT_DATE) VALUES (?,?,?)");
            logger.debug("Statement created");
            statement.setString(1, level);
            statement.setString(2, message);
            statement.setTimestamp(3, new Timestamp(new Date().getTime()));
            logger.debug("Statement parameters binded successfully");
            boolean result = statement.execute();
            logger.debug("Statement result: " + result);
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    public ArrayList<LogData> getLogs(int index, int count) {
        ArrayList<LogData> tmp = new ArrayList<LogData>();
        try {
            if (index >= 0) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOG_DATA ORDER BY AUDIT_DATE DESC limit ? offset ?;");
                logger.debug("Statement created");
                statement.setInt(1, count);
                statement.setInt(2, index);
                logger.debug("Statement parameters binded successfully");
                ResultSet rs = statement.executeQuery();
                if (rs != null) {
                    logger.debug("Starting the result parsing process");
                    parseResultSetLog(rs, tmp);
                    logger.debug("Result parsing process completed");
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        return tmp;
    }

    private void parseResultSetLog(ResultSet rs, ArrayList tmp) throws SQLException {
        while (rs.next()) {
            String level = rs.getString(1);
            String message = rs.getString(2);
            Date dateTime = rs.getDate(3);

            LogData tmpAud = new LogData();
            tmpAud.setDate(dateTime);
            tmpAud.setLevel(level);
            tmpAud.setMessage(message);
            tmp.add(tmpAud);
        }
    }

}
