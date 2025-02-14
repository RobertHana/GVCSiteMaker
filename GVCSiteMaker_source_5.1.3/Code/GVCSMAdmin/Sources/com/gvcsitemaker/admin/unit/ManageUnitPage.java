// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.unit;
import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.admin.appserver.*;
import com.gvcsitemaker.admin.commonwidgets.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

public class ManageUnitPage extends WOComponent implements SMSecurePage
{

    protected OrgUnit _unit;
    public OrgUnit unitToManage, newParentUnit;
    public String newAdminID, deleteFailedErrorString;
    public NSDictionary errorMessages;
    protected boolean includeSubUnitAdmins;
    protected String messageSubject;
    protected String messageBody;
    protected String missingSubjectError;
    public boolean shouldDownloadTemplate;      // Flag to assist in downloading
    public NSData templateData;             // Uploaded template data
    public String dummyFilePath;
    protected String invalidWebsiteCreationTemplateMesssage;

    public ManageUnitPage(WOContext aContext)
    {
        super(aContext);
        
        // Defaults
        setIncludeSubUnitAdmins(true);
        shouldDownloadTemplate = false;
    }


    /**
     * Handles template download and resets page state.
     * 
     * @see com.webobjects.appserver.WOElement#appendToResponse(com.webobjects.appserver.WOResponse, com.webobjects.appserver.WOContext)
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        // downloadWebsiteCreationMessage() sets this flag.
        if (shouldDownloadTemplate)
        {
            shouldDownloadTemplate = false;

            // Set the headers so the tempalte will be downloaded, not displayed.
            String filename = URLUtils.toLowerAndNormalize(getUnit().unitName()) + "_newsite.html";
            aResponse.setHeader("attachment; filename=\"" + filename + "\"", Response.ContentDispositionHeaderKey);
            aResponse.setHeader("application/octet-stream", Response.ContentTypeHeaderKey);  // Forces browser to download not display
            aResponse.setContent(getUnit().websiteCreationMessageTemplate());
            // Don't call super here or this page will get appended to the template!
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }
        
        errorMessages = null;
        setMissingSubjectError(null);
        setInvalidWebsiteCreationTemplateMesssage(null);
        templateData = null;
    }



    public WOComponent updateProperties() 
    {
        // Only update websiteCreationMessage if something was actually uploaded.
        if ( ! ((templateData == null) || templateData.isEqualToData(NSData.EmptyData)) )
        {
            String stringTemplate = new String(templateData.bytes());
            try
            {
                WebsiteCreationMessage.validateTemplate(stringTemplate);
                getUnit().setWebsiteCreationMessage(stringTemplate);
            }
            catch (NSValidation.ValidationException e)
            {
                setInvalidWebsiteCreationTemplateMesssage(e.getMessage());
            }
        }
        
        errorMessages = getUnit().updateProperties( newParentUnit, editingContext() );
    
        return null;
    }
    
    public ManageUnitPage manageUnit() {
        ManageUnitPage nextPage = (ManageUnitPage)pageWithName("ManageUnitPage");
        nextPage.setUnit( unitToManage );
        return nextPage;
    }


    public WOComponent addAdmin()
    {
        NSDictionary errorsFromAddingAdmin = getUnit().addAdminForAdminID(newAdminID);
        if (errorsFromAddingAdmin.count() == 0)
        {
            editingContext().saveChanges();
            newAdminID = "";
        }
        else
        {
            editingContext().revert();
            errorMessages = errorsFromAddingAdmin;
        }
        return null;
    }
    
    
    
    /**
     * Returns this page which results (via code in appendToResponse) in the template for this style being returned.
     *
     * @return this page which results (via code in appendToResponse) in the template for this style being returned.
     */
    public WOComponent downloadWebsiteCreationMessage()
    {
        
        shouldDownloadTemplate = true;
        
        return context().page();
    }

    
    
    /**
     * Removes any website creation message template from this unit but does not commit the changes.
     *  
     * @return this page
     */
    public WOComponent deleteWebsiteCreationMessage() 
    {
        getUnit().setWebsiteCreationMessage(null);
        
        return context().page();
    }


    public WOComponent toggleListingStatus() {
        errorMessages = null;
        editingContext().revert();
        getUnit().setDisplayOrgUnitInPublicListOfUnits(!getUnit().displayOrgUnitInPublicListOfUnits());
        editingContext().saveChanges();
        return null;
    }

    public Main removeUnit()
    {
        editingContext().deleteObject(getUnit());
        editingContext().saveChanges();
        return (Main)pageWithName("Main");
    }



    /**
     * Action method to send message to all admins of this unit and, optionally 
     * (<code>includeSubUnitAdmins()</code>), the admins of all units below this unit in the 
     * hierarchy.
     * 
     * @return this page
     */
    public WOComponent sendMessageToAdmins()
    {
        if (StringAdditions.isEmpty(messageSubject())) 
        {
            setMissingSubjectError("You must enter a subject for the message.");
        }  
        else
        {
            // Satisfy contract on SendEmail.sendIndividualMessages
            if (messageBody() == null) 
            {
                setMessageBody("");
            }  
            
            NSArray admins = includeSubUnitAdmins() ? getUnit().adminsInAndBelow() : getUnit().admins();
            
            if (admins.count() > 0)
            {
                NSArray emailAddresses = (NSArray) admins.valueForKey("emailAddress");
                SendEmail.sendIndividualMessages(currentUser().emailAddress(),
                                                 emailAddresses,
                                                 messageSubject(),
                                                 messageBody());
            }
            
            // Reset UI
            setMessageBody(null);
            setMessageSubject(null);
        }
        
        return context().page();
    }
    
    
    public Float fsUsage() 
    {
        return new Float(getUnit().fileSizeUsageInMegabytes());
    }



    /**
     * Returns <code>true</code> if the org units website creation message can be deleted.  It can't
     * be deleted if it does not exist, or if this is the root unit where the message is mandatory.
     *  
     * @return <code>true</code> if the org units website creation message can be deleted
     */
    public boolean canDeleteWebsiteCreationMessage()
    {
        return ( ! getUnit().isRootUnit()) && ( ! StringAdditions.isEmpty(getUnit().websiteCreationMessage()));
    }
    
    
    
    public boolean unitCanBeDeleted() {
        return (((Session)session()).currentUser().isSystemAdmin() &&
                getUnit().canBeDeleted());
    }

    public boolean displayNullUnitNameErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NULL_UNIT_NAME_ERROR_KEY) != null));
    }

    public boolean displayUnitNameExistsErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.UNIT_NAME_EXISTS_ERROR_KEY) != null));
    }

    public boolean displayNullEncompassingUnitErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NULL_ENCOMPASSING_UNIT_ERROR_KEY) != null));
    }

    public boolean displayNewParentIsChildErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NEW_PARENT_IS_CHILD_ERROR_KEY) != null));
    }

    public boolean displayNullFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NULL_FILESPACE_QUOTA_ERROR_KEY) != null));
    }

    public boolean displayInvalidFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.INVALID_FILESPACE_QUOTA_ERROR_KEY) != null));
    }

    public boolean displayAdminIDNotFoundErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(User.ADMIN_ID_NOT_FOUND_ERROR_KEY) != null));
    }

    public boolean displayAdminIDInUseErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(User.ADMIN_ID_IN_USE_ERROR_KEY) != null));
    }

    public boolean displayDeleteNotPermittedErrorMessages() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(Deleteable.DELETE_NOT_PERMITTED_ERROR_KEY) != null));
    }

    // the following methods are not required, but i'm using them so we
    // can make use of the constants defined for the keys.  then, if the keys
    // get changed for some reason in the future, the code will not suddenly
    // be broken.
    public String nullUnitNameErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NULL_UNIT_NAME_ERROR_KEY);
    }

    public String unitNameExistsErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.UNIT_NAME_EXISTS_ERROR_KEY);
    }

    public String nullEncompassingUnitErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NULL_ENCOMPASSING_UNIT_ERROR_KEY);
    }

    public String newParentIsChildErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NEW_PARENT_IS_CHILD_ERROR_KEY);
    }

    public String nullFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NULL_FILESPACE_QUOTA_ERROR_KEY);
    }

    public String invalidFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.INVALID_FILESPACE_QUOTA_ERROR_KEY);
    }

    public String adminIDNotFoundErrorMessage() {
        return (String)errorMessages.objectForKey(User.ADMIN_ID_NOT_FOUND_ERROR_KEY);
    }

    public String adminIDInUseErrorMessage() {
        return (String)errorMessages.objectForKey(User.ADMIN_ID_IN_USE_ERROR_KEY);
    }

    public NSArray deleteNotPermittedErrorArray() {
        return (NSArray)errorMessages.objectForKey(Deleteable.DELETE_NOT_PERMITTED_ERROR_KEY);
    }


    public String displaySystemWide()
    {
        return getUnit().displayOrgUnitInPublicListOfUnits() ? "Yes" : "No"; 
    }

    /* ********** Generic setters and getters ************** */
    public OrgUnit getUnit() {
        return _unit;
    }

    public void setUnit( OrgUnit newUnit ) {
        newParentUnit = newUnit.parentOrgUnit();
        _unit = newUnit;
    }

    public boolean includeSubUnitAdmins() {
        return includeSubUnitAdmins;
    }
    public void setIncludeSubUnitAdmins(boolean newIncludeSubUnitAdmins) {
        includeSubUnitAdmins = newIncludeSubUnitAdmins;
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

    public String missingSubjectError() {
        return missingSubjectError;
    }
    public void setMissingSubjectError(String newMissingSubjectError) {
        missingSubjectError = newMissingSubjectError;
    }

    public User currentUser() {
        return ((Session)session()).currentUser();
    }

    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
    }
    public String invalidWebsiteCreationTemplateMesssage() {
        return invalidWebsiteCreationTemplateMesssage;
    }
    public void setInvalidWebsiteCreationTemplateMesssage(String newInvalidWebsiteCreationTemplateMesssage) {
        invalidWebsiteCreationTemplateMesssage = newInvalidWebsiteCreationTemplateMesssage;
    }

}
