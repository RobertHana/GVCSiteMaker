package net.global_village.foundation;

import java.util.*;

import com.webobjects.foundation.*;


/**
 * Validation exception utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class ValidationExceptionAdditions extends Object
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private ValidationExceptionAdditions()
    {
        super();
    }



    /**
     * Returns an array of all the exceptions contained in <code>additionalExceptions()</code>, or just this exception if there are no additional exceptions.
     *
     * @param exception exception from which to extract unaggregated exceptions
     * @return an array of all the exceptions
     */
    public static NSArray unaggregateExceptions(com.webobjects.foundation.NSValidation.ValidationException exception)
    {
        /** require [valid_exception] exception != null;  **/
        NSMutableArray subExceptions = new NSMutableArray(exception);

        if (exception.additionalExceptions() != null)
        {
            subExceptions.addObjectsFromArray(exception.additionalExceptions());
        }

        JassAdditions.post("ValidationExceptionAdditions", "unaggregateExceptions", exception.additionalExceptions() == null ? subExceptions.count() == 1 : exception.additionalExceptions().count() + 1 == subExceptions.count());
        return subExceptions;

        /** ensure [valid_result] Result != null; **/
    }

    

    /**
     * Returns an array of localized messages of this exception and any exceptions in this' <code>additionalExceptions()</code>.  
     * Note that the localization is based on the <em>server's</em> locale, so this method is not appropriate for web apps.
     *
     * @see Throwable#getLocalizedMessage()
     *
     * @param exception exception from which to extract unaggregated exceptions
     * @return an array of localized messages, one message per exception
     */
    public static NSArray localizedMessagesForUnaggregatedExceptions(com.webobjects.foundation.NSValidation.ValidationException exception)
    {
        /** require [valid_exception] exception != null;  **/
        NSMutableArray exceptionMessages = new NSMutableArray();
        Enumeration exceptions = ValidationExceptionAdditions.unaggregateExceptions(exception).objectEnumerator();
        while (exceptions.hasMoreElements())
        {
            com.webobjects.foundation.NSValidation.ValidationException x = (com.webobjects.foundation.NSValidation.ValidationException)exceptions.nextElement();
            exceptionMessages.addObject(x.getLocalizedMessage());
        }

        return exceptionMessages;

        /** ensure [valid_result] Result != null; **/
    }
    

}
