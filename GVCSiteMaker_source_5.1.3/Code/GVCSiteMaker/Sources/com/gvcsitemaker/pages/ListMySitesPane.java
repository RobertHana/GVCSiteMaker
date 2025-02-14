// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class ListMySitesPane extends SMAuthComponent implements WebsiteContainer, SMSecurePage
{
    protected NSArray configurableWebsites;
    protected Website website;
    public Website aSite;

    public static final String SITEID_SORT = "siteID";
    public static final String BANNER_SORT = "banner.bannerText";
    public static final String OWNER_SORT = "owner.userID";
    public static final String QUOTA_SORT = "megQuota";
    public static final String FREE_SORT = "percentageOfQuotaUsed";
    public static final String DATE_CREATED_SORT = "dateCreated";
    public static final String DATE_MODIFIED_SORT = "dateLastModified";
    protected String sortOn = SITEID_SORT;
    protected NSSelector sortOrder = EOSortOrdering.CompareCaseInsensitiveAscending;


    public ListMySitesPane(WOContext context)
    {
        super(context);
    }



    /**
     * Returns the ConfigTabSet configured for aSite.
     *
     * @return the ConfigTabSet configured for aSite
     */
    public WOComponent editSite()
    {
        /** require [knows_site] aSite != null; **/
        ConfigTabSet tabSet = (ConfigTabSet)pageWithName("ConfigTabSet");
        tabSet.setWebsite(aSite);
        tabSet.setTabSelected("Tab1");

        return tabSet;
    }



    /**
     * Return the style for each row in the table of sites.  Styles the row differently
     * if the Uploaded Files quota is over WebsiteFileUsageWarningLevelPercent.
     *
     * @return the style for each row in the table of sites
     */
    public String rowStyle()
    {
        if ( ! aSite.isPublished())
        {
            return "color: grey;";
        }
        else if (aSite.percentageOfQuotaUsed() +
            ((SMApplication)application()).properties().floatValuePropertyForKey("WebsiteFileUsageWarningLevelPercent") > 100)
        {
            return "color: red;";
        }
        return "";
    }



    /**
     * Sorts the files in all folders based on sortColumn.  If sorting is already on this column, the
     * sort order (ascending or descending) is reversed.
     * 
     * @param sortColumn the column to sort on
     */
    public void sortOn(String sortColumn)
    {
        /** require [valid_sort_colum] (sortColumn != null);  **/

        if (sortOn().equals(sortColumn))
        {
            setSortOrder(isAscendingSort() ? EOSortOrdering.CompareCaseInsensitiveDescending : EOSortOrdering.CompareCaseInsensitiveAscending);
        }
        else
        {
            setSortOn(sortColumn);
            setSortOrder(EOSortOrdering.CompareCaseInsensitiveAscending);
        }

        // clear cached values
        configurableWebsites = null;

        /** ensure [sort_set] isSortingOn(sortColumn);   **/
    }



    /**
     * Returns <code>true</code> if sorting is currently on the passed column. 
     * 
     * @param sortColumn the column name to compare to the column currently sorted on
     * @return <code>true</code> if sorting is currently on the passed column
     */
    public boolean isSortingOn(String sortColumn)
    {
        /** require [valid_sort_colum] (sortColumn != null);  **/
        return sortOn().equals(sortColumn);
    }



    /**
     * @return <code>true</code> if sorting is currently in ascending order
     */
    public boolean isAscendingSort()
    {
        return sortOrder.equals(EOSortOrdering.CompareCaseInsensitiveAscending);
    }



    /**
     * Sorts the list of files by name.  If the files are already sorted on name, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnSiteID()
    {
        sortOn(SITEID_SORT);
        return context().page();
    }


    /**
     * Sorts the list of files by banner.  If the files are already sorted on banner, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnBanner()
    {
        sortOn(BANNER_SORT);
        return context().page();
    }


    /**
     * Sorts the list of files by owner.  If the files are already sorted on owner, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnOwner()
    {
        sortOn(OWNER_SORT);
        return context().page();
    }


    /**
     * Sorts the list of files by quota.  If the files are already sorted on quota, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnQuota()
    {
        sortOn(QUOTA_SORT);
        return context().page();
    }


    /**
     * Sorts the list of files by quota.  If the files are already sorted on quota, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnFree()
    {
        sortOn(FREE_SORT);
        return context().page();
    }


    /**
     * Sorts the list of files by quota.  If the files are already sorted on quota, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnDateCreated()
    {
        sortOn(DATE_CREATED_SORT);
        return context().page();
    }


    /**
     * Sorts the list of files by quota.  If the files are already sorted on quota, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnDateModified()
    {
        sortOn(DATE_MODIFIED_SORT);
        return context().page();
    }



    /**
     * @return the appropriate up/down arrow image for the selected sort column
     */
    public String sortDirectionImage()
    {
        return isAscendingSort() ? "/GVCSiteMaker/Images/sort-ascending.gif" : "/GVCSiteMaker/Images/sort-descending.gif";
    }



    /**
     * @return <code>true</code> if sorting is on file name
     */
    public boolean isSortingOnName()
    {
        return isSortingOn(SITEID_SORT);
    }


    /**
     * @return <code>true</code> if sorting is on file name
     */
    public boolean isSortingOnBanner()
    {
        return isSortingOn(BANNER_SORT);
    }


    /**
     * @return <code>true</code> if sorting is on file name
     */
    public boolean isSortingOnOwner()
    {
        return isSortingOn(OWNER_SORT);
    }


    /**
     * @return <code>true</code> if sorting is on file size
     */
    public boolean isSortingOnQuota()
    {
        return isSortingOn(QUOTA_SORT);
    }


    /**
     * @return <code>true</code> if sorting is on file size
     */
    public boolean isSortingOnFree()
    {
        return isSortingOn(FREE_SORT);
    }


    /**
     * @return <code>true</code> if sorting is on file size
     */
    public boolean isSortingOnDateCreated()
    {
        return isSortingOn(DATE_CREATED_SORT);
    }


    /**
     * @return <code>true</code> if sorting is on file size
     */
    public boolean isSortingOnDateModified()
    {
        return isSortingOn(DATE_MODIFIED_SORT);
    }



    public Website website()
    {
        return website;
    }



    public NSArray configurableWebsites()
    {
        if (configurableWebsites == null)
        {
            NSArray unorderedWebsites = ((SMSession)session()).currentUser().configurableWebsites();
            EOSortOrdering sortOrder = new EOSortOrdering(sortOn(), sortOrder());
            configurableWebsites = EOSortOrdering.sortedArrayUsingKeyOrderArray(unorderedWebsites, new NSArray(sortOrder));
        }
        return configurableWebsites;
    }



    public void setWebsite(Website newWebsite)
    {
        website = newWebsite;
    }



    public String sortOn()
    {
        return sortOn;
    }


    public void setSortOn(String sort)
    {
        sortOn = sort;
    }


    public NSSelector sortOrder()
    {
        return sortOrder;
    }


    public void setSortOrder(NSSelector order)
    {
        sortOrder = order;
    }



}
