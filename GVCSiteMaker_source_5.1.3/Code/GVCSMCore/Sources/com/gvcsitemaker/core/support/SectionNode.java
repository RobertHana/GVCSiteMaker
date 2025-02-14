// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import com.gvcsitemaker.core.*;
import com.webobjects.foundation.*;

/**
 * A class that represens a single node in a SecionNodeHierarchy. Each node represents a unique Section of a website.  
 */
public class SectionNode extends Object
{
    protected Section section;
    protected boolean isOnSelectedPath;    
    protected boolean isSelected;    
    protected SectionNode parentNode;    
    
    protected NSMutableArray sectionNodes;
    
    /**
     * Designated constructor.  
     *
     * @param aSection - the Section that this node is for
     */
    public SectionNode(Section aSection)
    {
        super();
        
        /** require
        [valid_section] aSection != null;
        **/        
        section = aSection;

        isOnSelectedPath = false;
        isSelected = false;
        parentNode = null;

        sectionNodes = new NSMutableArray();
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


    
    /**
     * Returns true if this SectionNode has sectionNodes 
     *
     * @param true if this SectionNode has sectionNodes
     */
    public boolean hasSectionNodes()
    {
        return sectionNodes().count() > 0;
    }
    


    /**
     * Returns true if this SectionNode is selected 
     *
     * @param true if this SectionNode is selected
     */    
    public boolean isSelected()
    {
        return isSelected;
    }    
    
    

    /**
     * Sets isSelected with newValue.  If isSelected is true, the selectedPath from the top level to this SectionNode is also set to true.
     */
    public void setIsSelected(boolean newValue)
    {
        isSelected = newValue;
        if (isSelected)
        {
            setIsOnSelectedPath(true);
            SectionNode parent = parentNode();
            
            while (parent != null)
            {
                parent.setIsOnSelectedPath(true);
                parent = parent.parentNode();
            }
        }
    }
    
    
    
    /**
     * Returns true if this SectionNode is navigable or displayable in the hiearchy. A SectionNode is navigable if its property navigable() is true and all its parentNodes are also navigable.
     * 
     * @return true if this SectionNode is navigable or displayable in the hiearchy. A SectionNode is navigable if its property navigable() is true and all its parentNodes are also navigable. 
     */
    public boolean isNavigable()
    {
        boolean isNavigable = section().isNavigable().booleanValue();
       
        //Check if all parents are navigable if not selected
        
        if ((isNavigable) && (! isOnSelectedPath()))
        {
            SectionNode parent = parentNode();
            
            while ( (parent != null) && (isNavigable != false) && (! parent.isOnSelectedPath()) ) 
            {
                isNavigable = parent.section().isNavigable().booleanValue();
                parent = parent.parentNode();
            }  
        }
      
        return isNavigable;  
    }
  

    
    /* Generic setters and getters ***************************************/
    
    
    public Section section()
    {
        return section;
        
        /** ensure [result_not_null] Result != null; **/          
    }
    
    public void setSection(Section aSection)
    {
        section = aSection;
    }
    
    public SectionNode parentNode()
    {
        return parentNode;
    }
    
    public void setParentNode(SectionNode newValue)
    {
        parentNode = newValue;
    }    
    
    public NSMutableArray sectionNodes()
    {
        return sectionNodes;
        
        /** ensure [result_not_null] Result != null; **/          
    }
    
    public boolean isOnSelectedPath()
    {
        return isOnSelectedPath;
    }
    
    public void setIsOnSelectedPath(boolean newValue)
    {
        isOnSelectedPath = newValue;
    }    
    
}
