// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.appserver;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.custom.appserver.*;
import com.webobjects.appserver.*;

public class DirectAction extends SMCustomDirectAction 
{


    public DirectAction(WORequest aRequest) {
        super(aRequest);
    }


    /**
     * Force all access to be via HTTPS.
     */
    public WOActionResults performActionNamed(String actionName)
    {
        WOActionResults result = null;
        
        if (! RequestUtils.isHTTPSRequest(request()))
        {
            WOComponent redirect = pageWithName("WORedirect");
            ((WORedirect)redirect).setUrl(SMActionURLFactory.secureProtocol() +
            RequestUtils.hostNameFromRequest(request()) + 
            RequestUtils.originalURLFromRequest(request()));
            result = redirect.generateResponse();
        }
        else result = super.performActionNamed(actionName);
        
        return result;
    }
        
        
        
    /**
     * Returns the LoginPage when a request is made for the default action.  This is because the 
     * SMAuthComponent method of protecting the Main page and ensuring login currently only works 
     * when cookies are used to store the session ID and instance number.  The default destination
     * of the LoginPage is the name named "Main".
     *
     * @return the LoginPage
     */
    public WOActionResults defaultAction()
    {
        return pageWithName("LoginPage");
    }



    /**
     * Returns the ManagePendingWebsiteRequests page. 
     *
     * @return the ManagePendingWebsiteRequests page
     */
    public WOActionResults managePendingRequestsAction()
    {
        return pageWithName("ManagePendingWebsiteRequests"); 
    }


}
