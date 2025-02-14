// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.appserver.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.ERXStringUtilities;

import net.global_village.virtualtables.VirtualRow;
import net.global_village.woextensions.HTMLFormatting;


/**
 * Implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessStringColumn
 * showing a Text input, TextArea input, or plain character string as configured.
 * It also implements the edit function for the default value, width, and height.
 *
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessStringColumn extends DataAccessColumn
{
    protected NSArray wysiwygEditorModes;

    /**
     * Designated constructor
     */
    public DataAccessStringColumn(WOContext context)
    {
        super(context);

        wysiwygEditorModes = com.gvcsitemaker.core.pagecomponent.DataAccessStringColumn.WysiwygEditorModes;
    }



    /**
     * Returns componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessStringColumn.
     *
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessStringColumn.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessStringColumn dataAccessStringColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessStringColumn) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns <code>true</code> if a TextArea input should be used (height > 1), and <code>false</code> if a standard Text input should be used.
     *
     * @return <code>true</code> if a TextArea input should be used (height > 1), and <code>false</code> if a standard Text input should be used.
     */
    public boolean useTextArea()
    {
        return Integer.parseInt(dataAccessStringColumn().height()) > 1;
    }



    /**
     * Overridden to handle validation errors caused by data input when configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.endsWith("height"))    // Edit Mode
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), "height");

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else if (keyPath.endsWith("width"))    // Edit Mode
        {
            validationMessageStore().registerValidationFailureForKey(t.getMessage(), "width");

            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Returns the validation error caused by an invalid width value value when configuring this DataAccessTimestampColumn.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an invalid width value value when configuring this DataAccessTimestampColumn or null if no validation error occured.
     */
    public String widthValidationMessage()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey("width");
    }



    /**
     * Returns the validation error caused by an invalid width value value when configuring this DataAccessTimestampColumn.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an invalid width value value when configuring this DataAccessTimestampColumn or null if no validation error occured.
     */
    public String heightValidationMessage()
    {
        /** require
        [edit_mode] isInEditMode();
        [parent_is_ValidationMessageStore]  parent() instanceof com.gvcsitemaker.core.interfaces.ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey("height");
    }



    /**
     * Returns the String to display for this field value when uneditable.
     * Handles converting line breaks to HTML and XML escaping if required.
     *
     * @return the String to display for this field value
     */
    public String fieldDisplayValue()
    {
        String fieldValue = fieldValue() != null ? (String)fieldValue() : "";

        if (dataAccessStringColumn().shouldConvertCR())
        {
            fieldValue = HTMLFormatting.replaceFormattingWithHTML(fieldValue);
        }

        if (shouldXMLEncodeData())
        {
            fieldValue = ERXStringUtilities.escapeNonXMLChars(fieldValue);
        }

        return fieldValue;
    }



    /**
     * The EditLive! WYSIWYG editor needs each text area to have a unique name.  This method provides
     * that unique name.
     *
     * @return unique name for the text area used to edit this field in this row
     */
    public String uniqueTextAreaId()
    {
        Object rowID = ((VirtualRow)((DataAccessMode)parent()).currentRow()).virtualRowID();
        if (rowID == null)
        {
            // can happen if we are adding
            return dataAccessColumn().normalizedFieldName() + "_Add";
        }

        if (SMApplication.smApplication().isUsingIntegerPKs())
        {
            return dataAccessColumn().normalizedFieldName() + "_" + rowID;
        }
        else
        {
            return dataAccessColumn().normalizedFieldName() + "_" + ERXStringUtilities.byteArrayToHexString(((NSData)rowID).bytes());
        }
    }



    public String stringFieldValue()
    {
        return (String)fieldValue();
    }


    public void setStringFieldValue(String newValue)
    {
        if (!((SMSession) session()).isUserAuthenticated())
        {
            // a public user must have his input escape of HTML
            newValue = WOMessage.stringByEscapingHTMLString(newValue);
        }

        setFieldValue(newValue);
    }



    /**
     * @return true if the WYSIWYG editor link should be shown
     */
    public boolean isAppWYSIWYGEnabled() {
        return SMApplication.appProperties().booleanPropertyForKey("WYSIWYGEditorEnabled");
    }


    public NSArray wysiwygEditorModes()
    {
        return wysiwygEditorModes;
    }



}
