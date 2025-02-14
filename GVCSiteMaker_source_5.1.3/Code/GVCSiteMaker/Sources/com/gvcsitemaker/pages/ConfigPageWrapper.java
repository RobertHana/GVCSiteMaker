// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.appserver.navigation.*;


public class ConfigPageWrapper extends net.global_village.woextensions.WOComponent
{
    public boolean hasSite;
    public String pageTitle;
    public Website website;

    protected Website containedWebsite;


    public ConfigPageWrapper(WOContext context)
    {
        super(context);
    }



    public NSKeyValueCoding navigationContext()
    {
        NSKeyValueCoding context = (NSKeyValueCoding)session().objectForKey("navigationContext");

        //log.debug(ERXNavigationManager.manager().navigationStateForSession(session()));
        if(context == null) {
            context = new NSMutableDictionary();
            session().setObjectForKey(context, "navigationContext");
        }
        ERXNavigationManager.manager().navigationStateForSession(session());
        // log.debug("NavigationState:" + state + "," + state.state() + "," + state.stateAsString());
        //log.info("navigationContext:" + session().objectForKey("navigationContext"));
        return context;
    }   



}
