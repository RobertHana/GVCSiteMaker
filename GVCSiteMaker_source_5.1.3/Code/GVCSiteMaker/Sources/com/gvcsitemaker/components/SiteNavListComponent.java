//Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
//This software is published under the terms of the Educational Community License (ECL) version 1.0,
//a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.components;

import java.util.Enumeration;

import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.support.SMActionURLFactory;
import com.gvcsitemaker.core.support.SectionNode;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;


/**
 * SiteNavListComponent is a WOComponent that generates HTML for a list of SectionNodes.  Used by SiteNavigationComponent.
 */
public class SiteNavListComponent extends WOComponent 
{
    public SectionNode aSectionNode;  //used by WORepetition 


    /**
     * Designated constructor.  
     */    
    public SiteNavListComponent(WOContext context) 
    {
        super(context);
    }



    /**
     * Overriden to make this a non-synchronizing component.
     *
     * @return false
     */    
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Returns the list of SectionNodes to be setup as an HTML unordered list
     *
     * @return the list of SectionNodes to be setup as an HTML unordered list
     */     
    public NSArray sectionNodeList()
    {
        return (NSArray) valueForBinding("sectionNodeList");

        /** ensure [result_not_null] Result != null; **/     
    }



    /**
     * Returns the id to be used for the <ul> tag.  <ul> will not have id property if this is null.  
     *
     * @return the id to be used for the <ul> tag
     */     
    public String listId()
    {
        return (String) valueForBinding("listId");
    }



    /**
     * Returns the website this component is displayed for  
     *
     * @return the website this component is displayed for
     */      
    public Website website() 
    {
        return (Website) valueForBinding("website");

        /** ensure [result_not_null] Result != null; **/     
    }



    /**
     * Returns that parentNode of the list of SectionNodes in this component.  This is null for the top-level nodes.  
     *
     * @return that parentNode of the list of SectionNodes in this component
     */      
    public SectionNode parentNode() 
    {
        return (SectionNode) valueForBinding("parentNode");
    }



    /**
     * Returns true if this component has SectionNodes to display  
     *
     * @return true if this component has SectionNodes to display
     */      
    public boolean hasNodesToDisplay()
    {
        boolean hasNodesToDisplay = false;

        Enumeration nodesEnum = sectionNodeList().objectEnumerator();
        while (nodesEnum.hasMoreElements() && (hasNodesToDisplay == false))
        {
            SectionNode aNode = (SectionNode) nodesEnum.nextElement();
            hasNodesToDisplay = aNode.isNavigable();
        }

        return hasNodesToDisplay;
    }



    /**
     * Returns true if this component should include an id to its <ul> tag  
     *
     * @return true if this component should include an id to its <ul> tag
     */      
    public boolean shouldIncludeId()
    {
        return (listId() != null);
    }



    /**
     * Returns URL of to show either a section of a site, or a section of a site embedded within another site.  See SMActionURLFactory.sectionURL(Website, Section, WORequest) for more details.
     *
     * @return URL of to show either a section of a site, or a section of a site embedded within another site.
     */
    public String urlForSection()
    {
        /** require
        [website_not_null] website() != null;
        [a_section_not_null] aSectionNode.section() != null;
        [context_request_not_null] context().request() != null;  **/

        return SMActionURLFactory.sectionURL(website(), aSectionNode.section(), context().request());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * The CSS class to indicate if link to return has submenu
     *
     * @return CSS class to indicate if link to return has submenu
     */
    public String listPathClass()
    {
        String listPathClass = "listNotOnSelectedPath";
        
        if ((parentNode() == null) || ((parentNode() != null) && parentNode().isOnSelectedPath() && ( ! parentNode().isSelected())))
        {
            listPathClass = "listOnSelectedPath";
        }

        return listPathClass;
    }



    /**
     * The CSS class for the <li> tag
     *
     * @return CSS class for the <li> tag
     */
    public String pathClass()
    {
        String pathClass = "linkNotOnSelectedPath";

        if ((aSectionNode != null) && aSectionNode.isOnSelectedPath())
        {
            pathClass = "linkOnSelectedPath";   
        }

        return pathClass;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * The CSS class for the <span> class around the link
     *
     * @return CSS class for the <span> class around the link
     */    
    public String linkClass()
    {
        String linkClass = "notSelectedLink";

        if (aSectionNode.isSelected())
        {
            linkClass = "selectedLink";
        }

        return linkClass;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * The CSS class for the <span> class around the link to indicate if link has submenu
     *
     * @return CSS class for the <span> class around the link to indicate if link has submenu
     */    
    public String subMenuClass()
    {
        String subMenuClass = "withoutSubmenu";

        if (aSectionNode.hasSectionNodes())
        {
            subMenuClass = "withSubmenu";
        }

        return subMenuClass;

        /** ensure [result_not_null] Result != null; **/
    }



    /*public boolean isExternalURL()
    {
    	return section().type() instanceof ExternalURLSectionType;
    }*/



}
