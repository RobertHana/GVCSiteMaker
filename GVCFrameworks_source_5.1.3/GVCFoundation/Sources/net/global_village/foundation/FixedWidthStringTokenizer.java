package net.global_village.foundation;

import java.util.Enumeration;

import com.webobjects.foundation.NSArray;


/**
 * A String tokenizer that accepts a string of an array of widths and returns the string separated into strings of width lengths.  
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class FixedWidthStringTokenizer extends AbstractStringTokenizer
{
    protected NSArray tokenWidths;
    protected Enumeration widthEnumerator;


    /**
     * Designated constructor.
     *
     * @param str the string to tokenize
     * @param widths the widths of the tokens
     * @param names the names of the tokens
     */
    public FixedWidthStringTokenizer(String str, NSArray widths, NSArray names)
    {
        super(str, names);
        /** require
        [valid_str_param] str != null;
        [valid_widths_param] widths != null;
        [widths_equals_string_length] ((Number)widths.valueForKeyPath("@sum")).intValue() == str.length();
        [has_name_for_each_width] names != null ? widths.count() == names.count() : true; **/
        JassAdditions.pre("FixedWidthStringTokenizer", "<init>", Collection.collectionContainsInstancesOf(widths, Integer.class));

        tokenWidths = widths;
        widthEnumerator = widths.objectEnumerator();
    }



    /**
     * Equivilant to <code>FixedWidthStringTokenizer(str, widths, null)</code>.
     *
     * @param str the string to tokenize
     * @param widths the widths of the tokens
     */
    public FixedWidthStringTokenizer(String str, NSArray widths)
    {
        this(str, widths, null);
        /** require
        [valid_str_param] str != null;
        [valid_widths_param] widths != null;
        [widths_equals_string_length] ((Number)widths.valueForKeyPath("@sum")).intValue() == str.length(); **/
        JassAdditions.pre("FixedWidthStringTokenizer", "<init>", Collection.collectionContainsInstancesOf(widths, Integer.class));
    }


    
    /**
     * Returns the next token as an Object.
     *
     * @return the next token
     */
    public Object nextElement()
    {
        /** require [has_more_elements] hasMoreElements(); **/
        
        int tokenLength = ((Integer)widthEnumerator.nextElement()).intValue();

        if (nameEnumerator != null)
        {
            nameOfCurrentToken = (String)nameEnumerator.nextElement();
        }

        Object token = stringToTokenize.substring(mark, mark + tokenLength);
        mark += tokenLength;
        return token;

        /** ensure [valid_result] Result != null; **/
    }


    /** invariant
    [has_string_to_tokenize] stringToTokenize != null;
    [has_token_widths] tokenWidths != null;
    [has_width_enumerator] widthEnumerator != null;
    [mark_is_consistant] mark <= stringToTokenize.length(); **/

}
