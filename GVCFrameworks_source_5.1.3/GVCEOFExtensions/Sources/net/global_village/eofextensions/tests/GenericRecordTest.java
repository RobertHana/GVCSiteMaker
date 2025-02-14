package net.global_village.eofextensions.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.gvcjunit.*;


/**
 * Test the GenericRecord functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class GenericRecordTest extends GVCJUnitEOTestCase
{
    protected static final String TEST_VALUE = "test value";

    protected TestEntity testEntity;
    protected TestEntityWithRelationship relatedTestEntity;
    protected KeyPathTestEntity keyPathTestEntity = null;
    protected KeyPathTestEntity keyPathTestEntityInOtherOSC = null;
    protected KeyPathTestEntity keyPathTestEntityInSameOSC = null;
    protected com.webobjects.eocontrol.EOEditingContext editingContextInSameOSC;
    protected com.webobjects.eocontrol.EOEditingContext editingContextInOtherOSC;
    protected EOObjectStoreCoordinator otherOSC;
    NSSelector squawker = new NSSelector("squawk", new Class[] {NSNotification.class});


    /**
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
    public GenericRecordTest(String name)
     {
         super(name);
     }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        testEntity = new TestEntity();
        editingContext().insertObject(testEntity);
        testEntity.setName("testEntity");

        relatedTestEntity = new TestEntityWithRelationship();
        editingContext().insertObject(relatedTestEntity);

        // In order create an optimistic locking failure during a saveChanges,
        // we need to create an editing context with its own EOF stack so that it has its own snapshots.
        otherOSC = new EOObjectStoreCoordinator();
        otherOSC.lock();
        editingContextInSameOSC = new EOEditingContext();
        editingContextInOtherOSC = new EOEditingContext(otherOSC);

        // Create the original object
        editingContextInSameOSC.lock();
        try
        {
            keyPathTestEntity = new KeyPathTestEntity();
            editingContextInSameOSC.insertObject(keyPathTestEntity);
            keyPathTestEntity.setName("Original Name");
            keyPathTestEntity.setValue("Original Value");
            editingContextInSameOSC.saveChanges();
        }
        finally
        {
            editingContextInSameOSC.unlock();
        }

        keyPathTestEntityInSameOSC = (KeyPathTestEntity) EOUtilities.localInstanceOfObject(editingContext(), keyPathTestEntity);
        keyPathTestEntityInSameOSC.willRead();

        // fault keyPathTestEntity into the second EOF stack, modify it and save changes to the database
        editingContextInOtherOSC.lock();
        try
        {
            keyPathTestEntityInOtherOSC = (KeyPathTestEntity) EOUtilities.localInstanceOfObject(editingContextInOtherOSC, keyPathTestEntity);
            keyPathTestEntityInOtherOSC.setValue("Updated Value");
            editingContextInOtherOSC.saveChanges();
        }
        finally
        {
            editingContextInOtherOSC.unlock();
        }

        // Uncomment these or move them into a test for debugging
        // Also uncomment the lines in tearDown.
        //EOFDebug.logSQL(true);
        //NSNotificationCenter.defaultCenter().addOmniscientObserver(this, squawker);
    }


    /**
     * This method can be useful for debugging these tests.
     * @param notification
     */
    public void squawk(NSNotification notification)
    {
        NSLog.out.appendln(notification.name() + " for " + notification.object());
        if (notification.userInfo().count() > 0) NSLog.out.appendln("   user info " + notification.userInfo());
    }



    /**
     * Test entityForSelf
     */
    public void testEntityForSelf()
    {
        TestEntity testEntityNotInEC = new TestEntity();

        // Using EOUtilities.entityForObject rather than EOModelGroup.defaultGroup().entityForObject can cause
        // sporadic test failures when EOUtilities.entityForObject picks up the model group from ModelConnectorTest
        assertEquals(testEntity.entity(), EOModelGroup.defaultGroup().entityForObject(testEntity));
        assertEquals(testEntity.entity(), EOModelGroup.defaultGroup().entityForObject(testEntityNotInEC));
        assertEquals(testEntity.entity(), EOModelGroup.defaultGroup().entityForObject(testEntityNotInEC));
    }


    /**
     * Test globalID
     */
    public void testGlobalID()
    {
        assertTrue(testEntity.globalID() != null);
        assertTrue(editingContext().globalIDForObject(testEntity).equals(testEntity.globalID()));
    }



    /**
     * Test attributeWithName
     */
    public void testAttributeWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("TestEntity");
        assertEquals(testEntity.attributeNamed("name"), theEntity.attributeNamed("name"));
    }



    /**
     * Test relationshipWithName
     */
    public void testRelationshipWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("TestEntityWithRelationship");
        assertEquals(relatedTestEntity.relationshipNamed("mandatoryToOne"),
                     theEntity.relationshipNamed("mandatoryToOne"));
    }



    /**
     * Test propertyWithName
     */
    public void testPropertyWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("TestEntityWithRelationship");
        assertEquals(relatedTestEntity.propertyNamed("name"), theEntity.attributeNamed("name"));
        assertEquals(relatedTestEntity.propertyNamed("mandatoryToOne"), theEntity.relationshipNamed("mandatoryToOne"));
    }



    /**
     * Test propertyWithName
     */
    public void testCommitedValueFor()
    {
        // Verify initial state
        assertEquals("Original Name", keyPathTestEntity.name());
        assertEquals(keyPathTestEntity.name(), keyPathTestEntity.committedValueFor("name"));

        // Update the name and verify change
        keyPathTestEntity.setName("other name");
        assertEquals("other name", keyPathTestEntity.name());

        // Verify that both access methods yield the same result
        assertEquals("Original Name", keyPathTestEntity.committedValueFor("name"));
        assertEquals("Original Name", keyPathTestEntity.valueForKeyPath("committedValueFor.name"));

        // Verify handling for uncommitted EOs
        assertEquals(null, testEntity.committedValueFor("name"));
    }



    /**
     * Test relationshipFromKeyPath
     */
    public void testIsDummyFaultEO()
    {
        assertTrue("temporary object was detected as dummy", ! relatedTestEntity.isDummyFaultEO());

        EOEditingContext localEC = new EOEditingContext();
        localEC.lock();
        try
        {

            TestEntity dummyObject = new TestEntity();
            localEC.insertObject(dummyObject);
            dummyObject.setName("permObjTest");
            localEC.saveChanges();
            try
            {
                assertTrue("saved object was detected as dummy", ! dummyObject.isDummyFaultEO());
            }
            finally
            {
                localEC.deleteObject(dummyObject);
                localEC.saveChanges();
            }
            dummyObject = (TestEntity)EOUtilities.faultWithPrimaryKeyValue(localEC, "TestEntity", new Integer(-1));
            assertTrue("dummy object not detected", dummyObject.isDummyFaultEO());
        }
        finally
        {
            localEC.unlock();
            localEC.dispose();
        }
    }



    public void testRefreshObject()
    {
        editingContextInSameOSC.lock();
        try
        {
            keyPathTestEntity.setName("Updated Name");
            assertEquals("Did not update name", "Updated Name", keyPathTestEntity.name());
            assertEquals("Value changed since creation.", "Original Value", keyPathTestEntity.value());

            keyPathTestEntity.refreshObject();

            assertEquals("Reverted name", "Updated Name", keyPathTestEntity.name());
            assertEquals("Failed to update value from database.", "Updated Value", keyPathTestEntity.value());

            assertEquals("Same eo, diferent ec, picked up name change", "Original Name", keyPathTestEntityInSameOSC.name());
            assertEquals("Same eo, diferent ec, failed to update value from database.", "Updated Value", keyPathTestEntityInSameOSC.value());
        }
        finally
        {
            editingContextInSameOSC.unlock();
        }
    }



    /**
     * Set's this object's values back to the state last fetched.
     */
    public void testRevertToSaved()
    {
        editingContextInSameOSC.lock();
        try
        {
            keyPathTestEntity.setName("Updated Name");
            assertEquals("Did not update name", "Updated Name", keyPathTestEntity.name());
            assertEquals("Value changed since creation.", "Original Value", keyPathTestEntity.value());

            keyPathTestEntity.revertToSaved();

            assertEquals("Did not revert name", "Original Name", keyPathTestEntity.name());
            assertEquals("Updated value from database.", "Original Value", keyPathTestEntity.value());
            assertEquals("Same eo, diferent ec, Overwrote name", "Original Name", keyPathTestEntityInSameOSC.name());
            assertEquals("Updated value from database.", "Original Value", keyPathTestEntityInSameOSC.value());

            keyPathTestEntityInSameOSC.refreshObject();
            keyPathTestEntity.revertToSaved();

            assertEquals("Did not revert name", "Original Name", keyPathTestEntity.name());
            assertEquals("Failed to update value from database.", "Updated Value", keyPathTestEntity.value());
            assertEquals("Same eo, diferent ec, Overwrote name", "Original Name", keyPathTestEntityInSameOSC.name());
            assertEquals("Same eo, diferent ec, Failed to update value from database.", "Updated Value", keyPathTestEntityInSameOSC.value());
        }
        finally
        {
            editingContextInSameOSC.unlock();
        }
    }


    public void testCacheClearing()
    {
        // Test basic functionality
        testEntity.setCachedValue(TEST_VALUE);
        assertEquals(TEST_VALUE, testEntity.cachedValue());
        testEntity.clearCachedValues();
        assertEquals(null, testEntity.cachedValue());

        // Test reverting an unsaved object
        assertTrue(testEntity.globalID().isTemporary());
        testEntity.setName(TEST_VALUE);
        testEntity.setCachedValue(TEST_VALUE);
        editingContext().revert();
        assertEquals(null, testEntity.cachedValue());

        try
        {
            // Test that saving does not clear cache
            testEntity = new TestEntity();
            editingContext().insertObject(testEntity);
            testEntity.setName("testEntity");
            testEntity.setCachedValue(TEST_VALUE);
            editingContext().saveChanges();
            assertEquals(TEST_VALUE, testEntity.cachedValue());

            // Test reverting a saved object
            testEntity.setName(TEST_VALUE);
            testEntity.setCachedValue(TEST_VALUE);
            editingContext().revert();
            assertEquals(null, testEntity.cachedValue());

            // Test saving object in another EC in same EOF stack
            TestEntity eo = (TestEntity) EOUtilities.localInstanceOfObject(editingContextInSameOSC, testEntity);
            eo.setName(TEST_VALUE);
            testEntity.setCachedValue(TEST_VALUE);
            editingContextInSameOSC.saveChanges();
            assertEquals(null, testEntity.cachedValue());

            // Test saving object in another EC in a different EOF stack
            // NOTE this test shows that the cached valued is NOT cleared in this case
            eo = (TestEntity) EOUtilities.localInstanceOfObject(editingContextInOtherOSC, testEntity);
            eo.setName("X");
            testEntity.setCachedValue(TEST_VALUE);
            editingContextInOtherOSC.saveChanges();
            assertEquals(TEST_VALUE, testEntity.cachedValue());

            // Test refreshObject when there is a change in the DB (from test above)
            testEntity.setCachedValue(TEST_VALUE);
            testEntity.refreshObject();
            assertEquals(null, testEntity.cachedValue());

            // Test refreshObject when there is no local change and no change in the DB
            // Note that this test shows that the cached value is NOT cleared as there have been no external changes
            testEntity.setCachedValue(TEST_VALUE);
            testEntity.refreshObject();
            assertEquals(TEST_VALUE, testEntity.cachedValue());

            // Test refreshObject when there is a local change, but no change in the DB
            // Note that this test shows that the cached value is NOT cleared as there have been no external changes
            testEntity.setName("foo");
            testEntity.setCachedValue(TEST_VALUE);
            testEntity.refreshObject();
            assertEquals(TEST_VALUE, testEntity.cachedValue());

            // Test invalidating a saved object
            testEntity.setName(TEST_VALUE);
            testEntity.setCachedValue(TEST_VALUE);
            editingContext().invalidateObjectsWithGlobalIDs(new NSArray(testEntity.globalID()));
            assertEquals(null, testEntity.cachedValue());

            // Test revertToSaved()
            testEntity.setCachedValue(TEST_VALUE);
            testEntity.revertToSaved();
            assertEquals(null, testEntity.cachedValue());
        }
        finally
        {
            testEntity.refreshObject();
            editingContext().deleteObject(testEntity);
            editingContext().saveChanges();
        }
    }



    /**
     * Delete inserted objects
     */
    public void tearDown() throws java.lang.Exception
    {
        //NSNotificationCenter.defaultCenter().removeOmniscientObserver(this);
        ///EOFDebug.logSQL(false);
        editingContextInOtherOSC.lock();
        try
        {
            editingContextInOtherOSC.deleteObject(keyPathTestEntityInOtherOSC);
            editingContextInOtherOSC.saveChanges();
        }
        finally
        {
            editingContextInOtherOSC.unlock();
            editingContextInOtherOSC.dispose();
        }

        editingContextInSameOSC.dispose();
        otherOSC.unlock();

        super.tearDown();
    }


}
