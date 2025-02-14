package net.global_village.foundation;

import com.webobjects.foundation.*;


/**
 * A String tokenizer that uses a delimiter and quote character to break the string into tokens.
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 * @version $Revision: 15$
 */
public class DelimitedStringTokenizer extends AbstractStringTokenizer
{
    protected String delimiter;
    protected String quoteCharacters;


    /**
     * Designated constructor.
     *
     * @param str the string to tokenize
     * @param delim the string to use as the delimiter.  Tokens are separated by this string
     * @param quoteChars all characters in this string will be considered quotes when found at the beginning and ending of a token.  Set to "" for no quote characters
     * @param names the names of the tokens. If non-null, there should be the same number of tokens in the string as there are names
     */
    public DelimitedStringTokenizer(String str, String delim, String quoteChars, NSArray names)
    {
        super(str, names);
        /** require
        [valid_str_param] str != null;
        [valid_delim_param] delim != null;
        [valid_quoteChars_param] quoteChars != null; **/

        delimiter = delim;
        quoteCharacters = quoteChars;
    }


    /**
     * Constructor with no names.
     *
     * @param str the string to tokenize
     * @param delim the string to use as the delimiter.  Tokens are separated by this string
     * @param quoteChars all characters in this string will be considered quotes when found at the beginning and ending of a token.  Set to "" for no quote characters
     */
    public DelimitedStringTokenizer(String str, String delim, String quoteChars)
    {
        super(str);
        /** require
        [valid_str_param] str != null;
        [valid_delim_param] delim != null;
        [valid_quoteChars_param] quoteChars != null; **/

        delimiter = delim;
        quoteCharacters = quoteChars;
    }



    /**
     * Returns true if there are more tokens.
     *
     * @return true if there are more tokens
     */
    public boolean hasMoreElements()
    {
        // The second part of this handles an empty token at the end of a line
        return (stringToTokenize.length() > 0) &&
               ((mark < stringToTokenize.length()) ||
                ((mark == stringToTokenize.length()) &&
                 (stringToTokenize.substring(mark - delimiter.length(), mark).equals(delimiter))));
    }



    /**
     * Returns the next token as an Object.  Returns an empty string if two delimiters are adjacent.
     *
     * @return the next token
     * @throws IllegalArgumentException if a quoted string has no closing quote
     */
    public Object nextElement()
    {
        /** require [has_more_elements] hasMoreElements(); **/

        StringBuffer token = new StringBuffer();

        // Handle an empty token at the end of the line
        if ((mark == stringToTokenize.length()) && (stringToTokenize.substring(mark - delimiter.length(), mark).equals(delimiter)))
        {
            mark++;  // Moves the mark beyond the end of the line so that hasMoreElements returns false
        }
        else
        {
            // Check for a starting quote
            String quotedWith = "";
            if (quoteCharacters.indexOf(nextChar()) != -1)
            {
                quotedWith = new String(new char[] {nextChar()});
                mark++;
            }

            for (; mark < stringToTokenize.length(); mark++)
            {
                // Are we inside a quote?
                if (quotedWith.indexOf(nextChar()) != -1)
                {
                    // This handles quoted quotes inside quoted data, as in 'O''brien'
                    if ((mark + 1 < stringToTokenize.length()) && (nextChar() == stringToTokenize.charAt(mark + 1)))
                    {
                        token.append(nextChar());
                        mark++;
                        continue;
                    }

                    // Check for a quote followed by a delimiter or the end of the line (as long as it's not two quote chars in a row)
                    if ( (mark + 1 == stringToTokenize.length()) ||
                         (stringToTokenize.substring(mark + 1, mark + 1 + delimiter.length()).equals(delimiter)))
                    {
                        quotedWith = "";
                        continue;
                    }
                }

                // If the delimiter appears outside of quotes, then we have the end of a token
                if ((quotedWith.equals("")) &&
                    (mark + delimiter.length() <= stringToTokenize.length()) &&
                    (stringToTokenize.substring(mark, mark + delimiter.length()).equals(delimiter)))
                {
                    mark += delimiter.length();
                    break;
                }
                token.append(nextChar());
            }

            if ( ! quotedWith.equals(""))
            {
                throw new IllegalArgumentException("Closing quote not found at " + mark);
            }
        }

        if (tokenNames != null)
        {
            nameOfCurrentToken = (String)nameEnumerator.nextElement();
        }

        return token.toString().trim();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return the next char to examine in stringToTokenize
     */
    protected char nextChar()
    {
    		/** require [mark_in_range] mark < stringToTokenize.length(); **/
    		return stringToTokenize.charAt(mark);
    		/** ensure [valid_result] true; **/
    }



    /** invariant
    [has_string_to_tokenize] stringToTokenize != null; **/



}
