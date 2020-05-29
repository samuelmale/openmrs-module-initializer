package org.openmrs.module.initializer.api;

import java.io.File;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class TestMessageServiceForFrenchLocale extends BaseModuleContextSensitiveTest {

	MessageSourceService mss;
	
	@Qualifier("testInitializerMessageSource")
	@Autowired()
	InitializerMessageSource ims;
	
	Locale locale;
		
	@Before
	public void setup() {
		mss = Context.getMessageSourceService();
		locale = Locale.FRENCH;

	}
	
	@Test
	public void runWithDefaultSource() {
		// replay
		String value = mss.getMessage("coreapps.findPatient.app.label", null, locale);
		
		// verify
		Assert.assertEquals("Trouver le dossier de l'USS", value);
	}
	
	@Test
	public void runWithInizCustomSource() {
		// we need this because for some reason, the InitializerService doesn't return the correct path to the
		// test configuration working directory: /../target/test-classes/testAppDataDir/configuration/messageproperties
		// so we need to override the messagePropertiesMap by calling addMessageProperties(String) with the correct path
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder
		        .append(
		            getClass().getClassLoader().getResource(DomainBaseModuleContextSensitiveTest.appDataTestDir).getPath())
		        .append(File.separator).append(InitializerConstants.DIR_NAME_CONFIG).append(File.separator)
		        .append(InitializerConstants.DOMAIN_MSGPROP);
		ims.addMessageProperties(pathBuilder.toString());
		
		// replay
		String value = ims.getMessage("coreapps.findPatient.app.label2", null, locale);
		
		// this test fails, the actual string is: Trouver le dossier de lUSS
		// this means that the custom message source from Iniz has the bug.
		Assert.assertEquals("Trouver le dossier de l'USS", value);
	}
}
