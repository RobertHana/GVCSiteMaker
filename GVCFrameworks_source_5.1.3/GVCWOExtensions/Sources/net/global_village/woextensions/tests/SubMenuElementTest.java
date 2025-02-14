package net.global_village.woextensions.tests;

import junit.framework.*;

import net.global_village.woextensions.dynamicmenu.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class SubMenuElementTest extends TestCase
{
    public static String expectedJavascriptForStaticSubMenuElement = "addSubmenuItem(\"http://www.apple.com\",\"Apple\",\"_self\",\"We Love Apple\");";
    public static String expectedJavascriptForDynamicSubMenuElement;
    public static String expectedJavascrptForSubMenuElementWithChildMenu;

    public static SubMenuElement staticSubMenuElement;
    public static SubMenuElement dynamicSubMenuElement;
    public static SubMenuElement subMenuElementWithChildMenu;

    
    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public SubMenuElementTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        SubMenuElementTest.staticSubMenuElement = new SubMenuElement("http://www.apple.com",
                                                                     "Apple",
                                                                     "_self",
                                                                     "We Love Apple",
                                                                     null,
                                                                     false);
        SubMenuElementTest.dynamicSubMenuElement = new SubMenuElement("testAction",
                                                                      "Test Action",
                                                                      "_self",
                                                                      "Test Action",
                                                                      true);
        String componentActionURL = AllTestsSuite.testContext.componentActionURL();
        String expectedActionURI = componentActionURL + "." + "testAction";
        SubMenuElementTest.expectedJavascriptForDynamicSubMenuElement =  "addSubmenuItem(" + "\"" + expectedActionURI + "\"" + "," + "\"Test Action\",\"_self\",\"Test Action\");";

        SubMenuElementTest.subMenuElementWithChildMenu = new SubMenuElement("http://www.news.com",
                                                                            "News",
                                                                            "_self",
                                                                            "C|Net",
                                                                            null,
                                                                            false);
        SubMenuElementTest.subMenuElementWithChildMenu.setChildMenu(SecondaryMenuTest.childMenu);
        SubMenuElementTest.expectedJavascrptForSubMenuElementWithChildMenu = "addSubmenuItem(\"http://www.news.com\",\"News\",\"_self\",\"C|Net\");" + DynamicMenu.newLineString + SecondaryMenuTest.expectedJavascriptForChildMenu + DynamicMenu.newLineString;  
    }



    /**
     * Test javascript()
     */
    public void testJavascript()
    {
        String staticSubMenuElementJavascript = SubMenuElementTest.staticSubMenuElement.javascript(AllTestsSuite.testContext);
        String dynamicSubMenuElementJavascript = SubMenuElementTest.dynamicSubMenuElement.javascript(AllTestsSuite.testContext);
        String subMenuElementWithChildMenuJavascript = SubMenuElementTest.subMenuElementWithChildMenu.javascript(AllTestsSuite.testContext);

        assertTrue("Static Sub Menu Element Javascript is correct", staticSubMenuElementJavascript.equals(SubMenuElementTest.expectedJavascriptForStaticSubMenuElement));
        assertTrue("Dynamic Sub Menu Element Javascript is correct", dynamicSubMenuElementJavascript.equals(SubMenuElementTest.expectedJavascriptForDynamicSubMenuElement));
        assertTrue("Sub Element with Child Menu javascript is correct", subMenuElementWithChildMenuJavascript.equals(SubMenuElementTest.expectedJavascrptForSubMenuElementWithChildMenu));
    }
}
