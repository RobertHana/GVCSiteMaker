package net.global_village.eofvalidation.tests;

import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;



/**
 * Tests for EOFValidation class
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class EOFValidationTest extends GVCJUnitEOTestCase
{


    /**
     * Designated constuctor.
     */
    public EOFValidationTest(String name)
    {
        super(name);
    }



    /**
     * Tests whether empty strings are correctly handled as nulls.
     */
    public void testShouldTreatEmptyStringsAsNullDoesNotAffectTrueNulls()
    {
        AttributeValidationTestEntity testEntity = new AttributeValidationTestEntity();

        editingContext().insertObject(testEntity);

        try
        {
            EOFValidation.setShouldTreatEmptyStringsAsNull(false);
            editingContext().saveChanges();
        }
        catch (EOFValidationException e)
        {
            assertTrue(e.doesFailureExist("requiredString", EOFValidation.NullNotAllowed));
        }

        try
        {
            EOFValidation.setShouldTreatEmptyStringsAsNull(true);
            editingContext().saveChanges();
        }
        catch (EOFValidationException e)
        {
            assertTrue(e.doesFailureExist("requiredString", EOFValidation.NullNotAllowed));
        }
    }



    /**
     * Tests whether true empty strings are correctly handled as nulls.
     */
    public void testShouldTreatEmptyStringsWithEmptyStrings()
    {
        AttributeValidationTestEntity testEntity = new AttributeValidationTestEntity();
        testEntity.setRequiredString("");
        
        editingContext().insertObject(testEntity);

        try
        {
            EOFValidation.setShouldTreatEmptyStringsAsNull(false);
            editingContext().saveChanges();
        }
        catch (EOFValidationException e)
        {
            assertTrue( ! e.doesFailureExist("requiredString", EOFValidation.NullNotAllowed));
        }

        try
        {
            EOFValidation.setShouldTreatEmptyStringsAsNull(true);
            editingContext().saveChanges();
        }
        catch (EOFValidationException e)
        {
            assertTrue(e.doesFailureExist("requiredString", EOFValidation.NullNotAllowed));
        }
    }



    /**
     * Tests whether white space strings are correctly handled as nulls.
     */
    public void testShouldTreatEmptyStringsAsNullWithWhiteSpace()
    {
        AttributeValidationTestEntity testEntity = new AttributeValidationTestEntity();
        testEntity.setRequiredString("\r\n\t  \t");

        editingContext().insertObject(testEntity);

        try
        {
            EOFValidation.setShouldTreatEmptyStringsAsNull(false);
            editingContext().saveChanges();
        }
        catch (EOFValidationException e)
        {
            assertTrue( ! e.doesFailureExist("requiredString", EOFValidation.NullNotAllowed));
        }

        try
        {
            EOFValidation.setShouldTreatEmptyStringsAsNull(true);
            editingContext().saveChanges();
        }
        catch (EOFValidationException e)
        {
            assertTrue(e.doesFailureExist("requiredString", EOFValidation.NullNotAllowed));
        }
    }



}
