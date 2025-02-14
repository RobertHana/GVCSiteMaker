package net.global_village.foundation;

import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * Simple comparator to compare objects based on values determined from a keypath.  The keypath must
 * terminate in a String, Number, or NSTimestmap value.  Thus objects can be sorted like this:<br/>
 * <pre>
 * NSComparator nameComparator = new KeyValueComparator("name", NSComparator.OrderedAscending);
 * NSArray result = testObjects.sortedArrayUsingComparator(nameComparator);
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class KeyValueComparator extends NSComparator
{
    private int direction;
    private String keyPath;
    private NSComparator defaultComparator = null;
    
    
        
    /**
     * Designated constructor.
     * 
     * @param aKeyPath the keyPath to use to find the actual values to compare
     * @param aDirection either of NSComparator.OrderedAscending or NSComparator.OrderedDescending
     */
    public KeyValueComparator(String aKeyPath, int aDirection)
    {
        super();
        
        /** require [valid_keyPath] aKeyPath != null;
                    [valid_direction] (aDirection == NSComparator.OrderedAscending) ||
                                      (aDirection == NSComparator.OrderedDescending);    **/
        keyPath = aKeyPath;
        direction = aDirection;
     
    }
    
    
    
    public int compare(Object object1,
                       Object object2)  throws NSComparator.ComparisonException
    {
        if ((object1 == null) || (object2 == null))
        {
            throw new NSComparator.ComparisonException("Can't compare null objects");
        }
        
        Object value1 = NSKeyValueCodingAdditions.Utility.valueForKeyPath(object1, keyPath());
        Object value2 = NSKeyValueCodingAdditions.Utility.valueForKeyPath(object2, keyPath());

        if ((value1 == null) || (value2 == null))
        {
            throw new NSComparator.ComparisonException("Can't compare null values");
        }
        
        
        return defaultComparator(value1).compare(value1, value2);
    }



    /**
     * Reuturns the primitive comparator to use
     *  
     * @param value value to use to determine type of comparator
     * @return comparator to use to compare type like value
     */
    protected NSComparator defaultComparator(Object value)
    {
        /** require [valid_value] value != null;
                    [valid_type] (value instanceof String) ||
                                 (value instanceof Number) ||
                                 (value instanceof com.webobjects.foundation.NSTimestamp);          **/
        
        if (defaultComparator == null)
        {
            if (direction == NSComparator.OrderedAscending)
            {
                if (value instanceof String)
                {
                    defaultComparator = NSComparator.AscendingStringComparator;
                }
                else if (value instanceof Number)
                {
                    defaultComparator = NSComparator.AscendingNumberComparator;
                }
                else
                {
                    defaultComparator = NSComparator.AscendingTimestampComparator;
                }
            }
            else
            {
                if (value instanceof String)
                {
                    defaultComparator = NSComparator.DescendingStringComparator;
                }
                else if (value instanceof Number)
                {
                    defaultComparator = NSComparator.DescendingNumberComparator;
                }
                else
                {
                    defaultComparator = NSComparator.DescendingTimestampComparator;
                }
            }
        }
        
        return defaultComparator;
        /** ensure [valid_result] defaultComparator != null;   **/
    }



    /**
     * Returns the comparison direction.
     * 
     * @return the comparison direction
     */
    public int direction()
    {
        return direction;
    }



    /**
     * Returns the keypath used to find the actual values to compare.
     * 
     * @return the keypath used to find the actual values to compare
     */
    public String keyPath()
    {
        return keyPath;
    }


}
