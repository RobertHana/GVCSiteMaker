package net.global_village.eofextensions;

import com.webobjects.foundation.*;


/**
 * A collection of debugging related methods.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class EOFDebug
{

    /**
     * Convenience method to aid in debugging tests.  Turns EOAdaptor SQL logging on or off.
     *
     * @param shouldLog - <code>true</code> if SQL logging should be turned on, <code>false</code> if it should be turned off.
     */
    public static void logSQL(boolean shouldLog)
    {
        if (shouldLog)
        {
            NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupSQLGeneration
                                             | NSLog.DebugGroupDatabaseAccess
                                             | NSLog.DebugGroupEnterpriseObjects);
            NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelDetailed);
        }
        else
        {
            NSLog.refuseDebugLoggingForGroups(NSLog.DebugGroupSQLGeneration
                                              | NSLog.DebugGroupDatabaseAccess
                                              | NSLog.DebugGroupEnterpriseObjects);
            NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelOff);
        }
    }
}
