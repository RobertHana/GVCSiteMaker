package net.global_village.genericobjects.tests;

import com.webobjects.foundation.NSArray;

import net.global_village.foundation.DefaultValueRetrieval;
import net.global_village.genericobjects.*;
import net.global_village.gvcjunit.GVCJUnitEOTestCase;


/**
 * Tests TestLookup - which is an example subclass of Lookup.
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class TestLookupTest extends GVCJUnitEOTestCase
{
    protected static final String ENGLISH = "English";
    protected static final String FRENCH = "French";

    protected TestLookup instanceOne;
    protected TestLookup instanceTwo;
    protected TestLookup instanceThree;
    protected TestLookup localizedInstance;
    protected Locale frenchLocale;

    protected Lookup defaultTestObject;
    protected String defaultTestObjectName;


    /**
     * Designated constructor.
     * 
     * @param name name of the test to be initialized
     */
    public TestLookupTest(String name)
    {
        super(name);
    }



    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        instanceOne = new TestLookup();
        instanceTwo = new TestLookup();
        instanceThree = new TestLookup();
        localizedInstance = new TestLookup();
        defaultTestObject = new TestLookup();

        editingContext.insertObject(instanceOne);
        editingContext.insertObject(instanceTwo);
        editingContext.insertObject(instanceThree);
        editingContext.insertObject(localizedInstance);
        editingContext.insertObject(defaultTestObject);

        instanceOne.setName(globallyUniqueString());
        instanceTwo.setName(globallyUniqueString());
        instanceThree.setName(globallyUniqueString());
        localizedInstance.setName("Some Value");


        frenchLocale = new Locale();
        editingContext.insertObject(frenchLocale);
        frenchLocale.setName(FRENCH);

        LookupLocalization aLocalization = new LookupLocalization();
        editingContext.insertObject(aLocalization);
        aLocalization.setLocale(frenchLocale);
        aLocalization.setLookup(localizedInstance);
        aLocalization.setLocalizedName("Some French Value");
        localizedInstance.addToLocalizations(aLocalization);

        defaultTestObjectName = DefaultValueRetrieval.defaultString(instanceOne
                .getClass(), "TestLookup.defaultName");
        defaultTestObject.setName(defaultTestObjectName);

        saveChangesShouldntThrow("creating Objects");
    }



    public void tearDown() throws java.lang.Exception
    {
        editingContext.deleteObject(instanceOne);
        editingContext.deleteObject(instanceTwo);
        editingContext.deleteObject(instanceThree);

        editingContext.deleteObject(frenchLocale);
        editingContext.deleteObject(localizedInstance);

        editingContext.deleteObject(defaultTestObject);

        saveChangesShouldntThrow("destroying Objects");

        super.tearDown();
    }



    /**
     * Array of Ordered objects for the Entity being tested
     */
    protected NSArray orderedObjects()
    {
        return TestLookup.orderedObjects(editingContext());

        /** ensure [Result_not_null] Result != null; **/
    }



    public void testObjectExistsForKeyAndValue()
    {
    	assertTrue("Should have returned true", Lookup.objectExistsForKeyAndValue(editingContext(), "TestLookup", "name", "Some Value"));
    	assertFalse("Should have returned false", Lookup.objectExistsForKeyAndValue(editingContext(), "TestLookup", "name", "Value that will never appear"));
    }



    public void testFetchCount()
    {
        NSArray resultArray = orderedObjects();
        assertTrue("results should have three or more object objects",
                resultArray.count() >= 3);
    }



    public void testObjectOrder()
    {
        NSArray resultArray = orderedObjects();

        for (int i = resultArray.count() - 1; i > 0; i--)
        {
            Lookup firstObject = (Lookup) resultArray.objectAtIndex(i - 1);
            Lookup secondObject = (Lookup) resultArray.objectAtIndex(i);
            String firstName = firstObject.name();
            String secondName = secondObject.name();
            assertTrue("firstName is equal to or less then secondName", firstName.compareToIgnoreCase(secondName) <= 0);
        }
    }



    /**
     * tests defaultObject
     */
    public void testDefaultObject()
    {
        Lookup result = (Lookup) Lookup.defaultObject(editingContext(),
                defaultTestObject.getClass()); // this way we have correct subclass
        assertTrue("result equals defaultTestObject", result
                .equals(defaultTestObject));
    }



    /**
     * tests localization
     */
    public void testLocalization()
    {
        assertTrue(localizedInstance.localizedNameExists(FRENCH));
        assertFalse(localizedInstance.localizedNameExists("Some non-existant localization"));
        assertEquals("Some French Value", localizedInstance.localizedName(FRENCH));
        assertEquals("Some French Value", localizedInstance.localizedNameWithDefault(FRENCH));
        assertEquals("Some Value", localizedInstance.localizedNameWithDefault("Some non-existant localization"));
    }



}
