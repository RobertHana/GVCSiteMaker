// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.pages;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.woextensions.*;



/**
 * This was added to handle the need to perform bootstrap like loading of style templates after the initial loading.  It creates a new style if it does not exist, and otherwise replaces the templates for the existing styles.  With a bit of GVCSiteMakerBootstrap.plist editing it could be used to bulk load styles after bootstrap.
 */
public class ReloadStyles extends WOComponent 
{


    public ReloadStyles(WOContext context)
    {
        super(context);
    }


    /**
     * This was only intended to be a one - off thing for UMich preconversion so things are a little rough and ready.
     */
    public WOComponent loadStyleTemplates()
    {
        ApplicationProperties properties = ((SMApplication)application()).properties();

        // Load the Bootstrap items and ensure they are present.
        DebugOut.println(1,"Loading bootstrap information.");
        properties.addPropertiesFromFile("GVCSiteMakerBootstrap.plist", "GVCSMCustom");

        if ( ! properties.hasPropertyForKey("SiteStyles"))
        {
            throw new RuntimeException("can not find SiteStyles in GVCSiteMakerBootstrap.plist");
        }
        else if ( ! (properties.propertyForKey("SiteStyles") instanceof NSDictionary))
        {
        throw new RuntimeException("SiteStyles property is not a properly formed dictionary, it is a " +
                                   properties.propertyForKey("SiteStyles").getClass());
        }
        else if ( ((NSDictionary)properties.propertyForKey("SiteStyles")).count() == 0)
        {
            throw new RuntimeException("SiteStyles has no styles defined");
        }
        else
        {
            NSDictionary styleDictionary = ((NSDictionary)properties.propertyForKey("SiteStyles"));
            Enumeration styleEnumeration = styleDictionary.keyEnumerator();
            while (styleEnumeration.hasMoreElements())
            {
                String styleName = (String)styleEnumeration.nextElement();
                String templateContents = ResourceManagerAdditions.stringFromResource("StyleTemplates/" + styleName + ".html", "GVCSMCustom");
                if (templateContents == null)
                {
                    throw new RuntimeException("Can not load style 'StyleTemplates/" + styleName + ".html' for style named " + styleName);
                }

                try
                {
                    SectionStyle style = (SectionStyle) EOUtilities.objectMatchingKeyAndValue(session().defaultEditingContext(),
                                                                                              "SectionStyle",
                                                                                              "styleID",
                                                                                              styleName);
                    style.setTemplate(templateContents);
                    DebugOut.println(1, "Updating style " + styleName);
                }
                catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
                {
                    // Handle style not previously loaded.
                    InitDatabase.loadStyle(OrgUnit.rootUnit(session().defaultEditingContext()),
                                           styleName,
                                           (String) styleDictionary.objectForKey(styleName));
                }
                session().defaultEditingContext().saveChanges();
            }
        }

        return pageWithName("Main");
    }


}
