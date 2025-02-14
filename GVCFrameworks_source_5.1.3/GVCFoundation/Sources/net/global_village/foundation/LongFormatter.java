package net.global_village.foundation;

import java.text.*;


/**
 * This class simply extends (renames) <code>java.text.DecimalFormat</code> as documentation that it 
 * returns <code>java.lang.Long</code>.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LongFormatter extends DecimalFormat
{

    /** Convenience instance.  **/
    public static final LongFormatter Formatter = new LongFormatter();

    /** Formats output without any commas. */
    public static final LongFormatter NoCommaFormatter = new LongFormatter("0");

    
    
    /**
     * @see DecimalFormat#DecimalFormat()
     */
    public LongFormatter()
    {
        super();
    }

    
    
    /**
     * @see DecimalFormat#DecimalFormat(String, DecimalFormatSymbols)
     */
    public LongFormatter(String pattern, DecimalFormatSymbols symbols)
    {
        super(pattern, symbols);
    }

    
    
    /**
     * @see DecimalFormat#DecimalFormat(String)
     */
    public LongFormatter(String pattern)
    {
        super(pattern);
    }
    
    
}
