package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.appserver.*;

import net.global_village.gvcjunit.*;


/**
 * Contains all the test suites that need to be run.  The tests have to be run in this order as I reuse the Objects in later tests. This is done by making them static.  Perhaps we should assign values to the static variables in the constructor instead of the setUp() method.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 18$
 */
public class AllTestsSuite extends EOTestSuite
{
    // We need that URL exactly as specified (or at least the "WebObjects" part)
    public static WOContext testContext;


    /**
     * Designated constuctor.
     */
    public AllTestsSuite()
    {
        super();

        // If WOApplication is not primed the WOContext constructor fails with a null pointer exception.
        // TODO Ignore this deprecation message, it is incorrect.
        WOApplication.primeApplication("GVCWOExtensions.framework", null, net.global_village.woextensions.tests.Application.class.getName());
        testContext = new WOContext(new WORequest("GET", "http://localhost/cgi-bin/WebObjects", "HTTP/1.1", null, null, null));

        addTest(new TestSuite(HTMLFormattingTest.class));

        // maintain order
        addTest(new TestSuite(ChildMenuElementTest.class));
        addTest(new TestSuite(SecondaryMenuTest.class));
        addTest(new TestSuite(SubMenuElementTest.class));
        addTest(new TestSuite(MainLevelMenuElementTest.class));
        addTest(new TestSuite(DynamicMenuTest.class));

        addTest(new TestSuite(NSDictionaryAdditionsTest.class));
        addTest(new TestSuite(ApplicationPropertiesTest.class));
        addTest(new TestSuite(RequestUtilitiesTest.class));

        addTest(new TestSuite(WOComponentTest.class));
        addTest(new TestSuite(OrderableListTest.class));
        addTest(new TestSuite(LocalizedWOComponentTest.class));
        
        addTest(new TestSuite(EditingPageTest.class));
    }



    public static Test suite()
    {
        return new AllTestsSuite();
    }



}
