// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.webobjects.appserver.*;


/**
 * Implementation of a page to change an external (GVC.SiteMaker managed) user's password.  Uses 
 * ChangePasswordPanel.wo
 */
public class ChangePasswordPage extends WebsiteContainerBase 
    implements com.gvcsitemaker.core.interfaces.SMSecurePage 
{

    protected String userID;


    /**
     * Designated constructor.
     */
    public ChangePasswordPage(WOContext aContext)
    {
        super(aContext);

        // Default to the logged in user's User ID.
        if (((SMSession)session()).isUserAuthenticated())
        {
            setUserID(((SMSession)session()).currentUser().userID());
        }
    }



    /* Generic setters and getters ***************************************/
    public String userID() {
        return userID;
    }
    public void setUserID(String newUserID) {
        userID = newUserID;
    }

    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Change Password Page";
    }

}
