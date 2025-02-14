// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.User;

import com.webobjects.appserver.WOContext;

import er.extensions.foundation.ERXStringUtilities;


/**
 * Implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessUserColumn.
 * There is no configuration and only the userID is shown in display mode.
 *
 * NOTE: DO NOT ADD WHITESPACE TO THE HTML, LACK OF WHITESPACE IS A REQUIREMENT
 */
public class DataAccessUserColumn extends DataAccessColumn
{

    /**
     * Designated constructor
     */
    public DataAccessUserColumn(WOContext context)
    {
        super(context);
    }



    /**
     * Returns the String to display for this field value when uneditable.
     * Handles converting line breaks to HTML and XML escaping if required.
     *
     * @return the String to display for this field value
     */
    public String fieldDisplayValue()
    {
        if (fieldValue() == null)
        {
            return null;
        }

        String fieldValue = ((User)fieldValue()).userID();

        if (shouldXMLEncodeData())
        {
            fieldValue = ERXStringUtilities.escapeNonXMLChars(fieldValue);
        }

        return fieldValue;
    }

}
