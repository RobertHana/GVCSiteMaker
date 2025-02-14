// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;


/**
 * This page is a simple list of links to all the websites which are published.  It is intended to be 'fed' to a search engine robot so that the GVC.SiteMaker sites will get indexed.
 */
public class ListOfAllWebsitesPage extends WOComponent
{

    /** @TypeInfo Website */
    protected NSArray activeWebsites;
    public Website aWebsite;


    public ListOfAllWebsitesPage(WOContext aContext)
    {
        super(aContext);
        activeWebsites = Website.publishedWebsites(session().defaultEditingContext());
    }

    

    /** @TypeInfo Website */
    public NSArray activeWebsites() {
        return activeWebsites;
    }


    
    public String pageTitle() {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": List of Published Sites";
    }
}
