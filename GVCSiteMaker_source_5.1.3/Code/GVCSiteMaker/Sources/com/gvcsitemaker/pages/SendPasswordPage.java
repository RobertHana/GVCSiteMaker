// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.appserver.SMApplication;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * Implementation of a page to send a password to a (GVC  SiteMaker managed) user's e-mail address.  Uses SendPasswordPanel.wo
 */
public class SendPasswordPage extends WOComponent {

    public SendPasswordPage(WOContext aContext)
    {
        super(aContext);
    }



    /* Generic setters and getters ***************************************/

    public String pageTitle() {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Send Password Page";
    }
}
