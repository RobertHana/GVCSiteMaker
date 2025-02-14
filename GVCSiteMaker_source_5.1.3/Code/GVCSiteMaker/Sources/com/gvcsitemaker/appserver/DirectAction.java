// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.appserver;

import net.global_village.virtualtables.*;
import net.global_village.woextensions.tests.Application;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.custom.appserver.*;
import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * WODirectActions for the GVC.SiteMaker application. This builds on what is
 * in SMDirectAction in GVCSMCore and SMCustomDirectAction in GVCSMCustom.
 */
public class DirectAction extends SMCustomDirectAction
{


    /**
     * Designated constructor
     */
    public DirectAction(WORequest aRequest)
    {
        super(aRequest);
    }



    /**
     * Returns the page displaying the Home page for the site indicated in the URL.
     *
     * @param site - the siteID of the site whose home page will be displayed, passed in on URL.
     *
     * @return the page displaying the Home page for the site indicated in the URL.
     * @deprecated use displaySection instead
     */
    public WOActionResults displaySiteAction()
    {
        return displaySectionAction();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page displaying the indicated section for the site indicated in the URL.
     *
     * @param site - the siteID of the site to display a section from, passed in on URL.
     * @param embeddedsite - if non-null indicates that this is an embedded section, passed in on URL.
     * @param section - the name of the section to display, passed in on URL.  If embeddedsite is not null this section is taken from the site identified by that ID, otherwise it is taken from the site identified by site.
     *
     * @return the page to display for siteID and sectionName.
     */
    public WOActionResults displaySectionAction()
    {
        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String embeddedSiteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.EMBEDDED_SITE_KEY);
        String sectionName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_KEY);
        String versionNumber = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.VERSION_NUMBER);
        String secureID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECURE_ID_KEY);

        return SectionDisplay.displaySection(siteID, embeddedSiteID, sectionName, versionNumber, secureID, context());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the requested file from the indicated site, or an error message page if it cannot be located.
     *
     * @param site - the siteID of the site to get the SiteFile from, passed in on URL.
     * @param file - the file name of the SiteFile to return, passed in on URL.
     * @param key - for access protected SiteFiles a password allowing access for those who would not normally be granted access, passed in on URL.
     *
     * @return the requested file from th indicated site, or an error message page if it cannot be located.
     */
    public WOActionResults displayFileAction()
    {
        WOActionResults displayFilePage = null;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String fileName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.FILE_KEY);
        String key = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.FILE_PASSWORD_KEY);

        // Handle missing site ID or filename.
        if ((siteID == null) || (fileName == null))
        {
            displayFilePage = SectionDisplay.invalidParametersError(context());
        }
        else
        {
            Website site = Website.websiteForSiteID(siteID, session().defaultEditingContext());

            // Handle reference to site that does not exist.
            if (site == null)
            {
                displayFilePage = SectionDisplay.noSuchSiteError(siteID, context());
            }
            else
            {
                fileName = URLUtils.toLowerAndNormalize(fileName);
                DebugOut.println(8, "Normalized fileName is " + fileName);
                SiteFile file = site.fileForFilename(fileName);

                // Handle reference to SiteFile that does not exist.
                if (file == null)
                {
                    displayFilePage = noSuchFileError(siteID, fileName);
                }
                // Handle http request for file requiring https.  This is waived when there is a password
                else if ((!file.hasPassword(key)) && file.requiresSSLConnection() && !RequestUtils.isHTTPSRequest(request()))
                {
                    DebugOut.println(2, "Insecure request for secure file, redirecting");
                    displayFilePage = pageWithName("WORedirect");
                    ((WORedirect) displayFilePage).setUrl(file.url(request()));
                }
                else
                {
                    DebugOut.println(10, "Got site: " + site.banner().bannerText());
                    DebugOut.println(10, "Got file: " + file.uploadedFilename());

                    displayFilePage = pageWithName("DisplayFile");
                    ((DisplayFile) displayFilePage).setFile(file);

                    // Use the access override passwords if it is present and validates.
                    if ((key != null) && (file.hasPassword(key)))
                    {
                        ((DisplayFile) displayFilePage).setAuthOverride(true);
                    }
                }
            }
        }

        return displayFilePage;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the requested DataAccess file from the site/section/row/column indicated, or an error message page if it cannot be located or if the user does not have permission to view it.
     *
     * @param site the siteID of the site to get the SiteFile from, passed in on URL
     * @param section the normalized name of the Section to access the SiteFile through, passed in on URL
     * @param recordID the PK of the VirtualRow which contains this file, passed in on URL
     * @param columnName the normalized name of the Column in the VirtualRow which contians the file, passed in on URL
     * @return the requested file, or an error message page if it cannot be located or that permission was denied
     */
    public WOActionResults displayDataAccessFileAction()
    {
        WOActionResults displayFilePage = null;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String sectionName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_KEY);
        String recordID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.RECORD_ID_KEY);
        String columnName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.COLUMN_NAME_KEY);

        // Handle missing parameters.
        if ((siteID == null) || (sectionName == null) || (recordID == null) || (columnName == null))
        {
            displayFilePage = SectionDisplay.invalidParametersError(context());
        }
        else
        {
            Website site = Website.websiteForSiteID(siteID, session().defaultEditingContext());

            // Handle reference to site that does not exist.
            if (site == null)
            {
                displayFilePage = SectionDisplay.noSuchSiteError(siteID, context());
            }
            else
            {
                sectionName = URLUtils.toLowerAndNormalize(sectionName);
                Section section = site.sectionForNormalizedName(sectionName);
                Session theSession = (Session) session();

                // TODO Ideally this would get pushed into RemoteParticipationGroup.isMember(User), BUT that needs session to verify
                boolean canRemoteParticipationUserViewSection = false;
                if (section != null)
                {
                    canRemoteParticipationUserViewSection = theSession.isRemoteParticipantSession() && theSession.authenticatedRemoteParticipantGroup() != null
                            && theSession.authenticatedRemoteParticipantGroup().parentWebsite().globalID().equals(section.website().globalID())
                            && section.groups().containsObject(theSession.authenticatedRemoteParticipantGroup());
                }

                // Handle reference to Section that does not exist.
                if (section == null)
                {
                    displayFilePage = SectionDisplay.noSuchSectionError(siteID, sectionName, context());
                }
                // Handle http request for file requiring https 
                else if (section.requiresSSLConnection() && !RequestUtils.isHTTPSRequest(request()))
                {
                    DebugOut.println(2, "Insecure request for secure data access file, redirecting");
                    displayFilePage = pageWithName("WORedirect");
                    ((WORedirect) displayFilePage).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                            + RequestUtils.originalURLFromRequest(request()));
                }
                // Handle wrong Section type
                else if (!(section.type() instanceof DataAccessSectionType))
                {
                    displayFilePage = SectionDisplay.dataAccessFileURLError(sectionName + " is not a Data Access section.", context());
                }
                // Handle authenticated user without permission to view Section
                else if (theSession.isUserAuthenticated() && (!section.isViewableByUser(theSession.currentUser()) && !canRemoteParticipationUserViewSection))
                {
                    displayFilePage = SectionDisplay.dataAccessFileURLError("You do not have permission to view data in that section.", context());
                }
                // Handle unauthenticated user without permission to view Section by forcing to log in
                else if ((!theSession.isUserAuthenticated()) && !section.isViewableByUser(theSession.currentUser()))
                {
                    displayFilePage = SMAuthComponent.redirectToLogin(context(), "Authentication is required to access this File.");
                }
                else
                {
                    // At this point we have a valid Website and Section, a not null recordID and columnName, and we know that the user has permission to view the contents of this section.
                    VirtualRow row;

                    try
                    {
                        Object rowPK;
                        if (SMApplication.smApplication().isUsingIntegerPKs())
                        {
                            rowPK = new Integer(recordID);
                        }
                        else
                        {
                            if (recordID.length() < 48)
                            {
                                recordID = ERXStringUtilities.leftPad(recordID, '0', 48);
                            }
                            rowPK = new NSData(ERXStringUtilities.hexStringToByteArray(recordID));
                        }

                        row = (VirtualRow) ((DataAccess) section.component()).databaseTable().objectWithPrimaryKey(rowPK);
                        com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode searchMode = (com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode) ((DataAccess) section
                                .component()).componentForMode(com.gvcsitemaker.core.pagecomponent.DataAccessMode.SEARCH_MODE);

                        if ((((DataAccess) section.component()).restrictToDefaultResults())
                                && (!searchMode.storedSearchRowsWithParameters(new DataAccessParameters(context(), (DataAccess) section.component()))
                                        .containsObject(row)))
                        {
                            displayFilePage = SectionDisplay.dataAccessFileURLError("You do not have permission to view the record holding that file.",
                                    context());
                        }
                        // Handle incorrect column name
                        else if (!row.hasColumnNamed(columnName))
                        {
                            displayFilePage = SectionDisplay.dataAccessFileURLError("There is no field named " + columnName + " in that section.", context());
                        }
                        // Handle a column that is not visible in Single or List mode
                        else if (!((DataAccess) section.component()).isColumnVisible(columnName))
                        {
                            displayFilePage = SectionDisplay.dataAccessFileURLError("You do not have permission to view the field holding that file.",
                                    context());
                        }
                        else
                        {
                            Object file = row.valueForFieldNamed(columnName);

                            // Handle missing VirtualSiteFileField or missing SiteFile
                            if (file == null)
                            {
                                displayFilePage = noSuchFileError(siteID, columnName);
                            }
                            // Handle incorrect field type
                            else if (!(file instanceof SiteFile))
                            {
                                displayFilePage = SectionDisplay.dataAccessFileURLError("The field named " + columnName + " does not contain a file.",
                                        context());
                            }
                            else
                            {
                                DebugOut.println(10, "Got site: " + site.banner().bannerText());
                                DebugOut.println(10, "Got section: " + sectionName);
                                DebugOut.println(10, "Got file: " + ((SiteFile) file).uploadedFilename());

                                // Everything is OK, display the file.
                                displayFilePage = pageWithName("DisplayFile");
                                ((DisplayFile) displayFilePage).setFile((SiteFile) file);

                                // Use the access override as we have already checked Section permission.  There is no file level permissions in database tables.
                                ((DisplayFile) displayFilePage).setAuthOverride(true);
                            }
                        }
                    }
                    // Handle missing row.
                    catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
                    {
                        displayFilePage = SectionDisplay.dataAccessFileURLError("The record holding that file has been deleted.", context());
                    }
                }
            }
        }

        return displayFilePage;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * This action is used to access files from an export file.  It uses a special action to restrict
     * access to site owners and co-owners.  Returns the requested DataAccess file from the 
     * site/row/column indicated, or an error message page if it cannot be located or if the user 
     * does not have permission to view it (is not a site owner or co-owner).
     *
     * @param site - the siteID of the site to get the SiteFile from, passed in on URL.
     * @param recordID - the PK of the VirtualRow which contains this file, passed in on URL.
     * @param columnName - the normalized name of the Column in the VirtualRow which contians the file, passed in on URL.
     *
     * @return the requested file, or an error message page if it cannot be located or that permission was denied.
     */
    public WOActionResults exportDataAccessFileAction()
    {
        WOActionResults displayFilePage = null;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String recordID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.RECORD_ID_KEY);
        String columnName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.COLUMN_NAME_KEY);

        // Handle missing parameters.
        if ((siteID == null) || (recordID == null) || (columnName == null))
        {
            displayFilePage = SectionDisplay.invalidParametersError(context());
        }
        else
        {
            Website site = Website.websiteForSiteID(siteID, session().defaultEditingContext());

            // Handle reference to site that does not exist.
            if (site == null)
            {
                displayFilePage = SectionDisplay.noSuchSiteError(siteID, context());
            }
            // Handle request without https 
            else if (!RequestUtils.isHTTPSRequest(request()))
            {
                DebugOut.println(2, "Insecure request for exported file, redirecting");
                displayFilePage = pageWithName("WORedirect");
                ((WORedirect) displayFilePage).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                        + RequestUtils.originalURLFromRequest(request()));
            }
            else
            {
                NSArray ownersAndCoOwners = site.configureGroup().users();
                Session theSession = (Session) session();

                // Handle authenticated user without permission to Configure Website
                if (theSession.isUserAuthenticated() && !ownersAndCoOwners.containsObject(theSession.currentUser()))
                {
                    displayFilePage = SectionDisplay.dataAccessFileURLError("You do not have permission to view data in that Website.", context());
                }
                // Handle unauthenticated user by forcing to log in
                else if (!theSession.isUserAuthenticated())
                {
                    displayFilePage = SMAuthComponent.redirectToLogin(context(), "Authentication is required to access this file.");
                }
                else
                {
                    // At this point we have a valid Website, a not null recordID and columnName, and we know that the user is an owner or co-owner.
                    VirtualRow row;

                    try
                    {
                        Object rowPK;
                        if (SMApplication.smApplication().isUsingIntegerPKs())
                        {
                            rowPK = new Integer(recordID);
                        }
                        else
                        {
                            rowPK = new NSData(ERXStringUtilities.hexStringToByteArray(recordID));
                        }
                        row = (VirtualRow) EOUtilities.faultWithPrimaryKeyValue(session().defaultEditingContext(), "VirtualRow", rowPK);
                        if (net.global_village.eofextensions.EOObject.isDummyFaultEO(row))
                        {
                            throw new com.webobjects.eoaccess.EOObjectNotAvailableException("Can't find VirtualRow with PK " + rowPK);
                        }

                        // Handle incorrect column name
                        if (!row.hasColumnNamed(columnName))
                        {
                            displayFilePage = SectionDisplay.dataAccessFileURLError("There is no field named " + columnName + " in that database table.",
                                    context());
                        }
                        else
                        {
                            Object file = row.valueForFieldNamed(columnName);

                            // Handle missing VirtualSiteFileField or missing SiteFile
                            if (file == null)
                            {
                                displayFilePage = noSuchFileError(siteID, columnName);
                            }
                            // Handle incorrect field type
                            else if (!(file instanceof SiteFile))
                            {
                                displayFilePage = SectionDisplay.dataAccessFileURLError("The field named " + columnName + " does not contain a file.",
                                        context());
                            }
                            else
                            {
                                DebugOut.println(10, "Got site: " + site.banner().bannerText());
                                DebugOut.println(10, "Got file: " + ((SiteFile) file).uploadedFilename());

                                // Everything is OK, display the file.
                                displayFilePage = pageWithName("DisplayFile");
                                ((DisplayFile) displayFilePage).setFile((SiteFile) file);

                                // Use the access override as we have already checked Section permission.  There is no file level permissions in database tables.
                                ((DisplayFile) displayFilePage).setAuthOverride(true);
                            }
                        }
                    }
                    // Handle missing row.
                    catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
                    {
                        displayFilePage = SectionDisplay.dataAccessFileURLError("The record holding that file has been deleted.", context());
                    }
                }
            }
        }

        return displayFilePage;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the page to be displayed when a request is made for a SiteFile which does not exist.
     *
     * @param siteID - the Id of the Website which does not exist
     * @param aContext - the WOContext of the request, used to create more pages
     *
     * @return	the page to be displayed when a request is made for a SiteFile which does not exist.
     */
    public WOComponent noSuchFileError(String siteID, String fileName)
    {
        ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage");
        errorPage.setMessage("A file named '" + fileName + "' cannot be found in the web site with a site ID of '" + siteID + "'.");
        errorPage.setReason("The URL may be incorrect, or the file may have been removed.");

        return errorPage;
    }



    /**
     * Returns the user info page.
     *
     * @return the user info page
     */
    public WOActionResults userInfoPageAction()
    {
        WOActionResults configPage = null;

        if (!RequestUtils.isHTTPSRequest(request()))
        {
            configPage = pageWithName("WORedirect");
            ((WORedirect) configPage).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                    + RequestUtils.originalURLFromRequest(request()));
        }
        else
        {
            configPage = pageWithName("UserInfoPage");
        }

        return configPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the support page.
     *
     * @return the support page
     */
    public WOActionResults supportAction()
    {
        WOActionResults supportPage = null;

        if (!RequestUtils.isHTTPSRequest(request()))
        {
            supportPage = pageWithName("WORedirect");
            ((WORedirect) supportPage).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                    + RequestUtils.originalURLFromRequest(request()));
        }
        else
        {
            supportPage = pageWithName("SupportPage");
        }

        return supportPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the configuration page for the indicated site, or an error message page if it cannot be located.
     * If no site is indicated returns the page to select a site to configure.
     *
     * @param site the siteID of the site to configure, passed in on URL.  Optional.
     * @return the configuration page for the indicated site, or the page to select a site to configure from,
     *         or an error message page if it cannot be located.
     */
    public WOActionResults configSiteAction()
    {
        WOActionResults configPage = null;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.CONFIG_SITE_KEY);
        DebugOut.println(30, "siteID = " + siteID);

        if (!RequestUtils.isHTTPSRequest(request()))
        {
            configPage = pageWithName("WORedirect");
            ((WORedirect) configPage).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                    + RequestUtils.originalURLFromRequest(request()));
        }

        // Handle missing site ID by displaying the page to select a site to configure from.
        else if (siteID == null)
        {
            configPage = pageWithName("ListMySitesPane");
        }
        else
        {
            Website siteToConfigure = Website.websiteForSiteID(siteID, session().defaultEditingContext());

            // Handle reference to site that does not exist.
            if (siteToConfigure == null)
            {
                configPage = SectionDisplay.noSuchSiteError(siteID, context());
            }
            else
            {
                configPage = pageWithName("ConfigTabSet");
                ((ConfigTabSet) configPage).setWebsite(siteToConfigure);
                ((Session) session()).setEditingWebsite(siteToConfigure);
            }
        }

        return configPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the file managment page for the indicated site, or an error message page if it cannot be located.
     *
     * @param site - the siteID of the site to manage files for, passed in on URL.
     *
     * @return the file managment page for the indicated site, or an error message page if it cannot be located.
     */
    public WOActionResults configSiteFileMgmtAction()
    {
        WOActionResults configPage = null;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.CONFIG_SITE_KEY);
        DebugOut.println(30, "siteID = " + siteID);

        if (!RequestUtils.isHTTPSRequest(request()))
        {
            configPage = pageWithName("WORedirect");
            ((WORedirect) configPage).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                    + RequestUtils.originalURLFromRequest(request()));
        }
        // Handle missing site ID.
        else if (siteID == null)
        {
            configPage = SectionDisplay.noSuchSiteError(siteID, context());
        }
        else
        {
            Website siteToConfigure = Website.websiteForSiteID(siteID, session().defaultEditingContext());

            // Handle reference to site that does not exist.
            if (siteToConfigure == null)
            {
                configPage = SectionDisplay.noSuchSiteError(siteID, context());
            }
            else
            {
                configPage = pageWithName("ConfigTabSet");
                ((ConfigTabSet)configPage).setWebsite(siteToConfigure);
                ((ConfigTabSet)configPage).setTab4Selected(true);
            }
        }

        return configPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the ShowErrorLogPage.
     *
     * @return the ShowErrorLogPage.
     */
    public WOActionResults showErrorLogAction()
    {
        return pageWithName("ShowErrorLogPage");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page used to change the password of a user managed by GVC.SiteMaker.
     *
     * @param userID (form parameter to direct action), optional, the User ID of the user to change the password for.
     *
     * @return the page used to change the password of a user managed by GVC.SiteMaker.
     */
    public WOActionResults changePasswordAction()
    {
        ChangePasswordPage changePasswordPage = (ChangePasswordPage) pageWithName("ChangePasswordPage");

        //  If the optional User ID was passed in to this direct action send it onto the page.
        String userID = (String) RequestUtils.cleanedFormValueForKey(request(), "userID");
        if (userID != null)
        {
            changePasswordPage.setUserID(userID);
        }

        return changePasswordPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
    * Returns the page used to send a password to a forgetful GVC.SiteMaker managed user.
     *
     * @return the page used to send a password to a forgetful GVC.SiteMaker managed user.
     */
    public WOActionResults sendPasswordAction()
    {
        return pageWithName("SendPasswordPage");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the ListOfAllWebsitesPage.  This is here so that this page can be referenced from a static URL for search engine indexing.
     *
     * @return the ListOfAllWebsitesPage.
     */
    public WOActionResults indexAction()
    {
        return pageWithName("ListOfAllWebsitesPage");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the SectionStyleManagementPage.  This is the entry point for style admins.
     *
     * @return the SectionStyleManagementPage.  
     */
    public WOActionResults manageSiteStylesAction()
    {
        return pageWithName("SectionStyleManagementPage");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the RequestSite page used to request a new site.
     *
     * @return the RequestSite.
     */
    public WOActionResults requestSiteAction()
    {
        return pageWithName("RequestSite");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the DirOfSites used to search for sites.
     *
     * @return the DirOfSites.
     */
    public WOActionResults siteDirectoryAction()
    {
        return pageWithName("DirOfSites");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page containing the EditLive! editor applet.
     *
     * @return the page containing the EditLive! editor applet
     */
    public WOActionResults editLiveAction()
    {
        return pageWithName("EditLivePage");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns a component with the contents of the pane matching the componentID in the url bindings
     *
     * @return a component with the contents of the pane matching the componentID in the url bindings
     */
    public WOActionResults displayPaneContentsAction()
    {
        WOActionResults displayPane = null;

        String componentID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.COMPONENT_ID_KEY);
        DebugOut.println(30, "componentID = " + componentID);
        try
        {
            Object panePK;
            if (SMApplication.smApplication().isUsingIntegerPKs())
            {
                panePK = new Integer(componentID);
            }
            else
            {
                panePK = new NSData(ERXStringUtilities.hexStringToByteArray(componentID));
            }
            com.gvcsitemaker.core.pagecomponent.MixedMediaPane pane = (com.gvcsitemaker.core.pagecomponent.MixedMediaPane) EOUtilities
                    .objectWithPrimaryKeyValue(session().defaultEditingContext(), "MixedMediaPane", panePK);

            DebugOut.println(30, "displaying pane..." + pane.typeName());

            displayPane = pageWithName("MixedMediaPane");
            ((com.gvcsitemaker.componentprimitives.MixedMediaPane) displayPane).setDisplayPane(pane);

        }
        // Handle missing pane.
        catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
        {
            displayPane = SectionDisplay.dataAccessFileURLError("Cannot find display pane.", context());
        }

        return displayPane;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page to download a Section based on siteID and sectionName.  The optional embeddedSiteID, if present, indicates that sectionName is from an embedded site and is not directly part of the site indentified by siteID.  The returned page may also be an instance of ErrorPageBase if the paramenters are not valid, or of WORedirect if the site is redirected.
     *
     * @return the page to download a Section based on siteID and sectionName.
     */
    public WOActionResults downloadSectionContentsAction()
    {
        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String embeddedSiteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.EMBEDDED_SITE_KEY);
        String sectionName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_KEY);

        return SectionDisplay.downloadSection(siteID, embeddedSiteID, sectionName, context());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the EditSection page configured for the passed section and to return to the previous URL when it is done.
     *
     * @return the page to download a Section based on siteID and sectionName.
     */
    public WOActionResults editSectionAction()
    {
        // If the user is authenticated, be sure were are in secure mode
        // We do not want to be in HTTPS for publicly edited sections as this will
        // trigger Cosign and other external Web SSO services
        if (((Session) session()).isUserAuthenticated() && !RequestUtils.isHTTPSRequest(request()))
        {
            WOComponent redirect = pageWithName("WORedirect");
            ((WORedirect) redirect).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                    + RequestUtils.originalURLFromRequest(request()));
            return redirect;
        }

        EOEditingContext ec = ((Session) session()).newEditingContext();
        String versionID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_VERSION_ID_KEY);
        String returnUrl = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.URL_KEY);
        if (returnUrl == null || (! ((SMApplication)Application.application()).urlHasValidDomain(returnUrl)))
        {
            returnUrl = SMActionURLFactory.mainPageURL();
        }

        DebugOut.println(30, "returnUrl = " + returnUrl);
        DebugOut.println(30, "versionID = " + versionID);

        Object versionIDAsObject;
        if (SMApplication.smApplication().isUsingIntegerPKs())
        {
            versionIDAsObject = new Integer(versionID);
        }
        else
        {
            versionIDAsObject = new NSData(ERXStringUtilities.hexStringToByteArray(versionID));
        }
        try
        {
            SectionVersion sectionVersion = (SectionVersion) EOUtilities.objectWithPrimaryKeyValue(ec, "SectionVersion", versionIDAsObject);
            Section section = sectionVersion.section();

            User localUser = (User) EOUtilities.localInstanceOfObject(ec, ((Session) session()).currentUser());

            DebugOut.println(30, "section = " + section.name());
            DebugOut.println(30, "verson = " + sectionVersion.versionNumber());

            // Prevent unauthorized access
            if (!(section.website().canBeConfiguredByUser(localUser) || section.isEditor(localUser) || (section.isVersioning().booleanValue() && section
                    .isContributor(localUser))))
            {
                ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage");
                errorPage.setMessage("Access refused.");
                errorPage.setReason("You do not have permission to edit this section.");
                return errorPage;
            }

            // Don't allow contributors access to edit the current version
            if ((section.isVersionable() && section.isContributor(localUser) && sectionVersion.isCurrent())
                    && (!(section.website().canBeConfiguredByUser(localUser) || section.isEditor(localUser))))
            {
                VersionMgtPage nextPage = (VersionMgtPage) pageWithName("VersionMgtPage");
                nextPage.setSection(section);
                nextPage.setWebsite(section.website());
                nextPage.setShouldShowUneditableWarning(true);
                nextPage.setReturnUrl(returnUrl);

                return nextPage;
            }
            else
            {
                // Public users get the insecure EditSectionPublicly page, authenticated users get the standard EditSection
                EditSectionPublicly nextPage = (EditSectionPublicly) pageWithName(localUser.isPublicUser() ? "EditSectionPublicly" : "EditSection");
                nextPage.setSection(section);
                nextPage.setWebsite(section.website());
                nextPage.setReturnUrl(returnUrl);
                if (section.isVersionable() && section.isVersioning().isTrue())
                {
                    nextPage.setVersion(sectionVersion);
                }
                return nextPage;
            }
        }
        catch (EOUtilities.MoreThanOneException e)
        {
            return SectionDisplay.invalidParametersError(context());
        }
        catch (EOObjectNotAvailableException e)
        {
            return SectionDisplay.invalidParametersError(context());
        }

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns a script of links list composed of the passed Website's sections, its uploaded files and links from a configuration file.  The list of links returned uses the template of the component mapped to URLLinkScript in the configuration file.
     *
     * @return  a script of links list of links composed of the passed Website's sections, its uploaded files and links from a configuration file
     */
    public WOActionResults insertableLinksListAction()
    {
        WOActionResults actionResult;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String sectionName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_KEY);

        if (siteID == null)
        {
            actionResult = SectionDisplay.invalidParametersError(context());
        }
        else
        {
            Website site = Website.websiteForSiteID(siteID, session().defaultEditingContext());
            // Handle reference to site that does not exist.
            if (site == null)
            {
                actionResult = SectionDisplay.noSuchSiteError(siteID, context());
            }
            else if (sectionName == null)
            {
                DebugOut.println(1, "No section found for " + sectionName);
                actionResult = SectionDisplay.noSuchSectionError(siteID, sectionName, context());
            }
            else
            {
                Section section = site.sectionForNormalizedName(URLUtils.toLowerAndNormalize(sectionName));

                if (((Session) session()).isUserAuthenticated()
                        && (site.canBeConfiguredByUser(((Session) session()).currentUser()) || section.isContributor(((Session) session()).currentUser()) || section
                                .isEditor(((Session) session()).currentUser())))
                {
                    NSMutableArray allLinks = new NSMutableArray(site.orderedSections());
                    allLinks.addObjectsFromArray(site.orderedUploadedFilesViewableByUser(((Session) session()).currentUser()));
                    allLinks.addObjectsFromArray(((SMApplication) WOApplication.application()).properties().arrayPropertyForKey("RTESelectableConfigLinks"));

                    URLLinkScript script = (URLLinkScript) pageWithName(((SMApplication) WOApplication.application()).properties().stringPropertyForKey(
                            "URLLinkScript"));
                    script.setLinkList(allLinks);
                    script.setLinkListName(((SMApplication) WOApplication.application()).properties().stringPropertyForKey("LinkListVariableName"));
                    actionResult = script;
                }
                else
                {
                    actionResult = SMAuthComponent.redirectToLogin(context(), "Authentication is required to access this list.");
                }
            }
        }

        return actionResult;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns a script of links list composed of the passed Website's image files that are accessible by the logged in user.  The list of links returned uses the template of the component mapped to URLLinkScript in the configuration file.
     *
     * @return  a script of links list composed of the passed Website's image files that are accessible by the logged in user
     */
    public WOActionResults insertableImagesListAction()
    {
        WOActionResults actionResult;

        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String sectionName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_KEY);

        // Handle missing site ID
        if (siteID == null)
        {
            actionResult = SectionDisplay.invalidParametersError(context());
        }
        else
        {
            Website site = Website.websiteForSiteID(siteID, session().defaultEditingContext());
            // Handle reference to site that does not exist.
            if (site == null)
            {
                actionResult = SectionDisplay.noSuchSiteError(siteID, context());
            }
            else if (sectionName == null)
            {
                DebugOut.println(1, "No section found for " + sectionName);
                actionResult = SectionDisplay.noSuchSectionError(siteID, sectionName, context());
            }
            else
            {
                Section section = site.sectionForNormalizedName(URLUtils.toLowerAndNormalize(sectionName));

                if (((Session) session()).isUserAuthenticated()
                        && (site.canBeConfiguredByUser(((Session) session()).currentUser()) || section.isContributor(((Session) session()).currentUser()) || section
                                .isEditor(((Session) session()).currentUser())))
                {
                    URLLinkScript script = (URLLinkScript) pageWithName(((SMApplication) WOApplication.application()).properties().stringPropertyForKey(
                            "URLLinkScript"));
                    script.setLinkList(site.orderedImageFilesViewableByUser(((Session) session()).currentUser()));
                    script.setLinkListName(((SMApplication) WOApplication.application()).properties().stringPropertyForKey("ImageListVariableName"));
                    actionResult = script;
                }
                else
                {
                    actionResult = SMAuthComponent.redirectToLogin(context(), "Authentication is required to access this list.");
                }
            }

        }

        return actionResult;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the page displaying the indicated section for the site indicated in the URL.
     *
     * @param site - the siteID of the site to display a section from, passed in on URL.
     * @param embeddedsite - if non-null indicates that this is an embedded section, passed in on URL.
     * @param section - the name of the section to display, passed in on URL.  If embeddedsite is not null this section is taken from the site identified by that ID, otherwise it is taken from the site identified by site.
     *
     * @return the page to display for siteID and sectionName.
     */
    public WOActionResults displayVersionAction()
    {
        String siteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SITE_KEY);
        String embeddedSiteID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.EMBEDDED_SITE_KEY);
        String sectionName = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_KEY);
        String versionNumber = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.VERSION_NUMBER);
        String secureID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECURE_ID_KEY);

        return SectionDisplay.displaySection(siteID, embeddedSiteID, sectionName, versionNumber, secureID, context());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the VersionMgtPage if currently logged in user has rights, error page otherwise
     *
     * @return the VersionMgtPage if currently logged in user has rights, error page otherwise
     */
    public WOActionResults viewVersionsAction()
    {
        // If the user is authenticated, be sure were are in secure mode
        // We do not want to be in HTTPS for publicly edited sections as this will
        // trigger Cosign and other external Web SSO services
        if (((Session) session()).isUserAuthenticated() && !RequestUtils.isHTTPSRequest(request()))
        {
            WOComponent redirect = pageWithName("WORedirect");
            ((WORedirect) redirect).setUrl(SMActionURLFactory.secureProtocol() + RequestUtils.hostNameFromRequest(request())
                    + RequestUtils.originalURLFromRequest(request()));
            return redirect;
        }

        String returnUrl = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.URL_KEY);
        String sectionID = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SECTION_ID_KEY);
        String showWarning = (String) RequestUtils.cleanedFormValueForKey(request(), SMActionURLFactory.SHOW_WARNING_KEY);

        EOEditingContext ec = ((Session) session()).newEditingContext();

        DebugOut.println(30, "returnUrl = " + returnUrl);
        DebugOut.println(30, "sectionID = " + sectionID);

        Object sectionIDAsObject;
        if (SMApplication.smApplication().isUsingIntegerPKs())
        {
            sectionIDAsObject = new Integer(sectionID);
        }
        else
        {
            sectionIDAsObject = new NSData(ERXStringUtilities.hexStringToByteArray(sectionID));
        }
        try
        {
            Section section = (Section) EOUtilities.objectWithPrimaryKeyValue(ec, "Section", sectionIDAsObject);
            User localUser = (User) EOUtilities.localInstanceOfObject(ec, ((Session) session()).currentUser());

            DebugOut.println(30, "section = " + section.name());

            // Prevent unauthorized access
            if (!(section.website().canBeConfiguredByUser(localUser) || section.isEditor(localUser) || section.isContributor(localUser)))
            {
                ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage");
                errorPage.setMessage("Access refused.");
                errorPage.setReason("You do not have permission to view this page.");
                return errorPage;
            }

            VersionMgtPage nextPage = (VersionMgtPage) pageWithName("VersionMgtPage");
            nextPage.setSection(section);
            nextPage.setWebsite(section.website());
            nextPage.setReturnUrl(returnUrl);

            if ((showWarning != null) && (showWarning.equals("Y")))
            {
                nextPage.setShouldShowUneditableWarning(true);
            }

            return nextPage;
        }
        catch (EOUtilities.MoreThanOneException e)
        {
            return SectionDisplay.invalidParametersError(context());
        }
        catch (EOObjectNotAvailableException e)
        {
            return SectionDisplay.invalidParametersError(context());
        }

        /** ensure [result_not_null] Result != null; **/
    }



    /* Generic setters and getters ***************************************/


}
