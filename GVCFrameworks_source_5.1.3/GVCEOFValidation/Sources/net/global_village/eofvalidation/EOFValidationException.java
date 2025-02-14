package net.global_village.eofvalidation;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * <p>An exception that contains a message key path, which can be used to retrieve a localized text message for display to
 * the user.</p>
 *
 * <p><b>Example validateForSave Usage:</b></p>
 *
 * <pre>
 * public void validateForSave() throws ValidationException
 * {
 *    NSMutableArray exceptions = new NSMutableArray();
 *
 *    try
 *    {
 *        super.validateForSave();
 *    }
 *    catch (NSValidation.ValidationException e)
 *    {
 *        exceptions.addObject(e);
 *    }
 *
 *    if (numPages().intValue() &gt; ( marriage().pagesPerSheet().intValue() * numSheets().intValue()) )
 *    {
 *        exceptions.addObject(new EOFValidationException(this, &quot;numPagesNumSheetsMismatch&quot;);
 *    }
 *
 *    if (exceptions.count() &gt; 0)
 *    {
 *        throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
 *    }
 * }
 * </pre>
 *
 *
 * <p><b>Example validateProperty Usage:</b></p>
 *
 * <pre>
 * public Object validateNumPages(Object value)
 * {
 *     if ( numPages().intValue() < 2 || numPages().intValue() % 2 != 0 )
 *     {
 *         throw new EOFValidationException(this, NUMPAGES, EOFValidation.InvalidValue, value);
 *         // OR throw EOFValidationException.invalidValue(this, NUMPAGES, value);
 *     }
 *     return value;
 * }
 *</pre>
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 * @version $Revision: 21$
 */
public class EOFValidationException extends com.webobjects.foundation.NSValidation.ValidationException
{
    protected Object failedValue;
    private Exception originalException;


    /**
     * Checks that a message key path is valid.
     *
     * @see StringAdditions#isValidPropertyKeyPath(String)
     * @param messageKeyPath the message key path to check
     * @return <code>true</code> if messageKeyPath is a valid property keypath
     */
    public static boolean isValidMessageKeyPath(String messageKeyPath)
    {
        /** require [valid_param] messageKeyPath != null; **/
        return StringAdditions.isValidPropertyKeyPath(messageKeyPath);
    }



    /**
     * Helper method for <code>propertyKey()</code> and <code>hasPropertyKey()</code>.
     *
     * @param messageKeyPath the failure message key to use
     * @return the property key if one exists, <code>null</code> otherwise
     */
    protected static String findPropertyKey(String messageKeyPath)
    {
        /** require
        [valid_param] messageKeyPath != null;
        [is_message_key_path] isValidMessageKeyPath(messageKeyPath); **/

        String middlePart = null;
        String firstPart = StringAdditions.objectKeyPathFromKeyPath(messageKeyPath);
        if (StringAdditions.isValidPropertyKeyPath(firstPart))
        {
            middlePart = StringAdditions.propertyNameFromKeyPath(firstPart);
        }
        return middlePart;
    }



    /**
     * Returns <code>true</code> if the message key path has a property component.
     *
     * @param messageKeyPath the failure message key to use
     * @return <code>true</code> if the message key path has a property component
     */
    public static boolean hasPropertyKey(String messageKeyPath)
    {
        return (messageKeyPath != null) &&
        		  (isValidMessageKeyPath(messageKeyPath)) &&
        		  findPropertyKey(messageKeyPath) != null;
    }



    /**
     * Returns the property key (the middle part of the message key path).
     *
     * @param messageKeyPath the failure message key to use
     * @return the property key
     */
    public static String propertyKey(String messageKeyPath)
    {
        /** require [has_property_key] hasPropertyKey(messageKeyPath); **/
        return findPropertyKey(messageKeyPath);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Convenience to create EOFValidation.InvalidValue exceptions in validate&lt;property&gt; methods.
     *
     * @param eo the EO object with an invalid value
     * @param propertyName name of the attribute or relationship that has an invalid value
     * @param aFailedValue the invalid value
     * @return EOFValidationException for EOFValidation.InvalidValue
     */
    public static EOFValidationException invalidValue(EOEnterpriseObject eo, String propertyName, Object aFailedValue)
    {
        /** require
         [valid_eo_param] eo != null;
         [valid_propertyName_param] propertyName != null;    **/
        return new EOFValidationException(eo, propertyName, EOFValidation.InvalidValue, aFailedValue);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Convenience to create EOFValidation.NullNotAllowed exceptions in validate&lt;property&gt; methods.
     *
     * @param eo the EO object with a null value
     * @param propertyName name of the attribute or relationship that has a null value
     * @return EOFValidationException for EOFValidation.NullNotAllowed
     */
    public static EOFValidationException nullNotAllowed(EOEnterpriseObject eo, String propertyName)
    {
        /** require
         [valid_eo_param] eo != null;
         [valid_propertyName_param] propertyName != null;    **/
        return new EOFValidationException(eo, propertyName, EOFValidation.NullNotAllowed, null);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This is the constructor to use to general property validation failures.  It constructs the message
     * key path using the eo's Entity's name, the propertyName, and the failureKey.
     *
     * @param eo EOEnterpriseObject that validation failed on, passed to NSValidation.ValidationException constructor as object
     * @param propertyName optional name of the attribute or relationship that failed to validate
     * @param failureKey the key (usually an EOFValidation constant) that indicates the type of validation failure
     * @param aFailedValue the value that failed validation
     */
    public EOFValidationException(EOEnterpriseObject eo, String propertyName, String failureKey, Object aFailedValue)
    {
        this(eo, eo.entityName(), propertyName, failureKey, aFailedValue);
        /** require
        [valid_eo_param] eo != null;
        [valid_failureKey_param] failureKey != null; **/

        failedValue = aFailedValue;
    }



    /**
     * This is the constructor to use for validations that affect more than one property, for example in validateForSave().
     * <code>partialPath</code> must be a valid failure message key path in the form of &lt;property key&gt;.&lt;failure key&gt;
     * or just &lt;failure key&gt;.  The entity name is extracted from the eo and this partial path is appended to it to
     * form a full messageKeyPath.
     *
     * @param eo the EO object that failed to validate, passed to NSValidation.ValidationException constructor as object
     * @param partialPath a partial failure key path
     */
    public EOFValidationException(EOEnterpriseObject eo, String partialPath)
    {
        this(eo, eo.entityName(), null, partialPath, null);
        /** require
        [valid_eo_param] eo != null;
        [valid_partialPath_param] partialPath != null;
        [valid_message_keypath] isValidMessageKeyPath(net.global_village.eofextensions.EOObject.entityForSelf(eo).name() + "." + partialPath); **/
    }



    /**
     * Designated constructor.  This is intended for internal use, but may be used for special purposes.
     *
     * <p>The message keypath will be formed as &lt;className&gt;.&lt;propertyName&gt;.&lt;failureKey&gt; or &lt;className&gt;.&lt;failureKey&gt;
     * if propertyName is null.
     *
     * @param object optional Object that validation failed on, passed to NSValidation.ValidationException constructor as object
     * @param className the "name" of object class that validation failed on, usually the EOF entity name but
     *          must match the name of a .strings file in the Resources.
     * @param propertyName optional name of the attribute or relationship that failed to validate,
     *          passed to NSValidation.ValidationException constructor as key
     * @param failureKey the key (usually an EOFValidation constant) that indicates the type of validation failure
     * @param aFailedValue the value that failed validation
     */
    public EOFValidationException(Object object, String className, String propertyName, String failureKey, Object aFailedValue)
    {
        super(className + "." + (propertyName != null ? propertyName + "." : "") + failureKey, object, propertyName);
        /** require
        [valid_className_param] className != null;
        [valid_failureKey_param] failureKey != null; **/

        failedValue = aFailedValue;
    }



    /**
     * This constructor is intended primarily:
     * <ul>
     * <li>to support the other public constructors</li>
     * <li>for those rare cases when you want to pass in a custom messageKeyPath</li>
     * </ul>
     *
     * <p>Constructs an exception with the same params as the superclass.
     * <code>messageKeyPath</code> must be a valid failure message key path.</p>
     *
     * @param messageKeyPath the failure key path in the form of &lt;entity name&gt;.&lt;property key&gt;.&lt;failure key&gt;
     *
     * @deprecated If you have a need for this constructor that is not better met by one of the other constructors,
     * then remove this deprecation and document it's usage in the class JavaDocs.  If this method is removed, it
     * is worth looking at replacing it with the (String, EOEnterpriseObject) constructor logic and deprecating that
     * constuctor.  -CH
     */
    public EOFValidationException(String messageKeyPath)
    {
        super(messageKeyPath, null, null);
        /** require
        [valid_messageKeyPath_param] messageKeyPath != null;
        [valid_message_keypath] isValidMessageKeyPath(messageKeyPath); **/
    }



    /**
     * This constructor is intended primarily:
     * <ul>
     * <li>to support the other public constructors</li>
     * <li>for those rare cases when you want to pass in a custom messageKeyPath</li>
     * <li>for when the object being validated is not an EOEnterprise object</li>
     * </ul>
     *
     * <p>Constructs an exception with the same params as the superclass.
     * <code>messageKeyPath</code> must be a valid failure message key path.</p>
     *
     * @param messageKeyPath the failure key path in the form of &lt;entity name&gt;.&lt;property key&gt;.&lt;failure key&gt; or &lt;entity name&gt;.&lt;failure key&gt;
     * @param object the object that failed to validate, passed to NSValidation.ValidationException constructor as the object param
     *
     * @deprecated If you have a need for this constructor that is not better met by one of the other constructors,
     * then remove this deprecation and document it's usage in the class JavaDocs.  If this method is removed, it
     * is worth looking at replacing it with the (String, EOEnterpriseObject) constructor logic and deprecating that
     * constuctor.  -CH
     */
    public EOFValidationException(String messageKeyPath, Object object)
    {
        super(messageKeyPath,
              object,
              hasPropertyKey(messageKeyPath) ? propertyKey(messageKeyPath) : null);
        /** require
        [valid_messageKeyPath_param] messageKeyPath != null;
        [valid_message_keypath] isValidMessageKeyPath(messageKeyPath); **/
    }



    /**
     * Constructs an exception with the same params as the superclass. <code>messageKeyPath</code> must be a valid failure message key path.
     * The propertyKey is derived from the message key path.  This is a shortcut for the constructors that take an attribute or relationship.
     *
     * @param messageKeyPath the failure key path in the form of &lt;entity name&gt;.&lt;property key&gt;.&lt;failure key&gt; or &lt;entity name&gt;.&lt;failure key&gt;
     * @param object the EO object that failed to validate, passed to NSValidation.ValidationException constructor as object
     * @param aFailedValue the value that failed validation
     *
     * @deprecated If you have a need for this constructor that is not better met by one of the other constructors,
     * then remove this deprecation and document it's usage in the class JavaDocs.  If this method is removed, it
     * is worth looking at replacing it with the (String, EOEnterpriseObject) constructor logic and deprecating that
     * constuctor.  -CH
     */
    public EOFValidationException(String messageKeyPath, EOEnterpriseObject object, Object aFailedValue)
    {
        this(messageKeyPath, object);
        /** require
        [valid_messageKeyPath_param] messageKeyPath != null;
        [valid_message_keypath] isValidMessageKeyPath(messageKeyPath); **/

        failedValue = aFailedValue;
    }



    /**
     * This method is for internal use by EOAttributeValidator.
     * Constructs the message key path using the EOAttribute and a failureKey.
     *
     * @param attribute the attribute that failed to validate
     * @param failureKey the key from EOFValidation that indicates the type of validation failure
     * @param aFailedValue the value that failed validation
     *
     * @deprecated If you have a need for this constructor that is not better met by one of the other constructors,
     * then remove this deprecation and document it's usage in the class JavaDocs.  If this method is removed, it
     * is worth looking at replacing it with the (String, EOEnterpriseObject) constructor logic and deprecating that
     * constuctor.  -CH
     */
    public EOFValidationException(EOAttribute attribute, String failureKey, Object aFailedValue)
    {
        this(attribute.entity().name() + "." + attribute.name() + "." + failureKey);
        /** require
        [valid_attribute_param] attribute != null;
        [valid_failureKey_param] failureKey != null; **/

        failedValue = aFailedValue;
    }



    /**
     * This method is for internal use by EOAttributeValidator.
     * Constructs the message key path using the EORelationship and a failureKey.
     *
     * @param relationship the relationship that failed to validate
     * @param failureKey the key from EOFValidation that indicates the type of validation failure
     * @param newFailedValue the value that failed to validate
     *
     * @deprecated If you have a need for this constructor that is not better met by one of the other constructors,
     * then remove this deprecation and document it's usage in the class JavaDocs.  If this method is removed, it
     * is worth looking at replacing it with the (String, EOEnterpriseObject) constructor logic and deprecating that
     * constuctor.  -CH
     */
    public EOFValidationException(EORelationship relationship, String failureKey, Object newFailedValue)
    {
        super(relationship.entity().name() + "." + relationship.name() + "." + failureKey);
        /** require
        [valid_relationship_param] relationship != null;
        [valid_failureKey_param] failureKey != null; **/

        failedValue = newFailedValue;
    }



    /**
     * Returns the entity name (the first part of the message key path).
     *
     * @return the entity name (the first part of the message key path)
     */
    public String entityName()
    {
        String firstPart = StringAdditions.objectKeyPathFromKeyPath(super.getMessage());
        if (StringAdditions.isValidPropertyKeyPath(firstPart))
        {
            firstPart = StringAdditions.objectKeyPathFromKeyPath(firstPart);
        }
        return firstPart;

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the entity (from the first part of the message key path).
     *
     * @return the entity
     */
    public EOEntity entity()
    {
        return EOModelGroup.defaultGroup().entityNamed(entityName());
    }


    /**
     * Returns <code>true</code> if the message key path has a property component.
     *
     * @return <code>true</code> if the message key path has a property component
     */
    public boolean hasPropertyKey()
    {
        return findPropertyKey(super.getMessage()) != null;
    }


    /**
     * Returns the property key (the middle part of the message key path).
     *
     * @return the property key
     */
    public String propertyKey()
    {
        /** require [has_property_key] hasPropertyKey(super.getMessage()); **/
        return findPropertyKey(super.getMessage());
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns <code>true</code> if the property key refers to an attribute.
     *
     * @return <code>true</code> if the property key refers to an attribute
     */
    public boolean propertyKeyRefersToAttribute()
    {
        /** require [has_property_key] hasPropertyKey(); **/

        return entity().attributeNamed(propertyKey()) != null;
    }


    /**
     * Returns the property key as an attribute.
     *
     * @return the property key as an attribute
     */
    public EOAttribute propertyKeyAsAttribute()
    {
        /** require
        [has_property_key] hasPropertyKey();
        [property_key_refers_to_attribute] propertyKeyRefersToAttribute(); **/

        return entity().attributeNamed(propertyKey());
    }


    /**
     * Returns the failure key (the last part of the message key path).
     *
     * @return the failure key (the last part of the message key path)
     */
    public String failureKey()
    {
        return StringAdditions.propertyNameFromKeyPath(super.getMessage());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Sets the value that failed to validate.
     *
     * @param newFailedValue the value that failed to validate
     */
    public void setFailedValue(Object newFailedValue)
    {
        failedValue = newFailedValue;
    }


    /**
     * Returns the value that failed to validate.
     *
     * @return the value that failed to validate
     */
    public Object failedValue()
    {
        return failedValue;
    }



    /**
     * Returns a dictionary with keys used to substitute values in the localized error message:
     * <ul>
     * <li>EOFValidation.EntityFailedOn</li>
     * <li>EOFValidation.EntityName</li>
     * <li>EOFValidation.EntityName</li>
     * <li>EOFValidation.FailedValue</li>
     * <li>EOFValidation.ObjectFailedOn</li>
     * <li>EOFValidation.PropertyName</li>
     * <li>EOFValidation.AttributeFailedOn</li>
     * </ul>
     *
     * @return a dictionary with keys used to substitute values in the localized error message
     */
    protected NSDictionary substitutionDictionary()
    {
        NSMutableDictionary dictionary = new NSMutableDictionary();

        if (entity() != null)
        {
            dictionary.setObjectForKey(entity(), EOFValidation.EntityFailedOn);
            dictionary.setObjectForKey(EOEntityAdditions.displayNameForEntity(entity()), EOFValidation.EntityName);
        }
        else
        {
            dictionary.setObjectForKey(entityName(), EOFValidation.EntityName);
        }

        if (failedValue() != null)
        {
            dictionary.setObjectForKey(failedValue(), EOFValidation.FailedValue);
        }

        if (object() != null)
        {
            dictionary.setObjectForKey(object(), EOFValidation.ObjectFailedOn);
        }

        if (entity() != null && hasPropertyKey())
        {
            dictionary.setObjectForKey(EOEntityAdditions.displayNameForPropertyNamed(propertyKey(), entity()), EOFValidation.PropertyName);

            if (propertyKeyRefersToAttribute())
            {
                dictionary.setObjectForKey(propertyKeyAsAttribute(), EOFValidation.AttributeFailedOn);
            }
            // CHECKME not sure if we need the same for relationships...
        }

        return dictionary;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return getLocalizedMessage()
     * @see #getLocalizedMessage(NSArray)
     */
    public String getMessage()
    {
        return getLocalizedMessage();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a localized message based on the message key path returned by super.getMessage().  Note that the localization is based on the <em>server's</em> locale, so this method is not appropriate for web apps.
     *
     * @return the localized message
     * @see #getLocalizedMessage(NSArray)
     */
    public String getLocalizedMessage()
    {
        // If we can't localize the string, better return some information than a pre-condition exception with no context
        if ( ! LocalizationEngine.localizedStringExists(entityName(), hasPropertyKey() ? propertyKey() : null, failureKey()))
        {
            return "Can't localize message: " + super.getLocalizedMessage();
        }

        String propertyKey = hasPropertyKey() ? propertyKey() : null;
        String localizedTemplate = LocalizationEngine.localizedString(entityName(), propertyKey, failureKey());

        /*
         * This was added to accommodate TestNG which produces a post test report by calling toString
         * on exceptions raised during test failures.  This causes an exception as any EO in this exception
         * was in an EC long since disposed.
         */
        if (object() != null && object() instanceof EOEnterpriseObject)
        {
            try
            {
                ((EOEnterpriseObject)object()).willRead();
            }
            catch (IllegalStateException e)
            {
                return localizedTemplate + " (ec disposed, substitution not possible, entity " + entityName() + ", property " + propertyKey + ")";
            }

        }

        return TemplateSubstitution.substituteValuesInStringFromDictionary(localizedTemplate, substitutionDictionary());

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns a localized message based on the message key path returned by super.getMessage().  Note that the localization is based on the <em>server's</em> locale, so this method is not appropriate for web apps.
     *
     * @param languages the array of languages to localize for
     * @return the localized message
     * @see #getLocalizedMessage(NSArray)
     */
    public String getLocalizedMessage(NSArray languages)
    {
        /** require
        [valid_param] languages != null;
        [localized_message_exists] LocalizationEngine.localizedStringExists(entityName(), hasPropertyKey() ? propertyKey() : null, failureKey(), languages); **/

        String propertyKey = hasPropertyKey() ? propertyKey() : null;
        String localizedTemplate = LocalizationEngine.localizedString(entityName(), propertyKey, failureKey(), languages);
        return TemplateSubstitution.substituteValuesInStringFromDictionary(localizedTemplate, substitutionDictionary());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an array of localized messages based on the message key path returned by super.getMessage() of this exception and any exceptions in this' <code>additionalExceptions()</code>.  Note that the localization is based on the <em>server's</em> locale, so this method is not appropriate for web apps.
     *
     * @see #getLocalizedMessage()
     *
     * @return an array of localized messages, one message per exception
     */
    public NSArray getLocalizedMessageForUnaggregatedExceptions()
    {
        return ValidationExceptionAdditions.localizedMessagesForUnaggregatedExceptions(this);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this exception is for the indicated type of validation failure on the named property.  This can be useful when implementing specific validation exception handling.
     *
     * @param propertyKey the name of the property to match for this failure. <code>null</code> matches when <code>propertyKey()</code> is <code>null</code>
     * @param failureKey the type of validation failure.  See EOFValidation
     * @return <code>true</code> if this exception equals the indicated type of validation failure on the named property
     */
    public boolean isExceptionForFailure(String propertyKey, String failureKey)
    {
        /** require
        [valid_failureKey_param] failureKey != null;
        [failureKey_is_a_valid_key] EOFValidation.setOfAllFailureKeys.containsObject(failureKey); **/

        boolean failureKeyMatches = failureKey().equals(failureKey);
        boolean propertyKeyMatches;

        if (hasPropertyKey())
        {
            propertyKeyMatches = propertyKey.equals(propertyKey());
        }
        else
        {
            propertyKeyMatches = propertyKey == null;
        }

        return failureKeyMatches && propertyKeyMatches;
    }



    /**
     * Helper method for <code>doesFailureExist</code> and <code>exceptionForFailure</code>.
     *
     * @param propertyKey the name of the property to match for this failure.
     * @param failureKey the type of validation failure.  See EOFValidation
     * @return <code>true</code> if this exception or any of it's additional exceptions equals the indicated type of validation failure on the named property
     */
    protected EOFValidationException findExceptionForFailure(String propertyKey, String failureKey)
    {
        /** require
        [valid_propertyKey_param] propertyKey != null;
        [valid_failureKey_param] failureKey != null;
        [failureKey_is_a_valid_key] EOFValidation.setOfAllFailureKeys.containsObject(failureKey); **/

        EOFValidationException exceptionForFailure = null;
        Enumeration exceptions = ValidationExceptionAdditions.unaggregateExceptions(this).objectEnumerator();
        while (exceptions.hasMoreElements())
        {
            EOFValidationException subException = (EOFValidationException)exceptions.nextElement();
            if ((subException != null) && subException.isExceptionForFailure(propertyKey, failureKey))
            {
                exceptionForFailure = subException;
                break;
            }
        }

        return exceptionForFailure;
    }



    /**
     * Returns <code>true</code> if this exception, or any of the exceptions it contains equals the indicated type of validation failure on the named property.  This can be useful when implementing specific validation exception handling.  It determines this by checking to see if the parent exception or any of the additional exceptions return <code>true</code> for <code>isExceptionForFailure()</code>.
     *
     * @param propertyKey the name of the property to match for this failure.
     * @param failureKey the type of validation failure.  See EOFValidation
     * @return <code>true</code> if this exception or any of it's additional exceptions equals the indicated type of validation failure on the named property
     */
    public boolean doesFailureExist(String propertyKey, String failureKey)
    {
        /** require
        [valid_propertyKey_param] propertyKey != null;
        [valid_failureKey_param] failureKey != null;
        [failureKey_is_a_valid_key] EOFValidation.setOfAllFailureKeys.containsObject(failureKey); **/

        return findExceptionForFailure(propertyKey, failureKey) != null;
    }



    /**
     * Returns the exception for the indicated type of validation failure on the named property from either this exception, or any of its additional exceptions.  This can be useful when implementing specific validation exception handling.
     *
     * @param propertyKey the name of the property to match for this failure.
     * @param failureKey the type of validation failure.  See EOFValidation and EOFValidationException.
     * @return the exception for the indicated type of validation failure on the named property from either this exception, or any of the exceptions under it's AdditionalExceptionsKey.
     */
    public EOFValidationException exceptionForFailure(String propertyKey, String failureKey)
    {
        /** require
        [valid_propertyKey_param] propertyKey != null;
        [valid_failureKey_param] failureKey != null;
        [failureKey_is_a_valid_key] EOFValidation.setOfAllFailureKeys.containsObject(failureKey);
        [failure_exists] doesFailureExist(propertyKey, failureKey); **/

        return findExceptionForFailure(propertyKey, failureKey);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the optional exception that was translated into this EOFValidation exception.
     *
     * @return the optional exception that was translated into this EOFValidation exception
     */
    public Exception originalException()
    {
        return originalException;
    }



    /**
     * Sets the optional exception that was translated into this EOFValidation exception.
     *
     * @param anException the optional exception that was translated into this EOFValidation exception
     */
    public void setOriginalException(Exception anException)
    {
        originalException = anException;
    }



}
