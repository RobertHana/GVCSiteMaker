// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.

package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSArray;

import net.global_village.woextensions.HTMLFormatting;


/**
 * This page displays the entries in the Error Log and allows them to be
 * deleted. // Copyright (c) 2001-2005, The Regents of the University of
 * Michigan, Ann Arbor, MI 48109 USA All rights reserved. // This software is
 * published under the terms of the Educational Community License (ECL) version
 * 1.0, // a copy of which has been included with this distribution in the
 * LICENSE.TXT file.
 */
public class ShowErrorLogPage extends SMAuthComponent implements com.gvcsitemaker.core.interfaces.SMSecurePage
{
    /**
     * Item for WORepetition
     */
    public ErrorLog errorLogItem;

    /** @TypeInfo ErrorLog */
    protected NSArray errorLogList;


    /**
     * Designated constructor.
     */
    public ShowErrorLogPage(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Overridden to handle authentication.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        // The first time through the user may not be authenticated.  Don't do this until after login.
        if (((Session) session()).isUserAuthenticated())
        {
            if (!((Session) session()).currentUser().isSystemAdmin())
            {
                ErrorPage errorPage = (ErrorPage) pageWithName("ErrorPage");
                errorPage.setMessage("Access Refused.");
                errorPage.setReason("This area is for system administrators only.");

                aResponse.setContent(errorPage.generateResponse().content());
            }
            else
            {
                super.appendToResponse(aResponse, aContext);
            }
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }
    }



    public String stackTrace()
    {
        String stackTrace = "";
        if (errorLogItem.stackTrace() != null)
        {
            stackTrace = HTMLFormatting.replaceFormattingWithHTML(errorLogItem.stackTrace());
        }

        return stackTrace;
    }



    /************ Generic Accessor and Mutators below here ************/

    /** @TypeInfo ErrorLog */
    public NSArray getErrorLogList()
    {
        if (errorLogList == null)
        {
            errorLogList = ErrorLog.orderedLogEntries(session().defaultEditingContext());
        }
        return errorLogList;
    }


    public void setErrorLogList(NSArray newErrorLogList)
    {
    }



}
