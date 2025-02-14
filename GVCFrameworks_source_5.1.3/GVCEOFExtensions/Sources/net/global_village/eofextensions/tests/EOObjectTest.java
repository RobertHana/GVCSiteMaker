package net.global_village.eofextensions.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test the EOObjectTest functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class EOObjectTest extends GVCJUnitEOTestCase
{
    protected SimpleTestEntity testEntity;
    protected SimpleRelationshipTestEntity relatedTestEntity;
    protected TestClassWithEOs testClassWithEOs;

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
    public EOObjectTest(String name)
     {
         super(name);
     }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        testEntity = new SimpleTestEntity();
        editingContext().insertObject(testEntity);
        testEntity.setName("testEntity");

        relatedTestEntity = new SimpleRelationshipTestEntity(); 
        editingContext().insertObject(relatedTestEntity);

        testClassWithEOs = new TestClassWithEOs();
        
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
        SimpleTestEntity testEntityNotInEC = new SimpleTestEntity();

        assertEquals(EOObject.entityForSelf(testEntity), EOModelGroup.defaultGroup().entityForObject(testEntity));
        assertEquals(EOObject.entityForSelf(testEntity), EOModelGroup.defaultGroup().entityForObject(testEntityNotInEC));
        assertEquals(EOObject.entityForSelf(testEntity), EOUtilities.entityForObject(editingContext(), testEntity));
    }


    /**
     * Test globalID
     */
    public void testGlobalID()
    {
        assertTrue(EOObject.globalID(testEntity) != null);	
        assertTrue(editingContext().globalIDForObject(testEntity).equals(EOObject.globalID(testEntity)));
    }




    /**
     * Test attributeWithName
     */
    public void testAttributeWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleTestEntity");
        assertEquals(EOObject.attributeWithName(testEntity, "name"), theEntity.attributeNamed("name"));
    }



    /**
     * Test relationshipWithName
     */
    public void testRelationshipWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleRelationshipTestEntity");       
        assertEquals(EOObject.relationshipWithName(relatedTestEntity, "mandatoryToOne"),
                     theEntity.relationshipNamed("mandatoryToOne"));
    }



    /**
     * Test propertyWithName
     */
    public void testPropertyWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleRelationshipTestEntity");
        assertEquals(EOObject.propertyWithName(relatedTestEntity, "name"), theEntity.attributeNamed("name"));
        assertEquals(EOObject.propertyWithName(relatedTestEntity, "mandatoryToOne"), theEntity.relationshipNamed("mandatoryToOne"));
    }


    
    /**
     * Test keyPathHasEntity
     */
    public void testKeyPathHasEntity()
    {
        assertTrue(EOObject.keyPathHasEntity(testClassWithEOs, "simpleTestEntity.name"));
        assertTrue(EOObject.keyPathHasEntity(testClassWithEOs, "simpleRelationshipTestEntity.name"));
        assertTrue( ! (EOObject.keyPathHasEntity(testClassWithEOs, "ficticious.key.path")));
    }



    /**
     * Test entityFromKeyPath
     */
    public void testEntityFromKeyPath()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleTestEntity");
        EOEntity theRelationshipEntity = EOModelGroup.defaultGroup().entityNamed("SimpleRelationshipTestEntity");

        assertEquals(EOObject.entityFromKeyPath(testClassWithEOs, "simpleTestEntity.name"), theEntity);
        assertEquals(EOObject.entityFromKeyPath(testClassWithEOs, "simpleRelationshipTestEntity.name"), theRelationshipEntity);
        try
        {
            EOObject.entityFromKeyPath(testClassWithEOs, "ficticious.key.path");
            fail("Expected exception in testEntityFromKeyPath did not occur");
        }
        catch (RuntimeException e) {}
    }



    /**
     * Test isKeyPathToAttribute
     */
    public void testIsKeyPathToAttribute()
    {
        assertTrue(EOObject.isKeyPathToAttribute(testClassWithEOs, "simpleTestEntity.name"));
        assertTrue( ! (EOObject.isKeyPathToAttribute(testClassWithEOs, "simpleTestEntity.ficticious")));
    }



    /**
     * Test attributeFromKeyPath
     */
    public void testAttributeFromKeyPath()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleTestEntity");
        assertEquals(EOObject.attributeFromKeyPath(testClassWithEOs, "simpleTestEntity.name"), theEntity.attributeNamed("name"));
        try
        {
            EOObject.attributeFromKeyPath(testClassWithEOs, "ficticious.ficticious");
            fail("Expected exception in testEntityFromKeyPath did not occur");
        }
        catch (RuntimeException e) {}
    }



    /**
     * Test isKeyPathToRelationship
     */
    public void testIsKeyPathToRelationship()
    {
        assertTrue(EOObject.isKeyPathToRelationship(testClassWithEOs, "simpleRelationshipTestEntity.mandatoryToOne"));
        assertTrue( ! (EOObject.isKeyPathToRelationship(testClassWithEOs, "simpleRelationshipTestEntity.ficticious")));
    }



    /**
     * Test relationshipFromKeyPath
     */
    public void testRelationshipFromKeyPath()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleRelationshipTestEntity");
        assertEquals(EOObject.relationshipFromKeyPath(testClassWithEOs, "simpleRelationshipTestEntity.mandatoryToOne"), theEntity.relationshipNamed("mandatoryToOne"));

        try
        {
            EOObject.relationshipFromKeyPath(testClassWithEOs, "ficticious.ficticious");
            fail("Expected exception in testRelationshipFromKeyPath did not occur");
        }
        catch (RuntimeException e) {}
    }



    /**
     * Test relationshipFromKeyPath
     */
    public void testIsDummyFaultEO()
    {
        assertTrue("temporary object was detected as dummy", ! EOObject.isDummyFaultEO(relatedTestEntity));

        EOEditingContext localEC = new EOEditingContext();

        SimpleTestEntity dummyObject = new SimpleTestEntity();
        localEC.insertObject(dummyObject);
        dummyObject.setName("permObjTest");
        localEC.saveChanges();
        try
        {
            assertTrue("saved object was detected as dummy", ! EOObject.isDummyFaultEO(dummyObject));
        }
        finally
        {
            localEC.deleteObject(dummyObject);
            localEC.saveChanges();
        }

        dummyObject = (SimpleTestEntity)EOUtilities.faultWithPrimaryKeyValue(localEC, "SimpleTestEntity", new Integer(-1));
        assertTrue("dummy object not detected", EOObject.isDummyFaultEO(dummyObject));
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
