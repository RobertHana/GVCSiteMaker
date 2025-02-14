package net.global_village.foundation.tests;

import java.text.*;
import java.lang.Boolean;

import junit.framework.*;

import net.global_village.foundation.BooleanFormatter;


/**
 * Tests for net.global_village.foundation.BooleanFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class BooleanFormatterTest extends TestCase
{
    Format basicFormatter = new BooleanFormatter();
    Format yesNoFormatter = new BooleanFormatter("Yes", "No");


    /**
     * Constructor for BooleanFormatterTest.
     * @param name of test
     */
    public BooleanFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        StringBuffer sb = basicFormatter.format(Boolean.TRUE, new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("true"));
        
        sb = basicFormatter.format(Boolean.FALSE, new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("false"));
        
        sb = yesNoFormatter.format(Boolean.TRUE, new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("Yes"));
        
        sb = yesNoFormatter.format(Boolean.FALSE, new StringBuffer(), new FieldPosition(0));
        assertTrue(sb.toString().equals("No"));
    }



    /*
     * Test for java.lang.String format(java.lang.Object)
     */
    public void testFormatObject()
    {
        assertTrue(basicFormatter.format(Boolean.TRUE).equals("true"));
        assertTrue(basicFormatter.format(Boolean.FALSE).equals("false"));

        assertTrue(yesNoFormatter.format(Boolean.TRUE).equals("Yes"));
        assertTrue(yesNoFormatter.format(Boolean.FALSE).equals("No"));
    }
    
    
    
    /*
     * Test for Object parseObject(String, ParsePosition)
     */
    public void testParseObjectStringParsePosition()
    {
        ParsePosition position = new ParsePosition(0);
        assertTrue(basicFormatter.parseObject("true", position).equals(Boolean.TRUE));
        assertTrue(position.getIndex() == 4);
        position.setIndex(0);
        
        assertTrue(basicFormatter.parseObject("True", position).equals(Boolean.TRUE));
        assertTrue(position.getIndex() == 4);
        position.setIndex(0);
        
        assertTrue(basicFormatter.parseObject("false", position).equals(Boolean.FALSE));
        assertTrue(position.getIndex() == 5);
        position.setIndex(0);
        
        assertTrue(basicFormatter.parseObject("False", position).equals(Boolean.FALSE));
        assertTrue(position.getIndex() == 5);
        position.setIndex(0);

        position.setIndex(1);
        assertTrue(basicFormatter.parseObject("False", position) == null);
        assertTrue(position.getIndex() == 1);

        position.setIndex(0);
        assertTrue(yesNoFormatter.parseObject("yes", position).equals(Boolean.TRUE));
        assertTrue(position.getIndex() == 3);
        position.setIndex(0);
        
        assertTrue(yesNoFormatter.parseObject("YES", position).equals(Boolean.TRUE));
        assertTrue(position.getIndex() == 3);
        position.setIndex(0);
        
        assertTrue(yesNoFormatter.parseObject("no", position).equals(Boolean.FALSE));
        assertTrue(position.getIndex() == 2);
        position.setIndex(0);
        
        assertTrue(yesNoFormatter.parseObject("No", position).equals(Boolean.FALSE));
        assertTrue(position.getIndex() == 2);
        position.setIndex(0);

        position.setIndex(1);
        assertTrue(yesNoFormatter.parseObject("Nye", position) == null);
        assertTrue(position.getIndex() == 1);
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        try
        {
            assertTrue(basicFormatter.parseObject("true").equals(Boolean.TRUE));
            assertTrue(basicFormatter.parseObject("True").equals(Boolean.TRUE));
            assertTrue(basicFormatter.parseObject("false").equals(Boolean.FALSE));
            assertTrue(basicFormatter.parseObject("False").equals(Boolean.FALSE));

            assertTrue(yesNoFormatter.parseObject("yes").equals(Boolean.TRUE));
            assertTrue(yesNoFormatter.parseObject("Yes").equals(Boolean.TRUE));
            assertTrue(yesNoFormatter.parseObject("no").equals(Boolean.FALSE));
            assertTrue(yesNoFormatter.parseObject("No").equals(Boolean.FALSE));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }

        try
        {
            assertTrue(basicFormatter.parseObject("troo").equals(Boolean.TRUE));
            fail("accepted troo");
        }
        catch (ParseException e) {}
    }

}
