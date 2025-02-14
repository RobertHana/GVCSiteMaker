// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import java.text.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * Implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessTimestampColumn
 * showing a Text input, or plain character string as configured.  It also implements
 * the edit function for the default value and width.
 *
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessTimestampColumn extends DataAccessColumnWithFormatter
{

    protected NSTimestamp exampleValue;

    /**
     * Designated constructor
     */
    public DataAccessTimestampColumn(WOContext context)
    {
        super(context);
        exampleValue = new NSTimestamp();
    }



    /**
     * Returns componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessTimestampColumn.
     *
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessTimestampColumn.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessTimestampColumn dataAccessTimestampColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessTimestampColumn) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the validation error caused by an input value in this DataAccessTimestampColumn.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an input value in this DataAccessTimestampColumn.  Returns null if no validation error occured.
     */
    public String inputValidationFailure()
    {
        /** require
        [display_mode] isInDisplayMode();
        [parent_is_ValidationMessageStore] parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(dataAccessColumn().normalizedFieldName());
    }



    /**
     * Returns the validation error caused by an invalid width value value when configuring this DataAccessTimestampColumn.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an invalid width value value when configuring this DataAccessTimestampColumn or null if no validation error occured.
     */
    public String widthValidationFailure()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(dataAccessColumn().normalizedFieldName() + ".width");
     }



    /**
     * Overridden to handle validation errors caused by data input when displaying or configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.equals("fieldValue"))   // Display Mode
        {
            validationMessageStore().registerValidationFailureForKey(value + " was not entered in the correct date/time format.", dataAccessColumn().normalizedFieldName());
        }
        else if (keyPath.endsWith("width"))    // Edit Mode
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), dataAccessColumn().normalizedFieldName() + ".width");

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Sets formatPatterns to the list of format patterns to display in the UI for the user to select from.
     */
    protected void loadFormatPatterns()
    {
        setFormatPatterns(((SMApplication)application()).properties().arrayPropertyForKey("DataAccessDateFormats"));
        /** ensure [format_patterns_loaded] formatPatterns() != null;  **/
    }



    /**
     * Returns the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     *
     * @return the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     */
    protected Object exampleValue()
    {
        return exampleValue;
    }



    /**
     * Returns the pattern to use when formatting output values in this column.
     *
     * @return the pattern to use when formatting output values in this column.
     */
    public String outputFormatPattern()
    {
        return dataAccessTimestampColumn().dateFormat();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a sub-class for Format appropriate for this column, configured with aFormatPattern.
     *
     * @param aPattern - the formatting pattern to configure the Format with
     * @return a sub-class for Format appropriate for this column, configured with aFormatPattern.
     */
    public Format createFormatterForPattern(String aPattern)
    {
        //** require [valid_param] aFormatPattern != null;  **/
        return new NSTimestampFormatter(aPattern);
        //** ensure [valid_result] Result != null;  **/
    }


    /**
     * Sets the field value ensuring that time is only accurate to second.
     */
    public void setFieldValue(Object newValue)
    {
        if (newValue != null)
        {
            super.setFieldValue(Date.accurateToSecond((NSTimestamp)newValue));
        }
        else
        {
            super.setFieldValue(newValue);
        }
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

        String fieldValue = outputFormatter().format(fieldValue());

        if (shouldXMLEncodeData())
        {
            fieldValue = ERXStringUtilities.escapeNonXMLChars(fieldValue);
        }

        return fieldValue;
    }



    //*********** Generic Get / Set methods  ***********\\


}
