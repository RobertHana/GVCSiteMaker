package net.global_village.virtualtables;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;



/**
 * A lookup column is one that references a specific column in another table (actually, it could be the same table, but that wouldn't be very useful).  The data in the referenced column could be used to populate a popup list.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class VirtualLookupColumn extends _VirtualLookupColumn
{
    protected static final String VirtualLookupColumnRestrictingValue = "VirtualLookupColumn";

    protected static final NSArray ValueOrdering =
        new NSMutableArray(EOSortOrdering.sortOrderingWithKey("value", EOSortOrdering.CompareAscending));


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(VirtualLookupColumnRestrictingValue);
    }



    /**
     * Sets the Column being looked up by this lookup column and ensures that the Column being looked knows that we are referring to it.
     *
     * @param value - the column that this lookup column refers to.
     */
    public void setLookupColumn(net.global_village.virtualtables.Column value)
    {
        if (lookupColumn() != value)
        {
            // If we are looking up an existing column then let it know we are not referring to it anymore.
            if (lookupColumn() != null)
            {
                lookupColumn().removeFromReferringColumns(this);
            }

            // Make sure that Columns we are referring to know about us.
            if (value != null)
            {
                value.addToReferringColumns(this);
            }

            super.setLookupColumn(value);
        }
    }



    /**
     * Returns a human-readable description of the type of this column.  Overriden to provide special output for lookup values.
     *
     * @return a human-readable description of the type of this column
     */
    public String typeDescription()
    {
        return lookupColumn().typeDescription() + " values from " + lookupColumn().name() + " field in " + lookupColumn().table().name() + " table";
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns all the VirtualField values from the source lookupColumn() sorted into ascending order.  Any of these values can be used as the value for a VirtualField of this column.
     *
     * @return all the VirtualField values from the source lookupColumn() sorted into ascending order.
     */
    public NSArray allLookupValues()
    {
        // Optimization
        EODatabaseContextAdditions.preloadRelationship(new NSArray(lookupColumn().table()), "columns");

        NSMutableArray allListValues = new NSMutableArray();
        Enumeration rowEnumerator = lookupColumn().table().objectsWithQualifier(null).objectEnumerator();
        String lookupNormalizedName = lookupColumn().normalizedName();

        while (rowEnumerator.hasMoreElements())
        {
            VirtualField fieldInRow = ((VirtualRow) rowEnumerator.nextElement()).findFieldNamed(lookupNormalizedName);

            if (fieldInRow != null)
            {
                allListValues.addObject(fieldInRow);
            }
        }

        EOSortOrdering.sortArrayUsingKeyOrderArray(allListValues, ValueOrdering);

        return allListValues;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the name of the field attribute that the import process should use to set the field data.
     *
     * @return the name of the field attribute that the import process should use
     */
    public String importAttributeName()
    {
        return "lookupValueID";
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Used by the table import process to perform additional processing on an imported field value.  This subclass looks for the value in it's lookup table and returns the lookup value's FK.<p>
     *
     * Note that no coercion is done, so <code>value</code> must be the correct type.
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @param value the value we got during import, possibly formatted by a formatter
     * @return the value to use during table import
     * @exception EOFValidationException thrown if <code>value</code> is not a valid value for this column
     */
    public Object valueForImportedValue(Object sender, Object value) throws EOFValidationException
    {
        /** require [valid_value_param] value != null; **/
        VirtualColumn lookupColumn = (VirtualColumn)lookupColumn();
        NSDictionary lookupFieldByValue = new NSDictionary(lookupColumn.fields(), (NSArray)lookupColumn.fields().valueForKey("value"));
        VirtualField lookupValue = (VirtualField)lookupFieldByValue.objectForKey(value);
        if (lookupValue == null)
        {
            EOFValidationException exception = new EOFValidationException(this,  "value.noLookupValue");
            exception.setFailedValue(value);
            throw exception;
        }
        return lookupValue.virtualFieldID();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a value representative of the type of column being looked up.  These
     * values are used to validate calculated columns without needing to refer to data in the table.
     *
     * @see Column#representativeValue()
     *
     * @return a value representative of this column type
     */
    public Object representativeValue()
    {
        return lookupColumn().type().representativeValue();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This can be used when sorting if something other than value() is needed for ordering.  This
     * is mostly useful when value() is a relation to another object which contains the actual value
     * to be compared when sorting.  The default implementation just returns normalizedName().
     *
     * @return the keypath to use when comparing fields of this type when sorting rows
     */
    public String keyPathForSorting()
    {
        return normalizedName() + ".value";
    }

}
