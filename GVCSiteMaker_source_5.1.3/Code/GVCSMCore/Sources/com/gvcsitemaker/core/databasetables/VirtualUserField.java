/**
 * Implementation of VirtualUserField common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */
package com.gvcsitemaker.core.databasetables;

import com.gvcsitemaker.core.*;
import com.webobjects.eocontrol.*;


public class VirtualUserField extends _VirtualUserField
{
    public static final String ColumnTypeName = "User";



    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(ColumnTypeName);
    }



    /**
     * The generic accessor for fields.  This class returns its User value.
     *
     * @return this field's value
     */
    public Object value()
    {
        return userValue();
    }



    /**
     * The generic setter for fields.  This class sets its User value.
     *
     * @param newValue the new value of the field
     */
    public void setValue(Object newValue)
    {
        /** require [valid_type] newValue == null || newValue instanceof User; **/
        setUserValue((User)newValue);
    }


    /**
     * Users are exported as their User ID.  This method is used by the table export process to perform additional processing on an exported field value.
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @return the value to use during table export
     */
    public Object valueForExport(Object sender)
    {
        return userValue().userID();
    }
    
}

