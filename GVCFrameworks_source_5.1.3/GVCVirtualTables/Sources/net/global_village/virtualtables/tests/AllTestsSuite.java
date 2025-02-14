package net.global_village.virtualtables.tests;

import junit.framework.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.virtualtables.*;


/**
 * Contains all tests.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class AllTestsSuite extends EOTestSuite
{


    public static Test suite()
    {
        return new AllTestsSuite();
    }



    public AllTestsSuite()
    {
        super();

        EOFValidation.installValidation();
        WOApplication.primeApplication(null, null, null);
        VirtualTable.initializeVirtualTables(1000, new NSArray());

        addTest(new TestSuite(ColumnTest.class));
        addTest(new TestSuite(VirtualTableTest.class));
        addTest(new TestSuite(VirtualRowTest.class));
        addTest(new TestSuite(CalculatedColumnTest.class));
        addTest(new TestSuite(PerformanceTest.class));
    }



}
