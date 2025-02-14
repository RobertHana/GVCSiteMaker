package net.global_village.genericobjects;


import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;

import net.global_village.foundation.JassAdditions;


/**
 * KeyedLookup differs from Lookup in that a known key is associated with each object.  This key, which must be unique, is used by code to retrieve a specific instance of this class while maintaining a name that has meaning to the user.  These keys must be pre-determined and defined in the program code.  For example: <p>
 * <pre> 
 * public static final String cancelledStatusKey = @"cancelled";
 * &nbsp;
 * public static MyKeyedLookup cancelledStatusWithEditingContext( EOEditingContext editingContext )
 * {
 *     return KeyedLookup.objectForKeyWithEditingContextAndEntityName( cancelledStatusKey, editingContext, "MyKeyedLookup" );
 * }
 * </pre>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */  
public class KeyedLookup extends _KeyedLookup
{


    /**
     * Returns <code>true</code> if the lookup subentity exists for the given key, <code>false</code> otherwise.
     *
     * @param aKey key of specific object to look for.
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @return <code>true</code> if the lookup subentity exists, <code>false</code> otherwise
     */
    static public boolean objectExistsForKey(EOEditingContext editingContext, String entityName, String aKey)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null;
        [valid_aKey_param] aKey != null; **/

        return objectExistsForKeyAndValue(editingContext, entityName, "key", aKey);
    }



    /**
     * Returns the object of the entity specified by <code>entityName</code> where <code>key == aKey</code>.  Throws an exception if there are no matching objects, or more than one matching object.  Subclasses may want to wrap this method to pass in their own entity name.<br>
     *
     * @param aKey key of specific object to look for.
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @return object associated with <code>aKey</code>
     */
    static public EOEnterpriseObject objectForKey(EOEditingContext editingContext, String entityName, String aKey)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null;
        [valid_aKey_param] aKey != null;
        [lookup_object_exists] objectExistsForKey(editingContext, entityName, aKey); **/

        EOEnterpriseObject lookupObject = objectForKeyAndValue(editingContext, entityName, "key", aKey);

        JassAdditions.post("KeyedLookup", "objectForKey", lookupObject.entityName().equals(entityName));
        return lookupObject;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the object of the entity specified by <code>entityName</code> where <code>key == aKey</code>.  Throws an exception if there are no matching objects, or more than one matching object.  Subclasses may want to wrap this method to pass in their own entity name.<br>
     *
     * @param aKey key of specific object to look for.
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @return object associated with <code>aKey</code>
     */
    static public String valueForKey(EOEditingContext editingContext, String entityName, String aKey)
    {
        /** require
        [valid_editingContext_param] editingContext != null;
        [valid_entityName_param] entityName != null;
        [valid_aKey_param] aKey != null;
        [lookup_object_exists] objectExistsForKey(editingContext, entityName, aKey); **/

        EOEnterpriseObject lookupObject = objectForKey(editingContext, entityName, aKey);
        String value = (String)lookupObject.valueForKey("name");

        JassAdditions.post("KeyedLookup", "valueForKey", lookupObject.entityName().equals(entityName));
        return value;

        /** ensure [valid_result] Result != null; **/
    }



}
