package net.global_village.eofvalidation.tests;

import java.math.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Tests for EOFValidation's EOFValidationException class.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class EOFValidationExceptionTest extends GVCJUnitEOTestCase
{
    protected EOEntity entity;
    protected EOEntity relationshipEntity;
    protected EOAttribute attribute;
    protected EORelationship relationship;
    protected EOEnterpriseObject eoObject;
    protected final String stringTooLong = "Unreasonably long string";
    protected RuntimeException FIXME;// fakeAdaptorFailureException;


    /**
     * Designated constuctor.
     */
    public EOFValidationExceptionTest(String name)
    {
        super(name);
    }



    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        eoObject = EOUtilities.createAndInsertInstance(editingContext(), "AttributeValidationTestEntity");
        entity = EOModelGroup.defaultGroup().entityNamed("AttributeValidationTestEntity");
        attribute = entity.attributeNamed("requiredString");

        relationshipEntity = EOModelGroup.defaultGroup().entityNamed("RelationshipValidationTestEntity");
        relationship = relationshipEntity.relationshipNamed("requiredBareEntity");
/*
        // Create fakeAdaptorFailureException as they are difficult to set up.  This is a very minimal example and is just enough to trick out code into believing it is a real exception.
        NSMutableDictionary adaptorFailureDictionary = new NSMutableDictionary();
        NSMutableDictionary failedDatabaseOperationDictionary = new NSMutableDictionary();
        EOEnterpriseObject someObject = new AttributeValidationTestEntity();

        // Just to indicate this is an adaptor failure exception.
        adaptorFailureDictionary.setObjectForKey("Place holder", EOAdaptorChannel.FailedAdaptorOperationKey);

        failedDatabaseOperationDictionary.setObjectForKey(entity, "_entity");
        failedDatabaseOperationDictionary.setObjectForKey(someObject, "_object");
        adaptorFailureDictionary.setObjectForKey(failedDatabaseOperationDictionary, "EOFailedDatabaseOperationKey");

        fakeAdaptorFailureException =  new RuntimeException("Ignore" + " " + "Ignore");
*/
    }



    /**
     * Tests getLocalizedMessage
     */
    public void testGetLocalizedMessage()
    {
        EOFValidationException exception = new EOFValidationException(null, attribute.entity().name(), attribute.name(), EOFValidation.NullNotAllowed, null);
        assertEquals(exception.getLocalizedMessage(), "The mandatory string must be entered for this Test Entity For Attribute Validation.");

        exception = new EOFValidationException(null, attribute.entity().name(), attribute.name(), EOFValidation.TooLong, stringTooLong);
        exception.setFailedValue(stringTooLong);
        assertEquals("The mandatory string you entered, '" + stringTooLong + "', is longer than the 50 characters permitted.", exception.getLocalizedMessage());
    }

    
    
    /**
     * Tests for creation of EOFValidationException.
     */
    public void testEOFValidationException()
    {
        // Attribute version
        EOFValidationException testException = new EOFValidationException(null, attribute.entity().name(), attribute.name(), EOFValidation.TooLong, stringTooLong);

        assertEquals(testException.getLocalizedMessage(), "The mandatory string you entered, '" + stringTooLong + "', is longer than the 50 characters permitted.");
        assertEquals(testException.propertyKey(), attribute.name());
        assertEquals(testException.failedValue(), stringTooLong);
        assertEquals(testException.failureKey(), EOFValidation.TooLong);

        // Relationship version
        testException = new EOFValidationException(null, relationship.entity().name(), relationship.name(), EOFValidation.NullNotAllowed, NSKeyValueCoding.NullValue);
        assertEquals(testException.getLocalizedMessage(), "You must enter a mandatory unadorned entity for this Relationship Validation Test Entity");
        assertEquals(testException.propertyKey(), relationship.name());
        assertEquals(testException.failedValue(), NSKeyValueCoding.NullValue);
        assertEquals(testException.failureKey(), EOFValidation.NullNotAllowed);
    }



    /**
     * Tests for exception checking methods.
     */
    public void testExceptionCheckingMethods()
    {
        AttributeValidationTestEntity testEntity = new AttributeValidationTestEntity();

        // FIXME commented out values no longer work...
        editingContext().insertObject(testEntity);
        //testEntity.takeValueForKey("7", "requiredInteger");
        testEntity.setRequiredDecimalNumber(new BigDecimal("13.13"));
        //testEntity.takeValueForKey(new NSTimestamp(), "requiredDouble");
        //testEntity.takeValueForKey(NSKeyValueCoding.NullValue, "requiredString");
        testEntity.setShortString("1234567890X");

        try
        {
            editingContext().saveChanges();
            fail("InvalidChanges saved");
        }
        catch (EOFValidationException e)
        {
            assertTrue("Failed to find exception with class method", e.doesFailureExist("requiredInteger", EOFValidation.NullNotAllowed));

            EOFValidationException specificException = e.exceptionForFailure("requiredInteger", EOFValidation.NullNotAllowed);
            assertTrue("Failed to verify exception with class method", specificException.isExceptionForFailure("requiredInteger", EOFValidation.NullNotAllowed));
            assertTrue("Failed to find exception with instance method",
                   specificException.doesFailureExist("requiredInteger", EOFValidation.NullNotAllowed));
        }
    }


    /**
     * Tests hasPropertyKey() property keypath handling code.
     */
    public void testHasPropertyKey()
    {
    		assertFalse("Accepted null as valid property key", EOFValidationException.hasPropertyKey(null));
    		assertFalse("Accepted foo as valid property key", EOFValidationException.hasPropertyKey("foo"));
    		assertFalse("Accepted foo.bar as valid property key", EOFValidationException.hasPropertyKey("foo.bar"));
    		assertTrue("Did not accept foo.bar.bax as valid property key", EOFValidationException.hasPropertyKey("foo.bar.baz"));
    }
    
}
