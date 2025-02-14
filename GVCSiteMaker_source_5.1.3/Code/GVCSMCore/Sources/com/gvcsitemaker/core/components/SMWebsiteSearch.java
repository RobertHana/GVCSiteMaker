// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Component implementing a search of websites by wild card text searches of siteID, banner.bannerText, owner.userID, parentOrgUnit.unitName, contactName, and contactEmail with optional filtering of results to only those in a sub-set of the OrgUnit hierarchy.  Uses a SearchResultManager and PageFlipper to control long lists.
 */
public class SMWebsiteSearch extends WOComponent 
{

    // Search Form Inputs
    public String searchString;  			// Public for KVC

    /** @TypeInfo OrgUnit */
    public NSArray allOrgUnits;			// For UnitChooserWithDefault popup of units
    public OrgUnit selectedOrgUnit;		// For UnitChooserWithDefault popup of units
    protected NSArray rootOrgUnits;			// For UnitChooserWithDefault popup of units
    protected boolean shouldExcludeContainedOrgUnits;

    // Search Results
    protected Website currentWebsite;
    protected SearchResultManager searchResultManager;
    protected boolean shouldShowNoResultsMessage;
    protected boolean didPerformSearch;


    public SMWebsiteSearch(WOContext aContext)
    {
        super(aContext);

        // Set the unit at the root of the UnitChooserWithDefault used in the SearchForm
        rootOrgUnits = new NSArray(OrgUnit.rootUnit(session().defaultEditingContext()));

        int defaultHitsPerPage = SMApplication.appProperties().intPropertyForKey("HitsDisplayedPerPageInDirectory");
        searchResultManager = new SearchResultManager(defaultHitsPerPage);

        // Initial state: No search results or error messages until a search has been done
        setDidPerformSearch(false);
        setShouldShowNoResultsMessage(false);
    }



    /**
     * Reset state for next time around.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);

        setShouldShowNoResultsMessage(false);
    }



    /**
     * Performs a search for sites matching criteria and redisplays the page.
     */
    public WOComponent searchAction()
    {
        NSMutableArray allQualifiers = new NSMutableArray();

        // Only return published sites.
        allQualifiers.addObject(Website.canBeDisplayedQualifier());

        if (searchString != null)
        {
            // Searches must be wild card.
            String wildCardString = "*" + searchString.trim() + "*";
            NSMutableArray searchStringQualifiers = new NSMutableArray();

            searchStringQualifiers.addObject(new EOKeyValueQualifier("siteID",
                                                            EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                            wildCardString));

            searchStringQualifiers.addObject(new EOKeyValueQualifier("banner.bannerText",
                                                            EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                            wildCardString));

            searchStringQualifiers.addObject(new EOKeyValueQualifier("owner.userID",
                                                            EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                            wildCardString));

            searchStringQualifiers.addObject(new EOKeyValueQualifier("parentOrgUnit.unitName",
                                                            EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                            wildCardString));

            searchStringQualifiers.addObject(new EOKeyValueQualifier("contactName",
                                                            EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                            wildCardString));

            searchStringQualifiers.addObject(new EOKeyValueQualifier("contactEmail",
                                                            EOQualifier.QualifierOperatorCaseInsensitiveLike,
                                                            wildCardString));

            allQualifiers.addObject(new EOOrQualifier(searchStringQualifiers));
        }

        if (selectedOrgUnit != null)
        {
            if ( ! shouldExcludeContainedOrgUnits())
            {
                // Create a qualifier ORing this org unit and all org units below it which are in the system wide list of org units.
                NSArray orgUnitTree = selectedOrgUnit.orderedSystemWideOrgUnitsInHierarchy();
                Enumeration orgUnitEnumeration = orgUnitTree.objectEnumerator();
                NSMutableArray orgUnitQualifiers = new NSMutableArray();

                while (orgUnitEnumeration.hasMoreElements())
                {
                    OrgUnit thisOrgUnit = (OrgUnit) orgUnitEnumeration.nextElement();
                    orgUnitQualifiers.addObject(new EOKeyValueQualifier("parentOrgUnit",
                                                                    EOQualifier.QualifierOperatorEqual,
                                                                    thisOrgUnit));
                }

                if (orgUnitQualifiers.count() > 0)
                {
                    allQualifiers.addObject(new EOOrQualifier(orgUnitQualifiers));
                }
            }
            else
            {
                allQualifiers.addObject(new EOKeyValueQualifier("parentOrgUnit",
                                                                EOQualifier.QualifierOperatorEqual,
                                                                selectedOrgUnit));
            }
        }

        EOQualifier qualifier = new EOAndQualifier(allQualifiers);
        DebugOut.println(30, "  qualifier " + qualifier);

        NSArray sortOrdering = new NSArray(new EOSortOrdering ("banner.bannerText", EOSortOrdering.CompareCaseInsensitiveAscending));

        EOFetchSpecification fetchSpec = new EOFetchSpecification("Website", qualifier, sortOrdering);
        NSArray foundSites = session().defaultEditingContext().objectsWithFetchSpecification(fetchSpec);
        DebugOut.println(30,"Found total of " + foundSites.count());

        searchResultManager.setObjectsToManage(foundSites);

        setDidPerformSearch(true);

        // We have performed a search, so now is an appropriate time to show this.
        setShouldShowNoResultsMessage( ! searchResultManager.hasSearchResults());

        return context().page();
    }



    /**
     * Returns either the string searched on, or an explanatory string if nothing was entered.
     *
     * @return either the string searched on, or an explanatory string if nothing was entered.
     */
    public String searchStringDescription()
    {
        return (searchString != null) ? searchString : "(anything)";
    }



    /**
     * Returns either the name of the OrgUnit searched on, or an explanatory string if nothing was selected.
     *
     * @return either the name of the OrgUnit searched on, or an explanatory string if nothing was selected.
     */
    public String selectedOrgUnitDescription()
    {
        return (selectedOrgUnit != null) ? selectedOrgUnit.unitName() : "(any unit)";
    }



    /**
     * Returns <code>true</code> if the Search Results Summary should be displayed.  This is only displayed when a search has returned results.
     *
     * @return <code>true</code> if the Search Results Summary should be displayed.
     */
    public boolean shouldDisplaySearchResultsSummary()
    {
        return didPerformSearch() && searchResultManager().hasSearchResults();
    }


    /* Generic setters and getters ***************************************/

    public Website currentWebsite() {
        return currentWebsite;
    }
    public void setCurrentWebsite(Website newCurrentWebsite) {
        currentWebsite = newCurrentWebsite;
    }

    /**
     * @return the unit at the root of the UnitChooserWithDefault used in the SearchForm
     */
    public NSArray rootOrgUnits() {
        return rootOrgUnits;
    }

    public SearchResultManager searchResultManager() {
        return searchResultManager;
    }

    public boolean shouldShowNoResultsMessage() {
        return shouldShowNoResultsMessage;
    }

    public void setShouldShowNoResultsMessage(boolean newShouldShowNoResultsMessage) {
        shouldShowNoResultsMessage = newShouldShowNoResultsMessage;
    }

    public boolean shouldExcludeContainedOrgUnits() {
        return shouldExcludeContainedOrgUnits;
    }

    public void setShouldExcludeContainedOrgUnits(boolean newShouldExcludeContainedOrgUnits) {
        shouldExcludeContainedOrgUnits = newShouldExcludeContainedOrgUnits;
    }

    public boolean didPerformSearch() {
        return didPerformSearch;
    }
    public void setDidPerformSearch(boolean newDidPerformSearch) {
        didPerformSearch = newDidPerformSearch;
    }

}
