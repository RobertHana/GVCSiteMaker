package net.global_village.foundation;

import com.webobjects.foundation.*;


/**
 * Additional functionality related to NSPropertyListSerialization.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.

 */
public class PropertyListSerialization
{

    /**
     * Attempts to verify that string is a valid serialized representation of an NSArray in classic (not XML) plist format.
     *
     * @param string the String to validate as a serialized representation of an NSArray
     * @return <code>true</code> if string is a valid serialized representation of an NSArray
     */
    public static boolean isStringSerializedArray(String string)
    {
        /** require [valid_string] string != null;  **/
        String trimmedString = string.trim();
NSLog.out.appendln("trimmedString " + trimmedString);
        try
        {
            if (trimmedString.startsWith("(") && trimmedString.endsWith(")") )
            {
                NSPropertyListSerialization.arrayForString(string);
                return true;
            }
            else NSLog.out.appendln("trimmedString not () wrapped " + trimmedString);

        }
        catch (Exception e)
        {
            NSLog.out.appendln(e);
        }

        return false;
    }



    /**
     * Attempts to verify that string is a valid serialized representation of an NSDictionary in classic (not XML) plist format.
     *
     * @param string the String to validate as a serialized representation of an NSDictionary
     * @return <code>true</code> if string is a valid serialized representation of an NSDictionary
     */
    public static boolean isStringSerializedDictionary(String string)
    {
        /** require [valid_string] string != null;  **/
        String trimmedString = string.trim();

        try
        {
            if (trimmedString.startsWith("{") && trimmedString.endsWith("}") )
            {
                NSPropertyListSerialization.dictionaryForString(string);
                return true;
            }
        }
        catch (Exception e)
        {
        }

        return false;
    }

}
