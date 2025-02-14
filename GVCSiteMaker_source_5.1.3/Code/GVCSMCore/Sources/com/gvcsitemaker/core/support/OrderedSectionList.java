package com.gvcsitemaker.core.support;

import net.global_village.foundation.*;
import net.global_village.woextensions.orderablelists.*;

import com.gvcsitemaker.core.*;
import com.webobjects.foundation.*;

/**
 * Section specific type of OrderableList.  The UI ordering is Home, 2, 3, etc.
 * 
 * @see com.webobjects.foundation.OrderableList
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class OrderedSectionList extends OrderableList 
{
    
    public static final String HOME = "Home";
    
    
    /**
     * @param sourceArray
     */
    public OrderedSectionList(NSArray sourceArray) 
    {
        super(sourceArray);
    }

    
    
    /**
     * Excludes the first position for sections that can't be the home section.
     * 
     * @param anObject the object from ordered() to tbe checked
     * @param index the index to check, 0 <= index < ordered().count()
     * @return <code>true</code> if this object can be moved to this index.
     */
    public boolean canMoveObjectToPosition(Object anObject, int index)
    {
        /** require [valid_object] anObject != null;  
                    [valid_index] (index >= 0) && (index < ordered().count());  **/
        return (index != 0) || (((Section)anObject).canBeHomeSection());
    }



    /**
     * Moves the object in the order() array and also updates the Section ordering.
     * 
     * @param anObject the object from ordered() being moved
     * @param index the index to move it to, 0 <= index < ordered().count()
     */
    public void moveObjectToPosition(Object anObject, int index)
    {
        /** require
        [valid_object] anObject != null;  
        [valid_index] (index >= 0) && (index < ordered().count());  
        [can_move] canMoveObjectToPosition(anObject, index); **/

        Section section = (Section)anObject;
        int sectionIndentation = section.indentation().intValue();

        int length = 0;
        Section nextSection = section;
        do
        {
            length++;
            if (nextSection.website().isLastSection(nextSection))
            {
                break;
            }
            nextSection = section.website().nextSection(nextSection);
        } while (nextSection.indentation().intValue() > sectionIndentation);

        NSRange rangeToMove = new NSRange(objects.indexOf(anObject), length);
        // Only move if the move actually changes things
        if ( ! rangeToMove.containsLocation(index))
        {
            if (rangeToMove.location() + rangeToMove.length() <= index)
            {
                index += 1;
            }
            if (index > objects.count())
            {
                index = objects.count();
            }

            NSArrayAdditions.moveObjectsInRangeToIndex(objects, rangeToMove, index);
            section.website().moveSectionToPosition(section, index);
        }

        /** ensure
        [object_moved] true;
        [same_number_of_objects] true; **/
    }



    /**
     * Returns a UI presentable name for position.  Home for the Home section, 2..N for the other
     * sections.
     * 
     * @param i the zero based position to return a postion name for
     * @return a UI presentable name for position i
     */
    protected Object positionNameForPosition(int i)
    {
        /** require [valid_position] (i >= 0) && (i < ordered().count());  **/        
        return (i == 0) ? (Object) HOME : new Integer(i + 1);
        /** ensure [valid_result] Result != null;         **/
    }
    

    
    /**
     * Returns the zero based position for the UI presentable name.
     * 
     * @param name the user presentable name to return a zero based position for
     * @return a UI presentable name for position i
     */
    public int positionForPositionName(Object name)
    {
        /** require [valid_name] name != null;         **/
        if (name.equals(HOME))
        {
            return 0;
        }
        else
        {
            return ((Integer)name).intValue()  - 1;
        }
        /** ensure [valid_position] (Result >= 0) && (Result < ordered().count());  **/        
    }
    
    

}
