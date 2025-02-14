package net.global_village.virtualtables;

import java.util.*;
import java.util.regex.*;

import ognl.*;
import ognl.webobjects.*;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;



/**
 * A calculated column is defined by an expression which is evaluated on the other fields in a row.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 13$
 */
public class VirtualCalculatedColumn extends _VirtualCalculatedColumn
{
    protected static final String VirtualCalculatedColumnRestrictingValue = "VirtualCalculatedColumn";

    /** Pattern to match first part of ParseException message. */
    Pattern ParseExceptionMessagePattern = Pattern.compile("(.*)\\..*");



    private static NSMutableArray columnTypeNamesForCalculation = null;


    /**
     * Returns names of ColumnTypes that can be used in a VirtualCalculatedColumn expression.
     *
     * @return list of names of ColumnTypes that can be used in a VirtualCalculatedColumn expression
     */
    public static NSArray columnTypeNamesForCalculation()
    {
        if (columnTypeNamesForCalculation == null)
        {
            columnTypeNamesForCalculation= new NSMutableArray(new String[]{ColumnType.StringColumnType,
                                                                            ColumnType.MemoColumnType,
                                                                            ColumnType.NumberColumnType,
                                                                            ColumnType.TimestampColumnType,
                                                                            ColumnType.BooleanColumnType,
                                                                            ColumnType.CalculatedColumnType,
                                                                            ColumnType.RecordIDColumnType});
        }

        return columnTypeNamesForCalculation;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Adds a column type name to the list of names of ColumnTypes that can be used in a VirtualCalculatedColumn expression.
     *
     * @param columnType column type name to add to the list of names of ColumnTypes that can be used in a VirtualCalculatedColumn expression
     * @see #columnTypeNamesForCalculation()
     */
    public static void addColumnTypeForCalculation(String columnType)
    {
        /** require [valid_columnType] columnType != null;  **/
        columnTypeNamesForCalculation();
        columnTypeNamesForCalculation.addObject(columnType);
        /** ensure [type_recorded] columnTypeNamesForCalculation().containsObject(columnType);  **/
    }



    /**
     * Returns <code>true</code> if the passed column can be used in a VirtualCalculatedColumn expression.
     * Columns whose ColumnType name() is in ColumnTypeNamesForCalculation can be used.  VirtualLookupColumns can
     * be used if their lookupColumn()'s ColumnType name() is in ColumnTypeNamesForCalculation
     *
     * @param column the Column to check
     * @return <code>true</code> if the passed column can be used in a VirtualCalculatedColumn expression
     */
    public static boolean canUseColumnInCalculation(Column column)
    {
        /** require [valid_column] column != null; **/
        if (columnTypeNamesForCalculation().containsObject(column.type().name()))
        {
            return true;
        }

        if ((column instanceof VirtualLookupColumn) && canUseColumnInCalculation(((VirtualLookupColumn)column).lookupColumn()) )
        {
            return true;
        }

        return false;
    }



    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(VirtualCalculatedColumnRestrictingValue);
        setExpression("true");
    }




    /**
     * Returns <code>false</code> as calculated values are not editable.
     *
     * @return <code>false</code> as calculated values are not editable
     */
    public net.global_village.foundation.GVCBoolean isEditableColumn()
    {
        return net.global_village.foundation.GVCBoolean.no();
    }



    /**
     * Evaluates an expression with an object and a calculation context.  Null is returned if the
     * left part of any keypath has a null value for any column.
     *
     * @param expression the expression to evaluate
     * @param context the calculation context to use for evaluation
     * @param object the Object to take the values from
     *
     * @return the result of evaluating the expression
     *
     * @throws EOFValidationException if the expression is not valid
     */
    protected Object evaluateExpressionWithContextInObject(String expression,
                                                           Map context,
                                                           Object object)
    {
        /** require [valid_context] context != null;
                    [valid_object] object != null; **/

        if (StringAdditions.isEmpty(expression))
        {
            throw new EOFValidationException(this, "expression", "nullNotAllowed", null);
        }

        Object value = null;
        try
        {
            value = Ognl.getValue(expression, context, object);
        }
        catch (EOFValidationException e)
        {
            throw e;
        }
        catch (NullPointerException e)
        {
            return null;
        }
        catch (OgnlException ex)
        {
            if (ex instanceof ExpressionSyntaxException && ex.getReason() instanceof ParseException)
            {
                Matcher m = ParseExceptionMessagePattern.matcher(ex.getReason().getMessage());
                m.find();
                throw new EOFValidationException(this, "expression", "parseError", m.group(1));
            }

            // this happens when something evals to null, which happens if the
            // calc references a field that has a null value
            if (ex.getMessage().indexOf("source is null") > -1)
            {
                return null;
            }

            throw new EOFValidationException(this, "expression", "syntaxError", null);
        }
        catch (ArithmeticException ae)
        {
            throw new EOFValidationException(this, "expression", "divideByZero", null);
        }
        catch (Exception e)
        {
            throw new EOFValidationException(this, "expression", "syntaxError", null);
        }
        return value;
   }



    /**
     * Calculates the value in this row.  Null is returned if the row has a null value for any column
     * referenced in the expression.
     *
     * @param row the VirtualRow to take the value from
     * @return this field's value
     * @throws EOFValidationException if the expression is not valid
     */
    public Object valueInRow(VirtualRow row)
    {
        /** require [valid_row] row != null; **/

        return evaluateExpressionWithContextInObject(expression(), row.calculationContext(), row);
   }



    /**
     * Calculates the value to export for this column in the passed row.
     *
     * @param row the VirtualRow to take the value from
     * @param sender an object that this method can use to further distinguish what should be done with the value
     *
     * @return the value to export for this column in the passed row
     */
    public Object exportValueInRow(VirtualRow row, Object sender)
    {
        /** require [valid_row] row != null; **/
        return valueInRow(row);
    }



    /**
     * Returns null so that calculated values are ignored on import.
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @param value the value we got during import, possibly formatted by a formatter
     * @return null so that calculated values are ignored on import
     * @exception EOFValidationException thrown if <code>value</code> is not a valid value for this column
     */
    public Object valueForImportedValue(Object sender, Object value) throws EOFValidationException
    {
        /** require [valid_value_param] value != null; **/
        return null;
    }



    /**
     * Calculates the value in this row as part of the evaluation of an expression.
     *
     * @see Column#representativeValue()
     *
     * @return a value representative of this column type
     */
    public Object representativeValue()
    {
        /** require [table_context_established] table().calculationContext() != null;  **/
        return evaluateExpressionWithContextInObject(expression(), table().calculationContext(), table());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Calculates the value in this row based on representative values for the columns.  This
     * calculation and value is used to validate calculated columns without needing to refer
     * to data in the table.
     *
     * @see Column#representativeValue()
     *
     * @return a value representative of this column type
     */
    public Object exampleValue()
    {
        try
        {
            table().setCalculationContext(newCalculationContext());
            return evaluateExpressionWithContextInObject(expression(), table().calculationContext(), table());
        }
        finally
        {
            table().setCalculationContext(null);
        }
    }



    /**
     * Maps the class of result to one of the ColumnType types: StringColumnType, NumberColumnType
     * BooleanColumnType or TimestampColumnType.
     *
     * @param result object to match to a ColumnType type
     * @return one of ColumnType.StringColumnType, ColumnType.NumberColumnType, ColumnType.BooleanColumnType, ColumnType.TimestampColumnType
     * @throws EOFValidationException if result is not one of the expected classes
     */
    public String expressionType(Object result)
    {
        /** require [non_null_value] result != null;  **/
        if (result instanceof String)
        {
            return ColumnType.StringColumnType;
        }
        else if (result instanceof java.lang.Number)
        {
            return ColumnType.NumberColumnType;
        }
        else if (result instanceof java.lang.Boolean)
        {
            return ColumnType.BooleanColumnType;
        }
        else if (result instanceof java.sql.Timestamp || result instanceof java.util.Calendar)
        {
            return ColumnType.TimestampColumnType;
        }

        throw new EOFValidationException(this, "expression", "unexpectedType", result);

        /** ensure [valid_result] Result != null;
                   [expected_type] Result.equals(ColumnType.StringColumnType) ||
                                   Result.equals(ColumnType.NumberColumnType) ||
                                   Result.equals(ColumnType.BooleanColumnType) ||
                                   Result.equals(ColumnType.TimestampColumnType);     **/
    }



    /**
     * Evaluates the expression using representative values and maps the class of result to one of the ColumnType types:
     * StringColumnType, NumberColumnType, BooleanColumnType or TimestampColumnType.
     *
     * @return one of ColumnType.StringColumnType, ColumnType.NumberColumnType, ColumnType.BooleanColumnType, ColumnType.TimestampColumnType
     */
    public String expressionType()
    {
        return expressionType(exampleValue());
        /** ensure [valid_result] Result != null;
                   [expected_type] Result.equals(ColumnType.StringColumnType) ||
                                   Result.equals(ColumnType.NumberColumnType) ||
                                   Result.equals(ColumnType.BooleanColumnType) ||
                                   Result.equals(ColumnType.TimestampColumnType);     **/
    }



    /**
     * Returns instance of ColumnType matching expressionType().
     *
     * @return instance of ColumnType matching expressionType()
     */
    public ColumnType expressionColumnType()
    {
        return ColumnType.columnTypeWithName(editingContext(), expressionType());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns an ordered list of columns from this Table which may be used in the formula of a calculated column.
     * This column is excluded from the list.
     *
     * @return an ordered list of columns from this Table which may be used in the formula of a calculated column
     */
    public NSArray columnsAvailableForCalculation()
    {
        NSMutableArray columnsAvailableForCalculation = ((VirtualTable)table()).columnsAvailableForCalculation().mutableClone();
        columnsAvailableForCalculation.removeObject(this);
        return columnsAvailableForCalculation;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns an context to be used when evaluating an expression. This contains a calculatingColumns key that points
     * to a list of columns that are in the process of being evaluated. The calculatingColumns list is used to detect
     * circular expressions. For OGNL, this also contains a classResolver key.  Member access is controlled by
     * WhiteListMemberAccess to prevent attacks via calculated columns.
     *
     * @see WhiteListMemberAccess
     * @see ClassResolver
     * @see VirtualRowPropertyAccessor
     * @return an OGNL context to be used when evaluating an expression.
     */
    public Map newCalculationContext()
    {
        OgnlContext context = new OgnlContext(null, //ClassResolver
                                              null, // TypeConverter
                                              new WhiteListMemberAccess(true),
                                              WOOgnl.factory().newDefaultContext());
        context.put(VirtualRowPropertyAccessor.CalculatingColumns, new NSMutableArray(this));
        return context;
        /** ensure [valid_result] Result != null;
                   [has_classResolver] Result.get("classResolver") != null;
                   [has_calculatingColumns] Result.get(VirtualRowPropertyAccessor.CalculatingColumns) != null;
                   [calculatingColumns_has_this_column] ((NSArray)Result.get(VirtualRowPropertyAccessor.CalculatingColumns)).containsObject(this);
         **/
    }



    /**
     * Validates this expression by calculating it with dummy representative based on the field types.
     *
     * @param newExpression the expression to validate
     * @return newExpression if it is valid
     * @throws EOFValidationException if the expression is not valid
     */
    public Object validateExpression(String newExpression) throws NSValidation.ValidationException
    {
        /** require [valid_newExpression] newExpression != null; **/

        try
        {
            table().setCalculationContext(newCalculationContext());
            expressionType(evaluateExpressionWithContextInObject(newExpression, table().calculationContext(), table()));
        }
        finally
        {
            table().setCalculationContext(null);
        }

        return newExpression;
    }



    /**
     * Overridden to perform custom validations.  Calling super's implementation
     * is a good idea, it is needed when using EO inheritance if the super class has any
     * custom  validations.
     *
     * @see GenericRecord#validateForSave(NSMutableArray)
     * @see #validateForSave()
     *
     * @param exceptions list of exceptions that can be added to
     * @throws ValidationException if validation error are found
     */
    protected void validateForSave(NSMutableArray exceptions) throws ValidationException
    {
        /** require [valid_list] exceptions != null;  **/
        super.validateForSave(exceptions);

        // If the expression for any column changes, then all calculated columns need to be re-validated
        // to ensure that a circular expression is not being formed
        if (hasChanged("expression"))
        {
            for (int i = 0; i < table().columns().count(); i++)
            {
                Column aColumn = (Column)table().columns().objectAtIndex(i);
                if (aColumn instanceof VirtualCalculatedColumn)
                {
                    try
                    {
                        VirtualCalculatedColumn column = (VirtualCalculatedColumn) aColumn;
                        column.validateExpression(column.expression());
                    }
                    catch (NSValidation.ValidationException e)
                    {
                        exceptions.addObject(e);
                    }
                }
            }
        }
    }


}
