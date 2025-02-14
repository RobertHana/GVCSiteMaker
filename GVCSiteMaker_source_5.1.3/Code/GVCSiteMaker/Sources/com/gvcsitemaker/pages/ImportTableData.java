// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;
import net.global_village.woextensions.orderablelists.*;

import com.gvcsitemaker.components.ReorderModalDialog;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.ajax.*;


public class ImportTableData extends ColumnSelectAndOrderPage implements ValidationMessageStore, SMSecurePage
{
    public String tableViewComponentName;

    protected NSMutableDictionary validationFailures = new NSMutableDictionary();
    protected static final String NO_FILE_VALIDATION_KEY = "noFile";
    protected static final String NO_COLUMNS_VALIDATION_KEY = "noColumns";
    protected static final String INVALID_FILE_VALIDATION_KEY = "invalidFile";

    protected boolean skipFirstLine;
    protected boolean retainExistingData;
    protected NSData uploadedFile;
    protected String uploadedFileName;
    protected String delimiter;

    public String importDialogMessage = "";


    public ImportTableData(WOContext context)
    {
        super(context);
        setSkipFirstLine(true);
        setRetainExistingData(true);
        delimiter = DataAccessImportExport.COMMA_DELIMITER_NAME;
    }



    /**
     * Overridden to clear validation failures.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        clearValidationFailures();
        /** ensure [no_validation_failures] ! hasValidationFailures(); **/
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
     * Action method to cancel export and return to the ConfigPage
     */
    public void reorder()
    {
        AjaxModalDialog.open(context(), "ReorderFieldsDialog");
    }



    /**
     * Pops up a modal dialog to allow the user to reorder the columns.
     */
    public void reorderFields()
    {
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
     * @param key a unique key associated with this failure
     * @return the appropriate registered validation error or null if no validation error was registered
     */
    public String validationFailureForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/
        return (String) validationFailures.objectForKey(aKey);
    }



    /**
     * Returns true if any validation errors have been registered.
     *
     * @return true if any validation errors have been registered
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
        /** ensure [did_clear] ! hasValidationFailures(); **/
    }



    /**
     * Validates the input.
     */
    public void validateUserInput()
    {
        if ((uploadedFile == null) || (uploadedFile.equals(NSData.EmptyData)) || (uploadedFileName == null) || (uploadedFileName.equals("")))
        {
            registerValidationFailureForKey("You must select a file for import.", NO_FILE_VALIDATION_KEY);
        }

        if (selectedColumns().count() == 0)
        {
            registerValidationFailureForKey("You must select at least one field to import.", NO_COLUMNS_VALIDATION_KEY);
        }
    }



    /**
     * Imports the data.
     *
     * @return <code>this</code>
     */
    public void validateImport()
    {
        validateUserInput();
        int numberOfRowsImported = 0;

        try
        {
            // Validating the import file only makes sense if we have one and if there are fields to import.
            if (!hasValidationFailures())
            {
                numberOfRowsImported = DataAccessImportExport.validateImportFile(uploadedFile().stream(), null, orderedSelectedColumns(),
                        ((SMVirtualTable) table()).importFormatters(), DataAccessImportExport.internalDelimiter(delimiter()), retainExistingData(),
                        skipFirstLine());
            }
        }
        catch (NSValidation.ValidationException ve)
        {
            if (ve.additionalExceptions() != null)
            {
                NSArray validationErrors = (NSArray) ve.additionalExceptions().valueForKey("message");
                registerValidationFailureForKey(validationErrors.componentsJoinedByString("<br>\n"), INVALID_FILE_VALIDATION_KEY);
            }
            else
            {
                registerValidationFailureForKey(ve.getMessage(), INVALID_FILE_VALIDATION_KEY);
            }
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }

        if (!hasValidationFailures())
        {
            if (retainExistingData())
            {
                importDialogMessage = "file '" + uploadedFileName + "', which will add " + numberOfRowsImported + " records to the table";
            }
            else
            {
                importDialogMessage = "file '" + uploadedFileName + "', which will delete " + table().rows().count() + " records and replace them with "
                        + numberOfRowsImported + " records for table";
            }
            AjaxModalDialog.open(context(), "ConfirmImportModalDialog");
        }
    }



    /**
     * Initializes the state of this page and the columns shown based on the columns contained in newDatabaseTable.
     *
     * @param newDatabaseTable the database table to initialize this page with
     */
    public void initializeWithDatabaseTable(VirtualTable newDatabaseTable)
    {
        /** require [valid_table] newDatabaseTable != null; **/

        if (newDatabaseTable != table)
        {
            table = newDatabaseTable;
            setOrderedColumns(new OrderableList(table.importableColumns()));
            setSelectedColumns(new NSMutableArray(orderedColumns().ordered()));
        }

        /** ensure [table_set] databaseTable() != null; **/
    }



    /**
     * Initializes the state of this page and the columns shown based on the columns contained in newDatabaseTable.  By default system created columns are not available.
     *
     * @param newDatabaseTable the database table to initialize this page with
     */
    public void cancelImport()
    {
        ConfigureDataTablesPane parent = (ConfigureDataTablesPane)parent();
        parent.tableViewComponentName = "ConfigureDataTablesPane";
        table = null;
    }



    /**
     * Used by the modal dialog to do the import.
     */
    public void performImport()
    {
        try
        {
            ((SMVirtualTable) table()).importTable(uploadedFile().stream(), null, orderedSelectedColumns(), DataAccessImportExport
                    .internalDelimiter(delimiter()), skipFirstLine(), !retainExistingData(), ((SMSession) session()).currentUser());
            table().editingContext().saveChanges();

            AjaxModalDialog.open(context(), "ImportSuccessModalDialog");
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * String to pass into the dynamic body component for use as browser window title.
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Import Database Table Data";
        /** ensure [valid_result] Result != null; **/
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


    public boolean skipFirstLine()
    {
        return skipFirstLine;
    }


    public void setSkipFirstLine(boolean newSkipFirstLine)
    {
        skipFirstLine = newSkipFirstLine;
    }


    public boolean retainExistingData()
    {
        return retainExistingData;
    }


    public void setRetainExistingData(boolean newRetainExistingData)
    {
        retainExistingData = newRetainExistingData;
    }


    public NSData uploadedFile()
    {
        return uploadedFile;
    }


    public void setUploadedFile(NSData newUploadedFile)
    {
        uploadedFile = newUploadedFile;
    }


    public String uploadedFileName()
    {
        return uploadedFileName;
    }


    public void setUploadedFileName(String newUploadedFileName)
    {
        uploadedFileName = newUploadedFileName;
    }


    public NSDictionary validationFailures()
    {
        return validationFailures;
    }


    public boolean hasNoFileValidationError()
    {
        return validationFailureForKey(NO_FILE_VALIDATION_KEY) != null;
    }


    public boolean hasNoColumnsValidationError()
    {
        return validationFailureForKey(NO_COLUMNS_VALIDATION_KEY) != null;
    }


    public boolean isInvalidFileValidationError()
    {
        return validationFailureForKey(INVALID_FILE_VALIDATION_KEY) != null;
    }

}
