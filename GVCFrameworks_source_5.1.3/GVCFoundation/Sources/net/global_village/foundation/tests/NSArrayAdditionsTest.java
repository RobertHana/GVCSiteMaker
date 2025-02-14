package net.global_village.foundation.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;



/*
 * Test NSArrayAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class NSArrayAdditionsTest extends TestCase
{


    public NSArrayAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Test sortedArrayWithComparator.
     */
    public void testSortedArrayUsingComparator()
    {
        NSArray testArray = new NSArray(new Object[] {"T", "H", "I", "S", "T", "R", "Y"});
        NSArray sortedArray = new NSArray(new Object[] {"H", "I", "R", "S", "T", "T", "Y"});

        try
        {
            assertTrue(NSArrayAdditions.sortedArrayUsingComparator(testArray, NSComparator.AscendingStringComparator).equals(sortedArray));
        }
        catch (Throwable e)
        {
            fail("NSArrayAdditions.sortedArrayUsingComparator() raised an exception: " + e);
        }

    }


    /**
     * Test sortedArrayWithComparator.
     */
    public void testSortUsingComparator()
    {
        NSMutableArray testArray = new NSMutableArray(new String[] {"T", "H", "I", "S", "T", "R", "Y"});
        NSMutableArray sortedArray = new NSMutableArray(new String[] {"H", "I", "R", "S", "T", "T", "Y"});

        try
        {
            NSArrayAdditions.sortUsingComparator(testArray, NSComparator.AscendingStringComparator);
            assertTrue(testArray.equals(sortedArray));
        }
        catch (Throwable e)
        {
            fail("NSArrayAdditions.sortedArrayUsingComparator() raised an exception: " + e);
        }

    }



    /**
     * Test caseInsensitiveContains.
     */
    public void testCaseInsensitiveContains()
    {
        NSArray testArray = new NSArray(new String[] {"FRED", "Joe", "mary"});
        assertTrue(NSArrayAdditions.caseInsensitiveContains(testArray, "FRED"));
        assertTrue(NSArrayAdditions.caseInsensitiveContains(testArray, "joe"));
        assertTrue(NSArrayAdditions.caseInsensitiveContains(testArray, "MARY"));
        assertFalse(NSArrayAdditions.caseInsensitiveContains(testArray, "Bill"));
    }



    /**
     * Test insertObjectsAtIndex.
     */
    public void testInsertObjectsAtIndex()
    {
        NSMutableArray testArray = new NSMutableArray(new Object[] {"T", "H", "T", "Y"});
        NSArray insertArray = new NSArray(new Object[] {"I", "R", "S"});
        NSArrayAdditions.insertObjectsAtIndex(testArray, insertArray, 2);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"T", "H", "I", "R", "S"});
        insertArray = new NSArray(new Object[] {"T", "Y"});
        NSArrayAdditions.insertObjectsAtIndex(testArray, insertArray, 5);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));
    }



    /**
     * Test moveObjectsInRangeToIndex.
     */
    public void testMoveObjectsInRangeToIndex()
    {
        NSMutableArray testArray = new NSMutableArray(new Object[] {"T", "H", "S", "T", "I", "R", "Y"});
        NSRange testRange = new NSRange(4, 2);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 2);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"H", "I", "R", "S", "T", "T", "Y"});
        testRange = new NSRange(4, 1);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 0);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"S", "T", "Y", "T", "H", "I", "R"});
        testRange = new NSRange(0, 3);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 7);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"T", "Y", "H", "I", "R", "S", "T"});
        testRange = new NSRange(2, 5);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 1);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"H", "I", "R", "S", "T", "T", "Y"});
        testRange = new NSRange(0, 5);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 6);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"T", "I", "H", "R", "S", "T", "Y"});
        testRange = new NSRange(1, 1);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 3);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));

        testArray = new NSMutableArray(new Object[] {"T", "H", "S", "I", "R", "T", "Y"});
        testRange = new NSRange(2, 1);
        NSArrayAdditions.moveObjectsInRangeToIndex(testArray, testRange, 5);
        assertEquals("THIRSTY", testArray.componentsJoinedByString(""));
    }



}
