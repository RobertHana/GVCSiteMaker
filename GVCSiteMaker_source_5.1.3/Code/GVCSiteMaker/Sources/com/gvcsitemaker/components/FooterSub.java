// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.components;

import com.gvcsitemaker.core.support.SMActionURLFactory;

import com.webobjects.appserver.WOContext;


public class FooterSub extends com.gvcsitemaker.core.components.WebsiteContainerBase
{


    public FooterSub(WOContext aContext)
    {
        super(aContext);
    }



    public String mailtoURL()
    {
        return "mailto:" + website().contactEmail();
    }


    public boolean hasSomeContactInfo()
    {
        if ((website().contactPhone() != null) || (website().contactEmail() != null) || (website().contactFax() != null) || (website().contactName() != null))
            return true;
        return false;
    }


    public boolean hasPhone()
    {
        if (website().contactPhone() != null)
            return true;
        return false;
    }


    public boolean hasEmail()
    {
        if (website().contactEmail() != null)
            return true;
        return false;
    }


    public boolean hasFax()
    {
        if (website().contactFax() != null)
            return true;
        return false;
    }


    public boolean hasName()
    {
        if (website().contactName() != null)
            return true;
        return false;
    }


    public boolean hasLinks()
    {
        if (website().footer().isLink1Visible() || website().footer().isLink2Visible() || website().footer().isLink3Visible()
                || website().footer().isLink4Visible())
            return true;
        return false;
    }


    public String configURL()
    {
        return SMActionURLFactory.configSiteURL(context().request(), website().siteID());
    }


    public String configSiteFileMgmtURL()
    {
        return SMActionURLFactory.configSiteFileMgmtURL(context().request(), website().siteID());
    }


    /* Generic setters and getters ***************************************/

}
