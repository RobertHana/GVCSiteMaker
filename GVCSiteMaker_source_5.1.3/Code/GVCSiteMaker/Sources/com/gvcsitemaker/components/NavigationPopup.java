//Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
//This software is published under the terms of the Educational Community License (ECL) version 1.0,
//a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.components;


import com.gvcsitemaker.core.support.SMActionURLFactory;
import com.gvcsitemaker.core.support.SMSectionHierarchy.SectionDepth;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;


/**
 * A variation of a NavBar.  This can be used to display sections of a Website in a drop-down list.  
 * The sections are indented based on their hierarchies.  This component works both when JavaScript
 * is enabled and when it is disabled.  When JavaScript is disabled, an alternate popup is shown and
 * the use of a submit button is required.  Embeded sites / sections can't be navigated to when 
 * JavaScript is disabled.
 */
public class NavigationPopup extends NavBar 
{
	
    /**
     * Designated constructor
     */	
    public NavigationPopup(WOContext context) 
    {
        super(context);
    }

    
    
    /**
     * The only think that can go in a script tag is, well, scripting.  So rather than this put this input
     * between script tags, we need to output it as a string so that it can be included with a
     * document.write().  The input thus created uses JavaScript so that rather than set a value or
     * submit a form, it sets the browser's location.href directly.
     * 
     * @return HTML seclect input as String
     */
    public String javaScriptNavigationPopUp()
    {
        StringBuffer html = new StringBuffer(512);
        html.append("<select class=\"navigationPopup\" onChange=\"location.href=this.options[this.selectedIndex].value; return false;\" ");
        html.append("name=\"dummy\">");
        
        NSArray navigableSections = sectionHierarchy().navigableSectionsInHierarchy();
        for (int i = 0; i < navigableSections.count(); i++)
        {
            setASection((SectionDepth)navigableSections.objectAtIndex(i));
            if (section().isNavigable().isTrue())
            {
                html.append("<option value=\"");
                html.append(urlForSection());
                html.append("\"");
                
                if (isViewingSection())
                {
                    html.append(" selected style=\"font-weight: bold;\"");
                }
                
                html.append(">");
                
                // Fix problem when section name contains ' which is used as a quote in the JS that wraps this
                String sectionName = paddedSectionName();
                if (sectionName.indexOf("'") > -1)
                {
                    sectionName = sectionName.replaceAll("'", "\\\\'");  // Looks odd, but that is what works
                }
                html.append(sectionName);
                
                html.append("</option>");
            }
        }    
        html.append("</select>");
        
        return html.toString();

        /** ensure [result_not_null] Result != null; **/
    }


    
    /**
     * Indents the Section names and makes the currently viewed Section in bold text.  This doesn't work in some browsers.
     * 
     * @return indented Section names and makes the currently viewed Section in bold text
     */
    public String paddedSectionName()
    {
        String paddedSectionName = getNameSpacers() + section().name();
        
        if (isViewingSection())
        {
            paddedSectionName = getNameSpacers() + "<B>" + section().name() + "</B>";
        }
        
        return paddedSectionName;
        
        /** ensure [result_not_null] Result != null; **/    		
    }
    
    
    
     /**
     * Returns URL of to show either a section of a site, or a section of a site embedded within another site.  
     * 
     * @see SMActionURLFactory.sectionURL(Website, Section, WORequest)
     * @return URL to display section()
     */
    public String urlForSection()
    {
        /** require
        [website_not_null] website() != null;
        [context_request_not_null] context().request() != null;  **/
        
        return SMActionURLFactory.sectionURL(website(), section(), context().request());

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * @return the URL that the form is submitted to when JavaScript is disabled
     */
    public String displaySectionDAUrl()
    {
        return SMActionURLFactory.directActionUrl(context().request(), "displaySection");
    }
    
 	
}