// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import java.util.Enumeration;

import com.gvcsitemaker.core.mixedmedia.MixedMediaParameter;
import com.gvcsitemaker.core.utility.HtmlTemplateUtils;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Manage display of different media types, knows how to generate HTML for display of media types based on template and values to sources and/or parameters, knows how to display content in IFRAME (if required)
 */
public class MixedMediaConfigurableContentPane extends MixedMediaPane
{
    // Binding keys
    public static final String TemplateKey = "template"; 
    protected NSDictionary parameterValueDictionary;
    
    /**
     * Designated constructor
     */    
    public MixedMediaConfigurableContentPane(WOContext context)
    {
        super(context);
        parameterValueDictionary = null;
    }

    
    
    /**
     * Convenience method to return the MixedMediaConfigurableContentPane PageComponent that it is displaying.
     */
    public com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane pane()
    {
    		return (com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane) componentObject();
    		
        /** ensure [valid_result] Result != null;  **/
    	}   
    
    
    
    /**
     * Returns the WOComponent template to use for this pane built from templates and parameter values .
     *
     * @return the WOComponent template to use for this pane
     */
    public WOElement template()
    {
    		WOElement template;

            
		if (pane().displayInIFrame() || pane().requiresIFrame())
		{
			template = HtmlTemplateUtils.elementsFromTemplateWithBindings(pane().iFrameTemplate(),
						pane().iFrameBindings(context().request()),
						context().session().languages());
		} 
		else 
		{
			template = HtmlTemplateUtils.elementsFromTemplateWithBindings(pane().contentConfiguration().template(),
						pane().generatedBindings(context().request()),
						context().session().languages());
		}

        return template;

        /** ensure [valid_result] Result != null; **/
    }  
    
    
    
    
    /**
     * Returns sourceURL for download with current request
     *
     * @return sourceURL for download with current request
     */    
    public String sourceURLAsDownload()
    {
        return (pane().sourceURLAsDownload(context().request()) == null) ? "" : pane().sourceURLAsDownload(context().request());   

        /** ensure [valid_result] Result != null; **/        
    }    
        
    
  
    /**
     * Returns a dictionary containing the parameter name as keys and their corresponding values as objects.  Used for generating bindings to display this pane.
     *
     * @return a dictionary containing the parameter name as keys and their corresponding values as objects
     */     
    public NSDictionary parameterValueDictionary()
    {
        if (parameterValueDictionary == null)
        {
            NSMutableDictionary parameterValueMutableDictionary = new NSMutableDictionary();
            Enumeration parameterEnumerator = pane().contentConfiguration().parameters().objectEnumerator();
            while(parameterEnumerator.hasMoreElements())
            {
                 MixedMediaParameter aMixedMediaParameter = (MixedMediaParameter) parameterEnumerator.nextElement();
                 String value = (pane().valueForBindingKey(aMixedMediaParameter.name()) == null ? "" : (String) pane().valueForBindingKey(aMixedMediaParameter.name()));
                 parameterValueMutableDictionary.setObjectForKey(value, aMixedMediaParameter.name()); 
            }

            parameterValueDictionary = parameterValueMutableDictionary.mutableClone();
        }
        
        return parameterValueDictionary;  
        
        /** ensure [valid_result] Result != null; **/        
    }    
    
    
    
    /**
     * Overridden to clear cached values
     */
    public void awake()
    {
        super.awake();
        parameterValueDictionary = null;
    }     
    
}