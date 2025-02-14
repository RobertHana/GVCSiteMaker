package net.global_village.eofvalidation.tests;

import java.math.*;
import java.sql.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.jdbcadaptor.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Tests for NSExceptionAdditions
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class NSExceptionAdditionsTest extends GVCJUnitEOTestCase
{
    ToOneEntity testObject1;
    ToOneEntity testObject2;
    com.webobjects.eocontrol.EOEditingContext ec1;
    com.webobjects.eocontrol.EOEditingContext ec2;
    EOObjectStoreCoordinator osc; 
    EOGeneralAdaptorException optimisticLockingFailureException;
    EOGeneralAdaptorException integrityConstraintViolationException;
    RuntimeException wrongExceptionType;
    JDBCAdaptorException jdcbAdaptorException;

    /**
     * Designated constuctor.
     */
    public NSExceptionAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        // The whole point of this setup to to create an exception of the appropriate type.
        
        // Editing context with default object store co-ordinator.
        ec1 = new net.global_village.eofvalidation.EOEditingContext();
        testObject1 = new ToOneEntity();
        ec1.insertObject(testObject1);
        testObject1.setName(globallyUniqueString());
        ec1.saveChanges();							

        // In order to simulate adapator level errors during a saveChanges, we need to create an editing context with its own EOF stack so that it has its own snapshots.  Also make it be an instance of the standard editing context so that it doesn't raise an EOFValidationException.
         osc = new EOObjectStoreCoordinator(); 
         ec2 = new com.webobjects.eocontrol.EOEditingContext(osc);

         testObject2 = (ToOneEntity)EOUtilities.localInstanceOfObject(ec2, testObject1);
         testObject2.setName(globallyUniqueString());

         testObject1.setName(globallyUniqueString());

         try
         {
             ec1.saveChanges();
             ec2.saveChanges();
         }
         catch (EOGeneralAdaptorException e)
         {
             optimisticLockingFailureException = e;
         }

         // Use EC2 as we do not want the exception interpreting functionality in net.global_village.eofvalidation.EOEditingContext
         ec2.revert();
         EntityWithConstraints constrainedObject = new EntityWithConstraints();
         ec2.insertObject(constrainedObject);
         constrainedObject.setQuantity(new BigDecimal("22.0"));
         try
         {
             ec2.saveChanges();
         }
         catch (EOGeneralAdaptorException e)
         {
             integrityConstraintViolationException = e;
         }
         
         // An NSException without the special adaptor failure exception dictionary.
         wrongExceptionType = new RuntimeException("NSGenericException");
         
         jdcbAdaptorException =  new JDBCAdaptorException(new SQLException("Exception condition 361. Integrity constraint violation (FOREIGN KEY, Entity_With_Constraints.EntityWithConstraints_FakeName(quantity)).", "23000"));
    }



    /**
     * Test isAdaptorOperationFailureException
     */
    public void testisAdaptorOperationFailureException()
    {
        // Check DBC
        try { NSExceptionAdditions.isAdaptorOperationFailureException(null);
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        assertTrue(NSExceptionAdditions.isAdaptorOperationFailureException(optimisticLockingFailureException));
    }



    /**
     * Test isOptimisticLockingFailure
     */
    public void testIsOptimisticLockingFailure()
    {
        // Check DBC
        try { NSExceptionAdditions.isOptimisticLockingFailure(null);
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        assertTrue(NSExceptionAdditions.isOptimisticLockingFailure(optimisticLockingFailureException));
    }

    
    
    /**
     * Test isIntegrityConstraintViolation
     */
    public void testIsIntegrityConstraintViolation()
    {
        // Check DBC
        try { NSExceptionAdditions.isIntegrityConstraintViolation(null);
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        assertTrue(NSExceptionAdditions.isIntegrityConstraintViolation(integrityConstraintViolationException));
        assertTrue(NSExceptionAdditions.isIntegrityConstraintViolation(jdcbAdaptorException));
        
    }
    
    
    /**
     * Test 
     */
    public void testViolatedIntegrityConstraintName()
    {
        assertEquals(NSExceptionAdditions.violatedIntegrityConstraintName(integrityConstraintViolationException),
                      "quantity_below_10");
        assertEquals(NSExceptionAdditions.violatedIntegrityConstraintName(jdcbAdaptorException),
        "EntityWithConstraints_FakeName");
    }
    


    /**
     * Test objectSaveFailedOn
     */
    public void testObjectSaveFailedOn()
    {
        // Check DBC
        try { NSExceptionAdditions.objectSaveFailedOn((EOGeneralAdaptorException)null);
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        assertEquals(NSExceptionAdditions.objectSaveFailedOn(optimisticLockingFailureException), testObject2);
    }



    /**
     * Test entitySaveFailedOn
     */
    public void testEntitySaveFailedOn()
    {
        // Check DBC
        try { NSExceptionAdditions.entitySaveFailedOn(null, testObject2.editingContext());
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        assertEquals(NSExceptionAdditions.entitySaveFailedOn(optimisticLockingFailureException, testObject2.editingContext()), EOObject.entityForSelf(testObject2));
        EOEntity entityWithContraints = EOUtilities.modelGroup(ec1).entityNamed("EntityWithConstraints");
        assertEquals(NSExceptionAdditions.entitySaveFailedOn(jdcbAdaptorException, testObject2.editingContext()), entityWithContraints);
    }



    /**
     * Test failedAdaptorOperator
     */
    public void testFailedAdaptorOperator()
    {
        // Check DBC
        try { NSExceptionAdditions.failedAdaptorOperator(null);
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        assertTrue(NSExceptionAdditions.failedAdaptorOperator(optimisticLockingFailureException) == EODatabaseOperation.AdaptorUpdateOperator);
    }



    /**
     * Test databaseExceptionReason
     */
    public void testDatabaseExceptionReason()
    {
        // Check DBC
        try { NSExceptionAdditions.databaseExceptionReason(null);
            fail("Accepted null exception");}
        catch (RuntimeException t) {}

        // Kind of a lame test, but at least we can see that it is getting the right string.
        assertTrue(NSExceptionAdditions.databaseExceptionReason(optimisticLockingFailureException).startsWith("com.webobjects.eoaccess.EOGeneralAdaptorException: updateValuesInRowDescribedByQualifier"));
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

        super.tearDown();
    }



}
