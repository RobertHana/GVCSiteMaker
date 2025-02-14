// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.admin.commonwidgets.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class ManageWebsitePage extends WOComponent implements SMSecurePage
{

    protected Website _website;
    public String websiteIDToManage, newOwnerID, deleteFailedErrorString;
    public OrgUnit newAssociatedUnit;
    public NSDictionary errorMessages;
    private final String INVALID_MANAGE_SITE_ID_ERROR_KEY = "invalidManageSiteIDErrorKey";
    
    /** @TypeInfo WebsiteChangeLog */
    protected NSArray changeLogs;
    public WebsiteChangeLog aChangeLog;
    protected String adminNotes;
    
    protected String messageSubject;
    protected String messageBody;
    protected String messageSubjectRequiredErrorMessage;

    


    public ManageWebsitePage(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Overridden to update list of change logs and reset error messages after display.
     */
    public void appendToResponse(WOResponse arg0, WOContext arg1)
    {
        changeLogs = getWebsite().orderedChangeLogs();
        super.appendToResponse(arg0, arg1);
        setMessageSubjectRequiredErrorMessage(null);
        errorMessages = null;
    }



    /**
     * Action method to validate and update Website properties.
     * 
     * @return this page
     */ 
    public WOComponent updateProperties()
    {
        errorMessages = getWebsite().updateProperties( newAssociatedUnit, newOwnerID);
        if (errorMessages == null)
        {
            NSDictionary before = editingContext().committedSnapshotForObject(getWebsite());
            editingContext().saveChanges();
            
            // Create change log if there is a note from the admin or if any properties changed.
            if (( ! StringAdditions.isEmpty(adminNotes)) || 
                (getWebsite().changesFromSnapshot(before).count() > 0))
            {
                getWebsite().logAdminChanges(currentAdmin(), adminNotes(), before);
                editingContext().saveChanges();
                setAdminNotes(null);
            }
        }
        else
        {
            editingContext().revert();
        }

        return context().page();
    }


    
    /**
     * Action method to delete this Website after confirmation.  Called from RemovalButton.
     * 
     * @return the Main page
     */
    public Main removeWebsite()
    {
        EOEditingContext ec = ((SMSession)session()).newEditingContext();
        try
        {
            Website localWebsite = (Website) EOUtilities.localInstanceOfObject(ec, getWebsite());
            DebugOut.println(1, "About to delete website " + localWebsite.siteID());
            ec.deleteObject(localWebsite);
            ec.saveChanges();
            DebugOut.println(1, "Finished deleting website");
        }
        finally
        {
            ((SMSession)session()).unregisterEditingContext(ec);
        }
            
        return (Main)pageWithName("Main");
    }



    /**
     * Action method to send message to Website owner and co-owners.  The message is recorded in the
     * change log.  Any changes to the Website properties are cancelled as a result of this action.
     * 
     * @return this page
     */
    public WOComponent sendMessageToOwners() 
    {
        if (StringAdditions.isEmpty(messageSubject()))
        {
            setMessageSubjectRequiredErrorMessage("Send Failed: Subject is a required field.");
        }
        else
        {
            editingContext().revert();  // To avoid unintentional save of changes to properties
        
            getWebsite().sendMessageToOwners(currentAdmin(), messageSubject(), messageBody());
            editingContext().saveChanges();
            
            setMessageSubject(null);
            setMessageBody(null);
        }
        return context().page();
    }
    


    
    public boolean displayInvalidManageSiteIDErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(INVALID_MANAGE_SITE_ID_ERROR_KEY) != null));
    }

    public boolean displayNullSiteIDErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.NULL_SITE_ID_ERROR_KEY) != null));
    }

    public boolean displayInvalidSiteIDErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.INVALID_SITE_ID_ERROR_KEY) != null));
    }

    public boolean displaySiteIDExistsErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.SITE_ID_EXISTS_ERROR_KEY) != null));
    }

    public boolean displayNullOwnerIDErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.NULL_OWNER_ID_ERROR_KEY) != null));
    }

    public boolean displayInvalidCharsInOwnerIDErrorMessage() {
        return ((errorMessages != null) &&
                (errorMessages.objectForKey(Website.INVALID_CHARS_ERROR_KEY) != null));
    }

    public boolean displayNullAssociatedUnitErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.NULL_ASSOCIATED_UNIT_ERROR_KEY) != null));
    }

    public boolean displayInvalidFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.INVALID_FILESPACE_QUOTA_ERROR_KEY) != null));
    }

    public boolean displayDeleteNotPermittedErrorMessages() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Deleteable.DELETE_NOT_PERMITTED_ERROR_KEY) != null));
    }

    public boolean displayWebsiteOutOfAdminScopeErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.WEBSITE_OUT_OF_ADMIN_SCOPE_ERROR_KEY) != null));
    }

    public boolean displayInvalidRedirectURLErrorMessage() {
        return ((errorMessages != null) &&
                (errorMessages.objectForKey(Website.INVALID_REDIRECT_URL_ERROR_KEY) != null));
    }


    // the following methods are not required, but i'm using them so we
    // can make use of the constants defined for the keys.  then, if the keys
    // get changed for some reason in the future, the code will not suddenly
    // be broken.
    public String invalidManageSiteIDErrorMessage() {
        return (String)errorMessages.objectForKey(INVALID_MANAGE_SITE_ID_ERROR_KEY);
    }

    public String nullSiteIDErrorMessage() {
        return (String)errorMessages.objectForKey(Website.NULL_SITE_ID_ERROR_KEY);
    }

    public String invalidSiteIDErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_SITE_ID_ERROR_KEY);
    }

    public String siteIDExistsErrorMessage() {
        return (String)errorMessages.objectForKey(Website.SITE_ID_EXISTS_ERROR_KEY);
    }

    public String nullOwnerIDErrorMessage() {
        return (String)errorMessages.objectForKey(Website.NULL_OWNER_ID_ERROR_KEY);
    }

    public String invalidCharsInOwnerIDErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_CHARS_ERROR_KEY);
    }

    public String nullAssociatedUnitErrorMessage() {
        return (String)errorMessages.objectForKey(Website.NULL_ASSOCIATED_UNIT_ERROR_KEY);
    }

    public String invalidFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_FILESPACE_QUOTA_ERROR_KEY);
    }

    public NSArray deleteNotPermittedErrorArray() {
        return (NSArray)errorMessages.objectForKey(Deleteable.DELETE_NOT_PERMITTED_ERROR_KEY);
    }

    public String websiteOutOfAdminScopeErrorMessage() {
        return (String)errorMessages.objectForKey(Website.WEBSITE_OUT_OF_ADMIN_SCOPE_ERROR_KEY);
    }

    public String invalidRedirectUrlErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_REDIRECT_URL_ERROR_KEY);
    }



    /**
     * Returns aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes.
     * 
     * @return aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes
     */
    public String formattedChangeLogNotes()
    {
        return HTMLFormatting.replaceFormattingWithHTML(aChangeLog.notes());
    }
    



    /**
     * Returns aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes.
     * 
     * @return aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes
     */
    public String formattedChangeLogChanges()
    {
        return HTMLFormatting.replaceFormattingWithHTML(aChangeLog.changes());
    }
    
    
    
    /**
     * @return the URL used to configure this website
     */
    public String websiteConfigURL()
    {
        return getWebsite().configURL(context().request());
    }

    
    
    /**
     * Returns true if the link to create a data table package should be displayed, false otherwise.  It will be displayed if website has at least one database table or user is a unit or a sys admin.
     * 
     * @return true if the link to create a data table package should be displayed, false otherwise.  It will be displayed if website has at least one database table or user is a unit or a sys admin. +-
     * -
     */
    public boolean showDataTablePackageLink()
    {
        return (getWebsite().hasDatabaseTables() && (currentAdmin().isSystemAdmin() || currentAdmin().isUnitAdmin()));
    }
   
    
    
    /**
     * Returns the Create Data Table Package page with the current website.
     * 
     * @return the Create Data Table Package page with the current website
     */
    public WOComponent createDataTablePackage()
    {
        CreateDataTablePackagePage nextPage = (CreateDataTablePackagePage)pageWithName("CreateDataTablePackagePage");
        nextPage.setWebsite(getWebsite());
        
        return nextPage;
        /** ensure [valid_result] Result != null; **/
    }    
    
    
    
    /* ********** Generic setters and getters ************** */
    public Website getWebsite() {
        return _website;
    }

    public void setWebsite( Website newWebsite ) {
        newAssociatedUnit = newWebsite.parentOrgUnit();
        newOwnerID = newWebsite.owner().userID();
        _website = newWebsite;
    }

    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
    }
    
    public User currentAdmin() {
        return ((SMSession)session()).currentUser();
    }
    

    public String productName() {
        return SMApplication.productName();
    }
    
    /** @TypeInfo WebsiteChangeLog */
    public NSArray changeLogs() {
        return changeLogs;
    }
    public void setChangeLogs(NSArray newChangeLogs) {
        changeLogs = newChangeLogs;
    }
    
    public String adminNotes() {
        return adminNotes;
    }
    public void setAdminNotes(String newAdminNotes) {
        adminNotes = newAdminNotes;
    }
    
    public String messageSubject() {
        return messageSubject;
    }
    public void setMessageSubject(String newMessageSubject) {
        messageSubject = newMessageSubject;
    }
    
    public String messageBody() {
        return messageBody;
    }
    public void setMessageBody(String newMessageBody) {
        messageBody = newMessageBody;
    }
    
    public String messageSubjectRequiredErrorMessage() {
        return messageSubjectRequiredErrorMessage;
    }
    public void setMessageSubjectRequiredErrorMessage(String newMessageSubjectRequiredErrorMessage) {
        messageSubjectRequiredErrorMessage = newMessageSubjectRequiredErrorMessage;
    }
    
}
