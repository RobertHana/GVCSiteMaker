// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.user;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class ValidUserList extends WOComponent
{

    public NSArray validUserArray;
    public NSDictionary currentUserDictionary;

    
    public ValidUserList(WOContext aContext)
    {
        super(aContext);
    }

}
