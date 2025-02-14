package net.global_village.foundation;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSTimestampFormatter;


/**
 * This is a collection of convenience methods for accessing CustomInfo.plist values.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class DefaultValueRetrieval
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private DefaultValueRetrieval()
    {
        super();
    }



    /**
     * Helper method for getting default values.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path: could be a dictionary, array or string.  Returns null if the keyPath was not found
     */
    protected static Object findDefaultValue(java.lang.Class aClass, String keyPath)
    {
        /** require [valid_aClass_param] aClass != null; [valid_keyPath_param] keyPath != null; **/

        NSBundle theBundle = NSBundleAdditions.bundleForClass(aClass);
        NSDictionary theDictionary = NSBundleAdditions.infoDictionary(theBundle);
        return theDictionary.valueForKeyPath(keyPath);
    }



    /**
     * Returns <code>true</code> if <code>keyPath</code> exists in <code>anObject</code>'s bundle's info dictionary.  Returns <code>false</code> otherwise.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return <code>true</code> iff <code>keyPath</code> exists in <code>anObject</code>'s bundle's info dictionary
     */
    public static boolean defaultValueExists(java.lang.Class aClass, String keyPath)
    {
        /** require [valid_aClass_param] aClass != null; [valid_keyPath_param] keyPath != null; **/

        return findDefaultValue(aClass, keyPath) != null;
    }



    /**
     * Returns <code>true</code> if <code>keyPath</code> exists in <code>anObject</code>'s bundle's info dictionary.  Returns <code>false</code> otherwise.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return <code>true</code> iff <code>keyPath</code> exists in <code>anObject</code>'s bundle's info dictionary
     */
    public static boolean defaultValueExists(Object anObject, String keyPath)
    {
        /** require [valid_anObject_param] anObject != null; [valid_keyPath_param] keyPath != null; **/

        return defaultValueExists(anObject.getClass(), keyPath);
    }



    /**
     * Returns an untyped value for the key path.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path: could be a dictionary, array or string
     */
    public static Object defaultValue(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath); **/

        return findDefaultValue(aClass, keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an untyped value for the key path.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path: could be a dictionary, array or string
     */
    public static Object defaultValue(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath); **/

        return defaultValue(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>String</code> value for the key path.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static String defaultString(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath);
        [default_value_is_string] defaultValue(aClass, keyPath) instanceof String; **/

        return (String)findDefaultValue(aClass, keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>String</code> value for the key path.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static String defaultString(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath);
        [default_value_is_string] defaultValue(anObject, keyPath) instanceof String; **/

        return defaultString(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>NSArray</code> value for the key path.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static NSArray defaultArray(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath);
        [default_value_is_NSArray] defaultValue(aClass, keyPath) instanceof NSArray; **/

        return (NSArray)findDefaultValue(aClass, keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>NSArray</code> value for the key path.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static NSArray defaultArray(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath);
        [default_value_is_NSArray] defaultValue(anObject, keyPath) instanceof NSArray; **/

        return defaultArray(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an NSNumberFormatter with the format set to the value of the key path.  Invalid formats are not detected.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static NSNumberFormatter defaultNumberFormatter(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath);
        [default_value_is_string] defaultValue(aClass, keyPath) instanceof String; **/

        return new NSNumberFormatter(defaultString(aClass, keyPath));

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an NSNumberFormatter with the format set to the value of the key path.  Invalid formats are not detected.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static NSNumberFormatter defaultNumberFormatter(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath);
        [default_value_is_string] defaultValue(anObject, keyPath) instanceof String; **/

        return defaultNumberFormatter(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestampFormatter</code> with the format set to the value of the key path.  Invalid formats are not detected.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static NSTimestampFormatter defaultTimestampFormatter(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath);
        [default_value_is_string] defaultValue(aClass, keyPath) instanceof String; **/

        return new NSTimestampFormatter(defaultString(aClass, keyPath));

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSTimestampFormatter</code> with the format set to the value of the key path.  Invalid formats are not detected.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static NSTimestampFormatter defaultTimestampFormatter(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath);
        [default_value_is_string] defaultValue(anObject, keyPath) instanceof String; **/

        return defaultTimestampFormatter(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>BigInteger</code> value for the key path.  Raises an exception if the key path can not be converted to an int.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static BigInteger defaultBigInteger(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath);
        [default_value_is_string] defaultValue(aClass, keyPath) instanceof String; **/

        return new BigInteger(defaultString(aClass, keyPath));

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>BigInteger</code> value for the key path.  Raises an exception if the key path can not be converted to an int.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static BigInteger defaultBigInteger(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath);
        [default_value_is_string] defaultValue(anObject, keyPath) instanceof String; **/

        return defaultBigInteger(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>BigDecimal</code> value for the key path.  Raises an exception if the key path can not be converted to an int.
     *
     * @param aClass the class in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static BigDecimal defaultBigDecimal(java.lang.Class aClass, String keyPath)
    {
        /** require
        [valid_aClass_param] aClass != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(aClass, keyPath);
        [default_value_is_string] defaultValue(aClass, keyPath) instanceof String; **/

        return new BigDecimal(defaultString(aClass, keyPath));

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a <code>BigDecimal</code> value for the key path.  Raises an exception if the key path can not be converted to an int.
     *
     * @param anObject the object in whose bundle we will search
     * @param keyPath the key path to search for
     * @return the value at the key path
     */
    public static BigDecimal defaultBigDecimal(Object anObject, String keyPath)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_keyPath_param] keyPath != null;
        [default_value_exists] defaultValueExists(anObject, keyPath);
        [default_value_is_string] defaultValue(anObject, keyPath) instanceof String; **/

        return defaultBigDecimal(anObject.getClass(), keyPath);

        /** ensure [valid_result] Result != null; **/
    }



}
