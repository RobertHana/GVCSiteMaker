// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import net.global_village.woextensions.*;

import com.webobjects.foundation.*;

/**
 * Wrapper class for a plist file providing convenient initialization and some accessor methods.  It is useful in a WOApplication sub-class so that bindings like application.properties.someValue can be made.
 */
public class SMProperties extends Object implements NSKeyValueCoding, NSKeyValueCodingAdditions
{

    protected NSMutableDictionary _properties = null;
  

    /**
     * Initializes the properties from the property list file plist in the indicated framework.
     *
     * @param plist - the path and name (relative to the Resources directory) of the plist file to create the properties from.
     * @param framework - optional framework to read the plist file from.  Should be null if plist is in the application.
     */
    public SMProperties(String plist, String framework)
    {
        /** require [plist_not_null] plist != null; **/
        
        DebugOut.println(20,"Initializing with properties from file: " + plist);

        _properties = new NSMutableDictionary();	

        addPropertiesFromFile(plist, framework);
    }

    

    /**
     * Adds the properties from the property list file plist in the indicated framework to the properties already in this object.  If a property already in this object has the same key as one in plist it will be replaced.  This method is useful for combining multiple configuration files into one.
     *
     * @param plist - the path and name (relative to the Resources directory) of the plist file to create the properties from.
     * @param framework - optional framework to read the plist file from.  Should be null if plist is in the application.
     *
     * @throw java.lang.IllegalArgumentException - if the plist file has a syntax error.
     */
    public void addPropertiesFromFile(String plist, String framework)
    {
        /** require [plist_not_null] plist != null; **/
        
        DebugOut.println(1,"Loading properties from file: " + plist + " in " +
            (framework == null ? "Application" : framework));

        NSDictionary newProperties;

        String plistString = ResourceManagerAdditions.stringFromResource(plist, framework);

        if (plistString != null)
        {
            newProperties = (NSDictionary) NSPropertyListSerialization.propertyListFromString(plistString);
        }
        else
        {
            newProperties = new NSDictionary();	// This value is used if the plist file cannot be loaded.
            DebugOut.println(1, "Can not locate file " + plist + " in framework " + framework + " to plist.  Setting properties to empty dictionary.");
        }

        properties().addEntriesFromDictionary(newProperties);

        DebugOut.println(1,"Done loading properties from file: " + plist);
    }



    /**
     * Adds the property and associates it with the indicated key.  If a property is already associated with this key it will be replaced.
     *
     * @param newProperty - property to add to this object
     * @param key - key to associate this property with
     */
    public void addPropertyForKey(Object newProperty, String key)
    {
        /** require
        [new_property_not_null] newProperty != null;
        [key_not_null] key != null; **/
        
        properties().setObjectForKey(newProperty, key);

        /** ensure [property_added] properties().valueForKey(key).equals(newProperty); **/
    }



    /**
     * Removes the property associated with the indicated key.
     *
     * @param key - key to remove associated this property for
     */
    public void removePropertyForKey(String key)
    {
        /** require  [key_not_null] key != null; **/
        
        properties().removeObjectForKey(key);

        /** ensure [property_removed] properties().valueForKey(key) == null; **/
    }



    /**
     * Returns <code>true</code> if there is a property for the corresponding key.
     *
     * @param key - the key to look up in the properties dictionary
     * @return <code>true</code> if there is a property for the corresponding key.
     */
    public boolean hasPropertyForKey(String key)
    {
        return (properties().objectForKey(key) != null);
    }



    /**
     * Returns the property (as a java.lang.Object) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the value in the properties dictionary corresponding to key.
     */
    public Object propertyForKey(String key)
    {
        Object propertyForKey = properties().objectForKey(key);

        if (propertyForKey == null)
        {
            throw new RuntimeException("No property found for key " + key);
        }

        return propertyForKey;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the property (as a java.lang.String) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a java.lang.String) for the corresponding key.
     */
    public String stringPropertyForKey(String key)
    {
        return (String)propertyForKey(key);
    }



    /**
     * Returns the property (as a com.webobjects.foundation.NSArray) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a com.webobjects.foundation.NSArray) for the corresponding key.
     */
    public NSArray arrayPropertyForKey(String key)
    {
        return (NSArray)propertyForKey(key);
    }



    /**
     * Returns the property (as a com.webobjects.foundation.NSDictionary) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a com.webobjects.foundation.NSDictionary) for the corresponding key.
     */
    public NSDictionary dictionaryPropertyForKey(String key)
    {
        return (NSDictionary)propertyForKey(key);
    }



    /**
     * Returns the property (as an int) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as an int) for the corresponding key.
     */
    public int intPropertyForKey(String key)
    {
        return Integer.parseInt(propertyForKey(key).toString());
    }



    /**
     * Returns the property (as an long) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as an long) for the corresponding key
     */
    public long longPropertyForKey(String key)
    {
        return Long.parseLong(propertyForKey(key).toString());
    }
    
    
    
    /**
     * Returns the property (as a java.lang.Integer) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a java.lang.Integer) for the corresponding key.
     */
    public Integer integerPropertyForKey(String key)
    {
        return new Integer(stringPropertyForKey(key));
    }



    /**
     * Returns the property (as a boolean) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a boolean) for the corresponding key.
     */
    public boolean booleanPropertyForKey(String key)
    {
        return Boolean.valueOf(propertyForKey(key).toString()).booleanValue();
    }



    /**
     * Returns the property (as a NSNumberFormatter) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a NSNumberFormatter) for the corresponding key.
     */
    public NSNumberFormatter numberFormatterForKey(String key)
    {
        return (NSNumberFormatter) propertyForKey(key);
    }



    /**
     * Returns the property (as a NSTimestampFormatter) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key - the key to look up in the properties dictionary
     * @return the property (as a NSTimestampFormatter) for the corresponding key.
     */
    public NSTimestampFormatter timestampFormatterForKey(String key)
    {
        return (NSTimestampFormatter) propertyForKey(key);
    }



    /**
     * Returns the properties formatted as a dictionary.
     *
     * @return the properties formatted as a dictionary
     */
    public String toString()
    {
        return properties().allKeys().toString();
    }



    /**
     * Returns the dictionary containing the properties.
     *
     * @return the dictionary containing the properties.
     */
    protected NSMutableDictionary properties()
    {
        return _properties;
    }

   

    //** Key Value Coding Support  **//

    /** Conformance to NSKeyValueCoding. */
    public Object valueForKey(String key)
    {
        return properties().valueForKey(key);
    }

    /** Conformance to NSKeyValueCoding. */
    public void takeValueForKey(Object value, String key)
    {
        properties().takeValueForKey(value, key);
    }



    /** Conformance to NSKeyValueCodingAdditions. */
    public Object valueForKeyPath(String keyPath)
    {
        return properties().valueForKeyPath(keyPath);
    }

    /** Conformance to NSKeyValueCodingAdditions. */
    public void takeValueForKeyPath(Object value, String keyPath)
    {
        properties().takeValueForKeyPath(value, keyPath);
    }

    /** invariant [properties_not_null] properties() != null; **/
}
