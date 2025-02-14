package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;


/**
 * DataAccessCalculatedColumn wraps a VirtualCalculatedColumn and provides implementations
 * for the several possible expression result types.
 *
 * Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class DataAccessCalculatedColumn extends _DataAccessBooleanColumn
{



    /**
     * Factory method to create new instances of DataAccessCalculatedColumn.  Use
     * this rather than calling the constructor directly so that customized
     * versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessBooleanColumn or a subclass.
     */
    public static DataAccessCalculatedColumn newDataAccessCalculatedColumn()
    {
        return (DataAccessCalculatedColumn)SMEOUtils.newInstanceOf("DataAccessCalculatedColumn");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Validates that the value is of the correct type by passing the validation request to one of the
     * other DataAccessColumn types (based on column.expressionType()).
     *
     * @param dataAccess the DataAccess PageComponent implementing the Section that the search is
     * being performed in.  This is used to get to the titles for the checked and unchecked states
     * in Single and List mode in order to validate the search value
     * @param column the column that we are validating
     * @param value the value that we are validating
     * @return the validated and coerced value
     * @exception NSValidation.ValidationException if the value was not valid enough to be coerced
     * to the right type
     */
    public static Object validateColumnSearchValue(DataAccess dataAccess, Column column, String value) throws NSValidation.ValidationException
    {
        /** require [valid_column_param] column != null; **/
        try
        {
            return NSSelector.invoke("validateColumnSearchValue",
                                     new Class[] {DataAccess.class, Column.class, String.class},
                                     DataAccessSearchCriterion.validatingColumnClass(((VirtualCalculatedColumn)column).expressionType()),
                                     new Object[] {dataAccess, column, value});

        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * Returns a qualifier for the given column and comparison type by passing the validation request to one of the
     * other DataAccessColumn types (based on column.expressionType()).
     *
     * @param column column that we are searching on
     * @param comparisonOperator the operator to use
     * @param value the (possibly munged) value to use in the qualifier
     * @return the qualifier
     */
    public static EOQualifier searchQualifier(Column column, String comparisonOperator, Object value)
    {
        /** require [valid_column_param] column != null;
                    [valid_comparisonOperator_param] comparisonOperator != null;   **/
        try
        {
            return (EOQualifier)NSSelector.invoke("searchQualifier", new Class[] {Column.class, String.class, Object.class},
                                                  DataAccessSearchCriterion.validatingColumnClass(((VirtualCalculatedColumn)column).expressionType()),
                                                  new Object[] {column, comparisonOperator, value});
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overridden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessCalculatedColumn");

        // Define default formats for each of the possible types (string has no formatting)
        setNumberFormat((String) SMApplication.appProperties().arrayPropertyForKey("DataAccessNumberFormats"). objectAtIndex(0));
        setDateFormat((String)SMApplication.appProperties().arrayPropertyForKey("DataAccessDateFormats").objectAtIndex(0));
        setCheckedTitle("True");
        setUnCheckedTitle("False");
    }



    /**
     * @return column() downcast to VirtualCalculatedColumn
     */
    public VirtualCalculatedColumn calculatedColumn()
    {
        return (VirtualCalculatedColumn)column();
    }



    /**
     * Returns <code>false</code> as calculated values are not editable.
     *
     * @return <code>false</code> as calculated values are not editable
     */
    public boolean isEditableColumn()
    {
        /** require [has_column] column() != null;  **/
        return false;
    }




    /**
     * EOF Validation method for the checked title binding.
     */
    public Object validateCheckedTitle(Object value) throws NSValidation.ValidationException
    {
        if (value == null)
        {
            throw new NSValidation.ValidationException("The checked value is required.");
        }

        return value;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * EOF Validation method for the unchecked title binding.
     */
    public Object validateUnCheckedTitle(Object value) throws NSValidation.ValidationException
    {
        if (value == null)
        {
            throw new NSValidation.ValidationException("The unchecked value is required.");
        }

        return value;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Performs some extra validations, particularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        try
        {
            validateCheckedTitle(checkedTitle());
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        try
        {
            validateUnCheckedTitle(unCheckedTitle());
        }
        catch ( NSValidation.ValidationException e)
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
     * Returns the number format pattern string to use when displaying numeric values in this column.
     *
     * @return the number format pattern string to use when displaying numeric values in this column
     */
    public String numberFormat()
    {
        return (String) valueForBindingKey(DataAccessNumberColumn.NUMBER_FORMAT_BINDINGKEY);
    }


    /**
     * Sets the number format pattern string to use when displaying numeric values in this column.
     *
     * @param newFormat - the number format pattern string to use when displaying numeric values in this column
     */
    public void setNumberFormat(String newFormat)
    {
        setBindingForKey(newFormat, DataAccessNumberColumn.NUMBER_FORMAT_BINDINGKEY);
    }



    /**
     * Returns the date format pattern string to use when displaying timestamp values in this column.
     *
     * @return the date format pattern string to use when displaying timestamp values in this column
     */
    public String dateFormat()
    {
        return (String) valueForBindingKey(DataAccessTimestampColumn.DATE_FORMAT_BINDINGKEY);
    }


    /**
     * Sets the date format pattern string to use when displaying timestamp values in this column.
     *
     * @param newFormat - the date format pattern string to use when displaying timestamp values in this column
     */
    public void setDateFormat(String newFormat)
    {
        setBindingForKey(newFormat, DataAccessTimestampColumn.DATE_FORMAT_BINDINGKEY);
    }



    /**
     * Returns the title for the true state when displaying boolean values in this column.
     *
     * @return the title for the true state when displaying boolean values in this column
     */
    public String checkedTitle()
    {
         return (String) valueForBindingKey(DataAccessBooleanColumn.CHECKED_TITLE_BINDINGKEY);
    }

    /**
     * Sets the title for the true state when displaying boolean values in this column
     *
     * @param newTitle - the title for the true state when displaying boolean values in this column
     */
    public void setCheckedTitle(String newTitle)
    {
        setBindingForKey(newTitle, DataAccessBooleanColumn.CHECKED_TITLE_BINDINGKEY);
    }


    /**
     * Returns the title for the false state when displaying boolean values in this column
     *
     * @return the title for the false state when displaying boolean values in this column
     */
    public String unCheckedTitle()
    {
        return (String) valueForBindingKey(DataAccessBooleanColumn.UNCHECKED_TITLE_BINDINGKEY);
    }

    /**
     * Sets the title for the false state when displaying boolean values in this column
     *
     * @param newTitle - the title for the false state when displaying boolean values in this column
     */
    public void setUnCheckedTitle(String newTitle)
    {
        setBindingForKey(newTitle, DataAccessBooleanColumn.UNCHECKED_TITLE_BINDINGKEY);
    }
}

