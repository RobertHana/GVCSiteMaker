// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.mixedmedia;


/**
 * Handles optional values defined for a MixedMediaParameter 
 */
public class MixedMediaParameterValues extends MixedMediaContentConfiguration
{
	protected String value;
	
    public void setValue(String aValue) 
    {
        value = aValue;
    }



    public String value() 
    {
        return value;
    }
}
