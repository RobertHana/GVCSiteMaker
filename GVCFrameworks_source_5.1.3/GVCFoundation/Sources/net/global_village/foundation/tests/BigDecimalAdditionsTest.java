package net.global_village.foundation.tests;

import java.math.BigDecimal;

import junit.framework.TestCase;
import net.global_village.foundation.BigDecimalAdditions;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class BigDecimalAdditionsTest extends TestCase
{
    BigDecimal test1;
    BigDecimal test2;
    BigDecimal test3;
    BigDecimal test4;
    BigDecimal test5;
    BigDecimal test6;
    BigDecimal test7;
    BigDecimal test8;
    BigDecimal test9;
    BigDecimal test10;
    BigDecimal test11;
    BigDecimal test12;
    

    public BigDecimalAdditionsTest(String name)
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
        test1 = new BigDecimal("2.6543");
        test2 = new BigDecimal("2.7");
        test3 = new BigDecimal("2.6");
        test4 = new BigDecimal("2.5");
        test5 = new BigDecimal("2.8");
        test6 = new BigDecimal("2.9");
        test7 = new BigDecimal("12.6");
        test8 = new BigDecimal("2.65432");
        test9 = new BigDecimal("2.65439");
        test10 = new BigDecimal("2.6544");
        test11 = new BigDecimal("2.6543");
        test12 = new BigDecimal("2.65449");
    }



    /**
     * Test equalsWithScale.
     */
    public void testEqualsWithScale()
    {
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test2));
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test3));
        assertTrue( ! BigDecimalAdditions.equalsWithScale(test1, test4));
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test5));
        assertTrue( ! BigDecimalAdditions.equalsWithScale(test1, test6));
        assertTrue( ! BigDecimalAdditions.equalsWithScale(test1, test7));
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test8));
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test9));
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test10));
        assertTrue(BigDecimalAdditions.equalsWithScale(test1, test11));
        assertTrue( ! BigDecimalAdditions.equalsWithScale(test1, test12));
    }



    /**
     * Test equalsWithScale.
     */
    public void testBetween()
    {
        assertTrue(BigDecimalAdditions.between(test3, test4, test2, true));
        assertTrue(BigDecimalAdditions.between(test3, test3, test2, true));
        assertTrue(BigDecimalAdditions.between(test3, test4, test3, true));
        assertTrue(BigDecimalAdditions.between(test3, test3, test3, true));
        assertTrue( ! BigDecimalAdditions.between(test3, test3, test2, false));
        assertTrue( ! BigDecimalAdditions.between(test3, test4, test3, false));
        assertTrue( ! BigDecimalAdditions.between(test3, test3, test3, false));
    }



}
