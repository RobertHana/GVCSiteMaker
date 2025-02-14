package net.global_village.foundation;

import java.text.*;


/**
 * A simple formatter for java.lang.Boolean that formats into two string values and parses two
 * string values case-insensitively.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class BooleanFormatter extends Format
{
    protected String trueString;
    protected String falseString;
    protected String lowerTrueString;
    protected String lowerFalseString;



    /**
     * Designated constructor.
     *  
     * @param trueValue the true value for parsing and formatting
     * @param falseValue the false value for parsing and formatting
     */
    public BooleanFormatter(String trueValue, String falseValue)
    {
        super();
        /** require [valid_true] trueValue != null;  [valid_false] falseValue != null;   **/
        trueString = trueValue;
        falseString = falseValue;
        lowerTrueString = trueValue.toLowerCase();
        lowerFalseString = falseValue.toLowerCase();    
    }
    
    
    /**
     * Covenience constructor creates formatter for 'true' and 'false'.
     */
    public BooleanFormatter()
    {
        this("true", "false");
    }




    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(Object aBoolean, StringBuffer stringBuffer, FieldPosition fieldPosition)
    {
        stringBuffer.append(((java.lang.Boolean)aBoolean).booleanValue() ? trueString : falseString);
        
        return stringBuffer;
    }



    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    public Object parseObject(String string, ParsePosition position)
    {
        Object parsedObject = null;
        
        String targetString = string.substring(position.getIndex()).toLowerCase();
        
        if (targetString.startsWith(lowerTrueString))
        {
            parsedObject = java.lang.Boolean.TRUE;
            position.setIndex(position.getIndex() + trueString.length());
        }
        else if (targetString.startsWith(lowerFalseString))
        {
            parsedObject = java.lang.Boolean.FALSE;
            position.setIndex(position.getIndex() + falseString.length());
        }
        
        return parsedObject;
    }



    /** invariant
     [valid_trueString] trueString != null; 
     [valid_falseString] falseString != null; 
     [valid_lowerTrueString] lowerTrueString != null; 
     [valid_lowerFalseString] lowerFalseString != null; 
     [true_string_matches] trueString.equalsIgnoreCase(lowerTrueString);
     [false_string_matches] falseString.equalsIgnoreCase(lowerFalseString);
     **/    

}
