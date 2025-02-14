package net.global_village.virtualtables;

import com.webobjects.foundation.NSKeyValueCoding;


/**
 * This class is not yet used.  I've left it here in case we need to revive it later.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
// Try later to make it extend EOCustomObject
public class VirtualObject extends Object implements com.webobjects.foundation.NSKeyValueCoding.ErrorHandling
{
    protected VirtualRow row;


    /**
     * Designated constructor.
     */
    public static VirtualObject newVirtualObject(Table table)
    {
        VirtualObject virtualObject = new VirtualObject();
        //VirtualRow virtualRow = table.newRow();
        //virtualObject.setRow(virtualRow);
        return virtualObject;
    }



    /**
     * Designated constructor.
     */
    public void setRow(VirtualRow newRow)
    {
        row = newRow;
    }



    /**
     * Designated constructor.
     */
    public Object handleQueryWithUnboundKey(String key)
    {
        if (row.hasColumnNamed(key))
        {
        }
        else
        {
        }
        return null;
    }


    /**
     * Designated constructor.
     */
    public void handleTakeValueForUnboundKey(Object value, String key)
    {
    }


    /**
     * Called when KVC can't set a key to null (when the key represents a scalar value).  Just 
     */
    public void unableToSetNullForKey(String key)
    {
        NSKeyValueCoding.DefaultImplementation.unableToSetNullForKey(this, key);
    }



}
