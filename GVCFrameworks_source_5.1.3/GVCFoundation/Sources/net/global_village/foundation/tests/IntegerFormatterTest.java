package net.global_village.foundation.tests;

import java.text.*;
import junit.framework.*;
import net.global_village.foundation.IntegerFormatter;


/**
 * Tests for net.global_village.foundation.IntegerFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class IntegerFormatterTest extends TestCase
{
    IntegerFormatter formatter = IntegerFormatter.Formatter;

    /**
     * Constructor for IntegerFormatterTest.
     * @param name of test
     */
    public IntegerFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        StringBuffer sb = formatter.format(new Integer(12345), new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("12345"));
        
        sb = formatter.format(new Integer(0), new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("0"));
        
        sb = formatter.format(new Integer(-1), new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("-1"));
    }



    /*
     * Test for java.lang.String format(java.lang.Object)
     */
    public void testFormatObject()
    {
        assertTrue(formatter.format(new Integer(12345)).equals("12345"));
        assertTrue(formatter.format(new Integer(0)).equals("0"));
        assertTrue(formatter.format(new Integer(-1)).equals("-1"));
    }
    
    
    
    /*
     * Test for Object parseObject(String, ParsePosition)
     */
    public void testParseObjectStringParsePosition()
    {
        // This method not implemented
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        try
        {
            assertTrue(formatter.parseObject("12345").equals(new Integer(12345)));
            assertTrue(formatter.parseObject("0").equals(new Integer(0)));
            assertTrue(formatter.parseObject("-1").equals(new Integer(-1)));
            assertTrue(formatter.parseObject("  12345  ").equals(new Integer(12345)));
            assertTrue(formatter.parseObject("  0 ").equals(new Integer(0)));
            assertTrue(formatter.parseObject(" -1 ").equals(new Integer(-1)));
            
            assertTrue(formatter.parseObject("  12345   ").equals(new Integer(12345)));
            assertTrue(formatter.parseObject("  0   ").equals(new Integer(0)));
            assertTrue(formatter.parseObject("   -1  ").equals(new Integer(-1)));
            
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }

        try
        {
            assertTrue(formatter.parseObject("foo").equals(new Integer(7)));
            fail("accepted troo");
        }
        catch (ParseException e) {}
    }

}
