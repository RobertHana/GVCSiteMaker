package net.global_village.foundation.tests;

import java.text.*;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Tests for net.global_village.foundation.ArrayFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ArrayFormatterTest extends TestCase
{

    NSArray arrayOfStrings = new NSArray(new String[] {"0", "1" , "2"});
    NSArray arrayOfIntegers = new NSArray(new Integer[] {new Integer(0), new Integer(1) , new Integer(2)});
    String stringArray = "0, 1,2";
    String cleanStringArray = "0,1,2";
    String trailingSeparatorStringArray = "0,1,2,";


    /**
     * Constructor.
     * @param name of test
     */
    public ArrayFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        Format formatter = new ArrayFormatter(",", NullFormatter.Formatter);
        StringBuffer sb = formatter.format("12345", new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("12345"));

        sb = formatter.format(arrayOfStrings, new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals(cleanStringArray));

        formatter = new ArrayFormatter(",", IntegerFormatter.Formatter);
        sb = formatter.format(arrayOfIntegers, new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals(cleanStringArray));
    }



    /*
     * Test for java.lang.String format(java.lang.Object)
     */
    public void testFormatObject()
    {
        Format formatter = new ArrayFormatter(",", NullFormatter.Formatter);
        assertTrue(formatter.format("12345").equals("12345"));
        assertTrue(formatter.format(arrayOfStrings).equals(cleanStringArray));
        assertTrue(formatter.format(arrayOfIntegers).equals(cleanStringArray));
        assertTrue(formatter.format(NSArray.EmptyArray).equals(""));
    }



    /*
     * Test for Object parseObject(String, ParsePosition)
     */
    public void testParseObjectStringParsePosition()
    {
        Format formatter = new ArrayFormatter(",", NullFormatter.Formatter);
        ParsePosition position = new ParsePosition(0);
        assertTrue(formatter.parseObject("12345", position).equals(new NSArray("12345")));
        assertTrue(position.getIndex() == "12345".length());

        position = new ParsePosition(0);
        assertTrue(formatter.parseObject(stringArray, position).equals(arrayOfStrings));
        assertTrue(position.getIndex() == stringArray.length());

        formatter = new ArrayFormatter(",", IntegerFormatter.Formatter);
        position = new ParsePosition(0);
        assertTrue(formatter.parseObject(stringArray, position).equals(arrayOfIntegers));
        assertTrue(position.getIndex() == stringArray.length());

        position = new ParsePosition(0);
        assertTrue(formatter.parseObject(trailingSeparatorStringArray, position).equals(arrayOfIntegers));
        assertTrue(position.getIndex() == trailingSeparatorStringArray.length());

        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("", position).equals(NSArray.EmptyArray));
        assertTrue(position.getIndex() == 0);

        position = new ParsePosition(0);
        assertTrue(formatter.parseObject("A,B,C", position) ==  null);
        assertTrue(position.getIndex() == 0);
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        Format formatter = new ArrayFormatter(",", NullFormatter.Formatter);
        try
        {
            assertTrue(formatter.parseObject("12345").equals(new NSArray("12345")));
            assertTrue(formatter.parseObject(stringArray).equals(arrayOfStrings));

            formatter = new ArrayFormatter(",", IntegerFormatter.Formatter);
            assertTrue(formatter.parseObject(stringArray).equals(arrayOfIntegers));
            assertTrue(formatter.parseObject("").equals(NSArray.EmptyArray));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }

        try
        {
            assertTrue(formatter.parseObject("A,B,C").equals(NSArray.EmptyArray));
            fail("accepted invalid string");
        }
        catch (ParseException e) {}

    }

}
