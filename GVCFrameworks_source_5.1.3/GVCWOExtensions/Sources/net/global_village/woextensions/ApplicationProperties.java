package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.*;


/**
 * <code>ApplicationProperties</code> provides convenient ways of accessing and combining properties from different property list files (Ascii or XML format). It is useful in a WOApplication sub-class so that bindings like application.properties.someValue can be made..<br>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 12$
 */
public class ApplicationProperties extends Object implements NSKeyValueCoding, NSKeyValueCodingAdditions
{
    protected NSMutableDictionary _properties = null;


    /**
     * Designated constructor.
     */
    public ApplicationProperties()
    {
        super();

        _properties = new NSMutableDictionary();
    }



    /**
     * Adds the properties from the property list file plist in the indicated framework to the properties already in this object.  If a property already in this object has the same key as one in plist it will be replaced.  This method is useful for combining multiple configuration files into one. The plist can be in XML or the ASCII plist format
     *
     * @param plist the path and name (relative to the Resources directory) of the plist file to create the properties from.
     * @param framework optional framework to read the plist file from.  Should be null if plist is in the application.
     *
     * @throws java.lang.IllegalArgumentException if the plist file has a syntax error.
     */
    public void addPropertiesFromFile(String plist, String framework)
    {
        /** require [plist_not_null] plist != null; **/

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            NSLog.debug.appendln("Loading properties from resource: " + plist);

        WOResourceManager rm = WOApplication.application().resourceManager();
        NSDictionary newProperties;

        // Try this first as bytesForResourceNamed throws a null pointer exception if the file cannot be found.
        java.io.InputStream resourceStream = rm.inputStreamForResourceNamed(plist, framework, null);
        if (resourceStream != null)
        {
            byte[] bytesFromResource = rm.bytesForResourceNamed(plist, framework, null);
            newProperties = (NSDictionary) NSPropertyListSerialization.propertyListFromData(new NSData(bytesFromResource), "US-ASCII");
        }
        else
        {
            newProperties = new NSDictionary();	// This value is used if the plist file cannot be loaded.
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                NSLog.debug.appendln("Can not locate file " + plist + " in framework " + framework + ".  Setting properties to empty dictionary.");
        }

        properties().addEntriesFromDictionary(newProperties);

        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            NSLog.debug.appendln("Done loading properties from file: " + plist);
    }



    /**
     * Adds the property and associates it with the indicated key.  If a property is already associated with this key it will be replaced.
     *
     * @param newProperty property to add to this object
     * @param key key to associate this property with
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
     * @param key key to remove associated this property for
     */
    public void removePropertyForKey(String key)
    {
        /** require  [key_not_null] key != null; **/

        properties().removeObjectForKey(key);

        /** ensure [property_removed] properties().valueForKey(key) == null; **/
    }


    /**
     * Creates a date formatter (NSTimestampFormatter) using a pattern looked up in properties for the key propertyName.  The pattern is then removed from properties (to prevent its accidental use in place of the formatter) and the newly created formatter is added under the key propertyName + "ter"  (e.g. the pattern named StandardDateFormat is removed and a date formatter is added under StandardDateFormatter).
     *
     * @param propertyName - the key to look up the date format string under in properties()
     */
    public void createDateFormatter(String propertyName)
    {
        /** require [valid_param] propertyName != null;  [property_exists] hasPropertyForKey(propertyName);  **/

        NSTimestampFormatter dateFormatter = new NSTimestampFormatter();
        dateFormatter.setPattern(stringPropertyForKey(propertyName));
        addPropertyForKey(dateFormatter, propertyName + "ter");
        removePropertyForKey(propertyName);

        /** ensure
        [property_removed] ! hasPropertyForKey(propertyName);
        [property_exists] hasPropertyForKey(propertyName + "ter");
        **/
    }



    /**
     * Creates a number formatter (NSNumberFormatter) using a pattern looked up in properties for the key propertyName.  The pattern is then removed from properties (to prevent its accidental use in place of the formatter) and the newly created formatter is added under the key propertyName + "ter"  (e.g. the pattern named StandardNumberFormat is removed and a date formatter is added under StandardNumberFormatter).
     *
     * @param propertyName - the key to look up the date format string under in properties()
     */
    public void createNumberFormatter(String propertyName)
    {
        /** require
        [valid_param] propertyName != null;
        [property_exists] hasPropertyForKey(propertyName);  **/

    	NSNumberFormatter numberFormatter;

        numberFormatter = new NSNumberFormatter();
        numberFormatter.setPattern(stringPropertyForKey(propertyName));
        addPropertyForKey(numberFormatter, propertyName + "ter");
        removePropertyForKey(propertyName);

        /** ensure
        [property_removed] ! hasPropertyForKey(propertyName);
        [property_exists] hasPropertyForKey(propertyName + "ter"); **/
    }



    /**
     * Returns <code>true</code> if there is a property for the corresponding key.
     *
     * @param aKey the key to look up in the properties dictionary
     * @return <code>true</code> if there is a property for the corresponding key.
     */
    public boolean hasPropertyForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/
        return valueForKey(aKey) != null;
    }



    /**
     * Returns the value for the property identified by <code>aKey</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public Object valueForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/
        return valueForKeyPath(aKey);
    }


    /**
     * Returns the value from the properties dictionary which corresponds to aKeyPath. Note: This method always looks for corresponding values for aKeyPath from the plist file and does not handle aKeyPath that is defined in this class.
     *
     * @param aKeyPath the key to search for
     * @return the value from the properties dictionary which corresponds to aKeyPath
     */
    public Object valueForKeyPath(String aKeyPath)
    {
        /** require [valid_param] aKeyPath != null; **/
        return properties().valueForKeyPath(aKeyPath);
    }



    /**
     * For corresdonance to NSKeyValueCoding. Just raises, since you can't set Application properties at runtime.
     *
     * @param value the value to take
     * @param aKey the key to search for
     */
    public void takeValueForKey(Object value, String aKey)
    {
        throw new RuntimeException("Can't set Application properties at runtime.");
    }



    /**
     * For corresdonance to NSKeyValueCodingAdditions. Just raises, since you can't set Application properties at runtime.
     *
     * @param value the value to take
     * @param aKeyPath the keypath to search for
     */
    public void takeValueForKeyPath(Object value, String aKeyPath)
    {
        throw new RuntimeException("Can't set Application properties at runtime.");
    }



    /**
     * Returns the value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. A cover method for valueForKeyPath()
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public Object propertyForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/

        return valueForKeyPath(aKey);
    }



    /**
     * Returns the <code>String</code> value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. It casts the return type to a <code>String</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public String stringPropertyForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/

        return (String) valueForKeyPath(aKey);
    }



    /**
     * Returns the integer value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. It casts the return type to a <code>integer</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public int intPropertyForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/
        Object property = propertyForKey(aKey);

        if (property instanceof String &&
           net.global_village.foundation.StringAdditions.isInteger((String)property))
        {
            return Integer.parseInt(property.toString());
        }

        if (property instanceof Number)
        {
            return ((Number)property).intValue();
        }

        throw new IllegalArgumentException(aKey + " is not a valid integer:" + property);
    }



    /**
     * Returns the integer value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. It casts the return type to a <code>integer</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public long longPropertyForKey(String aKey)
    {
        /** require
        [valid_param] aKey != null;
        [is_integer] net.global_village.foundation.StringAdditions.isInteger(stringPropertyForKey(aKey)); **/

        return Long.parseLong(propertyForKey(aKey).toString());
    }



    /**
     * Returns the boolean value corresponding to <code>aKey</code> from the <code>properties</code> dictionary.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public boolean booleanPropertyForKey(String aKey)
    {
        /** require
        [valid_param] aKey != null;
        [is_true_or_false] stringPropertyForKey(aKey).equals("true") || stringPropertyForKey(aKey).equals("false"); **/

        return Boolean.valueOf(stringPropertyForKey(aKey)).booleanValue();
    }



    /**
     * Returns the float value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. It casts the return type to a <code>float</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public float floatValuePropertyForKey(String aKey)
    {
        /** require
        [valid_param] aKey != null;
        [is_float] net.global_village.foundation.StringAdditions.isFloat(stringPropertyForKey(aKey)); **/

        return (new Float(stringPropertyForKey(aKey))).floatValue();
    }



    /**
     * Returns the array value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. It casts the return type to an <code>NSArray</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public NSArray arrayPropertyForKey(String aKey)
    {
        /** require
        [valid_param] aKey != null;
        [is_array] valueForKeyPath(aKey) instanceof NSArray; **/

        return (NSArray)valueForKeyPath(aKey);
    }



    /**
     * Returns the NSDictionary value corresponding to <code>aKey</code> from the <code>properties</code> dictionary. It casts the return type to an <code>NSDictionary</code>.
     *
     * @param aKey the key to search for
     * @return the value from the properties dictionary which corresponds to aKey
     */
    public NSDictionary dictionaryPropertyForKey(String aKey)
    {
        /** require
        [valid_param] aKey != null;
        [is_dictionary] valueForKeyPath(aKey) instanceof NSDictionary; **/

        return (NSDictionary)valueForKeyPath(aKey);
    }



    /**
     * Returns the property (as a NSNumberFormatter) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key the key to look up in the properties dictionary
     * @return the property (as a NSNumberFormatter) for the corresponding key
     */
    public NSNumberFormatter numberFormatterForKey(String key)
    {
        return (NSNumberFormatter) propertyForKey(key);
    }



    /**
     * Returns the property (as a java.text.Format) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key the key to look up in the properties dictionary
     * @return the property (as a java.text.Format) for the corresponding key
     */
    public java.text.Format formatterForKey(String key)
    {
        return (java.text.Format) propertyForKey(key);
    }



    /**
     * Returns the property (as a NSTimestampFormatter) for the corresponding key.  Raises RuntimeException if the key is not found.
     *
     * @param key the key to look up in the properties dictionary
     * @return the property (as a NSTimestampFormatter) for the corresponding key
     */
    public NSTimestampFormatter timestampFormatterForKey(String key)
    {
        return (NSTimestampFormatter) propertyForKey(key);
    }



    /**
     * An accessor method for the <code>properties</code> dictionary.  This dictionary is made protected as it shouldn't be accessed directly.  To access the values of this dictionary, the cover methods such as stringPropertyForKey(), intPropertyForKey(), etc. should be used
     *
     * @return the dictionary containing the properties
     */
    protected NSMutableDictionary properties()
    {
        return _properties;
    }



    /**
     * Overriden to print the dictionary of properties.
     *
     * @return a string representing the dictionary containing the properties
     */
    public String toString()
    {
        return _properties.toString();
    }



}
