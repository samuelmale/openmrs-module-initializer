/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loc.LocationsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocationsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	private LocationsLoader loader;
	
	@Autowired
	private DateDatatype dateDatatype;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif fetch by name
		{
			Location loc = ls.getLocation("LOCATION_NO_UUID");
			Assert.assertEquals("Main Street", loc.getAddress1());
			Assert.assertEquals("fdddc31a-3930-11ea-9712-a73c3c19744f", loc.getUuid());
		}
		// Verif creation
		{
			Location loc = ls.getLocation("The Lake Clinic-Cambodia");
			Assert.assertEquals("Paradise Street", loc.getAddress1());
			Assert.assertEquals("Siem Reap", loc.getCountyDistrict());
			Assert.assertEquals("Siem Reap", loc.getStateProvince());
			Assert.assertEquals("Cambodia", loc.getCountry());
			
			Set<LocationTag> tags = loc.getTags();
			Assert.assertThat(tags, notNullValue());
			Assert.assertThat(tags.size(), is(2));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Login Location")), is(true));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Facility Location")), is(true));
			
			Collection<LocationAttribute> attributes = loc.getActiveAttributes();
			Assert.assertThat(attributes.size(), is(2));
			Assert.assertEquals("CODE-TLC-123", ((LocationAttribute) attributes.toArray()[0]).getValue());
			Assert.assertEquals("2017-05-15",
			    dateDatatype.serialize((Date) ((LocationAttribute) attributes.toArray()[1]).getValue()));
		}
		// Verif creation
		{
			Location loc = ls.getLocation("OPD Room");
			Assert.assertEquals(ls.getLocation("The Lake Clinic-Cambodia"), loc.getParentLocation());
			
			Set<LocationTag> tags = loc.getTags();
			Assert.assertThat(tags, notNullValue());
			Assert.assertThat(tags.size(), is(1));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Consultation Location")), is(true));
		}
		// Verif edition
		{
			Location loc = ls.getLocation("Acme Clinic");
			Assert.assertEquals("This now becomes a child of TLC", loc.getDescription());
			Assert.assertEquals(ls.getLocation("The Lake Clinic-Cambodia"), loc.getParentLocation());
			
			Set<LocationTag> tags = loc.getTags();
			Assert.assertThat(tags, notNullValue());
			Assert.assertThat(tags.size(), is(1));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Login Location")), is(true));
			
			Collection<LocationAttribute> attributes = loc.getActiveAttributes();
			Assert.assertTrue(attributes.isEmpty());
			
		}
		// Verif retire
		{
			Location loc = ls.getLocationByUuid("cbaaaab4-d960-4ae9-9b6a-8983fbd947b6");
			Assert.assertThat(loc.getRetired(), is(true));
		}
	}
}
