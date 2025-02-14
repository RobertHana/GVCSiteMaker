package net.global_village.kerberos;

import java.net.InetAddress;
import java.util.Dictionary;

import com.dstc.security.kerberos.*;
import com.dstc.security.kerberos.creds.Credential;

import com.webobjects.foundation.NSLog;



/**
 * KerberosAuthenticator is a simple wrapper class around some of the functionality in the WedgeTail JSCI Kerberos classes.  It implements a very simple Kerberos authentication of a user ID and password and very little else.<br><br>
 * <br>
 * <pre>
 * // Create an instance we will manage
 * KerberosAuthenticator authenticator = new KerberosAuthenticator("kerberos.global-villge.net", "GLOBAL-VILLAGE.NET");
 * // or
 * // Create an instance to use as a singleton
 * KerberosAuthenticator.createInstance("kerberos.global-villge.net", "GLOBAL-VILLAGE.NET");
 * KerberosAuthenticator authenticator = KerberosAuthenticator.getInstance();
 *
 * // Authenticate and get a Kerberos credential
 * Credential credential = authenticator.credential("chill", "fuzzyBLUEostrich");
 *
 * // Check that a user ID and password combination is valid
 * if (authenticator..canAuthenticate("chill", "fuzzyBLUEostrich"))
 * {
 *     // proceed with login
 * }
 * </pre>
 * <br>
 * <b>Debugging</b>: Launch with -Djcsi.kerberos.debug=true
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class KerberosAuthenticator
{

    static protected KerberosAuthenticator instance = null;		// For the "singleton"
    static protected boolean hasInstalledCryptoProvider = false;	// For installing the cryptography provider
    static protected boolean isDebugMode = false;					// For debugging

    protected InetAddress keyDistributionCenter;				// Address of the KDC
    protected String realm;									// Name of the realm we are authenticating in 



    /**
     * Creates an instance of KerberosAuthenticator, configured for a specific key distribution center (KDC) and realm.  The instance created is accessible with getInstance().  createInstance() and getInstance() allow KerberosAuthenticator to be used as if it were a singleton.  This makes it easier to use as there is no need to manage an instance.
     *
     * @param kdcAddress - the (host) domain name or IP address of the Kerberos key distribution center (KDC)
     * @param theRealm - the Kerberos Realm to athenticate in
     *
     * @exception java.net.UnknownHostException - if no IP address for kdcAddress could be found
     */
    static public void createInstance(String kdcAddress, String theRealm)
        throws java.net.UnknownHostException
    {
        /** require
        kdcAddress != null;
        theRealm != null; **/

        instance = new KerberosAuthenticator(kdcAddress, theRealm);

        /** ensure instance != null; **/
    }



    /**
     * Returns the instance of KerberosAuthenticator previously created by calling createInstance().
     *
     * @return the instance of KerberosAuthenticator previously created by calling createInstance().
     */
    static public KerberosAuthenticator getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException("getInstance() called before createInstance()");
        }

        return instance;

        /** ensure Result != null; **/
    }



    /**
     * Turns debug mode on and off.
     *
     * @param debugMode - true for debugging on, false for debugging off.
     */
    public static void setDebugMode(boolean debugMode)
    {
        isDebugMode = debugMode;
        java.lang.System.setProperty("jcsi.kerberos.debug", new Boolean(isDebugMode).toString());
    }



    /**
     * Installs the com.dstc.security.provider.DSTC Cryptography Package Provider.  See the file JRE/lib/security/java.security file for more information and an alternative installation procedure.  This is called from the constructor.
     */
    protected static void installCryptoProvider()
    {
        if ( ! hasInstalledCryptoProvider)
        {
            java.security.Provider dtsc = new com.dstc.security.provider.DSTC();

            // Make sure someone else has not installed it!
            if (java.security.Security.getProvider(dtsc.getName()) == null)
            {
                java.security.Security.insertProviderAt(dtsc, 2);

                if (isDebugMode)
                {
                    NSLog.out.appendln("Installed Cryptography Package Provider com.dstc.security.provider.DSTC");
                }
            }

            hasInstalledCryptoProvider = true;

            if (isDebugMode)
            {
                try
                {
                    javax.crypto.Cipher ciph = javax.crypto.Cipher.getInstance("DES/CBC/NoPadding");
                    NSLog.out.appendln("Cipher info:\n" + ciph.getProvider().getInfo());
                }
                catch (javax.crypto.NoSuchPaddingException e)
                {
                    NSLog.out.appendln("No Cipher info because of NoSuchPaddingException");
                }
                catch (java.security.NoSuchAlgorithmException e)
                {
                    NSLog.out.appendln("No Cipher info because of NoSuchAlgorithmException");
                }
            }
        }
    }



    /**
     * Designated constructor.  Creates an instance of KerberosAuthenticator, configured for a specific key distribution center (KDC) and realm.  
     *
     * @param kdcAddress - the (host) domain name or IP address of the Kerberos key distribution center (KDC)
     * @param theRealm - the Kerberos Realm to athenticate in
     *
     * @exception java.net.UnknownHostException - if no IP address for kdcAddress could be found
     */
    public KerberosAuthenticator(String kdcAddress, String theRealm)
        throws java.net.UnknownHostException
    {
        super();

        /** require
        kdcAddress != null;
        theRealm != null; **/

        installCryptoProvider();
        realm = theRealm.toUpperCase();  // Must be upper case
        keyDistributionCenter = InetAddress.getByName(kdcAddress);
    }



    /**
     * Returns the credential obtained by authenticating the Principal described by userID and password in the realm / server this KerberosAuthenticator is set up for.  Returns null if the Principal can not be authenticated for any reason, including server and network errors.
     *
     * @param userID - the user ID (name) of the Principal as known by the Kerberos server
     * @param password - the password that is used to authenticate the Principal's userID
     *
     * @return the credential obtained by authenticating the Principal described by userID and password
     */
    public Credential credential(String userID, String password)
    {
        /** require
        userID != null;
        password != null; **/

        Credential credential = null;

        // Create a new context based on the Kerberos Principal being authenticated and get a reference to the server.
        KerberosContext context = new KerberosContext(realm,
                                                      new PrincipalName(userID.toLowerCase()),
                                                      keyDistributionCenter);

        Kerberos kerberos = Kerberos.getInstance(context);
        KerberosPassword kerberosPassword = new KerberosPassword(password.getBytes());

        try
        {
            if (isDebugMode)
            {
                NSLog.out.appendln("Trying to authenticate " + userID + " with KDC...");
            }

            credential = kerberos.requestTicketGrantingTicket(kerberosPassword, new KDCOptions(), null, null, null, null);

            if (isDebugMode && (credential != null))
            {
                NSLog.out.appendln("Authenticated.");
            }

        }
        catch (com.dstc.security.kerberos.CryptoException e)
        {
            // Authentication failed: Password incorrect.
            if (isDebugMode)
            {
                NSLog.out.appendln("Failed to authenticate, presuming incorrect password, exception: " + e.getMessage());
            }
        }
        catch (KerberosException e)
        {
            // Authentication failed: Client not found in Kerberos database
            if (isDebugMode)
            {
                NSLog.out.appendln("Failed to authenticate, presuming incorrect principal name, exception: " + e.getMessage());
            }
        }
        catch (java.io.IOException e)
        {
            // This probably means that the server could not be reached
            if (isDebugMode)
            {
                NSLog.out.appendln("Failed to authenticate for unknown reasons, exception: " + e.getMessage());
            }
        }

        return credential;
    }



    /**
     * Returns true if the Principal described by userID and password can be authenticated in the realm / server this KerberosAuthenticator is set up for.  Returns null if the Principal can not be authenticated for any reason, including server and network errors.
     *
     * @param userID - the user ID (name) of the Principal as known by the Kerberos server
     * @param password - the password that is used to authenticate the Principal's userID
     *
     * @return true if the Principal described by userID and password can be authenticated in the realm / server this KerberosAuthenticator is set up for.
     */
    public boolean canAuthenticate(String userID, String password)
    {
        /** require
        userID != null;
        password != null; **/

        return credential(userID, password) != null;
    }


    //
    // The following methods are obsolete, but preserved here in case there is a future need.
    //

    static protected Dictionary encryptionTypes = null;

    /**
     * Returns true if the passed encryption type name is recognized (is in ecryptionTypes).
     *
     * @param encryptionTypeName - the name of the encryption type to check for validity.
     *
     * @return true if the passed encryption type name is recognized.
     */
    static public boolean isValidEncryptionTypeName(String encryptionTypeName)
    {
        /** require encryptionTypeName != null; **/

        return encryptionTypes().get(encryptionTypeName) != null;
    }



    /**
     * Translates between an string description of an encryption type and the numeric value used by com.dstc.security.kerberos.Kerberos.  For example if passed "des-cbc-crc" it returns Kerberos.DES_CBC_CRC (i.e. 0x01).  See documentation for that class for more information.
     *
     * @param encryptionTypeName - the name of the encryption type to return the code for.
     *
     * @return the numeric value used by com.dstc.security.kerberos.Kerberos for this encryption type.
     */
    static public int encryptionType(String encryptionTypeName)
    {
        /** require
        encryptionTypeName != null;
        isValidEncryptionTypeName(encryptionTypeName); **/

        Integer encryptionType = (Integer) encryptionTypes().get(encryptionTypeName);

        return encryptionType.intValue();
    }



    /**
     * Returns a dictionary of the encryption type names ("des-cbc-crc", "des-cbc-md4", "des-cbc-md5", "des3-hmac-sha1", "rc4-hmac") and thier corresponding values.
     *
     * @return a dictionary of the encryption type names and thier corresponding values.
     */
    static public Dictionary encryptionTypes()
    {
        if (encryptionTypes == null)
        {
            encryptionTypes = new java.util.Hashtable();
            encryptionTypes.put("des-cbc-crc", new Integer(Kerberos.DES_CBC_CRC));
            encryptionTypes.put("des-cbc-md4", new Integer(Kerberos.DES_CBC_MD4));
            encryptionTypes.put("des-cbc-md5", new Integer(Kerberos.DES_CBC_MD5));
            encryptionTypes.put("des3-hmac-sha1", new Integer(Kerberos.DES3_CBC_HMAC_SHA1_KD));
            encryptionTypes.put("rc4-hmac", new Integer(Kerberos.RC4_HMAC));
        }

        return encryptionTypes;

        /** ensure Result != null; **/
    }



    /** invariant
    realm != null;
    keyDistributionCenter != null;  **/

}
