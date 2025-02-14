package net.global_village.genericobjects.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import net.global_village.eofvalidation.EOFValidation;
import net.global_village.gvcjunit.EOTestSuite;


/**
 * Test suite for the net.global_village.genericobjects package <BR>
 *
 * The test in the Java version for TestVersionObject, TestArchievableKeyedLookup, and TestArchievableLookup all are subclasses of VersionableArchievableKeyedLookup whilst TestVersionableArchievableLookup is a subclass of TestArchievableLookup.  This class hierarchy seems a bit off.  It differs from the Objective-C version.  It is thought the VersionableArchievable side should perhaps resemble the class hierarchy of the Lookup side which is the same on both Objective-C and Java.  The Test classes themselves also need to follow the same inherritence in order to function as expected.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class AllTestsSuite extends EOTestSuite
{

    /**
     * Designated constuctor.
     */
    public AllTestsSuite()
    {
        super();

        EOFValidation.installValidation();

        addTest(new TestSuite(TestLookupTest.class));
        addTest(new TestSuite(TestKeyedLookupTest.class));
        addTest(new TestSuite(TestVersionedObjectTest.class));
        addTest(new TestSuite(TestArchivableLookupTest.class));
        addTest(new TestSuite(TestArchivableKeyedLookupTest.class));
        addTest(new TestSuite(TestVersionedArchivableLookupTest.class));
        addTest(new TestSuite(TestOrderedLookupTest.class));
    }



    public static Test suite()
    {
        return new AllTestsSuite();
    }


}
