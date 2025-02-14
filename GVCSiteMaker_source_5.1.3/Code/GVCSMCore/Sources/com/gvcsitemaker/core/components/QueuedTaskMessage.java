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
 * Wrapper for template engine to generate the body of Queued Tasks notification messages.
 */
public class QueuedTaskMessage extends TemplatedTextGenerator
{
    public NSTimestampFormatter dateFormatter;
    protected Task task;
    

    /**
     * This is the method to use to generate the body for a Queued Task message.
     * 
     * @param aTask the Task related to this message
     * @param aTemplate the template defining the message to be produced
     * 
     * @return the message produced from aTask and aTemplate
     */
    public static String messageFor(Task aTask, String aTemplate)
    {
        /** require [has_aTask] aTask != null;                     **/

        return messageFor("QueuedTaskMessage", aTask, aTemplate);
         
        /** ensure [valid_result] Result != null;   **/
    }
    
    

    /**
     * This is the method to use to validate a template for the queued task message.
     * 
     * @param dummy parameter to keep Java happy
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    public static void validateTemplate(String aTemplate) throws NSValidation.ValidationException
    {
        validateTemplateFor("QueuedTaskMessage", null, aTemplate);
    }
    
    
    
    /**
     * Designated constructor
     */
    public QueuedTaskMessage(WOContext context) 
    {
        super(context);
        dateFormatter = SMApplication.appProperties().timestampFormatterForKey("DateAndTimeFormatter");
    }

    
    /* ********** Generic setters and getters ************** */
    public Task task() 
    {
        return (Task) object();
    }
    

}