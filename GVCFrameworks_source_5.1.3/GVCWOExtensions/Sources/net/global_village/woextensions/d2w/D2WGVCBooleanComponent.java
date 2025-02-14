package net.global_village.woextensions.d2w;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;

/**
 * D2WCheckBoxComponent is a custom property-level component designed for DirectToWeb that provides a WOCheckBox for a user to set a net.global_village.foundation.Boolean attribute to be either true or false.
 *
 * @author Copyright (c) 2003  Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 2$
 */
public class D2WGVCBooleanComponent extends WOComponent
{
	public EOEnterpriseObject object;
	public String key;

    
	public D2WGVCBooleanComponent(WOContext context)
	{
		super(context);
	}


    
	public boolean isChecked()
	{
		boolean isChecked = false;

		if (object.valueForKey(key) != null)
		{
			isChecked = ((net.global_village.foundation.GVCBoolean) object.valueForKey(key)).booleanValue();
		}
		
		return isChecked;
	}



	public void setIsChecked(boolean newIsChecked)
	{
		object.takeValueForKey(new net.global_village.foundation.GVCBoolean(newIsChecked), key);
	}
}
