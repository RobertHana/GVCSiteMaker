// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.utility.appserver;

import com.gvcsitemaker.custom.appserver.*;
import com.webobjects.appserver.*;


public class Application extends SMCustomApplication
{
    

    public static void main(String argv[])
    {
        WOApplication.main(argv, Application.class);
    }



    public Application()
    {
        super();

        // This conversions easier as they do not time out:
		System.setProperty("WODirectConnectEnabled", Boolean.TRUE.toString());
    }



    /**
     * Loads the application configuration information.
     */
   protected void loadConfigurationFiles()
   {
       super.loadConfigurationFiles();

       // For conversion
       properties().addPropertiesFromFile("GVCSiteMakerBootstrap.plist", "GVCSMCore");
       properties().addPropertiesFromFile("GVCSiteMakerBootstrap.plist", "GVCSMCustom");
   }



    /**
     * Returns the prefix for PageMap.list for this application.  We can not use name() as this is whatever is setup in Monitor and we don't want to go around renaming config files when the app name in Monitor changes.
     *
     * @return the prefix for PageMap.list for this application.
     *
     * @post return != null
     */
    public String pageMapPrefix()
    {
        return "GVCSiteMaker";
    }



}
