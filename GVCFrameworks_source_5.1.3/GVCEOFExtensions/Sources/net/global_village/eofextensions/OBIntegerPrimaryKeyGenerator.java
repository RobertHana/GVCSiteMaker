package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;


/**
 * Generates PKs for given OpenBase entities.
 *
 * @author Copyright (c) 2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class OBIntegerPrimaryKeyGenerator extends IntegerPrimaryKeyGenerator
{


    /**
     * Designated constructor.
     *
     * @param pksToCache the number of PKs to cache
     */
    public OBIntegerPrimaryKeyGenerator(int pksToCache)
    {
        super(pksToCache);
    }



    /**
     * Constructor that sets the number of PKs to cache to the default value.
     */
    public OBIntegerPrimaryKeyGenerator()
    {
        this(DefaultNumberOfPKsToCache);
    }



    /**
     * Adds additional PKs to the cache for the given entity.
     *
     * @param entity the entity whose PK cache we are caching
     * @param dbCtxt the database context from which we will get the PKs
     */
    protected void cacheAdditionalPKs(EOEntity entity, EODatabaseContext dbCtxt)
    {
        /** require
        [valid_entity_param] entity != null;
        [valid_dbCtxt_param] dbCtxt != null; **/

        EOModel model = entity.model();
        EOSQLExpressionFactory expressionFactory = new EOSQLExpressionFactory(EOAdaptor.adaptorWithModel(model));

        EOAdaptorChannel channel = (dbCtxt.availableChannel()).adaptorChannel();
        EOAdaptorContext adaptorContext = channel.adaptorContext();

        adaptorContext.beginTransaction();

        EOSQLExpression getUniqueExpression = expressionFactory.expressionForString("NEWID FOR \"" + entity.externalName() + "\" " + entity.primaryKeyAttributeNames().objectAtIndex(0));
        Number initialValue = (Number)EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, getUniqueExpression);

        adaptorContext.commitTransaction();

        pkAmountUsed.setObjectForKey(new Integer(0), entity.name());
        pkInitialValues.setObjectForKey(initialValue, entity.name());
    }



    /** invariant
    [valid_pkInitialValues] pkInitialValues != null;
    [valid_pkAmountUsed] pkAmountUsed != null; **/



}
