// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.interfaces;

/**
 * Interface for an object which basically has a display name and a URL. An object need to implement this interface to be added as a link when URLLinkScript is used.  
 */
public interface RTEListSelectable
{
    /**
     * Returns the name to display for the link.
     *
     * @return the name to display for the link.
     */
    public String listDisplayName();    
    
    
    
    /**
     * Returns the URL for the link.
     *
     * @return the URL for the link.
     */
    public String listURL();       
}
