/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.jdorg.organizers.impl;


import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alexa
 */
public class FileOrganizerTest {
    private FileOrganizer test;
    
    public FileOrganizerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
   @Before
	public void generateInstance(){
		test = new FileOrganizer();
	}
    
    @After
    public void tearDown() {
    }

 @Test
	public void testGenerateFolder_ValidEpisodeUSA() {
		String result = test.generateFolder("Show.S01E01.Episode.txt");
		Assert.assertEquals("Otros\\Documents",result);
                
                result = test.generateFolder("House.of.Cards.2013.S02E03.WEBRip.x264-2HD[rarbg].epub");
                Assert.assertEquals("Otros\\Books",result);
                
                
	}

 


  




    
}
