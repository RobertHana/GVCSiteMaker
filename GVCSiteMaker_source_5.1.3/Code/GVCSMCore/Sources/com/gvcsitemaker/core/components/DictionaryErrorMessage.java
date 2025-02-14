// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * A simple component to make dealing with the GVCSiteMaker validation errors dictionaries easier.  The API is:
 * <br>
 * prefixString - optional string to display before error message (outside of font).  This is intended for HTML to create <TR>, <TD>, <BR> etc.<br>
 * suffixString - optional string to display before error message (outside of font).  This is intended for HTML to create </TR>, </TD>, <BR> etc. <br>
 * genericMessage - optional string to include as part of the error message before the message from the dictionary. <br>
 * dictionary - the dictionary to look for the error message in. <br>
 * errorKey - the key to look under in dictionary for the error message. <br>
 * <br>
 * If errorKey is not found in dictionary then this component displays nothing.
 */
public class DictionaryErrorMessage extends WOComponent 
{

    /**
     * Designated constructor
     */
    public DictionaryErrorMessage(WOContext context)
    {
        super(context);
    }



    /**
     * We have no need of state.
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Returns <code>true</code> if dictionary has an error message under errorKey.  The component should be displayed if there is such an error.
     *
     * @return <code>true</code> if dictionary has an error message under errorKey.
     */
    public boolean hasError()
    {
        return errorMessage() != null;
    }



    /**
     * Returns the error message from dictionary() associated with errorKey() or null if there is no matching error message.
     *
     * @return the error message from dictionary() associated with errorKey() or null if there is no matching error message.
     */
    public String errorMessage()
    {
        return (String) ((NSDictionary) valueForBinding("dictionary")).valueForKey((String) valueForBinding("errorKey"));
    }
}
