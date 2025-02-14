// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.LDAPGroup;
import com.gvcsitemaker.pages.ConfigAccessGroup;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSValidation;



/** 
 * Editor used by the ConfigureAccessGroup page for LDAPGroups. 
 */
public class LDAPAccessGroupEditor extends WOComponent 
{
    protected LDAPGroup currentGroup;
    protected boolean doesExternalGroupExist;
    protected boolean isExternalGroupNameNull;
    protected String validationErrorMessage;
    
    
    public LDAPAccessGroupEditor(WOContext context) 
    {
        super(context);
    }



    /**
     * Overridden to setup state before display and reset afterward.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        setValidationErrorMessage(null);
    }



    /**
     * Overridden to perform page specific UI updates from validation.  This 
     * method is called if the Access Group name is invalid (missing) or is in 
     * use by another group.  validationErrorMessage() is set to an appropriate 
     * error message.
     * 
     * @see com.webobjects.appserver.WOComponent#validationFailedWithException(java.lang.Throwable, java.lang.Object, java.lang.String)
     */
    public void validationFailedWithException(Throwable failure,
                                              Object value,
                                              String keyPath)
    {
        if (keyPath.equals("currentGroup.ldapGroupName"))
        {
            // Nasty hack to get our validation message instead of EOF's.
            // REFACTOR Use GVCValidation for all this
            try
            {
                currentGroup().validateLdapGroupName(value);
            }
            catch (NSValidation.ValidationException e)
            {
                setValidationErrorMessage(e.getMessage());
            }

            
            // Force in the invalid value so that the user can see it.
            takeValueForKeyPath(value, keyPath);

            // Our parent needs to know this so that it does not try to save.
            parentEditor().setEditorHasErrors(true);
        }
        else
        {
            super.validationFailedWithException(failure, value, keyPath);
        }
    }


    /* Generic setters and getters ***************************************/
    
    public LDAPGroup currentGroup() {
        return currentGroup;
    }
    public void setCurrentGroup(LDAPGroup aGroup) {
        currentGroup = aGroup;
    }
     
    public String validationErrorMessage() {
        return validationErrorMessage;
    }
    public void setValidationErrorMessage(String newValidationErrorMessage) {
        validationErrorMessage = newValidationErrorMessage;
    }

    public ConfigAccessGroup parentEditor() {
        return (ConfigAccessGroup) parent();
    }
}
