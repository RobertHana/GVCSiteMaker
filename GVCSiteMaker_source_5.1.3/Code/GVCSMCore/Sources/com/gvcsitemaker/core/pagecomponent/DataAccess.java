// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.jmail.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * DataAccess is the root PageComponent implementing a DataAccess Section.  It has four children, one for each mode: Single Record, List of Records, Add Record, and Search for Records.
 */
public class DataAccess extends _DataAccess
{
    // Binding keys
    public static final String CAN_BROWSE_OR_SEARCH_BINDINGKEY = "canBrowseOrSearch";
    public static final String CAN_ADD_RECORDS_BINDINGKEY = "canAdd";
    public static final String CAN_DELETE_RECORDS_BINDINGKEY = "canDelete";
    public static final String CAN_IMPORT_RECORDS_BINDINGKEY = "canImport";
    
    public static final String DEFAULT_MODE_BINDINGKEY = "defaultMode";
    public static final String USE_DEFAULT_SEARCH_BINDINGKEY = "useDefaultSearch";
    public static final String RESTRICT_TO_DEFAULT_RESULTS_BINDINGKEY = "restrictToDefaultResults";

    public static final String GROUP_SIZE_BINDINGKEY = "groupSize";

    public static final String NOTIFY_ON_CREATE_BINDINGKEY = "notifyOnCreate";
    public static final String NOTIFY_ON_MODIFY_BINDINGKEY = "notifyOnModify";
    public static final String CREATE_RECORDS_NOTIFICATION_SUBJECT_BINDINGKEY = "createRecordsNotificationSubject";
    public static final String MODIFY_RECORDS_NOTIFICATION_SUBJECT_BINDINGKEY = "modifyRecordsNotificationSubject";
    public static final String USE_CUSTOM_TEMPLATE_ON_MODIFY_BINDINGKEY = "useCustomTemplateOnModify";
    public static final String USE_CUSTOM_TEMPLATE_ON_CREATE_BINDINGKEY = "useCustomTemplateOnCreate";
    public static final String CUSTOM_TEMPLATE_ON_CREATE_BINDINGKEY = "customTemplateOnCreate";
    public static final String CUSTOM_TEMPLATE_ON_MODIFY_BINDINGKEY = "customTemplateOnModify";
    public static final String OTHER_NOTIFICATION_RECIPIENTS_BINDINGKEY = "otherNotificationRecipients";
    public static final String NOTIFICATION_FROM_ADDRESS = "notificationFromAddress";



    /**
     * Factory method to create new instances of DataAccess.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccess or a subclass.
     */
    public static DataAccess newDataAccess()
    {
        return (DataAccess) SMEOUtils.newInstanceOf("DataAccess");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccess");

        // Set our defaults
        setCanAddRecords(true);
        setCanDeleteRecords(false);
        setCanImportRecords(false);
        setCanBrowseOrSearchRecords(true);
        setUseDefaultSearch(false);
        setRestrictToDefaultResults(false);
        setDefaultMode(DataAccessMode.LIST_MODE);
        setGroupSize((String)SMApplication.appProperties().
                     arrayPropertyForKey("DataAccessListSearchResultGroupSizes").objectAtIndex(0));
        setShouldNotifyOnCreateRecords(false);
        setShouldNotifyOnModifyRecords(false);
        setUseCustomTemplateForCreate(false);
        setUseCustomTemplateForModify(false);

        // Create children for each mode
        addChild(DataAccessAddMode.newDataAccessAddMode());
        addChild(DataAccessSingleMode.newDataAccessSingleMode());
        addChild(DataAccessListMode.newDataAccessListMode());
        addChild(DataAccessSearchMode.newDataAccessSearchMode());
        addChild(DataAccessImportMode.newDataAccessImportMode());
        
        /** ensure
        [can_add] canAddRecords();
        [cant_delete] ! canDeleteRecords();
        [can_browse_or_search] canBrowseOrSearchRecords();
        [dont_use_default_search] ! useDefaultSearch();
        [dont_restrict_to_default] ! restrictToDefaultResults();
        [has_add_mode] componentForMode(DataAccessMode.ADD_MODE) != null;
        [has_single_mode] componentForMode(DataAccessMode.SINGLE_MODE) != null;
        [has_list_mode] componentForMode(DataAccessMode.LIST_MODE) != null;
        [has_search_mode] componentForMode(DataAccessMode.SEARCH_MODE) != null;
        [has_group_size] groupSize() != null;
        [default_mode_list] defaultMode().equals(DataAccessMode.LIST_MODE); **/
    }



    /**
     * Returns a copy of an DataAccessColumn sub-class.  The copy is made manually as dataBaseTable needs to be set before our children are copied and the orderBy Column and the databaseTable unique to this component need to be copied.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this DataAccessColumn
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require 
             [copiedObjects_not_null] copiedObjects != null;  
             [databaseTable_not_null] databaseTable() != null;
         **/

        EOEnterpriseObject copy = EOCopying.Utility.newInstance(this);

        // Handle circular relationships by registering this object right away.
        EOGlobalID globalID = editingContext().globalIDForObject(this);
        copiedObjects.setObjectForKey(copy, globalID);

        EOCopying.Utility.copyAttributes(this, copy);

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        // Copy relationships in an order which prevents null pointer and other errors.
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toParent"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("sections"));        
        
        if (Section.isSectionOnlyCopy(copiedObjects))
        {
            DataAccess daCopy = (DataAccess) copy;
            // Our children need this when being copied so it should be copied first before copying toChildren
            daCopy.addObjectToBothSidesOfRelationshipWithKey(databaseTable(), "databaseTable");

            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toChildren"));           
            daCopy.setToRelatedFile(toRelatedFile());
            
            for (int i = 0, count = notificationColumns().count(); i < count; i++)
            {
                daCopy.addObjectToBothSidesOfRelationshipWithKey((EOEnterpriseObject)notificationColumns().objectAtIndex(i), 
                                                                 "notificationColumns");
            }

            for (int i = 0, count = notificationGroups().count(); i < count; i++)
            {
                daCopy.addObjectToBothSidesOfRelationshipWithKey((EOEnterpriseObject)notificationGroups().objectAtIndex(i), 
                                                                 "notificationGroups");
            }
        }
        else
        {
            // Our children need this when being copied so it should be copied first before copying toChildren
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("databaseTable"));
            
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toChildren"));            
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("toRelatedFile"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("notificationColumns"));            
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("notificationGroups"));
        }

        return copy;
        
        /** ensure
        [copy_made] Result != null;
        [databaseTable_valid] ((DataAccess)Result).databaseTable() != null; **/
    }



    /**
     * Validates the passed mode name.  If the name is valid and allowable given the configuration (can edit, can search etc.) of this component, it is returned.  If it is not valid the defaultMode() is returned.
     *
     * @param modeName the mode name to validate
     * @return the passed value if valid, defaultMode() if the passed value is not valid
     */
    public String validatedModeName(String modeName)
    {
        boolean isModeValid = (modeName != null);
        isModeValid = isModeValid && DataAccessMode.isValidMode(modeName);
        isModeValid = isModeValid && ((! modeName.equals(DataAccessMode.ADD_MODE)) || canAddRecords());
        isModeValid = isModeValid && ((! modeName.equals(DataAccessMode.SEARCH_MODE)) || canBrowseOrSearchRecords());
        isModeValid = isModeValid && ((! modeName.equals(DataAccessMode.IMPORT_MODE)) || canImportRecords());

        if ( ! isModeValid)
        {
            modeName = defaultMode();
        }

        return modeName;

        /** ensure [valid_mode] DataAccessMode.isValidMode(Result); **/
    }



    /**
     * Returns <code>true</code> if users can browse through the records in this DataAccess Section.
     *
     * @return <code>true</code> if users can browse through the records in this DataAccess Section.
     */
    public DataAccessMode componentForMode(String modeName)
    {
        /** require [mode_valid] DataAccessMode.isValidMode(modeName);  **/
        DataAccessMode componentForMode = null;

        Enumeration componentEnumerator = toChildren().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            DataAccessMode thisMode = (DataAccessMode) componentEnumerator.nextElement();

            if (thisMode.isMode(modeName))
            {
                componentForMode = thisMode;
                break;
            }
        }

        return componentForMode;
        /** esure [valid_result] Result != null;  **/
    }



    /**
     * Overridden to Capture existing columns on Table in each of the Data Access modes.
     *
     * @param aTable the Table to associate with this compoenent
     */
    public void setDatabaseTable(Table aTable)
    {
        /** require
        [valid_param] aTable != null;
        [table_not_set] (databaseTable() == null) || (databaseTable() == aTable); **/

        if (databaseTable() != aTable)
        {
            super.setDatabaseTable(aTable);
            synchronizeWithTable();
        }

        /** ensure [table_set] (databaseTable() != null) && (databaseTable() == aTable); **/
    }



    /**
     * Synchronizes columns on Table with DataAccessColumn children in each of the Data Access modes.
     */
    public void synchronizeWithTable()
    {
        /** require [has_table] databaseTable() != null; **/

        Enumeration componentEnumerator = toChildren().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            DataAccessMode thisMode = (DataAccessMode)componentEnumerator.nextElement();
            thisMode.synchronizeColumnsWithTable();
        }

        // If the user has deleted the last column(s) in the default search, then reset the "use default search" and "restrict to default search" flags.  There might not be a search mode when this is getting duplicated, hence the test:
        if ((componentForMode(DataAccessMode.SEARCH_MODE) != null)  &&
            (((DataAccessSearchMode)componentForMode(DataAccessMode.SEARCH_MODE)).orderedCriteria().count() == 0))
        {
            DebugOut.println(30, "Default search has no more criteria. Reseting flags.");
            setUseDefaultSearch(false);
            setRestrictToDefaultResults(false);
        }
    }



    /**
     * Returns <code>true</code> if the named column is visible in either Single Record or List of Records mode.  This is used to determine access on a field by field basis within a Section.
     *
     * @param aColumn the normalized name of the Column to check for permission to display
     * @return <code>true</code> if the named column is visible in either Single Record or List of Records mode.
     */
    public boolean isColumnVisible(String columnName)
    {
        /** require [valid_param] columnName != null;  [column_exists] databaseTable().hasColumnNamed(columnName);  **/
        DataAccessColumn singleModeColumn = componentForMode(DataAccessMode.SINGLE_MODE).dataAccessColumnForColumnNamed(columnName);
        DataAccessColumn listModeColumn = componentForMode(DataAccessMode.LIST_MODE).dataAccessColumnForColumnNamed(columnName);

        return singleModeColumn.showField() || listModeColumn.showField();
    }


    
    /**
     * Returns <code>true</code> if either create or modify notifications are enabled.
     * 
     * @return <code>true</code> if either create or modify notifications are enabled
     */
    public boolean areNotificationsEnabled() 
    {
        return shouldNotifyOnCreateRecords() || shouldNotifyOnModifyRecords();
    }

    
    
    /**
     * Posts a notification, which results in emails being sent to the groups configured to receive them.
     * 
     * @param isCreate <code>true</code> if this is a create notification, <code>false</code> if this is a modify notification
     * @param dataAccessParameters parameters to use in message creation
     * @param row the added or changed row the row to use in message creation
     * @param currentUser the user that did the add or change
     */
    public void postNotification(boolean isCreate, DataAccessParameters dataAccessParameters, VirtualRow row, User currentUser)
    {
        /** require [valid_dataAccessParameters_param] dataAccessParameters != null; [valid_row_param] row != null; **/

        // First make the message
        String template = isCreate ? templateForCreateNotification() : templateForModifyNotification();
        VirtualRow previousRow = (VirtualRow)dataAccessParameters.currentRow();  // Need to reset this
        dataAccessParameters.setCurrentRow(row);
        String messageText = DANotificationMessage.messageFor(dataAccessParameters, template);
        dataAccessParameters.setCurrentRow(previousRow);
        
        // Then figure out who it should be "From"
        String fromAddress = notificationFromAddress();
        if (StringAdditions.isEmpty(fromAddress))
        {
            if (currentUser.equals(PublicUser.publicUser(editingContext())))
            {
                fromAddress = sectionUsedIn().website().owner().emailAddress();
            }
            else
            {
                fromAddress = currentUser.emailAddress();
            }
        }

        // Finally, create and queue up messages for sending
        Enumeration groupEnumerator = allNotificationRecipients(row).objectEnumerator();
        ThreadedMailAgent mailAgent = ThreadedMailAgent.getInstance();
        while (groupEnumerator.hasMoreElements())
        {
            String emailAddress = (String)groupEnumerator.nextElement();
            Message message = mailAgent.newMessage();
            try
            {
                message.setToAddress(emailAddress);
                message.setFromAddress(fromAddress);
                message.setSubject(isCreate ? createRecordsNotificationSubject() : 
                                              modifyRecordsNotificationSubject());
                message.setText(messageText);

                mailAgent.sendMessageDeferred(message);
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }
    }



    /**
     * Deletes the custom notify on create template if one exists.
     */
    public void deleteCustomTemplateForCreate()
    {
        if (customTemplateForCreate() != null)
        {
            setUseCustomTemplateForCreate(false);
            setCustomTemplateForCreate(null);
        }

        /** ensure [use_default_template] ! useCustomTemplateForCreate();  [no_custom_template] customTemplateForCreate() == null; **/
    }



    /**
     * Deletes the custom notify on modify template if one exists.
     */
    public void deleteCustomTemplateForModify()
    {
        if (customTemplateForModify() != null)
        {
            setUseCustomTemplateForModify(false);
            setCustomTemplateForModify(null);
        }

        /** ensure [use_default_template] ! useCustomTemplateForModify();  [no_custom_template] customTemplateForModify() == null; **/
    }



    /**
     * Returns the default (standard) creation DA notification template.
     *
     * @return the default create template
     */
    public String defaultTemplateForCreate()
    {
        return SMApplication.appProperties().stringPropertyForKey("DANotificationMessageDefaultCreationTemplate");
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the default (standard) modification DA notification template.
     *
     * @return the default modify template
     */
    public String defaultTemplateForModify()
    {
        return SMApplication.appProperties().stringPropertyForKey("DANotificationMessageDefaultModificationTemplate");
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the default for a custom DA notification template.  The corresponding bindings are in
     * DANotificationMessage.
     *
     * @return the default for a custom DA notification template
     */
    public String defaultCustomTemplateForNotifications()
    {
        StringBuffer template = new StringBuffer(1024);
        template.append("The following record has been created/modified:\n");
        template.append("<WebObject name=\"DataAccess_ShowDetails\"></WebObject>\n\n");
        Enumeration columnEnum = databaseTable().columnsOrderedBy(new SMVirtualTable.DefaultUIComparator()).objectEnumerator();
        while (columnEnum.hasMoreElements())
        {
            Column aColumn = (Column) columnEnum.nextElement();
            template.append("<WebObject name=\"");
            template.append(aColumn.normalizedName());
            template.append("\"></WebObject>\n");
        }

        return template.toString();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns String representation of the bindings that will be used in the generation of the 
     * notification message.
     * 
     * @return String representation of the bindings for the notification message
     */
    public String notificationBindings()
    {
        String standardBindings = SMApplication.appProperties().stringPropertyForKey("DANotificationMessage.wod");

        StringBuffer wodFile = new StringBuffer(standardBindings);
        
        // For custom templates
        Enumeration columnEnum = databaseTable().columns().objectEnumerator();
        while (columnEnum.hasMoreElements())
        {
            Column aColumn = (Column) columnEnum.nextElement();

            wodFile.append(aColumn.normalizedName());
            wodFile.append(": WOString {\n");
            wodFile.append("    value = ");
            wodFile.append(aColumn.normalizedName());
            wodFile.append(";\n");
            wodFile.append("}\n");
            wodFile.append("\n");
        }
        return wodFile.toString();
        /** ensure [valid_result] Result != null;          **/
    }



    /**
     * Returns the text content for the custom modify template as an NSData object.  The defaultTemplateForModify() is returned if one has not been created.
     *
     * @return the text content for the custom modify template
     */
    public String templateForModifyNotification()
    {
        return ((useCustomTemplateForModify()) && (customTemplateForModify() != null)) ? customTemplateForModify() : defaultTemplateForModify();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the text content for the custom create template as an NSData object.  The 
     * defaultTemplateForCreate() is returned if one has not been created.
     *
     * @return the text content for the custom create template
     */
    public String templateForCreateNotification()
    {
        return ((useCustomTemplateForCreate()) && (customTemplateForCreate() != null)) ? customTemplateForCreate() : defaultTemplateForCreate();
        /** ensure [valid_result] Result != null;  **/
    }


    
    /**
     * Returns the HTML content for the Custom DA Creation Notification creation template as an 
     * NSData object.  The defaultCustomTemplateForNotifications() is returned if one has not been 
     * created.  This does not affect the status of useCustomTemplateForCreate().
     *
     * @return the HTML content for the Custom DA Creation Notification template
     */
    public NSData customCreationNotificationTemplateHtml()
    {
        String templateString = (customTemplateForCreate() != null) ? customTemplateForCreate() : defaultCustomTemplateForNotifications();
        return new NSData(templateString.getBytes());
        /** ensure [valid_result] Result != null;  **/
    }


    
    /**
     * Returns the HTML content for the Custom DA Modification Notification template as an NSData 
     * object.  The defaultCustomTemplateForNotifications() is returned if one has not been created.  
     * This does not affect the status of useCustomTemplateForModify().
     *
     * @return the HTML content for the Custom DA Modification Notification template
     */
    public NSData customModificationNotificationTemplateHtml()
    {
        String templateString = (customTemplateForModify() != null) ? customTemplateForModify() : defaultCustomTemplateForNotifications();
        return new NSData(templateString.getBytes());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Amalgamates the notificationGroups' recipients and the otherNotificationRecipients into one single array.  All entries will be well-formed email addresses, though (of course) they may not be valid.
     *
     * @return all notification recipients' email addresses
     */
    public NSArray allNotificationRecipients(VirtualRow row)
    {
        NSMutableSet allRecipients = new NSMutableSet();

        // Start with the groups that were selected by the user
        Enumeration notificationGroupEnumerator = notificationGroups().objectEnumerator();
        while (notificationGroupEnumerator.hasMoreElements())
        {
            WebsiteGroup group = (WebsiteGroup)notificationGroupEnumerator.nextElement();
            allRecipients.addObjectsFromArray((NSArray)group.users().valueForKey("emailAddress"));
        }

        // Then add the manually typed in email addresses and internal user ids
        allRecipients.addObjectsFromArray(arrayOfOtherNotificationRecipients());

        // Finally, add email addresses from the selected columns of the row we are modifying
        Enumeration columnEnumerator = notificationColumns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column column = (Column)columnEnumerator.nextElement();

            if (row.hasFieldNamed(column.name()))
            {
                String value = null;

                // We know that we only have text, text lookup or user fields.  Could obviously assign this polymorphically...
                if (column.type().name().equals(VirtualUserField.ColumnTypeName))
                {
                    if ( ! row.valueForFieldNamed(column.name()).equals(PublicUser.publicUser(editingContext())))
                    {
                        value = ((User)row.valueForFieldNamed(column.name())).emailAddress();
                    }
                }
                else if (column.type().name().equals(ColumnType.StringColumnType))
                {
                    value = (String)row.valueForFieldNamed(column.name());
                }
                else
                {
                    value = ((VirtualStringField)row.valueForFieldNamed(column.name())).stringValue();
                }

                if ((value != null) && (EmailAddress.isValidAddressFormat(value)))
                {
                    allRecipients.addObject(value);
                }
                else
                {
                    DebugOut.println(20, "Invalid row email address value not marshalled: " + value);
                }
            }
        }

        DebugOut.println(10, "Marshalled users for notification: " + allRecipients.toString());
        return allRecipients.allObjects();

        /** ensure
        [valid_result] Result != null;
        [valid_email_addresses] forall i : {0 .. Result.count() - 1} #
                                    EmailAddress.isValidAddressFormat((String)Result.objectAtIndex(i)); **/
    }



    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (toChildren().count() < 5)
        {
            exceptions.addObject(new NSValidation.ValidationException("DataAccess is missing children"));
        }


        if (defaultMode().equals(DataAccessMode.ADD_MODE) && ! canAddRecords())
        {
            exceptions.addObject(new NSValidation.ValidationException("Can't have default mode of Add without permission to add."));
        }

        if (defaultMode().equals(DataAccessMode.SEARCH_MODE) && ! canBrowseOrSearchRecords())
        {
            exceptions.addObject(new NSValidation.ValidationException("Can't have default mode of Search without permission to search."));
        }
        

        // TODO Restore this when AddSection no longer saves in the middle of things!
        /*
        if (databaseTable() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("DataAccess is missing databaseTable"));
        }
         */

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\

    /**
     * Returns <code>true</code> if users can browse through the records in this DataAccess Section.
     *
     * @return <code>true</code> if users can browse through the records in this DataAccess Section
     */
    public boolean canBrowseOrSearchRecords()
    {
        return booleanValueForBindingKey(CAN_BROWSE_OR_SEARCH_BINDINGKEY);
    }

    /**
     * Sets whether users can browse through the records in this DataAccess Section or not.
     *
     * @param booleanValue <code>true</code> if users can browse through the records, <code>false</code> otherwise
     */
    public void setCanBrowseOrSearchRecords(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, CAN_BROWSE_OR_SEARCH_BINDINGKEY);
    }



    /**
     * Returns <code>true</code> if users can add new records in this DataAccess Section.
     *
     * @return <code>true</code> if users can add new records in this DataAccess Section
     */
    public boolean canAddRecords()
    {
        return booleanValueForBindingKey(CAN_ADD_RECORDS_BINDINGKEY);
    }

    /**
     * Sets whether users can add new records in this DataAccess Section or not.
     *
     * @param booleanValue <code>true</code> if users can add new records, <code>false</code> otherwise
     */
    public void setCanAddRecords(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, CAN_ADD_RECORDS_BINDINGKEY);
    }



    /**
     * Returns <code>true</code> if users can delete records from this DataAccess Section.
     *
     * @return <code>true</code> if users can delete records from this DataAccess Section
     */
    public boolean canDeleteRecords()
    {
        return booleanValueForBindingKey(CAN_DELETE_RECORDS_BINDINGKEY);
    }

    /**
     * Sets whether users can delete records from this DataAccess Section or not.
     *
     * @param booleanValue <code>true</code> if users can delete records, <code>false</code> otherwise
     */
    public void setCanDeleteRecords(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, CAN_DELETE_RECORDS_BINDINGKEY);
    }



    /**
     * Returns <code>true</code> if users can import records to this DataAccess Section.
     *
     * @return <code>true</code> if users can import records to this DataAccess Section
     */
    public boolean canImportRecords()
    {
        return booleanValueForBindingKey(CAN_IMPORT_RECORDS_BINDINGKEY);
    }

    /**
     * Sets whether users can import records to this DataAccess Section or not.
     *
     * @param booleanValue <code>true</code> if users can import records, <code>false</code> otherwise
     */
    public void setCanImportRecords(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, CAN_IMPORT_RECORDS_BINDINGKEY);
    }


    /**
     * Returns <code>true</code> if the results of the Default Search are to be used if no other search is performed.  Returns <code>false</code> if users can view all records in the table if no other search is performed.
     *
     * @return Returns <code>true</code> if the results of the Default Search are to be used if no other search is performed.  <code>false</code> if users can view all records in the table if no other search is performed.  
     */
    public boolean useDefaultSearch()
    {
        return booleanValueForBindingKey(USE_DEFAULT_SEARCH_BINDINGKEY);
    }

    /**
     * Sets whether users can view all records in the table or the results of the Default Search if no other search is performed. 
     *
     * @param booleanValue <code>true</code> if the Default Search should be used, <code>false</code> if all records should be shown
     */
    public void setUseDefaultSearch(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, USE_DEFAULT_SEARCH_BINDINGKEY);
    }



    /**
     * Returns <code>true</code> if searches can only return results from within the results of the Default Search.
     *
     * @return <code>true</code> if searches can only return results from within the results of the Default Search
     */
    public boolean restrictToDefaultResults()
    {
        return booleanValueForBindingKey(RESTRICT_TO_DEFAULT_RESULTS_BINDINGKEY);
    }

    /**
     * Sets whether searches can only return results from within the results of the Default Search.
     *
     * @param booleanValue <code>true</code> if searches can only return results from within the results of the Default Search, <code>false</code> if all records can be searched
     */
    public void setRestrictToDefaultResults(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, RESTRICT_TO_DEFAULT_RESULTS_BINDINGKEY);
    }



    /**
     * Returns the String name of the default DataAccessMode to be used for this Section if no mode is specified.
     *
     * @return the String name of the default DataAccessMode to be used for this Section if no mode is specified
     */
    public String defaultMode()
    {
        return (String)valueForBindingKey(DEFAULT_MODE_BINDINGKEY);
        /** ensure [result_valid] DataAccessMode.isValidMode(Result); **/
    }

    /**
     * Sets the String name of the default DataAccessMode to be used for this Section if no mode is specified.
     *
     * @param value the String name of the default DataAccessMode to be used for this Section if no mode is specified
     */
    public void setDefaultMode(String mode)
    {
        /** require [mode_valid] DataAccessMode.isValidMode(mode); **/
        setBindingForKey(mode, DEFAULT_MODE_BINDINGKEY);
    }



    /**
     * Returns the number of rows that should occur in a group.
     *
     * @return the number of rows in a group
     */
    public String groupSize()
    {
        return (String)valueForBindingKey(GROUP_SIZE_BINDINGKEY);
    }

    /**
     * Returns the number of rows that should occur in a group as an int.
     *
     * @return the number of rows in a group
     */
    public int groupSizeInt()
    {
        /** require groupSize() != null; **/
        return new Integer(groupSize()).intValue();
    }

    /**
     * Sets the number of rows that should occur in a group.
     *
     * @param groupSize the number of rows that should occur in a group
     */
    public void setGroupSize(String groupSize)
    {
        setBindingForKey(groupSize, GROUP_SIZE_BINDINGKEY);
    }


    // These bindings relate to email notifications that are sent out when a user creates or modifies a record

    /**
     * Returns <code>true</code> if a notification should be sent when records are created.
     *
     * @return <code>true</code> if a notification should be sent when records are created
     */
    public boolean shouldNotifyOnCreateRecords()
    {
        return booleanValueForBindingKey(NOTIFY_ON_CREATE_BINDINGKEY);
    }

    /**
     * Sets whether notifications are sent when records are created.
     *
     * @param booleanValue <code>true</code> if notifications are sent when records are created, <code>false</code> otherwise
     */
    public void setShouldNotifyOnCreateRecords(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, NOTIFY_ON_CREATE_BINDINGKEY);
    }


    /**
     * Returns <code>true</code> if a notification should be sent when records are modified.
     *
     * @return <code>true</code> if a notification should be sent when records are modified
     */
    public boolean shouldNotifyOnModifyRecords()
    {
        return booleanValueForBindingKey(NOTIFY_ON_MODIFY_BINDINGKEY);
    }

    /**
     * Sets whether notifications are sent when records are modified.
     *
     * @param booleanValue <code>true</code> if notifications are sent when records are modified, <code>false</code> otherwise
     */
    public void setShouldNotifyOnModifyRecords(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, NOTIFY_ON_MODIFY_BINDINGKEY);
    }


    /**
     * Returns the subject for notification message sent when records are created.
     *
     * @return the subject for notification message sent when records are created
     */
    public String createRecordsNotificationSubject()
    {
        return (String)valueForBindingKey(CREATE_RECORDS_NOTIFICATION_SUBJECT_BINDINGKEY);
    }

    /**
     * Sets the subject for notification message sent when records are created.
     *
     * @param stringValue the subject for notification message sent when records are created
     */
    public void setCreateRecordsNotificationSubject(String stringValue)
    {
        setBindingForKey(stringValue, CREATE_RECORDS_NOTIFICATION_SUBJECT_BINDINGKEY);
    }


    /**
     * Returns the subject for notification message sent when records are modified.
     *
     * @return the subject for notification message sent when records are modified
     */
    public String modifyRecordsNotificationSubject()
    {
        return (String)valueForBindingKey(MODIFY_RECORDS_NOTIFICATION_SUBJECT_BINDINGKEY);
    }

    /**
     * Sets the subject for notification message sent when records are modified.
     *
     * @param stringValue the subject for notification message sent when records are modified
     */
    public void setModifyRecordsNotificationSubject(String stringValue)
    {
        setBindingForKey(stringValue, MODIFY_RECORDS_NOTIFICATION_SUBJECT_BINDINGKEY);
    }


    /**
     * Returns <code>true</code> if we use a custom templatee when sending notifications about created records.
     *
     * @return <code>true</code> if we use a custom templatee when sending notifications about created records
     */
    public boolean useCustomTemplateForCreate()
    {
        return booleanValueForBindingKey(USE_CUSTOM_TEMPLATE_ON_CREATE_BINDINGKEY);
    }

    /**
     * Sets whether we use a custom template when sending notifications about created records.  If
     * using a custom template is selected and none exists, a default is created.
     *
     * @param booleanValue <code>true</code> if we use a custom template, <code>false</code> otherwise
     */
    public void setUseCustomTemplateForCreate(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, USE_CUSTOM_TEMPLATE_ON_CREATE_BINDINGKEY);
        if (useCustomTemplateForCreate() && (customTemplateForCreate() == null))
        {
            setCustomTemplateForCreate(defaultCustomTemplateForNotifications());
        }
    }


    /**
     * Returns <code>true</code> if we use a custom templatee when sending notifications about modified records.
     *
     * @return <code>true</code> if we use a custom templatee when sending notifications about modified records
     */
    public boolean useCustomTemplateForModify()
    {
        return booleanValueForBindingKey(USE_CUSTOM_TEMPLATE_ON_MODIFY_BINDINGKEY);
    }

    /**
     * Sets whether we use a custom template when sending notifications about modified records. If
     * using a custom template is selected and none exists, a default is created.
     *
     * @param booleanValue <code>true</code> if we use a custom template, <code>false</code> otherwise
     */
    public void setUseCustomTemplateForModify(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, USE_CUSTOM_TEMPLATE_ON_MODIFY_BINDINGKEY);
        if (useCustomTemplateForModify() && (customTemplateForModify() == null))
        {
            setCustomTemplateForModify(defaultCustomTemplateForNotifications());
        }

    }


    /**
     * Returns the custom notify on create template.
     *
     * @return the custom notify on create template
     */
    public String customTemplateForCreate()
    {
        return (String)valueForBindingKey(CUSTOM_TEMPLATE_ON_CREATE_BINDINGKEY);
    }

    /**
     * Sets the custom notify on create template.
     *
     * @param template the custom notify on create template
     */
    public void setCustomTemplateForCreate(String template)
    {
        DANotificationMessage.validateTemplate(this, template);
        setBindingForKey(template, CUSTOM_TEMPLATE_ON_CREATE_BINDINGKEY);
    }


    /**
     * Returns the custom notify on modify template.
     *
     * @return the custom notify on modify template
     */
    public String customTemplateForModify()
    {
        return (String)valueForBindingKey(CUSTOM_TEMPLATE_ON_MODIFY_BINDINGKEY);
    }

    /**
     * Sets the custom notify on modify template.
     *
     * @param template the custom notify on modify template
     */
    public void setCustomTemplateForModify(String template) throws NSValidation.ValidationException
    {
        DANotificationMessage.validateTemplate(this, template);
        setBindingForKey(template, CUSTOM_TEMPLATE_ON_MODIFY_BINDINGKEY);
    }
    
    /**
     * Returns the from email address to be used for sending notification emails.
     *
     * @return the from email address to be used for sending notification emails
     */
    public String notificationFromAddress()
    {
        return (String)valueForBindingKey(NOTIFICATION_FROM_ADDRESS);
    }

    /**
     * Sets the from address for notification emails
     *
     * @param address the address to be used
     */
    public void setNotificationFromAddress(String address)
    {
        setBindingForKey(address, NOTIFICATION_FROM_ADDRESS);
    }    

    
    /**
     * Returns the list of other email addresses to send this notification to.
     *
     * @return the list of other email addresses to send this notification to
     */
    public String otherNotificationRecipients()
    {
        return (String)valueForBindingKey(OTHER_NOTIFICATION_RECIPIENTS_BINDINGKEY);
    }

    /**
     * Sets the list of other email addresses to send this notification to.
     *
     * @param otherRecipients the other recipients
     */
    public void setOtherNotificationRecipients(String otherRecipients)
    {
        setBindingForKey(otherRecipients, OTHER_NOTIFICATION_RECIPIENTS_BINDINGKEY);
    }


    /**
     * Returns the list of other email addresses to send this notification to as an array.
     *
     * @return the list of other email addresses to send this notification to as an array
     */
    public NSArray arrayOfOtherNotificationRecipients()
    {
        if (otherNotificationRecipients() != null)
        {
            return new UserIDTokenizer(otherNotificationRecipients()).emailAddressesOfValidUserIDs();
        }
        else
        {
            return new NSArray();
        }
        /** ensure
        [valid_result] Result != null;
        [valid_email_addresses] forall i : {0 .. Result.count() - 1} # 
                                    EmailAddress.isValidAddressFormat((String)Result.objectAtIndex(i)); **/
    }


}
