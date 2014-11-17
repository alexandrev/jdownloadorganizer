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
		int sleepTime = Integer.parseInt(prop.getProperty(Constants.SLEEP_TIME));
		
		Assert.assertEquals("F:/Videos",initialDirectory);
		Assert.assertEquals("F:/",finalDirectory);
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

	
}
