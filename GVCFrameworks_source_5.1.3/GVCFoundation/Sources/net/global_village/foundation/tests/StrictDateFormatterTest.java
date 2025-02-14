package net.global_village.foundation.tests;

import java.text.*;
import java.util.*;

import junit.framework.TestCase;
import net.global_village.foundation.StrictDateFormatter;

import com.webobjects.foundation.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class StrictDateFormatterTest extends TestCase
{

    NSTimestampFormatter testFormatter;

    
    public StrictDateFormatterTest(String name)
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
        testFormatter = new StrictDateFormatter("MM dd yyyy");
    }



    /**
     * Test parse
     */
    public void testParse()
    {
        try
        {
            testFormatter.parseObject("12 01 2001");
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid date");
        }

        try
        {
            testFormatter.parseObject("1 1 2001");
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid truncated date");
        }

        try
        {
            testFormatter.parseObject("13 01 2001");
            fail("Accepted month of 13");
        }
        catch (ParseException e) { }

        try
        {
            testFormatter.parseObject("12 32 2001");
            fail("Accepted day of 32");
        }
        catch (ParseException e) { }

        try
        {
            testFormatter.parseObject("02 30 2001");
            fail("Accepted Feb 30");
        }
        catch (ParseException e) { }

        try
        {
            testFormatter.parseObject("13 01");
            fail("Accepted missing year");
        }
        catch (ParseException e) { }

        try
        {
            testFormatter.parseObject("01 2001");
            fail("Accepted missing month");
        }
        catch (ParseException e) { }
        try
        {
            testFormatter.parseObject("Hello");
            fail("Accepted Hello");
        }
        catch (ParseException e) { }

        testFormatter.setPattern("dd MM yyyy");
        try
        {
            testFormatter.parseObject("30 01 2001");
        }
        catch (ParseException e)
        {
            fail("Failed parsing valid date after pattern changed.");
        }
    }


    
    /**
     * Test Reversibility
     *
     * For formatters to function properly, they must be able to call between format and parse without altering the data.
     */
    public void testReversibility()
    {
        testFormatter.setPattern("dd MM yyyy HH:mm:ss");

        // There is something around 1958 that makes this problem occur.
        GregorianCalendar aDate = new GregorianCalendar(1957, 0, 1);
        
        for (int dayCount = 0; dayCount <= (3 * 366); dayCount++)
        {
            try
            {
                NSTimestamp timestampFromDate = new NSTimestamp(aDate.getTime());
                String dateAsString = testFormatter.format(timestampFromDate);
                NSTimestamp stringAsTimestamp = (NSTimestamp) testFormatter.parseObject(dateAsString);
                assertTrue("Failed to reverse date " + aDate.getTime(), stringAsTimestamp.equals(timestampFromDate));
                aDate.add(Calendar.DAY_OF_YEAR , 1);
            }
            catch (ParseException e)
            {
                fail("Failed  to handle " + aDate);
            }
        }
    }

    
    /**
     * Test NSTimestampFormatter parsing with time zone Z format bug
     *
     * 
     */
    public void testNSTimestampFormatterParseZBug()
    {

        String dateWithGMTOffset = "2007-10-25 2:53:27 GMT-06:00";
        String dateWithRFC822Offset = "2007-10-25 2:53:27 -0600";
        
        String formatForGMTOffset = "yyyy-MM-dd HH:mm:ss z";
        String formatForRFC822Offset = "yyyy-MM-dd HH:mm:ss Z";
        
        NSTimestampFormatter gmtOffsetFormatter = new NSTimestampFormatter(formatForGMTOffset);
        NSTimestampFormatter rfc822OffsetFormatter = new NSTimestampFormatter(formatForRFC822Offset);
        
        Format gmtOffsetJavaFormatter = new SimpleDateFormat(formatForGMTOffset);
        Format rfc822OffsetJavaFormatter = new SimpleDateFormat(formatForRFC822Offset);

        try
        {
            NSTimestamp timestampFromGMTOffset = (NSTimestamp) gmtOffsetFormatter.parseObject(dateWithGMTOffset);
            NSTimestamp timestampFromRFC822Offset = (NSTimestamp) rfc822OffsetFormatter.parseObject(dateWithRFC822Offset);
            NSTimestamp expectedTimestamp = new NSTimestamp(2007, 10, 25, 2, 53, 27, NSTimeZone.timeZoneForSecondsFromGMT(-6 * 60 * 60));

            assertTrue("GMT Offset with Java", gmtOffsetJavaFormatter.parseObject(dateWithGMTOffset).equals(expectedTimestamp));
            assertTrue("RFC 822 Offset with Java", rfc822OffsetJavaFormatter.parseObject(dateWithRFC822Offset).equals(expectedTimestamp));
            
            assertTrue("GMT Offset", timestampFromGMTOffset.equals(expectedTimestamp));
            
            // This should be true.
            assertFalse("RFC 822 Offset", timestampFromRFC822Offset.equals(expectedTimestamp));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
    }
    
    
    
    /**
     * Test NSTimestampFormatter formatting with time zone Z format bug
     *
     * 
     */
    public void testNSTimestampFormatterFormatZBug()
    {

        String dateWithGMTOffset = "2007-10-25 2:53:27 GMT-06:00";
        String formatForGMTOffset = "yyyy-MM-dd HH:mm:ss z";
        String formatForRFC822Offset = "yyyy-MM-dd HH:mm:ss Z";
        
        NSTimestampFormatter gmtOffsetNSTimestampFormatter = new NSTimestampFormatter(formatForGMTOffset);
        NSTimestampFormatter rfc822OffsetNSTimestampFormatter = new NSTimestampFormatter(formatForRFC822Offset);
        
        Format gmtOffsetJavaFormatter = new SimpleDateFormat(formatForGMTOffset);
        Format rfc822OffsetJavaFormatter = new SimpleDateFormat(formatForRFC822Offset);
        
        try
        {
            NSTimestamp timestampFromNSTimestampFormatter = (NSTimestamp) gmtOffsetNSTimestampFormatter.parseObject(dateWithGMTOffset);
            Date timestampFromJavaFormatter = (Date) gmtOffsetJavaFormatter.parseObject(dateWithGMTOffset);
            assertEquals("Parsed timestamps match", 
                         timestampFromNSTimestampFormatter.getTime(),
                         timestampFromJavaFormatter.getTime());
            
            // These SHOULD be the same, but NSTimestampFormatter is producing 2007-10-25 01:53:27 -0659
            // instead of 2007-10-25 01:53:27 -0700 (-0700 is the local timezone).  Note that the timezone
            // is off by one minute.
            assertNotSame("RFC 822 Offset with Java", 
                         rfc822OffsetNSTimestampFormatter.format(timestampFromNSTimestampFormatter),
                         rfc822OffsetJavaFormatter.format(timestampFromNSTimestampFormatter));

            assertNotSame("RFC 822 Offset with Java", 
                         rfc822OffsetNSTimestampFormatter.format(timestampFromNSTimestampFormatter),
                         rfc822OffsetJavaFormatter.format(timestampFromJavaFormatter));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
    }
    
}
