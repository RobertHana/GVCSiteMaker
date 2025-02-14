package net.global_village.foundation;

import java.util.*;

import com.webobjects.foundation.*;


/**
 * Convenience and bug fixes for NSArray / NSMutableArray.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class NSArrayAdditions extends Object 
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private NSArrayAdditions()
    {
        super();
    }



    /**
     * A convenience method wrapping <code>array.sortedArrayUsingComparator(comparator)</code> which converts <code>NSComparator.ComparisonException</code> into a <code>RuntimeException</code>.
     *
     * @param array the list of objects to sort
     * @param comparator the <code>NSComparator</code> instance to use to sort array
     * @return the objects in array sorted by comparator
     * @exception RuntimeException if <code>NSArray.sortedArrayUsingComparator</code> raises a <code>NSComparator.ComparisonException</code> exception
     */
    public static NSArray sortedArrayUsingComparator(NSArray array, NSComparator comparator)
    {
        /** require
        [valid_array_param] array != null;
        [valid_comparator_param] comparator != null; **/

        NSArray orderedList = null;

        try
        {
            orderedList = array.sortedArrayUsingComparator( comparator );
        }
        catch (com.webobjects.foundation.NSComparator.ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }

        JassAdditions.post("NSArrayAdditions", "sortedArrayUsingComparator", orderedList.count() == array.count());
        return orderedList;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * A convenience method wrapping <code>array.sortUsingComparator(comparator)</code> which converts 
     * <code>NSComparator.ComparisonException</code> into a <code>RuntimeException</code>.
     *
     * @param array the list of objects to sort
     * @param comparator the <code>NSComparator</code> instance to use to sort array
     * @exception RuntimeException if <code>NSArray.sortUsingComparator</code> raises a <code>NSComparator.ComparisonException</code> exception
     */
    public static void sortUsingComparator(NSMutableArray array, NSComparator comparator)
    {
        /** require
        [valid_array_param] array != null;
        [valid_comparator_param] comparator != null; **/

        try
        {
            array.sortUsingComparator( comparator );
        }
        catch (com.webobjects.foundation.NSComparator.ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }
    }
    

     
    /**
     * A string specific method to replace NSArray.containsObject which uses equals() for comparisons.
     * This method ignores case when determining if an array contains a string.
     * 
     * @param array <b>must be an array of java.lang.String
     * @param targetString the string to find in array
     * @return true if target can be found in array, ignoring case
     */
    public static boolean caseInsensitiveContains(NSArray array, String targetString) 
    {
        /** require [valid_array] array != null;  
                    [valid_targetString] targetString != null;       **/
        
        boolean contains = false;
        
        Enumeration arrayEnum = array.objectEnumerator();
        while (arrayEnum.hasMoreElements() && ! contains) {
            contains = targetString.equalsIgnoreCase((String) arrayEnum.nextElement());
        }
        
        return contains;
    }



    /**
     * Inserts objectsToInsert into array at indexToInsertAt.
     * 
     * @param array the array to insert objects to
     * @param objectsToInsert the objects to insert
     * @param indexToInsertAt the index in array to insert the objects at
     */
    public static void insertObjectsAtIndex(NSMutableArray array, NSArray objectsToInsert, int indexToInsertAt) 
    {
        /** require
        [valid_array] array != null;  
        [valid_range] objectsToInsert != null;
        [valid_index] indexToInsertAt > -1 && indexToInsertAt <= array.count(); **/

        Enumeration reverseEnumerator = objectsToInsert.reverseObjectEnumerator();
        while (reverseEnumerator.hasMoreElements())
        {
            Object anObject = reverseEnumerator.nextElement();
            array.insertObjectAtIndex(anObject, indexToInsertAt);
        }
    }



    /**
     * Moves the objects in the given range of the given mutable array to the given
     * index.
     * 
     * @param array the array to move objects in
     * @param range the range of objects to move
     * @param moveToIndex the index in array to move the objects to
     */
    public static void moveObjectsInRangeToIndex(NSMutableArray array, NSRange range, int moveToIndex) 
    {
        /** require
        [valid_array] array != null;  
        [valid_range] range != null && ! range.containsLocation(moveToIndex);
        [valid_index] moveToIndex > -1 && moveToIndex <= array.count(); **/

        // if the moveTo index is after the range, subtract the length of the range
        // since we will be removing those before we insert
        if (moveToIndex > range.location() + range.length())
        {
            moveToIndex -= range.length();
        }
        NSArray objectsToMove = array.subarrayWithRange(range);
        array.removeObjectsInRange(range);
        insertObjectsAtIndex(array, objectsToMove, moveToIndex);
    }



}
