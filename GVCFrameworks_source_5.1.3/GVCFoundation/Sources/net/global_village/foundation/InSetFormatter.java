package net.global_village.foundation;

import java.text.*;

import com.webobjects.foundation.*;


/**
 * A formatter that parses a value with a supplied formatter and then verifies it is in a set of acceptable values. 
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class InSetFormatter extends Format
{
    
   
    protected NSArray acceptableValues;
    protected Format elementFormatter;    


    /**
     * Designated constructor.
     * 
     * @param values list of acceptable values
     * @param formatter formatter used to parse each element in the array
     */
    public InSetFormatter(NSArray values, Format formatter)
    {
        super();
        /** require [valid_values] values != null;  [valid_formatter] formatter != null;  **/
        acceptableValues = values;
        elementFormatter = formatter;
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
        try
        {
            Object result = parseObject(string);
            position.setIndex(string.length());
            return result;
        }
        catch (ParseException e)
        {
            return null;
        }

    }
    
    
    
    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String)
     */
    public Object parseObject(String string) throws ParseException
    {
        Object parsedObject = elementFormatter.parseObject(string);
        if ( ! acceptableValues.containsObject(parsedObject))
        {
            throw new ParseException(parsedObject + " not in allowed values " + acceptableValues, 0);
        }

        return parsedObject;
    }

}
