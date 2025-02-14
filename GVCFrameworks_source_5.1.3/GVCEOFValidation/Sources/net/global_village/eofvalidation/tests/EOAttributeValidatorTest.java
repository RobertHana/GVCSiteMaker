package net.global_village.eofvalidation.tests;

import java.math.*;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.GVCBoolean;
import net.global_village.gvcjunit.*;


/**
 * Tests for EOAttributeValidator
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class EOAttributeValidatorTest extends GVCJUnitEOTestCase
{
    protected EOEntity entity;
    protected EOAttribute stringAttribute;
    protected EOAttribute doubleAttribute;
    protected EOAttribute booleanAttribute;


    /**
     * Designated constuctor.
     */
    public EOAttributeValidatorTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        entity = EOModelGroup.defaultGroup().entityNamed("AttributeValidationTestEntity");
        stringAttribute = entity.attributeNamed("requiredString");
        doubleAttribute = entity.attributeNamed("requiredDouble");
        booleanAttribute = entity.attributeNamed("requiredBoolean");
    }



    /**
     * Tests checkTypeCompatibility
     */
    public void testcheckTypeCompatibility()
    {
        try
        {
            EOAttributeValidator.checkTypeCompatibility("test string", null);
            fail("DBC not correct");
        }
        catch (RuntimeException x) { }

        try { EOAttributeValidator.checkTypeCompatibility("test string", stringAttribute); }
        catch (EOFValidationException e) { fail("correct class failed type compatibility check"); }

        try { EOAttributeValidator.checkTypeCompatibility(new Double(1/3), doubleAttribute); }
        catch (EOFValidationException e) { fail("correct sub-class failed type compatibility check"); }

        try { EOAttributeValidator.checkTypeCompatibility(new Integer(1), doubleAttribute); }
        catch (EOFValidationException e) { fail("coerceable class failed type compatibility check"); }

        try { EOAttributeValidator.checkTypeCompatibility(null, stringAttribute); }
        catch (EOFValidationException e) {fail("null value failed type compatibility check"); }

        try { EOAttributeValidator.checkTypeCompatibility(NSKeyValueCoding.NullValue, stringAttribute); }
        catch (EOFValidationException e) {fail("EONullValue failed type compatibility check"); }

        try
        {
            EOAttributeValidator.checkTypeCompatibility("17", doubleAttribute);
            fail("Accepted string for double.");
        }
        catch (EOFValidationException e)
        {
            assertEquals("Dictionary contains correct attribute", e.propertyKey(), doubleAttribute.name());
            assertEquals("Dictionary contains correct invalid value", e.failedValue(), "17");
            assertEquals("Dictionary contains correct validation failure", e.failureKey(), EOFValidation.InvalidValue);
        }

        // Check custom attribute types
        try { EOAttributeValidator.checkTypeCompatibility(new GVCBoolean(true), booleanAttribute); }
        catch (EOFValidationException e) { fail("GVC Boolean failed type compatibility check"); }

        try { EOAttributeValidator.checkTypeCompatibility("Y", booleanAttribute);
            fail("Accepted string for GVC Boolean"); }
        catch (EOFValidationException e) {  }
    }



    /**
     * Tests for  checkNullity
     */
    public void testcheckNullity()
    {
        try
        {
            EOAttributeValidator.checkNullity(null, null);
            fail("DBC not correct");
        }
        catch (RuntimeException x) { }

        try { EOAttributeValidator.checkNullity(null, entity.attributeNamed("optionalString")); }
        catch (EOFValidationException e) { fail("null check failed for nullable attribute"); }

        try { EOAttributeValidator.checkNullity(null, entity.attributeNamed("optionalString")); }
        catch (EOFValidationException e) { fail("null check failed for nullable attribute"); }

        try { EOAttributeValidator.checkNullity(null, entity.attributeNamed("theID")); }
        catch (EOFValidationException e) { fail("null check failed for PK"); }

        try
        {
            EOAttributeValidator.checkNullity(NSKeyValueCoding.NullValue, stringAttribute);
            fail("Allowed null for not null attribute");
        }
        catch (EOFValidationException e)
        {
            assertEquals("Dictionary contains incorrect attribute", e.propertyKey(), stringAttribute.name());
            assertEquals("Dictionary contains incorrect invalid value", e.failedValue(), NSKeyValueCoding.NullValue);
            assertEquals("Dictionary contains incorrect validation failure", e.failureKey(), EOFValidation.NullNotAllowed);
        }

        try
        {
            EOAttributeValidator.checkNullity(NSKeyValueCoding.NullValue, stringAttribute);
            fail("Allowed EONullValue.nullValue() for not null attribute");
        }
        catch (EOFValidationException e) {}
    }



    /**
     * Tests for checkWidth
     */
    public void testcheckWidth() 
    {
        try
        {
            EOAttributeValidator.checkWidth("123", null);
            fail("null attribute precondition not detected");
        }
        catch (RuntimeException x) { }

        try
        {
            EOAttributeValidator.checkWidth(null, stringAttribute);
            fail("null value precondition not detected");
        }
        catch (RuntimeException x) { }

        try { EOAttributeValidator.checkWidth("0123456789", entity.attributeNamed("shortString")); }
        catch (EOFValidationException e) { fail("too long check failed for OK string"); }

        try { EOAttributeValidator.checkWidth("0123456789", entity.attributeNamed("shortData")); }
        catch (EOFValidationException e) { fail("too long check failed for OK data"); }

        try { EOAttributeValidator.checkWidth(new Integer(1), entity.attributeNamed("requiredInteger")); }
        catch (EOFValidationException e) { fail("too long check failed for requiredInteger"); }

        try
        {
            EOAttributeValidator.checkWidth("0123456789X", entity.attributeNamed("shortString"));
            fail("too long check failed for long string"); 
        }
        catch (EOFValidationException e) { }

        try
        {
            EOAttributeValidator.checkWidth("0123456789X", entity.attributeNamed("shortData"));
            fail("too long check failed for long data");
        }
        catch (EOFValidationException e)
        {
            assertEquals("Dictionary contains incorrect attribute", e.propertyKey(), entity.attributeNamed("shortData").name());
            assertEquals("Dictionary contains incorrect invalid value", e.failedValue(), "0123456789X");
            assertEquals("Dictionary contains incorrect validation failure", e.failureKey(), EOFValidation.TooLong);
        }
   }



    /**
     * Test for isGeneratedPrimaryKey
     */
    public void testIsGeneratedPrimaryKey()
    {
        try
        {
            EOAttributeValidator.isGeneratedPrimaryKey(null);
            fail("DBC not correct");
        }
        catch (RuntimeException r) {}

        assertTrue("Integer PK key improperly detected", EOAttributeValidator.isGeneratedPrimaryKey(entity.attributeNamed("theID")));

        assertTrue("Decimal PK key improperly detected", EOAttributeValidator.isGeneratedPrimaryKey(EOModelGroup.defaultGroup().entityNamed("EntityWithDecimalPK").attributeNamed("theDecimalPK")));

        assertTrue("NSData PK key improperly detected", EOAttributeValidator.isGeneratedPrimaryKey(EOModelGroup.defaultGroup().entityNamed("EntityWithNSDataPK").attributeNamed("theDataPK")));

        assertTrue("Compund PK key improperly detected", ! EOAttributeValidator.isGeneratedPrimaryKey(EOModelGroup.defaultGroup().entityNamed("EntityWithCompoundPK").attributeNamed("key1")));

        assertTrue("NSData non-PK attribute improperly detected", ! EOAttributeValidator.isGeneratedPrimaryKey(EOModelGroup.defaultGroup().entityNamed("EntityWithDecimalPK").attributeNamed("dataAttribute")));

        assertTrue("Integer non-PK attribute improperly detected", ! EOAttributeValidator.isGeneratedPrimaryKey(entity.attributeNamed("requiredInteger")));
    }



    /**
     * Tests for classForAttribute
     */
    public void testClassForAttribute()
    {
        try
        {
            EOAttributeValidator.classForAttribute(null);
            fail("DBC not correct");
        }
        catch (RuntimeException r) {}

       assertEquals("NSString not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredString")), String.class);

       assertEquals("NSData not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredData")), NSData.class);

       assertEquals("NSNumber from integer not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredInteger")), Number.class);

       assertEquals("NSNumber from double not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredDouble")), Number.class);

       assertEquals("NSDecimalNumber not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredDecimalNumber")), BigDecimal.class);

       assertEquals("NSCalendarDate not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredTimestamp")), NSTimestamp.class);

       assertEquals("Custom class not translated", EOAttributeValidator.classForAttribute(entity.attributeNamed("requiredBoolean")), net.global_village.foundation.GVCBoolean.class);
    }



    /**
     * Tests for displayNameForAttribute
     */
    public void testDisplayNameForAttribute()
    {
        try
        {
            EOAttributeValidator.displayNameForAttribute(null);
            fail("DBC not correct");
        }
        catch (RuntimeException r) { }

        assertEquals("Custom display name not found", EOAttributeValidator.displayNameForAttribute(stringAttribute), "mandatory string");

        assertEquals("Display name not created", EOAttributeValidator.displayNameForAttribute(entity.attributeNamed("optionalString")), "optional string");
    }



}
