package net.global_village.foundation.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/*
 * Test the StringAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class StringAdditionsTest extends TestCase
{


    public StringAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Test keyPathComponents().
     */
    public void testKeyPathComponents()
    {
        assertEquals(2, StringAdditions.keyPathComponents("a.test").count());
        assertEquals(1, StringAdditions.keyPathComponents("a test").count());
    }



    /**
     * Test isValidPropertyKeyPath().
     */
    public void testIsValidPropertyKeyPath()
    {
        assertTrue(StringAdditions.isValidPropertyKeyPath("a.test"));
        assertTrue( ! StringAdditions.isValidPropertyKeyPath("a test"));
    }



    /**
     * Test propertyNameFromKeyPath().
     */
    public void testPropertyNameFromKeyPath()
    {
        assertEquals("test", StringAdditions.propertyNameFromKeyPath("a.test"));
        assertEquals("is", StringAdditions.propertyNameFromKeyPath("a.test.this.is"));
        assertEquals("", StringAdditions.propertyNameFromKeyPath("a.test.this.is."));
        try
        {
            StringAdditions.propertyNameFromKeyPath("test");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test objectKeyPathFromKeyPath().
     */
    public void testObjectKeyPathFromKeyPath()
    {
        assertEquals("a", StringAdditions.objectKeyPathFromKeyPath("a.test"));
        assertEquals("a.test.this", StringAdditions.objectKeyPathFromKeyPath("a.test.this.is"));
        try
        {
            StringAdditions.objectKeyPathFromKeyPath("test");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test rootKeyFromKeyPath().
     */
    public void testRootKeyFromKeyPath()
    {
        assertEquals("a", StringAdditions.rootKeyFromKeyPath("a.test"));
        assertEquals("test", StringAdditions.rootKeyFromKeyPath("test.a.this.is"));
        try
        {
            StringAdditions.rootKeyFromKeyPath("test");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test removeRootKeyFromKeyPath().
     */
    public void testRemoveRootKeyFromKeyPath()
    {
        assertEquals("test", StringAdditions.removeRootKeyFromKeyPath("a.test"));
        assertEquals("test.this.is", StringAdditions.removeRootKeyFromKeyPath("a.test.this.is"));
        try
        {
            StringAdditions.removeRootKeyFromKeyPath("test");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test isEmpty().
     */
    public void testIsEmpty()
    {
        assertTrue(StringAdditions.isEmpty(null));
        assertTrue(StringAdditions.isEmpty(""));
        assertTrue(StringAdditions.isEmpty(new String()));
        assertFalse(StringAdditions.isEmpty("."));
        assertTrue(StringAdditions.isEmpty("\n"));
        assertTrue(StringAdditions.isEmpty(" "));
        assertTrue(StringAdditions.isEmpty("  "));
        assertTrue(StringAdditions.isEmpty("\t"));
        assertTrue(StringAdditions.isEmpty(" \t "));
        assertTrue(StringAdditions.isEmpty("\t\t\n\r  \t"));
    }



    /**
     * Test isInteger().
     */
    public void testIsInteger()
    {
        assertTrue(StringAdditions.isInteger("0976"));
        assertTrue(StringAdditions.isInteger(" 123"));
        assertTrue(StringAdditions.isInteger(" -123"));
        assertTrue(StringAdditions.isInteger("7457888 "));
        assertTrue(StringAdditions.isInteger(" 8223700 "));
        assertTrue(StringAdditions.isInteger("\t -0002 \r\n"));
        assertTrue( ! StringAdditions.isInteger("+0976"));
        assertTrue( ! StringAdditions.isInteger("\t - 0002 \r\n"));
        assertTrue( ! StringAdditions.isInteger("1.1"));
        assertTrue( ! StringAdditions.isInteger("a1"));
        assertTrue( ! StringAdditions.isInteger("asdf"));
        assertTrue( ! StringAdditions.isInteger("1a"));
        assertTrue( ! StringAdditions.isInteger(""));
        assertTrue( ! StringAdditions.isInteger(" "));

        // Suffixes here to show that these values will NOT be accepted!
        assertTrue( ! StringAdditions.isInteger("232l"));  // the last character is a lowercase "L"
        assertTrue( ! StringAdditions.isInteger("2L"));

        // Radix prefixes are not used!
        assertTrue( ! StringAdditions.isInteger("0x232A"));
        assertTrue(StringAdditions.isInteger("0279"));  // Looks like octal (since it begins with a "0"), but (as the "9" testifies), it is actually a decimal number, and will be accepted
    }



    /**
     * Test isFloat().
     */
    public void testIsFloat()
    {
        assertTrue(StringAdditions.isFloat("1.6"));
        assertTrue(StringAdditions.isFloat("1"));
        assertTrue(StringAdditions.isFloat(" 1.2"));
        assertTrue(StringAdditions.isFloat("132.23 "));
        assertTrue(StringAdditions.isFloat("132.23e5"));
        assertTrue(StringAdditions.isFloat("132.23e-5"));
        assertTrue(StringAdditions.isFloat("-132.23 "));
        assertTrue(StringAdditions.isFloat("+132.23 "));
        assertTrue(StringAdditions.isFloat(" 134.34 "));
        assertTrue(StringAdditions.isFloat("\t 123.2 \r\n"));
        assertTrue( ! StringAdditions.isFloat("\t - 123.2 \r\n"));
        assertTrue( ! StringAdditions.isFloat(""));
        assertTrue( ! StringAdditions.isFloat("a1"));
        assertTrue( ! StringAdditions.isFloat("asdf"));
        assertTrue( ! StringAdditions.isFloat("1a"));

        // Suffixes here to show that these values WILL be accepted!
        assertTrue(StringAdditions.isFloat("2d"));
        assertTrue(StringAdditions.isFloat("2.53454f"));

        // Radix prefixes are not used!
        assertTrue( ! StringAdditions.isFloat("0x2F21B"));
        assertTrue(StringAdditions.isFloat("02.59454"));  // Looks like octal (since it begins with a "0"), but (as the "9" testifies), it is actually a decimal number, and will be accepted
    }



    /**
     * Test isNumber().
     */
    public void testIsNumber()
    {
        assertTrue(StringAdditions.isNumber("0192"));
        assertTrue(StringAdditions.isNumber("+888"));
        assertTrue(StringAdditions.isNumber(" 123"));
        assertTrue(StringAdditions.isNumber("-123"));
        assertTrue(StringAdditions.isNumber("1.6"));
        assertTrue(StringAdditions.isNumber("+1"));
        assertTrue(StringAdditions.isNumber(" 1.2"));
        assertTrue(StringAdditions.isNumber("132.23 "));
        assertTrue(StringAdditions.isNumber("-132.23 "));
        assertTrue(StringAdditions.isNumber("-132.23e5 "));
        assertTrue(StringAdditions.isNumber("-132.23e-5 "));
        assertTrue(StringAdditions.isNumber(" 134.34 "));
        assertTrue(StringAdditions.isNumber("\t 123.2 \r\n"));
        assertTrue(StringAdditions.isNumber("\t -123.2 \r\n"));
        assertTrue( ! StringAdditions.isNumber(""));
        assertTrue( ! StringAdditions.isNumber(" "));
        assertTrue( ! StringAdditions.isNumber("\t - 123.2 \r\n"));
        assertTrue( ! StringAdditions.isNumber("a1"));
        assertTrue( ! StringAdditions.isNumber("asdf"));

        // Suffixes here to show that these values will NOT be accepted!
        assertTrue( ! StringAdditions.isInteger("2323l"));  // the last character is a lowercase "L"
        assertTrue( ! StringAdditions.isInteger("2L"));
        assertTrue( ! StringAdditions.isNumber("2d"));
        assertTrue( ! StringAdditions.isNumber("2.53454f"));

        // Radix prefixes are not used!
        assertTrue( ! StringAdditions.isFloat("0x2F21B"));
        assertTrue(StringAdditions.isFloat("02.59454"));  // Looks like octal (since it begins with a "0"), but (as the "9" testifies), it is actually a decimal number, and will be accepted
    }



    /**
     * Test isDigits().
     */
    public void testIsDigits()
    {
        assertTrue(StringAdditions.isDigits("0192"));
        assertTrue(StringAdditions.isDigits("888"));
        assertTrue(StringAdditions.isDigits(""));
        assertTrue(StringAdditions.isDigits(" 123"));
        assertTrue(StringAdditions.isDigits(" "));
        assertTrue( ! StringAdditions.isDigits("-123"));
        assertTrue( ! StringAdditions.isDigits("asdf"));
    }



    /**
     * Test isAlphaOnly().
     */
    public void testIsAlphaOnly()
    {
        assertTrue(StringAdditions.isAlphaOnly("ABC"));
        assertTrue(StringAdditions.isAlphaOnly("abc"));
        assertTrue(StringAdditions.isAlphaOnly("aBc"));
        assertTrue(StringAdditions.isAlphaOnly(""));
        assertFalse(StringAdditions.isAlphaOnly("123"));
        assertFalse(StringAdditions.isAlphaOnly(" ABC"));
        assertFalse(StringAdditions.isAlphaOnly("ABC "));
        assertFalse(StringAdditions.isAlphaOnly("_"));
        assertFalse(StringAdditions.isAlphaOnly(","));
    }



    /**
     * Tests that only the correct characters are matched.
     */
    final public void testIsLineBreak()
    {
       assertTrue("CR", StringAdditions.isLineBreak('\r'));
       assertTrue("newline", StringAdditions.isLineBreak('\n'));
       assertFalse("Tab", StringAdditions.isLineBreak('\t'));
    }



    /**
     * Tests that line break conintuations are correctly detected.
     */
    final public void testIsLineBreakContinuation()
    {
       assertTrue("CR-NL", StringAdditions.isLineBreakContinuation('\r', '\n'));
       assertTrue("NL-CR", StringAdditions.isLineBreakContinuation('\n', '\r'));
       assertFalse("CR-CR", StringAdditions.isLineBreakContinuation('\r', '\r'));
       assertFalse("NL-NL", StringAdditions.isLineBreakContinuation('\n', '\n'));
       assertFalse("?-CR", StringAdditions.isLineBreakContinuation(' ', '\r'));
       assertFalse("NL-?", StringAdditions.isLineBreakContinuation('\n', ' '));
    }



    /**
     * Test doesStringContainWordsInCollection().
     */
    public void testDoesStringContainWordsInCollection()
    {
        String longString = "The quick brown fox jumps over the head of the lazy dog.";
        String aStringWithAllSpaces = "     ";
        String[] longArrayString = {"The", "quick", "brown", "fox", "lion", "tiger"};
        String[] subsetOfLongArrayString = {"The", "quick", "brown", "fox"};

        NSArray longArray = new NSArray(longArrayString);
        NSArray subsetOfLongArray = new NSArray(subsetOfLongArrayString);

        NSArray arrayWithOneObject = new NSArray("fox");
        NSArray arrayWithOneObjectWithSpace = new NSArray(" ");
        NSArray arrayWithNoObject = new NSArray();

        assertTrue(StringAdditions.doesStringContainWordsInCollection(longString, longArray, false));
        assertTrue(StringAdditions.doesStringContainWordsInCollection(longString, arrayWithOneObject, false));

        assertTrue( ! StringAdditions.doesStringContainWordsInCollection(longString, longArray, true));
        assertTrue(StringAdditions.doesStringContainWordsInCollection(longString, subsetOfLongArray, true));


        assertTrue( ! StringAdditions.doesStringContainWordsInCollection(longString, arrayWithNoObject, false));

        assertTrue(StringAdditions.doesStringContainWordsInCollection(aStringWithAllSpaces, arrayWithOneObjectWithSpace, false));
    }



    /**
     * Test doesStringContainAnyWordsInString().
     */
    public void testDoesStringContainAnyWordsInString()
    {
        String longString = "The quick brown fox jumps over the head of the lazy dog.";
        String longStringWithSpaces = "The     quick    brown   fox jumps  over the head of the lazy dog.";
        String aStringWithAllSpaces = "     ";
        String aStringWithNoSpaces = "TheQuickBrownFox";

        assertTrue(StringAdditions.doesStringContainAnyWordsInString(longString, "quick and beautiful"));
        assertTrue( ! StringAdditions.doesStringContainAnyWordsInString(longString, "lion tiger"));

        assertTrue(StringAdditions.doesStringContainAnyWordsInString(longStringWithSpaces, "quick head lazy"));
        assertTrue( ! StringAdditions.doesStringContainAnyWordsInString(longStringWithSpaces, "lion tiger"));
        assertTrue( ! StringAdditions.doesStringContainAnyWordsInString(longStringWithSpaces, " "));

        assertTrue( ! StringAdditions.doesStringContainAnyWordsInString(aStringWithAllSpaces, " "));

        assertTrue(StringAdditions.doesStringContainAnyWordsInString(aStringWithNoSpaces, "TheQuickBrownFox"));
        assertTrue( ! StringAdditions.doesStringContainAnyWordsInString(aStringWithNoSpaces, "TheQuickBrownFox2"));
        assertTrue(StringAdditions.doesStringContainAnyWordsInString(aStringWithNoSpaces, "Quick"));
    }



    /**
     * Test doesStringContainAllWordsInString().
     */
    public void testDoesStringContainAllWordsInString()
    {
        String longString = "The quick brown fox jumps over the head of the lazy dog.";

        assertTrue(StringAdditions.doesStringContainAllWordsInString(longString, "lazy fox"));
        assertTrue( ! StringAdditions.doesStringContainAllWordsInString(longString, "quick and beautiful"));
    }



    /**
     * Test doesStringContainAnyWordsInCollection().
     */
    public void testDoesStringContainAnyWordsInCollection()
    {
        String longString = "The quick brown fox jumps over the head of the lazy dog.";
        String aStringWithAllSpaces = "     ";
        String[] longArrayString = {"The", "quick", "brown", "fox", "lion", "tiger"};

        NSArray longArray = new NSArray(longArrayString);

        NSArray arrayWithOneObject = new NSArray("fox");
        NSArray arrayWithOneObjectWithSpace = new NSArray(" ");
        NSArray arrayWithNoObject = new NSArray();

        assertTrue(StringAdditions.doesStringContainAnyWordsInCollection(longString, longArray));
        assertTrue(StringAdditions.doesStringContainAnyWordsInCollection(longString, arrayWithOneObject));

        assertTrue( ! StringAdditions.doesStringContainAnyWordsInCollection(longString, arrayWithNoObject));

        assertTrue(StringAdditions.doesStringContainAnyWordsInCollection(aStringWithAllSpaces, arrayWithOneObjectWithSpace));
    }



    /**
     * Test doesStringContainAllWordsInCollection().
     */
    public void testDoesStringContainAllWordsInCollection()
    {
        String longString = "The quick brown fox jumps over the head of the lazy dog.";
        String aStringWithAllSpaces = "     ";
        String[] longArrayString = {"The", "quick", "brown", "fox", "lion", "tiger"};
        String[] subsetOfLongArrayString = {"The", "quick", "brown", "fox"};

        NSArray longArray = new NSArray(longArrayString);
        NSArray subsetOfLongArray = new NSArray(subsetOfLongArrayString);

        NSArray arrayWithOneObjectWithSpace = new NSArray(" ");
        NSArray arrayWithNoObject = new NSArray();

        assertTrue( ! StringAdditions.doesStringContainAllWordsInCollection(longString, longArray));
        assertTrue(StringAdditions.doesStringContainAllWordsInCollection(longString, subsetOfLongArray));

        assertTrue( ! StringAdditions.doesStringContainAllWordsInCollection(longString, arrayWithNoObject));

        assertTrue(StringAdditions.doesStringContainAllWordsInCollection(aStringWithAllSpaces, arrayWithOneObjectWithSpace));
    }



    /**
     * Test escape().
     */
    public void testEscape()
    {
        assertEquals("Bill Smith", StringAdditions.escape("Bill Smith", "'", "\\"));
        assertEquals("Karen O\\'Brien", StringAdditions.escape("Karen O'Brien", "'", "\\"));
        assertEquals("Karen O\\'\\'Brien", StringAdditions.escape("Karen O''Brien", "'", "\\"));
        assertEquals("Karen OBrien\\'", StringAdditions.escape("Karen OBrien'", "'", "\\"));
        assertEquals("Karen M. OBrien", StringAdditions.escape("Karen OBrien", "OBrien", "M. "));

        assertEquals("Bill Smith", StringAdditions.escape("Bill Smith", '\'', '\\'));
        assertEquals("Karen O\\'Brien", StringAdditions.escape("Karen O'Brien", '\'', '\\'));
        assertEquals("Karen O\\'\\'Brien", StringAdditions.escape("Karen O''Brien", '\'', '\\'));
        assertEquals("Karen OBrien\\'", StringAdditions.escape("Karen OBrien'", '\'', '\\'));

        try
        {
            StringAdditions.escape(null, '\'', '\\');
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test escapeSQLForFrontBase().
     */
    public void testEscapeSQLForFrontBase()
    {
        assertEquals("'Bill Smith'", StringAdditions.escapeSQLForFrontBase("Bill Smith"));
        assertEquals("'Karen O''Brien'", StringAdditions.escapeSQLForFrontBase("Karen O'Brien"));
        assertEquals("'Karen O''''Brien'", StringAdditions.escapeSQLForFrontBase("Karen O''Brien"));
        assertEquals("'Karen OBrien'''", StringAdditions.escapeSQLForFrontBase("Karen OBrien'"));

        try
        {
            StringAdditions.escapeSQLForFrontBase(null);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test padString().
     */
    public void testPad()
    {
        assertEquals(StringAdditions.pad("hello", ' ', 10), "hello     ");
        try
        {
            StringAdditions.pad("hello", ' ', 4);
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) {}
    }



    /**
     * Test downcaseFirstLetter().
     */
    public void testDowncaseFirstLetter()
    {
        assertEquals("helloWorld", StringAdditions.downcaseFirstLetter("HelloWorld"));
        assertEquals("helloWorld", StringAdditions.downcaseFirstLetter("helloWorld"));
    }


    /**
     * Test to ensure we can find the last path component regardless of platform
     *
     */
    public void testLastPathComponentXPlatform()
    {
        assertTrue("UNIX style",
        StringAdditions.lastPathComponentXPlatform("/this/that/the/other/file.name").
            equals("file.name"));

        assertTrue("Windows style",
        StringAdditions.lastPathComponentXPlatform("C:\\this\\that\\the\\other\\file.name").
            equals("file.name"));

        assertTrue("UNIX style, ends in slash",
        StringAdditions.lastPathComponentXPlatform("/this/that/the/other/file.name/").
            equals("file.name"));

        assertTrue("Windows style, ends in slash",
        StringAdditions.lastPathComponentXPlatform("C:\\this\\that\\the\\other\\file.name\\").
            equals("file.name"));

        assertTrue("no slash",
        StringAdditions.lastPathComponentXPlatform("file.name").
            equals("file.name"));

        assertTrue("UNIX style, only slash",
        StringAdditions.lastPathComponentXPlatform("/").
            equals(""));

        assertTrue("Windows style, only slash",
        StringAdditions.lastPathComponentXPlatform("\\").
            equals(""));
    }
}


