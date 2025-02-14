package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.woextensions.orderablelists.*;

/**
 * Describe class here.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class OrderableListTest extends TestCase {

    protected NSArray originalList;
    protected NSArray reorderedList;
    protected NSArray reorderedFirstList;
    protected NSArray reorderedLastList;
    protected OrderableList list;
    
    
    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        originalList = new NSArray(new Object[] {"One", "Two", "Three", "Four", "Five"});
        reorderedFirstList = new NSArray(new Object[] {"Three", "One", "Two", "Four", "Five"});
        reorderedList = new NSArray(new Object[] {"One", "Two", "Four", "Three", "Five"});
        reorderedLastList = new NSArray(new Object[] {"One", "Two", "Four", "Five", "Three"});
        list = new OrderableList(originalList);
    }
    
    
    public void testPositionsForObject() {
        assertTrue(list.positionsForObject("One").equals(
                new NSArray(new Object[] {new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5)})));
    }

    
    
    public void testPositionNameForObject() {
        assertTrue(list.positionNameForObject("One").equals(new Integer(1)));
        assertTrue(list.positionNameForObject("Three").equals(new Integer(3)));
        assertTrue(list.positionNameForObject("Five").equals(new Integer(5)));
    }

    
    
    public void testPositionForPositionName() {
        for (int i = 0;  i < originalList.count(); i++)
        {
            assertTrue(list.positionForPositionName(list.positionNameForObject(originalList.objectAtIndex(i))) == i);
        }
    }

 
    
    public void testCanMoveObjectToPostition() {
        assertTrue(list.canMoveObjectToPosition("One", 1));
    }

    
    
    public void testMoveObjectToPosition1() {
        list.moveObjectToPosition("Three", 0);
        assertTrue(list.ordered().toString(), list.ordered().equals(reorderedFirstList));
    }

        
   
    public void testMoveObjectToMidPosition() {
        list.moveObjectToPosition("Three", 3);
        assertTrue(list.ordered().toString(), list.ordered().equals(reorderedList));
    }

        
   
    public void testMoveObjectToPositionN() {
        list.moveObjectToPosition("Three", 4);
        assertTrue(list.ordered().toString(), list.ordered().equals(reorderedLastList));
    }

        
   
    public void testOrdered() {
        assertTrue(list.ordered().equals(originalList));
    }

}
