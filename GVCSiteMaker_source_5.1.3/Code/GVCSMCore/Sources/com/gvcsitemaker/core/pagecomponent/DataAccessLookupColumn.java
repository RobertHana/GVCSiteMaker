/**
 * Implementation of DataAccessLookupColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */  
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * DataAccessLookupColumn wraps a Lookup Column and provides extra settings controlling the addition 
 * of new Fields in this column (defaults etc.).
 */
public class DataAccessLookupColumn extends _DataAccessLookupColumn
{
    public static final String FORMAT_BINDINGKEY = "format";
    public static final String GRID_COLUMNS_BINDINGKEY = "gridColumns";
    
    public static final String DISPLAY_TYPE_BINDINGKEY = "displayType";
    public static final String POPUP_DISPLAY_TYPE = "popup";
    public static final String HORIZONTAL_DISPLAY_TYPE = "horizontal";
    public static final String VERTICAL_DISPLAY_TYPE = "vertical";
    public static final String GRID_DISPLAY_TYPE = "grid";


    /**
     * Factory method to create new instances of DataAccessLookupColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessLookupColumn or a subclass.
     */
    public static DataAccessLookupColumn newDataAccessLookupColumn()
    {
        return (DataAccessLookupColumn) SMEOUtils.newInstanceOf("DataAccessLookupColumn");

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
        try
        {
            // Forward message off to the column we're looking up
            Class valueColumnClass = Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" + ((VirtualLookupColumn)column).lookupColumn().type().name() + "Column");
            return (String)NSSelector.invoke("valueAsString", Column.class, Object.class, valueColumnClass, column, value);
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
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

        try
        {
            // Forward message off to the column we're looking up
            Class validatingColumnClass = Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" + ((VirtualLookupColumn)column).lookupColumn().type().name() + "Column");
            return NSSelector.invoke("validateColumnSearchValue",
                       new Class[] {DataAccess.class, Column.class, String.class},
                       validatingColumnClass,
                       new Object[] {dataAccess, column, value});
        }
        catch (NSValidation.ValidationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * Munges an EOKeyValueQualifier that qualifies the column that this lookup is looking up
     * so that it now properly looks <em>through</em> the lookup column to the column being looked up.
     *
     * @param lookupColumn the column to lookup through
     * @param searchQualifier the qualifier to munge
     * @return the munged qualifier
     */
    public static EOKeyValueQualifier mapSearchQualifierToLookupSearchQualifier(Column lookupColumn, EOKeyValueQualifier searchQualifier)
    {
        /** require
        [valid_lookupColumn_param] lookupColumn != null;
        [valid_searchQualifier_param] searchQualifier != null; **/

        String key = searchQualifier.key();
        // If we have a key path, then take off the root (which should be the name of the lookup column) and tack the rest onto the end of the new key path
        if (StringAdditions.isValidPropertyKeyPath(key))
        {
            key = "." + StringAdditions.removeRootKeyFromKeyPath(key);
        }
        else
        {
            key = "";
        }

        String newKeyPath = lookupColumn.normalizedName() + ".value" + key;
        return new EOKeyValueQualifier(newKeyPath, searchQualifier.selector(), searchQualifier.value());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Munges an EOQualifier that qualifies the column that this lookup is looking up so that it now properly looks <em>through</em> the lookup column to the column being looked up.  This method recurses through the qualifier, testing for type and using the map... function above to actually do the key mapping change for EOKeyValueQualifier types.
     *
     * @param lookupColumn the column to lookup through
     * @param searchQualifier the qualifier to munge
     * @return the munged qualifier
     */
    public static EOQualifier searchQualifierToLookupSearchQualifier(Column lookupColumn, EOQualifier searchQualifier)
    {
        /** require
        [valid_lookupColumn_param] lookupColumn != null;
        [valid_searchQualifier_param] searchQualifier != null; **/

        if (searchQualifier instanceof EOKeyValueQualifier)
        {
            return mapSearchQualifierToLookupSearchQualifier(lookupColumn, (EOKeyValueQualifier)searchQualifier);
        }
        else if (searchQualifier instanceof EOAndQualifier)
        {
            NSMutableArray newQualifiers = new NSMutableArray();
            Enumeration qualifierEnumerator = ((EOAndQualifier)searchQualifier).qualifiers().objectEnumerator();
            while (qualifierEnumerator.hasMoreElements())
            {
                EOQualifier qualifier = (EOQualifier)qualifierEnumerator.nextElement();
                newQualifiers.addObject(searchQualifierToLookupSearchQualifier(lookupColumn, qualifier));
            }
            return new EOAndQualifier(newQualifiers);
        }
        else if (searchQualifier instanceof EOOrQualifier)
        {
            NSMutableArray newQualifiers = new NSMutableArray();
            Enumeration qualifierEnumerator = ((EOOrQualifier)searchQualifier).qualifiers().objectEnumerator();
            while (qualifierEnumerator.hasMoreElements())
            {
                EOQualifier qualifier = (EOQualifier)qualifierEnumerator.nextElement();
                newQualifiers.addObject(searchQualifierToLookupSearchQualifier(lookupColumn, qualifier));
            }
            return new EOOrQualifier(newQualifiers);
        }
        else if (searchQualifier instanceof EONotQualifier)
        {
            EOQualifier qualifier = ((EONotQualifier)searchQualifier).qualifier();
            return new EONotQualifier(searchQualifierToLookupSearchQualifier(lookupColumn, qualifier));
        }
        else
        {
            throw new Error("Qualifier type was unknown: " + searchQualifier);
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a qualifier for the given column and comparison type that is appropriate for this column type.
     * The lookup type, in particular, creates a qualifier that searches the lookup table, then
     * translates that to one on current table.
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

        // Forward message off to the column we're looking up
        try
        {
            Class qualifyingColumnClass = Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" + ((VirtualLookupColumn)column).lookupColumn().type().name() + "Column");

            EOQualifier qualifier = (EOQualifier)NSSelector.invoke("searchQualifier", new Class[] {Column.class, String.class, Object.class}, qualifyingColumnClass, new Object[] {column, comparisonOperator, value});

            // Munge the key that we got from the lookup column so that it properly looks _through_ this lookup column to the _looked_ up column's value
            return searchQualifierToLookupSearchQualifier(column, qualifier);
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns SQL for the given column and comparison type that is appropriate for this column type.
     * The lookup type, in particular, creates a qualifier that searches the lookup table, then
     * translates that to one on current table.
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
        [column_is_virtual_lookup] column instanceof VirtualLookupColumn; **/

    	VirtualLookupColumn lookupColumn = (VirtualLookupColumn)column;

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
        	// Forward message off to the column we're looking up
        	try
        	{
        		String operatorToUse = comparisonOperator;
        		/*if (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR))
            	{
            		operatorToUse = DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR;
            	} */

        		Class qualifyingColumnClass = Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" +
        				lookupColumn.lookupColumn().type().name() + "Column");
        		String sql = (String)NSSelector.invoke("searchSQL",
        				new Class[] {Column.class, String.class, Object.class},
        				qualifyingColumnClass,
        				new Object[] {lookupColumn.lookupColumn(), operatorToUse, value});

        		// the various column types return a query that selects virtual_row_id, but we need it
        		// to return virtual_field_id, so munge the query
        		/** check sql.indexOf("(SELECT virtual_row_id FROM ") >= 0; **/
        		int start = sql.indexOf("(SELECT virtual_row_id FROM ");
        		sql = sql.substring(0, start) + "(SELECT virtual_field_id FROM " + sql.substring(start + "(SELECT virtual_row_id FROM ".length());

        		// Special handling for !=
        		/*if (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR))
            	{
            		return "(SELECT virtual_row_id FROM virtual_row WHERE virtual_table_id = " + column.table().primaryKeyValue() +
            			") except (SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
            			column.primaryKeyValue() + " AND lookup_value_id IN (\n      " + sql + "\n    ))";
            	}
            	else*/
        		{
        			return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        			    columnPK + " AND lookup_value_id IN (\n      " + sql + "\n    ))";
        		}
        	}
        	catch (Exception e)
        	{
        		throw new ExceptionConverter(e);
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
        			columnPK + " AND (lookup_value_id IS NOT NULL)))";
        	}
        	else
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (lookup_value_id IS NOT NULL))";
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
        setComponentType("DataAccessLookupColumn");
        setDisplayType(POPUP_DISPLAY_TYPE);
        
        /** ensure [defaults_set] displayType().equals(POPUP_DISPLAY_TYPE);  **/
    }



    /**
     * Returns a copy of an DataAccessLookupColumn sub-class.  The copy is made manually as the defaultField needs to be added to the copy.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this DataAccessLookupColumn
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = super.duplicate(copiedObjects);

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();
        
        if (Section.isSectionOnlyCopy(copiedObjects))
        {
            ((DataAccessLookupColumn) copy).addObjectToBothSidesOfRelationshipWithKey(defaultField(), "defaultField");
        }
        else
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("defaultField"));
        }  

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Overridden to set the default format once the lookup column type is known.
     *
     * @param aColumn the Column this DataAccessColumn is bound to
     */
    public void setColumn(Column aColumn)
    {
        super.setColumn(aColumn);

        // Setting the default format pattern needs to be delayed until we know what the column is.
        if (formatPatterns().count() > 0)
        {
            setFormat((String) formatPatterns().objectAtIndex(0));
        }
    }

    

    /**
     * Returns the data type of the lookupColumn() for the column bound to this DataAccessColumn.
     *
     * @return the data type of the lookupColumn() for the column bound to this DataAccessColumn
     */
    public String fieldType()
    {
        /** require
        [has_column] column() != null;
        [has_column_lookup] virtualLookupColumn().lookupColumn() != null; **/

        return virtualLookupColumn().lookupColumn().type().textDescription();

        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the default value (if any) for new fields of this column.  Returns null if there is no default value.
     *
     * @return the default value (if any) for new fields of this column or null if there is no default value
     */
    public Object defaultValue()
    {
        return defaultField();
    }



    /**
     * Returns column() downcast to VirtualLookupColumn.
     *
     * @return column() downcast to VirtualLookupColumn
     */
    public VirtualLookupColumn virtualLookupColumn()
    {
        return (VirtualLookupColumn) column();
    }



    /**
     * Is this Lookup column is connected to a Number Column?<br/><br/>
     * <b>Note</b>: This and isStringLookup and isTimestampLookup are to support logic that varies when the type of column used as a lookup.  If Column or ColumnType is refactored into one class per type, code dependant on these methods can perhaps be moved there.
     *
     * @return <code>true</code> if this Lookup column is connected to a Number Column
     */
    public boolean isNumberLookup()
    {
        return virtualLookupColumn().lookupColumn().isType(ColumnType.NumberColumnType);
    }



    /**
     * Is this Lookup column is connected to a String Column?
     *
     * @return <code>true</code> if this Lookup column is connected to a String Column
     */
    public boolean isStringLookup()
    {
        return virtualLookupColumn().lookupColumn().isType(ColumnType.StringColumnType);
    }



    /**
     * Is this Lookup column is connected to a Timestamp Column?
     *
     * @return <code>true</code> if this Lookup column is connected to a Timestamp Column
     */
    public boolean isTimestampLookup()
    {
        return virtualLookupColumn().lookupColumn().isType(ColumnType.TimestampColumnType);
    }



    /**
     * Should the input for this column be displayed as a popup list of values?
     *
     * @return <code>true</code> if this Lookup column should be input as a popup list of values,
     * <code>false</code> if Radio Buttons should be used 
     */
    public boolean shouldDisplayAsPopUp()
    {
        return displayType().equals(POPUP_DISPLAY_TYPE);
    }



    /**
     * Returns the available patterns to format values in this lookup list.
     *
     * @return the available patterns to format values in this lookup list.
     */
    public NSArray formatPatterns()
    {
        NSArray formatPatterns = NSArray.EmptyArray;

        if (isNumberLookup())
        {
            formatPatterns = SMApplication.appProperties().arrayPropertyForKey("DataAccessNumberFormats");
        }
        else if (isTimestampLookup())
        {
            formatPatterns = SMApplication.appProperties().arrayPropertyForKey("DataAccessDateFormats");
        }

        return formatPatterns;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a Format suitable for use with values in this lookup list and configured with aFormatPattern.  This method is not applicable to columns which can not be formatted (e.g. Text, File, etc.)
     *
     * @param aFormatPattern - the format pattern to use to format values in this lookup list.
     * @return a Format suitable for use with values in this lookup list and configured with aFormatPattern.
     */
    public java.text.Format formatterForPattern(String aFormatPattern)
    {
        /** require [valid_param] aFormatPattern != null;  [is_valid_lookup_type] isNumberLookup() || isTimestampLookup();  **/

        java.text.Format formatterForPattern = null;
        if (isNumberLookup())
        {
            formatterForPattern = new NSNumberFormatter(aFormatPattern);
        }
        else if (isTimestampLookup())
        {
            formatterForPattern = new NSTimestampFormatter(aFormatPattern);
        }

        return formatterForPattern;

        /** ensure [valid_result] Result != null;  **/
    }
    

    
    /**
     * Convenience method to return all available lookup values from the corresponding VirtualLookupColumn.
     *
     * @return list of all available lookup values
     */
    public NSArray allLookupValues()
    {
        /** require [has_column] column() != null;  **/

        return virtualLookupColumn().allLookupValues();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * EOF Validation method for the gridColumns binding.
     */
    public Object validateGridColumns(Object value) throws NSValidation.ValidationException
    {
        boolean gridColumnsRequired = displayType().equals(GRID_DISPLAY_TYPE);
        return validateInteger(value, 1, 999, ! gridColumnsRequired, "Grid Columns", "gridColumns");
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
            validateGridColumns(gridColumns());
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }
        
        if ((isNumberLookup() || isTimestampLookup()) && (format() == null))
        {
            exceptions.addObject(new NSValidation.ValidationException("format is a required binding for this DataAccessLookupColumn"));
        }
        
        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\

    /**
     * Returns the format pattern string to use when displaying the value in this column.
     *
     * @return the format pattern string to use when displaying the value in this column.
     */
    public String format()
    {
        return (String) valueForBindingKey(FORMAT_BINDINGKEY);
    }


    /**
     * Sets the format pattern string to use when displaying the value in this column.
     *
     * @param newDefault - the format pattern string to use when displaying the value in this column.
     */
    public void setFormat(String newFormat)
    {
        setBindingForKey(newFormat, FORMAT_BINDINGKEY);
    }



    /**
     * Returns the input display type to use for the value in this column.
     *
     * @return the input display type to use for the value in this column
     */    
    public String displayType()
    {
        return (String) valueForBindingKey(DISPLAY_TYPE_BINDINGKEY);
        /** ensure [valid_result] Result != null;
                   [known_type] Result.equals(POPUP_DISPLAY_TYPE) ||
                                Result.equals(HORIZONTAL_DISPLAY_TYPE) ||
                                Result.equals(VERTICAL_DISPLAY_TYPE) || 
                                Result.equals(GRID_DISPLAY_TYPE);  **/ 
    }



    /**
     * Sets the input display type to use for the value in this column.
     *
     * @param newType the input display type to use for the value in this column
     */    
    public void setDisplayType(String newType)
    {
        /** require [valid_type] newType != null;
                    [known_type] newType.equals(POPUP_DISPLAY_TYPE) ||
                                 newType.equals(HORIZONTAL_DISPLAY_TYPE) ||
                                 newType.equals(VERTICAL_DISPLAY_TYPE) || 
                                 newType.equals(GRID_DISPLAY_TYPE);  **/ 
        setBindingForKey(newType, DISPLAY_TYPE_BINDINGKEY);        
    }



    /**
     * Returns the number of columns in the grid when displayType() == GRID_DISPLAY_TYPE. 
     *
     * @return the number of columns in the grid when displayType() == GRID_DISPLAY_TYPE
     */    
    public String gridColumns()
    {
        return (String) valueForBindingKey(GRID_COLUMNS_BINDINGKEY);
    }



    /**
     * Sets the number of columns in the grid when displayType() == GRID_DISPLAY_TYPE.
     *
     * @param numColumns the number of columns in the grid when displayType() == GRID_DISPLAY_TYPE.
     */    
    public void setGridColumns(String numColumns)
    {
        setBindingForKey(numColumns, GRID_COLUMNS_BINDINGKEY);        
    }

}

