// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

import net.global_village.foundation.ExceptionConverter;
import net.global_village.virtualtables.VirtualRow;


/**
 * DataAccessAddMode implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessAddMode, the Add view of a Data Access Section.
 */
public class DataAccessAddMode extends DataAccessMode
{
    protected EOEnterpriseObject theNewRow;


    /**
     * Designated constructor.
     */
    public DataAccessAddMode(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to create new row if one has not already been created.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        // This can not happen until after componentObject has been set
        if (currentRow() == null)
        {
            createNewRow();
        }

        super.appendToResponse(aResponse, aContext);
    }



    /**
     * Creates a new row in the table to add data to.  Creates the new as a duplicate of an existing row if requested.
     */
    public void createNewRow()
    {
        /** require [has_component_object] componentObject() != null;   **/

        // Handle duplicating an existing record
        if (dataAccessParameters().isDuplicateAction() && (dataAccessParameters().recordFromUrl() != null))
        {
            VirtualRow localRow = (VirtualRow) EOUtilities.localInstanceOfObject(editingContext(), dataAccessParameters().recordFromUrl());
            setCurrentRow(localRow.duplicate());
        }
        else
        {
            com.gvcsitemaker.core.pagecomponent.DataAccessAddMode addMode = (com.gvcsitemaker.core.pagecomponent.DataAccessAddMode)
            EOUtilities.localInstanceOfObject(editingContext(), componentObject());
            setCurrentRow(addMode.newRow());
        }

        User creatingUser = (User)EOUtilities.localInstanceOfObject(editingContext(), ((Session)session()).currentUser());
        currentRow().takeValueForKey(creatingUser, "Created By");
        currentRow().takeValueForKey(creatingUser, "Modified By");

        /** ensure [has_current_row] currentRow() != null;  **/
    }



    /**
     * Action method to cancel the additions of a new row.  Discards the row.
     */
    public WOComponent cancelChanges()
    {
        editingContext().revert();
        createNewRow();

        return redirectToUrl(SMDataAccessActionURLFactory.cancelAddUrl(dataAccessParameters()));
    }



    /**
     * Action method to save the new row and discard it after a successful save.
     */
    public WOComponent saveChanges()
    {
        WOComponent nextPage = context().page();

        if (! hasValidationFailures())
        {
            try
            {
                editingContext().saveChanges();
                dataAccessParameters().setCurrentRow(currentRow());

                if (( ! shouldNotSendNotification()) && (((DataAccess)parent()).dataAccessComponent().shouldNotifyOnCreateRecords()))
                {
                    postNotification(new NSArray(currentRow()), true);
                }

                nextPage = redirectToUrl(SMDataAccessActionURLFactory.postAddUrl(dataAccessParameters()));
                createNewRow();
            }
            catch (NSValidation.ValidationException e)
            {
                throw new ExceptionConverter(e);
            }
        }

        return nextPage;
    }



    /**
     * Action method to save the new row and prepare to add another.
     */
    public WOComponent addMoreRecords()
    {
        WOComponent nextPage = context().page();

        if (! hasValidationFailures())
        {
            try
            {
                editingContext().saveChanges();
                dataAccessParameters().setCurrentRow(currentRow());
                if (( ! shouldNotSendNotification()) && (((DataAccess)parent()).dataAccessComponent().shouldNotifyOnCreateRecords()))
                {
                    postNotification(new NSArray(currentRow()), true);
                }
                createNewRow();
            }
            catch (NSValidation.ValidationException e)
            {
                // Anything to do?
            }
        }

        return nextPage;
    }



    /**
     * Wrapper for DataAccessMode PageComponent method of same name.  Returns <code>true</code> if any of the children (DataAccessColumn) are editable.  Overidden for add mode to work even when there are no records.
     *
     * @return <code>true</code> if any of the children (DataAccessColumn) are editable.
     */
    public boolean hasEditableFields()
    {
        return modeComponent().hasEditableFields();
    }



    /**
     * This method is only here so that Jass generates the proper internal method of currentRow().
     */
    public Object fieldValue()
    {
        /** require [has_aDataAccessColumn] aDataAccessColumn != null; [has_current_row] currentRow() != null;  **/
        return super.fieldValue();
    }



    /**
     * Returns <code>true</code> if the "Notification not needed" checkbox should be displayed, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the "Notification not needed" checkbox should be displayed, <code>false</code> otherwise
     */
    public boolean showNotificationNotNeededCheckBox()
    {
        /** require [parent_is_dataaccess_component] modeComponent().toParent() instanceof com.gvcsitemaker.core.pagecomponent.DataAccess; **/
        boolean notifyForThisMode = ((com.gvcsitemaker.core.pagecomponent.DataAccess)modeComponent().toParent()).shouldNotifyOnCreateRecords();
        return notifyForThisMode && currentUser().canManageWebsite(componentObject().sectionUsedIn().website());
    }



    //************* Generic Accessors and Mutators below here *****************
    public EOEnterpriseObject currentRow()
    {
        return theNewRow;
    }
    public void setCurrentRow(EOEnterpriseObject newRow)
    {
        theNewRow = newRow;
    }
    
}
