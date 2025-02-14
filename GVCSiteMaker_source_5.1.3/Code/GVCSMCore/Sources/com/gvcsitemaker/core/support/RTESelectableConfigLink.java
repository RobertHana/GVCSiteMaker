// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.support;

import java.util.*;

import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.foundation.*;

/**
 * A simple object that implements RTEListSelectable.
 */
public class RTESelectableConfigLink extends Object implements RTEListSelectable
{
    protected String listDisplayName;
    protected String listURL;
    
    /**
     * Returns an array of RTESelectableConfigLinks based on the passed rawLinks.
     * 
     * @param rawLinks an array of dictionaries. Each dictionary should only have one key-value pair to be converted. Key should be the display name and value should be the URL.
     * @return an array of RTESelectableConfigLinks based on the passed rawLinks.
     */ 
    public static NSArray rteSelectableConfigLinks(NSArray rawLinks)
    {
        /** require [rawLinks_not_null] rawLinks != null; **/
        
        NSMutableArray rteSelectableConfigLinks = new NSMutableArray();
        
        Enumeration linksEnumerator = rawLinks.objectEnumerator();
        while(linksEnumerator.hasMoreElements())
        {
            NSDictionary aLinkDictionary = (NSDictionary) linksEnumerator.nextElement();
            if (aLinkDictionary.count() > 0)
            {
                RTESelectableConfigLink newRTESelectableConfigLink = new RTESelectableConfigLink();
                newRTESelectableConfigLink.setListDisplayName((String) aLinkDictionary.allKeys().objectAtIndex(0));
                newRTESelectableConfigLink.setListURL((String) aLinkDictionary.allValues().objectAtIndex(0));
                rteSelectableConfigLinks.addObject(newRTESelectableConfigLink);
            }
        }        
        
        return rteSelectableConfigLinks.mutableClone();
        
        /** ensure [result_not_null] Result != null; **/                
    }

    
    
    public String listDisplayName()
    {
        return listDisplayName;
    }
    

    
    public void setListDisplayName(String newValue)
    {
        listDisplayName = newValue;
    }

    

    public String listURL()
    {
        return listURL;
    }    
    
    
    
    public void setListURL(String newValue)
    {
        listURL = newValue;
    }    

    
}