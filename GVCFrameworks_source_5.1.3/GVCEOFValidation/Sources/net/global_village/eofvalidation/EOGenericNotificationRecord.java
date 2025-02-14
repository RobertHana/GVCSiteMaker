package net.global_village.eofvalidation;

import net.global_village.eofextensions.*;



/**
 * EOGenericNotificationRecord adds a default (no operation) implmentation of EditingContextNotification
 * to <code>net.global_village.eofextensions.GenericRecord</code>.
 *
 *@see net.global_village.eofextensions.GenericRecord
 *@see EditingContextNotification
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class EOGenericNotificationRecord extends GenericRecord implements EditingContextNotification
{


    /**
     * Designated constructor.
     */
    public EOGenericNotificationRecord()
    {
        super();
    }



    /**
     * Called before insertObject is invoked on .
     */
    public void willInsert()
    {
    }


    /**
     * Called after insertObject is invoked on .
     */
    public void didInsert()
    {
    }


    /**
     * Called after a newly inserted object has been saved to the persistent object store (a.k.a. database).
     */
    public void hasInserted()
    {
    }


    /**
     * Called before deleteObject is invoked on .
     */
    public void willDelete()
    {
    }


    /**
     * Called after the deletion is processed by the EOEditingContext.  At this point the delete rules have been applied and changes propogated to related objects.
     */
    public void didDelete()
    {
    }


    /**
     * Called after a deleted object has been removed from the persistent object store.  This would handle the need to clean up related, non-EO, resources when the EO was deleted.
     */
    public void hasDeleted()
    {
    }


    /**
     * Called before saveChanges is invoked on .  This would allow things such as setting the dateModified before saving an EO.  Note that changes in this method may result in more updated (or inserted or deleted!) objects, so it should iterate until there are no more changes.
     */
    public void willUpdate()
    {
    }


    /**
     * Called after saveChanges is invoked on .
     */
    public void hasUpdated()
    {
    }



    /**
     * Called after revert is invoked on .
     */
    public void hasReverted()
    {
    }


}
