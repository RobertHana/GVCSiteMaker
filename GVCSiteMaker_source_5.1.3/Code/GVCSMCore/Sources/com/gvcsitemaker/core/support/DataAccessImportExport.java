package com.gvcsitemaker.core.support;

import java.io.*;
import java.text.*;
import java.util.*;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.webobjects.foundation.*;

/**
 * Support code for use importing to and exporting fromt DataAccess tables.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DataAccessImportExport {

    public static final String COMMA_DELIMITER_NAME = "comma";
    public static final String COMMA_DELIMITER = ",";
    public static final String TAB_DELIMITER_NAME = "tab";
    public static final String TAB_DELIMITER = "\t";
    public static final NSDictionary delimiters = new NSDictionary(
            new String[] {COMMA_DELIMITER,      TAB_DELIMITER },
            new String[] {COMMA_DELIMITER_NAME, TAB_DELIMITER_NAME});
    
    
    /**
     * Translates between the UI representation of a delimiter (e.g. "tab") and the actual characters
     * used as a delimiter, e.g. the tab character (\t).
     * 
     * @param delimiter the UI representation of a delimiter to translated into the actual delimiter
     * @return the actual characters that the name represents
     */
    static public String internalDelimiter(String delimiter)
    {
        /** require [valid_delimiter] delimiters.objectForKey(delimiter) != null;   **/
        return (String)delimiters.objectForKey(delimiter);
        /** ensure [valid_result] Result != null;      **/
    }

    
    
    /**
     * Checks that, if the user wants to replace the existing data, that we can actually delete them.  Fields that lookup value in this table, for example, make that data undeletable.
     *
     * @param table the table for which we validate lookup values
     * @return an array of error messages
     */
    static protected NSArray validateCanDeleteValues(VirtualTable table)
    {
        /** require [valid_param] table != null; **/

        NSMutableSet deletingErrorMessages = new NSMutableSet();

        if (table.hasReferringTables())
        {
            Enumeration rowEnumerator = table.rows().objectEnumerator();
            while (rowEnumerator.hasMoreElements())
            {
                VirtualRow row = (VirtualRow)rowEnumerator.nextElement();

                Enumeration fieldEnumerator = row.fields().objectEnumerator();
                while (fieldEnumerator.hasMoreElements())
                {
                    VirtualField field = (VirtualField)fieldEnumerator.nextElement();
                    if (field.hasReferringFields())
                    {
                        deletingErrorMessages.addObject("Cannot delete the data from column " + field.column().name() + " because one or more values are being used as a lookup value.");
                    }
                    
                    if (SMVirtualTable.isLookupListDefaultValue(field))
                    {
                        deletingErrorMessages.addObject("Cannot delete the data from column " + field.column().name() + " because one or more values are being used as default for a lookup field.");
                    }
                }
            }
        }

        return deletingErrorMessages.allObjects();
    }


    /**
     * Checks that, if the user wants to replace the existing data, that we can actually delete them.  Fields that lookup value in this table, for example, make that data undeletable.
     *
     * @param table the table for which we validate sizes
     * @param columnsToImport the columns we are importing
     * @param retainExistingData should we retain the exsting data?
     * @param linesOfData the number of lines in the import file
     * @return an array of error messages
     */
    static public NSArray validateSize(VirtualTable table, NSArray columnsToImport, boolean retainExistingData, int linesOfData)
    {
        /** require [valid_table_param] table != null; [valid_columnsToImport_param] columnsToImport != null; **/

        NSMutableArray sizeErrorMessages = new NSMutableArray();

        int maxFieldsInImport = SMApplication.appProperties().intPropertyForKey("DataAccessMaximumNumberOfFieldsInImport");
        int numberOfFieldsInFile = linesOfData * columnsToImport.count();
        if (numberOfFieldsInFile > maxFieldsInImport)
        {
            sizeErrorMessages.addObject("This file exceeds the maximum allowed file size of " + maxFieldsInImport + " fields (rows x columns).");
        }

        int maxFieldsInTable = SMApplication.appProperties().intPropertyForKey("DataAccessMaximumNumberOfFieldsInTable");
        int tableFields = retainExistingData ? table.rows().count() * table.columns().count() : 0;
        // Don't forget to add the system columsn to the calculation
        if (numberOfFieldsInFile + (linesOfData * 5) + tableFields > maxFieldsInTable)
        {
            sizeErrorMessages.addObject("Importing this file will exceed the maximum allowed table size of " + maxFieldsInTable + " fields (rows x columns).");
        }

        return sizeErrorMessages;
    }


    /**
     * Validates that the import file given by <code>stream</code> is valid for import.  By neccessity, this does a lot of the things that an actual import does... Returns the number of rows that importing the given stream would add to the table.
     *
     * @param stream the stream of characters that will make up the new rows.  The stream is <em>not</em> closed at the end of this method - you'll have to do it yourself
     * @param sender an arbitrary object that is used as a parameter to Column's valueForImportedValue() method
     * @param columnsToImport an ordered list of columns that we will import.  The first item in this array cooresponds to the first field in the data file, the second item to the second field, and so on
     * @param formatters mapping of column names to java.text.Formats, used to format the field strings into objects of the correct type. You'll normally only need this for date/times and possibly for custom column types
     * @param delimiter the character to use as the delimiter
     * @param skipFirstLine should we skip the first line? Useful if, for example, the first line contains the column names
     * @return the number of rows that importing the given stream would add to the table
     * @exception IOException occurs when an IO exception occurs on the stream
     * @exception NSValidation.ValidationException if the import file fails validation.
     */
    static public int validateImportFile(InputStream stream,
                                  Object sender,
                                  NSArray columnsToImport,
                                  NSDictionary formatters,
                                  String delimiter,
                                  boolean retainExistingData,
                                  boolean skipFirstLine)
        throws IOException, NSValidation.ValidationException
    {
        /** require
        [valid_stream_param] stream != null;
        [valid_columnsToImport_param] columnsToImport != null;
        [has_columns_to_import] columnsToImport.count() > 0;
        /# [columns_refer_to_same_table] forall i : {1 .. columnsToImport.count() - 1} # ((Column)columnsToImport.objectAtIndex(i)).table().equals(((Column)columnsToImport.objectAtIndex(i-1)).table()); #/
        /# [columns_are_importable] forall i : {0 .. columnsToImport.count() - 1} # net.global_village.virtualtables.ColumnType.validImportExportTypes().containsObject(((Column)columnsToImport.objectAtIndex(i)).type().name()); #/
        [valid_formatters_param] formatters != null;
        [valid_delimiter_param] delimiter != null; **/

        NSMutableArray validationErrors = new NSMutableArray();
        VirtualTable table = (VirtualTable)((Column)columnsToImport.objectAtIndex(0)).table();

        // First make sure that, if we are to delete the existing rows, that they aren't being used as a lookup in another table
        if ( ! retainExistingData)
        {
            addExceptions(validationErrors, validateCanDeleteValues(table));
        }

        // Next, validate each field is valid for its column type
        boolean shouldSkipNextLine = skipFirstLine;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        int numberOfLines = 0;
        String oneLine;
        while ((oneLine = reader.readLine()) != null)
        {
            numberOfLines++;

            if (shouldSkipNextLine)
            {
                shouldSkipNextLine = false;
            }
            else
            {
                DelimitedStringTokenizer tokenizer = new DelimitedStringTokenizer(oneLine, delimiter, "'\"", null);

                for (int i = 0; i < columnsToImport.count(); i++)
                {
                    Column column = (Column)columnsToImport.objectAtIndex(i);

                    if ( ! tokenizer.hasMoreTokens())
                    {
                        addException(validationErrors, "There were less columns in the file than were chosen for import on line " + numberOfLines + ".");
                        break;
                    }
                    Object dataElement = tokenizer.nextToken();

                    // If the dataElement is empty (""), we don't need to worry about trying to format it
                    if ( ! dataElement.equals(""))
                    {
                        try
                        {
                            Format formatter = (Format)formatters.objectForKey(column.name());
                            if (formatter != null)
                            {
                                dataElement = formatter.parseObject((String)dataElement);
                            }

                            // dataElement can be null after formatting an empty string ""
                            if (dataElement != null)
                            {
                                dataElement = column.valueForImportedValue(sender, dataElement);
                            }
                        }
                        catch (ParseException e)
                        {
                            addException(validationErrors, "Could not format the value '" + dataElement + "' for column '" + column.name() + "' on line " + numberOfLines + ".");
                        }
                        catch (EOFValidationException e)
                        {
                            addException(validationErrors, e.getLocalizedMessage());
                        }
                    }
                }

                if (tokenizer.hasMoreTokens())
                {
                    addException(validationErrors, "There were more columns in the file than were chosen for import on line " + numberOfLines + ".");
                }
            }
        }

        // Make sure we have at least 1 row to import
        if (((skipFirstLine) && (numberOfLines < 2)) || (( ! skipFirstLine) && (numberOfLines < 1)))
        {
            addException(validationErrors, "The file has no rows to import");
        }

        // Finally, make sure the import isn't too large
        addExceptions(validationErrors, validateSize(table, columnsToImport, retainExistingData, skipFirstLine ? numberOfLines - 1 : numberOfLines));

        if (validationErrors.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(validationErrors);
        }

        return skipFirstLine ? numberOfLines - 1 : numberOfLines;
    }
    
    
    
    /**
     * Adds message to the list of validation exceptions.
     * 
     * @param exceptions list of validation exceptions
     * @param message message of exception to add
     */
    static protected void addException(NSMutableArray exceptions, String message) 
    {
        /** require [valid_exceptions] exceptions != null; [valid_message] message != null;   **/
        exceptions.addObject(new NSValidation.ValidationException(message));
    }

    
    
    /**
     * Adds multiple messages to the list of validation exceptions.
     * 
     * @param exceptions list of validation exceptions
     * @param messages list of messages to make exceptions from
     */
    static protected void addExceptions(NSMutableArray exceptions, NSArray messages) 
    {
        /** require [valid_exceptions] exceptions != null; [valid_messages] messages != null;   **/

        for (int i = 0; i < messages.count(); i++)
        {
            exceptions.addObject(new NSValidation.ValidationException((String)messages.objectAtIndex(i)));
        }
    }

}
