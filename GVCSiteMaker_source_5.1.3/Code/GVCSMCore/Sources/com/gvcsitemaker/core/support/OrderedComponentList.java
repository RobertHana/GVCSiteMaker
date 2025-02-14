package com.gvcsitemaker.core.support;

import net.global_village.woextensions.orderablelists.*;

import com.gvcsitemaker.core.pagecomponent.*;
import com.webobjects.foundation.*;

/**
 * Section specific type of OrderableList.  The UI ordering is Home, 2, 3, etc.
 * 
 * @see com.webobjects.foundation.OrderableList
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class OrderedComponentList extends OrderableList 
{

    public OrderedComponentList(NSArray sourceArray) 
    {
        super(sourceArray);
    }
    
    
    
    /**
     * Moves the object in the order() array and also updates the Component ordering.
     * 
     * @param anObject the object from ordered() being moved
     * @param index the index to move it to, 0 <= index < ordered().count()
     */
    public void moveObjectToPosition(Object anObject, int index)
    {
        /** require [valid_object] anObject != null;  
                    [valid_index] (index >= 0) && (index < ordered().count());  
                    [can_move] canMoveObjectToPosition(anObject, index);       **/
        super.moveObjectToPosition(anObject, index);
        
        if (anObject instanceof com.gvcsitemaker.core.pagecomponent.ListLink)
        {
            com.gvcsitemaker.core.pagecomponent.ListLink component = (com.gvcsitemaker.core.pagecomponent.ListLink) anObject;
            ((Layout) component.toParent()).moveComponentToPosition(component, index);
        }
        else
        {
            com.gvcsitemaker.core.pagecomponent.DataAccessColumn component = (com.gvcsitemaker.core.pagecomponent.DataAccessColumn) anObject;
            component.parentMode().moveComponentToPosition(component, index);
        }
        

        /** ensure [object_moved] ordered().objectAtIndex(index).equals(anObject);
                   [same_number_of_objects] true;                                      **/
    }    
}
