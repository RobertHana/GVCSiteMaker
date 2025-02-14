// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.admin.appserver.*;
import com.gvcsitemaker.admin.commonwidgets.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/** 
 * This page handles the creation of a new Website, optionally from a WebsiteRequest and / or as a
 * copy of another site.
 */
public class CreateWebsitePage extends WOComponent implements SMSecurePage
{
    public NSData fileData;
    public String filePath;
    public String ownerID, siteID, exampleSiteID, redirectUrl;
    protected NSArray rootOrgUnits;
    protected OrgUnit associatedUnit;
    protected OrgUnit previouslySelectedUnit;
    public NSDictionary errorMessages;
    public Integer quota;
    private static final String INVALID_BATCH_FILE_ERROR_KEY = "invalidBatchFileErrorKey";
    protected EOEditingContext editingContext;
    protected String saveFailureMessage;

    protected String messageSubject;
    protected String messageBody;
    protected boolean dontSendMessage;

    protected String adminNote;

    protected WebsiteRequest websiteRequest;    
    protected boolean isMissingRequiredSubject;
                    protected String templateError;
    
    
    
    /**
     * Designated constructor.  Sets up default values.
     */
    public CreateWebsitePage(WOContext aContext)
    {
        super(aContext);
        editingContext = ((SMSession)session()).newEditingContext();
        
        // Establish defaults
        quota = new Integer( SMApplication.appProperties().intPropertyForKey( "DefaultQuotaInMegabytes" ));
        rootOrgUnits = EOUtilities.localInstancesOfObjects(editingContext(), 
                                                           ((SMSession)session()).currentUser().orgUnitsToAdmin());
        rootOrgUnits = NSArrayAdditions.sortedArrayUsingComparator(rootOrgUnits, OrgUnit.HierarchicalComparator);
        setAssociatedUnit((OrgUnit)rootOrgUnits.objectAtIndex(0));                                                           
        setPreviouslySelectedUnit(null);
        setMessageSubject(SMApplication.appProperties().stringPropertyForKey("WebsiteCreationSubject"));
    }



    /**
     * Overridden to update the messageBody (the websiteCreationMessageTemplate) when a different
     * OrgUnit is selected.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        if (associatedUnit() != previouslySelectedUnit())
        {
            setMessageBody(associatedUnit().websiteCreationMessageTemplate());
            setPreviouslySelectedUnit(associatedUnit());
        }

        super.appendToResponse(response, context);
        
        // Clear error messages
        setIsMissingRequiredSubject(false);
        setTemplateError(null);
    }



    /**
     * This is the starting point for creating a new Website in response to a Website Request.
     * 
     * @param newWebsiteRequest the Website Request to base the new site on
     */
    public void setWebsiteRequest(WebsiteRequest newWebsiteRequest)
    {
        /** require [valid_website] newWebsiteRequest != null;  
                    [request_not_set] websiteRequest() == null;             **/
        
        websiteRequest = (WebsiteRequest) EOUtilities.localInstanceOfObject(editingContext(), 
                                                                            newWebsiteRequest);
        setAssociatedUnit(websiteRequest().orgUnit());
        setOwnerID(websiteRequest().requester().userID());
        setSiteID(websiteRequest().siteID());
        
        /** ensure [request_set] websiteRequest() != null;
                   [unit_set] associatedUnit().equals(websiteRequest().orgUnit());
                   [owner_set] ownerID().equals(websiteRequest().requester().userID());
                   [site_id_set] siteID().equals(websiteRequest().siteID());               **/ 
    }
    
    

    /**
     * Main method for validating and creating the new site.
     * 
     * @return the Main page if creation succeeded, this page if there were validation errors
     */
    public WOComponent createNewWebsite()
    {

        WOComponent nextPage = context().page();

        /* Website Creation message validation
         * If we are sending the message and it is invalid, don't even attempt validation as 
         * validation attempts creation which can result in site creation even if the overall 
         * submission is invalid (e.g. the message).         
         */
        if ( ! dontSendMessage())
        {
            if (StringAdditions.isEmpty(messageSubject()))
            {
                setIsMissingRequiredSubject(true);
            }
            
            try
            {
                WebsiteCreationMessage.validateTemplate(messageBody());
            }
            catch (NSValidation.ValidationException e)
            {
                setTemplateError(e.getMessage());
            }
            
            if (isMissingRequiredSubject() || (templateError() != null))
            {
                return nextPage;
            }
        }
        
        EOEditingContext ec = ((SMSession)session()).newEditingContext();

        try
        {
            setSaveFailureMessage(null);
            
            // Make local copies of the EOs
            User adminUser = (User)EOUtilities.localInstanceOfObject(ec, ((Session) session()).currentUser());
            OrgUnit underUnit = (OrgUnit)EOUtilities.localInstanceOfObject(ec, associatedUnit);
            WebsiteRequest request = null;
            if (websiteRequest() != null)
            { 
                request = (WebsiteRequest)EOUtilities.localInstanceOfObject(ec, websiteRequest());
            }
            
            // Validate and create
            if ( ! StringAdditions.isEmpty(exampleSiteID))
            {
                errorMessages = Website.validateWebsiteCreationBasedOnExampleSite( underUnit, siteID, ownerID, quota, redirectUrl,
                                                                                   ec, adminUser, exampleSiteID);
                if (errorMessages == null)
                {
                    CopySiteTask newCopySiteTask = CopySiteTask.newCopySiteTask(underUnit,  siteID, ownerID, quota, redirectUrl,
                            ec, adminUser, exampleSiteID,
                            messageSubject(), messageBody(), dontSendMessage(), request, adminNote);
                    
                    ec.saveChanges();            

                    WOComponent queuedTaskPage = pageWithName("QueuedTasksPage");
                    ((QueuedTasksPage) queuedTaskPage).setPageToReturnTo(pageWithName("Main"));
                    ((QueuedTasksPage) queuedTaskPage).setNewRequest(newCopySiteTask);            
                    
                    nextPage = queuedTaskPage;                    
                    
                    /*site copy is now being queued
                        Website.createWebsiteBasedOnExampleSite(underUnit,  siteID, ownerID, quota, redirectUrl,
                                                            editingContext, adminUser, exampleSiteID,
                                                            messageSubject(), messageBody(), dontSendMessage());*/
                }
            }
            else
            {
                errorMessages = Website.createWebsite(underUnit, siteID, ownerID, quota, redirectUrl, 
                                                      ec, adminUser, messageSubject(), 
                                                      messageBody(), dontSendMessage() );
                
                // Save (sort of, most of the Website has already been saved due to poor design in the
                // Website creation process.
                if (errorMessages == null)
                {
                    ec.saveChanges();
                    
                    // Damnation!  Create method don't return the website!
                    Website createdSite = Website.websiteForSiteID(siteID, ec);
                    
                    // Log site creation
                    createdSite.logSiteCreation(adminUser, 
                                                adminNote,
                                                request,
                                                exampleSiteID);
                        
                    // If this site was created as the result of a Website Request, then mark it granted.                            
                    if (request != null)
                    {
                        request.grant(createdSite);
                    }
                    
                    ec.saveChanges();            
                    nextPage = pageWithName("Main");
                }
            }

        }
        // Handle failures in saveChanges()
        catch (NSValidation.ValidationException e)
        {
            setSaveFailureMessage("This Website has problems and cannot be copied.  Specifically, " +
                                  ((NSArray)ValidationExceptionAdditions.unaggregateExceptions(e).valueForKey("message")).
                                  componentsJoinedByString(", "));
        }
        finally
        {
            ((SMSession)session()).unregisterEditingContext(ec);
        }

        return nextPage;
    }

    


    
    /* Commented out until we can implement the long response page and multi-threading.
    public WOComponent createNewWebsite()
    {

        WOComponent nextPage = context().page();
        try
        {
            if ( exampleSiteID != null && exampleSiteID.length() > 0 )
            {
                errorMessages = Website.validateWebsiteCreationBasedOnExampleSite( associatedUnit, siteID, ownerID, quota, redirectUrl,
                                                                                   editingContext(),
                                                                                   ((Session) session()).currentUser(), exampleSiteID);
                if (errorMessages == null)
                {
                    nextPage = pageWithName("CopyWebsitePage");
                    CopyWebsitePage copyWebsitePage = (CopyWebsitePage) nextPage;
                    copyWebsitePage.setAssociatedUnit(associatedUnit);
                    copyWebsitePage.setSiteID(siteID);
                    copyWebsitePage.setExampleSiteID(exampleSiteID);
                    copyWebsitePage.setOwnerID(ownerID);
                    copyWebsitePage.setQuota(quota);
                    copyWebsitePage.setRedirectUrl(redirectUrl);
                    copyWebsitePage.setAdminUser(((Session) session()).currentUser());
                }
                else
                {
                    editingContext().revert();
                }
            }
            else
            {
                errorMessages = Website.createWebsite(associatedUnit, siteID, ownerID, quota, redirectUrl, editingContext() );
                if (errorMessages == null)
                {
                    editingContext().saveChanges();
                    nextPage = pageWithName("Main");
                }
                else
                {
                    editingContext().revert();
                }
            }
        }
        
        return nextPage;
    }
    */


    public WOComponent createNewWebsitesFromBatchFile() {
        NSArray newWebsites = SMFileUtils.arrayOfObjectsFromBatchFileData(fileData,
                                                                         Website.BATCH_FILE_KEY_ARRAY);
        DebugOut.println(1, "uploaded data = \n" + newWebsites);
		if( newWebsites != null ) {
			Website.validateArrayOfWebsiteDictionaries( ((Session)session()).currentUser(),
                                                        newWebsites,
                                                        editingContext() );
			DebugOut.println(1, "validated data = \n" + newWebsites);

			ConfirmWebsiteCreationPage nextPage = (ConfirmWebsiteCreationPage)pageWithName("ConfirmWebsiteCreationPage");
			nextPage.setWebsiteArray(newWebsites);
			return nextPage;
		}

        NSMutableDictionary errorDict = new NSMutableDictionary();
        errorDict.setObjectForKey("The batch file you have selected is not the proper format " +
                                  "or it does not contain the proper number of fields",
                                  INVALID_BATCH_FILE_ERROR_KEY);
        errorMessages = errorDict;
		return null;
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

    public boolean displayNullAssociatedUnitErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.NULL_ASSOCIATED_UNIT_ERROR_KEY) != null));
    }

    public boolean displayInvalidFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.INVALID_FILESPACE_QUOTA_ERROR_KEY) != null));
    }

    public boolean displayInsufficientFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.INSUFFICIENT_FILESPACE_QUOTA_ERROR_KEY) != null));
    }
    
    public boolean displaySiteCanNotBeReplicatedErrorMessage() {
        return ((errorMessages != null) &&
                (errorMessages.objectForKey(Website.SITE_CAN_NOT_BE_REPLICATED_KEY) != null));
    }
    

    public boolean displayInvalidBatchFileErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(INVALID_BATCH_FILE_ERROR_KEY) != null));
    }

    public boolean displayExampleSiteIDNotFoundErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Website.EXAMPLE_SITE_ID_NOT_FOUND_ERROR_KEY) != null));
    }

    public boolean displayInvalidRedirectURLErrorMessage() {
        return ((errorMessages != null) &&
                (errorMessages.objectForKey(Website.INVALID_REDIRECT_URL_ERROR_KEY) != null));
    }

    public boolean displayInvalidCharsInOwnerIDErrorMessage() {
        return ((errorMessages != null) &&
                (errorMessages.objectForKey(Website.INVALID_CHARS_ERROR_KEY) != null));
    }

        
    // the following methods are not required, but i'm using them so we
    // can make use of the constants defined for the keys.  then, if the keys
    // get changed for some reason in the future, the code will not suddenly
    // be broken.
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

    public String nullAssociatedUnitErrorMessage() {
        return (String)errorMessages.objectForKey(Website.NULL_ASSOCIATED_UNIT_ERROR_KEY);
    }

    public String invalidFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_FILESPACE_QUOTA_ERROR_KEY);
    }

    public String insufficientFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INSUFFICIENT_FILESPACE_QUOTA_ERROR_KEY);
    }
    
    public String siteCanNotBeReplicatedErrorMessage() {
        return (String)errorMessages.objectForKey(Website.SITE_CAN_NOT_BE_REPLICATED_KEY);
    }


    public String invalidBatchFileErrorMessage() {
        return (String)errorMessages.objectForKey(INVALID_BATCH_FILE_ERROR_KEY);
    }

    public String exampleSiteIDNotFoundErrorMessage() {
        return (String)errorMessages.objectForKey(Website.EXAMPLE_SITE_ID_NOT_FOUND_ERROR_KEY);
    }

    public String invalidRedirectUrlErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_REDIRECT_URL_ERROR_KEY);
    }

    public String invalidCharsInOwnerIDErrorMessage() {
        return (String)errorMessages.objectForKey(Website.INVALID_CHARS_ERROR_KEY);
    }

    public Main returnToAdminMainPage()
    {
        Main nextPage = (Main)pageWithName("Main");

        editingContext().revert();

        return nextPage;
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

    public EOEditingContext editingContext()
    {
        return editingContext;
    }


    public String saveFailureMessage() {
        return saveFailureMessage;

    }
    public void setSaveFailureMessage(String newMessage) {
        saveFailureMessage = newMessage;

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
    
    public boolean dontSendMessage() {
        return dontSendMessage;
    }
    public void setDontSendMessage(boolean newDontSendMessage) {
        dontSendMessage = newDontSendMessage;
    }
    
    public String adminNote() {
        return adminNote;
    }
    public void setAdminNote(String newAdminNote) {
        adminNote = newAdminNote;
    }

    public OrgUnit associatedUnit() {
        return associatedUnit;
    }
    public void setAssociatedUnit(OrgUnit unit)
    {
        associatedUnit = unit;
    }


    public NSArray rootOrgUnits() {
        return rootOrgUnits;
    }
    
    public WebsiteRequest websiteRequest() {
        return websiteRequest;
    }
    public OrgUnit previouslySelectedUnit() {
        return previouslySelectedUnit;
    }
    public void setPreviouslySelectedUnit(OrgUnit newPreviouslySelectedUnit) {
        previouslySelectedUnit = newPreviouslySelectedUnit;
    }
    
    public String ownerID() {
        return ownerID;
    }
    public void setOwnerID(String string) {
        ownerID = string;
    }

    public String siteID() {
        return siteID;
    }
    public void setSiteID(String string) {
        siteID = string;
    }

    public boolean isMissingRequiredSubject() {
        return isMissingRequiredSubject;
    }
    public void setIsMissingRequiredSubject(boolean newIsMissingRequiredSubject) {
        isMissingRequiredSubject = newIsMissingRequiredSubject;
    }

    public String templateError() {
        return templateError;
    }
    public void setTemplateError(String newTemplateError) {
        templateError = newTemplateError;
    }
    
}
