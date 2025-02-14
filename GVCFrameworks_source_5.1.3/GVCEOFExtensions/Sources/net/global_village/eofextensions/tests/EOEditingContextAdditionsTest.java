package net.global_village.eofextensions.tests;

import java.math.*;
import java.util.*;

import junit.framework.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.foundation.Collection;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test the EOEditingContextAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 12$
 */
public class EOEditingContextAdditionsTest extends GVCJUnitEOTestCase
{
    protected static final String entityOneName = "One";
    protected static final String entityTwoName = "Two";
    protected static final String entityThreeName = "Three";
    protected static final String entityFourName = "Four";

    protected SimpleTestEntity entityOne;
    protected SimpleTestEntity entityTwo;
    protected SimpleTestEntity entityThree;
    protected SimpleTestEntity entityFour;
    protected NSArray objectsInGroup;


    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public EOEditingContextAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Creates and inserts SimpleTestEntity in the editingContext() with aName as name
     *
     * @param aName the name of the entity to be created
     * @return a SimpleTestEntity with aName as name
     */
    public SimpleTestEntity createSimpleTestEntityWithName(String aName)
    {
        SimpleTestEntity anEntity = new SimpleTestEntity();
        editingContext().insertObject(anEntity);
        anEntity.setName(aName);

        return anEntity;
    }



    /**
     * Creates and inserts EntityWithConstraints in the editingContext() with theValue
     *
     * @param theValue the name of the entity to be created
     */
    public void addEntityWithConstraintsWithValue(String theValue)
    {
        EntityWithConstraints anEntity = new EntityWithConstraints();
        editingContext().insertObject(anEntity);
        anEntity.setQuantity(new BigDecimal(theValue));
    }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        entityOne = createSimpleTestEntityWithName(entityOneName);
        entityTwo = createSimpleTestEntityWithName(entityTwoName);
        entityThree = createSimpleTestEntityWithName(entityThreeName);
        entityFour = createSimpleTestEntityWithName(entityFourName);

        addEntityWithConstraintsWithValue("2.7");
        addEntityWithConstraintsWithValue("9.0");
        addEntityWithConstraintsWithValue("-324.43");
        addEntityWithConstraintsWithValue("0.2309");
        addEntityWithConstraintsWithValue("-4.5627");

        editingContext().saveChanges();

        objectsInGroup = new NSArray (new Object[] {entityOne, entityTwo, entityThree});
    }



    /**
     * Tests if all the objects return does not include the objects to exclude
     */
    public void testObjectsForEntityNamed()
    {
        NSArray filteredObjects = EOEditingContextAdditions.objectsForEntityNamed(editingContext(),
                                                                                  "SimpleTestEntity",
                                                                                  objectsInGroup);

        assertTrue(filteredObjects.containsObject(entityFour));
        assertTrue( ! (filteredObjects.containsObject(entityOne)));
        assertTrue( ! (filteredObjects.containsObject(entityThree)));
    }



    /**
     * Tests countOfObjectsForEntityNamed
     */
    public void testCountOfObjectsForEntityNamed()
    {
        NSArray allSimpleTestEntities = EOUtilities.objectsForEntityNamed(editingContext(), "SimpleTestEntity");

        //Work around: Since Java cannot distinguish between column names and string literals, string value for the qualifier needs to be converted to proper SQL so it will not raise an error.
        // Trying WO5.1 without this...
        //NSArray args = new NSArray (StringAdditions.escapeSQLForFrontBase(entityThree.name()));
        EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("name != %@", new NSArray(entityThree.name()));


        NSArray someSimpleTestEntities = EOUtilities.objectsWithQualifierFormat(editingContext(),
                                                                                "SimpleTestEntity",
                                                                                "name != %@",
                                                                                new NSArray (entityThree.name()));

        Number countOfAllObjects = EOEditingContextAdditions.countOfObjectsForEntityNamed(editingContext(), "SimpleTestEntity", null);
        Number countOfSomeObjects = EOEditingContextAdditions.countOfObjectsForEntityNamed(editingContext(), "SimpleTestEntity", qual);

        assertTrue(countOfAllObjects.intValue() == allSimpleTestEntities.count());
        assertTrue(countOfSomeObjects.intValue() == someSimpleTestEntities.count());
    }



    /**
     * Tests computeAggregateFunction
     */
    public void testComputeAggregateFunction()
    {
        NSArray allEntitiesWithContraints = EOUtilities.objectsForEntityNamed(editingContext(), "EntityWithConstraints");

        BigDecimal nine = new BigDecimal(9.00);
        NSArray argsWithNine = new NSArray (nine);
        NSArray argsWithTwoNine = new NSArray (new Object[] {nine, nine});

        EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("quantity < %f", argsWithNine);

        EOQualifier noMatchQualifier = EOQualifier.qualifierWithQualifierFormat("(quantity < %f) AND (quantity > %f)", argsWithTwoNine);
        NSArray someEntitiesWithContraints = EOUtilities.objectsWithQualifierFormat(editingContext(), "EntityWithConstraints", "quantity < %f", argsWithNine);

        Object sumOfAllObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Sum, "quantity", "EntityWithConstraints", null);

        Object sumOfSomeObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Sum, "quantity", "EntityWithConstraints", qual);

        Object minimumOfAllObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Min, "quantity", "EntityWithConstraints", null);

        Object minimumOfSomeObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Min, "quantity", "EntityWithConstraints", qual);

        Object maximumOfAllObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Max, "quantity", "EntityWithConstraints", null);

        Object maximumOfSomeObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Max, "quantity", "EntityWithConstraints", qual);


        // #warning Test breaks.  Oracle Adaptor bug?  This *used* to work, no code changes...
        Object averageOfAllObjects  = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Avg, "quantity", "EntityWithConstraints", null);

        Object averageOfSomeObjects = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Avg, "quantity", "EntityWithConstraints", qual);

        // Previously this test checked for a BigDecimal(0) result.  That is just wrong.  No matches should
        // result in null not zero.  The docs for EOEditingContextAdditions.computeAggregateFunction correctly
        // state this.
        Object realResult = EOEditingContextAdditions.computeAggregateFunction(editingContext(), EOEditingContextAdditions.Avg, "quantity", "EntityWithConstraints", noMatchQualifier);
        assertEquals("no matching records returns null not zero", null, realResult);

        assertEquals("sumOfAllObjects.getClass() equals BigDecimal.class", sumOfAllObjects.getClass(), BigDecimal.class);
        assertEquals("sumOfSomeObjects.getClass() equals BigDecimal.class", sumOfSomeObjects.getClass(), BigDecimal.class);
        assertEquals("minimumOfAllObjects.getClass() equals BigDecimal.class", minimumOfAllObjects.getClass(), BigDecimal.class);
        assertEquals("minimumOfSomeObjects.getClass() equals BigDecimal.class", minimumOfSomeObjects.getClass(), BigDecimal.class);
        assertEquals("maximumOfAllObjects.getClass() equals BigDecimal.class", maximumOfAllObjects.getClass(), BigDecimal.class);
        assertEquals("maximumOfSomeObjects.getClass() equals BigDecimal.class", maximumOfSomeObjects.getClass(), BigDecimal.class);
        assertEquals("averageOfAllObjects.getClass() equals BigDecimal.class", averageOfAllObjects.getClass(), BigDecimal.class);
        assertEquals("averageOfSomeObjects.getClass() equals BigDecimal.class", averageOfSomeObjects.getClass(), BigDecimal.class);

        assertEquals("sumOfAllObjects equals sum.quantity", sumOfAllObjects, allEntitiesWithContraints.valueForKeyPath("@sum.quantity"));
        assertEquals("sumOfSomeObjects equals sum.quantity", sumOfSomeObjects, someEntitiesWithContraints.valueForKeyPath("@sum.quantity"));

        // equals() returns false if the scales are different...
        assertTrue("minimumOfAllObjects equals min.quantity", ((BigDecimal)minimumOfAllObjects).compareTo((BigDecimal)allEntitiesWithContraints.valueForKey("@min.quantity")) == 0);
        assertTrue("someEntitiesWithContraints equals min.quantity", ((BigDecimal)minimumOfSomeObjects).compareTo((BigDecimal)someEntitiesWithContraints.valueForKeyPath("@min.quantity")) == 0);
        assertTrue("allEntitiesWithContraints equals min.quantity", ((BigDecimal)maximumOfAllObjects).compareTo((BigDecimal)allEntitiesWithContraints.valueForKeyPath("@max.quantity")) == 0);
        assertTrue("maximumOfSomeObjects equals min.quantity",((BigDecimal)maximumOfSomeObjects).compareTo((BigDecimal)someEntitiesWithContraints.valueForKeyPath("@max.quantity")) == 0);

        assertTrue("averageOfAllObjects equals avg.quantity", BigDecimalAdditions.equalsWithScale((BigDecimal)averageOfAllObjects, (BigDecimal)allEntitiesWithContraints.valueForKeyPath("@avg.quantity")));
        assertTrue("averageOfSomeObjects equals avg.quantity", BigDecimalAdditions.equalsWithScale((BigDecimal)averageOfSomeObjects, (BigDecimal)someEntitiesWithContraints.valueForKeyPath("@avg.quantity")));

        try
        {
            EOEditingContextAdditions.computeAggregateFunction(editingContext(),
                                                               "bad",
                                                               "quantity",
                                                               "EntityWithConstraints",
                                                               qual);
            fail("Expected exception did not occur");
        }
        catch (Exception e) {}
    }



    /**
     * Tests orderedObjects
     */
    public void testOrderedObjects()
    {
        NSArray objects = EOEditingContextAdditions.orderedObjects(editingContext(),
                                                                   "SimpleTestEntity",
                                                                   "name",
                                                                   EOSortOrdering.CompareAscending);

        assertTrue(objects.containsObject(entityOne));
        assertTrue(objects.containsObject(entityTwo));
        assertTrue(objects.containsObject(entityThree));
        assertTrue(objects.containsObject(entityFour));

        // Alphabetically, the order is Four, One, Three, Two
        assertTrue(objects.indexOfObject(entityFour) < (objects.indexOfObject(entityOne)));
        assertTrue(objects.indexOfObject(entityOne) < (objects.indexOfObject(entityThree)));
        assertTrue(objects.indexOfObject(entityThree) < (objects.indexOfObject(entityTwo)));
    }



    /**
     * Tests objectsMatchingValue
     */
    public void testObjectsMatchingValue()
    {
// Logs sent to Apple for bug report
//        NSLog.out.appendln("GVC Boolean is " + _NSUtilities.classWithName("net.global_village.foundation.Boolean").getCanonicalName());
//        EOEntity entity = EOUtilities.entityNamed(editingContext(), "EntityWithJavaBooleanAttribute");
//        EOAttribute att = entity.attributeNamed("flag");
//        NSLog.out.appendln("1. value class " + att.valueClassName());
//        NSLog.out.appendln("2. class name " + att.className());
//        NSLog.out.appendln("3. value valueFactoryMethodName " + att.valueFactoryMethodName());
//        NSLog.out.appendln("4. value type " + att.valueType());
//        NSLog.out.appendln("5. value valueTypeClassName " + att.valueTypeClassName());



        EntityWithJavaBooleanAttribute entity1 = new EntityWithJavaBooleanAttribute();
        EntityWithJavaBooleanAttribute entity2 = new EntityWithJavaBooleanAttribute();
        EntityWithJavaBooleanAttribute entity3 = new EntityWithJavaBooleanAttribute();
        EntityWithJavaBooleanAttribute entity4 = new EntityWithJavaBooleanAttribute();
        NSArray resultArray;

        editingContext().insertObject(entity1);
        editingContext().insertObject(entity2);
        editingContext().insertObject(entity3);
        editingContext().insertObject(entity4);


        entity1.setName(entityOneName);
        entity1.setFlag(new net.global_village.foundation.GVCBoolean(true));
        entity2.setName(entityTwoName);
        entity2.setFlag(new net.global_village.foundation.GVCBoolean(true));
        entity3.setName(entityThreeName);
        entity3.setFlag(new net.global_village.foundation.GVCBoolean(false));
        entity4.setName(entityFourName);
        entity4.setFlag(new net.global_village.foundation.GVCBoolean(true));

        editingContext().saveChanges();

        resultArray = EOEditingContextAdditions.objectsMatchingValue(editingContext(),
                                                                     new net.global_village.foundation.GVCBoolean(true),
                                                                     "flag",
                                                                     "EntityWithJavaBooleanAttribute",
                                                                     "name",
                                                                     EOSortOrdering.CompareAscending);

        // These checks allow for the possibility of multiple users running the tests as the same time / old data being left in the table.
        assertTrue(resultArray.count() >= 3);
        assertTrue(resultArray.containsObject(entity1));
        assertTrue(resultArray.containsObject(entity2));
        assertTrue( ! (resultArray.containsObject(entity3)));
        assertTrue(resultArray.containsObject(entity4));

        // Alphabetically, the order is Four, One, Three, Two
        assertTrue(resultArray.indexOfObject(entity4) < resultArray.indexOfObject(entity1));
        assertTrue(resultArray.indexOfObject(entity1) < resultArray.indexOfObject(entity2));

        resultArray = EOEditingContextAdditions.objectsMatchingValue(editingContext(),
                                                                     new net.global_village.foundation.GVCBoolean(false),
                                                                     "flag",
                                                                     "EntityWithJavaBooleanAttribute",
                                                                     "name",
                                                                     EOSortOrdering.CompareAscending);

        assertTrue(resultArray.count() >= 1);
        assertTrue( ! (resultArray.containsObject(entity1)));
        assertTrue( ! (resultArray.containsObject(entity2)));
        assertTrue(resultArray.containsObject(entity3));
        assertTrue( ! (resultArray.containsObject(entity4)));

        editingContext().deleteObject(entity1);
        editingContext().deleteObject(entity2);
        editingContext().deleteObject(entity3);
        editingContext().deleteObject(entity4);
        editingContext().saveChanges();
    }



    /**
     * Tests objectsWithQualifier
     */
    public void testObjectsWithQualifier()
    {
        // Test unqualified fetch
        NSArray objects = EOEditingContextAdditions.objectsWithQualifier(editingContext(),
                                                                         "SimpleTestEntity",
                                                                         null);
        assertTrue(objects.count() == ((EOUtilities.objectsForEntityNamed(editingContext(), "SimpleTestEntity")).count()));
        assertTrue(objects.containsObject(entityOne));
        assertTrue(objects.containsObject(entityTwo));
        assertTrue(objects.containsObject(entityThree));
        assertTrue(objects.containsObject(entityFour));

        // Test qualified fetch
        NSArray args = new NSArray (entityOne.name());
        EOQualifier aQualifier = EOQualifier.qualifierWithQualifierFormat("name = %@", args);

        NSArray filteredObjects = EOEditingContextAdditions.objectsWithQualifier(editingContext(),
                                                                                 "SimpleTestEntity",
                                                                                 aQualifier);
        assertTrue(filteredObjects.count() == 1);

        NSSelector nameSelector = new NSSelector("name", new Class[] {});

        try
        {
            assertTrue(Collection.doAllObjectsRespond(filteredObjects, entityOne.name(), nameSelector, null));
        }
        catch (IllegalAccessException e) { Assert.fail("IllegalAccessException"); }
        catch (java.lang.reflect.InvocationTargetException e) { Assert.fail("InvocationTargetException"); }
    }



    /**
     * Tests countOfObjectsWithQualifier
     */
    public void testCountOfObjectsWithQualifier()
    {
        // Test unqualified fetch
        int countOfObjects = EOEditingContextAdditions.countOfObjectsWithQualifier(editingContext(),
                                                                                   "SimpleTestEntity",
                                                                                   null);
        assertTrue(countOfObjects == ((EOUtilities.objectsForEntityNamed(editingContext(), "SimpleTestEntity")).count()));

        // Test qualified fetch
        NSArray args = new NSArray (entityOne.name());
        EOQualifier aQualifier = EOQualifier.qualifierWithQualifierFormat("name = %@", args);

        int countOfFilteredObjects = EOEditingContextAdditions.countOfObjectsWithQualifier(editingContext(), "SimpleTestEntity", aQualifier);
        assertTrue(countOfFilteredObjects == 1);
    }



    /**
     * test objectForGlobalIDs
     */
    public void testObjectsForGlobalIDs()
    {
        NSArray emptyArray = new NSArray();
        NSArray arrayWithEntityOne = new NSArray(entityOne);
        NSArray arrayWithEntityOnesID = new NSArray(EOObject.globalID(entityOne));
        NSArray arrayWithFourEntityIDs = new NSArray(new Object[] {EOObject.globalID(entityOne),
            EOObject.globalID(entityTwo),
            EOObject.globalID(entityThree),
            EOObject.globalID(entityFour)});
        EOEditingContext newEditingContext = new EOEditingContext();

        assertEquals(EOEditingContextAdditions.objectsForGlobalIDs(editingContext(), emptyArray), emptyArray);
        assertEquals(EOEditingContextAdditions.objectsForGlobalIDs(editingContext(), arrayWithEntityOnesID), arrayWithEntityOne);

        try
        {
            EOEditingContextAdditions.objectsForGlobalIDs(newEditingContext, arrayWithEntityOnesID);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        EOUtilities.localInstanceOfObject(newEditingContext, entityOne);
        try
        {
            EOEditingContextAdditions.objectsForGlobalIDs(newEditingContext, arrayWithEntityOnesID);
        }
        catch (RuntimeException e) { fail("Unexpected exception occured"); }

        NSSet setWithFourEntities = new NSSet (new Object[] {entityOne, entityTwo, entityThree, entityFour});
        NSMutableSet resultSet = new NSMutableSet();
        resultSet.addObjectsFromArray(EOEditingContextAdditions.objectsForGlobalIDs(editingContext(), arrayWithFourEntityIDs));
        assertTrue(resultSet.isEqualToSet(setWithFourEntities));
    }



    /**
     * Test valueForAggregateSQL
     */
    public void testValueForAggregateSQL()
    {
        String sql = "SELECT sum(quantity) FROM \"Entity_With_Constraints\"";
        BigDecimal sumOfQuantity = EOEditingContextAdditions.valueForAggregateSQL(editingContext(), "EntityWithConstraints", sql);
        assertTrue("sumOfQuantity has correct sum", sumOfQuantity.setScale(4, BigDecimal.ROUND_HALF_UP).equals(new BigDecimal("-317.0618")));

        //Test with filter
        String sqlWithFilter = "SELECT avg(quantity) FROM \"Entity_With_Constraints\" where quantity > 0";
        BigDecimal average = EOEditingContextAdditions.valueForAggregateSQL(editingContext(), "EntityWithConstraints", sqlWithFilter);
        assertTrue("valueForAggregateSQL has correct average", average.setScale(4, BigDecimal.ROUND_HALF_UP).equals(new BigDecimal("3.9770")));

        //Test without result
        String sqlNoResults = "SELECT sum(quantity) FROM \"Entity_With_Constraints\" where quantity > 0 and quantity <= 0";
        BigDecimal sum = EOEditingContextAdditions.valueForAggregateSQL(editingContext(), "EntityWithConstraints", sqlNoResults);
        assertTrue("valueForAggregateSQL has correct sum if no results", sum.equals(new BigDecimal("0")));
    }



    /**
     * Test primaryKey
     */
    public void testPrimaryKey()
    {
        Integer primaryKeyOne = (Integer) EOEditingContextAdditions.primaryKey(editingContext(), entityOne, "theID");
        SimpleTestEntity equivalentObject = (SimpleTestEntity) EOUtilities.objectWithPrimaryKey(editingContext(),
                                                                                                "SimpleTestEntity",
                                                                                                new NSDictionary(primaryKeyOne, "theID"));
        assertTrue("correct primary key for entityOne", entityOne.equals(equivalentObject));

        //Test returns null for uncommited objects
        SimpleTestEntity newObjectInsertedToEC = new SimpleTestEntity();
        editingContext().insertObject(newObjectInsertedToEC);
        Integer primaryKeyforNewObjectInsertedToEC = (Integer) EOEditingContextAdditions.primaryKey(editingContext(), newObjectInsertedToEC, "theID");
        editingContext().deleteObject(newObjectInsertedToEC);
        assertTrue("correct primary key for newObject", primaryKeyforNewObjectInsertedToEC == null);
    }



    /**
     * Delete the inserted objects in the setup.
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().deleteObject(entityOne);
        editingContext().deleteObject(entityTwo);
        editingContext().deleteObject(entityThree);
        editingContext().deleteObject(entityFour);

        NSArray allObjectsWithContraints = EOUtilities.objectsForEntityNamed(editingContext(), "EntityWithConstraints");

        Enumeration objectEnumerator = allObjectsWithContraints.objectEnumerator();
        while(objectEnumerator.hasMoreElements())
        {
            EntityWithConstraints anObject = (EntityWithConstraints)objectEnumerator.nextElement();
            editingContext().deleteObject(anObject);
        }

        editingContext().saveChanges();

        super.tearDown();
    }
}
