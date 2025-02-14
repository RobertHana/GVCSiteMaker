package net.global_village.eofextensions;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.eof.*;

/**
 * <p>This class works around a bug in the 5.1.x and 5.2.x versions of WebObjects.  There is a bug
 * in EOEditingContext that causes problems when a fetch is performed while processing a saveChanges
 * messge.  When a fetch occurs a notification is sent to the editing context which causes it to
 * discard some cached information regarding inserted, updated, and deleted objects.  When this
 * happens during certain phases of the save it causes the editing context to lose track of what it
 * is doing.  This is a particular problem when processing delete rules.  This results in things
 * such as updates being sent instead of deletes and validation being performed after deletion
 * resulting in null required attributes where key fields have been set to null.</p>
 *
 * <p>The work around for this is to pre-process the delete rules for all obejcts being deleted to
 * ensure that all affected or referenced objects have been fetched before the save process is
 * allowed to start.  This does not fix the problem but it avoids the symptoms.</p>
 *
 * <p>It was originally thought that this problem would also be solved by using DeleteFiresFaultWorkAroundEditingContext.
 * However, when working on release 4.6 of GVC.SiteMaker, it was discovered that this does not address
 * all of the issues.  Delete operations get changed into updates, or deletes fail EOF update validation
 * if this class is not used.  This problem may be specific to some oddity in the EOModels used by GVC.SiteMaker.
 * </p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DeletePrefetcher
{
    private NSMutableArray processedObjects = new NSMutableArray();


    /**
     * Designated constructor.  Creates a new DeletePrefetcher and resets it.
     *
     */
    public DeletePrefetcher()
    {
        super();
        reset();
        /** ensure [processedObjects_cleared] processedObjects().count() == 0;    **/
    }



    /**
     * Ensures that all eo objects that are the target of delete rules from rootEO are not faults
     * and also ensure this for any objects that will be deleted as a result of cascade delete rules.
     * This is the main method to call.
     *
     * @param rootEO the EOEnterpriseObject that will be deleted
     */
    public void prefetchDeletionPathsFrom(EOEnterpriseObject rootEO)
    {
        /** require [valid_rootEO] rootEO != null;       **/

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("Prefetching delete rule targets for  " + EOObject.globalID(rootEO));
        }

        rootEO.willRead();  // Ensure fault cleared
        processedObjects.addObject(rootEO);
        processToManyRelationships(rootEO);
        processToOneRelationships(rootEO);
    }



    /**
     * Resets the state of DeletePrefetcher after a round of prefetching deletion paths.  This
     * should be called before or after calling prefetchDeletionPathsFrom(EOEnterpriseObject).
     */
    public void reset()
    {
        processedObjects.removeAllObjects();
        /** ensure [processedObjects_cleared] processedObjects().count() == 0;    **/
    }



    /**
     * Follows all the to-many relationships out of eo, bulk fetching the target of any delete rules
     * and restarting the cycle of prefetching along deletion paths for any that are the target of a
     * cascade rule.
     *
     * @param eo the EOEnterpriseObject to process to-many relationships on
     */
    protected void processToManyRelationships(EOEnterpriseObject eo)
    {
        /** require [valid_eo] eo != null;       **/

        Enumeration relationshipEnumerator = eo.toManyRelationshipKeys().objectEnumerator();
        while (relationshipEnumerator.hasMoreElements())
        {
            String relationshipName = (String)relationshipEnumerator.nextElement();
            NSArray relatedObjects = (NSArray) eo.valueForKey(relationshipName);
            EOEntity entity = EOObject.entityForSelf(eo);
            EORelationship relationship = entity.relationshipNamed(relationshipName);

            // Only process if there are related objects that will be affected by deletion
            if ((relatedObjects.count() > 0) && deleteRuleAffectsToMany(relationship.deleteRule()))
            {
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Prefetching objects in to-many relationship from " +
                        entity.name() + " to " + relationshipName);
                }

                // Bulk fetch related objects
                ERXRecursiveBatchFetching.batchFetch(new NSArray(eo), relationshipName);

                if (relationship.deleteRule() == EOClassDescription.DeleteRuleCascade)
                {
                    // Repeat process of prefetching along deletion path for cascade deleted objects
                    Enumeration relatedObjectEnumerator = relatedObjects.objectEnumerator();
                    while (relatedObjectEnumerator.hasMoreElements())
                    {
                        EOEnterpriseObject relatedObject =
                            (EOEnterpriseObject)relatedObjectEnumerator.nextElement();
                        if ( ! processedObjects().containsObject(relatedObject))
                        {
                            processInverseRelationship(relatedObject, relationship);
                            prefetchDeletionPathsFrom(relatedObject);
                        }
                    }
                }
            }
        }
    }



    /**
     * Follows all the to-one relationships out of eo, ensuring the targets of any delete rules are
     * not faults, and continues the cycle of prefetching along deletion paths for any that are the
     * target of a cascade rule.
     *
     * @param eo the EOEnterpriseObject to process to-many relationships on
     */
    protected void processToOneRelationships(EOEnterpriseObject eo)
    {
        /** require [valid_eo] eo != null;       **/

        Enumeration relationshipEnumerator =  eo.toOneRelationshipKeys().objectEnumerator();
        while (relationshipEnumerator.hasMoreElements())
        {
            String relationshipName = (String)relationshipEnumerator.nextElement();
            EOEntity entity = EOObject.entityForSelf(eo);
            EORelationship relationship = entity.relationshipNamed(relationshipName);
            if (deleteRuleAffectsToOne(relationship.deleteRule()))
            {
                EOEnterpriseObject relatedObject = (EOEnterpriseObject) eo.valueForKey(relationshipName);
                if (relatedObject != null)
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Prefetching object in to-one relationship from " +
                        entity.name() + " to " + relationshipName);
                    }

                    relatedObject.willRead();
                    if (relationship.deleteRule() == EOClassDescription.DeleteRuleCascade)
                    {
                        if ( ! processedObjects().containsObject(relatedObject))
                        {
                            prefetchDeletionPathsFrom(relatedObject);
                        }
                    }

                    processInverseRelationship(relatedObject, relationship);
                }
            }
        }
    }



    /**
     * If the relationship being fetched has an inverse to-many relationships we need to fetch all
     * the objects in that relationship so that those faults are not fired when this object is removed
     * from that relationship.
     *
     * @param relatedObject the target of the relationship
     * @param relationship the relationship to relatedObject, the relationship to check the inverse of
     */
    protected void processInverseRelationship(EOEnterpriseObject relatedObject, EORelationship relationship)
    {
        /** require [valid_eo] relatedObject != null;   [valid_relationship] relationship != null;     **/

        if (relationship.deleteRule() == EOClassDescription.DeleteRuleNullify)
        {
            EORelationship inverseRelationship = relationship.inverseRelationship();

            if ((inverseRelationship != null) && (inverseRelationship.isToMany()))
            {
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Prefetching inverse objects in inverse to-many relationship " +
                    inverseRelationship.name() + " from " + inverseRelationship.entity().name());
                }

                // Bulk fetch related objects
                ERXRecursiveBatchFetching.batchFetch(new NSArray(relatedObject), inverseRelationship.name());
            }
        }
    }



    /**
     * Returns <code>true</code> if the passed delete rule requires to-many relationships to be
     * faulted in.
     *
     * @param deleteRule the delete rule to check
     * @return <code>true</code> if the passed delete rule requires to-many relationships to be
     * faulted in
     * @see EOClassDescription
     */
    protected boolean deleteRuleAffectsToMany(int deleteRule)
    {
        /** require [valid_rule] (deleteRule == EOClassDescription.DeleteRuleNoAction) ||
                                 (deleteRule == EOClassDescription.DeleteRuleDeny) ||
                                 (deleteRule == EOClassDescription.DeleteRuleNullify) ||
                                 (deleteRule == EOClassDescription.DeleteRuleCascade);  **/

        return (deleteRule == EOClassDescription.DeleteRuleNullify) ||
               (deleteRule == EOClassDescription.DeleteRuleCascade);
    }



    /**
     * Returns <code>true</code> if the passed delete rule requires to-one relationships to be
     * faulted in.
     *
     * @param deleteRule the delete rule to check
     * @return <code>true</code> if the passed delete rule requires to-one relationships to be
     * faulted in
     * @see EOClassDescription
     */
    protected boolean deleteRuleAffectsToOne(int deleteRule)
    {
        /** require [valid_rule] (deleteRule == EOClassDescription.DeleteRuleNoAction) ||
                                 (deleteRule == EOClassDescription.DeleteRuleDeny) ||
                                 (deleteRule == EOClassDescription.DeleteRuleNullify) ||
                                 (deleteRule == EOClassDescription.DeleteRuleCascade);  **/

        return (deleteRule == EOClassDescription.DeleteRuleNullify) ||
               (deleteRule == EOClassDescription.DeleteRuleCascade);
    }



    /**
     * Returns the objects that have been processed so far.
     *
     * @return the objects that have been processed so far
     */
    protected NSMutableArray processedObjects()
    {
        return processedObjects;
    }


    /** invariant [has_processedObjects] processedObjects != null;     **/

}
