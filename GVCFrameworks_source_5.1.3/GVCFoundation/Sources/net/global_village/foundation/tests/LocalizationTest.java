package net.global_village.foundation.tests;

import junit.framework.TestCase;

import com.webobjects.foundation.NSBundle;

import net.global_village.foundation.DynamicInstanceVariable;
import net.global_village.foundation.Localization;
import net.global_village.foundation.NSBundleAdditions;


/*
 * Test the localization functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LocalizationTest extends TestCase
{
    private static final String GVCNonExistantString = "Not in any of the string files.";
    private static final String GVCTestString = "This string is in the Test table";
    private static final String GVCTestStringResult = "YES!";
    private static final String GVCTestGVCLocalizationString = "This string is in the TestGVCLocalization table";
    private static final String GVCTestGVCLocalizationStringResult = "Yay Yay Yay";
    private static final String GVCGVCJFoundationString = "This string is in the GVCFoundation table";
    private static final String GVCGVCJFoundationStringResult = "Yay Yay";
    private static final String GVCLocalizedStringsString = "This string is in the LocalizedStrings table";
    private static final String GVCLocalizedStringsStringResult = "Yay";


    public LocalizationTest(String name)
    {
        super(name);
    }



    /**
     * Tests localizedStringExistsForString.  Makes sure that the various files are checked properly.
     */
    public void testLocalizedStringExistsForString()
    {
        assertTrue( ! Localization.localizedStringExistsForString(this, GVCNonExistantString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCTestGVCLocalizationString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCGVCJFoundationString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCLocalizedStringsString));
        assertTrue( ! Localization.localizedStringExistsForString(this, GVCTestString));

        DynamicInstanceVariable.setInstanceVariable(this, Localization.GVCLocalizedFileNameVariable, "Test");
        // Test same four again after setting the file name DIV.
        assertTrue( ! Localization.localizedStringExistsForString(this, GVCNonExistantString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCTestGVCLocalizationString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCGVCJFoundationString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCLocalizedStringsString));
        assertTrue(Localization.localizedStringExistsForString(this, GVCTestString));

        DynamicInstanceVariable.setInstanceVariable(this, Localization.GVCLocalizedBundleVariable, 
            NSBundleAdditions.anyBundle(NSBundle.mainBundle()));
        assertTrue( ! Localization.localizedStringExistsForString(this, "Test file is not in the main bundle"));

        DynamicInstanceVariable.removeInstanceVariable(this, Localization.GVCLocalizedBundleVariable);
        DynamicInstanceVariable.setInstanceVariable(this, Localization.GVCLocalizedBundleVariable, NSBundle.bundleForClass(getClass()));
        assertTrue(Localization.localizedStringExistsForString(this, "This is a test string"));

        assertTrue( ! Localization.localizedStringExistsForString(this, "This is NOT a test string"));
    }



    /**
     * Tests localizedStringForString.  Makes sure that the various files are checked properly.
     */
    public void testLocalizedStringForString()
    {
        String aLocalizedString;

        try
        {
            Localization.localizedStringForString(this, null);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        try
        {
            Localization.localizedStringForString(this, GVCNonExistantString);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        try
        {
            Localization.localizedStringForString(this, GVCTestString);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        aLocalizedString = Localization.localizedStringForString(this, GVCTestGVCLocalizationString);
        assertEquals(aLocalizedString, GVCTestGVCLocalizationStringResult);

        aLocalizedString = Localization.localizedStringForString(this, GVCGVCJFoundationString);
        assertEquals(aLocalizedString, GVCGVCJFoundationStringResult);

        aLocalizedString = Localization.localizedStringForString(this, GVCLocalizedStringsString);
        assertEquals(aLocalizedString, GVCLocalizedStringsStringResult);


        // Test again once the file name DIV is set.
        DynamicInstanceVariable.setInstanceVariable(this,
                                                    Localization.GVCLocalizedFileNameVariable,
                                                    "Test");

        try
        {
            Localization.localizedStringForString(this, GVCNonExistantString);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        aLocalizedString = Localization.localizedStringForString(this, GVCTestString);
        assertEquals(aLocalizedString, GVCTestStringResult);

        aLocalizedString = Localization.localizedStringForString(this, GVCTestGVCLocalizationString);
        assertEquals(aLocalizedString, GVCTestGVCLocalizationStringResult);

        aLocalizedString = Localization.localizedStringForString(this, GVCGVCJFoundationString);
        assertEquals(aLocalizedString, GVCGVCJFoundationStringResult);

        aLocalizedString = Localization.localizedStringForString(this, GVCLocalizedStringsString);
        assertEquals(aLocalizedString, GVCLocalizedStringsStringResult);


        DynamicInstanceVariable.setInstanceVariable(this,
                                                    Localization.GVCLocalizedBundleVariable,
                                                    NSBundleAdditions.anyBundle(NSBundle.mainBundle()));
        try
        {
            Localization.localizedStringForString(this, "Test file is not in the main bundle");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        DynamicInstanceVariable.removeInstanceVariable(this, Localization.GVCLocalizedBundleVariable);
        DynamicInstanceVariable.setInstanceVariable(this,
                                                    Localization.GVCLocalizedBundleVariable,
                                                    NSBundle.bundleForClass(getClass()));
        aLocalizedString = Localization.localizedStringForString(this, "This is a test string");
        assertEquals(aLocalizedString, "Hello!");

        try
        {
            Localization.localizedStringForString(this, "This is NOT a test string");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Tears down the fixtures.
     * @exception Exception an exception that the tear down may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        DynamicInstanceVariable.deallocInstanceVariables(this);
        super.tearDown();
    }



}
