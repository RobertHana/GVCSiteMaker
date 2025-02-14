// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import java.util.Enumeration;

import com.gvcsitemaker.core.databasetables.SMVirtualTable;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import net.global_village.eofextensions.EODatabaseContextAdditions;
import net.global_village.virtualtables.Column;
import net.global_village.virtualtables.VirtualField;
import net.global_village.virtualtables.VirtualLookupColumn;
import net.global_village.virtualtables.VirtualRow;


/**
 * DataAccessDisplayMode adds support methods for the Single Record and List of Records data display modes.
 */
public class DataAccessDisplayMode extends DataAccessMode
{

    protected NSMutableDictionary recordsToDelete;

    
    /**
     * Designated constructor.
     */
    public DataAccessDisplayMode(WOContext context)
    {
        super(context);
        recordsToDelete = new NSMutableDictionary();
    }



    /**
     * Returns the ordered DataAccessComponents of modeComponent() which are to be shown.
     *
     * @return the ordered DataAccessComponents of modeComponent() which are to be shown.
     */
    public NSArray visibleFields()
    {
        // Cache this as an optimization
        if (visibleFields == null)
        {
            visibleFields = modeComponent().visibleFields();
        }

        return visibleFields;

        /** ensure [valid_result] Result != null; **/
    }


    
    /**
     * Returns <code>true</code> if the table has data in it and navigation is permitted.
     *
     * @return <code>true</code> if the table has data in it and navigation is permitted.
     */
    public boolean canNavigateTable()
    {
        return (dataAccessParameters().hasRows() &&
                modeComponent().canBrowseOrSearchRecords());
    }
    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if there is a record to duplicate and users can add new records in this DataAccess mode.
     *
     * @return <code>true</code> if there is a record to duplicate and users can add new records in this DataAccess mode.
     */
    public boolean canDuplicateRecord()
    {
        return dataAccessParameters().hasRows() && canAddRecords();
    }



    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if there is no record to duplicate or users cannot add new records in this DataAccess mode.
     *
     * @return <code>true</code> if  there is no record to duplicate or users cannot add new records in this DataAccess mode.
     */
    public boolean cannotDuplicateRecord()
    {
        return ! canDuplicateRecord();
    }



    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if there is a record to delete and users can delete records from this DataAccess mode.
     *
     * @return <code>true</code> if there is a record to delete and users can delete new records from this DataAccess mode.
     */
    public boolean canDeleteRecords()
    {
        return dataAccessParameters().hasRows() && modeComponent().canDeleteRecords();
    }



    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if users cannot delete records from this DataAccess mode.
     *
     * @return <code>true</code> if users cannot delete new records from this DataAccess mode.
     */
    public boolean cannotDeleteRecords()
    {
        return ! canDeleteRecords();
    }



    /**
     * Returns <code>true</code> if users can modify (add, edit, delete or duplicate) records in this DataAccess mode, <code>false</code> otherwise.
     *
     * @return <code>true</code> if users can modify (add, edit, delete or duplicate) records in this DataAccess mode, <code>false</code> otherwise
     */
    public boolean canModifyRecords()
    {
        return canAddRecords() || hasEditableFields() || canDeleteRecords() || canDuplicateRecord();
    }



    /**
     * Returns <code>true</code> if the mode is confirming the deletion of selected records.
     *
     * @return <code>true</code> if the mode is confirming the deletion of selected records.
     */
    public boolean isConfirmingDeletion()
    {
        return isConfirmingDeletion;
    }



    /**
     * Sets whether the mode is confirming the deletion of selected records.
     *
     * @param newStatus - <code>true</code> if the mode is confirming the deletion of selected records.
     */
    public void setIsConfirmingDeletion(boolean newStatus)
    {
        isConfirmingDeletion = newStatus;
    }



    /**
     * Returns <code>true</code> if currentRow() is marked for deletion
     *
     * @return <code>true</code> if currentRow() is marked for deletion
     */
    public boolean isCurrentRowMarkedForDeletion()
    {
        /** require [has_current_row] currentRow() != null;  **/
        return (recordsToDelete().objectForKey(currentRowPK()) != null);
    }



    /**
     * Sets whether currentRow() is marked for deletion.
     *
     * @param shouldDelete - <code>true</code> if currentRow() should be marked for deletion
     */
    public void markCurrentRowForDeletion(boolean shouldDelete)
    {
        /** require [has_current_row] currentRow() != null;  **/

        if (shouldDelete)
        {
            recordsToDelete().setObjectForKey(currentRow(), currentRowPK());
        }
        else
        {
            recordsToDelete().removeObjectForKey(currentRowPK());
        }
        /** ensure
        [correctly_processed] ((shouldDelete  && isCurrentRowMarkedForDeletion()) ||
                               ( ( ! shouldDelete) && ( !isCurrentRowMarkedForDeletion()))); **/
    }



    /**
     * Returns <code>true</code> if any records are marked for deletion
     *
     * @return <code>true</code> if any records are marked for deletion
     */
    public boolean hasRecordsMarkedForDeletion()
    {
        return recordsToDelete().count() > 0;
    }



    /**
     * Returns the number of records which have been marked for deletion
     *
     * @return the number of records which have been marked for deletion
     */
    public Object numberOfRecordsMarkedForDeletion()
    {
        return new Integer(recordsToDelete().count());
    }



    /**
     * Clears the list of records which have been marked for deletion
     */
    public void resetRecordsMarkedForDeletion()
    {
        recordsToDelete().removeAllObjects();
        /** ensure [marked_records_cleared]  !  hasRecordsMarkedForDeletion();   **/
    }



    /**
     * Checks that the records marked for deletion can actually be deleted.
     */
    public void validateRecordsMarkedForDeletion()
    {
        // We only need to validate rows for deletion if other tables refer to our table.
        if (databaseTable().hasReferringTables())
        {
            Enumeration recordEnumerator = recordsToDelete().objectEnumerator();
            while (recordEnumerator.hasMoreElements())
            {
                VirtualRow record = (VirtualRow) recordEnumerator.nextElement();

                Enumeration fieldEnumerator = record.fields().objectEnumerator();
                while (fieldEnumerator.hasMoreElements())
                {
                    VirtualField field = (VirtualField) fieldEnumerator.nextElement();
                    
                    if (SMVirtualTable.isLookupListDefaultValue(field) || field.hasReferringFields())
                    {
                        // Set the currentRow() so that registerValidationFailureForKey() can associate the failure with the correct row.
                        setCurrentRow(record);
                        
                        // Note that field.column().normalizedName() is the equivalent of dataAccessColumn().normalizedFieldName() in the DataAccessColumn sub-classes.  We do not have easy access to the DataAccessColumn here.
                        registerValidationFailureForKey(SMVirtualTable.deletionRefusedMessage(field),
                                                        field.column().normalizedName() + ".cantDelete");
                    }
                }
            }
        }
    }



    /**
     * Action method to save the changed data.  Displays a confirmation message if records have been selected for deletion.  If no records have been marked for deletion, redirects to re-display the current record in the next mode if the save succeeds.  Otherwise, redisplays this page with validation messages.
     */
    public WOComponent saveChanges()
    {
        WOComponent nextPage = context().page();

        DebugOut.println(30, "validateRecordsMarkedForDeletion");
        validateRecordsMarkedForDeletion();
        DebugOut.println(30, "validateRecordsMarkedForDeletion done");
        
        if (hasRecordsMarkedForDeletion() && ! hasValidationFailures())
        {
            setIsConfirmingDeletion(true);
        }
        else
        {
            nextPage = super.saveChanges();
        }

        return nextPage;
    }



    /**
     * Action method called from confirmation dialog to delete marked records.  Deletes the records and proceeds with saving the changed data.
     */
    public WOComponent acceptDeletion()
    {
        /** require
        [data_is_valid] ! hasValidationFailures();
        [has_marked_records] hasRecordsMarkedForDeletion();
        [is_confirming_deletion]  isConfirmingDeletion();
        **/

        /*
         Bug fix for WO 5.1.  See SCR 1251 for reproduction notes.  If the lookup values referenced by the rows being deleted are faults, they will be fired while processing the delete rules.  This messes up the internal state of the editing context (what is deleted, what is modified), resulting in it attempting to save as updates those records being deleted.  These then fail validateForSave.  To get around this we pre-fire the faults.
         */
        DebugOut.println(30, "Preprocessing deletion for " + recordsToDelete().count() + " records.");
        NSMutableArray lookupColumns = new NSMutableArray();
        Enumeration columnEnumerator = databaseTable().columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column aColumn = (Column)columnEnumerator.nextElement();
            if (aColumn instanceof VirtualLookupColumn)
            {
                lookupColumns.addObject(((VirtualLookupColumn)aColumn).lookupColumn());
            }
        }
        EODatabaseContextAdditions.preloadRelationship(lookupColumns, "fields");
        
        Enumeration recordEnumerator = recordsToDelete().objectEnumerator();
        while (recordEnumerator.hasMoreElements())
        {
            editingContext().deleteObject((EOEnterpriseObject)recordEnumerator.nextElement());
        }

        resetRecordsMarkedForDeletion();
        setIsConfirmingDeletion(false);
        DebugOut.println(30, "Preprocessing deletion done");

        return saveChanges();

        /** ensure [has_no_marked_records] ! hasRecordsMarkedForDeletion();  [has_confirmed_deletion]  ! isConfirmingDeletion();   **/
    }



    /**
     * Action method called from confirmation dialog to abort deletion of marked records.  Changes to the data are not saved, redisplays the current record.
     */
    public WOComponent rejectDeletion()
    {
        /** require
        [data_is_valid] ! hasValidationFailures();
        [has_marked_records] hasRecordsMarkedForDeletion();
        [is_confirming_deletion]  isConfirmingDeletion();
        **/

        setIsConfirmingDeletion(false);
        return context().page();

        /** ensure [has_marked_records] hasRecordsMarkedForDeletion();  [has_confirmed_deletion]  ! isConfirmingDeletion();   **/
    }



    /**
     * Returns the number of records in the found set.
     *
     * @return the number of records in the found set
     */
    public int numberOfRecordsInFoundSet()
    {
        return dataAccessParameters().allRows().count();
    }



    /**
     * Returns the number of records of the table that the user has access to.
     *
     * @return the number of records of the table that the user has access to
     */
    public int numberOfRecordsInTable()
    {
        return dataAccessParameters().countOfRowsInTable();
    }



    /**
     * Returns <code>true</code> if the "Notification not needed" checkbox should be displayed, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the "Notification not needed" checkbox should be displayed, <code>false</code> otherwise
     */
    public boolean showNotificationNotNeededCheckBox()
    {
        /** require [parent_is_dataaccess_component] modeComponent().toParent() instanceof com.gvcsitemaker.core.pagecomponent.DataAccess; **/
        boolean notifyForThisMode = ((com.gvcsitemaker.core.pagecomponent.DataAccess)modeComponent().toParent()).shouldNotifyOnModifyRecords();
        return notifyForThisMode && currentUser().canManageWebsite(componentObject().sectionUsedIn().website()) && hasEditableFields();
    }



    //************* Generic Accessors and Mutators below here *****************


    /**
     * Returns a dictionary of the records which have been marked for deletion.  The key into the dictionary is the virutalRowID of the object which  is a VirtualRow.
     *
     * @return a dictionary of the records which have been marked for deletion.
     */
    protected NSMutableDictionary recordsToDelete()
    {
        return recordsToDelete;
    }





}
