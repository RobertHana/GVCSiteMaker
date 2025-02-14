// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.appserver;

import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.components.PageScaffold;
import com.gvcsitemaker.core.utility.DebugOut;
import com.gvcsitemaker.custom.appserver.SMCustomSession;
import com.gvcsitemaker.pages.DisplayFile;

import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.foundation.NSMutableSet;


public class Session extends SMCustomSession 
{
    /** Contains websites that the user has configured.  If a website does <em>not</em>
     * appear in this set and a user enters the ConfigPage for that website, then the
     * website has it's lastModifiedDate updated. */
    protected NSMutableSet configuredWebsites = new NSMutableSet();


    /**
     * Overidden to allow some pages to be exempted from the page cache.  See shouldCachePage(WOComponent) for more details.
     *
     * @param aPage the WOComponent to save in the page cache
     */
    public void savePage(WOComponent aPage)
    {
        if (shouldCachePage(aPage))
        {
            DebugOut.println(30, "Saving page " + aPage.name());
            super.savePage(aPage);
        }
        else
        {
            DebugOut.println(30, "NOT saving page " + aPage.name());
        }
    }




    /**
     * Returns <code>true</code> if the passed WOComponent is eligible to be added to the page cache.  This is used to keep pages that do not need to be cached from the page cache.  For example, pages that are generated only from Direct Actions and which do not contain Component Action URLs do not need to be cached.
     *
     * @param aPage the WOComponent to check for eligibility
     * @return <code>true</code> if the passed WOComponent is eligible to be added to the page cache
     */
    public boolean shouldCachePage(WOComponent aPage)
    {
        // PageScaffold pages (Section display) don't get cached unless they specifically request it.  Most of these pages are generated from Direct Actions and contain only Direct Action URLs so there is no value in caching them.  Only pages with Component Action URLs need to be cached (for example Data Access sections with forms.
        boolean shouldCachePage = ( ! (aPage instanceof PageScaffold)) || ((PageScaffold)aPage).saveInPageCache();

        // This is generated from a Direct Action and contains no links so it does not need to be cached.
        shouldCachePage = shouldCachePage && ! (aPage instanceof DisplayFile);

        return shouldCachePage;
    }

    

    /**
     * Returns <code>true</code> if the passed Website is in the set of configured websites.
     *
     * @param website the website to test
     * @return <code>true</code> if the passed Website is in the set of configured websites
     */
    public boolean hasConfiguredWebsite(Website website)
    {
        return configuredWebsites.containsObject(EOUtilities.localInstanceOfObject(defaultEditingContext(), website));
    }


    /**
     * Adds the passed website to the set of configured websites.
     *
     * @param website the website to add
     */
    public void addToConfiguredWebsites(Website website)
    {
        configuredWebsites.addObject(website);
    }



}
