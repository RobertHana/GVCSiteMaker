// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.pagecomponent;

import net.global_village.woextensions.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;


/**
 * Subclass of MixedMediaPane, contains a child of Text which already handles edit and display of text/html content type
 *
 */
public class MixedMediaTextContentPane extends _MixedMediaTextContentPane
{
    // Type Name
    public static final String TypeName = "Text or HTML";
    
    // Binding keys
    public static final String TEXT_BINDINGKEY = "text";
    public static final String CONVERT_CR_BINDINGKEY = "convertCR";    


    /**
     * Factory method to create new instances of MixedMediaTextContentPane.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of MixedMediaTextContentPane or a subclass.
     */
    public static MixedMediaTextContentPane newMixedMediaTextContentPane()
    {
        return (MixedMediaTextContentPane) SMEOUtils.newInstanceOf("MixedMediaTextContentPane");

        /** ensure [result_not_null] Result != null; **/
    }
     
  
    
    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("MixedMediaTextContentPane");
        // Default bindings 
        setShouldConvertCR(SMApplication.appProperties().
                     booleanPropertyForKey("ConvertCarriageReturnsToLineBreaks"));
                     
        /** ensure [defaults_set] hasValueForBindingKey(CONVERT_CR_BINDINGKEY);  **/
    }    
    
    
    
    /**
     * Returns the text for display, it is formatted if required
     *
     * @return the text for display, it is formatted if required
     */    
    public String displayText()
    {
        return shouldConvertCR() ? HTMLFormatting.replaceFormattingWithHTML(text()) : text();
    }      
	

    
    /**
     * Returns the html source to be used for displaying this type of pane.
     *
     * @return the html source for this type of pane.
     */    
    public String template() 
    {
        StringBuffer htmlFile = new StringBuffer(2048);

        htmlFile.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n");
        htmlFile.append("<html>\n");
        htmlFile.append("<head></head><body>\n");
        htmlFile.append("<webobject name=Contents></webobject>\n");
        htmlFile.append("</body></html>\n");
        
        return htmlFile.toString();      
        
        /** ensure [result_not_null] Result != null; **/        
    }
    
    
    
    /**
     * Returns the wod bindings for displaying this type of pane
     *
     * @return the wod bindings for displaying this type of pane
     */    
    public String generatedBindings() 
    {
        StringBuffer wodFile = new StringBuffer(2048);

        wodFile.append("Contents" + ": WOString {\n");
        wodFile.append("    value = displayPane.displayText;\n");
        wodFile.append("    escapeHTML = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        return wodFile.toString();

        /** ensure [valid_result] Result != null;  **/
    }  	

    
    
    //************** Binding Cover Methods **************\\

    public String text() {
        return (String)valueForBindingKey(TEXT_BINDINGKEY);
    }
    public void setText(String aText) {
        setBindingForKey(aText, TEXT_BINDINGKEY);
    }

     public boolean shouldConvertCR() {
        return booleanValueForBindingKey(CONVERT_CR_BINDINGKEY);
    }
    public void setShouldConvertCR(boolean shouldConvert) {
        setBooleanValueForBindingKey(shouldConvert, CONVERT_CR_BINDINGKEY);
    }
        
}

