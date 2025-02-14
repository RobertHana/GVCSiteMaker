/**
 * Implementation of DataAccessTimestampColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */  
package com.gvcsitemaker.core.pagecomponent;

import java.text.*;

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
 * DataAccessTimestampColumn wraps a Timestamp (Date/Time) Column and provides extra settings controlling the addition of new Fields in this column (defaults etc.).
 */
public class DataAccessTimestampColumn extends _DataAccessTimestampColumn
{
    public static final String WIDTH_BINDINGKEY = "width";
    public static final String USE_DEFAULT_BINDINGKEY = "useDefault";
    public static final String DATE_FORMAT_BINDINGKEY = "dateFormat";

    
    /**
     * Factory method to create new instances of DataAccessTimestampColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessTimestampColumn or a subclass.
     */
    public static DataAccessTimestampColumn newDataAccessTimestampColumn()
    {
        return (DataAccessTimestampColumn) SMEOUtils.newInstanceOf("DataAccessTimestampColumn");

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
        NSTimestampFormatter formatter = SMApplication.appProperties().timestampFormatterForKey("InputDateFormatter");
        return formatter.format(value);
    }



    /**
     * Validates that the value is of the correct type by attempting to coerce it.  
     * Returns the coerced value if successful, raises an exception if not.
     * Handles the special search values of <code>&lt;CurrentDate&gt;</code> and 
     * <code>&lt;CurrentDate&gt;+/-offset</code>.   
     *
     * @param dataAccess the DataAccess PageComponent implementing the Section that the search is
     * being performed in
     * @param column the column that we are validating
     * @param value the value that we are validating
     * @return the validated and coerced value
     * @exception NSValidation.ValidationException if the value was not valid enough to be coerced 
     * to the right type
     */
    public static Object validateColumnSearchValue(DataAccess dataAccess, Column column, String value) throws NSValidation.ValidationException
    {
        /** require [valid_column_param] column != null; **/

        NSTimestampFormatter formatter = SMApplication.appProperties().timestampFormatterForKey("InputDateFormatter");

        if (value == null)
        {
            return null;
        }
        // Handle <CurrentDate> and <CurrentDate>+/-offset
        else if (value.startsWith(DataAccessSearchCriterion.CURRENT_DATE_SEARCH_TOKEN))
        {
            TimestampInRange currentDate;
            
            // Check for presense of offset
            int offsetStart = DataAccessSearchCriterion.CURRENT_DATE_SEARCH_TOKEN.length();
            if (value.length() > (offsetStart + 1))
            {
                String offset = value.substring(offsetStart);
                
                // Handle oddity in that java.lang.Integer does not allow 
                // numbers to start with +.
                if (offset.startsWith("+"))
                {
                    offset = offset.substring(1);
                }
                
                if (StringAdditions.isInteger(offset))
                {
                    int offsetInDays = Integer.parseInt(offset);
                    currentDate = new TimestampInRange(Date.dateByAddingDays(Date.now(), offsetInDays));
                }
                else  // offset is invalid 
                {
                    throw new NSValidation.ValidationException("Value contains invalid offset", offset, "value");
                }
            }
            else if (value.length() == (offsetStart + 1)) // offset is invalid (single character)
            {
                throw new NSValidation.ValidationException("Value contains invalid offset", value.substring(offsetStart), "value");
            }
            else  // No offset present
            {
                currentDate = new TimestampInRange(Date.now());
            }
            
            // This token is only accurate for day range searches.
            currentDate.setAccuracy(TimestampInRange.DAY_ACCURACY);
            return currentDate;
        }

        try
        {
            return formatter.parseObject(value);
        }
        catch (ParseException e)
        {
            throw new NSValidation.ValidationException("Value is not a valid date", value, "value");
        }
        
        /** ensure  [correct_class] (Result == null) || Result instanceof TimestampInRange;  **/
    }



    /**
     * Returns a qualifier for the given column and comparison type that is 
     * appropriate for this column type.  The timestamp type, in particular, 
     * handles "like" queries by changing the "like" operator to "=".  We might 
     * also need to fool around with the time of day, but for now, that is not 
     * done.
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
        [value_is_TimestampInRange] (value == null) || (value instanceof TimestampInRange);
        [time_is_midnight] /# the timestamp must have 00:00:00 (in the local timezone) as it's time part #/ true; **/

        String columnName = column.normalizedName();

        // Handle "like" queries by changing them to equals
        if (comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR))
        {
            comparisonOperator = DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR;
        }

        if (value != null)
        {
            // FIXME: discontinuity in time values values when crossing into daylight saving time 
            /*
             * This discontinuity happens either because it is the first Sunday of 
             * April, as per normal DST rules, or due to a bug in NSTimestamp that 
             * makes 1945-1-1 and others be in DST incorrectly) that causes this 
             * search to produce more results than it should. If we have a value in 
             * the database that is not in DST, say 1970-4-12 23:30:00 and the next 
             * day, the 13th, _is_ in DST, then searching for dates greater than or 
             * equal to 1970-4-13 00:00:00 will match 1970-4-12 23:30:00.  This is 
             * because all dates are converted to GMT by NSTimestamp.  So the 12th, 
             * not in DST, adds 8hrs to get to GMT, resulting in 1970-4-13 07:30:00, 
             * while the 13th, which _is_ in DST, adds 7 hrs to get to GMT, resulting 
             * in 1970-4-13 07:00:00, which is _less_ than the previous day.  There 
             * doesn't appear to be any easy way to get around this...
             */
            // SM doesn't accept time values, so search for the entire day or hour.  
            NSTimestamp valueFrom = ((TimestampInRange)value).beginningOfRange();
            NSTimestamp valueTo = ((TimestampInRange)value).endOfRange();
            DebugOut.println(1, "Searching from: " + valueFrom);
            DebugOut.println(1, "Searching   to: " + valueTo);

            // Now, depending on the comparisonOperator we're using, we need to create different qualifiers
            if ((comparisonOperator.equals("<")) || (comparisonOperator.equals(">=")))
            {
                return EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(valueFrom));
            }
            else if ((comparisonOperator.equals(">")) || (comparisonOperator.equals("<=")))
            {
                return EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(valueTo));
            }
            else if ((comparisonOperator.equals("=")) || (comparisonOperator.equals("!=")))
            {
                String qualifierString = column.normalizedName() + " >= %@ AND " + column.normalizedName() + " <= %@";
                if (comparisonOperator.equals("!="))
                {
                    qualifierString = "NOT (" + qualifierString + ")";
                }

                if (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR))
                {
                	return new EOAndQualifier(new NSArray(new Object[] {
                            EOQualifier.qualifierWithQualifierFormat(qualifierString, new NSArray(new Object[] {valueFrom, valueTo})),
                			EOQualifier.qualifierWithQualifierFormat(columnName + " != %@", new NSArray(NSKeyValueCoding.NullValue))}));
                }
                else
                {
                    return EOQualifier.qualifierWithQualifierFormat(qualifierString, new NSArray(new Object[] {valueFrom, valueTo}));
                }
            }
            else
            {
                throw new Error("Unknown comparison operator: " + comparisonOperator);
            }
        }
        else
        {
            // Must handle null search values
            return EOQualifier.qualifierWithQualifierFormat(column.normalizedName() + " " + comparisonOperator + " %@", new NSArray(NSKeyValueCoding.NullValue));
        }
    }



    /**
     * Returns SQL for the given column and comparison type that is 
     * appropriate for this column type.  The timestamp type, in particular, 
     * handles "like" queries by changing the "like" operator to "=".  We might 
     * also need to fool around with the time of day, but for now, that is not 
     * done.
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
        [value_is_TimestampInRange] (value == null) || (value instanceof NSTimestamp);
        [time_is_midnight] /# the timestamp must have 00:00:00 (in the local timezone) as it's time part #/ true; **/

        // Handle "like" queries by changing them to equals
        if (comparisonOperator.equals("like"))
        {
            comparisonOperator = "=";
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
            // FIXME: discontinuity in time values values when crossing into daylight saving time 
            /*
             * This discontinuity happens either because it is the first Sunday of 
             * April, as per normal DST rules, or due to a bug in NSTimestamp that 
             * makes 1945-1-1 and others be in DST incorrectly) that causes this 
             * search to produce more results than it should. If we have a value in 
             * the database that is not in DST, say 1970-4-12 23:30:00 and the next 
             * day, the 13th, _is_ in DST, then searching for dates greater than or 
             * equal to 1970-4-13 00:00:00 will match 1970-4-12 23:30:00.  This is 
             * because all dates are converted to GMT by NSTimestamp.  So the 12th, 
             * not in DST, adds 8hrs to get to GMT, resulting in 1970-4-13 07:30:00, 
             * while the 13th, which _is_ in DST, adds 7 hrs to get to GMT, resulting 
             * in 1970-4-13 07:00:00, which is _less_ than the previous day.  There 
             * doesn't appear to be any easy way to get around this...
             */
            // SM doesn't accept time values, so search for the entire day or hour.  
            EOEntity entity = model.entityNamed("VirtualTimestampField");
            EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).createExpression(entity);
            EOAttribute attribute = entity.attributeNamed("timestampValue");

            NSTimestamp valueFrom = ((TimestampInRange)value).beginningOfRange();
            NSTimestamp valueTo = ((TimestampInRange)value).endOfRange();
            DebugOut.println(40, "Searching from: " + valueFrom);
            DebugOut.println(40, "Searching   to: " + valueTo);
            DebugOut.println(40, "Attribute value type: " + attribute.valueType());
            DebugOut.println(40, "Attribute external type: " + attribute.externalType());
            String valueFromString = expression.formatValueForAttribute(valueFrom, attribute);
            String valueToString = expression.formatValueForAttribute(valueTo, attribute);

            // Now, depending on the comparisonOperator we're using, we need to create different qualifiers
            if ((comparisonOperator.equals("<")) || (comparisonOperator.equals(">=")))
            {
                return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                    columnPK + " AND (timestamp_value IS NOT NULL AND timestamp_value " +
                  	comparisonOperator + " " + valueFromString + "))";
            }
            else if ((comparisonOperator.equals(">")) || (comparisonOperator.equals("<=")))
            {
                return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                    columnPK + " AND (timestamp_value IS NOT NULL AND timestamp_value " +
                	comparisonOperator + " " + valueToString + "))";
            }
            else if ((comparisonOperator.equals("=")) || (comparisonOperator.equals("!=")))
            {
                if (comparisonOperator.equals("!="))
                {
                	return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                	    columnPK + " AND (timestamp_value IS NOT NULL AND timestamp_value < " +
                		valueFromString + " OR timestamp_value > " + valueToString + "))";
                }
                else
                {
                	return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                	    columnPK + " AND (timestamp_value IS NOT NULL AND timestamp_value >= " +
                		valueFromString + " AND timestamp_value <= " + valueToString + "))";
                }
            }
            else
            {
                throw new Error("Unknown comparison operator: " + comparisonOperator);
            }
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
        			columnPK + " AND (timestamp_value IS NOT NULL)))";
        	}
        	else
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (timestamp_value IS NOT NULL))";
        	}
        }
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessTimestampColumn");
        setWidth("30");
        setUseDefault(false);
        setDateFormat((String)SMApplication.appProperties().arrayPropertyForKey("DataAccessDateFormats").objectAtIndex(0));

        /** ensure [defaults_set] width().equals("30") && ! useDefault() && dateFormat() != null;  **/
    }



    /**
     * Returns the current time as the default value for new fields of this column is useDefault().  Returns null otherwise.
     *
     * @return the current time as the default value for new fields of this column is useDefault().  Returns null otherwise.
     */
    public Object defaultValue()
    {
        return useDefault() ? Date.now() : null;
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

        if ( ! hasValueForBindingKey(DATE_FORMAT_BINDINGKEY))
        {
            exceptions.addObject(new NSValidation.ValidationException("dateFormat is a required binding for DataAccessTimestampColumn"));
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
     * Returns the date format pattern string to use when displaying the value in this column.
     *
     * @return the date format pattern string to use when displaying the value in this column.
     */
    public String dateFormat()
    {
        return (String) valueForBindingKey(DATE_FORMAT_BINDINGKEY);
    }
  
    
    /**
     * Sets the date format pattern string to use when displaying the value in this column.
     *
     * @param newDefault - the date format pattern string to use when displaying the value in this column.
     */
    public void setDateFormat(String newFormat)
    {
        setBindingForKey(newFormat, DATE_FORMAT_BINDINGKEY);
    }


    
    /**
     * Returns <code>true</code> if a default value (current date and time) should be supplied for new rows.
     *
     * @return <code>true</code> if a default value (current date and time) should be supplied for new rows.
     */
    public boolean useDefault()
    {
        return booleanValueForBindingKey(USE_DEFAULT_BINDINGKEY);
    }

    
    /**
     * Sets whether a default value (current date and time) should be supplied for new rows.
     *
     * @param booleanValue - <code>true</code> if a default value (current date and time) should be supplied for new rows.
     */
    public void setUseDefault(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, USE_DEFAULT_BINDINGKEY);
    }
}

