package net.global_village.eofvalidation.tests;

import junit.framework.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;


/**
 * Test suite that runs EOFValidation tests against multiple EO Adaptors.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class AllTestsSuite extends EOTestSuite
{


	/**
     * Preferred constuctor.
     */
    public AllTestsSuite ()
    {
        super();


        EOFValidation.installValidation();
        WOApplication.primeApplication(null, null, null);
        // This is just an informational note from Fronbase when the UPDATE for ec2.saveChanges() fails to update a row.
        if (databaseType().equals("FrontBase") && NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelInformational))
        {
            NSLog.debug.appendln("\nIngore any errors like 'Completion condition 250. No data.'");
        }

        addTest(new TestSuite(EOFValidationTest.class));
        addTest(new TestSuite(EOEntityAdditionsTest.class));
        addTest(new TestSuite(LocalizationEngineTest.class));
        addTest(new TestSuite(EOFValidationExceptionTest.class));
        addTest(new TestSuite(EOAttributeValidatorTest.class));
        addTest(new TestSuite(EORelationshipValidatorTest.class));
        addTest(new TestSuite(NSExceptionAdditionsTest.class));
        addTest(new TestSuite(EOEditingContextTest.class));
        addTest(new TestSuite(NotifyingEditingContextTest.class));
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
