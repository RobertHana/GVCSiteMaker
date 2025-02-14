package net.global_village.foundation;

import java.math.*;
import java.util.*;

import com.webobjects.foundation.*;


/**
 * String utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 13$
 */
public class StringAdditions
{
    public static final String PasswordCharacters = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ123456789";
    protected static Random generator = new Random();

    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private StringAdditions()
    {
        super();
    }



    /**
     * Attempts to separate the key components of the given key path.  Right now, just uses the lame NSArray.componentsSeparatedByString, but if this ever doesn't cut it, this is the only method that need change.
     *
     * @param aString the key or key path to sepearate into components
     * @return <code>true</code> if <code>aString</code> has two or more components separated by periods
     */
    public static NSArray keyPathComponents(String aString)
    {
        /** require [valid_param] aString != null; **/
        return NSArray.componentsSeparatedByString(aString, ".");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given a string determines if it is a key path (two or more parts seperated by periods).
     *
     * @param aString a string to test
     * @return <code>true</code> if <code>aString</code> has two or more components separated by periods
     */
    public static boolean isValidPropertyKeyPath(String aString)
    {
        /** require [valid_param] aString != null; **/

        return keyPathComponents(aString).count() > 1;
    }



    /**
     * Given a key path like object.object.attribute returns the "attribute" part.
     *
     * @param aString the keypath
     * @return the final component in a keypath
     */
    public static String propertyNameFromKeyPath(String aString)
    {
        /** require [valid_param] aString != null; [valid_keypath] isValidPropertyKeyPath(aString); **/

        return (String)keyPathComponents(aString).lastObject();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given a key path like object1.object2.attribute returns the "object1.object2" part.
     *
     * @param aString the keypath
     * @return everything in the keypath except the final component
     */
    public static String objectKeyPathFromKeyPath(String aString)
    {
        /** require [valid_param] aString != null; [valid_keypath] isValidPropertyKeyPath(aString); **/

        NSMutableArray components = new NSMutableArray(keyPathComponents(aString));
        components.removeLastObject();
        return components.componentsJoinedByString(".");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given a key path like object1.object2.attribute returns the "object1" part.
     *
     * @param aString the keypath
     * @return the first component of a key path
     */
    public static String rootKeyFromKeyPath(String aString)
     {
        /** require [valid_param] aString != null; [valid_keypath] isValidPropertyKeyPath(aString); **/

        NSMutableArray components = keyPathComponents(aString).mutableClone();
        return (String)components.objectAtIndex(0);

        /** ensure [valid_result] Result != null; **/
     }



    /**
     * Given a key path like object1.object2.attribute returns the "object2.attribute" part.
     *
     * @param aString the keypath
     * @return everything in the keypath except the first component
     */
    public static String removeRootKeyFromKeyPath(String aString)
    {
        /** require [valid_param] aString != null; [valid_keypath] isValidPropertyKeyPath(aString); **/

        NSMutableArray components = keyPathComponents(aString).mutableClone();
        components.removeObjectAtIndex(0);
        return components.componentsJoinedByString(".");

        /** ensure [valid_result] Result != null; **/
    }



    /**
      * Returns <code>true</code> if this string is null or the empty String (trim().length() == 0).
      *
      * @param aString the string to check
      * @return <code>true</code> if this string is null or the empty String (length() == 0)
      */
     public static boolean isEmpty(String aString)
     {
         return (aString == null) || (aString.trim().length() == 0);
     }



    /**
     * Returns <code>true</code> if this string matched the integer pattern: [whitespace][minussign]digits[whitespace] and <code>false</code> otherwise.  Note that, unlike a float, an integer cannot be preceded by a plus sign "+".  For more info on Integer literals, see section 3.10.1 of the Java Language Specification.
     *
     * @param aString the string to check
     * @return <code>true</code> if this string matched the integer pattern, <code>false</code> otherwise
     */
    public static boolean isInteger(String aString)
    {
        /** require [valid_param] aString != null; **/

        boolean isInteger = true;
        try
        {
            Integer.valueOf(aString.trim());
        }
        catch (NumberFormatException e)
        {
            isInteger = false;
        }
        return isInteger;
    }


    /**
     * Returns <code>true</code> if this string matched the float pattern: [whitespace][sign]digits[.digits]["e" digits][floating point suffix][whitespace] and <code>false</code> otherwise.  For more info on Floating point literals, see section 3.10.2 of the Java Language Specification.
     *
     * @param aString the string to check
     * @return <code>true</code> if this string matched the float pattern, <code>false</code> otherwise
     */
    public static boolean isFloat(String aString)
    {
        /** require [valid_param] aString != null; **/

        boolean isFloat = true;
        try
        {
            Float.valueOf(aString.trim());
        }
        catch (NumberFormatException e)
        {
            isFloat = false;
        }
        return isFloat;
    }


    /**
     * Returns <code>true</code> if this string is a number (optionally surrounded by whitespace), <code>false</code> otherwise.  This method is more restrictive than the above method, since this one will only return <code>true</code> if <code>aString</code> is actually a number as most humans would understand the term (in particular, no suffixes or base prefixes are allowed).
     *
     * @param aString the string to check
     * @return <code>true</code> if this string is a number, <code>false</code> otherwise
     */
    public static boolean isNumber(String aString)
    {
        /** require [valid_param] aString != null; **/

        boolean isNumber = true;
        try
        {
            new BigDecimal(aString.trim());
        }
        catch (NumberFormatException e)
        {
            isNumber = false;
        }
        // This is needed for a bug in JDK 1.3.1.x
        catch (StringIndexOutOfBoundsException emptyString)
        {
            isNumber = false;
        }

        return isNumber;
    }



    /**
     * Return <code>true</code> if all the characters in the string are digits or white space, <code>false</code> otherwise.  Unlike <code>isFloat</code> and <code>isInteger</code>, this method returns <code>true</code> on strings like " 12 45 78 " where whitespace exists between the digits.
     *
     * @param aString the string to check
     * @return <code>true</code> if all the characters in the string are digits or white space, <code>false</code> otherwise
     */
    public static boolean isDigits(String aString)
    {
        /** require [valid_param] aString != null; **/

        boolean isDigits = true;

        int i;
        int stringLength = aString.length();

        //traverse the characters of the string and return false once a character is not a digit or whitespace
        for (i = 0; i < stringLength; i++ )
        {
            if ( ! (Character.isDigit(aString.charAt(i)) || Character.isWhitespace(aString.charAt(i))))
            {
                isDigits = false;
                break;
            }
        }

        return isDigits;
    }



    /**
     * Return <code>true</code> if all the characters in the string are alphabetic, <code>false</code> otherwise.
     *
     * @param aString the string to check
     * @return <code>true</code> if all the characters in the string are alphabetic, <code>false</code> otherwise
     */
    public static boolean isAlphaOnly(String aString)
    {
        /** require [valid_param] aString != null; **/

        boolean isAlphaOnly = true;

        for (int i = 0; isAlphaOnly && i < aString.length(); i++ )
        {
            if ( ! Character.isLetter(aString.charAt(i)))
            {
                isAlphaOnly = false;
            }
        }

        return isAlphaOnly;
    }



    /**
     * Returns <code>true</code> if <code>character</code> is a line break character.
     *
     * @param character the character to check
     * @return <code>true</code> if <code>character</code> is a line break character
     */
    public static boolean isLineBreak(char character)
    {
        return (character == '\n') || (character == '\r');
    }



    /**
     * Returns <code>true</code> if <code>firstCharacter</code> and
     * <code>secondCharacter</code> are not equal and are both a line break
     * character.  This is useful when parsing files which have /n/r or /r/n
     * for a single line break.
     *
     * @see #isLineBreak(char)
     * @param firstCharacter the first character in the line break sequence
     * @param secondCharacter the last character in the line break sequence
     * @return <code>true</code> if <code>firstCharacter</code> and
     * <code>secondCharacter</code> form a single line break
     */
    public static boolean isLineBreakContinuation(char firstCharacter,
                                                  char secondCharacter)
    {
        return (firstCharacter != secondCharacter) &&
        ((firstCharacter == '\n') || (firstCharacter == '\r')) &&
        ((secondCharacter == '\n') || (secondCharacter == '\r'));
    }



    /**
     * Returns <code>true</code> if <code>anEmail</code> is of valid email format: "&lt;string&gt;@&lt;string&gt;.&lt;string&gt;" where &lt;string&gt; is not empty, <code>false</code> otherwise.
     *
     * @deprecated Use the method in EmailAddress in GVCMail instead.
     * @param anEmail the string to check
     * @return <code>true</code> if <code>anEmail</code> is of valid email format, <code>false</code> otherwise
     */
    public static boolean isValidEmailAddressFormat(String anEmail)
    {
        /** require [valid_param] anEmail != null; **/

        NSArray listItems = NSArray.componentsSeparatedByString(anEmail, "@");
        boolean hasAtSignInMiddle = (listItems.count() == 2) &&
            (((String)listItems.objectAtIndex(0)).length() > 0) &&
            (((String)listItems.objectAtIndex(1)).length() > 0);

        boolean hasValidDomain = false;
        if (hasAtSignInMiddle)
        {
            NSArray domainItems = NSArray.componentsSeparatedByString((String)listItems.objectAtIndex(1), ".");
            hasValidDomain = (domainItems.count() >= 2) &&
                (((String)domainItems.objectAtIndex(0)).length() > 0) &&
                (((String)domainItems.objectAtIndex(1)).length() > 0);
        }

        return hasAtSignInMiddle && hasValidDomain;
    }



    /**
     * This will determine if an array of words is contained within the string. Either ensuring all words are matched, or at least 1 word is matched.  Case does not matter.
     * Worker method to support <code>doesStringContainAllWordsInArray</code>, <code>doesStringContainAnyWordsInArray</code>, <code>doesStringContainAllWordsInString</code>, <code>doesStringContainAnyWordsInString</code>.
     *
     * @param stringToSearch the string to search
     * @param collectionOfWords collection of strings to search for
     * @param ensureAllWordsMatched <code>true</code> if all words in <code>arrayOfWords</code> should be matched
     * @return <code>true</code> if at least one word in <code>arrayOfWords</code> exists in <code>stringToSearch</code> and <code>ensureAllWordsMatched</code> is <code>false</code>, <code>true</code> if all words in <code>arrayOfWords</code> exists in <code>stringToSearch</code> and <code>ensureAllWordsMatched</code> is <code>true</code>, <code>false</code> otherwise
     */
    public static boolean doesStringContainWordsInCollection(String stringToSearch,
                                                             Object collectionOfWords,
                                                             boolean ensureAllWordsMatched)
    {
        /** require
        [valid_stringToSearch_param] stringToSearch != null;
        [valid_collectionOfWords_param] collectionOfWords != null;
        [is_collection_of_strings] Collection.collectionContainsInstancesOf(collectionOfWords, ClassAdditions.unsafeClassForName("java.lang.String")); **/

        boolean isContained;

        String upperStringToSearch = stringToSearch.toUpperCase();
        Enumeration wordEnumerator = Collection.enumerationForCollection(collectionOfWords);

        isContained = false;
        while (wordEnumerator.hasMoreElements())
        {
            String thisWord = (String)wordEnumerator.nextElement();

            if (thisWord.length() > 0)
            {
                int index = upperStringToSearch.indexOf(thisWord.toUpperCase());
                if (index != -1)
                {
                    isContained = true;
                    if ( ! ensureAllWordsMatched)
                    {
                        break;
                    }
                }
                else
                {
                    if (ensureAllWordsMatched)
                    {
                        isContained = false;
                        break;
                    }
                }
            }
        }

        return isContained;
    }



    /**
     * Returns <code>true</code> if <code>stringToSearch</code> contains any words in <code>stringOfWords</code>, otherwise <code>false</code>.
     *
     * @param stringToSearch the string to search
     * @param stringOfWords the words that we want to look for, separated by spaces
     * @return <code>true</code> if <code>stringToSearch</code> contains any words in <code>collectionOfWords</code>
     */
    public static boolean doesStringContainAnyWordsInString(String stringToSearch, String stringOfWords)
    {
        /** require
        [valid_stringToSearch_param] stringToSearch != null;
        [valid_stringOfWords_param] stringOfWords != null; **/

        NSArray arrayOfWords = NSArray.componentsSeparatedByString(stringOfWords, " ");
        return doesStringContainWordsInCollection(stringToSearch, arrayOfWords, false);
    }



    /**
     * Returns <code>true</code> if <code>stringToSearch</code> contains all the words in <code>stringOfWords</code>, otherwise <code>false</code>.
     *
     * @param stringToSearch the string to search
     * @param stringOfWords the words that we want to look for, separated by spaces
     * @return <code>true</code> if <code>stringToSearch</code> contains all the words in <code>collectionOfWords</code>
     */
    public static boolean doesStringContainAllWordsInString(String stringToSearch, String stringOfWords)
    {
        /** require
        [valid_stringToSearch_param] stringToSearch != null;
        [valid_stringOfWords_param] stringOfWords != null; **/

        NSArray arrayOfWords = NSArray.componentsSeparatedByString(stringOfWords, " ");
        return doesStringContainWordsInCollection(stringToSearch, arrayOfWords, true);
    }



    /**
     * Returns <code>true</code> if <code>stringToSearch</code> contains any words in <code>arrayOfWords</code>, <code>false</code> otherwise.
     *
     * @param stringToSearch the string to search
     * @param collectionOfWords the words that we want to look for
     * @return <code>true</code> if <code>stringToSearch</code> contains any words in the collection <code>arrayOfWords</code>
     */
    public static boolean doesStringContainAnyWordsInCollection(String stringToSearch, Object collectionOfWords)
    {
        /** require
        [valid_stringToSearch_param] stringToSearch != null;
        [valid_collectionOfWords_param] collectionOfWords != null;
        [is_collection_of_strings] Collection.collectionContainsInstancesOf(collectionOfWords, ClassAdditions.unsafeClassForName("java.lang.String")); **/

        return doesStringContainWordsInCollection(stringToSearch, collectionOfWords, false);
    }



    /**
     * Returns <code>true</code> if <code>stringToSearch</code> contains all the words in <code>arrayOfWords</code>, otherwise <code>false</code>.
     *
     * @param stringToSearch the string to search
     * @param collectionOfWords the words that we want to look for
     * @return <code>true</code> if <code>stringToSearch</code> contains all the words in the collection <code>arrayOfWords</code>
     */
    public static boolean doesStringContainAllWordsInCollection(String stringToSearch, Object collectionOfWords)
    {
        /** require
        [valid_stringToSearch_param] stringToSearch != null;
        [valid_collectionOfWords_param] collectionOfWords != null;
        [is_collection_of_strings] Collection.collectionContainsInstancesOf(collectionOfWords, ClassAdditions.unsafeClassForName("java.lang.String")); **/

        return doesStringContainWordsInCollection(stringToSearch, collectionOfWords, true);
    }



    /**
     * Escapes <code>characterToEscape</code> in the given string with <code>escapeCharacter</code>.
     *
     * @param sourceString the string to escape
     * @param stringToEscape the character that will be escaped
     * @param escapeString the string to escape the <code>stringToEscape</code> with
     * @return the string properly escaped
     */
    public static String escape(String sourceString, String stringToEscape, String escapeString)
    {
        /** require
        [valid_sourceString_param] sourceString != null;
        [valid_stringToEscape_param] stringToEscape != null;
        [valid_escapeString_param] escapeString != null; **/

        StringBuffer resultingString = new StringBuffer();

        for (int i = 0; i < sourceString.length(); i++)
        {
            if ((i + stringToEscape.length() <= sourceString.length()) && (sourceString.substring(i, i + stringToEscape.length()).equals(stringToEscape)))
            {
                resultingString.append(escapeString);
            }
            resultingString.append(sourceString.charAt(i));
        }

        return resultingString.toString();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Escapes <code>characterToEscape</code> in the given string with <code>escapeCharacter</code>.
     *
     * @param sourceString the string to escape
     * @param characterToEscape the character that will be escaped
     * @param escapeCharacter the character to escape the <code>characterToEscape</code> with
     * @return the string properly escaped
     */
    public static String escape(String sourceString, char characterToEscape, char escapeCharacter)
    {
        /** require [valid_sourceString_param] sourceString != null; **/
        return escape(sourceString, new String(new char[] {characterToEscape}), new String(new char[] {escapeCharacter}));
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Converts <code>sourceString</code> to a valid SQL for FrontBase. Currently, EOF has a known bug in Java: it does not properly quote string literals. When this method is called, it will add "'" to both sides of the string and add the FrontBase's escape character (which is "'") for all occurences of "'".
     * eg. "Smith" => "'Smith'"
     *     "O'Brien" => "'O''Brien'"
     *
     * @param sourceString the string to convert
     * @return the string properly quoted
     */
    public static String escapeSQLForFrontBase(String sourceString)
    {
        /** require [valid_param] sourceString != null; **/

        String resultingString = escape(sourceString, '\'', '\'');
        resultingString = "'" + resultingString + "'";

        JassAdditions.post("StringAdditions", "escapeSQLForFrontBase", resultingString.charAt(0) == '\'' && resultingString.charAt(resultingString.length() - 1) == '\'');
        return resultingString;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Pads a string to the specified number of chars with the given pad char.
     *
     * @param string the string to pad
     * @param padChar the character to pad with
     * @param paddedLength the length to pad to
     * @return the padded string
     */
    public static String pad(String string, char padChar, int paddedLength)
    {
        /** require [valid_string_param] string != null; **/
        JassAdditions.pre("StringAdditions", "pad", string.length() <= paddedLength);

        StringBuffer buffer = new StringBuffer(string);
        for (int i = string.length(); i < paddedLength; i++)
        {
            buffer.append(padChar);
        }
        String resultString = buffer.toString();

        JassAdditions.post("StringAdditions", "pad", resultString.length() == paddedLength);
        return resultString;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Downcases the first letter of the given string.
     *
     * @param string the string to downcase
     * @return the string with the first letter downcased
     */
    public static String downcaseFirstLetter(String string)
    {
        /** require [valid_param] string != null; **/

        String result = string.substring(0, 1).toLowerCase() + string.substring(1);

        JassAdditions.post("StringAdditions", "downcaseFirstLetter", result.substring(0, 1).equals(string.substring(0, 1).toLowerCase()) && result.substring(1).equals(string.substring(1)));
        return result;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the last element of <code>wholePath</code>.  This does not use
     * the pathSeparator system property.  This is for files uploaded through a
     * browser, so it accomodates forward slash AND backslash pathSeparators.
     *
     * @param wholePath path to return the last path component (filename) from
     * @return the last path component (filename) of <code>wholePath</code>
     */
    public static String lastPathComponentXPlatform(String wholePath)
    {
        /** require [valid_path] wholePath != null;  **/

        while (wholePath.endsWith("\\") || wholePath.endsWith("/"))
        {
            wholePath = wholePath.substring(0, wholePath.length() - 1);
        }

        int lastSlash = wholePath.lastIndexOf("\\");
        String lastPathComponent = wholePath;

        // If we did not find the backslash, try  the forwardSlash
        if ( lastSlash < 0)
        {
            lastSlash = wholePath.lastIndexOf("/");
        }

        if ( lastSlash > -1)
        {
            lastPathComponent = wholePath.substring(lastSlash + 1);
        }


        return lastPathComponent;
        /** ensure [valid_result] Result != null;
                   [path_ends_in_name] wholePath.endsWith(Result);     **/
    }




    /**
     * Return a random string of length length.  Useful to create passwords.
     *
     * @param length the length of random string to create
     * @param charset characters that can appear in the random string
     * @return a random string of length length
     */
    public static String randomStringWithLengthFromCharset(int length, String charset)
    {
        /** require
        [valid_length] length >= 0;
        [valid_charset] charset != null; **/

        char[] randomString = new char[length];

        for(int i = 0; i < length; i++) {
            int num = generator.nextInt(charset.length());
            randomString[i] = charset.charAt(num);
        }

        return new String(randomString);

        /** ensure [valid_result] Result != null; **/
    }


}
