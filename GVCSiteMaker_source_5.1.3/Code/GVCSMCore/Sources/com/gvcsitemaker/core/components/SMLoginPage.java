// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;


/**
 * Implementation of a page to log in an external (SiteMaker managed) user.  Uses LoginPanel.wo
 *
 * SiteMaker actually uses a page named LoginPage for authenticating external users.  To use this page as LoginPage, either use the PageMap to map it to LoginPage, or sub-class it as LoginPage in GVCSMCustom.
 */
public class SMLoginPage extends WOComponent implements SMSecurePage
{

    protected WOComponent destination;	// Passed to LoginPanel, the page to display after login.
    protected String message;			// Optional message for things like "Authentication is required to access this Area."


    public SMLoginPage(WOContext aContext)
    {
        super(aContext);

        // The default destination is the Main page
        destination = pageWithName("Main");

    }


    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + " Login Page";
    }



    /* Generic setters and getters ***************************************/
    public WOComponent destination() {
        return destination;
    }
    public void setDestination(WOComponent newDestination) {
        destination = newDestination;
    }

    public String message() {
        return message;
    }
    public void setMessage(String newMessage) {
        message = newMessage;
    }
}
