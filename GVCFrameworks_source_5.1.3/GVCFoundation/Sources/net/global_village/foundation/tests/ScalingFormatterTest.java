package net.global_village.foundation.tests;

import java.math.*;
import java.text.*;

import junit.framework.*;

import net.global_village.foundation.*;


/**
 * Test ScalingFormatter functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ScalingFormatterTest extends TestCase
{

    /**
     * Constructor.
     * @param name of test
     */
    public ScalingFormatterTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testFormatObjectStringBufferFieldPosition()
    {
        Format formatter = new ScalingFormatter(.125, IntegerFormatter.Formatter);

        StringBuffer sb = formatter.format(new BigDecimal(800), new StringBuffer(), new FieldPosition(0));
        assertEquals("100", sb.toString());

        sb = formatter.format(new Integer(800), new StringBuffer(), new FieldPosition(0));
        assertEquals("100", sb.toString());

        sb = formatter.format(new Double(800), new StringBuffer(), new FieldPosition(0));
        assertEquals("100", sb.toString());

        formatter = new ScalingFormatter(8, IntegerFormatter.Formatter);

        sb = formatter.format(new BigDecimal(800), new StringBuffer(), new FieldPosition(0));
        assertEquals("6400", sb.toString());

        sb = formatter.format(new Integer(800), new StringBuffer(), new FieldPosition(0));
        assertEquals("6400", sb.toString());

        sb = formatter.format(new Double(800), new StringBuffer(), new FieldPosition(0));
        assertEquals("6400", sb.toString());
    }



    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        Format formatter = new ScalingFormatter(.125, IntegerFormatter.Formatter);
        try
        {
            assertTrue(formatter.parseObject("100").equals(new Integer(800)));

            formatter = new ScalingFormatter(8, IntegerFormatter.Formatter);
            assertTrue(formatter.parseObject("6400").equals(new Integer(800)));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
    }



}
