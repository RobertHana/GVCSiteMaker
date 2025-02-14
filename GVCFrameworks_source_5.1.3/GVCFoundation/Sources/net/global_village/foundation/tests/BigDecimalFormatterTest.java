package net.global_village.foundation.tests;

import java.text.*;
import junit.framework.*;
import net.global_village.foundation.BigDecimalFormatter;


/**
 * Tests for net.global_village.foundation.BigDecimalFormatter
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class BigDecimalFormatterTest extends TestCase
{
    BigDecimalFormatter formatter = BigDecimalFormatter.Formatter;

    /**
     * Constructor for BigDecimalFormatterTest.
     * @param name of test
     */
    public BigDecimalFormatterTest(String name)
    {
        super(name);
    }


    /*
     * Test for java.lang.Object parseObject(java.lang.String)
     */
    public void testParseObjectString()
    {
        try
        {
            assertTrue(formatter.parseObject("12345").equals(new java.math.BigDecimal(12345)));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
    }

}
