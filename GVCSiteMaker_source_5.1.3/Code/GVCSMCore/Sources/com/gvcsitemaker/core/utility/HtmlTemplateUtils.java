// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOElement;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSValidation;


/**
 * Collection of methods to generate more understandable exceptions from WOComponent.templateWithHTMLString(template, bindings, languages).
 */
public class HtmlTemplateUtils extends Object
{



    /**
     * Passes the parameters on to WOComponent.templateWithHTMLString(template, bindings, languages) and translate any NSForwardException thrown into a more descriptive IllegalArgumentException in which the message is user presentable.
     *
     * @param htmlTemplate - HTML string to create the template of the component
     * @param bindings - declaration on the template of the component
     * @param languages - list of languages in specific order
     * @return the template of the component
     */
    public static WOElement elementsFromTemplateWithBindings(String htmlTemplate,
                                                             String bindings,
                                                             NSArray languages) throws IllegalArgumentException
    {
        /** require
        [valid_template] htmlTemplate != null;
        [valid_bindings] bindings != null;
        [languages] languages != null;
        **/
        
        WOElement elements = null;
        try
        {
            elements = WOComponent.templateWithHTMLString(htmlTemplate, bindings, languages);
        }
        catch (NSForwardException e)
        {
            Throwable t = e.originalException();
            DebugOut.println(1, e.originalException().getClass().getName());
            DebugOut.println(1, e.originalException().getMessage());
            String message = t.getMessage();
            String translatedMessage = message;
            if (t instanceof com.webobjects.appserver._private.WOHTMLFormatException)
            {
                if (message.indexOf("There is an unbalanced WebObjects tag") != -1)
                {
                    // There is an unbalanced WebObjects tag named 'DataAccess_AddForm'.
                    translatedMessage = "Missing </WebObject> tag to close tag named \"" + SMStringUtils.lastWord(message) + "\"";
                }
                else if (message.indexOf("Unbalanced WebObject tags.") != -1)
                {
                    // Unbalanced WebObject tags. Either there is an extra closing </WEBOBJECT> tag in the html template, or one of the opening <WEBOBJECT ...> tag has a typo (extra spaces between a < sign and a WEBOBJECT tag ?).
                    translatedMessage = "Either there is an extra closing </WEBOBJECT> tag in the html template, or one of the opening <WEBOBJECT ...> tag has a typo (extra spaces between a < sign and a WEBOBJECT tag ?).";
                }
                else if ((message.indexOf("cannot initialize WebObject tag") != -1) && (message.indexOf("Failed parsing NAME parameter") != -1))
                { 
                    // Missing quote.
                    String declaration = message.substring(message.indexOf("cannot initialize WebObject tag") + 32, message.indexOf("Failed parsing NAME parameter") - 3);
                    translatedMessage = "There is a problem with the tag " + declaration + ".  Did you forget a quotation mark?";
                }
                

            }
            else if (t instanceof com.webobjects.appserver.parser.declaration.WODeclarationFormatException)
            {
                // <WOHTMLTemplateParser> no declaration for dynamic element (or component) named DataAcces_EditableFields
                // This can also happen if the WOD file is malformed and can't be parsed.
            }

            throw new IllegalArgumentException("Template has errors: " + translatedMessage);
        }
        return elements;
    }

    

    /**
     * Passes the parameters on to WOComponent.templateWithHTMLString(template, bindings, languages) and to validate the template with the bindings.  If any exception is raised it is translated into a more descriptive NSValidation.ValidationException in which the message is user presentable.
     *
     * @param htmlTemplate - HTML string to create the template of the component
     * @param bindings - declaration on the template of the component
     * @param languages - list of languages in specific order
     * @return the template of the component
     * @exception NSValidation.ValidationException - if the template is not valid for the bindings
     */
    public static void validateTemplateWithBindings(String htmlTemplate, String bindings, NSArray languages) throws NSValidation.ValidationException
    {
        /** require
        [valid_template] htmlTemplate != null;
        [valid_bindings] bindings != null;
        [languages] languages != null;
        **/

        try
        {
            elementsFromTemplateWithBindings(htmlTemplate, bindings, languages);
        }
        catch (IllegalArgumentException e)
        {
            throw new NSValidation.ValidationException(e.getMessage());
        }
    }

}
