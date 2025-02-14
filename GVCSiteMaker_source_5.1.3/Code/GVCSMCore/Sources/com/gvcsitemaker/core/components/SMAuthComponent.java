// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;


/**
 * <p>This class handles WOComponent level user authentication for components which can only be viewed 
 * by authenticated users.  It does not check that the authenticated user has any specific permission 
 * to view this component, only that they are authenticated.  If the user has not authenticated they 
 * are sent to the Login page and redirected to the originally requested URL once they log in.  
 * SMAuthComponent also provides some convenience methods for other classes needing to handle similar 
 * situations.</p>
 * 
 * <p>When using something like UM Cosign, the user will always be authenticated if they reach this
 * page in HTTPS mode.  All this page has to do is to ensure that the request is in HTTPS mode and 
 * the login page will never be shown.
 * </p>
 */
public class SMAuthComponent extends WOComponent implements SMSecurePage
{


    /**
     * Sets response to redirect to the login page and passes along the orignal URL so the login page can redirect back once the user has logged in.
     *
     * @param response - the response to change into a redirect
     * @param context - the WOContext to take the original URL from.
     * @param message - message to display on Login page to explain why the user needs to log in.
     */
    public static void redirectToLogin(WOResponse response, WOContext context, String message)
    {
        /** require [valid_response] response != null;  [valid_context] context != null; **/
        DebugOut.println(3,"WOResponse, WOContext, String: User NOT authenticated. Going to login page.");
        WORedirect loginRedirect = new WORedirect(context);
        String secureLoginURL = SMActionURLFactory.secureLoginURLForRequest(context.request(), message);
        DebugOut.println(3,"secureLoginURL is " + secureLoginURL);

        loginRedirect.setUrl(secureLoginURL);
        loginRedirect.appendToResponse(response, context);
    }



    /**
     * Returns a WORedirect to redirect to the login page and passes along the orignal URL so the login page can redirect back once the user has logged in.
     *
     * @param context - the WOContext to take the original URL from.
     * @param message - message to display on Login page to explain why the user needs to log in.
     */
    public static WORedirect redirectToLogin(WOContext context, String message)
    {
        /** require [valid_context] context != null;  **/

        DebugOut.println(3,"WOContext, String: User NOT authenticated. Going to login page.");
        WORedirect loginRedirect = new WORedirect(context);
        String secureLoginURL = SMActionURLFactory.secureLoginURLForRequest(context.request(), message);
        DebugOut.println(3,"secureLoginURL is " + secureLoginURL);

        loginRedirect.setUrl(secureLoginURL);
        return loginRedirect;

        /** ensure [valid_result]  Result != null;    **/
    }



    /**
     * Designated constructor
     */
    public SMAuthComponent(WOContext aContext)
    {
        super(aContext);
    }

    

    /**
     * Overridden to check if the user is authenticated and redirect them to the login page if not.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        // Now that we know we are in HTTPS, we can check for an authenticated user
        if ( ! ((SMSession)session()).isUserAuthenticated())
        {
            redirectToLogin(response, context, null);
        }
        else
        {
            super.appendToResponse(response, context);
            DebugOut.println(3,"User IS authenticated.");
        }
    }
    
    
    
    /** 
     * Overridden to handle direct action access to secure pages.  Checks if the request was made 
     * securely redirects securely if not.
     * 
     * @see com.webobjects.appserver.WOActionResults#generateResponse()
     */
    public WOResponse generateResponse() 
    {
        if ( ! RequestUtils.isHTTPSRequest(context().request()))
        {       
            DebugOut.println(1, "insecure direct action request for secure page detected, redirecting to https");

            WORedirect secureRedirect = new WORedirect(context());
            secureRedirect.setUrl(SMActionURLFactory.secureURLForRequest(context().request()));
            return secureRedirect.generateResponse();
        }
        else
        {
            return super.generateResponse();
        }

    }
}
