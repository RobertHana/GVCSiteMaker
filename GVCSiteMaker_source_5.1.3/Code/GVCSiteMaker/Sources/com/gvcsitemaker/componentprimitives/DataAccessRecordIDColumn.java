// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/*
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessRecordIDColumn extends DataAccessColumn
{

    /**
     * Designated constructor
     */
    public DataAccessRecordIDColumn(WOContext context)
    {
        super(context);
    }



    /**
     * Overriden to display NSData record IDs correctly.
     */
    public Object fieldValue()
    {
        Object value = valueForBinding("fieldValue");
        if ((value != null) && ( ! SMApplication.smApplication().isUsingIntegerPKs()))
        {
            return ERXStringUtilities.byteArrayToHexString(((NSData)value).bytes());
        }
        return value;
    }



}
