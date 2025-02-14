package net.global_village.eofextensions.tests;

import junit.framework.*;

import net.global_village.gvcjunit.*;



/**
 * Test suite to launch all tests.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 14$
 */
public class AllTestsSuite extends EOTestSuite
{


    public static Test suite()
    {
        return new AllTestsSuite();
    }



    public AllTestsSuite(String name)
    {
        super();
    }



    /**
     * Designated constuctor.
     */
    public AllTestsSuite ()
    {
        super();
        addTest(new TestSuite(ModelConnectorTest.class));
        addTest(new TestSuite(EOEditingContextAdditionsTest.class));
        addTest(new TestSuite(EOAdaptorChannelAdditionsTest.class));
        addTest(new TestSuite(NSArrayQualifierAdditionsTest.class));
        addTest(new TestSuite(EOObjectTest.class));
        addTest(new TestSuite(GenericRecordTest.class));
        addTest(new TestSuite(EOCopyingTest.class));
        addTest(new TestSuite(GVCFacadeTest.class));
        addTest(new TestSuite(EOSQLExpressionAdditionsTest.class));
        addTest(new TestSuite(FBIntegerPrimaryKeyGeneratorTest.class));
        addTest(new TestSuite(KeyValueCodingTests.class));
        addTest(new TestSuite(EOModelGroupAdditionsTest.class));  // Needs to run last, since it messes with the connection dictionaries and reconnects...
    }



}
