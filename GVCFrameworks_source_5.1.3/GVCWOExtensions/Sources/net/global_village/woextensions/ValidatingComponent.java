package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * ValidatingComponent should be used for custom components that can raise validation errors.
 * Subclass this and put the resulting component on a ValidatingPage subclass and all errors
 * that occur in the component will be propogated to the page.
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class ValidatingComponent extends net.global_village.woextensions.WOComponent
{
    private static final long serialVersionUID = 8824725452415169287L;

    protected static String InvalidDateStringReason = "Format.parseObject(String) failed";
    protected static String InvalidNumberStringReason = "not a valid character for a number with a pattern";
    protected static String InvalidNumberNotIntegerStringReason = "is not an integer value, and this number formatter does not accept floating point values";


    /**
     * Designated constructor.
     */
    public ValidatingComponent(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Returns the session downcast as a net.global_village.woextensions.WOSession.
     *
     * @return the session
     */
    public net.global_village.woextensions.WOSession gvcSession()
    {
        return (WOSession)session();
        /** ensure Result != null; **/
    }



    /**
     * Returns <code>true</code> if the exception is a formatter failure, <code>false</code> otherwise.
     *
     * @param exception the exception to examine
     * @return <code>true</code> if the exception is a formatter failure, <code>false</code> otherwise
     */
    public boolean isFormatterFailure(java.lang.Throwable exception)
    {
        /** require [valid_param] exception != null; **/

        return ((exception.getMessage().indexOf(InvalidDateStringReason) != -1) || (exception.getMessage().indexOf(InvalidNumberStringReason) != -1) || (exception.getMessage().indexOf(InvalidNumberNotIntegerStringReason) != -1));
    }



    /**
     * This method is called during the take values phase. As each value is taken from the request, any validation exceptions, formatter failures etc. are sent here. This is the best place to catch these as both the old and new values are available here. Also, WebObjects will leave the old value unchanged when a formatter fails, but will overwrite the old value when a validateAttribute method returns an exception.
     *
     * @param exception the exception raised
     * @param value the object that raised the exception
     * @param keyPath the keyPath of the exception ?
     */
    public void validationFailedWithException(Throwable exception, Object value, String keyPath)
    {
        /** require
        [valid_exception_param] exception != null;
        [valid_keyPath_param] keyPath != null;
        [top_level_is_ValidatingPage] topLevelComponent() instanceof ValidatingPage; **/

        if (isFormatterFailure(exception))
        {
            // Exceptions from formatter failures are not created by the GVCEOFValidation framework and do not have the customized exception messages.  Look up the correct message and use that.
            Throwable formattedException;
            if ((StringAdditions.isValidPropertyKeyPath(keyPath)) && (EOObject.isKeyPathToAttribute(this, keyPath)))
            {
                EOAttribute attribute = EOObject.attributeFromKeyPath(this, keyPath);
                formattedException = new EOFValidationException(attribute, EOFValidation.InvalidValue, value);
            }
            else
            {
                // if its not a valid key path, it must be a formatter exception for a page ivar, so use the default message
                formattedException = exception;
            }
            ((ValidatingPage)topLevelComponent()).addError(formattedException);
        }
        else
        {
            ((ValidatingPage)topLevelComponent()).addError(exception);
        }
    }



}
