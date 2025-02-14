// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.tests;

import junit.framework.*;

/**
 * Suite of tests for GVCSMCore.
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for com.gvcsitemaker.core.tests");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(SMStringUtilsTest.class));
        //$JUnit-END$
        return suite;
    }
}
