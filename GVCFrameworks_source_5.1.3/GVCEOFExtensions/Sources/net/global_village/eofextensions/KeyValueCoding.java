package net.global_village.eofextensions;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Methods related to NSKeyValueCoding. 
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$.
 */
public class KeyValueCoding
{

    
    
    /**
     * Copies values from source into destination based on mapping.  Keys in mapping are used to extract
     * values from source.  The resulting value value is set into destination with a key that is the value associated
     * with the key in mapping. Expressed in code:
     * <pre>
     * destination.takeValueForKey(source.valueForKey(key), 
     *                             mapping.objectForKey(key));
     * </pre>
     * Uses safeTakeValueForKey() to move the value into destination.  Values in source that do not correspond to a key 
     * in mapping are ignored.  This method is similar to EOKeyValueCodingAdditions.takeValuesFromDictionaryWithMapping 
     * but does not require a one to one mapping between source and mapping.
     * 
     * @param source keyed dictionary of values to put into destination
     * @param mapping keyed dictionary of names in destination
     * @param destination object to receive values from source under the names given in mapping
     */
    public static void copyMappedValues(NSDictionary source, NSDictionary mapping, NSKeyValueCoding destination)
    {
        /** require [valid_source] source != null;  
                    [valid_mapping] mapping != null;  
                    [valid_destination] destination != null; **/
        Enumeration sourceNames = mapping.keyEnumerator();
        while (sourceNames.hasMoreElements())
        {
            String sourceName = (String) sourceNames.nextElement();
            Object value = source.valueForKey(sourceName);
            String key = (String)mapping.objectForKey(sourceName);
            safeTakeValueForKey(destination, value, key);
        }
    }
    

    
    /**
     * Moves values from source into destination based on the contents of keys.  Essentially, this is a 
     * filtered version of EOKeyValueCodingAdditions.takeValuesFromDictionary where the contents of keys
     * filters which values are moved.  Uses safeTakeValueForKey() to move the value into destination.
     * 
     * @param source the object where the values will come from
     * @param destination the object where the values will go
     * @param keys list of keys determining which values will be moved
     */
    public static void takeValuesForKeys(NSKeyValueCoding source, NSKeyValueCoding destination, NSArray keys)
    {
        /** require [valid_source] source != null;
                    [valid_keys] keys != null;
                    [valid_destination] destination != null;  **/
        for (Enumeration keynum = keys.objectEnumerator(); keynum.hasMoreElements(); )
        {
            String key = (String)keynum.nextElement();
            safeTakeValueForKey(destination, source.valueForKey(key), key);
        }
     }
    
    
    
    /**
     * Implements takeValueForKey and safely handles both null and NSKeyValueCoding.NullValue values. If value is null 
     * and destination does not implement EOKeyValueCoding, NSKeyValueCoding.NullValue is set into destination.  
     * If value is NSKeyValueCoding.NullValue and destination implements EOKeyValueCoding, null is set into the destination. 
     * 
     * @param destination object to receive value
     * @param value value to put into destination
     * @param key name to put value under in destination
     */
    public static void safeTakeValueForKey( NSKeyValueCoding destination, Object value, String key)
    {
        /** require [valid_key] key != null;
                    [valid_destination] destination != null;  **/
        if (destination instanceof EOKeyValueCoding)
        {
            // EOEnterpriseObject objects handle null properly
            value = (value == NSKeyValueCoding.NullValue) ? null : value;
            ((EOKeyValueCoding)destination).takeValueForKey(value, key);
        }
        else
        {
            value = (value == null) ? NSKeyValueCoding.NullValue : value;
            destination.takeValueForKey(value, key);
        }
    }
    
}
