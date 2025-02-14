package net.global_village.jmail.tests;

import junit.framework.TestCase;
import net.global_village.jmail.IMAPInBox;


/**
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class IMAPInBoxTest extends  TestCase
{


    public IMAPInBoxTest(String name)
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
    public void testConnection()
    {
        try
        {
            new IMAPInBox(AllTestsSuite.TEST_SERVER, AllTestsSuite.TEST_USER, AllTestsSuite.TEST_PASSWORD);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }


}
