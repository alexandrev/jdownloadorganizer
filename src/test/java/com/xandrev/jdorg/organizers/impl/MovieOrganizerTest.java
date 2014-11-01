package com.xandrev.jdorg.organizers.impl;

import java.io.File;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class MovieOrganizerTest {

	private MovieOrganizer test;
	
	@Before
	public void generateInstance(){
		test = new MovieOrganizer();
	}
	
	@Test
	public void testGenerateFolder_Null() {
		String result = test.generateFolder("");
		Assert.assertNull(result);
	}
	
	@Test
	public void testGenerateFolder_NoSeason() {
		String result = test.generateFolder("No season");
		Assert.assertNull(result);
	}
	
	@Test
	public void testGenerateFolder_ValidFilm() {
		String result = test.generateFolder("Film.avi");
		Assert.assertEquals(test.getRootFolder()+File.separator+"Film",result);
	}
	
        @Test
        public void testExtractMovieName_Extract(){
            String movie = test.extractMovieName("D:\\Temporal\\Peliculas\\El lobo de Wall Street (DVD-Screener) (EliteTorrent.net)\\El lobo de Wall Street (DVD-Screener) (EliteTorrent.net).avi");
            Assert.assertEquals(movie, "El lobo de Wall Street");
        }

}
