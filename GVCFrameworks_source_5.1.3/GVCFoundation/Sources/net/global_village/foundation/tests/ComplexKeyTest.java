package net.global_village.foundation.tests;

import junit.framework.TestCase;
import net.global_village.foundation.ComplexKey;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;


/**
 * Test the ComplexKey functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ComplexKeyTest extends TestCase
{
    ComplexKey key1;
    ComplexKey key2;
    ComplexKey key3;


    public ComplexKeyTest(String name)
    {
        super(name);
    }



    /**
     * Sets up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        Object[] keys1and2 = {this, "TestString"};
        Object[] keys3 = {"TestString", this};
        key1 = new ComplexKey(new NSArray(keys1and2));
        key2 = new ComplexKey(new NSArray(keys1and2));
        key3 = new ComplexKey(new NSArray(keys3));
    }



    /**
     * Test equals().
     */
    public void testKeyEquality()
    {
        assertTrue(key1.equals(key2));
        assertTrue(key2.equals(key1));
        assertTrue( ! key1.equals(key3));
        assertTrue( ! key2.equals(key3));
    }



    /**
     * Test clone().
     */
    public void testKeyCopying()
    {
        ComplexKey key1Copy = (ComplexKey)key1.clone();

        assertTrue(key1Copy.equals(key1));
        assertTrue(key1Copy.equals(key2));
        assertTrue( ! key1Copy.equals(key3));
    }



    /**
     * Test hashCode().  Two hashs should be equal if equals() returns true.
     */
    public void testHash()
    {
        assertTrue(key1.hashCode() == key2.hashCode());
        assertTrue(key2.hashCode() == key1.hashCode());
    }



    /**
     * Test that the complex key works properly in a dictionary.
     */
    public void testDictionary()
    {
        NSMutableDictionary dict = new NSMutableDictionary();

        dict.setObjectForKey("Test1", key1);
        dict.setObjectForKey("Test3", key3);
        assertTrue(dict.objectForKey(key1).equals("Test1"));
        assertTrue(dict.objectForKey(key3).equals("Test3"));
        assertTrue(dict.count() == 2);

        dict.setObjectForKey("Test Repeat", key1);
        assertTrue(dict.objectForKey(key1).equals("Test Repeat"));
        assertTrue(dict.objectForKey(key3).equals("Test3"));
        assertTrue(dict.count() == 2);

        dict.setObjectForKey("Test2", key2);
        assertTrue(dict.objectForKey(key1).equals("Test2"));
        assertTrue(dict.objectForKey(key3).equals("Test3"));
        assertTrue(dict.count() == 2);
    }



}
