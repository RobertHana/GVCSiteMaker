// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;


/**
 * DataAccessSingleMode implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessSingleMode, the Single Record view of a Data Access Section.
 */
public class DataAccessSingleMode extends DataAccessDisplayMode
{


    /**
     * Designated constructor.
     */
    public DataAccessSingleMode(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to select the record to display.  This is needed to make Single Record View go.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        if (currentRow() == null)
        {
            dataAccessParameters().selectCurrentRow();
        }

        super.appendToResponse(aResponse, aContext);
    }

    

    /**
     * Action method to initiate deletion of the current record.
     *
     * @return the page to display next.
     */
    public WOComponent deleteRecord()
    {
        /** require [has_currentRow] currentRow() != null;  **/
        
        markCurrentRowForDeletion(true);

        WOComponent nextPage = saveChanges();

        // If this record can not be deleted reset the deletion flag as there is no way for the user to do so.
        if (hasRecordsMarkedForDeletion() && hasValidationFailures())
        {
            resetRecordsMarkedForDeletion();
        }

        return nextPage;
    }


    /**
     * Action method called from confirmation dialog to delete record.  Deletes the records and proceeds with saving the changed data.
     */
    public WOComponent acceptDeletion()
    {
        /** require
        [data_is_valid] ! hasValidationFailures();
        [has_marked_records] hasRecordsMarkedForDeletion();
        [is_confirming_deletion]  isConfirmingDeletion();
        **/

        dataAccessParameters().setCurrentRow(null);
        
        return super.acceptDeletion();
        
        /** ensure
        [has_no_marked_records] ! hasRecordsMarkedForDeletion();
        [has_confirmed_deletion]  ! isConfirmingDeletion();
        **/
    }

    

    /**
     * Returns a string indicating the index of the record that the user is current viewing.
     *
     * @return a string indicating the index of the record that the user is current viewing
     */
    public String recordNumberSelected()
    {
        return new Integer(dataAccessParameters().allRows().indexOfObject(dataAccessParameters().currentRow()) + 1).toString();
    }



    /**
     * Returns <code>true</code> if currentRow is the first record in the found set.  Also returns true if this is a bad request (this was used in a binding in an correct place in the template).
     *
     * @return <code>true</code> if currentRow is the first record in the found set.
     */
    public boolean isFirst()
    {
        return ( ! canNavigateTable()) || dataAccessParameters().isFirstRow();
    }



    /**
     * Returns <code>true</code> if currentRow is the last record in the found set.  Also returns true if this is a bad request (this was used in a binding in an correct place in the template).
     *
     * @return <code>true</code> if currentRow is the last record in the found set.
     */
    public boolean isLast()
    {
        return ( ! canNavigateTable()) || dataAccessParameters().isLastRow();
    }



    /**
     * Returns the URL to show the first record in the found set.  Returns the bad requrest URL if this is a bad request (this was used in a binding in an correct place in the template).
     *
     * @return the URL to show the first record in the found set.
     */
    public String goFirstUrl()
    {
        String goFirstURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable())
        {
            goFirstURL = SMDataAccessActionURLFactory.firstRecordInSingleModeUrl(dataAccessParameters());
        }

        return goFirstURL;
        /** ensure [valid_response]  Result != null;  **/
    }



    /**
     * Returns the URL to show the record before currentRow() in the found set.  Returns the bad requrest URL if this is a bad request (this was used in a binding in an correct place in the template).
     *
     * @return the URL to show the record before currentRow() in the found set.
     */
    public String goPreviousUrl()
    {
        String goPreviousURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable() && ! isFirst())
        {
            goPreviousURL = SMDataAccessActionURLFactory.previousRecordInSingleModeUrl(dataAccessParameters());
        }

        return goPreviousURL;
        /** ensure [valid_response]  Result != null;  **/
    }



    /**
     * Returns the URL to show the record after currentRow() in the found set.  Returns the bad requrest URL if this is a bad request (this was used in a binding in an correct place in the template).
     *
     * @return the URL to show the record after currentRow() in the found set.
     */
    public String goNextUrl()
    {
        String goNextURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable() && ! isLast())
        {
            goNextURL = SMDataAccessActionURLFactory.nextRecordInSingleModeUrl(dataAccessParameters());
        }

        return goNextURL;
        /** ensure [valid_response]  Result != null;  **/
    }



    /**
     * Returns the URL to show the last record in the found set.  Returns the bad requrest URL if this is a bad request (this was used in a binding in an correct place in the template).
     *
     * @return the URL to show the last record in the found set.
     */
    public String goLastUrl()
    {
        String goLastURL = SMDataAccessActionURLFactory.badRequestUrl(dataAccessParameters());

        if (canNavigateTable())
        {
            goLastURL = SMDataAccessActionURLFactory.lastRecordInSingleModeUrl(dataAccessParameters());
        }

        return goLastURL;
        /** ensure [valid_response]  Result != null;  **/
    }



    /**
     * Returns the URL to switch from Single Record to List of Records mode.
     *
     * @return the URL to switch from Single Record to List of Records mode.
     */
    public String toggleModeUrl()
    {
        return listModeUrl();
    }



    /**
     * Returns the URL to initiate creation of a new records based on the current record.
     *
     * @return the URL to initiate creation of a new records based on the current record.
     */
    public String duplicateRecordUrl()
    {
        return SMDataAccessActionURLFactory.duplicateRecordAndReturnToCurrentModeUrl(dataAccessParameters());
    }



}
