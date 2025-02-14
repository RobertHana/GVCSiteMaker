package net.global_village.foundation;

import java.text.*;


/**
 * As values are formatted as one constant string.  Parsing returns the parameter string unchanged.  Useful for things like
 * replacing all outgoing values with N/A.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class ConstantFormatter extends NullFormatter
{


    protected String constant;
    protected Format elementFormatter;


    /**
     * Designated constructor.
     *
     * @param sep string produce when any value is formatted
     */
    public ConstantFormatter(String sep)
    {
        super();
        /** require [valid_sep] sep != null;   **/
        constant = sep;
    }



    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition)
    {
        stringBuffer.append(constant);
        return stringBuffer;
    }

}
