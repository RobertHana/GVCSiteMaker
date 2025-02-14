package net.global_village.eofvalidation;

import java.sql.*;



/**
 * Super-class for exception interpreters for specific databases.
 *
 * @author Copyright (c) 2001-2007  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * version $Revision: 3$
 */
public abstract class DataBaseExceptionInterpreter
{

    /**
     * Returns the name of the violated constraint from an exception caused by an
     * integrity constraint violation.
     *
     * @param exception exception caused by an integrity constraint violation
     * @return name of the violated constraint
     */
    public abstract String violatedIntegrityConstraintName(SQLException exception);



    /**
     * Returns the name of the affected table from an exception caused by an
     * integrity constraint violation.
     *
     * @param exception exception caused by an integrity constraint violation
     * @return name of the affected table
     */
    public abstract String tableNameFromSQLException(SQLException exception);


}
