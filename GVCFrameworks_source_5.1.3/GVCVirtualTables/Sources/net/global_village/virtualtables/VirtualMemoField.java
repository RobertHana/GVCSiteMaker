package net.global_village.virtualtables;

import com.webobjects.eocontrol.EOEditingContext;


/**
 * Holds the data for a column with the Memo type.
 * TODO remove the memo type completely
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
public class VirtualMemoField extends _VirtualMemoField
{


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(ColumnType.MemoColumnType);
    }



    /**
     * The generic accessor for fields.  This class returns its string value.
     *
     * @return this field's value
     */
    public Object value()
    {
        return memoValue();
    }



    /**
     * The generic setter for fields.  This class sets its string value.
     *
     * @param newValue the new value of the field
     */
    public void setValue(Object newValue)
    {
        /** require [valid_type] newValue == null || newValue instanceof String; **/
        setMemoValue((String)newValue);
    }



}
