// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;


import net.global_village.foundation.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;


public class UsageLog extends _UsageLog {

    /**
     * Factory method to create new instances of UsageLog.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of UsageLog or a subclass.
     */
    public static UsageLog newUsageLog()
    {
        return (UsageLog) SMEOUtils.newInstanceOf("UsageLog");

        /** ensure [result_not_null] Result != null; **/
    }

 
    /**
     * Factory method to create new instances of UsageLog given the user, inserted into an ec.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of UsageLog or a subclass.
     */
    public static UsageLog newUsageLog(User user, EOEditingContext ec)
    {
        /** require
        [user_not_null] user != null;
        [ec_not_null] ec != null; **/
        
        UsageLog newUsageLog = newUsageLog();
        ec.insertObject(newUsageLog);

        newUsageLog.setAccessTime(Date.now());
        newUsageLog.setUser(user);
        DebugOut.println(1,"inserted UsageLog event for user: " + user.userID() );

        JassAdditions.post("UsageLog", "newUsageLog()", newUsageLog.user().equals(user));
        JassAdditions.post("UsageLog", "newUsageLog()", ec.insertedObjects().containsObject(newUsageLog));

        return newUsageLog;


        /** ensure [result_not_null] Result != null; **/
    }


}
