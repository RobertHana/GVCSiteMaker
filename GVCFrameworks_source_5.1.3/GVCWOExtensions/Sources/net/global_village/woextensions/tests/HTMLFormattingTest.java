package net.global_village.woextensions.tests;

import junit.framework.*;

import net.global_village.woextensions.*;

/**
 * Test for HTMLFormatting class.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class HTMLFormattingTest extends TestCase
{

    /**
     * Constructor for HTMLFormattingTest.
     * @param name name of test
     */
    public HTMLFormattingTest(String name)
    {
        super(name);
    }


    /**
     * Basic test of string replacement logic.
     */
    final public void testReplaceCarriageReturnsWithHTMLInString()
    {
        String result = HTMLFormatting.replaceFormattingWithHTML("This is a\n\ntest of\nthe code");
        assertTrue("newlines converted", result.equals("This is a<br/><br/>test of<br/>the code"));
        
        result = HTMLFormatting.replaceFormattingWithHTML("This is a\r\rtest of\rthe code");
        assertTrue("CR converted", result.equals("This is a<br/><br/>test of<br/>the code"));

        result = HTMLFormatting.replaceFormattingWithHTML("This is a\n\rtest of\rthe code");
        assertTrue("newline-CR converted", result.equals("This is a<br/>test of<br/>the code"));

        result = HTMLFormatting.replaceFormattingWithHTML("This is a\r\ntest of\rthe code");
        assertTrue("CR-newline converted", result.equals("This is a<br/>test of<br/>the code"));

        result = HTMLFormatting.replaceFormattingWithHTML(null);
        assertTrue("null handling", result.equals(""));

    }

}
