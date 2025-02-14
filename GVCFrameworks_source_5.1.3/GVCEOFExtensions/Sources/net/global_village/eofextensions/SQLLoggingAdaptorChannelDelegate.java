package net.global_village.eofextensions;


import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

/**
 * Watches the SQL that gets executed and logs out any statements that exceed a threshold in milliseconds.
 * The log message is prefixed by 'Slow Query' and contains the execution time in milliseconds,
 * the number of rows fetched, and the exact SQL used.  Each part is seperated by a tab so that the
 * lines can easily be grepped out and the resulting file parsed as tab delimited.  This makes it easy
 * to analyze with Excel. This delegate was inspired by the Project Wonder class er.extensions.ERXAdaptorChannelDelegate.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class SQLLoggingAdaptorChannelDelegate extends Object
{
    // The SQL statment being executed
    protected String statement;

    // The time that statment was sent to the database
    protected long startTimeMilliseconds;

    // The time that the result from statement was returned by the database
    protected long stopTimeMilliseconds;

    // The number of rows returned as a result of executing statement
    protected long rowsFetched = 0;

    /** The maximum SQL execution time before the statement is logged.  Use -1 to log everything. */
    protected static long queryThreshhold = Integer.MAX_VALUE;

    /** True if queries longer than queryThreshhold should be logged.     */
    protected static boolean shouldLogSlowQueries;

    /** True if the contents of fetched rows should be logged.    */
    protected static boolean shouldLogFetchedRows;

    /** True if a stack trace of the source of each SQL statement should be logged.    */
    protected static boolean shouldLogSQLSource;


    // DatabaseChannelNeededNotification handler
    protected static SQLLoggingAdaptorChannelDelegate handler;

    protected static final String LOG_MARKER = "Slow Query";


    /**
     * Call this method to install the SQL Logging delegate.  This method has no effect if called a
     * second time.
     *
     * @param logSlowQueries <code>true</code> if queries longer than threshold should be logged
     * @param threshold the maximum time, in milliseconds, that an SQL statement can take before it is logged
     * @param logFetchedRows <code>true</code> if the contents of fetched rows should be logged
     * @param logSQLSource <code>true</code> if a stack trace of the source of each fetch should be logged
     */
    public static void installDelegate(boolean logSlowQueries, long threshold, boolean logFetchedRows, boolean logSQLSource)
    {
        if (handler == null)
        {
            shouldLogFetchedRows = logFetchedRows;
            shouldLogSQLSource = logSQLSource;
            if (logSlowQueries)
            {
                // Tab delimited title row for log messages
                NSLog.out.appendln(LOG_MARKER + "\tTime\tRows\tSQL");
                queryThreshhold = threshold;
                shouldLogSlowQueries = logSlowQueries;
            }

            /* This delegate object is a special instance that catches the DatabaseChannelNeededNotification
               and creates a new instance of the SQLLoggingAdaptorChannelDelegate for each DB Channel.
               This is to avoid having the timings confused if more than one database channel / adaptor
               channel is created.  Yes, it would make more sense for this to be handled by an inner class,
               but Jass was puking on the static inner class.  Such is life with Jass.
              */
            handler = new SQLLoggingAdaptorChannelDelegate();
            NSNotificationCenter.defaultCenter().addObserver(handler,
                                                             new NSSelector("dataBaseChannelNeeded", new Class[] { NSNotification.class }),
                                                             EODatabaseContext.DatabaseChannelNeededNotification,
                                                             null);
        }
    }



    /**
     * Catches the EODatabaseContext.DatabaseChannelNeededNotification and creates a new instance of
     * SQLLoggingAdaptorChannelDelegate to act as a delegate for its EOAdaptorChannel.  Properly, this
     * should be in an inner class.
     *
     * @param notification EODatabaseContext.DatabaseChannelNeededNotification
     */
    public void dataBaseChannelNeeded(NSNotification notification) {
        NSLog.out.appendln("Installing SQLLoggingAdaptorChannelDelegate with threshold of " + queryThreshhold);
        EODatabaseContext context = (EODatabaseContext)notification.object();
        EODatabaseChannel channel = new EODatabaseChannel(context);
        context.registerChannel(channel);
        channel.adaptorChannel().setDelegate(new SQLLoggingAdaptorChannelDelegate());
     }




    /**
     * Notes the time before starting to execute a query, captures the query, and resets the fetched
     * row count.
     *
     * @param channel the EOAdaptorChannel this object is a delegate for
     * @param expression The expression to be sent to the database server
     *
     * @return <code>true</code> to permit the adaptor channel to send expression to the database server
     */
    public boolean adaptorChannelShouldEvaluateExpression(EOAdaptorChannel channel,  EOSQLExpression expression)
    {
        if(shouldLogSQLSource)
        {
            NSLog.out.appendln(new RuntimeException("SQL initiated from"));
        }

        statement = expression.statement();
        rowsFetched = 0;
        startTimeMilliseconds = System.currentTimeMillis();
        return true;
    }




    /**
     * Notes the time when a query has finished executing.
     *
     * @param channel the EOAdaptorChannel this object is a delegate for
     * @param expression the expression that was evaluated
     */
    public void adaptorChannelDidEvaluateExpression(EOAdaptorChannel channel,  EOSQLExpression expression)
    {
        stopTimeMilliseconds = System.currentTimeMillis();
    }



    /**
     * Increments the fetched row count.
     *
     * @param channel the EOAdaptorChannel this object is a delegate for
     * @param row data in the row just fetched
     */
    public void adaptorChannelDidFetchRow(EOAdaptorChannel channel, NSMutableDictionary row)
    {
        if (shouldLogFetchedRows)
        {
            NSLog.out.appendln("Fetched: " + row);
        }
        rowsFetched++;
    }



    /**
     * Compares the difference between stopTimeMilliseconds and startTimeMilliseconds and logs
     * statement if the difference is more than queryThreshhold.
     *
     * @param channel the EOAdaptorChannel this object is a delegate for
     */
    public void adaptorChannelDidFinishFetching(EOAdaptorChannel channel)
    {
        long millisecondsNeeded = stopTimeMilliseconds - startTimeMilliseconds;
        if(shouldLogSlowQueries && millisecondsNeeded > SQLLoggingAdaptorChannelDelegate.queryThreshhold)
        {
            NSLog.out.appendln(new RuntimeException("Fetched from"));
            NSLog.out.appendln(LOG_MARKER + "\t" + (millisecondsNeeded / 1000.0000) + "\t" + rowsFetched + "\t" + statement);
        }
    }

}
