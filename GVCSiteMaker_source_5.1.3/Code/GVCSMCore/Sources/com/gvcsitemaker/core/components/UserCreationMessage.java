// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

/**
 * This component exists solely to create the Welcome Message sent to new external (GVC.SiteMaker
 * managed) users.  It "merges" the boilerplate template from the application properties with the 
 * data from the logged in (creating) user and the newly created user.
 */
public class UserCreationMessage extends TemplatedTextGenerator
{


    /**
     * Returns the template to use to generate the body of a external user creation message.
     * 
     * @return the template to use to generate the body of a external user creation message
     */
    public static String messageTemplate()
    {
        return SMApplication.appProperties().stringPropertyForKey(SMApplication.UserCreationMessageKey);
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * This is the method to use to generate the body of a external user creation message.
     * 
     * @param createdUser the newly created user that this message is being sent for
     * 
     * @return the message produced from aWebsite and aTemplate
     */
    public static String messageFor(User createdUser)
    {
        /** require [has_createdUser] createdUser != null;
                    [createdUser_isExternal] ! createdUser.isInternalUser();          **/

        return messageFor("UserCreationMessage", createdUser, messageTemplate());
         
        /** ensure [valid_result] Result != null;   **/
    }
    
    

    /**
     * This is the method to use to validate a template for the external user creation message.
     * 
     * @param dummy parameter to keep Java happy
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    public static void validateTemplate(String aTemplate) throws NSValidation.ValidationException
    {
        validateTemplateFor("UserCreationMessage", null, messageTemplate());
    }
    
    
    
    /**
     * Designated constructor
     */
    public UserCreationMessage(WOContext aContext)
    {
        super(aContext);
    }
    
    
    /* ********** Generic setters and getters ************** */

    public User createdUser() {
        return (User)object();
    }
    
    public String productName() {
        return SMApplication.productName();
    }

    public String changePasswordURL() {
        return SMActionURLFactory.changePasswordURL(context().request(), createdUser().userID());
    }

    public String mainPageURL() {
        return SMActionURLFactory.mainPageURL();
    }

}
