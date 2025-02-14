package com.gvcsitemaker.core.databasetables;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;
import net.global_village.foundation.Collection;
import net.global_village.virtualtables.*;


/**
 * Implementation of SMVirtualTable common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class SMVirtualTable extends _SMVirtualTable implements SMTable
{
    public static final String RECORD_ID_COLUMN_NAME = "RecordID";
    public static final String NORMALIZED_RECORD_ID_COLUMN_NAME = "virtualRowID";
    public static final String DataTablePackageExport = "DataTablePackageExport";
    /** The PK of the user to use during import. Set by importTable(). */
    protected Object importUserPK;

    protected boolean shouldIncludeRowsInPackage;


    /**
     * Messages all DataAccess PageComponents using this Table to synchronize themselves with this table.  This method should be called after columns are added, removed, or updated.
     *
     * @param databaseTable - the table which has updated to its columns
     */
    public static void updateDataAccessSections(Table databaseTable)
    {
        /** require [valid_param]  databaseTable != null; **/
        Enumeration sectionEnumerator = ((SMVirtualTable)databaseTable).sectionsUsedIn().objectEnumerator();
        while (sectionEnumerator.hasMoreElements())
        {
            DataAccess aDataAccessComponent = (DataAccess) ((Section) sectionEnumerator.nextElement()).component();
            aDataAccessComponent.synchronizeWithTable();
        }
    }



    /**
     * Returns a user presentable error message for when the passed field cannot be deleted.  An example message: This field cannot be deleted as it is being used by the Rank field in the Rankings table, and the Length field in the Fish table.
     *
     * @return a user presentable error message for when the passed field cannot be deleted
     */
    public static String deletionRefusedMessage(VirtualField field)
    {
        /** require [valid_param]  field != null;  **/
        JassAdditions.pre("SMVirtualTable",
                          "deletionRefusedMessage(VirtualField) [hasReferringFields]",
                          field.hasReferringFields() || isLookupListDefaultValue(field));

        // Handle references by lookup fields.
        NSMutableArray references = new NSMutableArray();
        NSSet referringColumns = new NSSet((NSArray)field.referringFields().valueForKey("column"));
        Enumeration referringColumnsEnumerator = referringColumns.objectEnumerator();
        while (referringColumnsEnumerator.hasMoreElements())
        {
            VirtualColumn referringColumn = (VirtualColumn) referringColumnsEnumerator.nextElement();
            String reference = "the " + referringColumn.name() + " field in the " + referringColumn.table().name() + " table";
            references.addObject(reference);
        }

        // Handle GVC.SiteMaker special case of reference as the default value for a lookup field.
        Enumeration defaultValuesEnumerator = EOEditingContextAdditions.
            objectsWithQualifier(field.editingContext(), "DataAccessLookupColumn", defaultsToThisField(field)).objectEnumerator();
         while (defaultValuesEnumerator.hasMoreElements())
        {
            DataAccessLookupColumn lookupColumn = (DataAccessLookupColumn) defaultValuesEnumerator.nextElement();
            String reference = "as the default value for " + lookupColumn.column().name() + " in the " +
                lookupColumn.sectionUsedIn().name() + " section";
            references.addObject(reference);
        }

        return "This field cannot be deleted as it is being used by " + references.componentsJoinedByString(", and ") + ".";
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns true if the passed field is used as the default in a DataAccessLookupColumn. It would be
     * nice if this method was not here, but there is no better place to put it without making
     * GVC.SiteMaker use subclasses of all the field types in VirtualTables.
     *
     * @param field the VirtualField that might be used as a default value
     * @return true if the passed field is used as the default in a DataAccessLookupColumn
     */
    public static boolean isLookupListDefaultValue(VirtualField field)
    {
        /** require [valid_param]  field != null;  **/
         return EOEditingContextAdditions.countOfObjectsForEntityNamed(field.editingContext(),
                                                                      "DataAccessLookupColumn",
                                                                      defaultsToThisField(field)).intValue() > 0;
    }



    /**
     * Returns a qualifier for DataAccessLookupColumn to return columns that use field as a default value.
     *
     * @param field the VirtualField that might be used as a default value
     * @return qualifier for defaultField = <field>
     */
    protected static EOQualifier defaultsToThisField(VirtualField field)
    {
        return EOQualifier.qualifierWithQualifierFormat("defaultField = %@", new NSArray(field));
    }



    /**
     * Factory method to create new instances of SMVirtualTable.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Website or a subclass.
     */
    public static SMVirtualTable newSMVirtualTable()
    {
        return (SMVirtualTable) SMEOUtils.newInstanceOf("SMVirtualTable");
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);

        ColumnType recordIDColumnType = ColumnType.columnTypeWithName(ec, DataAccessRecordIDColumn.ColumnTypeName);
        Column recordID = newColumn(RECORD_ID_COLUMN_NAME,  recordIDColumnType);
        recordID.setIsSystemColumn(net.global_village.foundation.GVCBoolean.yes());

        ColumnType userColumnType = ColumnType.columnTypeWithName(ec, VirtualUserField.ColumnTypeName);

        Column modifiedBy = newColumn("Modified By", userColumnType);
        modifiedBy.setIsSystemColumn(net.global_village.foundation.GVCBoolean.yes());

        Column createdBy = newColumn("Created By",  userColumnType);
        createdBy.setIsSystemColumn(net.global_village.foundation.GVCBoolean.yes());

        /** ensure
        [has_createdBy_column] hasColumnNamed("Created By");
        [has_modifiedBy_column] hasColumnNamed("Modified By");
        [has_recordID_column] hasColumnNamed("RecordID");
        [createdBy_is_system_column] columnNamed("Created By").isSystemColumn().booleanValue();
        [modifiedBy_is_system_column] columnNamed("Modified By").isSystemColumn().booleanValue();
        [recordID_is_system_column] columnNamed("RecordID").isSystemColumn().booleanValue();
        **/
    }



    /**
     * Overridden to use 'Date Created' for this column name.
     */
    public String dateCreatedColumnName()
    {
        return "Date Created";
    }



    /**
     * Overridden to use 'Date Modified' for this column name.
     */
    public String dateModifiedColumnName()
    {
        return "Date Modified";
    }



    /**
     * Extends VirtualTable to disallow special column names (all start with DataAccess_) as these column names conflict with bindings used by GVC.SiteMaker.
     *
     * @return the column names that are invalid due to conflict with method or field names on VirtualRow
     */
    protected NSMutableSet defaultInvalidColumnNames()
    {
        NSMutableSet mutableSet = super.defaultInvalidColumnNames();

        // Handle special cases that do not start with DataAccess_
        mutableSet.addObjectsFromArray(SMApplication.appProperties().arrayPropertyForKey("DataAccessReservedNames"));

        return mutableSet;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overridden to trim whitespace from around name.
     */
    public void setName(String newName)
    {
        super.setName(newName == null ? null : newName.trim());
    }



    /**
     * Returns the Website that this Table belongs to.  This is work around for the actual relationship being a to-many to keep EOModeler happy.  It reality this is a to-one relationship.
     *
     * @return the Website that this Table belongs to.
     */
    public Website website()
    {
        /** require [has_single_website] websites().count() <= 1;  **/

        Website website = null;
        if (websites().count() > 0)
        {
            website = (Website) websites().lastObject();
        }

        return website;

        /** ensure [result_linked] (Result == null) || Result.databaseTables().containsObject(this);  **/
    }



    /**
     * Returns the column from this table whose normalized name matches columnName, ignoring the case or null if there is no such column.
     *
     * @param columnName - the name to check
     * @return the column from this table whose name matches columnName, ignoring the case or null if there is no such column.
     */
    public Column columnNamedIgnoreCase(String columnName)
    {
        /** require [valid_param] columnName != null; **/
        Column matchingColumn = null;

        String normalizedName = Column.normalizeStringForColumnNames(columnName);

        Enumeration columnMappingEnumerator = columnNameMapping().keyEnumerator();
        while (columnMappingEnumerator.hasMoreElements())
        {
            String aColumnName = (String) columnMappingEnumerator.nextElement();
            if (normalizedName.equalsIgnoreCase(aColumnName))
            {
                matchingColumn = (Column) columnNameMapping().objectForKey(aColumnName);
                break;
            }
        }

        return matchingColumn;
    }



    /**
     * Returns true if this table has a column with this normalized name, ignoring the case.  This was done to prevent having two columns with a name differing only by case to avoid user confusion.
     *
     * @param columnName - the name to check
     * @return true if this table has a column with this normalized name, ignoring the case.
     */
    public boolean hasColmnNamedIgnoreCase(String columnName)
    {
        /** require [valid_param] columnName != null; **/
        return columnNamedIgnoreCase(columnName) != null;
    }



    /**
     * Overridden to allow only  A-Z, a-z, 0-9, _ (underscore), and spaces and to disallow names starting with DataAccess_.  Returns <code>true</code> if the given name is a valid column name for this table, <code>false</code> otherwise.
     *
     * @param name the name to check
     * @return <code>true</code> if the given name is a valid column name, <code>false</code> otherwise
     */
    public boolean isValidColumnName(String name)
    {
        /** require [valid_param] name != null; **/
        boolean isValidColumnName = super.isValidColumnName(name);

        // Handle special prefixes used with field names in WOD file
        NSArray reservedPrefixes = SMApplication.appProperties().arrayPropertyForKey("DataAccessReservedPrefixes");
        DebugOut.println(1, "reservedPrefixes " + reservedPrefixes);
        for (int i = 0; isValidColumnName && (i < reservedPrefixes.count()); i++) {
            String aPrefix = (String) reservedPrefixes.objectAtIndex(i);
            isValidColumnName = ! Column.normalizeStringForColumnNames(name).toLowerCase().startsWith(aPrefix.toLowerCase());
        }

        // Handle special suffixes used with field names in WOD file
        NSArray reservedSuffixes = SMApplication.appProperties().arrayPropertyForKey("DataAccessReservedSuffixes");
        DebugOut.println(1, "reservedSuffixes " + reservedSuffixes);
        for (int i = 0; isValidColumnName && (i < reservedSuffixes.count()); i++) {
            String aSuffix = (String) reservedSuffixes.objectAtIndex(i);
            isValidColumnName = ! Column.normalizeStringForColumnNames(name).toLowerCase().endsWith(aSuffix.toLowerCase());
        }

        // Add special case names to configuration property DataAccessReservedNames

        // Disallow anything but A-Z, a-z, 0-9, _ (underscore), and spaces
        if (isValidColumnName)
        {
            for (int i = 0; i < name.length(); i++)
            {
                char character = name.charAt(i);
                if ( ! (Character.isLetterOrDigit(character) ||
                        character == '_' ||
                        Character.isSpaceChar(character)))
                {
                    isValidColumnName = false;
                    break;
                }
            }
        }

        return isValidColumnName;
    }



    /**
     * Returns an ordered list of columns from this Table which may be used as the source of a Lookup column.  At present this is Text, Timestamp and Number fields only.  System fields are not returned.
     *
     * @return an ordered list of columns from this Table which may be used as the source of a Lookup column
     */
    public NSArray lookupSourceColumns()
    {
        NSMutableArray lookupSourceColumns = new NSMutableArray();
        Enumeration columnEnumerator = orderedUserColumns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            VirtualColumn aColumn = (VirtualColumn) columnEnumerator.nextElement();

            if (aColumn.isType(ColumnType.StringColumnType) ||
                aColumn.isType(ColumnType.TimestampColumnType) ||
                aColumn.isType(ColumnType.NumberColumnType))
            {
                lookupSourceColumns.addObject(aColumn);
            }
        }

        return lookupSourceColumns;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Simple Comparator to sort Column by system column status and name (ignoring case).
     */
    static public class DefaultUIComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            Column column1 = (Column)object1;
            Column column2 = (Column)object2;

            int ordering = column1.isSystemColumn().compareTo(column2.isSystemColumn());

            if (ordering == NSComparator.OrderedSame)
            {
                ordering = column1.name().compareToIgnoreCase(column2.name());
            }

            return ordering;
        }
    }



    /**
     * Returns an ordered list of columns from this Table which may be used as part of a search.  At present this returns all non-SiteFile columns.
     *
     * @return an ordered list of columns from this Table which may be used as part of a search
     */
    public NSArray searchableColumns()
    {
        NSMutableArray searchableColumns = new NSMutableArray();
        Enumeration columnEnumerator = columnsOrderedBy(new DefaultUIComparator()).objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            VirtualColumn aColumn = (VirtualColumn)columnEnumerator.nextElement();

            if ( ! aColumn.type().name().equals("SiteFile"))
            {
                searchableColumns.addObject(aColumn);
            }
        }

        return searchableColumns;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns an ordered list of columns from this Table which may be used to sort a list view of the data.  At present this returns <code>searchableColumns()</code>.
     *
     * @return an ordered list of columns from this Table which may be used to sort a list view of the data
     */
    public NSArray orderableColumns()
    {
        return searchableColumns();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns ordered columns on this table that are valid for importing.
     *
     * @return ordered columns on this table that are valid for importing
     */
    public NSArray importableColumns()
    {
        return columnsOrderedBy(super.importableColumns(), Column.DefaultComparator);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the list of Sections displaying this table.
     *
     * @return the list of Sections displaying this table
     */
    public NSArray sectionsUsedIn()
    {
        NSMutableArray sectionsUsedIn = new NSMutableArray();

        if ((website() != null) && (website().sections() != null))
        {
            Enumeration sectionEnumerator = website().sections().objectEnumerator();
            while (sectionEnumerator.hasMoreElements())
            {
                Section aSection = (Section) sectionEnumerator.nextElement();
                if (aSection.usesDatabaseTable(this))
                {
                    sectionsUsedIn.addObject(aSection);
                }
            }
        }

        return sectionsUsedIn;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns true if this table is displayed by any Sections.
     *
     * @return true if this table is displayed by any Sections
     */
    public boolean isUsedBySections()
    {
        return sectionsUsedIn().count() > 0;
    }



    /**
     * Does everything needed to be done before the data in a table is deleted.  Right now, just
     * deletes all the SiteFileFields which in turn delete their files from disk.
     */
    public void cleanupNonTableData()
    {
        // Must use a NotifyingEditingContext so that SiteFiles get deleted from disk.
        EOEditingContext ec = ((SMApplication)WOApplication.application()).newEditingContext();
        ec.lock();
        try
        {
            //Set the fetch timestamp to and hour ago to avoid refetching.
            ec.setFetchTimestamp(new NSTimestamp().getTime() - (60 * 60 * 1000));
            NSArray localColumns = EOUtilities.localInstancesOfObjects(ec, columns());

            // First find all, if any, SiteFile columns
            Enumeration columnEnumerator = localColumns.objectEnumerator();
            NSMutableArray siteFileColumns = new NSMutableArray();
            while (columnEnumerator.hasMoreElements())
            {
                VirtualColumn aColumn = (VirtualColumn)columnEnumerator.nextElement();

                if (aColumn.isType(VirtualSiteFileField.ColumnTypeName))
                {
                    siteFileColumns.addObject(aColumn);
                }
            }

            if (siteFileColumns.count() > 0)
            {
                // We are about to perform a bulk operation so prefetch all relationships for
                // optimization and to avoid firing faults while processing delete rules
                EODatabaseContextAdditions.preloadRelationship(siteFileColumns, "fields");

                NSArray allFields = Collection.
                    arrayByCollapsingSubCollections(siteFileColumns.valueForKey("fields"), false);
                EODatabaseContextAdditions.preloadRelationship(allFields, "row");
                EODatabaseContextAdditions.preloadRelationship(allFields, "referringFields");
                EODatabaseContextAdditions.preloadRelationship(allFields, "siteFileValue");

                NSArray allSiteFiles = (NSArray)allFields.valueForKey("siteFileValue");
                EODatabaseContextAdditions.preloadRelationship(allSiteFiles, "groups");
                EODatabaseContextAdditions.preloadRelationship(allSiteFiles, "passwords");
                EODatabaseContextAdditions.preloadRelationship(allSiteFiles, "toComponents");

                // Delete each SiteFileField
                Enumeration fieldEnumerator = allFields.objectEnumerator();
                while (fieldEnumerator.hasMoreElements())
                {
                    ec.deleteObject((VirtualField)fieldEnumerator.nextElement());
                }

                ec.saveChanges();
            }
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
    }



    /**
     * Deletes all the rows in this virtual table.
     */
    public void deleteRows()
    {
        /** require [no_changes_on_table] ! editingContext().hasChanges(); **/

        cleanupNonTableData();
        super.deleteRows();

        /** ensure [has_no_rows] rows().count() == 0; **/
    }



    /**
     * Returns any import formatters for this table.  Uses the standard input formatters from the default properties.
     *
     * @return import formatters for this table
     */
    public NSDictionary importFormatters()
    {
        NSMutableDictionary formatters = new NSMutableDictionary();

        Enumeration columnEnumerator = columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column aColumn = (Column)columnEnumerator.nextElement();
            String aColumnName = aColumn.name();

            // For lookup columns, we want to add formatters for the columns they look up
            if (aColumn.isType(ColumnType.LookupColumnType))
            {
                aColumn = ((VirtualLookupColumn)aColumn).lookupColumn();
            }

            // Must use the export format as the input format so that we can import what we export
            if (aColumn.isType(ColumnType.TimestampColumnType))
            {
                formatters.setObjectForKey(VirtualTimestampField.exportFormatter, aColumnName);
            }
            else if (aColumn.isType(ColumnType.NumberColumnType))
            {
                formatters.setObjectForKey(SMApplication.appProperties().numberFormatterForKey("InputNumberFormatter"), aColumnName);
            }
            else if (aColumn.isType(ColumnType.BooleanColumnType))
            {
                formatters.setObjectForKey(new BooleanFormatter(), aColumnName);
            }
        }

        return formatters;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Appends or replaces rows to the table from the given stream.  The stream must contain character data in <code>delimiter</code> delimited format.  Note: right now, this method doesn't handle quoted data or anything fancy.  Also note that this method requires that the table has no changes on it...  call <code>saveChanges()</code> before using this.
     *
     * @param stream the stream of characters that will make up the new rows.  The stream is <em>not</em> closed at the end of this method - you'll have to do it yourself
     * @param sender an arbitrary object that is used as a parameter to Column's valueForImportedValue() method
     * @param columnsToImport an ordered list of columns that we will import.  The first item in this array cooresponds to the first field in the data file, the second item to the second field, and so on
     * @param delimiter the character to use as the delimiter
     * @param skipFirstLine should we skip the first line? Useful if, for example, the first line contains the column names
     * @param shouldReplace <code>true</code> if this should replace all the rows, <code>false</code> if it should append the rows
     * @param aUser the user to use when creating the Created By and Modified By fields
     * @exception IOException occurs when an IO exception occurs on the steam
     * @exception ParseException occurs when a formatter cannot format the data given it
     */
    public void importTable(InputStream stream,
                            Object sender,
                            NSArray columnsToImport,
                            String delimiter,
                            boolean skipFirstLine,
                            boolean shouldReplace,
                            User aUser)
        throws IOException, ParseException
    {
        /** require
        [valid_stream_param] stream != null;
        [valid_columnsToImport_param] columnsToImport != null;
        [has_columns_to_import] columnsToImport.count() > 0;
        [columns_are_importable] forall i : {0 .. columnsToImport.count() - 1} # ColumnType.validImportExportTypes().containsObject(((Column)columnsToImport.objectAtIndex(i)).type().name());
        [valid_delimiter_param] delimiter != null;
        [valid_aUser_param] aUser != null;
        [same_number_of_columns] /# Does the import data have the same number of columns as the columnsToImport? #/ true;
        [no_changes_on_table] /# The table must be committed to the database before calling this method #/ true; **/

        try
        {
            // Cleanup SiteFiles if we are deleting the data from the table
            if ( shouldReplace)
            {
                cleanupNonTableData();
            }

            importUserPK = columnNamed("Created By").valueForImportedValue(sender, aUser.userID());
            super.importTable(stream, sender, columnsToImport, importFormatters(), delimiter, skipFirstLine, shouldReplace);
        }
        finally
        {
            importUserPK = null;
        }
    }


    /**
     * Adds a new virtual row to the given batch statement. This maximizes insert speed by not using WO at all, instead talking directly to the JDBC adaptor (through a Statement) to insert.  Also inserts the two system fields.
     *
     * @param sender an arbitrary object that is used as a parameter to Column's valueForImportedValue() method
     * @param batchStatement the batch of statements to which this will add it's SQL
     * @return the newly created and inserted row's PK
     */
    public Object batchNewRow(Object sender, Statement batchStatement) throws SQLException
    {
        /** require
        [valid_param] batchStatement != null;
        [has_pk_generator] hasPKGenerator(); **/

        Object rowID = super.batchNewRow(sender, batchStatement);

        // Create the user "system" fields
        batchNewField(batchStatement, rowID, columnNamed("Created By"), importUserPK);
        batchNewField(batchStatement, rowID, columnNamed("Modified By"), importUserPK);

        return rowID;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overridden to ensure that the Table name is unique per Website.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        if ((name() == null) || (name().trim().length() == 0))
        {
            exceptions.addObject(new NSValidation.ValidationException("Please enter a name for the table.", this, "name") );
        }
        else if ((website() != null) && website().isDuplicateDatabaseTableName(name()))
        {
            exceptions.addObject(new NSValidation.ValidationException("This table name has already been used.  Choose a different name.",
                                                                     this, "name") );
        }
        else  // If everything looks OK to us, check with EOF
        {
            try
            {
                super.validateForSave();
            }
            catch ( NSValidation.ValidationException e)
            {
                exceptions.addObjectsFromArray(e.additionalExceptions());
            }
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Makes sure that this table can be deleted.  A table cannot be deleted if it is used in a Data Access Section.
     *
     * @exception EOFValidationException if the object was not valid for delete
     */
    public void validateForDelete() throws com.webobjects.foundation.NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForDelete();
        }
        catch (com.webobjects.foundation.NSValidation.ValidationException exception)
        {
            exceptions.addObject(exception);
        }

        // If this column is being looked up by other columns, we can't delete it
        if (sectionsUsedIn().count() > 0)
        {
            NSValidation.ValidationException exception = new NSValidation.ValidationException("Can't delete a databaseTable used in a Section.");
            exceptions.addObject(exception);
        }

        if (exceptions.count() > 0)
        {
            throw com.webobjects.foundation.NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Returns true if this table has uses other tables for lookup, false otherrwise.
     *
     * @return true if this table has uses other tables for lookup, false otherrwise.
     */
    public boolean hasLookupSourceTables()
    {
        return lookupSourceTables().count() > 0;
    }



    /**
     * Returns the related tables are any tables that this table takes its lookup values from, and any tables those tables take lookup values from, and so on, and so forth.
     *
     * @return the related tables are any tables that this table takes its lookup values from, and any tables those tables take lookup values from, and so on, and so forth.
     */
    public NSArray lookupSourceTables()
    {
        NSMutableSet lookupSourceTables = new NSMutableSet();

        Enumeration columnEnumerator = orderedUserColumns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            VirtualColumn aColumn = (VirtualColumn) columnEnumerator.nextElement();

            if (aColumn.isType(ColumnType.LookupColumnType))
            {
                lookupSourceTables.addObject(((VirtualLookupColumn) aColumn).lookupColumn().table());
                lookupSourceTables.addObjectsFromArray(((SMVirtualTable) ((VirtualLookupColumn) aColumn).lookupColumn().table()).lookupSourceTables());
            }
        }

        return lookupSourceTables.immutableClone().allObjects();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a copy of an SMVirtualTable.  The copy is made manually because the in-memory added relationship to the different VirtualColumns,
     * dataAccessNotifications, must be copied last since it has a to-one relationship with DataAccess.  Creating a DataAccess duplicate requires
     * other relationships of the VirtualColumn to be copied first before the DataAccess can duplicated successfully.
     *
     * @param copiedObjects the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this SMVirtualTable
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = EOCopying.Utility.newInstance(this);

        // Handle circular relationships by registering this object right away.
        EOGlobalID globalID = editingContext().globalIDForObject(this);
        copiedObjects.setObjectForKey(copy, globalID);

        EOCopying.Utility.copyAttributes(this, copy);

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        //Traverse the columns relationship so instances of VirtualColumn will use the local method of copying instead of the default implementation
        String relationshipName = "columns";
        EOEnterpriseObject source = this;
        EOEnterpriseObject destination = copy;
        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("Website Copying 2-M relationship " + relationshipName);
            NSLog.debug.appendln("                            from " + source.editingContext().globalIDForObject(source));
        }

        NSArray originalObjects = (NSArray)source.valueForKey(relationshipName);
        NSArray destinationObjects = (NSArray)destination.valueForKey(relationshipName);

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("Website Copying " + originalObjects.count() + " for relationship " + relationshipName);
        }

        for (int i = 0, count = originalObjects.count(); i < count; i++)
        {
            EOEnterpriseObject original =  (EOEnterpriseObject)originalObjects.objectAtIndex(i);
            EOEnterpriseObject originalCopy = null;

            if (original instanceof VirtualColumn)
            {
                originalCopy = virtualColumnCopy(copiedObjects, original);
            }
            else
            {
                originalCopy = ((EOCopying)original).copy(copiedObjects);
            }

            // This is a tricky part.  Making the copy in the previous line can set the relationship that we are about to set.  We need to check for this so that we do not create duplicated relationships.
            if ( ! destinationObjects.containsObject(originalCopy))
            {
                destination.addObjectToBothSidesOfRelationshipWithKey(originalCopy, relationshipName);
            }

        }

        //Copy the other relationships
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("createdBy"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("modifiedBy"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("rows"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("websites"));

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Returns a VirtualColumn copy of this VirtualColumn Object.  Since it will require more effort to override
     * the VirtualColumn class, as a work-around, this method is called whenever a VirtualColumn is copied from this SMVirtualTable
     * instead of the default copy method from EOCopying.
     *
     * @param copiedObjects the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a source the VirtualColumn
     */
    public EOEnterpriseObject virtualColumnCopy(NSMutableDictionary copiedObjects, EOEnterpriseObject source)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;
                    [valid_source] source != null;
                    [source_eocopying] source instanceof EOCopying;
                    [source_eocopying] source instanceof VirtualColumn;
        **/
        EOGlobalID globalID = source.editingContext().globalIDForObject(source);

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("Website Copying object " + globalID);
        }

        EOEnterpriseObject copy = (EOEnterpriseObject)copiedObjects.objectForKey(globalID);
        if (copy == null)
        {
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Website Creating duplicate of object " + globalID);
            }

            copy = virtualColumnDuplicate(copiedObjects, source);
            copiedObjects.setObjectForKey(copy, globalID);
        }

        return copy;
        /** ensure [copy_made] Result != null;
                   [copy_cached] copiedObjects.objectForKey(source.editingContext().globalIDForObject(source)) != null;  **/

    }



    /**
     * Returns a deep copy of this object. Overriden for VirtualColumns so dataAccessNotifications relationship is copied last
     * since it requires the other relationships especially the lookupColumn to be copied first.
     *
     * @param copiedObjects the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a source the virtualColumnSource
     */
    public EOEnterpriseObject virtualColumnDuplicate(NSMutableDictionary copiedObjects, EOEnterpriseObject virtualColumnSource)
    {
        /** require
         	[copiedObjects_not_null] copiedObjects != null;
         	[virtualColumnSource_not_null] virtualColumnSource != null;
         **/

        EOEnterpriseObject copy = EOCopying.Utility.newInstance(virtualColumnSource);

        // Handle circular relationships by registering this object right away.
        EOGlobalID globalID = editingContext().globalIDForObject(virtualColumnSource);
        copiedObjects.setObjectForKey(copy, globalID);

        EOCopying.Utility.copyAttributes(virtualColumnSource, copy);

        EOEntity entity = ((EOEntityClassDescription)virtualColumnSource.classDescription()).entity();

        EOCopying.Utility.deepCopyRelationship(copiedObjects, virtualColumnSource, copy, entity.relationshipNamed("type"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, virtualColumnSource, copy, entity.relationshipNamed("fields"));

        if (virtualColumnSource instanceof VirtualLookupColumn)
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, virtualColumnSource, copy, entity.relationshipNamed("lookupColumn"));
        }

        EOCopying.Utility.deepCopyRelationship(copiedObjects, virtualColumnSource, copy, entity.relationshipNamed("referringColumns"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, virtualColumnSource, copy, entity.relationshipNamed("table"));

        // Calculated columns don't have this
        if (entity.relationshipNamed("dataAccessNotifications") != null)
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, virtualColumnSource, copy, entity.relationshipNamed("dataAccessNotifications"));
        }

        return copy;

        /** ensure [copy_made] Result != null; **/
    }
 }


