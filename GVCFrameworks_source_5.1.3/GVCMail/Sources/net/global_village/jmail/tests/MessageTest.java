package net.global_village.jmail.tests;

import javax.mail.internet.AddressException;

import junit.framework.TestCase;
import net.global_village.jmail.MailDelivery;
import net.global_village.jmail.Message;

import com.webobjects.foundation.NSArray;

/**
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class MessageTest extends  TestCase
{
    protected String emailAddress1 = "someone.1@global-village.net";
    protected String emailAddress2 = "someone.2@global-village.net";
    protected String emailAddress3 = "someone.3@global-village.net";
    protected String emailAddress4 = "someone.4@global-village.net";
    protected NSArray oneAddressArray = new NSArray(emailAddress1);
    protected NSArray threeAddressArray = new NSArray(new Object[] {emailAddress1, emailAddress2, emailAddress3});


    
    public MessageTest(String name)
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
     * Tests "to" related methods to show that they are cumulative and that the mutators affect the accessor.
     */
    public void testToAddress()
    {
        try
        {
            Message testMessage = (new MailDelivery(AllTestsSuite.TEST_SERVER)).newMessage();
            testMessage.setToAddress(emailAddress1);
            assertTrue(testMessage.toAddresses().containsObject(emailAddress1));
            testMessage.setToAddress(emailAddress2);
            assertTrue(testMessage.toAddresses().containsObject(emailAddress1));
            assertTrue(testMessage.toAddresses().containsObject(emailAddress2));

            testMessage = (new MailDelivery(AllTestsSuite.TEST_SERVER)).newMessage();
            testMessage.setToAddresses(threeAddressArray);
            assertTrue(testMessage.toAddresses().equals(threeAddressArray));
            testMessage.setToAddress(emailAddress4);
            assertTrue(testMessage.toAddresses().containsObject(emailAddress1));
            assertTrue(testMessage.toAddresses().containsObject(emailAddress2));
            assertTrue(testMessage.toAddresses().containsObject(emailAddress3));
            assertTrue(testMessage.toAddresses().containsObject(emailAddress4));
        }
        catch (AddressException e)
        {
            fail("Malformed e-mail address: " + e.getMessage());
        }

    }




}
