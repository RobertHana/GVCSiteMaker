// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * This component exists solely to create the Website Creation Message sent to new site owners.  It
 * "merges" the boilerplate template from an Org Unit with the data from a website to generate the 
 *  body of the message.
 */
public class WebsiteCreationMessage extends TemplatedTextGenerator
{

    /**
     * This is the method to use to generate the body for a website creation message.
     * 
     * @param aWebsite the Website that was created
     * @param aTemplate the template defining the message to be produced
     * 
     * @return the message produced from aWebsite and aTemplate
     */
    public static String messageFor(Website aWebsite, String aTemplate)
    {
        /** require [has_website] aWebsite != null;                     **/

        return messageFor("WebsiteCreationMessage", aWebsite, aTemplate);
         
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * This is the method to use to validate a template for the Website Creation message.
     * 
     * @param aTemplate the template to be validated
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    public static void validateTemplate(String aTemplate) throws NSValidation.ValidationException
    {
        validateTemplateFor("WebsiteCreationMessage", null, aTemplate);
    }



    /**
     * Designated constructor
     */
    public WebsiteCreationMessage(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Convenience method to format the unit admins e-mail addresses separated by commas.
     * 
     * @return a string containing the e-mail addresses of the unit admins for the unit that 
     * website() is in.
     */
    public String emailAddressesForNearestAdmins()
    {
        /** require [has_website] website() != null;                     **/
        NSArray emailAddresses = (NSArray) website().parentOrgUnit().nearestAdmins().valueForKey("emailAddress");
        return emailAddresses.componentsJoinedByString(", ");
        /** ensure [valid_result] Result != null;   **/
    }


    /* ********** Generic setters and getters ************** */

    public Website website() {
        return (Website)object();
    }

    
    
    public String websiteConfigURL() {
        return website().configURL(context().request());
    }

}
