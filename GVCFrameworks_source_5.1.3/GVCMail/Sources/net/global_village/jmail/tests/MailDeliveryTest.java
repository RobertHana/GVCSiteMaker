package net.global_village.jmail.tests;

import junit.framework.TestCase;
import net.global_village.jmail.MailDelivery;
import net.global_village.jmail.Message;

/**
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class MailDeliveryTest extends  TestCase
{


    public MailDeliveryTest(String name)
    {
        super(name);
    }


    /**
     * Sets up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
    }



    /**
     * Test internetAddresses and emailAddresses to ensure that all elements are maintained and the process is reversible..
     */
    public void testSendImmediate()
    {
        try
        {
            MailDelivery mailDelivery = new MailDelivery(AllTestsSuite.TEST_SERVER);
            Message testMessage = mailDelivery.newMessage();
            testMessage.setToAddress(AllTestsSuite.TEST_ACCOUNT);
            testMessage.setFromAddress(AllTestsSuite.TEST_ACCOUNT);
            testMessage.setReplyToAddress(AllTestsSuite.TEST_ACCOUNT);
            testMessage.setSubject((new java.rmi.server.UID()).toString());
            testMessage.setText("This is a Test Message.");

            mailDelivery.sendImmediate(testMessage);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }


}
