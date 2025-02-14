package net.global_village.eofextensions;

import java.io.*;
import java.sql.*;

import org.dbunit.*;
import org.dbunit.database.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.jdbcadaptor.*;

import net.global_village.foundation.*;



/**
 * Collection of utility methods to make working with EOF and DBUnit easier.
 *
 * @author Copyright (c) 2008  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revsion:$
 */
public class DBUnitUtilities
{


    /**
     * Uses DBUnit to perform the selected operation on the indicated file of data inside of a transaction.
     *
     * @param operation the operation (e.g. insert, delete) to perform on the data
     * @param resourcePath the path below Resources/ to to take the data from
     * @throws SQLException
     * @throws DatabaseUnitException
     */
    public static void performOperationOnDataSet(IDatabaseConnection connection,
                                          DatabaseOperation operation,
                                          IDataSet dataset) throws DatabaseUnitException, SQLException
    {
            new TransactionOperation(operation).execute(connection, dataset);
    }



    /**
     * Creates and configures a new JDBC database connection based on an EOModel.
     *
     * @param model provides JDBC URL and credentials and used with ec to locate the EOAdaptor
     * @param ec used with model to locate the EOAdaptor
     * @param shouldQualifyTableNames enables or disables multiple schemas support. If <code>true</code>,
     *        DBUnit accesses tables with names fully qualified by schema using this format: SCHEMA.TABLE.
     *        If <code>false</code>, the single schema name is set in the IDatabaseConnection.  If this
     *        is enabled, XML datasets need to have the schema included, e.g.
     *        &lt;table name="SCHEMA.TableName"&gt;
      * @param escapePattern optional pattern to escape names in SQL, "\"?\"" is the standard double quote
     *
     * @return the created and configured database connection
     *
     * @throws ClassNotFoundException if the JDBC driver class can't be loaded
     * @throws SQLException if a connection can't be made
     * @throws DatabaseUnitException if DBUnit can't connect
     */
    public static IDatabaseConnection createConnection(EOModel model,
                                                    EOEditingContext ec,
                                                    boolean shouldQualifyTableNames,
                                                    String escapePattern) throws ClassNotFoundException, SQLException, DatabaseUnitException
    {
        /** require [valid_model] model != null;
                    [valid_ec] ec != null;     **/
        NSDictionary connectionDictionary = model.connectionDictionary();

        // force loading of JDBC driver
        String driverName = (String) connectionDictionary.objectForKey(JDBCAdaptor.DriverKey);
        if (StringAdditions.isEmpty(driverName))
        {
            // Find the default driver name
            EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(model, ec);
            dbContext.lock();
            try
            {
                JDBCAdaptor jdbcAdaptor = (JDBCAdaptor)dbContext.adaptorContext().adaptor();
                driverName = jdbcAdaptor.plugIn().defaultDriverName();
            }
            finally
            {
                dbContext.unlock();
            }
        }
        Class.forName(driverName);

        Connection jdbcConnection = DriverManager.getConnection( (String) connectionDictionary.objectForKey(JDBCAdaptor.URLKey),
                                                                 (String) connectionDictionary.objectForKey(JDBCAdaptor.UsernameKey),
                                                                 (String) connectionDictionary.objectForKey(JDBCAdaptor.PasswordKey));
        String schemaName = shouldQualifyTableNames ? null : (String) connectionDictionary.objectForKey(JDBCAdaptor.UsernameKey);
        return getConnection(jdbcConnection, schemaName, escapePattern);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Locates one of the JDBC connections that EOF is using and configures the database connection.
     *
     * @param model used with ec to locate the EOAdaptor
     * @param ec used with model to locate the EOAdaptor
     * @param shouldQualifyTableNames enables or disables multiple schemas support. If <code>true</code>,
     *        DBUnit accesses tables with names fully qualified by schema using this format: SCHEMA.TABLE.
     *        If <code>false</code>, the single schema name is set in the IDatabaseConnection.  If this
     *        is enabled, XML datasets need to have the schema included, e.g.
     *        &lt;table name="SCHEMA.TableName"&gt;
     * @param escapePattern optional pattern to escape names in SQL, "\"?\"" is the standard double quote
     *
     * @return the configured database connection
     *
     * @throws DatabaseUnitException if DBUnit can't connect
     */
    public static IDatabaseConnection getConnection(EOModel model,
                                                    EOEditingContext ec,
                                                    boolean shouldQualifyTableNames,
                                                    String escapePattern) throws DatabaseUnitException
    {
        /** require [valid_model] model != null;
                    [valid_ec] ec != null;     **/
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(model, ec);
        dbContext.lock();
        try
        {
            JDBCAdaptor jdbcAdaptor = (JDBCAdaptor)dbContext.adaptorContext().adaptor();
            // Ensure there is an active context
            if (jdbcAdaptor.contexts().count() == 0)
            {
                jdbcAdaptor.createAdaptorContext();
            }
            JDBCContext jdbcContext = (JDBCContext)jdbcAdaptor.contexts().lastObject();
            return getConnection(jdbcContext, shouldQualifyTableNames, escapePattern);
        }
        finally
        {
            dbContext.unlock();
        }
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Grabs the current JDBC connection from the passed JDBCContext and uses that for DBUnit.
     *
     * @param context JDBCContext providing a JDBC Connection object to use
     * @param shouldQualifyTableNames enables or disables multiple schemas support. If <code>true</code>,
     *        Dbunit accesses tables with names fully qualified by schema using this format: SCHEMA.TABLE.
     *        If <code>false</code>, the single schema name is set in the IDatabaseConnection.  If this
     *        is enabled, XML datasets need to have the schema included, e.g.
     *        &lt;table name="SCHEMA.TableName"&gt;
     * @param escapePattern optional pattern to escape names in SQL, "\"?\"" is the standard double quote
     *
     * @return the configured database connection
     *
     * @throws DatabaseUnitException if DBUnit can't connect
     */
    public static IDatabaseConnection getConnection(JDBCContext context,
                                                    boolean shouldQualifyTableNames,
                                                    String escapePattern) throws DatabaseUnitException
    {
        /** require [valid_context] context != null;     **/
        String schemaName = shouldQualifyTableNames ? null : ((JDBCAdaptor)context.adaptor()).username();
        return getConnection(context.connection(), schemaName, escapePattern);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Uses the java.sql.Connection to create a database connection for DBUnit.
     *
     * @param connection java.sql.Connection to database
     * @param schemaName enables or disables multiple schemas support. If <code>null</code>,
     *        Dbunit accesses tables with names fully qualified by schema using this format: SCHEMA.TABLE.
     *        If <code>non-null</code>, this single schema name is set in the IDatabaseConnection.  If this
     *        is null, XML datasets need to have the schema included, e.g.
     *        &lt;table name="SCHEMA.TableName"&gt;
     * @param escapePattern optional pattern to escape names in SQL, "\"?\"" is the standard double quote
     *
     * @return the configured database connection
     *
     * @throws DatabaseUnitException if DBUnit can't connect
     */
    public static IDatabaseConnection getConnection(java.sql.Connection connection,
                                                    String schemaName,
                                                    String escapePattern) throws DatabaseUnitException
    {
        /** require [valid_context] connection != null;  **/
        IDatabaseConnection dbConnection;
        if (schemaName == null)
        {
            dbConnection = new DatabaseConnection(connection);
        }
        else
        {
            dbConnection = new DatabaseConnection(connection, schemaName);
        }

        DatabaseConfig dbConfig = dbConnection.getConfig();

        if (escapePattern != null)
        {
            dbConfig.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, escapePattern);
        }
        dbConfig.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, schemaName == null);

        return dbConnection;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a data set loaded from the named resource in the bundle.
     *
     * @param resourcePath name of the data file to construct a DataSet for
     * @return data set loaded from the named resource in the bundle
     * @throws DataSetException
     * @throws Exception if the data can't be loaded or the DataSet created
     */
    public static IDataSet getDataSet(NSBundle bundle, String resourcePath) throws DataSetException
    {
        /** require [valid_bundle] bundle != null;
                    [valid_resourcePath] resourcePath != null;     **/
        InputStream dataSet = bundle.inputStreamForResourcePath(resourcePath);

        if (dataSet == null)
        {
            throw new RuntimeException("Can't locate resource " + resourcePath + " in bundle " + bundle.name());
        }
        return new XmlDataSet(dataSet);
        /** ensure [valid_result] Result != null;  **/
    }



}
