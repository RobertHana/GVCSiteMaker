package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;

/**
 * Methods added to extended the functionality of EOAdaptorChannel
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class EOAdaptorChannelAdditions extends Object
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EOAdaptorChannelAdditions()
    {
        super();
    }



    /**
     *  Returns the result of evaluating the SQL expression.
     *
     * @param expression the expression to be evaluated
     * @return null if the expression doesn't fetch anything (i.e. an UPDATE statement) or if the result was an SQL null; an object of any type (i.e. NSCalendarDate, NSString, NSNumber depending on the expression), if a single value is returned; an array of dictionarys if the expression returns multiple rows or columns.  This is the same format as a rawRows fetch
     */
    public static Object resultOfEvaluatingSQLExpression(EOAdaptorChannel adaptorChannel,
                                                         EOSQLExpression expression)
    {
        /** require
        [valid_adaptorChannel_param] adaptorChannel != null;
        [valid_expression_param] expression != null; **/
        JassAdditions.pre("EOAdaptorChannelAdditions", "resultOfEvaluatingSQLExpression", adaptorChannel.isOpen());
        JassAdditions.pre("EOAdaptorChannelAdditions", "resultOfEvaluatingSQLExpression", expression.statement() != null);

        Object result = null;

        NSMutableArray resultRows = new NSMutableArray();
        NSMutableDictionary nextRow;

        // If channel.evaluateExpression throws when committing, it won't close the JDBC transaction
        // Probably a bug in JDBCChannel, but we must take care of it
        boolean contextHadOpenTransaction = adaptorChannel.adaptorContext().hasOpenTransaction();
        try
        {
            adaptorChannel.evaluateExpression(expression);
        }
        catch (EOGeneralAdaptorException e)
        {
            if (adaptorChannel.adaptorContext().hasOpenTransaction() && ! contextHadOpenTransaction)
            {
                adaptorChannel.adaptorContext().rollbackTransaction();
            }
            throw e;
        }

        if (adaptorChannel.isFetchInProgress())
        {
            // Need to call this before actually fetching data!
            NSArray attributes = adaptorChannel.describeResults();

            // Hack to force SQL like "SELECT SUM(quantity)..." to return a BigDecimal instead of a double.
            if ((attributes.count() == 1) && (((EOAttribute)attributes.objectAtIndex(0)).valueType().equals("d")))
            {
                ((EOAttribute)attributes.objectAtIndex(0)).setValueType("B");
            }

            adaptorChannel.setAttributesToFetch(attributes);

            while (adaptorChannel.isFetchInProgress())
            {
                nextRow = adaptorChannel.fetchRow();
                if (nextRow != null)
                {
                    resultRows.addObject(nextRow);
                }
            }
        }

        switch (resultRows.count())
        {
            case 0 :		// No rows were returned, result is null.  Should this ever happen? MM: This happens during our Unit Tests.
                result = null;
                break;

            case 1 :		// A single row was returned, we either have a row, or a single value...
                if (((NSDictionary) (resultRows.objectAtIndex(0))).count() == 1)
                {
                    // We have a single value.
                    result = (((NSDictionary) (resultRows.objectAtIndex(0))).allValues()).objectAtIndex(0);

                    if (result == NSKeyValueCoding.NullValue)
                    {
                        result = null;
                    }
                }
                else
                {
                    // Multiple columns were returned, so the result is the same as a rawRows fetch.
                    result = resultRows;
                }
                break;

            default :	// Multiple rows were returned, so the result is the same as a rawRows fetch.
                result = resultRows;
        }

        return result;
    }



}
