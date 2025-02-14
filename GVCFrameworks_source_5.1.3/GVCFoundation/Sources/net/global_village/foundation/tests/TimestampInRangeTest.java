package net.global_village.foundation.tests;

import junit.framework.TestCase;

import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;

import net.global_village.foundation.TimestampInRange;

/**
 * Tests for TimestampInRange.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TimestampInRangeTest extends TestCase
{
    TimestampInRange startOfDay;
    TimestampInRange middleOfDay;
    TimestampInRange endOfDay;
    NSTimestamp startTs; 
    NSTimestamp middleTs; 
    NSTimestamp endTs; 
    NSTimestamp startHourTs; 
    NSTimestamp endHourTs;     

    /**
     * Constructor for TimestampInRangeTest.
     *      
     * @param name name of test
     */
    public TimestampInRangeTest(String name)
    {
        super(name);
    }



    protected void setUp() throws Exception
    {
        super.setUp();

        startTs = new NSTimestamp(2003, 04, 28, 0, 0, 0, NSTimeZone.getDefault()); 
        middleTs = new NSTimestamp(2003, 04, 28, 7, 55, 21, NSTimeZone.getDefault()); 
        endTs = new NSTimestamp(2003, 04, 28, 23, 59, 59, NSTimeZone.getDefault());
        
        // Set milliseconds to 999
        endTs = net.global_village.foundation.Date.endOfDayInDefaultTimeZone(endTs);

        startHourTs = new NSTimestamp(2003, 04, 28, 7, 55, 0, NSTimeZone.getDefault()); 
        endHourTs = new NSTimestamp(2003, 04, 28, 7, 55, 59, NSTimeZone.getDefault());      

        // Set milliseconds to 999
        endHourTs = net.global_village.foundation.Date.endOfMinuteInDefaultTimeZone(endHourTs);

        startOfDay = new TimestampInRange(startTs);
        middleOfDay = new TimestampInRange(middleTs);
        endOfDay = new TimestampInRange(endTs);
    }



    /**
     * Tests the accuracy range of this timestamp.  Timestamps are accurate 
     * either to the day or to the minute.    
     */
    public void testAccuracy()
    {
        TimestampInRange testTimestamp = new TimestampInRange(new NSTimestamp());
        assertTrue("day != min", TimestampInRange.DAY_ACCURACY != TimestampInRange.MINUTE_ACCURACY);

        testTimestamp.setAccuracy(TimestampInRange.MINUTE_ACCURACY);
        assertTrue("min == min", testTimestamp.accuracy() == TimestampInRange.MINUTE_ACCURACY);

        testTimestamp.setAccuracy(TimestampInRange.DAY_ACCURACY);
        assertTrue("day == day", testTimestamp.accuracy() == TimestampInRange.DAY_ACCURACY);
    }


    public void testBeginningOfRange()
    {
        startOfDay.setAccuracy(TimestampInRange.DAY_ACCURACY);
        middleOfDay.setAccuracy(TimestampInRange.DAY_ACCURACY);
        endOfDay.setAccuracy(TimestampInRange.DAY_ACCURACY);

        assertTrue(startOfDay.beginningOfRange().equals(startTs));
        assertTrue(middleOfDay.beginningOfRange().equals(startTs));
        assertTrue(endOfDay.beginningOfRange().equals(startTs));
         
        startOfDay.setAccuracy(TimestampInRange.MINUTE_ACCURACY);
        middleOfDay.setAccuracy(TimestampInRange.MINUTE_ACCURACY);
        endOfDay.setAccuracy(TimestampInRange.MINUTE_ACCURACY);

        assertTrue(startOfDay.beginningOfRange().equals(startTs));
        assertTrue(middleOfDay.beginningOfRange().equals(startHourTs));
    }



    public void testEndOfRange()
    {
        startOfDay.setAccuracy(TimestampInRange.DAY_ACCURACY);
        middleOfDay.setAccuracy(TimestampInRange.DAY_ACCURACY);
        endOfDay.setAccuracy(TimestampInRange.DAY_ACCURACY);

        assertTrue(startOfDay.endOfRange().equals(endTs));
        assertTrue(middleOfDay.endOfRange().equals(endTs));
        assertTrue(endOfDay.endOfRange().equals(endTs));

        startOfDay.setAccuracy(TimestampInRange.MINUTE_ACCURACY);
        middleOfDay.setAccuracy(TimestampInRange.MINUTE_ACCURACY);
        endOfDay.setAccuracy(TimestampInRange.MINUTE_ACCURACY);

        assertTrue(middleOfDay.endOfRange().equals(endHourTs));
        assertTrue(endOfDay.endOfRange().equals(endTs));
    }
}
