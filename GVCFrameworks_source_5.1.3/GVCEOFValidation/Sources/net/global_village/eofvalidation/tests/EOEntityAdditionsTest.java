package net.global_village.eofvalidation.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;


/**
 * Tests for localization on EOEntityAdditions
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */  
public class EOEntityAdditionsTest extends GVCJUnitEOTestCase
{
    protected EOEntity entity;
    protected EOEntity subClassedEntity;
    protected EOEntity dummyEntity;


    /**
     * Designated constuctor.
     */
    public EOEntityAdditionsTest(String name)
    {
        super(name);
    }



    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        entity = EOModelGroup.defaultGroup().entityNamed("AttributeValidationTestEntity");
        dummyEntity = EOModelGroup.defaultGroup().entityNamed("ValidationBareEntity");
        subClassedEntity = EOModelGroup.defaultGroup().entityNamed("AttributeValidationTestSubClass");
    }


    
    /**
     * Tests displayNameForEntity
     */
    public void testDisplayNameForEntity()
    {
        // DBC
        try
        {
            EOEntityAdditions.displayNameForEntity(null);
            fail("Missing null entity precondition");
        }
        catch (Throwable t) {}

        // Test defined displayName
        assertEquals(EOEntityAdditions.displayNameForEntity(entity), "Test Entity For Attribute Validation");

        // Test missing displayName
        assertEquals(EOEntityAdditions.displayNameForEntity(dummyEntity), "Validation Bare Entity");
    }



    /**
     * Tests for displayNameForPropertyNamed
     */
    public void testDisplayNameForPropertyNamed()
    {
        try
        {
            EOEntityAdditions.displayNameForPropertyNamed("requiredString", null);
            fail("null entity precondition missing");
        }
        catch (RuntimeException r) { }

        try
        {
            EOEntityAdditions.displayNameForPropertyNamed(null, entity);
            fail("null propertyName precondition missing");
        }
        catch (RuntimeException r) { }

        assertTrue("Custom display name not found", EOEntityAdditions.displayNameForPropertyNamed("requiredString", entity).equals("mandatory string"));

        assertTrue("Display name not created", EOEntityAdditions.displayNameForPropertyNamed("optionalString", entity).equals("optional string"));
    }



    /**
     *  Test isNullValue
     */
    public void testisNull()
    {
        assertTrue("null improperly allowed", EOEntityAdditions.isNull(null));
        assertTrue("EONullValue improperly allowed", EOEntityAdditions.isNull(NSKeyValueCoding.NullValue));
        assertTrue("non-null value improperly disallowed", ! EOEntityAdditions.isNull("x17"));
    }

    
    /**
     * Test propertyWithName
     */
    public void testPropertyWithName()
    {
        EOEntity theEntity = EOModelGroup.defaultGroup().entityNamed("SimpleRelationshipTestEntity");
        assertEquals(EOEntityAdditions.propertyWithName(theEntity, "name"), theEntity.attributeNamed("name"));
        assertEquals(EOEntityAdditions.propertyWithName(theEntity, "mandatoryToOne"), theEntity.relationshipNamed("mandatoryToOne"));
        assertEquals(EOEntityAdditions.propertyWithName(theEntity, "ficticious"), null);
    }


    


}
