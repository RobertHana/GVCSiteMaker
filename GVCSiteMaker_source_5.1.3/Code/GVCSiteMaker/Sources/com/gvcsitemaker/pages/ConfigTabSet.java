// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


public class ConfigTabSet extends SMAuthComponent implements SMSecurePage, WebsiteContainer
{
    protected Website containedWebsite;

    protected final String defaultTab = "Tab1";
    protected String selectedTab;

    protected String previewURL;
    protected Group editingAccessGroup;


    /**
     * Sets the Website to be displayed / edited to be the website contained by the page creating this WebsiteContainer.  This can be used with any object implementing WebsiteContainer.  This method <b>ONLY</b> works when called from a constructor!<br>
     *
     * <code>
     * public SomeClassImplmentingWebsiteContainer()
     * {
     *     super(context);
     *     WebsiteContainerBase.getWebsiteFromPageCreating(this);
     * }
     * </code>
     *
     * @param object implementing WebsiteContianer
     */
    static public void getWebsiteFromPageCreating(WOComponent websiteContainer)
    {
        /** require [website_containter_not_null] websiteContainer != null; **/
        
        // While in a WOComponent constructor, context().page() returns the page creating this WOComponent instance, not the instance itself.
        WOComponent creatingPage = ((WOComponent) websiteContainer).context().page();

        if (creatingPage instanceof WebsiteContainer)
        {
            String creationMessage = websiteContainer.getClass() + " created by " + creatingPage.getClass();
            WebsiteContainer creatingContainer = (WebsiteContainer) creatingPage;

            if (creatingContainer.website() != null)
            {
                ((WebsiteContainer)websiteContainer).setWebsite(creatingContainer.website());
                DebugOut.println(19, creationMessage + ": setting website to " + creatingContainer.website().siteID());
            }
            else
            {
                DebugOut.println(19, creationMessage + ": creating page is WebsiteContainer but has null website, leaving website null");
            }
        }
        else
        {
            String creationMessage;
            if (creatingPage != null)
            {
                creationMessage = websiteContainer.getClass() + " created by " + creatingPage.getClass();
            }
            else
            {
                creationMessage = websiteContainer.getClass() + " created by DirectAction";
            }
            DebugOut.println(19, creationMessage + ": creating page is not WebsiteContainer, leaving website null");
        }
    }



    public ConfigTabSet(WOContext context)
    {
        super(context);
        getWebsiteFromPageCreating(this);
    }



    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        // The first time through the user may not be authenticated.  Don't do this until after login.
        if ( ((Session)session()).isUserAuthenticated())
        {
            if ( ! canUserConfigureCurrentWebsite())
            {
                ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
                errorPage.setMessage("Access Refused.");
                errorPage.setReason("You do not have permission to edit that website.");

                //errorPage.appendToResponse(aResponse, aContext);
                aResponse.setContent(errorPage.generateResponse().content());
            }
            else
            {
                super.appendToResponse(aResponse, aContext);
            }
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }
    }



    /**
     * String to pass into the dynamic body component for use as browser window title.
     *
     * @return string to pass into the dynamic body component for use as browser window title.
     */
    public String pageTitle() {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Main Configuration Page";
    }



    /**
     * Returns true if the logged in user has permissions to configure this Website.
     *
     * @return <code>true</code> if the logged in user has permissions to configure this Website.
     */
    public boolean canUserConfigureCurrentWebsite()
    {
        Session session = (Session)session();

        // if the user is a remote user, specified as an admin by the remote tool, and if the user is trying to edit
        // the site that was configured for remote access, then let them config it
        boolean isRemoteAdministrator = session.isRemoteParticipantSession() &&
                                        session.isRemoteAdministrator();
        boolean isEditingRemoteSite = session.isRemoteParticipantSession() &&
                                      session.authenticatedRemoteParticipantGroup().parentWebsite().equals(website());

        return (isRemoteAdministrator && isEditingRemoteSite) || website().canBeConfiguredByUser(session.currentUser());
    }


    /**
     * Depending on whether the user can configure the site, the header needs to be passed differented information through the dynamic body.
     */
    public boolean hasSite()
    {
        return (((Session) session()).currentUser() != null) && (canUserConfigureCurrentWebsite());
    }



    /**
     * Returns true iff the given tab is selected.
     *
     * @param the tab name to test
     * @return true iff the given tab is selected
     */
    protected boolean tabSelected(String tabName)
    {
        /** require [tabName_not_null] tabName != null; website() != null; **/

        // the first time through, grab the value from the session if there is one
        if (selectedTab == null)
        {
            Website localWebsite = (Website)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), website());

            NSMutableDictionary windowViews = smSession().windowViews();
            NSMutableDictionary configTabSetViewData = (NSMutableDictionary)windowViews.objectForKey("ConfigTabSet");
            if (configTabSetViewData == null)
            {
                selectedTab = defaultTab;
                return tabName.equals(selectedTab);
            }

            NSMutableDictionary specificWebsiteConfigTabSetViewData = (NSMutableDictionary)configTabSetViewData.objectForKey(localWebsite);
            if (specificWebsiteConfigTabSetViewData == null)
            {
                selectedTab = defaultTab;
                return tabName.equals(selectedTab);
            }

            selectedTab = (String)specificWebsiteConfigTabSetViewData.objectForKey("selectedTab");

            // if it is still null, use the default
            if (selectedTab == null)
            {
                selectedTab = defaultTab;
            }
        }

        return tabName.equals(selectedTab);
    }


    /**
     * Sets the tab to be selected.
     *
     * @param the tab name to set as selected
     */
    public void setTabSelected(String tabName)
    {
        /** require [tabName_not_null] tabName != null; **/

        selectedTab = tabName;

        // set the selected tab into the session
        NSMutableDictionary windowViews = smSession().windowViews();
        NSMutableDictionary configTabSetViewData = (NSMutableDictionary)windowViews.objectForKey("ConfigTabSet");
        if (configTabSetViewData == null)
        {
            Website localWebsite = (Website)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), website());
            windowViews.setObjectForKey(
                    new NSMutableDictionary(
                            new NSMutableDictionary(tabName, "selectedTab"),
                            localWebsite),
                    "ConfigTabSet");
        }
        else
        {
            Website localWebsite = (Website)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), website());
            NSMutableDictionary specificWebsiteConfigTabSetViewData = (NSMutableDictionary)configTabSetViewData.objectForKey(localWebsite);
            if (specificWebsiteConfigTabSetViewData == null)
            {
                configTabSetViewData.setObjectForKey(
                        new NSMutableDictionary(tabName, "selectedTab"),
                        localWebsite);
            }
            else
            {
                specificWebsiteConfigTabSetViewData.setObjectForKey(tabName, "selectedTab");
            }
        }
    }



    /**
     * Returns true iff tab 1 is selected.
     *
     * @return true iff tab 1 is selected
     */
    public boolean tab1Selected()
    {
        return tabSelected("Tab1");
    }


    /**
     * Sets the tab to be selected.
     *
     * @param isSelected true iff tab 1 is to be selected
     */
    public void setTab1Selected(boolean isSelected)
    {
        if (isSelected)
        {
            setTabSelected("Tab1");
        }
    }



    /**
     * Returns true iff tab 2 is selected.
     *
     * @return true iff tab 2 is selected
     */
    public boolean tab2Selected()
    {
        return tabSelected("Tab2");
    }


    /**
     * Sets the tab to be selected.
     *
     * @param isSelected true iff tab 2 is to be selected
     */
    public void setTab2Selected(boolean isSelected)
    {
        if (isSelected)
        {
            setTabSelected("Tab2");
        }
    }



    /**
     * Returns true iff tab 3 is selected.
     *
     * @return true iff tab 3 is selected
     */
    public boolean tab3Selected()
    {
        return tabSelected("Tab3");
    }


    /**
     * Sets the tab to be selected.
     *
     * @param isSelected true iff tab 3 is to be selected
     */
    public void setTab3Selected(boolean isSelected)
    {
        if (isSelected)
        {
            setTabSelected("Tab3");
        }
    }



    /**
     * Returns true iff tab 4 is selected.
     *
     * @return true iff tab 4 is selected
     */
    public boolean tab4Selected()
    {
        return tabSelected("Tab4");
    }


    /**
     * Sets the tab to be selected.
     *
     * @param isSelected true iff tab 4 is to be selected
     */
    public void setTab4Selected(boolean isSelected)
    {
        if (isSelected)
        {
            setTabSelected("Tab4");
        }
    }



    /**
     * Returns true iff tab 5 is selected.
     *
     * @return true iff tab 5 is selected
     */
    public boolean tab5Selected()
    {
        return tabSelected("Tab5");
    }


    /**
     * Sets the tab to be selected.
     *
     * @param isSelected true iff tab 5 is to be selected
     */
    public void setTab5Selected(boolean isSelected)
    {
        if (isSelected)
        {
            setTabSelected("Tab5");
        }
    }



    /**
     * Returns true iff tab 6 is selected.
     *
     * @return true iff tab 6 is selected
     */
    public boolean tab6Selected()
    {
        return tabSelected("Tab6");
    }


    /**
     * Sets the tab to be selected.
     *
     * @param isSelected true iff tab 6 is to be selected
     */
    public void setTab6Selected(boolean isSelected)
    {
        if (isSelected)
        {
            setTabSelected("Tab6");
        }
    }



    /**
     * Sets the Website to be displayed / edited.
     *
     * @param the Website to be displayed / edited
     */
    public void setWebsite(Website newWebsite)
    {
        /** require [new_website_not_null] newWebsite != null; **/

        containedWebsite = newWebsite;
        setPreviewURL(newWebsite.siteURL());

        /** ensure [website_not_null] website() != null; **/
    }


    /**
     * Returns the Website being displayed / edited.  It may be possible for this to be null.
     *
     * @return the Website being displayed / edited
     */
    public Website website()
    {
        return containedWebsite;
    }



    /**
     * Returns the session cast to an SMSession.
     *
     * @return the session cast to an SMSession
     */
    public SMSession smSession()
    {
        return (SMSession)session();
    }



    public String previewURL()
    {
        return previewURL;
    }


    public void setPreviewURL(String newPreviewURL)
    {
        previewURL = newPreviewURL;
    }


    public Group editingAccessGroup()
    {
        return editingAccessGroup;
    }


    public void setEditingAccessGroup(Group newEditingAccessGroup)
    {
        editingAccessGroup = newEditingAccessGroup;
    }


}
