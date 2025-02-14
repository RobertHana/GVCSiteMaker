package net.global_village.eofextensions.tests;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test the EOAdaptorChannelAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class NSArrayQualifierAdditionsTest extends GVCJUnitEOTestCase
{
    protected static final String entityOneName = "One";
    protected static final String entityTwoName = "Two";
    protected static final String entityThreeName = "Three";
    protected static final String entityFourName = "Four";

    protected SimpleTestEntity entityOne;
    protected SimpleTestEntity entityTwo;
    protected SimpleTestEntity entityThree;
    protected SimpleTestEntity entityFour;

    protected NSArray someObjects;
    protected NSArray equalsArray;
    protected NSArray containsArray;


    /**
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
     public NSArrayQualifierAdditionsTest(String name)
     {
         super(name);
     }



    /**
     * Creates and inserts SimpleTestEntity in the editingContext() with aName as name
     *
     * @param aName		the name of the entity to be created
     * @return an SimpleTestEntity with aName as name
     */
    public SimpleTestEntity createSimpleTestEntityWithName(String aName)
    {
        SimpleTestEntity anEntity = new SimpleTestEntity();
        editingContext().insertObject(anEntity);
        anEntity.setName(aName);

        return anEntity;
    }



    /**
     * Create an array of key-value qualifiers for testing.
     *
     * @param selector 	selector to be used in creating the qualifiers
     * @return an array of key-value qualifiers
     */
    public NSArray testArrayWithOperator(NSSelector selector)
    {
        NSMutableArray anArray = new NSMutableArray();
        EOQualifier qualifier;

        qualifier = new EOKeyValueQualifier("name", selector, entityOne);
        anArray.addObject(qualifier);

        qualifier = new EOKeyValueQualifier("name", selector, entityTwo);
        anArray.addObject(qualifier);

        qualifier = new EOKeyValueQualifier("name", selector, entityThree);
        anArray.addObject(qualifier);

        return anArray;
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

        editingContext().saveChanges();

        someObjects = new NSArray (new Object[] {entityOne, entityTwo, entityThree});
        equalsArray = testArrayWithOperator(EOQualifier.QualifierOperatorEqual);
        containsArray = testArrayWithOperator(EOQualifier.QualifierOperatorContains);
    }


    
    /**
     * Test arrayOfQualifiers
     */
    public void testArrayOfQualifiers()
    {
        // EOQualifiers compare by memory address, not qualification!  Identical qualifier do not compare equal.
        // Their descriptions are equal however, so we can use those.
        String qualifierDescription =
            (NSArrayQualifierAdditions.arrayOfQualifiers(someObjects,
                                                         "name",
                                                         EOQualifier.QualifierOperatorEqual)).toString();
        assertTrue((equalsArray.toString()).equals(qualifierDescription));

        qualifierDescription =
            (NSArrayQualifierAdditions.arrayOfQualifiers(someObjects,
                                                         "name",
                                                         EOQualifier.QualifierOperatorContains)).toString();
        assertTrue((containsArray.toString()).equals(qualifierDescription));
    }



    /**
     * Test andQualifierForKeyOperatorSelector
     */
    public void testAndQualifierForKeyOperatorSelector()
    {
        EOAndQualifier theAndQualifier = new EOAndQualifier(equalsArray);
        String andQualifierDescription =
            (NSArrayQualifierAdditions.andQualifier(someObjects,
                                                    "name",
                                                    EOQualifier.QualifierOperatorEqual)).toString();
        assertTrue(theAndQualifier.toString().equals(andQualifierDescription));

        theAndQualifier = new EOAndQualifier(containsArray);
        andQualifierDescription =
            (NSArrayQualifierAdditions.andQualifier(someObjects,
                                                    "name",
                                                    EOQualifier.QualifierOperatorContains)).toString();
        assertTrue(theAndQualifier.toString().equals(andQualifierDescription));
    }



    /**
     * Test orQualifierForKeyOperatorSelector
     */
    public void testOrQualifierForKeyOperatorSelector()
    {
        EOOrQualifier theOrQualifier = new EOOrQualifier(equalsArray);
        String orQualifierDescription =
            (NSArrayQualifierAdditions.orQualifier(someObjects,
                                                   "name",
                                                   EOQualifier.QualifierOperatorEqual)).toString();
        assertTrue(theOrQualifier.toString().equals(orQualifierDescription));

        theOrQualifier = new EOOrQualifier(containsArray);
        orQualifierDescription =
            (NSArrayQualifierAdditions.orQualifier(someObjects,
                                                   "name",
                                                   EOQualifier.QualifierOperatorContains)).toString();
        assertTrue(theOrQualifier.toString().equals(orQualifierDescription));
    }



    /**
     * Delete inserted objects.
     */
     public void tearDown() throws java.lang.Exception
     {
        editingContext().deleteObject(entityOne);
        editingContext().deleteObject(entityTwo);
        editingContext().deleteObject(entityThree);
        editingContext().deleteObject(entityFour);

        editingContext().saveChanges();

        super.tearDown();
    }



}
