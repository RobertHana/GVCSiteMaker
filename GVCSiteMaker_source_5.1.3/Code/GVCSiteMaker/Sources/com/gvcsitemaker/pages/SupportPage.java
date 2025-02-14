// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;


public class SupportPage extends WebsiteContainerBase implements WebsiteContainer, SMSecurePage
{
    protected Website website;


    public SupportPage(WOContext context)
    {
        super(context);
    }



    public Website website()
    {
        return website;
    }



    public void setWebsite(Website newWebsite)
    {
        website = newWebsite;
    }



}
