package net.global_village.woextensions;

import java.util.*;

import com.webobjects.appserver._private.*;
import com.webobjects.foundation.*;


/**
 * Additions to the NSDictionary class that are appropriate for WOExtensions.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */  
public class NSDictionaryAdditions extends Object
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private NSDictionaryAdditions()
    {
        super();
    }



    /**
     * Returns the dictionary as a URL string.  Note that the order of the params on the URL is non-deterministic.
     *
     * @param dictionary the dictionary to convert to a URL string
     * @return the URL string
     */
    public static String urlEncodedDictionary(NSDictionary dictionary)
    {
        StringBuffer urlEncodedDictionary = new StringBuffer();
        
        Enumeration keyEnumerator = dictionary.keyEnumerator();
        while (keyEnumerator.hasMoreElements())
        {
            String aKey = keyEnumerator.nextElement().toString();
            Object value = dictionary.objectForKey(aKey);
    
            // TODO Handle multiple values in array
            if (value instanceof NSArray)
            {
                NSArray tempArray = (NSArray) value;
                value = tempArray.count() > 0 ? tempArray.objectAtIndex(0) : "";
            }
    
            // Skip for first element
            if (urlEncodedDictionary.length() > 0)
            {
                urlEncodedDictionary.append("&");
            }
            
            // TODO Replace this private API with java.net.URLEncoder when we move to JDK 1.4
            urlEncodedDictionary.append(WOURLEncoder.encode(aKey, "UTF-8"));
            urlEncodedDictionary.append("=");
            urlEncodedDictionary.append(WOURLEncoder.encode(value.toString(), "UTF-8"));
        }
    
        return urlEncodedDictionary.toString();
    }

}
