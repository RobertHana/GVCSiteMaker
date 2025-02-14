// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.OrgUnit;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.WebsiteRequest;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.components.SMAuthComponent;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;


/**
 * This page is used to request the creation of a new Website.
 */
public class RequestSite extends SMAuthComponent 
{

    public boolean isConfirmingRequest;
	public String newSiteID;
	public NSArray rootUnits;
	public OrgUnit selectedUnit;
    protected boolean isOrgUnitMissing;
    protected boolean isDuplicateSiteID;
    protected boolean isIllegalSiteID;
    protected boolean isNewSiteIDMissing;
    protected String requestorComments;


    public RequestSite(WOContext aContext)
    {
        super(aContext);

        // Set the unit at the root of the UnitChooserWithDefault used in the SearchForm
        rootUnits = new NSArray(OrgUnit.rootUnit(session().defaultEditingContext()));
    }


    
    /**
     * This is where the request is processed.  A new WebsiteRequest is created to track this and
     * a reminder e-mail is sent to the admins and cc'd to the requester.
     */
    public WOComponent sendRequest()
    {
        validateRequest();

        if (isRequestValid())
        {
            EOEditingContext ec = ((SMSession)session()).newEditingContext();
            try
            {
                // makeRequest does not accept null
                if (requestorComments() == null)
                {
                    setRequestorComments("");
                }

                WebsiteRequest.makeRequest(ec, 
                		(User)EOUtilities.localInstanceOfObject(ec, ((Session)session()).currentUser()), 
                		(OrgUnit)EOUtilities.localInstanceOfObject(ec, selectedUnit), 
                		newSiteID, 
                		requestorComments());

                // e-mail sent in didInsert() method of WebsiteRequest
                ec.saveChanges();

                // Show Confirmation Message
                isConfirmingRequest = true;
            }
            finally
            {
                ((SMSession)session()).unregisterEditingContext(ec);
            }
        }

        return context().page();
    }
    


    /**
     * Determines if all the information needed for a requrest are present and valid.
     *
     */
    public void validateRequest()
    {
        setIsOrgUnitMissing(selectedUnit == null);
        setIsNewSiteIDMissing(newSiteID == null);
        if ( ! isNewSiteIDMissing())
        {
            setIsIllegalSiteID( ! Website.isValidSiteID(newSiteID));
            setIsDuplicateSiteID(Website.websiteForSiteID(newSiteID, session().defaultEditingContext()) != null );
        }
    }



    /**
     * Returns <code>true</code> if all the information needed for a requrest are present and valid.
     *
     * @return <code>true</code> if all the information needed for a requrest are present and valid.
     */
    public boolean isRequestValid()
    {
        return ! (isOrgUnitMissing() || isNewSiteIDMissing()  || isDuplicateSiteID() || isIllegalSiteID());
    }



    /**
     * Returns the address for the FromAddress defined in GVCSiteMakerCustom.plist
     *
     * @return the address for the FromAddress
     */
    public String newSiteRequestAddress()
    {
        return SMApplication.appProperties().stringPropertyForKey("FromAddress");
    }



    /**
     * Returns a mailto: URL for the FromAddress defined in GVCSiteMakerCustom.plist
     *
     * @return a mailto: URL for the FromAddress 
     */
    public String newSiteRequestURL()
    {
        return "mailto:" + newSiteRequestAddress();
    }


    public String pageTitle() {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Request a New Site";
    }

    
    /* Generic setters and getters ***************************************/

    public boolean isOrgUnitMissing() {
        return isOrgUnitMissing;
    }
    public void setIsOrgUnitMissing(boolean newIsOrgUnitMissing) {
        isOrgUnitMissing = newIsOrgUnitMissing;
    }

    public boolean isDuplicateSiteID() {
        return isDuplicateSiteID;
    }
    public void setIsDuplicateSiteID(boolean newIsDuplicateSiteID) {
        isDuplicateSiteID = newIsDuplicateSiteID;
    }

    public boolean isIllegalSiteID() {
        return isIllegalSiteID;
    }
    public void setIsIllegalSiteID(boolean newIsIllegalSiteID) {
        isIllegalSiteID = newIsIllegalSiteID;
    }

    public boolean isNewSiteIDMissing() {
        return isNewSiteIDMissing;
    }
    public void setIsNewSiteIDMissing(boolean newIsNewSiteIDMissing) {
        isNewSiteIDMissing = newIsNewSiteIDMissing;
    }
    
    public String requestorComments() {
        return requestorComments;
    }
    public void setRequestorComments(String newRequestorComments) {
        requestorComments = newRequestorComments;
    }
    
}
