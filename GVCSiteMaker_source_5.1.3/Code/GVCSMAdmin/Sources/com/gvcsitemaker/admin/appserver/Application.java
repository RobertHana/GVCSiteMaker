// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.appserver;

import com.gvcsitemaker.custom.appserver.*;
import com.webobjects.appserver.*;


public class Application extends SMCustomApplication
{


    /**
     * Application bootstrap method.
     */
    public static void main(String argv[])
    {
        // Ensure that we get proper error pages when an exception happens in a direct action.
        // This must go here and not in the constructor or it won't work.
        System.setProperty("WODisplayExceptionPages", "true");
        WOApplication.main(argv, Application.class);
    }


    

    /**
     * Designated constructor.
     */
    public Application()
    {
        super();
    }


  
    /**
     * Returns the prefix for PageMap.list for this application.  We cannot use name() as this is whatever is setup in Monitor and we don't want to go around renaming config files when the app name in Monitor changes.
     *
     * @return the prefix for PageMap.list for this application.
     */
    public String pageMapPrefix()
    {
        return "GVCSMAdmin";

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * This is overridden in GVC.SMAdmin as there are three long running actions (copy website, delete website, send broadcast e-mail) that can also produce this error.  What happens is that they take so long to complete that the woadaptor sends the request to another instance which then does not recognise the session.  This page just provides some specific advice on what to do.  Once a WOLongResponsePage is used to handle these, this can be removed.
     */
    public WOResponse handleSessionRestorationErrorInContext(WOContext aContext)
    {
        return pageWithName("SessionRestorationErrorPage", aContext).generateResponse();
    }
}
