// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.webobjects.foundation.*;


/**
 * A class that represens a website's sections in hierarchical manner.  The hierarchy is setup in the contructor with the passed in website and selectedSection, it will iterate through the website().orderedSections() and setup the hierarchy of SectionNodes, where the top level nodes are those sections whose indentations are 0.  If a section's indentation is 0 and the next section in the list has indentation 1, it will be added as one of its sectionNodes(), and so on so forth.  
 */
public class SectionNodeHierarchy extends Object
{
    public static final int NOT_NESTED = 0;
    protected static final boolean INCLUDE_HOME_SECTION = true;
    protected static final boolean EXCLUDE_HOME_SECTION = false;
    
    protected NSMutableArray sectionNodes;
    protected NSMutableArray lastTopLevelNodes; //used in creating the hierarchy to easily know which is the parentNode of a new Section
    
    protected Section selectedSection;

    
    /**
     * Designated constructor.  Creates a hierarchy of sections from the Home section of rootWebsite 
     * down to theSelectedSection.  theSelectedSection may be in any Website reachable by navigating 
     * through the sections and sub-sections of rootWebsite.  Unpublished Websites are 
     * not entered.
     *
     * @param rootWebsite - the Website to root the hierarchy at.  The Home section of this is the root of the hirearchy
     * @param theSelectedSection- the Section to navigate to in the hierarchy of Sections.
     */
    public SectionNodeHierarchy(Website rootWebsite, Section theSelectedSection)
    {
        super();

        /** require
        [valid_website] rootWebsite != null;
        [valid_section] theSelectedSection != null;
        [ec_matches] rootWebsite.editingContext().equals(theSelectedSection.editingContext());
        /# [can_reach_destination]  must be able to reach selected section from root #/
         **/
        sectionNodes = new NSMutableArray();
        lastTopLevelNodes = new NSMutableArray();
        selectedSection = theSelectedSection;

        createHierarchy(rootWebsite, NOT_NESTED, true);
    }
    
    
    
    /**
     * Adds Sections from aWebsite to the hierarchy, nesting them at depth. 
     *
     * @param aWebsite the Website to start building the hierarchy at
     * @param depth the depth to nest the section in aWebsite at
     * @param includeHomeSection <code>true</code> if the Home section of aWebsite should be added to 
     * hierarchy, <code>false</code> for embedded websites. 
     */    
    protected void createHierarchy(Website aWebsite, int depth, boolean includeHomeSection)
    {
        /** require
        [valid_website] aWebsite != null;
        [valid_depth] depth >= NOT_NESTED;
         **/
        NSArray orderedSections = includeHomeSection ? aWebsite.orderedSections() : aWebsite.nonHomeSections();
        
        Enumeration sectionEnum = orderedSections.objectEnumerator();
        while(sectionEnum.hasMoreElements())
        {
            Section aSection = (Section) sectionEnum.nextElement();
            SectionNode newSectionNode = new SectionNode(aSection);
            
            int sectionDepth = aSection.indentation().intValue() + depth;
            
            if (sectionDepth == 0)
            {
                addSectionNode(newSectionNode);
            }
            else 
            {
                SectionNode parentNode = (SectionNode) lastTopLevelNodes.objectAtIndex(sectionDepth - 1);
                
                parentNode.addSectionNode(newSectionNode);
                newSectionNode.setParentNode(parentNode);
            }

            if (aSection.equals(selectedSection()))
            {
               newSectionNode.setIsSelected(true);
            }            

            lastTopLevelNodes.insertObjectAtIndex(newSectionNode, sectionDepth);
           
            // Add embedded sites.  Skip unpublished sites and Sections that do not embed the entire site.
            if (aSection.hasLinkedSite() && aSection.linkedSite().canBeDisplayed() &&
                    ( ! (aSection.type() instanceof EmbeddedSectionSectionType)))
            {
                createHierarchy(aSection.linkedSite(), sectionDepth + 1, EXCLUDE_HOME_SECTION);
            }                  
        }
        
        /** ensure [all_sections_added]  /# sectionsInHierarchy contains all the elements in aWebsite.nonHomeSections() #/ true;  **/        
    }

   
   
    /**
     * Adds sectionNode to the sectionNodes list. 
     *
     * @param sectionNode the SectionNode to add
     */
    public void addSectionNode(SectionNode sectionNode)
    {
        /** require [sectionNode_not_null] sectionNode != null; **/    
        
        sectionNodes.addObject(sectionNode);
        
        /** ensure [node_is_added] sectionNodes.containsObject(sectionNode); **/    
    }

    
    
    
    public NSMutableArray sectionNodes()
    {
        return sectionNodes;
        
        /** ensure [result_not_null] Result != null; **/            
    }

    
    
    public Section selectedSection()
    {
        return selectedSection;
        
        /** ensure [result_not_null] Result != null; **/    
    }
}
