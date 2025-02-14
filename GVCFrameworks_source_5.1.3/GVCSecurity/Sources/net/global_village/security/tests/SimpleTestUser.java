package net.global_village.security.tests;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * NOTE: In order to run the tests, the GVCTestEOModelBase framework must be linked into the app
 * as that framework contains the EOModel for the SimpleTestUser.
 *
 * @author Copyright (c) 2001-6  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class SimpleTestUser extends net.global_village.security.User
{
    private static final long serialVersionUID = 9002106634816724836L;

    protected String simpleAttribute;
    protected NSMutableArray toUsersGroups;


    public String simpleAttribute() {
        willRead();
        return simpleAttribute;
    }

    public void setSimpleAttribute(String value) {
        willChange();
        simpleAttribute = value;
    }

    public NSArray toUsersGroups() {
        willRead();
        return toUsersGroups;
    }

    public void setToUsersGroups(NSMutableArray value) {
        willChange();
        toUsersGroups = value;
    }

    public void addToToUsersGroups(EOCustomObject object) {
        willChange();
        toUsersGroups.addObject(object);
    }

    public void removeFromToUsersGroups(EOCustomObject object) {
        willChange();
        toUsersGroups.removeObject(object);
    }
}
