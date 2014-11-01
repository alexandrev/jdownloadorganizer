package com.xandrev.jdorg.organizers.service;

import junit.framework.Assert;

import org.junit.Test;

public class OrganizerManagerTest {

	@Test
	public void testOrganizerManager() {
		
		OrganizerManager org = new OrganizerManager(null);
		Assert.assertEquals(0,org.getOrganizerList().size());
		
		org = new OrganizerManager("");
		Assert.assertEquals(0,org.getOrganizerList().size());
		
		org = new OrganizerManager("NoExistClass");
		Assert.assertEquals(0,org.getOrganizerList().size());
		
		org = new OrganizerManager("MovieOrganizer");
		Assert.assertEquals(1,org.getOrganizerList().size());
		
		org = new OrganizerManager("MovieOrganizer,NoExistClass");
		Assert.assertEquals(1,org.getOrganizerList().size());
		
		org = new OrganizerManager("MovieOrganizer,TVShowsOrganizer");
		Assert.assertEquals(2,org.getOrganizerList().size());
		
		org = new OrganizerManager("MovieOrganizer,String");
		Assert.assertEquals(1,org.getOrganizerList().size());
		
		org = new OrganizerManager("MovieOrganizer,");
		Assert.assertEquals(1,org.getOrganizerList().size());
	}


}
