package net.global_village.kerberos.tests;


import net.global_village.kerberos.*;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Contains all tests.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class AllTestsSuite extends TestSuite
{


    public static Test suite()
    {
        return new AllTestsSuite();
    }



    public AllTestsSuite(String name)
    {
        super(name);
    }



    public AllTestsSuite()
    {
        super();

        addTest(new TestSuite(KerberosAuthenticatorTest.class));

        // This was just a temporary minimal test to verify WedgeTail libraries.
        //addTest(new TestSuite(WedgeTailTest.class));
    }



}
