package net.global_village.jndi.tests;

import junit.framework.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;

/**
 * Simple connectivity test for LDAPTLSPlugin.  This will fail if the EOModel is not configured 
 * correctly with a valid user name and password.
 * 
 * The name of the test group needs to be changed if not running against UM's public server.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LDAPTLSPluginTest extends TestCase
{

    /**
     * Constructor for LDAPTLSPluginTest.
     * @param name
     */
    public LDAPTLSPluginTest(String name)
    {
        super(name);
    }



    
    public void testConnectivity()
    {
        try
        {
            EOEditingContext ec = new EOEditingContext();
            EOUtilities.objectMatchingKeyAndValue(ec, "GVCTestLDAPGroup", "cn", "Pharmacology");
        }
        catch (Throwable t)
        {
            fail("Raised while fetching LDAP group " + t.getMessage());
            t.printStackTrace();
        }
    }

}
