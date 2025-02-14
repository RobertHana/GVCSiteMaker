package net.global_village.foundation.tests;

import java.util.Vector;

import junit.framework.TestCase;
import net.global_village.foundation.DynamicInstanceVariable;

import com.webobjects.foundation.NSMutableDictionary;


/*
 * Test the dynamic instance variable functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DynamicInstanceVariableTest extends TestCase
{
    private String testStringObject;
    private Vector testVectorObject;
    private Integer numberInstanceVariable;
    private String stringInstanceVariable;


    public DynamicInstanceVariableTest(String name)
    {
        super(name);
    }



    /**
     * Sets up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        testStringObject = new String();
        testVectorObject = new Vector();
        numberInstanceVariable = new Integer(0);
        stringInstanceVariable = new String();
    }



    /**
     * Test that two hash codes are different.
     */
    public void testIVarHashesAreDifferent()
    {
        int testStringObjectHash = testStringObject.hashCode();
        int testVectorObjectHash = testVectorObject.hashCode();
        assertTrue(testStringObjectHash != testVectorObjectHash);
    }



    /**
     * Test that the namespaces for two different variables are different.
     */
    public void testNamespacesAreDifferent()
    {
        DynamicInstanceVariable.setInstanceVariable(testStringObject,
                                                    "testInstanceVariable",
                                                    new Integer(123123));
        NSMutableDictionary testStringObjectNamespace =
            DynamicInstanceVariable.namespace((Object)testStringObject);
        NSMutableDictionary testVectorObjectNamespace =
            DynamicInstanceVariable.namespace((Object)testVectorObject);
        assertTrue( ! testStringObjectNamespace.equals(testVectorObjectNamespace));
    }



    /**
     * Test that setInstanceVariable works and that instanceVariableExists returns true for a variable that was set.
     */
    public void testIVarSetAndExists()
    {
        DynamicInstanceVariable.setInstanceVariable(testStringObject,
                                                    "testInstanceVariable",
                                                    new Integer(123123));
        assertTrue(DynamicInstanceVariable.instanceVariableExists(testStringObject,
                                                                     "testInstanceVariable"));
        DynamicInstanceVariable.setInstanceVariable(testStringObject,
                                                    "testInstanceVariable2",
                                                    numberInstanceVariable);
        assertTrue(DynamicInstanceVariable.instanceVariableExists(testStringObject,
                                                                     "testInstanceVariable2"));
        DynamicInstanceVariable.setInstanceVariable(testVectorObject,
                                                    "testInstanceVariable",
                                                    "StringValue");
        assertTrue(DynamicInstanceVariable.instanceVariableExists(testVectorObject,
                                                                     "testInstanceVariable"));

        assertTrue( ! DynamicInstanceVariable.instanceVariableExists(testStringObject,
                                                                        "asdf"));
        assertTrue( ! DynamicInstanceVariable.instanceVariableExists(testVectorObject,
                                                                        "testInstanceVariable2"));
    }



    /**
     * Test that instanceVariable() returns the same ivar that setInstanceVariable set.
     */
    public void testIVarSetAndGet()
    {
        Object instanceVariable;

        DynamicInstanceVariable.setInstanceVariable(testStringObject,
                                                    "numberInstanceVariable",
                                                    numberInstanceVariable);
        instanceVariable = DynamicInstanceVariable.instanceVariable(testStringObject,
                                                                    "numberInstanceVariable");
        assertNotNull(instanceVariable);
        assertEquals(numberInstanceVariable, instanceVariable);

        DynamicInstanceVariable.setInstanceVariable(testVectorObject,
                                                    "stringInstanceVariable",
                                                    stringInstanceVariable);
        instanceVariable = DynamicInstanceVariable.instanceVariable(testVectorObject,
                                                                    "stringInstanceVariable");
        assertNotNull(instanceVariable);
        assertEquals(stringInstanceVariable, instanceVariable);
    }



    /**
     * Test that an ivar removed by removeInstanceVariable is removed properly.
     */
    public void testIVarSetAndRemove()
    {
        DynamicInstanceVariable.setInstanceVariable(testStringObject,
                                                    "numberInstanceVariable",
                                                    numberInstanceVariable);
        DynamicInstanceVariable.removeInstanceVariable(testStringObject,
                                                       "numberInstanceVariable");
        assertTrue( ! DynamicInstanceVariable.instanceVariableExists(testStringObject,
                                                                        "numberInstanceVariable"));

        DynamicInstanceVariable.setInstanceVariable(testVectorObject,
                                                    "stringInstanceVariable",
                                                    stringInstanceVariable);
        DynamicInstanceVariable.removeInstanceVariable(testVectorObject,
                                                       "stringInstanceVariable");
        assertTrue( ! DynamicInstanceVariable.instanceVariableExists(testVectorObject,
                                                                        "stringInstanceVariable"));
    }



    /**
     * Tears down the fixtures.
     * @exception Exception an exception that the tear down may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        DynamicInstanceVariable.deallocInstanceVariables(testStringObject);
        DynamicInstanceVariable.deallocInstanceVariables(testVectorObject);
        super.tearDown();
    }



}
