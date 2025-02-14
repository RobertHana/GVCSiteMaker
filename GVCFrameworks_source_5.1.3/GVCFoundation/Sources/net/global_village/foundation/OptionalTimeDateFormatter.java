package net.global_village.foundation;

import java.text.DateFormatSymbols;
import java.text.ParseException;

import com.webobjects.foundation.NSTimestamp;


/**
 * <p>A sub-class of StrictDateFormatter that allows for the time component to be 
 * optional.  A default time is used when the time is not supplied.  If provided,
 * the time must be fully specified according to the input format. (e.g. 4:15 AM 
 * not just 4:15).  Some examples (default is 12:00 PM):</p>
 * <ul>
 * <li>1952-12-12 --> 1952-12-12 12:00 PM</li>
 * <li>1952-12-12 4:15 AM --> 1952-12-12 4:15 AM</li>
 * </ul>
 * <p>The restrictions on the usage of StrictDateFormatter formatter also apply 
 * here.</p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class OptionalTimeDateFormatter extends StrictDateFormatter
{
    public static final String DEFAULT_TIME = "12:00 PM";
    protected String defaultTimeString = null;


    /**
     * Overridden constructor.
     * 
     * @param pattern pattern for parsing and formatting time from java.text.SimpleDateFormat
     * is not explictly given 

     */
    public OptionalTimeDateFormatter(String pattern)
    {
            this(pattern, DEFAULT_TIME);
        /** require [valid_pattern]  pattern != null;  **/
    }



    /**
     * Overridden constructor.
     * 
     * @param pattern pattern for parsing and formatting time from java.text.SimpleDateFormat
     * @param newDefaultTimeString - the time, as a String, to use when a time 
     * is not explictly given 
     */
    public OptionalTimeDateFormatter(String pattern, String newDefaultTimeString)
    {
        super(pattern);
        /** require [valid_param] pattern != null;  
                    [valid_param]  newDefaultTimeString != null;  **/
        defaultTimeString = newDefaultTimeString;
    }


    
    /**
     * Overridden constructor.
     *
     * @param pattern pattern for parsing and formatting time from java.text.SimpleDateFormat
     * @param symbols DateFormatSymbols for formatting
     * @param newDefaultTimeString - the time, as a String, to use when a time 
     * is not explictly given 
     */
    public OptionalTimeDateFormatter(String pattern, 
                                     DateFormatSymbols symbols,
                                     String newDefaultTimeString)
    {
        super(pattern, symbols);
        /** require [valid_pattern] pattern != null;  
                    [valid_symbols] symbols != null;
                    [valid_time_string]  newDefaultTimeString != null;  **/
        defaultTimeString = newDefaultTimeString;
    }


 
    /**
     * Overriden to supply time if parsing fails.  Returns instance of 
     * TimestampInRange.
     * 
     * @see com.webobjects.foundation.NSTimestampFormatter#parseObjectInUTC(java.lang.String, java.text.ParsePosition)
     */
     public Object parseObject(String text) throws ParseException
    {
        Object parsedValue;
        int accuracy;        
        try
        {
            parsedValue = super.parseObject(text);
            accuracy = TimestampInRange.MINUTE_ACCURACY;
        }
        catch (ParseException e)
        {
            parsedValue = super.parseObject(text + " " + defaultTimeString()); 
            accuracy = TimestampInRange.DAY_ACCURACY;
        }

        parsedValue = new TimestampInRange((NSTimestamp)parsedValue);
        ((TimestampInRange)parsedValue).setAccuracy(accuracy);
 
        return parsedValue;
    }

    
    
    /**
     * Sets the time to use when a time is not explictly given in the String
     * to be parsed into an NSTimestamp.  It is the callers responsibility to 
     * ensure that this is in the correct format WRT the formatting pattern.
     *
     * @param newDefaultTimeString - the time, as a String, to use when a time 
     * is not explictly given 
     */
     public synchronized void setDefaultTimeString(String newDefaultTimeString)
    {
        /** require [valid_param]  newDefaultTimeString != null;  **/
        defaultTimeString = newDefaultTimeString;
    }
    


    /**
     * Returns the time to use when a time is not explictly given in the String
     * to be parsed into an NSTimestamp.
     *
     * @return the time to use when a time is not explictly given in the String
     * to be parsed into an NSTimestamp.
     */
    public synchronized String defaultTimeString()
    {
        return defaultTimeString;
        /** ensure [valid_result]  Result != null;  **/
    }


    /** invariant [defaultTimeString_set]  defaultTimeString != null;  **/

}
