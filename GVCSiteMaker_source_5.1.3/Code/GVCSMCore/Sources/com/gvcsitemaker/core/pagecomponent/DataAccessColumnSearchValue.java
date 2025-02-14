package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Stores search criteria for searching Virtual Tables.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class DataAccessColumnSearchValue extends _DataAccessColumnSearchValue
{
    // Binding keys
    public static final String JOIN_CONDITION_BINDINGKEY = "joinCondition";
    public static final String COMPARISON_TYPE_BINDINGKEY = "comparisonType";
    public static final String VALUE_BINDINGKEY = "value";


    /**
     * Factory method to create new instances of DataAccessColumnSearchValue.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessColumnSearchValue or a subclass.
     */
    public static DataAccessColumnSearchValue newDataAccessColumnSearchValue()
    {
        return (DataAccessColumnSearchValue)SMEOUtils.newInstanceOf("DataAccessColumnSearchValue");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessColumnSearchValue");

        /** ensure [defaults_set] true; **/
    }



    /**
     * EOF Validation method for the column binding.
     */
    public Object validateColumn(Object value) throws NSValidation.ValidationException
    {
        if (value == null)
        {
            throw new NSValidation.ValidationException("Field name must be selected.", this, "column");
        }

        return value;
    }


    /**
     * EOF Validation method for the comparisonType binding.
     */
    public Object validateComparisonType(Object value) throws NSValidation.ValidationException
    {
        if (value == null)
        {
            throw new NSValidation.ValidationException("The comparison type must be selected.", this, "comparisonType");
        }

        return value;
    }


    /**
     * EOF Validation method for the value binding.
     */
    public Object validateValue(Object value) throws NSValidation.ValidationException
    {
        if (value == null)
        {
            throw new NSValidation.ValidationException("The value must be entered.", this, "value");
        }

        return value;
    }



    /**
     * Validates that the value is coerce-able to the correct type, coerces the value and returns it.
     *
     * @exception NSValidation.ValidationException if the value is not valid enough to be coerced to the correct type
     */
    public Object validateAndCoerceValue() throws NSValidation.ValidationException
    {
        /** require [has_a_column] column() != null; [has_a_value] value() != null; **/

        return DataAccessSearchCriterion.validatedAndCoercedValue(dataAccessComponent(), column(), value());
    }



    /**
     * Overridden to ensure that the value is validated against the data access column's validation method.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        if (column() != null)
        {
            try
            {
                // We don't care about the coerced value here, since we're just slapping the string into the bindings dictionary anyways
                validateAndCoerceValue();
            }
            catch (NSValidation.ValidationException e)
            {
                exceptions.addObject(e);
            }
        }

        try
        {
            super.validateForSave();
        }
        catch (NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    //************** Binding Cover Methods **************\\


    /**
     * Returns the comparison type (less than, equal to, etc) for this search criterion.
     *
     * @return the comparison type (less than, equal to, etc) for this search criterion
     */
    public String comparisonType()
    {
        return (String)valueForBindingKey(COMPARISON_TYPE_BINDINGKEY);
    }


    /**
     * Sets the comparison type (less than, equal to, etc) for this search criterion.
     *
     * @param newComparisonType the comparison type (less than, equal to, etc) for this search criterion
     */
    public void setComparisonType(String newComparisonType)
    {
        setBindingForKey(newComparisonType, COMPARISON_TYPE_BINDINGKEY);
    }



    /**
     * Returns the comparison value for this search criterion.
     *
     * @return the comparison value for this search criterion
     */
    public String value()
    {
        return (String)valueForBindingKey(VALUE_BINDINGKEY);
    }


    /**
     * Sets the comparison value for this search criterion.
     *
     * @param newValue the comparison value for this search criterion
     */
    public void setValue(String newValue)
    {
        setBindingForKey(newValue, VALUE_BINDINGKEY);
    }



    /**
     * Returns the comparison value for this search criterion.
     *
     * @return the comparison value for this search criterion
     */
    public String joinCondition()
    {
        return (String)valueForBindingKey(JOIN_CONDITION_BINDINGKEY);
    }


    /**
     * Sets the comparison value for this search criterion.
     *
     * @param newValue the comparison value for this search criterion
     */
    public void setJoinCondition(String newJoinCondition)
    {
        setBindingForKey(newJoinCondition, JOIN_CONDITION_BINDINGKEY);
    }



}

