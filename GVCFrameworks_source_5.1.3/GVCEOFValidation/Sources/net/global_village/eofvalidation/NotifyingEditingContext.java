package net.global_village.eofvalidation;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * NotifyingEditingContext extends EOEditingContext by providing users with the ability to get
 * notifications about inserting, updating and deleting objects to/from the object graph.  If you
 * want to get these notifications, first create an instance of this class, as you would a regluar
 * editing context, then implement the EditingContextNotification interface on any EO for which you
 * want to receive notifications (the EOGenericNotificationRecord class is provided to do some of
 * that work for you).  Finally, insert, update and delete objects from this editing context as you
 * would a normal editing context.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 8$
 */
public class NotifyingEditingContext extends net.global_village.eofvalidation.EOEditingContext
{
    protected NSMutableSet willDeleteSet = new NSMutableSet();
    protected NSMutableSet hasDeletedSet = new NSMutableSet();
    protected NSMutableSet hasUpdatedSet = new NSMutableSet();
    protected NSMutableSet hasInsertedSet = new NSMutableSet();

    private NSMutableSet notifiedWillUpdateSet = new NSMutableSet();
    private EOEnterpriseObject objectBeingNotified;



    /**
     * Designated constuctor.  Creates a new EOEditingContext object with anObjectStore as its parent object store.
     *
     * @param anObjectStore parent object store
     */
    public NotifyingEditingContext(EOObjectStore anObjectStore)
    {
        super(anObjectStore);
    }



    /**
     * Creates a new EOEditingContext object with the default parent object store as its parent object store.
     */
    public NotifyingEditingContext()
    {
        this(EOEditingContext.defaultParentObjectStore());
    }



    /**
     * Reset all the sets to be empty.  Called at the end of saveChanges() and revert().
     */
    protected void resetSets()
    {
        hasInsertedSet.removeAllObjects();
        willDeleteSet.removeAllObjects();
        hasDeletedSet.removeAllObjects();
        hasUpdatedSet.removeAllObjects();
        resetNotifiedWillUpdateSet();
    }



    /**
     * Overriden to record this object as inserted so that notifications can be sent.  This method,
     * rather than insertObject(), is used so that parent editing contexts will also get notified of
     * objects inserted and saved into one of their nested editing contexts.
     *
     * @param object the EOEnterpriseObject to be inserted into this editing context
     */
    protected void _insertObjectWithGlobalID(EOEnterpriseObject object, EOGlobalID eoglobalid)
    {
        /** require [valid_object] object != null;  **/

        hasInsertedSet.addObject(object);
        super._insertObjectWithGlobalID(object, eoglobalid);

        /** ensure [in_objectsToBeInserted] hasInsertedSet.containsObject(object);  **/
    }



    /**
     * Override deleteObject() to provide various notifications.
     */
    public void deleteObject(EOEnterpriseObject object)
    {
        /** require [valid_object] object != null;  **/
        hasDeletedSet.addObject(object);

        if (isNotifiableObject(object) && ( ! willDeleteSet.containsObject(object)))
        {
            willDeleteSet.addObject(object);
            ((EditingContextNotification)object).willDelete();
        }

        super.deleteObject(object);
        /** ensure [will_be_deleted_sent] ( ! isNotifiableObject(object)) ||
                                            willDeleteSet.containsObject(object);
                   [in_objectsToBeDeleted] hasDeletedSet.containsObject(object);  **/

    }



    /**
     * Override objectWillChange() to track objects which may be sent the willUpdate notification.
     * This is ignored if the object this is called for is <code>objectBeingNotified()</code>.  If
     * the object is already in <code>notifiedWillUpdateSet()</code> it is removed so that the
     * notification will be sent again.
     */
    public void objectWillChange(Object object)
    {
        /** require [valid_object] object != null;  **/

        if (object != objectBeingNotified())
        {
            hasUpdatedSet.addObject(object);
            notifiedWillUpdateSet().removeObject(object);
        }

        super.objectWillChange(object);
        /** ensure [valid_state] (object == objectBeingNotified()) ||
                                           (hasUpdatedSet.containsObject(object) &&
                                            ! notifiedWillUpdateSet().containsObject(object));  **/
    }



    /**
     * Override saveChanges() to provide various notifications.
     */
    public void saveChanges()
    {
        notifyWillUpdate();

        super.saveChanges();

        notifyHasDeleted();
        notifyHasInserted();
        notifyHasUpdated();

        resetSets();
    }



    /**
     * Override revert() to keep our modified objects in sync.
     */
    public void revert()
    {
        super.revert();
        notifyHasReverted();
        resetSets();
    }



    /**
     * <p>Sends the willUpdate() notification to all modified objects.  Objects receiving this
     * message may update other objects so this is done repeatedly until no more objects get
     * updated and each updated object receives this message.  An object may recieve this message
     * more than once if other objects being notified modify it.  The number of messages received
     * depends on the order in which updated objects are processed.  This is non-deterministic and
     * must not be relied upon.  The <code>willUpdate()</code> method should be written to handle
     * multiple calls in a single save cycle.  This class will prevent the <code>willUpdate()</code>
     * method from producing notification cycles.</p>
     * <p>Newly inserted and deleted objects will also receive this message.  This method is intended
     * to be called in the context of saveChanges().  Calling it at other times will result in the
     * willUpdate() message being sent.  If the save fails, the message will be sent again if the
     * save is attempted again.</p>
     * <p>This notification can be sent even if the object receiving it has not actually changed the
     * values.  If this is a concern, you can determine if any values were actually changed by testing
     * <code>changesFromSnapshot(editingContext().committedSnapshotForObject(this)).count() > 0</code>.
     * </p>
     */
    protected void notifyWillUpdate()
    {
        NSSet unNotifiedObjectsToBeUpdated = new NSSet(hasUpdatedSet);
        resetNotifiedWillUpdateSet();

        while (unNotifiedObjectsToBeUpdated.count() > 0)
        {
            Enumeration objectsToBeUpdatedEnumerator =
                unNotifiedObjectsToBeUpdated.objectEnumerator();
            while (objectsToBeUpdatedEnumerator.hasMoreElements())
            {
                setObjectBeingNotified((EOEnterpriseObject)objectsToBeUpdatedEnumerator.nextElement());
                if (isNotifiableObject(objectBeingNotified()))
                {
                    ((EditingContextNotification) objectBeingNotified()).willUpdate();
                }

                // Ensure that we don't send the notification more than once
                notifiedWillUpdateSet().addObject(objectBeingNotified());
            }

            // Call process recent changes so that inserts and deletes are processed.
            // This may result in more updated objects.
            processRecentChanges();

            unNotifiedObjectsToBeUpdated = hasUpdatedSet.setBySubtractingSet(notifiedWillUpdateSet());
        }

        // Reset to avoid interfering with willChange()
        resetNotifiedWillUpdateSet();
        setObjectBeingNotified(null);
    }



    /**
     * Sends the hasDeleted() notification to all the objects which have just been removed from
     * the persistent object store.
     */
    protected void notifyHasDeleted()
    {
        Enumeration objectsToBeDeletedEnumerator =
            hasDeletedSet.objectEnumerator();
        while (objectsToBeDeletedEnumerator.hasMoreElements())
        {
            Object deletedObject = objectsToBeDeletedEnumerator.nextElement();
            if (isNotifiableObject((EOEnterpriseObject)deletedObject))
            {
                ((EditingContextNotification) deletedObject).hasDeleted();
            }
        }
    }



    /**
     * Sends the hasInserted() notification to all the new objects which have just been saved
     * to the persistent object store.
     */
    protected void notifyHasInserted()
    {
        Enumeration objectsToBeInsertedEnumerator =
            hasInsertedSet.objectEnumerator();
        while (objectsToBeInsertedEnumerator.hasMoreElements())
        {
            Object insertedObject = objectsToBeInsertedEnumerator.nextElement();
            if (isNotifiableObject((EOEnterpriseObject)insertedObject))
            {
                ((EditingContextNotification) insertedObject).hasInserted();
            }
        }
    }



    /**
     * Sends the hasUpdated() notification to all the updated objects which have just been saved
     * to the persistent object store.  Note that this is not sent to objects which recieve
     * hasInserted or hasDeleted().
     */
    public void notifyHasUpdated()
    {
        // Don't send this notification to objects which have just been inserted or deleted.
        NSMutableSet updatesOnly = hasUpdatedSet.mutableClone();
        updatesOnly.subtractSet(hasDeletedSet);
        updatesOnly.subtractSet(hasInsertedSet);

        Enumeration objectsToBeUpdatedEnumerator = updatesOnly.objectEnumerator();
        while (objectsToBeUpdatedEnumerator.hasMoreElements())
        {
            Object updatedObject = objectsToBeUpdatedEnumerator.nextElement();
            if (isNotifiableObject((EOEnterpriseObject)updatedObject))
            {
                ((EditingContextNotification) updatedObject).hasUpdated();
            }
        }
    }



    /**
     * Sends the hasReverted() notification to all the objects registered in this editing
     * context.
     */
    public void notifyHasReverted()
    {
        // Don't send this notification to objects which have just been inserted or deleted.
        NSMutableSet updatedEOs = hasUpdatedSet.mutableClone();

        Enumeration objectsToBeUpdatedEnumerator = updatedEOs.objectEnumerator();
        while (objectsToBeUpdatedEnumerator.hasMoreElements())
        {
            Object updatedObject = objectsToBeUpdatedEnumerator.nextElement();
            if (isNotifiableObject((EOEnterpriseObject)updatedObject))
            {
                ((EditingContextNotification) updatedObject).hasReverted();
            }
        }
    }



    /**
     * Returns <code>true</code> if eo should get notifications from this editing context
     *
     * @param eo the EOEnterpriseObject to check
     * @return <code>true</code> if eo should get notifications from this editing context
     */
    public boolean isNotifiableObject(EOEnterpriseObject eo)
    {
        /** require [valid_eo] eo != null;  **/

        return eo instanceof EditingContextNotification;
    }



    /**
     * Returns the set of objects already notified willUpdate in this save cycle.  Returns an empty
     * set if willUpdate notification is not in progress.
     *
     * @return the set of objects already notified willUpdate in this save cycle
     */
    protected NSMutableSet notifiedWillUpdateSet()
    {
        return notifiedWillUpdateSet;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Removes all objects from the set of objects already notified willUpdate in this save cycle
     */
    protected void resetNotifiedWillUpdateSet()
    {
        notifiedWillUpdateSet.removeAllObjects();
    }


    /**
     * Sets the eo object currently being sent the willUpdate notification.
     *
     * @param eoObject  the eo object currently being sent the willUpdate notification
     */
    protected void setObjectBeingNotified(EOEnterpriseObject eoObject)
    {
        objectBeingNotified = eoObject;
    }


    /**
     * Returns the eo object currently being sent the willUpdate notification.  This is used to
     * prevent notification cycles if the eo calls willChange() while processing the notification.
     *
     * @return the eo object currently being sent the willUpdate notification
     */
    protected EOEnterpriseObject objectBeingNotified()
    {
        return objectBeingNotified;
    }
}
