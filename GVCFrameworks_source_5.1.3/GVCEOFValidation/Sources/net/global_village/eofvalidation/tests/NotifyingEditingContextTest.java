package net.global_village.eofvalidation.tests;

import com.webobjects.eoaccess.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;


/**
 * Tests for NotifyingEditingContext.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class NotifyingEditingContextTest extends GVCJUnitEOTestCase
{
    NotifyingEditingContextTestObject testObject;


    /**
     * Designated constuctor.
     */
    public NotifyingEditingContextTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        // Make sure all tests use our fancy new editing context subclass...
        editingContext.unlock();
        editingContext.dispose();
        editingContext = new NotifyingEditingContext();
        editingContext.lock();

        testObject = new NotifyingEditingContextTestObject();
        editingContext().insertObject(testObject);
        testObject.setStringValue("original value");
    }



    /**
     * Test insert notifications
     */
    public void testInsertNotifications()
    {
        editingContext().saveChanges();
        assertEquals(1, testObject.calledHasInserted);
    }



    /**
     * Test delete notifications
     */
    public void testDeleteNotifications()
    {
        // Make the test object permanent first.
        editingContext().saveChanges();

        editingContext().deleteObject(testObject);
        assertEquals(1, testObject.calledWillDelete);

        // Test redundant call
        editingContext().deleteObject(testObject);
        assertEquals(1, testObject.calledWillDelete);

        editingContext().saveChanges();
        assertEquals(1, testObject.calledWillDelete);
        assertEquals(1, testObject.calledHasDeleted);
    }



    /**
     * Test update notifications
     */
    public void testUpdateNotifications()
    {
        editingContext().saveChanges();
        testObject.setStringValue("test");
        assertEquals(1, testObject.calledWillUpdate);
        editingContext().processRecentChanges();  // Should have no effect
        assertEquals(1, testObject.calledWillUpdate);
        assertEquals(0, testObject.calledHasUpdated);

        testObject.reset();
        editingContext().saveChanges();
        assertEquals(1, testObject.calledWillUpdate);
        assertEquals(1, testObject.calledHasUpdated);

        // Set up a related object so that we can test additional objects getting
        // updated via willUpdate().
        NotifyingEditingContextTestObject relatedObject =
            new NotifyingEditingContextTestObject();
        editingContext().insertObject(relatedObject);
        relatedObject.setStringValue("related");
        testObject.setRelatedObject(relatedObject);
        editingContext().saveChanges();

        // Needed as willUpdate on testObject will change this and interefere
        // with the test below.
        relatedObject.setStringValue("related");
        editingContext().saveChanges();

        // Needed for the next tests
        testObject.reset();
        relatedObject.reset();

        testObject.setStringValue("willUpdate test");
        editingContext().saveChanges();

        assertEquals(1, testObject.calledWillUpdate);
        assertEquals(1, testObject.calledHasUpdated);
        assertEquals(1, relatedObject.calledWillUpdate);
        assertEquals(1, relatedObject.calledHasUpdated);
    }



    /**
      * Test EOEditingContext nested in a NotifyingEditingContext
      * works correctly
      */
     public void testNesting()
     {
         // We don't want the partially constructed object.
         editingContext().revert();

         // Setup of nesting
         EOEditingContext nestedEC = new EOEditingContext(editingContext());
         NotifyingEditingContextTestObject nestedObject = new NotifyingEditingContextTestObject();
         nestedEC.insertObject(nestedObject);
         nestedObject.setStringValue("nested value");

         // Test insertion notifications
         nestedEC.saveChanges();
         assertEquals(0, nestedObject.calledHasInserted);

         // Now that the child has saved we can get a reference to the equivalent
         // object in the parent.
         NotifyingEditingContextTestObject parentObject =
             (NotifyingEditingContextTestObject)
             EOUtilities.localInstanceOfObject(editingContext(), nestedObject);

         editingContext().saveChanges();
         assertEquals(1, parentObject.calledHasInserted);

         // Reset for next test
         nestedObject.reset();
         parentObject.reset();

         // Test update notifications
         nestedObject.setStringValue("new nested value");
         nestedEC.saveChanges();
         assertEquals(0, nestedObject.calledWillUpdate);
         assertEquals(0, nestedObject.calledHasUpdated);
         assertEquals(0, parentObject.calledWillUpdate);
         assertEquals(0, parentObject.calledHasUpdated);

         editingContext().saveChanges();
         assertEquals(0, nestedObject.calledWillUpdate);
         assertEquals(0, nestedObject.calledHasUpdated);
         assertEquals(1, parentObject.calledWillUpdate);
         assertEquals(1, parentObject.calledHasUpdated);

         // Reset for next test
         nestedObject.reset();
         parentObject.reset();


         // Test delete notifications
         nestedEC.deleteObject(nestedObject);
         assertEquals(0, nestedObject.calledWillDelete);
         assertEquals(0, parentObject.calledWillDelete);

         nestedEC.saveChanges();
         assertEquals(0, nestedObject.calledWillDelete);
         assertEquals(0, nestedObject.calledHasDeleted);
         assertEquals(1, parentObject.calledWillDelete);
         assertEquals(0, parentObject.calledHasDeleted);

         editingContext().saveChanges();
         assertEquals(0, nestedObject.calledWillDelete);
         assertEquals(0, nestedObject.calledHasDeleted);
         assertEquals(1, parentObject.calledWillDelete);
         assertEquals(1, parentObject.calledHasDeleted);
     }




     /**
      * Test NotifyingEditingContext nested in a NotifyingEditingContext
      * works correctly
      */
     public void testDoubleNesting()
     {
         // We don't want the partiall constructed object.
         editingContext().revert();

         // Setup of nesting
         EOEditingContext nestedEC = new NotifyingEditingContext(editingContext());
         NotifyingEditingContextTestObject nestedObject = new NotifyingEditingContextTestObject();
         nestedEC.insertObject(nestedObject);
         nestedObject.setStringValue("nested value");

         // Test insertion notifications
         nestedEC.saveChanges();
         assertEquals(1, nestedObject.calledHasInserted);

         // Now that the child has saved we can get a reference to the equivalent
         // object in the parent.
         NotifyingEditingContextTestObject parentObject =
             (NotifyingEditingContextTestObject)
             EOUtilities.localInstanceOfObject(editingContext(), nestedObject);

         editingContext().saveChanges();
         assertEquals(1, parentObject.calledHasInserted);

         // Reset for next test
         nestedObject.reset();
         parentObject.reset();

         // Test update notifications
         nestedObject.setStringValue("new nested value");
         nestedEC.saveChanges();
         assertEquals(1, nestedObject.calledWillUpdate);
         assertEquals(1, nestedObject.calledHasUpdated);
         assertEquals(0, parentObject.calledWillUpdate);
         assertEquals(0, parentObject.calledHasUpdated);

         editingContext().saveChanges();
         assertEquals(1, nestedObject.calledWillUpdate);
         assertEquals(1, nestedObject.calledHasUpdated);
         assertEquals(1, parentObject.calledWillUpdate);
         assertEquals(1, parentObject.calledHasUpdated);

         // Reset for next test
         nestedObject.reset();
         parentObject.reset();


         // Test delete notifications
         nestedEC.deleteObject(nestedObject);
         assertEquals(1, nestedObject.calledWillDelete);
         assertEquals(0, parentObject.calledWillDelete);

         nestedEC.saveChanges();
         assertEquals(1, nestedObject.calledWillDelete);
         assertEquals(1, nestedObject.calledHasDeleted);
         assertEquals(1, parentObject.calledWillDelete);
         assertEquals(0, parentObject.calledHasDeleted);

         editingContext().saveChanges();
         assertEquals(1, nestedObject.calledWillDelete);
         assertEquals(1, nestedObject.calledHasDeleted);
         assertEquals(1, parentObject.calledWillDelete);
         assertEquals(1, parentObject.calledHasDeleted);
     }



     /**
      * Test insert notifications
      */
     public void testRevertNotifications()
     {
         editingContext().revert();
         assertEquals(1, testObject.calledHasReverted);
     }



    /**
     * Common test code.
     */
    public void tearDown() throws java.lang.Exception
    {
        if (testObject.editingContext() !=  null)
        {
            editingContext().deleteObject(testObject);
        }
        editingContext().saveChanges();
        super.tearDown();
    }



}
