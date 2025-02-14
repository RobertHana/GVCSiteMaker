package net.global_village.eofvalidation.tests;

import java.math.*;
import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;

import net.global_village.eofvalidation.*;
import net.global_village.eofvalidation.EOEditingContext;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test EOEditingContext.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class EOEditingContextTest extends GVCJUnitEOTestCase
{
    ToOneEntity testObject1;
    ToOneEntity testObject2;
    com.webobjects.eocontrol.EOEditingContext ec1;
    com.webobjects.eocontrol.EOEditingContext ec2;
    EOObjectStoreCoordinator osc; 


    /**
     * Designated constuctor.
     */
    public EOEditingContextTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        // Editing context with default object store co-ordinator.
        ec1 = new net.global_village.eofvalidation.EOEditingContext();
        testObject1 = new ToOneEntity();
        ec1.insertObject(testObject1);
        testObject1.setName(globallyUniqueString());  
        ec1.saveChanges();							  
                           
        // In order to simulate adapator level errors during a saveChanges, we need to create an editing context with its own EOF stack so that it has its own snapshots.
         osc = new EOObjectStoreCoordinator(); 
         ec2 = new net.global_village.eofvalidation.EOEditingContext(osc);
    }



    /**
     * Test Optimistic Locking Failures betweem the two editing contexts
     */
    public void testOptimisticLockingFailureOnUpdate()
    {
        testObject2 = (ToOneEntity)EOUtilities.localInstanceOfObject(ec2, testObject1);
        testObject2.setName(globallyUniqueString());

        testObject1.setName(globallyUniqueString());

        try
        {
            ec1.saveChanges();
            ec2.saveChanges();
            fail("Did not raise exception for testOptimisticLockingFailureOnUpdate");
        }
        catch (EOFValidationException e)
        {
            assertTrue(e.isExceptionForFailure(null, EOFValidation.OptimisticLockingFailure));
        }
    }



    /**
     * Test integrity constraint violation
     *
     *  This test requires a contstraint to be place on databse:
     *  Oracle:
     *      <code>ALTER TABLE entity_with_contraints ADD CONSTRAINT quantity_below_10 CHECK (quantity < 10);</code>
     *  FrontBase:
     *      <code>ALTER TABLE entity_with_constraints ADD CONSTRAINT quantity_below_10 CHECK (quantity < 10);</code>
     */
    public void testIntegrityConstraintViolation()
    {
        EOEditingContext ec = new EOEditingContext();
        EntityWithConstraints constrainedObject = new EntityWithConstraints();
        ec.insertObject(constrainedObject);
        constrainedObject.setQuantity(new BigDecimal("19.0"));

        try
        {
            ec.saveChanges();
            fail("Test constraint of 'quantity < 10.0' missing from database");
        }
        catch (EOFValidationException e)
        {
            assertTrue(e.originalException() != null);
            assertTrue(e.originalException() instanceof EOGeneralAdaptorException);
            assertTrue(NSExceptionAdditions.isIntegrityConstraintViolation((EOGeneralAdaptorException) e.originalException()));
            assertEquals(NSExceptionAdditions.violatedIntegrityConstraintName((EOGeneralAdaptorException) e.originalException()),
                         "quantity_below_10");
            assertEquals(e.getLocalizedMessage(), "This cannot be saved as it violates integrity constraint quantity_below_10.  Check your changes and re-submit.");
        }
    }



    /**
     * Common test code.
     */
    public void tearDown() throws java.lang.Exception
    {
        ec1.revert();
        ec2.revert();

        // Try to cleanup.
        try
        {
            ec1.deleteObject(testObject1);
            ec1.saveChanges();
        }
        catch (Throwable t){}


        Enumeration databaseContextEnumerator = osc.cooperatingObjectStores().objectEnumerator();

        while (databaseContextEnumerator.hasMoreElements())
        {
            EODatabaseContext currentDatabaseContext = (EODatabaseContext) databaseContextEnumerator.nextElement();

            if( ! currentDatabaseContext.hasBusyChannels())
            {
                Enumeration registeredChannelEnumerator =
                currentDatabaseContext.registeredChannels().objectEnumerator();

                while (registeredChannelEnumerator.hasMoreElements())
                {
                    ((EODatabaseChannel)registeredChannelEnumerator.nextElement()).adaptorChannel().closeChannel();
                }

                osc.removeCooperatingObjectStore(currentDatabaseContext);
            }
            else
            {
                // Should probably handle this in a more robust way...
                System.out.println("Busy channel preventing diconnection!");
            }
        }

        super.tearDown();
    }



}
