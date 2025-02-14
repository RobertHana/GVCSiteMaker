package net.global_village.foundation.tests;

import java.text.*;

import com.webobjects.foundation.*;

import junit.framework.*;
import net.global_village.foundation.*;


/**
 * Tests for net.global_village.foundation.InSetFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class InSetFormatterTest extends TestCase
{
    
    NSArray arrayOfStrings = new NSArray(new String[] {"0", "1" , "2"});
    NSArray arrayOfIntegers = new NSArray(new Integer[] {new Integer(0), new Integer(1) , new Integer(2)});

    
    
    /**
     * Constructor.
     * @param name of test
     */
    public InSetFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        Format formatter = new InSetFormatter(NSArray.EmptyArray, NullFormatter.Formatter);
        StringBuffer sb = formatter.format("12345", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("12345"));
    }



    /*
     * Test for java.lang.String format(java.lang.Object)
     */
    public void testFormatObject()
    {
        Format formatter = new InSetFormatter(NSArray.EmptyArray, NullFormatter.Formatter);
        assertTrue(formatter.format("12345").equals("12345"));
    }
    
    
    
    /*
     * Test for Object parseObject(String, ParsePosition)
     */
    public void testParseObjectStringParsePosition()
    {
        Format formatter = new InSetFormatter(arrayOfStrings, NullFormatter.Formatter);
        ParsePosition position = new ParsePosition(0);
        assertTrue(formatter.parseObject("1", position).equals("1"));
        assertTrue(position.getIndex() == 1);
        
        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("12", position) == null);
        assertTrue(position.getIndex() == 0);
        
        formatter = new InSetFormatter(arrayOfIntegers, IntegerFormatter.Formatter);
        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("1", position).equals(new Integer(1)));
        assertTrue(position.getIndex() == 1);
        
        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("12", position) == null);
        assertTrue(position.getIndex() == 0);
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        Format formatter = new InSetFormatter(arrayOfStrings, NullFormatter.Formatter);
        try
        {
            assertTrue(formatter.parseObject("1").equals("1"));
            formatter = new InSetFormatter(arrayOfIntegers, IntegerFormatter.Formatter);
            assertTrue(formatter.parseObject("2").equals(new Integer(2)));
         }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }

        try
        {
            assertTrue(formatter.parseObject("9").equals(new Integer(9)));
            fail("accepted unaccepable value");
        }
        catch (ParseException e) {}
        
    }

}
