package net.global_village.woextensions.tests;


import junit.framework.*;

import net.global_village.woextensions.tests.resources.*;


/**
 * Test WOComponent.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class WOComponentTest extends TestCase
{
    protected TestComponent testComponent;


    /**
     * Designated constructor.
     */
    public WOComponentTest(String name)
    {
        super(name);
    }


    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        AllTestsSuite.testContext.session()._awakeInContext(AllTestsSuite.testContext);
        com.webobjects.foundation.NSLog.out.appendln("WOComponentTest " + AllTestsSuite.testContext.session().context());

        testComponent = new TestComponent(AllTestsSuite.testContext);       
        testComponent._setIsPage(true);
        AllTestsSuite.testContext._setPageComponent(testComponent);
        testComponent._awakeInContext(AllTestsSuite.testContext);
        AllTestsSuite.testContext._setCurrentComponent(testComponent);

        testComponent.generateResponse();  // This causes TestSubcomponent's constructor to be called -- very important!
    }



    /**
     * Test keyPathForBinding
     */
    public void testKeyPathForBinding()
    {
        assertEquals("testParentField", TestSubcomponent.theSubcomponent.keyPathForBinding("testChildField"));
    }



}
