package net.global_village.foundation;

import java.math.*;
import java.text.*;

import com.webobjects.foundation.*;


/**
 * A simple formatter for java.lang.Integer.  This was created as DecimalFormat returns Longs and
 * NSNumberFormatter returns BigDecimal.  This is a very simple formatter and it does not
 * implement java.text.Format#parseObject(java.lang.String, java.text.ParsePosition).
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class IntegerFormatter extends NSNumberFormatter
{

    /** Convenience instance.  **/
    public static final IntegerFormatter Formatter = new IntegerFormatter();


    /** Formats output without any commas. */
    public static final IntegerFormatter NoCommaFormatter = new IntegerFormatter("0");



    /**
     * @see NSNumberFormatter#NSNumberFormatter(String)
     */
    public IntegerFormatter(String aPattern)
    {
        super(aPattern);
        setAllowsFloats(false);
    }



    /**
     * @see NSNumberFormatter#NSNumberFormatter()
     */
    public IntegerFormatter()
    {
        this("#");
    }



    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    public Object parseObject(String string, ParsePosition position)
    {
        Object result = super.parseObject(string, position);
        if (result instanceof BigDecimal)
        {
            result = new Integer(((BigDecimal)result).intValue());
        }
        return result;
    }



    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String)
     */
    public Object parseObject(String source) throws ParseException
    {
        Object result = super.parseObject(source);
        if (result instanceof BigDecimal)
        {
            result = new Integer(((BigDecimal)result).intValue());
        }
        return result;
    }


}
