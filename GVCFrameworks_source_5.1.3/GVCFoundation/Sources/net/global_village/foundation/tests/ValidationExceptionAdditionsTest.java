package net.global_village.foundation.tests;

import junit.framework.TestCase;
import net.global_village.foundation.ValidationExceptionAdditions;

import com.webobjects.foundation.NSArray;


/*
 * Test the Default Value Retrieval functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ValidationExceptionAdditionsTest extends TestCase
{


    public ValidationExceptionAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Tests for unaggregateExceptions.
     */
    public void testUnaggregateExceptions()
    {
        com.webobjects.foundation.NSValidation.ValidationException parentException = new com.webobjects.foundation.NSValidation.ValidationException("Test parent");
        com.webobjects.foundation.NSValidation.ValidationException childException = new com.webobjects.foundation.NSValidation.ValidationException("Test child");

        com.webobjects.foundation.NSValidation.ValidationException aggregateException = com.webobjects.foundation.NSValidation.ValidationException.aggregateExceptionWithExceptions(new NSArray(new Object[] {parentException, childException}));

        assertEquals("Number of exceptions not what was expected", ValidationExceptionAdditions.unaggregateExceptions(aggregateException).count(), 2);
        assertEquals("Number of exceptions not what was expected", ValidationExceptionAdditions.unaggregateExceptions(childException).count(), 1);
    }

    
    
    /**
     * Tests for unaggregateExceptions.
     */
    public void testLocalizedMessagesForUnaggregatedExceptions()
    {
        com.webobjects.foundation.NSValidation.ValidationException parentException = new com.webobjects.foundation.NSValidation.ValidationException("Test parent");
        com.webobjects.foundation.NSValidation.ValidationException childException = new com.webobjects.foundation.NSValidation.ValidationException("Test child");

        com.webobjects.foundation.NSValidation.ValidationException aggregateException = com.webobjects.foundation.NSValidation.ValidationException.aggregateExceptionWithExceptions(new NSArray(new Object[] {parentException, childException}));

        assertEquals("Number of exceptions not what was expected", ValidationExceptionAdditions.localizedMessagesForUnaggregatedExceptions(aggregateException).count(), 2);
        assertEquals("Number of exceptions not what was expected", ValidationExceptionAdditions.localizedMessagesForUnaggregatedExceptions(aggregateException), new NSArray(new String[] {"Test parent", "Test child"}));
    
        assertEquals("Number of exceptions not what was expected", ValidationExceptionAdditions.localizedMessagesForUnaggregatedExceptions(childException).count(), 1);

    }
    


}
