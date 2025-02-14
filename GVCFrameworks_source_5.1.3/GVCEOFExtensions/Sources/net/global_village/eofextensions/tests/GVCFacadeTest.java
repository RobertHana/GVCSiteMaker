package net.global_village.eofextensions.tests;

import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;



/**
 * Test of GVCFacade funtionality through a subclass TestFacade
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class GVCFacadeTest extends GVCJUnitEOTestCase
{
    protected SimpleTestEntity testEntity;
    protected SimpleTestEntity testEntityTwo;
    protected TestFacade testFacadeOne;  // has constructor created focusObject
    protected TestFacade testFacadeTwo; // hasNewlyInsertedFocusObject() 
    protected TestFacade testFacadeThree;  // has not newly inserted focusObject


    /**
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
    public GVCFacadeTest(String name)
    {
        super(name);
    }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        testEntityTwo = new SimpleTestEntity();
        editingContext().insertObject(testEntityTwo);
        testEntityTwo.setName("testEntityTwoFacade");

        editingContext().saveChanges(); // results in a not newly inserted focus object

        testEntity = new SimpleTestEntity();
        editingContext().insertObject(testEntity);
        testEntity.setName("testEntityFacade");

        // Facades now always have focusObjects, this uses all four constructors as the three below all point to the fourth one ultimately.
        testFacadeOne = new TestFacade();
        testFacadeTwo = new TestFacade(editingContext(), testEntity);
        testFacadeThree = new TestFacade(testEntityTwo);
    }



    /**
     * Delete inserted objects that were saved
     */
     public void tearDown() throws java.lang.Exception
     {
         editingContext().revert();
         editingContext().deleteObject(testEntityTwo);
         editingContext().saveChanges();
 
         super.tearDown();
    }



    /**
     * tests the copy method.
     */
    public void testCopy()
    {
        try
        {
            testFacadeOne.copy();
            fail("Precondition currently forbids copying of facades with newly inserted focus objects.");
        }
        catch(Exception e) {}

        TestFacade copyOfThree = (TestFacade)testFacadeThree.copy();
        // note equals has been overridden to compare global ids of focus objects
        assertEquals("Copy equals original", copyOfThree, testFacadeThree);
    }


    
    /**
     * tests revert method.
     */
    public void testRevert()
    {
        String newName = "Something new";

        testFacadeTwo.revert();
        assertTrue("testFacadeTwo still has newly inserted Entity Object", testFacadeTwo.isNewlyInsertedFocusObject());

        ((SimpleTestEntity) testFacadeTwo.focusObject()).setName(newName);
        assertTrue("testFacadeTwo's focusObject has been changed", testFacadeTwo.hasChanges());

        testFacadeTwo.revert();
        // the reason it is now null is revert created a new instance as it was never saved
        assertTrue("testFacadeTwo's focusObject no longer has a name.", ((SimpleTestEntity) testFacadeTwo.focusObject()).name() == null);
        // to prevent null exception in tearDown()
        ((SimpleTestEntity) testFacadeTwo.focusObject()).setName("Do not crash");

        assertTrue("testFacadeThree has no changes prior to calling revert", ! testFacadeThree.hasChanges());
        testFacadeThree.revert();
        assertTrue("testFacadeThree still has no changes after calling revert", ! testFacadeThree.hasChanges());

        ((SimpleTestEntity) testFacadeThree.focusObject()).setName(newName);
        assertTrue("testFacadeThree has changes prior to calling revert", testFacadeThree.hasChanges());
        testFacadeThree.revert();
        assertTrue("testFacadeThree's focusObject no longer has name newName", ! ((SimpleTestEntity) testFacadeTwo.focusObject()).name().equals(newName));
        assertTrue("testFacadeThree should not have changes to it's editingContext this time either", ! testFacadeThree.hasChanges());
    }



}
