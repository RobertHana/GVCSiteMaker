package net.global_village.eofvalidation.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;
import net.global_village.gvcjunit.*;


/**
 * Tests for validation's LocalizationEngine.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 *          reserved.
 * @version $Revision: 5$
 */
public class LocalizationEngineTest extends GVCJUnitEOTestCase
{
    protected final String ENGLISH = "English";
    protected final String FRENCH = "French";

    protected NSBundle validationBundle;
    protected EOEntity entity;
    protected EOEntity subClassedEntity;
    protected String localizationTestProperty;
    protected NSArray languages;



    /**
     * Designated constuctor.
     */
    public LocalizationEngineTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        validationBundle = NSBundleAdditions.bundleForClass(getClass());
        entity = EOModelGroup.defaultGroup().entityNamed(
                "AttributeValidationTestEntity");
        subClassedEntity = EOModelGroup.defaultGroup().entityNamed(
                "AttributeValidationTestSubClass");
        localizationTestProperty = "localizationTestProperty";
        languages = new NSArray(new String[] { ENGLISH, FRENCH });
    }



    /**
     * Test NSBundle localization cover methods.
     */
    public void testDoesBundleHaveLocalizedString()
    {
// The NSBundleAdditions methods are not useful for web apps and don't work properly as of WO 5.4
//        // DBC
//        try
//        {
//            NSBundleAdditions.localizedStringExistsForKey(null,
//                    "keyThatExists", "GVCEOFValidation");
//            fail("Missing null bundle precondition");
//        }
//        catch (Throwable t)
//        {
//        }
//
//        try
//        {
//            NSBundleAdditions.localizedStringExistsForKey(validationBundle,
//                    null, "GVCEOFValidation");
//            fail("Missing null key precondition");
//        }
//        catch (Throwable t)
//        {
//        }
//
//        try
//        {
//            NSBundleAdditions.localizedStringExistsForKey(validationBundle,
//                    "keyThatExists", null);
//            fail("Missing null fileName precondition");
//        }
//        catch (Throwable t)
//        {
//        }
//
//
//        assertTrue(NSBundleAdditions.localizedStringExistsForKey(
//                validationBundle, "keyThatExists", "GVCEOFValidation"));
//        assertTrue(!NSBundleAdditions.localizedStringExistsForKey(
//                validationBundle, "keyThatDoesNotExist", "GVCEOFValidation"));
//
//        assertTrue(NSBundleAdditions.localizedStringForKey(validationBundle,
//                "keyThatExists", "GVCEOFValidation").equals(
//                "Localized string for keyThatExists"));
//
//        assertTrue(NSBundleAdditions.localizedStringExistsForKey(
//                validationBundle, "keyThatExists", "GVCEOFValidation"));
//        assertTrue(!NSBundleAdditions.localizedStringExistsForKey(
//                validationBundle, "keyThatDoesNotExist", "GVCEOFValidation"));
    }



    /**
     * Test method checking one bundle for a key.
     */
    public void testlocalizedStringFromBundle()
    {
        // DBC
        try
        {
            LocalizationEngine.localizedStringFromBundle(null, entity.name(),
                    localizationTestProperty, "fullyQualifiedKeyTest");
            fail("Missing null bundle precondition");
        }
        catch (Throwable t)
        {
        }

        try
        {
            LocalizationEngine.localizedStringFromBundle(validationBundle,
                    null, localizationTestProperty, "fullyQualifiedKeyTest");
            fail("Missing null entity precondition");
        }
        catch (Throwable t)
        {
        }

        try
        {
            LocalizationEngine.localizedStringFromBundle(validationBundle,
                    entity.name(), localizationTestProperty, null);
            fail("Missing null key precondition");
        }
        catch (Throwable t)
        {
        }


        // Check for fully qualified key in entity's strings file
        assertEquals("Localization key for fully qualified key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "fullyQualifiedKeyTest"));

        // Check for partially qualified key in entity's strings file, and
        // empty property name
        assertEquals("Localization key for partially qualified key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), "", "partiallyQualifiedKeyTest"));

        // Check for generic key not being found in entity's strings file
        assertTrue(LocalizationEngine.localizedStringFromBundle(
                validationBundle, entity.name(), localizationTestProperty,
                "genericKeyTest") == null);
        // Check for fully qualified key in bundle's strings file
        assertEquals("Localization key for fully qualified bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "fullyQualifiedBundleKeyTest"));

        // Check for partially qualified key in bundle's strings file and null
        // property name
        assertEquals("Localization key for partially qualified bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), null, "partiallyQualifiedBundleKeyTest"));

        // Check for generic key in bundle's strings file
        assertEquals("Localization key for generic bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "genericBundleKeyTest"));



        // Check for fully qualified key in entity's strings file
        assertEquals("Localization key for fully qualified key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "fullyQualifiedKeyTest", ENGLISH));

        // Check for partially qualified key in entity's strings file, and
        // empty property name
        assertEquals("Localization key for partially qualified key",
                LocalizationEngine
                        .localizedStringFromBundle(validationBundle, entity
                                .name(), "", "partiallyQualifiedKeyTest",
                                ENGLISH));

        // Check for generic key not being found in entity's strings file
        assertTrue(LocalizationEngine.localizedStringFromBundle(
                validationBundle, entity.name(), localizationTestProperty,
                "genericKeyTest", ENGLISH) == null);
        // Check for fully qualified key in bundle's strings file
        assertEquals("Localization key for fully qualified bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "fullyQualifiedBundleKeyTest", ENGLISH));

        // Check for partially qualified key in bundle's strings file and null
        // property name
        assertEquals("Localization key for partially qualified bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), null, "partiallyQualifiedBundleKeyTest",
                        ENGLISH));

        // Check for generic key in bundle's strings file
        assertEquals("Localization key for generic bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "genericBundleKeyTest", ENGLISH));



        // Check for fully qualified key in entity's strings file
        assertEquals("French Localization key for fully qualified key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "fullyQualifiedKeyTest", FRENCH));

        // Check for partially qualified key in entity's strings file, and
        // empty property name
        assertEquals("French Localization key for partially qualified key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), "", "partiallyQualifiedKeyTest", FRENCH));

        // Check for generic key not being found in entity's strings file
        assertTrue(LocalizationEngine.localizedStringFromBundle(
                validationBundle, entity.name(), localizationTestProperty,
                "genericKeyTest", FRENCH) == null);
        // Check for fully qualified key in bundle's strings file
        assertEquals("French Localization key for fully qualified bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "fullyQualifiedBundleKeyTest", FRENCH));

        // Check for partially qualified key in bundle's strings file and null
        // property name
        assertEquals(
                "French Localization key for partially qualified bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), null, "partiallyQualifiedBundleKeyTest",
                        FRENCH));

        // Check for generic key in bundle's strings file
        assertEquals("French Localization key for generic bundle key",
                LocalizationEngine.localizedStringFromBundle(validationBundle,
                        entity.name(), localizationTestProperty,
                        "genericBundleKeyTest", FRENCH));

        // Check for generic key in bundle's strings file
        assertEquals("French Localization only test", LocalizationEngine
                .localizedStringFromBundle(validationBundle, entity.name(),
                        localizationTestProperty,
                        "onlyInFrenchLocalizationTest", FRENCH));
    }



    /**
     * Test method checking one bundle for a key with multiple languages.
     */
    public void testlocalizedStringWithArray()
    {
        // Check for partially qualified key in bundle's strings file and null
        // property name
        assertEquals("French Localization only test", LocalizationEngine
                .localizedString(entity.name(), localizationTestProperty,
                        "onlyInFrenchLocalizationTest", languages));

        // Check for generic key in bundle's strings file
        assertEquals("Localization key for generic bundle key",
                LocalizationEngine.localizedString(entity.name(),
                        localizationTestProperty, "genericBundleKeyTest",
                        languages));
    }



    /**
     * Test method checking full localization search for a key.
     */
    public void testlocalizedString()
    {
        // DBC
        try
        {
            LocalizationEngine.localizedString((String) null, null,
                    EOFValidation.DisplayName);
            fail("Missing null entity precondition");
        }
        catch (RuntimeException t)
        {
        }

        try
        {
            LocalizationEngine.localizedString(subClassedEntity, null, null);
            fail("Missing null key precondition");
        }
        catch (RuntimeException t)
        {
        }

        // Check exception for no localized message for key
        try
        {
            LocalizationEngine.localizedString(entity,
                    localizationTestProperty, "nonExistantKey");
            fail("Missing localized string non-existant precondition");
        }
        catch (RuntimeException r)
        {
        }


        // Check for key in main bundle.
        // NOTE: this needs to be added to whatever WOApp is testing this
        // framework!
        // Removed as it can not pass when using jUnit directly to test
        // framworks. - ch
        // assertTrue(LocalizationEngine.localizedString(subClassedEntity,
        // null, EOFValidation.DisplayName).equals("Localization Test: Display
        // Name from main bundle."));

        // Check for key on most derived class
        assertEquals("Localization Test: most derived class invalid value.",
                LocalizationEngine.localizedString(subClassedEntity,
                        localizationTestProperty, EOFValidation.InvalidValue));

        // Check for key on super-class
        assertEquals("Localization Test: super class null not allowed.",
                LocalizationEngine.localizedString(subClassedEntity,
                        localizationTestProperty, EOFValidation.NullNotAllowed));

        // Check for default key
        assertEquals(
                "<<failedValue>> is below the acceptable limit for <<propertyName>> for <<entityName>>.",
                LocalizationEngine.localizedString(subClassedEntity,
                        localizationTestProperty, EOFValidation.BelowMinimum));
    }



}
