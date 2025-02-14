package net.global_village.genericobjects.tests;

import com.webobjects.foundation.NSArray;

import net.global_village.foundation.DefaultValueRetrieval;
import net.global_village.genericobjects.*;
import net.global_village.gvcjunit.GVCJUnitEOTestCase;

/**
 * Tests TestKeyedLookup - which is an example subclass of KeyedLookup
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class TestKeyedLookupTest extends GVCJUnitEOTestCase
{
    protected KeyedLookup instanceOne; 
    protected KeyedLookup instanceTwo;
    protected KeyedLookup instanceThree;

    protected Lookup defaultTestObject;
    protected String defaultTestObjectName;

    
    /**
     * Designated constructor.
     *
     * @param name The name of the test to be initialized
     *
     */
    public TestKeyedLookupTest (String name)
    {
        super(name);
    }



    /**
     * Array of Ordered objects for the Entity being tested  
     */
    protected NSArray orderedObjects()
    {
        return TestKeyedLookup.orderedObjects(editingContext());
        /** ensure [valid_result] Result != null;  **/
    }



    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        instanceOne   = new TestKeyedLookup();
        instanceTwo   = new TestKeyedLookup();
        instanceThree = new TestKeyedLookup();

        instanceOne.setKey("One");
   	    instanceOne.setName(globallyUniqueString());

        instanceTwo.setKey("Two");
        instanceTwo.setName(globallyUniqueString());

        instanceThree.setKey("Three");
        instanceThree.setName(globallyUniqueString());

        editingContext.insertObject(instanceOne);
        editingContext.insertObject(instanceTwo);
        editingContext.insertObject(instanceThree);

        defaultTestObjectName = DefaultValueRetrieval.defaultString(instanceOne.getClass(), "TestKeyedLookup.defaultName");
        defaultTestObject = new TestKeyedLookup();
        ((TestKeyedLookup)defaultTestObject).setKey("Four"); 
        defaultTestObject.setName(defaultTestObjectName);
        editingContext.insertObject(defaultTestObject);

        saveChangesShouldntThrow("after creating Objects");
    }

    

    public void tearDown() throws java.lang.Exception
    {
        editingContext.deleteObject( instanceOne );
        editingContext.deleteObject( instanceTwo );
        editingContext.deleteObject( instanceThree );

        editingContext.deleteObject( defaultTestObject);

        saveChangesShouldntThrow("destroying Objects");

        super.tearDown();
    }


    
    public void testFetchCount()
    {
        NSArray resultArray = orderedObjects();
        assertTrue("results should have three or more object objects",
                resultArray.count() >= 3);
    }



    public void testObjectOrder()
    {
        NSArray resultArray = orderedObjects();

        for (int i = resultArray.count() - 1; i > 0; i--)
        {
            Lookup firstObject = (Lookup) resultArray.objectAtIndex(i - 1);
            Lookup secondObject = (Lookup) resultArray.objectAtIndex(i);
            String firstName = firstObject.name();
            String secondName = secondObject.name();
            assertTrue("firstName is equal to or less then secondName", firstName.compareToIgnoreCase(secondName) <= 0);
        }
    }



    public void testKeyedRetrieval()
    {      
        assertTrue("found instanceOne by key", TestKeyedLookup.objectForKey( instanceOne.key(), editingContext() ) == instanceOne );
        assertTrue("found instanceTwo by key", TestKeyedLookup.objectForKey( instanceTwo.key(), editingContext() ) == instanceTwo );
        assertTrue("found instanceThree by key", TestKeyedLookup.objectForKey( instanceThree.key(), editingContext() ) == instanceThree );
        assertTrue("found defaultTestObject by key", TestKeyedLookup.objectForKey(((TestKeyedLookup)defaultTestObject).key(), editingContext()) == defaultTestObject);
    }
    

    /**
     * tests defaultObject with an entity name
     */
    public void testDefaultObjectWithEntityName()
    {
        Lookup result = (Lookup)Lookup.defaultObject(editingContext(), "TestKeyedLookup");
        assertEquals("result equals defaultTestObject", result, defaultTestObject);
    }


}
