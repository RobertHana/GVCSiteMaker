// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import com.gvcsitemaker.core.utility.HtmlTemplateUtils;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;

/**
 * Holds common code to subclass implementation, has two display modes, one for direct display and one for use with IFRAME 
 */
public class MixedMediaPane extends ComponentPrimitive 
{

	protected com.gvcsitemaker.core.pagecomponent.MixedMediaPane displayPane;

	
    /**
     * Designated constructor.
     */	
    public MixedMediaPane(WOContext context) 
    {
        super(context);
    }
    
    
    
    /**
     * Returns the MixedMediaPane displayed in this component
     * 
     * @return the MixedMediaPane displayed in this component
     */	
    public com.gvcsitemaker.core.pagecomponent.MixedMediaPane displayPane()
    {
    		return displayPane;
    		
         /** ensure [valid_result] Result != null; **/    		
    }

    
    
    
    /**
     * Set the pane to display with aPane
     */	    
    public void setDisplayPane(com.gvcsitemaker.core.pagecomponent.MixedMediaPane aPane)
    {
        /** require [aPane_is_not_null] aPane != null; **/    	  
    	
    		displayPane = aPane;
    }
   
    
    
    /**
     * Returns the template for displaying this MixedMediaPage generated based on its type.
     * 
     * @return the template for displaying this MixedMediaPage generated based on its type.
     */	   
    public WOElement template()
    {
    		WOElement template = null;
    		
    		if (displayPane().isMixedMediaConfigurableContentPane())
    		{
    			com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane configurationContentPane = (com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane) displayPane();
				template = HtmlTemplateUtils.elementsFromTemplateWithBindings(configurationContentPane.contentConfiguration().template(),
						configurationContentPane.generatedBindings(context().request()),
						context().session().languages());
                

    		}
    		else if (displayPane().isMixedMediaTextContentPane())
    		{
    			com.gvcsitemaker.core.pagecomponent.MixedMediaTextContentPane textContentPane = (com.gvcsitemaker.core.pagecomponent.MixedMediaTextContentPane) displayPane();
    			template = HtmlTemplateUtils.elementsFromTemplateWithBindings(textContentPane.template(),
    						textContentPane.generatedBindings(),
						context().session().languages());    			
    		}

        return template;

        /** ensure [valid_result] Result != null; **/
    } 
    
    
    
    /**
     * Returns the contentURL of this pane based on request. Used for generating bindings for template.
     * 
     * @return the contentURL of this pane based on request
     */    
    public String contentURL()
    {
        return (displayPane().contentURL(context().request()) == null) ? "" : displayPane().contentURL(context().request());   
    }    
   
}