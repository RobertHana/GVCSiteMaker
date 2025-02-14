package net.global_village.foundation;

import java.util.*;

import com.webobjects.foundation.*;


/**
 * Abstract class representing a tokeniser that accepts a string and breaks it into tokens.  Each
 * token can be associated with an optional name. You can use the enumeration interface to return 
 * the strings one by one or get them all at once using the <code>allRemainingTokens()</code> method.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 1$
 */
public abstract class AbstractStringTokenizer implements Enumeration
{

    protected String stringToTokenize;
    protected int mark = 0;
    protected NSArray tokenNames = null;
    protected Enumeration nameEnumerator = null;
    protected String nameOfCurrentToken;
    

    /**
     * Designated constructor.
     *
     * @param str the string to tokenize
     * @param names the names of the tokens
     */
    public AbstractStringTokenizer(String str, NSArray names)
    {
        super();
        /** require  [valid_str_param] str != null; **/

        stringToTokenize = str;
        tokenNames = names;

        if (tokenNames != null)
        {
            nameEnumerator = names.objectEnumerator();
        }
    }



    /**
     * Equivilant to <code>AbstractStringTokenizer(str, null)</code>.
     *
     * @param str the string to tokenize
     */
    public AbstractStringTokenizer(String str)
    {
        this(str, null);
        /** require  [valid_str_param] str != null; **/
    }



    /**
     * Returns true if there are more tokens.
     *
     * @return true if there are more tokens
     */
    public boolean hasMoreElements()
    {
        return mark < stringToTokenize.length();
    }



    /**
     * Returns the next token as an Object.
     *
     * @return the next token
     */
    abstract public Object nextElement();




    /**
     * Returns true if there are more tokens.
     *
     * @return true if there are more tokens
     */
    public boolean hasMoreTokens()
    {
        return hasMoreElements();
    }



    /**
     * Returns the next token as a String.
     *
     * @return the next token
     */
    public String nextToken()
    {
        /** require [has_more_tokens] hasMoreTokens(); **/
        return (String)nextElement();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the array of token names.
     *
     * @return the array of token names
     */
    public NSArray tokenNames()
    {
        return tokenNames;
    }



    /**
     * Returns the current token's name.
     *
     * @return the current token's name
     */
    public String currentTokenName()
    {
        /** require [has_token_names] tokenNames() != null; **/
        return nameOfCurrentToken;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns all remaining tokens in an NSArray.  Calling this before using <code>nextElement()</code> or <code>nextToken()</code> will return all tokens.
     *
     * @return all remaining tokens
     */
    public NSArray allRemainingTokens()
    {
        NSMutableArray allTokens = new NSMutableArray();

        while (hasMoreTokens())
        {
            allTokens.addObject(nextToken());
        }

        return allTokens;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns all remaining tokens in an NSDictioanry, with the token names as keys and their values as values.
     *
     * @return all tokens
     */
    public NSDictionary allRemainingTokensWithNames()
    {
        NSMutableDictionary allTokensWithNames = new NSMutableDictionary();

        while (hasMoreTokens())
        {
            String token = nextToken();
            String name = currentTokenName();
            allTokensWithNames.setObjectForKey(token, name);
        }

        return allTokensWithNames;

        /** ensure [valid_result] Result != null; **/
    }

    
    
    /**
     * Returns the rest of the string, starting at the next position to be parsed.  The returned string
     * will include any unparsed delimiters.  If the string has been fully parsed ( ! hasMoreElements()),
     * the empty string is returned.
     * 
     * @return the rest of the string, or an empty string if there is no more.
     */
    public String restOfString()
    {
         return hasMoreElements() ? stringToTokenize.substring(mark) : "";

         /** ensure [valid_result] Result != null; **/
    }


    /** invariant  [has_string_to_tokenize] stringToTokenize != null;  **/
    
}
