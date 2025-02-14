package net.global_village.foundation.tests;

import java.text.*;

import junit.framework.*;

import net.global_village.foundation.NullFormatter;


/**
 * Tests for net.global_village.foundation.BooleanFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class NullFormatterTest extends TestCase
{
 

    /**
     * Constructor for BooleanFormatterTest.
     * @param name of test
     */
    public NullFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        StringBuffer sb = NullFormatter.Formatter.format("foo", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("foo"));
    }



    /*
     * Test for java.lang.String format(java.lang.Object)
     */
    public void testFormatObject()
    {
        assertTrue(NullFormatter.Formatter.format("bar").equals("bar"));
    }
    
    
    
    /*
     * Test for Object parseObject(String, ParsePosition)
     */
    public void testParseObjectStringParsePosition()
    {
        ParsePosition position = new ParsePosition(0);
        assertTrue(NullFormatter.Formatter.parseObject("baz", position).equals("baz"));
        assertTrue(position.getIndex() == "baz".length());
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        try
        {
            assertTrue(NullFormatter.Formatter.parseObject("gibble").equals("gibble"));
         }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
    }

}
