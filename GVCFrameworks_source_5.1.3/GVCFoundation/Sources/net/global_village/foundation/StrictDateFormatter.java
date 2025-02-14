package net.global_village.foundation;

import java.text.*;
import java.util.Date;

import com.webobjects.foundation.*;


/**
 * A simple sub-class (replacement) of NSTimestampFormatter that works around bugs in NSTimestampFormatter and makes
 * parsing of date strings strict (see java.text.DateFormat method lenient()). NSTimestampFormatter is lenient in that
 * it accepts months > 12, days > 31 etc. As long as the format somewhat matches the entered text it will produce some
 * sort of date. This is often not desireable for input validation. This class will throw a ParseException if the parsed
 * text does not strictly match the pattern and if any values are not in a normal range. This ParseException will then
 * result in public void validationFailedWithException(Throwable t, Object value, String keyPath) being called if this
 * formatter is used on a WOForm input component. <br>
 * <p>
 * <b>This is implemented using SimpleDateFormat so only the Java format patterns from java.text.SimpleDateFormat are supported.</b>
 * </p>
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 * @version $$
 */
public class StrictDateFormatter extends NSTimestampFormatter
{

    protected DateFormat dateFormatter = null;


    /**
     * Overridden constructor.
     */
    public StrictDateFormatter()
    {
            super();
    }



    /**
     * Overridden constructor.
     */
    public StrictDateFormatter(String pattern)
    {
        super(pattern);
        /** require [valid_param] pattern != null;  **/
    }



    /**
     * Overridden constructor.
     */
    public StrictDateFormatter(String pattern, DateFormatSymbols symbols)
    {
        super(pattern, symbols);
        /** require [valid_param] pattern != null;  [valid_symbols] symbols != null;  **/
    }



    /**
     * Parses text to produce an NSTimestamp object.
     *
     * @param text - the String to parse
     * @return - NSTimestamp object parsed from text, or null if there was no text to parse.
     * @exception ParseException - if the specified string contains an invalid date.
     */
    public Object parseObject(String text) throws ParseException
    {
        Object result = null;

        if ((text != null) && (text.length() > 0))
        {
            try
            {
                Date parsedDate = dateFormatter().parse(text);

                if (parsedDate == null)
                {
                    throw new java.text.ParseException(text, 0);
                }

                result = new NSTimestamp(parsedDate);
            }
            catch (NumberFormatException e)
            {
                throw new java.text.ParseException("Not a numeric value: " + text, 0);
            }
        }

        return result;
    }



    /**
     * Overridden to avoid bug in NSTimestampFormatter with Z format symbol.
     *
     * @see com.webobjects.foundation.NSTimestampFormatter#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(Object valueToBeFormatted, StringBuffer destinationStringBuffer, FieldPosition position)
    {
        return dateFormatter().format(valueToBeFormatted, destinationStringBuffer, position);
    }



    /**
     * Sets this NSNumberFormatter's pattern.  Overidden to clear internal state when the pattern is changed.
     *
     * @param newPattern - the format in which the object is to be formatted
     */
     public synchronized void setPattern(String newPattern)
    {
        super.setPattern(newPattern);
        dateFormatter = null;
        /** ensure [dateFormatter_cleared]  dateFormatter == null;  **/
    }



    /**
     * Returns the DateFormat that this formatter should use to parse dates.
     *
     * @return the DateFormat that this formatter should use to parse dates.
     */
    protected DateFormat dateFormatter()
    {
        if (dateFormatter == null)
        {
            dateFormatter = new SimpleDateFormat(pattern());
            dateFormatter.setLenient(false);
        }

        return dateFormatter;
        /** ensure [valid_result]  Result != null;  **/
    }


}
