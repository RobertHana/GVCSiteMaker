// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.Date;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This page provides functions for managing data tables' columns.
 */
public class AddEditDatabaseTableColumns extends WebsiteContainerBase implements WebsiteContainer, SMSecurePage
{
    public SMVirtualTable table;
    public VirtualColumn aColumn;
    public int aColumnIndex;

    public Column columnToDelete;
    public String deleteErrorString;
  
    public NSArray columnTypes;
    public ColumnType aColumnType;

    public ColumnType newColumnType;
    public Column newColumnTypeSelectedLookupSourceColumn;
    public String columnTypeRequiredString = "";

    public Column editColumn;

    protected EOEditingContext editingContext; // Peer ec


    /**
     * Designated constructor.  Sets Website for WebsiteContainer.
     */
    public AddEditDatabaseTableColumns(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);

        columnTypes = new NSArray(new Object[] {
                ColumnType.columnTypeWithName(editingContext(), ColumnType.StringColumnType),
                ColumnType.columnTypeWithName(editingContext(), ColumnType.BooleanColumnType),
                ColumnType.columnTypeWithName(editingContext(), ColumnType.TimestampColumnType),
                ColumnType.columnTypeWithName(editingContext(), ColumnType.NumberColumnType),
                ColumnType.columnTypeWithName(editingContext(), ColumnType.LookupColumnType),
                ColumnType.columnTypeWithName(editingContext(), ColumnType.CalculatedColumnType),
                ColumnType.columnTypeWithName(editingContext(), VirtualSiteFileField.ColumnTypeName) });
    }



    /**
     * Used by the AJAX control on this page to revert changes.
     *
     * @return null, this value is ignored
     */
    public void revertChanges()
    {
        table().editingContext().revert();
    }


    /**
     * Used by the AJAX control on this page to save changes.
     *
     * @return null, this value is ignored
     */
    public void saveChanges()
    {
        try
        {
            table().editingContext().saveChanges();
        }
        catch (Exception e)
        {
            // shouldn't be any exceptions
            table().editingContext().revert();
        }
    }


    /**
     * Returns to the main data table page.
     *
     * @return the main data table page
     */
    public void returnToDataTables()
    {
        ConfigureDataTablesPane parent = (ConfigureDataTablesPane)parent();
        parent.tableViewComponentName = "AddEditDatabaseTable";
        parent.table = null;
    }



    /**
     * Returns the page that allows the user to edit the calculation of a calculated field.
     *
     * @return the page that allows the user to edit the calculation of a calculated field
     */
    public void editCalculatedField()
    {
        /*EditCalculationPane result = (EditCalculationPane)pageWithName(EditCalculationPane.class.getCanonicalName());
        result.setColumn(aColumn);
        return result;*/
        calculatedColumn = (VirtualCalculatedColumn)aColumn;
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
     * @return <code>true</code> if aColumn instanceof VirtualCalculatedColumn
     */
    public boolean isCalculatedColumn()
    {
        return aColumn instanceof VirtualCalculatedColumn;
    }


    /**
     * Returns description of calculation type as <data type> Calculation.  Returns an error description
     * if the expression can't be evaluated (e.g. a referernced field has been deleted.
     *
     * @return description of calculation type as <data type> Calculation
     */
    public String calculationType()
    {
        try
        {
            return ((VirtualCalculatedColumn)aColumn).expressionColumnType().textDescription() + " " + aColumn.typeDescription();
        }
        catch (EOFValidationException e)
        {
            return "Error in expression: " + e.getLocalizedMessage();
        }
    }


    /**
     * Returns the fields in Database Tables in this Website which can be used as the source of a Lookup column.  Fields in the table being edited are not returned.
     *
     * @return the fields in Database Tables in this Website which can be used as the source of a Lookup column
     */
    /** @TypeInfo VirtualColumn */
    public NSArray lookupSourceColumns()
    {
        NSMutableArray lookupSourceColumns = new NSMutableArray();
        Enumeration tableEnumerator = website().orderedDatabaseTables().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            Table table = (Table) tableEnumerator.nextElement();
            if (table instanceof SMVirtualTable && ! table.equals(table()))
            {
                ((NSMutableArray)lookupSourceColumns).addObjectsFromArray(((SMVirtualTable)table).lookupSourceColumns());
            }
        }

        return lookupSourceColumns;
    }



    /**
     * Action method to create a new table.
     */
    public void createColumn()
    {
        if (newColumnType != null)
        {
            columnTypeRequiredString = "";
            newColumnType = (ColumnType)EOUtilities.localInstanceOfObject(editingContext(), newColumnType);
            Column newColumn = table().newColumn(table().uniqueColumnName("Untitled Field"), newColumnType);

            if (newColumnType.name().equals("Lookup"))
            {
                if (newColumnTypeSelectedLookupSourceColumn != null)
                {
                    newColumnTypeSelectedLookupSourceColumn = (VirtualColumn)EOUtilities.localInstanceOfObject(editingContext(), newColumnTypeSelectedLookupSourceColumn);
                    ((VirtualLookupColumn)newColumn).setLookupColumn(newColumnTypeSelectedLookupSourceColumn);
                }
                else
                {
                    // no source column, just close and do nothing
                    editingContext().revert();
                    return;
                }
            }

            // It would be nice to not have to do this manually.
            table().setModifiedBy((User) EOUtilities.localInstanceOfObject(editingContext(), smSession().currentUser()));
            table().setDateModified(net.global_village.foundation.Date.now());

            // Add this column to all the Data Access section using this table.
            SMVirtualTable.updateDataAccessSections(table());

            try
            {
                editingContext().saveChanges();
            }
            catch ( NSValidation.ValidationException e)
            {
                editingContext().revert();

                // The only errors here should be programming errors.  Just raise and let the error logger deal with it.
                throw e;
            }

            AjaxModalDialog.close(context());
        }
        else
        {
            columnTypeRequiredString = "<span class = \"warningText\">The field type is required.</span><br/>";
            AjaxModalDialog.update(context());
            DebugOut.println(3, "New column type is null");
        }
    }



    /**
     * Delete the current file.
     */
    public void deleteColumn()
    {
        /** require [tableToDelete_not_null] tableToDelete != null; **/

        DebugOut.println(3, "Deleting field " + columnToDelete.name());

        table().removeObjectFromBothSidesOfRelationshipWithKey(columnToDelete, "columns");
        editingContext().deleteObject(columnToDelete);


        if ( ! columnToDelete.hasReferringColumns())
        {
            // Use a NotifyingEditingContext so that SiteFiles get deleted from disk.  Set the fetch timestamp to avoid refetching.
            EOEditingContext ec = smSession().newEditingContext();
            ec.setFetchTimestamp(smSession().defaultEditingContext().fetchTimestamp());

            VirtualColumn localColumnToDelete = (VirtualColumn) EOUtilities.localInstanceOfObject(ec, columnToDelete);

            // Prefetch for optimization and to avoid firing faults while processing delete rules (see below).
            EODatabaseContextAdditions.preloadRelationship(new NSArray(localColumnToDelete), "fields");

            // For some reason the fields of all the rows containing a field of the column being deleted are fetched while deleting. Prefetch them to avoid firing faults while processing delete rules (see below).
            EODatabaseContextAdditions.preloadRelationship((NSArray) localColumnToDelete.fields().valueForKey("row"), "fields");

            // Bug fix for WO 5.1.  See SCR 1252 for reproduction notes.  See net.global_village.eofvalidation.EOEditingContext for bug detection and details.  If the lookup values referenced by the column being deleted are faults, they will be fired while processing the delete rules.  This messes up the internal state of the editing context (what is deleted, what is modified), resulting in it attempting to save as updates those records being deleted.  These then fail validateForSave.  To get around this we pre-fire the faults.
            if (localColumnToDelete instanceof VirtualLookupColumn)
            {
                NSArray lookupColumn = new NSArray(((VirtualLookupColumn) localColumnToDelete).lookupColumn());
                EODatabaseContextAdditions.preloadRelationship(lookupColumn, "fields");
            }

            Table tableToDeleteFrom = localColumnToDelete.table();
            tableToDeleteFrom.removeObjectFromBothSidesOfRelationshipWithKey(localColumnToDelete, "columns");

            // Remove this column from all the Data Access section using this table.
            SMVirtualTable.updateDataAccessSections(tableToDeleteFrom);

            ec.deleteObject(localColumnToDelete);
            ec.saveChanges();

            AjaxUtils.javascriptResponse("AUC.update('ConfigureWebsiteDataTablesColumnsPane');", context());

            DebugOut.println(3, "Deleted field " + columnToDelete.name());
        }
        else
        {
            String message = "";
            Enumeration columnEnumerator = columnToDelete.referringColumns().objectEnumerator();
            while (columnEnumerator.hasMoreElements())
            {
                Column column = (Column) columnEnumerator.nextElement();
                message += "Field " + column.name() + " in Database Table " + column.table().name() + "<br>";
            }

            revertChanges();
            DebugOut.println(3, "Failed to Delete field " + columnToDelete.name() + ", used as lookup in " + message);

            deleteErrorString = message;
        }
    }


    public void conditionallyOpenDeleteErrorDialog()
    {
        if (deleteErrorString != null)
        {
            AjaxModalDialog.open(context(), "DeleteColumnErrorModalDialog");
            deleteErrorString = null;
        }
    }



    public String columnNameTextFieldID()
    {
        return "columnNameTextField" + aColumnIndex;
    }


    public String columnDescriptionTextFieldID()
    {
        return "columnDescriptionTextField" + aColumnIndex;
    }


    public String editColumnNameInPlaceID()
    {
        return "editColumnName" + aColumnIndex;
    }


    public String editColumnNameInPlaceEdit()
    {
        return "editColumnName" + aColumnIndex + "Edit()";
    }


    public String editColumnNameInPlaceSave()
    {
        return "editColumnName" + aColumnIndex + "Save()";
    }


    public String editColumnNameInPlaceCancel()
    {
        return "editColumnName" + aColumnIndex + "Cancel()";
    }



    public String editColumnDescriptionInPlaceID()
    {
        return "editColumnDescription" + aColumnIndex;
    }


    public String editColumnDescriptionInPlaceEdit()
    {
        return "editColumnDescription" + aColumnIndex + "Edit()";
    }


    public String editColumnDescriptionInPlaceSave()
    {
        return "editColumnDescription" + aColumnIndex + "Save()";
    }


    public String editColumnDescriptionInPlaceCancel()
    {
        return "editColumnDescription" + aColumnIndex + "Cancel()";
    }



    public void setColumnToDelete()
    {
        columnToDelete = aColumn;
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



    public SMVirtualTable table()
    {
        return table;
    }


    public void setTable(VirtualTable value)
    {
        table = (SMVirtualTable)EOUtilities.localInstanceOfObject(editingContext(), value);
    }



    public void setWebsite(Website value)
    {
        containedWebsite = (Website) EOUtilities.localInstanceOfObject(editingContext(), value);
    }


    /************************** CALC MODAL DIALOG STUFF ************************/

    protected String invalidExpressionError;

    /** @TypeInfo Column */
    protected VirtualCalculatedColumn calculatedColumn;
    protected String expressionResult;
    protected String expressionResultType;

    public Column availableColumn;


    /**
     * Validates the new column information and save changes, returning to the revise table page, if it is valid.  If it is not valid, redisplays this page with error messages.
     */
    public void saveCalculation()
    {
        if (! hasErrors())
        {
            // It would be nice to not have to do this manually.
            databaseTable().setModifiedBy((User) EOUtilities.localInstanceOfObject(editingContext(),
                                                                                   ((SMSession)session()).currentUser()));
            databaseTable().setDateModified(Date.now());

            try
            {
                editingContext().saveChanges();
                expressionResultType = null;
                AjaxModalDialog.close(context());
            }
            catch ( NSValidation.ValidationException e)
            {
                editingContext().revert();

                AjaxModalDialog.update(context(), null);  // This is to update the page if we display errors later on
            }
        }
        else
        {
            AjaxModalDialog.update(context(), null);
        }
    }



    /**
     * Action used to cancel changes and return to AddEditDatabaseTable.
     *
     * @return the <code>AddEditDatabaseTableColumns</code> page set for databaseTable().
     */
    public void cancelChanges()
    {
        editingContext().revert();
        expressionResultType = null;
        AjaxModalDialog.close(context());
    }



    /**
     * Returns <code>true></code> if there are any validation errors which would prevent the creation of the column.
     *
     * @return <code>true></code> if there are any validation errors which would prevent the creation of the column.
     */
    public boolean hasErrors()
    {
        return (invalidExpressionError() != null);
    }



    /**
     * Submitting the page tests the expression for validity.  If it is valid, this method also gets an example
     * result and type and shows it on the page.
     *
     * @return this page
     */
    public void testExpression()
    {
        if (invalidExpressionError() == null)
        {
            Object exampleValue = calculatedColumn().exampleValue();
            setExpressionResult(exampleValue.toString());
            setExpressionResultType(calculatedColumn().expressionType(exampleValue));
        }
        AjaxModalDialog.update(context(), null);
    }



    /**
     * @return column() downcast to VirtualCalculatedColumn
     */
    public VirtualCalculatedColumn calculatedColumn()
    {
        /** require [is_VirtualCalculatedColumn] isCalculatedColumn();  **/
        return calculatedColumn;
    }



    /**
     * Clears validation messages after page is rendered
     *
     * @see net.global_village.woextensions.ClickToOpenComponent#appendToResponse(com.webobjects.appserver.WOResponse, com.webobjects.appserver.WOContext)
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        setInvalidExpressionError(null);
        setExpressionResult(null);
        setExpressionResultType(null);
    }



    /**
     * Catches expression validation exceptions to display in the UI.
     *
     * @see com.webobjects.appserver.WOComponent#validationFailedWithException(java.lang.Throwable, java.lang.Object, java.lang.String)
     */
    public void validationFailedWithException(Throwable exception, Object badValue, String keyPath)
    {
        if ("calculatedColumn.expression".equals(keyPath))
        {
            calculatedColumn().setExpression((String)badValue);
            setInvalidExpressionError(exception.getLocalizedMessage());
        }
        else
        {
            super.validationFailedWithException(exception, badValue, keyPath);
        }
    }



    /**
     * Returns the appropriate page title for the mode of this page (add/edit).
     *
     * @return the appropriate page title for the mode of this page (add/edit).
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Revise Field in Database Table";
    }


    //****************************  Generic Accessors Below Here  ****************************\\

    public SMVirtualTable databaseTable() {
        return (SMVirtualTable) calculatedColumn.table();
    }

    public String invalidExpressionError() {
        return invalidExpressionError;
    }
    public void setInvalidExpressionError(String message) {
        invalidExpressionError = message;
    }

    public String expressionResult() {
        return expressionResult;
    }
    public void setExpressionResult(String message) {
        expressionResult = message;
    }

    public String expressionResultType() {
        return expressionResultType;
    }
    public void setExpressionResultType(String message) {
        expressionResultType = message;
    }


}
