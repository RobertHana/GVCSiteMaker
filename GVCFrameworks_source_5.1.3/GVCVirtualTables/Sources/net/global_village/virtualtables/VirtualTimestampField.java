package net.global_village.virtualtables;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;


/**
 * Holds the data for a column with the Timestamp type.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
public class VirtualTimestampField extends _VirtualTimestampField
{

    // The import date format that Excel uses
    public static final NSTimestampFormatter exportFormatter = new NSTimestampFormatter("%Y-%m-%d %H:%M:%S");


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(ColumnType.TimestampColumnType);
    }



    /**
     * The generic accessor for fields.  This class returns its timestamp value.
     *
     * @return this field's value
     */
    public Object value()
    {
        return timestampValue();
    }



    /**
     * The generic setter for fields.  This class sets its timestamp value.
     *
     * @param newValue the new value of the field
     */
    public void setValue(Object newValue)
    {
        /** require [valid_type] newValue == null || newValue instanceof NSTimestamp; **/
        setTimestampValue((NSTimestamp)newValue);
    }

   

    /**
     * Formats the value into the local timezone.  Used by the table export process to perform additional processing on an exported field value.  
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @return the value to use during table export
     */
    public Object valueForExport(Object sender)
    {
        return exportFormatter.format(value());
    }
}
