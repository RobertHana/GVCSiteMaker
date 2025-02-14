// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.commonwidgets;
import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;

public class ConfirmDeletePage extends WOComponent {

    protected String message;
    protected RemovalButton callingPage;


    public ConfirmDeletePage(WOContext aContext)
    {
        super(aContext);
    }	


    
    public WOComponent proceed() {
        return getCallingPage().removeAction();
    }

    public WOComponent cancel() {
        return getCallingPage().parent();
    }


    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + " : Confirm Deletion";
    }

    /* ********** Generic setters and getters ************** */
    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public RemovalButton getCallingPage() {
        return callingPage;
    }

    public void setCallingPage( RemovalButton newCallingPage ) {
        callingPage = newCallingPage;
    }
}
