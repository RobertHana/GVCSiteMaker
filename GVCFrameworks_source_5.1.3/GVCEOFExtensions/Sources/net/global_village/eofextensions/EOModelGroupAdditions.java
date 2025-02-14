package net.global_village.eofextensions;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Collection;

/**
 * Methods added to extended the functionality of EOModelGroup
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 8$
 */
public class EOModelGroupAdditions
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EOModelGroupAdditions()
    {
        super();
    }




    /**
     * Connects the named models with the provided connectionInfo and adaptorName.  Any existing connections for these models are broken.
     *
     * @param modelsToConnect the list of model names to connect
     * @param adaptorName the adaptor that the connectionInfo is for.
     * @param connectionDictionary the connection dictionary that will be used for connecting the models
     * @param modelGroup the EOModel group that these models are in.  Uses EOModelGroup.defaultGroup() if null.
     */
    public static void connectModelsNamed(NSArray modelsToConnect,
                                          String adaptorName,
                                          NSDictionary connectionDictionary,
                                          EOModelGroup modelGroup)
    {
        /** require
        [valid_modelsToConnect_param] modelsToConnect != null;
        [valid_adaptorName_param] adaptorName != null;
        [valid_connectionDictionary_param] connectionDictionary != null; **/
        JassAdditions.pre("EOModelGroupAdditions", "connectModelsNamed", modelsToConnect.count() > 0);
        JassAdditions.pre("EOModelGroupAdditions", "connectModelsNamed", modelGroup != null ? Collection.collectionIsSubsetOfCollection(modelGroup.modelNames(), modelsToConnect) : Collection.collectionIsSubsetOfCollection(EOModelGroup.defaultGroup().modelNames(), modelsToConnect));

        // Use default group if not specified.
        if (modelGroup == null)
        {
            modelGroup = EOModelGroup.defaultGroup();
        }

        Enumeration modelNameEnumerator = modelsToConnect.objectEnumerator();
        while(modelNameEnumerator.hasMoreElements())
        {
            String thisModelName = (String)modelNameEnumerator.nextElement();
            EOModel thisModel = modelGroup.modelNamed(thisModelName);

            thisModel.setAdaptorName(adaptorName);
            thisModel.setConnectionDictionary(connectionDictionary);
        }

        // Finally we can force a connection/reconnection.  For WOApps this is needed even if there has not been previous database activity.  It appears that WOApplication() connects all the models with the connection dictionary that they were loaded with, so that unless we force a disconnect and reconnect, these fetches will fail if the original connection dictionary is not correct.
        EOEditingContext ec = new EOEditingContext();
        EODatabaseContext.forceConnectionWithModel(modelGroup.modelNamed((String)(modelsToConnect.lastObject())), new NSDictionary(), ec);
    }



    /**
     * Calls connectModelsNamed(NSArray, String, NSDictionary, EOModelGroup) using the connection dictionary and adaptor name from the named model.
     * Use like:<code><pre>
     * EOModelGroup.connectModelsNamed(new NSArray (new Object [] {"GVCGenericObjects", "GVCeCommerce"}),
     *                                 "AppSpecificModel", null);
     * </pre></code>
     *
     * @param modelsToConnect the list of model names to connect
     * @param mainModelName the name of the model which contains the connection dictionary that will be used for connecting the models
     * @param modelGroup the model group that contains the models to connect.  Uses EOModelGroup.defaultGroup() if null.
     */
    public static void connectModelsNamed(NSArray modelsToConnect,
                                          String mainModelName,
                                          EOModelGroup modelGroup)
    {
        /** require
        [valid_mainModelName_param] mainModelName != null;
        [valid_modelsToConnect_param] modelsToConnect != null; **/
        JassAdditions.pre("EOModelGroupAdditions", "connectModelsNamed[models_in_group]", modelGroup != null ?
            (modelGroup.modelNamed(mainModelName) != null) &&
            (Collection.collectionIsSubsetOfCollection(modelGroup.modelNames(), modelsToConnect)) :
            (EOModelGroup.defaultGroup().modelNamed(mainModelName) != null) &&
            (Collection.collectionIsSubsetOfCollection(EOModelGroup.defaultGroup().modelNames(), modelsToConnect)));

        // Use default group if not specified.
        if (modelGroup == null)
        {
            modelGroup = EOModelGroup.defaultGroup();
        }
        EOModel mainModel = modelGroup.modelNamed(mainModelName);
        NSDictionary connectionDictionary = mainModel.connectionDictionary();
        String adaptorName = mainModel.adaptorName();
        NSArray allModels = modelsToConnect.arrayByAddingObject(mainModelName);

        connectModelsNamed(allModels, adaptorName, connectionDictionary, modelGroup);
    }



    /**
     * Looks in all named models for entities marked as "cache in memory" and fetches all objects in these tables so that the object store will be prepopulated.  This allows this to be done at program startup during a known time frame and also ensures that these objects are in memory and can be referred to during <code>awakeFromFetchInEditingContext</code> without firing a fault requiring database access.
     *
     * @param modelGroup the model group to be used for fetching
     * @param modelsToPrefetch the names of the models to be prefetched
     */
    public static void fetchAllCachedEntities(EOModelGroup modelGroup,
                                              NSArray modelsToPrefetch)
    {
        /** require
        [valid_modelGroup_param] modelGroup != null;
        [valid_modelsToPrefetch_param] modelsToPrefetch != null; **/
        JassAdditions.pre("EOModelGroupAdditions", "fetchAllCachedEntities", Collection.collectionIsSubsetOfCollection(modelGroup.modelNames(), modelsToPrefetch));

        EOEditingContext ec = new EOEditingContext();
        ec.lock();
        try
        {
            Enumeration modelNameEnumerator = modelsToPrefetch.objectEnumerator();
            while(modelNameEnumerator.hasMoreElements())
            {
                String eachModelName = (String) modelNameEnumerator.nextElement();

                EOModel eachModel = modelGroup.modelNamed(eachModelName);
                Enumeration entityEnumerator = eachModel.entities().objectEnumerator();

                while(entityEnumerator.hasMoreElements())
                {
                    EOEntity eachEntity = (EOEntity) entityEnumerator.nextElement();
                    if (eachEntity.cachesObjects())
                    {
                        EOUtilities.objectsForEntityNamed(ec, eachEntity.name());
                    }
                }
            }
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
    }



    /**
     * @param modelGroup the model group to be used for fetching
     * @return unordered list of all entities in the model group
     */
    public static NSArray entities(EOModelGroup modelGroup)
    {
        /** require
        [valid_modelGroup_param] modelGroup != null;   **/

        NSMutableArray entities = new NSMutableArray();
        Enumeration modelEnumerator = modelGroup.models().objectEnumerator();
        while(modelEnumerator.hasMoreElements())
        {
            EOModel eachModel = (EOModel) modelEnumerator.nextElement();
            entities.addObjectsFromArray(eachModel.entities());
        }

        return entities.immutableClone();
        /** ensure [valid_result] Result != null;  **/
    }



}
