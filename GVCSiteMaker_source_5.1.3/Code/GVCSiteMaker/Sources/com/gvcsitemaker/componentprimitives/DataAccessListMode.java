// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;


/**
 * DataAccessListMode implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessListMode, the List of Records view of a Data Access Section.
 */
public class DataAccessListMode extends DataAccessDisplayMode
{
    
    /**
     * Designated constructor.
     */
    public DataAccessListMode(WOContext context)
    {
        super(context);
    }



    /**
     * Returns the list of records to display.
     *
     * @return the list of records to display
     */
    public NSArray recordsInCurrentGroup()
    {
        return dataAccessParameters().recordsInCurrentGroup();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a string indicating the range of records that the user is currently viewing.
     *
     * @return a string indicating the range of records that the user is currently viewing
     */
    public String recordRangeInGroup()
    {
        return (dataAccessParameters().allRows().indexOfObject(recordsInCurrentGroup().objectAtIndex(0)) + 1) + "-" + (dataAccessParameters().allRows().indexOfObject(recordsInCurrentGroup().lastObject()) + 1);
    }



    /**
     * Records the validation failure and associates with currentRow() for later retrieval in validationFailureForKey(String aKey).  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param validationFailure message to display in the UI for this validation failure
     * @param key a unique key to associate this failure with
     */
    public void registerValidationFailureForKey(String validationFailure, String key)
    {
        /** require  [valid_param] validationFailure != null;  [valid_key] key != null;   **/
        super.registerValidationFailureForKey(validationFailure, validationFailureKeyForCurrentRow() + key);
        /** ensure [message_recorded] super.validationFailureForKey(validationFailureKeyForCurrentRow() + key) != null;  **/
    }



    /**
     * Returns the registered validation error for aKey which is associated with currentRow(), or null if no validation error was registered.  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param key a unique key associated with this failure
     * @return the appropriate registered validation error or null if no validation error was registered
     */
    public String validationFailureForKey(String aKey)
    {
        /** require  [valid_param] aKey != null;   **/

        return super.validationFailureForKey(validationFailureKeyForCurrentRow() + aKey);
    }



    /**
     * Returns a key suitable for a dictionary that uniquely indentifies currentRow().
     *
     * @return a key suitable for a dictionary that uniquely indentifies currentRow()
     */
    protected String validationFailureKeyForCurrentRow()
    {
        /** require  [has_row] currentRow() != null;   **/
        return editingContext().globalIDForObject(currentRow()).toString();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> if current group is the first group of records in the found set.
     *
     * @return <code>true</code> if current group is the first group of records in the found set
     */
    public boolean isFirst()
    {
        return ( ! canNavigateTable()) || dataAccessParameters().isFirstGroup();
    }



    /**
     * Returns <code>true</code> if current group is the last group of records in the found set.
     *
     * @return <code>true</code> if current group is the last group of records in the found set
     */
    public boolean isLast()
    {
        return ( ! canNavigateTable()) || dataAccessParameters().isLastGroup();
    }



    /**
     * Returns the URL to show the first group of records in the found set.
     *
     * @return the URL to show the first group of records in the found set
     */
    public String goFirstUrl()
    {
        String goFirstURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable())
        {
            goFirstURL = SMDataAccessActionURLFactory.firstGroupInListModeUrl(dataAccessParameters());
        }

        return goFirstURL;
        /** ensure [valid_response]  Result != null;  **/

    }



    /**
     * Returns the URL to show the previous group of records in the found set.
     *
     * @return the URL to show the previous record in the found set.
     */
    public String goPreviousUrl()
    {
        String goPreviousURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable() && ! isFirst())
        {
            goPreviousURL = SMDataAccessActionURLFactory.previousGroupInListModeUrl(dataAccessParameters());
        }

        return goPreviousURL;
        /** ensure [valid_response]  Result != null;  **/

    }



    /**
     * Returns the URL to show the next group of records in the found set.
     *
     * @return the URL to show the next group of records in the found set.
     */
    public String goNextUrl()
    {
        String goNextURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable() & ! isLast())
        {
            goNextURL = SMDataAccessActionURLFactory.nextGroupInListModeUrl(dataAccessParameters());
        }

        return goNextURL;
        /** ensure [valid_response]  Result != null;  **/

    }



    /**
     * Returns the URL to show the last group of records in the found set.
     *
     * @return the URL to show the last group of records in the found set.
     */
    public String goLastUrl()
    {
        String goLastURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable())
        {
            goLastURL = SMDataAccessActionURLFactory.lastGroupInListModeUrl(dataAccessParameters());
        }

        return goLastURL;
        /** ensure [valid_response]  Result != null;  **/

    }
   
    

    /**
     * Returns the URL to switch from List of Records to Single Record mode.
     *
     * @return the URL to switch from List of Records to Single Record mode.
     */
    public String toggleModeUrl()
    {
        return singleModeUrl();
    }



    /**
     * Returns the URL to display the currentRow() in Single mode with the next mode as List of Records.  This is for the Details... link.
     *
     * @return the URL to display the currentRow() in Single mode with the next mode as List of Records.
     */
    public String singleRecordUrl()
    {
        String singleRecordUrl = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        // This link is allowed even if navigation is not.  I think...  -ch
        if (dataAccessParameters().hasRows() &&
             (currentRow() != null))
        {
            singleRecordUrl = SMDataAccessActionURLFactory.recordInSingleModeWithNextMode(dataAccessParameters());
        }

        return singleRecordUrl;
        /** ensure [valid_response]  Result != null;  **/

    }



    /**
     * Overridden to also return true if records can be deleted as the Save Changes button also needs to appear in this case in List Mode.
     *
     * @return <code>true</code> if records can be deleted or edited
     */
    public boolean hasEditableFields()
    {
        return super.hasEditableFields() || modeComponent().isEditable();
    }




    /**
     * Returns the value for the delete checkbox for currentRow
     *
     * @return the value for the delete checkbox for currentRow
     */
    public Object markRecordForDeletion()
    {
        return isCurrentRowMarkedForDeletion() ? currentRowPK() : null;
    }



    /**
     * Adds currentRow() to the set of records to delete if the passed value matches the Record ID (virtualRowID) of currentRow().
     *
     * @param value - value returned by delete checkbox.  
     */
    public void setMarkRecordForDeletion(Object value)
    {
        if (value != null)
        {
            if (value.equals(currentRowPK()))
            {
                markCurrentRowForDeletion(true);
            }
            // else there is some backtracking going on, now what?
        }
        else
        {
            markCurrentRowForDeletion(false);
        }
    }


}
