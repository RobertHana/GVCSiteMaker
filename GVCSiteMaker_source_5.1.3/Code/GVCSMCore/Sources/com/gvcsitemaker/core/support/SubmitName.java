// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import com.webobjects.appserver.*;


/**
 * This component is intended to be used to create WebObjects compatible HTML INPUT tags in forms in custom Data Access Section templates.  This component simply outputs it's inputName binding and, if it later finds that name as a form value invokes its action binding.  For example, to create a custom image submit button, a custom template could include this HTML:<br><br>
 * <pre>
 * &lt;input type=image name="DataAccess_SaveName" src="/site.id/files/SubmitImage.gif" border="0" width="24" height="24"&gt;
 * </pre>
 * DataAccess_SaveName would have a definition like this:
 * <pre>
 * DataAccess_SaveName: SubmitName {
 *    inputName = "DataAccess_SaveName";
 *    action = saveChanges;
 * }
 * </pre>
 * When the image is used to submit, saveChanges will be called just as for a regular WOActiveImage.  The template can also create a regular submit button with a custom caption by using HTML like this in the custom template:
 * <pre>
 * &lt;input type=submit name="DataAccess_SaveName" value="Register Now"&gt;
 * </pre>
 */
public class SubmitName extends WOComponent
{


    /**
     * Designated constructor.
     */
    public SubmitName(WOContext context)
    {
        super(context);
    }



    /**
     * This component manages its own bindings.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * This component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * We don't need a template so return an empty one to speed things up.
     *
     * @return empty WOElement graph
     */
    public WOElement template()
    {
        return templateWithHTMLString("", "", null);
    }



    /**
     * This component simply outputs it's inputName binding.
     */
    public void appendToResponse(WOResponse response,
                                 WOContext context)
    {
        // No need to call super.
        response.appendContentString(inputName());
    }



    /**
     * Invoked the method bound to action if the value bound to inputName is found in the form values.  This indicates that the input this SubmitName was used on was the UI element which submitted the form.
     */
    public WOActionResults invokeAction(WORequest request, WOContext context)
    {
        WOActionResults result = null;
        String inputName = inputName();

        // If our form value was submitted, then we are the submit button!
        if ((request.formValueForKey(inputName) != null)  ||             // WOSubmit
            ((request.formValueForKey(inputName + ".x") != null) &&      // WOActiveImage
             (request.formValueForKey(inputName + ".y") != null)))       // WOActiveImage
        {
            result = (WOActionResults) valueForBinding("action");

            // Tell the context that an action was invoked and that it can stop processing the invokeAction phase.
            context._setActionInvoked(result != null);
        }

        return result;
    }



    /*
     * Returns the value bound to inputName.
     *
     * @return the value bound to inputName.
     */
    public String inputName()
    {
        return (String) valueForBinding("inputName");
    }


}
