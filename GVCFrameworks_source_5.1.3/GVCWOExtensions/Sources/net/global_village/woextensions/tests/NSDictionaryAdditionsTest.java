package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.woextensions.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class NSDictionaryAdditionsTest extends TestCase
{


    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public NSDictionaryAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
    }



    /**
     * Test urlEncodedDictionary()
     */
    public void testUrlEncodedDictionary()
    {
        NSDictionary dict = new NSDictionary("value", "key");
        assertEquals("key=value", NSDictionaryAdditions.urlEncodedDictionary(dict));

        dict = new NSDictionary(new String[] {"value1", "value2"}, new String[] {"key1", "key2"});
        assertTrue(NSDictionaryAdditions.urlEncodedDictionary(dict).equals("key1=value1&key2=value2") || NSDictionaryAdditions.urlEncodedDictionary(dict).equals("key2=value2&key1=value1"));

        // Test URL encoding
        dict = new NSDictionary("value!with.strange(chars)", "key with spaces");
        assertEquals("key+with+spaces=value%21with.strange%28chars%29", NSDictionaryAdditions.urlEncodedDictionary(dict));

        // Test dictionary handling
        dict = new NSDictionary(new NSArray("value"), "key");
        assertEquals("key=value", NSDictionaryAdditions.urlEncodedDictionary(dict));

        dict = new NSDictionary(new NSArray(new String[] { "value", "other value"}), "key");
        assertEquals("key=value", NSDictionaryAdditions.urlEncodedDictionary(dict));

    }



}
