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
public class MainLevelMenuElementTest extends TestCase
{
    public static String expectedJavascriptForStaticMainLevelMenuElement = "addMainItem(\"http://www.microsoft.com\",\"Bill & Steve's Company\",100,\"center\",\"_self\",\"Microsoft\",0,0,\"\");";
    public static String expectedJavascriptForDynamicMainLevelMenuElement;
    public static String expectedJavascriptForMainLevelMenuElementWithSubMenu;
    public static String expectedJavascriptForSubMenu;

    public static SecondaryMenu subMenu;
    public static MainLevelMenuElement staticMainLevelMenuElement;
    public static MainLevelMenuElement dyanmicMainLevelMenuElement;
    public static MainLevelMenuElement mainLevelMenuElementWithSubMenu;

    
    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public MainLevelMenuElementTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();


        MainLevelMenuElementTest.staticMainLevelMenuElement = new MainLevelMenuElement("http://www.microsoft.com",
                                                                                   "Bill & Steve's Company",
                                                                                   "_self",
                                                                                   "Microsoft",
                                                                                   DynamicMenu.centerAlignment,
                                                                                   "",
                                                                                   null,
                                                                                   false);

        
        MainLevelMenuElementTest.dyanmicMainLevelMenuElement = new MainLevelMenuElement("testAction",
                                                                                        "Test",
                                                                                        "_self",
                                                                                        "Click Here to Test",
                                                                                        DynamicMenu.centerAlignment,
                                                                                        "",
                                                                                        null,
                                                                                        true);

        
        String componentActionURL = AllTestsSuite.testContext.componentActionURL();
        String expectedActionURI = componentActionURL + "." + "testAction";
        MainLevelMenuElementTest.expectedJavascriptForDynamicMainLevelMenuElement = "addMainItem("
            + StringUtilities.quotedString(expectedActionURI)
            + ","
            + StringUtilities.quotedString("Test")
            + ","
            + "100"
            + ","
            + StringUtilities.quotedString(DynamicMenu.centerAlignment)
            + ","
            + StringUtilities.quotedString("_self")
            + ","
            + StringUtilities.quotedString("Click Here to Test")
            + ","
            + "0"
            + ","
            + "0"
            + ","
            + "\"\");";

        NSMutableArray subMenuElements = new NSMutableArray();
        subMenuElements.addObject(SubMenuElementTest.staticSubMenuElement);
        subMenuElements.addObject(SubMenuElementTest.dynamicSubMenuElement);
        subMenuElements.addObject(SubMenuElementTest.subMenuElementWithChildMenu);
        
        MainLevelMenuElementTest.subMenu = new SecondaryMenu(100,
                                                             DynamicMenu.leftAlignment,
                                                             DynamicMenu.leftAlignment,
                                                             0,
                                                             0,
                                                             subMenuElements,
                                                             SecondaryMenu.subMenuType);
        MainLevelMenuElementTest.expectedJavascriptForSubMenu = "defineSubmenuProperties("
            + "100"
            + ","
            + StringUtilities.quotedString(DynamicMenu.leftAlignment)
            + ","
            + StringUtilities.quotedString(DynamicMenu.leftAlignment)
            + ","
            + "0"
            + ","
            + "0"
            + ");";
        MainLevelMenuElementTest.expectedJavascriptForSubMenu = MainLevelMenuElementTest.expectedJavascriptForSubMenu
            + DynamicMenu.newLineString
            + SubMenuElementTest.expectedJavascriptForStaticSubMenuElement
            + DynamicMenu.newLineString
            + SubMenuElementTest.expectedJavascriptForDynamicSubMenuElement
            + DynamicMenu.newLineString
            + SubMenuElementTest.expectedJavascrptForSubMenuElementWithChildMenu
            + DynamicMenu.newLineString;

        // this line below is miscolored too
        MainLevelMenuElementTest.mainLevelMenuElementWithSubMenu = new MainLevelMenuElement("",
                                                                                            "Sub Menu",
                                                                                            "_self",
                                                                                            "Show Sub Menu",
                                                                                            DynamicMenu.centerAlignment,
                                                                                            "",
                                                                                            MainLevelMenuElementTest.subMenu,
                                                                                            false);
        MainLevelMenuElementTest.expectedJavascriptForMainLevelMenuElementWithSubMenu = "addMainItem(\"\",\"Sub Menu\",100,\"center\",\"_self\",\"Show Sub Menu\",0,0,\"\");"
            + DynamicMenu.newLineString
            + MainLevelMenuElementTest.expectedJavascriptForSubMenu
            + DynamicMenu.newLineString;
    }



    /**
     * Test javascript()
     */
    public void testJavascript()
    {
        String staticTestJavascript = MainLevelMenuElementTest.staticMainLevelMenuElement.javascript(AllTestsSuite.testContext);
        String dynamicTestJavascript = MainLevelMenuElementTest.dyanmicMainLevelMenuElement.javascript(AllTestsSuite.testContext);
        String subMenuTestJavascript = MainLevelMenuElementTest.mainLevelMenuElementWithSubMenu.javascript(AllTestsSuite.testContext);

        assertTrue("Static Test", staticTestJavascript.equals(MainLevelMenuElementTest.expectedJavascriptForStaticMainLevelMenuElement));
        assertTrue("Dynamic Test", dynamicTestJavascript.equals(MainLevelMenuElementTest.expectedJavascriptForDynamicMainLevelMenuElement));
        assertTrue("Sub Menu Test", subMenuTestJavascript.equals(MainLevelMenuElementTest.expectedJavascriptForMainLevelMenuElementWithSubMenu));
    }
}
