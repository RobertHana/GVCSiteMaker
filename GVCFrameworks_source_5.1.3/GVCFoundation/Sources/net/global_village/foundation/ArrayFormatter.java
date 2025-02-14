package net.global_village.foundation;

import java.text.*;

import com.webobjects.foundation.*;


/**
 * A formatter that formats an array of objects into a string with each element of the array formatted by a
 * sub-formatter and separated by a separator string.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ArrayFormatter extends Format
{


    protected String separator;
    protected Format elementFormatter;


    /**
     * Designated constructor.
     *
     * @param sep string used to separate elements in the array
     * @param formatter formatter used to format and parse each element in the array
     */
    public ArrayFormatter(String sep, Format formatter)
    {
        super();
        /** require [valid_sep] sep != null;  [valid_formatter] formatter != null;  **/
        separator = sep;
        elementFormatter = formatter;
    }



    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition)
    {
        if (object instanceof NSArray)
        {
            NSArray array = (NSArray)object;
            for (int i = 0; i < array.count(); i++)
            {
                if (i > 0)
                {
                    stringBuffer.append(separator);
                }

                if ( ! array.objectAtIndex(i).equals(NSKeyValueCoding.NullValue))
                {
                    stringBuffer.append(elementFormatter.format(array.objectAtIndex(i)));
                }
            }
        }
        else
        {
            stringBuffer.append(object);
        }

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
        NSArray elements = NSArray.componentsSeparatedByString(string, separator);
        NSMutableArray  parsedObjects = new NSMutableArray(elements.count());
        for (int i = 0;  i < elements.count(); i++)
        {
            String element = ((String)elements.objectAtIndex(i)).trim();
            if (element.length() > 0)
            {
                parsedObjects.addObject(elementFormatter.parseObject(((String)elements.objectAtIndex(i)).trim()));
            }
        }

        return parsedObjects;
    }

}
