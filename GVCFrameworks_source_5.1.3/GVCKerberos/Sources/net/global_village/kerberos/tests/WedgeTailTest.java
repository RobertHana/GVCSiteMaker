package net.global_village.kerberos.tests;


import java.net.InetAddress;

import javax.crypto.Cipher;

import junit.framework.TestCase;

import com.dstc.security.kerberos.KDCOptions;
import com.dstc.security.kerberos.Kerberos;
import com.dstc.security.kerberos.KerberosPassword;
import com.dstc.security.kerberos.PrincipalName;
import com.dstc.security.kerberos.util.Kinit;


/*
 * Test the that the WedgeTail libraries are working.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class WedgeTailTest extends TestCase
{
    static protected String TestKDCHost = "fill me in";
    static protected String TestRealm =  "fill me in";
    static protected String TestValidUserID = "fill me in";
    static protected String TestValidPassword = "fill me in";
    static protected String TestInvalidUserID = "chillatgvc";
    static protected String TestInvalidPassword = "ScoobySnack";



    public WedgeTailTest(String name)
    {
        super(name);
    }



    /**
     * Test methods related to using KerberosAuthenticator as a singleton.
     */
    public void testWedgeTail() throws Exception
    {

        Cipher ciph = Cipher.getInstance("DES/CBC/NoPadding");
        System.out.println(ciph.getProvider().getInfo());

        Kinit kinit = new Kinit(TestRealm, TestKDCHost);

        kinit.setRequestOptions(new KDCOptions(),
                                new int[] {Kerberos.DES_CBC_CRC} ,
                                new InetAddress[] {InetAddress.getByName("localhost")},
                                10);

        kinit.authenticate(new PrincipalName(TestValidUserID),
                           new KerberosPassword(TestValidPassword.getBytes()), "test.cache");
    }

    
}
