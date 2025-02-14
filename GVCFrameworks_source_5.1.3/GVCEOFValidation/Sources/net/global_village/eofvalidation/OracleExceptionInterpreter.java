package net.global_village.eofvalidation;

import java.sql.*;
import java.util.regex.*;


/**
 * DatabaseExceptionInterpreter for Oracle.
 *
 * @author Copyright (c) 2001-2007  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * version $Revision: 2$
 */
public class OracleExceptionInterpreter extends DataBaseExceptionInterpreter
{
    /** Pattern to extract constraint name from message like "ORA-00001: unique constraint (MY_SCHEMA.MY_CONSTRAINT) violated" */
    // Regex testing: http://www.fileformat.info/tool/regex.htm
    public static final Pattern IntegrityConstraintNamePattern = Pattern.compile("(.*): (.* \\((.*)\\) .*)\n");



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
        return m.group(3);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
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
