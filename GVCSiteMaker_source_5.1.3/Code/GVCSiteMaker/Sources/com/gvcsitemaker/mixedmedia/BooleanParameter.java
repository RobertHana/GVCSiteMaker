// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.mixedmedia;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Component to handle editing a BooleanParameter
 */
public class BooleanParameter extends ContentParameter
{
	public String aValue;
	
	public NSArray options = new NSArray(new Object[]{"True", "False"});
	
    
    
    /**
     * Designated constructor.
     */
    public BooleanParameter(WOContext context)
    {
        super(context);
    }
    
}
