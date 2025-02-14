package net.global_village.foundation.tests;

import java.math.BigDecimal;

import junit.framework.TestCase;
import net.global_village.foundation.ScaledSumOperator;

import com.webobjects.foundation.NSArray;


/**
 * Test ScaledSumOperator functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ScaledSumOperatorTest extends TestCase
{
    public ScaledSumOperatorTest(String name)
    {
        super(name);
    }



    /**
     * Setup test
     *
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
    }



    /**
     * Test compute
     */
    public void testCompute()
    {
        try
        {
            new ScaledSumOperator(-1);
            fail("Expected exception did not occur: places >= 0");
        }
        catch (RuntimeException e) { }
        
        //test 2 decimal places
        new ScaledSumOperator(2);
        
        ObjectWithBigDecimal object1 = new ObjectWithBigDecimal();
        object1.setQuantity(new BigDecimal("1.2"));
        ObjectWithBigDecimal object2 = new ObjectWithBigDecimal();
        object2.setQuantity(new BigDecimal("4.354567"));
        ObjectWithBigDecimal object3 = new ObjectWithBigDecimal();
        object3.setQuantity(new BigDecimal("16"));
        ObjectWithBigDecimal object4 = new ObjectWithBigDecimal();
        object4.setQuantity(new BigDecimal("-31.2"));

        NSArray array = new NSArray(new Object[]{object1, object2, object3, object4});
        assertTrue("correct sum and scale2", array.valueForKeyPath("@sumScale2.quantity").equals(new BigDecimal("-9.65")));

        //test 4 decimal places
        new ScaledSumOperator(4);
        assertTrue("correct sum and scale4", array.valueForKeyPath("@sumScale4.quantity").equals(new BigDecimal("-9.6454")));

    }

}
