package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * A component to display an array of error messages.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */  
public class ValidationErrorsPanel extends net.global_village.woextensions.WOComponent
{
    /** Binding for the errors repetition. */
    public String anError;


    /**
     * Designated constructor.
     *
     * @param aContext context of a transaction
     */
    public ValidationErrorsPanel(WOContext aContext)
    {
        super(aContext);
        /** require [valid_param] aContext != null; **/
    }



    /**
     * Overriden to make this a non-synchronizing component.
     *
     * @return false
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Overriden to make this a stateless component.
     *
     * @return true
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Returns the array of errors.
     *
     * @return an array of errors or null if there are none
     */
    private NSArray _findErrors()
    {
        NSArray theErrors = null;

        if (hasNonNullBindingFor("errors"))
        {
            theErrors = (NSArray)valueForBinding("errors");
        }

        return theErrors;
    }



    /**
     * Returns true if this component has error messages set, false otherwise.
     *
     * @return true if there are error messages, false otherwise
     */
    public boolean hasErrors()
    {
        boolean hasErrors = false;
        NSArray theErrors = _findErrors();

        if (theErrors != null)
        {
            hasErrors = theErrors.count() > 0;
        }

        return hasErrors;
    }



    /**
     * Returns the list of error messages to be displayed in this component.
     *
     * @return an array of strings composed of error messages
     */
    public NSArray errors()
    {
        /** require [has_errors] hasErrors(); **/

        return _findErrors();

        /** ensure [valid_result] Result != null; **/
    }



}
