package net.global_village.eofextensions.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;


/**
 * Test the EOAdaptorChannelAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class EOModelGroupAdditionsTest extends GVCJUnitEOTestCase
{


    /*
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
     public EOModelGroupAdditionsTest(String name)
     {
         super(name);
     }



    /**
     * Test connectModelsNamed
     *
     * This test is for a bug where the subEntities attribute of an Entity would report subclasses more than once after connectModelsNamed was called.  This resulted in multiple fetches against the same table and duplicate results being returned.  This bug was fixed by adding a call to loadAllModelObjects() after each model was connected.
     *** Obsolete.  Code unfixable.
    public void testConnectModelsNamedDoesNotDuplicateSubEntities()
    {
        System.out.println("Before subEntities are "+EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase").entityNamed("AttributeValidationTestEntity").subEntities().count());

        EOModelGroupAdditions.connectModelsNamed(new NSArray(new String[] {"GVCTestEOModelBase", "GVCTestEOModelSubClass", "TestModel3"}),
                                                 "TestModel1",
                                                 EOModelGroup.defaultGroup());
        //System.out.println("After subEntities model is "+EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase"));
        //System.out.println("After subEntities entity is "+EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase").entityNamed("AttributeValidationTestEntity"));
        //System.out.println("After subEntities are "+EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase").entityNamed("AttributeValidationTestEntity").subEntities());
        System.out.println("After subEntities count is "+EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase").entityNamed("AttributeValidationTestEntity").subEntities().count());
        System.out.println("asserting");
        assertTrue(EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase").entityNamed("AttributeValidationTestEntity").subEntities().count() == 1);
        System.out.println("asserted");
    }
     */



    /**
     * Test connectModelsNamed
     */
    public void testConnectModelsNamed1()
    {
        NSDictionary testModel1ConnectionInfo = EOModelGroup.defaultGroup().modelNamed("TestModel1").connectionDictionary();
        String testModel1AdaptorName = EOModelGroup.defaultGroup().modelNamed("TestModel1").adaptorName();

        EOModelGroupAdditions.connectModelsNamed(new NSArray(new String[] {"GVCTestEOModelBase", "GVCTestEOModelSubClass", "TestModel3"}), "TestModel1", EOModelGroup.defaultGroup());

        // All changed models should have the same connection dictionary
        assertEquals("connectionInfo test1", EOModelGroup.defaultGroup().modelNamed("TestModel1").connectionDictionary(), testModel1ConnectionInfo);
        assertEquals("connectionInfo test2", EOModelGroup.defaultGroup().modelNamed("TestModel3").connectionDictionary(), testModel1ConnectionInfo);
        assertTrue("connectionInfo test3", ( ! EOModelGroup.defaultGroup().modelNamed("TestModel2").connectionDictionary().equals(testModel1ConnectionInfo)));

        // All changed models should have the same adaptor name
        assertEquals("adaptorName test1", EOModelGroup.defaultGroup().modelNamed("TestModel1").adaptorName(), testModel1AdaptorName);
        assertEquals("adaptorName test2", EOModelGroup.defaultGroup().modelNamed("TestModel3").adaptorName(), testModel1AdaptorName);
        assertTrue("adaptorName test3",  ! (EOModelGroup.defaultGroup().modelNamed("TestModel2").adaptorName().equals(testModel1AdaptorName)));

        // All changed models assert have the same database context.
        EODatabaseContext testModel1DC = EODatabaseContext.registeredDatabaseContextForModel(EOModelGroup.defaultGroup().modelNamed("TestModel1"), editingContext());
        EODatabaseContext testModel3DC = EODatabaseContext.registeredDatabaseContextForModel(EOModelGroup.defaultGroup().modelNamed("TestModel3"), editingContext());

        assertEquals("equals test",testModel1DC, testModel3DC);
    }



    /**
     * Test connectModelsNamed
     */
    public void testConnectModelsNamed2()
    {
        // Test second version of method
        NSDictionary testModel1ConnectionInfo = EOModelGroup.defaultGroup().modelNamed("TestModel1").connectionDictionary();
        String testModel1AdaptorName = EOModelGroup.defaultGroup().modelNamed("TestModel1").adaptorName();

        // Reset model from previous test
        EOModelGroup.defaultGroup().modelNamed("TestModel3").setAdaptorName("none");
        EOModelGroup.defaultGroup().modelNamed("TestModel3").setConnectionDictionary(new NSDictionary());

        EOModelGroupAdditions.connectModelsNamed(new NSArray(new String[] {"TestModel2", "TestModel1"}),
                                                 testModel1AdaptorName,
                                                 testModel1ConnectionInfo,
                                                 null);

        // All changed models should have the same connection dictionary
        assertEquals(EOModelGroup.defaultGroup().modelNamed("TestModel1").connectionDictionary(), testModel1ConnectionInfo);
        assertEquals(EOModelGroup.defaultGroup().modelNamed("TestModel2").connectionDictionary(), testModel1ConnectionInfo);
        assertTrue( ! EOModelGroup.defaultGroup().modelNamed("TestModel3").connectionDictionary().equals(testModel1ConnectionInfo));

        // All changed models should have the same adaptor name
        assertEquals(EOModelGroup.defaultGroup().modelNamed("TestModel1").adaptorName(), testModel1AdaptorName);
        assertEquals(EOModelGroup.defaultGroup().modelNamed("TestModel2").adaptorName(), testModel1AdaptorName);
        assertTrue( ! (EOModelGroup.defaultGroup().modelNamed("TestModel3").adaptorName().equals(testModel1AdaptorName)));

        // All changed models assert have the same database context.
        EODatabaseContext testModel1DC = EODatabaseContext.registeredDatabaseContextForModel(EOModelGroup.defaultGroup().modelNamed("TestModel1"), editingContext());
        EODatabaseContext testModel2DC = EODatabaseContext.registeredDatabaseContextForModel(EOModelGroup.defaultGroup().modelNamed("TestModel2"), editingContext());

        assertEquals(testModel1DC, testModel2DC);
    }



    /**
     * Test connectModelsNamed
     * There was a problem with the original method where it would fail if there had been any database activity.  This test verifies that this has been fixed.
     *** It turns out this was not possible to fix, so this test commented out.
    public void testConnectModelsNamedWithLiveConnections()
    {
        // TestModel1 should be setup to connect to the FrontBase test database.
        NSDictionary testModel1ConnectionInfo = EOModelGroup.defaultGroup().modelNamed("TestModel1").connectionDictionary();
        String testModel1AdaptorName = EOModelGroup.defaultGroup().modelNamed("TestModel1").adaptorName();

        EOModelGroupAdditions.connectModelsNamed(new NSArray(new String[] {"TestModel2", "TestModel1"}),
                                                 testModel1AdaptorName,
                                                 testModel1ConnectionInfo,
                                                 null);

        try
        {
            TestModel1Entity testObject = new TestModel1Entity(null, null, null);
            editingContext().insertObject(testObject);
            testObject.setName("TestObject for FrontBase");
            editingContext().saveChanges();

            // Remove test data
            editingContext().deleteObject(testObject);
            editingContext().saveChanges();
        }
        catch (Throwable t)
        {
            fail("Could not saveChanges to FrontBase due to:" + t);
        }

        // Now connect to Oracle and do it again.
        NSMutableDictionary oracleTestDB = new NSMutableDictionary();
        oracleTestDB.setObjectForKey("GVC", "serverId");
        oracleTestDB.setObjectForKey("test", "userName");
        oracleTestDB.setObjectForKey("test", "password");
        System.out.println("Connecting to Oracle");
        EOModelGroupAdditions.connectModelsNamed(new NSArray(new String[] {"TestModel2", "TestModel1"}),
                                                 "Oracle",
                                                 oracleTestDB,
                                                 null);
        try
        {
            TestModel1Entity testObject = new TestModel1Entity(null, null, null);
            editingContext().insertObject(testObject);
            testObject.setName("TestObject for Oracle");
            editingContext().saveChanges();

            // Remove test data
            editingContext().deleteObject(testObject);
            editingContext().saveChanges();
        }
        catch (Throwable t)
        {
            fail("Could not saveChanges to Oracle due to:" + t);
        }
    }
    */


    /**
     * Test fetchAllCachedEntities
     */
    public void testFetchAllCachedEntities()
    {
        // Don't know how to test that no further fetches result other than by watching debug window with -EOAdaptorDebugEnabled YES.
        assertTrue(true);
    }


    public void testEntities()
    {
        EOModelGroup defaultGroup = EOModelGroup.defaultGroup();
        NSArray entities = EOModelGroupAdditions.entities(defaultGroup);

        // Verify that all returned entities are in the group
        for (int i = 0; i < entities.count(); i++) {
			EOEntity entity = (EOEntity) entities.objectAtIndex(i);
			assertTrue("Returned entity not found in model group", defaultGroup.entityNamed(entity.name()) != null);
		}

        //Verify that the count of entities in the group match the returned result
        int entityCount = 0;
        for (int i = 0; i < defaultGroup.models().count(); i++) {
            entityCount += (defaultGroup.models().objectAtIndex(i)).entities().count();
        }
        assertEquals("Returned entity not found in model group", entityCount, entities.count());
    }

}
