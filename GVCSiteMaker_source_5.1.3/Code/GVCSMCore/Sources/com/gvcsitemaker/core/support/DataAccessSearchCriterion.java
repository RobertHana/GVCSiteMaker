// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.pagecomponent.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;



/**
 * DataAccessSearchCriterion represents one element in a search of data in a database table.  It holds the column being compared, the type of comparision, and the value being compared to.  It also holds an optional join condition if it is not the first criterion.  This is the non-EO version of DataAccessColumnSearchValue.
 */
public class DataAccessSearchCriterion extends Object implements NSKeyValueCoding, NSKeyValueCodingAdditions
{

    public static final String AND_JOIN_CONDITION = "AND";
    public static final String OR_JOIN_CONDITION = "OR";
    public static final String AND_NOT_JOIN_CONDITION = "AND NOT";
    public static final String OR_NOT_JOIN_CONDITION = "OR NOT";
    public static final NSArray JOIN_CONDITIONS = new NSArray(new String[] {
            AND_JOIN_CONDITION,
            OR_JOIN_CONDITION,
            AND_NOT_JOIN_CONDITION,
            OR_NOT_JOIN_CONDITION});

    public static final String IS_LESS_THAN_COMPARISON_TYPE = "is less than";
    public static final String IS_LESS_THAN_OR_EQUAL_TO_COMPARISON_TYPE = "is less than or equal to";
    public static final String IS_EQUAL_TO_COMPARISON_TYPE = "is equal to";
    public static final String IS_NOT_EQUAL_TO_COMPARISON_TYPE = "is not equal to";
    public static final String IS_GREATER_THAN_COMPARISON_TYPE = "is greater than";
    public static final String IS_GREATER_THAN_OR_EQUAL_TO_COMPARISON_TYPE = "is greater than or equal to";
    public static final String CONTAINS_COMPARISON_TYPE = "contains (text only)";
    public static final NSArray COMPARISON_TYPES = new NSArray(new String[] {
            IS_LESS_THAN_COMPARISON_TYPE,
            IS_LESS_THAN_OR_EQUAL_TO_COMPARISON_TYPE,
            IS_EQUAL_TO_COMPARISON_TYPE,
            IS_NOT_EQUAL_TO_COMPARISON_TYPE,
            IS_GREATER_THAN_COMPARISON_TYPE,
            IS_GREATER_THAN_OR_EQUAL_TO_COMPARISON_TYPE,
            CONTAINS_COMPARISON_TYPE});

    public static final String IS_LESS_THAN_COMPARISON_OPERATOR = "<";
    public static final String IS_LESS_THAN_OR_EQUAL_TO_COMPARISON_OPERATOR = "<=";
    public static final String IS_EQUAL_TO_COMPARISON_OPERATOR = "=";
    public static final String IS_NOT_EQUAL_TO_COMPARISON_OPERATOR = "!=";
    public static final String IS_GREATER_THAN_COMPARISON_OPERATOR = ">";
    public static final String IS_GREATER_THAN_OR_EQUAL_TO_COMPARISON_OPERATOR = ">=";
    public static final String CONTAINS_COMPARISON_OPERATOR = "like";
    public static final String NOT_CONTAINS_COMPARISON_OPERATOR = "not like";
    public static final NSArray COMPARISON_OPERATORS = new NSArray(new String[] {
            IS_LESS_THAN_COMPARISON_OPERATOR,
            IS_LESS_THAN_OR_EQUAL_TO_COMPARISON_OPERATOR,
            IS_EQUAL_TO_COMPARISON_OPERATOR,
            IS_NOT_EQUAL_TO_COMPARISON_OPERATOR,
            IS_GREATER_THAN_COMPARISON_OPERATOR,
            IS_GREATER_THAN_OR_EQUAL_TO_COMPARISON_OPERATOR,
            CONTAINS_COMPARISON_OPERATOR});
    public static final NSArray INVERTED_COMPARISON_OPERATORS = new NSArray(new String[] {
    		IS_GREATER_THAN_OR_EQUAL_TO_COMPARISON_OPERATOR,
    		IS_GREATER_THAN_COMPARISON_OPERATOR,
            IS_NOT_EQUAL_TO_COMPARISON_OPERATOR,
            IS_EQUAL_TO_COMPARISON_OPERATOR,
            IS_LESS_THAN_OR_EQUAL_TO_COMPARISON_OPERATOR,
            IS_LESS_THAN_COMPARISON_OPERATOR,
            NOT_CONTAINS_COMPARISON_OPERATOR});

    public static final NSDictionary OPERATOR_DICTIONARY = new NSDictionary(COMPARISON_OPERATORS, COMPARISON_TYPES);
    public static final NSDictionary INVERTED_OPERATOR_DICTIONARY = new NSDictionary(INVERTED_COMPARISON_OPERATORS, COMPARISON_TYPES);

    public static final String CURRENT_USER_SEARCH_TOKEN = "<CurrentUser>";
    public static final String CURRENT_DATE_SEARCH_TOKEN = "<CurrentDate>";
    public static final String BLANK_SEARCH_TOKEN = "<Blank>";

    protected DataAccessSearchCriteria searchCriteria;

    protected String joinCondition;
    protected String comparisonType;
    protected Column column;
    protected String value;



    /**
     * Returns one of the com.gvcsitemaker.core.pagecomponent.DataAccess*Column classes based on
     * the type of the passed Column.
     *
     * @param column Column determining which PageComponent sub-class is being returned
     * @return one of the com.gvcsitemaker.core.pagecomponent.DataAccess*Column classes
     */
    public static Class validatingColumnClass(Column column)
    {
        /** require [has_column] column != null; **/
        try
        {
            return validatingColumnClass(column.type().name());
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns one of the com.gvcsitemaker.core.pagecomponent.DataAccess*Column classes based on
     * the passed Column type name.
     *
     * @param column Column determining which PageComponent sub-class is being returned
     * @return one of the com.gvcsitemaker.core.pagecomponent.DataAccess*Column classes
     */
    public static Class validatingColumnClass(String columnTypeName)
    {
        /** require [has_columnTypeName] columnTypeName != null; **/
        try
        {
            return Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" +
                                 columnTypeName + "Column");
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Validate the value and coerces it to the correct type.
     *
     * @return the validated and coerced value
     * @exception NSValidation.ValidationException thrown in there is a validation
     * error in the value
     */
    public static Object validatedAndCoercedValue(DataAccess dataAccessComponent,
                                                  Column column,
                                                  String valueToValidate)
                                                  throws NSValidation.ValidationException
    {
        /** require [has_column] column != null;  [valid_param] valueToValidate != null;    **/

        Object validatedAndCoercedValue;

        try
        {
            if (valueToValidate.trim().equals(BLANK_SEARCH_TOKEN))
            {
                validatedAndCoercedValue = null;
            }
            else
            {
                validatedAndCoercedValue = NSSelector.
                    invoke("validateColumnSearchValue",
                           new Class[] {DataAccess.class, Column.class, String.class},
                           validatingColumnClass(column),
                           new Object[] {dataAccessComponent, column, valueToValidate});
            }
        }
        catch (NSValidation.ValidationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        return validatedAndCoercedValue;
    }



    /**
     * @param comparisonType one of the elements of COMPARISON_TYPES
     *
     * @return an EOQualfier comparison operator based on this comparisonType
     */
    public static String operatorForComparison(String comparisonType)
    {
        /** require [valid_comparisonOperator_param] comparisonType != null;
                    [known_comparisonOperator] COMPARISON_TYPES.containsObject(comparisonType);   **/
        return (String)OPERATOR_DICTIONARY.objectForKey(comparisonType);
        /** ensure [valid_result] Result != null;
                   [known_operator_result] COMPARISON_OPERATORS.containsObject(Result);  **/
    }



    /**
     * Designated constructor.
     *
     * @param aSearchCriteria - the DataAccessSearchCriteria instance owning this criterion
     */
    public DataAccessSearchCriterion(DataAccessSearchCriteria aSearchCriteria)
    {
        super();
        /** require [valid_param] aSearchCriteria != null;  **/
        searchCriteria = aSearchCriteria;
    }



    /**
     * Returns value() coerced to an appropriate type for use in an EOQualifier.
     *
     * @param currentUser - the User for VirtualUserField comparisons
     *
     * @return value() coerced to an appropriate type for use in an EOQualifier.
     */
    public Object searchValue(User currentUser)
    {
        /** require
        [valid_user] currentUser != null;
        [has_column] column() != null;
        [has_value] value() != null; **/

        // Coerce search value type
        Object searchValue;

        /*
         * If we're searching for a <CurrentUser>, munge the searchValue to
         * be the current user's id. Not sure how else to do this...
         */
        if ((value() != null) &&
            value().trim().equals(CURRENT_USER_SEARCH_TOKEN))
        {
            searchValue = currentUser.userID();
        }
        else
        {
            // Coerce search value type
            searchValue = validatedAndCoercedValue(value());
        }

        return searchValue;
    }



    /**
     * Returns an EOQualfier based on this search criterion.
     *
     * @param currentUser - the User for VirtualUserField comparisons
     *
     * @return an EOQualfier based on this search criterion.
     */
    public EOQualifier qualifier(User currentUser)
    {
        /** require [has_column] column() != null;  [has_value]  value() != null;   [valid_user] currentUser != null;  **/

        String comparisonOperator = (String)OPERATOR_DICTIONARY.objectForKey(comparisonType());
        try
        {
            return (EOQualifier)NSSelector.invoke("searchQualifier", new Class[] {Column.class, String.class, Object.class},
                                                  dataAccessColumnClass(),
                                                  new Object[] {column(), comparisonOperator, searchValue(currentUser)});
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns SQL based on this search criterion.
     *
     * @param currentUser the User for VirtualUserField comparisons
     * @param shouldInvert true iff we should invert the comparison (i.e. change A to !A)
     * @return SQL based on this search criterion.
     */
    public String sql(User currentUser, boolean shouldInvert)
    {
        /** require
        [has_column] column() != null;
        [has_value]  value() != null;
        [valid_user] currentUser != null; **/

        String comparisonOperator;
        if ( ! shouldInvert)
        {
        	comparisonOperator = (String)OPERATOR_DICTIONARY.objectForKey(comparisonType());
        }
        else
        {
        	comparisonOperator = (String)INVERTED_OPERATOR_DICTIONARY.objectForKey(comparisonType());
        }

        try
        {
            return (String)NSSelector.invoke("searchSQL",
            		new Class[] {Column.class, String.class, Object.class},
            		dataAccessColumnClass(),
            		new Object[] {column(), comparisonOperator, searchValue(currentUser)});
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        /** ensure [valid_result] Result != null; **/
    }



    public Class dataAccessColumnClass()
    {
        /** require [has_column] column() != null;  **/
        try
        {
            return Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" + column.type().name() + "Column");
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * EOF Validation method for the joinCondition part of the criterion.
     */
    public Object validateJoinCondition(Object valueToValidate) throws NSValidation.ValidationException
    {
        if ((valueToValidate != null) &&  ! JOIN_CONDITIONS.containsObject(valueToValidate))
        {
            throw new NSValidation.ValidationException("Join condition is not valid.", this, "column");
        }

        return valueToValidate;
    }



    /**
     * EOF Validation method for the criterionColumn part of the criterion.
     */
    public Object validateColumn(Object valueToValidate) throws NSValidation.ValidationException
    {
        if (valueToValidate == null)
        {
            throw new NSValidation.ValidationException("Field name must be selected.", this, "column");
        }

        return valueToValidate;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * EOF Validation method for the criterionComparisonType part of the criterion.
     */
    public Object validateComparisonType(Object valueToValidate) throws NSValidation.ValidationException
    {
        if (valueToValidate == null)
        {
            throw new NSValidation.ValidationException("The comparison type must be selected.", this, "comparisonType");
        }

        return valueToValidate;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * EOF Validation method for the value part of the criterion.
     */
    public String validateValue(String valueToValidate) throws NSValidation.ValidationException
    {
        if (valueToValidate == null)
        {
            throw new NSValidation.ValidationException("The value must be entered.", this, "value");
        }

        if (column() != null)
        {
            try
            {
                // We want the string value in the criteria, so ignore the coerced value
                validatedAndCoercedValue(valueToValidate);
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

        return valueToValidate;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Validate the value and coerces it to the correct type.
     *
     * @return the validated and coerced value
     * @exception NSValidation.ValidationException thrown in there is a validation error in the value
     */
    public Object validatedAndCoercedValue(String valueToValidate) throws NSValidation.ValidationException
    {
        /** require
        [valid_param] valueToValidate != null;
        [has_column] column() != null; **/

        return validatedAndCoercedValue(searchCriteria().dataAccessComponent(), column(), valueToValidate);
    }



    /**
     * Returns <code>true</code> if this criterion is valid.  A valid criterion has a column, value, comparision type, and join condition (if this is not the first criterion).
     *
     * @return <code>true</code> if this criterion is valid
     */
    public boolean isValid()
    {
        boolean isValid = true;
        try
        {
            validateJoinCondition(joinCondition());
            validateColumn(column());
            validateComparisonType(comparisonType());
            validateValue(value());
        }
        catch (NSValidation.ValidationException e)
        {
            isValid = false;
        }

        return isValid;
    }



    /**
     * Returns <code>true</code> if this is the first criterion, <code>false</code> otherwise.
     *
     * @return <code>true</code> if this is the first criterion, <code>false</code> otherwise.
     */
    public boolean isFirstCriterion()
    {
        return searchCriteria().isFirstCriterion(this);
    }



    /**
     * Returns the join condition for this criterion, or null if this is the first criterion.
     *
     * @return the join condition for this criterion, or null if this is the first criterion.
     */
    public String joinCondition()
    {
        return joinCondition;
        /** ensure [valid_join_condition] (joinCondition == null) || JOIN_CONDITIONS.containsObject(joinCondition);  **/
    }


    /**
     * Sets the join condition for this criterion.  The join can be null if this is the first criterion.
     *
     * @param newJoinCondition - the join condition for this criterion.
     */
    public void setJoinCondition(String newJoinCondition)
    {
        /** require [valid_join_condition] (newJoinCondition == null) || JOIN_CONDITIONS.containsObject(newJoinCondition);  **/
        joinCondition = newJoinCondition;
    }



    /**
     * Returns the comparision type for this criterion.
     *
     * @return the comparision type for this criterion.
     */
    public String comparisonType()
    {
        return comparisonType;
        /** ensure [valid_comparison_type] (comparisonType == null) || COMPARISON_TYPES.containsObject(comparisonType);  **/
    }


    /**
     * Sets the comparision type for this criterion.
     *
     * @param newComparisonType - the comparision type for this criterion.
     */
    public void setComparisonType(String newComparisonType)
    {
        /** require [valid_join_condition] (newComparisonType == null) || COMPARISON_TYPES.containsObject(newComparisonType);  **/
        comparisonType = newComparisonType;
    }



    /**
     * Returns the Column compared in this criterion.
     *
     * @return the Column compared in this criterion.
     */
    public Column column()
    {
        return column;
    }


    /**
     * Sets the Column compared in this criterion.
     *
     * @param newColumn the Column compared in this criterion
     */
    public void setColumn(Column newColumn)
    {
        column = newColumn;
    }



    /**
     * Returns the value compared to for this criterion.
     *
     * @return the value compared to for this criterion
     */
    public String value()
    {
        return value;
    }


    /**
     * Sets the value compared to in this criterion.
     *
     * @param newValue the value compared to in this criterion
     */
    public void setValue(String newValue)
    {
        value = newValue;
    }



    /**
     * Returns the DataAccessSearchCriteria that owns this criterion.
     *
     * @return the DataAccessSearchCriteria that owns this criterion.
     */
    public DataAccessSearchCriteria searchCriteria()
    {
        return searchCriteria;
    }



    /**
     * Returns a String representation useful for debugging.
     *
     * @return a String representation useful for debugging.
     */
    public String toString()
    {
        return (super.toString() +
                ", Join Condition: " +  joinCondition() +
                ", Column: " + (column() != null ? column().name() : null) +
                ", Comparison Type: " + comparisonType() +
                ", Value: " + value() +
                ", isFirstCriterion: " + isFirstCriterion() +
                ", isValid: " + isValid());
    }



    /**
     * Conformance to NSKeyValueCoding.
     */
    public Object valueForKey(String key)
    {
        return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
    }



    /**
     * Conformance to NSKeyValueCoding.
     */
    public void takeValueForKey(Object aValue, String key)
    {
        NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, aValue, key);
    }



    /**
     * Conformance to NSKeyValueCodingAdditions.
     */
    public Object valueForKeyPath(String keyPath)
    {
        return NSKeyValueCodingAdditions.DefaultImplementation.valueForKeyPath(this, keyPath);
    }


    /**
     * Conformance to NSKeyValueCodingAdditions.
     */
    public void takeValueForKeyPath(Object aValue, String keyPath)
    {
        NSKeyValueCodingAdditions.DefaultImplementation.takeValueForKeyPath(this, aValue, keyPath);
    }


}
