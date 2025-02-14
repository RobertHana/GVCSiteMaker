// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.appserver;


import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.custom.appserver.*;
import com.webobjects.appserver.*;

import er.extensions.appserver.navigation.*;
import er.extensions.components._private.*;
import er.extensions.foundation.*;

public class Application extends SMCustomApplication 
{


    /**
     * Applicaton bootstrap method
     */
    public static void main(String argv[])
    {
        // Ensure that we get proper error pages when an exception happens in a direct action.
        // This must go here and not in the constructor or it won't work.
        System.setProperty("WODisplayExceptionPages", "true");
        WOApplication.main(argv, Application.class);
    }


    
    public Application()
    {
        super();

        // Use this to accommodate changes in the collection being iterated
        ERXPatcher.setClassForName(ERXWORepetition.class, "WORepetition");
    }



    /**
     * For the GVCSiteMaker application, special handling is provided when aPageName is Main or null and MainPageSiteID has been defined in the configuration files.  In this case the page returned is the Home Section of the Site ID associated with MainPageSiteID.  This allows the Main page to be a GVC.SiteMaker site.
     *
     * @param aPageName the name of the page to return.
     * @param aContext the WOContext to create this page in
     * @return an instance of the page requested, or of another page if one has been mapped to this name.
     */
    public WOComponent pageWithName(String aPageName, WOContext aContext)
    {
        /** require  [page_map_not_null] pageMap() != null;   [a_context_not_null] aContext != null; **/
        
        WOComponent resultPage = null;

        if (properties.hasPropertyForKey("MainPageSiteID") &&
            ( (aPageName == null) || aPageName.equalsIgnoreCase("main") ) )
        {

            resultPage = SectionDisplay.displaySection( properties.stringPropertyForKey("MainPageSiteID"),
                                                        null,  // Embedded Site ID
                                                        null,  // Home section
                                                        null,
                                                        null,
                                                        aContext);
        }
        else
        {
            resultPage = super.pageWithName(aPageName, aContext);
        }
        
        return resultPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the prefix for PageMap.list for this application.  We cannot use name() as this is whatever is setup in Monitor and we don't want to go around renaming config files when the app name in Monitor changes.
     *
     * @return the prefix for PageMap.list for this application.
     */
    public String pageMapPrefix()
    {
        return "GVCSiteMaker";

        /** ensure [result_not_null] Result != null; **/
    }

    

    /**
     * Overidden to handle requests with expired session made while refusing new sessions.  See responseForRefusedRequest() for details.  This is here, rather than in GVCSMCore as it does not work for session IDs stored in the URL which is what GVCSMAdmin uses.
     */
    public WOResponse dispatchRequest(WORequest request)
    {
        WOResponse result;
        // Don't handle requests with expired sessions when refusing sessions.  
        // Requests where request.sessionID() == null are probably from JavaMonitor 
        // and should be processed normally.
        String sessionID = request.sessionID(); 
        if (isRefusingNewSessions() &&
            (sessionID != null) &&
            (sessionStore().restoreSessionWithID(sessionID, request) == null))
        {
            DebugOut.println(1, "Refusing new Sessions and request has session ID that is expired or from foreign instance: " + request.sessionID());
            DebugOut.println(1, "Requested uri: " + request.uri());
            result = responseForRefusedRequest();
        }
        else
        {
            result = super.dispatchRequest(request);
        }

        return result;
    }



    /**
     * Overidden to init the ERXNavigationMenu
     */
    protected void loadConfigurationFiles()
    {
        super.loadConfigurationFiles();
        ERXNavigationManager.manager().configureNavigation();
    }



}
