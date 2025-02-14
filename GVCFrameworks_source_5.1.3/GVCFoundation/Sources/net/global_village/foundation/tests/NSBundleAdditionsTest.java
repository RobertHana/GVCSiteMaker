package net.global_village.foundation.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/*
 * Test the Bundle functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class NSBundleAdditionsTest extends TestCase
{


    public NSBundleAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Test localizedStringForKey().
     */
    public void testLocalizedStringForKey()
    {
        assertTrue(NSBundleAdditions.localizedStringExistsForKey(NSBundle.bundleForClass(getClass()), "Localization Test String", "GVCNSObjectAdditionsForLocalizationTest"));
        assertTrue(NSBundleAdditions.localizedStringForKey(NSBundle.bundleForClass(getClass()), "Localization Test String", "GVCNSObjectAdditionsForLocalizationTest").equals("Test Succeeded."));

        try
        {
            NSBundleAdditions.localizedStringForKey(NSBundle.bundleForClass(getClass()), "Localization Test String", "NonexistantFile");
            fail("Expected exception did not occur: missing precondition on localizedStringForKey()");
        }
        catch (RuntimeException e) { }

        try
        {
            NSBundleAdditions.localizedStringForKey(NSBundle.bundleForClass(getClass()), "NonexistantKey", "GVCNSObjectAdditionsForLocalizationTest");
            fail("Expected exception did not occur: missing precondition on localizedStringForKey()");
        }
        catch (RuntimeException e) { }

        // localizedStringForKey with a default value
        assertTrue(NSBundleAdditions.localizedStringForKey(NSBundle.bundleForClass(getClass()), "NonexistantKey", "DefaultValue", "GVCNSObjectAdditionsForLocalizationTest").equals("DefaultValue"));
        assertTrue(NSBundleAdditions.localizedStringForKey(NSBundle.bundleForClass(getClass()), "Localization Test String", "DefaultValue", "NonexistantFile").equals("DefaultValue"));
    }



    /**
     * Test listExistsWithName(), listWithName().
     */
    public void testListLoading()
    {
        assertTrue(NSBundleAdditions.listExistsWithName(NSBundle.bundleForClass(getClass()), "GVCFoundationNSBundleTest.plist"));
        NSArray list = NSBundleAdditions.listWithName(NSBundle.bundleForClass(getClass()), "GVCFoundationNSBundleTest.plist");
        assertTrue(list.count() == 3);
        assertTrue(list.objectAtIndex(1).equals("b"));
    }



    /**
     * Test infoDictionary().
     */
    public void testInfoDictionary()
    {
        assertNotNull(NSBundleAdditions.infoDictionary(NSBundle.bundleForClass(getClass())));
        NSDictionary dic = NSBundleAdditions.infoDictionary(NSBundle.bundleForClass(getClass()));
        assertNotNull(dic.objectForKey("testString"));
    }



}
