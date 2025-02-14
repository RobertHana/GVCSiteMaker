package net.global_village.foundation.tests;

import java.util.TimeZone;

import junit.framework.TestCase;

import com.webobjects.foundation.*;

import net.global_village.foundation.KeyValueComparator;

/**
 * Tests for KeyValueComparator.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class KeyValueComparatorTest extends TestCase
{
    protected NSMutableArray testObjects;
    protected NSMutableDictionary object1;
    protected NSMutableDictionary object2;
    protected NSMutableDictionary object3;

    /**
     * Default constructor
     */
    public KeyValueComparatorTest()
    {
        super();
    }


    /**
     * Default constructor
     *
     * @param name name of test
     */
    public KeyValueComparatorTest(String name)
    {
        super(name);
    }


    public void setUp() throws Exception
    {
        super.setUp();
        
        object1 =  new NSMutableDictionary();
        object1.setObjectForKey("Apple", "name");
        object1.setObjectForKey(new Integer(30), "age");
        object1.setObjectForKey(new NSTimestamp(1964, 5, 5, 7, 55, 1, TimeZone.getDefault()), "date"); 
        
        object2 =  new NSMutableDictionary();
        object2.setObjectForKey("Goat", "name");
        object2.setObjectForKey(new Integer(10), "age");
        object2.setObjectForKey(new NSTimestamp(1954, 5, 5, 7, 55, 1, TimeZone.getDefault()), "date"); 

        object3 =  new NSMutableDictionary();
        object3.setObjectForKey("Zebra", "name");
        object3.setObjectForKey(new Integer(40), "age");
        object3.setObjectForKey(new NSTimestamp(1924, 5, 5, 7, 55, 1, TimeZone.getDefault()), "date"); 
        
        testObjects = new NSMutableArray();
        testObjects.addObject(object1);
        testObjects.addObject(object2);
        testObjects.addObject(object3);
    }



    public void testStringSort()
    {
        try
        {
            NSArray result = testObjects.sortedArrayUsingComparator(new KeyValueComparator("name", NSComparator.OrderedAscending));
            assertTrue(result.objectAtIndex(0) == object1);
            assertTrue(result.objectAtIndex(1) == object2);
            assertTrue(result.objectAtIndex(2) == object3);
            
            result = testObjects.sortedArrayUsingComparator(new KeyValueComparator("name", NSComparator.OrderedDescending));
            assertTrue(result.objectAtIndex(0) == object3);
            assertTrue(result.objectAtIndex(1) == object2);
            assertTrue(result.objectAtIndex(2) == object1);
        }
        catch (NSComparator.ComparisonException e)
        {
            fail(e.getMessage());
        }
    }




    public void testNumberSort()
    {
        try
        {
            NSArray result = testObjects.sortedArrayUsingComparator(new KeyValueComparator("age", NSComparator.OrderedAscending));
            assertTrue(result.objectAtIndex(0) == object2);
            assertTrue(result.objectAtIndex(1) == object1);
            assertTrue(result.objectAtIndex(2) == object3);
            
            result = testObjects.sortedArrayUsingComparator(new KeyValueComparator("age", NSComparator.OrderedDescending));
            assertTrue(result.objectAtIndex(0) == object3);
            assertTrue(result.objectAtIndex(1) == object1);
            assertTrue(result.objectAtIndex(2) == object2);
        }
        catch (NSComparator.ComparisonException e)
        {
            fail(e.getMessage());
        }
    }



    public void testDateSort()
    {
        try
        {
            NSArray result = testObjects.sortedArrayUsingComparator(new KeyValueComparator("date", NSComparator.OrderedAscending));
            assertTrue(result.objectAtIndex(0) == object3);
            assertTrue(result.objectAtIndex(1) == object2);
            assertTrue(result.objectAtIndex(2) == object1);
            
            result = testObjects.sortedArrayUsingComparator(new KeyValueComparator("date", NSComparator.OrderedDescending));
            assertTrue(result.objectAtIndex(0) == object1);
            assertTrue(result.objectAtIndex(1) == object2);
            assertTrue(result.objectAtIndex(2) == object3);
        }
        catch (NSComparator.ComparisonException e)
        {
            fail(e.getMessage());
        }
    }

}

