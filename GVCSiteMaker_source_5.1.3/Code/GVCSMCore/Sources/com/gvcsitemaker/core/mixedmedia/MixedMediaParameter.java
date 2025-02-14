// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.mixedmedia;

import com.webobjects.foundation.*;

/**
 * Handles parameters specified in MixedMediaContentConfiguration  
 */
public class MixedMediaParameter extends MixedMediaContentConfiguration
{
	//Parameter types
    public static final String StringType = "string";
    public static final String IntegerType = "integer";    
    public static final String BooleanType = "boolean";    
    public static final String PercentOrPixelsType = "percentOrPixels";    
    public static final String ValueListType = "valueList";    
    public static final NSArray validTypes = new NSArray(new String[] {StringType, IntegerType, BooleanType, PercentOrPixelsType, ValueListType});
    
    protected String name;
    protected String uiName;
    protected String type;	
    protected boolean isRequired;	
    protected NSArray values = new NSArray();
	
    public void setName(String aValue) 
    {
        name = aValue;
    }



    public String name() 
    {
        return name;
    }
    
    
	
    public void setUiName(String aValue) 
    {
        uiName = aValue;
    }



    public String uiName() 
    {
        return uiName;
    }    
    
    

	
    /**
     * Sets the type of this paramter
     * @param aValue the type for this parameter to take on
     * @throws IllegalArgumentException if aValue is non-null and not in validTypes
     */
    public void setType(String aValue) 
    {
        /* This is not done as Design by Contract as type originates from XML files and is therefore
         * user input which needs permanent checks.
         */
        if ((aValue != null) && (! validTypes.containsObject(aValue)))
        {
            throw new IllegalArgumentException("Parameter type " + aValue + " is not known, must be one of " + validTypes);
        }
        type = aValue;
    }



    public String type() 
    {
        return type;
    }    
    
    
	
    public void setIsRequired(boolean aValue) 
    {
        isRequired = aValue;
    }



    public boolean isRequired() 
    {
        return isRequired;
    }     
    
    
    
    public void setValues(NSArray aValue) 
    {
        values = aValue;
    }



    public NSArray values() 
    {
        return values;
    }    
    
    /******** Work-around to avoid formatter exceptions when decoding String to boolean *********/
    public boolean validateIsRequired(String isRequiredStr) throws NSValidation.ValidationException {
        return (new Boolean(isRequiredStr)).booleanValue();
    }     
}
