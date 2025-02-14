// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.util.*;

import net.global_village.foundation.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

public class ArrayExtras {

    public static final String ASCENDING_SORT = "ascending";

    public static NSDictionary splitArrayBasedOnKey(NSArray arrayToProcess, String errorMessageKey) {

        NSMutableArray tmpValidObjects = new NSMutableArray();
        NSMutableArray tmpInvalidObjects = new NSMutableArray();
        Enumeration e = arrayToProcess.objectEnumerator();

        while( e.hasMoreElements() ) {
            NSDictionary tmpDict = (NSDictionary)e.nextElement();

            if( tmpDict.objectForKey(errorMessageKey) == null ) {
                tmpValidObjects.addObject(tmpDict);
            }
            else {
                tmpInvalidObjects.addObject(tmpDict);
            }
        }

        NSMutableDictionary dict = new NSMutableDictionary();
        dict.setObjectForKey(tmpValidObjects, "valid");
        dict.setObjectForKey(tmpInvalidObjects, "invalid");
        return dict;
    }

    // Allows you to sort an array based on an attribute or key path to an attribute.
    public static NSArray sortedArray( NSArray anArray, String attribute, String direction ) {
        NSSelector theDirection;

        if ( direction.equals( ASCENDING_SORT ) )
            theDirection = EOSortOrdering.CompareCaseInsensitiveAscending;
        else
            theDirection = EOSortOrdering.CompareCaseInsensitiveDescending;

        Object orderings[]= { EOSortOrdering.sortOrderingWithKey( attribute, theDirection ) };
        return EOSortOrdering.sortedArrayUsingKeyOrderArray ( anArray, new NSArray( orderings ) );
    }

    // this method is the same as that above put sorts the array in place rather than returning a new array
    public static void sortArray( NSMutableArray anArray, String attribute, String direction ) {
        NSSelector theDirection;

        if ( direction.equals( ASCENDING_SORT ) )
            theDirection = EOSortOrdering.CompareCaseInsensitiveAscending;
        else
            theDirection = EOSortOrdering.CompareCaseInsensitiveDescending;

        Object orderings[]= { EOSortOrdering.sortOrderingWithKey( attribute, theDirection ) };
        EOSortOrdering.sortArrayUsingKeyOrderArray ( anArray, new NSArray( orderings ) );
    }

  
    
    /**
     * Returns a copy of anArray that lists this array's elements in an order determined by aComparator.  This is a modified version of NSArray's sortedArrayUsingComparator(NSComparator) which throws a RuntimeException instead of NSComparator.ComparisonException.  It should be used in cases where this exception does not need to be handled.
     *
     * @param anArray - the array to be sorted.
     * @param aComparator - It determines the ordering in the new sorted array by comparing two elements at a time and returning one of OrderedAscending, OrderedSame or OrderedDescending
     * @return a new sorted array that lists this array's elements in an order determined by comparator
     */
    public static NSArray sortedArrayUsingComparator( NSArray anArray, NSComparator aComparator)
    {
        /** require
        [an_array_not_null] anArray != null;
        [a_comparator_not_null] aComparator != null; **/
        
        NSArray sortedArray = null;
        
        try
        {
            sortedArray = anArray.sortedArrayUsingComparator(aComparator);
        }
        catch (NSComparator.ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }

        return sortedArray;

        /** ensure [result_not_null] Result != null; **/
    }
}
