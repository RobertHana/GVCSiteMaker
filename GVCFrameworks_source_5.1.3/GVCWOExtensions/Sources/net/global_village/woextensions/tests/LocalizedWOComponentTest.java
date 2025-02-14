package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.woextensions.tests.resources.*;


/**
 * Test Localized WOComponents.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LocalizedWOComponentTest extends TestCase
{
    protected WOContext context;


    /**
     * Designated constructor.
     */
    public LocalizedWOComponentTest(String name)
    {
        super(name);
    }


    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
    }




    protected String responseStringForLanguage(String acceptLanguage)
    {
        NSDictionary headers = new NSDictionary(acceptLanguage, "Accept-Language");
        WORequest request = new WORequest("GET", "http://localhost/cgi-bin/WebObjects", "HTTP/1.1", headers, null, null);
        context = new WOContext(request);
        TestComponent testComponent = (TestComponent)com.webobjects.appserver.WOApplication.application().pageWithName("TestComponent", context);
        initializePageInContext(testComponent, context);
        WOResponse testResponse = new WOResponse();
        testComponent.appendToResponse(testResponse, context);
        return testResponse.contentString();
    }


    /**
     * Test keyPathForBinding
     */
    public void testLocalizedKeyPathForBinding()
    {
        // User's browser accepts Danish first, then English
        String testString = responseStringForLanguage("da, en-gb;q=0.8, en;q=0.7");
        assertTrue(testString.indexOf("Hello Worlds!") > -1);
        assertTrue(testString.indexOf("My names are ") > -1);

        // User's browser accepts Proper Enlish first, then English
        testString = responseStringForLanguage("en-gb;q=0.8, en;q=0.7");
        assertTrue(testString.indexOf("British English") > -1);

        // Test @localizeBindings
        assertTrue(testString.indexOf("How are you?") > -1);
        assertTrue(testString.indexOf("Only for the TestComponent page.") > -1);
        assertTrue(testString.indexOf("I'm fine, thanks.") > -1);
        context.session()._sleepInContext(context);

        // User's browser accepts Spanish first, then English
        testString = responseStringForLanguage("es, en-gb;q=0.8, en;q=0.7");
        assertTrue(testString.indexOf("Hola Ustedas!") > -1);
        assertTrue(testString.indexOf("Yo soy ") > -1);

        // Test @localizeBindings
        assertTrue(testString.indexOf("Como esta usted?") > -1);
        assertTrue(testString.indexOf("Solamente para la TestComponent pagina.") > -1);
        assertTrue(testString.indexOf("Estoy bien, gracias.") > -1);
        context.session()._sleepInContext(context);

        // User's browser only accepts Danish, so we use the default language
        testString = responseStringForLanguage("da");
         assertTrue(testString.indexOf("Hello Worlds!") > -1);
        assertTrue(testString.indexOf("My names are ") > -1);

        // Test @localizeBindings
        assertTrue(testString.indexOf("How are you?") > -1);
        assertTrue(testString.indexOf("Only for the TestComponent page.") > -1);
        assertTrue(testString.indexOf("I'm fine, thanks.") > -1);
        context.session()._sleepInContext(context);
    }



    /**
     * This setup seems to be needed as we are not in the context of a real RR loop.
     * @param page
     * @param aContext
     */
    protected void initializePageInContext(WOComponent page, WOContext aContext)
    {
        aContext.session()._awakeInContext(aContext);
        page._setIsPage(true);
        aContext._setPageComponent(page);
        page._awakeInContext(aContext);
        aContext._setCurrentComponent(page);
    }

}
