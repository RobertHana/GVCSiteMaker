package net.global_village.foundation.tests;

import java.util.Enumeration;

import junit.framework.TestCase;
import net.global_village.foundation.DelimitedStringTokenizer;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;


/**
 * Test the delimited string tokenizer functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DelimitedStringTokenizerTest extends TestCase
{
    protected String testString;
    protected NSArray names;
    protected DelimitedStringTokenizer tokenizer;


    public DelimitedStringTokenizerTest(String name)
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

        testString = "this\t'two quote''\t test'\tis\ta\t\"tab\ttest\"\t'quote''test'\t";
        names = new NSArray(new String[] {"first", "second", "third", "fourth", "fifth", "sixth", "seventh"});

        tokenizer = new DelimitedStringTokenizer(testString, "\t", "'\"", names);
    }



    /**
     * Test basic functionality.
     */
    public void testBasic()
    {
        assertEquals("this", tokenizer.nextToken());
        assertEquals("two quote'\t test", tokenizer.nextToken());
        assertEquals("is", tokenizer.nextToken());
        assertEquals("a", tokenizer.nextToken());
        assertEquals("tab\ttest", tokenizer.nextToken());
        assertEquals("quote'test", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertTrue( ! tokenizer.hasMoreTokens());
    }



    /**
     * Test allRemainingTokens().
     */
    public void testAllRemainingTokens()
    {
        NSArray allTokens = tokenizer.allRemainingTokens();
        Enumeration tokenEnum = allTokens.objectEnumerator();

        assertEquals("this", tokenEnum.nextElement());
        assertEquals("two quote'\t test", tokenEnum.nextElement());
        assertEquals("is", tokenEnum.nextElement());
        assertEquals("a", tokenEnum.nextElement());
        assertEquals("tab\ttest", tokenEnum.nextElement());
        assertEquals("quote'test", tokenEnum.nextElement());
        assertEquals("", tokenEnum.nextElement());
        assertTrue( ! tokenEnum.hasMoreElements());
    }



    /**
     * Test allRemainingTokensWithNames().
     */
    public void testAllRemainingTokensWithNames()
    {
        NSDictionary allTokens = tokenizer.allRemainingTokensWithNames();

        assertEquals("this", allTokens.objectForKey("first"));
        assertEquals("two quote'\t test", allTokens.objectForKey("second"));
        assertEquals("is", allTokens.objectForKey("third"));
        assertEquals("a", allTokens.objectForKey("fourth"));
        assertEquals("tab\ttest", allTokens.objectForKey("fifth"));
        assertEquals("quote'test", allTokens.objectForKey("sixth"));
        assertEquals("", allTokens.objectForKey("seventh"));
    }


    /**
     * Test restOfString().
     */
    public void testRestOfString()
    {
        assertEquals("Does not return entire string from new tokenizer", tokenizer.restOfString(), testString);
        assertEquals("restOfString moved mark.", tokenizer.restOfString(), testString);
        
        tokenizer.nextToken();
        assertEquals("restOfString does not return correct partial string", tokenizer.restOfString(), 
               "'two quote''\t test'\tis\ta\t\"tab\ttest\"\t'quote''test'\t");

        while (tokenizer.hasMoreTokens()) tokenizer.nextToken();
        assertEquals("restOfString on exhausted tokenizer does not return empty string", tokenizer.restOfString(), "");
    }
        

    /**
     * Tests for a string index out of bounds bug that happened when the last token was shorter than the delimeter lenght.
     */
    public void testLastElement() 
    {
        testString = "this, is, test, a";
        tokenizer = new DelimitedStringTokenizer(testString, ", ", "", null);
        assertEquals("this", tokenizer.nextElement());
        assertEquals("is", tokenizer.nextElement());
        assertEquals("test", tokenizer.nextElement());
        assertEquals("a", tokenizer.nextElement());
    }

    
    /**
     * Test multiple quotes.
     */
    public void testNastyQuotes()
    {
        String quotedString = "'''this is an O''Brien''','''test''','''sort of test''','''You know?''','''fifth''',\"\"\"Sixth\"\"\",'''and last'''";
        DelimitedStringTokenizer tokenizer2 = new DelimitedStringTokenizer(quotedString, ",", "'\"", names);
 
        assertEquals("'this is an O'Brien'", tokenizer2.nextToken());
        assertEquals("'test'", tokenizer2.nextToken());
        assertEquals("'sort of test'", tokenizer2.nextToken());
        assertEquals("'You know?'", tokenizer2.nextToken());
        assertEquals("'fifth'", tokenizer2.nextToken());
        assertEquals("\"Sixth\"", tokenizer2.nextToken());
        assertEquals("'and last'", tokenizer2.nextToken());
        assertTrue( ! tokenizer2.hasMoreTokens());
    }



    /**
     * Test mismatched quotes.
     */
    public void testMismatchedQuotes()
    {
        String missingEndQuote = "\"\"This is a quote\" -- someone said this\"\t\"\"Single quote at the start\"\t\"Single quote at the end\"\"\t\"Single \" in the middle\"";
        DelimitedStringTokenizer tokenizer2 = new DelimitedStringTokenizer(missingEndQuote, ",", "'\"");
 
        /* These don't work, but should... definitely a bug.
        assertEquals("\"This is a quote\" -- someone said this", tokenizer2.nextToken());
        assertEquals("\"Single quote at the start", tokenizer2.nextToken());
        assertEquals("Single quote at the end\"", tokenizer2.nextToken());
        assertEquals("Single \" in the middle", tokenizer2.nextToken()); */
    }



}
