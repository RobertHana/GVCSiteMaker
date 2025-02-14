package net.global_village.eofvalidation;

import java.sql.*;


/**
 * DatabaseExceptionInterpreter for Microsoft's SQL Server. 
 *
 * @author Copyright (c) 2001-2007  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * version $Revision: 3$
 */
public class SQLServerExceptionInterpreter extends DataBaseExceptionInterpreter
{


    /**
     * @see net.global_village.eofvalidation.DataBaseExceptionInterpreter#violatedIntegrityConstraintName(java.sql.SQLException)
     */
    public String violatedIntegrityConstraintName(SQLException exception)
    {
        /** require [valid_exception] exception != null;  **/

        if (exception.getMessage().indexOf("UNIQUE KEY") > -1)
        {
            // The message looks like this:
            // Violation of UNIQUE KEY constraint 'UniqueName'. Cannot insert duplicate key in object 'dbo.DAPL'.
            // Single quotes cause some matching oddness, they don't match like other characters.  I give up.  - CH
            // String regex = ".*'(.*)('. )"; 
            String message = exception.getMessage();
            int firstQuote = message.indexOf('\'');
            int secondQuote = message.indexOf('\'', firstQuote + 1);
            return message.substring(firstQuote + 1, secondQuote);
        }
        else if (exception.getMessage().indexOf("REFERENCE") > -1)
        {
            // The message looks like this:
            // The DELETE statement conflicted with the REFERENCE constraint "DownloadLog_downloadedBy_FK". The conflict occurred in database "HHSPrepress", table "hhdev.DownloadLog", column 'oidUser'.
            // Yes, double not single quotes in this message.  
            String message = exception.getMessage();
            int firstQuote = message.indexOf('\"');
            int secondQuote = message.indexOf('\"', firstQuote + 1);
            return message.substring(firstQuote + 1, secondQuote);
        }
        else
        {
            throw new IllegalArgumentException("Can't interpret exception message " + exception.getMessage());
        }
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Throws RuntimeException as it is has no been necessary to implement this for SQL Server.
     * Returns the name of the affected table from an exception caused by an 
     * integrity contstraint violation.
     * 
     * @param exception exception caused by an integrity contstraint violation
     * @return name of the affected table
     */
    public String tableNameFromSQLException(SQLException exception)
    {
        throw new RuntimeException("Not Implemented");
    }



}
