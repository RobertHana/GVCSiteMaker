package net.global_village.foundation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;


/**
 * Date utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 13$
 */
public class Date
{
    public static final long millisecondsInADay = 86400000;

    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private Date()
    {
        super();
    }



    /**
     * Returns a <code>TimeZone</code> constructed from an <code>NSTimeZone</code>.  
     *
     * @param aTimeZone the <code>TimeZone</code> to use.
     * @return the constructed <code>NSTimeZone</code>.
     */
    public static TimeZone nsTimeZoneToTimeZone(NSTimeZone aTimeZone)
    {
        /** require [valid_param] aTimeZone != null; **/

        return TimeZone.getTimeZone(aTimeZone.getID());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimeZone</code> constructed from a <code>TimeZone</code>.
     *
     * @param aTimeZone the <code>TimeZone</code> to use.
     * @return the constructed <code>NSTimeZone</code>.
     */
    public static NSTimeZone timeZoneToNSTimeZone(TimeZone aTimeZone)
    {
        /** require [valid_param] aTimeZone != null; **/

        return NSTimeZone.timeZoneWithName(aTimeZone.getID(), true);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>GregorianCalendar</code> constructed from an <code>NSTimestamp</code>.
     *
     * @param aDate the <code>NSTimestamp</code> to use.
     * @return the constructed <code>GregorianCalendar</code>.
     */
    public static GregorianCalendar nsTimestampToGregorianCalendar(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar cal = new GregorianCalendar();
        cal.clear();
        cal.setTime(aDate);

        JassAdditions.post("Date", "nsTimestampToGregorianCalendar", cal.getTime().getTime() == aDate.getTime());
        return cal;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a NSGregoriandate constructed from an GregorianCalendar.
     *
     * @param aDate the <code>GregorianCalendar</code> to use.
     * @return the constructed <code>NSTimestamp</code>.
     */
    public static NSTimestamp gregorianCalendarToNSTimestamp(GregorianCalendar aDate)
    {
        /** require [valid_param] aDate != null; **/

        NSTimestamp ts = new NSTimestamp(aDate.getTime());
        JassAdditions.post("Date", "nsTimestampToGregorianCalendar", ts.getTime() == aDate.getTime().getTime());
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>aDate</code> accurate to the second.  In other words, the milliseconds portion of <code>aDate</code> is set to zero.  This is very useful for date/times written to an SQL database that is only accurate to the second.  The difference between the time sent to the database and the time retrieved can cause problems with EOF.  Specifically if the snapshot timeout expires and the object is refetched when its fault is fired during EOEditingContext _processRecentChanges(), the difference causes an EOObjectsChangedInStoreNotification to be broadcast.  This somehow messes up the internal state of the EOEditingContext in the context of  _processRecentChanges().  In one case it caused a deleted object to suddenly become an inserted object.  While this problem is rare, it can be extremely difficult to track down.
     *
     * @param aDate the <code>NSTimestamp</code> to use.
     * @return <code>aDate</code> accurate to the second.
     */
    public static NSTimestamp accurateToSecond(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(aDate);
        gc.set(GregorianCalendar.MILLISECOND, 0);

        JassAdditions.post("Date", "accurateToSecond", gc.getTime().getTime() <= aDate.getTime());
        return gregorianCalendarToNSTimestamp(gc);
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns <code>aDate</code> accurate to the day.  In other words, the time portion
     * of <code>aDate</code> is set to zero.  This is very useful for date/times written
     * to an SQL date type.
     *
     * @param aDate the <code>NSTimestamp</code> to use.
     * @return <code>aDate</code> accurate to the second.
     */
    public static NSTimestamp accurateToDay(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(aDate);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        JassAdditions.post("Date", "accurateToDay", gc.getTime().getTime() <= aDate.getTime());
        return gregorianCalendarToNSTimestamp(gc);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the current date and time accurate to the second.  See accurateToSecond(NSTimestamp) for details.
     *
     * @return the current date and time accurate to the second.  
     */
    public static NSTimestamp now()
    {
        return accurateToSecond(new NSTimestamp());
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the current date with the time set to zero.  See accurateToDay(NSTimestamp) for details.
     *
     * @return the current date
     */
    public static NSTimestamp currentDay()
    {
        return accurateToDay(new NSTimestamp());
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same year, month and day as <code>aDate</code>, but with the hours set to 12:00:00.000 noon in the default timezone for the passed in date.  This could be used to avoid issues when comparing dates that have daylight savings time changes between them, or when adjusting a date over a DST boundary.
     *
     * @param aDate the date to use.
     * @return the <code>NSTimestamp</code> with its time set to 12 noon in the passed in date's timezone.
     */
    public static NSTimestamp noon(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 12);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        NSTimestamp ts = gregorianCalendarToNSTimestamp(gc);

        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.YEAR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.YEAR));
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MONTH));
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.DAY_OF_MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.DAY_OF_MONTH));
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR_OF_DAY) == 12);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == 0);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == 0);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == 0);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(nsTimestampToGregorianCalendar(aDate).getTimeZone()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same year, month, and day as <code>aDate</code>, but with the hours set to 12:00:00.000 noon in the default timezone.  This could be used to avoid issues when comparing dates that have daylight savings time changes between them, or when adjusting a date over a DST boundary.
     * NOTE: If your machine _ever_ changes time zones (and that includes daylight savings time!), then this method will NOT return the same NSTimestamp as it did before.  This is a bad, bad, bad function.  You should probably be using noon() instead.
     *
     * @param aDate the date to use.
     * @return the <code>NSTimestamp</code> with its time set to 12 noon in the default timezone.
     */
    public static NSTimestamp noonInDefaultTimeZone(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 12);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        gc.setTimeZone(TimeZone.getDefault());
        NSTimestamp ts = gregorianCalendarToNSTimestamp(gc);

        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.YEAR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.YEAR));
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MONTH));
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.DAY_OF_MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.DAY_OF_MONTH));
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR_OF_DAY) == 12);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == 0);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == 0);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == 0);
        JassAdditions.post("Date", "noonInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(TimeZone.getDefault()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same year, month, and day as <code>aDate</code>, but with the time set to 00:00:00.000 in the default timezone.
     *
     * @param aDate the date to use.
     * @return the <code>NSTimestamp</code> with its time set to 00:00:00 in the default timezone.
     */
    public static NSTimestamp beginningOfDayInDefaultTimeZone(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        gc.setTimeZone(TimeZone.getDefault());
        NSTimestamp ts = gregorianCalendarToNSTimestamp(gc);

        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.YEAR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.YEAR));
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MONTH));
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.DAY_OF_MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.DAY_OF_MONTH));
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR_OF_DAY) == 0);
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == 0);
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == 0);
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == 0);
        JassAdditions.post("Date", "beginningOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(TimeZone.getDefault()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same year, month, and day as <code>aDate</code>, but with the time set to 23:59:59.999 in the default timezone.
     *
     * @param aDate the date to use.
     * @return the <code>NSTimestamp</code> with its time set to 23:59:59.999 in the default timezone.
     */
    public static NSTimestamp endOfDayInDefaultTimeZone(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
        gc.set(GregorianCalendar.MINUTE, 59);
        gc.set(GregorianCalendar.SECOND, 59);
        gc.set(GregorianCalendar.MILLISECOND, 999);
        gc.setTimeZone(TimeZone.getDefault());
        NSTimestamp ts = gregorianCalendarToNSTimestamp(gc);

        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.YEAR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.YEAR));
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MONTH));
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.DAY_OF_MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.DAY_OF_MONTH));
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR_OF_DAY) == 23);
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == 59);
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == 59);
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == 999);
        JassAdditions.post("Date", "endOfDayInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(TimeZone.getDefault()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same year, month, day, hour 
     * and minute as <code>aDate</code>, but with the seconds and milliseconds
     * set to 00.000 in the default timezone.
     *
     * @param aDate the date to use.
     * @return the <code>NSTimestamp</code> with its milliseconds set to 00.000 
     * in the default timezone
     */
    public static NSTimestamp beginningOfMinuteInDefaultTimeZone(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        gc.setTimeZone(TimeZone.getDefault());
        return gregorianCalendarToNSTimestamp(gc);

        /** ensure [valid_result] Result != null; 
                   [year_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.YEAR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.YEAR);
                   [month_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MONTH);
                   [day_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.DAY_OF_MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.DAY_OF_MONTH);
                   [hour_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.HOUR_OF_DAY) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.HOUR_OF_DAY);
                   [minute_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.MINUTE) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MINUTE);
                   [second_zero] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.SECOND) == 0;
                   [millisecond_zero] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.MILLISECOND) == 0;
                   [timezone_default] nsTimestampToGregorianCalendar(Result).getTimeZone().equals(TimeZone.getDefault());
         **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same year, month, day, hour 
     * and minute as <code>aDate</code>, but with the seconds and milliseconds
     * set to 59.999 in the default timezone.
     *
     * @param aDate the date to use.
     * @return the <code>NSTimestamp</code> with its milliseconds set to 59.999 
     * in the default timezone
     */
    public static NSTimestamp endOfMinuteInDefaultTimeZone(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        gc.set(GregorianCalendar.SECOND, 59);
        gc.set(GregorianCalendar.MILLISECOND, 999);
        gc.setTimeZone(TimeZone.getDefault());
        
        return gregorianCalendarToNSTimestamp(gc);

        /** ensure [valid_result] Result != null; 
                   [year_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.YEAR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.YEAR);
                   [month_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MONTH);
                   [day_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.DAY_OF_MONTH) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.DAY_OF_MONTH);
                   [hour_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.HOUR_OF_DAY) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.HOUR_OF_DAY);
                   [minute_same] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.MINUTE) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MINUTE);
                   [second_59] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.SECOND) == 59;
                   [millisecond_999] nsTimestampToGregorianCalendar(Result).get(GregorianCalendar.MILLISECOND) == 999;
                   [timezone_default] nsTimestampToGregorianCalendar(Result).getTimeZone().equals(TimeZone.getDefault());
         **/
    }



    /**
     * Returns an <code>NSTimestamp</code> with the same hours, minutes, seconds and milliseconds as <code>aTime</code>, but with the date reset to a constant and with the timezone reset to the default timezone.  This can be used to make time-only comparisons.<p>
     *
     * Note that the year 2000 is used to avoid any possible problems with the switch to a Gregorian calendar that occured in 1582 (and caused a discontinuity in the dates), but that, while the date will remain constant over multiple calls to this method, the actual constant should not be relied on (as implied by the contract).
     *
     * @param aTime the time to use.
     * @return the <code>NSTimestamp</code>
     */
    public static NSTimestamp constantDateInDefaultTimeZone(NSTimestamp aTime)
    {
        /** require [valid_param] aTime != null; **/

        final int privateYear = 2000;
        final int privateMonth = 1;
        final int privateDay = 1;

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aTime);
        gc.set(privateYear, privateMonth, privateDay);
        gc.setTimeZone(TimeZone.getDefault());
        NSTimestamp ts = gregorianCalendarToNSTimestamp(gc);

        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.YEAR) == privateYear);
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MONTH) == privateMonth);
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.DAY_OF_MONTH) == privateDay);
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR_OF_DAY) == nsTimestampToGregorianCalendar(aTime).get(GregorianCalendar.HOUR_OF_DAY));
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == nsTimestampToGregorianCalendar(aTime).get(GregorianCalendar.MINUTE));
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == nsTimestampToGregorianCalendar(aTime).get(GregorianCalendar.SECOND));
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == nsTimestampToGregorianCalendar(aTime).get(GregorianCalendar.MILLISECOND));
        JassAdditions.post("Date", "constantDateInDefaultTimeZone", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(TimeZone.getDefault()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a date offset by <code>offsetInDays</code> days.
     *
     * @param aDate the date to use.
     * @return a date offset by <code>offsetInDays</code> days.
     */
    public static NSTimestamp dateByAddingDays(NSTimestamp aDate, int offsetInDays)
    {
        /** require [valid_param] aDate != null; **/

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(aDate);
        cal.add(Calendar.DAY_OF_MONTH, offsetInDays);
        NSTimestamp ts = new NSTimestamp(cal.getTime());

        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.HOUR));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MINUTE));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.SECOND));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MILLISECOND));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(nsTimestampToGregorianCalendar(aDate).getTimeZone()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the day after <code>aDate</code>.
     *
     * @param aDate the date to use.
     * @return the day after <code>aDate</code>.
     */
    public static NSTimestamp tomorrow(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        NSTimestamp ts = dateByAddingDays(aDate, 1);

        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.HOUR));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MINUTE));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.SECOND));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MILLISECOND));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(nsTimestampToGregorianCalendar(aDate).getTimeZone()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the day after <code>aDate</code>.
     *
     * @param aDate the date to use.
     * @return the day after <code>aDate</code>.
     */
    public static NSTimestamp yesterday(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        NSTimestamp ts = dateByAddingDays(aDate, -1);

        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.HOUR) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.HOUR));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MINUTE) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MINUTE));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.SECOND) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.SECOND));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).get(GregorianCalendar.MILLISECOND) == nsTimestampToGregorianCalendar(aDate).get(GregorianCalendar.MILLISECOND));
        JassAdditions.post("Date", "dateByAddingDays", nsTimestampToGregorianCalendar(ts).getTimeZone().equals(nsTimestampToGregorianCalendar(aDate).getTimeZone()));
        return ts;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this date is a weekend day (Saturday or Sunday), <code>false</code> otherwise.
     *
     * @param aDate the date to use.
     * @return <code>true</code> if this date is a weekend day (Saturday or Sunday), <code>false</code> otherwise.
     */
    public static boolean isWeekend(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        int dayOfWeek = gc.get(GregorianCalendar.DAY_OF_WEEK);
        return (dayOfWeek == GregorianCalendar.SATURDAY) || (dayOfWeek == GregorianCalendar.SUNDAY);
    }



    /**
     * Returns <code>true</code> if this date is a weekday (not Saturday or Sunday), <code>false</code> otherwise.
     *
     * @param aDate the date to use.
     * @return <code>true</code> if this date is a weekday (not Saturday or Sunday), <code>false</code> otherwise.
     */
    public static boolean isWeekday(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        return ! Date.isWeekend(aDate);
    }



    /**
     * Compares <code>aDate</code> with <code>anotherDate</code> by day only, ignoring time.
     *
     * @param aDate the date to compare.
     * @param anotherDate the date to compare against.
     * @return <code>NSComparator.OrderedSame</code> if <code>aDate</code> is the same day as <code>anotherDate</code>, <code>NSComparator.OrderedDescending</code> if it is after, or <code>NSComparator.OrderedAscending</code> if it is before.
     */
    public static int compareToByDay(NSTimestamp aDate, NSTimestamp anotherDate)
    {
        /** require
        [valid_aDate_param] aDate != null;
        [valid_anotherDate_param] anotherDate != null; **/

        return noonInDefaultTimeZone(aDate).compare(noonInDefaultTimeZone(anotherDate));

        /** ensure [valid_result] (Result == NSComparator.OrderedSame) || (Result == NSComparator.OrderedDescending) || (Result == NSComparator.OrderedAscending); **/
    }



    /**
     * Returns <code>true</code> if the day portion of <code>aDate</code> is the same as the day portion of <code>anotherDate</code>.
     *
     * @param aDate the date to compare.
     * @param anotherDate the date to compare against.
     * @return <code>true</code> if the day portion of <code>aDate</code> is the same as the day portion of <code>anotherDate</code>.
     */
    public static boolean equalsByDay(NSTimestamp aDate, NSTimestamp anotherDate)
    {
        /** require
        [valid_aDate_param] aDate != null;
        [valid_anotherDate_param] anotherDate != null; **/

        return compareToByDay(aDate, anotherDate) == NSComparator.OrderedSame;
    }



    /**
     * Returns <code>true</code> if the day portion of <code>aDate</code> is between the day portion of <code>fromDate</code> and <code>toDate</code>, inclusive.
     *
     * @param aDate the date to compare for being between the other two arguments
     * @param fromDate the date from which to compare
     * @param toDate the date to which to compare
     * @return <code>true</code> if the day portion of <code>aDate</code> is between the day portion of <code>fromDate</code> and <code>toDate</code>, inclusive
     */
    public static boolean isBetweenDatesByDay(NSTimestamp aDate,
                                              NSTimestamp fromDate,
                                              NSTimestamp toDate)
    {
        /** require
        [valid_aDate_param] aDate != null;
        [valid_fromDate_param] fromDate != null;
        [valid_toDate_param] toDate != null; **/

        return ((compareToByDay(aDate, fromDate) == NSComparator.OrderedSame) || (compareToByDay(aDate, fromDate) == NSComparator.OrderedDescending)) && ((compareToByDay(aDate, toDate) == NSComparator.OrderedSame) || (compareToByDay(aDate, toDate) == NSComparator.OrderedAscending));

        /** ensure [valid_result] (compareToByDay(fromDate, toDate) == NSComparator.OrderedSame) || (compareToByDay(fromDate, toDate) == NSComparator.OrderedAscending); **/
    }



    /**
     * Returns <code>true</code> if this date is in the past, <code>false</code> otherwise.
     *
     * @param aDate the date to use.
     * @return <code>true</code> if this date is in the past, <code>false</code> otherwise.
     */
    public static boolean isInPastByDay(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        return compareToByDay(aDate, new NSTimestamp()) == NSComparator.OrderedAscending;
    }



    /**
     * Returns <code>true</code> if this date is the same as the current day, <code>false</code> otherwise.
     *
     * @param aDate the date to use.
     * @return <code>true</code> if this date is the same as the current day, <code>false</code> otherwise.
     */
    public static boolean isTodayByDay(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        return equalsByDay(aDate, new NSTimestamp());        	
    }

    

    /**
     * Returns <code>true</code> if this date is in the future, <code>false</code> otherwise.
     *
     * @param aDate the date to use
     * @return <code>true</code> if this date is in the future, <code>false</code> otherwise.
     */
    public static boolean isInFutureByDay(NSTimestamp aDate)
    {
        /** require [valid_param] aDate != null; **/

        return Date.compareToByDay(aDate, new NSTimestamp()) > 0;
    }



    /**
     * Compares <code>aTime</code> with <code>anotherTime</code> by time only, ignoring the date and the timezone.
     *
     * @param aTime the time to compare.
     * @param anotherTime the time to compare against.
     * @return <code>NSComparator.OrderedSame</code> if <code>aDate</code> is the same day as <code>anotherDate</code>, <code>NSComparator.OrderedDescending</code> if it is after, or <code>NSComparator.OrderedAscending</code> if it is before.
     */
    public static int compareToByTime(NSTimestamp aTime, NSTimestamp anotherTime)
    {
        /** require
        [valid_aTime_param] aTime != null;
        [valid_anotherTime_parma] anotherTime != null; **/

        return constantDateInDefaultTimeZone(aTime).compare(constantDateInDefaultTimeZone(anotherTime));

        /** ensure [valid_result] (Result == NSComparator.OrderedSame) || (Result == NSComparator.OrderedDescending) || (Result == NSComparator.OrderedAscending); **/
    }



    /**
     * Returns the number of occurences of a given week day in the time interval, inclusive.
     *
     * @param dayOfWeek the day of the week to count
     * @param fromDate the date from which to count
     * @param toDate the date to which to count
     * @return the number of occurences of a given week day in the time interval, inclusive
     */
    public static int occurancesOfDayOfWeek(int dayOfWeek,
                                            NSTimestamp fromDate,
                                            NSTimestamp toDate)
    {
        /** require
        [valid_dayOfWeek_param] (dayOfWeek == GregorianCalendar.SUNDAY) || (dayOfWeek == GregorianCalendar.MONDAY) || (dayOfWeek == GregorianCalendar.TUESDAY) || (dayOfWeek == GregorianCalendar.WEDNESDAY)  || (dayOfWeek == GregorianCalendar.THURSDAY) || (dayOfWeek == GregorianCalendar.FRIDAY) || (dayOfWeek == GregorianCalendar.SATURDAY);
        [valid_fromDate_param] fromDate != null;
        [valid_toDate_param] toDate != null;
        [fromDate_is_before_toDate] (compareToByDay(fromDate, toDate) == NSComparator.OrderedSame) || (compareToByDay(fromDate, toDate) == NSComparator.OrderedAscending); **/

        // Since we no longer have a way to get a date interval from a common reference point, we're going to have to do this manually!
        GregorianCalendar fromCal = nsTimestampToGregorianCalendar(noonInDefaultTimeZone(fromDate));
        GregorianCalendar toCal = nsTimestampToGregorianCalendar(noonInDefaultTimeZone(toDate));
        int numberOfOccurances = 0;

        while (fromCal.before(toCal) && (fromCal.get(GregorianCalendar.DAY_OF_WEEK) != dayOfWeek))
        {
            fromCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
        }

        while (fromCal.before(toCal))
        {
            numberOfOccurances++;
            fromCal.add(GregorianCalendar.WEEK_OF_YEAR, 1);
        }

        if ((fromCal.get(GregorianCalendar.DAY_OF_WEEK) == dayOfWeek) && fromCal.equals(toCal))
        {
            numberOfOccurances++;
        }

        return numberOfOccurances;
    }



    /**
     * Returns the corresponding value of a given time field based on the passed date. 
     *
     * @param aDate the date to evaluate
     * @param field the time field to get
     * @return the corresponding value of a given time field based on the passed date.
     */
    protected static int getValue(NSTimestamp aDate, int field)
    {
        /** require [valid_date] aDate != null;  **/
        GregorianCalendar gc = nsTimestampToGregorianCalendar(aDate);
        return gc.get(field);
    }


    
    /**
     * Returns the number that indicates the year, including the century, of the passed date (for example, 1995). The base year of the Common Era is 1 C.E. (which is the same as 1 A.D).
     *
     * @param aDate the date to evaluate
     * @return the number that indicates the year, including the century, of the receiver (for example, 1995).
     */
    public static int yearOfCommonEra(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.YEAR);
    }



    /**
     * Returns the number that indicates the day of the year (1 through 366) of the passed date.
     *
     * @param aDate the date to evaluate
     * @return the number that indicates the day of the year (1 through 366) of the passed date.
     */
    public static int dayOfYear(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.DAY_OF_YEAR);
    }




    /**
     * Returns the number that indicates the day of the month (1 through 31) of the passed date.
     *
     * @param aDate the date to evaluate
     * @return the number that indicates the day of the month (1 through 31) of the passed date.
     */
    public static int dayOfMonth(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.DAY_OF_MONTH);
    }



    /**
     * Returns the number that indicates the day of the week (1 through 7) of the passed date; 1 indicates Sunday.
     *
     * @param aDate the date to evaluate
     * @return the number that indicates the day of the week (1 through 7) of the passed date; 1 indicates Sunday.
     */
    public static int dayOfWeek(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.DAY_OF_WEEK);
    }


    
    /**
     * Returns the number that indicates the month of the year (1 through 12) of the passed date.
     *
     * @param aDate the date to evaluate
     * @return the number that indicates the month of the year (1 through 12) of the passed date.
     */
    public static int monthOfYear(NSTimestamp aDate)
    {
        //Need to add 1 since GregorianCalendar.MONTH assigned months from 0 to 11 
        return Date.getValue(aDate, GregorianCalendar.MONTH) + 1;
    }


    
    /**
     * Returns the number that indicates the week number within the current year of the passed date.
     *
     * @param aDate the date to evaluate
     * @return the number that indicates the week number within the current year of the passed date.
     */
    public static int weekOfYear(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.WEEK_OF_YEAR);
    }


    
    /**
     * Returns the hour value (0 through 23) of this NSTimestamp. On Daylight Savings "fall back" days, a value of 1 is returned for two consecutive hours, but with a different time zone (the first in daylight savings time and the second in standard time).
     *
     * @param aDate the date to evaluate
     * @return the hour value (0 through 23) of this NSTimestamp
     */
    public static int hourOfDay(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.HOUR_OF_DAY); 
    }



    /**
     * Returns the minutes value of this NSTimestamp.
     *
     * @param aDate the date to evaluate
     * @return the minutes value (0 through 59) of this NSTimestamp
     */
    public static int minuteOfHour(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.MINUTE);
    }



    /**
     * Returns the seconds value of this NSTimestamp.
     *
     * @param aDate the date to evaluate
     * @return the seconds value (0 through 59) of this NSTimestamp
     */
    public static int secondOfMinute(NSTimestamp aDate)
    {
        return Date.getValue(aDate, GregorianCalendar.SECOND);
    }
    

    
    /**
     * Returns the number of days between two dates.  
     *
     * @param aDate the first date to evaluate
     * @param otherDate the second date to evaluate
     * @return the number of days in between two dates
     */
    public static int daysInBetween(NSTimestamp aDate, NSTimestamp otherDate)
    {
        /** require [valid_start_date] aDate != null; [valid_end_date] otherDate != null;   **/
        Long difference = new Long((long) (otherDate.getTime() - aDate.getTime()) / (1000 * 60 * 60 * 24));

        return java.lang.Math.abs(difference.intValue());
    }


    /**
     * Returns the time interval in milliseconds.  This method can be use to replace the deprecated <code>timeIntervalSinceNow()</code> on NSTimestamp.  Dates in the past should return a negative number. 
     *
     * @param aDate the date to compare with the current time
     * @return the number of milliseconds between now and aDate
     */
    public static long timeIntervalFromNowUntil(NSTimestamp aDate)
    {
        /** require [valid_date] aDate != null;    **/
        long now = System.currentTimeMillis();
        long dateAsMilliseconds = aDate.getTime();
        long interval = dateAsMilliseconds - now;

        return interval;
    }

    
    
    /**
     * Returns <code>true</code> if the passed string is in correct 24 hour time format.  The correct
     * format is HH:MM.  Both HH and MM must be two digits, whitespace is not allowed.  HH must be 
     * between 00 and 23.  MM must be between 00 and 59.
     * 
     * @param timeString the 24 hour time string to check
     * @return <code>true</code> if the passed string is in correct 24 hour time format
     */
    public static boolean isValid24HourTime(String timeString)
    {
        if ((timeString == null) ||
            (timeString.trim().length() != 5) ||
            ( ! timeString.substring(2, 3).equals(":")) ||
            ( ! (Character.isDigit(timeString.charAt(0)) && 
                 Character.isDigit(timeString.charAt(1)) && 
                 Character.isDigit(timeString.charAt(3)) && 
                 Character.isDigit(timeString.charAt(4)))) )
        {
            return false;
        }
        
        int hours = Integer.parseInt(timeString.substring(0, 2));
        int minutes = Integer.parseInt(timeString.substring(3, 5));
        if ((hours < 0) || (hours > 23) || (minutes < 0) || (minutes > 59))
        {
            return false;
        }
        
        return true;
    }

}
