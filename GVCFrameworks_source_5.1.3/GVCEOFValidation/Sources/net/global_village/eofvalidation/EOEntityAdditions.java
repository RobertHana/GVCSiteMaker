package net.global_village.eofvalidation;


import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;


/**
 * EOEntity extensions for localization and validation.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class EOEntityAdditions extends Object
{


    /**
     * Convenience methods returning a localized display name for this entity, or the entity name if none is available.
     *
     * @param entity entity to lookup display name for
     * @return String a localized display name for this entity, or the entity name if none is available
     */
    public static String displayNameForEntity(EOEntity entity)
    {
        /** require [valid_param] entity != null; **/

        String displayName = entity.name();

        if (LocalizationEngine.localizedStringExists(entity, null, EOFValidation.DisplayName))
        {
            displayName = LocalizationEngine.localizedString(entity, null, EOFValidation.DisplayName);
        }
        // Join tables don't have a class description, all we have available is the entity name
        else if (! entity.className().equals("EOGenericRecord"))
        {
            EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(entity.name());
            displayName = classDescription.displayNameForKey(entity.name());
        }

        return displayName;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Convenience methods returning a localized display name for this property (attribute or relationship), or a prettified name from the model if no localized one is available.
     *
     * @param propertyName name of property to lookup display name for
     * @param entity entity on which to look up this property name
     * @return a localized display name for this property, or a prettified name from the model if no localized one is available
     */
    public static String displayNameForPropertyNamed(String propertyName, EOEntity entity)
    {
        /** require
        [valid_propertyName_param] propertyName != null;
        [valid_entity_param] entity != null; **/

        String displayName = null;
        String entityDisplayName = null;

        if (LocalizationEngine.localizedStringExists(entity, propertyName, EOFValidation.DisplayName))
        {
            displayName = LocalizationEngine.localizedString(entity, propertyName, EOFValidation.DisplayName);
        }
        if (LocalizationEngine.localizedStringExists(entity, null, EOFValidation.DisplayName))
        {
            entityDisplayName = LocalizationEngine.localizedString(entity, null, EOFValidation.DisplayName);
        }

        // There is an overlap between the partially qualified key used for attribute and entity display names.  This results in the entity's display name being returned if one is not defined for the attribute.  This is checked for and handled here.
        if ((! entity.className().equals("EOGenericRecord")) &&
            ((displayName == null) ||
             (entityDisplayName != null && displayName.equals(entityDisplayName))))
        {
            EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(entity.name());
            displayName = classDescription.displayNameForKey(propertyName).toLowerCase();
        }

        return displayName != null ? displayName : propertyName;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     *  Returns <code>true</code> if value is null or an instance of NSKeyValueCoding.Null.  This is useful when validation properties as in some situations, value will be an instance of NSKeyValueCoding.Null rather than null.  For example, the FrontBase adaptor calls validateValue:forKey: as part of its fetch processing during which time null values are represented as EONullValue
     *
     * @param aValue value to check for null
     * @return <code>true</code> if value is null or an instance of NSKeyValueCoding.Null
     */
    static public boolean isNull(Object aValue)
    {
        return (aValue == null) || (aValue instanceof com.webobjects.foundation.NSKeyValueCoding.Null);
    }



    /**
     * A "safe" version of Class.forName(String) which converts the ClassNotFoundException to a RuntimeException so that methods using this do not have to declare ClassNotFoundException and so can match the definition of framework methods.  Returns the class matching className or raises a RuntimeException if the class can not be found.
     *
     * @param entity the name of this entity is included in the exception if the class can not be found
     * @param className name of the class to lookup
     * @return the class matching this className
     * @exception RuntimeException if the class can not be found
     */
    static public Class safeClassForName(EOEntity entity, String className)
    {
        Class theClass = null;

        try
        {
            theClass = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            // Convert to RuntimeException so we do not have to declare exception so we can match definition of framework methods.
            throw new RuntimeException("Entity " + entity.name() + " has a reference to class " + className + " which can not be loaded.");
        }

        return theClass;
    }



    /**
     * Returns the <code>EOAttribute</code> or <code>EORelationship</code> which corresponds to the passed name or null if there is no such property.
     *
     * @param entity entity on which to look up this property name
     * @param propertyName the property name of the EOAttribute or EORelationship
     * @return the <code>EOproperty</code> which corresponds to the passed name or null if there is no such property
     */
    public static EOProperty propertyWithName(EOEntity entity, String propertyName)
    {
        /** require
        [valid_entity] entity != null;
        [valid_propertyName_param] propertyName != null;  **/

        EOProperty theProperty = entity.attributeNamed(propertyName);

        if (theProperty == null)
        {
            theProperty = entity.relationshipNamed(propertyName);
        }

        return theProperty;
    }


}
