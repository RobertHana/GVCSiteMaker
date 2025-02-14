// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.admin.website;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Collection;

import com.gvcsitemaker.admin.user.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class ManageWebsitesPage extends WOComponent implements SMSecurePage
{
    protected Integer websiteIndex;
    protected NSArray websitesToManage;
    protected Website aWebsite;
    protected NSMutableDictionary markedWebsites = new NSMutableDictionary();

    protected boolean displayNoMarksErrorMessage = false;

    public static final NSComparator ManageWebsiteComparator = new ManageWebsiteComparator();


    public ManageWebsitesPage(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Overriden to clear validation errors.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        setDisplayNoMarksErrorMessage(false);
    }



    /**
     * Returns the <code>CreateWebsitePage</code>.
     * 
     * @return the <code>CreateWebsitePage</code>
     */
    public CreateWebsitePage createWebsite()
    {
        return (CreateWebsitePage)pageWithName("CreateWebsitePage");
    }



    /**
     * Sets the websites to manage.  This method sorts the given array by parentOrgUnit (hierarchically), owner ID then site ID.
     *
     * @param websitesToManage the websites to manage
     */
    public void setWebsitesToManage(NSArray newWebsitesToManage)
    {
        /** require [valid_param] newWebsitesToManage != null; **/
        websitesToManage = NSArrayAdditions.sortedArrayUsingComparator(newWebsitesToManage, ManageWebsiteComparator);
        /** ensure [definition] websitesToManage != null; **/
    }


    /**
     * Returns the websites to manage.
     * 
     * @return the websites to manage
     */
    public NSArray websitesToManage()
    {
        return websitesToManage;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns this page with all websites marked.
     * 
     * @return this page
     */
    public WOComponent markAllSites()
    {
        for (int i = 0; i < websitesToManage().count(); i++)
        {
            // Just laziness, I know...
            websiteIndex = new Integer(i);
            setWebsiteMarked(true);
        }
        return context().page();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the Send Message page with the selected websites as the default recipients.
     * 
     * @return the Send Message page
     */
    public WOComponent emailMarkedSites()
    {
        if (hasMarkedWebsites())
        {
            EmailConfigureGroupMembers nextPage = (EmailConfigureGroupMembers)pageWithName("EmailConfigureGroupMembers");

            NSMutableSet addresses = new NSMutableSet();
            for (int i = 0; i < websitesToManage().count(); i++)
            {
                Integer index = new Integer(i);
                if (websiteMarkedAtIndex(index))
                {
                    Website website = (Website)websitesToManage().objectAtIndex(i);
                    addresses.addObject(website.owner().emailAddress());
                }
            }

            nextPage.setTextEnteredUserIDs(addresses.allObjects().componentsJoinedByString(", "));
            return nextPage;
        }
        else
        {
            setDisplayNoMarksErrorMessage(true);
            return context().page();
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the Send Message page with the selected websites as the default recipients.
     * 
     * @return the Send Message page
     */
    public WOComponent manageWebsite()
    {
        ManageWebsitePage nextPage = (ManageWebsitePage)pageWithName("ManageWebsitePage");
        nextPage.setWebsite(aWebsite());
        return nextPage;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the Send Message page with the selected websites as the default recipients.
     * 
     * @return the Send Message page
     */
    public String configureWebsiteURL()
    {
        return aWebsite().configURL(context().request());
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns this page after downloading the table as a text file.
     * 
     * @return this page
     */
    public NSData fileData()
    {
        final String delimiter = "\t";
        StringBuffer data = new StringBuffer();
        data.append("Org. Unit" + delimiter + "OwnerID" + delimiter + "SiteID" + delimiter + "Mb Used" + delimiter + "% Used" + delimiter + "Date Created" + delimiter + "Last Configure" + delimiter + "Published\n");
        // The import date format that Excel uses
        NSTimestampFormatter exportFormatter = new NSTimestampFormatter("%Y-%m-%d %H:%M:%S");

        
        Enumeration tableEnumerator = websitesToManage().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            Website website = (Website)tableEnumerator.nextElement();
            NSMutableArray row = new NSMutableArray();
            row.addObject(website.parentOrgUnit().unitName());
            row.addObject(website.owner().userID());
            row.addObject(website.siteID());
            row.addObject(new Float(website.fileSizeUsageInMegabytes()).toString());
            row.addObject(new Double(website.percentageOfQuotaUsed()).toString());
            row.addObject(exportFormatter.format(website.dateCreated()));
            row.addObject(exportFormatter.format(website.dateLastModified()));
            row.addObject(website.published());
            data.append(row.componentsJoinedByString(delimiter));
            data.append('\n');
        }
        return new NSData(data.toString(), "ASCII");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns this page after downloading the table as a text file.
     * 
     * @return this page
     */
    public WOComponent downloadTable()
    {
        return context().page();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if there is at least one marked website, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there is at least one marked website, <code>false</code> otherwise
     */
    public boolean hasMarkedWebsites()
    {
        try
        {
            return Collection.doAnyObjectsRespond(markedWebsites, java.lang.Boolean.TRUE, new NSSelector("booleanValue"), null);
        }
        catch (Exception e)
        {
            // this should never happen
            throw new Error(e.getMessage());
        }
    }


    /**
     * Sets the marked flag for the website at the current <code>websiteIndex</code>.
     *
     * @param marked is the website at <code>websiteIndex</code> marked?
     */
    public void setWebsiteMarked(boolean marked)
    {
        markedWebsites.setObjectForKey(new java.lang.Boolean(marked), websiteIndex());
    }


    /**
     * Returns <code>true</code> if the website at <code>index</code> is marked, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the website at <code>index</code> is marked, <code>false</code> otherwise
     */
    public boolean websiteMarkedAtIndex(Integer index)
    {
        java.lang.Boolean isMarked = (java.lang.Boolean)markedWebsites.objectForKey(index);
        return isMarked == null ? false : isMarked.booleanValue();
    }


    /**
     * Returns <code>true</code> if the website at the current <code>websiteIndex</code> is marked, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the website at the current <code>websiteIndex</code> is marked, <code>false</code> otherwise
     */
    public boolean websiteMarked()
    {
        return websiteMarkedAtIndex(websiteIndex());
    }



    /**
     * Returns the editing context used by this page.
     *
     * @return the editing context used by this page
     */
    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
    }



    public boolean hasResults() {
        return websitesToManage().count() > 0;
    }



    public boolean displayNoResultsErrorMessage() {
        return ! hasResults();
    }



    public Integer websiteIndex()
    {
        return websiteIndex;
    }

    public void setWebsiteIndex(Integer i)
    {
        websiteIndex = i;
    }



    public Website aWebsite()
    {
        return aWebsite;
    }

    public void setAWebsite(Website website)
    {
        aWebsite = website;
    }



    public boolean displayNoMarksErrorMessage()
    {
        return displayNoMarksErrorMessage;
    }

    public void setDisplayNoMarksErrorMessage(boolean b)
    {
        displayNoMarksErrorMessage = b;
    }



    /**
     * Sorts Websites by OrgUnits hierarchically, then OwnerID (alphabetical), and then siteID (alphabetical).
     */ 
    static public class ManageWebsiteComparator extends NSComparator
    {

        /**
         * Sorts websites.
         * 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            Website firstWebsite = (Website)firstObject;
            Website secondWebsite = (Website)secondObject;

            int result = OrgUnit.HierarchicalComparator.compare(firstWebsite.parentOrgUnit(), secondWebsite.parentOrgUnit());

            if (result == NSComparator.OrderedSame)
            {
                result = AscendingCaseInsensitiveStringComparator.compare(firstWebsite.owner().userID(), secondWebsite.owner().userID());
            }

            if (result == NSComparator.OrderedSame)
            {
                result = AscendingCaseInsensitiveStringComparator.compare(firstWebsite.siteID(), secondWebsite.siteID());
            }

            return result;
        }


    }

    
    
    /**
     * Returns the site URL with the publication override key on the end.
     *
     * @return the site URL with the publication override key on the end.
     */
    public String previewURL()
    {
        return aWebsite.siteURL() + SectionDisplay.configFlagForSession(session());
    }    
}
