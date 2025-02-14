package net.global_village.kerberos;


import junit.framework.TestCase;

import com.dstc.security.kerberos.Kerberos;



/*
 * Test the KerberosAuthenticator functionality.  This is not in the 
 * net.global_village.kerberos.test package as it needs package access to the 
 * net.global_village.kerberos.KerberosAuthenticator class.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class KerberosAuthenticatorTest extends TestCase
{
    static protected String TestKDCHost = "fill me in";
    static protected String TestRealm =  "fill me in";
    static protected String TestValidUserID = "fill me in";
    static protected String TestValidPassword = "fill me in";
    static protected String TestInvalidUserID = "chillatgvc";
    static protected String TestInvalidPassword = "ScoobySnack";

    protected KerberosAuthenticator testKerberosAuthenticator;


    public KerberosAuthenticatorTest(String name)
    {
        super(name);
    }



    /**
     * Sets up the fixtures.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        // KerberosAuthenticator.setDebugMode(true);
        testKerberosAuthenticator = new KerberosAuthenticator(TestKDCHost, TestRealm);
    }



    /**
     * Test methods related to using KerberosAuthenticator as a singleton.
     */
    public void testSingleton()
    {
        try
        {
            KerberosAuthenticator.getInstance();
            fail("getInstance() called before createInstance() but no DBC exception raised");
        } catch (Throwable t) {}

        try
        {
            KerberosAuthenticator.createInstance(null, TestRealm);
            fail("No DBC exception raised for null kdcHost");
        } catch (Throwable t) {}

        try
        {
            KerberosAuthenticator.createInstance(TestKDCHost, null);
            fail("No DBC exception raised for null realm");
        } catch (Throwable t) {}

        try
        {
            KerberosAuthenticator.createInstance("invalid.global-village.net", TestRealm);
            fail("No DBC exception raised invalid kdcHost");
        } catch (Throwable t) {}

        try
        {
            assertTrue(java.net.InetAddress.getByName(TestKDCHost).equals(testKerberosAuthenticator.keyDistributionCenter));
            assertTrue(TestRealm.equals(testKerberosAuthenticator.realm));
        }
        catch (Throwable t)
        {
            // Something unexpected happened.
            fail(t.toString());
        }
    }



    /**
     * Test the constructor
     */
    public void testConstructor()
    {
        try
        {
            new KerberosAuthenticator(null, TestRealm);
            fail("No DBC exception raised for null kdcHost");
        } catch (Throwable t) {}

        try
        {
            new KerberosAuthenticator(TestKDCHost, null);
            fail("No DBC exception raised for null realm");
        } catch (Throwable t) {}

        try
        {
            new KerberosAuthenticator("invalid.global-village.net", TestRealm);
            fail("No DBC exception raised invalid kdcHost");
        } catch (Throwable t) {}

        try
        {
            assertTrue(java.net.InetAddress.getByName(TestKDCHost).equals(testKerberosAuthenticator.keyDistributionCenter));
            assertTrue(TestRealm.equals(testKerberosAuthenticator.realm));
        }
        catch (Throwable t)
        {
            // Something unexpected happened.
            fail(t.toString());
        }
    }



    /**
     * Test credential method for authenticating
     */
    public void testCredential()
    {
        try
        {
            testKerberosAuthenticator.credential(null, TestValidPassword);
            fail("No DBC exception raised for null userID");
        } catch (Throwable t) {}

        try
        {
            testKerberosAuthenticator.credential(TestValidUserID, null);
            fail("No DBC exception raised for null password");
        } catch (Throwable t) {}

        assertTrue(testKerberosAuthenticator.credential(TestValidUserID, TestValidPassword) != null);
        assertTrue(testKerberosAuthenticator.credential(TestValidUserID, TestInvalidPassword) == null);
        assertTrue(testKerberosAuthenticator.credential(TestInvalidUserID, TestValidPassword) == null);
        assertTrue(testKerberosAuthenticator.credential(TestInvalidUserID, TestInvalidPassword) == null);
    }


    
    /**
     * Test canAuthenticate method for authenticating
     */
    public void testCanAuthenticate()
    {
        try
        {
            testKerberosAuthenticator.canAuthenticate(null, TestValidPassword);
            fail("No DBC exception raised for null userID");
        } catch (Throwable t) {}

        try
        {
            testKerberosAuthenticator.canAuthenticate(TestValidUserID, null);
            fail("No DBC exception raised for null password");
        } catch (Throwable t) {}

        assertTrue(testKerberosAuthenticator.canAuthenticate(TestValidUserID, TestValidPassword));
        assertTrue( ! testKerberosAuthenticator.canAuthenticate(TestValidUserID, TestInvalidPassword));
        assertTrue( ! testKerberosAuthenticator.canAuthenticate(TestInvalidUserID, TestValidPassword));
        assertTrue( !testKerberosAuthenticator.canAuthenticate(TestInvalidUserID, TestInvalidPassword));
    }



    /**
     * Tests the encryptionType(String) and encryptionTypes() methods
     */
    public void testEncryptionType()
    {
        try
        {
            KerberosAuthenticator.encryptionType(null);
            fail("No DBC exception raised for null encryptionTypeName");
        } catch (Throwable t) {}

        try
        {
            KerberosAuthenticator.encryptionType("scooby.doo");
            fail("No DBC exception raised for invalid encryptionTypeName");
        } catch (Throwable t) {}

        // Test the lazy creation dictionary
        assertTrue(KerberosAuthenticator.encryptionTypes() != null);

        // Test the known types.
        assertTrue(KerberosAuthenticator.encryptionType("des-cbc-crc") == Kerberos.DES_CBC_CRC);
        assertTrue(KerberosAuthenticator.encryptionType("des-cbc-md4") == Kerberos.DES_CBC_MD4);
        assertTrue(KerberosAuthenticator.encryptionType("des-cbc-md5") == Kerberos.DES_CBC_MD5);
        assertTrue(KerberosAuthenticator.encryptionType("des3-hmac-sha1") == Kerberos.DES3_CBC_HMAC_SHA1_KD);
        assertTrue(KerberosAuthenticator.encryptionType("rc4-hmac") == Kerberos.RC4_HMAC);
    }
  
    
}
