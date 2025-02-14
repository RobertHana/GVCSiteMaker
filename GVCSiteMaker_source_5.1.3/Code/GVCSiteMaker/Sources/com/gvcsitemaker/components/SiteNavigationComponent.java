//Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
//This software is published under the terms of the Educational Community License (ECL) version 1.0,
//a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.components;

import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.components.PageScaffold;
import com.gvcsitemaker.core.support.SectionNodeHierarchy;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * SiteNavigationComponent is a WOComponent that generates HTML that will help navigate a website through its sections.  It will return an ordered list of all its navigable sections in a hierarchical order.
 * The HTML produced by this component contains id's and CSS classes so it can be easily manipulated.  It currently supports three navigation types, vertical, horizontal and breadcrumb.
 * To use this, just bind the navigationType to any one of these: "verticalNavBar",  "horizontalNavBar" and "breadCrumb".    
 */
public class SiteNavigationComponent extends WOComponent 
{
    //The different navigation types
    public static final String verticalNavBarType = "verticalNavBar";
    public static final String horizontalNavBarType = "horizontalNavBar";
    public static final String breadcrumbNavBarType = "breadCrumb";
    
    protected SectionNodeHierarchy sectionNodeHierarchy;  //returns the actually sections in hierarchy

    
    /**
     * Designated constructor.  
     */    
    public SiteNavigationComponent(WOContext context) 
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
     * Returns the navigationType for this component
     *
     * @return the navigationType for this component
     */    
    public String navigationType()
    {
        return (String) valueForBinding("navigationType");
        
        /** ensure 
         [result_not_null] Result != null;
         **/     
    }
    
    

    /**
     * Returns true if this component's type is breadcrumb, false otherwise
     *
     * @return true if this componenet's type is breadcrumb, false otherwise
     */    
    public boolean isBreadcrumbType()
    {
        return navigationType().equals(breadcrumbNavBarType);
    }

    
    
    /**
     * Returns true if this component's type is horizontalNavBar, false otherwise
     *
     * @return true if this component's type is horizontalNavBar, false otherwise
     */    
    public boolean isHorizontalNavType()
    {
        return navigationType().equals(horizontalNavBarType);
    }

    

    /**
     * Returns true if this component's type is verticalNavBar, false otherwise
     *
     * @return true if this component's type is verticalNavBar, false otherwise
     */    
    public boolean isVerticalNavType()
    {
        return navigationType().equals(verticalNavBarType);
    }
    
    
    
    /**
     * Returns the CSS id to be set for this component's <div> container
     *
     * @return the CSS id to be set for this component's <div> container
     */    
    public String containerId()
    {
        String containerId = null;
        
        if (isBreadcrumbType())
        {
            containerId = "navLinkListBreadcrumbContainer";
        }
        else if (isVerticalNavType())
        {
            containerId = "navLinkListVerticalContainer";
        }
        else
        {
            containerId = "navLinkListHorizontalContainer";
        }
        
        return containerId;

        /** ensure [result_not_null] Result != null; **/          
    }
    
    
    
    /**
     * Returns the id to be set for this component's first <ul> tag 
     *
     * @return the id to be set for this component's first <ul> tag
     */    
    public String listId()
    {
        String listId = null;
        
        if (isBreadcrumbType())
        {
            listId = "navLinkListBreadcrumb";
        }
        else if (isVerticalNavType())
        {
            listId = "navLinkListVertical";
        }
        else
        {
            listId = "navLinkListHorizontal";
        }
        
        return listId;

        /** ensure [result_not_null] Result != null; **/      
    }
    
    
    
    /**
     * Returns the sectionNodeHierarchy that returns the website's sections in a hierarchical manner.  This is cached for performance purposes.
     *
     * @return the sectionNodeHierarchy that returns the website's sections in a hierarchical manner.
     */     
    public SectionNodeHierarchy sectionNodeHierarchy()
    {
        if (sectionNodeHierarchy == null)
        {
            sectionNodeHierarchy = new SectionNodeHierarchy(website(), currentSection());
        }
        
        return sectionNodeHierarchy;
        
        /** ensure [result_not_null] Result != null; **/           
    }
    
    
    
    /* Generic setters and getters ***************************************/

    public PageScaffold pageScaffold() {
        return (PageScaffold) parent();
    }
    
    public Website website() {
        return pageScaffold().website();
        
        /** ensure [result_not_null] Result != null; **/   
    }

    public Section currentSection() {
        return pageScaffold().sectionToDisplay();
        
        /** ensure [result_not_null] Result != null; **/           
    }
}