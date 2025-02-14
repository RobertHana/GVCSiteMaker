package net.global_village.woextensions.orderablelists;

import com.webobjects.foundation.*;


/**
 * This object holds an array that can be sorted and reordered. It is intended
 * for use with the UI widgets in this package, but other resuse is possible.
 * This is not a UI widget itself, but instead serves to act as a common point
 * of reference for the UI component which can't communicate peer to peer
 * (parent to child is possbible, but makes for a hard to maintain UI in
 * WOBuilder). <br/> <p>For manual re-ordering of the list, use ReorderPopup or
 * a similar component. ReorderPopup <b>must</b> go in a form.</p> <br/>
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights
 * reserved. This software is published under the terms of the Educational
 * Community License (ECL) version 1.0, a copy of which has been included with
 * this distribution in the LICENSE.TXT file.
 */
public class OrderableList
{
    public NSMutableArray objects;


    /**
     * Designated constructor.
     * 
     * @param sourceArray the list of objects to manage and order
     */
    public OrderableList(NSArray sourceArray)
    {
        super();
        /** require [valid_array] sourceArray != null;  **/
        objects = new NSMutableArray(sourceArray);
    }



    /**
     * Returns an array of Objects indicating the positions that this object can be moved to.  Positions 
     * can be explicitly excluded by canMoveObjectToPostition().  The objects indicating positions can
     * be anything.  By default they are java.lang.Integer, but positionNameForPosition and 
     * positionForPositionName can be overidden to translate between the int position and anything
     * you want to use for the UI.
     *  
     * @param anObject the object to return an array of potential positions for
     * @return an array of Integers indicating the postions that this object can be moved to
     */
    public NSArray positionsForObject(Object anObject)
    {
        /** require
             [valid_object] anObject != null;
             [object_in_list] ordered().containsObject(anObject);    **/

        NSMutableArray positionsForObject = new NSMutableArray();
        for (int i = 0; i < ordered().count(); i++)
        {
            if (canMoveObjectToPosition(anObject, i))
            {
                positionsForObject.addObject(positionNameForPosition(i));
            }
        }

        return positionsForObject;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a UI presentable name for position i, a zero based index.  The default implementation 
     * is new Integer(i + 1).  positionForPositionName must be the inverse of this function.  These 
     * two methods can be overridden to control the UI display of ordering.
     * 
     * @param i the zero based position to return a postion name for
     * @return a UI presentable name for position i
     */
    protected Object positionNameForPosition(int i)
    {
        /** require [valid_position] (i >= 0) && (i < ordered().count());  **/
        return new Integer(i + 1);
        /** ensure [valid_result] Result != null;         **/
    }



    /**
     * Returns the zero based position for the UI presentable name.  The default implementation 
     * is ((Integer)name).intValue() - 1.  positionNameForPosition must be the inverse of this function.
     * These two methods can be overridden to control the UI display of ordering.
     * 
     * @param name the user presentable name to return a zero based position for
     * @return a UI presentable name for position i
     */
    public int positionForPositionName(Object name)
    {
        /** require [valid_name] name != null;         **/
        return ((Integer) name).intValue() - 1;
        /** ensure [valid_position] (Result >= 0) && (Result < ordered().count());  **/
    }



    /**
     * Returns an Object with a UI presentable name indicating the postion that this object is in in 
     * the ordered() list. The returned value is from positionNameForPosition.
     *
     * @param anObject
     * @return an Integer indicating the postion that this object is in in the ordered() list
     */
    public Object positionNameForObject(Object anObject)
    {
        /** require
                 [valid_object] anObject != null;
                 [object_in_list] ordered().containsObject(anObject);    **/

        return positionNameForPosition(positionOfObject(anObject));

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the zero based position of anObject in the ordered list.
     * 
     * @param anObject the object to return the position for
     * @return the zero based position of anObject in the ordered list
     */
    public int positionOfObject(Object anObject)
    {
        /** require
                 [valid_object] anObject != null;
                 [object_in_list] ordered().containsObject(anObject);    **/

        return ordered().indexOfObject(anObject);

        /** ensure [valid_position] (Result >= 0) && (Result < ordered().count());  **/
    }



    /**
     * The default implementation always returns <code>true</code>.  Subclasses can override this if 
     * some objects can't be moved to some positions.
     * 
     * @param anObject the object from ordered() to tbe checked
     * @param index the index to check, 0 <= index < ordered().count()
     * @return <code>true</code> if this object can be moved to this index.
     */
    public boolean canMoveObjectToPosition(Object anObject, int index)
    {
        /** require [valid_object] anObject != null;  
                    [valid_index] (index >= 0) && (index < ordered().count());  **/
        return true;
    }



    /**
     * The default implementation just moves the object in the order() array.  Subclasses may choose to 
     * implement a different ordering algorithm (such as making the objects relocate themselves). Be aware
     * that this is not a UI component.  This change happens immediately and may cause undesired side
     * effects if called, for example, during takeValuesFromRequest.  
     * 
     * @param anObject the object from ordered() being moved
     * @param index the index to move it to, 0 <= index < ordered().count()
     */
    public void moveObjectToPosition(Object anObject, int index)
    {
        /** require [valid_object] anObject != null;  
                    [valid_index] (index >= 0) && (index < ordered().count());  
                    [can_move] canMoveObjectToPosition(anObject, index);       **/

        // The object gets removed from the list, then inserted again.  Despite this, we do not need 
        //to adjust the target index to accomodate the shorter list.
        objects.removeObject(anObject);
        objects.insertObjectAtIndex(anObject, index);
        /** ensure [object_moved] ordered().objectAtIndex(index).equals(anObject);
                   [same_number_of_objects] true;                                      **/
    }



    /**
     * Returns the list of objects being managed and orderd by this object in the current order.
     *  
     * @return the list of objects being managed and orderd by this object in the current order
     */
    public NSArray ordered()
    {
        return objects;
        /** ensure [valid_result]  Result != null;  **/
    }


    /**
     * Sets the ordered list.
     *  
     * @param newOrdered the new ordered list
     */
    public void setOrdered(NSArray newOrdered)
    {
        /** require newOrdered != null; **/
        objects = newOrdered.mutableClone();
    }


    /** invariant [has_objects] objects != null;  **/
}
