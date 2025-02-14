// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.admin.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.*;


/**
 * Main page for handling requests for new Websites.
 */
public class ManagePendingWebsiteRequests extends WOComponent implements SMSecurePage
{

    /** @TypeInfo WebsiteRequest */
    protected NSArray pendingRequests;
    
    /** @TypeInfo WebsiteRequest */
    protected WebsiteRequest aWebsiteRequest;
    
    
    /**
     * Designated constructor.
     */
    public ManagePendingWebsiteRequests(WOContext context) 
    {
        super(context);
    }
    
    
    
    /**
     * This page can be accessed via a direct action so we need to ensure that the user is 
     * authenticated and an admin before allowing this page to be displayed.
     */
    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        Session theSession = (Session)session(); 
        if (theSession.isUserAuthenticated() &&
             ( ! theSession.currentUser().isUnitAdmin()) )
        {
            ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
            errorPage.setMessage("Access Refused.");
            errorPage.setReason("This area is for unit and system administrators only.");

            aResponse.setContent(errorPage.generateResponse().content());
            ((SMSession)session()).logout(aResponse);
        }
        else if ( ! theSession.isUserAuthenticated())
        {
            SMLoginPage loginPage = (SMLoginPage) pageWithName("LoginPage");
            loginPage.setDestination(this);
            aResponse.setContent(loginPage.generateResponse().content());
        }
        else
        {
            setPendingRequests(WebsiteRequest.pendingRequestsForAdmin(currentUser()));
            super.appendToResponse(aResponse, aContext);
        }
    }
    
    
    
    /**
     * Action for submit button to display the ManageCompletedWebsiteRequests page.
     *
     * @return new instance of ManageCompletedWebsiteRequests page
     */
    public WOComponent showCompletedRequests() 
    {
        return pageWithName("ManageCompletedWebsiteRequests");
    }
    
    
    
    /**
     * Action to grant request for new Website.  Shows the CreateWebsitePage with defaults taken 
     * from this WebsiteRequest.
     * 
     * @return new instance of CreateWebsitePage with values from aWebsiteRequest()
     */
    public WOComponent grantRequest() 
    {
        CreateWebsitePage createWebsitePage = (CreateWebsitePage)pageWithName("CreateWebsitePage");
        createWebsitePage.setWebsiteRequest(aWebsiteRequest());
        return createWebsitePage;
    }
    


    /**
     * Action to deny request for new Website.  Shows the QueryDenyWebsiteRequest in deny mode. 
     * 
     * @return new instance of QueryDenyWebsiteRequest in deny mode for aWebsiteRequest()
     */
    public WOComponent denyRequest() 
    {
        QueryDenyWebsiteRequest denyWebsiteRequest = (QueryDenyWebsiteRequest)pageWithName("QueryDenyWebsiteRequest");
        denyWebsiteRequest.denyWebsiteRequest(aWebsiteRequest());
        return denyWebsiteRequest;
    }
    


    /**
     * Action to deny request for new Website.  Shows the QueryDenyWebsiteRequest in deny mode. 
     * 
     * @return new instance of QueryDenyWebsiteRequest in deny mode for aWebsiteRequest()
     */
    public WOComponent queryRequest() 
    {
        QueryDenyWebsiteRequest queryWebsiteRequest = (QueryDenyWebsiteRequest)pageWithName("QueryDenyWebsiteRequest");
        queryWebsiteRequest.queryWebsiteRequest(aWebsiteRequest());
        return queryWebsiteRequest;
    }
    
    
    
    /**
     * Action to refer request for new Website to another OrgUnit.  Shows the ReferWebsiteRequest 
     * page for aWebsiteRequest(). 
     * 
     * @return new instance of ReferWebsiteRequest for aWebsiteRequest()
     */
    public WOComponent referRequest() 
    {
        ReferWebsiteRequest referWebsiteRequest = (ReferWebsiteRequest)pageWithName("ReferWebsiteRequest");
        referWebsiteRequest.referWebsiteRequest(aWebsiteRequest());
        return referWebsiteRequest;
    }



    /**
     * Returns aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes.
     * 
     * @return aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes
     */
    public String formattedAdminNotes()
    {
        return HTMLFormatting.replaceFormattingWithHTML(aWebsiteRequest().administrativeNotes());
    }



    /**
     * Returns <code>true</code> if there are any pending requests to display.
     * 
     * @return <code>true</code> if there are any pending requests to display
     */
    public boolean hasPendingRequests() 
    {
        return pendingRequests().count() > 0;
    }


    public boolean hasRequestNote() 
    {
        return !StringAdditions.isEmpty(aWebsiteRequest().requesterNote());
    }



    /* ********** Generic setters and getters ************** */
    
    /** @TypeInfo WebsiteRequest */
    public NSArray pendingRequests() {
        return pendingRequests;
    }
    public void setPendingRequests(NSArray newPendingRequests) {
        pendingRequests = newPendingRequests;
    }
    
    /** @TypeInfo WebsiteRequest */
    public WebsiteRequest aWebsiteRequest() {
        return aWebsiteRequest;
    }
    public void setAWebsiteRequest(WebsiteRequest newAWebsiteRequest) {
        aWebsiteRequest = newAWebsiteRequest;
    }
    
    public User currentUser() {
        return ((SMSession)session()).currentUser();
    }

 }
