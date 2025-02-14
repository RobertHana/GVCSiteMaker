package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;
import com.webobjects.eoaccess.EOQualifierSQLGeneration.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Methods added to extended the functionality of EOAdaptorChannel
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class EOSQLExpressionAdditions extends Object
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EOSQLExpressionAdditions()
    {
        super();
    }



    /**
     * Returns the result of evaluating the SQL expression.
     *
     * @param expression the expression to use to generate the SQL.
     * @param qualifier the qualifier that will be changed to a string.
     * @return a <code>String</code> representation of <code>qualifier</code>, suitable for passing to a database in a where clause.
     */
    public static String sqlStringForQualifier(EOSQLExpression expression,
                                               EOQualifier qualifier)
    {
        /** require
        [valid_expression_param] expression != null;
        [valid_qualifier_param] qualifier != null; **/

        Support support = EOQualifierSQLGeneration.Support.supportForClass(qualifier.getClass());
        qualifier = support.schemaBasedQualifierWithRootEntity(qualifier, expression.entity());
        support = EOQualifierSQLGeneration.Support.supportForClass(qualifier.getClass());
        return support.sqlStringForSQLExpression(qualifier, expression);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the Frontbase SQL String representation of the passed NSTimestamp as "TIMESTAMP '2002-01-01 23:23:01'"
     *
     * @param aDate the date for format
     * @param timezone the timezone that the date is in.  This will convert the NSTimestamps' time from GMT to this timezone
     * @return the Frontbase SQL String representation of the passed NSTimestamp.
     */
    public static String timestampAsFrontBaseSQLString(NSTimestamp aDate, NSTimeZone timezone)
    {
        /** require [aDate_not_null] aDate != null; [timezone_not_null] timezone != null; **/

        NSTimestampFormatter formatter = new NSTimestampFormatter("%Y-%m-%d %H:%M:%S");
        formatter.setDefaultFormatTimeZone(timezone);
        String sqlDate = formatter.format(aDate);

        return "TIMESTAMP '" + sqlDate + "'";

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Returns the Frontbase SQL String representation of the passed NSTimestamp as "TIMESTAMP '2002-01-01 23:23:01'".  The date is assumed to be in the default timezone.
     *
     * @param aDate the date for format
     * @return the Frontbase SQL String representation of the passed NSTimestamp.
     */
    public static String timestampAsFrontBaseSQLString(NSTimestamp aDate)
    {
        /** require [aDate_not_null] aDate != null; **/
        return timestampAsFrontBaseSQLString(aDate, (NSTimeZone)NSTimeZone.getDefault());
        /** ensure [Result_not_null] Result != null; **/
    }



}
