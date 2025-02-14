package net.global_village.foundation.tests;

import junit.framework.TestCase;
import net.global_village.foundation.DefaultValueRetrieval;

import com.webobjects.foundation.NSArray;


/*
 * Test the Default Value Retrieval functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DefaultValueRetrievalTest extends TestCase
{


    public DefaultValueRetrievalTest(String name)
    {
        super(name);
    }



    /**
     * Test defaultValueExists().
     */
    public void testDefaultValueExists()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultValueExists(this, "testString"));
        assertTrue(DefaultValueRetrieval.defaultValueExists(this, "testArray"));
        assertTrue( ! DefaultValueRetrieval.defaultValueExists(this, "isntThere"));

        assertTrue(DefaultValueRetrieval.defaultValueExists(this, "aDictionary.testString"));
        assertTrue(DefaultValueRetrieval.defaultValueExists(this, "aDictionary.testArray"));
        assertTrue( ! DefaultValueRetrieval.defaultValueExists(this, "aDictionary.isntThere"));

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultValueExists(getClass(), "testString"));
        assertTrue(DefaultValueRetrieval.defaultValueExists(getClass(), "testArray"));
        assertTrue( ! DefaultValueRetrieval.defaultValueExists(getClass(), "isntThere"));

        assertTrue(DefaultValueRetrieval.defaultValueExists(getClass(), "aDictionary.testString"));
        assertTrue(DefaultValueRetrieval.defaultValueExists(getClass(), "aDictionary.testArray"));
        assertTrue( ! DefaultValueRetrieval.defaultValueExists(getClass(), "aDictionary.isntThere"));
    }



    /**
     * Test defaultValue().
     */
    public void testDefaultValue()
    {
        // Object versions
        Object aString = DefaultValueRetrieval.defaultValue(this, "testString");
        Object anArray = DefaultValueRetrieval.defaultValue(this, "testArray");
        assertTrue(aString.equals("A test string"));
        assertTrue(anArray != null);
        try
        {
            DefaultValueRetrieval.defaultValue(this, "isntThere");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        Object aDictionaryString = DefaultValueRetrieval.defaultValue(this, "aDictionary.testString");
        Object aDictionaryArray = DefaultValueRetrieval.defaultValue(this, "aDictionary.testArray");
        assertTrue(aDictionaryString.equals("A different test string"));
        assertTrue(aDictionaryArray != null);
        assertTrue(String.class.isAssignableFrom(aDictionaryString.getClass()));
        assertTrue(NSArray.class.isAssignableFrom(aDictionaryArray.getClass()));
        try
        {
            DefaultValueRetrieval.defaultValue(this, "aDictionary.isntThere");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue( ! aString.equals(aDictionaryString));

        // Class versions
        aString = DefaultValueRetrieval.defaultValue(getClass(), "testString");
        anArray = DefaultValueRetrieval.defaultValue(getClass(), "testArray");
        assertTrue(aString.equals("A test string"));
        assertTrue(anArray != null);
        try
        {
            DefaultValueRetrieval.defaultValue(getClass(), "isntThere");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        aDictionaryString = DefaultValueRetrieval.defaultValue(getClass(), "aDictionary.testString");
        aDictionaryArray = DefaultValueRetrieval.defaultValue(getClass(), "aDictionary.testArray");
        assertTrue(aDictionaryString.equals("A different test string"));
        assertTrue(aDictionaryArray != null);
        assertTrue(String.class.isAssignableFrom(aDictionaryString.getClass()));
        assertTrue(NSArray.class.isAssignableFrom(aDictionaryArray.getClass()));
        try
        {
            DefaultValueRetrieval.defaultValue(getClass(), "aDictionary.isntThere");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue( ! aString.equals(aDictionaryString));
    }



    /**
     * Test defaultString().
     */
    public void testDefaultString()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultString(this, "testString").equals("A test string"));
        try
        {
            DefaultValueRetrieval.defaultString(this, "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultString(this, "aDictionary.testString").equals("A different test string"));
        try
        {
            DefaultValueRetrieval.defaultString(this, "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultString(getClass(), "testString").equals("A test string"));
        try
        {
            DefaultValueRetrieval.defaultString(getClass(), "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultString(getClass(), "aDictionary.testString").equals("A different test string"));
        try
        {
            DefaultValueRetrieval.defaultString(getClass(), "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test defaultArray().
     */
    public void testDefaultArray()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultArray(this, "testArray") != null);
        try
        {
            DefaultValueRetrieval.defaultArray(this, "testString");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultArray(this, "aDictionary.testArray") != null);
        try
        {
            DefaultValueRetrieval.defaultArray(this, "aDictionary.testString");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultArray(this, "testArray") !=
                      DefaultValueRetrieval.defaultArray(this, "aDictionary.testArray"));

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultArray(getClass(), "testArray") != null);
        try
        {
            DefaultValueRetrieval.defaultArray(getClass(), "testString");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultArray(getClass(), "aDictionary.testArray") != null);
        try
        {
            DefaultValueRetrieval.defaultArray(getClass(), "aDictionary.testString");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultArray(getClass(), "testArray") !=
                      DefaultValueRetrieval.defaultArray(getClass(), "aDictionary.testArray"));
    }



    /**
     * Test defaultNumberFormatter().
     */
    public void testDefaultNumberFormatter()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultNumberFormatter(this, "testNumberFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultNumberFormatter(this, "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultNumberFormatter(this, "aDictionary.testNumberFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultNumberFormatter(this, "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultNumberFormatter(getClass(), "testNumberFormatter") != DefaultValueRetrieval.defaultNumberFormatter(getClass(), "aDictionary.testNumberFormatter"));

        assertTrue(DefaultValueRetrieval.defaultNumberFormatter(getClass(), "testNumberFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultNumberFormatter(getClass(), "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultNumberFormatter(getClass(), "aDictionary.testNumberFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultNumberFormatter(getClass(), "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultNumberFormatter(getClass(), "testNumberFormatter") != DefaultValueRetrieval.defaultNumberFormatter(getClass(), "aDictionary.testNumberFormatter"));
    }



    /**
     * Test defaultTimestampFormatter().
     */
    public void testTimestampFormatter()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultTimestampFormatter(this, "testDateFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultTimestampFormatter(this, "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultTimestampFormatter(this, "aDictionary.testDateFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultTimestampFormatter(this, "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultTimestampFormatter(this, "testDateFormatter") != DefaultValueRetrieval.defaultTimestampFormatter(this, "aDictionary.testDateFormatter"));

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultTimestampFormatter(getClass(), "testDateFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultTimestampFormatter(getClass(), "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultTimestampFormatter(getClass(), "aDictionary.testDateFormatter") != null);
        try
        {
            DefaultValueRetrieval.defaultTimestampFormatter(getClass(), "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultTimestampFormatter(getClass(), "testDateFormatter") != DefaultValueRetrieval.defaultTimestampFormatter(getClass(), "aDictionary.testDateFormatter"));
    }



    /**
     * Test defaultBigInteger().
     */
    public void testDefaultBigInteger()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultBigInteger(this, "testInteger") != null);
        try
        {
            DefaultValueRetrieval.defaultBigInteger(this, "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigInteger(this, "aDictionary.testInteger") != null);
        try
        {
            DefaultValueRetrieval.defaultBigInteger(this, "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigInteger(this, "testInteger") != DefaultValueRetrieval.defaultBigInteger(this, "aDictionary.testInteger"));

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultBigInteger(getClass(), "testInteger") != null);
        try
        {
            DefaultValueRetrieval.defaultBigInteger(getClass(), "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigInteger(getClass(), "aDictionary.testInteger") != null);
        try
        {
            DefaultValueRetrieval.defaultBigInteger(getClass(), "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigInteger(getClass(), "testInteger") !=
 DefaultValueRetrieval.defaultBigInteger(getClass(), "aDictionary.testInteger"));
    }



    /**
     * Test defaultBigDecimal().
     */
    public void testDefaultBigDecimal()
    {
        // Object versions
        assertTrue(DefaultValueRetrieval.defaultBigDecimal(this, "testDecimalNumber") != null);
        try
        {
            DefaultValueRetrieval.defaultBigDecimal(this, "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigDecimal(this, "aDictionary.testDecimalNumber") != null);
        try
        {
            DefaultValueRetrieval.defaultBigDecimal(this, "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigDecimal(this, "testDecimalNumber") !=
 DefaultValueRetrieval.defaultBigDecimal(this, "aDictionary.testDecimalNumber"));

        // Class versions
        assertTrue(DefaultValueRetrieval.defaultBigDecimal(getClass(), "testDecimalNumber") != null);
        try
        {
            DefaultValueRetrieval.defaultBigDecimal(getClass(), "testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigDecimal(getClass(), "aDictionary.testDecimalNumber") != null);
        try
        {
            DefaultValueRetrieval.defaultBigDecimal(getClass(), "aDictionary.testArray");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }

        assertTrue(DefaultValueRetrieval.defaultBigDecimal(getClass(), "testDecimalNumber") !=
 DefaultValueRetrieval.defaultBigDecimal(getClass(), "aDictionary.testDecimalNumber"));
    }



}
