// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;

/**
 * Component that generates link list script for TinyMCE.
 */
public class TinyMCEURLLinkScript extends URLLinkScript 
{
    public RTEListSelectable aLinkList;    
    
    
    /**
     * Designated constructor
     */      
    public TinyMCEURLLinkScript(WOContext context)
    {
        super(context);
    }
    
    
    /**
     * Returns true if aLinkList is the last link.
     * 
     * @return true if aLinkList is the last link.
     */    
    public boolean isLastLink()
    {
        return aLinkList.equals(linkList().lastObject());
    }   
 
}