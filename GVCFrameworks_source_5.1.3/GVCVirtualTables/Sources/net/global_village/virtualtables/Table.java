package net.global_village.virtualtables;

import java.io.*;
import java.text.*;
import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Date;


/**
 * The root object for a virtual table, which is a set of objects that can be created easily at runtime, that, together, act as if they were a single object (see VirtualRow) with a persistent store.<p>
 *
 * Table is the only object that you actually have to insert into the editing context.  Columns and Rows are created by using <code>newColumn()</code> and <code>newRow()</code> respectively, and these methods automatically insert the newly created column or row into the table's editing context.<p>
 *
 * Usage is simple: choose the correct subclass of Table (only one to choose from right now, so that is simple ;-) and instantiate it and insert it into an editing context (there is a convenience method on <code>VirtualTable</code> to help you with this). Then use <code>newColumn()</code> to create columns on the new table and <code>newRow</code> to make new rows, and finally, use <code>valueForFieldNamed()</code> and <code>takeValueForFieldNamed()</code> or KVC to populate row data.  For example:
 * <code>
 * testTable = VirtualTable.createVirtualTable(editingContext(), "Test Table");
 * numberColumn = testTable.newColumn("number column", ColumnType.NumberColumnType);
 * row = testTable.newRow();
 * row.takeValueForKey(new Integer(1), "number column");
 * </code>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 12$
 */
public class Table extends _Table
{
    protected NSDictionary columnNameMapping;

    /** Holds the column names that are invalid due to conflict with method or field names on VirtualRow. */
    protected NSMutableSet invalidColumnNames = defaultInvalidColumnNames();

    private Map calculationContext;


    /**
     * Instance of NameComparator to be used when sorting Tables.
     */
    static final public NSComparator NameComparator = new NameComparator();


    /**
     * Returns a table with the given ID.
     *
     * @param ec the editing context in which to return this
     * @param tableID the table ID to look for
     * @return the table with the given ID
     */
    public static Table tableWithTableID(EOEditingContext ec, Object tableID)
    {
        /** require
        [valid_ec_param] ec != null;
        [valid_tableID_param] tableID != null; **/

        NSDictionary bindings = new NSDictionary(tableID, "tableID");
        return (Table)EOUtilities.objectWithFetchSpecificationAndBindings(ec, "Table", "tableWithTableID", bindings);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an array of the Columns ordered by <code>comparator</code>.
     *
     * @param columns the columns to sort
     * @param comparator the comparator to order the columns by
     * @return an array of the Columns ordered by <code>comparator</code>
     */
    public static NSArray columnsOrderedBy(NSArray columns, NSComparator comparator)
    {
        /** require [valid_columns_param] columns != null; [valid_comparator_param] comparator != null; **/
        return NSArrayAdditions.sortedArrayUsingComparator(columns, comparator);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns true iff this application is using integer PKs (as opposed to byte(24))
     *
     * @return true iff this application is using integer PKs
     */
    public static boolean isUsingIntegerPKs()
    {
        return ! EOModelGroup.defaultGroup().modelNamed("VirtualTables").entityNamed("VirtualRow").attributeNamed("virtualRowID").className().equals("com.webobjects.foundation.NSData");
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
        setDateCreated(Date.now());
        setDateModified(Date.now());
    }



    /**
     * Called when we are fetched from an external data store, or when another editing context changes this object (for example, by adding a column).
     *
     * @param ec the editing context in which this is being fetched
     */
    public void awakeFromFetch(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromFetch(ec);
        clearColumnNameMapping();
    }



    /**
     * Overriden to clear cached values when object reverted in ec.
     */
    public void updateFromSnapshot(NSDictionary aSnapshot)
    {
        super.updateFromSnapshot(aSnapshot);
        clearColumnNameMapping();
    }



    /**
     * Returns the default set of invalid column names.
     *
     * @return the column names that are invalid due to conflict with method or field names on VirtualRow
     */
    protected NSMutableSet defaultInvalidColumnNames()
    {
        return new NSMutableSet();
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the column names that are invalid due to conflict with method or field names on VirtualRow.
     *
     * @return the column names that are invalid due to conflict with method or field names on VirtualRow
     */
    public NSSet invalidColumnNames()
    {
        return invalidColumnNames;
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Adds <code>invalidName</code> to the set of names that VT considers invalid.
     *
     * @param invalidName the name to add to the invalid names
     */
    public void addInvalidColumnName(String invalidName)
    {
        /** require [valid_param] invalidName != null; **/
        invalidColumnNames.addObject(invalidName);
    }


    /**
     * Adds <code>invalidNames</code> to the set of names that VT considers invalid.
     *
     * @param invalidNames the names to add to the invalid names
     */
    public void addInvalidColumnNames(NSSet invalidNames)
    {
        /** require [valid_param] invalidNames != null; **/
        invalidColumnNames.unionSet(invalidNames);
    }



    /**
     * Returns <code>true</code> if the given name is a valid column name for this table, <code>false</code> otherwise.  Note: does <em>not</em> check for duplicate column names.  Use <code>Table.hasColumnNamed()</code> for that.
     *
     * @param name the name to check
     * @return <code>true</code> if the given name is a valid column name, <code>false</code> otherwise
     */
    public boolean isValidColumnName(String name)
    {
        /** require [valid_param] name != null; **/

        // Probably only need to check the normalized value, but let's be safe...
        return ! ((name.length() == 0) ||
                  ( ! Character.isLetter(name.charAt(0))) ||
                  (invalidColumnNames().containsObject(name)) ||
                  (invalidColumnNames().containsObject(Column.normalizeStringForColumnNames(name))) );
    }



    /**
     * Returns an array of the Columns ordered by <code>comparator</code>.
     *
     * @param comparator the comparator to order the columns by
     * @return an array of the Columns ordered by <code>comparator</code>
     */
    public NSArray columnsOrderedBy(NSComparator comparator)
    {
        /** require [valid_param] comparator != null; **/
        return columnsOrderedBy(columns(), comparator);
        /** ensure [valid_result] Result != null; [all_tables_returned] Result.count() == columns().count(); **/
    }



    /**
     * Returns a sorted list of the Columns ordered by Column.DefaultComparator.
     *
     * @return a sorted list of the Columns ordered by Column.DefaultComparator
     */
    public NSArray orderedColumns()
    {
        return columnsOrderedBy(Column.DefaultComparator);
        /** ensure [valid_result] Result != null; [all_tables_returned] Result.count() == columns().count(); **/
    }



    /**
     * Returns the subset of columns which are user created (! isSystemColumn()).
     *
     * @return the subset of columns which are user created (! isSystemColumn())
     */
    public NSArray userColumns()
    {
        return columnsWithQualfier(new EOKeyValueQualifier("isSystemColumn",
                                                           EOQualifier.QualifierOperatorEqual,
                                                           net.global_village.foundation.GVCBoolean.no()));
        /** ensure [valid_result] Result != null; [valid_count] Result.count() <= columns().count(); **/
    }



    /**
     * Returns the subset of columns which are editable (isEditableColumn()).
     *
     * @return the subset of columns which are editable
     */
    public NSArray editableColumns()
    {
        return columnsWithQualfier(new EOKeyValueQualifier("isEditableColumn",
                                                           EOQualifier.QualifierOperatorEqual,
                                                           net.global_village.foundation.GVCBoolean.yes()));
        /** ensure [valid_result] Result != null; [valid_count] Result.count() <= columns().count(); **/
    }



    /**
     * Returns a sorted list of the userColumns ordered by Column.DefaultComparator.
     *
     * @return a sorted list of the userColumns ordered by Column.DefaultComparator
     */
    public NSArray orderedUserColumns()
    {
        return columnsOrderedBy(userColumns(), Column.DefaultComparator);
        /** ensure [valid_result] Result != null; [valid_count] Result.count() == userColumns().count(); **/
    }



    /**
     * Returns the subset of columns which are not user created (isSystemColumn()).
     *
     * @return the subset of columns which are not user created (isSystemColumn())
     */
    public NSArray systemColumns()
    {
        return columnsWithQualfier(new EOKeyValueQualifier("isSystemColumn",
                                                           EOQualifier.QualifierOperatorEqual,
                                                           net.global_village.foundation.GVCBoolean.yes()));
        /** ensure [valid_result] Result != null; [valid_count] Result.count() <= columns().count(); **/

    }



    /**
     * Returns a sorted list of the systemColumns ordered by Column.DefaultComparator.
     *
     * @return a sorted list of the systemColumns ordered by Column.DefaultComparator
     */
    public NSArray orderedSystemColumns()
    {
        return columnsOrderedBy(systemColumns(), Column.DefaultComparator);
        /** ensure [valid_result] Result != null; [valid_count] Result.count() == systemColumns().count(); **/
    }



    /**
     * Returns columns on this table that are valid for importing.
     *
     * @return columns on this table that are valid for importing
     */
    public NSArray importableColumns()
    {
        NSMutableArray importableColumns = new NSMutableArray();

        Enumeration columnEnumerator = columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column aColumn = (Column)columnEnumerator.nextElement();
            if ((ColumnType.validImportTypes().containsObject(aColumn.type().name())) && ( ! (aColumn.isSystemColumn().booleanValue())))
            {
                importableColumns.addObject(aColumn);
            }
        }

        return importableColumns;
    }



    /**
     * Returns the subset of colums() which match qualifier.
     *
     * @return the subset of colums() which match qualifier
     */
    public NSArray columnsWithQualfier(EOQualifier qualifier)
    {
        /** require [valid_param] qualifier != null; **/

        return EOQualifier.filteredArrayWithQualifier(columns(), qualifier);

        /** ensure [valid_result] Result != null; [valid_count] Result.count() <= columns().count(); **/
    }




    /**
     * Fetches and returns the objects specified by the qualifier.  Note that a <code>VirtualTable</code> cannot handle SQL qualifiers.  Also note that you should use the normalized name of a column, not its name, as in:<br>
     * <code>String normalizedName = Column.normalizedStringForColumnNames(name);
     * EOQualifier qualifier = new EOQualifier.qualifierWithFormat(normalizedName + " = 'hello'");</code><p>
     * If <code>qualifier</code> is null all objects are returned.  Subclasses should override to provide the proper mechanism to do the fetch.
     *
     * @param qualifier the qualifier that specifies which objects to return
     * @return the fetched objects
     */
    public NSArray objectsWithQualifier(EOQualifier qualifier)
    {
        throw new Error("Subclass responsibility");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the object (row) specified by the primary key.  Subclasses should override to provide the proper object.
     *
     * @param primaryKey - an object that specifies a primary key appropriate for this table
     * @return object with matching primary key
     * @exception EOObjectNotAvailableException - if there is no matching object
     */
    public EOEnterpriseObject objectWithPrimaryKey(Object primaryKey) throws com.webobjects.eoaccess.EOObjectNotAvailableException
    {
        throw new Error("Subclass responsibility");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the object (row) specified by the Integer primary key.  Use this method to avoid cast exception.
     *
     * @param primaryKey - an Integer that specifies a primary key appropriate for this table
     * @return object with matching primary key
     * @exception EOObjectNotAvailableException - if there is no matching object
     */
    public EOEnterpriseObject objectWithIntegerPrimaryKey(Integer primaryKey) throws com.webobjects.eoaccess.EOObjectNotAvailableException
    {
        return objectWithPrimaryKey(primaryKey);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> if there are any objects (rows) in this table.  Subclasses should override to provide an efficient implementation as the default implementation uses objectsWithQualifier(null).
     *
     * @return <code>true</code> if there are any objects (rows) in this table.
     */
    public boolean hasObjects()
    {
        return objectsWithQualifier(null).count() > 0;
    }



    /**
     * Returns the list of other tables which may refer to data in this table.  This is the case if columns in this table are the source of data for a Lookup column in another table.
     *
     * @return the list of other tables which may refer to data in this table.
     */
    public NSArray referringTables()
    {
        NSMutableSet referringTables = new NSMutableSet();

        Enumeration columnEnumerator = columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column aColumn = (Column) columnEnumerator.nextElement();

            Enumeration referringColumnEnumerator = aColumn.referringColumns().objectEnumerator();
            while (referringColumnEnumerator.hasMoreElements())
            {
                Column referringColumn = (Column) referringColumnEnumerator.nextElement();
                referringTables.addObject(referringColumn.table());
            }
        }

        return referringTables.allObjects();
    }



    /**
     * Returns <code>true</code> if any other table may refer to data in this table.  This is the case if columns in this table are the source of data for a Lookup column in another table.  If this returns <code>true</code>, then this table cannot be deleted (prevented by Deny deletion rule in Column / VirutalColumn).
     *
     * @return <code>true</code> if any other table may refer to data in this table.
     */
    public boolean hasReferringTables()
    {
        return referringTables().count() > 0;
    }



    /**
     * Returns <code>true</code> if the given name is a valid name for a <em>new</em> column on this table, <code>false</code> otherwise. The reason this method is only intended for new column names is that if you check the name of a column that is already on the table, it will always return <code>false</code> (this method believes it's a duplicate column name).
     *
     * @param name the name to check
     * @return <code>true</code> if the given name is a valid name for a <em>new</em> column on this table, <code>false</code> otherwise
     */
    public boolean isValidNewColumnName(String name)
    {
        /** require [valid_param] name != null; **/
        return isValidColumnName(name) && ( ! hasColumnNamed(name));
    }


    /**
     * Returns a column name that is unique, starting with startingWith.
     *
     * @param startingWith the string to start with
     * @return a unique column name
     */
    public String uniqueColumnName(String startingWith)
    {
        /** require [valid_param] startingWith != null; **/
        int i = 1;
        while ( ! isValidNewColumnName(startingWith + " " + i))
        {
            i++;
        }
        return startingWith + " " + i;
    }



    /**
     * Creates a new column of the given type that is appropriate for this table and inserts it into the table's editing context.  Subclasses should override to create the proper type of column.
     *
     * @param type the column type to create
     * @return the newly created column
     */
    protected Column createAndInsertColumn(ColumnType type)
    {
        /** require
        [valid_param] type != null;
        [same_ec] editingContext().equals(type.editingContext()); **/

        throw new Error("Subclass responsibility");

        /** ensure
        [valid_result] Result != null;
        [in_table_ec] editingContext().equals(Result.editingContext()); **/
    }



    /**
     * Creates a new column, inserts it into this object's editing context and links up the table and type relationships.
     *
     * @param name the column's name
     * @param type the column's type
     * @return the newly created and added column
     */
    public Column newColumn(String name, ColumnType type)
    {
        /** require
        [valid_name_param] name != null;
        [valid_type_param] type != null;
        [same_ec] editingContext().equals(type.editingContext()); **/

        Column column = createAndInsertColumn(type);
        column.setTable(this);
        addToColumns(column);
        column.setType(type);
        column.setName(name);
        return column;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Creates a new column, inserts it into this object's editing context and links up the table relationship.
     *
     * @param name the column's name
     * @param typeName the column's type name
     * @return the newly created and added column
     */
    public Column newColumn(String name, String typeName)
    {
        /** require [valid_name_param] name != null; [valid_typeName_param] typeName != null; **/

        ColumnType type = ColumnType.columnTypeWithName(editingContext(), typeName);
        return newColumn(name, type);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Clears the column name mapping.
     */
    public void clearColumnNameMapping()
    {
        columnNameMapping = null;
    }



    /**
     * Returns the mapping between column names and columns as a dictionary.
     *
     * @return the mapping between column names and columns
     */
    protected NSDictionary columnNameMapping()
    {
        if (columnNameMapping == null)
        {
            columnNameMapping = new NSDictionary(columns(), (NSArray)columns().valueForKey("normalizedName"));
        }
        return columnNameMapping;

        /** ensure
        [valid_result] Result != null;
        [same_number_as_columns] Result.count() == columns().count();
        [contains_all_columns] forall i : {0 .. columns().count() - 1} # Result.allValues().containsObject(columns().objectAtIndex(i)); **/
    }



    /**
     * Returns the column with the given name, or <code>null</code> if this table doesn't have a column with the given name.
     *
     * @param columnName the column name to look for
     * @return the column with the given name, or <code>null</code> if this table doesn't have a column with the given column
     */
    protected Column findColumnNamed(String columnName)
    {
        /** require [valid_param] columnName != null; **/
        String normalizedName = Column.normalizeStringForColumnNames(columnName);
        return (Column)columnNameMapping().objectForKey(normalizedName);
    }



    /**
     * Does this table contain a column with the given name?
     *
     * @param columnName the column name to look for
     * @return <code>true</code> if the table contains a column with the given name, <code>false</code> otherwise
     */
    public boolean hasColumnNamed(String columnName)
    {
        /** require [valid_param] columnName != null; **/
        return findColumnNamed(columnName) != null;
    }



    /**
     * Returns the column with the given name.
     *
     * @param columnName the column name to look for
     * @return the column with the given name
     */
    public Column columnNamed(String columnName)
    {
        /** require
        [valid_param] columnName != null;
        [has_column] hasColumnNamed(columnName); **/
        return findColumnNamed(columnName);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Appends or replaces rows to the table from the given stream.  The stream must contain character data in <code>delimiter</code> delimited format.
     *
     * @param stream the stream of characters that will make up the new rows.  The stream is <em>not</em> closed at the end of this method - you'll have to do it yourself
     * @param sender an arbitrary object that is used as a parameter to Column's valueForImportedValue() method
     * @param columnsToImport an ordered list of columns that we will import.  The first item in this array cooresponds to the first field in the data file, the second item to the second field, and so on
     * @param formatters mapping of column names to java.text.Formats, used to format the field strings into objects of the correct type. You'll normally only need this for date/times and possibly for custom column types
     * @param delimiter the string to use as the delimiter
     * @param skipFirstLine should we skip the first line? Useful if, for example, the first line contains the column names
     * @param shouldReplace <code>true</code> if this should replace all the rows, <code>false</code> if it should append the rows
     * @exception IOException occurs when an IO exception occurs on the steam
     * @exception ParseException occurs when a formatter cannot format the data given it
     */
    public void importTable(InputStream stream,
                            Object sender,
                            NSArray columnsToImport,
                            NSDictionary formatters,
                            String delimiter,
                            boolean skipFirstLine,
                            boolean shouldReplace)
        throws IOException, ParseException
    {
        /** require
        [valid_stream_param] stream != null;
        [valid_columnsToImport_param] columnsToImport != null;
        [has_columns_to_import] columnsToImport.count() > 0;
        [columns_are_importable] forall i : {0 .. columnsToImport.count() - 1} # ColumnType.validImportTypes().containsObject(((Column)columnsToImport.objectAtIndex(i)).type().name());
        [valid_formatters_param] formatters != null;
        [valid_delimiter_param] delimiter != null;
        [same_number_of_columns] /# Does the import data have the same number of columns as the columnsToImport? #/ true; **/

        throw new Error("Subclass responsibility");
    }



    /**
     * Exports the data in this table, writing the resulting string to the output stream given.
     *
     * @param qualifier EOQualifier to control which rows are exported, or null if all rows are to be exported.
     * @param sortOrderings NSArray of EOSortOrder to control the order in which rows are exported, or null if no specific order is required.
     * @param stream the stream to write the data to.  The stream is <em>not</em> closed at the end of this method - you'll have to do it yourself
     * @param columnsToExport an ordered list of column names that we will export.  The first name in this array cooresponds to the first field in the data file, the second name to the second field, and so on
     * @param delimiter the string to use as the delimiter
     * @param writeHeaderLine should we write the header line, which will contain the names of the columns we are exporting
     */
    public void exportTable(EOQualifier qualifier, NSArray sortOrderings, OutputStream stream, NSArray columnsToExport,
                            String delimiter, boolean writeHeaderLine) throws IOException
    {
        /** require
        [valid_stream_param] stream != null;
        [valid_columnsToExport_param] columnsToExport != null;
        [valid_delimiter_param] delimiter != null;
        [column_types_can_be_exported] true; **/

        throw new Error("Subclass responsibility");
    }



    /**
     * Overriden to clear the cached column mapping.
     */
    public void setColumns(NSMutableArray value)
    {
        clearColumnNameMapping();
        super.setColumns(value);
    }

    /**
     * Overriden to clear the cached column mapping.
     */
    public void addToColumns(net.global_village.virtualtables.VirtualColumn object)
    {
        clearColumnNameMapping();
        super.addToColumns(object);
    }

    /**
     * Overriden to clear the cached column mapping.
     */
    public void removeFromColumns(net.global_village.virtualtables.VirtualColumn object)
    {
        clearColumnNameMapping();
        super.removeFromColumns(object);
    }



    /**
     * Simple Comparator to sort Tables by name, ignoring case.
     */
    static protected class NameComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            return ((Table) object1).name().compareToIgnoreCase(((Table) object2).name());
        }
    }



    /**
     * Returns the Context used when validating calculated fields.  This will return null
     * if there is no evaluation in progress.  This context is passed down the chain when calculated fields
     * include other calculated fields in their expressions.
     *
     * @return the Context used when validating calculated fields
     */
    public Map calculationContext()
    {
        return calculationContext;
    }



    /**
     * Sets the Context used when validating calculated fields.  Called with null after
     * an validation is complete.
     *
     * @param context the Context to be used when validating calculated fields
     */
    public void setCalculationContext(Map context)
    {
        calculationContext = context;
    }



}
