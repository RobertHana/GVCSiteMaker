// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.text.*;
import java.util.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.foundation.*;


/** String munging/generation/manipulation.
 *
 * @author   B.W. Fitzpatrick &lt;fitz@apple.com&gt;
 * @version $Revision: 1.3 $
 */
public class SMStringUtils extends Object
{
    private static String keyChars = "abcdefghijklmnopqrstuvwxyz" +
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "012345678901234567890123456789" +
        "012345678901234567890123456789";

    private static String passwordChars = null;
    private static Random generator = new Random();


    public static String keyChars()
    {
        return keyChars;
    }

    public static String passwordChars()
    {
        if (passwordChars == null)
        {
            setPasswordChars(SMApplication.appProperties().stringPropertyForKey("PasswordChars"));
        }
        
        return passwordChars;
    }
    
    
    /** Sets the characters that are used for generating passwords in
     * SMStringUtils.passwordWithLength(int). */
    public static void setPasswordChars(String value) {
        passwordChars = value;
    }

    
    
    
    /** Good for making up Session-key flavored things. */
    public static String randomKeyWithLength(int length)
    {
        return SMStringUtils._randomStringWithLengthFromCharset(length, keyChars);
    }


    
    /** Good for making up random (but not non-deterministic)
     * passwords for users. Not terribly secure, but adequate for
     * lightweight protection. You can change the set of characters
     * used to generate these passwords by feeding a string of
     * characters to SMStringUtils.setPasswordChars(java.lang.String)
     * */
    public static String passwordWithLength(int length)
    {
        if (SMApplication.appProperties().booleanPropertyForKey("DevelopmentMode")) 
            return "foo";
        
        return SMStringUtils._randomStringWithLengthFromCharset(length, passwordChars());
    }


    
    protected static String _randomStringWithLengthFromCharset(int length, String charset)
    {
        int someNumber;
        char[] password = new char[length];

        for(int i = 0; i < length; i++) {
            short num = (short)SMStringUtils.generator.nextInt();
            if (num < 0) { num += 32768; } // Piece of crap only returns signed ints.
            someNumber = num % charset.length();
            password[i] = charset.charAt(someNumber);
        }
        
        return new String(password);
    }


    
    public static boolean stringContainsString(String parentString, String subString)
    {

        // Use indexOf to check for location of substring; -1 is the result of nothing found
        if (parentString.indexOf(subString) >= 0)
            return true;
        else
            return false;
    }

    
    public static String stringByReplacingInString(String toReplace, String replaceWith, String sourceString)
    {
        String workingString = new String(sourceString);

        // Check for index matches, and proceed if found
        while (workingString.indexOf(toReplace) >= 0) {

            int startIndex = workingString.indexOf(toReplace);
            int endIndex = startIndex + toReplace.length();

            // Simply replace the item by index size
            workingString = workingString.substring(0, startIndex) + replaceWith + workingString.substring(endIndex, workingString.length());
        }

        return workingString;
    }


    
    public static NSArray arrayFromStringWithSeparator(String stringToBeSplit,
                                                       String separator)
    {

        NSMutableArray strings = new NSMutableArray();

        int start = 0;
        int pos;
        int separatorLength = separator.length();

        while ((pos = stringToBeSplit.indexOf(separator, start)) >= 0) {
            if( pos == 0 )
                strings.addObject( new String("") );
            else
                strings.addObject( stringToBeSplit.substring(start, pos) );

            start = pos + separatorLength;
        }

        // must remember to add the last item also!
        strings.addObject( stringToBeSplit.substring(start) );

        return new NSArray(strings);
    }
  


    /**
     * Returns <code>true</code> if the string <code>aString</code> contains only characters in the string <code>allowedChars</code>.  The comparision is case insensitive.
     *
     * @param aString - the string to check
     * @param allowedChars - string of characters to allow.
     * @return <code>true</code> if the string <code>aString</code> contains only characters in the string <code>allowedChars</code>
     */
    public static boolean stringContainsOnlyCharsInString(String aString, String allowedChars)
    {
        /** require
        [a_string_not_null] aString != null;
        [allowed_chars_not_null] allowedChars != null; **/
        
        boolean isValidString = true;
        
        allowedChars = allowedChars.toLowerCase();
        StringCharacterIterator iterator = new StringCharacterIterator(aString.toLowerCase());
        for (char nextChar = iterator.first(); nextChar != StringCharacterIterator.DONE; nextChar = iterator.next())
        {
            if (allowedChars.indexOf(nextChar) == -1)
            {
                isValidString = false;
                break;
            }
        }

        return isValidString;
    }



    /**
     * Return the last word (all the text after the last space) from the passed string.
     *
     * @return the last word (all the text after the last space) from the passed string.
     */
    public static String lastWord(String text)
    {
        /** require [valid_param] text != null;  **/
        
        JassAdditions.pre("SMStringUtils", "lastWord [has_space] ", text.lastIndexOf(' ') != -1 );
        
        return text.substring(text.lastIndexOf(' ') + 1);
        
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Trims text and returns it.  Returns null if null is passed, or if text has
     * zero length after being trimmed.
     *
     * @param text the text to trim
     * @return trimmed text Trims or null if null is passed, or if text has
     * zero length after being trimmed
     */
    public static String trimmedString(String text)
    {
        if (text != null)
        {
            text = text.trim();

            if (text.length() == 0)
            {
                text = null;
            }
        }
        
        return text;
        
        /** ensure [valid_result] (Result == null) || (Result.trim().equals(Result)); **/
    }
    


    /* Generic setters and getters ***************************************/



}
