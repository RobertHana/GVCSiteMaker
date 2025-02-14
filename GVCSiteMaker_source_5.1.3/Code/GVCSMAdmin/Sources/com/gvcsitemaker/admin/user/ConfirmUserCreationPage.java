// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.user;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class ConfirmUserCreationPage extends WOComponent implements SMSecurePage
{

    protected NSArray _userArray, _validUserArray, _invalidUserArray;


    public ConfirmUserCreationPage(WOContext aContext)
    {
        super(aContext);
    }



    public WOComponent cancel() {
        return pageWithName("CreateUserPage");
    }

    public WOComponent create() {
        User.createUsersFromArrayOfUserDictionaries( validUserArray(),
                                                     session().defaultEditingContext() );
        UserCreationReportPage nextPage = (UserCreationReportPage)pageWithName("UserCreationReportPage");
        nextPage.setUserArray(getUserArray());

        return nextPage;
    }

    public NSArray validUserArray() {
        if( _validUserArray != null )
            return _validUserArray;

        NSDictionary dict = ArrayExtras.splitArrayBasedOnKey( getUserArray(), User.ERROR_MESSAGE_KEY);
        _validUserArray = (NSArray)dict.objectForKey("valid");
        _invalidUserArray = (NSArray)dict.objectForKey("invalid");

        return _validUserArray;
    }

    public NSArray invalidUserArray() {
        if( _invalidUserArray != null )
            return _invalidUserArray;

        NSDictionary dict = ArrayExtras.splitArrayBasedOnKey( getUserArray(), User.ERROR_MESSAGE_KEY);
        _validUserArray = (NSArray)dict.objectForKey("valid");
        _invalidUserArray = (NSArray)dict.objectForKey("invalid");
	
        return _invalidUserArray;
    }


    /* ********** Generic setters and getters ************** */
    public NSArray getUserArray() {
        return _userArray;
    }

    public void setUserArray( NSArray newUserArray ) {
        _userArray = newUserArray;
    }

}
