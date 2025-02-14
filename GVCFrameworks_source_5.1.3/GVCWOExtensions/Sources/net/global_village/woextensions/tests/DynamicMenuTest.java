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
public class DynamicMenuTest extends TestCase
{
    // Since these tests are run last I don't need the variables to be static
    protected DynamicMenu testMenu;
    protected String expectedJavascript;
    
    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public DynamicMenuTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        NSMutableArray menuElements = new NSMutableArray();

        menuElements.addObject(MainLevelMenuElementTest.staticMainLevelMenuElement);
        menuElements.addObject(MainLevelMenuElementTest.dyanmicMainLevelMenuElement);
        menuElements.addObject(MainLevelMenuElementTest.mainLevelMenuElementWithSubMenu);

        testMenu = new DynamicMenu(menuElements);
        expectedJavascript = DynamicMenu.javaScriptOpenTag
            + DynamicMenu.newLineString
            + DynamicMenu.htmlCommentOpenTag
            + DynamicMenu.newLineString
            + MainLevelMenuElementTest.expectedJavascriptForStaticMainLevelMenuElement
            + DynamicMenu.newLineString
            + MainLevelMenuElementTest.expectedJavascriptForDynamicMainLevelMenuElement
            + DynamicMenu.newLineString
            + MainLevelMenuElementTest.expectedJavascriptForMainLevelMenuElementWithSubMenu
            + DynamicMenu.newLineString
            + DynamicMenu.htmlCommentCloseTag
            + DynamicMenu.newLineString
            + DynamicMenu.javaScriptCloseTag;
    }


    /**
     * Test javascript()
     */
    public void testJavascript()
    {
        String actualJavascript = testMenu.javascript(AllTestsSuite.testContext);
        assertTrue("Javascript is produced as expected", actualJavascript.equals(expectedJavascript));
    }
}
