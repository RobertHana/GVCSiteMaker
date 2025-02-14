// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.databasetables.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.foundation.NSValidation.*;


/**
 * DataAccessSearchSortOrder represents one element of a sort ordering in a database table.  It holds the column being sorted and the direction of the sort.  This is the non-EO version of DataAccessColumnSearchSortOrder.
 */
public class DataAccessSearchSortOrder extends Object implements NSKeyValueCoding, NSKeyValueCodingAdditions
{
    public static final String ORDER_DIRECTION_ASCENDING = "Ascending";
    public static final String ORDER_DIRECTION_DESCENDING = "Descending";
    public static final NSArray ORDER_DIRECTIONS = new NSArray(new String[] {ORDER_DIRECTION_ASCENDING, ORDER_DIRECTION_DESCENDING});

    protected DataAccessSearchCriteria searchCriteria;

    protected Column sortColumn;
    protected String sortDirection;


    /**
     * Designated constructor.
     *
     * @param aSearchCriteria the DataAccessSearchCriteria instance owning this criterion
     */
    public DataAccessSearchSortOrder(DataAccessSearchCriteria aSearchCriteria)
    {
        super();
        /** require [valid_param] aSearchCriteria != null; **/
        searchCriteria = aSearchCriteria;
    }



    /**
     * Returns an EOSortOrdering based on this sort order.
     *
     * @return an EOSortOrdering based on this sort order
     */
    public EOSortOrdering sortOrdering()
    {
        /** require [has_column] sortColumn() != null; [has_direction] sortDirection() != null; **/

        NSSelector orderSelector =
            sortDirection().equalsIgnoreCase(ORDER_DIRECTION_ASCENDING) ?
                    EOSortOrdering.CompareCaseInsensitiveAscending :
                    EOSortOrdering.CompareCaseInsensitiveDescending;

        // HACK if we're sorting on a record ID column, we have to munge the column name to a value that will get looked up in the virtual row, since that is where the "record ID" comes from.  We considered the option of creating a new VirtualColumn subclass to do this work, but then the column's name would be different from it's normalized name, which, at very least, would be confusing, and would likely cause errors
        String normalizedName = sortColumn().keyPathForSorting();
        if (sortColumn().name().equals(SMVirtualTable.RECORD_ID_COLUMN_NAME))
        {
            normalizedName = SMVirtualTable.NORMALIZED_RECORD_ID_COLUMN_NAME;
        }

        return new EOSortOrdering(normalizedName, orderSelector);

        /** ensure [valid_result] Result != null; **/
    }



    public class DuplicateSortOrderValidationException extends ValidationException
    {
        public DuplicateSortOrderValidationException(String message)
        {
            super(message);
        }
    }



    /**
     * EOF Validation method for the criterionColumn part of the criterion.
     */
    public Object validateSortColumn(Object valueToValidate) throws ValidationException
    {
        if (valueToValidate == null)
        {
            throw new ValidationException("A column to sort on must be selected.", this, "sortColumn");
        }
        else if (searchCriteria().hasSortOrderWithSameColumnAs((Column)valueToValidate, this))
        {
            throw new DuplicateSortOrderValidationException("You cannot select the same column for sorting more than once.");
        }

        return valueToValidate;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * EOF Validation method for the criterionComparisonType part of the criterion.
     */
    public Object validateSortDirection(Object valueToValidate) throws ValidationException
    {
        if (valueToValidate == null)
        {
            throw new ValidationException("The order direction must be selected.", this, "direction");
        }

        if ( ! ORDER_DIRECTIONS.containsObject(valueToValidate))
        {
            throw new ValidationException("The order direction is not valid.", this, "direction");
        }

        return valueToValidate;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this criterion is valid.  A valid criterion has a column, value, comparision type, and join condition (if this is not the first criterion).
     *
     * @return <code>true</code> if this criterion is valid
     */
    public boolean isValid()
    {
        boolean isValid = true;
        try
        {
            validateSortColumn(sortColumn());
            validateSortDirection(sortDirection());
        }
        catch (ValidationException e)
        {
            isValid = false;
        }
        
        return isValid;
    }



    /**
     * Returns <code>true</code> if this is the first sort order, <code>false</code> otherwise.
     *
     * @return <code>true</code> if this is the first sort order, <code>false</code> otherwise
     */
    public boolean isFirstSortOrder()
    {
        return searchCriteria().isFirstSortOrder(this);
    }



    /**
     * Returns the Column compared in this criterion.
     *
     * @return the Column compared in this criterion.
     */
    public Column sortColumn()
    {
        return sortColumn;
    }


    /**
     * Sets the Column compared in this criterion.
     *
     * @param newColumn the Column compared in this criterion
     */
    public void setSortColumn(Column newColumn)
    {
        sortColumn = newColumn;
    }



    /**
     * Returns the direction for this criterion.
     *
     * @return the direction for this criterion
     */
    public String sortDirection()
    {
        return sortDirection;
    }


    /**
     * Sets the direction for this criterion.
     *
     * @param newDirection the direction for this criterion
     */
    public void setSortDirection(String newDirection)
    {
        sortDirection = newDirection;
    }



    /**
     * Returns the DataAccessSearchCriteria that owns this criterion.
     *
     * @return the DataAccessSearchCriteria that owns this criterion.
     */
    public DataAccessSearchCriteria searchCriteria()
    {
        return searchCriteria;
    }



    /**
     * Returns a String representation useful for debugging.
     *
     * @return a String representation useful for debugging.
     */
    public String toString()
    {
        return (super.toString() +
                ", Column: " + (sortColumn() != null ? sortColumn().name() : null) +
                ", Direction: " + sortDirection() +
                ", isFirstSortOrder: " + isFirstSortOrder() +
                ", isValid: " + isValid());
    }

    

    /**
     * Conformance to NSKeyValueCoding.
     */
    public Object valueForKey(String key)
    {
        return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
    }


    /**
     * Conformance to NSKeyValueCoding.
     */
    public void takeValueForKey(Object aValue, String key)
    {
        NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, aValue, key);
    }



    /**
     * Conformance to NSKeyValueCodingAdditions.
     */
    public Object valueForKeyPath(String keyPath)
    {
        return NSKeyValueCodingAdditions.DefaultImplementation.valueForKeyPath(this, keyPath);
    }


    /**
     * Conformance to NSKeyValueCodingAdditions.
     */
    public void takeValueForKeyPath(Object aValue, String keyPath)
    {
        NSKeyValueCodingAdditions.DefaultImplementation.takeValueForKeyPath(this, aValue, keyPath);
    }



}
