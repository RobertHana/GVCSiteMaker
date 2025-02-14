package net.global_village.eofextensions;

import java.util.*;

import org.apache.log4j.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Work around for a rather nasty EOEditingContext bug.  See DeleteFiresFaultWorkAroundEditingContext.txt
 * for a detailed explanation.
 *
 * @author Lenny Marks, original idea and code
 * @author Chuck Hill, fix for notifications broadcast from EC being saved
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revsion:$
 */
public class DeleteFiresFaultWorkAroundEditingContext extends ForgetfulEC
{


    private boolean savingChanges;
    private NSMutableArray queuedNotifications = new NSMutableArray();
    private Thread threadSavingEditingContext;
    private Logger logger = LoggerFactory.makeLogger();


    public DeleteFiresFaultWorkAroundEditingContext()
    {
        super();
    }



    public DeleteFiresFaultWorkAroundEditingContext(EOObjectStore anObjectStore)
    {
        super(anObjectStore);
    }



    public void saveChanges()
    {
        _EOAssertSafeMultiThreadedAccess("saveChanges()");
        savingChanges = true;

        // Need to track this so we can properly process notifications that this EC sends as part of the saveChanges process
        setThreadSavingEditingContext(Thread.currentThread());
        try
        {
            super.saveChanges();
        }
        finally
        {
            setThreadSavingEditingContext(null);
            savingChanges = false;
        }

        processQueuedNotifications();
    }



    public void _objectsChangedInStore(NSNotification nsnotification)
    {
        /* Only queue notifications from other threads.  If we queue notifications that are send as a result
         * of saving this EC, then the save objects will get turned into faults causing excess fetching among
         * other problems (e.g. with out caching).
         *
         * HOWEVER - doing this may just make the original problem return!
         * This bug and patch needs to be researched and re-implemented or verified.

            at com.webobjects.eocontrol.EOFaultHandler.makeObjectIntoFault(EOFaultHandler.java:158)
            at com.webobjects.eoaccess.EODatabaseContext._turnToFaultGidEditingContextIsComplete(EODatabaseContext.java:3518)
            at com.webobjects.eoaccess.EODatabaseContext.refaultObject(EODatabaseContext.java:3624)
            at com.webobjects.eocontrol.EOObjectStoreCoordinator.refaultObject(EOObjectStoreCoordinator.java:699)
            at com.webobjects.eocontrol.EOEditingContext.refaultObject(EOEditingContext.java:4062)
            at com.webobjects.eocontrol.EOEditingContext._refaultObjectWithGlobalID(EOEditingContext.java:3293)
            at com.webobjects.eocontrol.EOEditingContext._newChangesFromInvalidatingObjectsWithGlobalIDs(EOEditingContext.java:3522)
            at com.webobjects.eocontrol.EOEditingContext._processObjectStoreChanges(EOEditingContext.java:3558)
            at net.global_village.eofvalidation.EOEditingContext._processObjectStoreChanges(EOEditingContext.java:380)
            ...
            at com.webobjects.eocontrol.EOEditingContext._sendOrEnqueueNotification(EOEditingContext.java:4784)
            at com.webobjects.eocontrol.EOEditingContext._objectsChangedInStore(EOEditingContext.java:3598)
            at net.global_village.eofextensions.DeleteFiresFaultWorkAroundEditingContext._objectsChangedInStore(DeleteFiresFaultWorkAroundEditingContext.java:76)
            at net.global_village.eofextensions.DeleteFiresFaultWorkAroundEditingContext.processQueuedNotifications(DeleteFiresFaultWorkAroundEditingContext.java:96)
            at net.global_village.eofextensions.DeleteFiresFaultWorkAroundEditingContext.saveChanges(DeleteFiresFaultWorkAroundEditingContext.java:62)
            at net.global_village.eofvalidation.EOEditingContext.saveChanges(EOEditingContext.java:133)
         */
        if (savingChanges &&
            ! Thread.currentThread().equals(threadSavingEditingContext()))
        {
            queuedNotifications.addObject(nsnotification);
        }
        else
        {
            super._objectsChangedInStore(nsnotification);
        }
    }



    private void processQueuedNotifications()
    {
        logger.debug("processQueuedNotifications()");

        synchronized(queuedNotifications)
        {
            for(Enumeration e = queuedNotifications.objectEnumerator(); e.hasMoreElements();)
            {
                NSNotification n = (NSNotification)e.nextElement();
                if (logger.isTraceEnabled())
                {
                    logger.trace("Processing " + n.userInfo());
                }
                super._objectsChangedInStore(n);
            }
            queuedNotifications.removeAllObjects();
        }
    }



    /**
     * @return the thread that is running saveChanges() on this EC or null if a save is not in progress
     */
    public Thread threadSavingEditingContext()
    {
        return threadSavingEditingContext;
    }



    /**
     * @param thread the thread that is running saveChanges() on this EC or null if a save is not in progress
     */
    public void setThreadSavingEditingContext(Thread thread)
    {
        threadSavingEditingContext = thread;
    }

 }
