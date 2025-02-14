package net.global_village.woextensions.tests;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * This class is added in this test package as a work-around for the menu tests to successfully run.  Without this class, an error "Class 'Session' exists (class javax.mail.Session) but is not a subclass of WOSession", will be raised. The reason for this is that, when it tries to create a session for these tests, it looks for any class named Session and since it doesn't have this class,  it uses javax.mail.Session, which is in Javamail.framework.  Therefor, this class is needed in this package so that the correct subclass of WOSession will be used. 
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class Session extends net.global_village.woextensions.WOSession
{
    public WOContext context()
    {
        if (super.context() == null) NSLog.out.appendln(new RuntimeException("Null Context"));
        return super.context();
    }
}
