package net.global_village.woextensions;

import net.global_village.foundation.*;

/**
 * Describe class here.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class HTMLFormatting
{
    
    public static final String HTML_LINE_BREAK = "<br/>";
    


    /**
     * <p>Replaces non-HTML formatting in <code>originalString</code> with HTML 
     * formatting.  This is useful when displaying text that has been uploaded 
     * or cut and pasted without the benefit of added HTML formatting.</p>
     * <p><b>Note</b>: at present the only change is that line breaks are replaced
     * with the <code>HTML_LINE_BREAK</code> sequence.
     *    
     * @param originalString the string to convert to HTML formatting
     * @return <code>originalString</code> with non-HTML formatting replaced with 
     * HTML formatting
     */
    public static String replaceFormattingWithHTML(String originalString)
    {
        // Special handling for null strings
        if (originalString == null)
        {
            return "";
        }
        
        StringBuffer workingString = new StringBuffer(originalString.length() + 100);
        
        int i = 0;
        while (i < originalString.length())
        {
            char thisChar = originalString.charAt(i);
            if (StringAdditions.isLineBreak(thisChar))
            {
                workingString.append(HTML_LINE_BREAK);

                if ((i < (originalString.length()-1)) && 
                     StringAdditions.isLineBreakContinuation(thisChar, 
                                                             originalString.charAt(i + 1))) 
                {
                    i++;
                } 
            }
            else
            {
                workingString.append(thisChar);
            }
            i++;
        }

        return workingString.toString();
        /** ensure [valid_result] Result != null;  
                   [no_new_lines] Result.indexOf('\n') == -1;
                   [no_carriage_returns] Result.indexOf('\r') == -1;    **/
    }


}
