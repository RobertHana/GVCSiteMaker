package net.global_village.foundation.tests;

import java.text.*;

import junit.framework.*;
import net.global_village.foundation.*;


/**
 * Tests for net.global_village.foundation.LengthCheckFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LengthCheckFormatterTest extends TestCase
{

    /**
     * Constructor for BooleanFormatterTest.
     * @param name of test
     */
    public LengthCheckFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        Format formatter = new LengthCheckFormatter(0, 5);
        StringBuffer sb = formatter.format("12345", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("12345"));
        
        sb = formatter.format("0", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("0"));
        
        sb = formatter.format("-1", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("-1"));
        
        sb = formatter.format("", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals(""));
    }



    /*
     * Test for java.lang.String format(java.lang.Object)
     */
    public void testFormatObject()
    {
        Format formatter = new LengthCheckFormatter(0, 5);
        assertTrue(formatter.format("12345").equals("12345"));
        assertTrue(formatter.format("0").equals("0"));
        assertTrue(formatter.format("-1").equals("-1"));
        assertTrue(formatter.format("").equals(""));
    }
    
    
    
    /*
     * Test for Object parseObject(String, ParsePosition)
     */
    public void testParseObjectStringParsePosition()
    {
        Format formatter = new LengthCheckFormatter(0, 5);
        ParsePosition position = new ParsePosition(0);
        assertTrue(formatter.parseObject("baz", position).equals("baz"));
        assertTrue(position.getIndex() == "baz".length());

        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("", position).equals(""));
        assertTrue(position.getIndex() == 0);
        
        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("too long string", position) ==  null);
        assertTrue(position.getIndex() == 0);
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        Format formatter = new LengthCheckFormatter(0, 5);
        try
        {
            assertTrue(formatter.parseObject("12345").equals("12345"));
            assertTrue(formatter.parseObject("0").equals("0"));
            assertTrue(formatter.parseObject("-1").equals("-1"));
            assertTrue(formatter.parseObject("0").equals("0"));
            
            formatter = new LengthCheckFormatter(5, 5);
            assertTrue(formatter.parseObject("12345").equals("12345"));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }

        try
        {
            assertTrue(formatter.parseObject("123456").equals("123456"));
            fail("accepted too long string");
        }
        catch (ParseException e) {}
        
        formatter = new LengthCheckFormatter(3, 5);
        try
        {
            assertTrue(formatter.parseObject("12").equals("12"));
            fail("accepted too short string");
        }
        catch (ParseException e) {}
        
    }

}
