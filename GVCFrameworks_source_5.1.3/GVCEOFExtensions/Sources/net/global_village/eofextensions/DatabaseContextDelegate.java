package net.global_village.eofextensions;


import org.apache.log4j.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;

import net.global_village.foundation.*;


/**
 * EODatabaseContext.Delegate implementation installed by PrincipalClass.
 *
 * @author Copyright (c) 2001-2009  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 */
public class DatabaseContextDelegate
{

    private Logger logger = LoggerFactory.makeLogger();


    /**
     * Delegate on EODatabaseContext that gets called when a to-one fault cannot find its data in the database. The object is
     * a cleared fault. We raise here to restore the functionality that existed prior to WebObjects 4.5.  Whenever a fault fails
     * for a globalID (i.e. the object is NOT found in the database), we may raise.
     *
     * Source: Kelly Hawk, http://wodeveloper.com/omniLists/eof/2000/December/msg00149.html
     */
    public boolean databaseContextFailedToFetchObject(com.webobjects.eoaccess.EODatabaseContext context,
                                                      java.lang.Object object,
                                                      com.webobjects.eocontrol.EOGlobalID globalID)
    {
        EOEditingContext ec = ((EOEnterpriseObject) object).editingContext();

        // we need to refault the object before raising, otherwise, if the caller traps the exception, it will be a successful
        // lookup the next time a fault with the same global id fires.  NOTE: refaulting in a sharedEditingContext is illegal,
        // so we specifically check for that special case.
        if (! (ec instanceof EOSharedEditingContext))
        {
            context.refaultObject((EOEnterpriseObject) object, globalID, ec);
        }

        String failureMessage = "Failed to fetch " + object.getClass() + " with globalID " + globalID;
        logger.error(failureMessage);
        throw new EOObjectNotAvailableException(failureMessage);
    }


}
