package net.global_village.foundation.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.GVCBoolean;


/*
 * Test the Boolean functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class BooleanTest extends TestCase
{
    private GVCBoolean trueValue;
    private GVCBoolean falseValue;


    public BooleanTest(String name)
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
        trueValue = new GVCBoolean(true);
        falseValue = new GVCBoolean(false);
    }



    /**
     * Test the various factory methods and isTrue and isFalse.
     */
    public void testFactoryMethods()
    {
        assertTrue(trueValue.isTrue());
        assertTrue(falseValue.isFalse());

        trueValue = GVCBoolean.booleanWithBoolean(true);
        falseValue = GVCBoolean.booleanWithBoolean(false);

        assertTrue(trueValue.isTrue());
        assertTrue(falseValue.isFalse());

        trueValue = GVCBoolean.booleanWithString("Y");
        falseValue = GVCBoolean.booleanWithString("N");

        assertTrue(trueValue.isTrue());
        assertTrue(falseValue.isFalse());

        assertTrue(GVCBoolean.yes().isTrue());
        assertTrue(GVCBoolean.no().isFalse());

        try
        {
            GVCBoolean.booleanWithString("n");
            fail("Expected exception did not occur: missing precondition on booleanWithString()");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test booleanValue() and stringValue().
     */
    public void testSpecialAccessors()
    {
        assertTrue(trueValue.booleanValue() == true);
        assertTrue(falseValue.booleanValue() == false);

        assertTrue(trueValue.stringValue().equals("Y"));
        assertTrue(falseValue.stringValue().equals("N"));
    }



    /**
     * Test toString().
     */
    public void testToString()
    {
        assertTrue(trueValue.toString().equals("Yes"));
        assertTrue(falseValue.toString().equals("No"));
    }



    /**
     * Test negate().
     */
    public void testNegate()
    {
        assertTrue(trueValue.negate().equals(falseValue));
        assertTrue(falseValue.negate().equals(trueValue));
    }



    /**
     * Test equals().
     */
    public void testEquals()
    {
        assertTrue(trueValue.equals(new GVCBoolean(true)));
        assertTrue(falseValue.equals(new GVCBoolean(false)));
        assertTrue( ! trueValue.equals(new GVCBoolean(false)));
        assertTrue( ! falseValue.equals(new GVCBoolean(true)));
        assertTrue( ! falseValue.equals(new NSArray()));
    }



    /**
     * Test compareTo().
     */
    public void testCompareTo()
    {
        assertTrue(trueValue.compareTo(falseValue) >= 1);
        assertTrue(falseValue.compareTo(trueValue) <= -1);
        assertTrue(trueValue.compareTo(trueValue) == 0);
        assertTrue(falseValue.compareTo(falseValue) == 0);
    }



    /**
     * Test clone().
     */
    public void testClone()
    {
        assertNotNull(trueValue.clone());
        assertTrue(trueValue.clone().equals(new GVCBoolean(true)));
    }



}
