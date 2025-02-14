package net.global_village.foundation;

import java.text.*;


/**
 * A string formatter that checks that what it processes falls withing a minimum and maximum length.  
 * It does no other formatting.  Useful for validating input data.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LengthCheckFormatter extends Format
{
    
   
    protected long maxLength;
    protected long minLength;    


    /**
     * Designated constructor.
     * 
     * @param minSize the minimum length for he String
     * @param maxSize the maximum length for the String
     */
    public LengthCheckFormatter(long minSize, long maxSize)
    {
        super();
        /** require [valid_min] minSize >= 0;  [valid_max] maxSize >= minSize;  **/
        minLength = minSize;
        maxLength = maxSize;
    }
    


    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition)
    {
        stringBuffer.append(object);
        
        return stringBuffer;
    }



    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    public Object parseObject(String string, ParsePosition position)
    {
        if (string.length() < minLength || string.length() > maxLength)
        {
            return null;
        }

        position.setIndex(string.length());
        return string;
    }
    
    
    
    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String)
     */
    public Object parseObject(String string) throws ParseException
    {
        if (string.length() < minLength)
        {
           throw new ParseException("Less than " + minLength + " characters", 0);
        }
        
        if (string.length() > maxLength)
        {
            throw new ParseException("Longer than " + maxLength + " characters", 0);
        }
        
        return string;
    }

}
