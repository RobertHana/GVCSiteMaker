package net.global_village.eofvalidation;

import java.sql.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;
import com.webobjects.jdbcadaptor.*;

import er.extensions.eof.*;

import net.global_village.foundation.*;


/**
 * Helper methods for NSException for use in interperting non-EOF validation exceptions (e.g. from saveChanges()).
 *
 * @author Copyright (c) 2001-2007  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * version $Revision: 11$
 */
public class NSExceptionAdditions extends Object
{

    /**
     * String returned as the exception reason when a NSDateFormatter fails.
     */
    public static final String InvalidDateStringReason   = "Invalid date string";


    /**
     * String returned as the exception reason when a NSNumber fails.
     */
    public static final String InvalidNumberStringReason = "Invalid number";


    /**
     * Keypath to make accessing the contents of adaptor failure exceptions easier.
     */
    public static final String GVCValidationAdaptorExceptionKeypath = "EOFailedDatabaseOperationKey._adaptorOps._exception";

    /** Readable name for FrontBase, used with DatabaseTypeNamesForJDBCTypes and to to generate class names.  */
    public static final String FrontBase = "FrontBase";

    /** Readable name for Microsoft SQL Server, used with DatabaseTypeNamesForJDBCTypes and to to generate class names.  */
    public static final String SQLServer = "SQLServer";

    /** Readable name for Oracle, used with DatabaseTypeNamesForJDBCTypes and to to generate class names.  */
    public static final String Oracle = "Oracle";


    /**
     * Dictionary of upper-case names from JDBC URLs (e.g. SQLSERVER from jdbc:sqlserver:...) to readable names that are used
     * for other purposed, e.g. to generate class names (e.g. SQLServerExceptionInterpreter).
     */
    public static NSMutableDictionary DatabaseTypeNamesForJDBCTypes = new NSMutableDictionary(new String[] {FrontBase, SQLServer, Oracle},
                                                                                       new String[] {"FRONTBASE", "SQLSERVER", "ORACLE"});

    private static NSMutableDictionary ExceptionInterpreters = new NSMutableDictionary(3);


    /**
     * Designated constuctor.  Don't instantiate this class, use static methods.
     */
    private NSExceptionAdditions()
    {
        super();
    }



    /**
     * Returns <code>true</code> if this exception is from an EOAdaptorOperation failure.
     * This is detected by checking for the presence of EOFailedAdaptorOperationKey.
     * If this key is present, then this exception has the expected keys and can be analysed as an adaptor failure exception.
     *
     * @param exception the exception to check.
     * @return true if this exception is from an EOAdaptor failure
     */
    static public boolean isAdaptorOperationFailureException(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null; **/

        boolean isAdaptorOperationFailureException = false;
        if (exception.userInfo() != null)
        {
            isAdaptorOperationFailureException = exception.userInfo().objectForKey(EOAdaptorChannel.FailedAdaptorOperationKey) != null;
        }

        return isAdaptorOperationFailureException;
    }



    /**
     * Returns <code>true</code> if this exception is from a database base level failure.  This is the case if it is
     * an JDBCAdaptorException or if it has an EOFailedAdaptorOperationKey.
     *
     * @param exception the exception to check
     * @return true if this exception is from a database base level failure
     */
    static public boolean isDatabaseFailureException(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null; **/
        return isAdaptorOperationFailureException(exception) || exception instanceof JDBCAdaptorException;
    }



    /**
     * Returns <code>true</code> if this exception is from an EOAdaptor failure.  This is detected by checking for
     * the presence of EOAdaptorChannel.AdaptorFailureKey.  If this key is present, then this exception has the expected
     * keys and can be analysed as an adaptor failure exception.
     *
     * @param exception	the exception to check.
     * @return <code>true</code> if this exception is an optimistic locking failure
     */
    static public boolean isOptimisticLockingFailure(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null; **/

        boolean isOptimisticLockingFailure = false;

        if (exception.userInfo() != null)
        {
            String adaptorFailureKey = (String)exception.userInfo().objectForKey(EOAdaptorChannel.AdaptorFailureKey);
            if (adaptorFailureKey != null)
            {
                isOptimisticLockingFailure = adaptorFailureKey.equals(EOAdaptorChannel.AdaptorOptimisticLockingFailure);
            }
        }

        return isOptimisticLockingFailure;
    }



    /**
     * Returns <code>true</code> if this exception is from an integrity constraint violation.
     * This is detected by checking for an X/Open SQL CAE SQLState of 23XXX in the nested SQLException. See
     * linked pages for lists of the error codes.
     * <p>
     * The SQLException documentation also states that this can be an SQL 99 error state.  SQL99 codes
     * are not yet supported by this method.
     * </p>
     *
     * @see <a href="http://www.roth.net/perl/odbc/SQLState/">SQLState 1</a>
     * @see <a href="http://publib.boulder.ibm.com/infocenter/idshelp/v10/index.jsp?topic=/com.ibm.esqlc.doc/esqlc209.htm">SQLState 2</a>
     * @see <a href="http://www.halfile.com/odbc.html">SQLState 3</a>
     *
     * @param exception the exception to check.
     * @return <code>true</code> if this exception is an integrity constraint violation
     */
    static public boolean isIntegrityConstraintViolation(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null;    **/
        return isDatabaseFailureException(exception) && sqlException(exception) != null && sqlException(exception).getSQLState().startsWith("23");
    }



    /**
     * Returns the name of the violated constraint from an exception caused by an
     * integrity contstraint violation.
     *
     * @param exception exception caused by an integrity contstraint violation
     * @return name of the violated constraint
     */
    public static String violatedIntegrityConstraintName(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null;
                    [isIntegrityConstraintViolation] isIntegrityConstraintViolation(exception);    **/

        return exceptionInterpreterFor(databaseTypeFromException(exception)).violatedIntegrityConstraintName(sqlException(exception));
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Extracts and returns the SQLException that caused the EOGeneralAdaptorException.  If this happened on a specific
     * SQL statement (non deferred constraint), it will be an EOAdaptorOperation that failed.  If this happened
     * during commit (deferred constraint), there will be only the JDBCAdaptorException.
     *
     * @param exception the exception to extract the SQLException from
     * @return the SQLException that caused the EOGeneralAdaptorException
     */
    static public SQLException sqlException(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null;
                    [isDatabaseFailureException] isDatabaseFailureException(exception);       **/

        JDBCAdaptorException jdbcException = null;
        if (isAdaptorOperationFailureException(exception))
        {
            EOAdaptorOperation failedOperation = (EOAdaptorOperation)exception.userInfo().objectForKey(EOAdaptorChannel.FailedAdaptorOperationKey);
            Throwable opException = failedOperation.exception();
            if (opException == null || ! (opException instanceof com.webobjects.jdbcadaptor.JDBCAdaptorException))
            {
                throw new NSForwardException(exception, "EOGeneralAdaptorException failed operation has unexpected exception: " + opException);
            }
            jdbcException = (JDBCAdaptorException)opException;
        }
        else
        {
            jdbcException = (JDBCAdaptorException)exception;
        }

        return jdbcException.sqlException();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the object that the adaptor failure exception was raised for.
     *
     * @param exception	the exception to check.
     * @return the object that the adaptor failure exception was raised for.
     */
    static public EODatabaseOperation databaseOperation(EOGeneralAdaptorException exception)
    {
        /** require
        [valid_param] exception != null;
        [is_adaptor_failure_exception] isAdaptorOperationFailureException(exception); **/

        return (EODatabaseOperation)exception.userInfo().valueForKeyPath(EODatabaseContext.FailedDatabaseOperationKey);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the EO object that the adaptor failure exception was raised for. Can return
     * <code>null</code> if the exception refers to a delete of a row of a correlation table.
     *
     * @param exception	the exception to check.
     * @return the EO object that the adaptor failure exception was raised for.
     */
    static public EOEnterpriseObject objectSaveFailedOn(EOGeneralAdaptorException exception)
    {
        /** require
        [valid_param] exception != null;
        [is_adaptor_failure_exception] isAdaptorOperationFailureException(exception); **/

        EODatabaseOperation operation = databaseOperation(exception);
        return (EOEnterpriseObject)operation.object();
    }



    /**
     * Returns the entity associated with the database failure exception.  If this is
     * an EOAdaptorOperation level failure, the enity can be determined directly.  If this is
     * a JDDBAdaptor exception, the entity must be inferred from the exception returned from
     * the database.  This is not nearly as exact.
     *
     * @see DataBaseExceptionInterpreter#tableNameFromSQLException(SQLException)
     * @see ERXEOAccessUtilities#entityUsingTable(EOEditingContext, String)
     *
     * @param exception	the exception to check
     * @param ec to use to find entity if this is a JDBCAdaptorException
     * @return the entity for the object that the adaptor failure exception was raised for
     */
    static public EOEntity entitySaveFailedOn(EOGeneralAdaptorException exception, com.webobjects.eocontrol.EOEditingContext ec)
    {
        /** require
        [valid_exception] exception != null;
        [valid_ec] ec != null;
        [is_adaptor_failure_exception] isDatabaseFailureException(exception); **/

        if (isAdaptorOperationFailureException(exception))
        {
            EODatabaseOperation operation = databaseOperation(exception);
            return operation.entity();
        }

        /* We have a JDCBAdaptorException here.  We have only the SQLException, so all that we can
         * do is to try and extract the table name from the message and reverse that into an entity.
         * As one table can be used for multiple entities, this is at best approximate.
         */
        String tableName = exceptionInterpreterFor(databaseTypeFromException(exception)).tableNameFromSQLException(sqlException(exception));
        EOEntity entity = ERXEOAccessUtilities.entityUsingTable(ec, tableName);
        if (entity == null)
        {
            throw new IllegalArgumentException("Can't determine EOEntity for table named " + tableName + " from exception " + exception);
        }
        return entity;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the adaptor operation that was being performed when the adaptor failure exception occurred.
     *
     * @param exception	the exception to check.
     * @return the adaptor operation that was being performed when the adaptor failure exception occurred.
     */
    static public int failedAdaptorOperator(EOGeneralAdaptorException exception)
    {
        /** require
        [valid_param] exception != null;
        [is_adaptor_failure_exception] isAdaptorOperationFailureException(exception); **/

        // Although the name of the key EOFailedDatabaseOperationKey would suggest that a singular result is returned, the result is in fact an array.  The array has a single element which is a number (Adaptor Operators are defined as ints).
        NSArray adaptorOperators = databaseOperation(exception).adaptorOperations();
        return ((EOAdaptorOperation)adaptorOperators.objectAtIndex(0)).adaptorOperator();
    }



    /**
     * Returns the exception message from the database which caused the save to fail.
     *
     * @param exception	the exception to check.
     * @return the exception message from the database which caused the save to fail.
     */
    static public String databaseExceptionReason(EOGeneralAdaptorException exception)
    {
        /** require
        [valid_param] exception != null;
        [is_adaptor_failure_exception] isAdaptorOperationFailureException(exception); **/

        // Although the name of the key EOFailedDatabaseOperationKey would suggest that a singular result is returned, the result is in fact an array.  The array has a single element which is a String.
        EODatabaseOperation databaseOperation = databaseOperation(exception);
        EOAdaptorOperation adaptorOperation = (EOAdaptorOperation)databaseOperation.adaptorOperations().objectAtIndex(0);
        return adaptorOperation.exception().toString();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Extracts the type of database (e.g. FrontBase, Oracle, etc.) from the exception.  This is converted
     * to a standard form using DatabaseTypeNamesForJDBCTypes to translate the name extracted from the connection
     * URL (e.g. jdbc:FrontBase:...)
     *
     * @param exception EOGeneralAdaptorException to use to determine the database type
     * @return the type of database from the exception
     */
    public static String databaseTypeFromException(EOGeneralAdaptorException exception)
    {
        /** require [valid_param] exception != null;
                    [isDatabaseFailureException] isDatabaseFailureException(exception);    **/

        String databaseType = null;

        if (isAdaptorOperationFailureException(exception))
        {
            EOAdaptorOperation failedOperation = (EOAdaptorOperation)exception.userInfo().objectForKey(EOAdaptorChannel.FailedAdaptorOperationKey);
            String dbURL = (String)failedOperation.entity().model().connectionDictionary().objectForKey(JDBCAdaptor.URLKey);
            int firstColon = dbURL.indexOf(':');
            int secondColon = dbURL.indexOf(':', firstColon + 1);
            String jdbcType = dbURL.substring(firstColon + 1, secondColon);
            databaseType = (String)DatabaseTypeNamesForJDBCTypes.objectForKey(jdbcType.toUpperCase());
            if (databaseType == null)
            {
                throw new RuntimeException("DatabaseTypeNamesForJDBCTypes does not have a name defined for " + jdbcType);
            }
        }

        /* OK, this part is nasty.
         * We have a JDCBAdaptorException here.  We don't know the JDCBAdaptor, only the SQLException.
         * To work around this, we could implement the EOAdaptorContext.Delegate to track the last context to try to
         * commit in this thread and record the adaptor in the thread for use here.  We would then extract the
         * connectionDictionaryURL() from the adaptor.
         *
         * In the meantime, we will cheat by guessing from the message.
         */
        else if (exception.getMessage().startsWith("Exception condition"))
        {
            databaseType = FrontBase;
        }
        else {
            throw new RuntimeException("Can't determine database type from message " + exception.getMessage());
        }

        return databaseType;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Locates and returns a subclass of DataBaseExceptionInterpreter appropriate to the database type.
     * The database type is intended to be from databaseTypeFromException(EOGeneralAdaptorException), though
     * this is not required.
     *
     * @see #databaseTypeFromException(EOGeneralAdaptorException)
     * @param dbType String identifying the database, dbType + "ExceptionInterpreter" must be a valid class
     * @return a subclass of DataBaseExceptionInterpreter appropriate to the database type
     */
    public static DataBaseExceptionInterpreter exceptionInterpreterFor(String dbType)
    {
        /** require [non_null_type] dbType != null;   **/
        DataBaseExceptionInterpreter exceptionInterpreter = (DataBaseExceptionInterpreter) ExceptionInterpreters.objectForKey(dbType);

        // Lazy creation
        if (exceptionInterpreter == null)
        {
            Class exceptionInterpreterClass = _NSUtilities.classWithName(dbType + "ExceptionInterpreter");
            if (exceptionInterpreterClass == null)
            {
                throw new RuntimeException("Class not found for " + dbType + "ExceptionInterpreter");
            }

            try
            {
                exceptionInterpreter = (DataBaseExceptionInterpreter)exceptionInterpreterClass.newInstance();
                ExceptionInterpreters.setObjectForKey(exceptionInterpreter, dbType);
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }

        return exceptionInterpreter;
        /** ensure [valid_result] Result != null;  **/
    }


}
