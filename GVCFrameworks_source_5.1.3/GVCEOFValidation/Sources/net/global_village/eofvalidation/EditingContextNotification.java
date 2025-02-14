package net.global_village.eofvalidation;


/**
 * The interface used by the NotifyingEditingContext to determine which EOs should receive notifications.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public interface EditingContextNotification
{

    /**
     * Called after a newly inserted object has been saved to the persistent object store (a.k.a. database).
     */
    public void hasInserted();

    /**
     * Called before deleteObject is invoked on our EOEditingContext.
     */
    public void willDelete();

    /**
     * Called after a deleted object has been removed from the persistent object store.  This would handle the need to clean up related, non-EO, resources when the EO was deleted.
     */
    public void hasDeleted();

    /**
     * Called before saveChanges is invoked on our EOEditingContext.  This would allow things such as setting the dateModified before saving an EO.  Note that changes in this method may result in more updated (or inserted or deleted!) objects, so it should iterate until there are no more changes.
     */
    public void willUpdate();

    /**
     * Called after saveChanges is invoked on our EOEditingContext.
     */
    public void hasUpdated();

    /**
     * Called after revert is invoked on our EOEditingContext.
     */
    public void hasReverted();

}
