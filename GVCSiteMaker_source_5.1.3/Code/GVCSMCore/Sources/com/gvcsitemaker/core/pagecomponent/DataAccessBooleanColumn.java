/**
 * Implementation of DataAccessBooleanColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * DataAccessBooleanColumn wraps a Boolean Column and provides extra settings controlling the
 * addition of new Fields in this column, value conversion for searching, and validation.
 */
public class DataAccessBooleanColumn extends _DataAccessBooleanColumn
{

    // Binding keys
    public static final String DEFAULT_VALUE_BINDINGKEY = "defaultValue";
    public static final String CHECKED_TITLE_BINDINGKEY = "checked";
    public static final String UNCHECKED_TITLE_BINDINGKEY = "unchecked";
    public static final NSSet DEFAULT_TRUE_SET = new NSSet(new String[] {"true", "yes"});
    public static final NSSet DEFAULT_FALSE_SET = new NSSet(new String[] {"false", "no"});



    /**
     * Factory method to create new instances of DataAccessBooleanColumn.  Use
     * this rather than calling the constructor directly so that customized
     * versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessBooleanColumn or a subclass.
     */
    public static DataAccessBooleanColumn newDataAccessBooleanColumn()
    {
        return (DataAccessBooleanColumn)SMEOUtils.newInstanceOf("DataAccessBooleanColumn");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Given a value (of a type this column accepts), returns the value as a string.
     *
     * @param column the column that we are coercing for
     * @param value the value that we are coercing
     * @return the coerced value
     */
    public static String valueAsString(Column column, Object value)
    {
        /** require
        [valid_column_param] column != null;
        [valid_value_param] value != null; **/
        return value.toString();
    }



    /**
     * Validates that the value is of the correct type by attempting to coerce it. Returns the coerced
     * value if successful, raises an exception if not.  Valid values are anything in
     * <code>DEFAULT_TRUE_SET</code>, <code>DEFAULT_FALSE_SET</code>, or the checked or unchecked
     * title for this column in the Single or List mode.
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

        if (value == null)
        {
            return null;
        }

        Object coercedValue = null;

        // When searching in a Section allow the title of the boolean column to be used as a search
        // value.  This is not available when filtering in other situations, exporting for example.
        if (dataAccess != null)
        {
            // First check and see if the search value is one of the titles in single mode
            DataAccessMode mode = dataAccess.componentForMode(DataAccessMode.SINGLE_MODE);
            DataAccessColumn daColumn = mode.dataAccessColumnForColumn(column);
            coercedValue = DataAccessBooleanColumn.booleanForValueFromTitles(value,
                                                                             (String)daColumn.valueForBindingKey(CHECKED_TITLE_BINDINGKEY),
                                                                             (String)daColumn.valueForBindingKey(CHECKED_TITLE_BINDINGKEY));

            if (coercedValue == null)
            {
                // Second choice is if the search value is one of the titles in list mode
                mode = dataAccess.componentForMode(DataAccessMode.LIST_MODE);
                daColumn = mode.dataAccessColumnForColumn(column);
                coercedValue = DataAccessBooleanColumn.booleanForValueFromTitles(value,
                                                                                 (String)daColumn.valueForBindingKey(CHECKED_TITLE_BINDINGKEY),
                                                                                 (String)daColumn.valueForBindingKey(CHECKED_TITLE_BINDINGKEY));
            }
        }

        // If there is no Section or Section specific matches fail, try the defaults
        if (coercedValue == null)
        {
            coercedValue = booleanFromDefaults(value);
        }

        // If we still cannot match it then treat it as an error.
        if (coercedValue == null)
        {
            throw new NSValidation.ValidationException("The value is not acceptable.", value, "value");
        }

        return coercedValue;
    }



    /**
     * Returns <code>Boolean.TRUE</code> if <code>value</code> is in <code>DEFAULT_TRUE_SET</code>
     * and <code>Boolean.FALSE</code> if <code>value</code> is in <code>DEFAULT_FALSE_SET</code>,
     * and null  if <code>value</code> is not in either.  Comparisons are not case sensitive.
     *
     * @param value the String value to determine a Boolean value from.
     * @return Boolean.TRUE, Boolean.FALSE, or null depending on value
     */
    public static Boolean booleanFromDefaults(String value)
    {
        /** require [valid_value_param] value != null;   **/
        Boolean booleanFromString = null;

        value = value.toLowerCase();

        if (DEFAULT_TRUE_SET.containsObject(value))
        {
            booleanFromString = Boolean.TRUE;
        }
        else  if (DEFAULT_FALSE_SET.containsObject(value))
        {
            booleanFromString = Boolean.FALSE;
        }

        return booleanFromString;
    }



    /**
     * Returns <code>Boolean.TRUE</code> if <code>value</code> is <code>checkedTitle()</code>
     * and <code>Boolean.FALSE</code> if <code>value</code> is in <code>unCheckedTitle()</code>,
     * and null  if <code>value</code> is neither.  Comparisons are not case sensitive.
     *
     * @param value the String value to determine a Boolean value from.
     * @param trueValue string to match to value to detect TRUE value
     * @param falseValue string to match to value to detect FALSE value
     * @return Boolean.TRUE, Boolean.FALSE, or null depending on value
     */
    public static Boolean booleanForValueFromTitles(String value, String trueValue, String falseValue)
    {
        /** require [valid_value_param] value != null;
                    [valid_trueValue_param] trueValue != null;
                    [valid_falseValue_param] falseValue != null; **/
        Boolean booleanFromTitle = null;
        value = value.toLowerCase();

        if (trueValue.toLowerCase().equals(value))
        {
            booleanFromTitle = Boolean.TRUE;
        }
        else if (falseValue.toLowerCase().equals(value))
        {
            booleanFromTitle = Boolean.FALSE;
        }

        return booleanFromTitle;
    }



    /**
     * Returns a qualifier for the given column and comparison type that is
     * appropriate for this column type.
     *
     * @param column column that we are searching on
     * @param comparisonOperator the operator to use
     * @param value the (possibly munged) value to use in the qualifier
     * @return the qualifier
     */
    public static EOQualifier searchQualifier(Column column, String comparisonOperator, Object value)
    {
        /** require
        [valid_column_param] column != null;
        [valid_comparisonOperator_param] comparisonOperator != null;  **/

        String columnName = column.normalizedName();

        if (value != null)
        {
            // Handle "like" queries
            if (comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR))
            {
                comparisonOperator = "=";
            }

            if (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR))
            {
            	return new EOAndQualifier(new NSArray(new Object[] {
            			EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(value)),
            			EOQualifier.qualifierWithQualifierFormat(columnName + " != %@", new NSArray(NSKeyValueCoding.NullValue))}));
            }
            else
            {
            	return EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(value));
            }
        }
        else
        {
            return EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(NSKeyValueCoding.NullValue));
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns SQL for the given column and comparison type that is appropriate for this column type.
     * The boolean type, in particular, handles "like" queries by changing the "like" operator to "=".
     *
     * @param column column that we are searching on
     * @param comparisonOperator the operator to use
     * @param value the (possibly munged) value to use in the qualifier
     * @return SQL appropriate to the params
     */
    public static String searchSQL(Column column, String comparisonOperator, Object value)
    {
        /** require
        [valid_column_param] column != null;
        [valid_comparisonOperator_param] comparisonOperator != null; **/

    	// convert operators to SQL operators
        if (comparisonOperator.equals("!="))
        {
        	comparisonOperator = "<>";
        }

        EOModel model = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        EOAdaptor adaptor = EOAdaptor.adaptorWithModel(model);
        EOEntity columnEntity = model.entityNamed("VirtualColumn");
        EOSQLExpression columnExpression = new EOSQLExpressionFactory(adaptor).createExpression(columnEntity);

        Object columnPK = column.primaryKeyValue();
        if (columnPK instanceof NSData)
        {
            EOAttribute columnAttribute = columnEntity.attributeNamed("columnID");
            columnPK = columnExpression.formatValueForAttribute(columnPK, columnAttribute);
        }

        if (value != null)
        {
            // Handle "like" queries
            if (comparisonOperator.equals("like"))
            {
                comparisonOperator = "=";
            }

            return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                columnPK + " AND (boolean_value IS NOT NULL AND boolean_value " +
              	comparisonOperator + " '" + value + "'))";
        }
        else
        {
            Object tablePK = column.table().primaryKeyValue();
            if (tablePK instanceof NSData)
            {
                EOAttribute tableAttribute = columnEntity.attributeNamed("tableID");
                tablePK = columnExpression.formatValueForAttribute(tablePK, tableAttribute);
            }

        	if (comparisonOperator.equals("="))
        	{
        		return "((SELECT virtual_row_id FROM virtual_row WHERE virtual_table_id = " + tablePK +
        			") EXCEPT (SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        			columnPK + " AND (boolean_value IS NOT NULL)))";
        	}
        	else
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (boolean_value IS NOT NULL))";
        	}
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessBooleanColumn");
        setCheckedTitle("True");
        setUnCheckedTitle("False");
        setDefaultValue(Boolean.FALSE);
        /** ensure [defaults_set] checkedTitle().equals("True") &&
                                  unCheckedTitle().equals("False") &&
                                  defaultValue().equals(Boolean.FALSE);  **/
    }



    /**
     * Returns <code>Boolean.TRUE</code> if <code>value</code> is <code>checkedTitle()</code>
     * and <code>Boolean.FALSE</code> if <code>value</code> is in <code>unCheckedTitle()</code>,
     * and null  if <code>value</code> is neither.  Comparisons are not case sensitive.
     *
     * @param value the String value to determine a Boolean value from.
     * @return Boolean.TRUE, Boolean.FALSE, or null depending on value
     */
    public Boolean booleanFromTitle(String value)
    {
        /** require [valid_value_param] value != null;   **/
        return booleanForValueFromTitles(value, checkedTitle(), unCheckedTitle());
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
     * Returns the default value for this column.
     *
     * @return the default value for this column.
     */
     public Object defaultValue()
    {
        Object defaultValue = null;
        if (valueForBindingKey(DEFAULT_VALUE_BINDINGKEY) != null)
        {
            defaultValue = new Boolean((String) valueForBindingKey(DEFAULT_VALUE_BINDINGKEY));
        }

        return defaultValue;
    }

    /**
     * Sets the default value for this column.
     *
     * @param newDefault - the default value for this column.
     */
    public void setDefaultValue(Boolean newDefault)
    {
        setBindingForKey((newDefault != null) ? newDefault.toString() : null, DEFAULT_VALUE_BINDINGKEY);
    }



    /**
     * Returns the title for the true state of this Boolean value.
     *
     * @return the title for the true state of this Boolean value
     */
    public String checkedTitle()
    {
         return (String) valueForBindingKey(CHECKED_TITLE_BINDINGKEY);
    }

    /**
     * Sets the title for the true state of this Boolean value
     *
     * @param newDefault - the title for the true state of this Boolean value
     */
    public void setCheckedTitle(String newTitle)
    {
        setBindingForKey(newTitle, CHECKED_TITLE_BINDINGKEY);
    }


    /**
     * Returns the title for the false state of this Boolean value.
     *
     * @return the title for the false state of this Boolean value
     */
    public String unCheckedTitle()
    {
        return (String) valueForBindingKey(UNCHECKED_TITLE_BINDINGKEY);
    }

    /**
     * Sets the title for the false state of this Boolean value
     *
     * @param newDefault - the false for the true state of this Boolean value
     */
    public void setUnCheckedTitle(String newTitle)
    {
        setBindingForKey(newTitle, UNCHECKED_TITLE_BINDINGKEY);
    }


}

