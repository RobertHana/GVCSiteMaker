// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * This page refers a website request to a different OrgUnit.
 */
public class ReferWebsiteRequest extends WOComponent implements SMSecurePage
{

    /** @TypeInfo OrgUnit */
    protected NSArray rootUnits;
    /** @TypeInfo OrgUnit */
    protected NSArray excludedUnits;
        
    /** @TypeInfo OrgUnit */
    protected OrgUnit unitReferedTo;
    protected String referralNote;

    protected WebsiteRequest websiteRequest;

    
    /**
     * Designated constructor. 
     */
    public ReferWebsiteRequest(WOContext context) 
    {
        super(context);
    }



    /**
     * Sets up this page to refer <code>aWebsiteRequest</code> to a different OrgUnit.
     * 
     * @param aWebsiteRequest the WebsiteRequest to deny.
     */
    public void referWebsiteRequest(WebsiteRequest aWebsiteRequest)
    {
        websiteRequest = aWebsiteRequest;
        rootUnits = new NSArray(OrgUnit.rootUnit(websiteRequest().editingContext()));
        
        // Can't refer to the unit it is already in.
        excludedUnits = new NSArray(websiteRequest.orgUnit());
    }
    
    
    
    /**
     * Action method to cancel the referral and return to the ManagePendingWebsiteRequests page
     * 
     * @return ManagePendingWebsiteRequests page
     */
    public WOComponent cancelReferral() 
    {
        return pageWithName("ManagePendingWebsiteRequests");
    }
    
    
    
    /**
     * Action method for the Send button.  Processes a Website Request referral and returns the 
     * ManagePendingWebsiteRequests page.
     * 
     * @return the ManagePendingWebsiteRequests page
     */
   public WOComponent makeReferral() 
    {
        // Change null to empty string for better results
        if (referralNote() == null)
        {
            setReferralNote("");
        }
            
        // Use a NotifyingEditingContext as this works with WebsiteRequest to send out e-mails
        EOEditingContext ec = ((SMSession)session()).newEditingContext();
        try
        {
            WebsiteRequest localRequest = (WebsiteRequest) EOUtilities.localInstanceOfObject(ec, websiteRequest());
            OrgUnit localOrgUnit = (OrgUnit)EOUtilities.localInstanceOfObject(ec, unitReferedTo());
            User localUser = (User)EOUtilities.localInstanceOfObject(ec, currentUser());

            localRequest.referTo(localUser, localOrgUnit, referralNote());
                 
            ec.saveChanges();
        }
        finally
        {
            ((SMSession)session()).unregisterEditingContext(ec);
        }
        
        return pageWithName("ManagePendingWebsiteRequests");
    }
    
    /**
     * Returns aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes.
     * 
     * @return aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes
     */
    public String formattedAdminNotes()
    {
        return HTMLFormatting.replaceFormattingWithHTML(websiteRequest().administrativeNotes());
    }
    
    
    /* ********** Generic setters and getters ************** */

    /** @TypeInfo OrgUnit */
    public NSArray rootUnits() {
        return rootUnits;
    }

    /** @TypeInfo OrgUnit */
    public NSArray excludedUnits() 
    {
        return excludedUnits;
    }

    public OrgUnit unitReferedTo() {
        return unitReferedTo;
    }
    public void setUnitReferedTo(OrgUnit newUnitReferedTo) {
        unitReferedTo = newUnitReferedTo;
    }
    
    public String referralNote() {
        return referralNote;
    }
    public void setReferralNote(String newReferralNote) {
        referralNote = newReferralNote;
    }
    
    /** @TypeInfo WebsiteRequest */
    public WebsiteRequest websiteRequest() {
        return websiteRequest;
    }
    
    public User currentUser() {
        return ((SMSession)session()).currentUser();
    }

}
