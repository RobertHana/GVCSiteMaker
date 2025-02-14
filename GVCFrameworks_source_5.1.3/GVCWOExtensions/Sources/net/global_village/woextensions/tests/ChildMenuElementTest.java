package net.global_village.woextensions.tests;

import junit.framework.*;

import net.global_village.woextensions.dynamicmenu.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ChildMenuElementTest extends TestCase
{
    public static String expectedJavascriptForStaticChildMenuElement = "addChildmenuItem(\"http://www.global-village.net\",\"<b>GVC</b>\",\"_self\",\"GVC\");";
    public static String expectedJavascriptForDynamicChildMenuElement;

    public static ChildMenuElement staticChildMenuElement;
    public static ChildMenuElement dynamicChildMenuElement;

    
    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public ChildMenuElementTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        ChildMenuElementTest.staticChildMenuElement = new ChildMenuElement("http://www.global-village.net",
                                                                           "<b>GVC</b>",
                                                                           "_self",
                                                                           "GVC",
                                                                           false);
        ChildMenuElementTest.dynamicChildMenuElement = new ChildMenuElement("testAction",
                                                                            "Test Action",
                                                                            "_self",
                                                                            "Test Action",
                                                                            true);
        String componentActionURL = AllTestsSuite.testContext.componentActionURL();
        String expectedActionURI = componentActionURL + "." + "testAction";
        ChildMenuElementTest.expectedJavascriptForDynamicChildMenuElement =  "addChildmenuItem(" + "\"" + expectedActionURI + "\"" + "," + "\"Test Action\",\"_self\",\"Test Action\");";
    }


    
    /**
     * This test creates a session so we need to put it to sleep so that the defaultEditingContext gets unlocked properly.
     */
    public void tearDown() throws Exception
    {
        AllTestsSuite.testContext.session()._sleepInContext(AllTestsSuite.testContext);
        super.tearDown();
    }
    
    
    
    /**
     * Test javascript()
     */
    public void testJavascript()
    {
        String staticJavascript = staticChildMenuElement.javascript(AllTestsSuite.testContext);
        String dynamicJavascript = dynamicChildMenuElement.javascript(AllTestsSuite.testContext);
        assertTrue("Static Javascript is correct", staticJavascript.equals(ChildMenuElementTest.expectedJavascriptForStaticChildMenuElement));
        assertTrue("Dyanmic Javascript is correct", dynamicJavascript.equals(ChildMenuElementTest.expectedJavascriptForDynamicChildMenuElement));
    }
}
