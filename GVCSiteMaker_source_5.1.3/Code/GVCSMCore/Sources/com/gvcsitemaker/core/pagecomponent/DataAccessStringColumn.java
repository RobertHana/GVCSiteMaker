/**
 * Implementation of DataAccessStringColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */  
package com.gvcsitemaker.core.pagecomponent;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * DataAccessStringColumn wraps a String Column and provides extra settings controlling the addition of new Fields in this column (defaults etc.).  
 */
public class DataAccessStringColumn extends _DataAccessStringColumn
{

    // Binding keys
    public static final String DEFAULT_VALUE_BINDINGKEY = "defaultValue";
    public static final String HEIGHT_BINDINGKEY = "height";
    public static final String WIDTH_BINDINGKEY = "width";
    public static final String CONVERT_CR_BINDINGKEY = "convertCR";
    public static final String WYSIWYGEditorMode = "WYSIWYGEditorMode";
    
    // WYSIWYG Editor modes
    public static final String OffMode = "Off";
    public static final String SimpleMode = "Simple";    
    public static final String AdvancedMode = "Advanced";
    public static final NSArray WysiwygEditorModes = new NSArray( new String[] {OffMode, SimpleMode, AdvancedMode});

    /**
     * Factory method to create new instances of DataAccessStringColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessStringColumn or a subclass.
     */
    public static DataAccessStringColumn newDataAccessStringColumn()
    {
        return (DataAccessStringColumn)SMEOUtils.newInstanceOf("DataAccessStringColumn");

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
        return (String)value;
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

        // Any string value is valid
        return value;
    }



    /**
     * Returns a qualifier for the given column and comparison type that is appropriate for this column type. The string type, in particular, makes a case insensitive qualifier.
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
        [value_is_string] (value == null) || (value instanceof String); **/

        // Lower case the value of the column using KVC
        String columnName = column.normalizedName() + ".toLowerCase";

        if (value != null)
        {
            // Lower case the search value directly
            String searchValue = ((String)value).toLowerCase();

            // Handle "like" queries
            if (comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR))
            {
                searchValue = "*" + searchValue + "*";
            }

            if (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR))
            {
            	return new EOAndQualifier(new NSArray(new Object[] {
            			EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(searchValue)),
            			EOQualifier.qualifierWithQualifierFormat(columnName + " != %@", new NSArray(NSKeyValueCoding.NullValue))}));
            }
            else
            {
            	return EOQualifier.qualifierWithQualifierFormat(columnName + " " + comparisonOperator + " %@", new NSArray(searchValue));
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
     * The string type, in particular, makes a case insensitive qualifier.
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
        [value_is_string] (value == null) || (value instanceof String); **/

    	// convert operators to SQL operators
        if (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR))
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
            String searchValue = ((String)value).toLowerCase();

            EOEntity entity = model.entityNamed("VirtualStringField");
            EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).createExpression(entity);

            // Handle "like" queries
            if (comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR) ||
            		comparisonOperator.equals(DataAccessSearchCriterion.NOT_CONTAINS_COMPARISON_OPERATOR))
            {
                searchValue = "%" + searchValue + "%";
            }
            searchValue = expression.formatStringValue(searchValue);

            return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
                columnPK + " AND (string_value IS NOT NULL AND string_value " +
              	comparisonOperator + " " + searchValue + " COLLATE INFORMATION_SCHEMA.CASE_INSENSITIVE))";
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
        			columnPK + " AND (string_value IS NOT NULL)))";
        	}
        	else
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (string_value IS NOT NULL))";
        	}
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns SQL for the given column and comparison type that is appropriate for this column type.
     * The string type, in particular, makes a case insensitive qualifier.
     *
     * @param column column that we are searching on
     * @param comparisonOperator the operator to use
     * @param value the (possibly munged) value to use in the qualifier
     * @return SQL appropriate to the params
     */
    public static String oldSearchSQL(Column column, String comparisonOperator, Object value)
    {
        /** require
        [valid_column_param] column != null;
        [valid_comparisonOperator_param] comparisonOperator != null;
        [value_is_string] (value == null) || (value instanceof String); **/

    	// convert operators to SQL operators
        if (comparisonOperator.equals("!="))
        {
        	comparisonOperator = "<>";
        }

        String columnName = "\"" + column.normalizedName() + "\".string_value";

        if (value != null)
        {
            String searchValue = ((String)value).toLowerCase();

            EOModel model = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
            EOEntity entity = model.entityNamed("VirtualStringField");
            EOAdaptor adaptor = EOAdaptor.adaptorWithModel(model);
            EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).createExpression(entity);

            // Handle "like" queries
            if (comparisonOperator.equals("like"))
            {
                searchValue = "%" + searchValue + "%";
            }
            searchValue = expression.formatStringValue(searchValue);

            return "(" + columnName + " is not null and lower(" + columnName + ") " + comparisonOperator + " " + searchValue + ")";
        }
        else
        {
            return columnName + " is " + (( ! comparisonOperator.equals("=")) ? "not " : "") + "null";
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns true if the WYSIWYG editor for this DataAccessStringColumn should be shown. It should be shown if the application wide setting is true and wysiwygEditorMode is not Off.
     * 
     * @return true if the WYSIWYG editor for this DataAccessStringColumn should be shown
     */
    public boolean isWYSIWYGEnabled() 
    {
        return (SMApplication.appProperties().booleanPropertyForKey("WYSIWYGEditorEnabled") && 
                ( ! wysiwygEditorMode().equals(OffMode)));
    }
    
    
    
    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessStringColumn");
        setHeight("1");
        setWidth("30");
        setShouldConvertCR(SMApplication.appProperties().
                     booleanPropertyForKey("ConvertCarriageReturnsToLineBreaks"));
        setDefaultComparison(DataAccessSearchCriterion.CONTAINS_COMPARISON_TYPE);
        setWysiwygEditorMode(OffMode);
        

        /** ensure [defaults_set] height().equals("1") && 
                                  width().equals("30") &&
                                  hasValueForBindingKey(CONVERT_CR_BINDINGKEY) &&
                                  defaultComparison().equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_TYPE);  **/
    }	



    /**
     * EOF Validation method for the width binding.
     */
    public Object validateWidth(Object value) throws NSValidation.ValidationException
    {
        return validateInteger(value, 1, 999, false, "Width", "width");
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * EOF Validation method for the height binding.
     */
    public Object validateHeight(Object value) throws NSValidation.ValidationException
    {
        return validateInteger(value, 1, 999, false, "Height", "height");
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

        try
        {
            validateHeight(height());
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
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



    /**
     * Returns the height of the edit control to use when editing the value in this column.
     *
     * @return the height of the edit control to use when editing the value in this column.
     */
    public String height()
    {
         return (String) valueForBindingKey(HEIGHT_BINDINGKEY);
    }

    /**
     * Sets the height of the edit control to use when editing the value in this column.
     *
     * @param newDefault - the height of the edit control to use when editing the value in this column.
     */
    public void setHeight(String newDefault)
    {
        setBindingForKey(newDefault, HEIGHT_BINDINGKEY);
    }


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
    public void setWidth(String newDefault)
    {
        setBindingForKey(newDefault, WIDTH_BINDINGKEY);
    }


    /**
     * Returns whether this component should translated EOL markers into <br />
     * when displaying.
     *
     * @return whether this component should translated EOL markers into <br />
     * when displaying
     */
    public boolean shouldConvertCR() {
        return booleanValueForBindingKey(CONVERT_CR_BINDINGKEY);
    }
    
    /**
     * Sets whether this component should translated EOL markers into <br /> when 
     * displayingthe width of the edit control to use when editing the value in this column.
     *
     * @param shouldConvert - whether this component should translated EOL 
     * markers into <br /> when displaying
     */
    public void setShouldConvertCR(boolean shouldConvert) {
        setBooleanValueForBindingKey(shouldConvert, CONVERT_CR_BINDINGKEY);
    }
   
    
    
    /**
     * Returns the mode of the wysiwyg editor to use when editing the value in this column. To handle pre-existing data, it returns OffMode if null.
     *
     * @return the mode of the wysiwyg editor to use when editing the value in this column.
     */
    public String wysiwygEditorMode()
    {
        Object wysiwygEditorMode = valueForBindingKey(WYSIWYGEditorMode);
        
        //Handle existing data before this binding was added
        if (wysiwygEditorMode == null)
        {
            wysiwygEditorMode = OffMode;
        }
        
        return (String) wysiwygEditorMode;
    }

    
    
    /**
     * Sets the mode of the wysiwyg editor to use when editing the value in this column.
     *
     * @param newDefault - the mode of the wysiwyg editor to use when editing the value in this column
     */
    public void setWysiwygEditorMode(String newDefault)
    {
        setBindingForKey(newDefault, WYSIWYGEditorMode);
    }    
}

