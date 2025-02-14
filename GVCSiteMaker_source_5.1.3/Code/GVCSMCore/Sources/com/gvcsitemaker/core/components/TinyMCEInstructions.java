// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;

/**
 * The Edit page instructions for the TinyMCE text editor 
 */
public class TinyMCEInstructions extends RichTextComponent 
{

    /**
     * Designated constructor
     */		
    public TinyMCEInstructions(WOContext context) 
    {
        super(context);
    }
    
    
    public boolean isStateless()
    {
        return true;
    }
    
}