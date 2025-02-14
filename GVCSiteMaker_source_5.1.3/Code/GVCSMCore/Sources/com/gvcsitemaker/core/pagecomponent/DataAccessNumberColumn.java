/**
 * Implementation of DataAccessNumberColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import java.math.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * DataAccessNumberColumn wraps a Number Column and provides extra settings controlling the addition of new Fields in this column (defaults etc.).
 */
public class DataAccessNumberColumn extends _DataAccessNumberColumn
{
    public static final String WIDTH_BINDINGKEY = "width";
    public static final String DEFAULT_VALUE_BINDINGKEY = "defaultValue";
    public static final String NUMBER_FORMAT_BINDINGKEY = "numberFormat";



    /**
     * Factory method to create new instances of DataAccessNumberColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessNumberColumn or a subclass
     */
    public static DataAccessNumberColumn newDataAccessNumberColumn()
    {
        return (DataAccessNumberColumn) SMEOUtils.newInstanceOf("DataAccessNumberColumn");

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
        return ((BigDecimal)value).toString();
    }



    /**
     * Validates that the value is of the correct type by attempting to coerce it.  Returns the coerced value if successful, raises an exception if not.
     *
     * @param dataAccess the DataAccess PageComponent implementing the Section that the search is
     * being performed in
     * @param column the column that we are validating
     * @param value the value that we are validating
     * @return the validated and coerced value
     * @exception NSValidation.ValidationException if the value was not valid enough to be coerced to the right type
     */
    public static Object validateColumnSearchValue(DataAccess dataAccess, Column column, String value) throws NSValidation.ValidationException
    {
        /** require [valid_column_param] column != null; **/

        if (value == null)
        {
            return null;
        }
        else if ( ! StringAdditions.isNumber(value))
        {
            throw new NSValidation.ValidationException("The value is not a number.", value, "value");
        }
        else
        {
            // This is a bit nasty.  For some reason this worked before without setting the scale.
            // Then it stopped working.  This number is dependent on what is in the VT model.  If this
            // changes again, we should do something better than this!
            BigDecimal coercedValue = new BigDecimal(value);

            EOEntity entity = EOUtilities.entityForClass(column.editingContext(), VirtualNumberField.class);
            /** check [found_entity] entity != null; **/

            EOAttribute attribute = entity.attributeNamed("numberValue");
            /** check [found_attribute] attribute != null; **/


            coercedValue.setScale(attribute.scale(), BigDecimal.ROUND_HALF_EVEN);
            return coercedValue;
        }
    }



    /**
     * Returns a qualifier for the given column and comparison type that is appropriate for this column type.  The number type, in particular, handles "like" queries by changing the "like" operator to "=".
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
        [valid_comparisonOperator_param] comparisonOperator != null;
        [value_is_number] (value == null) || (value instanceof Number); **/

        String columnName = column.normalizedName();

        // Handle "like" queries by changing them to equals
        if (comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR))
        {
            comparisonOperator = DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR;
        }

        if (value != null)
        {
            // Coerce the value to the same scale as the database values
            if ((comparisonOperator.equals(DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR)) ||
            		(comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR)))
            {
                EOEntity entity = EOUtilities.entityForClass(column.editingContext(), VirtualNumberField.class);
                /** check [found_entity] entity != null; **/

                EOAttribute attribute = entity.attributeNamed("numberValue");
                /** check [found_attribute] attribute != null; **/

                // For some reason 0 is special and doesn't need a scale
                if ( ! ((BigDecimal)value).equals(new BigDecimal(0)))
                {
                    value = ((BigDecimal)value).setScale(attribute.scale(), BigDecimal.ROUND_HALF_EVEN);
                }
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
     * The number type, in particular, handles "like" queries by changing the "like" operator to "=".
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
        [valid_comparisonOperator_param] comparisonOperator != null;
        [value_is_number] (value == null) || (value instanceof Number); **/

        // Handle "like" queries by changing them to equals
        if (comparisonOperator.equals("like"))
        {
            comparisonOperator = "=";
        }
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

        Object tablePK = column.table().primaryKeyValue();
        if (tablePK instanceof NSData)
        {
            EOAttribute tableAttribute = columnEntity.attributeNamed("tableID");
            tablePK = columnExpression.formatValueForAttribute(tablePK, tableAttribute);
        }

        if (value != null)
        {
            // Coerce the value to the same scale as the database values
            if ((comparisonOperator.equals("=")) || (comparisonOperator.equals("!=")))
            {
                EOEntity entity = model.entityNamed("VirtualNumberField");
                EOAttribute attribute = entity.attributeNamed("numberValue");

                // For some reason 0 is special and doesn't need a scale
                if ( ! ((BigDecimal)value).equals(new BigDecimal(0)))
                {
                    value = ((BigDecimal)value).setScale(attribute.scale(), BigDecimal.ROUND_HALF_EVEN);
                }
            }

            return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                columnPK + " AND (number_value IS NOT NULL AND number_value " +
              	comparisonOperator + " " + value + "))";
        }
        else
        {
        	if (comparisonOperator.equals("="))
        	{
        		return "((SELECT virtual_row_id FROM virtual_row WHERE virtual_table_id = " + tablePK +
        			") EXCEPT (SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        			columnPK + " AND (number_value IS NOT NULL)))";
        	}
        	else
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (number_value IS NOT NULL))";
        	}
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessNumberColumn");
        setWidth("30");
        setNumberFormat((String) SMApplication.appProperties().arrayPropertyForKey("DataAccessNumberFormats"). objectAtIndex(0));

        /** ensure [defaults_set] width().equals("30") && numberFormat() != null;  **/
    }



    /**
     * EOF validation method for the width binding.
     */
    public Object validateWidth(Object value) throws NSValidation.ValidationException
    {
        return validateInteger(value, 1, 999, false, "Width", "width");
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * EOF validation method for the defaultValue binding.
     */
    public Object validateDefaultValue(Object valueToValidate) throws NSValidation.ValidationException
    {
        if (valueToValidate == null)
        {
            // OK, no default value
        }
        else if ( ! (valueToValidate instanceof String))
        {
            throw new NSValidation.ValidationException("defaultValue must be a String.  Did you use a formatter by mistake?",
                                                       this, "defaultValue");
        }
        else
        {
            try
            {
                NSNumberFormatter inputFormatter = SMApplication.appProperties().numberFormatterForKey("InputNumberFormatter");
                inputFormatter.parseObject((String)valueToValidate);
            }
            catch (java.text.ParseException e)
            {
                throw new NSValidation.ValidationException(valueToValidate + " was not recognized as a number.",
                                                           this, "defaultValue");
            }
        }

        return valueToValidate;
    }



    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
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

        if ( ! hasValueForBindingKey(NUMBER_FORMAT_BINDINGKEY))
        {
            exceptions.addObject(new NSValidation.ValidationException("numberFormat is a required binding for DataAccessNumberColumn"));
        }

        try
        {
            validateWidth(width());
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
     * Returns the width of the edit control to use when editing the value in this column.
     *
     * @return the width of the edit control to use when editing the value in this column.
     */
    public String width()
    {
        return (String) valueForBindingKey(WIDTH_BINDINGKEY);
    }

    /**
     * Sets the width of the edit control to use when editing the value in this column.
     *
     * @param newDefault - the width of the edit control to use when editing the value in this column.
     */
    public void setWidth(String newWidth)
    {
        setBindingForKey(newWidth, WIDTH_BINDINGKEY);
    }


    /**
     * Returns the number format pattern string to use when displaying the value in this column.
     *
     * @return the number format pattern string to use when displaying the value in this column.
     */
    public String numberFormat()
    {
        return (String) valueForBindingKey(NUMBER_FORMAT_BINDINGKEY);
    }


    /**
     * Sets the number format pattern string to use when displaying the value in this column.
     *
     * @param newDefault - the number format pattern string to use when displaying the value in this column.
     */
    public void setNumberFormat(String newFormat)
    {
        setBindingForKey(newFormat, NUMBER_FORMAT_BINDINGKEY);
    }


    /**
     * Returns the default value for this column.
     *
     * @return the default value for this column.
     */
     public Object defaultValue()
    {
        return valueForBindingKey(DEFAULT_VALUE_BINDINGKEY);
    }

    /**
     * Sets the default value for this column.
     *
     * @param newDefault - the default value for this column.
     */
    public void setDefaultValue(String newDefault)
    {
        setBindingForKey(newDefault, DEFAULT_VALUE_BINDINGKEY);
    }

}

