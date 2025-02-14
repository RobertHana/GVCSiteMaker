// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;


public class HeaderSub extends WebsiteContainerBase implements WebsiteContainer
{
    public String pageType;


    /**
     * Designated constructor.
     */
     public HeaderSub(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Logs the user out and returns the LogoutPage.
     *
     * @return the LogoutPage.
     *
     * post return not null
     */
    public WOComponent logoutPage()
    {
        return pageWithName("LogoutPage");
    }



}
