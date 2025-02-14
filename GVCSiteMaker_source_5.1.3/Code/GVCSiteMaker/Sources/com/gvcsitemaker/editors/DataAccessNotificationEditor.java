// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.jmail.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * This page is used to edit (configure) notifications for DA sections.  Since no other section 
 * types have notifications, this doesn't try to do anything fancy to allow multiple editor types.
 */
public class DataAccessNotificationEditor extends DataAccessBaseEditor
{
    public String anInvalidRecipient;
    public WebsiteGroup anAccessGroup;
    public Column aColumn;
    public String dummyTemplateFilePath;           // For WOFileUpload, which fails to upload anything without a filePath binding!

    protected NSArray invalidOtherNotificationRecipients = null;
    protected String createTemplateValidationError;
    protected String modifyTemplateValidationError;
    protected boolean isMissingRecipients;
    protected boolean isCreateSubjectMissing;
    protected boolean isModifySubjectMissing;
    protected boolean isFromAddressInvalid;


    /**
     * Designated constructor.
     * 
     * @param context the context in which this page is created
     */
    public DataAccessNotificationEditor(WOContext context)
    {
        super(context);
        /** require [valid_param] context != null; **/
    }



    /*
     * Overidden to reset validation flags.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);

        setInvalidOtherNotificationRecipients(null);
        setCreateTemplateValidationError(null);
        setModifyTemplateValidationError(null);
        setIsMissingRecipients(false);
        setIsCreateSubjectMissing(false);
        setIsModifySubjectMissing(false);
        setIsFromAddressInvalid(false);
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Validates the configuration.  Redisplays this page with error messages if it is invalid.  
     * If everything is valid, uses _doUpdateWithPreview to save changes to this Section's
     * components and returns the result of that method as the next page to display.
     *
     * @return the page to show after saving.
     */
    public WOComponent updateSettings()
    {
        validateConfiguration();
        
        if (isConfigurationValid())
        {
            return super.updateSettings();
        }
        else
        {
            return context().page();
        }
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Performs local validation for attributes changed directly in this component.  The templates
     * are checked at point of upload.  All other values are checked here.
     */
    public void validateConfiguration()
    {
        // We must have at least one recipient if we are sending any notifications
        if ((dataAccessComponent().shouldNotifyOnCreateRecords() || 
             dataAccessComponent().shouldNotifyOnModifyRecords()    ) && 
            (dataAccessComponent().notificationGroups().count() == 0) &&
             StringAdditions.isEmpty(dataAccessComponent().otherNotificationRecipients())  &&
             (dataAccessComponent().notificationColumns().count() == 0) )
        {
            setIsMissingRecipients(true);
        }

        // Check validity of any manually entered e-mail addresses or internal user IDs
        UserIDTokenizer idChecker = new UserIDTokenizer(dataAccessComponent().otherNotificationRecipients());
        if (idChecker.foundInvalidUserIDs())
        {
            setInvalidOtherNotificationRecipients(idChecker.invalidUserIDs());
        }
        
        // Check required subjects
        setIsCreateSubjectMissing(dataAccessComponent().shouldNotifyOnCreateRecords() &&
            StringAdditions.isEmpty(dataAccessComponent().createRecordsNotificationSubject()));
        setIsModifySubjectMissing(dataAccessComponent().shouldNotifyOnModifyRecords() &&
            StringAdditions.isEmpty(dataAccessComponent().modifyRecordsNotificationSubject()));
        
        if (( ! StringAdditions.isEmpty(dataAccessComponent().notificationFromAddress())) && (! EmailAddress.isValidAddressFormat(dataAccessComponent().notificationFromAddress())))
        {
            setIsFromAddressInvalid(true);
        }        
    }



    /**
     * Returns <code>true</code> if all parts of the configuration are valid.  Save will not proceed
     * if this returns <code>false</code>.
     * 
     * @return <code>true</code> if all parts of the configuration are valid
     */
    public boolean isConfigurationValid()
    {
        return ! (isMissingRecipients() || 
                  otherNotificationRecipientsInvalid() ||
                  isFromAddressInvalid() ||
                  (createTemplateValidationError() != null) ||
                  (modifyTemplateValidationError()!= null) );
    }



    /**
     * Returns <code>true</code> if either of the templates were uploaded and found to be invalid.
     * 
     * @return <code>true</code> if either of the templates are invalid
     */
    public boolean hasInvalidUpload() 
    {
        return (createTemplateValidationError() != null) ||
               (modifyTemplateValidationError() != null);
    }
    


    /**
     * Returns <code>true</code> if the otherNotificationRecipients are invalid, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the otherNotificationRecipients are invalid, <code>false</code> otherwise
     */
    public boolean otherNotificationRecipientsInvalid()
    {
        return invalidOtherNotificationRecipients() != null;
    }



    /**
     * Action used to open the section for regular editing.
     *
     * @return the <code>EditSection</code> page set for the given section 
     */
    public EditSection editSection()
    {
        EditSection nextPage = (EditSection)pageWithName("EditSection");
        nextPage.setSection(section());
        return nextPage;
    }



    /**
     * Deletes the custom create template.  Does not save changes.
     *
     * @return this page
     */
    public WOComponent deleteCreateTemplate()
    {
        dataAccessComponent().deleteCustomTemplateForCreate();
        return context().page();
    }



    /**
     * Set the content of the custom create template if something was uploaded.
     *
     * @param newUploadedTemplate the new content of the custom create template.
     */
    public void setUploadedCreateTemplate(NSData newUploadedTemplate)
    {
        if ((newUploadedTemplate != null) && (newUploadedTemplate.length() > 0))
        {
            try
            {
                dataAccessComponent().setCustomTemplateForCreate(new String(newUploadedTemplate.bytes()));
                dataAccessComponent().setUseCustomTemplateForCreate(true);            
            }
            catch (NSValidation.ValidationException e)
            {
                setCreateTemplateValidationError("Template not uploaded! " + e.getMessage() + 
                    " For help see below.");
            }
        }
    }



    /**
     * Empty method to keep binding synchronization happy (WOFileUpload).
     */
    public NSData uploadedCreateTemplate()
    {
        return NSData.EmptyData;
    }



    /**
     * Returns the content for the create template as an NSData object.
     *
     * @return the content for the create template
     */
    public NSData templateForCreateNotification()
    {
        return new NSData(dataAccessComponent().templateForCreateNotification().getBytes());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Deletes the custom modify template.  Does not save changes.
     *
     * @return this page
     */
    public WOComponent deleteModifyTemplate()
    {
        dataAccessComponent().deleteCustomTemplateForModify();
        return context().page();
    }



    /**
     * Set the content of the custom modify template if something was uploaded.
     *
     * @param newUploadedTemplate the new content of the custom modify template.
     */
    public void setUploadedModifyTemplate(NSData newUploadedTemplate)
    {
        if ((newUploadedTemplate != null) && (newUploadedTemplate.length() > 0))
        {
            try
            {
                dataAccessComponent().setCustomTemplateForModify(new String(newUploadedTemplate.bytes()));
                dataAccessComponent().setUseCustomTemplateForModify(true);
            }
            catch (NSValidation.ValidationException e)
            {
                setModifyTemplateValidationError("Template not uploaded! " + e.getMessage() + 
                    " For help see below.");
            }
        }
    }



    /**
     * Empty method to keep binding synchornization happy (WOFileUpload).
     */
    public NSData uploadedModifyTemplate()
    {
        return NSData.EmptyData;
    }



    /**
     * Returns the content for the modify template as an NSData object.
     *
     * @return the content for the modify template
     */
    public NSData templateForModifyNotification()
    {
        return new NSData(dataAccessComponent().templateForModifyNotification().getBytes());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Cover method for section().component().useCustomTemplateOnCreate() for radio button.
     */
    public boolean useDefaultTemplateForCreate() 
    {
        return ! dataAccessComponent().useCustomTemplateForCreate();
    }



    /**
     * Cover method for section().component().setUseCustomTemplateOnCreate() for radio button.
     */
    public void setUseDefaultTemplateForCreate(boolean newUseDefaultTemplate) 
    {
        if (newUseDefaultTemplate)
        {
            dataAccessComponent().setUseCustomTemplateForCreate(false);
        }
    }



    /**
     * Cover method for section().component().useCustomTemplateOnCreate() for radio button.
     */
    public boolean useCustomTemplateForCreate()  
    {
        return dataAccessComponent().useCustomTemplateForCreate();
    }



    /**
     * Cover method for section().component().setUseCustomTemplateOnCreate() for radio button.
     */
    public void setUseCustomTemplateForCreate(boolean newUseCustomTemplate) 
    {
        if (newUseCustomTemplate)
        {
            dataAccessComponent().setUseCustomTemplateForCreate(true);
        }
    }

    
    
    /**
     * Cover method for section().component().useCustomTemplateOnModify() for radio button.
     */
    public boolean useDefaultTemplateForModify()
    {
        return ! dataAccessComponent().useCustomTemplateForModify();
    }



    /**
     * Cover method for section().component().setUseCustomTemplateOnModify() for radio button.
     */
    public void setUseDefaultTemplateForModify(boolean newUseDefaultTemplate) 
    {
        if (newUseDefaultTemplate)
        {
            dataAccessComponent().setUseCustomTemplateForModify(false);
        }
    }



    /**
     * Cover method for section().component().useCustomTemplateOnModify() for radio button.
     */
    public boolean useCustomTemplateForModify()
    {
        return dataAccessComponent().useCustomTemplateForModify();
    }



    /**
     * Cover method for section().component().setUseCustomTemplateOnModify() for radio button.
     */
    public void setUseCustomTemplateForModify(boolean newUseCustomTemplate) 
    {
        if (newUseCustomTemplate)
        {
            dataAccessComponent().setUseCustomTemplateForModify(true);
        }
    }



    /**
     * Returns the list of columns to display to the user as options to send notification messages 
     * to.  This is all columns that might lead to an e-mail address: String columns, User columns, 
     * and Lookup columns looking up a String column.
     * 
     * @return the list of columns to display to the user
     */
    public NSArray columns()
    {
        NSMutableArray columns = new NSMutableArray();
        VirtualTable table = (VirtualTable)databaseTable();

        Enumeration columnEnumerator = table.orderedColumns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column column = (Column)columnEnumerator.nextElement();
            if (column.type().name().equals(ColumnType.StringColumnType) || 
                column.type().name().equals(VirtualUserField.ColumnTypeName) ||
                ((column.type().name().equals(ColumnType.LookupColumnType)) && 
                 (((VirtualLookupColumn)column).lookupColumn().type().name().equals(ColumnType.StringColumnType))) ||
                ((column.type().name().equals(ColumnType.CalculatedColumnType)) &&
                 ((VirtualCalculatedColumn)column).expressionType().equals(ColumnType.StringColumnType)))
            {
                columns.addObject(column);
            }
        }

        return columns;
        /** ensure [valid_result] Result != null;  **/
    }




    /**
     * Returns the website's local groups whose members receive notifications from this section.
     * 
     * @TypeInfo Group 
     * @return the website's local groups whose members receive notifications from this section.
     */   
    public NSArray selectedAccessGroups() 
    {
        return dataAccessComponent().notificationGroups();
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Sets the website's local groups whose members receive notifications from this section.
     * 
     * @param selectedAccessGroups the groups whose members will receive notifications from this section
     */   
    public void setSelectedAccessGroups(NSArray selectedAccessGroups) 
    {
        /** require [valid_param] selectedAccessGroups != null;  **/
        Enumeration groupEnum = website().orderedLocalGroups().objectEnumerator();
        while (groupEnum.hasMoreElements())
        {
            Group aGroup = (Group) groupEnum.nextElement();
            if (selectedAccessGroups.containsObject(aGroup) && 
                ! dataAccessComponent().notificationGroups().containsObject(aGroup))
            {
                dataAccessComponent().addObjectToBothSidesOfRelationshipWithKey(aGroup, "notificationGroups");
            }
            else if (dataAccessComponent().notificationGroups().containsObject(aGroup) && 
                ! selectedAccessGroups.containsObject(aGroup))
            {
                dataAccessComponent().removeObjectFromBothSidesOfRelationshipWithKey(aGroup, "notificationGroups");
            }
        }
    }



    //*********** Generic Get / Set methods  ***********\\


    public NSArray invalidOtherNotificationRecipients() {
        return invalidOtherNotificationRecipients;
    }
    public void setInvalidOtherNotificationRecipients(NSArray newInvalidOtherNotificationRecipients) {
        invalidOtherNotificationRecipients = newInvalidOtherNotificationRecipients;
    }

    public String createTemplateValidationError() {
        return createTemplateValidationError;
    }
    public void setCreateTemplateValidationError(String newCreateTemplateValidationError) {
        createTemplateValidationError = newCreateTemplateValidationError;
    }
    
    public String modifyTemplateValidationError() {
        return modifyTemplateValidationError;
    }
    public void setModifyTemplateValidationError(String newModifyTemplateValidationError) {
        modifyTemplateValidationError = newModifyTemplateValidationError;
    }
    
    public boolean isMissingRecipients() {
        return isMissingRecipients;
    }
    public void setIsMissingRecipients(boolean newIsMissingRecipients) {
        isMissingRecipients = newIsMissingRecipients;
    }
    
    public boolean isCreateSubjectMissing() {
        return isCreateSubjectMissing;
    }
    public void setIsCreateSubjectMissing(boolean newIsCreateSubjectMissing) {
        isCreateSubjectMissing = newIsCreateSubjectMissing;
    }

    public boolean isModifySubjectMissing() {
        return isModifySubjectMissing;
    }
    public void setIsModifySubjectMissing(boolean newIsModifySubjectMissing) {
        isModifySubjectMissing = newIsModifySubjectMissing;
    }

    public boolean isFromAddressInvalid() {
        return isFromAddressInvalid;
    }
    public void setIsFromAddressInvalid(boolean value) {
        isFromAddressInvalid = value;
    }    
}
