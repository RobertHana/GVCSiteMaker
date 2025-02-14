// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.mixedmedia;


import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;



/**
 * Abstract class to edit a Parameter, knows how to display if its required or not
 */
public class ContentParameter extends WOComponent 
{
	protected String parameterComponent;
	
    /**
     * Designated constructor.
     */
    public ContentParameter(WOContext context)
    {
        super(context);
    }
    
    
    
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }
    
    
    
    /**
     * Convenience method to return parameter being edited.
     *
     * @return the parameter being edited
     */
    public com.gvcsitemaker.core.mixedmedia.MixedMediaParameter parameter()
    {
    		return (com.gvcsitemaker.core.mixedmedia.MixedMediaParameter) valueForBinding("parameter");

    		/** ensure [result_not_null] Result != null; **/
    }    

    
    
    
    /**
     * Convenience method to return pane being edited.
     *
     * @return the pane being edited
     */
    public com.gvcsitemaker.core.pagecomponent.MixedMediaPane pane()
    {
    		return (com.gvcsitemaker.core.pagecomponent.MixedMediaPane) valueForBinding("pane");

    		/** ensure [result_not_null] Result != null; **/
    }    
  


    /**
     * Returns the appropriate parameter component name to displayed based on the parameter type.
     *
     * @return the appropriate parameter component name to displayed based on the parameter.
     */
    public String parameterComponent()
    {
    		if (parameterComponent == null)
    		{
    			if (parameter().type().equals("integer") || parameter().type().equals("string"))
    			{
    				parameterComponent = "StringParameter";
    			}
    			else if (parameter().type().equals("boolean"))
    			{
    				parameterComponent = "BooleanParameter";
    			}
    			else if (parameter().type().equals("percentOrPixels"))
    			{
    				parameterComponent = "PercentOrPixelsParameter";
    			}    	
    			else if (parameter().type().equals("valueList"))
    			{
    				parameterComponent = "ValueListParameter";
    			}     			
    		}
    	

    		return parameterComponent;

    		/** ensure [result_not_null] Result != null; **/
    } 
    
    
    
    public String parameterValue() 
    {
		return (String) pane().valueForBindingKey(parameter().name());
    }
    
    
    
    public void setParameterValue(String aValue)
    {
        	pane().setBindingForKey(aValue == null ? aValue : aValue.toString(), parameter().name());
    }        
}

