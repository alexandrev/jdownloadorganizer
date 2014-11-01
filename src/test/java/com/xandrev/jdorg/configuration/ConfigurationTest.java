package com.xandrev.jdorg.configuration;

import java.util.Properties;
import junit.framework.Assert;
import org.junit.Test;

public class ConfigurationTest {

        private Configuration cfg = Configuration.getInstance();
    
	@Test
	public void testLoadProperties() {
		Properties prop = cfg.loadProperties(Constants.CONFIG_FILE_PATH,true);
		String initialDirectory = prop.getProperty(Constants.INITIAL_FOLDER);
		String finalDirectory = prop.getProperty(Constants.FINAL_FOLDER);
		String extensionsArray = prop.getProperty(Constants.VIDEO_EXTENSIONS);
		int sleepTime = Integer.parseInt(prop.getProperty(Constants.SLEEP_TIME));
		
		Assert.assertEquals("F:/Videos",initialDirectory);
		Assert.assertEquals("F:/",finalDirectory);
		Assert.assertEquals("avi,mp4,mkv",extensionsArray);
		Assert.assertEquals(3600000,sleepTime);

	}
	
	@Test
	public void testLoadProperties_Null() {
		Properties prop = cfg.loadProperties(null,true);
		Assert.assertEquals(0, prop.size());

	}
	
	@Test
	public void testLoadProperties_NotValid() {
		Properties prop = cfg.loadProperties("frog.gif",true);
		Assert.assertEquals(0, prop.size());

	}
	
	@Test
	public void testLoadProperties_NotExists() {
		Properties prop = cfg.loadProperties("frog2.gif",true);
		Assert.assertEquals(0, prop.size());
	}

	@Test
	public void testSaveProperties() {
		Properties prop = cfg.loadProperties(Constants.CONFIG_FILE_PATH,true);
		String initialDirectory = prop.getProperty(Constants.INITIAL_FOLDER);
		String finalDirectory = prop.getProperty(Constants.FINAL_FOLDER);
		String extensionsArrayOrig = prop.getProperty(Constants.VIDEO_EXTENSIONS);
		int sleepTime = Integer.parseInt(prop.getProperty(Constants.SLEEP_TIME));
		cfg.removeVideoExtension("mp4");
		cfg.removeVideoExtension("avi");
                cfg.removeVideoExtension("mkv");
		cfg.addVideoExtension(".1");
		cfg.addVideoExtension(".2");
		
		
		cfg.saveProperties(Constants.CONFIG_FILE_PATH);
		prop = cfg.loadProperties(Constants.CONFIG_FILE_PATH,true);
		String extensionsArray = prop.getProperty(Constants.VIDEO_EXTENSIONS);
		Assert.assertEquals(".1,.2", extensionsArray);
		
		
		cfg.removeVideoExtension(".1");
		cfg.removeVideoExtension(".2");
		cfg.addVideoExtension("avi");
		cfg.addVideoExtension("mp4");
		cfg.saveProperties(Constants.CONFIG_FILE_PATH);
		
	}

	
}
