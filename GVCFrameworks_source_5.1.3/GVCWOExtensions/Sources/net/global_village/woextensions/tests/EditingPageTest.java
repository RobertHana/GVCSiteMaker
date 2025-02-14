package net.global_village.woextensions.tests;


import junit.framework.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.testeomodelbase.*;
import net.global_village.woextensions.*;


/**
 * Tests for EditingPageTest.  Some of the functionality in EditingPage is impossible to test without
 * creating pages based on this class and submitting full form submits to the page. For now I will defer
 * "testing" of those areas to the actual use of this class.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EditingPageTest extends TestCase
{
    EditingPage pageA;
    EditingPage pageB;
    WOContext contextA;
    WOContext contextB;
    
    
    public EditingPageTest(String name)
    {
        super(name);
    }
    
    
    
    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        
        // We need that URL exactly as specified (or at least the "WebObjects" part)
        WORequest request = new WORequest("GET", "http://localhost/cgi-bin/WebObjects", "HTTP/1.1", null, null, null);
        contextA = new WOContext(request);
        contextA.session();  // Wake up session
        pageA = (EditingPage) com.webobjects.appserver.WOApplication.application().pageWithName("EditingPage", contextA);
        pageA._setIsPage(true);
        contextA._setPageComponent(pageA);
        pageA._awakeInContext(contextA);
        contextA.incrementLastElementIDComponent();  // elementID() is null if this is not done
        
        // We need that URL exactly as specified (or at least the "WebObjects" part)
        request = new WORequest("GET", "http://localhost/cgi-bin/WebObjects", "HTTP/1.1", null, null, null);
        contextB= new WOContext(request);
        contextB.session();  // Wake up session
        pageB = (EditingPage) com.webobjects.appserver.WOApplication.application().pageWithName("EditingPage", contextB);
        pageB._setIsPage(true);
        contextB._setPageComponent(pageB);
        pageB._awakeInContext(contextB);
        contextB.incrementLastElementIDComponent();  // elementID() is null if this is not done
   }
    
    
    
    public void tearDown() throws Exception
    {
        // Put the session to sleep so that it unlocks the editing contexts
        contextA.session()._sleepInContext(contextA);
        contextB.session()._sleepInContext(contextB);
        super.tearDown();
    }

    
    
    public void testValidationFailureRecording()
    {
        pageA.recordValidationFailureForKey("error message", "stringKey");
        assertTrue(pageA.hasValidationFailures());
        assertTrue(pageA.hasValidationFailureForKey("stringKey"));
        
        pageA.recordValidationFailureForKey(new NSArray("array error message"), "arrayKey");
        assertTrue(pageA.hasValidationFailures());
        assertTrue(pageA.hasValidationFailureForKey("arrayKey"));
  
        assertEquals("error message", pageA.validationFailureMessageForKey("stringKey"));
        assertEquals(new NSArray("array error message"), pageA.validationFailureForKey("arrayKey"));

        assertEquals(pageA.validationFailures().count(), pageA.validationFailuresAsList().count());
        assertEquals(new NSSet(new Object[] {"error message", new NSArray("array error message")}), new NSSet(pageA.validationFailuresAsList()));
        
        pageA.generateResponse();
        assertFalse(pageA.hasValidationFailures());
    }
    
    
    
    public void testValidationFailedWithException()
    {
        // Test special handling for EOFValidationException
        assertFalse(pageA.hasValidationFailures());
        EOFValidationException validationException = new EOFValidationException("AttributeValidationTestEntity.localizationTestProperty.fullyQualifiedBundleKeyTest");
        pageA.validationFailedWithException(validationException, null, "some.path");
        assertTrue(pageA.hasValidationFailures());
        assertEquals("Localization key for fully qualified bundle key", pageA.validationFailureMessageForKey(contextA.elementID()));
        
        assertEquals(pageA.validationFailures().count(), pageA.validationFailuresAsList().count());
        assertEquals(new NSArray("Localization key for fully qualified bundle key"), pageA.validationFailuresAsList());

        
        // Test handling for all other exception types
        assertFalse(pageB.hasValidationFailures());
        Exception exception = new Exception("Some reason");
        pageB.validationFailedWithException(exception, null, "some.path");
        assertTrue(pageB.hasValidationFailures());
        assertEquals("Some reason", pageB.validationFailureMessageForKey(contextB.elementID()));
        
        assertEquals(pageB.validationFailures().count(), pageB.validationFailuresAsList().count());
        assertEquals(new NSArray("Some reason"), pageB.validationFailuresAsList());
    }
    
    
    
    /**
     * Tests basic detection of validation errors and handling of validation errors during save changes.
     */
    public void testBasicSaveAndGoForward()
    {
        SimpleTestEntity eo = (SimpleTestEntity)EOUtilities.createAndInsertInstance(pageA.editingContext(), "SimpleTestEntity");
        
        // Test that saveChanges is not called if there are validation errors
        pageA.recordValidationFailureForKey("error message", "stringKey");         // Dummy error
        assertTrue(pageA.hasValidationFailures());
        pageA.saveAndGoForward();
        assertTrue(pageA.hasValidationFailures());
        
        // Test that saveChanges is called if there are no validation errors
        pageA.generateResponse(); // calls appendToContext to clear validation errors
        assertFalse(pageA.hasValidationFailures());
        pageA.saveAndGoForward();
        assertTrue(pageA.hasValidationFailures());
        assertTrue(pageA.hasValidationFailureForKey(EditingPage.saveChangesExceptionsKey));

        // Test that saveChanges can succeed and leave no validation errors
        pageA.generateResponse(); // calls appendToContext to clear validation errors
        eo.setName("foo");
        pageA.saveAndGoForward();
        // Cleanup
        pageA.editingContext().deleteObject(eo);
        pageA.editingContext().saveChanges();
        
        assertFalse(pageA.hasValidationFailures());
        assertEquals(NSArray.EmptyArray, pageA.editingContext().insertedObjects());
    }
    
    
    
    /**
     * Tests detection and handling of optimistic locking within a single EOF stack.
     */
    public void testSingleStackOptimisticLockHandling()
    {
        // Create object to use in test, verify initial state
        SimpleTestEntity eoA = (SimpleTestEntity)EOUtilities.createAndInsertInstance(pageA.editingContext(), "SimpleTestEntity");
        eoA.setName("SSOLH");
        pageA.saveAndGoForward();
        
        try
        {
            assertFalse(pageA.hasValidationFailures());
            assertEquals(NSArray.EmptyArray, pageA.editingContext().insertedObjects());
            SimpleTestEntity eoB = (SimpleTestEntity)EOUtilities.localInstanceOfObject(pageB.editingContext(), eoA);

            // Change object in page B
            eoB.setName("Name in B");
            pageB.saveAndGoForward();
            assertFalse(pageB.hasValidationFailures());
 
            /* This test is not a good test of the actual functionality.  In a real application these pages would be in 
             * different sessions and be processed by different threads.  As they are in the same thread here, notifications
             * from one editing context will be immediately processed by the other even though it is locked.  Thus some of the
             * tests we would have liked to have had are not possible:
             * assertFalse(pageA.optimisticLockingConflictDetected());
             * contextA.session()._sleepInContext(contextA);
             * contextA.session();
             * assertTrue(pageA.optimisticLockingConflictDetected());
             */
            
            assertTrue(pageA.hasOptimisticLockingFailures());
            assertTrue(pageA.hasValidationFailureForKey(EditingPage.optimisticLockingExceptionKey));
            assertTrue(pageA.hasValidationFailureForKey(EditingPage.optimisticLockingExceptionObjectKey));
            assertEquals(eoA, pageA.validationFailureForKey(EditingPage.optimisticLockingExceptionObjectKey));
            assertEquals(pageA.optimisticLockingFailureMessage(eoA), pageA.validationFailureForKey(EditingPage.optimisticLockingExceptionKey));
        }
        finally
        {
            // Cleanup
            pageA.editingContext().deleteObject(eoA);
            pageA.editingContext().saveChanges(); 
        }
    }
    
    
    
    /**
     * Tests detection and handling of optimistic locking across EOF stacks / instances.
     */
    public void testCrossStackOptimisticLockHandling()
    {
        // Create object to use in test, verify initial state
        SimpleTestEntity eoA = (SimpleTestEntity)EOUtilities.createAndInsertInstance(pageA.editingContext(), "SimpleTestEntity");
        eoA.setName("SSOLH");
        pageA.saveAndGoForward();
        
        // In order create an optimistic locking failure during a saveChanges, 
        // we need to create an editing context with its own EOF stack so that it has its own snapshots.
        EOObjectStoreCoordinator otherOSC = new EOObjectStoreCoordinator(); 
        otherOSC.lock();
        EOEditingContext editingContextInOtherOSC = new EOEditingContext(otherOSC);

        // fault eoA into the second EOF stack, modify it and save changes to the database
        editingContextInOtherOSC.lock();
        try
        {
            SimpleTestEntity testEntityInOtherOSC = (SimpleTestEntity) EOUtilities.localInstanceOfObject(editingContextInOtherOSC, eoA);
            testEntityInOtherOSC.setName("Updated Value");
            editingContextInOtherOSC.saveChanges();
        }
        finally
        {
            editingContextInOtherOSC.unlock();
        }

        try
        {
            assertFalse(pageA.hasValidationFailures());
            assertEquals(NSArray.EmptyArray, pageA.editingContext().insertedObjects());
 
            // Change object in page A
            eoA.setName("new Name");
            pageA.saveAndGoForward();
            
            assertTrue(pageA.hasOptimisticLockingFailures());
            assertTrue(pageA.hasValidationFailureForKey(EditingPage.optimisticLockingExceptionKey));
            assertTrue(pageA.hasValidationFailureForKey(EditingPage.optimisticLockingExceptionObjectKey));
            assertEquals(eoA, pageA.validationFailureForKey(EditingPage.optimisticLockingExceptionObjectKey));
            assertEquals(pageA.optimisticLockingFailureMessage(eoA), pageA.validationFailureForKey(EditingPage.optimisticLockingExceptionKey));
        }
        finally
        {
            // Cleanup
            pageA.editingContext().deleteObject(eoA);
            pageA.editingContext().saveChanges(); 
        }
    }

}
