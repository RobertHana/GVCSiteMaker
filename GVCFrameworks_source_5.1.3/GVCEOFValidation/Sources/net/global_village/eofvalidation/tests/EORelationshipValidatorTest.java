package net.global_village.eofvalidation.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.gvctesteomodelsubclass.*;
import net.global_village.testeomodelbase.*;


/**
 * Tests for EORelationshipValidator
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class EORelationshipValidatorTest extends GVCJUnitEOTestCase
{
    protected EOEntity entity;
    protected EOEntity optionalEntity;
    protected EOEntity mandatoryEntity;
    protected EOEntity optionalEntitySubClass;
    protected EORelationship mandatoryToOne;
    protected EORelationship mandatoryToMany;
    protected EORelationship optionalToOne;
    protected EORelationship optionalToMany;
    protected EOEnterpriseObject optionalInstance;
    protected EOEnterpriseObject mandatoryInstance;
    

    /**
     * Designated constuctor.
     */
    public EORelationshipValidatorTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        entity = EOModelGroup.defaultGroup().entityNamed("RelationshipValidationTestEntity");
        optionalEntity = EOModelGroup.defaultGroup().entityNamed("EntityWithDecimalPK");
        mandatoryEntity = EOModelGroup.defaultGroup().entityNamed("ValidationBareEntity");
        optionalEntitySubClass = EOModelGroup.defaultGroup().entityNamed("DecimalPKSubclass");
        mandatoryToOne = entity.relationshipNamed("requiredBareEntity");
        mandatoryToMany = entity.relationshipNamed("requiredBareEntities");
        optionalToOne = entity.relationshipNamed("optionalDecimalEntity");
        optionalToMany = entity.relationshipNamed("optionalDecimalEntities");
        optionalInstance = new EntityWithDecimalPK();
        mandatoryInstance = new ValidationBareEntity();
    }



    /**
     * Tests checkTypeCompatibility
     */
    public void testcheckTypeCompatibility()
    {
        // Test DBC
        try
        {
            EORelationshipValidator.checkTypeCompatibility("test string", null);
            fail("DBC not correct");
        }
        catch (RuntimeException x) { }

        try
        {
            EORelationshipValidator.checkTypeCompatibility(mandatoryInstance, optionalToOne);
            fail("Accepted wrong to one entity");
        }
        catch (EOFValidationException e) { }

        // Test acceptance of non-null values
        try
        {
            EORelationshipValidator.checkTypeCompatibility(new NSMutableArray(mandatoryInstance), optionalToMany);
            fail("Accepted wrong to many entity");
        }
        catch (EOFValidationException e)
        {
            assertEquals("Dictionary contains incorrect relationship", e.propertyKey(), optionalToMany.name());
            assertEquals("Dictionary contains incorrect invalid value", e.failedValue(), mandatoryInstance);
            assertEquals("Dictionary contains incorrect validation failure", e.failureKey(), EOFValidation.InvalidValue);
        }

        try { EORelationshipValidator.checkTypeCompatibility(optionalInstance, optionalToOne); }
        catch (EOFValidationException e) { fail("Failed to accept valid to one"); }

        try { EORelationshipValidator.checkTypeCompatibility(new NSMutableArray(optionalInstance), optionalToMany); }
        catch (EOFValidationException e) { fail("Failed to accept valid to many"); }

        try
        {
            EORelationshipValidator.checkTypeCompatibility(new NSMutableArray(new Object[] {optionalInstance, optionalToOne}), optionalToMany);
            fail("Failed to check validity of all elements of to many"); 
        }
        catch (EOFValidationException e) { }

        try { EORelationshipValidator.checkTypeCompatibility(new DecimalPKSubclass(), optionalToOne); }
        catch (EOFValidationException e) { fail("Failed to accept valid to one sub class"); }


        // Test that null values are accepted as type compatible
        try
        {
            EORelationshipValidator.checkTypeCompatibility(null, optionalToMany);
            fail("Accepted null to many");
        }
        catch (EOFValidationException e) { }

        try
        {
            EORelationshipValidator.checkTypeCompatibility(NSKeyValueCoding.NullValue, optionalToMany);
            fail("Accepted EONullValue to to many"); 
        }
        catch (EOFValidationException e) { }

        try { EORelationshipValidator.checkTypeCompatibility(new NSMutableArray(), optionalToMany); }
        catch (EOFValidationException e) { fail("Failed to accept empty to many"); }

        try { EORelationshipValidator.checkTypeCompatibility(null, optionalToOne); }
        catch (EOFValidationException e) { fail("Failed to accept null to one"); }

        try { EORelationshipValidator.checkTypeCompatibility(NSKeyValueCoding.NullValue, optionalToOne); }
        catch (EOFValidationException e) { fail("Failed to accept EONullValue to to one"); }
    }



    /**
     * Tests for  checkNullity
     */
    public void testcheckNullity()
    {
        // Test DBC
        try
        {
            EORelationshipValidator.checkNullity("test string", null);
            fail("DBC not correct");
        }
        catch (RuntimeException x) { }

        // Many to One Checks
        try
        {
            EORelationshipValidator.checkNullity(NSKeyValueCoding.NullValue, mandatoryToOne);
            fail("Accepted null for mandatory to one entity");
        }
        catch (EOFValidationException e)
        {
            assertEquals("Dictionary contains correct attribute", e.propertyKey(), mandatoryToOne.name());
            assertEquals("Dictionary contains correct invalid value", e.failedValue(), NSKeyValueCoding.NullValue);
            assertEquals("Dictionary contains correct validation failure", e.failureKey(), EOFValidation.NullNotAllowed);
        }

        try
        {
            EORelationshipValidator.checkNullity(NSKeyValueCoding.NullValue, mandatoryToOne);
            fail("Accepted EONullValue.nullValue() for mandatory to one entity");
        }
        catch (EOFValidationException e) { }

        try { EORelationshipValidator.checkNullity(null, optionalToOne); }
        catch (EOFValidationException e) { fail("Failed to accept valid null to one"); }

        try { EORelationshipValidator.checkNullity(NSKeyValueCoding.NullValue, optionalToOne); }
        catch (EOFValidationException e) { fail("Failed to accept valid EONullValue.nullValue() to one"); }


        // Many to Many Checks
        try
        {
            EORelationshipValidator.checkNullity(new NSMutableArray(), mandatoryToMany);
            fail("Accepted empty mandatory to many entity");
        }
        catch (EOFValidationException e) {  }

        try { EORelationshipValidator.checkNullity(new NSMutableArray(), optionalToMany); }
        catch (EOFValidationException e) { fail("Failed to accept valid empty to many"); }
    }



    /**
     * Tests for displayNameForRelationship
     */
    public void testDisplayNameForRelationship()
    {
        try
        {
            EORelationshipValidator.displayNameForRelationship(null);
            fail("DBC not correct");
        }
        catch (RuntimeException r) { }

        assertEquals("Custom display name not found", EORelationshipValidator.displayNameForRelationship(entity.relationshipNamed("requiredBareEntity")), "mandatory unadorned entity");
        
        assertEquals("Display name not created", EORelationshipValidator.displayNameForRelationship(entity.relationshipNamed("optionalDecimalEntity")), "optional decimal entity");
    }



}
