// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;


/**
 * A totaly empty and blank WOComponent.  This can be mapped to another component to remove it from a page.  For example, this can be mapped to SMUserInstructions so that the internal user specific instructions do not appear.
 */
public class EmptyComponent extends WOComponent 
{

    public EmptyComponent(WOContext context) 
    {
        super(context);
    }


}
