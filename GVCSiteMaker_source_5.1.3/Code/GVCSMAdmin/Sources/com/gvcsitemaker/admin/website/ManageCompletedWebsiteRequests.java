// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import com.gvcsitemaker.admin.user.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Main page for handling completed (granted or denied) requests for new Websites.
 */
public class ManageCompletedWebsiteRequests extends WOComponent implements WOSMConfirmable, SMSecurePage
{
    /** @TypeInfo WebsiteRequest */
    protected NSArray completedRequests;
    
    /** @TypeInfo WebsiteRequest */
    protected WebsiteRequest aWebsiteRequest;
    
    protected NSMutableArray markedRequests = new NSMutableArray();
    
    
    /**
     * Designated constructor.
     */
    public ManageCompletedWebsiteRequests(WOContext context) 
    {
        super(context);
    }
    
    
    
    /**
     * Refreshed list of WebsiteRequests.
     */
    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        setCompletedRequests(WebsiteRequest.completedRequestsForAdmin(currentUser()));
        super.appendToResponse(aResponse, aContext);
    }


    
    /**
     * Action to show WebsiteRequestNotes page for aWebsiteRequest(). 
     * 
     * @return new instance of WebsiteRequestNotes page for aWebsiteRequest()
     */
    public WOComponent showResolution() 
    {
        /** require [has_request] aWebsiteRequest() != null;  **/
        WebsiteRequestNotes websiteRequestNotes = (WebsiteRequestNotes) pageWithName("WebsiteRequestNotes");    
        websiteRequestNotes.showNotesFor(aWebsiteRequest());
        return websiteRequestNotes;
        /** ensure [different_page_returned] (Result != null) && (Result != context().page());  **/
    }



    /**
     * Marks all completed requests for deletion.
     * 
     * @return this page with all requests marked for deletion
     */
    public WOComponent markAllRequests() 
    {
        setMarkedRequests(new NSMutableArray(completedRequests()));
        
        return context().page();
    }

    
    
    /**
     * Action for submit button to display the ManagePendingWebsiteRequests page.
     *
     * @return new instance of ManagePendingWebsiteRequests page
     */
    public WOComponent showPendingRequests() 
    {
        return pageWithName("ManagePendingWebsiteRequests");
    }
    
    
    
    /**
     * Action method to confirm deletion of any marked WebsiteRequests.
     * 
     * @return a confirmation page or this page if there aren't any marked requests 
     */
    public WOComponent deleteMarkedRequests() 
    {
        WOComponent resultPage = context().page();  // no op
        if (markedRequests().count() > 0)
        {
            WOSMConfirmAction confirmPage = (WOSMConfirmAction) pageWithName("WOSMConfirmAction");
            confirmPage.setMessage("You are about to delete " + markedRequests().count() +
            " completed website requests.<br><br>Do you want to proceed?");
            confirmPage.setCallingComponent(this);
            resultPage = confirmPage;
        }
        
        return resultPage;
    }
    
    
    /**
     * Action method to process actual deletion of any marked WebsiteRequests.
     * 
     * @return this page with or without all marked requests deleted
     */
    public WOComponent confirmAction(boolean confirmed)
    {
        if (confirmed)
        {
            if (markedRequests().count() > 0)
            {
                EOEditingContext ec = ((WebsiteRequest)markedRequests().lastObject()).editingContext();
            
                for (int i = 0; i < markedRequests().count(); i++)
                {
                    WebsiteRequest aRequest = (WebsiteRequest) markedRequests().objectAtIndex(i);
                    ec.deleteObject(aRequest);
                }          
                ec.saveChanges();
                setMarkedRequests(new NSMutableArray());
            }
        }

        return this;
        /** ensure [no_marked_requests] ( ! confirmed) || (markedRequests().count() == 0);  **/
    }



    /**
     * Returns page to manage the website created from a granted request.
     * 
     * @return ManageWebsitePage for <code>aWebsiteRequest().website()</code>
     */
    public WOComponent manageWebsite() 
    {
        /** require [has_been_granted] aWebsiteRequest().hasBeenGranted();  **/ 
        ManageWebsitePage nextPage = (ManageWebsitePage)pageWithName("ManageWebsitePage");
        nextPage.setWebsite(aWebsiteRequest().website());
        return nextPage;
    }



    /**
     * Returns page to manage the user who created a request.
     * 
     * @return ManageUserPage for <code>aWebsiteRequest().requester()</code>
     */
     public WOComponent manageUser() 
     {
         /** require [is_sys_admin] currentUser().isSystemAdmin();  **/
        ManageUserPage nextPage = (ManageUserPage)pageWithName("ManageUserPage");
        nextPage.setUser(aWebsiteRequest().requester());
        return nextPage;
     }

    
    
    /**
     * Returns <code>true</code> if <code>aWebsiteRequest()</code> is marked for deletion.
     * 
     * @return <code>true</code> if <code>aWebsiteRequest()</code> is marked for deletion
     */
    public boolean isRequestMarked() 
    {
        /** require [has_request] aWebsiteRequest() != null;  **/
        return markedRequests().containsObject(aWebsiteRequest());    
    }
    
    
    
    /**
     * Adds <code>aWebsiteRequest()</code> to the list of requests marked for deletion 
     * if <code>newIsRequestMarked</code>, otherwise removes it from the list of requests marked for 
     * deletion.
     *   
     * @param newIsRequestMarked <code>true</code> to add <code>aWebsiteRequest()</code> to the list 
     * of requests marked for deletion,  <code>false</code> to remove it
     */
    public void setIsRequestMarked(boolean newIsRequestMarked) 
    {
        /** require [has_request] aWebsiteRequest() != null;  **/
        if (newIsRequestMarked && ! isRequestMarked())
        {
            markedRequests().addObject(aWebsiteRequest());
        }
        else if (isRequestMarked() && ! newIsRequestMarked)
        {
            markedRequests().removeObject(aWebsiteRequest());
        }
    }



    /**
     * Returns <code>true</code> if there are any completed requests to display.
     * 
     * @return <code>true</code> if there are any completed requests to display
     */
    public boolean hasCompletedRequests() 
    {
        return completedRequests().count() > 0;
    }
    
    
    
    /**
     * Returns <code>true</code> if the logged in user is not a system admin.  This is used to 
     * restrict display of functions to sys admins only.
     * 
     * @return <code>true</code> if the logged in user is not a system admin
     */
     public boolean isntSysAdmin() 
     {
         return ! currentUser().isSystemAdmin();
     }


    /* ********** Generic setters and getters ************** */
    
    /** @TypeInfo WebsiteRequest */
    public NSArray completedRequests() {
        return completedRequests;
    }
    public void setCompletedRequests(NSArray newCompletedRequests) {
        completedRequests = newCompletedRequests;
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
    
    public NSMutableArray markedRequests() {
        return markedRequests;
    }
    public void setMarkedRequests(NSMutableArray array) {
        markedRequests = array;
    }

    /** invariant [has_markedRequests] markedRequests != null;  **/

 }
