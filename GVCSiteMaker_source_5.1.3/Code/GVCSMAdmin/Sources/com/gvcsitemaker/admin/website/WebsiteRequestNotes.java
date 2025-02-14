// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;


/** 
 * Simple page / popup window showing the original site request notes and subsequent admin notes.
 */
public class WebsiteRequestNotes extends WOComponent 
{

    protected WebsiteRequest websiteRequest;


    /**
     * Designated constructor.
     */
    public WebsiteRequestNotes(WOContext context) 
    {
        super(context);
    }


    /**
     * Call this to setup this page for a specific WebsiteRequest.
     *  
     * @param aWebsiteRequest the WebsiteRequest to display notes for
     */
    public void showNotesFor(WebsiteRequest aWebsiteRequest)
    {
        /** require [valid_parameter] aWebsiteRequest != null;   **/
        websiteRequest = aWebsiteRequest;
        /** ensure [websiteRequest_set] websiteRequest != null;   **/
    }



    /**
     * Returns aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes.
     * 
     * @return aWebsiteRequest().administrativeNotes() with line breaks converted to HTML codes
     */
    public String formattedAdminNotes()
    {
        return HTMLFormatting.replaceFormattingWithHTML(websiteRequest().administrativeNotes());
    }


    /* Generic setters and getters ***************************************/

    public WebsiteRequest websiteRequest() {
        return websiteRequest;
    }

}
