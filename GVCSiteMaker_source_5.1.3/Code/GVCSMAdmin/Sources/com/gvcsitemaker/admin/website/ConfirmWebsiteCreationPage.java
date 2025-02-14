// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class ConfirmWebsiteCreationPage extends WOComponent implements SMSecurePage
{

    protected NSArray _websiteArray, _validWebsiteArray, _invalidWebsiteArray;


    public ConfirmWebsiteCreationPage(WOContext aContext)
    {
        super(aContext);
    }



    public WOComponent cancel() {
        return pageWithName("CreateWebsitePage");
    }

    public WOComponent create() {
        Website.createWebsitesFromArrayOfWebsiteDictionaries( validWebsiteArray(),
                                                              session().defaultEditingContext() );
        WebsiteCreationReportPage nextPage = (WebsiteCreationReportPage)pageWithName("WebsiteCreationReportPage");
        nextPage.setWebsiteArray(getWebsiteArray());

        return nextPage;
    }

    public NSArray validWebsiteArray() {
        if( _validWebsiteArray != null )
            return _validWebsiteArray;

        NSDictionary dict = ArrayExtras.splitArrayBasedOnKey( getWebsiteArray(), Website.ERROR_MESSAGE_KEY);
        _validWebsiteArray = (NSArray)dict.objectForKey("valid");
        _invalidWebsiteArray = (NSArray)dict.objectForKey("invalid");

        return _validWebsiteArray;
    }

    public NSArray invalidWebsiteArray() {
        if( _invalidWebsiteArray != null )
            return _invalidWebsiteArray;

        NSDictionary dict = ArrayExtras.splitArrayBasedOnKey( getWebsiteArray(), Website.ERROR_MESSAGE_KEY);
        _validWebsiteArray = (NSArray)dict.objectForKey("valid");
        _invalidWebsiteArray = (NSArray)dict.objectForKey("invalid");
	
        return _invalidWebsiteArray;
    }


    /* ********** Generic setters and getters ************** */
    public NSArray getWebsiteArray() {
        return _websiteArray;
    }

    public void setWebsiteArray( NSArray newWebsiteArray ) {
        _websiteArray = newWebsiteArray;
    }


}
