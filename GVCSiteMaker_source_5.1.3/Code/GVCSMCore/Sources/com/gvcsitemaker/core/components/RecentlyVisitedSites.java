// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;


/**
 * Component to display the list of Recently Visited Websites.  Uses a SearchResultManager and PageFlipper to control long lists.
 */
public class RecentlyVisitedSites extends WOComponent
{

    /** @TypeInfo Website */
    public SearchResultManager searchResultManager;	// SearchResultManager controlling Recently Visited Websites
    public Website aWebsite;							// Item in WORepetition over Recently Visited Websites


    /**
     * Designated constructor.  Sets up SearchResultManager.
     */
    public RecentlyVisitedSites(WOContext context)
    {
        super(context);

        int defaultHitsPerPage = SMApplication.appProperties().intPropertyForKey("HitsDisplayedPerPageInDirectory");
        searchResultManager = new SearchResultManager(defaultHitsPerPage);
        searchResultManager.setObjectsToManage( ((SMSession)session()).recentlyVisitedWebsites(context()) );
    }


}
