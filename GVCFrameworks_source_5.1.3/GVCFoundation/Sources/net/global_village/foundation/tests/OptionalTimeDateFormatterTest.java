package net.global_village.foundation.tests;

import java.text.ParseException;

import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

import net.global_village.foundation.OptionalTimeDateFormatter;
import net.global_village.foundation.TimestampInRange;

/**
 * Describe class here.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class OptionalTimeDateFormatterTest extends StrictDateFormatterTest
{
    
     
    public OptionalTimeDateFormatterTest(String name)
    {
        super(name);
    }



    /**
     * Setup test
     *
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        testFormatter = new OptionalTimeDateFormatter("MM dd yyyy h:mm aa", "10:37 PM");
    }



    /**
    * Test parse
    */
    public void testParse()
    {
        NSTimestamp ts;
        
        try
        {
            ts = (NSTimestamp)testFormatter.parseObject("12 01 2001 5:27 AM");
            assertTrue("correct class", ts instanceof TimestampInRange);
            assertTrue("correct accuracy", ((TimestampInRange)ts).accuracy() ==
            TimestampInRange.MINUTE_ACCURACY);
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid date and time " + e.getMessage());
        }
        
        try
        {
            ts = (NSTimestamp)testFormatter.parseObject("12 01 2001");
            assertTrue("correct class", ts instanceof TimestampInRange);
            assertTrue("correct accuracy", ((TimestampInRange)ts).accuracy() ==
            TimestampInRange.DAY_ACCURACY);
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid date " + e.getMessage());
        }

        try
        {
            ts = (NSTimestamp)testFormatter.parseObject("1 1 2001 2:05 AM");
            assertTrue("correct class", ts instanceof TimestampInRange);
            assertTrue("correct accuracy", ((TimestampInRange)ts).accuracy() ==
            TimestampInRange.MINUTE_ACCURACY);
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid truncated date and time " + e.getMessage());
        }

        try
        {
            ts = (NSTimestamp)testFormatter.parseObject("1 1 2001");
            assertTrue("correct class", ts instanceof TimestampInRange);
            assertTrue("correct accuracy", ((TimestampInRange)ts).accuracy() ==
            TimestampInRange.DAY_ACCURACY);
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid truncated date " + e.getMessage());
        }

        try
        {
            assertTrue("Assumed time equals explicit time", 
                testFormatter.parseObject("12 01 2001 10:37 PM").
                    equals(testFormatter.parseObject("12 01 2001")));
        }
        catch (ParseException e) { }
        
    }

}


