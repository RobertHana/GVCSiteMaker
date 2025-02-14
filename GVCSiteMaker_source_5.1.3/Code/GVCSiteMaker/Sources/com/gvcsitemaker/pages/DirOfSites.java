// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.appserver.SMApplication;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * Default implementation of the Directory of SiteMaker Websites page.  Has an SMWebsiteSearch component and a RecentlyVisitedWebsites component.
 */
public class DirOfSites extends WOComponent
{


    public DirOfSites(WOContext aContext)
    {
        super(aContext);
    }


    public String pageTitle()
    {
        return "Search for " + SMApplication.appProperties().propertyForKey("ProductName") + " Websites";
    }
}
