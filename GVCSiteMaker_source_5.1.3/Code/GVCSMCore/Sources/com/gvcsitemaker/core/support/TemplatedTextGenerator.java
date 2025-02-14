// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.support;

import net.global_village.woextensions.WOComponent;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.*;



/**
 * This is a simple, yet powerful, WO driven merge engine that combines data
 * from a single object with a template to produce a result string. It is
 * intended to produce messages from a boilerplate template containing WO tags.
 */
public class TemplatedTextGenerator extends WOComponent
{
    protected String templateString;
    protected Object object;


    /**
     * This is the method to use to generate the body for a website creation message.
     * 
     * @param componentName the name of the sub-class implementing the specific template funcationality
     * to be used
     * @param anObject the Object to customize the message with
     * @param aTemplate the template defining the message to be produced (null produces empty message)
     * 
     * @return the message produced from anObject and aTemplate
     */
    protected static String messageFor(String componentName, Object anObject, String aTemplate)
    {
        /** require [has_componentName] componentName != null;
                    [has_anObject] anObject != null;                     **/

        TemplatedTextGenerator messageEngine = newInstance(componentName, anObject, aTemplate);
        return messageEngine.generateMessage();

        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * This is the method to use to validate a template.
     * 
     * @param componentName the name of the sub-class implementing the specific template funcationality
     * to be validated
     * @param anObject optional Object to use when validating
     * @param aTemplate the template to be validated (null produces empty message)
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    protected static void validateTemplateFor(String componentName, Object anObject, String aTemplate) throws NSValidation.ValidationException
    {
        /** require [has_componentName] componentName != null;       **/

        TemplatedTextGenerator messageEngine = newInstance(componentName, anObject, aTemplate);
        messageEngine.validateTemplate();
    }



    /**
     * Call this if you just want to create a new instance of the template engine.  Normally this is
     * called internally by other methods.
     * 
     * @param componentName the name of the sub-class implementing the specific template funcationality
     * to be instantiated
     * @param anObject optional Object to provide to new instance
     * @param aTemplate the template to be validated (null produces empty message)
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    protected static TemplatedTextGenerator newInstance(String componentName, Object anObject, String aTemplate)
    {
        /** require [has_componentName] componentName != null;       **/

        WORequest fakeRequest = new WORequest("GET", "/", "HTTP/1.1", NSDictionary.EmptyDictionary, NSData.EmptyData, NSDictionary.EmptyDictionary);
        WOContext dummy = new WOContext(fakeRequest);
        TemplatedTextGenerator messageEngine = (TemplatedTextGenerator) WOApplication.application().pageWithName(componentName, dummy);
        messageEngine.setObject(anObject);
        messageEngine.setTemplateString(aTemplate);

        return messageEngine;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Designated constructor
     */
    protected TemplatedTextGenerator(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Checks to see if the template in <code>templateString()</code> is valid when combined with
     * <code>bindingsString().</code> 
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    public void validateTemplate() throws NSValidation.ValidationException
    {
        HtmlTemplateUtils.validateTemplateWithBindings(safeTemplateString(), bindingsString(), NSArray.EmptyArray);
    }



    /**
     * This methods merges the data in object() with templateString() and returns the result.
     *  
     * @return String produced by merging the data in object() with templateString() 
     */
    protected String generateMessage()
    {
        /** require [has_object] object() != null;                     **/

        return generateResponse().contentString();

        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * This method integrates the template (which originates in an OrgUnit) with the bindings from
     * WebsiteCreationMessage.wod.
     *  
     * @return the WOElement root object to produce this new website message
     */
    public WOElement template()
    {
        return HtmlTemplateUtils.elementsFromTemplateWithBindings(safeTemplateString(), bindingsString(), NSArray.EmptyArray);
    }



    /**
     * Returns the String containing the bindings between this object and the tags in 
     * templateString().  By default this is the property named 
     * <code>[name of this component].wod</code>.
     *
     * @return a String containing the bindings to use when generating the message
     */
    protected String bindingsString()
    {
        String componentName = NSPathUtilities.pathExtension(name());
        return SMApplication.appProperties().stringPropertyForKey(componentName + ".wod");
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns <code>templateString()</code> unless it is null in which case the empty string is
     * returned.
     * 
     * @return <code>templateString()</code> or ""
     */
    protected String safeTemplateString()
    {
        return (templateString() != null ? templateString() : "");
        /** ensure [valid_result] Result != null;  **/
    }


    /* ********** Generic setters and getters ************** */

    public String templateString()
    {
        return templateString;
    }


    public void setTemplateString(String string)
    {
        templateString = string;
    }


    public Object object()
    {
        return object;
    }


    public void setObject(Object newObject)
    {
        object = newObject;
    }

}
