// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;


import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.woextensions.*;
import com.webobjects.eocontrol.EOEditingContext;


/**
 * CopyWebsitePage is a WOLongResponsePage that handles creating a Website as a copy of another Website.  
 *
 * NOTE: This page not currently in use.  See CreateWebsitePage for details.
 */
public class CopyWebsitePage extends WOLongResponsePage implements SMSecurePage
{

    // Parameters passed in and passed on to creation process
    protected OrgUnit associatedUnit;
    protected String ownerID;
    protected String siteID;
    protected String exampleSiteID;
    protected Number quota;
    protected String redirectUrl;
    protected User adminUser;

    // UI Status message
    public String statusMessage;
    

    /**
     * Designated constructor
     */
    public CopyWebsitePage(WOContext context)
    {
        super(context);
        setRefreshInterval( 5);  // Reload page every five seconds
        statusMessage = "Copying website";  // Initial status message
    }



    /**
     * Overridden to give some feedback that progress is being made.
     */
    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        statusMessage += "...";

        super.appendToResponse(aResponse, aContext);
    }


    
    /**
     * Overridden performAction method to copy Website.
     */
    public Object performAction()
    {
        // Get EC from application not session as *we* want to manage the locks
        EOEditingContext editingContext = ((SMApplication)application()).newEditingContext();
        editingContext.lock();

        try
        {
            // TODO Fix bug with admin not being able to override the replication flag
            // TODO Implement site creation message parameters
            Website.createWebsiteBasedOnExampleSite((OrgUnit)EOUtilities.localInstanceOfObject(editingContext, associatedUnit()),
                                                    siteID,
                                                    ownerID,
                                                    quota,
                                                    redirectUrl,
                                                    editingContext,
                                                    (User)EOUtilities.localInstanceOfObject(editingContext, adminUser()),
                                                    exampleSiteID, null, null, true);
            statusMessage = "Saving new website...";  // Update status message the next time the page is refreshed.

            editingContext.saveChanges();
        }
        finally
        {
            editingContext.unlock();
            editingContext.dispose();
        }
            
        return null;  // We have no result to return
    }

    

    /**
     * This method returns the page to show (Main) when the copy is complete.
     */
    public WOComponent pageForResult( Object result )
    {
        return pageWithName("Main");
    }



    /* ********** Generic setters and getters ************** */

    public OrgUnit associatedUnit() {
        return associatedUnit;
    }
    public void setAssociatedUnit(OrgUnit newAssociatedUnit) {
        associatedUnit = newAssociatedUnit;
    }


    public String ownerID() {
        return ownerID;
    }
    public void setOwnerID(String newOwnerID) {
        ownerID = newOwnerID;
    }
    
    public String siteID() {
        return siteID;
    }
    public void setSiteID(String newSiteID) {
        siteID = newSiteID;
    }

    public String exampleSiteID() {
        return exampleSiteID;
    }
    public void setExampleSiteID(String newExampleSiteID) {
        exampleSiteID = newExampleSiteID;
    }

    public Number quota() {
        return quota;
    }
    public void setQuota(Number newQuota) {
        quota = newQuota;
    }

    public String redirectUrl() {
        return redirectUrl;
    }
    public void setRedirectUrl(String newRedirectUrl) {
        redirectUrl = newRedirectUrl;
    }

    public User adminUser() {
        return adminUser;
    }
    public void setAdminUser(User newAdminUser) {
        adminUser = newAdminUser;
    }
    
}
