package net.global_village.foundation.tests;

import junit.framework.TestCase;
import net.global_village.foundation.Debug;



/**
 * Test the Debug functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DebugTest extends TestCase
{


    public DebugTest(String name)
    {
        super(name);
    }



    /**
     * Test if dumpJVMConfiguration prints out the JVM Configuration properly
     */
    public void testDumpJVMConfiguration()
    {
        assertTrue(Debug.dumpJVMConfiguration().length() > 0);
    }



}
