// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.user;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class InvalidUserList extends WOComponent
{

    public NSArray invalidUserArray;
    public NSDictionary currentUserDictionary;


    public InvalidUserList(WOContext aContext)
    {
        super(aContext);
    }



    public String getUserID() {
        String userID = (String)currentUserDictionary.objectForKey( User.USER_ID_KEY );

        if( userID == null || userID.length() < 1 )
            return "<I>(unspecified)</I>";
        else
            return userID;
    }

    public String errorString() {
        NSDictionary errorDict = (NSDictionary)currentUserDictionary.objectForKey( User.ERROR_MESSAGE_KEY );
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
