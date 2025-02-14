// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.components.SMAuthComponent;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSMutableArray;


/**
 * Page that displays a list of sites the user has permission to configure. User selects one from the list and moves on to the ConfigPage. This page is not used when the user has exactly one site to configure. In that case, the user is taken immediately to the config page.
 */
public class MultCSites extends SMAuthComponent
{
    protected Website aConfigurableWebsite;
    public NSMutableArray configurableWebsites;


    public MultCSites(WOContext aContext)
    {
        super(aContext);
    }


    
    /**
     * Method to prepare and return the <code>ConfigPage</code> configured for aConfigurableWebsite().
     *
     * @return the <code>ConfigPage</code> for the selected website is returned
     */
    public ConfigTabSet goConfig()
    {
        ConfigTabSet nextPage = (ConfigTabSet)pageWithName("ConfigTabSet");
        nextPage.setWebsite(aConfigurableWebsite());
        ((Session)session()).setEditingWebsite(aConfigurableWebsite());
        return nextPage;
    }



    /**
     * Handles user login and the special case where the user may configure exactly one site in which case the ConfigPage for that site is returned directly to save the from having to select that one site.
     *
     * @param WOResponse the response to append to
     * @param WOContext the context the response is in
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        DebugOut.println(3,"Entering...");

        // Set the list of websites after the user has logged in, but before rendering the page.
        if (((Session)session()).isUserAuthenticated() &&
            (configurableWebsites == null))
        {
            createConfigurableWebsiteList();
        }

        // super is first here since it does the auth stuff
        super.appendToResponse(response, context);

        if (((Session)session()).isUserAuthenticated())
        {
            // Handle the case where the user can only configure a single website by jumping directly to the ConfigPage.
            if ( ((Session)session()).isUserAuthenticated() &&
                ! ((Session)session()).currentUser().canConfigureMultipleSites() &&
                 (((Session)session()).currentUser().configurableWebsites().count() != 0))
            {
                DebugOut.println(16,"User can configure none, or only a single site");
                DebugOut.println(3,"Going to configpage!...");
                // Set the focused website so that goConfig() works properly
                //setAConfigurableWebsite((Website) ((Session)session()).currentUser().configurableWebsites().objectAtIndex(0));

                setAConfigurableWebsite((Website)configurableWebsites.objectAtIndex(0));
                response.setContent(goConfig().generateResponse().content());
            }
        }
        DebugOut.println(16,"Leaving...");
    }



    /**
     * Creates a sorted list of websites the logged in user has permission to configure. The sort ordering is by the owner's userID, then by bannerText.
     *
     * Possible Refactoring: A sort method such as this,  should be moved into a Domain Object
     *
     * @return void
     */
    public void createConfigurableWebsiteList()
    {
        DebugOut.println(10,"Entering...");
        configurableWebsites = new NSMutableArray(((Session)session()).currentUser().configurableWebsites());

        NSMutableArray sortOrdering = new NSMutableArray();
        sortOrdering.addObject(EOSortOrdering.sortOrderingWithKey("owner.userID",
                                                                  EOSortOrdering.CompareCaseInsensitiveAscending));
        sortOrdering.addObject(EOSortOrdering.sortOrderingWithKey("banner.bannerText",
                                                                  EOSortOrdering.CompareCaseInsensitiveAscending));
        EOSortOrdering.sortArrayUsingKeyOrderArray(configurableWebsites, sortOrdering);
        DebugOut.println(10,"Leaving...");
    }



    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Choose Site to Configure";
    }



    /* Generic setters and getters ***************************************/

    public Website aConfigurableWebsite() {
        return aConfigurableWebsite;
    }
    public void setAConfigurableWebsite(Website value) {
        aConfigurableWebsite = value;
    }

}


