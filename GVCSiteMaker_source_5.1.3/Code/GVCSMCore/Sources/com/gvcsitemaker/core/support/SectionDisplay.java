// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.support;

import net.global_village.eofextensions.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;



/*
 * * This is the central location for all Website and Section display logic and
 * supporting methods, error messages, etc. <b>All</b> display should be done
 * through these methods.
 */
public class SectionDisplay extends Object
{
    static final public String SECTION_STYLE_OVERRIDE_FLAG = "style";
    static final public String CONFIG_FLAG = "config";



    /**
     * Returns the page to display for siteID and sectionName.  The optional embeddedSiteID, if present, indicates that sectionName is from an embedded site and is not directly part of the site indentified by siteID.  The returned page may also be an instance of ErrorPageBase if the paramenters are not valid, or of WORedirect if the site is redirected.
     *
     * @param siteID the siteID of the site to display a section from
     * @param embeddedSiteID if non-null indicates that this is an embedded site
     * @param sectionName the name of the section to display.  If embeddedSiteID is not null this section is taken from the site identified by that ID, otherwise it is taken from the site identified by siteID.
     * @param versionNumber the version number of the section to display
     * @param secureID unused
     * @param aContext the WOContext of the request this page is for, used to create more pages and get editing context
     * @return the page to display for siteID and sectionName.
     */
    static public WOComponent displaySection(String siteID, String embeddedSiteID, String sectionName, String versionNumber, String secureID, WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        WOComponent pageToDisplay;
        EOEditingContext ec = aContext.session().defaultEditingContext();
        WOApplication application = WOApplication.application();

        // Handle missing siteID or section name.
        if (siteID == null)
        {
            DebugOut.println(2, "No site ID provided.");
            pageToDisplay = noSuchSiteError(siteID, aContext);
        }
        else
        {
            // Handle reference to a site that does not exist.
            Website site = Website.websiteForSiteID(siteID, ec);
            if (site == null)
            {
                DebugOut.println(2, "No site found for ID" + siteID);
                pageToDisplay = noSuchSiteError(siteID, aContext);
            }
            // Redirected sites get redirected even if they are unpublished.
            else if (site.isRedirected())
            {
                DebugOut.println(30, "Site [" + siteID + "] is redirected to URL: " + site.redirectURL());
                pageToDisplay = application.pageWithName("WORedirect", aContext);
                ((WORedirect) pageToDisplay).setUrl(site.redirectURL());
            }
            // Handle unpublished site.  Ignore the published status if the configuration override is present.
            else if (!canSiteBeViewedForRequest(site, aContext.request()))
            {
                DebugOut.println(2, "Site is unpublished");
                pageToDisplay = siteNotPublishedError(aContext);
            }
            else
            // Site is all OK, now check section
            {
                // Optimization
                if (((EOEnterpriseObject) site.sections().objectAtIndex(0)).isFault())
                {
                    EODatabaseContextAdditions.preloadRelationship(new NSArray(site), "sections");
                }

                Website embeddedSite = null;
                if (embeddedSiteID != null)
                {
                    embeddedSite = Website.websiteForSiteID(embeddedSiteID, ec);
                    DebugOut.println(10, "Found embedded site " + embeddedSiteID);
                }

                // Validate optional embedded site.
                if ((embeddedSiteID != null) && (embeddedSite == null))
                {
                    DebugOut.println(2, "No embedded site found for ID" + embeddedSiteID);
                    pageToDisplay = noSuchSiteError(embeddedSiteID, aContext);
                }
                else
                // No embedded site, or embedded site is valid
                {
                    if (sectionName != null)
                    {
                        sectionName = URLUtils.toLowerAndNormalize(sectionName);
                    }
                    DebugOut.println(8, "Normalized sectionName is " + sectionName);

                    // Get the section from the embedded site if specified, or the base site if not.
                    Section section;
                    if (embeddedSite == null)
                    {
                        if (sectionName != null)
                        {
                            section = site.sectionForNormalizedName(sectionName);
                        }
                        else
                        {
                            //If request has no section name, redirect with the home section. This will keep the URL form consistent and make for easier JavaScript manipulation.  This should be a simple change to the site display code.
                            section = site.homeSection();
                            WORedirect redirectToDestination = new WORedirect(aContext);
                            redirectToDestination.setUrl(section.sectionURL(aContext.request()));
                            return redirectToDestination;
                        }
                    }
                    else
                    {
                        // Optimization
                        if (((EOEnterpriseObject) embeddedSite.sections().objectAtIndex(0)).isFault())
                        {
                            EODatabaseContextAdditions.preloadRelationship(new NSArray(embeddedSite), "sections");
                        }
                        section = embeddedSite.sectionForNormalizedName(sectionName);
                    }

                    // Handle reference to section that does not exist.
                    if (section == null)
                    {
                        DebugOut.println(1, "No section found for " + sectionName);
                        pageToDisplay = noSuchSectionError((embeddedSiteID == null) ? siteID : embeddedSiteID, sectionName, aContext);
                    }
                    else if (section.requiresPublicHttpsAccess() && !RequestUtils.isHTTPSRequest(aContext.request()))
                    {
                        DebugOut.println(2, "Insecure request for secure public section, redirecting with secureID");
                        pageToDisplay = application.pageWithName("WORedirect", aContext);
                        ((WORedirect) pageToDisplay).setUrl(SMActionURLFactory.secureURLWithSecureIDForRequest(aContext.request()));
                    }

                    // Handle http request for section requiring https 
                    else if (section.requiresSSLConnection() && !RequestUtils.isHTTPSRequest(aContext.request()))
                    {
                        DebugOut.println(2, "Insecure request for secure section, redirecting");
                        pageToDisplay = application.pageWithName("WORedirect", aContext);
                        ((WORedirect) pageToDisplay).setUrl(SMActionURLFactory.secureURLForRequest(aContext.request()));
                    }
                    else
                    // We made it!  Show the section.
                    {
                        DebugOut.println(10, "Got site: " + site.banner().bannerText());
                        DebugOut.println(10, "Got section: " + section.name());

                        // Select the style template to use.

                        // Use the override if it is provided
                        SectionStyle sectionStyle = sectionStyleOverride(aContext);
                        if (sectionStyle == null)
                        {
                            // If this is a section of an embedded site, then use the style of the
                            // embedding section or site
                            if (embeddedSite != null)
                            {
                                Section embeddingSection = site.sectionEmbeddingSite(embeddedSite);
                                if (embeddingSection != null)
                                {
                                    sectionStyle = embeddingSection.activeSectionStyle();
                                }
                                else
                                {
                                    DebugOut.println(1, "Could not find the section that embeds site " + embeddedSite.siteID() + " in embedding site "
                                            + site.siteID());
                                    sectionStyle = section.activeSectionStyle();
                                }
                            }
                            // Otherwise let the Section decide what style it wants.
                            else
                            {
                                sectionStyle = section.activeSectionStyle();
                            }
                        }

                        SectionVersion sectionVersion = null;
                        if (versionNumber != null)
                        {
                            int versionNumberInInt = (new Integer(versionNumber)).intValue();
                            //TODO:test if versionNumber is int
                            sectionVersion = section.versionWithNumber(versionNumberInInt);
                        }

                        pageToDisplay = application.pageWithName(sectionStyle.pageScaffoldComponent(), aContext);
                        ((PageScaffold) pageToDisplay).setWebsite(site);
                        ((PageScaffold) pageToDisplay).setSectionToDisplay(section);
                        ((PageScaffold) pageToDisplay).setVersionToDisplay(sectionVersion);
                        ((PageScaffold) pageToDisplay).setActiveSectionStyle(sectionStyle);
                    }
                }
            }
        }

        return pageToDisplay;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page to be displayed when a request is made for a site which does not exist.
     *
     * @param siteID - the Id of the Website which does not exist
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return	the page to be displayed when a request is made for a site which does not exist.
     */
    static public WOComponent noSuchSiteError(String siteID, WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        ErrorPage errorPage = (ErrorPage) WOApplication.application().pageWithName("ErrorPage", aContext);
        errorPage.setMessage("A site with a site ID of '" + siteID + "' could not be located.");
        errorPage.setReason("You may have typed it incorrectly, or it may have been removed.");
        errorPage.setRobotShouldIndex(false);

        return errorPage;
    }



    /**
     * Returns the page to be displayed when a request is made for a section which does not exist.
     *
     * @param siteID - the Id of the Website the Section was requested from
     * @param siteID - the name of the Section which does not exist
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return	the page to be displayed when a request is made for a section which does not exist.
     */
    static public WOComponent noSuchSectionError(String siteID, String sectionID, WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        ErrorPage errorPage = (ErrorPage) pageWithName("ErrorPage", aContext);
        errorPage.setMessage("A page (section) named '" + sectionID + "' cannot be found in the web site with a site ID of '" + siteID + "'.");
        errorPage.setReason("You may have typed it incorrectly, or it may have been removed.");
        errorPage.setRobotShouldIndex(false);

        return errorPage;
    }



    /**
     * Returns the page to be displayed when a request contains invalid parameters which prevent it from being processed.
     *
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return	the page to displayed when a request contains invalid parameters which prevent it from being processed.
     */
    static public WOComponent invalidParametersError(WOContext aContext)
    {
        /** require [valid_param] aContext != null;  **/

        ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage", aContext);
        errorPage
                .setMessage("The above URL is missing information or contains invalid information (" + aContext.request().uri() + ") and cannot be processed.");
        errorPage.setReason("You may have typed it incorrectly, or there may have been a system error.");

        return errorPage;
    }



    /**
     * Returns the page to be displayed when a request is made for a site which is not published.
     *
     * @param siteID - the Id of the Website which is not published
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return	the page to be displayed when a request is made for a site which is not published.
     */
    static public WOComponent siteNotPublishedError(WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage", aContext);
        errorPage.setMessage("This site is temporarily unavailable - please check back later.");
        errorPage.setReason("Publication of the site is currently turned off.");

        return errorPage;
    }



    /**
     * Returns the page to be displayed when a request is made for a site which is not published.
     *
     * @param siteID - the Id of the Website which is not published
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return	the page to be displayed when a request is made for a site which is not published.
     */
    static public WOComponent dataAccessFileURLError(String message, WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage", aContext);
        errorPage.setMessage("This file can not be accessed.");
        errorPage.setReason(message);

        return errorPage;
    }



    /**
     * Returns the Style indicated on the URL via the style form value, or null if the no Style
     * was indicated or if it cannot be found.
     *
     * @param style the SectionStyle name of the style to use to display this section, passed on URL.
     * @param aContext the WOContext of the request, used to create more pages
     * @return the Style indicated on the URL, or null if the no Style was indicated or if it cannot be found.
     */
    static protected SectionStyle sectionStyleOverride(WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        SectionStyle sectionStyle = null;

        String sectionStyleID = (String) RequestUtils.cleanedFormValueForKey(aContext.request(), SECTION_STYLE_OVERRIDE_FLAG);
        if (sectionStyleID != null)
        {
            DebugOut.println(10, "Got style ID override: " + sectionStyleID);
            sectionStyle = SectionStyle.sectionStyleWithStyleID(aContext.session().defaultEditingContext(), sectionStyleID);
            DebugOut.println(10, "Found matching sectionStyle : " + (sectionStyle != null));
        }

        return sectionStyle;
    }



    /**
     * Returns <code>true</code> if the website is published or if the request contains a valid publication override (a form value named CONFIG_FLAG with a value equal to the session's ID).  This could be improved by checking that the session has an authenticated user with permission to configure this site.  This code probably belongs somewhere else, but it is not clear where...
     *
     * @see #hasValidPublicationOverrideFlag(WORequest)
     *
     * @return <code>true</code> if the website is published or if the request contains a valid publication override.
     */
    static public boolean canSiteBeViewedForRequest(Website site, WORequest request)
    {
        /** require
        [site_not_null] site != null;
        [request_not_null] request != null; **/

        return site.canBeDisplayed() || hasValidPublicationOverrideFlag(request);
    }



    /**
     * Returns <code>true</code> if the request contains a valid publication override (a form value named CONFIG_FLAG with a value equal to the session's ID).  This overridding form value is put in by the configuration pages so that unpublished sites can be previewed before publication.
     *
     * @return <code>true</code> if the request contains a valid publication override.
     */
    static public boolean hasValidPublicationOverrideFlag(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        String configFlag = (String) request.formValueForKey(CONFIG_FLAG);

        return (configFlag != null) && configFlag.equals(request.cookieValueForKey("wosid"));
    }



    /**
     * Returns the publication override (a form value named CONFIG_FLAG with a value equal to the session's ID).  This overridding form value is put in by the configuration pages so that unpublished sites can be previewed before publication.
     *
     * @return  the publication override (a form value named CONFIG_FLAG with a value equal to the session's ID).
     */
    static public String configFlagForSession(WOSession session)
    {
        /** require [session_not_null] session != null; **/

        return "&" + CONFIG_FLAG + "=" + session.sessionID();
    }


    /**
     * Returns the publication override (if any) on the passed WORequest.  This allows the flag to be duplicated on any URLs show from this request to propogate the configuration override status.
     *
     * @return the publication override (if any) on the passed WORequest
     */
    static public String configFlagFromRequest(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        String configFlagFromRequest = "";
        String configSessionID = (String) request.formValueForKey(CONFIG_FLAG);

        if (configSessionID != null)
        {
            configFlagFromRequest = "&" + CONFIG_FLAG + "=" + configSessionID;
        }

        return configFlagFromRequest;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the section style override flag (if any) on the passed WORequest.  This allows the flag to be duplicated on any URLs show from this request to propogate the style override.
     *
     * @return the section style override flag (if any) on the passed WORequest
     */
    static public String sectionStyleOverrideFlag(WORequest aRequest)
    {
        /** require [a_request_not_null] aRequest != null; **/

        String sectionStyleOverride = "";

        String sectionStyleID = (String) RequestUtils.cleanedFormValueForKey(aRequest, SectionDisplay.SECTION_STYLE_OVERRIDE_FLAG);
        if (sectionStyleID != null)
        {
            sectionStyleOverride = "&" + SectionDisplay.SECTION_STYLE_OVERRIDE_FLAG + "=" + sectionStyleID;
        }

        return sectionStyleOverride;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenince method returning the named page in the provided WOContext.
     *
     * @param pageName - the name of the WOComponent to return
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return the named page in the provided WOContext.
     */
    static public WOComponent pageWithName(String pageName, WOContext aContext)
    {
        /** require [pageName_not_null] pageName != null; 
                    [a_context_not_null] aContext != null; **/

        return WOApplication.application().pageWithName(pageName, aContext);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page to download a Section based on siteID and sectionName.  The optional embeddedSiteID, if present, indicates that sectionName is from an embedded site and is not directly part of the site indentified by siteID.  The returned page may also be an instance of ErrorPageBase if the paramenters are not valid, or of WORedirect if the site is redirected.
     *
     * @param siteID - the siteID of the site to display a section from
     * @param embeddedSiteID - if non-null indicates that this is an embedded section
     * @param sectionName - the name of the section to display.  If embeddedSiteID is not null this section is taken from the site identified by that ID, otherwise it is taken from the site identified by siteID.
     * @param aContext - the WOContext of the request this page is for, used to create more pages and get editing context
     *
     * @return the page to download a Section based on siteID and sectionName.
     */
    static public WOComponent downloadSection(String siteID, String embeddedSiteID, String sectionName, WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        //TODO: joee Refactor. Very mnuch the same as displaySection except that it returns DownloadableSectionContents instead of PageScaffold
        WOComponent pageToDisplay;
        EOEditingContext ec = aContext.session().defaultEditingContext();
        WOApplication application = WOApplication.application();

        // Handle missing siteID or section name.
        if (siteID == null)
        {
            DebugOut.println(2, "No site ID provided.");
            pageToDisplay = noSuchSiteError(siteID, aContext);
        }
        else
        {
            // Handle reference to a site that does not exist.
            Website site = Website.websiteForSiteID(siteID, ec);
            if (site == null)
            {
                DebugOut.println(2, "No site found for ID" + siteID);
                pageToDisplay = noSuchSiteError(siteID, aContext);
            }
            // Redirected sites get redirected even if they are unpublished.
            else if (site.isRedirected())
            {
                DebugOut.println(30, "Site [" + siteID + "] is redirected to URL: " + site.redirectURL());
                pageToDisplay = application.pageWithName("WORedirect", aContext);
                ((WORedirect) pageToDisplay).setUrl(site.redirectURL());
            }
            // Handle unpublished site.  Ignore the published status if the configuration override is present.
            else if (!canSiteBeViewedForRequest(site, aContext.request()))
            {
                DebugOut.println(2, "Site is unpublished");
                pageToDisplay = siteNotPublishedError(aContext);
            }
            else
            // Site is all OK, now check section
            {
                // Optimization
                if (((EOEnterpriseObject) site.sections().objectAtIndex(0)).isFault())
                {
                    EODatabaseContextAdditions.preloadRelationship(new NSArray(site), "sections");
                }

                Website embeddedSite = null;
                if (embeddedSiteID != null)
                {
                    embeddedSite = Website.websiteForSiteID(embeddedSiteID, ec);
                }

                // Validate optional embedded site.
                if ((embeddedSiteID != null) && (embeddedSite == null))
                {
                    DebugOut.println(2, "No embedded site found for ID" + embeddedSiteID);
                    pageToDisplay = noSuchSiteError(embeddedSiteID, aContext);
                }
                else
                // No embedded site, or embedded site is valid
                {
                    if (sectionName != null)
                    {
                        sectionName = URLUtils.toLowerAndNormalize(sectionName);
                    }
                    DebugOut.println(8, "Normalized sectionName is " + sectionName);

                    // Get the section from the embedded site if specified, or the base site if not.
                    Section section;
                    if (embeddedSite == null)
                    {
                        // Handle optional home section name for URls like /siteID
                        if (sectionName != null)
                        {
                            section = site.sectionForNormalizedName(sectionName);
                        }
                        else
                        {
                            section = site.homeSection();
                        }
                    }
                    else
                    {
                        // Optimization
                        if (((EOEnterpriseObject) embeddedSite.sections().objectAtIndex(0)).isFault())
                        {
                            EODatabaseContextAdditions.preloadRelationship(new NSArray(embeddedSite), "sections");
                        }
                        section = embeddedSite.sectionForNormalizedName(sectionName);
                    }

                    // Handle reference to section that does not exist.
                    if (section == null)
                    {
                        DebugOut.println(1, "No section found for " + sectionName);
                        pageToDisplay = noSuchSectionError((embeddedSiteID == null) ? siteID : embeddedSiteID, sectionName, aContext);
                    }
                    // Handle http request for section requiring https 
                    else if (section.requiresSSLConnection() && !RequestUtils.isHTTPSRequest(aContext.request()))
                    {
                        DebugOut.println(2, "Insecure request for secure section, redirecting");
                        pageToDisplay = application.pageWithName("WORedirect", aContext);
                        ((WORedirect) pageToDisplay).setUrl(SMActionURLFactory.secureURLForRequest(aContext.request()));
                    }
                    else
                    // We made it!  Show the section.
                    {
                        DebugOut.println(10, "Got site: " + site.banner().bannerText());
                        DebugOut.println(10, "Got section: " + section.name());

                        pageToDisplay = application.pageWithName("DownloadableSectionContents", aContext);
                        ((DownloadableSectionContents) pageToDisplay).setSection(section);
                    }
                }
            }
        }

        return pageToDisplay;

        /** ensure [result_not_null] Result != null; **/
    }


}
