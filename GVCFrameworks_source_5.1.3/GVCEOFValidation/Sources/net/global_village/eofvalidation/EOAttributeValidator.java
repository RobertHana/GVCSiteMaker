package net.global_village.eofvalidation;

import java.math.*;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


/**
 * A collection of static methods which validate a value for an attribute.  The only method you should ever need to call directory is <a href="#displayNameForAttribute">displayNameForAttribute</a>.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class EOAttributeValidator extends Object
{
    protected final static int generatedPrimaryKeySize = 24;


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EOAttributeValidator()
    {
        super();
    }



    /**
     * Returns <code>true</code> if EOAttribute's implementation would have raised an exception for this value for this attribute.  This is used only for the post condition of validateValueForAttribute(Object, EOAttribute)
     *
     * @param value value to validate for this attribute
     * @param attribute EOAttribute to validate this value for
     * @return <code>true</code> if EOAttribute would have raised an exception for this value for this attribute.
     */
    static protected boolean doesSuperRaise(Object value, EOAttribute attribute)
    {
        /** require [valid_param] attribute != null; **/

        boolean doesRaise = true;
        try
        {
            attribute.validateValue(value);
            doesRaise = false;
        }
        catch (Exception e) { }

        return doesRaise;
    }



    /**
     * This method oversees validating this value for this attribute.  If it is not valid the exception returned contains a customized, localized validation failure message.
     *
     * @param value value to validate for this attribute
     * @param attribute EOAttribute to validate this value for
     * @return null if the value is acceptable, or returns a new value if needed (e.g. coercion).
     * @exception EOFValidationException if the value is not acceptable for this attribute
     */
    static public Object validateValue(Object value, EOAttribute attribute) throws EOFValidationException
    {
        /** require [valid_param] attribute != null; **/

        checkTypeCompatibility(value, attribute);
        checkNullity(value, attribute);

        if (! EOEntityAdditions.isNull(value))
        {
            checkWidth(value, attribute);
        }

        // We could also check the scale / precision of an NSNumber.  EOF does not appear to do this, and thus relying on database exceptions to indicate this.  FrontBase does not (as of 1.2w) raise an exception and ignores scale and precision.  Oracle rounds up decimals, but raises an exception if the scale is exceeded.

        return value;

        /** ensure [we_must_raise_if_super_raises] ! doesSuperRaise(value, attribute); **/
    }



    /**
     * Raises a validation exception if value is not compatible with the internal type (class) that this attribute is mapped to.  The value to is compatible in three cases:
     * <ul>
     * <li> it is of the same class as the value class for this attribute
     * <li> it is of a sub-class of the value class for this attribute
     * <li> it is null or EONullValue
     * </ul>
     *
     * @param value value to validate for this attribute
     * @param attribute EOAttribute to validate this value for
     * @exception EOFValidationException if the value is not type compatible with this attribute
     */
    static public void checkTypeCompatibility(Object value, EOAttribute attribute) throws EOFValidationException
    {
        /** require [valid_param] attribute != null; **/

        // EOF allows conversion from non-numeric text to NaN as an acceptable value, we do not.  Looks like this is not an issue in Java.  How does Java handle this?
        // boolean isInvalidNumber = (value instanceof BigDecimal) && (value.equals(NaN));

        if ( ! EOEntityAdditions.isNull(value))
        {
            Class desiredClass = classForAttribute(attribute);

            if ( ! desiredClass.isInstance(value))
            {
                throw new EOFValidationException(null, attribute.entity().name(), attribute.name(), EOFValidation.InvalidValue, value);
            }
        }
    }



    /**
     * Raises a validation exception if this attribute is null and is not allowed to be, or if it is an empty / all white space string and empty strings are being considered as null.
     *
     * @param value value to validate for this key
     * @param attribute EOAttribute to validate this value for
     * @exception EOFValidationException if the value is not type compatible with this attribute
     */
    static public void checkNullity(Object value,
                                    EOAttribute attribute) throws EOFValidationException
    {
        /** require [valid_param] attribute != null; **/

        boolean cantBeNull = ! (attribute.allowsNull() || isGeneratedPrimaryKey(attribute));

        boolean isDisallowedEmptyString = EOFValidation.shouldTreatEmptyStringsAsNull() &&
            (value instanceof String) && (((String)value).trim().length() == 0);

        if (cantBeNull && (EOEntityAdditions.isNull(value) || isDisallowedEmptyString))
        {
            throw new EOFValidationException(null, attribute.entity().name(), attribute.name(), EOFValidation.NullNotAllowed, value);
        }
    }



    /**
     * Raises a validation exception if this attribute is wider (longer) than allowed.  This only applied to NSString and NSData types.
     *
     * @param value value to validate for this key
     * @param attribute EOAttribute to validate this value for
     * @exception EOFValidationException	if the value is not type compatible with this attribute
     */
    static public void checkWidth(Object value,
                                  EOAttribute attribute) throws EOFValidationException
    {
        /** require
        [valid_attribute_param] attribute != null;
        [valid_value_param] ! EOEntityAdditions.isNull(value); **/

        // The value passed here has been corerced by the adaptor so it is one of four known types. Two of these types, NSString and NSData, can have width restrictions and also have a length method.  Doesn't check width for attributes that have a width of 0, since those are likely to be LOBs.
        Class classOfAttribute = classForAttribute(attribute);
        if ((attribute.width() > 0) && ((classOfAttribute.equals(NSData.class)) || (classOfAttribute.equals(String.class))))
        {
            int actualWidth = 0;

            if (value instanceof NSData)
            {
                actualWidth = ((NSData)value).length();
            }
            else
            {
                actualWidth = ((String)value).length();
            }

            if (actualWidth > attribute.width())
            {
                throw new EOFValidationException(null, attribute.entity().name(), attribute.name(), EOFValidation.TooLong, value);
            }
        }
    }



    /**
     * Returns <code>true</code> if this attribute represents a primary key which will be generated by EOF. This attribute is considered to represent a primary key which will be generated if:
     * <ul>
     * <li>it is part of the primary key
     * <li>there is only one column in the primary key (EOF does not generate for compound keys)
     * <li>the attribute type is appropriate for generation (NSNumber or 12 byte NSData)
     * </ul>
     *
     * @param attribute EOAttribute to check
     * @return <code>true</code> if the attribute is a generatable primary key
     */
    static public boolean isGeneratedPrimaryKey(EOAttribute attribute)
    {
        /** require [valid_param] attribute != null; **/

        NSArray primaryKeysForEntity = attribute.entity().primaryKeyAttributes();
        int columnsInPrimaryKey = primaryKeysForEntity.count();
        boolean isPartOfPrimaryKey = primaryKeysForEntity.containsObject(attribute);
        boolean isPrimaryKey = (columnsInPrimaryKey == 1) && isPartOfPrimaryKey;

        Class attributeValueClass = classForAttribute(attribute);
        boolean isGeneratableType = (attributeValueClass.equals(Number.class)) || (attributeValueClass.equals(BigDecimal.class)) || (attributeValueClass.equals(NSData.class) && (attribute.width() == generatedPrimaryKeySize));

        return isPrimaryKey && isGeneratableType;
    }



    /**
     * Returns the Java class that is used to implement this attribute.  This is based on the Objective-C name from the EOModel as determined by valueClassName() for this attribute.
     *
     * @param attribute EOAttribute to return class for
     * @return the Java class that is used to implement this attribute
     * @exception RuntimeException if the class for this attribute can not be located
     *
     */
    static public Class classForAttribute(EOAttribute attribute)
    {
        /** require [valid_param] attribute != null; **/

        return EOEntityAdditions.safeClassForName(attribute.entity(), attribute.className());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Convenience methods returning a localized display name for this attribute, or a prettified attribute name if none is available.
     *
     * @param attribute attribute to lookup display name for.
     * @return a localized display name for this attribute, or a prettified version of the attribute name if none is available.
     */
    public static String displayNameForAttribute(EOAttribute attribute)
    {
        /** require [valid_param] attribute != null; **/

        return EOEntityAdditions.displayNameForPropertyNamed(attribute.name(), attribute.entity());

        /** ensure [valid_result] Result != null; **/
    }



}

