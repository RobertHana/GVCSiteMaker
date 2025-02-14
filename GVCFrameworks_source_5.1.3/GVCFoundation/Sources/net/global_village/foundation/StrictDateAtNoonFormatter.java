package net.global_village.foundation;

import java.text.*;

import com.webobjects.foundation.*;



/**
 * Same functionality as StrictDateFormatter, but this one ignores the time and forces it to noon.
 * This is intended to be used with date only formats where there is no time given.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class StrictDateAtNoonFormatter extends StrictDateFormatter
{

    /**
     * @see SimpleDateFormat#SimpleDateFormat()
     */
    public StrictDateAtNoonFormatter()
    {
    }



    /**
     * @see SimpleDateFormat#SimpleDateFormat(String)
     */
    public StrictDateAtNoonFormatter(String pattern)
    {
        super(pattern);
    }



    /**
     * @see SimpleDateFormat#SimpleDateFormat(String,DateFormatSymbols)
     */
    public StrictDateAtNoonFormatter(String pattern, DateFormatSymbols symbols)
    {
        super(pattern, symbols);
    }

    
    
    /**
     * Parses text to produce an NSTimestamp object with the time set to noon
     *
     * @see Date#noon(NSTimestamp)
     * 
     * @param text - the String to parse
     * @return - NSTimestamp object with the time set to noon parsed from text, or null if there was no text to parse
     * @exception ParseException - if the specified string contains an invalid date
     */
    public Object parseObject(String text) throws ParseException
    {
        NSTimestamp result = (NSTimestamp)super.parseObject(text);
        return Date.noon(result);
    }
    
    
}
