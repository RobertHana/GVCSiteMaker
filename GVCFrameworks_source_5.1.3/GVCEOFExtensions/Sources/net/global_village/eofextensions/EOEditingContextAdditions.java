package net.global_village.eofextensions;

import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Collection;


/**
 * Methods added to extended the functionality of EOEditingContext in the manner of EOUtilities
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 10$
 */  
public class EOEditingContextAdditions
{
    /** Aggregate function constants. */
    public static final String Max = "max";
    public static final String Min = "min";
    public static final String Count = "count";
    public static final String Avg = "avg";
    public static final String Sum = "sum";

    /**
     * A set of aggregate functions.
     */
    public static final NSSet setOfAllAggregateFunctions = new NSSet(new Object[] {Max, Min, Count, Avg, Sum});


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EOEditingContextAdditions()
    {
        super();
    }



    /**
     * Returns an array of all the objects of the named entitiy, except for the ones in the <code>objectsToExclude</code> array.
     *
     * @param editingContext the editing context to be used for fetching the all the objects
     * @param entityName the entity name of the objects being compared to
     * @param objectsToExclude the list of objects that will be removed from the the objects associated with the entityName
     * @return an array of objects excluding the ones in the <code>objectsToExclude</code> array
     */
    public static NSArray objectsForEntityNamed(EOEditingContext editingContext,
                                                String entityName,	
                                                NSArray objectsToExclude)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null;
        [valid_objectsToExclude_param] objectsToExclude != null; **/
        JassAdditions.pre("EOEditingContextAdditions", "objectsForEntityNamed", EOModelGroup.defaultGroup().entityNamed(entityName) != null);

        NSArray allObjects = EOUtilities.objectsForEntityNamed(editingContext, entityName);

        //Convert the arrays to sets
        NSMutableSet setOfAllObjects = Collection.collectionToNSMutableSet(allObjects);
        NSMutableSet setObjectsToExclude = Collection.collectionToNSMutableSet(objectsToExclude);

        setOfAllObjects.subtractSet(setObjectsToExclude);

        return setOfAllObjects.allObjects();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the number of objects of the specified entity, optionally restricted by qualifer.  This should be used when fetching all the objects is not desirable and server side evaluation is needed.<p>
     *
     * Example Usage:<code><pre>
     *    EOQualfier unpaidInvoices = EOQualfier.qualifierWithQualifierFormat("isPaid = 'N'", null);
     *    Number numberOfUnpaidInvoices = countObjectsForEntityNamed(editingContext,
     *                                                               "Invoice",
     *                                                               unpaidInvoices);</pre></code>
     *
     * @param editingContext the editingContext to be used for fetching
     * @param entityName the entity name of the objects to be counted
     * @param qualifier the qualifier to be applied 
     * @return the number of objects of the specified entity
     */
    public static Number countOfObjectsForEntityNamed(EOEditingContext editingContext,
                                                      String entityName,
                                                      EOQualifier qualifier)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null; **/
        JassAdditions.pre("EOEditingContextAdditions", "countOfObjectsForEntityNamed", EOModelGroup.defaultGroup().entityNamed(entityName) != null);

        Number result;

        EOEntity anEntity = EOModelGroup.defaultGroup().entityNamed(entityName);
//        String dummyAttributeName = ((EOAttribute) (anEntity.attributes().objectAtIndex(0))).name();

        String dummyAttributeName = ((EOAttribute) anEntity.primaryKeyAttributes().objectAtIndex(0)).name();
        result = (Number) EOEditingContextAdditions.computeAggregateFunction(editingContext,
                                                                             "count",
                                                                             dummyAttributeName,
                                                                             entityName,
                                                                             qualifier);

        if (result == null)
        {
            result = new Integer(0);
        }

        JassAdditions.post("EOEditingContextAdditions", "countOfObjectsForEntityNamed", result.intValue() >= 0);
        return result;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Computes aggregate function (min, max, sum, and avg are currently supported) on the attribute named <code>attributeName</code> on the entity named <code>entityName</code>, the objects operated on are optionally restricted by <code>qualifer</code>. The result is returned as the appropriate class. NOTE: there is a bug in the SQL generation facility that this method uses, so if you use a qualifier that qualifies based on a literal, you will have to properly quote and escape the literal <em>before</em> creating the qualifier (see the example).  Qualifiers used elsewhere seem to work fine...<p>
     *
     * Example Usage:<code><pre>
     *    EOQualifier unpaidInvoices = EOQualfier.qualifierWithQualifierFormat("isPaid = 'N'", null);
     *    BigDecimal totalOfUnpaidInvoices = EOEditingContextAdditions.computeAggregateFunction(editingContext, "sum", "grandTotal", "Invoice", unpaidInvoices);
     * </pre></code>
     *
     * @param editingContext the editing context to be used for fetching
     * @param aggregateFunction the aggregate function to be applied to the attribute
     * @param attributeName the name of the attibute to be evaluated
     * @param entityName the name of the attribute's entity
     * @param qualifier the qualifier to be applied to the fetch
     * @return the result of the aggregate function, null is returned if there are no matching records in the database
     */
    public static Object computeAggregateFunction(EOEditingContext editingContext,
                                                  String aggregateFunction,
                                                  String attributeName,
                                                  String entityName,
                                                  EOQualifier qualifier)
     {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_aggregateFunction_param] aggregateFunction != null;
        [valid_entityName_param] entityName != null;
        [valid_attributeName_param] attributeName != null; **/
        JassAdditions.pre("EOEditingContextAdditions", "computeAggregateFunction", EOModelGroup.defaultGroup().entityNamed(entityName) != null);
        JassAdditions.pre("EOEditingContextAdditions", "computeAggregateFunction", EOModelGroup.defaultGroup().entityNamed(entityName).attributeNamed(attributeName) != null);
        JassAdditions.pre("EOEditingContextAdditions", "computeAggregateFunction", setOfAllAggregateFunctions.containsObject(aggregateFunction));

        editingContext.rootObjectStore().lock();
        Object anObject = null;
        try
        {
            EOEntity anEntity = EOModelGroup.defaultGroup().entityNamed(entityName);
            EOAttribute theAttribute = EOModelGroup.defaultGroup().entityNamed(entityName).attributeNamed(attributeName);
            EODatabaseContext context = EODatabaseContext.registeredDatabaseContextForModel(anEntity.model(), editingContext);
            EOAdaptorChannel channel = (context.availableChannel()).adaptorChannel();
            EOAdaptor adaptor = channel.adaptorContext().adaptor();

            EOSQLExpression expression = null;
            try
            {
                Constructor eoConstructor = adaptor.expressionClass().getConstructor(new Class[] {EOEntity.class});
                expression = (EOSQLExpression) eoConstructor.newInstance(new Object[] {anEntity});
            }
            catch(Exception e)
            {
                //ClassNotFoundException, NoSuchMethodException, InstantiationException, illegalAccessException, InvocationTargetException
                System.out.println("Exception thrown: " + e.getMessage());
            }

            expression.setUseAliases(true);

            String selectFunction = aggregateFunction + "(" + expression.sqlStringForAttribute(theAttribute) + ")";
            String sql;

            if (qualifier != null)
            {
                String whereClause = EOSQLExpressionAdditions.sqlStringForQualifier(expression, qualifier);

                expression.joinExpression();

                if (expression.joinClauseString().length() > 0)
                {
                    whereClause = "(" + expression.joinClauseString() + ") AND " + whereClause;
                }
                sql = "SELECT " + selectFunction + " FROM " + expression.tableListWithRootEntity(anEntity) + " WHERE " + whereClause;
            }
            else
            {
                sql = "SELECT " + selectFunction + " FROM " + expression.tableListWithRootEntity(anEntity);
            }

            expression.setStatement(sql);

            anObject = EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, expression);
        }
        finally
        {
            editingContext.rootObjectStore().unlock();
        }

        return anObject;
    }



    /**
     * Fetches and returns the enterprise objects retrieved with the specified qualifier using the specified editing context. If qualifier is null, it will return all the objects.
     *
     * @param editingContext the editingContext to be used for fetching
     * @param entityName the name of the entity to be qualified
     * @param qualifier the qualifier to be used in the fetch
     * @return an array of enterprise objects
     */
    public static NSArray objectsWithQualifier(EOEditingContext editingContext,
                                               String entityName,
                                               EOQualifier qualifier)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null; **/

        EOFetchSpecification fetchSpec = new EOFetchSpecification (entityName,
                                                                   qualifier,
                                                                   null);
        return editingContext.objectsWithFetchSpecification(fetchSpec);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Fetches and returns the enterprise objects retrieved with the specified qualifier using the specified editing context. If qualifier is null, it will return all the objects.
     *
     * @param editingContext the editingContext to be used for fetching
     * @param entityName the name of the entity to be qualified
     * @param qualifier the qualifier to be used in the fetch
     * @return an array of enterprise objects
     */
    public static int countOfObjectsWithQualifier(EOEditingContext editingContext,
                                                  String entityName,
                                                  EOQualifier qualifier)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null; **/

        NSArray objects = EOEditingContextAdditions.objectsWithQualifier(editingContext,
                                                                         entityName,
                                                                         qualifier);

        return objects.count();

        /** ensure [valid_result] Result >= 0; **/
    }



    /**
     * Returns an array of all the objects of the named entitiy, sorted on the named attribute, with the sort determined by selector.  Selector will most commonly be one of CompareAscending, CompareDescending, CompareCaseInsensitiveAscending, CompareCaseInsensitiveDescending.  See EOSortOrdering for more details.
     *
     * @param editingContext the editingContext to be used for fetching
     * @param entityName the entityName of the objects to fetch
     * @param attributeName the name of the attribute to be sorted
     * @param selector the selector to be used for sorting
     * @return an array of objects
     */
    public static NSArray orderedObjects(EOEditingContext editingContext,
                                         String entityName,
                                         String attributeName,
                                         NSSelector selector)    
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null;
        [valid_attributeName_param] attributeName != null;
        [valid_selector_param] selector != null; **/
        JassAdditions.pre("EOEditingContextAdditions", "orderedObjects", EOModelGroup.defaultGroup().entityNamed(entityName) != null);

        NSArray ordering = new NSArray(new EOSortOrdering(attributeName, selector));

        EOFetchSpecification fetchSpec = new EOFetchSpecification(entityName,
                                                                  null,
                                                                  ordering);

        return editingContext.objectsWithFetchSpecification(fetchSpec);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an array of the objects of the named entitiy, which have aKey = aValue, sorted on the named attribute, with the sort determined by selector.  Selector will most commonly be one of EOCompareAscending, EOCompareDescending, EOCompareCaseInsensitiveAscending, EOCompareCaseInsensitiveDescending.  See EOSortOrdering for more details.
     *
     * @param editingContext the editing context to be used for the fetch
     * @param aValue the value of the key to match
     * @param aKey the key to match
     * @param entityName the entity name of the objects to fetch
     * @param attributeName the attribute name to which the ordering will be applied
     * @param selector the selector to be used for the ordering of the resulting array
     * @return an array of objects 
     */
    public static NSArray objectsMatchingValue(EOEditingContext editingContext,
                                               Object aValue,
                                               String aKey,
                                               String entityName,
                                               String attributeName,
                                               NSSelector selector)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_aKey_param] aKey != null;
        [valid_entityName_param] entityName != null;
        [valid_attributeName_param] attributeName != null;
        [valid_selector_param] selector != null; **/
        JassAdditions.pre("EOEditingContextAdditions", "objectsMatchingValue", EOModelGroup.defaultGroup().entityNamed(entityName) != null);
        JassAdditions.pre("EOEditingContextAdditions", "objectsMatchingValue", EOModelGroup.defaultGroup().entityNamed(entityName).attributeNamed(attributeName) != null);

        EOQualifier qualifier = new EOKeyValueQualifier(aKey,
                                                        EOQualifier.QualifierOperatorEqual,
                                                        aValue);
        NSArray ordering = new NSArray (new EOSortOrdering(attributeName, selector));
        EOFetchSpecification fetchSpec = new EOFetchSpecification (entityName,
                                                                   qualifier,
                                                                   ordering);

        return editingContext.objectsWithFetchSpecification(fetchSpec);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an array of the objects identified by the EOGlobalIDs in <code>globalIDs</code>, or raises RuntimeException if no object has been registered in the EOEditingContext with one of the global IDs.
     *
     * @param editingContext the editingContext to be used for identifying the objects
     * @param globalIDs the array of globalIDs to look for
     * @return an array of objects
     * @exception RuntimeException if no object has been registered in the <code>EOEditingContext</code> with one of the global IDs
     */
    public static NSArray objectsForGlobalIDs(EOEditingContext editingContext,
                                              NSArray globalIDs)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_globalIDs_param] globalIDs != null; **/

        NSMutableArray objects = new NSMutableArray();

        Enumeration globalIDEnumerator = globalIDs.objectEnumerator();
        while(globalIDEnumerator.hasMoreElements())
        {
            EOGlobalID globalID = (EOGlobalID) globalIDEnumerator.nextElement();

            Object localObject = editingContext.objectForGlobalID(globalID);

            if (localObject == null)
            {
                throw new RuntimeException("There is no object for GlobalID " + globalID +
                                           " in editing context " + editingContext);
            }
            objects.addObject(localObject);
        }

        JassAdditions.post("EOEditingContextAdditions", "objectsForGlobalIDs", objects.count() == globalIDs.count());
        return objects;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an array of the EOGlobalIDs identifying the EOEnterpriseObjects in <code>eoObjects</code>, or raises RuntimeException if one of the objects has not been registered in <code>editingContext</code>.
     *
     * @param editingContext the editingContext to be used for identifying the objects
     * @param eoObjects the array of EOEnterpriseObjects to look for
     * @return an array of EOGlobalIDs
     * @exception RuntimeException one of the objects has not been registered in <code>editingContext</code>
     */
    public static NSArray globalIDsForObjects(EOEditingContext editingContext,
                                              NSArray eoObjects)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_globalIDs_param] eoObjects != null; **/

        NSMutableArray globalIDs = new NSMutableArray();

        Enumeration eoObjectEnumerator = eoObjects.objectEnumerator();
        while(eoObjectEnumerator.hasMoreElements())
        {
            EOEnterpriseObject eoObject = (EOEnterpriseObject) eoObjectEnumerator.nextElement();

            EOGlobalID globalID = editingContext.globalIDForObject(eoObject);

            if (globalID == null)
            {
                throw new RuntimeException("There is no object " + eoObject +
                                           " registered in editing context " + editingContext);
            }
            globalIDs.addObject(globalID);
        }

        JassAdditions.post("EOEditingContextAdditions", "objectsForGlobalIDs", eoObjects.count() == globalIDs.count());
        return globalIDs;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the resulting BigDecimal value after evaluating the specified SQL for entityName.  Returns 0 if the SQL evaluates to null.  Note that this method should only be used for SQL returning one and only one BigDecimal value. This is very useful when using aggregate functions, ie sum, avg, min, max, etc.  
     *
     * @return the value of the of the passed sql.
     */
    public static BigDecimal valueForAggregateSQL(EOEditingContext ec, String entityName, String sqlString)
    {
        /** require
        [entityName_not_null] entityName != null;
        [sqlString_not_null] sqlString != null;
        [ec_not_null] ec != null;
        **/
        
        EOEntity entity = EOUtilities.entityNamed(ec, entityName);
        String modelName = entity.model().name();

        NSArray resultSet = EOUtilities.rawRowsForSQL(ec, modelName, sqlString, null);

        //Get the corresponding value from the resulting Dictionary
        NSDictionary firstRow = (NSDictionary)resultSet.objectAtIndex(0);
        String firstColumnKey = (String)firstRow.allKeys().objectAtIndex(0);

        BigDecimal value;
        if (firstRow.objectForKey(firstColumnKey) != NSKeyValueCoding.NullValue)
        {
            value = new BigDecimal(((java.lang.Double) firstRow.objectForKey(firstColumnKey)).doubleValue());
        }
        else
        {
            value = new BigDecimal("0");
        }

        return value;
        
        /** ensure [Result_not_null] Result != null; **/
    }


    
    /**
     * A convenience method for returning the primary key of an Object with the passed key.
     *
     * @return primary key of this Object with the passed key.
     */
    public static Object primaryKey(EOEditingContext ec, EOEnterpriseObject theObject, String theKey)
    {
        /** require
        [valid_param] ec != null;
        [valid_param] theObject != null;
        [valid_param] theKey != null;
        **/
        Object result = null;
        
        NSDictionary primaryKeyDictionary = EOUtilities.primaryKeyForObject(ec, theObject);
        if (primaryKeyDictionary != null)
        {
            result = primaryKeyDictionary.objectForKey(theKey);
        }
        
        JassAdditions.post("EOEditingContextAdditions",
                           "primaryKey",
                           (result != null) || (theObject.editingContext().globalIDForObject(theObject).isTemporary() && (result == null)));

        return result;
    }
}
