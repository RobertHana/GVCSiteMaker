// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;

// Base class for all error pages.
public class ErrorPageBase extends WOComponent {

    protected String message;
    protected String reason;
    protected WOComponent returnPage;
    protected boolean shouldTerminateSession;
    protected ErrorLog errorLog;
    
    /**
     * Designated constructor.
     */
    public ErrorPageBase(WOContext aContext)
    {
        super(aContext);
    }



    
    /* Generic setters and getters ***************************************/
    public String message() {
        return message;
    }
    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public String reason() {
        return reason;
    }
    public void setReason(String newReason) {
        reason = newReason;
    }

    public WOComponent returnPage() {
        return returnPage;
    }
    public void setReturnPage(WOComponent value) {
        returnPage = value;
    }
    public boolean hasReturnPage() {
        return (returnPage != null);
    }

    public boolean shouldTerminateSession() {
        return shouldTerminateSession;
    }
    public void setShouldTerminateSession(boolean b) {
        shouldTerminateSession = b;
    }

    public ErrorLog errorLog() {
        return errorLog;
    }
    public void setErrorLog(ErrorLog log)
    {
        errorLog = log;
    }

}

