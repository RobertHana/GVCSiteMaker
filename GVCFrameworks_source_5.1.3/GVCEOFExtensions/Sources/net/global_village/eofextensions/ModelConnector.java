package net.global_village.eofextensions;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * <p>ModelConnector handles configuring the connection dictionary and adaptor name
 * in EOModels at runtime.  It can ignore some models if they are not needed or if
 * they need different connection information. Note that this class updates the
 * connection dictionary <em>as the models are loaded</em>.  This means that if a model gets
 * loaded before you instantiate this class, it's connection dictionary will not be
 * updated.</p>
 *
 * <p>If an EOModel already has a set adaptor type (e.g. JDBC, JDNI) and that type
 * is not the same as our <code>adaptorType()</code> then the model will also be
 * ignored.  This prevents one connector (e.g. JDBC) from trashing models
 * (e.g. JDNI) models.</p>
 *
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 16$
 */
public class ModelConnector extends Object
{
    // TODO Retest bug hack (are they still needed?)
    protected NSDictionary connectionDictionary;
    protected NSArray modelNamesToIgnore;
    protected String adaptorName;
    protected EOModelGroup modelGroup;


    /**
     * Designated constructor for the default model group, creates a ModelConnector to use
     * the indicated connection dictionary and adaptor name when updating all models not named
     * in <code>ingoredModels</code>.  Only models with an adaptorType of <code>theAdaptorName</code>
     * or with no adaptorName, in the default model group are updated.
     *
     * @param aConnectionDictionary the connection dictionary to connect all the EOModels with
     * @param theAdaptorName the name of the EOAdaptor that aConnectionDictionary is for
     * @param theModelNamesToIgnore NSArray of the names of models to <b>not</b> update with connectionDictionary()
     */
    public ModelConnector(NSDictionary aConnectionDictionary,
            String theAdaptorName,
            NSArray theModelNamesToIgnore)
    {
        // Be careful not to call EOModelGroup.defaultGroup() or this.modelGroup() until the observer is registered
        super();
        /** require
        [valid_aConnectionDictionary_param] aConnectionDictionary != null;
        [valid_adaptorName] theAdaptorName != null;
        [valid_theModelNamesToIgnore_param] theModelNamesToIgnore != null; **/

        connectionDictionary = aConnectionDictionary;
        adaptorName = theAdaptorName;
        modelNamesToIgnore = theModelNamesToIgnore;

        // This notification method does not check which group the model is in, thus avoiding infinite recursion
        addObserver("handleModelAddedInDefaultGroupNotification");

        // Causes the EOModels to be loaded from the bundles referenced on the classpath.
        EOModelGroup.defaultGroup().models();

        // EOF bug work around
        if (modelGroup().models().count() > 0)
        {
            registerModels();
        }

        // Now switch the notification method to the one that checks for model groups
        removeObserver();
        addObserver("handleModelAddedInCustomGroupNotification");
    }


    /**
     * Convenience constructor for JDBC connections in the default model group.
     *
     * @param aConnectionDictionary the connection information to use
     * @param ignoredModels list of names of models that should no be processed
     */
    public ModelConnector(NSDictionary aConnectionDictionary,
            NSArray ignoredModels)
    {
        this(aConnectionDictionary, "JDBC", ignoredModels);

        /** require [valid_connectionDictionary] aConnectionDictionary != null;
                    [valid_ignoredModels] ignoredModels != null;  **/
    }



    /**
     * Designated constructor for custom model groups, creates a ModelConnector to use the
     * indicated connection dictionary and adaptor name when updating all models not named in
     * <code>ingoredModels</code>.  Only models with an adaptorType of <code>theAdaptorName</code>
     * or with no adaptorName, and in the given model group, are updated.
     *
     * <p><b>Using a custom model group</b><br>
     * Since this class sets the connection dictionary of models <em>as they are loaded</em>, you need
     * to order your code carefully when using a custom model group:<br>
     * <code>
     * EOModelGroup myModelGroup = new EOModelGroup();
     * new ModelConnector(dict, adaptor, ignore, myModelGroup);
     * myModelGroup.addModel(myModel);
     * </code></p>
     *
     * @param aConnectionDictionary the connection dictionary to connect all the EOModels with
     * @param theAdaptorName the name of the EOAdaptor that aConnectionDictionary is for
     * @param theModelNamesToIgnore NSArray of the names of models to <b>not</b> update with connectionDictionary()
     * @param theModelGroup restricts connections to models in this model group, <code>null</code> indicates the default model group, do <b>not</b> pass in EOModelGroup.defaultGroup()
     */
    public ModelConnector(NSDictionary aConnectionDictionary,
            String theAdaptorName,
            NSArray theModelNamesToIgnore,
            EOModelGroup theModelGroup)
    {
        super();
        /** require
        [valid_aConnectionDictionary_param] aConnectionDictionary != null;
        [valid_adaptorName] theAdaptorName != null;
        [valid_theModelNamesToIgnore_param] theModelNamesToIgnore != null;
        [valid_theModelGroup_param] theModelGroup != null; **/

        connectionDictionary = aConnectionDictionary;
        adaptorName = theAdaptorName;
        modelNamesToIgnore = theModelNamesToIgnore;
        modelGroup = theModelGroup;

        // Establish this instance as a handler of this notification so that the EOModels can be updated with the proper connection dictionary.
        addObserver("handleModelAddedInCustomGroupNotification");

        // EOF bug work around
        if (modelGroup().models().count() > 0)
        {
            registerModels();
        }
    }


    /**
     * Convenience constructor for JDBC connections.  See the designated constructor above
     * for instructions on how to use this class with a custom model group.
     *
     * @param aConnectionDictionary the connection information to use
     * @param ignoredModels list of names of models that should no be processed
     * @param theModelGroup restricts connections to models in this model group, <code>null</code> indicates the default model group, do <b>not</b> pass in EOModelGroup.defaultGroup()
     */
    public ModelConnector(NSDictionary aConnectionDictionary,
            NSArray ignoredModels,
            EOModelGroup theModelGroup)
    {
        this(aConnectionDictionary, "JDBC", ignoredModels, theModelGroup);

        /** require [valid_connectionDictionary] aConnectionDictionary != null;
                    [valid_ignoredModels] ignoredModels != null;
                    [valid_theModelGroup] theModelGroup != null; **/
    }



    /**
     * Register for notifications.
     *
     * @param methodName the method name that will handle the notifications
     */
    protected void addObserver(String methodName)
    {
        NSSelector modelAddedSelector = new NSSelector(methodName, new Class[] { NSNotification.class } );
        NSNotificationCenter.defaultCenter().addObserver(this, modelAddedSelector, EOModelGroup.ModelAddedNotification, null);
    }


    /**
     * Unregister us from notifications so that we are not messaged.
     */
    protected void removeObserver()
    {
        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("ModelConnector: removing observer for: " + this);
        }

        // It is not clear to me if this can happen with NSNotificationCenter's weak references but this can not hurt
        NSNotificationCenter.defaultCenter().removeObserver(this);
    }



    /**
     * Unregister us from notifications so that we are not messaged.
     */
    protected void finalize()
    {
        removeObserver();
    }



    /**
     * Registers all of the EOModels that we have switched connection dictionaries for with their
     * EODatabase provided that the model's name is not in modelNamesToIgnore().  This was done to
     * work around a bug in this situation: two EOModels, an EOEntity in one is a sub-class of an
     * EOEntity in the other, and an instance of the sub-class is created before any entities in the
     * model containing the superclass are created.  This results in this exception:<br>
     * <br>
     * java.lang.NullPointerException<br>
     * at com.webobjects.jdbcadaptor.FrontbasePlugIn._newPrimaryKey(FrontbasePlugIn.java:215)<br>
     * at com.webobjects.jdbcadaptor.FrontbasePlugIn.newPrimaryKeys(FrontbasePlugIn.java:205)<br>
     * at com.webobjects.jdbcadaptor.JDBCChannel.primaryKeysForNewRowsWithEntity(JDBCChannel.java:544)<br>
     * at com.webobjects.eoaccess.EODatabaseContext._batchNewPrimaryKeysWithEntity(EODatabaseContext.java:5697)<br>
     * at com.webobjects.eoaccess.EODatabaseContext.prepareForSaveWithCoordinator(EODatabaseContext.java:5676)<br>
     * at com.webobjects.eocontrol.EOObjectStoreCoordinator.saveChangesInEditingContext(EOObjectStoreCoordinator.java:389)<br>
     * at com.webobjects.eocontrol.EOEditingContext.saveChanges(EOEditingContext.java:2486)<br>
     * <br>
     * It appears that when the sub-class referred to its parent that it was not found in the database.  This code just registeres each EOModel with it's database.
     */
    public void registerModels()
    {
        /** require [has_models] modelGroup().models().count() > 0; **/

        // There are probably other and possibly better ways to accomplish this.
        String anEntityName =  modelWithEntities().entityNames().lastObject();
        EODatabaseDataSource ds = new EODatabaseDataSource(new EOEditingContext(), anEntityName);
        EODatabaseContext dbContext = ds.databaseContext();
        dbContext.lock();
        try
        {
            EODatabase db = dbContext.database();

            NSArray allModels = modelGroup().models();
            Enumeration modelEnumerator = allModels.objectEnumerator();
            while (modelEnumerator.hasMoreElements())
            {
                EOModel aModel = (EOModel)modelEnumerator.nextElement();

                if (connectionDictionary().equals(aModel.connectionDictionary()))
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("ModelConnector: registering model: " + aModel.name());
                    }

                    if ( ! db.addModelIfCompatible(aModel))
                    {
                        NSLog.debug.appendln("Correct adaptor type? " + db.adaptor().name().equals(aModel.adaptorName()));
                        NSLog.debug.appendln("db.adaptor().name()  " + db.adaptor().name());
                        NSLog.debug.appendln("aModel.adaptorName()  " + aModel.adaptorName());
                        NSLog.debug.appendln("Connection dictionaries match? " + db.adaptor().canServiceModel(aModel));
                        NSLog.debug.appendln("aModel.connectionDictionary()  " + aModel.connectionDictionary());
                        NSLog.debug.appendln("db.adaptor().connectionDictionary() " + db.adaptor().connectionDictionary());
                        throw new RuntimeException("Unhandled condition: model '" + aModel.name() + "' is not compatible with database");
                    }
                }
            }
        }
        finally
        {
            dbContext.unlock();
        }
    }



    /**
     * Get an eo model from the model group that contains at least one entity.
     *
     * @return an eo model with at least one entity
     */
    public EOModel modelWithEntities()
    {
        /** require [has_models] modelGroup().models().count() > 0; **/

        EOModel resultModel = null;
        NSArray allModels = modelGroup().models();
        Enumeration modelEnumerator = allModels.objectEnumerator();
        while (modelEnumerator.hasMoreElements())
        {
            EOModel aModel = (EOModel)modelEnumerator.nextElement();
            if ((aModel.entityNames().count() > 0) &&  ( ! modelNamesToIgnore().containsObject(aModel.name())))
            {
                resultModel = aModel;
                break;
            }
        }
        return resultModel;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Connect the passed in model using this object's connection dictionary and adaptor name.
     *
     * @param theModel the model we are connecting
     */
    public void connectModel(EOModel theModel)
    {
        /** require [valid_param] theModel != null; **/

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("ModelConnector: Connecting model: " + theModel.name() +
                    " with dictionary " + connectionDictionary());
        }

        theModel.setConnectionDictionary(connectionDictionary());
        theModel.setAdaptorName(adaptorName());

        /** ensure
        [connection_dictionary_set] theModel.connectionDictionary().equals(connectionDictionary());
        [adaptor_name_set] theModel.adaptorName().equals(adaptorName()); **/
    }



    /**
     * The object in the notification is the model being added.  If this model's name is not in modelNamesToIgnore(), overrides the connection dictionary in the loaded model with connectionDictionary().
     *
     * @param aNotification notification of which EOModel is being added
     */
    public void handleModelAddedInDefaultGroupNotification(NSNotification aNotification)
    {
        /** require [valid_param] aNotification != null; **/

        EOModel theModel = (EOModel) aNotification.object();

        if ( ! isIgnoringModel(theModel))
        {
            connectModel(theModel);
        }
        else if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("ModelConnector: Skipping model: " + theModel.name());
        }

        /** ensure [not_ignored_means_connection_disctionary_set]
        ( ! isIgnoringModel((EOModel)aNotification.object())) ? ((EOModel)aNotification.object()).connectionDictionary().equals(connectionDictionary()) : true; **/
    }



    /**
     * The object in the notification is the model being added.  If this model's name is not in modelNamesToIgnore(), overrides the connection dictionary in the loaded model with connectionDictionary().
     *
     * @param aNotification notification of which EOModel is being added
     */
    public void handleModelAddedInCustomGroupNotification(NSNotification aNotification)
    {
        /** require [valid_param] aNotification != null; **/

        EOModel theModel = (EOModel)aNotification.object();

        if (( ! isIgnoringModel(theModel)) && (modelGroup().models().containsObject(theModel)))
        {
            connectModel(theModel);
        }
        else if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("ModelConnector: Skipping model: " + theModel.name());
        }

        /** ensure [not_ignored_and_in_our_model_group_means_connection_disctionary_set]
        (( ! isIgnoringModel((EOModel)aNotification.object())) && (modelGroup().models().containsObject((EOModel)aNotification.object()))) ? ((EOModel)aNotification.object()).connectionDictionary().equals(connectionDictionary()) : true; **/
    }



    /**
     * Returns <code>true</code> if <code>aModel</code> will be ignored by this
     * EOModelConnector instance.  The model will be ignored if it is in the list
     * of models to ignore, if it has an adaptor type which is not compatible
     * (not null and not our adaptor type), or if it does not belong to the model
     * group that we are connecting.
     *
     * @param aModel the EOModel to check
     * @return <code>true</code> if <code>aModel</code> will be ignored by this
     * EOModelConnector instance
     */
    public boolean isIgnoringModel(EOModel aModel)
    {
        /** require [aModel_valid] aModel != null; **/
        return (modelNamesToIgnore().containsObject(aModel.name()) ) ||
               ( (aModel.adaptorName() != null) &&
                 ( ! aModel.adaptorName().equals(adaptorName())) );
    }



    /**
     * Returns the connection dictionary for all EOModels handled by this ModelConnector instance.  A connection dictionary looks like this;
     * <br><br>
     * <pre>
     * connectionDictionary = {
     *          URL = "jdbc:oracle:thin:@OTTAWA:1521:GVC";
     *          driver = "";
     *          password = smaker2;
     *          plugin = "";
     *          username = smaker2;
     * };
     * </pre>
     */
    public NSDictionary connectionDictionary()
    {
        return connectionDictionary;

        /** ensure
        [valid_result] Result != null;
        [url_key_is_present] Result.objectForKey("URL") != null; **/
    }



    /**
     * Returns the list of EOModel names to be ignored by this ModelConnector instance.
     */
    public NSArray modelNamesToIgnore()
    {
        return modelNamesToIgnore;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the adaptor type matching <code>connectionDictionary()</code>.
     *
     * @return the adaptor type matching <code>connectionDictionary()</code>
     */
    public String adaptorName()
    {
        return adaptorName;
    }



    /**
     * Returns the adaptor type matching <code>connectionDictionary()</code>.
     *
     * @return the adaptor type matching <code>connectionDictionary()</code>
     */
    public EOModelGroup modelGroup()
    {
        if (modelGroup == null)
        {
            modelGroup = EOModelGroup.defaultGroup();
        }
        return modelGroup;
    }



    /** invariant
    [has_connection_dictionary] connectionDictionary != null;
    [has_model_name_to_ignore] modelNamesToIgnore != null;
    [has_adaptor_name] adaptorName != null; **/



}
