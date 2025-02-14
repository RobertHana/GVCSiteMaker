package net.global_village.eofvalidation;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * A collection of static methods which validate a value for a relationship.  The only method you should ever need to call directory is <a href="#displayNameForRelationship">displayNameForRelationship</a>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class EORelationshipValidator extends Object
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EORelationshipValidator()
    {
        super();
    }



    /**
    * Returns <code>true</code> if EOAttribute's implementation would have raised an exception for this value for this attribute.  This is used only for the post condition of validateValueForAttribute(Object, EOAttribute)
     *
     * @param value value to validate for this attribute
     * @param relationship EOAttribute to validate this value for
     * @return <code>true</code> if EOAttribute would have raised an exception for this value for this attribute.
     */
    static protected boolean doesSuperRaise(Object value, EORelationship relationship)
    {
        /** require [valid_param] relationship != null; **/

        boolean doesRaise = true;
        try
        {
            relationship.validateValue(value);
            doesRaise = false;
        }
        catch (Exception e) { }

        return doesRaise;
    }



    /**
     * This method oversees validating this value for this relationship.  If it is not valid, the exception returned contains a customized, localized validation failure message.
     *
     * @param value value to validate for this relationship
     * @param relationship EORelationship to validate this value for
     * @return null if the value is acceptable
     * @exception EOFValidationException if the value is not acceptable for this relationship
     */
    static public Object validateValue(Object value,
                                       EORelationship relationship) throws EOFValidationException
    {
        /** require [valid_param] relationship != null; **/

        checkTypeCompatibility(value, relationship);
        checkNullity(value, relationship);

        return value;

        /** ensure [we_must_raise_if_super_raises] ! doesSuperRaise(value, relationship); **/
    }



    /**
     * Returns <code>true</code> if EORelationship's implementation would have raised an exception for this value for this relationship.  This is used only for the post condition of validateValueForRelationship(Object, EORelationship)
     *
     * @param value value to validate for this relationship
     * @param relationship EORelationship to validate this value for
     * @return <code>true</code> if EORelationship would have raised an exception for this value for this relationship.
     */
    static protected boolean doesSuperRaise(EOEnterpriseObject value, EORelationship relationship)
    {
        /** require [valid_param] relationship != null; **/

        boolean doesRaise = true;
        try
        {
            relationship.validateValue(value);
            doesRaise = false;
        }
        catch (Exception e) { }

        return doesRaise;
    }



    /**
     * Raises a validation exception if the value pointed at is not compatible with the internal type (class) that this relationship is mapped to.  The value to is compatible in three cases:
     * <ul>
     * <li> it is of the same class as the destination class for this relationship
     * <li> it is of a sub-class of the destination class for this relationship
     * <li> it is null or EONullValue
     * </ul>
     *
     * @param value value to validate for this relationship
     * @param relationship EORelationship to validate this value for
     * @exception EOFValidationException if the value is not type compatible with this relationship
     */
    static public void checkTypeCompatibility(Object value, EORelationship relationship) throws EOFValidationException
    {
        /** require [valid_param] relationship != null; **/

        // Don't validate faults as this just causes extra fetching
        if (value == null || (value instanceof EOFaulting && ((EOFaulting)value).isFault()))
        {
            return;
        }

        Class destinationClass = EOEntityAdditions.safeClassForName(relationship.entity(), relationship.destinationEntity().className());

        if (relationship.isToMany())
        {
            // This would indicate that someone has made a rather serious error in their code.
            if (EOEntityAdditions.isNull(value) || ( ! NSArray.class.isInstance(value)))
            {
                throw new EOFValidationException(null, relationship.entity().name(), relationship.name(), EOFValidation.InvalidValue, value);
            }

            // The type of each entity in a to many relationship must be checked.
            for (Enumeration e = ((NSArray)value).objectEnumerator(); e.hasMoreElements();)
            {
                Object destinationObject = e.nextElement();
                // Don't validate faults as this just causes extra fetching
                if (((EOFaulting)destinationObject).isFault())
                {
                    return;
                }
                if (! destinationClass.isInstance(destinationObject))
                {
                    throw new EOFValidationException(null, relationship.entity().name(), relationship.name(), EOFValidation.InvalidValue, destinationObject);
                }
            }
        }
        else
        {
            if ( ! (EOEntityAdditions.isNull(value) || destinationClass.isInstance(value)) )
            {
                throw new EOFValidationException(null, relationship.entity().name(), relationship.name(), EOFValidation.InvalidValue, value);
            }
        }
    }



    /**
     * Raises a validation exception if this relationship is null (an an empty array for a to many relationship) and is not allowed to be..
     *
     * @param value value to validate for this relationship
     * @param relationship EORelationship to validate this value for
     * @exception EOFValidationException if the value is not type compatible with this attribute
     */
    static public void checkNullity(Object value,
                                    EORelationship relationship) throws EOFValidationException
    {
        /** require [valid_param] relationship != null; **/
        JassAdditions.pre("EORelationshipValidator", "checkNullity", ( ! relationship.isToMany()) || NSArray.class.isInstance(value));

        if (relationship.isMandatory())
        {
            boolean isMissingToOne = ( ! relationship.isToMany()) && (EOEntityAdditions.isNull(value));

            boolean isMissingToMany = relationship.isToMany() && (((NSArray)value).count() == 0);

            if (isMissingToOne || isMissingToMany)
            {
                throw new EOFValidationException(null, relationship.entity().name(), relationship.name(), EOFValidation.NullNotAllowed, value);
            }
        }
    }



    /**
     * Convenience method returning a localized display name for this relationship, or a prettified relationship name if none is available.
     *
     * @param relationship relationship to lookup display name for.
     * @return  a localized display name for this relationship, or a prettified version of the relationship name if none is available.
     */
    public static String displayNameForRelationship(EORelationship relationship)
    {
        /** require [valid_param] relationship != null; **/

        return EOEntityAdditions.displayNameForPropertyNamed(relationship.name(), relationship.entity());

        /** ensure [valid_result] Result != null; **/
    }



}
