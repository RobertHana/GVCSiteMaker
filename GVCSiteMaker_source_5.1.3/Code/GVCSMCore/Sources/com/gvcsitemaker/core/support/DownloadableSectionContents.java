// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.support;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;

/*
 *  Returns the component to display the contents of a Section with no adornments.  Useful when displaying a Section contents in an IFRAME.
 */
public class DownloadableSectionContents extends net.global_village.woextensions.WOComponent
{
	protected Section section;
	
    /**
     * Designated constructor.
     */	
	public DownloadableSectionContents(WOContext aContext) 
	{
		super(aContext);

	}
	
	
	
    /**
     * Returns the template for this component based from the evaluated sectionHTML() and sectionBindings()
     * 
     * @return the template for this component based from the evaluated sectionHTML() and sectionBindings()
     */		
	public WOElement template()
	{	

		WOElement template = null;
	    		
		template = HtmlTemplateUtils.elementsFromTemplateWithBindings(sectionHTML(),
																   sectionBindings(),
																   context().session().languages());

		return template;
		
		/** ensure [result_not_null] Result != null; **/
	}     	
	
	
	
    /**
     * Returns the HTML source for this Section
     * 
     * @return the HTML source for this Section
     */	
	public String sectionHTML() 
	{	   
		return "<webobject name=SectionComponent></webobject>";
		
        /** ensure [result_not_null] Result != null; **/		
	}
	    

	
    /**
     * Returns the wod bindings for this component
     * 
     * @return the HTML source for this Section
     */	
	public String sectionBindings() 
	{	   

		StringBuffer wodFile = new StringBuffer(2048);
	        
        wodFile.append("SectionComponent" + ": WOSwitchComponent {\n");
        wodFile.append("    componentObject = section.component;\n");
        wodFile.append("    currentMode = \"display\";\n");	        
        wodFile.append("    WOComponentName = \"" + section().component().componentType() + "\";\n");
        wodFile.append("}\n");
  	
        return wodFile.toString();
        
        /** ensure [result_not_null] Result != null; **/
    }    

	
	
    /**
     * The section to display
     */		
	public Section section()
	{	
		return section;
		
		/** ensure [Result_not_null] Result != null; **/			
	}

	    
	
    /**
     * Sets the Section to display
     */		
	public void setSection(Section aSection)
	{
		/** require [aSection_not_null] aSection != null; **/
		
		section = aSection;
		
		/** ensure [section_not_null] section != null; **/		
	}	
	
}
