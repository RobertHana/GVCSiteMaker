package net.global_village.jmail.tests;

import javax.mail.internet.*;

import junit.framework.TestCase;

import com.webobjects.foundation.NSArray;

import net.global_village.jmail.EmailAddress;

/**
 * Describe class here.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EmailAddressTest extends TestCase
{
    protected String emailAddress1 = "someone.1@global-village.net";
    protected String emailAddress2 = "someone.2@global-village.net";
    protected String emailAddress3 = "someone.3@global-village.net";
    protected String emailAddress4 = "someone.4@global-village.net";
    protected NSArray oneAddressArray = new NSArray(emailAddress1);
    protected NSArray threeAddressArray = new NSArray(new Object[] {emailAddress1, emailAddress2, emailAddress3});


    /**
     * Constructor for EmailAddressTest.
     * @param name
     */
    public EmailAddressTest(String name)
    {
        super(name);
    }



    /**
     * Test isValidEmailAddressFormat().
     */
    public void testIsValidEmailAddressFormat()
    {
        assertTrue(EmailAddress.isValidAddressFormat("john@smith.com"));
        assertTrue(EmailAddress.isValidAddressFormat("john.smith@john.smith.com"));
        assertTrue( ! EmailAddress.isValidAddressFormat("@smith.com"));
        assertTrue( ! EmailAddress.isValidAddressFormat("johnsmith.com"));
        assertTrue( ! EmailAddress.isValidAddressFormat("john@"));
        assertTrue( ! EmailAddress.isValidAddressFormat("john@smith@com"));
        assertTrue( ! EmailAddress.isValidAddressFormat("john@smith"));
        assertTrue( ! EmailAddress.isValidAddressFormat("john@smith."));
        assertTrue( ! EmailAddress.isValidAddressFormat("john@.smith"));
        assertTrue( ! EmailAddress.isValidAddressFormat("john@.smith."));
        assertTrue( ! EmailAddress.isValidAddressFormat("http://www.gvcsitemaker.com/me@home.com"));
    }
    
    
    
    /**
     * Test internetAddresses and emailAddresses to ensure that all elements are maintained and the process is reversible..
     */
    public void testInternetAddresses()
    {
        addressReversabilityTest(NSArray.EmptyArray);
        addressReversabilityTest(oneAddressArray);
        addressReversabilityTest(threeAddressArray);
    }



    /**
     * Helper method for testInternetAddresses
     */
    protected void addressReversabilityTest(NSArray arrayToTest)
    {
        try
        {
            InternetAddress[] resultArray = EmailAddress.internetAddresses(arrayToTest);
            assertTrue(arrayToTest.count() == resultArray.length);
            assertTrue(arrayToTest.equals(EmailAddress.emailAddresses(resultArray)));
        }
        catch (AddressException e)
        {
            fail("Malformed e-mail address: " + e.getMessage());
        }
    }


}
