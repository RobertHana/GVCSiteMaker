// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;


/**
 * SiteMaker logout page.  There is no template or HTML page associated with logging out.  The user is logged out and the next page shown is the main page.
 */
public class SMLogoutPage extends WOComponent
{
    
    private String destinationUrl;
    
    /**
     * Designated constructor
     */
    public SMLogoutPage(WOContext aContext)
    {
        super(aContext);
    }


    /**
     * Returns the URL to show after logout.  This can be set explicitly, or will default to
     * the URL to the main page.
     * 
     * @param aContext WOContext to determine if a page is being viewed
     * @return URL to display after logout is complete
     */
    public String destinationUrl(WOContext aContext)
    {
        /** require [valid_context] aContext != null;  **/
        
        if (destinationUrl() == null)
        {
            setDestinationUrl(((SMApplication)application()).mainPageURL());
        }

         // If using external authentication and there is a logout URL specified, redirect there
        if (SMApplication.appProperties().booleanPropertyForKey(SMApplication.UsesExternalAuthenticationKey)
            && (SMApplication.appProperties().hasPropertyForKey(SMApplication.ExternalAuthenticationLogoutURLKey)))
        {
            // Append the destination to the logout URL: This depends on the external service handling this 
            // and on ExternalAuthenticationLogoutURLKey being property configured
            setDestinationUrl(SMApplication.appProperties().stringPropertyForKey(SMApplication.ExternalAuthenticationLogoutURLKey) + destinationUrl());
        }
        return destinationUrl();
        /** ensure [valid_result] Result != null;   **/
    }

    
    
    /**
     * Logs out the user (expires the session) and returns a redirect to destinationUrl().
     */
    public void appendToResponse(WOResponse aResponse,
                                 WOContext aContext)
    {
        // Cleanup and expire the session
        ((SMSession)session()).logout(aResponse);

        WORedirect redirect = (WORedirect)pageWithName("WORedirect");
        redirect.setUrl(destinationUrl(aContext));
        redirect.appendToResponse(aResponse, aContext);
    }

    /* Generic setters and getters ***************************************/

    public void setDestinationUrl(String newDestination) {
        destinationUrl = newDestination;
    }
    
    public String destinationUrl() {
        return destinationUrl;
    }
}
