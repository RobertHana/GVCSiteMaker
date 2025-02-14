// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;


import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import er.extensions.foundation.ERXStringUtilities;


/*
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessBooleanColumn extends DataAccessColumn
{
    protected static final String CHECKED_TITLE_KEY = "checkedTitle";
    protected static final String UNCHECKED_TITLE_KEY = "unCheckedTitle";
    protected static final String CHECKED_DEFAULT_KEY = "checked";
    protected static final String UNCHECKED_DEFAULT_KEY = "unchecked";

    /** @TypeInfo java.lang.String */
    protected NSArray defaultValues;
    protected String selectedDefaultValue;


    /**
     * Designated constructor
     */
    public DataAccessBooleanColumn(WOContext context)
    {
        super(context);
        defaultValues = new NSArray(new String[] {CHECKED_DEFAULT_KEY,
                                                  UNCHECKED_DEFAULT_KEY});
    }



    /**
     * Returns componentObject() downcast to
     * com.gvcsitemaker.core.pagecomponent.DataAccessBooleanColumn.
     *
     * @return componentObject() downcast to
     * com.gvcsitemaker.core.pagecomponent.DataAccessBooleanColumn
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessBooleanColumn dataAccessBooleanColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessBooleanColumn) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the String to display for this field value when uneditable.
     * This will be null, or the checked or unchecked title.  XML escaping is handled.
     *
     * @return the String to display for this field value when uneditable
     */
    public String fieldDisplayValue()
    {
        String fieldDisplayValue = null;

        if (fieldValue() != null)
        {
            fieldDisplayValue = ((Boolean)fieldValue()).booleanValue() ?
                                    dataAccessBooleanColumn().checkedTitle() :
                                    dataAccessBooleanColumn().unCheckedTitle();
        }


        if (shouldXMLEncodeData() && fieldDisplayValue != null)
        {
            fieldDisplayValue = ERXStringUtilities.escapeNonXMLChars(fieldDisplayValue);
        }

        return fieldDisplayValue;
    }



    /**
     * Overridden to handle validation errors caused by data input when
     * configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.endsWith(CHECKED_TITLE_KEY))    // Edit Mode
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), CHECKED_TITLE_KEY);

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else if (keyPath.endsWith(UNCHECKED_TITLE_KEY))    // Edit Mode
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
    * Returns the list of possible default values for this column.
    *
    * @return the list of possible default values for this column
    * @TypeInfo java.lang.String
    * */
    public NSArray defaultValues() {
        return defaultValues;
    }



    /**
     * Returns the UI string representing the selected default value for this
     * checkbox.
     *
     * @return the UI string representing the selected default value
     */
    public String selectedDefaultValue()
    {
        /** require [has_default] dataAccessBooleanColumn().defaultValue() != null;  **/
        return
                ((Boolean)dataAccessBooleanColumn().defaultValue()).booleanValue()
                    ? CHECKED_DEFAULT_KEY : UNCHECKED_DEFAULT_KEY;
    }



    /**
     * Sets the selected default value based on  the UI string.
     *
     * @param newSelectedDefaultValue the UI string representing the selected default value
     */
    public void setSelectedDefaultValue(String newSelectedDefaultValue)
    {
        /** require [has_default] newSelectedDefaultValue != null;  **/
        dataAccessBooleanColumn().setDefaultValue(new Boolean(
                newSelectedDefaultValue.equals(CHECKED_DEFAULT_KEY)));
    }

}
