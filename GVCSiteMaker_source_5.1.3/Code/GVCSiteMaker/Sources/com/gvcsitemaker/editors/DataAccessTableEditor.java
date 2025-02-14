// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;


/**
 * This page implements the one time only second step of creating a DataAccess Section - choosing the table.  This page is only shown when the newly created section does not have an assigned table.  After a table has been assigned, the DataAccessConfigurationEditor is used to edit this type of Section.
 */
public class DataAccessTableEditor extends DataAccessBaseEditor 
{
    /** @TypeInfo Table */
    public Table aDatabaseTable;

    /** @TypeInfo Table */
    protected Table selectedTable;
    protected String errorMessage;


    /**
     * Designated constructor.
     */
    public DataAccessTableEditor(WOContext context)
    {
        super(context);
    }



    /**
     * Cleans up the newly or partially created Section in the case where someone tries to create a Data Access Section *before* they have created any Database Tables.  This is the only place where this can be done reliably.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);

        if (website().databaseTables().count() == 0)
        {
            discardNewSection();
        }
    }



    /**
     * This action can be called when someone tries to create a Data Access
     * Section *before* they have created any Database Tables.  That results in
     * a page with a link directly to Add a New Database Table.
     *
     * @return a page where the user can add tables
     */
    public WOComponent gotoAddTable()
    {
        ConfigTabSet tabSet = (ConfigTabSet)pageWithName("ConfigTabSet");
        tabSet.setTab5Selected(true);
        return tabSet;
    }



    /**
     * Cleans up the newly or partially created Section if the process cannot be compeleted, or the user abandons it.
     */
    protected void discardNewSection()
    {
        // Make sure this has not already been done to avoid problems with page reloads.
        if (section().editingContext() != null)
        {
            // If we were using peer editing contexts, this would be enough...
            // Unfortunately, having the desired effect is problematic.  If we delete it, then the rest of the RR loops dies with null pointer exceptions when it references the deleted section.  If we delete it, then do something like setSection(website().homeSection()) then we get class cast exceptions.  So, until we get proper peer ECs things are less than ideal.  Even then disposing of the EC will have consequences later.
            editingContext().revert();
        }
    }



    /**
     * Action used to set selectedTable into the new Section and move to the next mode (DataAccessConfigurationEditor) in editing a Data Access Section.
     *
     * @return EditSection page showing DataAccessConfigurationEditor.
     */
    public WOComponent selectTable()
    {
        WOComponent nextPage = context().page();

        // Validate selected table.
        setErrorMessage(null);
        if (selectedTable() == null)
        {
            setErrorMessage("Please select the Databse Table to show in this section.");
        }

        if (errorMessage() == null)
        {
            // Backtracking protection: only do this once!
            if (databaseTable() == null)
            {
                dataAccessComponent().setDatabaseTable(selectedTable());
                editingContext().saveChanges();
            }

            // Create a new editor so that everything shows up in the correct mode.
            nextPage = pageWithName("EditSection");
            ((EditSection)nextPage).setSection(section());
        }

        return nextPage;

        /** ensure [valid_result] (errorMessage() != null) || 
             (((com.gvcsitemaker.core.pagecomponent.DataAccess) section().component()).databaseTable() != null); **/
    }



    /**
     * Abandons the process of creating the new Data Access Section.
     *
     * @return the ConfigPage
     */
    public WOComponent cancelSection()
    {
        discardNewSection();
        return pageWithName("ConfigTabSet");
    }



    //****************************  Generic Accessors Below Here  ****************************\\

    /** @TypeInfo Table */
    public Table selectedTable() {
        return selectedTable;
    }
    public void setSelectedTable(Table newSelectedTable) {
        selectedTable = newSelectedTable;
    }

    public String errorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String newErrorMessage) {
        errorMessage = newErrorMessage;
    }

}
