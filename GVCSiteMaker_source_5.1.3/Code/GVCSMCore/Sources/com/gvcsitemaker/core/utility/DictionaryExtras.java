// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.util.*;

import net.global_village.foundation.*;

import com.webobjects.foundation.*;

public class DictionaryExtras {

    public static boolean dictionaryHasKey(NSDictionary aDictionary, String aKey) {

        // Set the return value (default) to false	
        boolean returnValue = false;

        Enumeration enumerator = aDictionary.keyEnumerator();
        while (enumerator.hasMoreElements()) {

            // Check each key/value pair in the dictionary
            Object nextKey = enumerator.nextElement(); 
            if ((nextKey.toString()).equals(aKey)) {

                returnValue = true;
            } 
        }

        // Send back the results;
        return returnValue;
    }


    /**
     * Swaps the keys and elements in a dictionary.  Only works if the objects are unique and can be used as keys.
     *
     * @return the "inverse" of the dictionary passed in.
     */
    static public NSMutableDictionary invertDictionary(NSDictionary dictionary)
    {
        NSMutableDictionary inverseDictionary = new NSMutableDictionary();

        if (dictionary != null)
        {
            Enumeration dictEnum = dictionary.keyEnumerator();
            while (dictEnum.hasMoreElements())
            {
                // Take the element and invert the values when put into the return dict
                String nextKey = (String)dictEnum.nextElement();
                inverseDictionary.setObjectForKey(nextKey, dictionary.objectForKey(nextKey));
            }
        }

        JassAdditions.post("DictionaryExtras",
                           "invertDictionary()",
                           ((dictionary == null) || (dictionary.count() == inverseDictionary.count())));

        return inverseDictionary;

        /** ensure [result_not_null] Result != null; **/
    }
}
