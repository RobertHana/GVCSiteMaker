package net.global_village.genericobjects.tests;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.foundation.NSArray;

import net.global_village.foundation.DefaultValueRetrieval;
import net.global_village.genericobjects.*;
import net.global_village.genericobjects.OrderedLookup;
import net.global_village.gvcjunit.GVCJUnitEOTestCase;


/**
 * Test GVCOrderedLookup
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class TestOrderedLookupTest extends GVCJUnitEOTestCase
{
    protected TestOrderedLookup instanceOne;
    protected TestOrderedLookup instanceTwo;
    protected TestOrderedLookup instanceThree;

    protected Lookup defaultTestObject;
    protected String defaultTestObjectName;


    /**
     * Designated constructor.
     *
     * @param name The name of the test to be initialized
     */
    public TestOrderedLookupTest(String name)
    {
        super(name);
    }
    


    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        instanceOne   = new TestOrderedLookup();
        instanceTwo   = new TestOrderedLookup();
        instanceThree = new TestOrderedLookup();

        instanceOne.setName("One");
        instanceOne.setOrdering(new Integer(1));
        instanceTwo.setName("Two");
        instanceTwo.setOrdering(new Integer(2));
        instanceThree.setName("Three");
        instanceThree.setOrdering(new Integer(3));

        editingContext.insertObject( instanceOne );
        editingContext.insertObject( instanceTwo );
        editingContext.insertObject( instanceThree );

        defaultTestObjectName = DefaultValueRetrieval.defaultString(instanceOne.getClass(), "TestOrderedLookup.defaultName");
        defaultTestObject = new TestOrderedLookup();
        ((TestOrderedLookup) defaultTestObject).setOrdering(new Integer(4));
        defaultTestObject.setName(defaultTestObjectName);
        editingContext.insertObject(defaultTestObject);

        saveChangesShouldntThrow("after creating Objects");
    }



    public void tearDown() throws java.lang.Exception
    {
        editingContext.deleteObject(instanceOne);
        editingContext.deleteObject(instanceTwo);
        editingContext.deleteObject(instanceThree);

        editingContext.deleteObject(defaultTestObject);

        saveChangesShouldntThrow("destroying Objects");

        super.tearDown();
    }




    /**
     * Array of Ordered objects for the Entity being tested
     */
    protected NSArray orderedObjects()
    {
        return TestOrderedLookup.orderedObjects(editingContext());
    }



    public void testObjectOrder()
    {
        NSArray resultArray = orderedObjects();

        for (int i = resultArray.count() - 1; i > 0; i-- )
        {
            OrderedLookup firstObject = (OrderedLookup) resultArray.objectAtIndex( i - 1 );
            OrderedLookup secondObject = (OrderedLookup) resultArray.objectAtIndex( i );
            Number firstOrdering =  firstObject.ordering();
            Number secondOrdering =  secondObject.ordering();

            assertTrue("firstObject compared to secondObject is before", firstOrdering.intValue() < secondOrdering.intValue());
        }
    }

    

    /**
     * Test OrderingComparator
     */
    public void testOrderingComparator()
    {
        NSArray orderedTestObjects = OrderedLookup.orderedObjects(editingContext, "TestOrderedLookup", OrderedLookup.OrderingComparator);

        TestOrderedLookup firstObject = (TestOrderedLookup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                        "TestOrderedLookup",
                                                                                        "name",
                                                                                        "One");
        TestOrderedLookup middleObject = (TestOrderedLookup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                         "TestOrderedLookup",
                                                                                         "name",
                                                                                         "Two");
        TestOrderedLookup lastObject = (TestOrderedLookup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                       "TestOrderedLookup",
                                                                                       "name",
                                                                                       "Three");

        assertTrue(orderedTestObjects.indexOfObject(firstObject) < orderedTestObjects.indexOfObject(middleObject));
        assertTrue(orderedTestObjects.indexOfObject(middleObject) < orderedTestObjects.indexOfObject(lastObject));
        assertTrue(orderedTestObjects.indexOfObject(lastObject) < orderedTestObjects.indexOfObject(defaultTestObject));
    }



}
