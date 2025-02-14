// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.componentprimitives;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.databasetables.SMVirtualTable;
import com.gvcsitemaker.core.support.DataAccessImportExport;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSValidation;

import net.global_village.foundation.DelimitedStringTokenizer;
import net.global_village.foundation.ExceptionConverter;
import net.global_village.foundation.StringAdditions;
import net.global_village.virtualtables.Column;
import net.global_village.virtualtables.ColumnType;


/**
 * DataAccessImportMode implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessImportMode, 
 * the Import Records view of a Data Access Section.
 */
public class DataAccessImportMode extends DataAccessMode {

    protected static final boolean SKIP_FIRST_LINE = true;
    protected static final NSArray TRUE_FALSE = new NSArray(new Object[] {"true", "false"});
    
    
    // Main import values
    protected String delimiter;
    protected boolean retainExistingData;
    protected NSData uploadedFile;
    protected String uploadedFileName;
  
    // For internal use while importing
    protected NSMutableArray columnsToImport;
    
    // UI support
    protected boolean isImporting;  
    public String anErrorMessage = null;
    public NSMutableArray errorMessages = new NSMutableArray();
    public int numberOfRowsImported = 0;
    
    
    /**
     * Designated constructor.
     */
    public DataAccessImportMode(WOContext context) {
        super(context);
        
        retainExistingData = true;
        delimiter = DataAccessImportExport.COMMA_DELIMITER_NAME;
        isImporting = true;
        columnsToImport = new NSMutableArray();
    }



    /**
     * Action method to cancel the additions of a new row.  Discards the row.
     */
    public WOComponent cancelChanges()
    {
        editingContext().revert();
        return redirectToUrl(SMDataAccessActionURLFactory.cancelImportUrl(dataAccessParameters()));
    }



    /**
     * Action method to save the new row and discard it after a successful save.
     */
    public WOComponent importRecords()
    {
        validateParameters(); 
        
        if ( ! hasValidationFailures())
        {
            validateAndCreateColumnsToImport();
        }

        if ( ! hasValidationFailures())
        {
            validateImportFileContents();
        }
        
        if (! hasValidationFailures())
        {
            try
            {
                ((SMVirtualTable)databaseTable()).importTable(uploadedFile().stream(),
                        null,
                        columnsToImport(),
                        DataAccessImportExport.internalDelimiter(delimiter()),
                        SKIP_FIRST_LINE,
                        ! retainExistingData(),
                        ((SMSession)session()).currentUser());
                editingContext().saveChanges();
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }

        setIsImporting(false);  // Show confirmation view
        return context().page();
    }

    
    
    /**
     * Validates the delimiter, retain data, and uploaded file.
     *
     */
    public void validateParameters() 
    {
        // These are not neccessarily set automatically when the custom template is used so we always
        // get the value manually
        String delimiterFormValue = (String)context().request().formValueForKey("DataAccess_ImportDelimiter");
        DebugOut.println(20, "Import delimiter: " + delimiterFormValue);
        if (StringAdditions.isEmpty(delimiterFormValue) || (delimiters().objectForKey(delimiterFormValue) == null))
        {
            recordValidationError("The import file delimiter " + 
                    (StringAdditions.isEmpty(delimiterFormValue) ? 
                            "is missing." : 
                                "'" + delimiterFormValue + "' must be either tab or comma."));        
        }
        else
        {
            setDelimiter(delimiterFormValue);
        }
        
        String retainDataFormValue = (String)context().request().formValueForKey("DataAccess_ImportRetainData");
        DebugOut.println(20, "Retain data: " + retainDataFormValue);
        if (StringAdditions.isEmpty(retainDataFormValue) || ( ! TRUE_FALSE.containsObject(retainDataFormValue)))
        {
            recordValidationError("The retain data value '" + retainDataFormValue + "' must be either true or false.");        
        }
        else
        {
            setRetainExistingData(new java.lang.Boolean(retainDataFormValue).booleanValue());
        }
        
        if ((uploadedFile() == null) || (uploadedFile().length() == 0) ||(StringAdditions.isEmpty(uploadedFileName())))
        {
            recordValidationError("You did not select a file to be imported.");        
        }
        DebugOut.println(20, "Importing file : " + uploadedFile());

    }
    
    
    
    /**
     * Validates the first row of the import file as a list of names of the columns to import.  Also
     * creates the columnsToImport() list.
     *
     */
    public void validateAndCreateColumnsToImport()
    {
        columnsToImport.removeAllObjects();
        BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile().stream()));
        String headerLine;
        
        try {
            headerLine = reader.readLine();
        } catch (IOException e) {
            // I can't see how this would happen...
            recordValidationError("Failed reading file: " + e);
            return;
        }
        
        if (headerLine != null)
        {
            DebugOut.println(10, "Import columns header line: " + headerLine);
            DebugOut.println(30, "Delimiting with: " + delimiter());
            int columnIndex = 0;
            NSMutableArray parsedColumnNames = new NSMutableArray();
            
            String actualDelimiter = DataAccessImportExport.internalDelimiter(delimiter);
            DelimitedStringTokenizer tokenizer = new DelimitedStringTokenizer(headerLine, actualDelimiter, "'\"", null);
            while (tokenizer.hasMoreTokens())
            {
                columnIndex++;
                String columnName = tokenizer.nextToken().trim();
                DebugOut.println(10, "Column " + columnIndex + " parsed as : " + columnName);
                if (StringAdditions.isEmpty(columnName))
                {
                    recordValidationError("Header row has no name for column " + columnIndex);
                }
                else if (parsedColumnNames.containsObject(columnName))
                {
                    recordValidationError("Duplicate column name '" + columnName + "' in column " + columnIndex);
                }
                else if ( ! databaseTable().hasColumnNamed(columnName))
                {
                    recordValidationError("This database table does not have a field named '" + columnName + "' as found in column " + columnIndex);
                }
                else
                {
                    Column aColumn = databaseTable().columnNamed(columnName);
                    if ( ! ColumnType.validImportTypes().containsObject(aColumn.type().name()))
                    {
                        recordValidationError("The field '" + columnName + "' as found in column " + columnIndex + " is a " + aColumn.type().textDescription() + " which is not importable.");
                    }
                    else
                    {
                        parsedColumnNames.addObject(columnName);
                        columnsToImport.addObject(aColumn);
                    }
                }
            }
        }

    }
    
    
    
    /**
     * Validates the contents of the import file.
     *
     */
    public void validateImportFileContents()
    {
        try
        {
            // Validating the import file only makes sense if we have one and if there are fields to import.
            if ( ! hasValidationFailures())
            {
                numberOfRowsImported = DataAccessImportExport.validateImportFile(uploadedFile().stream(),
                        null,
                        columnsToImport(),
                        ((SMVirtualTable)databaseTable()).importFormatters(),
                        DataAccessImportExport.internalDelimiter(delimiter()),
                        retainExistingData(),
                        SKIP_FIRST_LINE);
            }
        }
        catch (NSValidation.ValidationException ve)
        {
            // Validation failures are thrown as an exception
            if (ve.additionalExceptions() != null)
            {
                NSArray validationErrors = (NSArray) ve.additionalExceptions().valueForKey("message");
                for (int i = 0; i < validationErrors.count(); i++)
                {
                    recordValidationError((String) validationErrors.objectAtIndex(i));
                }
            }
            else
            {
                recordValidationError(ve.getMessage());
            }
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
    }
    
    
    
    /**
     * Overriden to refer to errorMessages().
     * 
     * @return
     */
    public boolean hasValidationFailures()
    {
        return errorMessages.count() > 0;
    }
    
    
    /**
     * Convenience method to record a validation failure for display on the confirmation page.
     * 
     * @param message text of the failure message
     */
    public void recordValidationError(String message)
    {
        errorMessages.addObject(message);
    }
    
    
    
    //************* Generic Accessors and Mutators below here *****************
 
    public boolean retainExistingData() {
        return retainExistingData;
    }
    public void setRetainExistingData(boolean newRetainExistingData) {
        retainExistingData = newRetainExistingData;
    }

    public NSData uploadedFile() {
        return uploadedFile;
    }
    public void setUploadedFile(NSData newUploadedFile) {
        uploadedFile = newUploadedFile;
    }

    public String uploadedFileName() {
        return uploadedFileName;
    }
    public void setUploadedFileName(String newUploadedFileName) {
        uploadedFileName = newUploadedFileName;
    }

    public String delimiter() {
        return delimiter;
    }
    public void setDelimiter(String newDelimiter) {
        delimiter = newDelimiter;
    }

    public NSDictionary delimiters()
    {
        return DataAccessImportExport.delimiters;
    }

    public boolean isImporting() {
        return isImporting;
    }
    public void setIsImporting(boolean newIsImporting) {
        isImporting = newIsImporting;
    }

    public boolean didImportSucceed() {
        return (! isImporting()) && (errorMessages.count() == 0);
    }

    public boolean didImportFail() {
        return (! isImporting()) && (errorMessages.count() > 0);
    }

    public NSArray columnsToImport() {
        return columnsToImport;
    }
}
