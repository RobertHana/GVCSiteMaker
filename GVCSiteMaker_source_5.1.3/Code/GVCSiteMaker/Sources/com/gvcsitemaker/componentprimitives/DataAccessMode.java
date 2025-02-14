package com.gvcsitemaker.componentprimitives;

import java.util.Enumeration;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.databasetables.SMVirtualTable;
import com.gvcsitemaker.core.interfaces.ValidationMessageStore;
import com.gvcsitemaker.core.pagecomponent.PageComponent;
import com.gvcsitemaker.core.support.DataAccessParameters;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;
import com.gvcsitemaker.core.utility.DebugOut;
import com.gvcsitemaker.core.utility.HtmlTemplateUtils;
import com.gvcsitemaker.core.utility.SMEOUtils;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORedirect;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;
import com.webobjects.foundation.NSValidation;

import net.global_village.eofvalidation.EOFValidation;
import net.global_village.foundation.Date;
import net.global_village.foundation.ExceptionConverter;
import net.global_village.virtualtables.Table;
import net.global_village.virtualtables.VirtualColumn;
import net.global_village.virtualtables.VirtualField;
import net.global_village.virtualtables.VirtualRow;
import net.global_village.virtualtables.VirtualTable;


/**
 * DataAccessMode provides support methods for the sub-classes which implement specific modes.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class DataAccessMode extends ComponentPrimitive implements ValidationMessageStore
{
    // These are for repetitions over the fields
    protected NSArray visibleFields = null;
    protected NSArray editableFields = null;
    public com.gvcsitemaker.core.pagecomponent.DataAccessColumn aDataAccessColumn;

    protected WOElement cachedTemplate = null;
    protected User currentUser = null;
    protected NSMutableDictionary validationFailures = new NSMutableDictionary();
    protected boolean isConfirmingDeletion = false;
    protected boolean shouldNotSendNotification = false;

    protected NSMutableDictionary cachedLookupValues = new NSMutableDictionary();

    protected int index;
    
    /**
     * Designated constructor.
     */
    public DataAccessMode(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to clear validation failures.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        clearValidationFailures();
        dataAccessParameters().clearCachedValues();
        /** ensure [no_validation_failures] ! hasValidationFailures(); **/
    }



    /**
     * Records the validation failure for later retrieval in validationFailureForKey(String aKey).  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param validationFailure - message to display in the UI for this validation failure.
     * @param key - a unique key to associate this failure with
     */
    public void registerValidationFailureForKey(String validationFailure, String key)
    {
        /** require  [valid_param] validationFailure != null;  [valid_key] key != null;   **/
        validationFailures.setObjectForKey(validationFailure, key);
        /** ensure [message_recorded] validationFailures.objectForKey(key) != null;  **/
    }


        
    /**
     * Returns the registered validation error for aKey or null if no validation error was registered.  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param key - a unique key associated with this failure
     * @return the appropriate registered validation error or null if no validation error was registered.
     */
    public String validationFailureForKey(String aKey)
    {
        /** require  [valid_param] aKey != null;   **/

        return (String) validationFailures.objectForKey(aKey);
    }


    
    /**
     * Returns true if any validation errors have been registered.
     *
     * @return true if any validation errors have been registered.
     */
    public boolean hasValidationFailures()
    {
        return validationFailures.count() > 0;
    }



    /**
     * Removes any validation errors that have been registered.
     */
    public void clearValidationFailures()
    {
        validationFailures = new NSMutableDictionary();
        /** ensure [did_clear] ! hasValidationFailures();  **/
    }



    /**
     * Action method to save the changed data.  Redirects to re-display the current record in the next mode if the save succeeds.  Otherwise, redisplays this page with validation messages.
     */
    public WOComponent saveChanges()
    {
        WOComponent nextPage = context().page();

        if ( ! hasValidationFailures())
        {
            try
            {
                DebugOut.println(30, "Starting to save");
                updateRowStamps();  // Date and user modifying

                // Save the rows that were changed before saving
                NSArray changedNotificationRows = null;
                if (( ! shouldNotSendNotification()) && (((DataAccess)parent()).dataAccessComponent().shouldNotifyOnModifyRecords()))
                {
                    changedNotificationRows = updatedRowsForNotification();
                }

                DebugOut.println(30, "Calling saveChanges()");
                editingContext().saveChanges();
                DebugOut.println(30, "saveChanges() Done");

                if (( ! shouldNotSendNotification()) && (((DataAccess)parent()).dataAccessComponent().shouldNotifyOnModifyRecords()))
                {
                    DebugOut.println(30, "Posting changed notification");
                    postNotification(changedNotificationRows, false);
                    DebugOut.println(30, "Changed notification posted");
                }

                nextPage = redirectToUrl(SMDataAccessActionURLFactory.nextModeAndCurrentRecordUrl(dataAccessParameters()));
            }
            catch (NSValidation.ValidationException e)
            {
                if (e.getMessage().endsWith(EOFValidation.OptimisticLockingFailure))
                {
                    VirtualField field = (VirtualField)e.object();
                    VirtualColumn column = field.column();
                    registerValidationFailureForKey("Another user has changed this field, their data is shown.  Check your changes and re-submit. ",
                            column.normalizedName() + "." + EOFValidation.OptimisticLockingFailure);
                    SMEOUtils.invalidateObject(field);
                }
                else
                {
                    // Uh Oh.  That was NOT supposed to happen.  
                    DebugOut.println(1, "Save failure message: " + e.getMessage());
                    DebugOut.println(1, "Save failed on object: " + e.object());

                    //Better clean up a bit
                    editingContext().revert();
                    editingContext().undoManager().removeAllActions();
                    throw new ExceptionConverter(e);
                }
            }
        }

        return nextPage;
    }



    /**
     * Returns the virtual rows that the user has changed.  Does <em>not</em> return deleted rows.
     * 
     * @return the virtual rows that the user has changed
     */
    protected NSArray updatedRows()
    {
        // insertedObjects() detects newly added fields (e.g. formerly null), deletedObjects() detects newly deleted fields (e.g. with a null value), and updatedObjects() detects editied fields.
        NSMutableArray updatedObjects = new NSMutableArray(editingContext().updatedObjects());
        updatedObjects.addObjectsFromArray(editingContext().insertedObjects());
        updatedObjects.addObjectsFromArray(editingContext().deletedObjects());

        NSMutableSet updatedRows = new NSMutableSet();
        Enumeration updatedObjectEnumerator = updatedObjects.objectEnumerator();
        while (updatedObjectEnumerator.hasMoreElements())
        {
            Object thisEO = updatedObjectEnumerator.nextElement();

            // VirtualRows will be present in the case of additions and deletions.  VirtualFields will be present in the case of additions or edits.
            if (thisEO instanceof VirtualRow)
            {
                updatedRows.addObject(thisEO);
            }
            else if (thisEO instanceof VirtualField)
            {
                VirtualRow updatedRow = ((VirtualField)thisEO).row();
                // This can be null if processRecentChanges() has been invoked on the editing context.
                if (updatedRow != null)
                {
                    updatedRows.addObject(updatedRow);
                }
            }
        }

        // Don't include deleted rows!
        updatedRows.subtractSet(new NSSet(editingContext().deletedObjects()));

        return updatedRows.allObjects();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This method updates the Date Modified and Modified By fields on updated rows based on the current state of editingContext().  This needs to be done here, rather than in setFieldValue(), as setFieldValue() is not called by Custom Templates.
     */
    protected void updateRowStamps()
    {
        NSArray updatedRows = updatedRows();
        updatedRows.takeValueForKey(currentUser(), "Modified By");
        updatedRows.takeValueForKey(Date.now(), "Date Modified");
    }



    /**
     * Returns the rows <em>of this table</em> that have been modified since the last save.  In the case where there is a lookup column, assigning a value changes both the referring and the referred to values, so we need to manually remove those rows that don't come from our table.
     */
    protected NSArray updatedRowsForNotification()
    {
        NSMutableSet updatedRowsForNotification = new NSMutableSet(updatedRows());
        updatedRowsForNotification.intersectSet(new NSSet(((VirtualTable)databaseTable()).rows()));

        return updatedRowsForNotification.allObjects();
    }



    /**
     * Posts an add or modify notification, which results in emails being sent to the groups configured to receive them.
     * 
     * @param rows the rows to send a notification for
     * @param isCreate <code>true</code> if this is a create notification, <code>false</code> if it is a modify notification
     */
    protected void postNotification(NSArray rows, boolean isCreate)
    {
        Enumeration rowEnumerator = rows.objectEnumerator();
        while (rowEnumerator.hasMoreElements())
        {
            VirtualRow row = (VirtualRow)rowEnumerator.nextElement();
            ((com.gvcsitemaker.core.pagecomponent.DataAccess)modeComponent().toParent()).postNotification(isCreate, dataAccessParameters(), row, currentUser());
        }
    }



    /**
     * Returns the WOComponent template to use for this mode built from generatedBindings() and the DataAccessMode PageComponent's template.
     *
     * @return the WOComponent template to use for this mode
     */
    public WOElement template()
    {
        // Cache for optimization
        if (cachedTemplate == null)
        {
            cachedTemplate = HtmlTemplateUtils.elementsFromTemplateWithBindings(modeComponent().template() ,
                                                                                modeComponent().generatedBindings(),
                                                                                context().session().languages());
        }

        return cachedTemplate;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessMode.
     *
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessMode.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessMode modeComponent()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessMode) componentObject();
        /** ensure [result_valid] Result != null; **/
    }    



    /**
     * Returns the ordered DataAccessComponents of modeComponent() which can be edited.
     *
     * @return the ordered DataAccessComponents of modeComponent() which can be edited.
     */
    public NSArray editableFields()
    {
        // Cache this as an optimization
        if (editableFields == null)
        {
            editableFields = modeComponent().editableFields();
        }

        return editableFields;

        /** ensure [valid_result] Result != null; **/
    }

    

    /**
      * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if there are any records and any of the children (DataAccessColumn) are editable.
     *
     * @return <code>true</code> if there are any records and any of the children (DataAccessColumn) are editable.
     */
    public boolean hasEditableFields()
    {
        return dataAccessParameters().hasRows() && modeComponent().hasEditableFields();
    }



    /**
     * Returns the field named aDataAccessColumn.normalizedFieldName from currentRow.  Used to access the values in a WORepetition.
     *
     * @return the field named aDataAccessColumn.normalizedFieldName from currentRow
     */
    public VirtualField field()
    {
        /** require [has_aDataAccessColumn] aDataAccessColumn != null;   **/
        return ((VirtualRow)currentRow()).fieldNamed(aDataAccessColumn.normalizedFieldName());
    }



    /**
     * Overridden to handle special bindings:
     * <ul>
     * <li>fieldNamed.&lt;field name&gt; - needed for custom templates</li>
     * </ul>
     */
    public Object valueForKeyPath(String key) 
    {
		if (key.startsWith("fieldNamed"))
		{
			return ((VirtualRow)currentRow()).fieldNamed(key.substring(11));
		}
		else return super.valueForKeyPath(key);
	}



	/**
     * Returns the value of the attribute (column) named aDataAccessColumn.normalizedFieldName from currentRow.  Used to access the values in a WORepetition.
     *
     * @return the value of the attribute (column) named aDataAccessColumn.normalizedFieldName from currentRow.
     */
    public Object fieldValue()
    {
        /** require [has_aDataAccessColumn] aDataAccessColumn != null; [has_current_row] currentRow() != null;  **/
        return currentRow().valueForKey(aDataAccessColumn.normalizedFieldName());
    }



    /**
     * Sets the value of the attribute (column) named aDataAccessColumn.normalizedFieldName in newRow.  Used to set the values from a WORepetition.  Also handles attempts to delete fields which are the source of data for lookup fields in other tables.
     *
     * @return the value of the attribute (column) named aDataAccessColumn.normalizedFieldName in newRow.
     */
    public void setFieldValue(Object value)
    {
        /** require [has_aDataAccessColumn] aDataAccessColumn != null ; **/

        try
        {
            /* Bug workaround.
            If this is not checked prior to taking the null value, then the delete proceeds and is validated and the validate fails.  However, there is a bug in EOEditingContext when rolling back the deletion.  If this occurs in a List of Records, and more than one row fails, then WO either deadlocks, or raises an exception like this:
            [2003-01-25 12:17:12 PST] <WorkerThread13> com.webobjects.foundation.NSValidation$ValidationException: Removal of 'VirtualNumberField' object denied: in its 'referringFields' relationship, there are related objects
                    at com.webobjects.eoaccess.EOEntity.validateObjectForDelete(EOEntity.java:3892)
                    at com.webobjects.eoaccess.EOEntityClassDescription.validateObjectForDelete(EOEntityClassDescription.java:414)
                    at com.webobjects.eocontrol.EOCustomObject.validateForDelete(EOCustomObject.java:1004)
                    at com.webobjects.eocontrol.EOEditingContext.validateTable(EOEditingContext.java:1698)
                    at com.webobjects.eocontrol.EOEditingContext.validateDeletesUsingTable(EOEditingContext.java:2347)
                    at com.webobjects.eocontrol.EOEditingContext._processDeletedObjects(EOEditingContext.java:1638)
                    at com.webobjects.eocontrol.EOEditingContext._processRecentChanges(EOEditingContext.java:1207)
                    at com.webobjects.eocontrol.EOEditingContext.processRecentChanges(EOEditingContext.java:1402)
                    at net.global_village.eofvalidation.NotifyingEditingContext.processRecentChanges(NotifyingEditingContext.java:151)
                    at com.webobjects.eocontrol.EOEditingContext._processEndOfEventNotification(EOEditingContext.java:1657)
                    at com.webobjects.eocontrol.EOEditingContext._undoManagerCheckpoint(EOEditingContext.java:1407)
                    at java.lang.reflect.Method.invoke(Native Method)
                    at com.webobjects.foundation.NSSelector._safeInvokeMethod(NSSelector.java:71)
                    at com.webobjects.foundation.NSNotificationCenter$_Entry.invokeMethod(NSNotificationCenter.java:580)
                    at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:522)
                    at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:537)
                    at com.webobjects.foundation.NSUndoManager._postCheckpointNotification(NSUndoManager.java:175)
                    at com.webobjects.foundation.NSUndoManager.endUndoGrouping(NSUndoManager.java:322)
                    at com.webobjects.foundation.NSUndoManager._processEndOfEventNotification(NSUndoManager.java:165)
                    at java.lang.reflect.Method.invoke(Native Method)
                    at com.webobjects.foundation.NSSelector.invoke(NSSelector.java:296)
                    at com.webobjects.foundation.NSSelector._safeInvokeSelector(NSSelector.java:59)
                    at com.webobjects.foundation.NSDelayedCallbackCenter$NSLightInvocation.invoke(NSDelayedCallbackCenter.java:219)
                    at com.webobjects.foundation.NSDelayedCallbackCenter._eventEnded(NSDelayedCallbackCenter.java:188)
                    at com.webobjects.foundation.NSDelayedCallbackCenter.eventEnded(NSDelayedCallbackCenter.java:151)
                    at com.webobjects.appserver.WOApplication.saveSessionForContext(WOApplication.java:1343)
                    at com.webobjects.appserver._private.WOComponentRequestHandler._dispatchWithPreparedApplication(WOComponentRequestHandler.java:321)

            To avoid this we check the validity of the deletion before attempting it.  This code can be deleted if this bug is ever fixed.
            */
            if (value == null)
            {
                VirtualRow row = (VirtualRow)currentRow();
                VirtualField field = row.fieldNamed(aDataAccessColumn.normalizedFieldName());
                field.validateForDelete();
            }

            currentRow().takeValueForKey(value, aDataAccessColumn.normalizedFieldName());
        }
        catch (NSValidation.ValidationException e)
        {
            // This checks for and handles validation failures that result from an attempt to delete the contents of a field which is the source of data for lookup fields in other tables.  Background: Setting a field to null deletes it, a Deny delete rule prevents the deletion from succeeding.  This is an explicit check for this particular failure.
            if (value == null)
            {
                VirtualRow row = (VirtualRow)currentRow();
                VirtualField field = row.fieldNamed(aDataAccessColumn.normalizedFieldName());

                try
                {
                    field.validateForDelete();

                    // It is something unexpected!
                    throw new ExceptionConverter(e);
                }
                catch (NSValidation.ValidationException deletionNotValid)
                {
                    registerValidationFailureForKey(SMVirtualTable.deletionRefusedMessage(field),
                                                    aDataAccessColumn.normalizedFieldName() + ".cantDelete");
                }
            }
            else
            {
                // It is something unexpected!
                throw new ExceptionConverter(e);
            }
        }
    }



    /**
     * Returns the logged in User in our editing context.
     *
     * @return the logged in User in our editing context.
     */
    public User currentUser()
    {
        if (currentUser == null)
        {
            currentUser = (User) EOUtilities.localInstanceOfObject(editingContext(), ((Session)session()).currentUser());
        }

        return currentUser;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the DataAccessParameters object which provides the parameters controlling the display of this section.  These parameters are obtained from the parent DataAccess component primitive.
     *
     * @return the DataAccessParameters object which provides the parameters controlling the display of this section.
     */
    public DataAccessParameters dataAccessParameters()
    {
        /** require [parent_is_dataaccess] parent() instanceof DataAccess;  **/

        return ((DataAccess)parent()).dataAccessParameters();

        /** ensure [valid_result] Result != null ; **/
    }



    /**
     * Returns the Table which is being displayed or edited in this component.
     *
     * @return the Table which is being displayed or edited in this component.
     */
    public Table databaseTable()
    {
        /** require [parent_is_dataaccess] parent() instanceof DataAccess;  **/

        return ((DataAccess)parent()).dataAccessComponent().databaseTable();

        /** ensure [valid_result] Result != null ; **/
    }



    /**
     * Returns the EOEditingContext object in which this Section is being edited.  This is obtained from the parent DataAccess component primitive.
     *
     * @return the DataAccessParameters object which provides the parameters controlling the display of this section.
     */
    public EOEditingContext editingContext()
    {
        /** require [parent_is_dataaccess] parent() instanceof DataAccess;  **/

        return ((DataAccess)parent()).editingContext();

        /** ensure [valid_result] Result != null ; **/
    }


    
    /**
     * Returns a WORedirect to the passed URL.
     *
     * @param aUrl - the URL to redirect to
     * @return a WORedirect to the passed URL.
     */
    public WOComponent redirectToUrl(String aUrl)
    {
        /** require [valid_param] aUrl != null ; **/

        WORedirect redirect = (WORedirect)pageWithName("WORedirect");
        redirect.setUrl(aUrl);

        return redirect;

        /** ensure [valid_result] Result != null ; **/
    }



    /**
     * Returns all available lookup values for aDataAccessColumn (which must be a DataAccessLookupColumn).  The results are cached here as an optimization for the List Mode.
     *
     * @return list of all available lookup values for aDataAccessColumn
     */
    public NSArray lookupValues(com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn aLookupColumn)
    {
        /** require  [has_column] aLookupColumn != null;  **/

        String lookupColumnName = aLookupColumn.normalizedFieldName();

        if (cachedLookupValues.objectForKey(lookupColumnName) == null)
        {
            com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn column = (com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn)
            EOUtilities.localInstanceOfObject(editingContext(), aLookupColumn);

            cachedLookupValues.setObjectForKey(column.allLookupValues(), lookupColumnName);
        }

        return (NSArray) cachedLookupValues.objectForKey(lookupColumnName);
        
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Overridden to use local editing context.
     */
    public void setComponentObject(PageComponent aPageComponent)
    {
        /** require [valid_param] aPageComponent != null;  **/

        super.setComponentObject((PageComponent) EOUtilities.localInstanceOfObject(editingContext(), aPageComponent));

        /** ensure  [componentObject_set] componentObject() != null;   **/
    }



    /**
     * Returns <code>true</code> if the "Notification not needed" checkbox should be displayed, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the "Notification not needed" checkbox should be displayed, <code>false</code> otherwise
     */
    public boolean showNotificationNotNeededCheckBox()
    {
        /** require [parent_is_dataaccess_component] modeComponent().toParent() instanceof com.gvcsitemaker.core.pagecomponent.DataAccess; **/
        throw new RuntimeException("Sub-class responsibility");
    }





    
    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if users can browse through the records in this DataAccess mode.
     *
     * @return <code>true</code> if users can browse through the records in this DataAccess mode.
     */
    public boolean canBrowseOrSearchRecords()
    {
        return modeComponent().canBrowseOrSearchRecords();
    }



    /**
     * Returns <code>true</code> if users cannot browse through the records in this DataAccess mode.
     *
     * @return <code>true</code> if users cannot browse through the records in this DataAccess mode.
     */
    public boolean cannotBrowseOrSearchRecords()
    {
        return ! canBrowseOrSearchRecords();
    }



    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if users can add new records in this DataAccess mode.
     *
     * @return <code>true</code> if users can add new records in this DataAccess mode.
     */
    public boolean canAddRecords()
    {
        return modeComponent().canAddRecords();
    }



    /**
     * Returns <code>true</code> if users cannot add new records in this DataAccess mode.
     *
     * @return <code>true</code> if users cannot add new records in this DataAccess mode.
     */
    public boolean cannotAddRecords()
    {
        return ! canAddRecords();
    }

    
    
    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if users 
     * can import records in this DataAccess mode.
     *
     * @return <code>true</code> if users can import records in this DataAccess mode.
     */
    public boolean canImportRecords()
    {
        return modeComponent().canImportRecords();
    }



    /**
     * Returns <code>true</code> if users cannot import records in this DataAccess mode.
     *
     * @return <code>true</code> if users cannot import records in this DataAccess mode.
     */
    public boolean cannotImportRecords()
    {
        return ! canImportRecords();
    }



    /**
     * Returns the URL to display the current record in the current mode without any user search restrictions.
     *
     * @return the URL to display the current record in the current mode without any user search restrictions.
     */
    public String showAllRecordsUrl()
    {
        return SMDataAccessActionURLFactory.currentModeAndRecordWithoutQualifierUrl(dataAccessParameters());
    }



    /**
     * Returns the URL to show the Search for Records mode.
     *
     * @return the URL to show the Search for Records mode.
     */
    public String searchForRecordsUrl()
    {
        return SMDataAccessActionURLFactory.searchForRecordsUrl(dataAccessParameters());
    }



   /**
     * Returns the URL to add a new record and then return to the current mode.
     *
     * @return the URL to add a new record and then return to the current mode.
     */
    public String addRecordUrl()
    {
        return SMDataAccessActionURLFactory.addRecordAndReturnToCurrentModeUrl(dataAccessParameters());
    }



    /**
     * Returns the URL to add a new record and then return to the current mode.
     *
     * @return the URL to add a new record and then return to the current mode.
     */
    public String importRecordsUrl()
    {
        return SMDataAccessActionURLFactory.importRecordsAndReturnToCurrentModeUrl(dataAccessParameters());
    }



   /**
     * Returns the URL to switch from List of Records to List Record mode.
     *
     * @return the URL to switch from List of Records to List Record mode.
     */
    public String listModeUrl()
    {
        return SMDataAccessActionURLFactory.currentRecordInListMode(dataAccessParameters());
    }
    
    

    /**
     * Returns the URL to switch from List of Records to Single Record mode.
     *
     * @return the URL to switch from List of Records to Single Record mode.
     */
    public String singleModeUrl()
    {
        return SMDataAccessActionURLFactory.currentRecordInSingleMode(dataAccessParameters());
    }

    //************* Generic Accessors and Mutators below here *****************

    public EOEnterpriseObject currentRow()
    {
        return dataAccessParameters().currentRow() == null ? null :
            EOUtilities.localInstanceOfObject(editingContext(), dataAccessParameters().currentRow());
    }
    public void setCurrentRow(EOEnterpriseObject newRow)
    {
        dataAccessParameters().setCurrentRow(newRow);
    }

    public String currentRowPK()
    {
        if (currentRow() == null)
        {
            return null;
        }
        else
        {
            return dataAccessParameters().currentRowPK();
        }
    }

    public com.gvcsitemaker.core.pagecomponent.DataAccessColumn aDataAccessColumn()
    {
        return aDataAccessColumn;
    }
    public void  setADataAccessColumn(com.gvcsitemaker.core.pagecomponent.DataAccessColumn newColumn)
    {
        aDataAccessColumn = newColumn;
    }

    public boolean shouldNotSendNotification()
    {
        return shouldNotSendNotification;
    }
    public void setShouldNotSendNotification(boolean shouldNotSend)
    {
        shouldNotSendNotification = shouldNotSend;
    }

    public int index()
    {
        return index;
    }
    public void setIndex(int newIndex)
    {
        index = newIndex;
    }


}
