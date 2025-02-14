package net.global_village.foundation.tests;

import java.util.Enumeration;

import junit.framework.TestCase;
import net.global_village.foundation.FixedWidthStringTokenizer;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;


/**
 * Test the fixed width string tokenizer functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class FixedWidthStringTokenizerTest extends TestCase
{
    protected String testString;
    protected NSArray widths, names;
    protected FixedWidthStringTokenizer tokenizer;


    public FixedWidthStringTokenizerTest(String name)
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

        testString = "1234567890";
        widths = new NSArray(new Integer[] {new Integer(1), new Integer(4), new Integer(5)});
        names = new NSArray(new String[] {"first", "second", "third"});

        tokenizer = new FixedWidthStringTokenizer(testString, widths, names);
    }



    /**
     * Test basic functioanlity.
     */
    public void testBasic()
    {
        assertEquals("1", tokenizer.nextToken());
        assertEquals("2345", tokenizer.nextToken());
        assertEquals("67890", tokenizer.nextToken());
    }



    /**
     * Test some contracts.
     */
    public void testContracts()
    {
        NSArray longWidths = new NSArray(new Integer[] {new Integer(1), new Integer(4), new Integer(6)});
        NSArray shortWidths = new NSArray(new Integer[] {new Integer(1), new Integer(4), new Integer(4)});

        try {
            new FixedWidthStringTokenizer(testString, longWidths);
            fail("Widths are too long");
        } catch (RuntimeException e) {}

        try {
            new FixedWidthStringTokenizer(testString, shortWidths);
            fail("Widths are too short");
        } catch (RuntimeException e) {}
    }



    /**
     * Test allRemainingTokens().
     */
    public void testAllRemainingTokens()
    {
        NSArray allTokens = tokenizer.allRemainingTokens();
        Enumeration tokenEnum = allTokens.objectEnumerator();

        assertEquals(tokenEnum.nextElement(), "1");
        assertEquals(tokenEnum.nextElement(), "2345");
        assertEquals(tokenEnum.nextElement(), "67890");
    }



    /**
     * Test allRemainingTokensWithNames().
     */
    public void testAllRemainingTokensWithNames()
    {
        NSDictionary allTokens = tokenizer.allRemainingTokensWithNames();
        Enumeration tokenEnum = allTokens.objectEnumerator();
        Enumeration nameEnum = allTokens.keyEnumerator();

        assertEquals(tokenEnum.nextElement(), "1");
        assertEquals(tokenEnum.nextElement(), "2345");
        assertEquals(tokenEnum.nextElement(), "67890");

        assertEquals(nameEnum.nextElement(), "first");
        assertEquals(nameEnum.nextElement(), "second");
        assertEquals(nameEnum.nextElement(), "third");
    }



}
