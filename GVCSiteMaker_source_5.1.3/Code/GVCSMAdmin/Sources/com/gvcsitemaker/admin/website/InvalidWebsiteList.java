// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class InvalidWebsiteList extends WOComponent
{

    public NSArray invalidWebsiteArray;
    public NSDictionary currentWebsiteDictionary;



    public InvalidWebsiteList(WOContext aContext)
    {
        super(aContext);
    }



    public String getSiteID() {
        String siteID = (String)currentWebsiteDictionary.objectForKey( Website.SITE_ID_KEY );

        if( siteID == null )
            return "<I>(unspecified)</I>";
        else
            return siteID;
    }

    public String errorString() {
        NSDictionary errorDict = (NSDictionary)currentWebsiteDictionary.objectForKey( OrgUnit.ERROR_MESSAGE_KEY );
        String errorMessage = "";
        Enumeration e = errorDict.objectEnumerator();

        if( !e.hasMoreElements() )
            return errorMessage;

        while( e.hasMoreElements() ) {
            errorMessage = errorMessage + "<BR>" + e.nextElement();
        }

        return errorMessage.substring(4);
    }

}
