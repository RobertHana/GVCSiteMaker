package net.global_village.security.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.global_village.eofvalidation.EOFValidation;
import net.global_village.gvcjunit.EOTestSuite;


/**
 * Contains all the test suites that need to be run.  NOTE: in order to run the tests, the GVCJTestEOModelBase framework must be linked into the app, as that framework contains the EOModel for the SimpleTestUser.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class AllTestsSuite extends EOTestSuite
{
// TODO Update to use dbUnit not SQL script to populate test database

    /**
     * Designated constuctor.
     */
    public AllTestsSuite()
    {
        super();

        addTest(new TestSuite(UserTest.class));
        addTest(new TestSuite(UserGroupTest.class));
        addTest(new TestSuite(PrivilegeTest.class));
    }



    public static Test suite()
    {
        return new AllTestsSuite();
    }



    /**
     * Overridden to install EOFValidation before connecting the models and switching
     * the prototypes. 
     */
    public void performEarlySuiteInitialization()
    {
        EOFValidation.installValidation();
    }

}
