// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.admin.commonwidgets;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;


/**
 * See handleSessionRestorationErrorInContext in Application for why this page exists.
 */
public class SessionRestorationErrorPage extends WOComponent
{


    public SessionRestorationErrorPage(WOContext context)
    {
        super(context);
    }

    

    public String frontPageUrl()
    {
        return SMActionURLFactory.adminMainPageURL();
    }


    
    public String pageTitle()
    {
        return   SMApplication.appProperties().propertyForKey("ProductName") + ": Session Restoration Error";
    }
}
