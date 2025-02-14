/**
 * Implementation of DataAccessRecordIDColumn common to all installations. //
 * Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
 * Arbor, MI 48109 USA All rights reserved. // This software is published under
 * the terms of the Educational Community License (ECL) version 1.0, // a copy
 * of which has been included with this distribution in the LICENSE.TXT file.
 * 
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import java.math.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/*
 * * DataAccessRecordIDColumn provides easy access to the virtualRowID attribute
 * of a VirtualRow.
 */
public class DataAccessRecordIDColumn extends _DataAccessRecordIDColumn
{
    // For the ColumnType
    public static final String ColumnTypeName = "RecordID";


    /**
     * Factory method to create new instances of DataAccessRecordIDColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessRecordIDColumn or a subclass.
     */
    public static DataAccessRecordIDColumn newDataAccessRecordIDColumn()
    {
        return (DataAccessRecordIDColumn) SMEOUtils.newInstanceOf("DataAccessRecordIDColumn");

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
        if (!SMApplication.smApplication().isUsingIntegerPKs())
        {
            return ERXStringUtilities.byteArrayToHexString(((NSData) value).bytes());
        }
        return ((BigDecimal) value).toString();
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
        /** require  [valid_column_param] column != null; **/

        if (value == null)
        {
            return null;
        }
        else
        {
            boolean isInteger = (value.length() < 48) && (StringAdditions.isInteger(value));

            if (SMApplication.smApplication().isUsingIntegerPKs())
            {
                if ( ! isInteger)
                {
                    throw new NSValidation.ValidationException("The value is not a number.", value, "value");
                }
                return new BigDecimal(value);
            }
            else
            {
                if (isInteger)
                {
                    value = ERXStringUtilities.leftPad(value, '0', 48);
                }
                return new NSData(ERXStringUtilities.hexStringToByteArray(value));
            }
        }
    }



    /**
     * Returns a qualifier for the given column and comparison type that is appropriate for this column type.  The record id type, in particular, handles "like" queries by changing the "like" operator to "=".
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

        // Handle "like" queries by changing them to equals
        if (comparisonOperator.equals(DataAccessSearchCriterion.CONTAINS_COMPARISON_OPERATOR))
        {
            comparisonOperator = DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_OPERATOR;
        }

        if (value != null)
        {
            return EOQualifier.qualifierWithQualifierFormat(SMVirtualTable.NORMALIZED_RECORD_ID_COLUMN_NAME + " " + comparisonOperator + " %@", new NSArray(
                    value));
        }
        else
        {
            return EOQualifier.qualifierWithQualifierFormat(SMVirtualTable.NORMALIZED_RECORD_ID_COLUMN_NAME + " " + comparisonOperator + " %@", new NSArray(
                    NSKeyValueCoding.NullValue));
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns SQL for the given column and comparison type that is appropriate for this column type.
     * The record id type, in particular, handles "like" queries by changing the "like" operator to "=".
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

        // convert operators to SQL operators
        if (comparisonOperator.equals("!="))
        {
            comparisonOperator = "<>";
        }
        // Handle "like" queries by changing them to equals
        if (comparisonOperator.equals("like"))
        {
            comparisonOperator = "=";
        }

        if (value != null)
        {
            EOModel model = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
            EOAdaptor adaptor = EOAdaptor.adaptorWithModel(model);
            EOEntity columnEntity = model.entityNamed("VirtualRow");
            EOSQLExpression columnExpression = new EOSQLExpressionFactory(adaptor).createExpression(columnEntity);

            if (value instanceof NSData)
            {
                EOAttribute columnAttribute = columnEntity.attributeNamed("virtualRowID");
                value = columnExpression.formatValueForAttribute(value, columnAttribute);
            }

            return "(SELECT virtual_row_id FROM virtual_row WHERE virtual_row_id " + comparisonOperator + " " + value + ")";
        }
        else
        {
            if (comparisonOperator.equals("="))
            {
                // will never match anything
                return "(-1)";
            }
            else
            {
                // matches all rows
                return "(SELECT virtual_row_id FROM virtual_row)";
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
        setComponentType("DataAccessRecordIDColumn");
    }



    /**
     * Returns "virtualRowID" so that this column can directly access that attibute of VirtualRow rather than a VirtualField named RecordID.
     *
     * @return "virtualRowID" so that this column can directly access that attibute of VirtualRow rather than a VirtualField named RecordID.
     */
    public String normalizedFieldName()
    {
        return SMVirtualTable.NORMALIZED_RECORD_ID_COLUMN_NAME;
    }



    /**
     * Returns the constant field type appropriate for record ID columns.
     *
     * @return the constant field type appropriate for record ID columns
     */
    public String fieldType()
    {
        /** require [has_column] column() != null; **/
        return "Hex Number";
        /** ensure [result_valid] Result != null; **/
    }



}
