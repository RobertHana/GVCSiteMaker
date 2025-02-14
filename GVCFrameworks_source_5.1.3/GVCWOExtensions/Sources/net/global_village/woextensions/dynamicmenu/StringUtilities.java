package net.global_village.woextensions.dynamicmenu; 



/**
 * String manipulation methods used to generate the Javascript.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */  
public class StringUtilities extends Object
{
    public static final String doubleQuote = "\"";


    /**
     * Adds double quotes around the original string so that the returned value is "original"
     *
     * @param original unquoted String
     * @return quoted String
     */
    public static String quotedString(String original)
    {
        /** require [valid_param] original != null; **/

        return doubleQuote + original + doubleQuote;

        /** ensure [valid_result] Result != null; **/
    }



}
