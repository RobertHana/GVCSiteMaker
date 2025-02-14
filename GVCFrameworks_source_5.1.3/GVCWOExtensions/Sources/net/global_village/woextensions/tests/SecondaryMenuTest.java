package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.woextensions.dynamicmenu.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class SecondaryMenuTest extends TestCase
{
    public static String expectedJavascriptForChildMenu;
    public static SecondaryMenu childMenu;
    
    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public SecondaryMenuTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        NSMutableArray childMenuElements = new NSMutableArray();
        childMenuElements.addObject(ChildMenuElementTest.staticChildMenuElement);
        childMenuElements.addObject(ChildMenuElementTest.dynamicChildMenuElement);
        SecondaryMenuTest.childMenu = new SecondaryMenu(100,
                                                        DynamicMenu.leftAlignment,
                                                        DynamicMenu.leftAlignment,
                                                        0,
                                                        0,
                                                        childMenuElements,
                                                        SecondaryMenu.childMenuType);
        SecondaryMenuTest.expectedJavascriptForChildMenu = "defineChildmenuProperties(" + "100" + "," + StringUtilities.quotedString(DynamicMenu.leftAlignment) + "," + StringUtilities.quotedString(DynamicMenu.leftAlignment) + "," + "0" + "," + "0" + ");";
        SecondaryMenuTest.expectedJavascriptForChildMenu = SecondaryMenuTest.expectedJavascriptForChildMenu + DynamicMenu.newLineString + ChildMenuElementTest.expectedJavascriptForStaticChildMenuElement + DynamicMenu.newLineString + ChildMenuElementTest.expectedJavascriptForDynamicChildMenuElement + DynamicMenu.newLineString;
    }


    
    /**
     * Test javascript()
     */
    public void testJavascript()
    {
        String childMenuJavascript = SecondaryMenuTest.childMenu.javascript(AllTestsSuite.testContext);
        assertTrue("Javascript matches expectation", childMenuJavascript.equals(SecondaryMenuTest.expectedJavascriptForChildMenu));
    }
}
