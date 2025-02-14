package net.global_village.foundation;

import java.math.BigDecimal;

import com.webobjects.foundation.NSArray;


/**
* An operator class that computes the sum of an array's elements based on a keyPath.  For example, to compute the sum of the hours of an array of WorkDetails, the method valueForKeyPath can be used with "@sumScale2.hours" as key path.The operatorNames method returns the keys for the operators that NSArray knows about, and operatorForKey returns the operator for a specified key.    This class can be made available for use with NSArrays with the method setOperatorForKey. <br><br>
 * <pre>
 * // Register a summing the array operator that scales everything to two decimal places.  The name of the operator is @sumScale2
 * new ScaledSumOperator(2);
 * </pre>
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class ScaledSumOperator implements com.webobjects.foundation.NSArray.Operator
{

    private int scale; 

    /**
     * Designated constructor
     *
     * @param places the scale to use for the resulting sum of the array's elements
     */
    public ScaledSumOperator(int places)
    {
        super();

        /** require
        [valid_param] places >= 0;
        **/
        
        scale = places;
        NSArray.setOperatorForKey(operatorName(), this);

        /** ensure
        [scale_is_valid] scale >= 0;
        [operator_is_registered] NSArray.operatorForKey(operatorName()) == this;
        **/
    }

    

    /**
     * Returns the operator's name as "sumScale<scale>", ie "sumScale2", "sumScale4"
     *
     * return the operator's name as "sumScale<scale>", ie "sumScale2", "sumScale4"
     */
    public String operatorName()
    {
        return "sumScale" + new Integer(scale);

        /* [Result_not_null] Result != null; */
    }

    

    /**
     * Returns the sum of the array's elements based on the keyPath with the set scale.
     *
     * @param values array of input objects
     * @param keyPath property of the elements in values to perform the operation on
     * @return the sum of the array's elements based on the keyPath with the set scale.
     */
    public Object compute(NSArray values, String keyPath)
    {
        /** require
        [valid_param] values != null;
        [valid_param] keyPath != null; 
        **/
        
        Object theValue = values.valueForKeyPath("@sum." + keyPath);
       
        return ((BigDecimal) theValue).setScale(scale, BigDecimal.ROUND_HALF_UP);

        /** ensure [Result_not_null] Result != null; **/
    }



}
