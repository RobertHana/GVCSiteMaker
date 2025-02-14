// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

/**
 * Superclass for generating link list scripts.  Inherit from this component to generate specific link scripts.
 */
public class URLLinkScript extends WOComponent 
{
    protected NSArray linkList;
    protected String linkListName;
    
    
    /**
     * Designated constructor
     */     
    public URLLinkScript(WOContext context) 
    {
        super(context);
        linkList = new NSArray();
    }

    
    
    /**
     * Returns the generated content of this component.
     * 
     * @return the generated content of this component.
     */    
    public String generatedScript()
    {
        return generateResponse().contentString();
    }
    
    
    
    /**
     * Returns the list of RTEListSelectables
     * 
     * @return the list of RTEListSelectables
     */      
    public NSArray linkList()
    {
        return linkList;
    }
    
    
    
    public void setLinkList(NSArray newValue)
    {
        linkList = newValue;
    }
    
    

    /**
     * Returns variable name of the script
     * 
     * @return variable name of the script
     */      
    public String linkListName()
    {
        return linkListName;
    }
    
    
    
    public void setLinkListName(String newValue)
    {
        linkListName = newValue;
    }    
}
