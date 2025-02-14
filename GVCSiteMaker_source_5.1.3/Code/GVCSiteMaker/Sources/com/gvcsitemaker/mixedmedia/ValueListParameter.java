// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.mixedmedia;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Component to handle a ValueListParameter  
 */
public class ValueListParameter extends ContentParameter
{
	public String aValue;
	
    /**
     * Designated constructor.
     */
    public ValueListParameter(WOContext context)
    {
        super(context);
    }
    
    
    
    public NSArray options()
    {
		/** require [parameter_values_not_null] parameter().values() != null; **/
    	
    		return (NSArray) parameter().values().valueForKey("value");    	
    		
    		/** ensure [result_not_null] Result != null; **/    		
    }

}
