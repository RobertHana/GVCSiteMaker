// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.io.*;
import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.virtualtables.*;
import net.global_village.woextensions.*;
import net.global_village.woextensions.orderablelists.*;

import com.gvcsitemaker.components.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This page handles exporting the data from selected rows in a database
 * table.
 */
public class ExportTableData extends ColumnSelectAndOrderPage implements SMSecurePage
{
    // Static value accessors for WOBuilder
    public NSArray joinConditions = com.gvcsitemaker.core.support.DataAccessSearchCriterion.JOIN_CONDITIONS;
    public NSArray comparisonTypes = com.gvcsitemaker.core.support.DataAccessSearchCriterion.COMPARISON_TYPES;

    protected String exportFileName;
    protected boolean includeHeaderLine;
    protected boolean shouldDownloadData;
    protected NSData exportedData;
    protected String delimiter;

    /** @TypeInfo Column */
    protected NSArray searchableColumns;
    protected DataAccessSearchCriteria searchTerms;
    protected DataAccessSearchCriterion aCriterion;
    protected int criteriaIndex;
    protected Column aColumn;
    protected String aComparisonType;
    protected String aJoinCondition;
    protected Number totalRecordCount;
    protected Number selectedRecordCount;
    protected NSMutableDictionary validationFailures;
    protected String exportValidationErrorMessage;


    /**
     * Designated constructor.
     */
    public ExportTableData(WOContext context)
    {
        super(context);
        includeHeaderLine = true;
        searchTerms = new DataAccessSearchCriteria(null);
        setValidationFailures(new NSMutableDictionary());
        delimiter = DataAccessImportExport.COMMA_DELIMITER_NAME;
    }



    /**
     * Overridden to handle deleting unused criteria to avoid spurious validation failure messages.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);
        deleteUnusedSearchTerms();
    }



    /**
     * Handles export download, updating selected row count, and resets page state.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        // Updates selected row count with every R-R loop
        if (!hasValidationFailures())
        {
            deleteUnusedSearchTerms();
            setSelectedRecordCount(new Integer(table().objectsWithQualifier(exportRowQualifier()).count()));
            searchTerms().newSearchCriterion();
        }

        // Ensures there is always at least one search term
        if (!searchTerms().hasCriteria())
        {
            searchTerms().newSearchCriterion();
        }

        // performExport() only creates the data and sets this flag.  The download is initiated here.
        if (shouldDownloadData)
        {
            shouldDownloadData = false;

            // Set the headers and MIME type so the export will be downloaded, not displayed.
            aResponse.setHeader("attachment; filename=\"" + exportFileName() + "\"", Response.ContentDispositionHeaderKey);
            aResponse.setHeader("application/octet-stream", Response.ContentTypeHeaderKey);
            aResponse.setContentEncoding("UTF-8");
            aResponse.setContent(exportedData());
            setExportedData(null);
            // Don't call super here or this page will get appended to the export
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }

        validationFailures().removeAllObjects();
        setExportValidationErrorMessage(null);
    }



    /**
     * Action method to cancel export and return to the ConfigPage
     */
    public void reorder()
    {
        //AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteDataTablesExportPane", context());
        AjaxModalDialog.open(context(), "ReorderFieldsDialog");
    }



    /**
     * Action method to cancel export and return to the ConfigPage
     */
    public void cancelExport()
    {
        ConfigureDataTablesPane parent = (ConfigureDataTablesPane) parent();
        parent.tableViewComponentName = "AddEditDatabaseTable";
        table = null;
    }



    /**
     * Action method to create the exported data to download.  The download is actually done in appendToResponse.  If no columns have been selected for export, or if the criteria selecting the records is invalid, an error message is how and the export is not processed.
     */
    public WOComponent performExport()
    {
        /** require [has_table] databaseTable() != null;  [valid_delimiter] delimiters().objectForKey(delimiter()) != null;  **/

        if (hasValidationFailures())
        {
            setExportValidationErrorMessage("You need to fix the problems in the criteria selecting the records before exporting.");
        }
        else if (selectedColumns().count() == 0)
        {
            setExportValidationErrorMessage("You need to select at least one field before exporting.");
        }
        else
        {
            ByteArrayOutputStream exportStream = new ByteArrayOutputStream(table().rows().count() * 1024);

            try
            {
                deleteUnusedSearchTerms(); // Needed to produce valid qualifier
                table.exportTable(exportRowQualifier(), exportSortOrdering(), exportStream, this, orderedSelectedColumns(), (String) delimiters().objectForKey(
                        delimiter()), includeHeaderLine);
                setExportedData(new NSData(exportStream.toByteArray()));
                exportStream.close();
                //shouldDownloadData = true;
            }
            catch (IOException e)
            {
                throw new ExceptionConverter(e);
            }
        }

        return context().page();
    }



    /**
     * Adds a place in the UI for the users to add a single search term.
     *
     * @return the current page
     */
    public WOComponent addCriterion()
    {
        // As this is done in appendToResponse if all other terms are valid there is nothing to do here.
        return context().page();
    }



    /**
     * Removes from the search criteria those criteria that 1) is not the first row and the join condition is null ("not used..."), or 2) <em>is</em> the first row and none of the fields (column, comparison type, or value) are entered. Also removes any error messages that might have been generated for those rows.
     */
    public void deleteUnusedSearchTerms()
    {
        NSMutableArray searchValuesToRemove = new NSMutableArray();
        NSMutableArray errorMessagesToRemove = new NSMutableArray();
        int index = -1;
        Enumeration criterionEnumerator = searchTerms().criterionEnumerator();
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion criterion = (DataAccessSearchCriterion) criterionEnumerator.nextElement();
            index++;

            // Delete if either: 1) its not the first row and the join condition is null ("not used..."), or 2) it _is_ the first row and none of the fields (column, comparison type, or value) are entered
            if (((!criterion.isFirstCriterion()) && (criterion.joinCondition() == null))
                    || ((criterion.isFirstCriterion()) && ((criterion.column() == null) && (criterion.comparisonType() == null) && (criterion.value() == null))))
            {
                searchValuesToRemove.addObject(criterion);
                DebugOut.println(20, "Removing invalid criterion: " + criterion);
                errorMessagesToRemove.addObject(new Integer(index).toString());
            }
        }
        validationFailures().removeObjectsForKeys(errorMessagesToRemove);
        searchTerms().removeCriteriaInArray(searchValuesToRemove);
    }



    /**
     * Returns the qualifier built by the user's search criteria, or null if there are no search criteria.
     *
     * @return the qualifier built by the user's search criteria, or null if there are no search criteria.
     */
    public EOQualifier exportRowQualifier()
    {
        return searchTerms().searchQualifier(((SMSession) session()).currentUser());
    }



    /**
     * Returns an array of sort orders that controls the order of the rows in the export file.
     *
     * @return an array of sort orders that controls the order of the rows in the export file.
     */
    public NSArray exportSortOrdering()
    {
        // At present there is no option, export is always in Date Created order.
        return new NSArray(new EOSortOrdering(table().dateCreatedColumnName(), EOSortOrdering.CompareAscending));
    }



    /**
     * Pops up a modal dialog to allow the user to reorder the columns.
     */
    public WOActionResults openReorderFieldsDialog()
    {
        //AjaxModalDialog.open(context(), "ReorderFieldsModalDialog");

        ReorderModalDialog result = (ReorderModalDialog) pageWithName(ReorderModalDialog.class.getCanonicalName());
        result.setOrdered(orderedColumns().objects);

        return result;
    }


    /**
     * Pops up a modal dialog to allow the user to reorder the columns.
     */
    public void reorderFields()
    {
    }



    /**
     * Initializes the state of this page and the columns shown based on the columns contained in newDatabaseTable.  All system created columns *except* Record ID are added to the user created columns.
     *
     * @param newDatabaseTable the database table to initialize this page with
     */
    public void initializeWithDatabaseTable(VirtualTable newDatabaseTable)
    {
        /** require [valid_table] newDatabaseTable != null;  **/

        if (newDatabaseTable != table)
        {
            table = newDatabaseTable;

            // Prefetch for optimization.  This makes the page slow to load but makes searching and exporting quicker.
            EODatabaseContextAdditions.preloadRelationship(new NSArray(table()), "rows");
            EODatabaseContextAdditions.preloadRelationship(table().rows(), "fields");

            // Systems columns are available for export.  The system column RecordID is excluded as it is too troublesome to implement with the current architecture of the VirtualTable framework.
            NSMutableArray columns = new NSMutableArray(table.orderedUserColumns());
            columns.addObjectsFromArray(table.systemColumns());
            columns.removeObject(table.columnNamed(SMVirtualTable.RECORD_ID_COLUMN_NAME));
            setOrderedColumns(new OrderableList(columns));

            setSelectedColumns(new NSMutableArray(columns));

            // TODO Refactor Column.normalizeStringForColumnNames into string utils someplace
            setExportFileName(Column.normalizeStringForColumnNames(table.name()) + ".txt");

            // Initialize searching
            setSearchableColumns(((SMVirtualTable) table()).searchableColumns());
            setTotalRecordCount(new Integer(table().rows().count()));
            setSelectedRecordCount(totalRecordCount());
        }

        /** ensure [table_set] databaseTable() != null;  [exportFileName_set] exportFileName() != null;  **/
    }



    /**
     * String to pass into the dynamic body component for use as browser window title.
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Export Database Table Data";
    }



    /**
     * Overridden to handle validation errors caused by data input when searching.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if ((keyPath.endsWith(DataAccessSearchParameters.COLUMN_CRITERION_KEY)) || (keyPath.endsWith(DataAccessSearchParameters.COMPARISON_TYPE_CRITERION_KEY))
                || (keyPath.endsWith(DataAccessSearchParameters.VALUE_CRITERION_KEY)))
        {
            // Force the invalid value into the cirterion so that the user can see it
            takeValueForKeyPath(value, keyPath);

            // Register validation failure unless the line is marked as ignore
            if ((aCriterion().joinCondition() != null) || (criteriaIndex() == 0))
            {
                validationFailures().setObjectForKey(t.getMessage(), new Integer(criteriaIndex()).toString());
            }
        }
        else
        {
            super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Returns the validation failure message for the search search criterion at criteriaIndex(), or null if it is valid.
     *
     * @return  the validation failure message for the search search criterion at criteriaIndex(), or null if it is valid
     */
    public String validationFailureForCriterion()
    {
        return (String) validationFailures().objectForKey(new Integer(criteriaIndex()).toString());

    }



    /**
     * Returns <code>true</code> if there are any validation failures. 
     *
     * @return <code>true</code> if there are any validation failures
     */
    public boolean hasValidationFailures()
    {
        return validationFailures().count() > 0;
    }


    //****************************  Generic Accessors Below Here  ****************************\\

    public String delimiter()
    {
        return delimiter;
    }


    public void setDelimiter(String newDelimiter)
    {
        delimiter = newDelimiter;
    }


    public NSDictionary delimiters()
    {
        return DataAccessImportExport.delimiters;
    }


    public String exportFileName()
    {
        return exportFileName;
    }


    public void setExportFileName(String newExportFileName)
    {
        exportFileName = newExportFileName;
    }


    public boolean includeHeaderLine()
    {
        return includeHeaderLine;
    }


    public void setIncludeHeaderLine(boolean newIncludeHeaderLine)
    {
        includeHeaderLine = newIncludeHeaderLine;
    }


    public NSData exportedData()
    {
        return exportedData;
    }


    public void setExportedData(NSData newExportedData)
    {
        exportedData = newExportedData;
    }


    /** @TypeInfo Column */
    public NSArray searchableColumns()
    {
        return searchableColumns;
    }


    public void setSearchableColumns(NSArray newSearchableColumns)
    {
        searchableColumns = newSearchableColumns;
    }


    public NSArray searchCriteria()
    {
        return searchTerms().searchCriteria();
    }


    public DataAccessSearchCriteria searchTerms()
    {
        return searchTerms;
    }


    public DataAccessSearchCriterion aCriterion()
    {
        return aCriterion;
    }


    public void setACriterion(DataAccessSearchCriterion newCriterion)
    {
        aCriterion = newCriterion;
    }


    public int criteriaIndex()
    {
        return criteriaIndex;
    }


    public void setCriteriaIndex(int newCriteriaIndex)
    {
        criteriaIndex = newCriteriaIndex;
    }


    public Column aColumn()
    {
        return aColumn;
    }


    public void setAColumn(Column newColumn)
    {
        aColumn = newColumn;
    }


    public String aComparisonType()
    {
        return aComparisonType;
    }


    public void setAComparisonType(String newComparisonType)
    {
        aComparisonType = newComparisonType;
    }


    public String aJoinCondition()
    {
        return aJoinCondition;
    }


    public void setAJoinCondition(String newJoinCondition)
    {
        aJoinCondition = newJoinCondition;
    }


    public Number totalRecordCount()
    {
        return totalRecordCount;
    }


    public void setTotalRecordCount(Number newTotalRecordCount)
    {
        totalRecordCount = newTotalRecordCount;
    }


    public Number selectedRecordCount()
    {
        return selectedRecordCount;
    }


    public void setSelectedRecordCount(Number newSelectedRecordCount)
    {
        selectedRecordCount = newSelectedRecordCount;
    }


    public NSMutableDictionary validationFailures()
    {
        return validationFailures;
    }


    public void setValidationFailures(NSMutableDictionary newValidationFailures)
    {
        validationFailures = newValidationFailures;
    }


    public String exportValidationErrorMessage()
    {
        return exportValidationErrorMessage;
    }


    public void setExportValidationErrorMessage(String newExportValidationErrorMessage)
    {
        exportValidationErrorMessage = newExportValidationErrorMessage;
    }



    public String columnIncludedCheckBoxName()
    {
        return "includeColumn" + aColumn().name() + "CheckBox";
    }



}
