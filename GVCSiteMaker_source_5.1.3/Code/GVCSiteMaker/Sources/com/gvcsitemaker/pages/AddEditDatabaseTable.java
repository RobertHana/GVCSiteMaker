// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This page provides functions for managing data tables.
 */
public class AddEditDatabaseTable extends WebsiteContainerBase implements WebsiteContainer, SMSecurePage
{
    protected NSMutableSet tablesBeingEdited = new NSMutableSet();

    public String tableViewComponentName;
    public VirtualTable table;

    public VirtualTable aTable;
    public int aTableIndex;

    public String anActionString;
    protected NSMutableDictionary selectedTableAction = new NSMutableDictionary();
    protected NSMutableSet tableNamesBeingEdited = new NSMutableSet();

    protected EOEditingContext editingContext; // Peer ec

    protected Table tableToDelete;
    public String deleteTableErrorString;


    /**
     * Designated constructor.  Sets Website for WebsiteContainer.
     */
    public AddEditDatabaseTable(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
    }



    /**
     * Used by the AJAX control on this page to revert changes.
     *
     * @return null, this value is ignored
     */
    public WOComponent revertChanges()
    {
        website().editingContext().revert();
        return null; // return value is ignored
    }


    /**
     * Used by the AJAX control on this page to save changes.
     *
     * @return null, this value is ignored
     */
    public WOComponent saveChanges()
    {
        try
        {
            website().editingContext().saveChanges();
            AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteDataTablesPane", context());
        }
        catch (Exception e)
        {
            // shouldn't be any exceptions
            website().editingContext().revert();
        }
        return null; // return value is ignored
    }



    /**
     * Returns the current user in our editingContext().
     *
     * @return the current user in our editingContext().
     */
    public User currentUser()
    {
        return (User)EOUtilities.localInstanceOfObject(editingContext(), smSession().currentUser());
    }



    /**
     * Action method to create a new table.
     */
    public void createTable()
    {
        SMVirtualTable newTable = SMVirtualTable.newSMVirtualTable();
        editingContext().insertObject(newTable);
        website().addDatabaseTable(newTable);

        newTable.setName(website().uniqueTableName("Untitled Table"));
        newTable.setCreatedBy(currentUser());
        newTable.setModifiedBy(currentUser());

        saveChanges();
    }



    /**
     * Delete the current file.
     */
    public void deleteTable()
    {
        /** require [tableToDelete_not_null] tableToDelete != null; **/
        ((SMVirtualTable)tableToDelete).deleteRows();
        editingContext().deleteObject(tableToDelete);
        website().removeFromDatabaseTables(tableToDelete);
        tableToDelete = null;

        saveChanges();
    }



    /**
     * Action method to modify a data table's properties.
     */
    public void modifyTable(VirtualTable theTable)
    {
        /** require theTable != null; **/
        ConfigureDataTablesPane parent = (ConfigureDataTablesPane)parent();
        parent.tableViewComponentName = "AddEditDatabaseTableColumns";
        table = theTable;
    }


    /**
     * Action method to import data into a table.
     */
    public void importIntoTable(VirtualTable theTable)
    {
        /** require theTable != null; **/
        ConfigureDataTablesPane parent = (ConfigureDataTablesPane)parent();
        parent.tableViewComponentName = "ImportTableData";
        table = theTable;
    }


    /**
     * Action method to export data from a table.
     */
    public void exportFromTable(VirtualTable theTable)
    {
        /** require theTable != null; **/
        ConfigureDataTablesPane parent = (ConfigureDataTablesPane)parent();
        parent.tableViewComponentName = "ExportTableData";
        table = theTable;
    }


    /**
     * Returns the selected choice in the Actions column for the current table.
     *
     * @return the selected choice in the Actions column for the current table
     */
    public String selectedTableAction()
    {
        return (String)selectedTableAction.objectForKey(aTable);
    }


    /**
     * Sets the selected action in the Actions column for the current table.
     *
     * @param newAction the action to set
     */
    public void setSelectedTableAction(String newAction)
    {
        if (newAction != null)
        {
            selectedTableAction.setObjectForKey(newAction, aTable);
        }
        else
        {
            selectedTableAction.removeObjectForKey(aTable);
        }
    }


    /**
     * Returns the list of actions appropriate for the selected file.
     *
     * @return a list of actions as strings
     */
    public NSArray actionsList()
    {
        NSMutableArray actionsList = new NSMutableArray();
        actionsList.addObject("Modify Fields");  // configFile
        actionsList.addObject("Import Data");
        actionsList.addObject("Export Data");
        actionsList.addObject("Delete");  // deleteFile

        return actionsList;
    }


    /**
     * Main dispatch for actions applying to a single data table.
     */
    public void dataTableRepetitionObserverAction()
    {
        if ((selectedTableAction != null) && (selectedTableAction.count() > 0))
        {
            // should only be a single key and value in the dictionary
            SMVirtualTable theTable = (SMVirtualTable)selectedTableAction.allKeys().lastObject();
            String actionForCurrentSection = (String) selectedTableAction.objectForKey(theTable);
            selectedTableAction = new NSMutableDictionary();

            if ("Modify Fields".equals(actionForCurrentSection))
            {
                modifyTable(theTable);
            }
            else if ("Import Data".equals(actionForCurrentSection))
            {
                importIntoTable(theTable);
            }
            else if ("Export Data".equals(actionForCurrentSection))
            {
                exportFromTable(theTable);
            }
            else if ("Delete".equals(actionForCurrentSection))
            {
                if (theTable.isUsedBySections())
                {
                    NSArray sectionsUsedIn = (NSArray)theTable.sectionsUsedIn().valueForKey("name");
                    deleteTableErrorString = "It is being used by one or more sections: " + sectionsUsedIn.componentsJoinedByString(", ");
                    AjaxModalDialog.open(context(), "DeleteTableErrorModalDialog");
                }
                else if (theTable.hasReferringTables())
                {
                    NSArray referringTables = (NSArray)theTable.referringTables().valueForKey("name");
                    deleteTableErrorString = "It is being used as the source of Lookup Value fields in one or more tables: " +
                        referringTables.componentsJoinedByString(", ");
                    AjaxModalDialog.open(context(), "DeleteTableErrorModalDialog");
                }
                else
                {
                    tableToDelete = theTable;
                    AjaxModalDialog.open(context(), "ConfirmDeleteTableModalDialog");
                }
            }
        }
    }



    public String editTableNameInPlaceNameFieldID()
    {
        return "editTableNameInPlaceNameField" + aTableIndex;
    }


    public String editTableNameInPlaceDescriptionFieldID()
    {
        return "editTableNameInPlaceDescriptionField" + aTableIndex;
    }


    public String editTableNameInPlaceID()
    {
        return "editTableName" + aTableIndex;
    }


    public String editTableNameInPlaceEdit()
    {
        return "editTableName" + aTableIndex + "Edit()";
    }


    public String editTableNameInPlaceSave()
    {
        return "editTableName" + aTableIndex + "Save();";
    }


    public String editTableNameInPlaceCancel()
    {
        return "editTableName" + aTableIndex + "Cancel()";
    }



    public String editTableDescriptionInPlaceID()
    {
        return "editTableDescription" + aTableIndex;
    }


    public String editTableDescriptionInPlaceEdit()
    {
        return "editTableDescription" + aTableIndex + "Edit()";
    }


    public String editTableDescriptionInPlaceSave()
    {
        return "editTableDescription" + aTableIndex + "Save()";
    }


    public String editTableDescriptionInPlaceCancel()
    {
        return "editTableDescription" + aTableIndex + "Cancel()";
    }



    /**
     * Returns true iff the user can edit the current group.
     *
     * @return true iff the user can edit the current group
     */
    public boolean canEditTableName()
    {
        return (tableNamesBeingEdited.count() == 0 || tableNamesBeingEdited.contains(aTable));
    }



    public boolean isEditingTableName()
    {
        return (tableNamesBeingEdited.contains(aTable));
    }

    public void setIsEditingTableName(boolean isEditing)
    {
        // only turn editing on for the first one
        if (isEditing && tableNamesBeingEdited.count() == 0)
        {
            tableNamesBeingEdited.add(aTable);
        }
    }



    /* Generic setters and getters ***************************************/


    public EOEditingContext editingContext()
    {
        // Create this on demand and our super's constructor causes setWebsite to be called and it needs this.
        if (editingContext == null)
        {
            editingContext = ((Session) session()).newEditingContext();
        }

        return editingContext;
    }



    public void setWebsite(Website value)
    {
        containedWebsite = (Website) EOUtilities.localInstanceOfObject(editingContext(), value);
    }



}
