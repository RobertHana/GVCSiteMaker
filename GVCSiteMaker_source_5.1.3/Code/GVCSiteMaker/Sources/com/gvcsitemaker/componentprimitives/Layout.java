// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.pagecomponent.PageComponent;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;


/**
 * Layout provides the base functionality of a list of ordered components for Layout sub-classes to use.
 */
public class Layout extends ComponentPrimitive 
{

    // For WORepetition
    protected NSArray orderedComponents;
    public PageComponent aComponent;



    /**
     * Designated constructor.
     */
    public Layout(WOContext aContext)
    {
        super(aContext);
    }

    

    /**
     * Returns the components controlled by this Layout in the order in which they should be displayed.
     *
     * @return the components controlled by this Layout in the order in which they should be displayed.
     */
    public NSArray orderedComponents()
    {
        if (orderedComponents == null)
        {
            com.gvcsitemaker.core.pagecomponent.Layout layout = (com.gvcsitemaker.core.pagecomponent.Layout) componentObject();

            // This is an NSArray of the children of this layout in the order they are to be displayed in.
            orderedComponents = layout.orderedComponents();
            DebugOut.println(30,"Entering");
            DebugOut.println(30,"Got " + componentObject().toChildren().count() + " Children");
            //DebugOut.println(60,"Got objects: " + componentObject().toChildren());
        }
        
        return orderedComponents;
        /** ensure [valid_result] Result != null; [has_all_children] Result.count() == componentObject().toChildren().count();  **/
    }

}
