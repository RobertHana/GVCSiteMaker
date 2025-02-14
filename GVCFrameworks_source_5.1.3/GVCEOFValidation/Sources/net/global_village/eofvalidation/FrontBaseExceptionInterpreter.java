package net.global_village.eofvalidation;

import java.sql.*;
import java.util.regex.*;


/**
 * DatabaseExceptionInterpreter for FrontBase.
 *
 * @author Copyright (c) 2001-2007  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * version $Revision: 6$
 */
public class FrontBaseExceptionInterpreter extends DataBaseExceptionInterpreter
{
    /** Pattern to extract constraint name from message like "Exception condition 360. Integrity constraint
     *  violation (UNIQUE, DAPL.UniqueName(name='foo')).
     */
    // Regex testing: http://www.fileformat.info/tool/regex.htm
    public static final Pattern IntegrityConstraintNamePattern = Pattern.compile(".*?\\(.*?, (.*?)\\.(.*?)\\(.*");



    /**
     * @see net.global_village.eofvalidation.DataBaseExceptionInterpreter#violatedIntegrityConstraintName(SQLException)
     */
    public String violatedIntegrityConstraintName(SQLException exception)
    {
        /** require [valid_exception] exception != null;  **/

        Matcher m = IntegrityConstraintNamePattern.matcher(exception.getMessage());
        if ( ! m.matches())
        {
            throw new RuntimeException("Pattern " + IntegrityConstraintNamePattern.pattern() + " failed to match SQLException message [" + exception.getMessage() + "]");
        }
        return m.group(2);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the name of the affected table from an exception caused by an
     * integrity constraint violation.
     *
     * @param exception exception caused by an integrity constraint violation
     * @return name of the affected table
     */
    public String tableNameFromSQLException(SQLException exception)
    {
        /** require [valid_exception] exception != null;  **/

        // The message is expected to look like this:
        // Exception condition 361. Integrity constraint violation (FOREIGN KEY, DownloadLog.FOREIGN_KEY_DownloadLog_oidUser_PPUser_oid(oidUser)).
        Matcher m = IntegrityConstraintNamePattern.matcher(exception.getMessage());
        if ( ! m.matches())
        {
            throw new RuntimeException("Pattern " + IntegrityConstraintNamePattern.pattern() + " failed to match SQLException message [" + exception.getMessage() + "]");
        }
        return m.group(1);

        /** ensure [valid_result] Result != null;  **/
    }



}
