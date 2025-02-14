package net.global_village.foundation.tests;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import net.global_village.foundation.Date;

import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;


/**
 * Test the Date functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DateTest extends TestCase
{
    GregorianCalendar gc;
    NSTimestamp gd;
    NSTimestamp gd2;
    NSTimestamp gd3;
    NSTimestamp gd4;
    NSTimestamp gd5;


    public DateTest(String name)
    {
        super(name);
    }



    /**
     * Sets up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        gc = new GregorianCalendar(1971, 03, 20);
        gd = new NSTimestamp(1971, 3, 20, 13, 14, 55, TimeZone.getDefault());
        gd2 = new NSTimestamp(2001, 3, 26, 4, 7, 3, TimeZone.getDefault());
        gd3 = new NSTimestamp(2001, 3, 26, 5, 7, 3, TimeZone.getDefault());
        gd4 = new NSTimestamp(2001, 3, 28, 21, 55, 0, TimeZone.getDefault());
        gd5 = new NSTimestamp(2001, 4, 28, 21, 55, 0, TimeZone.getDefault());
    }



    /**
     * Test timezone conversions.
     */
    public void testTimeZoneConversions()
    {
        final String testTZAbbreviation = "PST";

        NSTimeZone nsTZ = NSTimeZone.timeZoneWithName(testTZAbbreviation, true);
        TimeZone tz = TimeZone.getTimeZone(testTZAbbreviation);

        assertTrue(nsTZ.getOffset(GregorianCalendar.AD,
                                  2002,
                                  Calendar.FEBRUARY,
                                  6,
                                  Calendar.WEDNESDAY,
                                  0) ==
                   tz.getOffset(GregorianCalendar.AD,
                                2002,
                                Calendar.FEBRUARY,
                                6,
                                Calendar.WEDNESDAY,
                                0));
        assertTrue(nsTZ.getOffset(GregorianCalendar.AD,
                                  2002,
                                  Calendar.FEBRUARY,
                                  6,
                                  Calendar.WEDNESDAY,
                                  0) ==
                   Date.timeZoneToNSTimeZone(tz).getOffset(GregorianCalendar.AD,
                                                           2002,
                                                           Calendar.FEBRUARY,
                                                           6,
                                                           Calendar.WEDNESDAY,
                                                           0));
        assertTrue(Date.nsTimeZoneToTimeZone(nsTZ).getOffset(GregorianCalendar.AD,
                                                             2002,
                                                             Calendar.FEBRUARY,
                                                             6,
                                                             Calendar.WEDNESDAY,
                                                             0) ==
                   tz.getOffset(GregorianCalendar.AD,
                                2002,
                                Calendar.FEBRUARY,
                                6,
                                Calendar.WEDNESDAY,
                                0));
    }



    /**
     * Test date conversions.
     */
    public void testDateConversions()
    {
        NSTimestamp newTS = Date.gregorianCalendarToNSTimestamp(gc);
        assertTrue(newTS.getTime() == gc.getTime().getTime());

        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(gd);
        assertTrue(newGC.getTime().getTime() == gd.getTime());
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 20);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 55);
    }



    /**
     * Test noonInDefaultTimeZone.  Assumes that nsTimestampToGregorianCalendar works, since we need to convert to a calendar subclass to test.
     */
    public void testNoonInDefaultTimeZone()
    {
        NSTimestamp newTS = Date.noonInDefaultTimeZone(gd);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 20);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 12);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 0);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 0);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.noonInDefaultTimeZone(gd2);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 2001);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 26);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 12);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 0);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 0);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));
    }



    /**
     * Test beginningOfDayInDefaultTimeZone.
     */
    public void testBeginningOfDayInDefaultTimeZone()
    {
        NSTimestamp newTS = Date.beginningOfDayInDefaultTimeZone(gd);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 20);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 0);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 0);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 0);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.beginningOfDayInDefaultTimeZone(gd2);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 2001);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 26);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 0);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 0);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 0);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));
    }



    /**
     * Test endOfDayInDefaultTimeZone.
     */
    public void testEndOfDayInDefaultTimeZone()
    {
        NSTimestamp newTS = Date.endOfDayInDefaultTimeZone(gd);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 20);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 23);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 59);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 59);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 999);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.endOfDayInDefaultTimeZone(gd2);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 2001);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 26);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 23);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 59);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 59);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 999);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));
    }



    /**
     * Test beginningOfDayInDefaultTimeZone.
     */
    public void testBeginningOfMinuteInDefaultTimeZone()
    {
        NSTimestamp newTS = Date.beginningOfMinuteInDefaultTimeZone(gd);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 20);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 0);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.beginningOfMinuteInDefaultTimeZone(gd2);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 2001);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 26);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 4);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 7);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 0);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));
    }



    /**
     * Test endOfDayInDefaultTimeZone.
     */
    public void testEndOfMinuteInDefaultTimeZone()
    {
        NSTimestamp newTS = Date.endOfMinuteInDefaultTimeZone(gd);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 20);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 59);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 999);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.endOfMinuteInDefaultTimeZone(gd2);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 2001);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 26);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 4);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 7);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 59);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 999);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));
    }



    /**
     * Test constantDateInDefaultTimeZone.
     */
    public void testConstantDateInDefaultTimeZone()
    {
        NSTimestamp newTS = Date.constantDateInDefaultTimeZone(gd);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 55);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        NSTimestamp newTS2 = Date.constantDateInDefaultTimeZone(gd2);
        newGC = Date.nsTimestampToGregorianCalendar(newTS2);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 4);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 7);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 3);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        assertTrue(Date.equalsByDay(newTS, newTS2));
    }



    /**
     * Test dateByAddingDays.
     */
    public void testDateByAddingDays()
    {
        NSTimestamp newTS = Date.dateByAddingDays(gd, 1);
        GregorianCalendar newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 21);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 55);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        // This addition will pass over the daylight savings time
        newTS = Date.dateByAddingDays(gd2, 26);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 2001);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.APRIL);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 21);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 4); // hour will NOT change over daylight savings time...
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 7);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 3);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.tomorrow(gd);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 21);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 55);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));

        newTS = Date.yesterday(gd);
        newGC = Date.nsTimestampToGregorianCalendar(newTS);
        assertTrue(newGC.get(GregorianCalendar.YEAR) == 1971);
        assertTrue(newGC.get(GregorianCalendar.MONTH) == Calendar.MARCH);
        assertTrue(newGC.get(GregorianCalendar.DAY_OF_MONTH) == 19);
        assertTrue(newGC.get(GregorianCalendar.HOUR_OF_DAY) == 13);
        assertTrue(newGC.get(GregorianCalendar.MINUTE) == 14);
        assertTrue(newGC.get(GregorianCalendar.SECOND) == 55);
        assertTrue(newGC.get(GregorianCalendar.MILLISECOND) == 0);
        assertTrue(newGC.getTimeZone().equals(TimeZone.getDefault()));
    }



    /**
     * Test weekend and weekday functions.
     */
    public void testWeekendAndWeekday()
    {
        assertTrue(Date.isWeekend(gd));
        assertTrue( ! Date.isWeekday(gd));
        assertTrue(Date.isWeekday(gd2));
        assertTrue( ! Date.isWeekend(gd2));
    }



    /**
     * Test comparisons by day.
     */
    public void testComparisonsByDay()
    {
        assertTrue(Date.compareToByDay(gd, gd2) == NSComparator.OrderedAscending);
        assertTrue(Date.compareToByDay(gd2, gd) == NSComparator.OrderedDescending);
        assertTrue(Date.compareToByDay(gd2, gd3) == NSComparator.OrderedSame);

        assertTrue(Date.equalsByDay(gd2, gd3));
        assertTrue( ! Date.equalsByDay(gd2, gd));
    }



    /**
     * Test isInFutureByDay, isInPastByDay and isTodayByDay functions
     */
    public void testPastTodayAndFuture()
    {
        NSTimestamp today = new NSTimestamp();
        
        assertTrue(Date.isInPastByDay(gd));
        assertTrue( ! Date.isInFutureByDay(gd));
        assertTrue( ! Date.isTodayByDay(gd));

        assertTrue(Date.isTodayByDay(today));
        assertTrue( ! Date.isInPastByDay(today));
        assertTrue( ! Date.isInFutureByDay(today));

        assertTrue(Date.isInFutureByDay(Date.dateByAddingDays(today, 20)));
        assertTrue( ! Date.isTodayByDay(Date.dateByAddingDays(today, 20)));
        assertTrue( ! Date.isInPastByDay(Date.dateByAddingDays(today, 20)));
    }


    
    /**
        * Test compareToByTime.
     */
    public void testCompareToByTime()
    {
        assertTrue(Date.compareToByTime(gd2, gd3) == NSComparator.OrderedAscending);
        assertTrue(Date.compareToByTime(gd2, gd4) == NSComparator.OrderedAscending);
        assertTrue(Date.compareToByTime(gd4, gd5) == NSComparator.OrderedSame);
        assertTrue(Date.compareToByTime(gd, gd2) == NSComparator.OrderedDescending);
    }



    /**
     * Test isBetweenDatesByDay.
     */
    public void testIsBetweenDatesByDay()
    {    
        assertTrue(Date.isBetweenDatesByDay(gd3, gd, gd4)); 
        assertTrue(Date.isBetweenDatesByDay(gd3, gd2, gd4));   
        assertTrue(Date.isBetweenDatesByDay(gd3, gd2, gd3)); 
        assertTrue( ! Date.isBetweenDatesByDay(gd, gd3, gd4)); 
        assertTrue( ! Date.isBetweenDatesByDay(gd, gd4, gd5)); 

        try
        {
            Date.isBetweenDatesByDay(gd, gd4, gd3);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test occurancesOfDayOfWeek.
     */
    public void testOccurancesOfDayOfWeek()
    {
        assertTrue(Date.occurancesOfDayOfWeek(GregorianCalendar.SUNDAY, gd2, gd4) == 0);
        assertTrue(Date.occurancesOfDayOfWeek(GregorianCalendar.TUESDAY, gd2, gd4) == 1);
        assertTrue(Date.occurancesOfDayOfWeek(GregorianCalendar.WEDNESDAY, gd4, gd5) == 5);
        assertTrue(Date.occurancesOfDayOfWeek(GregorianCalendar.SATURDAY, gd4, gd5) == 5);
        assertTrue(Date.occurancesOfDayOfWeek(GregorianCalendar.SUNDAY, gd4, gd5) == 4);
        assertTrue(Date.occurancesOfDayOfWeek(GregorianCalendar.TUESDAY, gd4, gd5) == 4);
    }



    /**
     * Test calendar function.
     */
    public void testCalendarFunctions()
    {
        //test yearOfCommonEra
        assertTrue(Date.yearOfCommonEra(gd) == 1971);
        assertTrue(Date.yearOfCommonEra(gd2) == 2001);
        assertTrue(Date.yearOfCommonEra(gd3) == 2001);
        assertTrue(Date.yearOfCommonEra(gd4) == 2001);
        assertTrue(Date.yearOfCommonEra(gd5) == 2001);

        //test dayOfYear
        assertTrue(Date.dayOfYear(gd) == 79);
        assertTrue(Date.dayOfYear(gd2) == 85);
        assertTrue(Date.dayOfYear(gd3) == 85);
        assertTrue(Date.dayOfYear(gd4) == 87);
        assertTrue(Date.dayOfYear(gd5) == 118);

        //test dayOfMonth
        assertTrue(Date.dayOfMonth(gd) == 20);
        assertTrue(Date.dayOfMonth(gd2) == 26);
        assertTrue(Date.dayOfMonth(gd3) == 26);
        assertTrue(Date.dayOfMonth(gd4) == 28);
        assertTrue(Date.dayOfMonth(gd5) == 28);

        //test dayOfWeek
        assertTrue(Date.dayOfWeek(gd) == 7);
        assertTrue(Date.dayOfWeek(gd2) == 2);
        assertTrue(Date.dayOfWeek(gd3) == 2);
        assertTrue(Date.dayOfWeek(gd4) == 4);
        assertTrue(Date.dayOfWeek(gd5) == 7);

        //test monthOfYear
        assertTrue(Date.monthOfYear(gd) == 3);
        assertTrue(Date.monthOfYear(gd2) == 3);
        assertTrue(Date.monthOfYear(gd3) == 3);
        assertTrue(Date.monthOfYear(gd4) == 3);
        assertTrue(Date.monthOfYear(gd5) == 4);

        //test weekOfYear
        assertTrue(Date.weekOfYear(gd) == 12);
        assertTrue(Date.weekOfYear(gd2) == 13);
        assertTrue(Date.weekOfYear(gd3) == 13);
        assertTrue(Date.weekOfYear(gd4) == 13);
        assertTrue(Date.weekOfYear(gd5) == 17);

        //test hourOfDay
        assertTrue(Date.hourOfDay(gd) == 13);
        assertTrue(Date.hourOfDay(gd2) == 4);
        assertTrue(Date.hourOfDay(gd3) == 5);
        assertTrue(Date.hourOfDay(gd4) == 21);
        assertTrue(Date.hourOfDay(gd5) == 21);

        //test minuteOfHour
        assertTrue(Date.minuteOfHour(gd) == 14);
        assertTrue(Date.minuteOfHour(gd2) == 7);
        assertTrue(Date.minuteOfHour(gd3) == 7);
        assertTrue(Date.minuteOfHour(gd4) == 55);
        assertTrue(Date.minuteOfHour(gd5) == 55);


        //test secondOfMinute  ***
        assertTrue(Date.secondOfMinute(gd) == 55);
        assertTrue(Date.secondOfMinute(gd2) == 3);
        assertTrue(Date.secondOfMinute(gd3) == 3);
        assertTrue(Date.secondOfMinute(gd4) == 0);
        assertTrue(Date.secondOfMinute(gd5) == 0);
    }



    /**
     * Test daysInBetween
     */
    public void testDaysInBetween()
    {
        assertTrue(Date.daysInBetween(gd, gd5) == 10997);  //very far apart
        assertTrue(Date.daysInBetween(gd2, gd3) == 0); //same day
        assertTrue(Date.daysInBetween(gd2, gd5) == 33); //same year
        assertTrue(Date.daysInBetween(gd5, gd2) == 33); //fromDate is earlier that toDate
    }


    /**
     * Test timeIntervalFromNowUntil
     */
    public void testTimeIntervalFromNowUntil()
    {
        // since now is in Milliseconds and will change everytime the tests are run it is difficult to write exact tests for this method, hence the use of a tiny delta.
        NSTimestamp psuedoNow = new NSTimestamp();
        long tinyDelta = 1000;  // one second should be even more accurate then this
        assertTrue("psuedoNow and now are very close together", Date.timeIntervalFromNowUntil(psuedoNow) < tinyDelta);
        NSTimestamp tomorrow = Date.dateByAddingDays(psuedoNow, 1);
        assertTrue("tommorrow is just under one days worth of milliseconds away", Date.timeIntervalFromNowUntil(tomorrow) < (Date.millisecondsInADay + tinyDelta));
        NSTimestamp yesterday = Date.dateByAddingDays(psuedoNow, -1);
        assertTrue("yesterday is just a little over one days worth of milliseconds away", Date.timeIntervalFromNowUntil(yesterday) < (-1 * (Date.millisecondsInADay - tinyDelta))); 
    }



    /**
     * Test accurateToSecond and now
     */
    public void testSecondAccuracy()
    {
        NSTimestamp timeWithMillis = new NSTimestamp(1044764184036L);
        NSTimestamp timeWithoutMillis = Date.accurateToSecond(timeWithMillis);
        assertTrue("timeWithoutMillis on or beore timeWitMillis", timeWithoutMillis.getTime() <= timeWithMillis.getTime());

        NSTimestamp timeWithZeroMillis = new NSTimestamp(1044764184000L);
        timeWithoutMillis = Date.accurateToSecond(timeWithMillis);
        assertTrue("times equal when no millis", timeWithoutMillis.equals(timeWithZeroMillis));
        assertTrue("times equal when no millis by long", timeWithoutMillis.getTime() <= timeWithMillis.getTime());

        NSTimestamp nowWithMillis = new NSTimestamp();
        NSTimestamp nowWithoutMillis = Date.now();
        // This might fail if test run right on boundry.
        assertTrue("nowWithoutMillis on or beore nowWithMillis", nowWithoutMillis.getTime() <= nowWithMillis.getTime());
    }


    
    /**
     * Test accurateToSecond and now
     */
    public void testDayAccuracy()
    {
        NSTimestamp dateWithTime = new NSTimestamp(1044764184036L);
        NSTimestamp dateWithoutTime = Date.accurateToDay(dateWithTime);
        assertTrue("dateWithoutTime on or beore dateWithTime", dateWithoutTime.getTime() <= dateWithTime.getTime());

        NSTimestamp today = Date.currentDay();
        NSTimestamp todayAccurateToDay = Date.accurateToDay(today);
        assertEquals("current day does not equal accurateToDay", today, todayAccurateToDay);

        assertTrue(Date.hourOfDay(today) == 0);
        assertTrue(Date.minuteOfHour(today) == 0);
        assertTrue(Date.secondOfMinute(today) == 0);
    }


    
    /**
     * Test isValid24HourTime 
     */
    public void testIsValid24HourTime()
    {
        assertFalse("rejects null", Date.isValid24HourTime(null));
        assertFalse("rejects bad seperator", Date.isValid24HourTime("12;30"));
        assertFalse("rejects misplaced seperator", Date.isValid24HourTime("123:0"));
        assertFalse("rejects too short", Date.isValid24HourTime("12:3"));
        assertFalse("rejects too long", Date.isValid24HourTime("12:309"));
        assertFalse("rejects bad hours", Date.isValid24HourTime(" 2:30"));
        assertFalse("rejects bad minutes", Date.isValid24HourTime("12:5"));
        assertFalse("rejects invalid hours", Date.isValid24HourTime("24:30"));
        assertFalse("rejects invalid minutes", Date.isValid24HourTime("12:65"));
        
        assertTrue("good test A", Date.isValid24HourTime("00:00"));
        assertTrue("good test B", Date.isValid24HourTime("23:59"));
        assertTrue("good test C", Date.isValid24HourTime("12:30"));
        assertTrue("good test D", Date.isValid24HourTime("02:00"));
        assertTrue("good test E", Date.isValid24HourTime("12:00"));
    }
    
}
