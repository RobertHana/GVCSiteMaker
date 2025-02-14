package net.global_village.foundation.tests;

import junit.framework.TestCase;
import net.global_village.foundation.ClassAdditions;

import com.webobjects.foundation.NSArray;


/*
 * Test the Class functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ClassAdditionsTest extends TestCase
{


    public ClassAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Test classesAreEqual().
     */
    public void testClassesAreEqual()
    {
        assertTrue(ClassAdditions.classesAreEqual(String.class, String.class));
        assertTrue(ClassAdditions.classesAreEqual(String.class.getName(), String.class.getName()));
    }



    /**
     * Test unqualifiedClassName().
     */
    public void testUnqualifiedClassName()
    {
        assertTrue(ClassAdditions.unqualifiedClassName(String.class).equals("String"));
        assertTrue(ClassAdditions.unqualifiedClassName(NSArray.class).equals("NSArray"));
    }



}
