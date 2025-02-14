package net.global_village.foundation;

import com.webobjects.foundation.*;


/**
 * This class simply extends (renames) <code>com.webobjects.foundation.NSNumberFormatter</code> as documentation that it 
 * returns <code>java.math.BigDecimal</code>.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class BigDecimalFormatter extends NSNumberFormatter
{


    /** Convenience instance.  **/
    public static final BigDecimalFormatter Formatter = new BigDecimalFormatter();
    
}
