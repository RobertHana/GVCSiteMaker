package net.global_village.eofextensions.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;


/**
 * Tests for KeyValueCoding. 
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class KeyValueCodingTests extends TestCase
{

    
    public void testCopyMappedValuesWithNSKeyValueCoding()
    {
        NSDictionary source = new NSDictionary(new String[] {"value 1", "value 2", "value 3", "value 4", "value 5"}, 
                                               new String[] {"key 1"  , "key 2"  , "key 3"  , "key 4", "extra value"});
        NSDictionary mapping = new NSDictionary(new String[] {"dest 1", "dest 2",  "dest 3",  "dest 4", "null value"}, 
                                               new String[] {"key 1"  , "key 2"  , "key 3"  , "key 4", "missing value"});
        NSMutableDictionary destination = new NSMutableDictionary();
        
        KeyValueCoding.copyMappedValues(source, mapping, destination);
        assertEquals(5, destination.count());
        assertEquals("value 1", destination.valueForKey("dest 1"));
        assertEquals("value 2", destination.valueForKey("dest 2"));
        assertEquals("value 3", destination.valueForKey("dest 3"));
        assertEquals("value 4", destination.valueForKey("dest 4"));
        assertTrue(destination.allKeys().containsObject("null value"));
        assertFalse(destination.allKeys().containsObject("extra value"));
        assertEquals(NSKeyValueCoding.NullValue, destination.valueForKey("null value"));
    }
    
    
    
    public void testCopyMappedValuesWithEOKeyValueCodingAndNull()
    {
        NSDictionary source = new NSDictionary(new Object[] {"the name", "the value"}, 
                                               new String[] {"key 1"  , "key 2"});
        NSDictionary mapping = new NSDictionary(new String[] {"name", "value",  "relationship"}, 
                                               new String[] {"key 1"  , "key 2"  , "key 3"});
        KeyPathTestEntity destination = new KeyPathTestEntity();
        destination.setRelationship(new TestEntityWithRelationship());
        assertNotNull(destination.relationship());
        
        KeyValueCoding.copyMappedValues(source, mapping, destination);

        assertEquals("the name", destination.name());
        assertEquals("the value", destination.value());
        assertNull(destination.relationship());
    }
    
    
    
    
    public void testCopyMappedValuesWithEOKeyValueCodingAndNullValue()
    {
        NSDictionary source = new NSDictionary(new Object[] {"the name", "the value", NSKeyValueCoding.NullValue}, 
                                               new String[] {"key 1"  , "key 2", "key 3"});
        NSDictionary mapping = new NSDictionary(new String[] {"name", "value",  "relationship"}, 
                                               new String[] {"key 1"  , "key 2"  , "key 3"});
        KeyPathTestEntity destination = new KeyPathTestEntity();
        destination.setRelationship(new TestEntityWithRelationship());
        assertNotNull(destination.relationship());
        
        KeyValueCoding.copyMappedValues(source, mapping, destination);

        assertEquals("the name", destination.name());
        assertEquals("the value", destination.value());
        assertNull(destination.relationship());
    }
    
    
    
    public void testTakeValuesForKeys()
    {
        NSDictionary source = new NSDictionary(new Object[] {"the name", "the value", NSKeyValueCoding.NullValue}, 
                                               new String[] {"key 1"  , "key 2", "key 3"});
        NSMutableDictionary destination = new NSMutableDictionary();
        
        KeyValueCoding.takeValuesForKeys(source, destination, new NSArray(new String[] {"key 1", "key 3"}));

        assertEquals(destination.allKeys().count(), 2);
        assertEquals(destination.valueForKey("key 1"), "the name");
        assertEquals(destination.valueForKey("key 3"), NSKeyValueCoding.NullValue);
    }
    
}
