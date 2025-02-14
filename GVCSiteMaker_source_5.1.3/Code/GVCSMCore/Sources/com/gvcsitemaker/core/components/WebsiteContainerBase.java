// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;


/**
 * This is a default implementation of WebsiteContainer that can be used as a super-class.
 */
public class WebsiteContainerBase extends net.global_village.woextensions.WOComponent implements WebsiteContainer 
{
    protected Website containedWebsite;
  

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
    static public void getWebsiteFromPageCreating(WebsiteContainer websiteContainer)
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
                websiteContainer.setWebsite(creatingContainer.website());
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



    /**
     * Designated constructor.  Sets website to the website of the WOComponent creating this object if that WOComponent is also a WebsiteContainer.  See #getWebsiteFromPageCreating(WebsiteContainer). Otherwise website() is null and must be set some other way (e.g. external call to setWebsite(Website) ).
     */
    public WebsiteContainerBase(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
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
     * Sets the Website to be displayed / edited.
     *
     * @param the Website to be displayed / edited
     */
    public void setWebsite(Website newWebsite)
    {
        /** require [new_website_not_null] newWebsite != null; **/

        containedWebsite = newWebsite;

        /** ensure [website_not_null] website() != null; **/
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



}
