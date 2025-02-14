package net.global_village.virtualtables;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.text.ParseException;
import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.foundation.Date;
import ognl.*;
import ognl.webobjects.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.jdbcadaptor.*;

import er.extensions.foundation.*;


/**
 * A table that is not represented on disk by an actual database table, but by a "virtual" table.
 *
 * @author Copyright (c) 2001-2010  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 21$
 */
public class VirtualTable extends _VirtualTable
{
    protected static IntegerPrimaryKeyGenerator pkGenerator = null;
    protected static NSArray accessorMutatorPrefixes = new NSArray(new String [] {"get", "set", "is", "_", "_get", "_is", "_set"});
	public static final String STRING_ENCODING = "UTF-8";


    /**
     * Returns <code>true</code> if there is a PK generator ready for use, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there is a PK generator ready for use, <code>false</code> otherwise
     */
    public static synchronized boolean hasPKGenerator()
    {
        return pkGenerator != null;
    }



    /**
     * Initializes the PK generator.  NOTE: any other delegate on the DB context will be overwriten with extreme prejudice.
     *
     * @param additionalEntitiesToCache additional entities that this PK generator will generate PKs for.  Primarily designed for Table, Column and Field subclasses, but could be used for anything
     * @param numberOfPKsToCache the number of PK to cache
     */
    public static synchronized void startCachingPKs(int numberOfPKsToCache, NSArray additionalEntitiesToCache)
    {
        /** require
        [no_pk_generator] ! hasPKGenerator();
        [valid_additionalEntitiesToCache_param] additionalEntitiesToCache != null; **/

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/

        pkGenerator = new FBIntegerPrimaryKeyGenerator(numberOfPKsToCache);
        pkGenerator.cachePKsForModel(vtModel);

        Enumeration additionalEntitiesToCacheEnumerator = additionalEntitiesToCache.objectEnumerator();
        while (additionalEntitiesToCacheEnumerator.hasMoreElements())
        {
            EOEntity entity = (EOEntity)additionalEntitiesToCacheEnumerator.nextElement();
            pkGenerator.cachePKsForEntity(entity);
        }

        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, EOObjectStoreCoordinator.defaultCoordinator());
        /** check [has_dbcontext] dbContext != null; **/

        dbContext.lock();
        try
        {
            dbContext.setDelegate(pkGenerator);
        }
        finally
        {
            dbContext.unlock();
        }
        /** ensure [has_pk_generator] hasPKGenerator(); **/
    }


    /**
     * Removes the PK generator.  The DB Context's delegate is set to <code>null</code>.
     */
    public static synchronized void stopCachingPKs()
    {
        /** require [has_pk_generator] hasPKGenerator(); **/
        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/

        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, EOObjectStoreCoordinator.defaultCoordinator());
        /** check [has_dbcontext] dbContext != null; **/

        dbContext.lock();
        try
        {
            dbContext.setDelegate(null);
            pkGenerator = null;
        }
        finally
        {
            dbContext.unlock();
        }

        /** ensure [pk_generator_cleared] ! hasPKGenerator(); **/
    }


    /**
     * Initializes the VT engine.  Right now, that means setting up the DB context
     * delegate to provide quicker primary keys. NOTE: any other delegate on the DB context
     * will be overwritten with extreme prejudice.
     *
     * @param numberOfPKsToCache the number of PK to cache
     * @param additionalEntitiesToCache additional entities that this PK generator
     * will generate PKs for.  Primarily designed for Table, Column and Field
     * subclasses, but could be used for anything
     */
    public static synchronized void initializeVirtualTables(int numberOfPKsToCache, NSArray additionalEntitiesToCache)
    {
        /** require [valid_additionalEntitiesToCache_param] additionalEntitiesToCache != null; **/

        if (numberOfPKsToCache > 0)
        {
            System.out.println("************* startCachingPKs");
            startCachingPKs(numberOfPKsToCache, additionalEntitiesToCache);
        }

        WOOgnl.factory().configureWOForOgnl();

        // Allow virtual column access by name
        OgnlRuntime.setPropertyAccessor(VirtualRow.class, new VirtualRowPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(VirtualTable.class, new VirtualTablePropertyAccessor());

        // Add useful methods
        OgnlRuntime.setMethodAccessor(NSTimestamp.class, new NSTimestampMethodAccessor());
        OgnlRuntime.setMethodAccessor(java.lang.String.class, new StringMethodAccessor());
        OgnlRuntime.setMethodAccessor(java.math.BigDecimal.class, new BigDecimalMethodAccessor());
    }



    /**
     * Creates and returns a new table in the given editing context.
     *
     * @param ec the editing context into which to create a new virtual table
     * @param name the name of the new table
     * @return a new table in the given editing context with the given name
     */
    public static VirtualTable createVirtualTable(EOEditingContext ec, String name)
    {
        /** require
        [valid_ec_param] ec != null;
        [valid_name_param] name != null; **/

        VirtualTable newTable = new VirtualTable();
        ec.insertObject(newTable);
        newTable.setName(name);
        return newTable;

        /** ensure [valid_result] Result != null; **/
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

        Column column = newColumn(dateCreatedColumnName(), ColumnType.TimestampColumnType);
        column.setIsSystemColumn(net.global_village.foundation.GVCBoolean.yes());

        column = newColumn(dateModifiedColumnName(), ColumnType.TimestampColumnType);
        column.setIsSystemColumn(net.global_village.foundation.GVCBoolean.yes());
    }



    /**
     * Looks in <code>names</code> for names that begin with one of the standard KVC prefixes ("get", "set", "is", "_", "_get", "_is").  Any such names are munged to remove the prefix.  Also, if the munged name begins with a capital letter, then the first letter is lowercased and also added to the set.
     *
     * @return the munged column names
     */
    protected NSSet mungeInvalidAccessorMutatorPrefixes(NSArray names)
    {
        /** require [valid_param] names != null; **/

        NSMutableSet mungedNames = new NSMutableSet();
        Enumeration nameEnumerator = names.objectEnumerator();
        while (nameEnumerator.hasMoreElements())
        {
            String name = (String)nameEnumerator.nextElement();

            Enumeration prefixEnumerator = accessorMutatorPrefixes.objectEnumerator();
            while (prefixEnumerator.hasMoreElements())
            {
                String prefix = (String)prefixEnumerator.nextElement();
                if (name.startsWith(prefix))
                {
                    String mungedName = name.substring(prefix.length());
                    mungedNames.addObject(mungedName);
                    if (Character.isUpperCase(mungedName.charAt(0)))
                    {
                        mungedNames.addObject(StringAdditions.downcaseFirstLetter(mungedName));
                    }
                }
            }
        }
        return mungedNames;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the default set of invalid column names.
     *
     * @return the column names that are invalid due to conflict with method or field names on VirtualRow
     */
    protected NSMutableSet defaultInvalidColumnNames()
    {
        NSMutableSet defaultInvalidColumnNames = super.defaultInvalidColumnNames();

        NSArray methods = new NSArray(VirtualRow.class.getMethods());
        NSArray methodNames = (NSArray)methods.valueForKey("name");
        NSArray fields = new NSArray(VirtualRow.class.getFields());
        NSArray fieldNames = (NSArray)fields.valueForKey("name");

        defaultInvalidColumnNames.addObjectsFromArray(methodNames);
        defaultInvalidColumnNames.addObjectsFromArray(fieldNames);
        defaultInvalidColumnNames.unionSet(mungeInvalidAccessorMutatorPrefixes(methodNames));
        defaultInvalidColumnNames.unionSet(mungeInvalidAccessorMutatorPrefixes(fieldNames));

        return defaultInvalidColumnNames;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Makes sure that all the rows and fields are in memory so that we can do in-memory evaluation without firing off a million faults.  We make use of the pre-fetching facilities of fetch specs to accomplish this.  Note that this is likely not really needed due to the batch faulting facility of the EOModel.
     */
    protected void cacheData()
    {
        NSDictionary bindings = new NSDictionary(tableID(), "tableID");
        EOUtilities.objectWithFetchSpecificationAndBindings(editingContext(), "VirtualTable", "cacheTableWithTableID", bindings);
    }



    /**
     * Fetches and returns the objects specified by the qualifier.  Note that a <code>VirtualTable</code> cannot handle SQL qualifiers.  Also note that you should use the normalized name of a column, not its name, as in:<br>
     * <code>String normalizedName = Column.normalizedStringForColumnNames(name);
     * EOQualifier qualifier = new EOQualifier.qualifierWithFormat(normalizedName + " = 'hello'");</code><br>
     * If <code>qualifier</code> is null all objects are returned.
     *
     * @param qualifier the qualifier that specifies which objects to return, null to return all objects
     * @return the fetched objects
     */
    public NSArray objectsWithQualifier(EOQualifier qualifier)
    {
        //cacheData();  See note in cacheData() above...
        return (qualifier != null) ? EOQualifier.filteredArrayWithQualifier(rows(), qualifier) : rows();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the object (row) specified by the primary key.
     *
     * @param primaryKey - an object that specifies a primary key appropriate for this table
     * @return object with matching primary key
     * @exception EOObjectNotAvailableException - if there is no matching object
     */
    public EOEnterpriseObject objectWithPrimaryKey(Object primaryKey) throws com.webobjects.eoaccess.EOObjectNotAvailableException
    {
        EOEnterpriseObject object = EOUtilities.faultWithPrimaryKeyValue(editingContext(), "VirtualRow", primaryKey);

        if (EOObject.isDummyFaultEO(object))
        {
            throw new com.webobjects.eoaccess.EOObjectNotAvailableException("Can't find VirtualRow with PK " + primaryKey);
        }

        return object;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Creates a new virtual column of the given type and inserts it into the table's editing context.
     *
     * @param type the column type to create
     * @return the newly created column
     */
    protected Column createAndInsertColumn(ColumnType type)
    {
        /** require
        [valid_param] type != null;
        [same_ec] editingContext().equals(type.editingContext()); **/

        String virtualColumnEntityName = "Virtual" + type.entityNameRestrictor() + "Column";
        return (Column)EOUtilities.createAndInsertInstance(editingContext(), virtualColumnEntityName);

        /** ensure
        [valid_result] Result != null;
        [in_table_ec] editingContext().equals(Result.editingContext()); **/
    }



    /**
     * Creates a new row that is appropriate for this table.
     *
     * @return the newly created row
     */
    protected VirtualRow createAndInsertRow()
    {
        VirtualRow row = new VirtualRow();
        editingContext().insertObject(row);
        return row;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Creates a new row, inserts it into this object's editing context and links up the table relationship.
     *
     * @return the newly created and inserted row
     */
    public VirtualRow newRow()
    {
        VirtualRow row = createAndInsertRow();

        addObjectToBothSidesOfRelationshipWithKey(row, "rows");

        row.takeValueForKey(Date.now(), dateCreatedColumnName());
        row.takeValueForKey(Date.now(), dateModifiedColumnName());

        return row;
        /** ensure [valid_result] Result != null; **/
    }



    /**
         * Returns the name to use for the standard "date created" system column.  Subclasses can override this to use a different name.
     *
     * @return  the name to use for the standard "date created" system column.
     */
    public String dateCreatedColumnName()
    {
        return "dateCreated";
    }



    /**
     * Returns the name to use for the standard "date modified" system column.  Subclasses can override this to use a different name.
     *
     * @return  the name to use for the standard "date modified" system column.
     */
    public String dateModifiedColumnName()
    {
        return "dateModified";
    }



    /**
     * Returns an ordered list of columns from this Table which may be used in the formula of a calculated column.
     *
     * @return an ordered list of columns from this Table which may be used in the formula of a calculated column
     */
    public NSArray columnsAvailableForCalculation()
    {
        NSMutableArray columnsAvailableForCalculation = new NSMutableArray();
        Enumeration columnEnumerator = orderedColumns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            VirtualColumn aColumn = (VirtualColumn) columnEnumerator.nextElement();

            if (VirtualCalculatedColumn.canUseColumnInCalculation(aColumn))
            {
                columnsAvailableForCalculation.addObject(aColumn);
            }
        }

        return columnsAvailableForCalculation;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Updates statement to hold SQL to delete all the rows in this virtual table.
     *
     * @param batchStatement the batch of statements to which this will add it's SQL
     */
    public void batchDeleteRows(Statement batchStatement) throws SQLException
    {
        /** require [valid_param] batchStatement != null; **/

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/

        // No need to lock dbContext as this is called in the context of a locked co-ordinator
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, editingContext());
        /** check [has_dbcontext] dbContext != null; **/
        EOEntity vrEntity = EOModelGroup.defaultGroup().entityNamed("VirtualRow");
        /** check [has_vrEntity] vrEntity != null; **/
        EOEntity vfEntity = EOModelGroup.defaultGroup().entityNamed("VirtualField");
        /** check [has_vfEntity] vfEntity != null; **/
        JDBCContext adContext = (JDBCContext)dbContext.adaptorContext();
        EOAdaptor adaptor = adContext.adaptor();
        Enumeration rowEnumerator = rows().objectEnumerator();

        while (rowEnumerator.hasMoreElements())
        {
            VirtualRow row = (VirtualRow)rowEnumerator.nextElement();

            EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("virtualRowID = %@", new NSArray(row.virtualRowID()));
            EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).deleteStatementWithQualifier(qualifier, vfEntity);
            System.out.println("Batching statement: " + expression.statement());
            batchStatement.addBatch(expression.statement());
        }

        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("virtualTableID = %@", new NSArray(tableID()));
        EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).deleteStatementWithQualifier(qualifier, vrEntity);
        System.out.println("Batching statement: " + expression.statement());
        batchStatement.addBatch(expression.statement());
    }


    /**
     * Deletes all the rows in this virtual table. This maximizes delete speed by not using WO at all, instead talking directly to the JDBC adaptor to delete.
     */
    public void deleteRows()
    {
        /** require [no_changes_on_table] ! editingContext().hasChanges(); **/

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, editingContext());
        /** check [has_dbcontext] dbContext != null; **/

        dbContext.coordinator().lock();
        try
        {
            JDBCContext adaptorContext = (JDBCContext)dbContext.adaptorContext();
            java.sql.Connection connection = adaptorContext.connection();

            try
            {
                Statement statement = connection.createStatement();
                batchDeleteRows(statement);
                statement.executeBatch();
                connection.commit();
            }
            catch (SQLException e)
            {
                try
                {
                    connection.rollback();
                }
                catch (SQLException x) { }
                throw new ExceptionConverter(e);
            }

            refreshTableData();
        }
        finally
        {
            dbContext.coordinator().unlock();
        }

        /** ensure [has_no_rows] rows().count() == 0; **/
    }


    /**
     * This method should be called after direct SQL manipulation of the data in this table (i.e. importing data or deleting rows).  The data will have been modified in the database, but EOF doesn't know about that. Make EOF refault the table and its columns so that the relationships referring to these directly modifed objects get reloaded.
     */
    public void refreshTableData()
    {
        NSMutableArray invalidObjects = new NSMutableArray(editingContext().globalIDForObject(this));
        invalidObjects.addObjectsFromArray(EOEditingContextAdditions.globalIDsForObjects(editingContext(), columns()));
        editingContext().invalidateObjectsWithGlobalIDs(invalidObjects);
    }



    /**
     * Appends or replaces rows to the table from the given stream.  The stream must contain character data in <code>delimiter</code> delimited format.  Note: right now, this method doesn't handle quoted data or anything fancy.  Also note that this method requires that the table has no changes on it...  call <code>saveChanges()</code> before using this.
     *
     * @param stream the stream of characters that will make up the new rows.  The stream is <em>not</em> closed at the end of this method - you'll have to do it yourself
     * @param sender an arbitrary object that is used as a parameter to Column's valueForImportedValue() method
     * @param columnsToImport an ordered list of columns that we will import.  The first item in this array cooresponds to the first field in the data file, the second item to the second field, and so on
     * @param formatters mapping of column names to java.text.Formats, used to format the field strings into objects of the correct type. You'll normally only need this for date/times and possibly for custom column types
     * @param delimiter the character to use as the delimiter
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
        [columns_refer_to_same_table] forall i : {0 .. columnsToImport.count() - 1} # ((Column)columnsToImport.objectAtIndex(i)).table().equals(((Column)columnsToImport.objectAtIndex(0)).table());
        [columns_are_importable] forall i : {0 .. columnsToImport.count() - 1} # ColumnType.validImportTypes().containsObject(((Column)columnsToImport.objectAtIndex(i)).type().name());
        [valid_formatters_param] formatters != null;
        [valid_delimiter_param] delimiter != null;
        [same_number_of_columns] /# Does the import data have the same number of columns as the columnsToImport? #/ true;
        [no_changes_on_table] ! editingContext().hasChanges(); **/

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, editingContext());
        /** check [has_dbcontext] dbContext != null; **/

        dbContext.coordinator().lock();
        try
        {
            JDBCContext adaptorContext = (JDBCContext)dbContext.adaptorContext();
            java.sql.Connection connection = adaptorContext.connection();

            try
            {
                if (shouldReplace)
                {
                    Statement statement = connection.createStatement();
                    batchDeleteRows(statement);
                    statement.executeBatch();
                }

                // Read the file in the same ecoding that we exported it in.
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, STRING_ENCODING));
                String oneLine;
                while ((oneLine = reader.readLine()) != null)
                {
                    if (skipFirstLine)
                    {
                        skipFirstLine = false;
                    }
                    else
                    {
                        // My testing indicates that it is faster (by more than 5x for larger imports) to execute statements in smaller chunks (like, say, per virtual row)
                        Statement statement = connection.createStatement();

                        //** check [same_number_of_columns] dataElements.count() == columnsToImport.count(); **/
                        DelimitedStringTokenizer tokenizer = new DelimitedStringTokenizer(oneLine, delimiter, "'\"", null);

                        Object rowID = batchNewRow(sender, statement);
                        for (int i = 0; i < columnsToImport.count(); i++)
                        {
                            Column column = (Column)columnsToImport.objectAtIndex(i);
                            Object dataElement = tokenizer.nextToken();

                            // dataElement can be null or an empty string "", and we don't want empty or null values in our table
                            if ((dataElement != null) && ( ! dataElement.equals("")))
                            {
                                Format formatter = (Format)formatters.objectForKey(column.name());
                                if (formatter != null)
                                {
                                    dataElement = formatter.parseObject((String)dataElement);
                                }

                                dataElement = column.valueForImportedValue(sender, dataElement);

                                // valueForImportedValue can return null to indicate that the value should not be imported
                                if (dataElement != null)
                                {
                                    batchNewField(statement, rowID, column, dataElement);
                                }
                            }
                        }
                        /** check [nothing_left_to_tokenize] ! tokenizer.hasMoreTokens(); **/

                        System.out.println("BATCH: " + statement);
                        statement.executeBatch();
                    }
                }

                connection.commit();
            }
            catch (SQLException e)
            {
                try
                {
                    connection.rollback();
                }
                catch (SQLException x) { }
                throw new ExceptionConverter(e);
            }

            refreshTableData();
        }
        finally
        {
            dbContext.coordinator().unlock();
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
        [valid_param] batchStatement != null; **/

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, editingContext());
        /** check [has_dbcontext] dbContext != null; **/

        dbContext.coordinator().lock();
        try
        {
            EOEntity entity = EOModelGroup.defaultGroup().entityNamed("VirtualRow");
            /** check [has_entity] entity != null; **/

            Object rowID;
            if (Table.isUsingIntegerPKs())
            {
                rowID = pkGenerator.databaseContextNewPrimaryKey(dbContext, null, entity).objectForKey("virtualRowID");
            }
            else
            {
                EOAdaptorChannel adaptorChannel = dbContext.availableChannel().adaptorChannel();
                if (!adaptorChannel.isOpen())
                {
                    adaptorChannel.openChannel();
                }
                NSDictionary pk = (NSDictionary) adaptorChannel.primaryKeysForNewRowsWithEntity(1, entity).lastObject();
                rowID = pk.allValues().lastObject();
            }
            NSMutableDictionary valueDictionary = new NSMutableDictionary();
            valueDictionary.setObjectForKey(rowID, "virtualRowID");
            valueDictionary.setObjectForKey(tableID(), "virtualTableID");

            JDBCContext adContext = (JDBCContext)dbContext.adaptorContext();
            EOAdaptor adaptor = adContext.adaptor();
            EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).insertStatementForRow(valueDictionary, entity);
            System.out.println("Batching statement: " + expression.statement());
            batchStatement.addBatch(expression.statement());

            // Create the "system" fields
            Column column = columnNamed(dateCreatedColumnName());
            batchNewField(batchStatement, rowID, column, column.valueForImportedValue(sender, Date.now()));
            column = columnNamed(dateModifiedColumnName());
            batchNewField(batchStatement, rowID, column, column.valueForImportedValue(sender, Date.now()));

            return rowID;
        }
        finally
        {
            dbContext.coordinator().unlock();
        }
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Adds a new virtual field to the given batch statement. This maximizes insert speed by not using WO at all, instead talking directly to the JDBC adaptor (through a Statement) to insert.
     *
     * @param batchStatement the batch of statements to which this will add it's SQL
     * @param column the column that this field applies to
     * @param rowID the row that this field applies to
     * @param value the value of the field.  The value should be coerced with Column's valueForImportedValue() before calling this
     * @return the newly created and inserted field's PK
     */
    public Object batchNewField(Statement batchStatement, Object rowID, Column column, Object value) throws SQLException
    {
        /** require
        [valid_batchStatement_param] batchStatement != null;
        [valid_column_param] column != null;
        [column_has_permanent_globalID] ! editingContext().globalIDForObject(column).isTemporary();
        [valid_rowID_param] rowID != null; **/

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        /** check [has_eomodel] vtModel != null; **/
        // No need to lock dbContext as this method called in the context of a locked co-ordinator
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, editingContext());
        /** check [has_dbcontext] dbContext != null; **/
        String columnTypeName = column.type().name();
        EOEntity entity = EOModelGroup.defaultGroup().entityNamed("Virtual" + columnTypeName + "Field");
        /** check [has_entity] entity != null; **/

        NSMutableDictionary valueDictionary = new NSMutableDictionary();
        Object fieldID;
        if (Table.isUsingIntegerPKs())
        {
            fieldID = pkGenerator.databaseContextNewPrimaryKey(dbContext, null, entity).valueForKey("virtualFieldID");
        }
        else
        {
            EOAdaptorChannel adaptorChannel = dbContext.availableChannel().adaptorChannel();
            if (!adaptorChannel.isOpen())
            {
                adaptorChannel.openChannel();
            }
            NSDictionary pk = (NSDictionary) adaptorChannel.primaryKeysForNewRowsWithEntity(1, entity).lastObject();
            fieldID = pk.allValues().lastObject();
            System.out.println("Batching field for fieldID: " + ERXStringUtilities.byteArrayToHexString(((NSData)fieldID).bytes()));
        }

        valueDictionary.setObjectForKey(fieldID, "virtualFieldID");
        valueDictionary.setObjectForKey(rowID, "virtualRowID");
        NSDictionary columnPK = EOUtilities.primaryKeyForObject(column.editingContext(), column);
        valueDictionary.setObjectForKey(columnPK.objectForKey("columnID"), "virtualColumnID");
        valueDictionary.setObjectForKey(columnTypeName, "restrictingValue");

        valueDictionary.setObjectForKey(value, column.importAttributeName());

        JDBCContext adContext = (JDBCContext)dbContext.adaptorContext();
        EOAdaptor adaptor = adContext.adaptor();
        EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).insertStatementForRow(valueDictionary, entity);
        System.out.println("Batching statement: " + expression.statement());
        batchStatement.addBatch(expression.statement());

        return fieldID;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Changes EOL strings ("\r\n", "\r", "\n") in <code>aString</code> to a space.
     *
     * @param aString the string in which to change EOL characters to spaces
     * @return the newly created and inserted field's PK
     */
    public String mungeEOLCharacters(String aString)
    {
        /** require [valid_param] aString != null; **/

        StringBuffer stringBuffer = new StringBuffer(aString);
        for (int i = 0; i < stringBuffer.length(); i++)
        {
            char aChar = stringBuffer.charAt(i);
            if ((aChar == '\n') || (aChar == '\r'))
            {
                if ((aChar == '\r') && (stringBuffer.charAt(i + 1) == '\n'))
                {
                    stringBuffer.deleteCharAt(i + 1);
                }
                stringBuffer.setCharAt(i, ' ');
            }
        }
        return stringBuffer.toString();

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Exports the data in this table, writing the resulting string to the output stream given.
     *
     * @param qualifier EOQualifier to control which rows are exported, or null if all rows are to be exported.
     * @param sortOrderings NSArray of EOSortOrder to control the order in which rows are exported, or null if no specific order is required.
     * @param stream the stream to write the data to.  The stream is <em>not</em> closed at the end of this method - you'll have to do it yourself
     * @param sender an arbitrary object that is used as a parameter to Column's exportValue() method
     * @param columnsToExport an ordered list of column names that we will export.  The first name in this array cooresponds to the first field in the data file, the second name to the second field, and so on
     * @param delimiter the character to use as the delimiter
     * @param writeHeaderLine should we write the header line, which will contain the names of the columns we are exporting
     */
    public void exportTable(EOQualifier qualifier,
                            NSArray sortOrderings,
                            OutputStream stream,
                            Object sender,
                            NSArray columnsToExport,
                            String delimiter,
                            boolean writeHeaderLine)
        throws IOException
    {
        /** require
        [valid_stream_param] stream != null;
        [valid_columnsToExport_param] columnsToExport != null;
        [valid_delimiter_param] delimiter != null;
        [column_types_can_be_exported] true; **/

        final char quoteCharacter = '"';

        // Set a string encoding that will handle odd characters.  Without this, text such as
        // “. . . il ne résulte pas de l’instruction . . . que les craintes de l’intéressée puissent etre regardées comme fondées.” II(K)
        // Will get exported as
        // ?. . . il ne r?esulte pas de l?instruction . . . que les craintes de l?int?eress?ee puissent etre regard?ees comme fond?ees.? II(K)
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, STRING_ENCODING));

        if (writeHeaderLine)
        {
            NSMutableArray dataValues = new NSMutableArray();
            Enumeration columnsToExportEnumerator = columnsToExport.objectEnumerator();
            while (columnsToExportEnumerator.hasMoreElements())
            {
                VirtualColumn column = (VirtualColumn)columnsToExportEnumerator.nextElement();
                String columnName = column.name();

                // Quote the column name if it has the delimiter string inside or starts with the quote character
                if ( (columnName.length() > 0) &&
                     ((columnName.indexOf(delimiter) != -1) || (columnName.charAt(0) == quoteCharacter)) )
                {
                    // Need to escape the quotes if they are there
                    columnName = quoteCharacter + StringAdditions.escape(columnName, quoteCharacter, quoteCharacter) + quoteCharacter;
                }

                dataValues.addObject(columnName);
            }

            String dataString = dataValues.componentsJoinedByString(delimiter);
            writer.write(dataString);

            // Use \n instead of newLine() so that outout is consistent across
            // all platforms.
            writer.write('\n');
        }

        // Select and sort rows to export.
        NSMutableArray rowsToExport = new NSMutableArray(objectsWithQualifier(qualifier));
        if (sortOrderings != null)
        {
            EOSortOrdering.sortArrayUsingKeyOrderArray(rowsToExport, sortOrderings);
        }

        try
        {
            Enumeration rowEnumerator = rowsToExport.objectEnumerator();
            while (rowEnumerator.hasMoreElements())
            {
                VirtualRow row = (VirtualRow)rowEnumerator.nextElement();
                NSMutableArray dataValues = new NSMutableArray();
                Enumeration columnsToExportEnumerator = columnsToExport.objectEnumerator();
                VirtualColumn column;
                Object fieldValue;
                while (columnsToExportEnumerator.hasMoreElements())
                {
                    column = (VirtualColumn)columnsToExportEnumerator.nextElement();
                    fieldValue = row.exportValueForFieldNamed(column.name(), sender);

                    if (fieldValue == null)
                    {
                        fieldValue = new String();
                    }

                    String stringFieldValue = mungeEOLCharacters(fieldValue.toString());

                    // Quote the string if it has the delimiter string inside or starts with the quote character
                    if ( (stringFieldValue.length() > 0) &&
                         ((stringFieldValue.indexOf(delimiter) != -1) || (stringFieldValue.charAt(0) == quoteCharacter)) )
                    {
                        // Need to escape the quotes if they are there
                        stringFieldValue = quoteCharacter + StringAdditions.escape(stringFieldValue, quoteCharacter, quoteCharacter) + quoteCharacter;
                    }

                    dataValues.addObject(stringFieldValue);
                }

                String dataString = dataValues.componentsJoinedByString(delimiter);
                writer.write(dataString);

                // Use \n instead of newLine() so that outout is consistent across
                // all platforms.
                writer.write('\n');
            }
        }
        catch (UnsupportedEncodingException e) { /* Not very likely is it? */  }

        writer.flush();
    }



}
