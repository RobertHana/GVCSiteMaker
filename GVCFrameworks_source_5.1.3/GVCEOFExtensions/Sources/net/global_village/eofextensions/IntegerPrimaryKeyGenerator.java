package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Generates PKs for given entities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public abstract class IntegerPrimaryKeyGenerator
{
    public static final int DefaultNumberOfPKsToCache = 100;

    protected int numberOfPKsToCache;

    /** Holds the entities for which this will generate PKs. */
    protected NSMutableSet cachePKsForEntities = new NSMutableSet();

    /** Holds the first cached value with the entity name as the key. */
    protected NSMutableDictionary pkInitialValues = new NSMutableDictionary();

    /** Holds the number of values we've used with the entity name as the key. */
    protected NSMutableDictionary pkAmountUsed = new NSMutableDictionary();


    /**
     * Designated constructor.
     *
     * @param pksToCache the number of PKs to cache
     */
    public IntegerPrimaryKeyGenerator(int pksToCache)
    {
        super();
        numberOfPKsToCache = pksToCache;
    }



    /**
     * Constructor that sets the number of PKs to cache to the default value.
     */
    public IntegerPrimaryKeyGenerator()
    {
        this(DefaultNumberOfPKsToCache);
    }



    /**
     * Returns <code>true</code> if this PK generator generates PKs for the given entity.
     *
     * @param entity the entity to check
     */
    public boolean cachesPKsForEntity(EOEntity entity)
    {
        /** require [valid_param] entity != null; **/
        return cachePKsForEntities.containsObject(entity);
    }



    /**
     * Tells this to start generating PKs for the given entity.
     *
     * @param entity the entity whose PKs we will cache
     */
    public void cachePKsForEntity(EOEntity entity)
    {
        /** require [valid_param] entity != null; **/
        cachePKsForEntities.addObject(entity);
        /** ensure [caching_for_entity] cachePKsForEntities.containsObject(entity); **/
    }



    /**
     * Tells this to start generating PKs for all entities in the given model.
     *
     * @param model the model whose entities we will cache PKs for
     */
    public void cachePKsForModel(EOModel model)
    {
        /** require [valid_param] model != null; **/
        cachePKsForEntities.addObjectsFromArray(model.entities());
        /** ensure [caching_for_entities_of_model] (forall i : {0 .. model.entities().count() - 1} # cachePKsForEntities.containsObject(model.entities().objectAtIndex(i))); **/
    }



    /**
     * Returns <code>true</code> if the PK cache for the given entity name is used up, <code>false</code> otherwise.
     *
     * @param entity the entity whose PK cache we are checking
     * @return <code>true</code> if the PK cache for the given entity name is used up, <code>false</code> otherwise
     */
    protected boolean needAdditionalPKs(EOEntity entity)
    {
        /** require [valid_param] entity != null; **/

        Number amountUsed = (Number)pkAmountUsed.objectForKey(entity.name());
        return (amountUsed == null) || (amountUsed.intValue() > numberOfPKsToCache);
    }



    /**
     * Adds additional PKs to the cache for the given entity.  The default implementation just caches the PKs in memory, which, of course, isn't terribly useful...
     *
     * @param entity the entity whose PK cache we are caching
     * @param dbCtxt the database context from which we will get the PKs
     */
    protected void cacheAdditionalPKs(EOEntity entity, EODatabaseContext dbCtxt)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_dbCtxt_param] dbCtxt != null; **/

        Integer amountUsed = (Integer)pkAmountUsed.objectForKey(entity.name());
        pkAmountUsed.setObjectForKey(new Integer(0), entity.name());
        Integer initialValue = (Integer)pkInitialValues.objectForKey(entity.name());
        if (initialValue == null)
        {
            initialValue = new Integer(0);
        }
        else
        {
            initialValue = new Integer(initialValue.intValue() + amountUsed.intValue());
        }
        pkInitialValues.setObjectForKey(initialValue, entity.name());

        /** ensure
        [no_pks_used] ((Integer)pkAmountUsed.objectForKey(entity.name())).intValue() == 0;
        [has_initial_value] pkInitialValues.objectForKey(entity.name()) != null; **/
    }



    /**
     * Invoked to provide a PK for a VT entity.  This is part of EODatabaseContext's delegate interface.
     *
     * @param dbCtxt the database context
     * @param object a newly inserted EO, or null if you're doing raw rows inserts
     * @param entity the EOEntity object
     * @return a dictionary containing the PK attribute names and their values, or <code>null</code> if this method didn't generate a PK for the given object
     */
    public synchronized NSDictionary databaseContextNewPrimaryKey(EODatabaseContext dbCtxt, Object object, EOEntity entity)
    {
        /** require
        [valid_dbCtxt_param] dbCtxt != null;
        [valid_entity_param] entity != null; **/
        if (cachesPKsForEntity(entity))
        {
            if (object instanceof PregeneratesPrimaryKey &&
                            ((PregeneratesPrimaryKey)object).pregeneratedPrimaryKey() != null)
            {
                return new NSDictionary(((PregeneratesPrimaryKey)object).pregeneratedPrimaryKey(), entity.primaryKeyAttributeNames().objectAtIndex(0));
            }

            // Get the top-level entity
            while (entity.parentEntity() != null)
            {
                entity = entity.parentEntity();
            }

            if (needAdditionalPKs(entity))
            {
                cacheAdditionalPKs(entity, dbCtxt);
            }

            Number amountUsed = (Number)pkAmountUsed.objectForKey(entity.name());
            Number initialValue = (Number)pkInitialValues.objectForKey(entity.name());
            Number cachedValue = new Integer(initialValue.intValue() + amountUsed.intValue());

            amountUsed = new Integer(amountUsed.intValue() + 1);
            pkAmountUsed.setObjectForKey(amountUsed, entity.name());

            return new NSDictionary(cachedValue, entity.primaryKeyAttributeNames().objectAtIndex(0));
        }

        return null;
    }



    /**
     * Generates and records a primary key value for the passed EO before saveChanges() is called.
     *
     * @see PregeneratesPrimaryKey
     *
     * @param eo the object to generate a primary key value for
     */
    public void pregeneratePrimaryKeyForObject(PregeneratesPrimaryKey eo)
    {
        /** require [valid_eo] eo != null;
                    [eo_in_ec] ((EOEnterpriseObject)eo).editingContext() != null;
         **/
        EOEntity entity = EOUtilities.entityForObject(eo.editingContext(), eo);
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(entity.model(), eo.editingContext());
        dbContext.lock();
        try
        {
            NSDictionary pkDictionary = databaseContextNewPrimaryKey(dbContext, eo, entity);
            eo.setPregeneratedPrimaryKey(pkDictionary.allValues().lastObject());
        }
        finally
        {
            dbContext.unlock();
        }
    }



    /**
     * Interface for EOs that need to know their PK value before saving.  This is used with this IntegerPrimaryKeyGenerator
     * to generate the PK early and then use that value it when the object is saved.
     */
    public interface PregeneratesPrimaryKey extends EOEnterpriseObject
    {
        /**
         * Call this to set the pre-generated key value, or to clear it.  This method is called by the
         * method @link {@link IntegerPrimaryKeyGenerator#pregeneratePrimaryKeyForObject(PregeneratesPrimaryKey)}.
         *
         * @param pkValue the pre-generated key value, or null to clear it
         */
        public void setPregeneratedPrimaryKey(Object pkValue);


        /**
         * @return value previously set by {@link #setPregeneratedPrimaryKey(Object)} or null if none set
         */
        public Object pregeneratedPrimaryKey();
    }


    /** invariant
    [valid_pkInitialValues] pkInitialValues != null;
    [valid_pkAmountUsed] pkAmountUsed != null; **/



}
