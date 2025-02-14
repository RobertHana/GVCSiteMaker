package net.global_village.eofvalidation;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.appserver.*;

import net.global_village.foundation.*;


/**
 * EOModelNotificationProxy stands in for an EOModel to receive the EOClassDescription notifications
 * EOClassDescriptionNeededForClassNotification and EOClassDescriptionNeededForEntityNameNotification. The model itself
 * is disconnected from these notifications. This allows the proxy to register a special sub-class of EOClassDescription
 * in place of the EOEntityClassDescription that an EOModel returns by default. This appears to be the only way to get
 * EOF to use any other sub-class as there are no public methods that could be overridden nor any delegation methods on
 * either EOModelGroup or EOModel. The proxy also receives the EOModelGroup notification EOModelInvalidatedNotification
 * so that it can retire itself with the model.
 * <p>
 * <b>Notes</b>
 * <p>
 * <p>
 * These notifications are broadcast to all models and could be handled by a single object if there was only ever a
 * single model group. As this is not guaranteed, the notifications are handled on a model by model basis so that no
 * model "slips between the cracks".
 * <p>
 * <p>
 * These notifications must be handled on an on going basis as the cache of class descriptions is invalidated each time
 * a model is loaded (from constructor of EOModel). That is, they can not just be registered once at startup due to
 * repeated invalidations of the cache.
 * <p>
 * <p>
 * EOClassDescriptionNeededForClassNotification is sent for EO objects implemented as their own class.
 * EOClassDescriptionNeededForEntityNameNotification is sent for EO objects implemented as EOGenericRecord (many
 * entities share the same EOGenericRecord class).
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class EOModelNotificationProxy extends Object
{
    /**
     * This is needed to prevent the proxies from being garbage collected as notifications are referenced by the notification center through "weak" references, which don't show up on the garbage collection radar.
     */
    protected static Vector allProxies = new Vector();

    protected EOModel model;
    protected NSMutableDictionary cachedDescriptionsByEntityName;
    protected NSMutableDictionary cachedDescriptionsByClassName;


    /**
     * This method installs the EOFValidation "hook".  It is intended that it be called before any EOModels have been loaded (this is caused by EOF activity or direct references to EOModel or EOModelGroup).  The best place to call this is in the constructor of the main class in the system.  If this is called after EOModels have been loaded, all of those models will need to be invalidated and reloaded so that their notifications can be diconnected.
     */
    public static void listenForAddedModels()
    {
        // Listen for newly loaded models so that we can grab thier notifications.
        NSNotificationCenter defaultCenter = NSNotificationCenter.defaultCenter();
        NSSelector modelAddedNotification = new NSSelector("modelAddedNotification", new Class[] {NSNotification.class});
        defaultCenter.addObserver(EOModelNotificationProxy.class,
                                  modelAddedNotification,
                                  EOModelGroup.ModelAddedNotification,
                                  null);
        EOClassDescription.invalidateClassDescriptionCache();

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            new NotificationSquawker(EOModelGroup.ModelAddedNotification);
            new NotificationSquawker(EOClassDescription.ClassDescriptionNeededForClassNotification);
            new NotificationSquawker(EOClassDescription.ClassDescriptionNeededForEntityNameNotification);
            NSLog.debug.appendln("Installed notification squawker");
        }
    }



    /**
     * This notification is sent whenever a model is loaded or re-loaded (see EOModelGroup for details).  This is handled by creating a proxy to handle the notifications that this model would normally handle.
     *
     * @param notification notification of which model was added.
     * @see EOModelNotificationProxy#EOModelNotificationProxy
     */
    public static void modelAddedNotification(NSNotification notification)
    {
        JassAdditions.pre("EOModelNotificationProxy", "modelAddedNotification", EOModel.class.isAssignableFrom(notification.object().getClass()));

        EOModel model = (EOModel)notification.object();
        EOModelNotificationProxy proxy = new EOModelNotificationProxy(model);

        NSSelector selector = new NSSelector("setupEntity", new Class[] {NSNotification.class});
        NSNotificationCenter.defaultCenter().addObserver(proxy, selector, EOModel.EntityLoadedNotification, null);
    }



    /**
     * Creates cache(s) of class descriptions to speed up processing.
     *
     * @param proxy the proxy that we are setting up the caches for
     */
    protected static void createClassDescriptionCache(EOModelNotificationProxy proxy)
    {
        /** require [valid_param] proxy != null; **/

        synchronized (proxy)
        {
            proxy.cachedDescriptionsByEntityName = new NSMutableDictionary();
            proxy.cachedDescriptionsByClassName = new NSMutableDictionary();

            EOEntity thisEntity;
            Enumeration entityEnumerator = proxy.model().entities().objectEnumerator();
            while (entityEnumerator.hasMoreElements())
            {
                thisEntity = (EOEntity)entityEnumerator.nextElement();
                if (thisEntity.className() != null)	// Exclude prototypes and malformed entities
                {
                    EOClassDescription descriptionForEntity = proxy.eoEntityClassDescriptionSubClassForEntity(thisEntity);

                    proxy.cachedDescriptionsByEntityName.setObjectForKey(descriptionForEntity,  thisEntity.name());

                    // No need to cache for EOGenericRecord as they are never requested by class
                    if ( ! thisEntity.className().equals("EOGenericRecord"))
                    {
                        proxy.cachedDescriptionsByClassName.setObjectForKey(descriptionForEntity,  thisEntity.className());
                    }
                }
            }
        }
    }



    /**
     * Creates caches of class descriptions for all proxies to speed up processing.
     */
    protected static void createClassDescriptionCaches()
    {
        /** require [all_proxies_valid] allProxies != null; **/

        Enumeration proxyEnumeration = allProxies.elements();
        while (proxyEnumeration.hasMoreElements())
        {
            EOModelNotificationProxy proxy = (EOModelNotificationProxy)proxyEnumeration.nextElement();
            createClassDescriptionCache(proxy);
        }
    }



    /**
     * Initializes a proxy to stand in for the model.
     *
     * @param aModel model to stand in for.
     */
    public EOModelNotificationProxy(EOModel aModel)
    {
        super();
        /** require [valid_param] aModel != null; **/

        model = aModel;
        NSNotificationCenter defaultCenter = NSNotificationCenter.defaultCenter();
        // Warning: Do not call createClassDescriptionCache() here or it bungs up the linking of models when they load (inheritance is lost!).  This is too early to call methods on EOModel

        // In with the new...
        NSSelector classDescriptionNeededForClass =
            new NSSelector("classDescriptionNeededForClass", new Class[] { NSNotification.class } );
        defaultCenter.addObserver(this,
                                  classDescriptionNeededForClass,
                                  EOClassDescription.ClassDescriptionNeededForClassNotification,
                                  null);

        NSSelector classDescriptionNeededForEntityName =
            new NSSelector("classDescriptionNeededForEntityName", new Class[] { NSNotification.class } );
        defaultCenter.addObserver(this ,
                                  classDescriptionNeededForEntityName,
                                  EOClassDescription.ClassDescriptionNeededForEntityNameNotification,
                                  null);

        // Out with old...
        defaultCenter.removeObserver(model(), EOClassDescription.ClassDescriptionNeededForClassNotification, null);
        defaultCenter.removeObserver(model(), EOClassDescription.ClassDescriptionNeededForEntityNameNotification, null);

        // Watch for this so we know when to kill ourself.
        NSSelector modelInvalidatedNotification =
            new NSSelector("modelInvalidatedNotification", new Class[] { NSNotification.class } );
        defaultCenter.addObserver(this ,
                                  modelInvalidatedNotification,
                                  EOModelGroup.ModelInvalidatedNotification,
                                  null);

        // Prevent ourself from being garbage collected!
        allProxies.addElement(this);

        /** ensure [ivar_set] model() != null; **/
    }



    /**
     * Called by the EntityLoadedNotification.  Sets up the entity to have the correct class description, this one.
     *
     * @param notification the notification
     */
    public void setupEntity(NSNotification notification)
    {
        EOEntity anEntity = (EOEntity)notification.object();
        try
        {
            //HACK: We push the class description rather rudely into the entity to have it ready when classDescriptionForNewInstances() is called on it. We will have to add a com.webobjects.eoaccess.KVCProtectedAccessor to make this work
            synchronized (anEntity)
            {
                NSKeyValueCoding.Utility.takeValueForKey(anEntity, this, "classDescription");
            }
        }
        catch (RuntimeException ex)
        {
            //Ignore entities without class definitions
        }
    }



    /**
     * Returns a new instance of an EOEntityClassDescription sub-class initialized with this entity.  The default implementation uses EOFValidationEOEntityClassDescription.  Sub-classes can override this so that different sub-classes are used.
     *
     * @param anEntity entity to create class description for.
     * @return EOClassDescription class description for this entity
     */
    protected EOClassDescription eoEntityClassDescriptionSubClassForEntity(EOEntity anEntity)
    {
        return new EOFValidationEOEntityClassDescription(anEntity);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Tries to find a class description for the named entity.
     *
     * @param entityName name of entity to lookup class description for.
     * @return EOEntityClassDescription	class description for this entity or null if the entity is not in the model.
     */
    public synchronized EOEntityClassDescription classDescriptionForEntityNamed(String entityName)
    {
        /** require [valid_param] entityName != null; **/

        if (cachedDescriptionsByEntityName == null)
        {
            createClassDescriptionCaches();
        }
        return (EOEntityClassDescription)cachedDescriptionsByEntityName.objectForKey(entityName);
    }



    /**
     * Tries to find a class description for the named class.
     *
     * @param className name of class to lookup class description for.
     * @return EOEntityClassDescription	class description for this class or null if an entity with this className is not in the model
     */
    public synchronized EOEntityClassDescription classDescriptionForClassNamed(String className)
    {
        /** require [valid_param] className != null; **/

        if (cachedDescriptionsByClassName == null)
        {
            createClassDescriptionCaches();
        }
        return (EOEntityClassDescription)cachedDescriptionsByClassName.objectForKey(className);
    }



    /**
     * Returns the model that we are a proxy for.
     *
     * @return EOModel model we are a proxy for.
     */
    public EOModel model()
    {
        return model;
    }



    /**
     * Checks the model to see if it has an entity corresponding to this class and registers a class description for it if it does.  This is the most common notification.
     *
     * @param notification notification of which class a description is needed for.
     */
    public synchronized void classDescriptionNeededForClass(NSNotification notification)
    {
        /** require [valid_param] notification != null; **/

        // Try and find an entity in this model implemented by the class in the notification, or implemented by a superclass of the the class in the notification.
        Class classFromThisModel = (Class)notification.object();
        EOEntityClassDescription classDescriptionFromModel = null;
        while (classFromThisModel != null && classDescriptionFromModel == null)
        {
            classDescriptionFromModel = classDescriptionForClassNamed(classFromThisModel.getName());

            if (classDescriptionFromModel == null)
            {
                classFromThisModel = classFromThisModel.getSuperclass();
            }
        }

        // If the model does not contain the entity (class) requested, ask for it again.
        // Don't ask me why this works, but it prevents the "A class description of a generic record
        // cannot be null" exception.  I think there is a more efficient implementation for this, but
        // I am too tired now to see it and this works.
        if (classFromThisModel != (Class)notification.object())
        {
            // Voodoo.  Bug fixed in WO 5.4.2 (or at least causes a stack overflow error)
            if ( ! ERXApplication.isWO54())
            {
                EOClassDescription.classDescriptionForClass(((Class)notification.object()));
            }
        }
        // Otherwise we have the right description so register it.
        else if (classDescriptionFromModel != null)
        {
            EOClassDescription.registerClassDescription(classDescriptionFromModel, (Class)notification.object());

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Request for class " + notification.object() + " satisfied by model " + model().name());
            }
        }
    }



    /**
     * Checks the model to see if it has an entity with ths name and registers a class description for it if it does.  This notification is used with entities implemented as EOGenericRecords.
     *
     * @param notification notification of which entity a description is needed for.
     * @exception ClassNotFoundException if the className for the entity can not be resolved into a class
     */
    public synchronized void classDescriptionNeededForEntityName(NSNotification notification) throws ClassNotFoundException
    {
        /** require [valid_param] notification != null; **/

        String entityName = (String)notification.object();
        EOEntityClassDescription classDescription = classDescriptionForEntityNamed(entityName);
        if (classDescription != null)
        {
            Class entityClass = Class.forName(classDescription.entity().className());
            EOClassDescription.registerClassDescription(classDescription, entityClass);
            /** check [class_description_registered] EOClassDescription.classDescriptionForEntityName(entityName) == classDescription; **/

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Request for entity " + notification.object() + " satisfied by model " + model().name());
            }
        }
    }



    /**
     * This notification indicates that this proxy is no longer needed and can be destroyed.
     *
     * @param notification notification of which model is being invalidated.
     */
    public void modelInvalidatedNotification(NSNotification notification)
    {
        if (model().equals(notification.object()))
        {
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelInformational))
                NSLog.debug.appendln("GVCEOFValidation unhooking model " + ((EOModel)notification.object()).name());
            destroyProxy();
        }
    }



    /**
     * This method removes the proxy as an observer of events and releases any internal resources.
     */
    protected synchronized void destroyProxy()
    {
        NSNotificationCenter defaultCenter = NSNotificationCenter.defaultCenter();
        defaultCenter.removeObserver(this);
        model = null;

        if (cachedDescriptionsByEntityName != null)
        {
            cachedDescriptionsByEntityName.removeAllObjects();
            cachedDescriptionsByEntityName = null;
        }

        if (cachedDescriptionsByClassName != null)
        {
            cachedDescriptionsByClassName.removeAllObjects();
            cachedDescriptionsByClassName = null;
        }

        // Release ourself unto the garbage collector.
        allProxies.removeElement(this);

        /** ensure
        [model_reference_destroyed] model == null;
        [class_cache_destroyed] cachedDescriptionsByClassName == null;
        [entity_cache_destroyed] cachedDescriptionsByEntityName == null; **/
    }



}
