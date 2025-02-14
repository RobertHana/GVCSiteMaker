package net.global_village.foundation;

import java.text.*;


/**
 * A null object formatter that just returns its input.  Useful for when the API needs a formatter
 * but there is no formatting to do.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class NullFormatter extends Format
{
    
    /** Convenience instance.  **/
    public static final NullFormatter Formatter = new NullFormatter();
    


    /**
     * Designated constructor.
     */
    public NullFormatter()
    {
        super();
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
        position.setIndex(string.length());
        return string;
    }
    
}
