// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;



public class Text extends _Text
{


    // Binding keys
    public static final String TEXT_BINDINGKEY = "text";
    public static final String FONT_BINDINGKEY = "font";
    public static final String SIZE_BINDINGKEY = "size";
    public static final String CONVERT_CR_BINDINGKEY = "convertCR";



    /**
     * Factory method to create new instances of Text.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Text or a subclass.
     */
    public static Text newText()
    {
        return (Text) SMEOUtils.newInstanceOf("Text");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("Text");
        // Default bindings for use in Text/Image section
        setSize("3");
        setFont("Arial");
        setText("Please enter text here.");
        setShouldConvertCR(SMApplication.appProperties().
                     booleanPropertyForKey("ConvertCarriageReturnsToLineBreaks"));
                     
        /** ensure [defaults_set] size().equals("3") && 
                                  font().equals("Arial") &&
                                  hasValueForBindingKey(CONVERT_CR_BINDINGKEY);  **/
    }


  
    /**
     * Returns a CSS style string for the font and size attributes of this Text component.  The font value is used as is, but default font names are appended from FallbackFontFaceList defined in GVCSiteMaker.plist.  The font size is converted into pixels using the formula size() x 2 + 8.  The resulting sytle string will look like this: <code>font-family: Arial, Verdana, Helvetica, sans-serif; font-size: 16px;</code>.  This is to be used in a DIV or SPAN tag like this: <code>&lt;div style="font-family: Arial, Verdana, Helvetica, sans-serif; font-size: 16px;"&gt;</code>
     *
     * @return a CSS style string for the font and size attributes of this Text component.
     */
    public String cssStyleForText()
    {
        /** require
        [font_not_null] font() != null;
        [size_not_null] size() != null; **/
        
        StringBuffer cssStyleForText = new StringBuffer(75);

        Integer fontSize = new Integer(Integer.parseInt(size()) * 2 + 8);
        cssStyleForText.append("font-family: ");
        cssStyleForText.append(font());
        cssStyleForText.append(", ");
        cssStyleForText.append(SMApplication.appProperties().stringPropertyForKey("FallbackFontFaceList"));
        cssStyleForText.append("; font-size: ");
        cssStyleForText.append(fontSize.toString());
        cssStyleForText.append("px;");

        return cssStyleForText.toString();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (font() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("Font is a required binding for Text"));
        }

        if (size() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("Size is a required binding for Text"));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    //************** Binding Cover Methods **************\\

    // Notes:
    // The contents of the bindings at this point in time are
    // {size = "3"; align = "none"; canEdit = "YES"; text = "Please enter text here."; font = "Arial"; }
    // The align binding is legacy cruft and not used or referenced anywhere.
    // Sometimes there is a binding of componentOrder = {}.  This is due to a bug in PageComponent which has been fixed.
    public String text() {
        return (String)valueForBindingKey(TEXT_BINDINGKEY);
    }
    public void setText(String aText) {
        setBindingForKey(aText, TEXT_BINDINGKEY);
    }

    public String font() {
        return (String)valueForBindingKey(FONT_BINDINGKEY);
    }
    public void setFont(String aFont) {
        setBindingForKey(aFont, FONT_BINDINGKEY);
    }

    public String size() {
        return (String)valueForBindingKey(SIZE_BINDINGKEY);
    }
    public void setSize(String aSize) {
        setBindingForKey(aSize, SIZE_BINDINGKEY);
    }

    public boolean shouldConvertCR() {
        return booleanValueForBindingKey(CONVERT_CR_BINDINGKEY);
    }
    public void setShouldConvertCR(boolean shouldConvert) {
        setBooleanValueForBindingKey(shouldConvert, CONVERT_CR_BINDINGKEY);
    }
    
 
}
