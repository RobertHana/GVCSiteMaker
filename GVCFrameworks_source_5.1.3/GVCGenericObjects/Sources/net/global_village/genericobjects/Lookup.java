package net.global_village.genericobjects;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * This is the abstract base class for lookup type objects.  <code>Lookup</code> allows a
 * specific object to be associated with a (freely changeable) name that has meaning to the
 * user.  It is intended to be used as the base for lookup lists and other like object families
 * where a name is needed for UI display. <p>
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 10$
 */
public class Lookup extends _Lookup
{
    private static final long serialVersionUID = -6977182887634937228L;

    /** Instance of NameComparator to be used when sorting objects. */
    public static final NSComparator NameComparator = new NameComparator();

    /** Key for use when looking up the name of the default object in <code>CustomInfo.plist</code>. */
    public static final String defaultNameKey = "defaultName";


    /**
     * A convenience method which returns all objects of the Entity indicated by <code>entityName</code> sorted by <code>comparator</code>.  Subclasses may want to wrap this method to pass in their own entity name or comparator.
     *
     * @param entityName the kind of objects you want to fetch with this method
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param comparator the <code>NSComparator</code> instance to use to sort the fetched objects.
     * @return NSArray of all objects of the entity specified by <code>entityName</code> and sorted by <code>comparator</code>
     */
    public static NSArray orderedObjects(EOEditingContext editingContext, String entityName, NSComparator comparator)
    {
        /** require
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;
        [valid_comparator] comparator != null;             **/
        NSArray orderedList = EOUtilities.objectsForEntityNamed(editingContext, entityName);

        try
        {
            orderedList = orderedList.sortedArrayUsingComparator(comparator);
        }
        catch (com.webobjects.foundation.NSComparator.ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }
        
        return orderedList;
        /** ensure [valid_result] Result != null;  **/
    }

 

    /**
     * A convenience method which returns all objects of the Entity indicated by <code>entityName</code> sorted by name.  Subclasses may want to wrap this method to pass in their own entity name.
     *
     * @param entityName the kind of objects you want to fetch with this method
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of all objects of the entity specified by <code>entityName</code> and sorted by name
     */
    public static NSArray objectsOrderedByName(EOEditingContext editingContext, String entityName)
    {
        /** require
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;           **/
        return orderedObjects(editingContext, entityName, NameComparator);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the instance of Lookup class subclass whose key matches value or null if one doesn't exist.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName entity's name to retrieve from database
     * @param key the entity's attirbute to search on
     * @param value the value of the attribute to find
     * @return object who's key matches value or null if one doesn't exist
     */
    protected static EOEnterpriseObject findObjectForKeyAndValue(EOEditingContext editingContext, String entityName, String key, String value)
    {
        /** require
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;           
        [valid_key_param] key != null;           
        [valid_value_param] value != null; **/

    	EOEnterpriseObject lookupObject;
        try
        {
            lookupObject = (EOEnterpriseObject)EOUtilities.objectMatchingKeyAndValue(editingContext, entityName, key, value);
        }
        catch (EOObjectNotAvailableException e)
        {
            lookupObject = null;
        }
        return lookupObject;
    }



    /**
     * Returns <code>true</code> if the lookup subentity exists for the given key and value, <code>false</code> otherwise.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName entity's name to retrieve from database
     * @param key the entity's attirbute to search on
     * @param value the value of the attribute to find
     * @return <code>true</code> if the lookup subentity exists, <code>false</code> otherwise
     */
    public static boolean objectExistsForKeyAndValue(EOEditingContext editingContext, String entityName, String key, String value)
    {
        /**
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;           
        [valid_key_param] key != null;           
        [valid_value_param] value != null;                     **/
        return findObjectForKeyAndValue(editingContext, entityName, key, value) != null;
    }



    /**
     * Returns the instance of Lookup class subclass whose key matches value.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName entity's name to retrieve from database
     * @param key the entity's attirbute to search on
     * @param value the value of the attribute to find
     * @return object who's key matches value
     */
    public static EOEnterpriseObject objectForKeyAndValue(EOEditingContext editingContext, String entityName, String key, String value)
    {
        /**
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;           
        [valid_key_param] key != null;           
        [valid_value_param] value != null;                     
        [object_exists] objectExistsForKeyAndValue(editingContext, entityName, key, value);  **/
        return findObjectForKeyAndValue(editingContext, entityName, key, value);
        /** ensure [valid_result] Result != null;  
                   [right_entity] Result.entityName().equals(entityName);       **/
    }



    /**
     * Returns <code>true</code> if the lookup subentity exists for the given name, <code>false</code> otherwise.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName subclass entity's name to retrieve from database
     * @param name name to find
     * @return <code>true</code> if the lookup subentity exists, <code>false</code> otherwise
     */
    public static boolean objectExistsForName(EOEditingContext editingContext, String entityName, String name)
    {
        /**
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;           
        [valid_name_param] name != null;           **/
        return objectExistsForKeyAndValue(editingContext, entityName, "name", name);
    }



    /**
     * Returns the instance of Lookup class subclass that matches the name.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName subclass entity's name to retrieve from database
     * @param name name to find
     * @return object who's name matches name
     */
    public static EOEnterpriseObject objectForName(EOEditingContext editingContext, String entityName, String name)
    {
        /**
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;           
        [valid_name_param] name != null;           
        [object_exists] objectExistsForKeyAndValue(editingContext, entityName, key, value);  **/
        return objectForKeyAndValue(editingContext, entityName, "name", name);
        /** ensure [valid_result] Result != null;  
                   [right_entity] Result.entityName().equals(entityName);       **/
    }



    /**
     * Returns the instance of Lookup class subclass that matches the name taken from the entity's <code>userInfo</code> dictionary with key <code>defaultName</code>.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName subclass entity's name to retrieve from database
     * @return Lookup object who's name matches the defaultName
     * </jml>
     */
    public static EOEnterpriseObject defaultObject(EOEditingContext editingContext, String entityName)
    {
        /**
             [valid_editingContext_param] editingContext != null; 
             [valid_entityName_param] entityName != null;           
             [valid_name_param] name != null;           
             [has_defaultNameKey] EOModelGroup.defaultGroup().entityNamed(entityName).userInfo().objectForKey(defaultNameKey) != null;
         **/
        String defaultName = (String)EOModelGroup.defaultGroup().entityNamed(entityName).userInfo().objectForKey(defaultNameKey);
        return objectForName(editingContext, entityName, defaultName);
        /** ensure [valid_result] Result != null;  
                   [correct_entity] Result.entityName().equals(entityName);     **/
    }



    /**
     * Returns the instance of Lookup class subclass that matches the name taken from the CustomInfo.plist in the editingContext passed in.  The CustomInfo.plist must have an entry of the following form "<EntityName>.defaultName" else an exception is raised.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param subclass to retrieve from database
     * @return Lookup object who's name matches the defaultName
     */
    public static EOEnterpriseObject defaultObject(EOEditingContext editingContext, Class subclass)
    {
        /** require
                [valid_editingContext] editingContext != null;
                [valid_subclass] subclass != null;
                [default_value_exists] DefaultValueRetrieval.defaultValueExists(subclass, EOUtilities.entityForClass(editingContext, subclass).name() + "." + defaultNameKey);
                [default_value_is_string] DefaultValueRetrieval.defaultValue(subclass, EOUtilities.entityForClass(editingContext, subclass).name() + "." + defaultNameKey) instanceof String;
         **/
        String name = DefaultValueRetrieval.defaultString(subclass, EOUtilities.entityForClass(editingContext, subclass).name() + "." + defaultNameKey);
        String subclassEntityName = EOUtilities.entityForClass(editingContext, subclass).name();
        return objectForName(editingContext, subclassEntityName, name);
        /** ensure [valid_result] Result != null;      **/
    }



    /**
     * Comparator to sort objects by name, case insensitively.
     */
    static protected class NameComparator extends NSComparator
    {

        /**
         * Compares two Lookup objects, case-insensitivly comparing their names.
         *
         * @param object1 the object to compare
         * @param object2 the object to compare against
         * @return <code>NSComparator.OrderedSame</code> if the values are the same, <code>NSComparator.OrderedDescending</code> if <code>object1</code>'s value is larger, or <code>NSComparator.OrderedAscending</code> if it is smaller
         *
         * <jml>
         * also
         *   requires object1 != null;
         *   requires object2 != null;
         * </jml>
         */
        public int compare(Object object1, Object object2)
        {
            /* require jass doesn't like contracting a nested class...
            object1 != null;
            object2 != null; **/
            String name1 = (String)(((EOGenericRecord)object1)).valueForKey("name");
            String name2 = (String)(((EOGenericRecord)object2)).valueForKey("name");
            return name1.compareToIgnoreCase(name2);
        }

    }



    /**
     * Returns this object's name localized in the given language. Returns <code>null</code> if no language exists for this generic object in the given language.
     *
     * @param language the language that we want this object's name returned in
     * @return this generic object's name localized in the given language. Returns <code>null</code> if no language exists for this generic object in the given language
     */
    protected String findLocalizedName(String language)
    {
        /** require [valid_language] language != null;  **/
        Enumeration localizationEnumerator = localizations().objectEnumerator();
        while (localizationEnumerator.hasMoreElements())
        {
            LookupLocalization lookupLocalization = (LookupLocalization)localizationEnumerator.nextElement();
            if (lookupLocalization.locale().name().equals(language))
            {
                return lookupLocalization.localizedName();
            }
        }
        return null;
    }


    /**
     * Returns <code>true</code> if this object has a name localized in the given language.
     *
     * @param language the language that we want this object's name returned in
     * @return <code>true</code> if this generic object has a name localized in the given language
     */
    public boolean localizedNameExists(String language)
    {
        /** require [valid_language] language != null;  **/
        return findLocalizedName(language) != null;
    }


    /**
     * Returns this object's name localized in the given language.
     *
     * @param language the language that we want this object's name returned in
     * @return this object's localized name for the given language
     */
    public String localizedName(String language)
    {
        /** require [valid_language] language != null;  
                    [localizedNameExists] localizedNameExists(language);     **/
        return findLocalizedName(language);
        /** ensure [valid_result] Result != null;  **/
    }


    /**
     * Returns this object's name localized in the given language, with the non-localized name being returned if a localization cannot be found for the given language.
     *
     * @param language the language that we want this object's name returned in
     * @return if it exists, this object's localized name, otherwise, this object's name
     */
    public String localizedNameWithDefault(String language)
    {
        /** require [valid_language] language != null;  **/
        if (localizedNameExists(language))
        {
            return localizedName(language);
        }
        else
        {
            return name();
        }
    }



    /**
     * Returns a copy of this Lookup object as a reference to this object: lookup values should not be duplicated.
     *
     * @param copiedObjects the copied objects keyed on the EOGlobalID of the object the copy was made from
     * @return a reference to this object
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [valid_copiedObjects] copiedObjects != null;  **/
        return this;
        /** ensure [valid_result] Result != null;  **/
    }



}
