package com.xandrev.jdorg.organizers.impl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TVShowsOrganizerTest {

	private TVShowsOrganizer test;
	
	@Before
	public void generateInstance(){
		test = new TVShowsOrganizer();
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
	public void testGenerateFolder_ValidEpisodeUSA() {
		String result = test.generateFolder("Show.S01E01.Episode.avi");
		Assert.assertEquals("Series\\Show",result);
                
                result = test.generateFolder("House.of.Cards.2013.S02E03.WEBRip.x264-2HD[rarbg]");
                Assert.assertEquals("Series\\House of Cards 2013",result);
                
                result = test.generateFolder("house.of.cards.2013.s02e04.webrip.x264-2hd");
                Assert.assertEquals("Series\\house of cards 2013",result);
                
                result = test.generateFolder("Elementary - 2x15 - Corpse de Ballet");
                Assert.assertEquals("Series\\Elementary",result);
                
                result = test.generateFolder("Secret.Societies.1of3.The.Heirs.of.the.Knights.Templar.720p.HDTV.x264.AAC.MVGroup.org");
                Assert.assertEquals("Series\\Secret Societies",result);
                
                result = test.generateFolder("Secret.Societies.301.The.Heirs.of.the.Knights.Templar.720p.HDTV.x264.AAC.MVGroup.org");
                Assert.assertEquals("Series\\Secret Societies",result);
                
	}
	
	@Test
	public void testGenerateFolder_ValidEpisodeSpanish() {
		String result = test.generateFolder("Show.1x01.Episode.avi");
		Assert.assertEquals("Series\\Show",result);
		
		result = test.generateFolder("Show.01x01.Episode.avi");
		Assert.assertEquals("Series\\Show",result);
		
		result = test.generateFolder("Show.01x1.Episode.avi");
		Assert.assertEquals("Series\\Show",result);
		
		result = test.generateFolder("Show.1x1.Episode.avi");
		Assert.assertEquals("Series\\Show",result);
	}

}
