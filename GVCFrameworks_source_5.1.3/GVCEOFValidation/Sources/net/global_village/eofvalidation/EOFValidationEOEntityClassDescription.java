package net.global_village.eofvalidation;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Sub-class of EOEntityClassDescription used to hook into EOF's validation mechanism.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class EOFValidationEOEntityClassDescription extends EOEntityClassDescription
{


    /**
     * Designated constuctor. Inherited from EOEntityClassDescription.
     *
     * @param anEntity entity to create class description for
     */
    public EOFValidationEOEntityClassDescription(EOEntity anEntity)
    {
        super(anEntity);
        /** require [valid_param] anEntity != null; **/
    }



    /**
     * Overridden for future use in determining whether the values being saved for object are acceptable.
     *
     * @param object object to validate for save
     * @exception NSValidation.ValidationException if EOF will not allow the object to be saved in its current state
     */
    public void validateObjectForSave(EOEnterpriseObject object) throws NSValidation.ValidationException
    {
        if (NSLog.debugLoggingAllowedForGroups(NSLog.DebugGroupValidation))
            NSLog.debug.appendln("Validating object " + object + " for save.");

        // Wow, this aches.  Since super.validateObjectForSave(object) is ambiguous...
        // (error message is: EOFValidationEOEntityClassDescription.java:42: reference to validateObjectForSave is ambiguous, both method validateObjectForSave(com.webobjects.eocontrol.EOEnterpriseObject) in com.webobjects.eocontrol.EOClassDescription and method validateObjectForSave(java.lang.Object) in com.webobjects.eoaccess.EOEntityClassDescription match super.validateObjectForSave((EOEnterpriseObject)object);)
        // ...we need to use reflection to actually call the method we want.  Note that casting the object param to Object will not be ambiguous, but will call the wrong method.
        // ambiguous call: super.validateObjectForSave(object);
        /*this doesn't actually work... not sure how to get around this...
        try
        {
            Class superClass = Class.forName("com.webobjects.eocontrol.EOClassDescription");
            Method superMethod = superClass.getMethod("validateObjectForSave", new Class[] {com.webobjects.eocontrol.EOEnterpriseObject.class});
            superMethod.invoke(this, new Object[] {object});
        }
        catch (Exception x) { never happens } */

        super.validateObjectForSave((Object)object);
    }



    /**
     * Overridden for future use in determining whether it's permissible to delete object.
     *
     * @param object object to validate for delete
     * @exception NSValidation.ValidationException if EOF will not allow the object to be deleted from the object graph
     */
    public void validateObjectForDelete(EOEnterpriseObject object) throws NSValidation.ValidationException
    {
        if (NSLog.debugLoggingAllowedForGroups(NSLog.DebugGroupValidation))
            NSLog.debug.appendln("Validating object " + object + " for delete.");
        super.validateObjectForDelete(object);
    }



    /**
     * Overridden to provide extended validations and customized, localized validation error messages.
     *
     * @param value value to validate for this key
     * @param key name of property (relationship or attribute) to validate this value for
     * @return returns <code>value</code> if the value is acceptable, or returns a new value if needed (e.g. coercion)
     * @exception NSValidation.ValidationException if the value is not acceptable for this properly
     */
    public Object validateValueForKey(Object value, String key) throws NSValidation.ValidationException
    {
        /** require [valid_key_param] key != null; **/

        if (NSLog.debugLoggingAllowedForGroups(NSLog.DebugGroupValidation))
            NSLog.debug.appendln("In GVC Validating value '" + value + "' for key " + key + " for entity " + entity().name());

        // Get the super class to coerce the value, but ignore any errors that it generates...
        Object coercedValue = value;
        try {
            coercedValue = super.validateValueForKey(value, key);
        } catch (com.webobjects.foundation.NSValidation.ValidationException e) {}

        if (attributeKeys().containsObject(key))
        {
            EOAttribute attribute = entity().attributeNamed(key);
            coercedValue = EOAttributeValidator.validateValue(coercedValue, attribute);
        }
        else if (toOneRelationshipKeys().containsObject(key) || toManyRelationshipKeys().containsObject(key))
        {
            EORelationship relationship = entity().relationshipNamed(key);
            coercedValue = EORelationshipValidator.validateValue(coercedValue, relationship);
        }

        return coercedValue;
    }


}
