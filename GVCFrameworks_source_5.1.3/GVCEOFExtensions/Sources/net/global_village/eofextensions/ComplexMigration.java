package net.global_village.eofextensions;

import java.sql.*;

import org.apache.log4j.*;
import org.dbunit.*;
import org.dbunit.database.*;
import org.dbunit.dataset.*;
import org.dbunit.operation.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.jdbcadaptor.*;

import er.extensions.eof.*;
import er.extensions.jdbc.*;
import er.extensions.migration.*;

import net.global_village.foundation.*;


/**
 * Migration that provides for migrations that combine changes from Java code, SQL
 * scripts, and DBUnit datasets.  This assumes that any SQL files are in a specific
 * structure under Resources:
 * <pre>
 * Resources/
 *     &lt;OptionalDirectory&gt;/
 *         &lt;DatabaseVendorName&gt;/
 *             &lt;scripts go here&gt;/
 * </pre>
 *
 * @author Copyright (c) 2001-2008  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 */
public abstract class ComplexMigration extends ERXMigrationDatabase.Migration
{

    private Logger logger = LoggerFactory.makeLogger();


    /**
     * Returns an empty list to indicate that there are no dependencies.  Subclasses
     * should override this as needed.  A declaration in the version 0 migration should
     * be sufficient.  An example:
     * <pre>
     * return new NSArray(new ERXModelVersion("OtherModel", 0) );
     * </pre>
     *
     * @see er.extensions.migration.IERXMigration#modelDependencies()
     *
     * @return an array of model versions that this migration depends on
     */
    public NSArray modelDependencies()
    {
        return null;
    }



    /**
     * This is the method to implement to perform migration.
     *
     * @see er.extensions.migration.ERXMigrationDatabase.Migration#upgrade(com.webobjects.eocontrol.EOEditingContext, er.extensions.migration.ERXMigrationDatabase)
     *
     * @param editingContext locked EOEditingContext that can be used if needed
     * @param database ERXMigrationDatabase that the migration is for
     * @throws Throwable if any errors prevent migration
     */
    public abstract void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable;



    /**
     * This  method does nothing and does not need to be implemented unless you really are supporting down grades
     *
     * @see er.extensions.migration.ERXMigrationDatabase.Migration#downgrade(com.webobjects.eocontrol.EOEditingContext, er.extensions.migration.ERXMigrationDatabase)
     *
     * @param editingContext locked EOEditingContext that can be used if needed
     * @param database ERXMigrationDatabase that the migration is for
     * @throws Throwable if any errors prevent migration
     */
    public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable
    {
        /** require [valid_editingContext] editingContext != null;
                    [valid_database] database != null;
         **/
        // DO NOTHING
    }



    /**
     * Returns the bundle that the resources are loaded from.  By default this is the bundle that the migration
     * is located in.
     *
     * @return  the bundle that the resources are loaded from
     */
    protected NSBundle bundle()
    {
        return NSBundle.bundleForClass(getClass());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Convenience method to take a script path and suffix and customize it with the vendor name of the database
     * currently being migrated.  For example, "SQL" and "DropTables.sql" would become "SQL/FrontBase/DropTables.sql when
     * migrating a FrontBase database.
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param scriptPath path to database specific script directories relative to the Resources directory
     * @param scriptSuffix final part of the script name including file type extension
     * @return full path to database specific script
     */
    protected String databaseSpecificNameForScript(ERXMigrationDatabase database, String scriptPath, String scriptSuffix)
    {
        /** require [valid_database] database != null;
                    [valid_scriptSuffix] scriptSuffix != null;
         **/
         return NSPathUtilities.stringByAppendingPathComponent(NSPathUtilities.stringByAppendingPathComponent(scriptPath, database.productName()),
                                                               scriptSuffix);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns true if a database specific script exists.
     *
     * @see #databaseSpecificNameForScript(ERXMigrationDatabase, String, String)
     * @see #hasScriptResource(EOEditingContext, ERXMigrationDatabase, String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param scriptPath path to database specific script directories relative to the Resources directory
     * @param scriptSuffix final part of the script name including file type extension
     * @return <code>true</code> if a database specific script exists
     */
    protected boolean hasScript(ERXMigrationDatabase database, String scriptPath, String scriptSuffix)
    {
        /** require [valid_database] database != null;
                    [valid_scriptSuffix] scriptSuffix != null;
         **/
        return hasScriptResource(databaseSpecificNameForScript(database, scriptPath, scriptSuffix));
    }



    /**
     * Returns true if a resource exists at the path.
     *
     * @param resourcePath full path to resource relative to the Resources directory
     * @return <code>true</code> if a resource exists at the path
     */
    protected boolean hasScriptResource(String resourcePath)
    {
        /** require [valid_resourcePath] resourcePath != null;  **/
        return NSBundleAdditions.hasResourceAtPath(bundle(), resourcePath);
    }



    /**
     * Throws if a database specific script does not exist.
     *
     * @see #databaseSpecificNameForScript(ERXMigrationDatabase, String, String)
     * @see #hasScript(ERXMigrationDatabase, String, String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param scriptPath path to database specific script directories relative to the Resources directory
     * @param scriptSuffix final part of the script name including file type extension
     *
     * @throws ERXMigrationFailedException if a database specific script does not exist
     */
    protected void throwIfScriptNotFound(ERXMigrationDatabase database, String scriptPath, String scriptSuffix) throws ERXMigrationFailedException
    {
        /** require [valid_database] database != null;
                    [valid_scriptSuffix] scriptSuffix != null;
         **/
        if (! hasScript(database, scriptPath, scriptSuffix))
        {
            throw new ERXMigrationFailedException(databaseSpecificNameForScript(database, scriptPath, scriptSuffix) + " not found");
        }
    }



    /**
     * Throws if specified script does not exist.
     *
     * @see #databaseSpecificNameForScript(ERXMigrationDatabase, String, String)
     * @see #hasScriptResource(String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param resourcePath path to script relative to Resources directory
     *
     * @throws ERXMigrationFailedException if a database specific script does not exist
     */
    protected void throwIfScriptNotFound(ERXMigrationDatabase database, String resourcePath) throws ERXMigrationFailedException
    {
        /** require [valid_database] database != null;
                    [valid_resourcePath] resourcePath != null;
         **/
        if (! hasScriptResource(resourcePath))
        {
            throw new ERXMigrationFailedException(resourcePath + " not found");
        }
    }



    /**
     * Runs the database specific script if it exists.
     *
     * @see #databaseSpecificNameForScript(ERXMigrationDatabase, String, String)
     * @see #hasScriptResource(EOEditingContext, ERXMigrationDatabase, String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param scriptPath path to database specific script directories relative to the Resources directory
     * @param scriptSuffix final part of the script name including file type extension
     *
     * @throws SQLException if the script fails at the database
     */
    protected void runScript(ERXMigrationDatabase database, String scriptPath, String scriptSuffix) throws SQLException
    {
        /** require [valid_database] database != null;
                    [valid_scriptSuffix] scriptSuffix != null;
         **/
        runScriptResource(database, databaseSpecificNameForScript(database, scriptPath, scriptSuffix));
    }



    /**
     * Runs the database specific script if it exists and throws if it does not.
     *
     * @see #databaseSpecificNameForScript(ERXMigrationDatabase, String, String)
     * @see #hasScriptResource(EOEditingContext, ERXMigrationDatabase, String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param scriptPath path to database specific script directories relative to the Resources directory
     * @param scriptSuffix final part of the script name including file type extension
     *
     * @throws SQLException if the script fails at the database
     * @throws ERXMigrationFailedException if a database specific script does not exist
     */
    protected void runRequiredScript(ERXMigrationDatabase database, String scriptPath, String scriptSuffix) throws SQLException
    {
        /** require [valid_database] database != null;
                    [valid_scriptSuffix] scriptSuffix != null;
         **/
        throwIfScriptNotFound(database, scriptPath, scriptSuffix);
        runScriptResource(database, databaseSpecificNameForScript(database, scriptPath, scriptSuffix));
    }



    /**
     * Runs the contents of the indicated resource as an SQL script.
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param resourcePathh full path to resource relative to the Resources directory
     *
     * @throws SQLException if the script fails at the database
     */
    protected void runScriptResource(ERXMigrationDatabase database, String resourcePath) throws SQLException
    {
        /** require [valid_database] database != null;
                    [valid_resourcePath] resourcePath != null;
        **/
        logger.info("Running " + resourcePath + " from " + bundle().name());
        String fileContents = NSBundleAdditions.contentsOfResourceAtPath(bundle(), resourcePath);
        runScript(database,  fileContents);
        logger.info("Finished running " + resourcePath);
    }



    /**
     * Runs a database and migration specific script if it exists and throws if it does not.  The script name
     * is formed as &lt;scriptPath&gt;/&lt;database.productName&gt;/&lt;migrationClass.simpleName&gt;.sql".
     * For example when migrating to version 6 of MyModel, calling this for FrontBase with a scriptPath of SQL
     * the name would be "SQL/FrontBase/MyModel6.sql.
     *
     * @see #databaseSpecificNameForScript(ERXMigrationDatabase, String, String)
     * @see #hasScriptResource(EOEditingContext, ERXMigrationDatabase, String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param scriptPath path to database specific script directories relative to the Resources directory
     *
     * @throws SQLException if the script fails at the database
     * @throws ERXMigrationFailedException if a database specific script does not exist
     */
    protected void runScriptForMigration(ERXMigrationDatabase database, String scriptPath) throws SQLException
    {
        /** require [valid_database] database != null;       **/
        String scriptSuffix = getClass().getSimpleName() + ".sql";
        throwIfScriptNotFound(database, scriptPath, scriptSuffix);
        runScriptResource(database, databaseSpecificNameForScript(database, scriptPath, scriptSuffix));
    }



    /**
     * Runs the contents of the indicated resource as an SQL script.
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param script SQL script to run as a string
     *
     * @throws SQLException if the script fails at the database
     */
    protected void runScript(ERXMigrationDatabase database, String script) throws SQLException
    {
        /** require [valid_database] database != null;
                    [valid_script] script != null;
        **/
        ERXJDBCUtilities.executeUpdateScript(database.adaptorChannel(), script, true);
    }



    /**
     * Runs the statement string as a single SQL statement.
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param statement SQL script to run as a statement
     *
     * @throws SQLException if the statement fails at the database
     */
    protected void runStatement(ERXMigrationDatabase database, String statement) throws SQLException
    {
        /** require [valid_database] database != null;
                    [valid_statement] statement != null;
        **/
        ERXJDBCUtilities.executeUpdate(database.adaptorChannel(), statement);
    }



    /**
     * Inserts the contents of the indicated resource as DBUnit data set.  This should be called only after
     * the schema is complete as later ALTER TABLE or ALTER COLUMN statements will cause an SQL error due to
     * the uncommitted data inserted by this method.  The primary key sequence for each table data is imported
     * into can be updated by the number of rows imported.
     *
     * @see #importData(ERXMigrationDatabase, String)
     *
     * @param editingContext the EOEditingContext used to update the primary key sequence
     * @param database ERXMigrationDatabase that the migration is for
     * @param resourcePathh full path to resource relative to the Resources directory
     *
     * @throws ClassNotFoundException if the JDBC driver can't be loaded
     * @throws SQLException if a database exception is thrown
     * @throws DatabaseUnitException if DBUnit can't proceed
     */
    protected void importDataAndUpdatePKSequence(EOEditingContext editingContext, ERXMigrationDatabase database, String resourcePath) throws ClassNotFoundException, SQLException, DatabaseUnitException
    {
        /** require [valid_editingContext] editingContext != null;
                    [valid_database] database != null;
                    [valid_resourcePath] resourcePath != null;
         **/
        importData(database, resourcePath);
        IDataSet dataset = DBUnitUtilities.getDataSet(bundle(), resourcePath);
        logger.info("Updating primary key sequences");
        ITableIterator tableIterator = dataset.iterator();
        while (tableIterator.next())
        {
            ITable table = tableIterator.getTable();
            String tableName = table.getTableMetaData().getTableName();
            // Remove schema name if present
            if (tableName.indexOf('.') > -1)
            {
                tableName = NSPathUtilities.pathExtension(tableName);
            }

            EOEntity entity = ERXEOAccessUtilities.entityUsingTable(editingContext, tableName);
            database.adaptorChannel().primaryKeysForNewRowsWithEntity(table.getRowCount(), entity);
        }
       logger.info("Finished updating primary key sequences");

    }



    /**
     * Removes the contents of the indicated resource as DBUnit data set.
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param resourcePathh full path to resource relative to the Resources directory
     *
     * @throws ClassNotFoundException if the JDBC driver can't be loaded
     * @throws SQLException if a database exception is thrown
     * @throws DatabaseUnitException if DBUnit can't proceed
     */
    protected void removeData(ERXMigrationDatabase database, String resourcePath) throws ClassNotFoundException, SQLException, DatabaseUnitException
    {
        /** require [valid_database] database != null;
                    [valid_resourcePath] resourcePath != null;
         **/
        performDBUnitOperation(DatabaseOperation.DELETE, "removing", database, resourcePath);
    }



    /**
     * Updates existing rows in the database with the DBUnit data set.  Inserts rows that do not exist.
     * This should be called only after the schema is complete as later ALTER TABLE or ALTER COLUMN
     * statements will cause an SQL error due to the uncommitted data inserted by this method.
     *
     * @see #importDataAndUpdatePKSequence(EOEditingContext, ERXMigrationDatabase, String)
     *
     * @param database ERXMigrationDatabase that the migration is for
     * @param resourcePathh full path to resource relative to the Resources directory
     *
     * @throws ClassNotFoundException if the JDBC driver can't be loaded
     * @throws SQLException if a database exception is thrown
     * @throws DatabaseUnitException if DBUnit can't proceed
     */
    protected void importData(ERXMigrationDatabase database, String resourcePath) throws ClassNotFoundException, SQLException, DatabaseUnitException
    {
        /** require [valid_database] database != null;
                    [valid_resourcePath] resourcePath != null;
         **/
        performDBUnitOperation(DatabaseOperation.REFRESH, "inserting/updating", database, resourcePath);
    }



    /**
     * Performs the indicated DbUnit operation on the database with the DBUnit data set.
     *
     * @param operation DbUnit operation to perform
     * @param logAction verb to DbUnit operation to use in log messages
     * @param database ERXMigrationDatabase that the migration is for
     * @param resourcePathh full path to resource relative to the Resources directory
     *
     * @throws ClassNotFoundException if the JDBC driver can't be loaded
     * @throws SQLException if a database exception is thrown
     * @throws DatabaseUnitException if DBUnit can't proceed
     */
    protected void performDBUnitOperation(DatabaseOperation operation,
                                          String logAction,
                                          ERXMigrationDatabase database,
                                          String resourcePath) throws ClassNotFoundException, SQLException, DatabaseUnitException
    {
        /** require [valid_operation] operation != null;
                    [valid_logAction] logAction != null;
                    [valid_database] database != null;
                    [valid_resourcePath] resourcePath != null;
         **/
        logger.info("DBUnit " + logAction + " with " + resourcePath + " from " + bundle().name());
        IDatabaseConnection connection = DBUnitUtilities.getConnection(((JDBCContext)database.adaptorChannel().adaptorContext()), false, "\"?\"");
        IDataSet dataset = DBUnitUtilities.getDataSet(bundle(), resourcePath);
        operation.execute(connection, dataset);  // Note NOT run in its own transaction
        logger.info("DBUnit finished " + logAction + " with " + resourcePath);
    }


}