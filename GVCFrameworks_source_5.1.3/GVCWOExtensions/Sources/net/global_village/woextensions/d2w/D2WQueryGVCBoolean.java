package net.global_village.woextensions.d2w;

import com.webobjects.appserver.*;
import com.webobjects.directtoweb.*;
import com.webobjects.foundation.*;


/**
 * This property-level component builds a query based on a net.global_village.foundation.Boolean 
 * attribute.  It displays a WOPopUpButton with "Yes", "No" and "none" as its options.  Selecting 
 * "none" will not create a query.
 *
 * @author Copyright (c) 2003  Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 2$
 */
public class D2WQueryGVCBoolean extends D2WCustomQueryComponent
{
    public NSArray availableBooleanValues;
    public net.global_village.foundation.GVCBoolean aBoolean;
	public String key;	

	
	
    public D2WQueryGVCBoolean(WOContext context)
    {
        super(context);
        //Initialize the list 
        availableBooleanValues = new NSArray(new Object[]{
            new net.global_village.foundation.GVCBoolean(true), 
            new net.global_village.foundation.GVCBoolean(false)});
    }

    
    
    public Object selection() 
    { 
        return displayGroup.queryMatch().valueForKey(key);
    }

    
    
    public void setSelection(Object newSelection) 
    {
    	displayGroup.queryMatch().takeValueForKey(newSelection, key);
    }    
}
