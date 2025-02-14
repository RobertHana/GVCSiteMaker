// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import java.math.*;
import java.text.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * Implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessCalculatedColumn showing a plain
 * character string. It also implements the edit function for selecting the format for numbers and timestamps
 * and the true and false titles for boolean values.
 *
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessCalculatedColumn extends DataAccessColumn
{
    protected static final String CHECKED_TITLE_KEY = "checkedTitle";
    protected static final String UNCHECKED_TITLE_KEY = "unCheckedTitle";

    private NSArray formatPatterns;
    private String aFormatPattern;
    private Object exampleValue;
    private String expressionType;


    public DataAccessCalculatedColumn(WOContext context)
    {
        super(context);
    }



    /**
     * Clears cached values
     *
     * @see com.gvcsitemaker.componentprimitives.ComponentPrimitive#reset()
     */
    public void reset()
    {
        super.reset();
        formatPatterns = null;
        aFormatPattern = null;
        exampleValue = null;
        expressionType = null;
    }



    /**
     * Returns the type of the result of evaluating the formula for this column.  This is used to
     * select the rest of the functionality on this page.
     *
     * @return the type of the result of evaluating the formula for this column.
     */
    public String expressionType()
    {
        if (expressionType == null)
        {
            expressionType = dataAccessCalculatedColumn().calculatedColumn().expressionType();
        }

        return expressionType;
        /** ensure [expected_type] ColumnType.NumberColumnType.equals(Result) ||
                                   ColumnType.TimestampColumnType.equals(Result) ||
                                   ColumnType.StringColumnType.equals(Result) ||
                                   ColumnType.BooleanColumnType.equals(Result);
         **/
    }



    /**
     * Returns the list of format patterns to allow the user to select from.
     *
     * @return the list of format patterns to allow the user to select from.
     */
    public NSArray formatPatterns()
    {
        /** require [expected_type] ColumnType.NumberColumnType.equals(expressionType()) ||
                                    ColumnType.TimestampColumnType.equals(expressionType());  **/
        if (formatPatterns == null)
        {

            if (ColumnType.NumberColumnType.equals(expressionType()))
            {
                setFormatPatterns(((SMApplication)application()).properties().arrayPropertyForKey("DataAccessNumberFormats"));
            }
            else if (ColumnType.TimestampColumnType.equals(expressionType()))
            {
                setFormatPatterns(((SMApplication)application()).properties().arrayPropertyForKey("DataAccessDateFormats"));
            }
            else throw new RuntimeException("Unhandled expression result type: " + expressionType());
        }

        return formatPatterns;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     *
     * @return the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     */
    protected Object exampleValue()
    {
        /** require [expected_type] ColumnType.NumberColumnType.equals(expressionType()) ||
                                    ColumnType.TimestampColumnType.equals(expressionType());  **/
        if (exampleValue == null)
        {
            if (ColumnType.NumberColumnType.equals(expressionType()))
            {
                exampleValue = new BigDecimal("987654321.123456789");
            }
            else if (ColumnType.TimestampColumnType.equals(expressionType()))
            {
                exampleValue = new NSTimestamp();
            }
            else throw new RuntimeException("Unhandled expression result type: " + expressionType());
        }

        return exampleValue;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the exampleValue() formatted according to aFormatPattern().  This is to use in a WOPopUp to indicate what each format in formatPatterns() does.
     *
     * @return the exampleValue() formatted according to aFormatPattern().
     */
    public String formatExample()
    {
        /** require [has_aFormatPattern] aFormatPattern() != null;
                    [expected_type] ColumnType.NumberColumnType.equals(expressionType()) ||
                                    ColumnType.TimestampColumnType.equals(expressionType());  **/
        return formatterForPattern(aFormatPattern()).format(exampleValue());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a Format configured with aFormatPattern.  The Format is created by createFormatterForPattern(String).
     *
     * @param aPattern the formatting pattern to configure the Format with
     * @return a Format configured with aFormatPattern.
     */
    public Format formatterForPattern(String aPattern)
    {
        /** require [valid_param] aPattern != null;
                    [expected_type] ColumnType.NumberColumnType.equals(expressionType()) ||
                                    ColumnType.TimestampColumnType.equals(expressionType());  **/

        ApplicationProperties appProperties = ((SMApplication)application()).properties();
        Format formatterForPattern = null;

        // In most cases the formatter will have already been created during application initialization. However it is
        // possible that columns were configured with a pattern that was later removed from the config file. In this
        // case the formatter is created on the fly so that values in these columns can be formatted correctly until a
        // more current format is chosen.
        if (appProperties.hasPropertyForKey(aPattern))
        {
            formatterForPattern = (Format) appProperties.propertyForKey(aPattern);
        }
        else
        {
            if (ColumnType.NumberColumnType.equals(expressionType()))
            {
                formatterForPattern = new NSNumberFormatter(aPattern);
            }
            else if (ColumnType.TimestampColumnType.equals(expressionType()))
            {
                formatterForPattern = new NSTimestampFormatter(aPattern);
            }
            else throw new RuntimeException("Unhandled expression result type: " + expressionType());

            appProperties.addPropertyForKey(formatterForPattern, aPattern);
        }

        return formatterForPattern;

        /** ensure
        [valid_result] Result != null;
        [formatter_created] ((SMApplication)application()).properties().hasPropertyForKey(aPattern); **/
    }



    /**
     * Returns the Format to use to format display values in this column or null if no formatter is required.
     *
     * @return the Format to use to format values in this column or null if no formatter is required.
     */
    public Format outputFormatter()
    {
        // Strings don't get formatted
        if (ColumnType.StringColumnType.equals(expressionType()))
        {
            return null;
        }

        if (ColumnType.BooleanColumnType.equals(expressionType()))
        {
            return new BooleanFormatter(dataAccessCalculatedColumn().checkedTitle(), dataAccessCalculatedColumn().unCheckedTitle());
        }

        return formatterForPattern(formatBinding());
    }



    /**
     * @return <code>true</code> if this type of expression uses a formatter
     */
    public boolean canConfigureFormatter()
    {
        return ColumnType.NumberColumnType.equals(expressionType()) || ColumnType.TimestampColumnType.equals(expressionType());
    }



    /**
     * @return <code>true</code> if this type of expression is Boolean
     */
    public boolean isBooleanColumn()
    {
        return ColumnType.BooleanColumnType.equals(expressionType());
    }



    /**
     * @return <code>true</code> if this type of expression is String
     */
    public boolean isStringColumn()
    {
        return ColumnType.StringColumnType.equals(expressionType());
    }



    /**
     * Returns the configured pattern to use when formatting output values in this column.
     *
     * @return the configured pattern to use when formatting output values in this column.
     */
    public String formatBinding()
    {
        /** require [expected_type] ColumnType.NumberColumnType.equals(expressionType()) ||
                                    ColumnType.TimestampColumnType.equals(expressionType());  **/
        if (ColumnType.NumberColumnType.equals(expressionType()))
        {
            return dataAccessCalculatedColumn().numberFormat();
        }
        else if (ColumnType.TimestampColumnType.equals(expressionType()))
        {
            return dataAccessCalculatedColumn().dateFormat();
        }
        else throw new RuntimeException("Unhandled expression result type: " + expressionType());

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Sets the configured pattern to use when formatting output values in this column.
     *
     * @param format the pattern to use when formatting output values in this column
     */
    public void setFormatBinding(String format)
    {
        /** require [non_null_format] format != null;
                    [expected_type] ColumnType.NumberColumnType.equals(expressionType()) ||
                                    ColumnType.TimestampColumnType.equals(expressionType());  **/
        if (ColumnType.NumberColumnType.equals(expressionType()))
        {
            dataAccessCalculatedColumn().setNumberFormat(format);
        }
        else if (ColumnType.TimestampColumnType.equals(expressionType()))
        {
            dataAccessCalculatedColumn().setDateFormat(format);
        }
        else throw new RuntimeException("Unhandled expression result type: " + expressionType());

    }



    /**
     * Overridden to handle validation errors caused by data input when
     * configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.endsWith(CHECKED_TITLE_KEY))
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), CHECKED_TITLE_KEY);

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else if (keyPath.endsWith(UNCHECKED_TITLE_KEY))
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), UNCHECKED_TITLE_KEY);

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Returns the validation error caused by a missing checked title value when
     * configuring this DataAccessBooleanColumn.  Returns null if no validation
     * error occured.
     *
     * @return the validation error caused by a missing checked title value
     */
    public String checkedTitleValidationMessage()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(CHECKED_TITLE_KEY);
    }



    /**
     * Returns the validation error caused by a missing unchecked title value when
     * configuring this DataAccessBooleanColumn.  Returns null if no validation
     * error occured.
     *
     * @return the validation error caused by a missing checked title value
     */
    public String unCheckedTitleValidationMessage()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(UNCHECKED_TITLE_KEY);
    }



    /**
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessColumn.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessCalculatedColumn dataAccessCalculatedColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessCalculatedColumn) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the String to display for this field value when uneditable.
     * Handles formatting and XML escaping if required.
     *
     * @return the String to display for this field value
     */
    public String fieldDisplayValue()
    {
        if (fieldValue() == null)
        {
            return null;
        }

        String fieldValue;

        if (outputFormatter() != null)
        {
            fieldValue = outputFormatter().format(fieldValue());
        }
        else
        {
            fieldValue = fieldValue().toString();
        }

        if (shouldXMLEncodeData())
        {
            fieldValue = ERXStringUtilities.escapeNonXMLChars(fieldValue);
        }

        return fieldValue;
    }


    //*********** Generic Get / Set methods  ***********\\


    public void setFormatPatterns(NSArray newFormatterns) {
        formatPatterns = newFormatterns;
    }

    public String aFormatPattern() {
        return aFormatPattern;
    }
    public void setAFormatPattern(String newFormatPattern) {
        aFormatPattern = newFormatPattern;
    }


}