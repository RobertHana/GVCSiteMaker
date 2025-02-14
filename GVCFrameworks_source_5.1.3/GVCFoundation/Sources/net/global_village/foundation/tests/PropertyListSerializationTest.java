package net.global_village.foundation.tests;

import junit.framework.*;

import net.global_village.foundation.*;



/**
 * Tests for PropertyListSerialization.
 *
 * @see PropertyListSerialization
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class PropertyListSerializationTest extends TestCase
{


    /**
     * Constructor for BooleanFormatterTest.
     * @param name of test
     */
    public PropertyListSerializationTest(String name)
    {
        super(name);
    }



    public void testIsStringSerializedArray()
    {
        assertFalse(PropertyListSerialization.isStringSerializedArray(""));
        assertFalse(PropertyListSerialization.isStringSerializedArray("Foo"));
        assertFalse(PropertyListSerialization.isStringSerializedArray("{Foo}"));
        assertFalse(PropertyListSerialization.isStringSerializedArray("(Foo"));
        assertFalse(PropertyListSerialization.isStringSerializedArray("Foo)"));

        assertTrue(PropertyListSerialization.isStringSerializedArray("()"));
        assertTrue(PropertyListSerialization.isStringSerializedArray("(Foo)"));
        assertTrue(PropertyListSerialization.isStringSerializedArray("(\"Foo\")"));
        assertTrue(PropertyListSerialization.isStringSerializedArray("(Foo, bar)"));
        assertTrue(PropertyListSerialization.isStringSerializedArray("(\"Foo\"  ,   bar)"));
        assertTrue(PropertyListSerialization.isStringSerializedArray("({Foo=bar;})"));

        // Not really valid, but accepted by the parser
        assertTrue(PropertyListSerialization.isStringSerializedArray("(Foo,)"));
    }



    public void testIsStringSerializedDictionary()
    {
        assertFalse(PropertyListSerialization.isStringSerializedDictionary(""));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("Foo"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("{Foo}"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("(Foo)"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("{Foo"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("Foo}"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("Foo=bar;"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("(Foo=bar;)"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("{Foo=bar;"));
        assertFalse(PropertyListSerialization.isStringSerializedDictionary("Foo=bar;}"));

        assertTrue(PropertyListSerialization.isStringSerializedDictionary("{}"));
        assertTrue(PropertyListSerialization.isStringSerializedDictionary("{Foo=bar;}"));
        assertTrue(PropertyListSerialization.isStringSerializedDictionary("{\"Foo\"=\"=bar\";}"));
        assertTrue(PropertyListSerialization.isStringSerializedDictionary("{Foo= bar;}"));
        assertTrue(PropertyListSerialization.isStringSerializedDictionary("{\"Foo\"  =   bar;}"));
        assertTrue(PropertyListSerialization.isStringSerializedDictionary("{foo=(bar);}"));
    }


}
