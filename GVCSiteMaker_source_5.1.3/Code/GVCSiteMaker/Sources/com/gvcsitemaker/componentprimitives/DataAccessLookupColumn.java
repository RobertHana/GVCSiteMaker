// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import java.math.BigDecimal;
import java.text.Format;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.foundation.ERXStringUtilities;

import net.global_village.virtualtables.VirtualField;


/**
 * Implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn showing a input
 * that may be a popup, or a horizontal, vertical, or grid of radio buttons.  It displays a plain
 * character string as when contents are not editable.  All data (input or static display) is
 * formatted is applicable and so configured.  It also implements the edit function for the default
 * value, formatter (where applicable), and input display type.
 * 
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessLookupColumn extends DataAccessColumnWithFormatter
{

    /** For input popup and default value popup */
    public VirtualField aVirtualField;

    /** Popup, horizontal, vertical, grid modes for UI */
    protected NSArray uiDisplayModes;

    /** Inernal values from <code>com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn</code>
     * for Popup, horizontal, vertical, grid modes for UI */
    protected NSArray internalDisplayModes;



    /**
     * Designated constructor
     */
    public DataAccessLookupColumn(WOContext context)
    {
        super(context);
        uiDisplayModes = new NSArray(new String[] {"Pick List", "Horizontal Radio Buttons", "Vertical Radio Buttons", "Radio Button Grid"});
        internalDisplayModes = new NSArray(new String[] {
            com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.POPUP_DISPLAY_TYPE,
            com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.HORIZONTAL_DISPLAY_TYPE,
            com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.VERTICAL_DISPLAY_TYPE,
            com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.GRID_DISPLAY_TYPE});
    }



    /**
     * Returns componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.
     *
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn dataAccessLookupColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Overridden to return an empty array.  See formatPatterns() for reason why this does nothing.
     */
    protected void loadFormatPatterns()
    {
        setFormatPatterns(NSArray.EmptyArray);
        /** ensure [format_patterns_loaded] formatPatterns != null;  **/
    }



    /**
     * Returns the list of format patterns to allow the user to select from.  formatPatterns() is created on the fly so that this stateless component can handle multiple types of Lookup (date, number, text, etc.).
     *
     * @return the list of format patterns to allow the user to select from.
     */
    /** @TypeInfo java.lang.String */
    public NSArray formatPatterns()
    {
        return dataAccessLookupColumn().formatPatterns();
    }



    /**
     * Returns the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     *
     * @return the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     */
    protected Object exampleValue()
    {
        Object exampleValue = "error";

        if (dataAccessLookupColumn().isNumberLookup())
        {
            exampleValue = new BigDecimal("987654321.123456789");
        }
        else if (dataAccessLookupColumn().isTimestampLookup())
        {
            exampleValue = new NSTimestamp();
        }

        return exampleValue;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the curent value being placed into the WOPopup of values to select from according to the format selected for this column.  This makes input and output values have the same format.
     *
     * @return the curent value being placed into the WOPopup of values to select from according to the format selected for this column.
     */
    public Object formattedLookupValue()
    {
        /** require [has_aVirtualField] aVirtualField != null;   **/

        Object formattedLookupValue = aVirtualField.value();

        // Some columns do not need a formatter.
        if (outputFormatter() != null)
        {
            formattedLookupValue = outputFormatter().format(formattedLookupValue);
        }

        return formattedLookupValue;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the current value being placed into the WOPopup of default values according to the
     * format selected for this column.
     *
     * @return the current value being placed into the WOPopup of default values according to the
     * format selected for this column
     */
    public Object formattedDefaultValue()
    {
        /** require [has_aVirtualField] aVirtualField != null;   **/

        Object formattedLookupValue = aVirtualField.value();

        if (dataAccessLookupColumn().format() != null)
        {
            formattedLookupValue = formatterForPattern(dataAccessLookupColumn().format()).format(formattedLookupValue);
        }

        return formattedLookupValue;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns all available lookup values for aDataAccessColumn.
     *
     * @return list of all available lookup values for aDataAccessColumn
     */
    public NSArray lookupValues()
    {
        /** require [has_column] dataAccessLookupColumn() != null;  **/

        return ((DataAccessMode)parent()).lookupValues(dataAccessLookupColumn());

        /** ensure [valid_result] Result != null;  **/
    }


    /**
     * Returns the number of columns to display in the radio button grid.  This controls whether the
     * display is as a grid or a horizontal or vertical list of buttons.
     *
     * @return the number of columns to display in the radio button grid
     */
    public int numberOfColumns()
    {
        /** require [not_popup_display] ! dataAccessLookupColumn().shouldDisplayAsPopUp();  **/

        int numberOfColumns = 1;  // com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.VERTICAL_DISPLAY_TYPE
        String displayType = dataAccessLookupColumn().displayType();

        if (displayType.equals(com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.GRID_DISPLAY_TYPE))
        {
            numberOfColumns = Integer.parseInt(dataAccessLookupColumn().gridColumns());
        }
        else if (displayType.equals(com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn.HORIZONTAL_DISPLAY_TYPE))
        {
            numberOfColumns = lookupValues().count();
        }

        return numberOfColumns;
    }



    /**
     * Returns the Format to use to format display values in this column or null if no formatter is required.
     *
     * @return the Format to use to format values in this column or null if no formatter is required.
     */
    public Format outputFormatter()
    {
        Format outputFormatter = null;

        if ( ! dataAccessLookupColumn().isStringLookup())
        {
            outputFormatter = formatterForPattern(outputFormatPattern());
        }

        return outputFormatter;
    }



    /**
     * Returns the pattern to use when formatting output values in this column or null if no formatter is required.
     *
     * @return the pattern to use when formatting output values in this column or null if no formatter is required.
     */
    public String outputFormatPattern()
    {
        return dataAccessLookupColumn().format();
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
        return dataAccessLookupColumn().formatterForPattern(aPattern);
        //** ensure [valid_result] Result != null;  **/
    }



    /**
     * Overridden to handle validation errors caused by data input when configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.endsWith("gridColumns"))    // Edit Mode
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), "gridColumns");

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Returns the validation error caused by an invalid grid columns value value when configuring
     * this DataAccessLookupColumn.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an invalid grid columns value value when configuring this
     * DataAccessLookupColumn or null if no validation error occured.
     */
    public String gridColumnsValidationMessage()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey("gridColumns");
    }



    /**
     * Returns the String to display for this field value when uneditable.
     * Handles formatting and XML escaping if required.
     *
     * @return the String to display for this field value
     */
    public String fieldDisplayValue()
    {
        if (fieldValue() == null || ((VirtualField)fieldValue()).value() == null)
        {
            return null;
        }

        String fieldDisplayValue;
        if (outputFormatter() != null)
        {
            fieldDisplayValue = outputFormatter().format(((VirtualField)fieldValue()).value());
        }
        else
        {
            fieldDisplayValue = ((VirtualField)fieldValue()).value().toString();
        }

        if (shouldXMLEncodeData())
        {
            fieldDisplayValue = ERXStringUtilities.escapeNonXMLChars(fieldDisplayValue);
        }

        return fieldDisplayValue;
    }


    //*********** Generic Get / Set methods  ***********\\

    public NSArray uiDisplayModes()
    {
        return uiDisplayModes;
    }

    public NSArray internalDisplayModes()
    {
        return internalDisplayModes;
    }
}
