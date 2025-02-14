// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.mixedmedia;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;


/**
 * Component to handle editing a PercentOrPixelsParameter
 */
public class PercentOrPixelsParameter extends ContentParameter
{
	public String aUnit;
	protected NSArray units;
			
    /**
     * Designated constructor.
     */
    public PercentOrPixelsParameter(WOContext context)
    {
        super(context);
        
        units = new NSArray(new Object[]{"Percent", "Pixels"});
    }
    
    
    
    public NSArray units()
    {
    		return units;
    		
    		/** ensure [result_not_null] Result != null; **/    		
    }
    
    
    
    public String unit()
    {
    		return (String) pane().valueForBindingKey(parameter().name() + "Unit");    		
    }
    
    
    
    public void setUnit(String aValue)
    {
    		pane().setBindingForKey(aValue == null ? aValue : aValue.toString(), parameter().name() + "Unit");
    }        
}
