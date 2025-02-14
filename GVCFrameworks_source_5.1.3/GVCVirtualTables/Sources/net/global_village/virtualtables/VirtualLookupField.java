package net.global_village.virtualtables;

import com.webobjects.eocontrol.EOEditingContext;

import net.global_village.eofvalidation.*;


/**
 * Holds the data for a column with the Lookup type.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class VirtualLookupField extends _VirtualLookupField
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
        setRestrictingValue(ColumnType.LookupColumnType);
    }



    /**
     * Sets the value (VirutalField) being looked up by this field and ensures that the VirutalField being looked knows that we are referring to it.
     *
     * @param value - the value that this field refers to.
     */
    public void setLookupValue(net.global_village.virtualtables.VirtualField value)
    {
        if (lookupValue() != value)
        {
            // If we are looking up an existing value then let it know we are not referring to it anymore.
            if (lookupValue() != null)
            {
                lookupValue().removeFromReferringFields(this);
            }

            // Make sure that fields we are referring to know about us.
            if (value != null)
            {
                value.addToReferringFields(this);
            }

            super.setLookupValue(value);
        }
    }



    /**
     * The generic accessor for fields.  This class returns its lookup value.
     *
     * @return this field's value
     */
    public Object value()
    {
        // Don't return a fault as this won't work with such things as sorting on a Lookup Field using EOSortOrdering.sortedArrayUsingKeyOrderArray.  If a fault is returned, then that is what gets compared.
        Object value = lookupValue();
        if (value != null)
        {
            ((VirtualField)value).willRead();
        }

        return value;
    }



    /**
     * The generic setter for fields.  This class sets its lookup value ID (the FK of the thing we are looking up).
     *
     * @param newValue the new value of the field
     */
    public void setValue(Object newValue)
    {
        /** require [valid_type] newValue == null || newValue instanceof Number; **/
        setLookupValue((VirtualField)newValue);
    }



    /**
     * Validates that the value of the lookup field comes from the correct lookup table.  FIXME: this, like the rest of the lookup stuff only works with VirualFields right now.
     *
     * @param value the value to validate
     * @return the coerced or changed value
     * @exception EOFValidationException if the value was not valid
     */
    public VirtualField validateLookupValue(VirtualField value) throws EOFValidationException
    {
        if ( ! value.column().equals(((VirtualLookupColumn)column()).lookupColumn()))
        {
            EOFValidationException exception = new EOFValidationException(this, "lookupValue.invalidColumnLookedUp");
            exception.setFailedValue(value);
            throw exception;
        }

        return value;
    }



    /**
     * Used by the table export process to perform additional processing on an exported field value.  This subclass dereferences the value, asking the lookup field for the valueForExport().  This should be moved to Column when column types are refactored into sub-classes instead of here on VirtualField because not all tables will even have a VirtualField (a concrete table, for example).
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @return the value to use during table export
     */
    public Object valueForExport(Object sender)
    {
        return lookupValue().valueForExport(sender);
    }

}
