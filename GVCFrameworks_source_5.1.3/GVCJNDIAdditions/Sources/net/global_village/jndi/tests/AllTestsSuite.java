package net.global_village.jndi.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Main suite of tests for GVCJNDIAdditions.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class AllTestsSuite extends TestSuite
{

    public static Test suite()
    {
        TestSuite suite = new AllTestsSuite("Test for net.global_village.jndi.tests");
        return suite;
    }
    
    


    public AllTestsSuite(String name)
    {
        super(name);
        //$JUnit-BEGIN$
        addTest(new TestSuite(net.global_village.jndi.tests.LDAPTLSPluginTest.class));
        //$JUnit-END$
    }
    
    
    
    public AllTestsSuite()
    {
        this("Default");
    }
        
}
