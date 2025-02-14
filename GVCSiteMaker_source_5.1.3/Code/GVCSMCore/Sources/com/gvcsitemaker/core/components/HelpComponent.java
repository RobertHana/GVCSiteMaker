// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;


/**
 * Displays a link to page-specific help.  It is here in a component so that
 * it can be replaced on an installation by installation basis if the generic
 * version is not suitable.
 */
public class HelpComponent extends WOComponent
{
    public String pageClass;


    /**
     * Designated constructor
     */
    public HelpComponent(WOContext context)
    {
        super(context);
    }



    /**
     * Designated constructor
     */
    public String helpHref()
    {
        return (String)SMApplication.appProperties().
            dictionaryPropertyForKey("PageClassToHelpURLMap").
                objectForKey(pageClass);
    }



}
