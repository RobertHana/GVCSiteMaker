// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


public class UserInfoPage extends SMAuthComponent implements WebsiteContainer, SMSecurePage
{
    public OrgUnit aUnit;
    protected Website website; // For WebsiteContainer implementation, the Website we are managing files for
    protected NSArray errors = NSArray.EmptyArray;


    public UserInfoPage(WOContext context)
    {
        super(context);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
    }



    /**
     * Overridden to clear errors.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        errors = NSArray.EmptyArray;
    }



    /**
     * Returns the Change Password Page.
     *
     * @return the Change Password Page
     */
    public WOComponent changePasswordPage()
    {
        return pageWithName("ChangePasswordPage");
    }



    /**
     * Used by the AJAX control on this page to save changes.
     */
    public void saveChanges()
    {
        User user = ((SMSession)session()).currentUser();
        errors = user.validateUser();
        if (errors.count() == 0)
        {
            user.editingContext().saveChanges();
        }
        else
        {
            user.editingContext().revert();
        }
    }


    /**
     * Used by the AJAX control on this page to save changes.
     */
    public void revertChanges()
    {
        User user = ((SMSession)session()).currentUser();
        user.editingContext().revert();
    }



    /**
     * Returns the contents of the errors array.
     */
    public String errorMessage()
    {
        if (errors.count() > 0)
        {
            return "<div class=\"warningText\">" + errors.componentsJoinedByString(", ") + "</div>";
        }
        return "";
    }



    public Website website()
    {
        return website;
    }


    public void setWebsite(Website value)
    {
        website = (Website) EOUtilities.localInstanceOfObject(((SMSession)session()).currentUser().editingContext(), value);
    }



}
