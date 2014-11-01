package com.xandrev.jdorg.utils;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testCheckExtensions_ReNull() {
		boolean result = Utils.checkExtensions(null, null);
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void testCheckExtensions_Array_Null() {
		String ext = "avi";
		boolean result = Utils.checkExtensions(ext, null);
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void testCheckExtensions_Name_Null() {
		String[] ext = {"avi","mp4"};
		boolean result = Utils.checkExtensions(null, ext);
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void testCheckExtensions_Contained() {
		String ext = "avi";
		String[] extList = {"avi","mp4"};
		boolean result = Utils.checkExtensions(ext, extList);
		Assert.assertEquals(true, result);
	}
	
	@Test
	public void testCheckExtensions_NotContained() {
		String ext = "mp3";
		String[] extList = {"avi","mp4"};
		boolean result = Utils.checkExtensions(ext, extList);
		Assert.assertEquals(false, result);
	}
	
	
	
	
	
	
	
	@Test
	public void testExtractEpisodeFromFile_NotContained() {
		String ext = "mp3";
		String result = Utils.extractEpisodeFromFile(ext);
		Assert.assertEquals(null, result);
	}
	
	@Test
	public void testExtractEpisodeFromFile_Null() {
		String ext = null;
		String result = Utils.extractEpisodeFromFile(ext);
		Assert.assertEquals(null, result);
	}
	
	@Test
	public void testExtractEpisodeFromFile_AmericanStyle() {
		String ext = "Show.S01E02.avi";
		String result = Utils.extractEpisodeFromFile(ext);
		Assert.assertEquals("The extracted failed unexpected","S01E02", result);
	}
	
	@Test
	public void testExtractEpisodeFromFile_SpanishStyle() {
		String ext = "Show.01x02.avi";
		String result = Utils.extractEpisodeFromFile(ext);
		Assert.assertEquals("The extracted failed unexpected","01X02", result);
	}
	
	@Test
	public void testAvailabeFeatures() {
		List<String> list = Utils.listFeaturesAvailables();
		Assert.assertEquals(0, list.size());
	}
	
	

}
