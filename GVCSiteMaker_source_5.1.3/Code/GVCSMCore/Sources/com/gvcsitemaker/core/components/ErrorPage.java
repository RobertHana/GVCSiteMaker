// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.components;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;


/**
 * Common error page for all applications.
 */
public class ErrorPage extends ErrorPageBase
{
    protected boolean robotShouldIndex = true;
    protected boolean isDuplicateFile;
    protected SiteFile duplicateFile;


    /**
     * Designated constructor. 
     */
    public ErrorPage(WOContext aContext)
    {
        super(aContext);
    }


    /**
     * Overridden to terminate session.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        if (shouldTerminateSession())
        {
            ((SMSession)session()).logout(response);
        }
    }



    /**
     * Action to handle attempt at duplicate file upload.
     * 
     * @return FileUpload page in replace mode
     */
    public WOComponent modifyFile() 
    {
        WOComponent nextPage = pageWithName("FileUpload");
        
        // Use takeValueForKey as FileUpload is actually in GVCSiteMaker but this page is shared 
        // with the GVCSMAdmin app.
        nextPage.takeValueForKey(duplicateFile, "currentFile");
        nextPage.takeValueForKey(Boolean.TRUE, "isModifyingExistingFile");
        nextPage.takeValueForKey(duplicateFile.website(), "website");

        return nextPage;
    }



    /**
     * Returns <code>true</code> if the action that caused this error was a direct action.  In this
     * case a link is displayed to re-execute this action.
     * 
     * @return <code>true</code> if the action that caused this error was a direct action
     */
    public boolean wasDirectActionUrl() 
    {
        return session().context().request().uri().indexOf("/wa/") > -1;
    }


    
    /**
     * Returns <code>true</code> if the action that caused this error was a direct action.  In this
     * case a link is displayed to re-execute this action.
     * 
     * @return <code>true</code> if the action that caused this error was a direct action
     */
    public String robotShouldNotIndexHeaderString() 
    {
        return robotShouldIndex() ? "" : "<META NAME=\"ROBOTS\" CONTENT=\"NOINDEX, NOFOLLOW\">";
    }


    
    /* Generic setters and getters ***************************************/

    public boolean robotShouldIndex() {
        return robotShouldIndex;
    }
    public void setRobotShouldIndex(boolean value) {
        robotShouldIndex = value;
    }

    public boolean isDuplicateFile() {
        return isDuplicateFile;
    }
    public void setIsDuplicateFile(boolean value) {
        isDuplicateFile = value;
    }

    public SiteFile duplicateFile() {
        return duplicateFile;
    }
    public void setDuplicateFile(SiteFile value) {
        duplicateFile = value;
    }

    public String pageTitle() {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Message";
    }



}

