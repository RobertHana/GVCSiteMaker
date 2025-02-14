/**
 * Implementation of DataAccessUserColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */  
package com.gvcsitemaker.core.pagecomponent;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * DataAccessUserColumn wraps a User (Date/Time) Column.
 */
public class DataAccessUserColumn extends _DataAccessUserColumn
{


    /**
     * Factory method to create new instances of DataAccessUserColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessUserColumn or a subclass.
     */
    public static DataAccessUserColumn newDataAccessUserColumn()
    {
        return (DataAccessUserColumn) SMEOUtils.newInstanceOf("DataAccessUserColumn");

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

        // Any string value is fine, but we do want to make sure the user name is cannonical
        if (value != null)
        {
            return User.canonicalUserID(value);
        }
        else
        {
            return null;
        }
    }



    /**
     * Returns a qualifier for the given column and comparison type that is appropriate for this column type.
     * The user type, in particular, handles queries by changing operators other than != and like to "=".
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
        [known_comparisonOperator] DataAccessSearchCriterion.OPERATOR_DICTIONARY.allValues().containsObject(comparisonOperator);
        [value_is_string] (value == null) || (value instanceof String); **/

        // Lower case the value of the column using KVC
        String columnName = column.normalizedName() + ".userID.toLowerCase";

        // All user queries except "!=" and contains/like use the equals operator
        if ( ! (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR) || 
        		   comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR)) )
        {
            comparisonOperator = DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR;
        }

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
     * The user type, in particular, handles queries by changing operators other than != and like to "=".
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
        [known_comparisonOperator] DataAccessSearchCriterion.OPERATOR_DICTIONARY.allValues().containsObject(comparisonOperator);
        [value_is_string] (value == null) || (value instanceof String); **/

        // All user queries except "!=" and contains/like use the equals operator
        if ( ! (comparisonOperator.equals(DataAccessSearchCriterion.IS_NOT_EQUAL_TO_COMPARISON_OPERATOR) || 
        		   comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR)) )
        {
            comparisonOperator = DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR;
        }

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
            if (comparisonOperator.equals("like"))
            {
                searchValue = "%" + searchValue + "%";
            }
            searchValue = expression.formatStringValue(searchValue);

            return "(SELECT virtual_row_id FROM virtual_field, sm_user WHERE virtual_column_id = " +
                columnPK + " AND user_fkey = user_pkey AND user_id " +
              	comparisonOperator + " " + searchValue + " COLLATE INFORMATION_SCHEMA.CASE_INSENSITIVE)";
        }
        else
        {
        	if (comparisonOperator.equals("="))
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (user_fkey IS NULL))";
        	}
        	else
        	{
        		return "(SELECT virtual_row_id FROM virtual_field WHERE virtual_column_id = " +
        		    columnPK + " AND (user_fkey IS NOT NULL))";
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
        setComponentType("DataAccessUserColumn");
    }


    //************** Binding Cover Methods **************\\


}

