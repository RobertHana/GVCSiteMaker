// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.io.*;
import java.util.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.foundation.*;

/**
 * A collection of URL related methods.
 */
public class URLUtils extends Object
{
    // Added @ so e-mail addresses validate.  Not sure if this is a good idea... -ch
    private static String urlChars = "abcdefghijklmnopqrstuvwxyz0123456789-_.@";


    /**
     * Returns a string containing only those characters which are valid in URLs without URLEncoding.
     *
     * @return a string containing only those characters which are valid in URLs without URLEncoding.
     */
    public static String urlChars()
    {
        return urlChars;

        /** ensure [result_not_null] Result != null; **/
    }

    

    /**
     * Returns a list of the recognized URI schemes (referred to in the application as prefixes).  See <a href="http://www.w3.org/Addressing/schemes.html">Addressing Schemes</a> for details.
     *
     * @return a list of the recognized URI schemes (referred to in the application as prefixes).
     */
    public static NSArray uriSchemes()
    {
        return SMApplication.appProperties().arrayPropertyForKey("URLPrefixes");

        /** ensure [result_not_null] Result != null; **/
    }
  


    /**
     * Returns <code>true</code> if the string contains only valid URL characters as determined by SMStringUtils.urlChars().  The comparision is case insensitive.
     *
     * @return <code>true</code> if the string contains only valid URL characters as determined by SMStringUtils.urlChars()
     */
    public static boolean stringContainsOnlyValidURLChars(String aString)
    {
        /** require [a_string_not_null] aString != null; **/
        
        return SMStringUtils.stringContainsOnlyCharsInString(aString, urlChars());
    }



    /**
     * Returns <code>true</code> if the string starts with a recognized URI scheme (referred to in the application as prefix).
     *
     * @return <code>true</code> if the string starts with a recognized URI scheme (referred to in the application as prefix).
     */
    public static boolean hasValidURIScheme(String aURI)
    {
        /** require [a_uri_not_null] aURI != null; **/
 
        return hasValidURIScheme(uriSchemes(), aURI);
    }
    
    
    
    /**
     * Returns <code>true</code> if aURI has a scheme that is in the list of uriSchemes.
     *
     * @return <code>true</code> if aURI has a scheme that is in the list of uriSchemes.
     */
    public static boolean hasValidURIScheme(NSArray listOfURISchemes, String aURI)
    {
        /** require [a_uri_not_null] aURI != null;
            		   [list_of_uri_schemes_not_null] listOfURISchemes != null;
         **/
        
        boolean hasValidURIScheme = false;
        String normalizedURI = aURI.toLowerCase();
        
        Enumeration schemeEnumerator = listOfURISchemes.objectEnumerator();
        while (schemeEnumerator.hasMoreElements())
        {
            String scheme = (String) schemeEnumerator.nextElement();
            if (normalizedURI.startsWith(scheme))
            {
                hasValidURIScheme = true;
                break;
            }
        }

        return hasValidURIScheme;
    }    



    /**
     * Returns a recognized scheme from a URI.
     *
     * @return a recognized scheme from a URI.
     */
    public static String schemeFromURI(String aURI)
    {
        /** require
        [a_uri_not_null] aURI != null;
        [has_valid_uri_scheme] hasValidURIScheme(aURI); **/
        
        String schemeFromURI = null;
        String normalizedURI = aURI.toLowerCase();

        Enumeration schemeEnumerator = uriSchemes().objectEnumerator();
        while (schemeEnumerator.hasMoreElements())
        {
            String scheme = (String) schemeEnumerator.nextElement();
            if (normalizedURI.startsWith(scheme))
            {
                schemeFromURI = scheme;
                break;
            }
        }

        JassAdditions.post("URLUtils",
                           "schemeFromURI",
                           uriSchemes().containsObject(schemeFromURI));

        return schemeFromURI;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the part of a URI following a recognized scheme.  This part is scheme specific and does not have a standard name, but I use address as this is generally applicable.
     *
     * @return the part of a URI following a recognized scheme.  This part is scheme specific and does not have a standard name, but I use address as this is generally applicable.
     */
    public static String addressFromURI(String aURI)
    {
        /** require
        [a_uri_not_null] aURI != null;
        [has_valid_uri_scheme] hasValidURIScheme(aURI); **/
        
        int startOfAddress = schemeFromURI(aURI).length();
        
        return aURI.substring(startOfAddress);

        /** ensure [result_not_null] Result != null; **/
    }



    /** Make something url/http normal without URLEncoding. Nice to
     * have around if you want to scrub all the cruft out of a string
     * and make it into a filename or url or something something.
     * */
    public static String toLowerAndNormalize(String aString)
    {
        StringBuffer b = new StringBuffer(aString.toLowerCase());
        int i;
        char c;

        for (i = 0; i < b.length(); i++) {
            c = b.charAt(i);
            
            // Only normalize Latin characters (0x0000 to 0x00FF).  All others 
            // (Unicode) get left as is and are handled by URLEncoding.
            if ((c <= 0x00FF) && (urlChars.indexOf(c) < 0)) {
                b.setCharAt(i, '_');
            }
        }
        return b.toString();
    }



    /**
     * Translates a string into x-www-form-urlencoded format.  See java.net.URLEncoder for more information. 
     *
     * @param s - String to be translated.
     * @return the translated String.
     */
    static public String urlEncode(String s)
    {
        try
        {
            return java.net.URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * Decodes a "x-www-form-urlencoded" to a String.  See java.net.URLDecoder for more information. 
     *
     * @param s - the String to decode
     * @return the newly decoded String
     */
    static public String urlDecode(String s)
    {
        try
        {
            return java.net.URLDecoder.decode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * Returns an error message to display to the user for an invalid URL.  
     *
     * @return an error message to display to the user for an invalid URL.
     */
    public static String invalidURLErrorMessage(String url)
    {
        /** require [url_not_null] url != null; **/
        
        return "The URL '" + url + "' is not valid because it does not start with one of the recognized internet prefixes from this list: " + URLUtils.uriSchemes().componentsJoinedByString(", ") + ".";
    }
    
    
}
