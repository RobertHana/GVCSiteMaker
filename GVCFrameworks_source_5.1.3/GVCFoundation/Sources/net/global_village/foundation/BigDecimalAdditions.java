package net.global_village.foundation;

import java.math.BigDecimal;


/**
 * BigDecimal utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class BigDecimalAdditions extends Object
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private BigDecimalAdditions()
    {
        super();
    }



    /**
     * Checks if the given <code>BigDecimal</code> is between the two values.
     *
     * @param value the value being compared
     * @param lower the lower value
     * @param upper the upper value
     * @param lowerInclusive should the lower comparison be inclusive or exclusive?
     * @param upperInclusive should the upper comparison be inclusive or exclusive?
     * @return <code>true</code> if value is between (inclusively or exclusively, as determined by the two inclusive params) <code>upper</code> and <code>lower</code>, <code>false</code> otherwise
     */
    public static boolean between(BigDecimal value, BigDecimal lower, BigDecimal upper, boolean lowerInclusive, boolean upperInclusive)
    {
        /** require
        [valid_value_param] value != null;
        [valid_lower_param] lower != null;
        [valid_upper_param] upper != null; **/

        int lowerComparisonValue = lowerInclusive ? 0 : 1;
        int upperComparisonValue = upperInclusive ? 0 : -1;
        return (value.compareTo(lower) >= lowerComparisonValue) && (value.compareTo(upper) <= upperComparisonValue);
    }



    /**
     * Checks if the given <code>BigDecimal</code> is between the two values.
     *
     * @param value the value being compared
     * @param lower the lower value
     * @param upper the upper value
     * @param inclusive should the comparison be inclusive or exclusive?
     * @return <code>true</code> if value is between (inclusively or exclusively, as determined by <code>inclusive</code>) <code>upper</code> and <code>lower</code>, <code>false</code> otherwise
     */
    public static boolean between(BigDecimal value, BigDecimal lower, BigDecimal upper, boolean inclusive)
    {
        /** require
        [valid_value_param] value != null;
        [valid_lower_param] lower != null;
        [valid_upper_param] upper != null; **/

        return between(value, lower, upper, inclusive, inclusive);
    }



    /**
     * Compares two BigDecimals for equality within (1 / 10^<minimum scale>) tolerance.  First changes the scale of the most precise number by lowering it to the scale of the least precise, rounding as necessary.  Then subtracts the two numbers, takes the absolute value and multiply's that by 10^<minimum scale>.  If that number is less than or equal to 1, then the numbers are considered equal.  This is designed for use when comparing two values, one of which may have been rounded during calculation.  Note that this uses the ROUND_HALF_DOWN rounding mode to make the more precise param less precise.  This may affect the result you expect, so be careful.<p>
     * For example:<br>
     * 2.6543  (scale = 4)   compared to<br>
     * 2.6     (scale = 1)   will result in true (as will 2.7 and 2.8)<br>
     * 2.6543  (scale = 4)   compared to<br>
     * 2.9     (scale = 1)   will result in false (as will 2.5)<br>
     *
     * @param op1 one of the BigDecimals to compare
     * @param op2 one of the BigDecimals to compare
     * @return <code>true</code> if the two <code>BigDecimals</code> are equal to within (1 / 10^<minimum scale>), after setting both scales to the least precise scale, <code>false</code> otherwise
     */
    public static boolean equalsWithScale(BigDecimal op1, BigDecimal op2)
    {
        /** require [valid_op1_param] op1 != null; [valid_op2_param] op2 != null; **/

        int op1Scale = op1.scale();
        int op2Scale = op2.scale();
        if (op1Scale > op2Scale)
        {
            op1 = op1.setScale(op2Scale, BigDecimal.ROUND_HALF_DOWN);
        }
        else if (op2Scale > op1Scale)
        {
            op2 = op2.setScale(op1Scale, BigDecimal.ROUND_HALF_DOWN);
        }

        BigDecimal tester = op1.subtract(op2).abs();
        tester = tester.movePointRight(op1.scale());

        if (tester.compareTo(new BigDecimal("1")) <= 0)
            return true;
        else
            return false;
    }



}
