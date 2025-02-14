package net.global_village.jmail.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Contains all tests.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class AllTestsSuite extends TestSuite
{

    public static final String MAIL_SERVER_KEY = "MailServer";
    public static final String POP_ACCOUNT_KEY = "POPAccount";
    public static final String MAIL_USER_KEY = "MailUser";
    public static final String MAIL_USER_PASSWORD_KEY = "MailUserPassword";
    public static final String TEST_CONFIGURATION_FILE = "TestConfiguration.plist";
    private NSDictionary testConfigurationDictionary;

    public static String TEST_SERVER;
    public static String TEST_ACCOUNT;
    public static String TEST_USER;
    public static String TEST_PASSWORD;


    public static Test suite()
    {
        return new AllTestsSuite();
    }



    public AllTestsSuite(String name)
    {
        super(name);
        TEST_SERVER =   (String)testConfigurationDictionary().objectForKey(MAIL_SERVER_KEY);
        TEST_ACCOUNT =  (String)testConfigurationDictionary().objectForKey(POP_ACCOUNT_KEY);
        TEST_USER =     (String)testConfigurationDictionary().objectForKey(MAIL_USER_KEY);
        TEST_PASSWORD = (String)testConfigurationDictionary().objectForKey(MAIL_USER_PASSWORD_KEY);

        addTest(new TestSuite(MessageTest.class));
        addTest(new TestSuite(MailDeliveryTest.class));
        addTest(new TestSuite(IMAPInBoxTest.class));
        addTest(new TestSuite(EmailAddressTest.class));
    }



    public AllTestsSuite()
    {
        this("GVCMailTests");
    }



    /**
     * Returns the configuration dictionary for this test run.  This class gets the
     * dictionary from the TestConfigurationDictionary.plist of the framework in
     * the working directory when the tests are run.<br>
     * <br>
     * The dictionary is expected to have this format:
     * <code>
     * {
     *     MailServer = "mail.someserver.com";
     *     POPAccount = "you@someserver.com";
     *     MailUser = "you";
     *     MailUserPassword = "secret";
     * }
     * </code>
     *
     * @return the configuration dictionary for this test run
     */
    protected NSDictionary testConfigurationDictionary()
    {
        if (testConfigurationDictionary == null)
        {
            NSBundle bundle = NSBundle.mainBundle();
            if (NSBundleAdditions.tableExistsWithName(bundle, TEST_CONFIGURATION_FILE))
            {
                testConfigurationDictionary = NSBundleAdditions.tableWithName(bundle, TEST_CONFIGURATION_FILE);
            }
            else
            {
                throw new IllegalStateException("Can't find configuration in " + TEST_CONFIGURATION_FILE);
            }

            // Validate dictionary contents
            if (testConfigurationDictionary.objectForKey(MAIL_SERVER_KEY) == null)
            {
                throw new IllegalArgumentException(MAIL_SERVER_KEY +
                    " key missing from testConfigurationDictionary()");
            }

            if (testConfigurationDictionary.objectForKey(POP_ACCOUNT_KEY) == null)
            {
                throw new IllegalArgumentException(POP_ACCOUNT_KEY +
                    " key missing from testConfigurationDictionary()");
            }

            if (testConfigurationDictionary.objectForKey(MAIL_USER_KEY) == null)
            {
                throw new IllegalArgumentException(MAIL_USER_KEY +
                    " key missing from testConfigurationDictionary()");
            }

            if (testConfigurationDictionary.objectForKey(MAIL_USER_PASSWORD_KEY) == null)
            {
                throw new IllegalArgumentException(MAIL_USER_PASSWORD_KEY +
                    " key missing from testConfigurationDictionary()");
            }
        }

        return testConfigurationDictionary;

        /** ensure
        [valid_result] Result != null;
        [mail_server_key_is_present] Result.objectForKey(MAIL_SERVER_KEY) != null;
        [pop_account_key_is_present] Result.objectForKey(POP_ACCOUNT_KEY) != null;
        [mail_user_key_is_present] Result.objectForKey(MAIL_USER_KEY) != null;
        [mail_user_password_key_is_present] Result.objectForKey(MAIL_USER_PASSWORD_KEY) != null; **/
    }

}
