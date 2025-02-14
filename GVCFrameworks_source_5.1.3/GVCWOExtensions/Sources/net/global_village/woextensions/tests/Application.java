package net.global_village.woextensions.tests;


/**
 * This class is added in this test package as a work-around for the tests to successfully run.
 * Without this class, an error "Class 'Session' exists (class javax.mail.Session) but is not a subclass
 * of WOSession", will be raised. The reason for this is that, when it tries to create a session for
 * these tests, it looks for any class named Application.
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.

 */
public class Application extends net.global_village.woextensions.WOApplication
{

    /**
     * Defines required properties with no defaults.
     */
    protected void loadConfigurationInfo()
    {
        super.loadConfigurationInfo();
        properties().addPropertyForKey("/cgi-bin/WebObjects", "WOAdaptorURL");
        properties().addPropertyForKey("smtp.local.host", "SMTPHost");
        properties().addPropertyForKey("FrontBase", "DBType");
        properties().addPropertyForKey("Main", "DefaultPageName");

    }


}
