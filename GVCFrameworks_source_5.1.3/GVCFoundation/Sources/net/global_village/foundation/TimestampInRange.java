package net.global_village.foundation;

import net.global_village.foundation.Date;
import java.util.TimeZone;

import com.webobjects.foundation.NSTimestamp;

/**
 * <p><code>NSTimestamp</code> that can also return a beginning and ending 
 * timestamp for a range containing this timestamp with an accuracy of days or 
 * minutes.  For example, if a <code>TimestampInRange</code> is created for 
 * Oct 24, 2003 at 4:38 PM and the accuracy is set to days, then the beginning 
 * and end of range are Oct 24, 2003 at 00:00:00.000 AM and Oct 24, 2003 at 
 * 23:59:59.999 PM.  If the accuracy is set to minutes, then the beginning 
 * and end of range are Oct 24, 2003 at 4:38:00.000 AM and Oct 24, 2003 at 
 * 4:38:59.999 PM.</p>
 * <p>This class was create for use with the OptionalTimeFormatter when using 
 * the resulting timestamp in time comparisons so that the comparison accuracy 
 * would be relative to the accuracy of the data entered.</p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TimestampInRange extends NSTimestamp
{
    public static final int DAY_ACCURACY = 1;
    public static final int MINUTE_ACCURACY = 2;
    
    
    protected int accuracy;
    
    
    public TimestampInRange()
    {
        super();
    }

    public TimestampInRange(long arg0)
    {
        super(arg0);
    }

    public TimestampInRange(long arg0, int arg1)
    {
        super(arg0, arg1);
    }

    public TimestampInRange(long arg0, NSTimestamp arg1)
    {
        super(arg0, arg1);
    }

    public TimestampInRange(long arg0, TimeZone arg1)
    {
        super(arg0, arg1);
    }

    public TimestampInRange(long arg0, int arg1, TimeZone arg2)
    {
        super(arg0, arg1, arg2);
    }

    public TimestampInRange(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, TimeZone arg6)
    {
        super(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    public TimestampInRange(java.util.Date arg0)
    {
        super(arg0);
    }

    public TimestampInRange(java.sql.Timestamp arg0)
    {
        super(arg0);
    }



    /**
     * Returns the accuracy range of this timestamp.  Timestamps are accurate 
     * either to the day or to the minute.    
     * 
     * @return the accuracy range of this timestamp
     */
    public int accuracy()
    {
        return accuracy;
        /** ensure [valid_result] (Result == DAY_ACCURACY) || 
                                  (Result == MINUTE_ACCURACY);  **/
    }



    /**
     * Sets the accuracy range for this timestamp.  Timestamps are accurate 
     * either to the day or to the minute.    
     * 
     * @param value the accuracy range for this timestamp
     */
    public void setAccuracy(int value)
    {
        /** require [valid_value] (value == DAY_ACCURACY) || 
                                  (value == MINUTE_ACCURACY);  **/
        accuracy = value;
    }



    /**
     * Returns the beginning of the range containing this timestamp, accurate
     * according to <code>accuracy()</code>.
     *  
     * @return the beginning of the range containing this timestamp
     */
    public NSTimestamp beginningOfRange()
    {
        NSTimestamp beginningOfRange = null;
        
        if (accuracy() == DAY_ACCURACY)
        {
            beginningOfRange = Date.beginningOfDayInDefaultTimeZone(this);
        }
        else
        {
            beginningOfRange = Date.beginningOfMinuteInDefaultTimeZone(this);
        }
        
        return beginningOfRange;
    }



    /**
     * Returns the end of the range containing this timestamp, accurate 
     * according to <code>accuracy()</code>.
     *  
     * @return the end of the range containing this timestamp
     */
    public NSTimestamp endOfRange()
    {
        NSTimestamp endOfRange = null;
        
        if (accuracy() == DAY_ACCURACY)
        {
            endOfRange = Date.endOfDayInDefaultTimeZone(this);
        }
        else
        {
            endOfRange = Date.endOfMinuteInDefaultTimeZone(this);
        }
        
        return endOfRange;
    }
}
