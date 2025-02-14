// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;


/**
 * Component to display a brief description of a Website.  This is for use in search results and the list of recently visited sites.
 */
public class WebsiteDescriptionComponent extends WOComponent
{


    /**
     * Designated constructor
     */
    public WebsiteDescriptionComponent(WOContext context)
    {
        super(context);
    }



    /**
     * No need for state, this is data display only
     */
    public boolean isStateless()
    {
        return true;
    }


}
