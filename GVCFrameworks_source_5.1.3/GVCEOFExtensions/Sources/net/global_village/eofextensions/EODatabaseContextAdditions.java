package net.global_village.eofextensions;

import com.webobjects.foundation.*;

import er.extensions.eof.*;


/**
 * Methods added to extended the functionality of EODatabaseContext
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class EODatabaseContextAdditions extends Object
{

    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EODatabaseContextAdditions()
    {
        super();
    }



    /**
     * Prefetches the named relationship on each of the objects in sourceEOs.  This is intended for optimization.<br>
     * <b>Warning:</b> Becareful of how you use this method.  It will force a fetch from the database even if the objects are already cached in the object store.  Used incorrectly this can reduce, instead of increase, performance.
     *
     * @param sourceEOs the list of EOEnterpriseObjects to prefetch the relationship named relationshipName for
     * @param relationshipName the named of the relationship to prefetch on sourceEOs
     *
     * @deprecated Use ERXRecursiveBatchFetching
     */
    public static void preloadRelationship(NSArray sourceEOs, String relationshipName)
    {
        /** require
        [sourceEOs_valid] sourceEOs != null;
        [relationshipName_valid] relationshipName != null;
        [relationship_exists] true;  /# (forall i : {0 .. sourceEOs.count() - 1} # ((EOEnterpriseObject)sourceEOs.objectAtIndex(i)).toManyRelationshipKeys().containsObject(relationshipName) || ((EOEnterpriseObject)sourceEOs.objectAtIndex(i)).toOneRelationshipKeys().containsObject(relationshipName) ); #/          **/


        ERXRecursiveBatchFetching.batchFetch(sourceEOs, relationshipName);

//         if (sourceEOs.count() != 0)
//         {
//             EOEnterpriseObject sampleEO = (EOEnterpriseObject) sourceEOs.objectAtIndex(0);
//             EOEditingContext ec = sampleEO.editingContext();
//
//             EOObjectStoreCoordinator osc = (EOObjectStoreCoordinator) ec.rootObjectStore();
//             osc.lock();
//             try
//             {
//                 EOEntity entity = EOModelGroup.modelGroupForObjectStoreCoordinator(osc).entityNamed(sampleEO.entityName());
//                 EODatabaseContext databaseContext =  EODatabaseContext.registeredDatabaseContextForModel(entity.model(), osc);
//                 EORelationship relationship = entity.relationshipNamed(relationshipName);
//                /** check [relationship_exists] relationship != null;  /# Too hard to make precondition #/  **/
//
//                databaseContext.batchFetchRelationship(relationship, sourceEOs, ec);
//             }
//             finally
//             {
//                 osc.unlock();
//             }
//         }
    }


}
