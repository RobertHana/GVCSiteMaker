package net.global_village.woextensions;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * <b>Scenario:</b>
 *  (saveChanges #1) an object is created and committed to database with a bunch of related objects. So far so good.
 *  (saveChanges #2) Then this object is deleted, including dependent objects. Those are deleted through "cascade" rule set in relationship. During saveChanges, delete transaction is processed correctly, but the exception something like below is thrown.
 *  EODatabase 0x26fb290 -- is unable to decrement snapshot count for object with global ID [GID: BlaBla, (1)] - count is  already 0 or this snapshot doesn't exist.
 * <b>Fix:</b>
      Invalidate all new objects right after commit
 *<b>Usage: (wrap saveChanges #1)</b><br><code>
         EOEditingContext ec = session().defaultEditingContext();
         WOSnapshotErrorFix bugFix = new WOSnapshotErrorFix(ec);
         ec.saveChanges();
         bugFix.postprocessOnSave();</code>
 *
 * @author Andrus Adamchik, email: andrus-wo@objectstyle.org
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class WOSnapshotErrorFix extends Object
{
    private EOEditingContext ec;
    private NSArray insertedObjects;
    
    public WOSnapshotErrorFix(EOEditingContext anEC)
    {
        super();
        ec = anEC;
        // do it by copy
        insertedObjects = new NSArray(anEC.insertedObjects());        
    }


    
    public void postprocessOnSave()
    {
        int len = insertedObjects.count();
        if (len > 0)
        {
            NSMutableArray gids = new NSMutableArray();
            for(int i = 0; i < len; i++)
            {
                EOGlobalID gid = ec.globalIDForObject((EOEnterpriseObject)insertedObjects.objectAtIndex(i));

                // if it's null, it must have been inserted then deleted before saving
                if (gid != null)
                {
                    gids.addObject(gid);
                }
            }
            ec.invalidateObjectsWithGlobalIDs(gids);
        }
    }
}
